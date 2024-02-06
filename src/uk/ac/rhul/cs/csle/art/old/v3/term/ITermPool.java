package uk.ac.rhul.cs.csle.art.old.v3.term;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.core.ARTUncheckedException;

/*
 *
 * This is the manager class for a pool of immutable terms. An application should create one instance of this class for each
 * independent universe of immutable terms. Most often there will only be one.
 *
 *  Terms are then created by using findInStringPool() to ensure that the label is uniquely represnted and then findInTermPool()
 *  to make a unique instance of that immutable term.
 *
 *  Each term has a number. In this implementation they are sequentially allocated, but in some applications the hashnumber would be the number.
 *
 */
public class ITermPool {
  /*
   * Data
   */
  private int firstVariableStringIndex;
  private int firstSequenceVariableStringIndex;
  private int firstIntrinsicFunctionStringIndex;
  private int firstIntrinsicTypeStringIndex;
  private int firstExtrinsicStringIndex;

  private boolean isVariable(int stringIndex) {
    return stringIndex >= firstVariableStringIndex && stringIndex < firstSequenceVariableStringIndex;
  }

  private boolean isSequenceVariable(int stringIndex) {
    return stringIndex >= firstSequenceVariableStringIndex && stringIndex < firstIntrinsicFunctionStringIndex;
  }

  private boolean isIntrinsicFunction(int stringIndex) {
    return stringIndex >= firstIntrinsicFunctionStringIndex && stringIndex < firstIntrinsicTypeStringIndex;
  }

  private boolean isIntrinsicType(int stringIndex) {
    return stringIndex >= firstIntrinsicTypeStringIndex && stringIndex < firstExtrinsicStringIndex;
  }

  private boolean isExtrinsic(int stringIndex) {
    return stringIndex >= firstExtrinsicStringIndex;
  }

  private final Map<String, Integer> stringPoolToIndex = new HashMap<>(); // Each unique string is mapped onto the naturals
  private final Map<Integer, String> stringPoolFromIndex = new HashMap<>(); // Reverse map for recovering the strings
  private Integer stringPoolNextFreeIndex = 1; // defensive programming: index zero is never used

  private final Map<ITerm, Integer> termPoolToIndex = new HashMap<>(); // Each unique immutable term is mapped onto the naturals
  private final Map<Integer, ITerm> termPoolFromIndex = new HashMap<>(); // Reverse map for recovering the terms
  private Integer termPoolNextFreeIndex = 1; // defensive programming: index zero is never used

  /*
   * Constructors
   */
  public ITermPool(BufferedReader input) { // Load with persistent data dump previously created with ITermpPool.dump()
    undump(input);
  }

  public ITermPool(int variableCount, int sequenceVariableCount) {
    this(variableCount, sequenceVariableCount, Set.of(Functions.values()), Set.of(Types.values()));
  }

  public ITermPool(int variableCount, int sequenceVariableCount, Set<Functions> functionsInUse, Set<Types> typesInUse) {
    // Now create string map entries for built ins which will be used when decoding special actions in substitutions
    firstVariableStringIndex = stringPoolNextFreeIndex;
    findString("_");
    for (int v = 1; v <= variableCount; v++)
      findString("_" + v);
    firstSequenceVariableStringIndex = stringPoolNextFreeIndex;
    findString("_*");
    for (int v = 1; v <= sequenceVariableCount; v++)
      findString("_" + v + "*");
    firstIntrinsicFunctionStringIndex = stringPoolNextFreeIndex;
    for (Functions f : functionsInUse)
      findString(f.name());
    firstIntrinsicTypeStringIndex = stringPoolNextFreeIndex;
    for (Types t : typesInUse)
      findString(t.name());
    firstExtrinsicStringIndex = stringPoolNextFreeIndex;
  }

