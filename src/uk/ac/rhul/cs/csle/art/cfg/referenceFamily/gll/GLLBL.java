package uk.ac.rhul.cs.csle.art.cfg.referenceFamily.gll;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.Reference;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.ReferenceParser;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GIFTKind;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GKind;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GNode;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.Grammar;

public class GLLBL extends ReferenceParser {
  @Override
  public void visualise() {
    new GSS2Dot(gss, "gssA.dot");
    new SPPF2Dot(sppf, sppfRootNode, "sppf_full.dot", SPPF2DotKind.FULL, true, true);
    new SPPF2Dot(sppf, sppfRootNode, "sppf_core.dot", SPPF2DotKind.CORE, true, true);
    if (sppfRootNode == null) return; // Can only visualise derivations of there are some
    sppfSelectFirst(sppfRootNode);
    new SPPF2Dot(sppf, sppfRootNode, "sppf_derivation_first.dot", SPPF2DotKind.DERIVATION, false, false);
    new SPPF2Dot(sppf, sppfRootNode, "sppf_derivation_annotated_first.dot", SPPF2DotKind.DERIVATION, true, false);
    new SPPF2Dot(sppf, sppfRootNode, "sppf_derivation_annotated_binarised_first.dot", SPPF2DotKind.DERIVATION, true, true);
    sppfSelectLast(sppfRootNode);
    new SPPF2Dot(sppf, sppfRootNode, "sppf_derivation_last.dot", SPPF2DotKind.DERIVATION, false, false);
    new SPPF2Dot(sppf, sppfRootNode, "sppf_derivation_annotated_last.dot", SPPF2DotKind.DERIVATION, true, false);
    new SPPF2Dot(sppf, sppfRootNode, "sppf_derivation_annotated_binarised_last.dot", SPPF2DotKind.DERIVATION, true, true);
  }

  @Override
  protected String subStatistics() {
    int descriptorCount = descS.size(), gssNodeCount = gss.keySet().size(), gssEdgeCount = 0, popCount = 0, sppfNodeCount = sppf.keySet().size(),
        sppfPCount = 0, sppfEdgeCount = 0, sppfAmbiguityCount = 0;
    for (GSSN g : gss.keySet()) {
      gssEdgeCount += g.edges.size();
      popCount += g.pops.size();
    }
    for (SPPFN s : sppf.keySet()) {
      sppfPCount += s.packNS.size();
      if (s.packNS.size() > 1) sppfAmbiguityCount++;
      for (SPPFPN p : s.packNS) {
        sppfEdgeCount++; // inedge
        if (p.leftChild != null) sppfEdgeCount++;
        if (p.rightChild != null) sppfEdgeCount++;
        // System.out.println("SPPF node " + s.gn.toStringAsProduction() + ", " + s.li + ", " + s.ri + " pack node " + p.gn.toStringAsProduction() + ", " +
        // p.pivot
        // + ": " + "[" + (p.leftChild == null ? "null SPPF node" : (p.leftChild.gn.toStringAsProduction()) + ", " + p.leftChild.li + ", " + p.leftChild.ri)
        // + "] [" + p.rightChild.gn.toStringAsProduction() + ", " + p.rightChild.li + ", " + p.rightChild.ri + "]");
      }
    }

    return descriptorCount + "," + gssNodeCount + "," + gssEdgeCount + "," + popCount + "," + sppfNodeCount + "," + sppfPCount + "," + sppfEdgeCount + ","
        + sppfAmbiguityCount + "," + (endMemory - startMemory) + "," + String.format("%.2f", (double) (endMemory - startMemory) / input.length);
  }

  private void sppfClearSelected() {
    for (SPPFN s : sppf.keySet())
      for (SPPFPN p : s.packNS)
        p.selected = false;
  }

  private void sppfClearSuppressed() {
    for (SPPFN s : sppf.keySet())
      for (SPPFPN p : s.packNS)
        p.suppressed = false;
  }

