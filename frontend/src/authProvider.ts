import type { AuthProvider } from 'react-admin';

// const API = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1';
const AUTH = (import.meta.env.VITE_AUTH_URL || 'http://localhost:8080/api/auth');

export const authProvider: AuthProvider = {
  login: async ({ username, password }) => {
    const res = await fetch(`${AUTH}/login`, { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({ username, password }) });
    if (!res.ok) throw new Error('Invalid credentials');
    const { token } = await res.json();
    localStorage.setItem('token', token);
    // fetch permissions
    const me = await fetch(`${AUTH}/me`, { headers: { Authorization: `Bearer ${token}` } }).then(r=>r.json());
    localStorage.setItem('role', me.role);
    localStorage.setItem('username', me.username);
    return;
  },
  logout: () => { localStorage.removeItem('token'); localStorage.removeItem('role'); localStorage.removeItem('username'); return Promise.resolve(); },
  checkAuth: () => localStorage.getItem('token') ? Promise.resolve() : Promise.reject(),
  checkError: async (e) => {
    // Only 401 should end the session
    if ((e as any)?.status === 401) {
      localStorage.removeItem('token');
      throw e;
    }
    // 403 should NOT log you out
    return;
  },
  getIdentity: async () => ({ id: localStorage.getItem('username') || 'me', fullName: localStorage.getItem('username') || 'me' }),
  getPermissions: async () => localStorage.getItem('role') || 'CLIENT',
};
