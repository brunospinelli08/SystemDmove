// Tipos espelhando os DTOs do backend.

export type TaskStatus = "TO_DO" | "IN_PROGRESS" | "IN_REVIEW" | "DONE";
export type Priority = "URGENT" | "HIGH" | "NORMAL" | "LOW";
export type MemberRole = "OWNER" | "ADMIN" | "MEMBER";

export interface User {
  id: number;
  name: string;
  email: string;
  avatarUrl?: string | null;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface Workspace {
  id: number;
  name: string;
  description?: string | null;
  ownerId: number;
  role: MemberRole;
}

export interface Space {
  id: number;
  workspaceId: number;
  name: string;
  color?: string | null;
  icon?: string | null;
}

export interface Folder {
  id: number;
  spaceId: number;
  name: string;
}

export interface TaskList {
  id: number;
  spaceId: number;
  folderId?: number | null;
  name: string;
}

export interface TagSummary {
  id: number;
  name: string;
  color?: string | null;
}

export interface Task {
  id: number;
  listId: number;
  title: string;
  description?: string | null;
  status: TaskStatus;
  priority: Priority;
  dueDate?: string | null;
  assigneeId?: number | null;
  assigneeName?: string | null;
  createdById: number;
  parentId?: number | null;
  position: number;
  tags: TagSummary[];
  createdAt: string;
  updatedAt: string;
}

export const STATUS_LABELS: Record<TaskStatus, string> = {
  TO_DO: "To Do",
  IN_PROGRESS: "In Progress",
  IN_REVIEW: "In Review",
  DONE: "Done",
};

export const STATUS_ORDER: TaskStatus[] = ["TO_DO", "IN_PROGRESS", "IN_REVIEW", "DONE"];

export const PRIORITY_LABELS: Record<Priority, string> = {
  URGENT: "Urgent",
  HIGH: "High",
  NORMAL: "Normal",
  LOW: "Low",
};

export const PRIORITY_COLORS: Record<Priority, string> = {
  URGENT: "#f56565",
  HIGH: "#ed8936",
  NORMAL: "#4299e1",
  LOW: "#718096",
};
