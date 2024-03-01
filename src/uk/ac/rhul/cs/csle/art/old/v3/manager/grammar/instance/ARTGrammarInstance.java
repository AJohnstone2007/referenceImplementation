package uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance;

import java.io.PrintWriter;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.rhul.cs.csle.art.old.util.graph.ARTAbstractGraph;
import uk.ac.rhul.cs.csle.art.old.util.graph.ARTTreeVertexDoublyLinked;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.ARTFold;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElement;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementNonterminal;

public class ARTGrammarInstance extends ARTTreeVertexDoublyLinked implements Comparable<ARTGrammarInstance> {

  public final Set<ARTGrammarElement> first = new TreeSet<ARTGrammarElement>();
  public final Set<ARTGrammarElement> follow = new TreeSet<ARTGrammarElement>();
  public final Set<ARTGrammarElement> guard = new TreeSet<ARTGrammarElement>();

  public Set<ARTGrammarElement> getFirst() {
    return first;
  }

  public Set<ARTGrammarElement> getFollow() {
    return follow;
  }

  public ARTFold fold = ARTFold.EMPTY; // Fold operator attached to this instance node
  public int instanceNumberWithinProduction; // instance number for this element in a production to generate attribute name
  public String instanceName; // For a nonterminal with attributes or a names terminal, the instance name
  // For named nonterminals and terminals the instance name is that name
  // For unnamed nonterminals the instance name is node id concatentated with the instance number
  public String gatherName; // the gather nonterminal name given in a ! expression in the RDT
  public char rangeUpperCharacterTerminal; // Top element of character enumeration

  public ARTGrammarInstance tearLink; // for nodes labelled 'tear', a link back to the node that is to be inserted
  public ARTGrammarInstanceLHS lhsL; // Left Hand Side nonterminal rule (pos nodes only)
  public ARTGrammarInstanceCat productionL; // the root instance for this production
  public int prefixLength = 23; // Huh? what's this value? probably an unlikely value for debug tracing
  public ARTGrammarInstance niL; // Nonterminal instance preceding slot (pos nodes only)
  public ARTGrammarInstance aL;
  public ARTGrammarInstance nL;
  public ARTGrammarInstance pL;
  public ARTGrammarInstance lrL; // The L_r slot
  public ARTGrammarInstance erL; // The E_r slot
  public ARTGrammarInstance ssL; // The semantics selector node

  public boolean isLHS; // is top level scaffolding node for this nonterminal
  public boolean isDelayed; // Delay evaluation at this node
  public boolean isSPPFLabel; // Corresponds to an SPPF label in the generated parser
  public boolean isCodeLabel; // Corresponds to a GOTO label in the generated parser
  public boolean isTestRepeatLabel; // Corresponds to a testRepeat() label parameter in the generated parser
  public boolean isAltLabel; // Corresponds to a GLL_A_ style label used in the alternate template
  public boolean isRELabel; // Corresponds to a GLL_T_ style label used in the nullable bracket templates - only set during parser output!
  public boolean isReferredLabel; // Corresponds to a label that is used in the aL, pL
  public boolean isClosureLabel; // Corresponds to a label that is used in closure templates
  public boolean isGiftLabel; // Corresponds to an element that has a GIFT operator
  public boolean isSlotSelector; // Is a POS node for which a selector set should be emitted
  public boolean isSlotParentLabel; // Labels a POS node which has semantics or insertions below it
  public boolean isPopD; // Is popping descriptor
  public boolean isPostPredictivePop; // Is after deep end of FGLL do-first bracket
  public boolean isPredictivePop; // Is deep end of FGLL do-first bracket
  public boolean isNullableBracket; // bracket body matches epsilon OR bracket is )? OR bracket is )*

  public boolean isEoOP; // is End of Optional or Parenthesis (pos nodes only) - now does not appear in paper, but still needs to be computed
  public boolean isEoA; // is End of Alternate (pos nodes only)
  public boolean isEoR; // is End of Rule (pos nodes only)
  public boolean isFiR; // is First in Rule (pos nodes only)
  public boolean isFfCE; // + is First in Positive Closure (pos nodes only) - added in Auguest 2016 EBNF paper