  /*
   * Low level finders
   */
  public int findString(String string) {
    Integer ret;

    if ((ret = stringPoolToIndex.get(string)) == null) {
      stringPoolToIndex.put(string, stringPoolNextFreeIndex);
      stringPoolFromIndex.put(stringPoolNextFreeIndex, new String(string)); // Don't assume the old string is immutable...
      ret = stringPoolNextFreeIndex++;
    }

    return ret;
  }

  public int findTerm(String symbolString, int arity, int[] children) {
    return findTerm(0, findString(symbolString), arity, children);
  }

  public int findTerm(int typeTermIndex, String symbolString, int arity, int[] children) {
    return findTerm(typeTermIndex, findString(symbolString), arity, children);
  }

  public int findTerm(int typeTermIndex, int symbolStringIndex, int arity, int[] children) {
    Integer ret;
    ITerm term = new ITerm(typeTermIndex, symbolStringIndex, arity, children);

    if ((ret = termPoolToIndex.get(term)) == null) {
      termPoolToIndex.put(term, termPoolNextFreeIndex);
      termPoolFromIndex.put(termPoolNextFreeIndex, term);
      ret = termPoolNextFreeIndex++;
    }

    return ret;
  }

  // This routine models the use of an anonymous array of terms in the hardware - deploy with care!
  // It only makes sense to use this when a traversing algorithms knows the label and type of a child node, so will never look it up
  // This situation is actually quite common
  public int findTermAnonymous(int arity, int[] children) {
    Integer ret;
    ITerm term = new ITerm(0, 0, arity, children);

    if ((ret = termPoolToIndex.get(term)) == null) {
      termPoolToIndex.put(term, termPoolNextFreeIndex);
      termPoolFromIndex.put(termPoolNextFreeIndex, term);
      ret = termPoolNextFreeIndex++;
    }

    return ret;
  }

  // This routine models the use of an arityless array of terms in the hardware - deploy with care!
  // It only makes sense to use this when a traversiing algorithms knows the label and type AND ARITY of a child node, so will never look it up
  // This situation is actually quite common
  // We can construct arrays of arrays of arrays... of terms using this pseudo-term, since children are held in arrays
  public int findTermAnonymousArityless(int arity, int[] children) {
    Integer ret;
    ITerm term = new ITerm(0, 0, 0, children);

    if ((ret = termPoolToIndex.get(term)) == null) {
      termPoolToIndex.put(term, termPoolNextFreeIndex);
      termPoolFromIndex.put(termPoolNextFreeIndex, term);
      ret = termPoolNextFreeIndex++;
    }

    return ret;
  }

