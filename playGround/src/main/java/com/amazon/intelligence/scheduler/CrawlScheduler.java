package com.amazon.intelligence.scheduler;

import com.amazon.intelligence.config.AppProperties;
import com.amazon.intelligence.service.CrawlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.crawl.scheduler-enabled", havingValue = "true", matchIfMissing = true)
public class CrawlScheduler {

    private static final Logger log = LoggerFactory.getLogger(CrawlScheduler.class);

    private final CrawlService crawlService;
    private final AppProperties appProperties;

    public CrawlScheduler(CrawlService crawlService, AppProperties appProperties) {
        this.crawlService = crawlService;
        this.appProperties = appProperties;
    }

    @Scheduled(cron = "${app.crawl.schedule-cron}")
    public void scheduledCrawl() {
        log.info("Starting scheduled crawl (cron: {})", appProperties.getCrawl().getScheduleCron());
        crawlService.crawlAll();
        log.info("Scheduled crawl completed");
    }
}
