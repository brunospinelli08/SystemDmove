# SystemDmove

Sistema de gestão de projetos e produtividade inspirado no ClickUp — workspaces, spaces,
folders, lists e tarefas com visualizações List e Board (Kanban com drag-and-drop).

## Stack

- **Frontend:** React + Vite + TypeScript, React Query, React Router, dnd-kit (drag-and-drop)
- **Backend:** Java 17 + Spring Boot 4, Spring Security (JWT + BCrypt), JPA/Hibernate, Flyway
- **Banco de dados:** PostgreSQL

## Estrutura

```
newproject/
├── frontend/          → interface React (login, sidebar, List view, Board view)
├── backend/           → API Spring Boot (auth, workspaces, tasks, drag-and-drop)
└── banco-de-dados/    → migrations/seeds/schemas (schema via Flyway no backend)
```

## Como rodar

### 1. Banco
```sql
CREATE DATABASE systemdmove;
```
(ou via Docker — ver `backend/README.md`)

### 2. Backend → http://localhost:8080
```bash
cd backend
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```
O Flyway cria as tabelas automaticamente no primeiro boot.

### 3. Frontend → http://localhost:5173
```bash
cd frontend
npm install
npm run dev
```
A URL da API pode ser configurada via `VITE_API_URL` (padrão `http://localhost:8080/api`).

## Funcionalidades (fase atual — fundação)
- ✅ Cadastro e login com JWT + BCrypt
- ✅ Workspaces, Spaces e Lists (sidebar hierárquico)
- ✅ CRUD de tarefas com status, prioridade, responsável e vencimento
- ✅ **List view** com reordenação por drag-and-drop
- ✅ **Board view** (Kanban) com drag-and-drop entre colunas (status)
- ✅ Painel lateral de detalhes da tarefa
- ✅ Tema escuro (#1a1a2e) com acentos roxo/azul

## Próximas iterações
Calendar view · Dashboard · notificações · filtros avançados · comentários · tags coloridas ·
convite de membros · anexos.
