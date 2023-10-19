package uk.ac.rhul.cs.csle.art.cfg;

/* This class provides both a compact, rapidly iterable representation of TWE sets AND a fast loader
 *
 * We exploit the observation that tokens with stropped patterns like 'this' have only one string in their patterns and are thus fixed length
 * We call these tokens singleton tokens and maintaion a separate lookup table containing their lengths
 *
 */
public class TWESet {
  final int[] tokenLengths;
  final int[][] singletonTokens; // l,t_l
  final int[][][] nonSingletonTokens; // l,r_l,t_lr
  int currentL; // The current input pointer, hence left extent
  final int[][] currentSets; // t, n for t in T - only non-singleton tokens need the second dimension
  final int[][] currentLists; // t_ns, n for t in T_ns - only non-singleton tokens need the first dimension
  final int[] currentListExtents; // t_ns, n for t in T_ns - only non-singleton tokens need the first dimension

  TWESet(int n, int[] tokenLengths) {
    this.tokenLengths = tokenLengths;
    singletonTokens = new int[n][];
    nonSingletonTokens = new int[n][][];
    currentL = 0;
    currentListExtents = new int[n];
    currentLists = new int[n][];
    currentSets = new int[n][];
    for (int i = 0; i < tokenLengths.length; i++)
      if (tokenLengths[i] == 0) {
        currentLists[i] = new int[n];
        currentSets[i] = new int[n];
      }
  }

  @Override
  public String toString() {
    return "";
  }

  public String counts() {
    return "";
  }

  public void dump(String filename) {

  }

  public void undump(String filename) {

  }

}
