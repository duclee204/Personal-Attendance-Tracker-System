const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export function getToken() {
  return localStorage.getItem('pats_token');
}

export function setSession(auth) {
  localStorage.setItem('pats_token', auth.token);
  localStorage.setItem('pats_user', JSON.stringify(auth.user));
}

export function clearSession() {
  localStorage.removeItem('pats_token');
  localStorage.removeItem('pats_user');
}

export async function api(path, options = {}) {
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
  const token = getToken();
  if (token) headers.Authorization = `Bearer ${token}`;
  const res = await fetch(`${API_BASE}${path}`, { ...options, headers });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || `HTTP ${res.status}`);
  }
  if (res.status === 204) return null;
  const text = await res.text();
  return text ? JSON.parse(text) : null;
}
