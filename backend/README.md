# Backend

Parte da aplicação que roda no servidor: regras de negócio, API e acesso ao banco de dados.

## Estrutura

- `src/config/` — configurações (variáveis de ambiente, conexão com o banco)
- `src/controllers/` — recebem as requisições e devolvem as respostas
- `src/routes/` — definição das rotas/endpoints da API
- `src/models/` — modelos que representam os dados do banco
- `src/middleware/` — funções intermediárias (autenticação, validação, logs)
- `src/services/` — regras de negócio e lógica principal
- `src/utils/` — funções auxiliares do backend
