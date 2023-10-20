package uk.ac.rhul.cs.csle.art.term;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.Reference;

public abstract class ITerms {
  private final TermTraverserText tt = new TermTraverserText(this, "iTerms traverser"); // This is intended for debugging - show the term without any
                                                                                        // translations
  public static int variableCount = 20;
  public static int sequenceVariableCount = 10;

  protected int firstVariableIndex;
  protected int firstSequenceVariableIndex;
  protected int firstSpecialSymbolIndex;
  protected int firstNormalSymbolIndex;

  // public int bottomTermIndex;
  // public int doneTermIndex;
  // public int emptyTermIndex;
  ITerms() {
    // Load text traverser default action which appends the escaped version of the constructor
    tt.addAction(-1, (Integer t) -> tt.append(escapeMeta(getTermSymbolString(t)) + (getTermArity(t) == 0 ? "" : "(")), (Integer t) -> tt.append(", "),
        (Integer t) -> tt.append(getTermArity(t) == 0 ? "" : ")"));
  }

  public String toString(int term) {
    if (term == 0) return "null term";
    return tt.toString(term);
  }

  public String toString(Integer term, Boolean indent, Integer depthLimit, Map<Integer, Integer> localAliases) {
    if (term == 0) return "null term";
    return tt.toString(term, indent, depthLimit, localAliases);
  }

  abstract public int findString(String string);

  abstract public int findTerm(int symbolNameStringIndex, int... children);

  abstract public int findTerm(String symbolString, int... children);

  abstract public int getTermSymbolIndex(int termIndex);

  abstract public String getTermSymbolString(int termIndex);

  abstract public int[] getTermChildren(int termIndex);

  abstract public int getTermArity(int termIndex);

  abstract public int getTermVariableNumber(int termIndex);

  abstract public boolean isSequenceVariableSymbol(int symbolIndex);

  public abstract boolean isSequenceVariableTerm(int termIndex);

  abstract public boolean isVariableSymbol(int symbolIndex);

  public abstract boolean isVariableTerm(int termIndex);

  abstract public boolean isSpecialSymbol(int symbolIndex);

  abstract public boolean isSpecialTerm(int termIndex);

  public abstract int termCardinality();

  public abstract int stringCardinality();

  public abstract String getString(int stringIndex);

  abstract protected int getStringMapNextFreeIndex();

  public abstract int getStringTotalBytes();

  public abstract int termBytes();

  public int getSubterm(int term, int... path) {
    int ret = term;
    for (int i = 0; i < path.length; i++)
      ret = getTermChildren(ret)[path[i]];
    return ret;
  }

  /*
   *
   * High level finder: parse a term written in a human-comfortable string form
   *
   * Expression grammar
   *
   * term ::= name ( `( WS subterms `) WS )?
   *
   * subterms ::= term ( `, WS term )*
   *
   * The lexical structure is a litle unusual in that a name can be any string of characters that does not cause an LL(1) nondeterminism, and escape characters
   * are allowed in names. Escape sequences (backslash-X) where X is n, t, r or u are special and have their Java meanings. All other escape sequences yield \
   * c. Hence a name can have an embedded * in it, for instance, written \*
   *
   * name ::= nondigit char* WS
   *
   * ----
   *
   * The grammar is arranged so that each method has a single integer synthesized attribute, hence we can use a return type of int
   *
   * For character sequences, the returned value is the key into the string pool.
   *
   * For terms, the returned value is the key into the term pool
   *
   */
  public int findTerm(String term) {
    int ret = 0;
    ret = findTermThrow(term);

    return ret;
  }

  public int findTermThrow(String term) {
    parserSetup(term);
    int ret = term();
    if (cp != input.length()) {
      syntaxError("Unexpected characters after term");
      return 0;
    } else
      return ret;
  }

  /*
   * String to term parser
   */
  public void parserSetup(String term) {
    input = (term + "\0");
    cp = 0;
    getc();
  }

