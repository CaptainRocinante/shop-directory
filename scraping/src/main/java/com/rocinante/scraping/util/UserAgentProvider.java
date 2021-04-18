package com.rocinante.scraping.util;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Random;

public class UserAgentProvider {
  private static final ImmutableSet<String> USER_AGENT_FILES =
      new ImmutableSet.Builder<String>().add("user-agents/chrome-single.txt").build();

  private final List<String> userAgents;
  private final Random random;

  public UserAgentProvider() {
    this.userAgents = new ImmutableSet.Builder<String>()
        .add("Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36")
        .build()
        .asList();
    this.random = new Random();
  }

  public String getRandomUserAgent() {
    return userAgents.get(random.nextInt(userAgents.size())).trim();
  }
}
