package uk.ac.rhul.cs.csle.art.v3.value;

import java.util.Map;

import uk.ac.rhul.cs.csle.art.core.ARTUncheckedException;
import uk.ac.rhul.cs.csle.art.term.ValueException;

public class ARTValueTerm extends ARTValue {
  protected ARTValue payload; // this is the payload: typically a string or some other identifier signifying the constructor
  private ARTValueTerm child = null;
  private ARTValueTerm sibling = null;

  public ARTValueTerm(ARTValue payload) {
    this.payload = payload;
  }

  public ARTValueTerm(String string, ARTValueTerm... children) {
    payload = new ARTValueString(string);
    for (ARTValueTerm c : children)
      addChild(c);
  }

  /*
   * Static term builder from string
   *
   * Grammar
   *
   * terms ::= term (',' term)*
   *
   * term ::= '`'? name ( '(' terms ')' )? // we are not allowed to have `x()
   *
   * name ::= ( "any printing characte except ` , ( )" )+
   */
  private static int index;
  private static String string;
  private static char c;
  private static final char eos = (char) 0;

  private static char getc() {
    if (index < string.length() - 1)
      return c = string.charAt(++index);
    else
      return c = (char) 0;
  }

  private static boolean isSpaceCharacter() {
    return Character.isSpaceChar(c);
  }

  private static boolean isEOSCharacter() {
    return c == eos;
  }

  private static void skipWhitespace() {
    while (isSpaceCharacter() && !isEOSCharacter())
      getc();
  }

  private static boolean matchBackquoteCharacter() {
    if (c == '`') {
      getc();
      skipWhitespace();
      return true;
    }

    return false;
  }

  private static boolean matchCommaCharacter() {
    if (c == ',') {
      getc();
      skipWhitespace();
      return true;
    }

    return false;
  }

  private static boolean matchOpenParenthesisCharacter() {
    if (c == '(') {
      getc();
      skipWhitespace();
      return true;
    }

    return false;
  }

  private static boolean matchCloseParenthesisCharacter() {
    if (c == ')') {
      getc();
      skipWhitespace();
      return true;
    }

    return false;
  }

  private static boolean isMetaCharacter() {
    return c == '(' || c == ')' || c == ',' || c == '`';
  }

  private static String name() {
    int start = index;
    while (!(isSpaceCharacter() || isMetaCharacter() || isEOSCharacter()))
      getc();
    int end = index; // index is one in advance of the character itself
    skipWhitespace();
    return string.substring(start, end);
  }

  private static ARTValueTerm term() {
    boolean isVariable = matchBackquoteCharacter(); // figure out use of interface
    if (isMetaCharacter()) throw new ARTUncheckedException("in ARTValue.maketerms() unexpected metacharacter " + c + " at start of name");

    ARTValueTerm ret = new ARTValueTerm(new ARTValueString(name()));
    if (matchOpenParenthesisCharacter()) {
      ret.child = terms();
      if (!matchCloseParenthesisCharacter()) throw new ARTUncheckedException("in ARTValueTerm.makeTerms(): expected ) found " + c);
    }

    return ret;
  }

  private static ARTValueTerm terms() {
    ARTValueTerm ret = term();
    ARTValueTerm tmp = ret;

    while (matchCommaCharacter()) {
      tmp.sibling = term();
      tmp = tmp.sibling;
    }

    return ret;

  }

  static public ARTValueTerm makeTerms(String stringTerm) throws ValueException {
    string = stringTerm;
    index = 0;
    c = string.charAt(index);
    skipWhitespace(); // Skip leading whitespace
    ARTValueTerm ret = terms();
    if (c != eos) throw new ValueException("in ARTValue.maketerms() unexpected characters following completet term");
    return ret;
  }

  public ARTValueTerm getChild() {
    return child;
  }

  public ARTValueTerm getSibling() {
    return sibling;
  }

  public void setChild(ARTValueTerm child) {
    this.child = child;
  }

  public void setSibling(ARTValueTerm sibling) {
    this.sibling = sibling;
  }

  @Override
  public ARTValue getPayload() {
    return payload;
  }

  public void setPayload(ARTValue payload) {
    this.payload = payload;
  }

  public void addChild(ARTValueTerm v) {
    if (v.sibling != null) throw new ARTUncheckedException("Attempt to add term as child which has non-null sibling field");
    if (child == null) {
      child = v;
      return;
    }
    for (ARTValueTerm c = child;; c = c.sibling)
      if (c.sibling == null) {
        c.sibling = v;
        return;
      }
  }

  @Override
  public String toString() {
    return (payload == null ? "( )" : payload.toString()) + toStringChildren();
  }

  private String toStringChildren() {
    if (child == null) return "";

    String ret = "(";
    boolean first = true;
    for (ARTValueTerm c = child; c != null; c = c.sibling) {
      if (first)
        first = false;
      else
        ret += ", ";
      ret += c.toString();
    }
    ret += ")";

    return ret;
  }

  @Override
  public String toLatexString(Map<String, String> map) {
    return payload.toLatexString(map) + toStringChildren(map);
  }

