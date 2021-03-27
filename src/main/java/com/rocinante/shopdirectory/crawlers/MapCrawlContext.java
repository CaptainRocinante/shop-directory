package com.rocinante.shopdirectory.crawlers;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class MapCrawlContext implements CrawlContext {
  private final Map<String, Object> variables;

  public MapCrawlContext(@Nullable Map<String, Object> variables) {
    this.variables = (variables == null ? new LinkedHashMap<>(10) : new LinkedHashMap<>(variables));
  }

  @Override
  public boolean containsVariable(String name) {
    return this.variables.containsKey(name);
  }

  @Override
  public Set<String> getVariableNames() {
    return this.variables.keySet();
  }

  @Override
  public Object getVariable(String name) {
    return this.variables.get(name);
  }
}
