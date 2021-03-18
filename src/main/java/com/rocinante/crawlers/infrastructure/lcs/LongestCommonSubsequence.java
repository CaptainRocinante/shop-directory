package com.rocinante.crawlers.infrastructure.lcs;

import java.util.List;

public class LongestCommonSubsequence {
  /* Returns length of LCS for X[0..m-1], Y[0..n-1] */
  private static int lcs(LCSToken[] X, LCSToken[] Y) {
    int m = X.length;
    int n = Y.length;
    int[][] L = new int[m+1][n+1];

    /* Following steps build L[m+1][n+1] in bottom up fashion. Note
         that L[i][j] contains length of LCS of X[0..i-1] and Y[0..j-1] */
    for (int i=0; i<=m; i++) {
      for (int j=0; j<=n; j++) {
        if (i == 0 || j == 0) {
          L[i][j] = 0;
        } else if (X[i-1].equals(Y[j-1])) {
          L[i][j] = L[i-1][j-1] + 1;
        } else {
          L[i][j] = Math.max(L[i-1][j], L[i][j-1]);
        }
      }
    }
    return L[m][n];
  }

  public static int computeLcs(List<LCSToken> a, List<LCSToken> b) {
    return lcs(a.toArray(new LCSToken[0]), b.toArray(new LCSToken[0]));
  }
}
