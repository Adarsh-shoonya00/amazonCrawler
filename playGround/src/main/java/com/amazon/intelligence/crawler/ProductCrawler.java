package com.amazon.intelligence.crawler;

public interface ProductCrawler {

    CrawlResult crawl(String asin) throws InterruptedException;
}
