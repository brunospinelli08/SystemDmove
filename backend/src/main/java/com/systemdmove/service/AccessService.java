package com.systemdmove.service;

import com.systemdmove.exception.ApiException;
import com.systemdmove.repository.WorkspaceMemberRepository;
import org.springframework.stereotype.Service;

/**
 * Verificacoes de acesso: garante que o usuario e membro do workspace
 * antes de operar sobre seus recursos (spaces, lists, tasks...).
 */
@Service
public class AccessService {

    private final WorkspaceMemberRepository memberRepository;

    public AccessService(WorkspaceMemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void requireMember(Long workspaceId, Long userId) {
        if (!memberRepository.existsByIdWorkspaceIdAndIdUserId(workspaceId, userId)) {
            throw ApiException.forbidden("Voce nao tem acesso a este workspace");
        }
    }
}
