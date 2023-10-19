package uk.ac.rhul.cs.csle.art.cfg.referenceFamily.osbrd;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GKind;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GNode;

//@formatter:off
public class OSBRDE extends OSBRDParser {
 class StackNode extends SNode {
  GNode returnNode; int i_entry; DerivationNode dn_entry; StackNode next;

 public StackNode(GNode returnNode, int i, SNode next, DNode dn) {
  this.returnNode = returnNode;
  this.i_entry = i;
  this.next = (StackNode) next;
  this.dn_entry = (DerivationNode) dn;
 }
}

SNode sn;
GNode gn;

boolean osbrdE() {
 initialise();
 while (true)
 switch (gn.elm.kind) {
 case T:
  if (match(gn)) {i++; gn = gn.seq; }
  else if (backtrack()) return false;
  break;
 case N: call(gn); break;
 case EPS: gn = gn.seq; break;
 case END:
  dn_update(gn.alt.num); gn = retrn();
  if (sn == null) return true;
  break;
 }}

void initialise() {
 gn = grammar.rules.get(grammar.startNonterminal).alt.seq; i = 0;
 dn = null; sn = new StackNode(grammar.endOfStringNode, 0, null, dn);
}

void call(GNode caller) {sn = new StackNode(caller.seq, i, sn, dn); gn = lhs(gn).alt.seq;}

GNode retrn() {GNode tmp = ((StackNode) sn).returnNode; sn = ((StackNode) sn).next; return tmp;}

boolean backtrack() { // return true if no backtrack target found
 while (true) {
  while (gn.elm.kind != GKind.END) gn = gn.seq;
  if (gn.alt.alt == null) {
   gn = retrn();
   if (sn == null) return true;
  }
  else {
   i = ((StackNode) sn).i_entry; dn =((StackNode) sn).dn_entry;
   gn = gn.alt.alt.seq;
   break;
  }}
  return false;
}

@Override
public void parse(){accepted = osbrdE() && input[i]==0;}
}
