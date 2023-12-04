package uk.ac.rhul.cs.csle.art.cfg.referenceFamily.rdsob;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.Reference;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.ReferenceParser;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GrammarKind;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GrammarNode;

public class RDSOBExplicitStack extends ReferenceParser {
  class SNode {
    GrammarNode returnNode;
    int i_entry;
    DerivationSingletonNode dnAtEntry;
    SNode next;

    public SNode(GrammarNode retNode, int i, SNode next, DerivationSingletonNode dn) {
      this.returnNode = retNode;
      this.i_entry = i;
      this.next = next;
      if (dn.next == null) dn.next = new DerivationSingletonNode(gn, null);
      dn = dn.next;
      this.dnAtEntry = dn;
    }
  }

  SNode sn;
  GrammarNode gn;

  boolean rdsobExplicitStack() {
    while (true)
      switch (gn.elm.kind) {
      case B, C, T, TI:
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
        // dn_update(gn.alt);
        gn = retrn();
        if (sn == null) return true;
        break;
      case ALT, DO, EOS, KLN, OPT, POS:
        Reference.fatal("internal error - unexpected grammar node in rdsobExplicitStack");
      }
  }

  void call(GrammarNode caller) {
    sn = new SNode(caller.seq, i, sn, dn);
    gn = lhs(gn).alt.seq;
  }

  GrammarNode retrn() {
    GrammarNode tmp = sn.returnNode;
    sn = sn.next;
    return tmp;
  }

  boolean backtrack() { // return true if no backtrack target found
    while (true) {
      while (gn.elm.kind != GrammarKind.END)
        gn = gn.seq;
      if (gn.alt.alt == null) {
        gn = retrn();
        if (sn == null) return true;
      } else {
        i = sn.i_entry;
        dn = sn.dnAtEntry;
        gn = gn.alt.alt.seq;
        dn.gn = gn;
        break;
      }
    }
    return false;
  }

  @Override
  public void parse() {
    gn = grammar.rules.get(grammar.startNonterminal).alt.seq;
    i = 0;
    dnRoot = dn = new DerivationSingletonNode(grammar.endOfStringNode, null);
    sn = new SNode(grammar.endOfStringNode, 0, null, dn);
    accepted = rdsobExplicitStack() && input[i] == 0;
  }

  @Override
  public int derivationAsTerm() {
    return derivationSingletonAsTerm();
  }
}
