package uk.ac.rhul.cs.csle.art.old.util.bsr;

import java.io.StringWriter;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.core.ARTUncheckedException;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTTextHandlerFile;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElement;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementNonterminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstance;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceCat;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceNonterminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceSlot;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceTerminal;

public class ARTBSRSet {
  private final Set<ARTBSR> set;
  private final ARTGrammar grammar;

  public final Map<ARTBSRMapNode, ARTBSRMapNode> bsrMap = new HashMap<>();

  public ARTBSRSet(ARTGrammar grammar) {
    this.grammar = grammar;
    this.set = new HashSet<>();
  }

  public ARTBSRSet(ARTGrammar grammar, Set<ARTBSR> bsrSet) {
    this.grammar = grammar;
    this.set = bsrSet;
  }

  public ARTGrammar getGrammar() {
    return grammar;
  }

  public Set<ARTBSR> getBSRSet() {
    return set;
  }

  public void add(ARTBSR bsr) {
    set.add(bsr);
  }

  @Override
  public String toString() {
    StringWriter stringWriter = new StringWriter();
    for (ARTBSR element : set)
      stringWriter.append((element.getInstance() instanceof ARTGrammarInstanceCat ? "Production " : "Prefix ") + element.getInstance().toGrammarString(" .")
          + " " + element.getI() + ", " + element.getJ() + ", " + element.getK() + "\n");
    return stringWriter.toString();
  }

  private String toSymbolNodeString(ARTGrammarElement element, int left, int right) {
    return "\"" + element + " " + left + "," + right + "\"";
  }

  private String toIntermediateNodeString(ARTGrammarInstance instance, int left, int right) {
    return "\"[" + instance.toGrammarPrefixString() + "] " + left + "," + right + "\"";
  }

  private String toPackedString(ARTGrammarInstance instance, int left, int pivot, int right) {
    return "\"" + instance.toGrammarString(".") + " " + left + "," + pivot + "," + right + "\"";
  }

  private String toEdgeString(String source, String destination) {
    return source + "->" + destination;
  }

  private ARTGrammarInstanceSlot finalSlot(ARTGrammarInstanceCat production) {
    ARTGrammarInstanceSlot pm = (ARTGrammarInstanceSlot) production.getChild();

    while (true)
      if (pm.getSibling() == null)
        return pm;
      else
        pm = (ARTGrammarInstanceSlot) pm.getSibling().getSibling();
  }

  void updateSPPF(ARTGrammarInstance instance, int i, int k, int j) {
    ARTBSRMapNode key = new ARTBSRMapNode(instance, i, j);

    if (bsrMap.get(key) == null) bsrMap.put(key, key);
    bsrMap.get(key).addPivot(k);
  }

  public void constructFullSPPF() {
    for (ARTBSR u : set)
      updateSPPF(u.getInstance(), u.getI(), u.getK(), u.getJ());
  }

  public void printSPPF() {
    System.out.println("BSR set elements");

    for (ARTBSRMapNode s : bsrMap.keySet())
      System.out.println(
          s.getInstance().getKey() + ": " + s.getInstance().toGrammarString(".") + ", " + s.getLeftExtent() + ", " + s.getRightExtent() + " " + s.getPivots());
  }

