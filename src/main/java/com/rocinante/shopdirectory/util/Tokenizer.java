package com.rocinante.shopdirectory.util;

import com.rocinante.shopdirectory.lcs.StringLCSToken;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import opennlp.tools.tokenize.SimpleTokenizer;

public class Tokenizer {
  public static List<StringLCSToken> simpleLcsTokens(String sentence) {
    return Arrays
        .stream(SimpleTokenizer.INSTANCE.tokenize(sentence))
        .map(StringLCSToken::new)
        .collect(Collectors.toList());
  }

  public static void main(String[] args) {
    var tokens1 = simpleLcsTokens("https://www.gymshark.com/products/gymshark-2-16kg-resistance"
       + "-band-black");
    var tokens2 = simpleLcsTokens("4LB TO 25LB RESISTANCE BAND");
  }
}
