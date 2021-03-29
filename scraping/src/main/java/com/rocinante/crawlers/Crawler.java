package com.rocinante.crawlers;

import com.rocinante.html.RenderedHtml;

public interface Crawler<R> {
  CrawlerType type();

  R crawlUrl(String url, CrawlContext crawlContext);

  R crawlHtml(RenderedHtml html, String baseUrl, CrawlContext crawlContext);
}
