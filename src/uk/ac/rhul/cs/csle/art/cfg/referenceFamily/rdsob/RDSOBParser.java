package uk.ac.rhul.cs.csle.art.cfg.referenceFamily.rdsob;

import java.util.LinkedList;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.Reference;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.ReferenceParser;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GrammarElement;
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
    LinkedList<Integer> tmp = new LinkedList<>();
    derivationAsTermRec(false, grammar.startNonterminal, dnRoot.next, tmp);
    return tmp.getFirst();
  }

  private void derivationAsTermRec(boolean promoted, GrammarElement lhsNonterminal, DerivationNode dn, LinkedList<Integer> childrenFromParent) {
    LinkedList<Integer> children = promoted ? childrenFromParent : new LinkedList<>(); // If we are not promoting, then make new children list

    for (GrammarNode s = dn.gn.seq;; s = s.seq)
      switch (s.elm.kind) {
      case B, C, T, TI, EPS:
        children.add(grammar.iTerms.findTerm(s.elm.str));
        break;

      case N:
        switch (s.giftKind) {
        case OVER: // overwrite parent node label; note flowthrough to next case
          lhsNonterminal = s.elm;
        case UNDER: // add children onto our children idnicated by null lhs
          derivationAsTermRec(true, null, dn = dn.next, children);
          break;
        default: // no promotion operators so make a complete new term and add to our children
          derivationAsTermRec(false, s.elm, dn = dn.next, children);
        }
        break;

      case END:
        if (!promoted) childrenFromParent.add(grammar.iTerms.findTerm(lhsNonterminal.str, children));
        return;

      case ALT, DO, EOS, KLN, OPT, POS:
        Reference.fatal("Unexpected grammar node in RDSOB derivation builder " + s);
        break;
      }
  }

  @Override
  public void show() {
    System.out.println("Leftmost derivation: ");
    for (DerivationNode tmp = dnRoot; tmp != null; tmp = tmp.next)
      System.out.println(tmp + " " + grammar == null ? "" : grammar.nodesByNumber.get(tmp.gn.num).toStringAsProduction() + "..");
  }
}
