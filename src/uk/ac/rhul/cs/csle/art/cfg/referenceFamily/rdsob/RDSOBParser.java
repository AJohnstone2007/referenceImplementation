package uk.ac.rhul.cs.csle.art.cfg.referenceFamily.rdsob;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.Reference;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.ReferenceParser;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GrammarKind;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GrammarNode;

public class RDSOBParser extends ReferenceParser {

  protected class DerivationNode {
    GrammarNode gn;
    DerivationNode next;

    public DerivationNode(GrammarNode gn, DerivationNode next) {
      super();
      this.gn = gn;
      this.next = next;
    }

    @Override
    public String toString() {
      return gn.toString();
    }
  }

  protected DerivationNode dnRoot, dn;

  @Override
  public int derivationAsTerm() {
    int element = 0;
    for (DerivationNode tmp = dnRoot; tmp != null; tmp = tmp.next)
      System.out.println(element++ + " " + tmp.gn.toStringAsProduction());
    return derivationAsTermRec(dnRoot.next);
  }

  private int derivationAsTermRec(DerivationNode dn) {
    GrammarNode lhs;

    // It would be useful for the grammar to know the LHS of each alternate
    for (GrammarNode s = dn.gn.seq;; s = s.seq)
      if (s.elm.kind == GrammarKind.END) {
        lhs = s.seq;
        break;
      }

    // It would be useful for the grammar to know the lenth of each alternate
    int length = 0;
    for (GrammarNode s = dn.gn.seq; s.elm.kind != GrammarKind.END; s = s.seq)
      length++;
    int[] children = new int[length];

    length = 0;
    for (GrammarNode s = dn.gn.seq; s.elm.kind != GrammarKind.END; s = s.seq)
      switch (s.elm.kind) {
      case B, C, T, TI, EPS:
        children[length++] = grammar.iTerms.findTerm(s.elm.str);
        break;

      case N:
        children[length++] = derivationAsTermRec(dn = dn.next);
        break;

      case ALT, DO, END, EOS, KLN, OPT, POS:
        Reference.fatal("Unexpected grammar node in RDSOB " + s);
        break;
      }
    return grammar.iTerms.findTerm(lhs.elm.str, children);
  }

  @Override
  public void show() {
    System.out.println("Leftmost derivation: ");
    for (DerivationNode tmp = dnRoot; tmp != null; tmp = tmp.next)
      System.out.println(tmp + " " + grammar == null ? "" : grammar.nodesByNumber.get(tmp.gn.num).toStringAsProduction() + "..");
  }

}