  private void sppfSelectFirst(SPPFN sppfRootNode) {
    sppfClearSelected();
    sppfRootNode.selectFirstRec();
  }

  private void sppfSelectLast(SPPFN sppfRootNode) {
    sppfClearSelected();
    sppfRootNode.selectLastRec();
  }

  private GNode rules(GNode gn) {
    return grammar.rules.get(gn.elm);
  }

  private boolean accepting(GNode gn) {
    return grammar.acceptingNodeNumbers.contains(gn.num);
  }

  private void gllBLT() {
    initialise();
    nextDescriptor: while (dequeueDesc()) {
      trace(1, "\n****** Processing descriptor (" + gn.toStringAsProduction() + ", " + i + ", " + sn + ", " + dn + ")");
      while (true) {
        switch (gn.elm.kind) {
        case EPS:
          du(0);
          gn = gn.seq;
          trace(1, "Index " + i + " Node " + gn.num + " Epsilon - continue thread");
          break;
        case C, B, T:
          if (match(gn)) {
            trace(1, "Index " + i + " Node " + gn.num + " " + gn + " Terminal match - continue thread");
            du(1);
            i++;
            gn = gn.seq;
            break;
          } else {
            trace(1, "Index " + i + " Node " + gn.num + " " + gn + " Terminal mismatch - abort thread");
            continue nextDescriptor;
          }
        case N:
          call(gn);
          trace(1, "Index " + i + " Node " + gn.num + " Nonterminal call - end of  thread");
          continue nextDescriptor;
        case END:
          if (Grammar.isLHS(gn.seq)) {
            ret();
            trace(1, "Index " + i + " Node " + gn.num + " End of production - end of thread");
            continue nextDescriptor;
          }
          gn = gn.seq.seq;
          trace(1, "Index " + i + " Node " + gn.num + " End of alternate under bracket - continue thread with successor of bracket");
          break;
        case ALT:
          for (GNode tmp = gn; tmp != null; tmp = tmp.alt)
            queueDesc(tmp.seq, i, sn, dn);
          trace(1, "Index " + i + " Node " + gn.num + " Alternates queued - end of thread");
          continue nextDescriptor;
        case DO:
          gn = gn.alt;
          trace(1, "Index " + i + " Node " + gn.num + " Do first - continue thread with alternates under bracket");
          break;

        case OPT:
        case POS:
        case KLN:
          inadmissable = true;
          System.out.println("Inadmissable: unsupported EBNF element with kind " + gn.elm.kind);
          return;
        case EOS:
          inadmissable = true;
          System.out.println("Inadmissable: production contains invalid element with kind " + gn.elm.kind);
          return;
        }
      }
    }
  }

  @Override
  public void parse() {
    gllBL();
    endTime = readClock();
    endMemory = memoryUsed();
    if (!accepted && !suppressEcho) parserError("GLLBL ");
  }

  private void parserError(String msg) {
    System.out.print(Reference.echo(msg + "syntax error", positions[sppfWidestIndex()], inputString));
  }

