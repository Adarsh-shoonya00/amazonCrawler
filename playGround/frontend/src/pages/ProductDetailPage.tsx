import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { api } from '../api/client';
import { PriceHistoryChart } from '../components/PriceHistoryChart';
import type { PriceSnapshot, ProductDetail } from '../types';

export function ProductDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [product, setProduct] = useState<ProductDetail | null>(null);
  const [history, setHistory] = useState<PriceSnapshot[]>([]);
  const [selectedImage, setSelectedImage] = useState(0);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!id) return;

    const productId = id;

    async function load() {
      try {
        setLoading(true);
        const [detail, priceHistory] = await Promise.all([
          api.getProduct(productId),
          api.getPriceHistory(productId),
        ]);
        setProduct(detail);
        setHistory(priceHistory);
        setError(null);
      } catch (e) {
        setError(e instanceof Error ? e.message : 'Failed to load product');
      } finally {
        setLoading(false);
      }
    }

    load();
  }, [id]);

  if (loading) return <p className="muted">Loading product...</p>;
  if (error) return <p className="error">{error}</p>;
  if (!product) return <p className="muted">Product not found.</p>;

  const images = product.imageUrls?.length ? product.imageUrls : [];

  return (
    <div className="page">
      <Link to="/" className="back-link">← All products</Link>

      <header className="product-header">
        <div>
          <p className="eyebrow">ASIN {product.asin}</p>
          <h1>{product.name ?? 'Unnamed product'}</h1>
          <p className="seller">
            {product.currentPrice != null
              ? `${product.currency ?? 'USD'} ${product.currentPrice.toFixed(2)}`
              : 'Price unavailable'}
            {product.seller ? ` · sold by ${product.seller}` : ''}
          </p>
        </div>
        <span className={`badge badge-${product.lastCrawlStatus.toLowerCase()}`}>
          {product.lastCrawlStatus}
        </span>
      </header>

      <div className="grid-two">
        <section className="card">
          <h2>Gallery</h2>
          {images.length > 0 ? (
            <>
              <img
                src={images[selectedImage]}
                alt={product.name ?? product.asin}
                className="hero-image"
              />
              <div className="thumbnails">
                {images.map((url, index) => (
                  <button
                    key={url}
                    type="button"
                    className={index === selectedImage ? 'thumb active' : 'thumb'}
                    onClick={() => setSelectedImage(index)}
                  >
                    <img src={url} alt="" />
                  </button>
                ))}
              </div>
            </>
          ) : (
            <p className="muted">No images yet. Run a crawl to populate product data.</p>
          )}
        </section>

        <section className="card">
          <h2>Description</h2>
          <p>{product.description ?? 'No description available.'}</p>
        </section>
      </div>

      <section className="card">
        <h2>Price history</h2>
        <PriceHistoryChart snapshots={history} currency={product.currency} />
      </section>

      {product.competitors.length > 0 && (
        <section className="card">
          <h2>Competitor comparison</h2>
          <table className="data-table">
            <thead>
              <tr>
                <th>Product</th>
                <th>ASIN</th>
                <th>Price</th>
                <th>Delta vs yours</th>
                <th>Seller</th>
              </tr>
            </thead>
            <tbody>
              {product.competitors.map((c) => (
                <tr key={c.id}>
                  <td>{c.name ?? '—'}</td>
                  <td>{c.asin}</td>
                  <td>
                    {c.currentPrice != null
                      ? `${c.currency ?? 'USD'} ${c.currentPrice.toFixed(2)}`
                      : '—'}
                  </td>
                  <td className={c.priceDelta != null && c.priceDelta < 0 ? 'positive' : 'negative'}>
                    {c.priceDelta != null
                      ? `${c.priceDelta >= 0 ? '+' : ''}${c.priceDelta.toFixed(2)}`
                      : '—'}
                  </td>
                  <td>{c.seller ?? '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      )}
    </div>
  );
}
