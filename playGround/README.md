# Amazon Product Intelligence Platform

A full-stack assignment platform that tracks Amazon product data via a mock crawler, stores price history in an in-memory JSON-backed store, and exposes a React dashboard plus a password-protected admin panel.

## What it does

1. **Crawler (backend)** — fetches mock Amazon product data by ASIN, stores results with timestamps for price history
2. **Product detail page** — name, description, image gallery, current price, price history chart, competitor comparison
3. **Admin panel** — CRUD for own/competitor ASINs, crawl status, manual crawl triggers

---

## Architecture

```
React (Vite)
    ↓
Spring Boot REST API
    ↓
StoreCoordinator (unit of work)  →  InMemoryStore  →  data/store.json (transactional)
    ↓
CrawlService  →  ProductCrawler (mock)  →  CatalogRepository  →  mock-catalog.json (master)
    ↓
CrawlScheduler (optional, every 6 hours via cron)
```

### Master vs transactional data

| Layer | Purpose | Mutable? | Storage |
|-------|---------|----------|---------|
| **Catalog master** | Mock Amazon product details (name, description, images, base price, seller) | No — reload on restart | `app.master.catalog-file` → `mock-catalog.json` |
| **Seed master** | Bootstrap list of ASINs to track on first run | No | `app.master.seed-file` → `seed-products.json` |
| **Transactional registry** | Tracked products, crawl status, competitor links | Yes — admin CRUD | `store.json` → `products[]` |
| **Transactional history** | Append-only price snapshots | Yes — crawl appends; delete cascades | `store.json` → `priceHistory{}` |
| **Last-crawl projection** | `name`, `description`, `imageUrls` on `Product` | Updated on successful crawl | Denormalized cache on `Product`, not master |

Master files are **never** written to `store.json`. On first boot (no `store.json`), `SeedDataLoader` reads `seed-products.json` and bootstraps the transactional registry.

### Backend package layout

```
com.amazon.intelligence
├── config/          AppProperties, Security, MockCrawlerConfiguration, CrawlConfigValidator
├── master/          CatalogEntry, CatalogRepository, SeedDataLoader (read-only masters)
├── domain/          Product, PriceSnapshot, ProductType, CrawlStatus
├── crawler/         ProductCrawler (interface), MockAmazonCrawler, CrawlResult
├── repository/      ProductRepository, PriceHistoryRepository
│   └── persistence/ InMemoryStore, JsonFilePersistence, StoreCoordinator, StoreBootstrap
├── service/         ProductService, CrawlService, PriceHistoryService
├── scheduler/       CrawlScheduler (config-gated)
└── web/             Controllers, DTOs, ProductMapper, GlobalExceptionHandler
```

### Design patterns

| Pattern | Where |
|---------|-------|
| Layered architecture | Controller → Service → Repository |
| Repository | `ProductRepository`, `PriceHistoryRepository` → `InMemoryStore` |
| Strategy | `ProductCrawler` interface; `MockAmazonCrawler` is the mock implementation |
| Unit of Work | `StoreCoordinator.mutate()` — apply changes, then flush once |
| DTO + Mapper | API responses via DTOs; `ProductMapper` converts domain → API |
| Configuration properties | `AppProperties` + `application.yml` |

---

## Data schemas

### Catalog master (`mock-catalog.json`)

Keyed by ASIN. Used by the crawler as the source of truth for mock Amazon data.

```json
{
  "B08N5WRWNW": {
    "name": "Echo Dot (4th Gen) Smart Speaker",
    "description": "...",
    "images": ["https://..."],
    "basePrice": 49.99,
    "currency": "USD",
    "seller": "Amazon.com"
  }
}
```

### Seed master (`seed-products.json`)

Loaded once on first startup when `data/store.json` does not exist.

```json
[
  { "asin": "B08N5WRWNW", "type": "OWN" },
  { "asin": "B07XJ8C8F5", "type": "COMPETITOR", "linkedOwnAsin": "B08N5WRWNW" }
]
```

### Transactional store (`data/store.json`)

```json
{
  "products": [
    {
      "id": "uuid",
      "asin": "B08N5WRWNW",
      "name": "Echo Dot (4th Gen) Smart Speaker",
      "description": "...",
      "imageUrls": ["https://..."],
      "type": "OWN",
      "linkedOwnProductId": null,
      "lastCrawlAt": "2026-06-28T17:54:29Z",
      "lastCrawlStatus": "SUCCESS",
      "lastCrawlError": null
    }
  ],
  "priceHistory": {
    "product-uuid": [
      {
        "id": "uuid",
        "productId": "product-uuid",
        "price": 49.99,
        "currency": "USD",
        "seller": "Amazon.com",
        "crawledAt": "2026-06-28T17:54:29Z"
      }
    ]
  }
}
```

Current price is derived from the latest `PriceSnapshot`, not stored directly on `Product`.

---

## Prerequisites

- Java 17+
- Maven 3.9+
- Node.js 18+

## Run the backend

```bash
mvn spring-boot:run
```

API base URL: `http://localhost:8080`

Delete `data/store.json` to re-bootstrap from `seed-products.json` on next startup.

## Run the frontend

```bash
cd frontend
npm install
npm run dev
```

