package com.rocinante.scraping.crawlers;

import com.rocinante.scraping.html.RenderedHtml;

public interface Crawler<R> {
  CrawlerType type();

  R crawlUrl(String url, CrawlContext crawlContext);

  R crawlHtml(RenderedHtml html, String baseUrl, CrawlContext crawlContext);
}
