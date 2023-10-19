package uk.ac.rhul.cs.csle.art.cfg.referenceFamily.osbrd;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.ReferenceParser;

public class OSBRDParser extends ReferenceParser {

  protected class DerivationNode extends DNode {
    int altn;
    DerivationNode next;

    DerivationNode(int altn, DerivationNode next) {
      this.altn = altn;
      this.next = next;
    }

    @Override
    public String toString() {
      return altn + (next == null ? "" : " " + next.toString());
    }
  }

  protected DNode dn;

  protected void dn_update(int altn) {
    dn = new DerivationNode(altn, (DerivationNode) dn);
  }

  @Override
  public void visualise() {
    System.out.println("Left most derivation");
    visualiseDerivationRec((DerivationNode) dn);
  }

  private void visualiseDerivationRec(DerivationNode dn) {
    if (dn == null) return;
    System.out.println(dn.altn + " " + grammar == null ? "" : grammar.nodesByNumber.get(dn.altn).toStringAsProduction());
    visualiseDerivationRec(dn.next);
  }
}