  /*
   *
   * High level finder: parse a term written in a human-comfortable string form
   *
   * Expression grammar
   *
   * term ::= name type ( `( WS subterms `) WS )?
   *
   * type ::= ':' WS name | #
   *
   * subterms ::= term ( `, WS term )*
   *
   * The lexical structure is a litle unusual in that a name can be any string of characters that does not cause an LL(1) nondeterminism, and escape characters
   * are allowed in names. Escape sequences (backslash-X) where X is n, t, r or u are special and havetheir Java meanings. All other escape sequences yield c.
   * Hence a name can have an embedded * in it, for instance, written \*
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
    try {
      ret = findTermThrow(term);
    } catch (ARTExceptionTermParser e) {
      syntaxError(e);
    }
    return ret;
  }

  public int findTermThrow(String term) throws ARTExceptionTermParser {
    parserSetup(term);
    int ret = term();
    if (cp != input.length())
      throw new ARTExceptionTermParser("Unexpected characters after term", cp, input);
    else
      return ret;
  }

  void parserSetup(String term) {
    input = (term + "\0");
    cp = 0;
    getc();
  }

  String input;
  int cp;
  char cc;

  void getc() {
    cc = input.charAt(cp++);
  }

  void syntaxError(ARTExceptionTermParser e) {
    System.out.println("** " + e.getErrorMessage());
    System.out.println(e.getInput());
    for (int i = 0; i < e.getInputIndex() - 1; i++)
      System.out.print("-");
    System.out.println("^");
  }

  int term() throws ARTExceptionTermParser {
    int symbolNameStringIndex = symbolName();
    // Semantic checks on symbol name
    String symbolNameString = stringPoolFromIndex.get(symbolNameStringIndex);
    if (symbolNameString.charAt(0) == '_') {
      if (symbolNameString.length() > 1 && symbolNameString.charAt(1) == '_') {// two underscores so must be intrinsic function or type
        if (!isIntrinsicFunction(symbolNameStringIndex) && !(isIntrinsicType(symbolNameStringIndex)))
          throw new ARTExceptionTermParser("unknown evaluatable function or type" + symbolNameString, cp, input);
      } else {
        if (!isVariable(symbolNameStringIndex) && !isSequenceVariable(symbolNameStringIndex))
          throw new ARTExceptionTermParser("unknown variable" + symbolNameString, cp, input);
      }
    }

    int typeNameStringIndex = type();
    if (typeNameStringIndex != 0) {
      // Semantic checks on type
      String typeNameString = stringPoolFromIndex.get(typeNameStringIndex);
      if (typeNameString.charAt(0) == '_') {
        if (typeNameString.length() > 1 && typeNameString.charAt(1) == '_') {// must be intrinsic type
          if (!(isIntrinsicType(typeNameStringIndex))) throw new ARTExceptionTermParser("unknown type" + typeNameString, cp, input);
        } else {
          if (!isVariable(typeNameStringIndex) && !isSequenceVariable(typeNameStringIndex))
            throw new ARTExceptionTermParser("unknown variable" + typeNameString, cp, input);
        }
      }
    }

    List<Integer> subterms;
    if (cc == '(') {
      getc();
      ws();
      subterms = subterms();
      if (cc != ')') throw new ARTExceptionTermParser("Expected ')' or ','", cp, input);
      getc();
      ws();
    } else
      subterms = new LinkedList<>();

    int[] children = new int[subterms.size()];
    for (int i = 0; i < children.length; i++)
      children[i] = subterms.get(i);

    return findTerm(typeNameStringIndex, symbolNameStringIndex, children.length, children); // Variable has string = 0
  }

  private List<Integer> subterms() throws ARTExceptionTermParser {
    List<Integer> ret = new LinkedList<>();
    ret.add(term());
    while (cc == ',') {
      getc();
      ws();
      ret.add(term());
    }
    return ret;
  }

  private int type() throws ARTExceptionTermParser {
    if (cc != ':') return 0;
    getc();
    ws();
    return symbolName();
  }

  private int symbolName() throws ARTExceptionTermParser {
    String name = new String();
    if (Character.isWhitespace(cc) || cc == '(' || cc == ')' || cc == ',' || cc == ':' || cc == (char) 0)
      throw new ARTExceptionTermParser("Empty name", cp, input);
    while (!Character.isWhitespace(cc) && cc != '(' && cc != ')' && cc != ',' && cc != ':' && cc != (char) 0) {
      if (cc == '\\') {
        getc();
        if (cc == 'n')
          cc = '\n';
        else if (cc == 't')
          cc = '\t';
        else if (cc == 'r') cc = '\r';
      }
      name += cc;
      getc();
    }
    ws();
    return findString(name);
  }

  void ws() {
    while (Character.isWhitespace(cc) && cc != (char) 0)
      getc();
  }

  /*
   * Pool statistics
   */
  public int stringCardinality() {
    return stringPoolNextFreeIndex - 1; // zero element does not count as an entry
  }

  public int stringBytes() {
    int ret = 0;
    for (String k : stringPoolToIndex.keySet())
      ret += k.length() + 1; // Allow for terminating byte in flat memory implementation

    return ret;
  }

  public int termCardinality() {
    return termPoolNextFreeIndex - 1; // zero element does not count as an entry
  }

