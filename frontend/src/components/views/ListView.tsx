import { useEffect, useState } from "react";
import {
  DndContext,
  PointerSensor,
  closestCenter,
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
import { STATUS_LABELS, type Task } from "../../types";
import NewTaskInput from "../task/NewTaskInput";

interface Props {
  listId: number;
  tasks: Task[];
  loading: boolean;
  onOpenTask: (id: number) => void;
}

function SortableRow({ task, onOpen }: { task: Task; onOpen: () => void }) {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({
    id: task.id,
  });
  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
  };
  return (
    <div ref={setNodeRef} style={style} className="list-row" onClick={onOpen}>
      <span className="drag-handle" {...attributes} {...listeners} onClick={(e) => e.stopPropagation()}>
        ⠿
      </span>
      <span className={`status-chip status-${task.status}`}>{STATUS_LABELS[task.status]}</span>
      <span className="list-row-title">{task.title}</span>
      <span className="list-row-meta">{task.assigneeName ?? "-"}</span>
    </div>
  );
}

export default function ListView({ listId, tasks, loading, onOpenTask }: Props) {
  const queryClient = useQueryClient();
  const [items, setItems] = useState<Task[]>(tasks);
  const sensors = useSensors(useSensor(PointerSensor, { activationConstraint: { distance: 5 } }));

  useEffect(() => setItems(tasks), [tasks]);

  const move = useMutation({
    mutationFn: ({ id, position }: { id: number; position: number }) =>
      taskApi.move(id, { position }),
    onSettled: () => queryClient.invalidateQueries({ queryKey: ["tasks", listId] }),
  });

  function handleDragEnd(event: DragEndEvent) {
    const { active, over } = event;
    if (!over || active.id === over.id) return;
    const oldIndex = items.findIndex((t) => t.id === active.id);
    const newIndex = items.findIndex((t) => t.id === over.id);
    const reordered = arrayMove(items, oldIndex, newIndex);
    setItems(reordered);

    const moved = reordered[newIndex];
    // Posicao dentro da mesma coluna (status) na nova ordem.
    const positionInStatus = reordered
      .filter((t) => t.status === moved.status)
      .findIndex((t) => t.id === moved.id);
    move.mutate({ id: moved.id, position: positionInStatus });
  }

  if (loading) return <div className="empty-state">Carregando tarefas...</div>;

  return (
    <div className="list-view">
      <NewTaskInput listId={listId} />
      {items.length === 0 ? (
        <div className="empty-state">Nenhuma tarefa ainda. Crie a primeira acima.</div>
      ) : (
        <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
          <SortableContext items={items.map((t) => t.id)} strategy={verticalListSortingStrategy}>
            <div className="list-rows">
              {items.map((task) => (
                <SortableRow key={task.id} task={task} onOpen={() => onOpenTask(task.id)} />
              ))}
            </div>
          </SortableContext>
        </DndContext>
      )}
    </div>
  );
}
