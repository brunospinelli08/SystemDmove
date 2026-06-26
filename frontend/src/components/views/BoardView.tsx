import { useEffect, useState } from "react";
import {
  DndContext,
  PointerSensor,
  closestCorners,
  useDroppable,
  useSensor,
  useSensors,
  type DragEndEvent,
} from "@dnd-kit/core";
import {
  SortableContext,
  arrayMove,
  useSortable,
  verticalListSortingStrategy,
} from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { taskApi } from "../../services/endpoints";
import { STATUS_LABELS, STATUS_ORDER, type Task, type TaskStatus } from "../../types";
import TaskCard from "../task/TaskCard";
import NewTaskInput from "../task/NewTaskInput";

interface Props {
  listId: number;
  tasks: Task[];
  loading: boolean;
  onOpenTask: (id: number) => void;
}

type Columns = Record<TaskStatus, Task[]>;

function group(tasks: Task[]): Columns {
  const cols: Columns = { TO_DO: [], IN_PROGRESS: [], IN_REVIEW: [], DONE: [] };
  for (const t of [...tasks].sort((a, b) => a.position - b.position)) {
    cols[t.status].push(t);
  }
  return cols;
}

function SortableCard({ task, onOpen }: { task: Task; onOpen: () => void }) {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({
    id: task.id,
  });
  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.4 : 1,
  };
  return (
    <div ref={setNodeRef} style={style} {...attributes} {...listeners}>
      <TaskCard task={task} onClick={onOpen} />
    </div>
  );
}

function Column({
  status,
  tasks,
  listId,
  onOpen,
}: {
  status: TaskStatus;
  tasks: Task[];
  listId: number;
  onOpen: (id: number) => void;
}) {
  const { setNodeRef } = useDroppable({ id: status });
  return (
    <div className="board-column">
      <div className="board-column-head">
        <span className={`status-chip status-${status}`}>{STATUS_LABELS[status]}</span>
        <span className="count">{tasks.length}</span>
      </div>
      <SortableContext items={tasks.map((t) => t.id)} strategy={verticalListSortingStrategy}>
        <div ref={setNodeRef} className="board-column-body">
          {tasks.map((t) => (
            <SortableCard key={t.id} task={t} onOpen={() => onOpen(t.id)} />
          ))}
        </div>
      </SortableContext>
      <NewTaskInput listId={listId} status={status} placeholder="+ Adicionar" />
    </div>
  );
}

export default function BoardView({ listId, tasks, loading, onOpenTask }: Props) {
  const queryClient = useQueryClient();
  const [columns, setColumns] = useState<Columns>(() => group(tasks));
  const sensors = useSensors(useSensor(PointerSensor, { activationConstraint: { distance: 5 } }));

  useEffect(() => setColumns(group(tasks)), [tasks]);

  const move = useMutation({
    mutationFn: ({ id, status, position }: { id: number; status: TaskStatus; position: number }) =>
      taskApi.move(id, { status, position }),
    onSettled: () => queryClient.invalidateQueries({ queryKey: ["tasks", listId] }),
  });

  function findContainer(id: number): TaskStatus | null {
    return (STATUS_ORDER.find((s) => columns[s].some((t) => t.id === id)) as TaskStatus) ?? null;
  }

  function handleDragEnd(event: DragEndEvent) {
    const { active, over } = event;
    if (!over) return;
    const activeId = Number(active.id);
    const sourceStatus = findContainer(activeId);
    if (!sourceStatus) return;

    // O alvo pode ser uma coluna (string) ou um card (number).
    let targetStatus: TaskStatus;
    let targetIndex: number;
    if (typeof over.id === "string") {
      targetStatus = over.id as TaskStatus;
      targetIndex = columns[targetStatus].length;
    } else {
      const overId = Number(over.id);
      targetStatus = findContainer(overId) ?? sourceStatus;
      targetIndex = columns[targetStatus].findIndex((t) => t.id === overId);
      if (targetIndex < 0) targetIndex = columns[targetStatus].length;
    }

    let finalIndex = targetIndex;
    setColumns((prev) => {
      const next: Columns = { ...prev };
      if (sourceStatus === targetStatus) {
        const oldIndex = prev[sourceStatus].findIndex((t) => t.id === activeId);
        next[sourceStatus] = arrayMove(prev[sourceStatus], oldIndex, targetIndex);
        finalIndex = next[sourceStatus].findIndex((t) => t.id === activeId);
      } else {
        const source = [...prev[sourceStatus]];
        const idx = source.findIndex((t) => t.id === activeId);
        const [moved] = source.splice(idx, 1);
        const target = [...prev[targetStatus]];
        target.splice(targetIndex, 0, { ...moved, status: targetStatus });
        next[sourceStatus] = source;
        next[targetStatus] = target;
        finalIndex = targetIndex;
      }
      return next;
    });

    move.mutate({ id: activeId, status: targetStatus, position: finalIndex });
  }

  if (loading) return <div className="empty-state">Carregando tarefas...</div>;

  return (
    <DndContext sensors={sensors} collisionDetection={closestCorners} onDragEnd={handleDragEnd}>
      <div className="board">
        {STATUS_ORDER.map((status) => (
          <Column
            key={status}
            status={status}
            tasks={columns[status]}
            listId={listId}
            onOpen={onOpenTask}
          />
        ))}
      </div>
    </DndContext>
  );
}
