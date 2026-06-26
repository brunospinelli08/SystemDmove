# Backend — SystemDmove (Java + Spring Boot)

API REST do SystemDmove: autenticação JWT, workspaces, spaces, folders, lists e tarefas
(estilo ClickUp). Java 17, Spring Boot 4, Spring Security, JPA/Hibernate, Flyway e PostgreSQL.

## Pré-requisitos
- Java 17+
- PostgreSQL rodando (local ou Docker)

## 1. Criar o banco
```sql
CREATE DATABASE systemdmove;
```
Ou via Docker:
```bash
docker run --name systemdmove-pg -e POSTGRES_DB=systemdmove \
  -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:16
```

## 2. Variáveis de ambiente (opcional)
Os valores padrão estão em `src/main/resources/application.properties`. Para sobrescrever,
defina antes de rodar (veja `.env.example`):

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/systemdmove` | URL JDBC |
| `DB_USERNAME` | `postgres` | usuário do banco |
| `DB_PASSWORD` | `postgres` | senha do banco |
| `APP_JWT_SECRET` | (dev) | segredo do JWT em Base64 (>= 32 bytes) |
| `APP_UPLOAD_DIR` | `uploads` | pasta de anexos |
| `SERVER_PORT` | `8080` | porta da API |

## 3. Rodar
```bash
./mvnw spring-boot:run     # Linux/Mac
mvnw.cmd spring-boot:run   # Windows
```
No boot, o **Flyway** cria todas as tabelas automaticamente (`db/migration/V1__init.sql`).

## Endpoints principais
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/auth/register` | cria conta (name, email, password, confirmPassword) |
| POST | `/api/auth/login` | login → retorna `{ token, user }` |
| GET | `/api/auth/me` | dados do usuário logado |
| GET/POST | `/api/workspaces` | listar/criar workspaces |
| GET/POST | `/api/workspaces/{id}/spaces` | spaces do workspace |
| GET/POST | `/api/spaces/{id}/folders` | folders do space |
| GET/POST | `/api/spaces/{id}/lists` | lists do space |
| GET/POST | `/api/lists/{id}/tasks` | tarefas da list (`?search=` para buscar) |
| PUT | `/api/tasks/{id}` | editar tarefa |
| PATCH | `/api/tasks/{id}/move` | mover/reordenar (drag-and-drop): `{ status, position }` |
| DELETE | `/api/tasks/{id}` | excluir tarefa |

Todas as rotas (exceto `/api/auth/**` e `/api/health`) exigem o header
`Authorization: Bearer <token>`. Todas as queries usam prepared statements (JPA) — sem SQL injection.

## Arquitetura (`src/main/java/com/systemdmove/`)
`config/` (segurança + JWT) · `model/` (entidades) · `repository/` (JPA) · `service/` (regras) ·
`controller/` (REST) · `dto/` · `util/` (JWT) · `session/` (usuário autenticado) · `exception/`.
