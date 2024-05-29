package uk.ac.rhul.cs.csle.art.term;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ITerms {
  private final TermTraverserText tt = new TermTraverserText(this); // This is intended for debugging - show the term without any
                                                                    // translations
  public static int variableCount = 20;
  public static int sequenceVariableCount = 10;

  protected int firstVariableIndex;
  protected int firstSequenceVariableIndex;
  protected int firstSpecialSymbolIndex;
  protected int firstNormalSymbolIndex;

  public ARTPluginInterface plugin = new ARTPlugin();

  public int bottomTermIndex;
  public int doneTermIndex;
  public int emptyTermIndex;

  public int termBooleanTrue;
  public int termBooleanFalse;
  public __bool valueBoolTrue;
  public __bool valueBoolFalse;
  public __int32 valueInt32Zero;
  public __int32 valueInt32One;
  public __int32 valueInt32MinusOne;
  public __intAP valueIntAPZero;
  public __intAP valueIntAPOne;
  public __intAP valueIntAPMinusOne;
  public __bottom valueBottom;
  public __done valueDone;
  public __empty valueEmpty;
  public __blob valueBlob;

  // Cut and paste these from the output after runnng the mainline in ITerms.java
  public final int __bottomStringIndex = 33;
  public final int __doneStringIndex = 34;
  public final int __emptyStringIndex = 35;
  public final int __quoteStringIndex = 36;
  public final int __procStringIndex = 37;
  public final int __procV3StringIndex = 38;
  public final int __inputStringIndex = 39;
  public final int __outputStringIndex = 40;
  public final int __blobStringIndex = 41;
  public final int __bindingStringIndex = 42;
  public final int __adtProdStringIndex = 43;
  public final int __adtSumStringIndex = 44;
  public final int __boolStringIndex = 45;
  public final int __charStringIndex = 46;
  public final int __intAPStringIndex = 47;
  public final int __int32StringIndex = 48;
  public final int __realAPStringIndex = 49;
  public final int __real64StringIndex = 50;
  public final int __stringStringIndex = 51;
  public final int __arrayStringIndex = 52;
  public final int __listStringIndex = 53;
  public final int __flexArrayStringIndex = 54;
  public final int __setStringIndex = 55;
  public final int __mapStringIndex = 56;
  public final int __mapChainStringIndex = 57;
  public final int __eqStringIndex = 58;
  public final int __neStringIndex = 59;
  public final int __gtStringIndex = 60;
  public final int __ltStringIndex = 61;
  public final int __geStringIndex = 62;
  public final int __leStringIndex = 63;
  public final int __compareStringIndex = 64;
  public final int __notStringIndex = 65;
  public final int __andStringIndex = 66;
  public final int __orStringIndex = 67;
  public final int __xorStringIndex = 68;
  public final int __cndStringIndex = 69;
  public final int __lshStringIndex = 70;
  public final int __rshStringIndex = 71;
  public final int __ashStringIndex = 72;
  public final int __rolStringIndex = 73;
  public final int __rorStringIndex = 74;
  public final int __negStringIndex = 75;
  public final int __addStringIndex = 76;
  public final int __subStringIndex = 77;
  public final int __mulStringIndex = 78;
  public final int __divStringIndex = 79;
  public final int __modStringIndex = 80;
  public final int __expStringIndex = 81;
  public final int __sizeStringIndex = 82;
  public final int __catStringIndex = 83;
  public final int __sliceStringIndex = 84;
  public final int __getStringIndex = 85;
  public final int __putStringIndex = 86;
  public final int __containsStringIndex = 87;
  public final int __removeStringIndex = 88;
  public final int __extractStringIndex = 89;
  public final int __unionStringIndex = 90;
  public final int __intersectionStringIndex = 91;
  public final int __differenceStringIndex = 92;
  public final int __castStringIndex = 93;
  public final int __cast__boolStringIndex = 94;
  public final int __cast__charStringIndex = 95;
  public final int __cast__intAPStringIndex = 96;
  public final int __cast__int32StringIndex = 97;
  public final int __cast__realAPStringIndex = 98;
  public final int __cast__real64StringIndex = 99;
  public final int __cast__stringStringIndex = 100;
  public final int __cast__arrayStringIndex = 101;
  public final int __cast__listStringIndex = 102;
  public final int __cast__flexArrayStringIndex = 103;
  public final int __cast__setStringIndex = 104;
  public final int __cast__mapStringIndex = 105;
  public final int __cast__mapChainStringIndex = 106;
  public final int __termArityStringIndex = 107;
  public final int __termRootStringIndex = 108;
  public final int __termChildStringIndex = 109;
  public final int __termMakeStringIndex = 110;
  public final int __termMatchStringIndex = 111;
  public final int __userStringIndex = 112;

  ITerms() {
    Value.iTerms = this;
    // Load text traverser default action which appends the escaped version of the constructor
    tt.addAction(-1, (Integer t) -> tt.append(escapeMeta(getTermSymbolString(t)) + (getTermArity(t) == 0 ? "" : "(")), (Integer t) -> tt.append(", "),
        (Integer t) -> tt.append(getTermArity(t) == 0 ? "" : ")"));

    plugin = new ARTPlugin();
    // Try and find a plugin for __user() calls
    Class<?> pluginClass;
    try {
      pluginClass = getClass().getClassLoader().loadClass("ValueUserPlugin");
      plugin = (ARTPluginInterface) pluginClass.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      // Silently absorb exception - when the default is used
    }
    // if (plugin.name() != null) System.out.println("Attached to plugin: " + plugin.name());
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

  abstract public int findTerm(String symbolString, LinkedList<Integer> children);

  abstract public int findTerm(int symbolStringIndex, LinkedList<Integer> children);

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
    System.out.println(echo(s, cp, input));
    // !!!
    // System.out.println("** " + s);
    // System.out.println(input);
    // for (int i = 0; i < cp - 1; i++)
    // System.out.print("-");
    // System.out.println("^");
    System.exit(1);
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

  /*
   * Pattern matching and substitution
   */
  public boolean matchOneSV(int closedTermIndex, int openTermIndex, int[] bindings) { // This matcher allows one sequence variable per
                                                                                      // sequence of siblings
    return false;
  }

  public boolean matchZeroSV(int closedTermIndex, int openTermIndex, int[] bindings) { // This matcher does not allow sequence
                                                                                       // variables
    // System.out
    // .println("matchZeroSV() " + closedTermIndex + ":" + toString(closedTermIndex) + " against open term " + openTermIndex + ":" + toString(openTermIndex));

    if (isSequenceVariableTerm(openTermIndex)) throw new RewriteException("in matchZeroSV() right hand side must not contain sequence variables");

    if (isVariableTerm(openTermIndex)) {
      int variableNumber = getTermVariableNumber(openTermIndex);
      if (variableNumber != 0) bindings[variableNumber] = closedTermIndex; // Variable zero means match anything but don't bind
      // System.out.println("matchZeroSV() binds to variable and returns true");
      return true;
    }

    if (!(getTermSymbolIndex(closedTermIndex) == getTermSymbolIndex(openTermIndex) && getTermArity(closedTermIndex) == getTermArity(openTermIndex)))
      return false;
    for (int i = 0; i < getTermArity(openTermIndex); i++)
      if (!matchZeroSV(getTermChildren(closedTermIndex)[i], getTermChildren(openTermIndex)[i], bindings)) return false;

    // System.out.println("matchZeroSV() matched children and root, and returns true");

    return true;
  }

  public int substitute(int[] bindings, int openTermIndex, int level) {
    int ret;

    // Postorder substitution so substitute children first

    // System.out.println(level + " Substitute " + toString(openTermIndex) + " with bindings {" + toStringBindings(bindings) + "}");
    // System.out.println(level + " Open term is " + toString(openTermIndex));
    int arity = getTermArity(openTermIndex);
    // System.out.println(level + " Arity is " + arity);

    int[] children = new int[arity];
    int newArity = 0;
    for (int i = 0; i < arity; i++) {
      children[i] = substitute(bindings, getTermChildren(openTermIndex)[i], level + 1);
      if (isSequenceVariableTerm(children[i]))
        newArity += getTermArity(children[i]);
      else
        newArity++;
    }

    // System.out.println(level + " After substitution, open term is " + toString(openTermIndex));

    // if (newArity != arity) {// There were sequence variable bindings, so we must promote the children of the sequences
    // int[] newChildren = new int[newArity];
    // int nextNewChild = 0;
    //
    // for (int i = 0; i < arity; i++) { // If not a sequence child, copy else copy children
    // if (getTermSymbolIndex(children[i]) == 0) // Are we substituting a sequence variable?
    // for (int j = 0; j < getTermArity(children[i]); j++)
    // newChildren[nextNewChild++] = getTermChildren(children[i])[j];
    // else
    // newChildren[nextNewChild++] = children[i];
    // }
    // children = newChildren;
    // }
    //
    if (isVariableTerm(openTermIndex) || isSequenceVariableTerm(openTermIndex)) {
      int termVariableNumber = getTermVariableNumber(openTermIndex);
      int boundValue = bindings[termVariableNumber];
      if (boundValue == 0) throw new RewriteException("attempt to substitute unbound variable " + tt.toString(openTermIndex));
      ret = boundValue;
      // Now reduce substituted values by evaluation
      if (isSpecialTerm(boundValue))
        ret = evaluateTerm(boundValue, this.getTermChildren(boundValue));
      else
        ret = boundValue;
    } else if (isSpecialTerm(openTermIndex))
      ret = evaluateTerm(openTermIndex, children);
    else
      ret = findTerm(getTermSymbolIndex(openTermIndex), children);

    // System.out.println("Substitute " + toString(openTermIndex) + " with bindings " + toStringBindings(bindings) + " returns " + toString(ret));
    return ret;
  }

  public String toStringBindings(int[] bindings) {
    String ret = "";
    for (int i = 0; i < bindings.length; i++) {
      if (bindings[i] != 0) ret += " _" + i + "=" + tt.toString(bindings[i]);
    }
    return ret;
  }

  protected int arityCheck(int termIndex, int i) {
    if (getTermArity(termIndex) == i) return termIndex;
    throw new RewriteException("Term must have arity " + i + ": " + tt.toString(termIndex));
  }

  protected int typeCheck(int termIndex, int typeStringIndex) {
    if (getTermSymbolIndex(termIndex) != typeStringIndex)
      throw new RewriteException("Term must have type " + getString(typeStringIndex) + ": " + tt.toString(termIndex));
    return termIndex;
  }

  // Take a closed term and a list of k (match, substitute) terms represented as an integer array of length 2*k
  // Return a list of rewritten terms of length j, where j is the number of successful matches
  int[] bindings = new int[variableCount + sequenceVariableCount]; // Enough space for all variables

  // public int[] rewrite(int closedTerm, int... matchSubstituteTerms) {
  // if (!(matchSubstituteTerms.length > 0 && matchSubstituteTerms.length % 2 != 0))
  // throw new RewriteException("internal - matchSubstituteTerms must be of length 2n, n>0");
  //
  // int[] rewrites = new int[matchSubstituteTerms.length / 2];
  // int matches = 0;
  // for (int i = 0; i < matchSubstituteTerms.length; i += 2)
  // if (matchZeroSV(closedTerm, matchSubstituteTerms[i], bindings)) {
  // rewrites[matches++] = substitute(bindings, closedTerm, 0);
  // Arrays.fill(bindings, 0);
  // }
  // return Arrays.copyOf(rewrites, matches);
  // }

  /*** Value system below this line ******************************************************************************************************/

  boolean mixCheck(Value l, Value r, int term) {
    if (l.getClass() != r.getClass()) {
      System.out.println("!! Function error: " + getTermSymbolString(term) + "(" + l.getClass().getSimpleName() + "," + r.getClass().getSimpleName()
          + ") - operands must be of same type; returning __bottom");
      return true;
    } else
      return false;
  }

  public int evaluateTerm(int term, int[] children) {
    int rootSpecialSymbol = getTermSymbolIndex(term);
    String rootSymbolString = getString(rootSpecialSymbol);

    // First do typed literals
    switch (rootSpecialSymbol) {
    case __bottomStringIndex:
    case __doneStringIndex:
    case __emptyStringIndex:
    case __quoteStringIndex:
    case __procStringIndex:
    case __procV3StringIndex:
    case __inputStringIndex:
    case __outputStringIndex:
    case __blobStringIndex:
    case __bindingStringIndex:
    case __adtProdStringIndex:
    case __adtSumStringIndex:
    case __boolStringIndex:
    case __charStringIndex:
    case __intAPStringIndex:
    case __int32StringIndex:
    case __realAPStringIndex:
    case __real64StringIndex:
    case __arrayStringIndex:
    case __listStringIndex:
    case __flexArrayStringIndex:
    case __stringStringIndex:
    case __setStringIndex:
    case __mapStringIndex:
    case __mapChainStringIndex:
      return findTerm(getTermSymbolIndex(term), children);

    case __termArityStringIndex:
      arityCheck(term, 1);
      return findTerm("__int32(" + (getTermArity(children[0])) + ")");

    case __termRootStringIndex:
      arityCheck(term, 1);
      return findTerm(getTermSymbolString(children[0]));

    case __termChildStringIndex:
      arityCheck(term, 2);
      typeCheck(children[1], __int32StringIndex);
      int childNumber = ((__int32) valueFromTerm(children[1])).javaValue();
      if (getTermArity(children[0]) <= childNumber) return bottomTermIndex;
      return getSubterm(children[0], childNumber);

    case __termMakeStringIndex: // __termConstruct(oldTerm, root, leftChildIndex, rightChildIndex)
      arityCheck(term, 4);
      typeCheck(children[2], __int32StringIndex);
      typeCheck(children[3], __int32StringIndex);
      int leftChildIndex = ((__int32) valueFromTerm(children[2])).javaValue();
      int rightChildIndex = ((__int32) valueFromTerm(children[3])).javaValue();
      int[] oldTermChildren = getTermChildren(children[0]);
      int[] newChildren = new int[rightChildIndex - leftChildIndex + 1];
      for (int i = leftChildIndex; i <= rightChildIndex; i++)
        newChildren[i - leftChildIndex] = oldTermChildren[i];
      int rootSymbol = getTermSymbolIndex(children[1]);
      int ret = findTerm(rootSymbol, newChildren);
      return ret;

    case __userStringIndex:
      Value[] values = new Value[children.length];
      for (int i = 0; i < children.length; i++)
        values[i] = valueFromTerm(children[i]);
      return findTerm(values[0].__plugin(values).toString());

    case __termMatchStringIndex:
      arityCheck(term, 2);
      return children[0] == children[1] ? emptyTermIndex : bottomTermIndex;

    default:
      break;
    }

    int arity = children.length;
    Value rr = valueEmpty, r = valueEmpty, l = valueEmpty;
    if (arity > 0) l = valueFromTerm(children[0]);
    if (arity > 1) r = valueFromTerm(children[1]);
    if (arity > 2) rr = valueFromTerm(children[2]);

    // System.out.println("Evaluating term " + toString(term) + " with l = " + l.toCanonicalString() + " and r = " + r.toCanonicalString() + " and rr = "
    // + rr.toCanonicalString());
    if (arity == 0)
      throw new RewriteException("Unknown arity-zero function " + rootSymbolString);
    else if (arity == 1)

      switch (rootSpecialSymbol) {
      case __notStringIndex:
        return findTerm(l.__not().toString());
      case __negStringIndex:
        return findTerm(l.__neg().toString());
      case __sizeStringIndex:
        return findTerm(l.__size().toString());
      case __sliceStringIndex:
        return findTerm(l.__slice().toString());
      case __getStringIndex:
        return findTerm(l.__get().toString());
      case __extractStringIndex:
        return findTerm(l.__extract().toString());
      case __cast__boolStringIndex:
        return findTerm(l.__cast__bool().toString());
      case __cast__charStringIndex:
        return findTerm(l.__cast__char().toString());
      case __cast__intAPStringIndex:
        return findTerm(l.__cast__intAP().toString());
      case __cast__int32StringIndex:
        return findTerm(l.__cast__int32().toString());
      case __cast__realAPStringIndex:
        return findTerm(l.__cast__realAP().toString());
      case __cast__real64StringIndex:
        return findTerm(l.__cast__real64().toString());
      case __cast__stringStringIndex:
        return findTerm(l.__cast__string().toString());
      case __cast__arrayStringIndex:
        return findTerm(l.__cast__array().toString());
      case __cast__listStringIndex:
        return findTerm(l.__cast__list().toString());
      case __cast__flexArrayStringIndex:
        return findTerm(l.__cast__flexArray().toString());
      case __cast__setStringIndex:
        return findTerm(l.__cast__set().toString());
      case __cast__mapStringIndex:
        return findTerm(l.__cast__map().toString());
      case __cast__mapChainStringIndex:
        return findTerm(l.__cast__mapChain().toString());

      default:
        throw new RewriteException("Unknown monadic function " + rootSymbolString);
      }
    else if (arity == 2)
      switch (rootSpecialSymbol) {
      case __compareStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__compare(r).toString());
      case __eqStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__eq(r).toString());
      case __neStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__ne(r).toString());
      case __gtStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__gt(r).toString());
      case __ltStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__lt(r).toString());
      case __geStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__ge(r).toString());
      case __leStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__le(r).toString());
      case __andStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__and(r).toString());
      case __orStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__or(r).toString());
      case __xorStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__xor(r).toString());
      case __cndStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__cnd(r).toString());
      case __lshStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__lsh(r).toString());
      case __rshStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__rsh(r).toString());
      case __ashStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__ash(r).toString());
      case __rolStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__rol(r).toString());
      case __rorStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__ror(r).toString());
      case __addStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__add(r).toString());
      case __subStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__sub(r).toString());
      case __mulStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__mul(r).toString());
      case __divStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__div(r).toString());
      case __modStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__mod(r).toString());
      case __expStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__exp(r).toString());
      case __catStringIndex:
        return findTerm(l.__cat(r).toString());
      case __putStringIndex:
        return findTerm(l.__put(r).toString());
      case __getStringIndex:
        return findTerm(l.__get(r).toString());
      case __containsStringIndex:
        return findTerm(l.__contains(r).toString());
      case __removeStringIndex:
        return findTerm(l.__remove(r).toString());
      case __unionStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__union(r).toString());
      case __intersectionStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__intersection(r).toString());
      case __differenceStringIndex:
        if (mixCheck(l, r, term)) return bottomTermIndex;
        return findTerm(l.__difference(r).toString());
      // Now the cast despatcher
      case __castStringIndex: {
        int targetType = getTermSymbolIndex(children[1]);
        if (targetType == __quoteStringIndex) targetType = getTermSymbolIndex(this.getTermChildren(children[1])[0]);
        switch (targetType) {
        case __boolStringIndex:
          return findTerm(l.__cast__bool().toString());
        case __charStringIndex:
          return findTerm(l.__cast__char().toString());
        case __intAPStringIndex:
          return findTerm(l.__cast__intAP().toString());
        case __int32StringIndex:
          return findTerm(l.__cast__int32().toString());
        case __realAPStringIndex:
          return findTerm(l.__cast__realAP().toString());
        case __real64StringIndex:
          return findTerm(l.__cast__real64().toString());
        case __stringStringIndex:
          return findTerm(l.__cast__string().toString());
        case __arrayStringIndex:
          return findTerm(l.__cast__array().toString());
        case __listStringIndex:
          return findTerm(l.__cast__list().toString());
        case __flexArrayStringIndex:
          return findTerm(l.__cast__flexArray().toString());
        case __setStringIndex:
          return findTerm(l.__cast__set().toString());
        case __mapStringIndex:
          return findTerm(l.__cast__map().toString());
        case __mapChainStringIndex:
          return findTerm(l.__cast__mapChain().toString());
        default:
          throw new RewriteException("illegal __cast target type " + tt.toString(children[1]));
        }
      }
      default:
        throw new RewriteException("Unknown arity-2 function " + rootSymbolString);
      }
    else if (arity == 3) switch (rootSpecialSymbol) {
    case __putStringIndex:
      return findTerm(l.__put(r, rr).toString());
    case __sliceStringIndex:
      return findTerm(l.__slice(r, rr).toString());
    default:
      throw new RewriteException("Unknown arity-3 function " + rootSymbolString);
    }
    return 0; // Actually this should never be executed as exceptions will cut in first
  }

  public Value valueFromTerm(int term) {
    int rootSpecialSymbol = getTermSymbolIndex(term);

    switch (rootSpecialSymbol) {
    case __bottomStringIndex:
      arityCheck(term, 0);
      return valueBottom;
    case __doneStringIndex:
      arityCheck(term, 0);
      return valueDone;
    case __emptyStringIndex:
      arityCheck(term, 0);
      return valueEmpty;
    case __quoteStringIndex:
      arityCheck(term, 1);
      return new __quote(term);
    case __boolStringIndex:
      arityCheck(term, 1);
      return new __bool(term);
    case __charStringIndex:
      arityCheck(term, 1);
      return new __char(term);
    case __intAPStringIndex:
      arityCheck(term, 1);
      return new __intAP(term);
    case __int32StringIndex:
      arityCheck(term, 1);
      return new __int32(term);
    case __realAPStringIndex:
      arityCheck(term, 1);
      return new __realAP(term);
    case __real64StringIndex:
      arityCheck(term, 1);
      return new __real64(term);
    case __stringStringIndex:
      arityCheck(term, 1);
      return new __string(term);
    // case __arrayStringIndex:
    // return new __array(term);
    case __listStringIndex:
      return new __list(term);
    case __mapStringIndex:
      return new __map(term);
    case __mapChainStringIndex:
      return new __mapChain(term);

    default:
      return new __quote(term);
    }
  }

  static int symbolValue;

  static void printInitialisations(String... s) {
    symbolValue = variableCount + sequenceVariableCount + 1 + 2;
    System.out.println("These go in the initialisation for the high level ITerms");
    for (String sym : s)
      System.out.println("public final int " + sym + "StringIndex = " + (symbolValue++) + ";");

    symbolValue = variableCount + sequenceVariableCount + 1 + 2;
    System.out.println("\nThese go at the end of the low level constructor after the string table has been initialised");
    for (String sym : s)
      System.out.println("if(findString(\"" + sym + "\") != " + symbolValue++ + ") System.out.println(\"String index mismatch for " + sym + "\");");
  }

  // A main line which prints out the initialisation code needed in ITerms.java (this file) for use when the Value system changes
  public static void main(String[] args) {
    printInitialisations("__bottom", "__done", "__empty", "__quote", "__proc", "__input", "__output", "__blob", "__adtProd", "__adtSum", "__bool", "__char",
        "__intAP", "__int32", "__realAP", "__real64", "__string", "__array", "__list", "__map", "__mapChain", "__eq", "__ne", "__gt", "__lt", "__ge", "__le",
        "__compare", "__not", "__and", "__or", "__xor", "__cnd", "__lsh", "__rsh", "__ash", "__rol", "__ror", "__neg", "__add", "__sub", "__mul", "__div",
        "__mod", "__exp", "__size", "__cat", "__slice", "__get", "__put", "__contains", "__remove", "__extract", "__union", "__intersection", "__difference",
        "__cast", "__cast__bool", "__cast__char", "__cast__intAP", "__cast__int32", "__cast__realAP", "__cast__real64", "__cast__string", "__cast__array",
        "__cast__list", "__cast__map", "__cast__mapChain", "__termArity", "__termRoot", "__termChild", "__termMake", "__termMatch", "__plugin");
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
        if (!metaCharacters.contains(c)) throw new RewriteException("iTerms.unescapeMeta found escaped non-meta character");
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