  public boolean isEoD; // is End of Sequence under Do first
  public boolean isEoO; // is End of Sequence under Optional
  public boolean isEoP; // is End of Sequence under Positive Closure
  public boolean isEoK; // is End of Sequence under Kleene Closure

  public ARTGrammarInstance(int key, ARTGrammarElement payload) {
    super(key, payload);
    // System.out.printf("** new ARTInstance number %d %s:%s\n", key, this.getClass(), payload);
  }

  @Override
  public int compareTo(ARTGrammarInstance right) {
    if ((Integer) key == (Integer) (right.key)) return 0;
    if ((Integer) key < (Integer) (right.key))
      return -1;
    else
      return 1;
  }

  @Override
  public ARTGrammarElement getPayload() {
    return (ARTGrammarElement) payload;
  }

  @Override
  public ARTGrammarInstance getChild() {
    return (ARTGrammarInstance) child;
  }

  @Override
  public ARTGrammarInstance getSibling() {
    return (ARTGrammarInstance) sibling;
  }

  @Override
  public ARTGrammarInstance getLeftSibling() {
    return (ARTGrammarInstance) leftSibling;
  }

  public int getPrefixLength() {
    return prefixLength;
  }

  public ARTGrammarInstance addChild(ARTGrammarInstance newChild) {
    return (ARTGrammarInstance) super.addChild(newChild);
  }

  public String toDotString() {
    String indexString = "ARTGrammarInstance";
    String kindFull = getClass().toString();
    String kind = kindFull.substring(kindFull.lastIndexOf(indexString) + indexString.length()).toLowerCase();

    String ret = key + ": " + "[" + instanceNumberWithinProduction + "]" + kind + " " + toEnumString()
        + (payload == null ? "" : (this instanceof ARTGrammarInstanceActionValue ? "" : " " + payload.toString())) + fold
        + (gatherName == null ? "" : "!" + gatherName) + (instanceName == null ? "" : ":" + instanceName) + "\n" + (isLHS ? "isLHS " : "")
        + (isDelayed ? "isDelayed " : "") + (isSPPFLabel ? "isSPPFLabel " : "") + (isCodeLabel ? "isCodeLabel " : "")
        + (isTestRepeatLabel ? "isTestRepeatLabel " : "") + (isAltLabel ? "isAltLabel " : "") + (isReferredLabel ? "isReferredLabel " : "")
        + (isClosureLabel ? "isClosureLabel " : "") + (isGiftLabel ? "isGiftLabel " : "") + (isSlotSelector ? "isSlotSelector " : "")
        + (isSlotParentLabel ? "isSlotParentLabel " : "") + (isPopD ? "isPopD " : "") + (isPostPredictivePop ? "isPostPredictivePop " : "")
        + (isPredictivePop ? "isPredictivePop " : "") + (isEoOP ? "isEoOP " : "") + (isEoA ? "isEoA " : "") + (isEoR ? "isEoR " : "") + (isFiR ? "isFiR " : "")

        + (isRELabel ? "isRELabel " : "") + (isFfCE ? "isFiPC " : "")

        + (isNullableBracket ? "isNullableBracket " : "") + (isEoD ? "isEoD " : "") + (isEoO ? "isEoO " : "") + (isEoP ? "isEoP " : "")
        + (isEoK ? "isEoK " : "") + toRefEnumString(tearLink, "tearLink") + toRefEnumString(getLhsL(), "lhsL")
        + toRefEnumString(getProductionL(), "productionL") + "\nprefixLength: " + prefixLength + toRefEnumString(niL, "niL") + toRefEnumString(aL, "aL")
        + toRefEnumString(nL, "nL") + toRefEnumString(pL, "pL") + toRefEnumString(lrL, "lrL") + toRefEnumString(erL, "erL") + toRefEnumString(ssL, "ssL");

    if (!first.isEmpty()) ret += "\nfirst: " + first;
    if (!follow.isEmpty()) ret += "\nfollow: " + follow;
    if (!getGuard().isEmpty()) ret += "\nguard: " + getGuard();

    ret += "\nleftSibling: " + (leftSibling == null ? "null" : leftSibling.getKey());
    ret += "\nrightSibling: " + (sibling == null ? "null" : sibling.getKey());
    return ret;
  }