  public void sppfToDotFull(String filename) {
    ARTText text = new ARTText(new ARTTextHandlerFile(filename));
    sppfSymbolNodes = new HashSet<>();
    sppfIntermediateNodes = new HashSet<>();
    sppfPackedNodes = new HashSet<>();
    System.out.println("\nWhilst generating sppfFull.dot\n");
    text.println(
        "digraph \"SPPF from BSR set\"{\ngraph[ordering=out]\nnode[fontname=Helvetica fontsize=9 shape=box height=0 width=0 margin=0.04]\nedge[arrowsize=0.3]\n");

    for (ARTBSRMapNode s : bsrMap.keySet()) {
      ARTGrammarInstance l = s.getInstance();
      int i = s.getLeftExtent(), j = s.getRightExtent();

      for (Integer k : s.getPivots()) {
        System.out.println(toPackedString(l, i, k, j) + "[label=\"" + l.getKey() + ": " + k + "\"]" + "--->" + new ARTBSRSPPFPackedNode(l, i, k, j));
        sppfPackedNodes.add(new ARTBSRSPPFPackedNode(l, i, k, j));
        text.println(toPackedString(l, i, k, j) + "[label=\"" + l.getKey() + ": " + k + "\"]"); // Establish label for packed nodes

        if (l instanceof ARTGrammarInstanceCat) {// Print edge to packed node
          System.out.println("S1" + toSymbolNodeString(l.getLhsL().getPayload(), i, j) + "---->" + new ARTBSRSPPFNode(normaliseInstance(l), i, j));
          sppfSymbolNodes.add(new ARTBSRSPPFNode(normaliseInstance(l), i, j));
          text.println(toEdgeString(toSymbolNodeString(l.getLhsL().getPayload(), i, j), toPackedString(l, i, k, j)));
        } else {
          System.out.println("I1" + toIntermediateNodeString(l, i, j) + "---->" + new ARTBSRSPPFNode(grammar.getPrefixSlotMap().get(l), i, j));
          sppfIntermediateNodes.add(new ARTBSRSPPFNode(grammar.getPrefixSlotMap().get(l), i, j));
          text.println(toEdgeString(toIntermediateNodeString(l, i, j), toPackedString(l, i, k, j)));
        }
        ARTGrammarInstanceSlot pS = (l instanceof ARTGrammarInstanceCat ? finalSlot((ARTGrammarInstanceCat) l) : (ARTGrammarInstanceSlot) l);

        // Every packed node will have a right symbol child; note kludge to overcome lack of payload on epsilon nodes. Need to fix that
        System.out
            .println("S3" + s + (toSymbolNodeString(pS.getLeftSibling().getPayload() == null ? grammar.getEpsilon() : pS.getLeftSibling().getPayload(), k, j))
                + "--->" + new ARTBSRSPPFNode(normaliseInstance(pS), k, j));
        sppfSymbolNodes.add(new ARTBSRSPPFNode(normaliseInstance(pS), k, j));
        text.println(toEdgeString(toPackedString(l, i, k, j),
            toSymbolNodeString(pS.getLeftSibling().getPayload() == null ? grammar.getEpsilon() : pS.getLeftSibling().getPayload(), k, j)));

        int prefixLength = pS.getPrefixLength();

        if (prefixLength > 1) {// Is there is a left child
          if (prefixLength == 2) // Special case two children both symbols to avoid wasteful extra intermediate node
          {
            System.out.println("S4" + toSymbolNodeString(pS.getLeftSibling().getLeftSibling().getLeftSibling().getPayload(), i, k) + "--->"
                + new ARTBSRSPPFNode(normaliseInstance(pS.getLeftSibling().getLeftSibling()), i, k));
            sppfSymbolNodes.add(new ARTBSRSPPFNode(normaliseInstance(pS.getLeftSibling().getLeftSibling()), i, k));
            text.println(
                toEdgeString(toPackedString(l, i, k, j), toSymbolNodeString(pS.getLeftSibling().getLeftSibling().getLeftSibling().getPayload(), i, k)));
          } else {
            System.out.println("I4" + toIntermediateNodeString(pS.getLeftSibling(), i, k) + "--->" + new ARTBSRSPPFNode(pS.getLeftSibling(), i, k));
            sppfIntermediateNodes.add(new ARTBSRSPPFNode(pS.getLeftSibling(), i, k));
            text.println(toEdgeString(toPackedString(l, i, k, j), toIntermediateNodeString(pS.getLeftSibling(), i, k)));
          }
        }
      }
    }
    text.print("}\n");
    text.close();

    System.out.println("sppfSymbolNodes = " + sppfSymbolNodes);
    System.out.println("sppIntermediateNodes = " + sppfIntermediateNodes);
    System.out.println("\nFull SPPF node counts: symbol = " + sppfSymbolNodes.size() + ", intermediate = " + sppfIntermediateNodes.size() + ", packed = "
        + sppfPackedNodes.size());
  }

  // lookup an SPPF node by instance, leftExtent, rightExtent and call the main traverser
  private void sppfTraverseSymbolRec(ARTText text, ARTGrammarInstanceSlot slot, Integer leftExtent, int rightExtent) {
    // System.out.println("Entered sppfTraverseSymbolRec (unpacked triple) < " + slot + ", " + leftExtent + ", " + rightExtent + " >");

    if (slot.getLeftSibling() instanceof ARTGrammarInstanceNonterminal)
      sppfTraverseRec(text, (ARTGrammarElementNonterminal) slot.getLeftSibling().getPayload(), leftExtent, rightExtent);
    else {
      sppfSymbolNodes.add(new ARTBSRSPPFNode(normaliseInstance(slot), leftExtent, rightExtent));
      text.println("S " + slot.getKey() + ": \"" + slot.getLeftSibling().getPayload() + " " + leftExtent + "," + rightExtent + "\"");
    }
  }

  // Search the SPPF for productions of n with these extents and call
  // Note that this can be made more efficient by accessing the grammar to get the productions of N, and then doing a get() on that SPPFNode to see if it exists
  private void sppfTraverseRec(ARTText text, ARTGrammarElementNonterminal n, int leftExtent, int rightExtent) {
    // System.out.println("Entered sppfTraverseRec (nonterminal) < " + n + ", " + leftExtent + ", " + rightExtent + " >");

    boolean found = false;
    for (ARTBSRMapNode s : bsrMap.keySet())
      if (s.getInstance() instanceof ARTGrammarInstanceCat && s.getInstance().getLhsL().getPayload() == n && s.getLeftExtent() == leftExtent
          && s.getRightExtent() == rightExtent) {
        found = true;
        sppfTraverseRec(text, s);
      }

    if (!found) throw new ARTUncheckedException("no SPPF nodes found for nonterminal node " + n + " with i = " + leftExtent + " and j = " + rightExtent);
  }

