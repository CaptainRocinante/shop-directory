package com.rocinante.client;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

/*
Usage:

Scrape all categories for a merchant:

java -jar target/clients-1.0-SNAPSHOT.jar <API_KEY> <CSV_PATH> categories

Scrape all products for a merchant:

java -jar target/clients-1.0-SNAPSHOT.jar <API_KEY> <CSV_PATH> products
 */
@Slf4j
public class ScrapingClient {
  private final String apiKey;

  public ScrapingClient(String apiKey) {
    this.apiKey = apiKey;
  }

  private void scrapeAllCategoriesForMerchant(UUID merchantUuid) {

  }

  private void scrapeAllProductsForMerchant(UUID merchantUuid) {

  }

  public static void main(String[] args) {
    if (args.length < 3) {
      throw new IllegalStateException("Missing arguments");
    }

    log.info(args[0] + " " + args[1] + " " + args[2]);

    final String apiKey = args[0];
    final String merchantUuidCsvPath = args[1];
    final Mode mode = Mode.fromKey(args[2]);



    switch (mode) {
      case CATEGORIES:
        break;
      case PRODUCTS:
        break;
      default:
        throw new IllegalStateException("Unsupported scraping mode");
    }
  }
}
