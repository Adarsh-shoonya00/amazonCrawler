import { FormEvent, useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api, clearAuth, isAuthenticated, setAuth } from '../api/client';
import type { AdminProduct } from '../types';

type Tab = 'own' | 'competitors';

export function AdminPage() {
  const [loggedIn, setLoggedIn] = useState(isAuthenticated());
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('admin123');
  const [loginError, setLoginError] = useState<string | null>(null);

  const [tab, setTab] = useState<Tab>('own');
  const [products, setProducts] = useState<AdminProduct[]>([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const [ownAsin, setOwnAsin] = useState('');
  const [compAsin, setCompAsin] = useState('');
  const [linkedOwnId, setLinkedOwnId] = useState('');

  const loadProducts = useCallback(async () => {
    setLoading(true);
    try {
      const data = await api.listAdminProducts();
      setProducts(data);
      setError(null);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to load products');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (loggedIn) {
      loadProducts();
    }
  }, [loggedIn, loadProducts]);

  async function handleLogin(e: FormEvent) {
    e.preventDefault();
    setAuth(username, password);
    try {
      await api.listAdminProducts();
      setLoggedIn(true);
      setLoginError(null);
    } catch {
      clearAuth();
      setLoggedIn(false);
      setLoginError('Invalid credentials');
    }
  }

  function handleLogout() {
    clearAuth();
    setLoggedIn(false);
  }

  const ownProducts = products.filter((p) => p.type === 'OWN');
  const competitors = products.filter((p) => p.type === 'COMPETITOR');
  const visible = tab === 'own' ? ownProducts : competitors;

  async function handleCreateOwn(e: FormEvent) {
    e.preventDefault();
    try {
      await api.createProduct(ownAsin);
      setOwnAsin('');
      setMessage('Own product added');
      await loadProducts();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to add product');
    }
  }

  async function handleCreateCompetitor(e: FormEvent) {
    e.preventDefault();
    try {
      await api.createCompetitor(compAsin, linkedOwnId);
      setCompAsin('');
      setMessage('Competitor added');
      await loadProducts();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to add competitor');
    }
  }

  async function handleDelete(id: string, type: 'OWN' | 'COMPETITOR') {
    if (!confirm('Delete this product?')) return;
    try {
      if (type === 'OWN') {
        await api.deleteProduct(id);
      } else {
        await api.deleteCompetitor(id);
      }
      setMessage('Deleted');
      await loadProducts();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Delete failed');
    }
  }

  async function handleCrawlAll() {
    try {
      const result = await api.crawlAll();
      setMessage(`Crawled ${result.crawledCount} products`);
      await loadProducts();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Crawl failed');
    }
  }

  async function handleCrawlOne(id: string) {
    try {
      await api.crawlProduct(id);
      setMessage('Crawl completed');
      await loadProducts();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Crawl failed');
    }
  }

  if (!loggedIn) {
    return (
      <div className="page narrow">
        <h1>Admin login</h1>
        <form className="card form-card" onSubmit={handleLogin}>
          <label>
            Username
            <input value={username} onChange={(e) => setUsername(e.target.value)} />
          </label>
          <label>
            Password
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </label>
          {loginError && <p className="error">{loginError}</p>}
          <button type="submit">Sign in</button>
        </form>
        <Link to="/" className="back-link">← Back to dashboard</Link>
      </div>
    );
  }

  return (
    <div className="page">
      <div className="admin-toolbar">
        <div>
          <h1>Admin panel</h1>
          <p className="muted">Manage product IDs and trigger crawls</p>
        </div>
        <div className="toolbar-actions">
          <button type="button" onClick={handleCrawlAll}>Crawl all</button>
          <button type="button" className="secondary" onClick={handleLogout}>Logout</button>
          <Link to="/" className="button-link secondary">Dashboard</Link>
        </div>
      </div>

      {message && <p className="success">{message}</p>}
      {error && <p className="error">{error}</p>}

      <div className="tabs">
        <button
          type="button"
          className={tab === 'own' ? 'tab active' : 'tab'}
          onClick={() => setTab('own')}
        >
          Own products
        </button>
        <button
          type="button"
          className={tab === 'competitors' ? 'tab active' : 'tab'}
          onClick={() => setTab('competitors')}
        >
          Competitors
        </button>
      </div>

      {tab === 'own' ? (
        <form className="card form-inline" onSubmit={handleCreateOwn}>
          <input
            placeholder="ASIN e.g. B08N5WRWNW"
            value={ownAsin}
            onChange={(e) => setOwnAsin(e.target.value)}
            required
          />
          <button type="submit">Add own product</button>
        </form>
      ) : (
        <form className="card form-inline" onSubmit={handleCreateCompetitor}>
          <input
            placeholder="Competitor ASIN"
            value={compAsin}
            onChange={(e) => setCompAsin(e.target.value)}
            required
          />
          <select value={linkedOwnId} onChange={(e) => setLinkedOwnId(e.target.value)} required>
            <option value="">Link to own product</option>
            {ownProducts.map((p) => (
              <option key={p.id} value={p.id}>
                {p.asin} {p.name ? `— ${p.name}` : ''}
              </option>
            ))}
          </select>
          <button type="submit">Add competitor</button>
        </form>
      )}

      <section className="card">
        {loading ? (
          <p className="muted">Loading...</p>
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th>ASIN</th>
                <th>Name</th>
                {tab === 'competitors' && <th>Linked own product</th>}
                <th>Last crawl</th>
                <th>Status</th>
                <th>Error</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {visible.map((p) => (
                <tr key={p.id}>
                  <td>{p.asin}</td>
                  <td>{p.name ?? '—'}</td>
                  {tab === 'competitors' && <td>{p.linkedOwnProductName ?? p.linkedOwnProductId}</td>}
                  <td>{p.lastCrawlAt ? new Date(p.lastCrawlAt).toLocaleString() : '—'}</td>
                  <td>
                    <span className={`badge badge-${p.lastCrawlStatus.toLowerCase()}`}>
                      {p.lastCrawlStatus}
                    </span>
                  </td>
                  <td className="error-cell">{p.lastCrawlError ?? '—'}</td>
                  <td className="actions">
                    <button type="button" onClick={() => handleCrawlOne(p.id)}>Crawl</button>
                    <button
                      type="button"
                      className="danger"
                      onClick={() => handleDelete(p.id, p.type)}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </div>
  );
}
