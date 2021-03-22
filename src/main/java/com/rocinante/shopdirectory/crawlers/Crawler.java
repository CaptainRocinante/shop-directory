package com.rocinante.shopdirectory.crawlers;

public interface Crawler<R> {
  CrawlerType type();

  R crawlUrl(String url, CrawlContext crawlContext);

  R crawlHtml(String html, String baseUrl, CrawlContext crawlContext);
}
