package uk.ac.rhul.cs.csle.art.term;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/*
 *  Low level implementation of terms which uses the Java API classes to represent individual terms, whilst maintaining statistics that reflect a more efficient pool implementation
 *
 *  Terms are then created by using findInStringPool() to ensure that the label is uniquely represented and then findInTermPool()
 *  to make a unique instance of that immutable term.
 *
 *  Each term has a number. In this implementation they are sequentially allocated, but in some applications the hashnumber would be the number.
 *
 */
public class ITermsLowLevelAPI extends ITerms {
  /*
   * Constructors
   */
  // public ITermsLowLevelAPI(BufferedReader input) { // Load with persistent data dump previously created with ITermpPool.dump()
  // undump(input);
  // }

  private final Map<String, Integer> stringToIndex = new HashMap<>(); // Each unique string is mapped onto the naturals
  final Map<Integer, String> indexToString = new HashMap<>(); // Reverse map for recovering the strings
  private Integer stringNextFreeIndex = 1; // defensive programming: index zero is never used and can be used as fail value

  public ITermsLowLevelAPI() {
    super();
    // Now create string map entries for built ins which will be used when decoding special actions in substitutions
    firstVariableIndex = getStringMapNextFreeIndex();
    findString("_");
    for (int v = 1; v <= variableCount; v++)
      findString("_" + v);
    firstSequenceVariableIndex = getStringMapNextFreeIndex();
    findString("_*");
    for (int v = 1; v <= sequenceVariableCount; v++)
      findString("_" + v + "*");
    firstSpecialSymbolIndex = getStringMapNextFreeIndex();

    firstNormalSymbolIndex = getStringMapNextFreeIndex();
  }

  abstract class ITermAPI {
    public abstract String getSymbolString();

    public abstract int getSymbolIndex();

    public abstract int getArity();

