import { useMutation, useQueryClient } from "@tanstack/react-query";
import { listApi, spaceApi, workspaceApi } from "../../services/endpoints";
import { useAuth } from "../../context/AuthContext";
import type { Space, TaskList, Workspace } from "../../types";

interface Props {
  workspaces: Workspace[];
  spaces: Space[];
  lists: TaskList[];
  workspaceId: number | null;
  spaceId: number | null;
  listId: number | null;
  onSelectWorkspace: (id: number) => void;
  onSelectSpace: (id: number) => void;
  onSelectList: (id: number) => void;
}

export default function Sidebar(props: Props) {
  const { user, logout } = useAuth();
  const queryClient = useQueryClient();

  const createWorkspace = useMutation({
    mutationFn: (name: string) => workspaceApi.create({ name }),
    onSuccess: (ws) => {
      queryClient.invalidateQueries({ queryKey: ["workspaces"] });
      props.onSelectWorkspace(ws.id);
    },
  });

  const createSpace = useMutation({
    mutationFn: (name: string) => spaceApi.create(props.workspaceId!, { name }),
    onSuccess: (sp) => {
      queryClient.invalidateQueries({ queryKey: ["spaces", props.workspaceId] });
      props.onSelectSpace(sp.id);
    },
  });

  const createList = useMutation({
    mutationFn: (name: string) => listApi.create(props.spaceId!, { name }),
    onSuccess: (l) => {
      queryClient.invalidateQueries({ queryKey: ["lists", props.spaceId] });
      props.onSelectList(l.id);
    },
  });

  function ask(label: string, action: (name: string) => void) {
    const name = window.prompt(label);
    if (name && name.trim()) action(name.trim());
  }

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        System<span>Dmove</span>
      </div>

      {/* Workspaces */}
      <div className="sidebar-section">
        <div className="sidebar-section-head">
          <span>Workspaces</span>
          <button onClick={() => ask("Nome do workspace", createWorkspace.mutate)} title="Novo workspace">
            +
          </button>
        </div>
        <select
          className="workspace-select"
          value={props.workspaceId ?? ""}
          onChange={(e) => props.onSelectWorkspace(Number(e.target.value))}
        >
          {props.workspaces.length === 0 && <option value="">Nenhum workspace</option>}
          {props.workspaces.map((w) => (
            <option key={w.id} value={w.id}>
              {w.name}
            </option>
          ))}
        </select>
      </div>

      {/* Spaces */}
      {props.workspaceId != null && (
        <div className="sidebar-section">
          <div className="sidebar-section-head">
            <span>Spaces</span>
            <button onClick={() => ask("Nome do space", createSpace.mutate)} title="Novo space">
              +
            </button>
          </div>
          <ul className="tree">
            {props.spaces.map((s) => (
              <li key={s.id}>
                <button
                  className={`tree-item ${props.spaceId === s.id ? "active" : ""}`}
                  onClick={() => props.onSelectSpace(s.id)}
                >
                  <span className="dot" style={{ background: s.color ?? "#7b61ff" }} />
                  {s.name}
                </button>
              </li>
            ))}
          </ul>
        </div>
      )}

      {/* Lists */}
      {props.spaceId != null && (
        <div className="sidebar-section">
          <div className="sidebar-section-head">
            <span>Lists</span>
            <button onClick={() => ask("Nome da list", createList.mutate)} title="Nova list">
              +
            </button>
          </div>
          <ul className="tree">
            {props.lists.map((l) => (
              <li key={l.id}>
                <button
                  className={`tree-item ${props.listId === l.id ? "active" : ""}`}
                  onClick={() => props.onSelectList(l.id)}
                >
                  # {l.name}
                </button>
              </li>
            ))}
          </ul>
        </div>
      )}

      <div className="sidebar-user">
        <div className="avatar">{user?.name?.charAt(0).toUpperCase()}</div>
        <div className="sidebar-user-info">
          <strong>{user?.name}</strong>
          <button className="logout" onClick={logout}>
            Sair
          </button>
        </div>
      </div>
    </aside>
  );
}
