import type { DataProvider, GetListParams } from 'react-admin';
import { fetchUtils } from 'react-admin';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1';

const httpClient = (url: string, options: fetchUtils.Options = {}) => {
  const token = localStorage.getItem('token');
  const headers = new Headers(options.headers || {});
  if (!headers.has('Content-Type')) headers.set('Content-Type', 'application/json');
  if (token) headers.set('Authorization', `Bearer ${token}`);
  return fetchUtils.fetchJson(url, { ...options, headers });
};

const toLower = (v: any) => (v == null ? '' : String(v).toLowerCase());
const parseDate = (v?: any) => (v ? new Date(v) : null);

export const dataProvider: DataProvider = {
  getList: async (resource, params: GetListParams) => {
    const { json } = await httpClient(`${API_URL}/${resource}`);
    const rows: any[] = Array.isArray(json) ? json : [];

    const { filter = {}, sort = { field: 'id', order: 'ASC' }, pagination = { page: 1, perPage: 25 } } = params;

    // 1) TEXT search across multiple fields (q)
    const q = toLower(filter.q);
    let filtered = rows.filter(r => {
      if (!q) return true;
      return [
        r.id,
        r.transactionRef,
        r.reason,
        r.status,
        r.username,
      ].some(val => toLower(val).includes(q));
    });

    // 2) Field-specific filters
    if (filter.transactionRef) {
      const val = toLower(filter.transactionRef);
      filtered = filtered.filter(r => toLower(r.transactionRef).includes(val));
    }
    if (filter.reason) {
      const val = toLower(filter.reason);
      filtered = filtered.filter(r => toLower(r.reason).includes(val));
    }
    if (filter.status) {
      filtered = filtered.filter(r => String(r.status) === String(filter.status));
    }
    if (filter.username) {
      const val = toLower(filter.username);
      filtered = filtered.filter(r => toLower(r.username).includes(val));
    }
    // Date range on createdAt
    const from = parseDate(filter.createdAt_gte);
    const to = parseDate(filter.createdAt_lte);
    if (from) filtered = filtered.filter(r => parseDate(r.createdAt)! >= from);
    if (to)   filtered = filtered.filter(r => parseDate(r.createdAt)! <= to);

    // 3) Sort
    const dir = sort.order === 'ASC' ? 1 : -1;
    filtered.sort((a: any, b: any) => {
      const av = a[sort.field];
      const bv = b[sort.field];
      if (av == null && bv == null) return 0;
      if (av == null) return -1 * dir;
      if (bv == null) return  1 * dir;
      // string-aware compare
      return String(av).localeCompare(String(bv), undefined, { numeric: true }) * dir;
    });

    // 4) Paginate (client-side)
    const start = (pagination.page - 1) * pagination.perPage;
    const end = start + pagination.perPage;
    const pageData = filtered.slice(start, end);

    return { data: pageData, total: filtered.length };
  },

  getOne: async (resource, params) => {
    const { json } = await httpClient(`${API_URL}/${resource}/${params.id}`);
    return { data: json };
  },

  getMany: async (resource, params) => {
    const { json } = await httpClient(`${API_URL}/${resource}`);
    const map = new Map(json.map((r: any) => [String(r.id), r]));
    return { data: params.ids.map(id => map.get(String(id))).filter(Boolean) as any[] };
  },

  getManyReference: async (resource, _params) => {
    const { json } = await httpClient(`${API_URL}/${resource}`);
    return { data: json, total: Array.isArray(json) ? json.length : 0 };
  },

  create: async (resource, params) => {
    const { json } = await httpClient(`${API_URL}/${resource}`, {
      method: 'POST',
      body: JSON.stringify(params.data),
    });
    return { data: json };
  },

  update: async () => { throw new Error('update not implemented'); },
  updateMany: async () => ({ data: [] }),
  delete: async () => { throw new Error('delete not implemented'); },
  deleteMany: async () => ({ data: [] }),
};