  // Paper material below this line

//@formatter:off

/* Thread handling *********************************************************/
Set<Desc> descS;
Deque<Desc> descQ;
GNode gn;
GSSN sn;
SPPFN dn;

void queueDesc(GNode gn, int i, GSSN gssN, SPPFN sppfN) {
  Desc tmp = new Desc(gn, i, gssN, sppfN);
  if (descS.add(tmp)) descQ.addFirst(tmp);
 }

boolean dequeueDesc() {
 Desc tmp = descQ.poll();
 if (tmp == null) return false;
 gn = tmp.gn; i = tmp.i; sn = tmp.sn; dn = tmp.dn;
 return true;
}

/* Stack handling **********************************************************/
Map<GSSN, GSSN> gss;
GSSN gssRoot;

GSSN gssFind(GNode gn, int i) {
 GSSN gssN = new GSSN(gn, i);
 if (gss.get(gssN) == null) gss.put(gssN, gssN);
 return gss.get(gssN);
}

void call(GNode gn) {
 GSSN gssN = gssFind(gn.seq, i);
 GSSE gssE = new GSSE(sn, dn);
 if (!gssN.edges.contains(gssE)) {
  gssN.edges.add(gssE);
  for (SPPFN rc : gssN.pops)
   queueDesc(gn.seq, rc.ri, sn, sppfUpdate(gn.seq, dn, rc));
 }
 for (GNode p = rules(gn).alt; p != null; p = p.alt)
  queueDesc(p.seq, i, gssN, null);
}

void ret() {
 if (sn.equals(gssRoot)) { // Stack base
  if (accepting(gn) && (i == input.length - 1)) {
    sppfRootNode = sppf.get(new SPPFN(grammar.rules.get(grammar.startNonterminal), 0, input.length - 1));
    accepted = true;
  } else {
    rightmostParseIndex = sppfWidestIndex();
  }
  return; // End of parse
 }
 sn.pops.add(dn);
 for (GSSE e : sn.edges)
  queueDesc(sn.gn, i, e.dst, sppfUpdate(sn.gn, e.sppfnode, dn));
}

/* Derivation handling *****************************************************/
Map<SPPFN, SPPFN> sppf;
SPPFN sppfRootNode;

SPPFN sppfFind(GNode dn, int li, int ri) {
 SPPFN tmp = new SPPFN(dn, li, ri);
 if (!sppf.containsKey(tmp)) sppf.put(tmp, tmp);
 return sppf.get(tmp);
}

SPPFN sppfUpdate(GNode gn, SPPFN ln, SPPFN rn) {
 SPPFN ret = sppfFind(gn.elm.kind == GKind.END ? gn.seq : gn,
                 ln == null ? rn.li : ln.li,
                 rn.ri);
 ret.packNS.add(
  new SPPFPN(gn, ln == null ? rn.li : ln.ri, ln, rn));
 return ret;
}

void du(int width) {
  dn = sppfUpdate(gn.seq, dn, sppfFind(gn, i, i + width));
}

private int sppfWidestIndex() {
  int ret = 0;
  for (SPPFN s:sppf.keySet()) if (ret<s.ri) ret = s.ri;
  return ret;
}

/* Parser ******************************************************************/
void initialise() {
 descS = new HashSet<>(); descQ = new LinkedList<>();
 sppf = new HashMap<>(); gss = new HashMap<>();
 gssRoot = new GSSN(grammar.endOfStringNode, 0);
 gss.put(gssRoot, gssRoot);
 i = 0; sn = gssRoot; dn = null;
 for (GNode p = grammar.rules.get(grammar.startNonterminal).alt; p != null; p = p.alt)
  queueDesc(p.seq, i, sn, dn);
 startMemory = memoryUsed();
 startTime = readClock();
 accepted = false;
}


void gllBL() {
 initialise();
 nextDescriptor: while (dequeueDesc())
 while (true) {
  switch (gn.elm.kind) {
   case B,T,TI,C: if (input[i] == gn.elm.ei)
           {du(1); i++; gn = gn.seq; break;}
           else continue nextDescriptor;
   case N: call(gn); continue nextDescriptor;
   case EPS: du(0); gn = gn.seq; break;
   case END: ret(); continue nextDescriptor;


   case DO: gn = gn.alt; break;
   case ALT:
     for (GNode tmp = gn; tmp != null; tmp = tmp.alt)
       queueDesc(tmp.seq, i, sn, dn);
     continue nextDescriptor;
}}}

/* Baseline parser term generation *********************************************************/
HashSet<SPPFN> visited;

private SPPFPN firstSelectedPackNode(SPPFN sppfn) {
  SPPFPN candidate = null;
  for (SPPFPN p : sppfn.packNS)
    // if (p.selected)
    if (candidate == null)
      candidate = p;
    else
      System.out.println("Multiple selected pack nodes at SPPF node " + sppfn);
  if (candidate == null) System.out.println("No selected pack nodes found at SPPF node " + sppfn);
  return candidate;
}

private boolean isSymbol(SPPFN sppfn) {
  return sppfn.packNS.size() == 0 /* terminal or epsilon */ || (sppfn.gn.elm.kind == GKind.N && sppfn.gn.seq == null /* LHS */);
}

private int[] intListToArray(LinkedList<Integer> children) {
  int i = 0, ret[] = new int[children.size()];
  for (int c : children)
    ret[i++] = c;
  return ret;
}

private String constructorOf(SPPFN sppfn, GNode gn) {
  if (gn.elm.kind == GKind.B) switch (gn.elm.str) {
  case "ID": {
    int right = positions[sppfn.li];
    while (right< inputString.length() &&( Character.isAlphabetic(inputString.charAt(right)) || Character.isDigit(inputString.charAt(right))|| inputString.charAt(right)=='_')) right++;

    return inputString.substring(positions[sppfn.li],right) ;
  }
  case "STRING_PLAIN_SQ":{
    int right = positions[sppfn.li]+1;
    while (inputString.charAt(right) != '\'') right++;

    return inputString.substring(positions[sppfn.li],right+1) ;
  }

  }
  return gn.elm.str;
}

private String derivationAsTermRec(SPPFN sppfn, LinkedList<Integer> childrenFromParent, GNode gn) {
//   System.out.println("\nEntering derivationAsTermRec() at node " + sppfn + " instance " + gn);
  if (visited.contains(sppfn)) Reference.fatal("ArtDerivationAsTermRec() found cycle in derivation");
  visited.add(sppfn);

  LinkedList<Integer> children = (gn.giftKind == GIFTKind.OVER || gn.giftKind == GIFTKind.UNDER) ? childrenFromParent : new LinkedList<>();
  String constructor = null;

  if (sppfn.packNS.size() != 0) { // Non leaf symbol nodes
    SPPFPN sppfpn = firstSelectedPackNode(sppfn);
    GNode cgn = sppfpn.gn.alt.seq;
    LinkedList<SPPFN> childSymbolNodes = new LinkedList<>();
    collectChildNodesRec(sppfpn, childSymbolNodes);
    for (SPPFN s : childSymbolNodes) {
      String newConstructor = derivationAsTermRec(s, children, cgn);
      if (newConstructor != null) constructor = newConstructor; // Update on every ^^ child so that the last one wins
      cgn = cgn.seq; // Step to next child grammar node
    }
  }

  if (constructor == null) constructor = constructorOf(sppfn, gn); // If there were no OVERs, then set the constructor to be our symbol
  if (children != childrenFromParent) childrenFromParent.add(grammar.iTerms.findTerm(constructor, intListToArray(children)));

  visited.remove(sppfn);
  return (gn.giftKind == GIFTKind.OVER) ? constructor : null;
}

private void collectChildNodesRec(SPPFPN sppfpn, LinkedList<SPPFN> childNodes) {
  // System.out.println("CollectChildNodesRec() at pack node " + sppfpn);
  SPPFN leftChild = sppfpn.leftChild, rightChild = sppfpn.rightChild;
  if (leftChild != null) {
    if (isSymbol(leftChild)) // found a symbol
      childNodes.add(leftChild);
    else
      collectChildNodesRec(firstSelectedPackNode(leftChild), childNodes);
  }

  if (isSymbol(rightChild)) // found a symbol
    childNodes.add(rightChild);
  else
    collectChildNodesRec(firstSelectedPackNode(rightChild), childNodes);
}

@Override
public int derivationTerm() {
  if (sppfRootNode == null) return 0;
  visited = new HashSet<>();
  LinkedList<Integer> carrier = new LinkedList<>();
  derivationAsTermRec(sppfRootNode, carrier, firstSelectedPackNode(sppfRootNode).gn.seq); // Root packed node must have a grammar node that is the end of a start production
  return carrier.getFirst();
}
}