  // For bracket instances such as (), ()*, ()+ and ()* we overload method to return this
  public ARTGrammarInstance bracketInstance(ARTGrammarInstance oldBracketInstance) {
    return oldBracketInstance;
  }

  @Override
  public void printDot(ARTAbstractGraph graph, PrintWriter printWriter) {
    printWriter.print("\n\"" + key + "\"  [label=\"" + toDotString() + "\"]");
    // Now visit the children
    for (ARTGrammarInstance target = (ARTGrammarInstance) child; target != null; target = (ARTGrammarInstance) target.sibling) {
      printWriter.print("\n\"" + key + "\"->\"" + target.getKey() + "\"");
      target.printDot(graph, printWriter);
    }
  }

  public void printDump(PrintWriter printWriter, int level) {
    if (level != 0) {
      printWriter.print("(" + level + "\n");
      printWriter.print(key + " (" + instanceNumberWithinProduction + "): " + toDotString() + "\n");
    }
    // Now visit the children
    for (ARTGrammarInstance target = (ARTGrammarInstance) child; target != null; target = (ARTGrammarInstance) target.sibling)
      target.printDump(printWriter, level + 1);
    if (level != 0) printWriter.print(level + ")" + "\n");
  }

  public String toEnumString() {
    if (getLhsL() == null) return "";

    return ARTText.toIdentifier(((ARTGrammarElementNonterminal) getLhsL().getPayload()).getModule().getId() + "_"
        + ((ARTGrammarElementNonterminal) getLhsL().getPayload()).getId() + (isLHS ? "" : "_" + getKey()));
  }

  public String toEnumString(String prefix) {
    return ARTText.toIdentifier("ART" + prefix + "_" + toEnumString());
  }

  public String toRefEnumString(ARTGrammarInstance label, String labelName) {
    if (label == null || label == this)
      return "";
    else
      return "\n" + labelName + ": " + label.toEnumString();
  }

  String optionalSpace() {
    return this.getSibling() == null ? "" : " ";
  }

  boolean abortToGrammarSlotStringRec = false;

  public String toGrammarSlotStringRec(ARTGrammarInstance slotNode, String slotDesignator, String s, boolean stopAfterSlot, boolean terminalsAsParaterminals,
      String injectInstanceString, String injectProductionString, Set<ARTGrammarElementNonterminal> paraterminals, boolean paraterminalsAsTokens) {
    // System.out.println("toGrammarSlotStringRec(" + this + ")");
    // Preorder actions
    if (this instanceof ARTGrammarInstanceDiff) s += "/" + optionalSpace();

    if (this instanceof ARTGrammarInstanceTerminal) if (!paraterminalsAsTokens && terminalsAsParaterminals) {
      s += getPayload().toParaterminalString();
      s += " " + injectInstanceString;
      s += optionalSpace();
    } else {
      s += getPayload().toString();
      s += " " + injectInstanceString;
      s += optionalSpace();
    }

    if (this instanceof ARTGrammarInstanceNonterminal) {
      if (paraterminalsAsTokens && paraterminals.contains(this.payload))
        s += "'" + getPayload().toString() + "'";
      else
        s += getPayload().toString();
      if (terminalsAsParaterminals && paraterminals.contains(this.payload)) s += " " + injectInstanceString;
      s += optionalSpace();
    }

    if (this instanceof ARTGrammarInstanceEpsilon) s += "#" + optionalSpace();
    if (this instanceof ARTGrammarInstanceDoFirst) s += "(" + optionalSpace();
    if (this instanceof ARTGrammarInstanceOptional) s += "(" + optionalSpace();
    if (this instanceof ARTGrammarInstancePositiveClosure) s += "(" + optionalSpace();
    if (this instanceof ARTGrammarInstanceKleeneClosure) s += "(" + optionalSpace();

    if (this instanceof ARTGrammarInstanceSlot) if (this == slotNode) {
      s += slotDesignator + "" + optionalSpace();
      if (stopAfterSlot) {
        abortToGrammarSlotStringRec = true;
        return s;
      }
    }

    if (this instanceof ARTGrammarInstanceAnnotation) s += "{" + optionalSpace();
    if (this instanceof ARTGrammarInstanceInsertion) s += "[" + optionalSpace();
    if (this instanceof ARTGrammarInstanceTear) s += "$" + optionalSpace();

    if (abortToGrammarSlotStringRec) return s;

    // Inorder actions
    for (ARTGrammarInstance child = getChild(); child != null; child = child.getSibling()) {
      s = child.toGrammarSlotStringRec(slotNode, slotDesignator, s, stopAfterSlot, terminalsAsParaterminals, injectInstanceString, injectProductionString,
          paraterminals, paraterminalsAsTokens);

      if (injectProductionString != null && this instanceof ARTGrammarInstanceLHS) s += " " + injectProductionString;

      if (child.getSibling() != null) {
        if (this instanceof ARTGrammarInstanceDiff) s += "/" + optionalSpace();
        if (this instanceof ARTGrammarInstanceIter) s += "@" + optionalSpace();
        if (this instanceof ARTGrammarInstanceAlt) s += "| " + optionalSpace();
        if (this instanceof ARTGrammarInstanceLHS) s += "\n    | " + optionalSpace();
      }
    }

    // Postorder actions
    if (this instanceof ARTGrammarInstanceDoFirst) s += ")" + optionalSpace();
    if (this instanceof ARTGrammarInstanceOptional) s += ")?" + optionalSpace();
    if (this instanceof ARTGrammarInstancePositiveClosure) s += ")+" + optionalSpace();
    if (this instanceof ARTGrammarInstanceKleeneClosure) s += ")*" + optionalSpace();
    if (this instanceof ARTGrammarInstanceAnnotation) s += "}" + optionalSpace();
    if (this instanceof ARTGrammarInstanceInsertion) s += "]" + optionalSpace();

    return s;
  }

