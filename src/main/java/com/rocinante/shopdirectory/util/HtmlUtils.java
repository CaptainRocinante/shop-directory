package com.rocinante.shopdirectory.util;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HtmlUtils {
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
}
