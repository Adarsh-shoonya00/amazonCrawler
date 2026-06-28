package com.amazon.intelligence.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Persistence persistence = new Persistence();
    private final Master master = new Master();
    private final Crawl crawl = new Crawl();
    private final Admin admin = new Admin();

    public Persistence getPersistence() {
        return persistence;
    }

    public Master getMaster() {
        return master;
    }

    public Crawl getCrawl() {
        return crawl;
    }

    public Admin getAdmin() {
        return admin;
    }

    public static class Persistence {
        private String file = "data/store.json";

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }
    }

    public static class Master {
        private String catalogFile = "classpath:mock-catalog.json";
        private String seedFile = "classpath:seed-products.json";

        public String getCatalogFile() {
            return catalogFile;
        }

        public void setCatalogFile(String catalogFile) {
            this.catalogFile = catalogFile;
        }

        public String getSeedFile() {
            return seedFile;
        }

        public void setSeedFile(String seedFile) {
            this.seedFile = seedFile;
        }
    }

    public static class Crawl {
        private String impl = "mock";
        private boolean schedulerEnabled = true;
        private String scheduleCron = "0 0 */6 * * *";
        private int priceJitterPercent = 2;
        private int simulatedLatencyMsMin = 100;
        private int simulatedLatencyMsMax = 300;

        public String getImpl() {
            return impl;
        }

        public void setImpl(String impl) {
            this.impl = impl;
        }

        public boolean isSchedulerEnabled() {
            return schedulerEnabled;
        }

        public void setSchedulerEnabled(boolean schedulerEnabled) {
            this.schedulerEnabled = schedulerEnabled;
        }

        public String getScheduleCron() {
            return scheduleCron;
        }

        public void setScheduleCron(String scheduleCron) {
            this.scheduleCron = scheduleCron;
        }

        public int getPriceJitterPercent() {
            return priceJitterPercent;
        }

        public void setPriceJitterPercent(int priceJitterPercent) {
            this.priceJitterPercent = priceJitterPercent;
        }

        public int getSimulatedLatencyMsMin() {
            return simulatedLatencyMsMin;
        }

        public void setSimulatedLatencyMsMin(int simulatedLatencyMsMin) {
            this.simulatedLatencyMsMin = simulatedLatencyMsMin;
        }

        public int getSimulatedLatencyMsMax() {
            return simulatedLatencyMsMax;
        }

        public void setSimulatedLatencyMsMax(int simulatedLatencyMsMax) {
            this.simulatedLatencyMsMax = simulatedLatencyMsMax;
        }
    }

    public static class Admin {
        private String username = "admin";
        private String password = "admin123";

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
