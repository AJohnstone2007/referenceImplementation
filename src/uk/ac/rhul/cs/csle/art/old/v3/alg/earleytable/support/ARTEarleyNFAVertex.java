package uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.support;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.util.cache.ARTCache;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElement;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementEoS;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementEpsilon;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementNonterminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstance;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceCat;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceEpsilon;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceSlot;

public class ARTEarleyNFAVertex {
  /*
   * An NFA vertex is uniquely identified by its label which is a set of grammar slots.
   *
   * Each vertex has associated with it: unique integer label called its state number
   *
   * The outedges are labelled with a grammar symbol, and each such edge has three associated sets
   *
   * an epn set an ee set a red set of
   *
   * These sets are stored in the vertex, not on the edge. We maintain a map from ARTGrammar symbol to set for each edge label
   *
   * These sets are constructed in method computePayload() below
   *
   */

  private int number;

  private final Set<ARTGrammarInstanceSlot> label;
  private final boolean notCore;
  private final HashMap<ARTGrammarElement, Integer> epnMap = new HashMap<>();
  private final HashMap<ARTGrammarElement, Integer> eeMap = new HashMap<>();
  private final HashMap<ARTGrammarElement, Integer> redMap = new HashMap<>();
  private final HashMap<ARTGrammarElement, ARTEarleyNFAVertex> outEdgeMap = new HashMap<>();
  private final Set<ARTGrammarElement> select = new HashSet<>();
  private final Set<ARTGrammarElementNonterminal> rLHS = new HashSet<>();

  public ARTEarleyNFAVertex(Set<ARTGrammarInstanceSlot> slots, boolean constructedByEpsilonMove) {
    super();
    this.label = new HashSet<ARTGrammarInstanceSlot>(slots);
    this.notCore = constructedByEpsilonMove;
  }

  public HashMap<ARTGrammarElement, Integer> getEpnMap() {
    return epnMap;
  }

  public HashMap<ARTGrammarElement, Integer> getEeMap() {
    return eeMap;
  }

  public HashMap<ARTGrammarElement, Integer> getRedMap() {
    return redMap;
  }