  public int termBytes() {
    int ret = 0;
    for (ITerm k : termPoolToIndex.keySet())
      ret += k.arity + 3; // stringIndex, arity, typeIndex, arity*childIndex

    return ret;
  }

  /*
   * Persistence
   */
  public void dump(PrintStream out) {
    out.println(stringCardinality() + " " + termCardinality() + " " + firstVariableStringIndex + " " + firstSequenceVariableStringIndex + " "
        + firstIntrinsicFunctionStringIndex + " " + firstIntrinsicTypeStringIndex + " " + firstExtrinsicStringIndex);

    for (int i = 1; i <= stringCardinality(); i++) {
      out.print(stringPoolFromIndex.get(i));
      out.println(out == System.out ? " = " + i : "");
    }

    for (int i = 1; i <= termCardinality(); i++) {
      ITerm t = termPoolFromIndex.get(i);
      out.print(t.typeNameStringIndex + " " + t.symbolNameStringStringIndex + " " + t.arity);
      if (t.symbolNameStringStringIndex != 0) // variables have stringIndex = 0, in which case arity is highjacked as a variable number and there are no
                                              // chilren!
        for (int j = 0; j < t.arity; j++)
        out.print(" " + t.childTermIndices[j]);
      out.println(out == System.out ? " = " + toString(i, 2) : "");
    }
  }

  private void undump(BufferedReader input) {// Private since only accessed from constructor
    System.out.println("readAll() text not yet implemented");
  }

  /*
   * String rendering - unparsing
   */
  public String toString(int termIndex) {
    return toString(termIndex, -1); // negative depthLimit means no limit so show full tree
  }

  public String toString(int termIndex, int depthLimit) {
    StringBuilder sb = new StringBuilder();
    toStringRec(termIndex, sb, depthLimit);
    return sb.toString();
  }

  private void toStringRec(int termIndex, StringBuilder sb, int depthLimit) {
    if (depthLimit == 0) {
      sb.append("..");
      return;
    }

    ITerm t = termPoolFromIndex.get(termIndex);

    sb.append(stringPoolFromIndex.get(t.symbolNameStringStringIndex));

    if (t.typeNameStringIndex != 0) sb.append(":" + stringPoolFromIndex.get(t.typeNameStringIndex));

    if (t.arity > 0) {
      sb.append("(");
      for (int i = 0; i < t.arity; i++) {
        toStringRec(t.childTermIndices[i], sb, depthLimit > 0 ? depthLimit - 1 : depthLimit);
        if (i < t.arity - 1) sb.append(", ");
      }
      sb.append(")");
    }
  }

  /*
   * Pattern matching and substitution
   */
  public boolean matchZeroSV(int closedTermIndex, int openTermIndex, int[] bindings) { // This matcher does not allow sequence variables
    ITerm closedTerm = termPoolFromIndex.get(closedTermIndex);
    ITerm openTerm = termPoolFromIndex.get(openTermIndex);

    if (!isExtrinsic(closedTerm.symbolNameStringStringIndex))
      throw new ARTUncheckedException("in matchZeroSV() left hand side must be a closed term over extrinsics");

    if (isSequenceVariable(openTerm.symbolNameStringStringIndex))
      throw new ARTUncheckedException("in matchZeroSV() right hand side must not contain sequence variables");

    if (isVariable(openTerm.symbolNameStringStringIndex)) {
      if (openTerm.typeNameStringIndex != 0 && openTerm.typeNameStringIndex != closedTerm.typeNameStringIndex) return false; // Check type compatibility unless
      // this is untyped
      int variableNumber = openTerm.symbolNameStringStringIndex - firstVariableStringIndex;
      if (variableNumber != 0) bindings[variableNumber] = closedTermIndex; // Variable zero means match anything but don't bind
      return true;
    }

    if (!(closedTerm.typeNameStringIndex == openTerm.typeNameStringIndex && closedTerm.symbolNameStringStringIndex == openTerm.symbolNameStringStringIndex
        && closedTerm.arity == openTerm.arity))
      return false;
    for (int i = 0; i < openTerm.arity; i++)
      if (!matchZeroSV(closedTerm.childTermIndices[i], openTerm.childTermIndices[i], bindings)) return false;
    return true;
  }

