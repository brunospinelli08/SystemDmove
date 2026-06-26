# Banco de Dados — PostgreSQL

O schema é gerenciado por **Flyway** e aplicado automaticamente quando o backend sobe.

## Script de criação (fonte da verdade)
O script com todas as tabelas fica em:

```
backend/src/main/resources/db/migration/V1__init.sql
```

O Flyway executa esse arquivo no primeiro boot e registra o histórico na tabela
`flyway_schema_history`. Para adicionar mudanças futuras, crie novos arquivos versionados
(`V2__...sql`, `V3__...sql`) na mesma pasta — nunca edite uma migration já aplicada.

## Pastas
- `migrations/` — referência/espelho das migrations (a execução real é via Flyway no backend)
- `seeds/` — dados iniciais para popular o banco
- `schemas/` — diagramas/documentação da estrutura

## Tabelas
`users`, `workspaces`, `workspace_members`, `spaces`, `folders`, `lists`, `tasks`,
`task_comments`, `task_attachments`, `tags`, `task_tags`, `notifications`.
