package uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily.osbrd;

import uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily.DNode;
import uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily.grammar.GNode;

// @formatter:off
public class OSBRDF extends OSBRDParser {

boolean osbrdF(GNode lhs) {
 int i_entry = i; DNode dn_entry = dn;
 altLoop: for (GNode tmp = lhs.alt; tmp != null; tmp = tmp.alt) {
  i = i_entry; dn = dn_entry;
  GNode gn = tmp.seq;
  while (true) {
   switch (gn.elm.kind) {
   case T: if (match(gn)) {i++; ; gn = gn.seq; break;}
           else continue altLoop;
   case N: if (osbrdF(lhs(gn))) {gn = gn.seq; break;}
           else continue altLoop;
   case EPS: gn = gn.seq; break;
   case END: dn_update(tmp.num); return true;
   }}}
 return false;
}

@Override
public void parse(){accepted = osbrdF(grammar.rules.get(grammar.startNonterminal)) && input[i] == 0;}
}
