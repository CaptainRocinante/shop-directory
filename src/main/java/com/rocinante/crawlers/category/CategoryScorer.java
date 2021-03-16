package com.rocinante.crawlers.category;

import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.jsoup.nodes.Element;

public class CategoryScorer {
  private static final ImmutableSet<String> CATEGORY_FILES =
      new ImmutableSet.Builder<String>()
          .add("overallCategories.txt")
          .add("clothesCategories.txt")
          .build();

  public final ImmutableSet<String> CATEGORIES;
  private final PorterStemmer porterStemmer;
  private final SimpleTokenizer tokenizer;


  private List<String> parseAllCategories() {
    return CATEGORY_FILES
        .stream()
        .map(cFile -> {
          try {
            return new File(
                Objects.requireNonNull(getClass().getClassLoader().getResource("categories/" + cFile)).toURI());
          } catch (URISyntaxException e) {
            throw new RuntimeException(e);
          }
        })
        .map(f -> {
          try {
            return Files.readAllLines(f.toPath(), Charset.defaultCharset());
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }


  public CategoryScorer() {
    this.porterStemmer = new PorterStemmer();
    this.tokenizer = SimpleTokenizer.INSTANCE;
    this.CATEGORIES = ImmutableSet.copyOf(new ImmutableSet.Builder<String>()
        .addAll(parseAllCategories())
        .build()
        .stream()
        .map(tokenizer::tokenize)
        .map(s -> String.join(" ", s))
        .map(String::toLowerCase)
        .map(porterStemmer::stem)
        .collect(Collectors.toSet()));
  }

  private boolean evalLinkText(String text) {
    return Arrays.stream(tokenizer.tokenize(text))
        .map(porterStemmer::stem)
        .map(String::toLowerCase)
        .anyMatch(CATEGORIES::contains);
  }

  public int score(Siblings siblings) {
    return siblings
        .getLinks()
        .stream()
        .map(Element::text)
        .reduce(0, (sum, text) -> sum + (evalLinkText(text) ? 1 : 0), Integer::sum);
  }
}
