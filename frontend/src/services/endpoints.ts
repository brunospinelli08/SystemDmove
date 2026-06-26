import { api } from "./api";
import type {
  AuthResponse,
  Folder,
  Space,
  Task,
  TaskList,
  TaskStatus,
  Priority,
  User,
  Workspace,
} from "../types";

// ===== Auth =====
export const authApi = {
  register: (data: { name: string; email: string; password: string; confirmPassword: string }) =>
    api.post<AuthResponse>("/auth/register", data).then((r) => r.data),
  login: (data: { email: string; password: string }) =>
    api.post<AuthResponse>("/auth/login", data).then((r) => r.data),
  me: () => api.get<User>("/auth/me").then((r) => r.data),
};

// ===== Workspaces =====
export const workspaceApi = {
  list: () => api.get<Workspace[]>("/workspaces").then((r) => r.data),
  create: (data: { name: string; description?: string }) =>
    api.post<Workspace>("/workspaces", data).then((r) => r.data),
  update: (id: number, data: { name: string; description?: string }) =>
    api.put<Workspace>(`/workspaces/${id}`, data).then((r) => r.data),
  remove: (id: number) => api.delete(`/workspaces/${id}`).then((r) => r.data),
};

// ===== Spaces =====
export const spaceApi = {
  listByWorkspace: (workspaceId: number) =>
    api.get<Space[]>(`/workspaces/${workspaceId}/spaces`).then((r) => r.data),
  create: (workspaceId: number, data: { name: string; color?: string; icon?: string }) =>
    api.post<Space>(`/workspaces/${workspaceId}/spaces`, data).then((r) => r.data),
  remove: (id: number) => api.delete(`/spaces/${id}`).then((r) => r.data),
};

// ===== Folders =====
export const folderApi = {
  listBySpace: (spaceId: number) =>
    api.get<Folder[]>(`/spaces/${spaceId}/folders`).then((r) => r.data),
  create: (spaceId: number, data: { name: string }) =>
    api.post<Folder>(`/spaces/${spaceId}/folders`, data).then((r) => r.data),
};

// ===== Lists =====
export const listApi = {
  listBySpace: (spaceId: number) =>
    api.get<TaskList[]>(`/spaces/${spaceId}/lists`).then((r) => r.data),
  create: (spaceId: number, data: { name: string; folderId?: number }) =>
    api.post<TaskList>(`/spaces/${spaceId}/lists`, data).then((r) => r.data),
  remove: (id: number) => api.delete(`/lists/${id}`).then((r) => r.data),
};

// ===== Tasks =====
export const taskApi = {
  listByList: (listId: number, search?: string) =>
    api
      .get<Task[]>(`/lists/${listId}/tasks`, { params: search ? { search } : {} })
      .then((r) => r.data),
  get: (id: number) => api.get<Task>(`/tasks/${id}`).then((r) => r.data),
  create: (
    listId: number,
    data: {
      title: string;
      description?: string;
      status?: TaskStatus;
      priority?: Priority;
      dueDate?: string | null;
      assigneeId?: number | null;
      parentId?: number | null;
    },
  ) => api.post<Task>(`/lists/${listId}/tasks`, data).then((r) => r.data),
  update: (
    id: number,
    data: {
      title?: string;
      description?: string;
      status?: TaskStatus;
      priority?: Priority;
      dueDate?: string | null;
      assigneeId?: number | null;
    },
  ) => api.put<Task>(`/tasks/${id}`, data).then((r) => r.data),
  move: (id: number, data: { status?: TaskStatus; position?: number }) =>
    api.patch<Task>(`/tasks/${id}/move`, data).then((r) => r.data),
  remove: (id: number) => api.delete(`/tasks/${id}`).then((r) => r.data),
};