class Desc {
  public GNode gn;
  public int i;
  public GSSN sn;
  public SPPFN dn;

  public Desc(GNode gn, int index, GSSN sn, SPPFN dn) {
    super();
    this.gn = gn;
    this.i = index;
    this.sn = sn;
    this.dn = dn;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    sb.append(i);
    sb.append(", ");
    sb.append(gn.toStringAsProduction());
    sb.append(", ");
    sb.append(sn);
    sb.append(", ");
    sb.append(dn);
    sb.append(")");
    return sb.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((sn == null) ? 0 : sn.hashCode());
    result = prime * result + i;
    result = prime * result + ((gn == null) ? 0 : gn.hashCode());
    result = prime * result + ((dn == null) ? 0 : dn.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Desc other = (Desc) obj;
    if (sn == null) {
      if (other.sn != null) return false;
    } else if (!sn.equals(other.sn)) return false;
    if (i != other.i) return false;
    if (gn == null) {
      if (other.gn != null) return false;
    } else if (!gn.equals(other.gn)) return false;
    if (dn == null) {
      if (other.dn != null) return false;
    } else if (!dn.equals(other.dn)) return false;
    return true;
  }
}

class GSSN {
  public final GNode gn;
  final int i;
  public final Set<GSSE> edges = new HashSet<>();
  public final Set<SPPFN> pops = new HashSet<>();

