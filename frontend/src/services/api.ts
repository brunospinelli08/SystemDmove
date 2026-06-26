import axios from "axios";

const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080/api";

export const TOKEN_KEY = "systemdmove_token";

export const api = axios.create({
  baseURL: API_URL,
});

// Injeta o JWT em toda requisicao.
api.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY);
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Em 401, limpa a sessao e volta para o login.
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem(TOKEN_KEY);
      if (!window.location.pathname.startsWith("/login")) {
        window.location.href = "/login";
      }
    }
    return Promise.reject(error);
  },
);
