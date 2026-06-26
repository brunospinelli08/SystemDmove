# SystemDmove

Aplicação web organizada em três partes.

## Stack

- **Frontend:** React + Vite + TypeScript
- **Backend:** Java 17 + Spring Boot (Maven) + JPA/Hibernate
- **Banco de dados:** PostgreSQL

## Estrutura

```
newproject/
├── frontend/          → interface (React + Vite + TS)
├── backend/           → API e regras de negócio (Spring Boot)
└── banco-de-dados/    → migrations, seeds e schemas do PostgreSQL
```

Cada pasta tem seu próprio `README.md` com detalhes e instruções de execução.

## Rodando o projeto

1. **Backend:** `cd backend && ./mvnw spring-boot:run` (ou `mvnw.cmd` no Windows) → `http://localhost:8080`
2. **Frontend:** `cd frontend && npm install && npm run dev` → `http://localhost:5173`
3. **Banco:** PostgreSQL rodando em `localhost:5432` (ver `backend/.env.example`)
