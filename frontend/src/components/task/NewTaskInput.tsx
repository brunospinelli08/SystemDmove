import { useState, type FormEvent } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { taskApi } from "../../services/endpoints";
import type { TaskStatus } from "../../types";

interface Props {
  listId: number;
  status?: TaskStatus;
  placeholder?: string;
}

export default function NewTaskInput({ listId, status, placeholder }: Props) {
  const queryClient = useQueryClient();
  const [title, setTitle] = useState("");

  const create = useMutation({
    mutationFn: (t: string) => taskApi.create(listId, { title: t, status }),
    onSuccess: () => {
      setTitle("");
      queryClient.invalidateQueries({ queryKey: ["tasks", listId] });
    },
  });

  function submit(e: FormEvent) {
    e.preventDefault();
    if (title.trim()) create.mutate(title.trim());
  }

  return (
    <form className="new-task" onSubmit={submit}>
      <input
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        placeholder={placeholder ?? "+ Nova tarefa"}
      />
    </form>
  );
}