    public abstract int[] getChildren();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);
  }

  class ITermFixedArity extends ITermAPI {
    protected final int symbolIndex;
    protected final int[] children;

    public ITermFixedArity(int symbolIndex, int[] children) {
      this.symbolIndex = symbolIndex;
      this.children = children;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode(children);
      result = prime * result + symbolIndex;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      ITermFixedArity other = (ITermFixedArity) obj;
      if (!Arrays.equals(children, other.children)) return false;
      if (symbolIndex != other.symbolIndex) return false;
      return true;
    }

    @Override
    public String getSymbolString() {
      return indexToString.get(symbolIndex);
    }

    @Override
    public int getSymbolIndex() {
      return symbolIndex;
    }

    @Override
    public int getArity() {
      return children.length; // This is a cheat - we should be using the arity table...
    }

    @Override
    public int[] getChildren() {
      return children;
    }

    @Override
    public String toString() {
      return (symbolIndex + "(" + Arrays.toString(children) + ")");
    }

  }

  class ITermVariableArityAPI extends ITermFixedArity {
    private final int arity;

    public ITermVariableArityAPI(int symbolIndex, int[] children) {
      super(symbolIndex, children);
      arity = children.length;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + arity;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!super.equals(obj)) return false;
      if (getClass() != obj.getClass()) return false;
      ITermVariableArityAPI other = (ITermVariableArityAPI) obj;
      if (arity != other.arity) return false;
      return true;
    }

    @Override
    public int getArity() {
      return arity;
    }

    @Override
    public String toString() {
      return (symbolIndex + "(" + Arrays.toString(children) + ")");
    }
  }

  /*
   * Data
   */
  @Override
  protected int getStringMapNextFreeIndex() {
    return stringNextFreeIndex;
  }

  @Override
  public String getString(int stringIndex) {
    return indexToString.get(stringIndex);
  }

  private final Map<ITermAPI, Integer> termToIndex = new HashMap<>(); // Each unique immutable term is mapped onto the naturals
  final Map<Integer, ITermAPI> indexToTerm = new HashMap<>(); // Reverse map for recovering the terms
  private Integer termNextFreeIndex = 1; // defensive programming: index zero is never used

  private final Map<Integer, Integer> symbolIndexToArity = new HashMap<>();

  /*
   * kindOfs
   */
  @Override
  public boolean isVariableSymbol(int symbolIndex) {
    return symbolIndex >= firstVariableIndex && symbolIndex < firstSequenceVariableIndex;
  }

  @Override
  public boolean isVariableTerm(int term) {
    return isVariableSymbol(indexToTerm.get(term).getSymbolIndex());
  }

  @Override
  public boolean isSequenceVariableSymbol(int symbolIndex) {
    return symbolIndex >= firstSequenceVariableIndex && symbolIndex < firstSpecialSymbolIndex;
  }

  @Override
  public boolean isSequenceVariableTerm(int term) {
    return isSequenceVariableSymbol(indexToTerm.get(term).getSymbolIndex());
  }

  @Override
  public boolean isSpecialSymbol(int symbolIndex) {
    return symbolIndex >= firstSpecialSymbolIndex && symbolIndex < firstNormalSymbolIndex;
  }

  @Override
  public boolean isSpecialTerm(int term) {
    return isSpecialSymbol(indexToTerm.get(term).getSymbolIndex());
  }

  public boolean isNormalSymbol(int symbolIndex) {
    return symbolIndex >= firstNormalSymbolIndex;
  }

  public boolean isNormalTerm(int term) {
    int symbolIndex = indexToTerm.get(term).getSymbolIndex();
    return symbolIndex >= firstNormalSymbolIndex;
  }

  /*
   * Low level finders
   */
  @Override
  public int findString(String string) {
    Integer ret;
    if ((ret = stringToIndex.get(string)) == null) {
      stringToIndex.put(string, stringNextFreeIndex);
      indexToString.put(stringNextFreeIndex, new String(string)); // Don't assume the old string is immutable...
      ret = stringNextFreeIndex++;
    }
    // System.out.println("findString() on " + string + " returns " + ret);
    return ret;
  }

  @Override
  public int findTerm(int symbolStringIndex, int... children) {
    Integer ret;
    ITermAPI term = new ITermVariableArityAPI(symbolStringIndex, children);
    // System.out.print("findTerm() on " + symbolStringIndex + ":" + indexToString.get(symbolStringIndex));

    if ((ret = termToIndex.get(term)) == null) {
      termToIndex.put(term, termNextFreeIndex);
      indexToTerm.put(termNextFreeIndex, term);
      ret = termNextFreeIndex++;
    }

    // System.out.println(" returns " + ret);
    return ret;
  }

  @Override
  public int findTerm(String string, int... children) {
    return findTerm(findString(string), children);
  }

  public int findTerm(int symbolStringIndex, LinkedList<Integer> children) {
    int nc = 0, newChildren[] = new int[children.size()];
    for (int i : children)
      newChildren[nc++] = i;
    return findTerm(symbolStringIndex, newChildren);
  }

  @Override
  public int findTerm(String string, LinkedList<Integer> children) {
    return findTerm(findString(string), children);
  }

  /*
   * Attributes
   */
  @Override
  public int getTermArity(int term) {
    return indexToTerm.get(term).getArity();
  }

  @Override
  public String getTermSymbolString(int term) {
    return indexToTerm.get(term).getSymbolString();
  }

  @Override
  public int getTermSymbolIndex(int term) {
    var apiTerm = indexToTerm.get(term);
    return apiTerm.getSymbolIndex();
  }

  @Override
  public int[] getTermChildren(int term) {
    return indexToTerm.get(term).getChildren();
  }

  @Override
  public int getTermVariableNumber(int term) {
    return getTermSymbolIndex(term) - firstVariableIndex;
  }

  /*
   * Pool statistics
   */
  @Override
  public int stringCardinality() {
    return stringNextFreeIndex - 1; // zero element does not count as an entry
  }

  @Override
  public int getStringTotalBytes() { // Compute how much memory a hardware implementation would require
    int ret = 0;
    for (String k : stringToIndex.keySet())
      ret += k.length() + 1; // Allow for terminating byte in flat memory implementation

    return ret;
  }

  @Override
  public int termCardinality() {
    return termNextFreeIndex - 1; // zero element does not count as an entry
  }

  @Override
  public int termBytes() { // Compute how much memory a harware implentation would require
    int ret = 0;
    for (int k : indexToTerm.keySet()) {
      Integer arity = symbolIndexToArity.get(k);
      // if (arity == null) System.out.println("Warning - variable arity constructor detected");
      ret += 1; // add one for the constructor
      ret += (arity == null) ? indexToTerm.get(k).getArity() + 1 : arity;
    }
    return ret;
  }

}
