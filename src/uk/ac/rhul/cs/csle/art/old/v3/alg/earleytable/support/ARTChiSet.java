package uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.support;

import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.core.ARTUncheckedException;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstance;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceCat;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceSlot;

public class ARTChiSet {
  private final Set<ARTGrammarInstance> set;

  public ARTChiSet(ARTChiSet chiSet) {
    set = new HashSet<>(chiSet.set);
  }

  public ARTChiSet() {
    set = new HashSet<>();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((set == null) ? 0 : set.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTChiSet)) return false;
    ARTChiSet other = (ARTChiSet) obj;
    if (set == null) {
      if (other.set != null) return false;
    } else if (!set.equals(other.set)) return false;
    return true;
  }

  /*
   * Each state G in an Earley NFA is decorated with sets EPN(x) and EE(x) which are of type ARTEarleyNFAChiSet (this class).
   *
   * A chi set may contain either a production or a string \gamma of ARTGrammarElement which will form the prefix of some slot
   *
   * In ART a production is represented by the root of the production tree which will be a cat node of type ARTGrammarInstanceCat
   *
   * In ART a slot is represented by a node of type ARTGrammarInstanceSlot
   *
   * All ARTGrammarInstanceX types are subtypes of ARTGrammarInstance, so we can represent a chi set as a set of ARTGrammarInstance elements e: if e is a cat
   * node then it represents a production, oherwise e must be a slot which then represensets the prefix string of that slot.
   *
   * This class is essentially just a wrapper around a Set<ARTGrammarInstance> but with two type-speficific add routines to provide some protection to the user,
   * and a toString method which renders the elements as prefixes or productions depending on whether they are slot or cat nodes
   *
   */

  /*
   * Note that these two add productions are simply using the type system to avoid programming errors: we could have a single add method that took an
   * ARTGrammarInstance but that would also allow (erroneously) ARTGrammarInstanceNonterminal and other types to be put into the Chi set.
   */
  public void add(ARTGrammarInstanceSlot slot) {
    set.add(slot);
  }

  public void add(ARTGrammarInstanceCat production) {
    set.add(production);
  }

  public Set<ARTGrammarInstance> getSet() {
    return set;
  }

  /* Scan the set elements, printing instances of ARTInstanceSlot as prefixes, and instances of ARTInstanceCat as productions */
  @Override
  public String toString() {
    String ret = "{";
    boolean first = true;
    for (ARTGrammarInstance instance : set) {
      if (instance instanceof ARTGrammarInstanceCat)
        ret += (first ? " " : ", ") + instance.toGrammarString();
      else
        ret += (first ? " " : ", ") + instance.toGrammarPrefixString();
      first = false;
    }
    return ret + "}";
  }

  public boolean isEmpty() {
    return set.isEmpty();
  }

  static boolean isPrefixString(ARTGrammarInstance instance) {
    if (instance instanceof ARTGrammarInstanceSlot) return true;
    if (instance instanceof ARTGrammarInstanceCat) return false;
    throw new ARTUncheckedException("attempt to classify Chi set element which is neith a prefix nor a production");
  }

  static Set<ARTGrammarInstanceCat> productionsOf(ARTGrammarInstance instance) {
    HashSet<ARTGrammarInstanceCat> ret = new HashSet<>();

    for (ARTGrammarInstanceCat child = (ARTGrammarInstanceCat) instance.getLhsL().getChild(); child != null; child = (ARTGrammarInstanceCat) child.getSibling())
      ret.add(child);

    return ret;
  }
}
