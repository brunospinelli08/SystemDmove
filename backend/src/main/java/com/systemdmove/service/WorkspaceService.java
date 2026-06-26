package com.systemdmove.service;

import com.systemdmove.dto.WorkspaceDtos.*;
import com.systemdmove.exception.ApiException;
import com.systemdmove.model.User;
import com.systemdmove.model.Workspace;
import com.systemdmove.model.WorkspaceMember;
import com.systemdmove.model.enums.MemberRole;
import com.systemdmove.repository.UserRepository;
import com.systemdmove.repository.WorkspaceMemberRepository;
import com.systemdmove.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final AccessService accessService;

    public WorkspaceService(WorkspaceRepository workspaceRepository,
                            WorkspaceMemberRepository memberRepository,
                            UserRepository userRepository,
                            AccessService accessService) {
        this.workspaceRepository = workspaceRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.accessService = accessService;
    }

    @Transactional(readOnly = true)
    public List<WorkspaceDto> listForUser(Long userId) {
        List<WorkspaceDto> result = new ArrayList<>();
        for (Workspace w : workspaceRepository.findAllForUser(userId)) {
            String role = memberRepository.findByIdWorkspaceIdAndIdUserId(w.getId(), userId)
                    .map(m -> m.getRole().name())
                    .orElse(MemberRole.MEMBER.name());
            result.add(toDto(w, role));
        }
        return result;
    }

    @Transactional
    public WorkspaceDto create(WorkspaceRequest req, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("Usuario nao encontrado"));

        Workspace ws = new Workspace();
        ws.setName(req.name());
        ws.setDescription(req.description());
        ws.setOwner(owner);
        ws = workspaceRepository.save(ws);

        memberRepository.save(new WorkspaceMember(ws.getId(), userId, MemberRole.OWNER));
        return toDto(ws, MemberRole.OWNER.name());
    }

    @Transactional
    public WorkspaceDto update(Long workspaceId, WorkspaceRequest req, Long userId) {
        accessService.requireMember(workspaceId, userId);
        Workspace ws = getOrThrow(workspaceId);
        ws.setName(req.name());
        ws.setDescription(req.description());
        return toDto(ws, currentRole(workspaceId, userId));
    }

    @Transactional
    public void delete(Long workspaceId, Long userId) {
        WorkspaceMember member = memberRepository.findByIdWorkspaceIdAndIdUserId(workspaceId, userId)
                .orElseThrow(() -> ApiException.forbidden("Voce nao tem acesso a este workspace"));
        if (member.getRole() != MemberRole.OWNER) {
            throw ApiException.forbidden("Apenas o dono pode excluir o workspace");
        }
        workspaceRepository.deleteById(workspaceId);
    }

    @Transactional(readOnly = true)
    public List<MemberDto> listMembers(Long workspaceId, Long userId) {
        accessService.requireMember(workspaceId, userId);
        List<MemberDto> result = new ArrayList<>();
        for (WorkspaceMember m : memberRepository.findByIdWorkspaceId(workspaceId)) {
            userRepository.findById(m.getId().getUserId()).ifPresent(u ->
                    result.add(new MemberDto(u.getId(), u.getName(), u.getEmail(), m.getRole().name())));
        }
        return result;
    }

    @Transactional
    public MemberDto invite(Long workspaceId, InviteRequest req, Long userId) {
        accessService.requireMember(workspaceId, userId);
        User invited = userRepository.findByEmail(req.email())
                .orElseThrow(() -> ApiException.badRequest("Nenhum usuario cadastrado com este email"));

        if (memberRepository.existsByIdWorkspaceIdAndIdUserId(workspaceId, invited.getId())) {
            throw ApiException.badRequest("Usuario ja e membro do workspace");
        }
        MemberRole role = parseRole(req.role());
        memberRepository.save(new WorkspaceMember(workspaceId, invited.getId(), role));
        return new MemberDto(invited.getId(), invited.getName(), invited.getEmail(), role.name());
    }

    private MemberRole parseRole(String role) {
        if (role == null || role.isBlank()) {
            return MemberRole.MEMBER;
        }
        try {
            return MemberRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw ApiException.badRequest("Papel invalido: " + role);
        }
    }

    private String currentRole(Long workspaceId, Long userId) {
        return memberRepository.findByIdWorkspaceIdAndIdUserId(workspaceId, userId)
                .map(m -> m.getRole().name())
                .orElse(MemberRole.MEMBER.name());
    }

    private Workspace getOrThrow(Long workspaceId) {
        return workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> ApiException.notFound("Workspace nao encontrado"));
    }

    private WorkspaceDto toDto(Workspace w, String role) {
        return new WorkspaceDto(w.getId(), w.getName(), w.getDescription(), w.getOwner().getId(), role);
    }
}
