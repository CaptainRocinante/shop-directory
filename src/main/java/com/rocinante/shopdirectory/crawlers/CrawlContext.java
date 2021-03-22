package com.rocinante.shopdirectory.crawlers;

import java.util.Set;

public interface CrawlContext {
  boolean containsVariable(final String name);
  Set<String> getVariableNames();
  Object getVariable(final String name);
}
