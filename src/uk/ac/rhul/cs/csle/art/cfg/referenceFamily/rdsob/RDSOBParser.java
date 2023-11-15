package uk.ac.rhul.cs.csle.art.cfg.referenceFamily.rdsob;

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

  protected DerivationNode dn;

  protected void dn_update(GrammarNode gn) {
    dn = new DerivationNode(gn, dn);
  }

  @Override
  public void show() {
    System.out.println("Left most derivation");
    for (DerivationNode tmp = dn; tmp != null; tmp = tmp.next)
      System.out.println(dn + " " + grammar == null ? "" : grammar.nodesByNumber.get(dn.gn.num).toStringAsProduction());
  }

  DerivationNode nextDerivationStep;

  @Override
  public int derivationAsTerm() {
    int element = 0;
    for (DerivationNode tmp = dn; tmp != null; tmp = tmp.next)
      System.out.println(element++ + " " + tmp.gn.toStringAsProduction());
    nextDerivationStep = dn;
    return derivationAsTermRec();
  }

  private int derivationAsTermRec() {
    DerivationNode dn = nextDerivationStep;
    nextDerivationStep = nextDerivationStep.next;

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
        children[length++] = grammar.iTerms.findTerm(s.elm.str); // Debug
        break;

      case N:
        children[length++] = derivationAsTermRec();

        break;

      case ALT, DO, END, EOS, KLN, OPT, POS:
        break;

      default:
        break;
      }
    return grammar.iTerms.findTerm(lhs.elm.str, children);
  }
}
