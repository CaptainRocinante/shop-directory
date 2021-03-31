package com.rocinante.util;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.validator.routines.UrlValidator;

public class HtmlUtils {
  private static final UrlValidator URL_VALIDATOR = new UrlValidator(
      new String[] {"http", "https"});

  public static List<String> extractImageUrlsFromSrcSet(String srcSet) {
    final String[] allSources = srcSet.split(",");
    return Arrays.stream(allSources)
        .map(String::trim)
        .map(s -> s.substring(0, s.contains(" ") ? s.indexOf(" ") : s.length()))
        .collect(Collectors.toList());
  }

  public static String normalizedUriRepresentation(URI uri) {
    return uri.getPath() + "?" + uri.getQuery() + "#" + uri.getFragment();
  }

  public static boolean isValidUri(String url) {
    return URL_VALIDATOR.isValid(url);
  }

  public static void main(String[] args) {
    System.out.println(HtmlUtils.isValidUri("mailto:service@aritzia.com"));
  }
}
