package com.rocinante.crawlers.summary;

import com.rocinante.crawlers.infrastructure.RenderedHtmlProvider;
import java.io.File;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class SummaryCrawler {
  private void bfs(Document document) {

  }

  public static void main(String[] args) throws InterruptedException {
    final RenderedHtmlProvider renderedHtmlProvider = new RenderedHtmlProvider();
    final String pageSource = renderedHtmlProvider.downloadHtml("https://www.dsw"
        + ".com/en/us/brands/adidas/N-1z141hg");
    System.out.println(pageSource);
    final Document doc = Jsoup.parse(pageSource);

    SummaryCrawler summaryCrawler = new SummaryCrawler();
    summaryCrawler.bfs(doc);
  }
}
