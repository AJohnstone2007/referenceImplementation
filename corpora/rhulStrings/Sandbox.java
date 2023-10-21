package uk.ac.rhul.cs.csle.sandbox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

class Sandbox {
  String input;
  int cc, co, ts, te, oracleLength, oracle[], nextNode;
  PrintWriter vcgFile;
  TreeNode rdt;

  final int TREE_EPSILON = 0, TREE_TERMINAL = 1, TREE_BUILTIN = 2, TREE_NONTERMINAL = 3;

  final int TIF_NONE = 0, TIF_FOLD_UNDER = 1, TIF_FOLD_OVER = 2, TIF_FOLD_ABOVE
= 3;

  Sandbox() {
    oracleLength = 1000;
    oracle = new int[oracleLength];
    cc = co = 0;
    nextNode = 1;
  }

  void postParse(TreeNode tree) {
  }

  String tifString(TIFKind tifOp) {
    switch (tifOp) {
    case TIF_NONE:
      return "";
    case TIF_FOLD_UNDER:
      return "^";
    case TIF_FOLD_OVER:
      return "^^";
    case TIF_FOLD_ABOVE:
      return "^^^";
    default:
      return "!! unknown TIF operator!!";
    }
  }

  String labelPreString(TreeKind kind) {
    switch (kind) {
    case TREE_EPSILON:
      return "";
    case TREE_TERMINAL:
      return "'";
    case TREE_BUILTIN:
      return "&";
    case TREE_NONTERMINAL:
      return "";
    default:
      return "!! unknown TREE kind !!";
    }
  }

  String attributeString() {
    return input.substring(ts, te);
  }

  String labelPostString(TreeKind kind) {
    switch (kind) {
    case TREE_EPSILON:
      return "";
    case TREE_TERMINAL:
      return "'";
    case TREE_BUILTIN:
      return "";
    case TREE_NONTERMINAL:
      return "";
    default:
      return "!! unknown TREE kind !!";
    }
  }

  String readInput(String filename) throws FileNotFoundException {
    return new Scanner(new File(filename)).useDelimiter("\\Z").next() + "\0";
  }

  void oracleSet(int i) {
    if (co == oracleLength) {
      int oracleLengthOld = oracleLength;
      oracleLength += oracleLength / 2;
      int newOracle[] = new int[oracleLength];
      System.arraycopy(oracle, 0, newOracle, 0, oracleLengthOld);
      oracle = newOracle;
      System.out.printf("resized oracle from %d to %d\n", oracleLengthOld, oracleLength);
    }
    oracle[co++] = i;
  }

  boolean match(String s) {
    System.out.printf("At %d '%c' match %s\n", cc, input.charAt(cc), s);

    System.out.printf("cc=%d s = '%s' length = %d\n", cc, s, s.length()); 

    if (input.regionMatches(cc, s, 0, s.length())) {
      cc += s.length();
      System.out.printf("successful match\n");
      builtIn_WHITESPACE();
      return true;
    }

    return false;
  }

  boolean builtIn_ID() {
    System.out.printf("At %d '%c' builtin_ID\n", cc, input.charAt(cc)); 
    if (!Character.isJavaIdentifierStart(input.charAt(cc))) return false;

    ts = cc++;

    while (Character.isJavaIdentifierPart(input.charAt(cc)))
      cc++;

    te = cc;

    System.out.printf("Matched substring %d-%d\n", ts, te);

    builtIn_WHITESPACE();

    return true;
  }

  boolean builtIn_WHITESPACE() {
    System.out.printf("At %d '%c' builtin_WHITESPACE\n", cc, input.charAt(cc));

    while (Character.isWhitespace(input.charAt(cc)))
      cc++;

    System.out.printf("Advanced to %d\n", cc);
    return true;
  }

  boolean isxdigit(char c) {
    if (Character.isDigit(c)) return true;
    if (c >= 'a' && c <= 'f') return true;
    if (c >= 'A' && c <= 'F') return true;
    return false;
  }

  boolean builtIn_INTEGER() {
    if (!Character.isDigit(input.charAt(cc))) return false;

    ts = cc;

    boolean hex = (input.charAt(cc) == '0' && (input.charAt(cc + 1) == 'x' || input.charAt(cc + 1) == 'X'));

    if (hex) cc += 2;

    while (hex ? isxdigit(input.charAt(cc)) : Character.isDigit(input.charAt(cc)))
      cc++;

    te = cc;

    builtIn_WHITESPACE();

    return true;
  }

  boolean builtIn_REAL() {
    if (!Character.isDigit(input.charAt(cc))) return false;

    ts = cc;

    while (Character.isDigit(input.charAt(cc)))
      cc++;

    if (input.charAt(cc) != '.') return true;

    cc++;

    while (Character.isDigit(input.charAt(cc)))
      cc++;

    if (input.charAt(cc) == 'e' || input.charAt(cc) == 'E') {
      cc++;

      while (Character.isDigit(input.charAt(cc)))
        cc++;
    }

    te = cc;

    builtIn_WHITESPACE();

    return true;
  }

  boolean builtIn_CHAR_SQ() {
    if (input.charAt(cc) != '\'') return false;

    cc++;

    ts = cc;

    if (input.charAt(cc) == '\\') cc++;

    cc++;

    if (input.charAt(cc) != '\'') return false;

    te = cc;

    cc++;

    builtIn_WHITESPACE();

    return true;
  }

  boolean builtIn_STRING_SQ() {
    if (input.charAt(cc) != '\'') return false;

    ts = cc + 1;

    do {
      if (input.charAt(cc) == '\\') cc++;

      cc++;
    } while (input.charAt(cc) != '\'');

    te = cc;

    cc++;

    builtIn_WHITESPACE();

    return true;
  }

  boolean builtIn_STRING_DQ() {
    if (input.charAt(cc) != '"') return false;

    ts = cc + 1;

    do {
      if (input.charAt(cc) == '\\') cc++;
      cc++;
    } while (input.charAt(cc) != '"');

    te = cc;
    cc++;

    builtIn_WHITESPACE();

    return true;
  }

  boolean builtIn_ACTION() {
    if (!(input.charAt(cc) == '[' && input.charAt(cc + 1) == '*')) return false;

    cc += 2;

    ts = cc;

    while (true) {
      if (input.charAt(cc) == 0) break;

      if (input.charAt(cc) == '*' && input.charAt(cc) == ']') {
        cc += 2;
        break;
      }

      cc++;
    }

    te = cc - 2;

    builtIn_WHITESPACE();

    return true;
  }
}
