import type { ViewMode } from "../../pages/AppPage";

interface Props {
  title: string;
  view: ViewMode;
  onChangeView: (v: ViewMode) => void;
  search: string;
  onSearch: (s: string) => void;
}

export default function Header({ title, view, onChangeView, search, onSearch }: Props) {
  return (
    <header className="header">
      <h2 className="header-title">{title}</h2>

      <div className="header-center">
        <div className="view-toggle">
          <button className={view === "list" ? "active" : ""} onClick={() => onChangeView("list")}>
            List
          </button>
          <button className={view === "board" ? "active" : ""} onClick={() => onChangeView("board")}>
            Board
          </button>
        </div>
        <input
          className="search"
          placeholder="Buscar tarefas..."
          value={search}
          onChange={(e) => onSearch(e.target.value)}
        />
      </div>

      <button className="bell" title="Notificacoes">
        🔔
        <span className="badge">0</span>
      </button>
    </header>
  );
}
