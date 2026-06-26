# Frontend — React + Vite + TypeScript

Interface do SystemDmove. Usa React, Vite e TypeScript.

## Estrutura (`src/`)

- `components/` — componentes reutilizáveis de interface
- `pages/` — telas/páginas da aplicação
- `services/` — comunicação com o backend (ex.: `api.ts`)
- `hooks/` — hooks customizados do React
- `styles/` — estilos compartilhados
- `utils/` — funções auxiliares
- `assets/` — imagens, ícones e mídias

## Como rodar

```bash
# A partir da pasta frontend/
npm install      # primeira vez
npm run dev      # ambiente de desenvolvimento
```

O frontend sobe em `http://localhost:5173` e consome a API em `http://localhost:8080/api`
(configurável via `VITE_API_URL`).
