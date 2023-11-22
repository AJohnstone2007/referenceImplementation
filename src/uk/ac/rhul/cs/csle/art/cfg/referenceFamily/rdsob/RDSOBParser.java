package uk.ac.rhul.cs.csle.art.cfg.referenceFamily.rdsob;

import java.util.LinkedList;

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
  private int inputIndex = 0;

  @Override
  public int derivationAsTerm() {
    // int element = 0;
    // for (DerivationNode tmp = dnRoot; tmp != null; tmp = tmp.next)
    // System.out.println(element++ + " " + tmp.gn.toStringAsProduction());
    LinkedList<Integer> tmp = new LinkedList<>();
    inputIndex = 0;
    dn = dnRoot.next;
    derivationAsTermRec(false, grammar.startNonterminal.str, tmp); // Initial call builds term into first element of tmp
    return tmp.getFirst();
  }

  // Some care is required due to phasing around promotion operators
  // We pass in the label for the called nonterminal and our children
  // If this nonterminal is being promoted, nodes are added to childrenFromParent, and any promoted names are passed back
  // If this nonterminal is not being promoted, a new children list is created and a complete new term is added to our children
  private String derivationAsTermRec(boolean promoted, String nonterminalName, LinkedList<Integer> childrenFromParent) {
    // System.out.println("** derivationAsTermRec() with parent label " + parentLabel + " and derivation node " + dn.gn.toStringAsProduction());

    LinkedList<Integer> children = promoted ? childrenFromParent : new LinkedList<>(); // If we are not promoted, then make new children list
    String ret = nonterminalName; // by default, pass back our own name

    for (GrammarNode s = dn.gn.seq;; s = s.seq) {
      // System.out.println("Processing grammar element " + s.toStringAsProduction());
      String label = s.elm.str;
      switch (s.elm.kind) {
      case B: // Note flow through
        label = lexemeForBuiltin(inputIndex);
      case C, T, TI, EPS:
        switch (s.giftKind) {
        case OVER:
          ret = label; // No children to add but promote this label
          break;
        case UNDER:
          break; // no children to add and no promotion either
        default: // no promotion operators so just add us to the children
          children.add(grammar.iTerms.findTerm(label, new int[0])); // This is a slightly unexpected construction: if we just findTerm on the string then
                                                                    // the term parser will be used, and we'd need to escape the metacharacters
        }
        if (s.elm.kind != GrammarKind.EPS) inputIndex++;
        break;

      case N:
        dn = dn.next;
        switch (s.giftKind) {
        case OVER: // overwrite parent node label; note flowthrough to next case
          ret = label;
        case UNDER: // add children onto our children
          ret = derivationAsTermRec(true, ret, children);
          break;
        default: // no promotion operators so make a complete new term and add to our children
          derivationAsTermRec(false, s.elm.str, children);
        }
        break;

      case END:
        if (!promoted) childrenFromParent.add(grammar.iTerms.findTerm(ret, children)); // At end of rule make new term
        return ret;

      case ALT, DO, EOS, KLN, OPT, POS:
        Reference.fatal("Unexpected grammar node in RDSOB derivation builder " + s);
        break;
      }
    }
  }

}