  public GSSN(GNode gn, int i) {
    super();
    this.gn = gn;
    this.i = i;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + i;
    result = prime * result + ((gn == null) ? 0 : gn.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    GSSN other = (GSSN) obj;
    if (i != other.i) return false;
    if (gn == null) {
      if (other.gn != null) return false;
    } else if (!gn.equals(other.gn)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "(" + gn.toStringAsProduction() + ", " + i + ")";
  }

}

class GSSE {
  public final GSSN dst;
  public final SPPFN sppfnode;

  public GSSE(GSSN dst, SPPFN sppfNode) {
    this.sppfnode = sppfNode;
    this.dst = dst;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dst == null) ? 0 : dst.hashCode());
    result = prime * result + ((sppfnode == null) ? 0 : sppfnode.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    GSSE other = (GSSE) obj;
    if (dst == null) {
      if (other.dst != null) return false;
    } else if (!dst.equals(other.dst)) return false;
    if (sppfnode == null) {
      if (other.sppfnode != null) return false;
    } else if (!sppfnode.equals(other.sppfnode)) return false;
    return true;
  }
}

class SPPFN {
  public final GNode gn;
  public final int li;
  public final int ri;
  public final Set<SPPFPN> packNS = new HashSet<>();

  public SPPFN(GNode gn, int li, int ri) {
    super();
    this.gn = gn;
    this.li = li;
    this.ri = ri;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((gn == null) ? 0 : gn.hashCode());
    result = prime * result + li;
    result = prime * result + ri;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SPPFN other = (SPPFN) obj;
    if (gn == null) {
      if (other.gn != null) return false;
    } else if (!gn.equals(other.gn)) return false;
    if (li != other.li) return false;
    if (ri != other.ri) return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    if (gn == null)
      sb.append("NULL");
    else
      sb.append(gn.toString());
    sb.append(", ");
    sb.append(li);
    sb.append(", ");
    sb.append(ri);
    sb.append(")");
    return sb.toString();
  }

  public void selectFirstRec() {
    SPPFPN selectedNode = null;
    for (SPPFPN p : packNS)
      if (!p.suppressed) {
        selectedNode = p;
        break;
      }

    if (selectedNode != null) {
      selectedNode.selected = true;
      if (selectedNode.leftChild != null) selectedNode.leftChild.selectFirstRec();
      if (selectedNode.rightChild != null) selectedNode.rightChild.selectFirstRec();
    }
  }

