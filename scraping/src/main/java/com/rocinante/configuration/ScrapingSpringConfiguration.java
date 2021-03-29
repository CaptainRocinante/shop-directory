package com.rocinante.configuration;

import com.rocinante.crawlers.category.CategoryCrawler;
import com.rocinante.crawlers.summary.SummaryCrawler;
import com.rocinante.html.RenderedHtmlProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScrapingSpringConfiguration {
  @Bean
  public RenderedHtmlProvider renderedHtmlProvider() {
    return new RenderedHtmlProvider();
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
