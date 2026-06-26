import { AxiosError } from "axios";

/** Extrai uma mensagem amigavel de um erro de requisicao. */
export function getErrorMessage(err: unknown): string {
  if (err instanceof AxiosError) {
    const data = err.response?.data as { message?: string } | undefined;
    if (data?.message) return data.message;
    if (err.message) return err.message;
  }
  if (err instanceof Error) return err.message;
  return "Ocorreu um erro inesperado";
}
