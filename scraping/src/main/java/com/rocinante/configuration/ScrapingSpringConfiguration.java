package com.rocinante.configuration;

import com.rocinante.crawlers.category.CategoryCrawler;
import com.rocinante.crawlers.summary.SummaryCrawler;
import com.rocinante.html.RenderedHtmlProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScrapingSpringConfiguration {
  private final String scrapoxyUrl;
  private final String chromeDriverPath;

  public ScrapingSpringConfiguration(@Value("${scraping.scrapoxy.url}") String scrapoxyUrl,
      @Value("${scraping.chrome.driver.path}") String chromeDriverPath) {
    this.scrapoxyUrl = scrapoxyUrl;
    this.chromeDriverPath = chromeDriverPath;
  }

  @Bean
  public RenderedHtmlProvider renderedHtmlProvider() {
    return new RenderedHtmlProvider(scrapoxyUrl, chromeDriverPath);
  }

  @Bean
  public CategoryCrawler categoryCrawler(RenderedHtmlProvider renderedHtmlProvider) {
    return new CategoryCrawler(renderedHtmlProvider);
  }

  @Bean
  public SummaryCrawler summaryCrawler(RenderedHtmlProvider renderedHtmlProvider) {
    return new SummaryCrawler(renderedHtmlProvider);
  }
}