  int substitute(int[] bindings, int openTermIndex) {
    ITerm openTerm = termPoolFromIndex.get(openTermIndex);

    if (isVariable(openTerm.symbolNameStringStringIndex) || isSequenceVariable(openTerm.symbolNameStringStringIndex))
      return bindings[openTerm.symbolNameStringStringIndex - firstVariableStringIndex];

    // Try to evaluate
    int ret = evaluateIntrinsicFunction(openTermIndex);
    if (ret != 0) return ret;

    // Neither variable nor evaluatable function so do ordinary matching
    int arity = openTerm.arity;
    int[] children = new int[arity];
    int newArity = 0;
    for (int i = 0; i < arity; i++) {
      children[i] = substitute(bindings, openTerm.childTermIndices[i]);
      ITerm childTerm = termPoolFromIndex.get(children[i]);
      if (isSequenceVariable(childTerm.symbolNameStringStringIndex))
        newArity += childTerm.arity;
      else
        newArity++;
    }

    if (newArity != arity) {// There were sequence variable bindings, so we must promote the children of the sequences
      int[] newChildren = new int[newArity];
      int nextNewChild = 0;

      for (int i = 0; i < arity; i++) { // If not a sequence child, copy else copy children
        ITerm childTerm = termPoolFromIndex.get(children[i]);
        if (childTerm.symbolNameStringStringIndex == 0) // Are we substituting a sequence variable?
          for (int j = 0; j < childTerm.arity; j++)
            newChildren[nextNewChild++] = childTerm.childTermIndices[j];
        else
          newChildren[nextNewChild++] = children[i];
      }
      children = newChildren;
    }

    return findTerm(openTerm.typeNameStringIndex, openTerm.symbolNameStringStringIndex, children.length, children);
  }

