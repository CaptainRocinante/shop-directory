package com.rocinante.client;

import com.opencsv.bean.CsvToBeanBuilder;
import com.rocinante.common.api.dto.MerchantCrawlDto;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/*
Usage:

Scrape all categories for a merchant:

java -jar target/clients-1.0-SNAPSHOT.jar <API_KEY> <CSV_PATH> <NOT_CRAWLED_IN_X_DAYS> categories

Scrape all products for a merchant:

java -jar target/clients-1.0-SNAPSHOT.jar <API_KEY> <CSV_PATH> <NOT_CRAWLED_IN_X_DAYS> products
 */
@Slf4j
public class ScrapingClient {
  private static final String BASE_SCRAPE_API = "https://api.paylatergoods.com/crawl";
  private static final String API_KEY_HEADER = "apiKey";

  private final String apiKey;
  private final Client webClient;

  public ScrapingClient(String apiKey) {
    this.apiKey = apiKey;
    this.webClient = ClientBuilder.newClient();
  }

  private void scrapeAllCategoriesForMerchant(UUID merchantUuid, int daysBefore) {
    log.info("Invoking category crawl for {} with daysBefore {}", merchantUuid, daysBefore);
    final MerchantCrawlDto merchantCrawlDto =
        new MerchantCrawlDto(merchantUuid.toString(), daysBefore);

    final Response rs =
        webClient
            .target(BASE_SCRAPE_API)
            .path("categories")
            .request(MediaType.APPLICATION_JSON)
            .header(API_KEY_HEADER, apiKey)
            .buildPost(Entity.entity(merchantCrawlDto, MediaType.APPLICATION_JSON))
            .invoke();
    log.info("Response code {}", rs.getStatus());
  }

  private void scrapeAllProductsForMerchant(UUID merchantUuid, int daysBefore) {
    log.info("Invoking product crawl for {} with daysBefore {}", merchantUuid, daysBefore);
    final MerchantCrawlDto merchantCrawlDto =
        new MerchantCrawlDto(merchantUuid.toString(), daysBefore);

    final Response rs =
        webClient
            .target(BASE_SCRAPE_API)
            .path("products")
            .request(MediaType.APPLICATION_JSON)
            .header(API_KEY_HEADER, apiKey)
            .buildPost(Entity.entity(merchantCrawlDto, MediaType.APPLICATION_JSON))
            .invoke();
    log.info("Response code {}", rs.getStatus());
  }

  public static void main(String[] args) throws FileNotFoundException {
    if (args.length < 4) {
      throw new IllegalStateException("Missing arguments");
    }

    final String apiKey = args[0];
    final String merchantUuidCsvPath = args[1];
    final int notCrawledInDays = Integer.parseInt(args[2]);
    final Mode mode = Mode.fromKey(args[3]);

    final List<UUID> merchantUuids =
        new CsvToBeanBuilder<MerchantCsvDto>(new FileReader(merchantUuidCsvPath))
            .withType(MerchantCsvDto.class)
                .withSkipLines(1) // Skip CSV 1st line with column descriptions
                .build().stream()
                .map(MerchantCsvDto::getMerchantUuid)
                .map(UUID::fromString)
                .collect(Collectors.toList());

    final ScrapingClient scrapingClient = new ScrapingClient(apiKey);
    switch (mode) {
      case CATEGORIES:
        log.info("Running in categories mode");
        merchantUuids.forEach(
            u -> scrapingClient.scrapeAllCategoriesForMerchant(u, notCrawledInDays));
        break;
      case PRODUCTS:
        log.info("Running in products mode");
        merchantUuids.forEach(
            u -> scrapingClient.scrapeAllProductsForMerchant(u, notCrawledInDays));
        break;
      default:
        throw new IllegalStateException("Unsupported scraping mode");
    }
  }
}
