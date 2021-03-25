package com.rocinante.shopdirectory.crawlers;

import com.rocinante.shopdirectory.html.RenderedHtml;

public interface Crawler<R> {
  CrawlerType type();

  R crawlUrl(String url, CrawlContext crawlContext);

  R crawlHtml(RenderedHtml html, String baseUrl, CrawlContext crawlContext);
}
