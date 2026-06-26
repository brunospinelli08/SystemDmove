-- ============================================================
-- SystemDmove - Schema inicial (estilo ClickUp)
-- ============================================================

-- ===== Usuarios =====
CREATE TABLE users (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name          VARCHAR(120) NOT NULL,
    email         VARCHAR(180) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    avatar_url    VARCHAR(500),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ===== Workspaces =====
CREATE TABLE workspaces (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(120) NOT NULL,
    description VARCHAR(500),
    owner_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_workspaces_owner ON workspaces(owner_id);

-- ===== Membros do workspace =====
CREATE TABLE workspace_members (
    workspace_id BIGINT NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
    user_id      BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role         VARCHAR(20) NOT NULL DEFAULT 'MEMBER'
                 CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER')),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (workspace_id, user_id)
);

-- ===== Spaces (divisoes principais do workspace) =====
CREATE TABLE spaces (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    workspace_id BIGINT NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
    name         VARCHAR(120) NOT NULL,
    color        VARCHAR(20),
    icon         VARCHAR(60)
);
CREATE INDEX idx_spaces_workspace ON spaces(workspace_id);

-- ===== Folders (pastas dentro de spaces) =====
CREATE TABLE folders (
    id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    space_id BIGINT NOT NULL REFERENCES spaces(id) ON DELETE CASCADE,
    name     VARCHAR(120) NOT NULL
);
CREATE INDEX idx_folders_space ON folders(space_id);

-- ===== Lists (em folders ou direto em spaces) =====
CREATE TABLE lists (
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    folder_id BIGINT REFERENCES folders(id) ON DELETE CASCADE,
    space_id  BIGINT NOT NULL REFERENCES spaces(id) ON DELETE CASCADE,
    name      VARCHAR(120) NOT NULL
);
CREATE INDEX idx_lists_space ON lists(space_id);
CREATE INDEX idx_lists_folder ON lists(folder_id);

-- ===== Tasks (nucleo do sistema) =====
CREATE TABLE tasks (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    list_id     BIGINT NOT NULL REFERENCES lists(id) ON DELETE CASCADE,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    status      VARCHAR(20) NOT NULL DEFAULT 'TO_DO'
                CHECK (status IN ('TO_DO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE')),
    priority    VARCHAR(20) NOT NULL DEFAULT 'NORMAL'
                CHECK (priority IN ('URGENT', 'HIGH', 'NORMAL', 'LOW')),
    due_date    TIMESTAMPTZ,
    assignee_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_by  BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    parent_id   BIGINT REFERENCES tasks(id) ON DELETE CASCADE,
    position    INTEGER NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_tasks_list ON tasks(list_id);
CREATE INDEX idx_tasks_assignee ON tasks(assignee_id);
CREATE INDEX idx_tasks_parent ON tasks(parent_id);
CREATE INDEX idx_tasks_status ON tasks(status);

-- ===== Comentarios =====
CREATE TABLE task_comments (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    task_id    BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    user_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content    TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_comments_task ON task_comments(task_id);

-- ===== Anexos =====
CREATE TABLE task_attachments (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    task_id     BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    file_name   VARCHAR(255) NOT NULL,
    file_path   VARCHAR(500) NOT NULL,
    uploaded_by BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_attachments_task ON task_attachments(task_id);

-- ===== Tags =====
CREATE TABLE tags (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    workspace_id BIGINT NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
    name         VARCHAR(60) NOT NULL,
    color        VARCHAR(20)
);
CREATE INDEX idx_tags_workspace ON tags(workspace_id);

-- ===== Tarefa <-> Tag (N:N) =====
CREATE TABLE task_tags (
    task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    tag_id  BIGINT NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (task_id, tag_id)
);

-- ===== Notificacoes =====
CREATE TABLE notifications (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title      VARCHAR(180) NOT NULL,
    message    VARCHAR(500),
    read       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_notifications_user ON notifications(user_id);
