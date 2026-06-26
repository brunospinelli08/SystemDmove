import { useEffect, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { taskApi } from "../../services/endpoints";
import {
  PRIORITY_LABELS,
  STATUS_LABELS,
  STATUS_ORDER,
  type Priority,
  type TaskStatus,
} from "../../types";

interface Props {
  taskId: number;
  onClose: () => void;
}

const PRIORITIES: Priority[] = ["URGENT", "HIGH", "NORMAL", "LOW"];

export default function TaskDetailPanel({ taskId, onClose }: Props) {
  const queryClient = useQueryClient();
  const { data: task, isLoading } = useQuery({
    queryKey: ["task", taskId],
    queryFn: () => taskApi.get(taskId),
  });

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [status, setStatus] = useState<TaskStatus>("TO_DO");
  const [priority, setPriority] = useState<Priority>("NORMAL");
  const [dueDate, setDueDate] = useState("");

  useEffect(() => {
    if (task) {
      setTitle(task.title);
      setDescription(task.description ?? "");
      setStatus(task.status);
      setPriority(task.priority);
      setDueDate(task.dueDate ? task.dueDate.substring(0, 10) : "");
    }
  }, [task]);

  const invalidate = () => {
    if (task) queryClient.invalidateQueries({ queryKey: ["tasks", task.listId] });
    queryClient.invalidateQueries({ queryKey: ["task", taskId] });
  };

  const save = useMutation({
    mutationFn: () =>
      taskApi.update(taskId, {
        title,
        description,
        status,
        priority,
        dueDate: dueDate ? new Date(dueDate).toISOString() : null,
      }),
    onSuccess: () => {
      invalidate();
      onClose();
    },
  });

  const remove = useMutation({
    mutationFn: () => taskApi.remove(taskId),
    onSuccess: () => {
      invalidate();
      onClose();
    },
  });

  return (
    <>
      <div className="panel-overlay" onClick={onClose} />
      <aside className="task-panel">
        <div className="task-panel-head">
          <h3>Detalhes da tarefa</h3>
          <button className="icon-btn" onClick={onClose}>
            ✕
          </button>
        </div>

        {isLoading || !task ? (
          <div className="empty-state">Carregando...</div>
        ) : (
          <div className="task-panel-body">
            <label className="field">
              <span>Titulo</span>
              <input value={title} onChange={(e) => setTitle(e.target.value)} />
            </label>

            <label className="field">
              <span>Descricao</span>
              <textarea
                rows={5}
                value={description}
                onChange={(e) => setDescription(e.target.value)}
              />
            </label>

            <div className="field-row">
              <label className="field">
                <span>Status</span>
                <select value={status} onChange={(e) => setStatus(e.target.value as TaskStatus)}>
                  {STATUS_ORDER.map((s) => (
                    <option key={s} value={s}>
                      {STATUS_LABELS[s]}
                    </option>
                  ))}
                </select>
              </label>

              <label className="field">
                <span>Prioridade</span>
                <select value={priority} onChange={(e) => setPriority(e.target.value as Priority)}>
                  {PRIORITIES.map((p) => (
                    <option key={p} value={p}>
                      {PRIORITY_LABELS[p]}
                    </option>
                  ))}
                </select>
              </label>
            </div>

            <label className="field">
              <span>Data de vencimento</span>
              <input type="date" value={dueDate} onChange={(e) => setDueDate(e.target.value)} />
            </label>

            <div className="task-panel-actions">
              <button className="btn-danger" onClick={() => remove.mutate()}>
                Excluir
              </button>
              <button className="btn-primary" onClick={() => save.mutate()}>
                Salvar
              </button>
            </div>
          </div>
        )}
      </aside>
    </>
  );
}