  /*
   * Intrinsic function evaluation
   */
  private int evaluateIntrinsicFunction(int termIndex) {
    ITerm term = termPoolFromIndex.get(termIndex);
    String intrinsicName = stringPoolFromIndex.get(term.symbolNameStringStringIndex);

    if (!isIntrinsicFunction(term.symbolNameStringStringIndex)) return 0;
    // System.out.println("Checking arity for intrinsic " + intrinsicName);
    // Now check that there is at least one child, that all children are leaves and that exactly one type appears
    if (term.arity == 0) return 0;
    // System.out.println("Passed arity check");
    int typeNameStringIndex = term.typeNameStringIndex;
    for (int i = 0; i < term.arity; i++) {
      // System.out.println("Type testing child " + i);
      int childTypeNameStringIndex = termPoolFromIndex.get(term.childTermIndices[i]).typeNameStringIndex;
      if (typeNameStringIndex == 0) typeNameStringIndex = childTypeNameStringIndex; // remeber first nonempty type
      if (childTypeNameStringIndex != 0 && typeNameStringIndex != childTypeNameStringIndex) return 0;
    }

    // System.out.println("After child type testing, type is " + typeNameStringIndex);
    if (typeNameStringIndex == 0) return 0; // No types anywhere so we don't know what to do
    int ret = term.childTermIndices[0];

    // System.out.println("Executing intrinsic");

    for (int child = 1; child < term.arity; child++)
      switch (Functions.valueOf(intrinsicName)) {
      case __add:
        if (typeNameStringIndex == stringPoolToIndex.get("__int"))
          ret = findTerm(Integer.toString(asInt(ret) + asInt(term.childTermIndices[child])));
        else if (typeNameStringIndex == stringPoolToIndex.get("__real"))
          ret = findTerm(Double.toString(asDouble(ret) + asDouble(term.childTermIndices[child])));
        else
          throw new ARTUncheckedException("__add intrinsic type error");
        break;
      case __and:
        break;
      case __ash:
        break;
      case __block:
        break;
      case __cardinality:
        break;
      case __cast:
        break;
      case __cat:
        break;
      case __close:
        break;
      case __cnd:
        break;
      case __contains:
        break;
      case __delete:
        break;
      case __deleteKey:
        break;
      case __difference:
        break;
      case __div:
        if (typeNameStringIndex == stringPoolToIndex.get("__int"))
          ret = findTerm(Integer.toString(asInt(ret) / asInt(term.childTermIndices[child])));
        else if (typeNameStringIndex == stringPoolToIndex.get("__real"))
          ret = findTerm(Double.toString(asDouble(ret) / asDouble(term.childTermIndices[child])));
        else
          throw new ARTUncheckedException("__dic intrinsic type error");
        break;
      case __eq:
        break;
      case __exp:
        break;
      case __finalKey:
        break;
      case __flush:
        break;
      case __ge:
        break;
      case __gt:
        break;
      case __input:
        break;
      case __insert:
        break;
      case __insertKey:
        break;
      case __intersection:
        break;
      case __is:
        break;
      case __isCastable:
        break;
      case __key:
        break;
      case __le:
        break;
      case __lsh:
        break;
      case __lt:
        break;
      case __mod:
        if (typeNameStringIndex == stringPoolToIndex.get("__int"))
          ret = findTerm(Integer.toString(asInt(ret) % asInt(term.childTermIndices[child])));
        else if (typeNameStringIndex == stringPoolToIndex.get("__real"))
          ret = findTerm(Double.toString(asDouble(ret) % asDouble(term.childTermIndices[child])));
        else
          throw new ARTUncheckedException("__mod intrinsic type error");
        break;
      case __mul:
        if (typeNameStringIndex == stringPoolToIndex.get("__int"))
          ret = findTerm(Integer.toString(asInt(ret) * asInt(term.childTermIndices[child])));
        else if (typeNameStringIndex == stringPoolToIndex.get("__real"))
          ret = findTerm(Double.toString(asDouble(ret) * asDouble(term.childTermIndices[child])));
        else
          throw new ARTUncheckedException("__mul intrinsic type error");
        break;
      case __ne:
        break;
      case __neg:
        break;
      case __not:
        break;
      case __open:
        break;
      case __or:
        break;
      case __output:
        break;
      case __range:
        break;
      case __rol:
        break;
      case __rolc:
        break;
      case __ror:
        break;
      case __rorc:
        break;
      case __rsh:
        break;
      case __sub:
        if (typeNameStringIndex == stringPoolToIndex.get("__int"))
          ret = findTerm(Integer.toString(asInt(ret) - asInt(term.childTermIndices[child])));
        else if (typeNameStringIndex == stringPoolToIndex.get("__real"))
          ret = findTerm(Double.toString(asDouble(ret) - asDouble(term.childTermIndices[child])));
        else
          throw new ARTUncheckedException("__sub intrinsic type error");
        break;
      case __union:
        break;
      case __update:
        break;
      case __updateOrdered:
        break;
      case __value:
        break;
      case __valueKey:
        break;
      case __xor:
        break;
      case __head:
        break;
      case __tail:
        break;
      default:
        break;
      }
    return ret;
  }

  private int asInt(int termIndex) {
    return Integer.parseInt(stringPoolFromIndex.get(termPoolFromIndex.get(termIndex).symbolNameStringStringIndex));
  }

  private double asDouble(int termIndex) {
    return Double.parseDouble(stringPoolFromIndex.get(termPoolFromIndex.get(termIndex).symbolNameStringStringIndex));
  }
}
