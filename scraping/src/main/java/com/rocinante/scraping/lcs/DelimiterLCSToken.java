package com.rocinante.scraping.lcs;

public class DelimiterLCSToken implements LCSToken {
  @Override
  public boolean equals(LCSToken otherLCSToken) {
    return otherLCSToken instanceof DelimiterLCSToken;
  }
}