UI: `http://localhost:5173` (proxies `/api` to the backend)

## Admin credentials

Configured in `src/main/resources/application.yml`:

- Username: `admin`
- Password: `admin123`

## Demo flow

1. Start backend and frontend.
2. Open **Admin panel** → sign in → click **Crawl all**.
3. Return to the dashboard and open a product to see images, description, price history chart, and competitor comparison.

---

## API overview

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/api/products` | Public | List own products |
| GET | `/api/products/{id}` | Public | Product detail + competitor prices |
| GET | `/api/products/{id}/price-history` | Public | Price snapshots for chart |
| GET | `/api/admin/products` | Basic | All products with crawl status |
| POST | `/api/admin/products` | Basic | Add own product ASIN |
| PUT | `/api/admin/products/{id}` | Basic | Update own product ASIN |
| DELETE | `/api/admin/products/{id}` | Basic | Delete own product + competitors |
| POST | `/api/admin/competitors` | Basic | Add competitor |
| PUT | `/api/admin/competitors/{id}` | Basic | Update competitor |
| DELETE | `/api/admin/competitors/{id}` | Basic | Delete competitor |
| POST | `/api/admin/crawl` | Basic | Crawl all products |
| POST | `/api/admin/crawl/{productId}` | Basic | Crawl one product |

---

## Transactional flows

All writes go through `StoreCoordinator.mutate()` — in-memory changes are applied inside a synchronized block, then `store.json` is flushed once per operation.

| Flow | Steps | Failure behavior |
|------|-------|------------------|
| Create own product | Validate unique ASIN → save `PENDING` → flush | Duplicate → 409 |
| Create competitor | Validate ASIN + own-product link → save → flush | Invalid link → 400 |
| Delete own product | Cascade delete competitors + history → flush | Single atomic `mutate()` |
| Crawl one product | Catalog lookup → on success: update projection + append snapshot; on failure: mark `FAILED` | Failed crawl does not add snapshot |
| Crawl all | Crawl each product → **one flush** at end | Per-product failures isolated |
| Read product detail | Read from transactional store only | No live crawl |

---

## Pre-seeded ASINs

**Catalog master** (`mock-catalog.json`) — product details the crawler can return:

| ASIN | Product |
|------|---------|
| B08N5WRWNW | Echo Dot (4th Gen) |
| B09V3KXJPB | Fire TV Stick 4K Max |
| B07XJ8C8F5 | Google Nest Mini |
| B0BSHF7WHW | Apple HomePod mini |
| B09G9FPHY6 | Fire TV Stick Lite |

**Seed master** (`seed-products.json`) — which ASINs are tracked and how competitors link to own products on first boot.

Unknown ASINs fail the crawl (`FAILED` status) and are retried on the next cycle.

---

## Configuration

```yaml
app:
  persistence:
    file: data/store.json
  master:
    catalog-file: classpath:mock-catalog.json   # supports classpath: and file:
    seed-file: classpath:seed-products.json
  crawl:
    impl: mock                    # only supported value; startup fails if unsupported
    scheduler-enabled: true       # false = no scheduled crawls; manual API still works
    schedule-cron: "0 0 */6 * * *"
    price-jitter-percent: 2       # mock price variation per crawl
    simulated-latency-ms-min: 100
    simulated-latency-ms-max: 300
  admin:
    username: admin
    password: admin123
```

| Property | Effect |
|----------|--------|
| `app.master.catalog-file` | Path to catalog master (mock Amazon data) |
| `app.master.seed-file` | Path to seed master (bootstrap ASINs) |
| `app.crawl.impl` | Crawler implementation (`mock` only for now) |
| `app.crawl.scheduler-enabled` | Enable/disable `CrawlScheduler` bean |
| `app.crawl.schedule-cron` | Cron expression for scheduled crawls |
| `app.persistence.file` | Transactional store file path |

To disable scheduled crawls while keeping manual crawl via admin API:

```yaml
app:
  crawl:
    scheduler-enabled: false
```

---

## Tests

```bash
mvn test
```

Covers catalog repository, seed loader, persistence round-trip, store coordinator, crawl service, mock crawler, API integration, and scheduler-disabled config.

---

## Future architecture: CQRS and Saga

**CQRS** (Command Query Responsibility Segregation) splits writes (commands) from optimized read models. This app already uses an informal version: admin POST/PUT/DELETE are commands, public GETs are queries, and `Product.name/description` after crawl is a read projection. Full CQRS would add separate read stores when dashboard reads and crawl writes need to scale independently — not needed at assignment scale.

**Saga** orchestrates multi-step distributed transactions with compensating actions across services. The current monolith uses `StoreCoordinator` as a single local unit of work — no saga required. A saga would matter in production if crawl became: API → job queue → crawler worker → product service → history service → notifications, where each step has its own database and failures need coordinated rollback/retry.

---

## Assignment notes

- **No real Amazon scraping** — crawler reads from catalog master and applies configurable price jitter.
- **No external DB** — `ConcurrentHashMap` + JSON file for transactional data only.
- **Basic Auth** — sufficient for a simple admin section.
- **Config-driven crawler/scheduler** — swap crawler implementation or disable scheduler via `application.yml` without code changes (mock only implemented today).
