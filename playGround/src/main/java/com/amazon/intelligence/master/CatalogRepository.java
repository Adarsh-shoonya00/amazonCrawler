package com.amazon.intelligence.master;

import java.util.Optional;

public interface CatalogRepository {

    Optional<CatalogEntry> findByAsin(String asin);
}
