package uk.ac.rhul.cs.csle.art.cfg.referenceFamily.rdsob;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.ReferenceParser;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GrammarNode;
import uk.ac.rhul.cs.csle.art.util.Util;

public class RDSOBFunction extends ReferenceParser {

  boolean rdsobFunction(GrammarNode lhs) {
    if (dn.next == null) dn.next = new DerivationSingletonNode(grammar.endOfStringNode, null);
    dn = dn.next;
    DerivationSingletonNode dnAtEntry = dn;

    int i_entry = i;
    altLoop: for (GrammarNode tmp = lhs.alt; tmp != null; tmp = tmp.alt) {
      i = i_entry;
      dn = dnAtEntry;
      dn.gn = tmp;
      GrammarNode gn = tmp.seq;
      while (true) {
        switch (gn.elm.kind) {
        case B, C, T, TI:
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
          return true;
        case ALT, DO, EOS, KLN, OPT, POS:
          Util.fatal("internal error - unexpected grammar node in rdsobFunction: " + gn);
        }
      }
    }
    return false;
  }

  @Override
  public void parse() {
    i = 0;
    dnRoot = dn = new DerivationSingletonNode(grammar.endOfStringNode, null);
    accepted = rdsobFunction(grammar.rules.get(grammar.startNonterminal)) && input[i] == 0;
    if (!accepted) Util.echo("Syntax error at location " + i, Util.lineNumber(i, inputString), inputString);
  }

  @Override
  public int derivationAsTerm() {
    return derivationSingletonAsTerm();
  }
}
