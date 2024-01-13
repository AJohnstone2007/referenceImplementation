package uk.ac.rhul.cs.csle.art.cfg.lexer;

import java.util.ArrayList;

import uk.ac.rhul.cs.csle.art.cfg.LexerBase;
import uk.ac.rhul.cs.csle.art.cfg.grammar.LKind;

public class LexerLM extends LexerBase {
  private ArrayList<Integer> tokenList;
  private ArrayList<Integer> positionList;
  public int[] tokens;
  public int[] positions;

  @Override
  public void lex(String inputString, LKind[] kinds, String[] strings, LKind[] whitespaces) {
    this.inputString = inputString + "\0";
    this.kinds = kinds;
    this.strings = strings;
    this.whitespaces = whitespaces;

    input = inputString.toCharArray();
    inputIndex = 0;
    inputLength = inputString.length();

    tokenList = new ArrayList<>();
    positionList = new ArrayList<>();
    tokens = positions = null;

    longestMatchRightIndex = 0;
    longestMatchToken = 0;
    // System.out.println("Input: " + inputString);

    while (inputIndex < input.length) {
      // Absorb a run of whitespace lexemes
      while (true) {
        int wsStart = inputIndex;

        for (LKind w : whitespaces)
          processBuiltin(w, null);
        if (inputIndex == wsStart) break;
      }

      if (inputIndex == inputLength) break;

      leftIndex = inputIndex;

      for (int token = 1; token < kinds.length; token++) {
        processBuiltin(kinds[token], strings[token]);
        checkLongestMatch(token);
      }

      if (longestMatchRightIndex == leftIndex) { // We matched nothing, which is an error
        lexicalError("Unrecognised lexeme");
        tokenList = null;
        return;
      }

      tokenList.add(longestMatchToken);
      positionList.add(leftIndex);
      inputIndex = longestMatchRightIndex;
    }
    tokenList.add(0); // Terminating EOS
    positionList.add(inputString.length());
    tokens = new int[tokenList.size()];
    positions = new int[tokenList.size()];
    for (int i = 0; i < tokens.length; i++) {
      tokens[i] = tokenList.get(i);
      positions[i] = positionList.get(i);
    }
  }

  private void checkLongestMatch(int token) {
    if (inputIndex > longestMatchRightIndex) {
      longestMatchRightIndex = inputIndex;
      longestMatchToken = token;
      inputIndex = leftIndex;
    }
  }

  public void report() {
    System.out.println("String: " + inputString);
    System.out.print("Token names: ");
    int index = 0;
    for (int i : tokenList)
      System.out.print((index++) + ":" + strings[i] + " ");
    System.out.println();

    System.out.print("Tokens: ");
    for (int i : tokenList)
      System.out.print(i + " ");
    System.out.println();
  }
}