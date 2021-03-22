package com.rocinante.shopdirectory.lcs;

public class StringLCSToken implements LCSToken {
  private final String str;

  public StringLCSToken(String str) {
    this.str = str;
  }

  @Override
  public boolean equals(LCSToken otherLCSToken) {
    if (!(otherLCSToken instanceof StringLCSToken)) {
      return false;
    } else {
      return str.equals(((StringLCSToken) otherLCSToken).str);
    }
  }
}
