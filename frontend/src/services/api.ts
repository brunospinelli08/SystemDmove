// Cliente HTTP base para falar com o backend (Spring Boot).
// A URL vem da variavel de ambiente VITE_API_URL (veja .env.example).

const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080/api";

export async function apiGet<T>(path: string): Promise<T> {
  const response = await fetch(`${API_URL}${path}`);
  if (!response.ok) {
    throw new Error(`Erro na requisicao: ${response.status}`);
  }
  return response.json() as Promise<T>;
}

export interface HealthResponse {
  status: string;
  service: string;
  timestamp: string;
}

// Exemplo: consome o endpoint /api/health do backend.
export function getHealth(): Promise<HealthResponse> {
  return apiGet<HealthResponse>("/health");
}
