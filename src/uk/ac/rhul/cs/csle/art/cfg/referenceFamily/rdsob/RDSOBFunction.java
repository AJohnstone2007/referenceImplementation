package uk.ac.rhul.cs.csle.art.cfg.referenceFamily.rdsob;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.Reference;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GNode;

public class RDSOBFunction extends RDSOBParser {

  boolean rdsobFunction(GNode lhs) {
    int i_entry = i;
    DNode dn_entry = dn;
    altLoop: for (GNode tmp = lhs.alt; tmp != null; tmp = tmp.alt) {
      i = i_entry;
      dn = dn_entry;
      GNode gn = tmp.seq;
      while (true) {
        switch (gn.elm.kind) {
        case T:
          if (match(gn)) {
            i++;
            gn = gn.seq;
            break;
          } else
            continue altLoop;
        case N:
          if (rdsobFunction(lhs(gn))) {
            gn = gn.seq;
            break;
          } else
            continue altLoop;
        case EPS:
          gn = gn.seq;
          break;
        case END:
          dn_update(tmp);
          return true;
        case ALT, B, C, DO, EOS, KLN, OPT, POS, TI:
          Reference.fatal("internal error - unexpected grammar node in rdsobFunction");
        }
      }
    }
    return false;
  }

  @Override
  public void parse() {
    accepted = rdsobFunction(grammar.rules.get(grammar.startNonterminal)) && input[i] == 0;
  }
}