  public void selectLastRec() {
    SPPFPN selectedNode = null;
    for (SPPFPN p : packNS)
      if (!p.suppressed) selectedNode = p;

    if (selectedNode != null) {
      selectedNode.selected = true;
      if (selectedNode.leftChild != null) selectedNode.leftChild.selectLastRec();
      if (selectedNode.rightChild != null) selectedNode.rightChild.selectLastRec();
    }
  }
}

class SPPFPN {
  public final GNode gn;
  public final int pivot;
  public final SPPFN leftChild;
  public final SPPFN rightChild;
  public boolean suppressed = false;
  public boolean selected = false;

  public SPPFPN(GNode gn, int pivot, SPPFN leftChild, SPPFN rightChild) {
    super();
    this.gn = gn;
    this.pivot = pivot;
    this.leftChild = leftChild;
    this.rightChild = rightChild;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((gn == null) ? 0 : gn.hashCode());
    result = prime * result + pivot;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SPPFPN other = (SPPFPN) obj;
    if (gn == null) {
      if (other.gn != null) return false;
    } else if (!gn.equals(other.gn)) return false;
    if (pivot != other.pivot) return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(gn.toStringAsProduction());
    builder.append(", " + pivot);
    builder.append(suppressed ? "(suppressed)" : "");
    builder.append(selected ? "(selected)" : "");
    return builder.toString();
  }

}

enum SPPF2DotKind {
  FULL, CORE, DERIVATION, ALL_DERIVATIONS
}

class SPPF2Dot {
  final String packNodeStyle = "[style=rounded]";
  final String ambiguousStyle = "[color=red]";
  final String selectedStyle = "[color=blue]";
  final String intermediateNodeStyle = "[style=filled fillcolor=grey92]";
  final String symbolNodeStyle = "";
  PrintStream sppfOut = null;
  private final Map<SPPFN, SPPFN> sppf;
  private final boolean showIndicies;
  private final boolean showIntermediates;

  private String renderSPPFPackNode(SPPFN parent, SPPFPN node) {
    return renderSPPFPackNodeLabel(parent, node) + "[label = \"" + node.gn.toStringAsProduction() + " " + node.pivot + "\" ]" + packNodeStyle;
  }

  private String renderSPPFPackNodeLabel(SPPFN parent, SPPFPN node) {
    return "\"" + parent.gn.toStringAsProduction() + "," + parent.li + "," + parent.ri + " " + node.gn.toStringAsProduction() + " " + node.pivot + "\"";
  }

  private String renderSPPFNode(SPPFN node) {
    // if (node == null) return "null";
    return renderSPPFNodeLabel(node) + (node.packNS.size() > 1 ? ambiguousStyle : "") + (isSymbol(node) ? symbolNodeStyle : intermediateNodeStyle);
  }

  private boolean isSymbol(SPPFN node) {
    return node.packNS.size() == 0 || Grammar.isLHS(node.gn);
  }

  private String renderSPPFNodeLabel(SPPFN node) {
    String label;
    if (node == null)
      return "NULL node";
    else {
      if (node.gn == null)
        label = "NULL label";
      else if (node.packNS.size() == 0)
        label = node.gn.toString();
      else if (Grammar.isLHS(node.gn)) {
        label = node.gn.elm.str;
      } else
        label = node.gn.toStringAsProduction();
      return "\"" + label + (showIndicies ? (" " + node.li + "," + node.ri) : "") + "\"";
    }
  }

  public SPPF2Dot(Map<SPPFN, SPPFN> sppf, SPPFN rootNode, String filename, SPPF2DotKind renderKind, boolean showIndicies, boolean showIntermediates) {
    this.sppf = sppf;
    this.showIndicies = showIndicies;
    this.showIntermediates = showIntermediates;
    try {
      sppfOut = new PrintStream(new File(filename));
      sppfOut.println("digraph \"SPPF\" {\n"
          + "graph[ordering=out ranksep=0.1]\n node[fontname=Helvetica fontsize=9 shape=box height=0 width=0 margin=0.04 color=gray]\nedge[arrowsize=0.1 color=gray]");
      if (sppf != null) switch (renderKind) {
      case ALL_DERIVATIONS:
        break;
      case CORE:
        coreSPPFRec(rootNode);
        break;
      case DERIVATION:
        derivationSPPFRec(rootNode, null);
        break;
      case FULL:
        fullSPPF(rootNode);
        break;
      default:
        break;
      }
      sppfOut.println("}");
      sppfOut.close();
    } catch (FileNotFoundException e) {
      System.out.println("Unable to write SPPF visualisation to " + filename);
    }
  }

