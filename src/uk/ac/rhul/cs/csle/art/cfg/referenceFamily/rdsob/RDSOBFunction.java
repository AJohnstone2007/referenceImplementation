package uk.ac.rhul.cs.csle.art.cfg.referenceFamily.rdsob;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.Reference;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GrammarNode;

public class RDSOBFunction extends RDSOBParser {

  private int level;

  public int traceLevel = 0;

  void trace(String msg) {
    System.out.print("[" + level + "@" + i + "]");

    for (int i = 0; i < level; i++)
      System.out.print("  ");
    System.out.println(msg);
  }

  boolean rdsobFunction(GrammarNode lhs) {
    if (dn.next == null) dn.next = new DerivationNode(grammar.endOfStringNode, null);
    DerivationNode dnAtEntry = dn = dn.next;

    level++;

    if (traceLevel > 0) trace("LHS GNode " + lhs);
    int i_entry = i;
    altLoop: for (GrammarNode tmp = lhs.alt; tmp != null; tmp = tmp.alt) {
      i = i_entry;
      dn = dnAtEntry;
      dn.gn = tmp;
      GrammarNode gn = tmp.seq;
      if (traceLevel > 0) trace("Alternate " + tmp.toStringAsProduction());
      while (true) {
        switch (gn.elm.kind) {
        case B:
          if (traceLevel > 0) trace("Testing builtin " + gn + " against input index " + i + " contents " + input[i]);
        case C, T, TI:
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
          if (traceLevel > 0) trace("Accepted " + tmp.toStringAsProduction());
          level--;
          return true;
        case ALT, DO, EOS, KLN, OPT, POS:
          Reference.fatal("internal error - unexpected grammar node in rdsobFunction: " + gn);
        }
      }
    }
    if (traceLevel > 0) trace("Failed on LHS " + lhs);
    level--;
    return false;
  }

  @Override
  public void parse() {
    level = 0;
    i = 0;
    dnRoot = dn = new DerivationNode(grammar.endOfStringNode, null);
    accepted = rdsobFunction(grammar.rules.get(grammar.startNonterminal)) && input[i] == 0;
    if (!accepted) Reference.echo("Syntax error at location " + i, Reference.lineNumber(i, inputString), inputString);
  }
}
