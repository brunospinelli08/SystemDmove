import { useEffect, useMemo, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { listApi, spaceApi, taskApi, workspaceApi } from "../services/endpoints";
import Sidebar from "../components/layout/Sidebar";
import Header from "../components/layout/Header";
import ListView from "../components/views/ListView";
import BoardView from "../components/views/BoardView";
import TaskDetailPanel from "../components/task/TaskDetailPanel";

export type ViewMode = "list" | "board";

export default function AppPage() {
  const [workspaceId, setWorkspaceId] = useState<number | null>(null);
  const [spaceId, setSpaceId] = useState<number | null>(null);
  const [listId, setListId] = useState<number | null>(null);
  const [view, setView] = useState<ViewMode>("list");
  const [search, setSearch] = useState("");
  const [selectedTaskId, setSelectedTaskId] = useState<number | null>(null);

  const workspacesQuery = useQuery({ queryKey: ["workspaces"], queryFn: workspaceApi.list });

  const spacesQuery = useQuery({
    queryKey: ["spaces", workspaceId],
    queryFn: () => spaceApi.listByWorkspace(workspaceId!),
    enabled: workspaceId != null,
  });

  const listsQuery = useQuery({
    queryKey: ["lists", spaceId],
    queryFn: () => listApi.listBySpace(spaceId!),
    enabled: spaceId != null,
  });

  const tasksQuery = useQuery({
    queryKey: ["tasks", listId, search],
    queryFn: () => taskApi.listByList(listId!, search || undefined),
    enabled: listId != null,
  });

  // Auto-selecao em cascata do primeiro item disponivel.
  useEffect(() => {
    if (workspaceId == null && workspacesQuery.data?.length) {
      setWorkspaceId(workspacesQuery.data[0].id);
    }
  }, [workspacesQuery.data, workspaceId]);

  useEffect(() => {
    setSpaceId(null);
    setListId(null);
  }, [workspaceId]);

  useEffect(() => {
    if (spaceId == null && spacesQuery.data?.length) {
      setSpaceId(spacesQuery.data[0].id);
    }
  }, [spacesQuery.data, spaceId]);

  useEffect(() => {
    setListId(null);
  }, [spaceId]);

  useEffect(() => {
    if (listId == null && listsQuery.data?.length) {
      setListId(listsQuery.data[0].id);
    }
  }, [listsQuery.data, listId]);

  const currentList = useMemo(
    () => listsQuery.data?.find((l) => l.id === listId) ?? null,
    [listsQuery.data, listId],
  );
  const currentWorkspace = useMemo(
    () => workspacesQuery.data?.find((w) => w.id === workspaceId) ?? null,
    [workspacesQuery.data, workspaceId],
  );

  return (
    <div className="app-shell">
      <Sidebar
        workspaces={workspacesQuery.data ?? []}
        spaces={spacesQuery.data ?? []}
        lists={listsQuery.data ?? []}
        workspaceId={workspaceId}
        spaceId={spaceId}
        listId={listId}
        onSelectWorkspace={setWorkspaceId}
        onSelectSpace={setSpaceId}
        onSelectList={setListId}
      />

      <div className="app-main">
        <Header
          title={currentList?.name ?? currentWorkspace?.name ?? "SystemDmove"}
          view={view}
          onChangeView={setView}
          search={search}
          onSearch={setSearch}
        />

        <main className="app-content">
          {listId == null ? (
            <div className="empty-state">
              Selecione (ou crie) um workspace, space e uma list para comecar.
            </div>
          ) : view === "list" ? (
            <ListView
              listId={listId}
              tasks={tasksQuery.data ?? []}
              loading={tasksQuery.isLoading}
              onOpenTask={setSelectedTaskId}
            />
          ) : (
            <BoardView
              listId={listId}
              tasks={tasksQuery.data ?? []}
              loading={tasksQuery.isLoading}
              onOpenTask={setSelectedTaskId}
            />
          )}
        </main>
      </div>

      {selectedTaskId != null && (
        <TaskDetailPanel taskId={selectedTaskId} onClose={() => setSelectedTaskId(null)} />
      )}
    </div>
  );
}