  private String toStringChildren(Map<String, String> map) {
    if (child == null) return "";

    String ret = mapString("(", map);
    boolean first = true;
    for (ARTValueTerm c = child; c != null; c = c.sibling) {
      if (first)
        first = false;
      else
        ret += mapString(",", map) + " ";
      ret += c.toLatexString(map);
    }
    ret += mapString(")", map);

    return ret;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((child == null) ? 0 : child.hashCode());
    result = prime * result + ((payload == null) ? 0 : payload.hashCode());
    result = prime * result + ((sibling == null) ? 0 : sibling.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTValueTerm)) return false;
    ARTValueTerm other = (ARTValueTerm) obj;
    if (child == null) {
      if (other.child != null) return false;
    } else if (!child.equals(other.child)) return false;
    if (payload == null) {
      if (other.payload != null) return false;
    } else if (!payload.equals(other.payload)) return false;
    if (sibling == null) {
      if (other.sibling != null) return false;
    } else if (!sibling.equals(other.sibling)) return false;
    return true;
  }

  // public boolean closedMatchPatternSingletonBindings(ARTValueTerm pattern, Map<ARTValueTermVariable, ARTValueTerm> bindings) throws ARTException {
  // // ARTSOSSpecification.trace("Term.closedMatch(" + this + ", " + pattern + ")");
  // if (payload instanceof ARTValueTermVariable)
  // throw new ARTException("Attempt to execute closedMatch() on term " + this + " but root is labelled with a variable");
  // if (pattern.getPayload() instanceof ARTValueTermVariable) {
  // ARTValueTermVariable key = (ARTValueTermVariable) pattern.getPayload();
  //
  // // ARTSOSSpecification.trace("Pattern root is variable: binding " + key + " to " + this);
  // if (bindings.containsKey(key)) throw new ARTException("Attempt to rebind term variable " + key);
  // bindings.put(key, this);
  // return true;
  // }
  //
  // boolean ret = payload.equals(pattern.getPayload());
  // // ARTSOSSpecification.trace("Pattern root " + payload + (ret ? " passed" : " failed") + " match against " + pattern.getPayload());
  // if (!ret) return false;
  //
  // Iterator<ARTValueTerm> termIterator = children.iterator();
  // Iterator<ARTValueTerm> patternIterator = pattern.children.iterator();
  // while (ret && termIterator.hasNext() && patternIterator.hasNext())
  // if (!termIterator.next().closedMatchPatternSingletonBindings(patternIterator.next(), bindings)) {
  // // ARTSOSSpecification.trace("Child match failed: returning false");
  // return false;
  // }
  //
  // if (termIterator.hasNext() || patternIterator.hasNext()) {
  // // ARTSOSSpecification.trace("term and iterator arity do not match: returning false");
  // return false;
  // }
  //
  // // ARTSOSSpecification.trace("Pattern children match for root " + payload + (ret ? " passed" : " failed"));
  //
  // // ARTSOSSpecification.trace("Term.closedMatch(" + this + ", " + pattern + ") returns true");
  // return true;
  // }
  //
  // public boolean closedMatchPatternRepeatBindings(ARTValueTerm pattern, Map<ARTValueTermVariable, ARTValueTerm> bindings) throws ARTException {
  // if (payload instanceof ARTValueTermVariable)
  // throw new ARTException("Attempt to execute closedMatch() on term " + this + " but root is labelled with a variable");
  // if (pattern.getPayload() instanceof ARTValueTermVariable) {
  // ARTValueTermVariable key = (ARTValueTermVariable) pattern.getPayload();
  //
  // if (bindings.containsKey(key))
  // return closedMatchClosed(bindings.get(key));
  // else
  // bindings.put(key, this);
  // return true;
  // }
  //
  // boolean ret = payload.equals(pattern.getPayload());
  // if (!ret) return false;
  //
  // Iterator<ARTValueTerm> termIterator = children.iterator();
  // Iterator<ARTValueTerm> patternIterator = pattern.children.iterator();
  // while (ret && termIterator.hasNext() && patternIterator.hasNext())
  // if (!termIterator.next().closedMatchPatternRepeatBindings(patternIterator.next(), bindings)) {
  // return false;
  // }
  //
  // if (termIterator.hasNext() || patternIterator.hasNext()) {
  // return false;
  // }
  //
  // return true;
  // }
  //
  // // Recursively match a closed term to this closed term
  // public boolean closedMatchClosed(ARTValueTerm closedTerm) throws ARTException {
  // if (payload instanceof ARTValueTermVariable)
  // throw new ARTException("Attempt to execute closedMatchClosed() on term " + this + " but root is labelled with a variable");
  //
  // if (closedTerm.payload instanceof ARTValueTermVariable)
  // throw new ARTException("Attempt to execute closedMatchClosed() on pattern " + this + " but root is labelled with a variable");
  //
  // if (!payload.equals(closedTerm.getPayload())) return false;
  //
  // Iterator<ARTValueTerm> termIterator = children.iterator();
  // Iterator<ARTValueTerm> patternIterator = closedTerm.children.iterator();
  //
  // while (termIterator.hasNext() && patternIterator.hasNext())
  // if (!termIterator.next().closedMatchClosed(patternIterator.next())) return false;
  //
  // if (termIterator.hasNext() || patternIterator.hasNext()) return false;
  //
  // return true;
  // }
  //
  // // Recursively substitute against a set of bindings
  // public ARTValueTerm substitute(Map<ARTValueTermVariable, ARTValueTerm> bindings) throws ARTException {
  // if (payload instanceof ARTValueTermVariable) {
  // if (!bindings.containsKey(payload)) throw new ARTException("Attempt to substitute unbound term variable " + payload);
  // return bindings.get(payload);
  // }
  // ARTValueTerm ret = new ARTValueTerm(payload);
  // for (ARTValueTerm t : children)
  // ret.children.add(t.substitute(bindings));
  //
  // return ret;
  // }
  //
  // // Recursively construct a copy of ourselves
  // public ARTValueTerm deepcopy() {
  // ARTValueTerm ret = new ARTValueTerm(this.payload);
  // for (ARTValueTerm c : children)
  // ret.children.add(c.deepcopy());
  // return ret;
  // }
}
