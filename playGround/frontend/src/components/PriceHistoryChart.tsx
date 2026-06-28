import {
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import type { PriceSnapshot } from '../types';

interface PriceHistoryChartProps {
  snapshots: PriceSnapshot[];
  currency?: string | null;
}

export function PriceHistoryChart({ snapshots, currency }: PriceHistoryChartProps) {
  if (snapshots.length === 0) {
    return <p className="muted">No price history yet. Run a crawl from the admin panel.</p>;
  }

  const data = snapshots.map((s) => ({
    date: new Date(s.crawledAt).toLocaleString(),
    price: Number(s.price),
  }));

  return (
    <div className="chart-container">
      <ResponsiveContainer width="100%" height={320}>
        <LineChart data={data}>
          <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
          <XAxis dataKey="date" tick={{ fontSize: 12 }} />
          <YAxis tick={{ fontSize: 12 }} domain={['auto', 'auto']} />
          <Tooltip
            formatter={(value: number) => [`${currency ?? 'USD'} ${value.toFixed(2)}`, 'Price']}
          />
          <Line type="monotone" dataKey="price" stroke="#2563eb" strokeWidth={2} dot={{ r: 4 }} />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
