import type { AdminProduct, PriceSnapshot, ProductDetail, ProductSummary } from '../types';

const AUTH_KEY = 'api_auth';

export function setAuth(username: string, password: string) {
  const token = btoa(`${username}:${password}`);
  sessionStorage.setItem(AUTH_KEY, token);
}

export function clearAuth() {
  sessionStorage.removeItem(AUTH_KEY);
}

export function isAuthenticated(): boolean {
  return sessionStorage.getItem(AUTH_KEY) !== null;
}

function authHeaders(): HeadersInit {
  const token = sessionStorage.getItem(AUTH_KEY);
  return token ? { Authorization: `Basic ${token}` } : {};
}

async function request<T>(url: string, options: RequestInit = {}): Promise<T> {
  const response = await fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...authHeaders(),
      ...options.headers,
    },
  });

  if (!response.ok) {
    const body = await response.json().catch(() => ({ message: response.statusText }));
    throw new Error(body.message || `Request failed: ${response.status}`);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json();
}

export const api = {
  listProducts: () => request<ProductSummary[]>('/api/products'),

  getProduct: (id: string) => request<ProductDetail>(`/api/products/${id}`),

  getPriceHistory: (id: string) => request<PriceSnapshot[]>(`/api/products/${id}/price-history`),

  listAdminProducts: () => request<AdminProduct[]>('/api/admin/products'),

  createProduct: (asin: string) =>
    request<AdminProduct>('/api/admin/products', {
      method: 'POST',
      body: JSON.stringify({ asin }),
    }),

  updateProduct: (id: string, asin: string) =>
    request<AdminProduct>(`/api/admin/products/${id}`, {
      method: 'PUT',
      body: JSON.stringify({ asin }),
    }),

  deleteProduct: (id: string) =>
    request<void>(`/api/admin/products/${id}`, { method: 'DELETE' }),

  createCompetitor: (asin: string, linkedOwnProductId: string) =>
    request<AdminProduct>('/api/admin/competitors', {
      method: 'POST',
      body: JSON.stringify({ asin, linkedOwnProductId }),
    }),

  updateCompetitor: (id: string, asin: string, linkedOwnProductId: string) =>
    request<AdminProduct>(`/api/admin/competitors/${id}`, {
      method: 'PUT',
      body: JSON.stringify({ asin, linkedOwnProductId }),
    }),

  deleteCompetitor: (id: string) =>
    request<void>(`/api/admin/competitors/${id}`, { method: 'DELETE' }),

  crawlAll: () => request<{ crawledCount: number }>('/api/admin/crawl', { method: 'POST' }),

  crawlProduct: (id: string) =>
    request<AdminProduct>(`/api/admin/crawl/${id}`, { method: 'POST' }),
};
