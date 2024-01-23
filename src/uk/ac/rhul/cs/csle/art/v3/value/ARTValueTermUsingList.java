package uk.ac.rhul.cs.csle.art.v3.value;

/***
 * ARTValueTerm.java
 *
 * This is a term implementation which sits atop the value system. It provides match and substitute functionality for trees made up of
 * ARTValueTerms, each node of which is a carrier for an ARTValue.
 *
 * An ARTValueTerm has a payload which is any ARTValue and a list of children which must be ARTValueTerms
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uk.ac.rhul.cs.csle.art.core.ARTUncheckedException;

public class ARTValueTermUsingList extends ARTValue {
  protected ARTValue payload; // this is the payload: typically a string or some other identifier signifying the constructor
  private final List<ARTValueTermUsingList> children = new ArrayList<ARTValueTermUsingList>();

  public ARTValueTermUsingList(ARTValue payload) {
    this.payload = payload;
  }

  public ARTValueTermUsingList(String string, ARTValueTermUsingList... children) {
    this.payload = new ARTValueString(string);
    for (ARTValueTermUsingList c : children)
      addChild(c);
  }

  @Override
  public String toString() {
    return (payload == null ? "( )" : payload.toString()) + toStringChildren();
  }

  private String toStringChildren() {
    if (getChildren().isEmpty()) return "";

    String ret = "(";
    boolean first = true;
    for (ARTValueTermUsingList c : getChildren()) {
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
    if (getChildren().isEmpty()) return "";

    String ret = mapString("(", map);
    boolean first = true;
    for (ARTValueTermUsingList c : getChildren()) {
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
  public ARTValue getPayload() {
    return payload;
  }

  public void setPayload(ARTValue payload) {
    this.payload = payload;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((children == null) ? 0 : children.hashCode());
    result = prime * result + ((payload == null) ? 0 : payload.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTValueTermUsingList)) return false;
    ARTValueTermUsingList other = (ARTValueTermUsingList) obj;
    if (children == null) {
      if (other.children != null) return false;
    } else if (!children.equals(other.children)) return false;
    if (payload == null) {
      if (other.payload != null) return false;
    } else if (!payload.equals(other.payload)) return false;
    return true;
  }

  public List<ARTValueTermUsingList> getChildren() {
    return children;
  }

  public void addChild(ARTValueTermUsingList v) {
    children.add(v);
  }

  public boolean closedMatchPatternSingletonBindings(ARTValueTermUsingList pattern, Map<ARTValueTermVariable, ARTValueTermUsingList> bindings) {
    // ARTSOSSpecification.trace("Term.closedMatch(" + this + ", " + pattern + ")");
    if (payload instanceof ARTValueTermVariable)
      throw new ARTUncheckedException("Attempt to execute closedMatch() on term " + this + " but root is labelled with a variable");
    if (pattern.getPayload() instanceof ARTValueTermVariable) {
      ARTValueTermVariable key = (ARTValueTermVariable) pattern.getPayload();

      // ARTSOSSpecification.trace("Pattern root is variable: binding " + key + " to " + this);
      if (bindings.containsKey(key)) throw new ARTUncheckedException("Attempt to rebind term variable " + key);
      bindings.put(key, this);
      return true;
    }

    boolean ret = payload.equals(pattern.getPayload());
    // ARTSOSSpecification.trace("Pattern root " + payload + (ret ? " passed" : " failed") + " match against " + pattern.getPayload());
    if (!ret) return false;

    Iterator<ARTValueTermUsingList> termIterator = children.iterator();
    Iterator<ARTValueTermUsingList> patternIterator = pattern.children.iterator();
    while (ret && termIterator.hasNext() && patternIterator.hasNext())
      if (!termIterator.next().closedMatchPatternSingletonBindings(patternIterator.next(), bindings)) {
        // ARTSOSSpecification.trace("Child match failed: returning false");
        return false;
      }

    if (termIterator.hasNext() || patternIterator.hasNext()) {
      // ARTSOSSpecification.trace("term and iterator arity do not match: returning false");
      return false;
    }

    // ARTSOSSpecification.trace("Pattern children match for root " + payload + (ret ? " passed" : " failed"));

    // ARTSOSSpecification.trace("Term.closedMatch(" + this + ", " + pattern + ") returns true");
    return true;
  }

  public boolean closedMatchPatternRepeatBindings(ARTValueTermUsingList pattern, Map<ARTValueTermVariable, ARTValueTermUsingList> bindings) {
    if (payload instanceof ARTValueTermVariable)
      throw new ARTUncheckedException("Attempt to execute closedMatch() on term " + this + " but root is labelled with a variable");
    if (pattern.getPayload() instanceof ARTValueTermVariable) {
      ARTValueTermVariable key = (ARTValueTermVariable) pattern.getPayload();

      if (bindings.containsKey(key))
        return closedMatchClosed(bindings.get(key));
      else
        bindings.put(key, this);
      return true;
    }

    boolean ret = payload.equals(pattern.getPayload());
    if (!ret) return false;

    Iterator<ARTValueTermUsingList> termIterator = children.iterator();
    Iterator<ARTValueTermUsingList> patternIterator = pattern.children.iterator();
    while (ret && termIterator.hasNext() && patternIterator.hasNext())
      if (!termIterator.next().closedMatchPatternRepeatBindings(patternIterator.next(), bindings)) {
        return false;
      }

    if (termIterator.hasNext() || patternIterator.hasNext()) {
      return false;
    }

    return true;
  }

  // Recursively match a closed term to this closed term
  public boolean closedMatchClosed(ARTValueTermUsingList closedTerm) {
    if (payload instanceof ARTValueTermVariable)
      throw new ARTUncheckedException("Attempt to execute closedMatchClosed() on term " + this + " but root is labelled with a variable");

    if (closedTerm.payload instanceof ARTValueTermVariable)
      throw new ARTUncheckedException("Attempt to execute closedMatchClosed() on pattern " + this + " but root is labelled with a variable");

    if (!payload.equals(closedTerm.getPayload())) return false;

    Iterator<ARTValueTermUsingList> termIterator = children.iterator();
    Iterator<ARTValueTermUsingList> patternIterator = closedTerm.children.iterator();

    while (termIterator.hasNext() && patternIterator.hasNext())
      if (!termIterator.next().closedMatchClosed(patternIterator.next())) return false;

    if (termIterator.hasNext() || patternIterator.hasNext()) return false;

    return true;
  }

  // Recursively substitute against a set of bindings
  public ARTValueTermUsingList substitute(Map<ARTValueTermVariable, ARTValueTermUsingList> bindings) {
    if (payload instanceof ARTValueTermVariable) {
      if (!bindings.containsKey(payload)) throw new ARTUncheckedException("Attempt to substitute unbound term variable " + payload);
      return bindings.get(payload);
    }
    ARTValueTermUsingList ret = new ARTValueTermUsingList(payload);
    for (ARTValueTermUsingList t : children)
      ret.children.add(t.substitute(bindings));

    return ret;
  }

  // Recursively construct a copy of ourselves
  public ARTValueTermUsingList deepcopy() {
    ARTValueTermUsingList ret = new ARTValueTermUsingList(this.payload);
    for (ARTValueTermUsingList c : children)
      ret.children.add(c.deepcopy());
    return ret;
  }
}
