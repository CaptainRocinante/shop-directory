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

  public ScrapingSpringConfiguration(@Value("${scrapoxy.url}") String scrapoxyUrl) {
    this.scrapoxyUrl = scrapoxyUrl;
  }

  @Bean
  public RenderedHtmlProvider renderedHtmlProvider() {
    return new RenderedHtmlProvider(scrapoxyUrl);
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
