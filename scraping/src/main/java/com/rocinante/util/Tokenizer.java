package com.rocinante.util;

import com.rocinante.lcs.StringLCSToken;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import opennlp.tools.tokenize.SimpleTokenizer;

public class Tokenizer {
  private static final Pattern ALPHA_NUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");
  private static final Pattern ONLY_NUMBERS_PATTERN = Pattern.compile("^[0-9]+$");

  public static List<StringLCSToken> alphaNumericLcsTokens(String sentence) {
    return Arrays.stream(SimpleTokenizer.INSTANCE.tokenize(sentence))
        .filter(token -> ALPHA_NUMERIC_PATTERN.matcher(token).matches())
        .filter(token -> !ONLY_NUMBERS_PATTERN.matcher(token).matches())
        .map(StringLCSToken::new)
        .collect(Collectors.toList());
  }

  public static void main(String[] args) {
    var tokens1 =
        alphaNumericLcsTokens(
            "https://www.gymshark.com/products/gymshark-2-16kg-resistance" + "-band-black");
    var tokens2 = alphaNumericLcsTokens("4LB TO 25LB RESISTANCE BAND");
  }
}
