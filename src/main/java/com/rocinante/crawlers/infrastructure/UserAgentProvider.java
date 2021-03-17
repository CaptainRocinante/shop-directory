package com.rocinante.crawlers.infrastructure;

import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class UserAgentProvider {
  private static final ImmutableSet<String> USER_AGENT_FILES =
      new ImmutableSet.Builder<String>()
          .add("user-agents/chrome.txt")
          .build();

  private final List<String> userAgents;
  private final Random random;

  public UserAgentProvider() {
    this.userAgents = USER_AGENT_FILES
        .stream()
        .map(cFile -> {
          try {
            return new File(
                Objects.requireNonNull(getClass().getClassLoader().getResource(cFile)).toURI());
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
    this.random = new Random();
  }

  public String getRandomUserAgent() {
    return userAgents.get(random.nextInt(userAgents.size())).trim();
  }

  public static void main(String[] args) {
    UserAgentProvider userAgentProvider = new UserAgentProvider();
    System.out.print(userAgentProvider.getRandomUserAgent());
  }
}
