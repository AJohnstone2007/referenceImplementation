package uk.ac.rhul.cs.csle.art.cfg.grammar;

import java.util.Set;
import java.util.TreeSet;

import uk.ac.rhul.cs.csle.art.util.Util;

public class GrammarNode {
  public static String caseSensitiveTerminalStrop = "'";
  public static String caseInsensitiveTerminalStrop = "\"";
  public static String characterTerminalStrop = "`";
  public static String builtinTerminalStrop = "&";

  static Grammar grammar;
  public int num;
  public final GrammarElement elm; // Grammar element
  public GrammarNode seq; // sequence link
  public GrammarNode alt; // alternate link
  public GIFTKind giftKind = GIFTKind.NONE;
  public int action; // Holds an action term used by attribute evaluators

  public final Set<GrammarElement> instanceFirst = new TreeSet<>();
  public final Set<GrammarElement> instanceFollow = new TreeSet<>();
  public boolean isInitialSlot = false, isPenultimateSlot = false, isFinalSlot = false, isNullableSlot = false;

  /*
   * compute as gn.prev != null && gn.prev.prev == null && gn.seq.kind != gnKind.END && (gn.prev.kind == gn.Kind.TERMINALLC || (gn.prev.kind ==
   * gn.Kind.NONTERMINAL && gn.prev.isNullable))
   */
  public GrammarNode(GrammarKind kind, String str, Grammar grammar) {
    GrammarNode.grammar = grammar;
    grammar.endOfStringElement = elm = GrammarNode.grammar.findElement(kind, "");
  }

  public GrammarNode(GrammarKind kind, String str, int action, GIFTKind fold, GrammarNode previous, GrammarNode parent) {
    super();
    this.elm = GrammarNode.grammar.findElement(kind, str);
    this.action = action;
    this.giftKind = fold;
    if (previous != null) previous.seq = this;
    if (parent != null) parent.alt = this;
    if (kind == GrammarKind.END) isNullableSlot = true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + num;
    result = prime * result + ((elm == null) ? 0 : elm.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    GrammarNode other = (GrammarNode) obj;
    if (num != other.num) return false;
    if (elm == null) {
      if (other.elm != null) return false;
    } else if (!elm.equals(other.elm)) return false;
    return true;
  }

  private String giftToString(GIFTKind kind) {
    switch (kind) {
    case NONE:
      return "";
    case OVER:
      return "^^";
    case TEAR:
      return "^^-";
    case UNDER:
      return "^";
    default:
      return "^???";
    }
  }

  public String toStringDot() {
    String ret = num + " ";
    if (action != 0) ret += /* action + */ " ";
    switch (elm.kind) {
    case EOS, ALT, DO, KLN, OPT, POS:
      return ret + elm.kind;
    case T, C, B, N, EPS:
      return ret + elm.kind + "\n" + elm.str + giftToString(giftKind);
    case END:
      return ret + "END " + "\n(" + seq.num + "," + alt.num + ")";
    default:
      return "???";
    }
  }

  @Override
  public String toString() {
    switch (elm.kind) {
    case EOS:
      return "EOS node";
    case T:
      return caseSensitiveTerminalStrop + elm.str + caseSensitiveTerminalStrop + giftToString(giftKind);
    case C:
      return characterTerminalStrop + elm.str + giftToString(giftKind);
    case B:
      return builtinTerminalStrop + elm.str + giftToString(giftKind);
    case EPS:
      return "#" + giftToString(giftKind);
    case N:
      return elm.str + giftToString(giftKind);
    case ALT:
      return "|";
    case END:
      return "";
    case DO:
      return ")";
    case OPT:
      return ")?";
    case POS:
      return ")+";
    case KLN:
      return ")*";
    default:
      return "???";
    }
  }

  public String toStringAsProduction() { // convenience method for common use case
    return toStringAsProduction(" ::=", " .");
  }

  public String toStringAsProduction(String rewritesDenotation, String slotDenotation) { // Print a node in the context of its production
    // System.out.println("toStringAsProduction called on " + num + ": " + this);
    StringBuilder sb = new StringBuilder();

    if (Grammar.isLHS(this))
      sb.append(elm.str);
    else if (seq.elm.kind == GrammarKind.EOS)
      sb.append(seq);
    else {
      GrammarNode tmp;
      for (tmp = this; !(tmp.elm.kind == GrammarKind.END && Grammar.isLHS(tmp.seq)); tmp = tmp.seq) {// Locate the end of this production
        // System.out.println("toStringAsProduction at " + tmp + " with next-in-sequence element " + tmp.seq.elm);
      }
      sb.append(tmp.seq.elm.str + rewritesDenotation); // Render LHS

      toStringAsSequenceRec(sb, tmp.alt, slotDenotation, this); // Render RHS
    }
    return sb.toString();
  }

  private void toStringAsSequenceRec(StringBuilder sb, GrammarNode alt, String slotDenotation, GrammarNode targetNode) {
    // System.out.println("toStringAsSequenceRec called on " + this.instanceNumber + ":" + this);
    if (alt.elm.kind != GrammarKind.ALT) Util.fatal("toStringAsSequenceRe()c called on node " + alt.num + " which is not not an ALT node");
    for (GrammarNode tmpSeq = alt.seq;; tmpSeq = tmpSeq.seq) { // run down this sequence
      if (tmpSeq == targetNode) sb.append(slotDenotation);
      if (tmpSeq.elm.kind != GrammarKind.END && tmpSeq.alt != null) { // If this element has an alt, then recursively process it first
        sb.append(" (");
        for (GrammarNode tmpAlt = tmpSeq.alt; tmpAlt != null; tmpAlt = tmpAlt.alt) {
          toStringAsSequenceRec(sb, tmpAlt, slotDenotation, targetNode);
          if (tmpAlt.alt != null) sb.append(" |"); // Closing parethesis supplied by next level up
        }
      }
      if (tmpSeq.elm.kind == GrammarKind.END) return;
      sb.append(" " + tmpSeq);
    }
  }

  public String toStringAsRHS(String slotDenotation) {
    StringBuilder sb = new StringBuilder();
    toStringAsSequenceRec(sb, this, slotDenotation, this);
    return sb.toString();
  }
}
