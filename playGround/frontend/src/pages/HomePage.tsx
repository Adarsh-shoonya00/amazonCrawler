import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api/client';
import type { ProductSummary } from '../types';

export function HomePage() {
  const [products, setProducts] = useState<ProductSummary[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    api.listProducts()
      .then(setProducts)
      .catch((e) => setError(e instanceof Error ? e.message : 'Failed to load products'));
  }, []);

  return (
    <div className="page">
      <header className="hero">
        <div>
          <p className="eyebrow">Amazon Product Intelligence</p>
          <h1>Competitor pricing dashboard</h1>
          <p className="muted">
            Track your products, monitor competitor prices, and view historical trends.
          </p>
        </div>
        <Link to="/admin" className="button-link">Admin panel</Link>
      </header>

      {error && <p className="error">{error}</p>}

      <section className="card">
        <h2>Your products</h2>
        <div className="product-grid">
          {products.map((p) => (
            <Link key={p.id} to={`/products/${p.id}`} className="product-card">
              <p className="eyebrow">{p.asin}</p>
              <h3>{p.name ?? 'Awaiting first crawl'}</h3>
              <span className={`badge badge-${p.lastCrawlStatus.toLowerCase()}`}>
                {p.lastCrawlStatus}
              </span>
            </Link>
          ))}
        </div>
        {products.length === 0 && (
          <p className="muted">No products yet. Add them from the admin panel.</p>
        )}
      </section>
    </div>
  );
}
