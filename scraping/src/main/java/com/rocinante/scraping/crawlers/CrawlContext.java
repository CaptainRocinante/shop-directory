package com.rocinante.scraping.crawlers;

import java.util.Set;

public interface CrawlContext {
  boolean containsVariable(final String name);

  Set<String> getVariableNames();

  Object getVariable(final String name);
}
