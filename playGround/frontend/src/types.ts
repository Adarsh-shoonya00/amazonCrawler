export type ProductType = 'OWN' | 'COMPETITOR';
export type CrawlStatus = 'PENDING' | 'SUCCESS' | 'FAILED';

export interface ProductSummary {
  id: string;
  asin: string;
  name: string | null;
  type: ProductType;
  lastCrawlStatus: CrawlStatus;
  lastCrawlAt: string | null;
}

export interface CompetitorPrice {
  id: string;
  asin: string;
  name: string | null;
  currentPrice: number | null;
  currency: string | null;
  seller: string | null;
  priceDelta: number | null;
}

export interface ProductDetail {
  id: string;
  asin: string;
  name: string | null;
  description: string | null;
  imageUrls: string[];
  currentPrice: number | null;
  currency: string | null;
  seller: string | null;
  lastCrawlStatus: CrawlStatus;
  lastCrawlAt: string | null;
  competitors: CompetitorPrice[];
}

export interface PriceSnapshot {
  id: string;
  price: number;
  currency: string;
  seller: string;
  crawledAt: string;
}

export interface AdminProduct {
  id: string;
  asin: string;
  name: string | null;
  type: ProductType;
  linkedOwnProductId: string | null;
  linkedOwnProductName: string | null;
  lastCrawlStatus: CrawlStatus;
  lastCrawlAt: string | null;
  lastCrawlError: string | null;
}
