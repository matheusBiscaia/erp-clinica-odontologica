import axios from "axios";

export function storedToken() {
  return localStorage.getItem("token") || sessionStorage.getItem("token");
}

export function clearSession() {
  localStorage.removeItem("token");
  sessionStorage.removeItem("token");
  window.dispatchEvent(new Event("thay-e-theu-session"));
}

export function expiration(token) {
  try {
    const parts = token?.split(".");
    if (parts?.length !== 3 || !parts.every(part => /^[\w-]+$/.test(part))) return 0;
    const header = JSON.parse(atob(parts[0].replaceAll("-", "+").replaceAll("_", "/")));
    const claims = JSON.parse(atob(parts[1].replaceAll("-", "+").replaceAll("_", "/")));
    return /^HS(256|384|512)$/.test(header.alg) && claims.sub && Number.isFinite(claims.exp)
      ? claims.exp * 1000 : 0;
  } catch {
    return 0;
  }
}

export function saveSession(token, remember) {
  if (expiration(token) <= Date.now()) throw new Error("Sessão inválida");
  localStorage.removeItem("token");
  sessionStorage.removeItem("token");
  (remember ? localStorage : sessionStorage).setItem("token", token);
  window.dispatchEvent(new Event("thay-e-theu-session"));
}

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || (import.meta.env.DEV ? "http://localhost:8080" : "/"),
  timeout: 15000,
});

api.interceptors.request.use(config => {
  if (config.url === "/api/auth/login") return config;
  const token = storedToken();
  if (expiration(token) <= Date.now()) {
    clearSession();
    throw new axios.CanceledError("Sessão inválida ou expirada");
  }
  config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(response => response, error => {
  const authorization = error.config?.headers?.Authorization;
  if ([401, 403].includes(error.response?.status) && authorization &&
      authorization === `Bearer ${storedToken()}`) clearSession();
  return Promise.reject(error);
});

export function apiError(error) {
  if (!error.response) return "Não foi possível conectar à API. Tente novamente.";
  const { status, data } = error.response;
  if (status < 500 && typeof data?.message === "string" &&
      !["No message available", "Validation failed"].includes(data.message)) return data.message;
  if (status === 400) return "Revise os campos informados.";
  if (status === 404) return "Registro não encontrado.";
  return "Não foi possível concluir a operação. Tente novamente.";
}

export default api;
