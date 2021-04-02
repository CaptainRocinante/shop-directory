package com.rocinante.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.validator.routines.UrlValidator;

public class UrlUtils {
  private static final UrlValidator URL_VALIDATOR = new UrlValidator(
      new String[] {"http", "https"});

  public static List<String> extractImageUrlsFromSrcSet(String srcSet) {
    final String[] allSources = srcSet.split(",");
    return Arrays.stream(allSources)
        .map(String::trim)
        .map(s -> s.substring(0, s.contains(" ") ? s.indexOf(" ") : s.length()))
        .collect(Collectors.toList());
  }

  public static String domainRemovedUriRepresentation(URI uri) {
    return uri.getPath() + "?" + uri.getQuery() + "#" + uri.getFragment();
  }

  public static String getUrlOrUriStringRepresentation(String input) {
    try {
      return new URL(input).toString();
    } catch (MalformedURLException e) {
      try {
        return domainRemovedUriRepresentation(new URI(input));
      } catch (URISyntaxException ex) {
        return input;
      }
    }
  }

  public static boolean isValidUri(String url) {
    return URL_VALIDATOR.isValid(url);
  }
}