  public HashMap<ARTGrammarElement, ARTEarleyNFAVertex> getOutEdgeMap() {
    return outEdgeMap;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public <T> String sortedSetString(Set<T> set) {
    StringWriter sw = new StringWriter();
    Object[] slotArray = set.toArray();
    Arrays.sort(slotArray);
    for (Object s : slotArray)
      sw.write(((ARTGrammarInstance) s).toGrammarString(".") + "\n");
    return sw.toString();
  }

  @Override
  public String toString() {
    return "G" + number + "\n" + sortedSetString(label);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + number;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTEarleyNFAVertex)) return false;
    ARTEarleyNFAVertex other = (ARTEarleyNFAVertex) obj;
    if (number != other.number) return false;
    return true;
  }

  public boolean isConstructedByEpsilonMove() {
    return notCore;
  }

  public Set<ARTGrammarInstanceSlot> getLabel() {
    return label;
  }

  public int getNumber() {
    return number;
  }

  // Test whether an individual payload has epsilon in its first set
  private boolean isNullable(ARTGrammarElement payload, ARTGrammarElement epsilon) {
    boolean ret;
    // System.err.print("isNullable() on " + payload);
    if (payload == null)
      ret = true; // Special case for espilon instances which have no payload
    else {
      if (payload instanceof ARTGrammarElementNonterminal) {
        // System.err.print("First set is " + ((ARTGrammarElementNonterminal) payload).first);
        ret = ((ARTGrammarElementNonterminal) payload).getFirst().contains(epsilon);
      } else
        ret = false;
    }
    // System.err.println(" returns " + ret);
    return ret;
  }

  // Check that a sequence of grammar instances is nullable and is at least minimumLength symbols long
  private boolean isNullable(int minimumLength, ARTGrammarInstance startInstance, ARTGrammarInstance stopInstance, ARTGrammarElement epsilon) {
    // System.err.println("isNullable() on " + startInstance + " to " + stopInstance + " with minimum length " + minimumLength);
    int length = 0;
    for (ARTGrammarInstance i = startInstance; i != stopInstance; i = i.getSibling().getSibling()) {
      length++;
      if (!isNullable(i.getPayload(), epsilon)) {
        // System.err.println("Found non-null element; returning false");
        return false;
      }
    }
    // System.err.println("Length " + length);

    if (length >= minimumLength) {
      // System.err.println("Returning true");
      return true;
    } else {
      // System.err.println("Returning false");
      return false;
    }
  }

  // Ensure that we are not the slot before the last instance
  // slot.sibling is our instance (x)
  // slot.sibling.sibling is the right slot of our instance
  // slot.sibling.sibling.sibling is the following instance
  private boolean suffixNotEmpty(ARTGrammarInstanceSlot slot) {
    boolean ret = slot.getSibling() != null && slot.getSibling().getSibling().getSibling() != null;
    // System.err.println("suffixNotEmpty on " + slot.toGrammarString(".") + " returns " + ret);
    return ret;
  }

  private void addSlot(Map<ARTGrammarInstanceSlot, ARTGrammarInstanceSlot> prefixSlotMap, ARTChiSet chiSet, ARTGrammarInstanceSlot slot) {
    // System.err.println("Adding slot " + slot.toGrammarPrefixString());
    chiSet.add(prefixSlotMap.get(slot));
  }

  public void computePayload(Map<ARTGrammarInstanceSlot, ARTGrammarInstanceSlot> prefixSlotMap, ARTGrammarElementEpsilon epsilon,
      ARTCache<ARTChiSet> chiSetCache, ARTCache<Set<ARTGrammarElement>> redSetCache) {
    final HashMap<ARTGrammarElement, ARTChiSet> epnSetMap = new HashMap<>();
    final HashMap<ARTGrammarElement, ARTChiSet> eeSetMap = new HashMap<>();
    final HashMap<ARTGrammarElement, Set<ARTGrammarElement>> redSetMap = new HashMap<>();

    // Create the epn, ee and red sets for every outedge of this vertex
    for (ARTGrammarElement s : outEdgeMap.keySet()) {
      // System.println("In state " + number + "initialising sets for out edge labelled " + s);
      epnSetMap.put(s, new ARTChiSet());
      eeSetMap.put(s, new ARTChiSet());
    }
    // Create ee and epn sets for epsilon, even if there is no outedge labelled epsilon
    eeSetMap.put(epsilon, new ARTChiSet());
    epnSetMap.put(epsilon, new ARTChiSet());

    // Walk the slots, computing sets
    for (ARTGrammarInstanceSlot slot : label) {
      // System.err.println("Testing " + slot.toGrammarString("."));
      ARTGrammarInstance xInstanceNode = slot.getSibling(); // If we are at the end of a rule, instance will be null
      if (xInstanceNode instanceof ARTGrammarInstanceEpsilon) xInstanceNode = null; // If we are an epsilon rule, then formally we are at the end of a rule
      ARTGrammarElement x = xInstanceNode == null ? null : xInstanceNode.getPayload();
      ARTGrammarInstanceCat production = slot.getProductionL();
      ARTGrammarInstanceSlot productionInitialSlot = (ARTGrammarInstanceSlot) production.getChild();

      // W = N \cup T, L = { l | l \in W, l =*> \epsilon
      // EPNG(x), x \in W
      // Clause 1: Y ::= \gamma . x, \gammma in W* => add production: x must be the last thing in the production
      if (xInstanceNode != null && xInstanceNode.getSibling().getSibling() == null) // we are the last instance in a production
        epnSetMap.get(x).add(production);

      // Clause 2: Y ::= \delta . x \tau, \delta \in W+, \tau \in W+ => add \delta x
      // !NB = that is sibling(sibling(slot)) or sibling(instance)
      if (xInstanceNode != null && productionInitialSlot != slot /* \delta \ne epsilon */ && suffixNotEmpty(slot) /* \tau \ne \epsilon */)
        addSlot(prefixSlotMap, epnSetMap.get(x), (ARTGrammarInstanceSlot) xInstanceNode.getSibling());

      // EEG(\epsilon) !! Changed from EPNG(\Epsilon) 8 Dec 2019 on rework of lookahead in Earley Table
      if (notCore) {
        // Clause 1: Y ::= \gamma ., \gamma \in L* => add production
        if (xInstanceNode == null && isNullable(0, productionInitialSlot.getSibling(), null, epsilon)) eeSetMap.get(epsilon).add(production);

        // Clause 2: Y ::= \nu . \tau, \nu \in LL+, \tau \in W+ => add slot
        if (xInstanceNode != null && isNullable(2, productionInitialSlot.getSibling(), xInstanceNode, epsilon))
          addSlot(prefixSlotMap, eeSetMap.get(epsilon), slot);
      }
      // EEG(x)
      // Clause 1: Y ::= \gamma . x \mu, \gamma \in W*, \mu \in L* => add production
      if (xInstanceNode != null && isNullable(1, xInstanceNode.getSibling().getSibling(), null, epsilon)) eeSetMap.get(x).add(production);

      // Clause 2: Y ::= \gamma . x \mu \tau, \gamma \in W*, \mu \in L+, \tau \in W+ => add \delta x \mu
      if (xInstanceNode != null) { // !!! Check Tau below
        for (ARTGrammarInstanceSlot s = (ARTGrammarInstanceSlot) xInstanceNode.getSibling(); s.getSibling() != null
            && suffixNotEmpty(s); s = (ARTGrammarInstanceSlot) s.getSibling().getSibling()) {
          ARTGrammarInstance testInstance = s.getSibling();
          if (!isNullable(testInstance.getPayload(), epsilon)) break;
          addSlot(prefixSlotMap, eeSetMap.get(x), (ARTGrammarInstanceSlot) s.getSibling().getSibling()); // This element passes, so put its
          // right-sibling slot in
          // (because
          // the prefix is a slot is everything up to the slot)
        }
      }
      // EPNG(\epsilon) = {} so nothing to do

      // Note RED sets are not used in the Lookahead variant but we'll leave this code in for future applications
      // RED(x)
      if (xInstanceNode == null) {
        ARTGrammarElementNonterminal lhs = (ARTGrammarElementNonterminal) slot.getLhsL().getPayload();
        for (ARTGrammarElement e : lhs.getFollow())
          if (e instanceof ARTGrammarElementEoS) {
            if (redSetMap.get(epsilon) == null) redSetMap.put(epsilon, new HashSet<ARTGrammarElement>());
            redSetMap.get(epsilon).add(lhs);

          } else {
            if (redSetMap.get(e) == null) redSetMap.put(e, new HashSet<ARTGrammarElement>());
            redSetMap.get(e).add(lhs);
          }
      }
    }

    // Now cache the sets and load the permanant maps
    for (ARTGrammarElement s : epnSetMap.keySet())
      epnMap.put(s, chiSetCache.find(epnSetMap.get(s)));

    for (ARTGrammarElement s : eeSetMap.keySet())
      eeMap.put(s, chiSetCache.find(eeSetMap.get(s)));

    for (ARTGrammarElement s : redSetMap.keySet())
      redMap.put(s, redSetCache.find(redSetMap.get(s)));

    eeMap.put(epsilon, chiSetCache.find(eeSetMap.get(epsilon)));
    epnMap.put(epsilon, chiSetCache.find(epnSetMap.get(epsilon)));

    // Now compute select set
    for (ARTGrammarInstanceSlot s : label)
      getSelect().addAll(s.getGuard());

    // Now compute rLHS set
    for (ARTGrammarInstanceSlot s : label)
      if (s.getSibling() == null) // only add in for end slots of the fom x ::= \alpha .
        getrLHS().add((ARTGrammarElementNonterminal) s.getLhsL().getPayload());
  }

  public Set<ARTGrammarElement> getSelect() {
    return select;
  }

  public Set<ARTGrammarElementNonterminal> getrLHS() {
    return rLHS;
  }
}