  public String input;
  public int cp;
  public char cc;

  public void getc() {
    cc = input.charAt(cp++);
  }

  /* Support for echoing the current line */
  public static int lineNumber(int index, String buffer) {
    if (buffer == null || index < 0) return 0;

    if (index >= buffer.length()) index = buffer.length() - 1;

    int lineCount = 1;

    for (int tmp = 0; tmp < index; tmp++)
      if (buffer.charAt(tmp) == '\n') lineCount++;

    return lineCount;

  }

  public static int columnNumber(int index, String buffer) { // Return x coordinate: note that the first column is column zero!
    int columnCount = 0;

    if (buffer == null || index < 0) return 0;

    if (index >= buffer.length()) index = buffer.length() - 1;

    if (index == 0) return 0;

    do {
      index--;
      columnCount++;
    } while (index > 0 && buffer.charAt(index) != '\n');

    if (index != 0) columnCount--; // If we did not terminate on start of buffer, then we must have terminated on \n so step forward 1

    return columnCount;
  }

  private static void appendLine(StringBuilder sb, int lineNumber, String buffer) {
    if (lineNumber < 1) return;
    int length = buffer.length();
    int tmp = lineNumber;
    int i;
    for (i = 0; tmp > 1 && i < length; i++)
      if (buffer.charAt(i) == '\n') tmp--;
    if (i >= length) return;

    sb.append(String.format("%5d: ", lineNumber));
    for (; i < length && buffer.charAt(i) != '\n'; i++)
      sb.append(buffer.charAt(i));
    sb.append("\n");
  }

  public static String echo(String message, int index, String buffer) {
    StringBuilder sb = new StringBuilder();
    int lineNumber = lineNumber(index, buffer), columnNumber = columnNumber(index, buffer);
    sb.append(lineNumber + "," + columnNumber + " " + message + "\n");
    if (buffer == null || index < 0) return sb.toString();

    // sb.append(String.format("%5d: ", lineNumber));

    int echoColumn = columnNumber;

    appendLine(sb, lineNumber - 1, buffer);
    appendLine(sb, lineNumber, buffer);
    // int echoIndex = index - echoColumn;
    // do {
    // sb.append(buffer.charAt(echoIndex++));
    // } while (echoIndex < buffer.length() && buffer.charAt(echoIndex) != '\n' && buffer.charAt(echoIndex) != '\0');

    sb.append("-------"); // Print pointer line
    for (int tmp = 0; tmp < echoColumn; tmp++)
      sb.append("-");
    sb.append("^\n");
    appendLine(sb, lineNumber + 1, buffer);
    sb.append("\n");

    return sb.toString();
  }

  public void syntaxError(String s) {
    Reference.fatal("Term parser syntax error: " + echo(s, cp, input));
  }

  public int term() {
    int symbolNameStringIndex = symbolName();
    // Semantic checks on symbol name
    String symbolNameString = getString(symbolNameStringIndex);
    if (symbolNameString.length() > 0 && symbolNameString.charAt(0) == '_') {
      if (symbolNameString.length() > 1 && symbolNameString.charAt(1) == '_') {// two underscores so must be intrinsic function or type
        if (!isSpecialSymbol(symbolNameStringIndex)) syntaxError("unknown evaluatable function: " + symbolNameString);
      } else {
        if (!isVariableSymbol(symbolNameStringIndex) && !isSequenceVariableSymbol(symbolNameStringIndex)) syntaxError("unknown variable");
      }
    }

    List<Integer> subterms;
    if (cc == '(') {
      getc();
      ws();
      if (cc == ')') {
        getc();
        ws();
        subterms = new LinkedList<>();
      } else {
        subterms = subterms();
        if (cc != ')') syntaxError("Syntax error whilst parsing term: expected ')' or ',' but found '" + cc + "'");
        getc();
        ws();
      }
    } else
      subterms = new LinkedList<>();

    int[] children = new int[subterms.size()];
    for (int i = 0; i < subterms.size(); i++)
      children[i] = subterms.get(i);

    return findTerm(symbolNameStringIndex, children); // Variable has string = 0
  }

