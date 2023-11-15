package uk.ac.rhul.cs.csle.art.cfg.referenceFamily.rdsob;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.ReferenceParser;
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

  protected void dn_update(GrammarNode gn) {
    dn = new DerivationNode(gn, dn);
  }

  @Override
  public void show() {
    System.out.println("Left most derivation");
    for (DerivationNode tmp = dnRoot; tmp != null; tmp = tmp.next)
      System.out.println(dn + " " + grammar == null ? "" : grammar.nodesByNumber.get(dn.gn.num).toStringAsProduction());
  }

  @Override
  public int derivationAsTerm() {
    derivationAsTermRec(dn);

    int element = 0;
    for (DerivationNode tmp = dnRoot; tmp != null; tmp = tmp.next)
      System.out.println(element++ + " " + tmp.gn.toStringAsProduction());
    return 0;
  }

  private void derivationAsTermRec(DerivationNode dn) {
    GrammarNode gn = dn.gn;

  }

}