  private void sppfTraverseRec(ARTText text, ARTBSRMapNode node) {
    // System.out.println("Entered SPPFTraverseRec (main) " + node);
    if (node.isVisited()) {
      System.out.println("Already visited; returning");
      return;
    }
    node.setVisited(true);

    ARTGrammarInstance l = node.getInstance();
    int i = node.getLeftExtent(), j = node.getRightExtent();

    // render this node
    if (l instanceof ARTGrammarInstanceCat) {
      text.println("S " + l.getKey() + ": " + toSymbolNodeString(l.getLhsL().getPayload(), i, j));
      sppfSymbolNodes.add(new ARTBSRSPPFNode(normaliseInstance(l), i, j));
    } else if (l instanceof ARTGrammarInstanceSlot) {
      text.println("I " + l.getKey() + ": " + toIntermediateNodeString(l, i, j));
      sppfIntermediateNodes.add(new ARTBSRSPPFNode(l, i, j));
    } else
      throw new ARTUncheckedException("unexpected instance class " + l.getClass() + " is BSR SPPF REC");

    ARTGrammarInstanceSlot pS = (l instanceof ARTGrammarInstanceCat ? finalSlot((ARTGrammarInstanceCat) l) : (ARTGrammarInstanceSlot) l);
    // System.out.println("pS is node " + pS.getKey());
    // Now visit the packed nodes
    for (Integer k : node.getPivots()) {
      sppfPackedNodes.add(new ARTBSRSPPFPackedNode(l, i, j, k));
      text.println("P " + l.getKey() + ": " + toPackedString(l, i, k, j));
      if (pS.getPrefixLength() > 1) if (pS.getPrefixLength() == 2)
        sppfTraverseSymbolRec(text, (ARTGrammarInstanceSlot) pS.getLeftSibling().getLeftSibling(), i, k); // left child
      else
        sppfTraverseRec(text, bsrMap.get(new ARTBSRMapNode(grammar.getPrefixSlotMap().get(pS.getLeftSibling().getLeftSibling()), i, k)));

      sppfTraverseSymbolRec(text, pS, k, j); // right child is one slot back
    }
  }

  Set<ARTBSRSPPFNode> sppfSymbolNodes;
  Set<ARTBSRSPPFNode> sppfIntermediateNodes;
  Set<ARTBSRSPPFPackedNode> sppfPackedNodes;

  public void sppfTraverse(ARTGrammar grammar, String filename, int m) {
    // ARTText text = new ARTText(new ARTTextHandlerFile(filename));
    sppfSymbolNodes = new HashSet<>();
    sppfIntermediateNodes = new HashSet<>();
    sppfPackedNodes = new HashSet<>();

    System.out.println("\nBSR set traversal displaying corresponding SPPF nodes top down, left to right with visited flags");
    ARTText text = new ARTText();

    for (ARTBSRMapNode s : bsrMap.keySet())
      s.setVisited(false); // clear the visited flags

    sppfTraverseRec(text, grammar.getDefaultStartNonterminal(), 0, m);

    // System.out.println("Symbols: " + sppfSymbolNodes);
    // System.out.println("Intermediates: " + sppfIntermediateNodes);
    // System.out.println("Packed: " + sppfPackedNodes);

    System.out.println("\nReachable SPPF node counts: symbol = " + sppfSymbolNodes.size() + ", intermediate = " + sppfIntermediateNodes.size() + ", packed = "
        + sppfPackedNodes.size());

    // Now output terminals
    System.out.println("\nSPPF symbol nodes labelled with terminal\n");

    Set<ARTTerminalWithRightExtent> uniqueTerminals = new HashSet<>();
    BigInteger multipliedCardinalities = BigInteger.ONE;

    for (int i = 0; i < m; i++) {
      int count = 0;
      uniqueTerminals.clear();
      System.out.print(i + ": ");
      for (ARTBSRSPPFNode s : sppfSymbolNodes) {
        if (s.getInstance().getLeftSibling() instanceof ARTGrammarInstanceTerminal && s.leftExtent == i) {
          System.out.print(s.instance.getLeftSibling().getPayload() + " ");
          uniqueTerminals.add(new ARTTerminalWithRightExtent((ARTGrammarElementTerminal) s.instance.getLeftSibling().getPayload(), s.rightExtent));
          count++;
        }
      }
      System.out.println("[" + count + "]-> " + uniqueTerminals.size());
      if (uniqueTerminals.size() > 0) multipliedCardinalities = multipliedCardinalities.multiply(new BigInteger("" + uniqueTerminals.size()));
    }

    System.out.println("Product of non-zero unique terminal cardinalities = " + multipliedCardinalities + " which is approximately "
        + multipliedCardinalities.toString().charAt(0) + " x 10^" + (multipliedCardinalities.toString().length() - 1));
  }

  ARTGrammarInstance normaliseInstance(ARTGrammarInstance instance) {
    ARTGrammarInstance ret = subNormaliseIntance(instance);
    if (ret == null) ret = instance;
    // System.out.println("Normalised " + instance + " to " + ret);
    return ret;
  }

  private ARTGrammarInstance subNormaliseIntance(ARTGrammarInstance instance) {
    if (instance instanceof ARTGrammarInstanceCat)
      return instance.getLhsL().getChild();
    else
      return grammar.getPrefixSlotMap().get(instance);
  }

}
