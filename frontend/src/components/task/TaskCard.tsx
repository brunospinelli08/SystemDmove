import { PRIORITY_COLORS, PRIORITY_LABELS, type Task } from "../../types";
import { format } from "date-fns";

interface Props {
  task: Task;
  onClick?: () => void;
}

export default function TaskCard({ task, onClick }: Props) {
  return (
    <div className="task-card" onClick={onClick}>
      <div className="task-card-top">
        <span className="priority-pill" style={{ background: PRIORITY_COLORS[task.priority] }}>
          {PRIORITY_LABELS[task.priority]}
        </span>
        {task.dueDate && (
          <span className="due">{format(new Date(task.dueDate), "dd/MM")}</span>
        )}
      </div>
      <div className="task-title">{task.title}</div>
      <div className="task-card-bottom">
        {task.tags.map((t) => (
          <span key={t.id} className="tag" style={{ background: t.color ?? "#3a3a5e" }}>
            {t.name}
          </span>
        ))}
        {task.assigneeName && <span className="assignee">{task.assigneeName}</span>}
      </div>
    </div>
  );
}
