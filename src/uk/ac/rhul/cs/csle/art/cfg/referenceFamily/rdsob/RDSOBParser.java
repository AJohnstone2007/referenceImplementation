package uk.ac.rhul.cs.csle.art.cfg.referenceFamily.rdsob;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.ReferenceParser;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GNode;

public class RDSOBParser extends ReferenceParser {

  protected class DNode {
    GNode gn;
    DNode next;

    public DNode(GNode gn, DNode next) {
      super();
      this.gn = gn;
      this.next = next;
    }

    @Override
    public String toString() {
      return gn.num + (next == null ? "" : " " + next.toString());
    }
  }

  protected DNode dnRoot, dn;

  protected void dn_update(GNode gn) {
    dn.next = new DNode(gn, dn);
    dn = dn.next;
  }

  @Override
  public void visualise() {
    System.out.println("Left most derivation");
    for (DNode tmp = dnRoot; tmp != null; tmp = tmp.next)
      System.out.println(dn + " " + grammar == null ? "" : grammar.nodesByNumber.get(dn.gn.num).toStringAsProduction());
  }
}