  private List<Integer> subterms() {
    List<Integer> ret = new LinkedList<>();
    ret.add(term());
    while (cc == ',') {
      getc();
      ws();
      ret.add(term());
    }
    return ret;
  }

  private int symbolName() {
    String name = new String();
    if (Character.isWhitespace(cc) || cc == '(' || cc == ')' || cc == ',' || cc == ':' || cc == (char) 0) {
      syntaxError("Empty name");
    }
    /* 31 March 2021 - added literal strings as names */
    /* 3 January 2022 - dropped quotes from parsed payload */
    /* 27 May 2022 - retain backslashes */

    // 1. String processing
    if (cc == '"') {
      do {
        name += cc;
        if (cc == '\\') {
          getc();
          name += cc;
        }
        getc();
      } while (cc != '"');
      name += cc;
      getc();
    } else
      // 2. nonstring processing
      while (!Character.isWhitespace(cc) && cc != '(' && cc != ')' && cc != ',' && cc != ':' && cc != (char) 0) {
        if (cc == '\\') {
          // name += cc;
          getc();
        }
        name += cc;
        getc();
      }

    ws();
    return

    findString(name);
  }

  public void ws() {
    while (Character.isWhitespace(cc) && cc != (char) 0)
      getc();
  }

  /*
   * Persistence
   */
  public void dump(PrintStream out) {
    out.println("stringCardinality termCardinality firstVariableIndex firstSequenceVariableIndex firstSpecialStringIndex firstNormalStringIndex");
    out.println(stringCardinality() + " " + termCardinality() + " " + firstVariableIndex + " " + firstSequenceVariableIndex + " " + firstSpecialSymbolIndex
        + " " + firstNormalSymbolIndex);

    for (int stringIndex = 1; stringIndex <= stringCardinality(); stringIndex++) {
      out.print(out == System.out ? (stringIndex + " ") : "");
      out.println(getString(stringIndex));
    }

    for (int termIndex = 1; termIndex <= termCardinality(); termIndex++) {
      out.print(termIndex + " ");
      out.print(getTermSymbolIndex(termIndex));
      if (getTermSymbolIndex(termIndex) != 0) // variables have stringIndex = 0, in which case arity is highjacked as a variable number
        for (int j = 0; j < getTermChildren(termIndex).length; j++)
        out.print(" " + getTermChildren(termIndex)[j]);
      out.println(out == System.out ? (" " + tt.toString(termIndex)) : "");
    }
  }

  protected void undump(BufferedReader input) {// Protected since only accessed from constructor
    System.out.println("readAll() text not yet implemented");
  }

  static Set<Character> metaCharacters = Set.of('(', ')', ',', '_', '*', ':', '\\', '\"', ' ');

  public static String escapeMeta(String string) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < string.length(); i++) {

      char c = string.charAt(i);

      if (metaCharacters.contains(c)) sb.append('\\');
      sb.append(c);
    }

    return sb.toString();
  }

  public static String unescapeMeta(String string) {
    StringBuilder sb = new StringBuilder();
    int i = 0;
    while (i < string.length()) {
      char c = string.charAt(i);

      if (c == '\\') {
        i++;
        c = string.charAt(i);
        if (!metaCharacters.contains(c)) Reference.fatal("iTerms.unescapeMeta found escaped non-meta character" + c);
      }
      sb.append(c);
      i++;
    }
    return sb.toString();
  }

  public boolean hasSymbol(Integer term, String string) {
    // System.out.println("Checking term " + toString(term) + " against symbol " + string);
    return this.getTermSymbolIndex(term) == this.findString(string);
  }
}