  private void derivationSPPFRec(SPPFN s, SPPFN parent) {
    if (isSymbol(s) | showIntermediates) {
      sppfOut.println(renderSPPFNode(s));
      if (parent != null) sppfOut.println(renderSPPFNodeLabel(parent) + "->" + renderSPPFNodeLabel(s));
      parent = s;
    }

    SPPFPN selectedNode = null;
    for (SPPFPN p : s.packNS)
      if (p.selected) {
        selectedNode = p;
        break;
      }

    if (selectedNode != null) {
      if (selectedNode.leftChild != null) derivationSPPFRec(selectedNode.leftChild, parent);
      if (selectedNode.rightChild != null) derivationSPPFRec(selectedNode.rightChild, parent);
    }
  }

  private void coreSPPFRec(SPPFN s) {
    if (sppf == null || s == null) return;
    sppfOut.println(renderSPPFNode(s));
    for (SPPFPN p : s.packNS) {
      sppfOut.println(renderSPPFPackNode(s, p));
      sppfOut.println(renderSPPFNodeLabel(s) + "->" + renderSPPFPackNodeLabel(s, p) + (s.packNS.size() > 1 ? ambiguousStyle : ""));

      if (p.leftChild != null) sppfOut.println(renderSPPFPackNodeLabel(s, p) + "->" + renderSPPFNodeLabel(p.leftChild));
      sppfOut.println(renderSPPFPackNodeLabel(s, p) + "->" + renderSPPFNodeLabel(p.rightChild));

      if (p.leftChild != null) coreSPPFRec(p.leftChild);
      if (p.rightChild != null) coreSPPFRec(p.rightChild);
    }
  }

  private void fullSPPF(SPPFN rootnode) {
    if (sppf == null || rootnode == null) return;
    for (SPPFN s : sppf.keySet()) {
      sppfOut.println(renderSPPFNode(s));
      for (SPPFPN p : s.packNS) {
        sppfOut.println(renderSPPFPackNode(s, p));
        sppfOut.println(renderSPPFNodeLabel(s) + "->" + renderSPPFPackNodeLabel(s, p) + (s.packNS.size() > 1 ? ambiguousStyle : ""));
        if (p.leftChild != null) sppfOut.println(renderSPPFPackNodeLabel(s, p) + "->" + renderSPPFNodeLabel(p.leftChild));
        sppfOut.println(renderSPPFPackNodeLabel(s, p) + "->" + renderSPPFNodeLabel(p.rightChild));
      }
    }
  }
}

class GSS2Dot {
  public GSS2Dot(Map<GSSN, GSSN> gss, String filename) {
    PrintStream gssOut;
    try {
      gssOut = new PrintStream(new File(filename));
      gssOut.println("digraph \"GSS\" {\n" + "node[fontname=Helvetica fontsize=9 shape=box height = 0 width = 0 margin= 0.04  color=gray]\n"
          + "graph[ordering=out ranksep=0.1]\n" + "edge[arrowsize = 0.3  color=gray]");

      if (gss != null) for (GSSN s : gss.keySet()) {
        gssOut.println("\"" + s + "\" [label=\"" + s.gn.toStringAsProduction() + "\n" + s.i + "\"]");
        for (GSSE c : s.edges) // iterate over children
          gssOut.println("\"" + s + "\"->\"" + c.dst + "\"");
      }
      gssOut.println("}");
      gssOut.close();
    } catch (FileNotFoundException e) {
      System.out.println("Unable to write GSS visualisation to " + filename);
    }
  }
}
