package uk.ac.rhul.cs.csle.art.cfg.referenceFamily.rdsob;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.Reference;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GKind;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GNode;

public class RDSOBExplicitStack extends RDSOBParser {
  class SNode {
    GNode returnNode;
    int i_entry;
    DNode dn_entry;
    SNode next;

    public SNode(GNode returnNode, int i, SNode next, DNode dn) {
      this.returnNode = returnNode;
      this.i_entry = i;
      this.next = next;
      this.dn_entry = dn;
    }
  }

  SNode sn;
  GNode gn;

  boolean rdsobExplicitStack() {
    initialise();
    while (true)
      switch (gn.elm.kind) {
      case T:
        if (match(gn)) {
          i++;
          gn = gn.seq;
        } else if (backtrack()) return false;
        break;
      case N:
        call(gn);
        break;
      case EPS:
        gn = gn.seq;
        break;
      case END:
        dn_update(gn.alt);
        gn = retrn();
        if (sn == null) return true;
        break;
      case ALT, B, C, DO, EOS, KLN, OPT, POS, TI:
        Reference.fatal("internal error - unexpected grammar node in rdsobExplicitStack");
      }
  }

  void initialise() {
    gn = grammar.rules.get(grammar.startNonterminal).alt.seq;
    i = 0;
    dnRoot = dn = new DNode(grammar.endOfStringNode, null);
    sn = new SNode(grammar.endOfStringNode, 0, null, dn);
  }

  void call(GNode caller) {
    sn = new SNode(caller.seq, i, sn, dn);
    gn = lhs(gn).alt.seq;
  }

  GNode retrn() {
    GNode tmp = sn.returnNode;
    sn = sn.next;
    return tmp;
  }

  boolean backtrack() { // return true if no backtrack target found
    while (true) {
      while (gn.elm.kind != GKind.END)
        gn = gn.seq;
      if (gn.alt.alt == null) {
        gn = retrn();
        if (sn == null) return true;
      } else {
        i = sn.i_entry;
        dn = sn.dn_entry;
        gn = gn.alt.alt.seq;
        break;
      }
    }
    return false;
  }

  @Override
  public void parse() {
    accepted = rdsobExplicitStack() && input[i] == 0;
  }
}
