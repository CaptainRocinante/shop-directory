package com.rocinante.crawlers.summary;

import java.io.File;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class SummaryCrawler {
  private void bfs(Document document) {

  }

  public static void main(String[] args) throws IOException {
    Document doc =
        Jsoup.parse(
            new File(SummaryCrawler.class.getClassLoader()
                .getResource("herschelmensnewarrivals.html").getFile()),
            "utf-8");
    SummaryCrawler summaryCrawler = new SummaryCrawler();
    summaryCrawler.bfs(doc);
  }
}
