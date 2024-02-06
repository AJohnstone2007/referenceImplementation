package uk.ac.rhul.cs.csle.art.old.term;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
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

    // Cut and paste these from the output after running the mainline in ITerms.java
    if (findString("__bottom") != 33) System.out.println("String index mismatch for __bottom");
    if (findString("__done") != 34) System.out.println("String index mismatch for __done");
    if (findString("__empty") != 35) System.out.println("String index mismatch for __empty");
    if (findString("__quote") != 36) System.out.println("String index mismatch for __quote");
    if (findString("__proc") != 37) System.out.println("String index mismatch for __proc");
    if (findString("__procV3") != 38) System.out.println("String index mismatch for __procV3");
    if (findString("__input") != 39) System.out.println("String index mismatch for __input");
    if (findString("__output") != 40) System.out.println("String index mismatch for __output");
    if (findString("__blob") != 41) System.out.println("String index mismatch for __blob");
    if (findString("__binding") != 42) System.out.println("String index mismatch for __binding");
    if (findString("__adtProd") != 43) System.out.println("String index mismatch for __adtProd");
    if (findString("__adtSum") != 44) System.out.println("String index mismatch for __adtSum");
    if (findString("__bool") != 45) System.out.println("String index mismatch for __bool");
    if (findString("__char") != 46) System.out.println("String index mismatch for __char");
    if (findString("__intAP") != 47) System.out.println("String index mismatch for __intAP");
    if (findString("__int32") != 48) System.out.println("String index mismatch for __int32");
    if (findString("__realAP") != 49) System.out.println("String index mismatch for __realAP");
    if (findString("__real64") != 50) System.out.println("String index mismatch for __real64");
    if (findString("__string") != 51) System.out.println("String index mismatch for __string");
    if (findString("__array") != 52) System.out.println("String index mismatch for __array");
    if (findString("__list") != 53) System.out.println("String index mismatch for __list");
    if (findString("__flexArray") != 54) System.out.println("String index mismatch for __flexArray");
    if (findString("__set") != 55) System.out.println("String index mismatch for __set");
    if (findString("__map") != 56) System.out.println("String index mismatch for __map");
    if (findString("__mapChain") != 57) System.out.println("String index mismatch for __mapChain");
    if (findString("__eq") != 58) System.out.println("String index mismatch for __eq");
    if (findString("__ne") != 59) System.out.println("String index mismatch for __ne");
    if (findString("__gt") != 60) System.out.println("String index mismatch for __gt");
    if (findString("__lt") != 61) System.out.println("String index mismatch for __lt");
    if (findString("__ge") != 62) System.out.println("String index mismatch for __ge");
    if (findString("__le") != 63) System.out.println("String index mismatch for __le");
    if (findString("__compare") != 64) System.out.println("String index mismatch for __compare");
    if (findString("__not") != 65) System.out.println("String index mismatch for __not");
    if (findString("__and") != 66) System.out.println("String index mismatch for __and");
    if (findString("__or") != 67) System.out.println("String index mismatch for __or");
    if (findString("__xor") != 68) System.out.println("String index mismatch for __xor");
    if (findString("__cnd") != 69) System.out.println("String index mismatch for __cnd");
    if (findString("__lsh") != 70) System.out.println("String index mismatch for __lsh");
    if (findString("__rsh") != 71) System.out.println("String index mismatch for __rsh");
    if (findString("__ash") != 72) System.out.println("String index mismatch for __ash");
    if (findString("__rol") != 73) System.out.println("String index mismatch for __rol");
    if (findString("__ror") != 74) System.out.println("String index mismatch for __ror");
    if (findString("__neg") != 75) System.out.println("String index mismatch for __neg");
    if (findString("__add") != 76) System.out.println("String index mismatch for __add");
    if (findString("__sub") != 77) System.out.println("String index mismatch for __sub");
    if (findString("__mul") != 78) System.out.println("String index mismatch for __mul");
    if (findString("__div") != 79) System.out.println("String index mismatch for __div");
    if (findString("__mod") != 80) System.out.println("String index mismatch for __mod");
    if (findString("__exp") != 81) System.out.println("String index mismatch for __exp");
    if (findString("__size") != 82) System.out.println("String index mismatch for __size");
    if (findString("__cat") != 83) System.out.println("String index mismatch for __cat");
    if (findString("__slice") != 84) System.out.println("String index mismatch for __slice");
    if (findString("__get") != 85) System.out.println("String index mismatch for __get");
    if (findString("__put") != 86) System.out.println("String index mismatch for __put");
    if (findString("__contains") != 87) System.out.println("String index mismatch for __contains");
    if (findString("__remove") != 88) System.out.println("String index mismatch for __remove");
    if (findString("__extract") != 89) System.out.println("String index mismatch for __extract");
    if (findString("__union") != 90) System.out.println("String index mismatch for __union");
    if (findString("__intersection") != 91) System.out.println("String index mismatch for __intersection");
    if (findString("__difference") != 92) System.out.println("String index mismatch for __difference");
    if (findString("__cast") != 93) System.out.println("String index mismatch for __cast");
    if (findString("__cast__bool") != 94) System.out.println("String index mismatch for __cast__bool");
    if (findString("__cast__char") != 95) System.out.println("String index mismatch for __cast__char");
    if (findString("__cast__intAP") != 96) System.out.println("String index mismatch for __cast__intAP");
    if (findString("__cast__int32") != 97) System.out.println("String index mismatch for __cast__int32");
    if (findString("__cast__realAP") != 98) System.out.println("String index mismatch for __cast__realAP");
    if (findString("__cast__real64") != 99) System.out.println("String index mismatch for __cast__real64");
    if (findString("__cast__string") != 100) System.out.println("String index mismatch for __cast__string");
    if (findString("__cast__array") != 101) System.out.println("String index mismatch for __cast__array");
    if (findString("__cast__list") != 102) System.out.println("String index mismatch for __cast__list");
    if (findString("__cast__flexArray") != 103) System.out.println("String index mismatch for __cast__flexArray");
    if (findString("__cast__set") != 104) System.out.println("String index mismatch for __cast__set");
    if (findString("__cast__map") != 105) System.out.println("String index mismatch for __cast__map");
    if (findString("__cast__mapChain") != 106) System.out.println("String index mismatch for __cast__mapChain");
    if (findString("__termArity") != 107) System.out.println("String index mismatch for __termArity");
    if (findString("__termRoot") != 108) System.out.println("String index mismatch for __termRoot");
    if (findString("__termChild") != 109) System.out.println("String index mismatch for __termChild");
    if (findString("__termMake") != 110) System.out.println("String index mismatch for __termMake");
    if (findString("__termMatch") != 111) System.out.println("String index mismatch for __termMatch");
    if (findString("__user") != 112) System.out.println("String index mismatch for __user");

    firstNormalSymbolIndex = getStringMapNextFreeIndex();
    bottomTermIndex = findTerm("__bottom");
    doneTermIndex = findTerm("__done");
    emptyTermIndex = findTerm("__empty");

    valueBoolTrue = new __bool(true);
    valueBoolFalse = new __bool(false);
    valueInt32Zero = new __int32(0, 0);
    valueInt32One = new __int32(1, 0);
    valueInt32MinusOne = new __int32(-1, 0);
    valueIntAPZero = new __intAP(new BigInteger("0"));
    valueIntAPOne = new __intAP(new BigInteger("1"));
    valueIntAPMinusOne = new __intAP(new BigInteger("-1"));
    valueBottom = new __bottom();
    valueDone = new __done();
    valueEmpty = new __empty();
    valueBlob = new __blob(emptyTermIndex);
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
