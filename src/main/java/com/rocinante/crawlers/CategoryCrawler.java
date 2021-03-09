package com.rocinante.crawlers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CategoryCrawler {
    private static class Siblings {
        private final List<Element> siblings;

        public Siblings() {
            this.siblings = new ArrayList<>();
        }

        public Siblings(List<Element> siblings) {
            this.siblings = siblings;
        }

        public void addElement(Element e) {
            this.siblings.add(e);
        }

        public static List<Siblings> createSiblingsFromChildNodes(List<Node> childNodes) {
            Map<String, Siblings> sameTagSiblings = new HashMap<>();
            childNodes
                    .stream()
                    .filter(x -> x instanceof Element)
                    .map(x -> (Element) x)
                    .forEach(element -> {
                        String elementTag = element.tag().getName();
                        if (sameTagSiblings.containsKey(elementTag)) {
                            sameTagSiblings.get(elementTag).addElement(element);
                        } else {
                            Siblings siblings = new Siblings();
                            siblings.addElement(element);
                            sameTagSiblings.put(elementTag, siblings);
                        }
                    });
            return new ArrayList<>(sameTagSiblings.values());
        }

        public List<Element> getLinks() {
            return siblings
                    .stream()
                    .filter(e -> e.tag().getName().equals("a"))
                    .collect(Collectors.toList());
        }

        public void printAllLinks() {
            if (getLinks().size() != 0) {
                System.out.println("\n-------------------\nPrinting a set of Sibling links\n-------------------\n");
            }

            getLinks().forEach(l -> System.out.println("Link: " + l));
        }

        public List<Siblings> getChildren() {
            return Siblings.createSiblingsFromChildNodes(
                    siblings
                            .stream()
                            .map(Node::childNodes)
                            .flatMap(List::stream)
                            .collect(Collectors.toList())
            );
        }

//
//        public int categoryContainerScore() {
//            for (int i = 0; i < siblings.size(); ++i) {
//                Node currentSibling = siblings.get(i);
//
//            }
//        }
    }

    private static void bfs(Document document) {
        final Queue<Siblings> bfsQueue = new LinkedList<>(Siblings.createSiblingsFromChildNodes(document.childNodes()));

        int level = 0;
        while(!bfsQueue.isEmpty()) {
            int currentLevelCount = bfsQueue.size();

            System.out.println("List for level " + level + " of size " + currentLevelCount);

            for (int i = 0; i < currentLevelCount; ++i) {
                Siblings current = bfsQueue.poll();
                current.printAllLinks();
                bfsQueue.addAll(current.getChildren());
            }
            ++level;
        }
    }

    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.parse(new File(CategoryCrawler.class.getClassLoader().getResource("herschel.html").getFile()),
                "utf-8");
        bfs(doc);
    }
}
