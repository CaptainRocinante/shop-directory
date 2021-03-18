package com.rocinante.crawlers.summary;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class SummaryCrawler {
  private SubtreeTraversalResult dfs(Element root,
      PriorityQueue<SubtreeTraversalResult> highestLcsScoreHeap) {
    List<SubtreeTraversalResult> subtreeTraversalResults = new LinkedList<>();

    root
        .childNodes()
        .stream()
        .filter(node -> node instanceof Element)
        .forEach(node -> subtreeTraversalResults.add(dfs((Element) node, highestLcsScoreHeap)));

    final String rootTag;
    rootTag = root.tag().getName();
    SubtreeTraversalResult result = new SubtreeTraversalResult(rootTag, subtreeTraversalResults);
    highestLcsScoreHeap.add(result);
    return result;
  }

  public static void main(String[] args) throws InterruptedException, IOException {
    Document doc = Jsoup.parse(new File(
            SummaryCrawler.class.getClassLoader().getResource("dswsummarypage.html").getFile()),
    "utf-8");
//    Document doc = Jsoup.connect("https://www.dsw.com/").get();

//    final RenderedHtmlProvider renderedHtmlProvider = new RenderedHtmlProvider();
//    final String pageSource = renderedHtmlProvider.downloadHtml("https://www.dsw"
//        + ".com/en/us/brands/adidas/N-1z141hg");
//    System.out.println(pageSource);
//    final Document doc = Jsoup.parse(pageSource);

    SummaryCrawler summaryCrawler = new SummaryCrawler();
    PriorityQueue<SubtreeTraversalResult> highestLcsScoreHeap =
        new PriorityQueue<>((r1, r2) -> r2.getChildrenLCSScore() - r1.getChildrenLCSScore());
    SubtreeTraversalResult subtreeTraversalResult = summaryCrawler.dfs(doc, highestLcsScoreHeap);
  }
}