  private boolean containsSlotRec(ARTGrammarInstance slotNode) {
    boolean retValue = false;

    if (this == slotNode) return true;

    for (ARTGrammarInstance child = getChild(); child != null; child = child.getSibling())
      retValue |= child.containsSlotRec(slotNode);

    return retValue;
  }

  public String toGrammarString() {
    return toGrammarString("");
  }

  public String toGrammarString(String slotDesignator) {
    if (this instanceof ARTGrammarInstanceLHS) return payload.toString();

    abortToGrammarSlotStringRec = false;
    ARTGrammarInstance production = getProductionL();
    ARTGrammarInstanceLHS lhs = getLhsL();
    String lhsString = "???";
    if (lhs != null && lhs.getPayload() != null) lhsString = lhs.getPayload().toString();
    if (production != null)
      return production.toGrammarSlotStringRec(this, slotDesignator, lhsString + " ::= ", false, false, "", "", null, false);
    else
      return "(" + key + ", " + payload + ")";
  }

  public String toGrammarString(String slotDesignator, String instanceString, Set<ARTGrammarElementNonterminal> paraterminals) {
    if (this instanceof ARTGrammarInstanceLHS) return payload.toString();

    abortToGrammarSlotStringRec = false;
    ARTGrammarInstance production = getProductionL();
    ARTGrammarInstanceLHS lhs = getLhsL();
    String lhsString = "???";
    if (lhs != null && lhs.getPayload() != null) lhsString = lhs.getPayload().toString();
    if (production != null)
      return production.toGrammarSlotStringRec(this, slotDesignator, lhsString + " ::= ", false, paraterminals != null, instanceString, "", paraterminals,
          false);
    else
      return "(" + key + ", " + payload + ")";
  }

  public String instanceString() {
    return instanceName + instanceNumberWithinProduction;
  }

  public String nameOrInstanceString() {
    if (instanceName != null)
      return instanceName;
    else
      return instanceString();
  }

  public String toGrammarPrefixString() { // This only works for BNF
    String ret = "";
    for (ARTGrammarInstance i = getProductionL().getChild(); i != this; i = i.getSibling())
      if (!(i instanceof ARTGrammarInstanceSlot || i instanceof ARTGrammarInstanceEpsilon)) ret += i.payload + " ";

    return ret;
  }

  public ARTGrammarInstanceCat getProductionL() {
    return productionL;
  }

  public ARTGrammarInstanceLHS getLhsL() {
    return lhsL;
  }

  public String toSymbolString() {
    if (this instanceof ARTGrammarInstanceEpsilon) return "#";
    if (payload != null) return payload.toString();
    return "???";
  }

  public Set<ARTGrammarElement> getGuard() {
    return guard;
  }
}
