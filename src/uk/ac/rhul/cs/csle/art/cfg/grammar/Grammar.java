
package uk.ac.rhul.cs.csle.art.cfg.grammar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import uk.ac.rhul.cs.csle.art.term.ITerms;
import uk.ac.rhul.cs.csle.art.util.Util;

public class Grammar {
  public String name = "";
  public final ITerms iTerms;
  public final static Set<GrammarKind> grammarBNFKinds = Set.of(GrammarKind.T, GrammarKind.TI, GrammarKind.C, GrammarKind.B, GrammarKind.EPS, GrammarKind.N);
  public final Map<GrammarElement, GrammarElement> elements = new TreeMap<>();
  public final Map<Integer, GrammarElement> elementsByNumber = new TreeMap<>();
  public int lexSize;
  public final Map<Integer, GrammarNode> nodesByNumber = new TreeMap<>();
  public final Set<LKind> whitespaces = new HashSet<>();
  public final Map<GrammarElement, GrammarNode> rules = new TreeMap<>(); // Map from nonterminals to list of productions represented by their LHS node
  public GrammarElement epsilonElement;
  public GrammarElement endOfStringElement;
  public final GrammarNode endOfStringNode;

  public LKind[] lexicalKindsArray;
  public String[] lexicalStringsArray;
  public LKind[] whitespacesArray;

  public GrammarElement startNonterminal;
  public Set<Integer> acceptingNodeNumbers = new TreeSet<>(); // Set of nodes which are END nodes of the start production

  private int nextFreeEnumerationElement;
  public boolean empty;

  public Grammar(String name, ITerms iTerms) {
    this.name = name;
    this.iTerms = iTerms;
    endOfStringNode = new GrammarNode(GrammarKind.EOS, "$", this); // Note that this first GNode to be created fills in static grammar field
    endOfStringNode.seq = endOfStringNode; // trick to ensure initial call collects rootNode
    epsilonElement = findElement(GrammarKind.EPS, "#");
    whitespaces.add(LKind.SIMPLE_WHITESPACE); // default whitespace if non declared
  }

  boolean isBNFElement(GrammarElement e) {
    return grammarBNFKinds.contains(e.kind);
  }

  public void normalise() {
    nextFreeEnumerationElement = 0;
    numberElementsAndNodes();

    setEndNodeLinks();

    checkRules();

    computeAttributes();

    // System.out.println(this.toStringDot());
    // System.out.println(this.toString());
    // System.out.println("Grammar " + name + " has whitespace elements: " + whitespaces);
  }

  private void numberElementsAndNodes() {
    Set<GrammarKind> lexKinds = Set.of(GrammarKind.B, GrammarKind.C, GrammarKind.T, GrammarKind.TI);
    for (GrammarElement s : elements.keySet()) {
      s.ei = nextFreeEnumerationElement++;
      if (lexKinds.contains(s.kind)) lexSize = s.ei;
      // System.out.println("Enumerating grammar element " + s.ei + ": " + s.str);
    }
    lexSize++;

    nodesByNumber.put(endOfStringNode.num = nextFreeEnumerationElement++, endOfStringNode);
    for (GrammarElement n : rules.keySet())
      numberElementsAndNodesRec(rules.get(n));
  }

  private void numberElementsAndNodesRec(GrammarNode node) {
    if (node != null) {
      nodesByNumber.put(node.num = nextFreeEnumerationElement++, node);
      if (node.elm.kind != GrammarKind.END) {
        numberElementsAndNodesRec(node.seq);
        numberElementsAndNodesRec(node.alt);
      }
    }
  }

  private void setEndNodeLinks() {
    for (GrammarElement n : rules.keySet()) {
      GrammarNode lhs = rules.get(n);
      for (GrammarNode production = lhs.alt; production != null; production = production.alt)
        setEndNodeLinksRec(lhs, production);
    }
  }

  private void setEndNodeLinksRec(GrammarNode parentNode, GrammarNode altNode) {
    GrammarNode gn;
    for (gn = altNode.seq; gn.elm.kind != GrammarKind.END; gn = gn.seq) {
      // System.out.println("processEndNodes at " + gn.ni + " " + gn);
      if (gn.alt != null) for (GrammarNode alternate = gn.alt; alternate != null; alternate = alternate.alt)
        setEndNodeLinksRec(gn, alternate); // Recurse into brackets
    }
    // System.out.println("processEndNodes processing " + gn.ni + " " + gn);
    gn.alt = altNode; // We are now at the end of a production or of a bracketed alternate
    gn.seq = parentNode;
    if (parentNode == rules.get(startNonterminal)) acceptingNodeNumbers.add(gn.num);
    // System.out.println("processEndNodes updated alt and seq to " + gn.alt.ni + " " + gn.seq.ni);
  }

  void checkRules() {
    empty = true;
    Set<GrammarElement> tmp = new HashSet<>();
    for (GrammarElement e : elements.keySet())
      if (e.kind == GrammarKind.N) {
        if (rules.get(e) == null)
          tmp.add(e);
        else
          empty = false;
      }

    if (tmp.size() > 0) {
      StringBuilder sb = new StringBuilder();
      sb.append("Nonterminal" + (tmp.size() == 1 ? " " : "s ") + "used but not defined: ");
      for (GrammarElement n : tmp)
        sb.append(n.str + " ");
      Util.fatal(sb.toString());
    }

    // reachable nonterminal test

    // cycle test
  }

  private void computeAttributes() {
    // 1. Compute lexical data
    lexicalKindsArray = new LKind[lexSize];
    lexicalStringsArray = new String[lexSize];

    int token = 0;
    lexicalStringsArray[0] = "EOS";
    for (GrammarElement e : elements.keySet()) {
      // System.out.println("Computing lexical arrays for " + e);
      switch (e.kind) {
      case B:
        try {
          lexicalKindsArray[token] = LKind.valueOf(e.str);
        } catch (IllegalArgumentException ex) {
          Util.fatal("Unknown builtin &" + e.str);
        }
        lexicalStringsArray[token] = e.str;
        break;
      case C:
        lexicalKindsArray[token] = LKind.CHARACTER;
        lexicalStringsArray[token] = e.str;
        break;
      case T:
        lexicalKindsArray[token] = LKind.SINGLETON_CASE_SENSITIVE;
        lexicalStringsArray[token] = e.str;
        break;
      case TI:
        lexicalKindsArray[token] = LKind.SINGLETON_CASE_INSENSITIVE;
        lexicalStringsArray[token] = e.str;
        break;
      default:
        break;
      }
      token++;
    }
    whitespacesArray = whitespaces.toArray(new LKind[0]);

    // 2. Positional attributes
    for (GrammarElement e : elements.keySet())
      if (e.kind == GrammarKind.N) for (GrammarNode gn = rules.get(e).alt; gn != null; gn = gn.alt) {
        GrammarNode gs = gn.seq;
        gs.isInitialSlot = true;
        while (true)
          if (gs.seq.elm.kind == GrammarKind.END) {
            gs.isPenultimateSlot = true;
            gs.seq.isFinalSlot = true;
            break;
          } else
            gs = gs.seq;
      }

    // 3. First and follow sets
    firstAndFollowSetsEBNF();
  }

  private boolean changed;

  private void firstAndFollowSetsEBNF() {
    if (startNonterminal != null) startNonterminal.follow.add(endOfStringElement);

    changed = true;
    while (changed) {
      changed = false;
      for (GrammarElement e : elements.keySet())
        if (e.kind == GrammarKind.N) {
          GrammarNode lhsNode = rules.get(e);
          firstSetsEBNFAltRec(lhsNode);
          changed |= e.first.addAll(lhsNode.instanceFirst);
        }

      for (GrammarElement e : elements.keySet())
        if (e.kind == GrammarKind.N) {
          GrammarNode lhsNode = rules.get(e);
          followSetsEBNFAltRec(lhsNode);
          changed |= e.first.addAll(lhsNode.instanceFirst);
        }
    }
  }

  private void followSetsEBNFAltRec(GrammarNode root) {
    // System.out.println("FollowAlt with root " + root.num);
    for (GrammarNode gn = root.alt; gn != null; gn = gn.alt)
      followSetsEBNFSeqRec(gn.seq, root);
  }

  // Debug BNF only
  private void followSetsEBNFSeqRec(GrammarNode gn, GrammarNode root) {
    if (gn.elm.kind == GrammarKind.END) return;

    followSetsEBNFSeqRec(gn.seq, root); // Work in reverse order for compatability with first set construction

    if (gn.alt == null)
      ; // BNF nodes
    else
      followSetsEBNFAltRec(gn);

    Set<GrammarElement> tmp = new HashSet<>(gn.seq.instanceFirst);
    tmp.remove(epsilonElement);
    changed |= gn.instanceFollow.addAll(tmp);
    changed |= gn.elm.follow.addAll(tmp);
    if (gn.seq.isNullableSlot) {
      changed |= gn.elm.follow.addAll(root.elm.follow);
      changed |= gn.instanceFollow.addAll(root.elm.follow);
    }

  }

  private void firstSetsEBNFAltRec(GrammarNode root) {
    for (GrammarNode gn = root.alt; gn != null; gn = gn.alt) {
      if (firstSetsEBNFSeqRec(gn.seq)) changed |= root.instanceFirst.add(epsilonElement); // Seen only nullable
      Set<GrammarElement> tmp = new HashSet<>(gn.seq.instanceFirst);
      tmp.remove(epsilonElement);
      changed |= root.instanceFirst.addAll(tmp);
    }
  }

  private boolean firstSetsEBNFSeqRec(GrammarNode gn) {
    boolean seenOnlyNullable = true;

    if (gn.elm.kind == GrammarKind.END) {
      changed |= gn.instanceFirst.add(epsilonElement);
      return true;
    }
    seenOnlyNullable &= firstSetsEBNFSeqRec(gn.seq); // Work in reverse order to reduce the number of passes

    if (gn.alt == null) { // BNF nodes
      changed |= gn.instanceFirst.add(gn.elm); // These are nonterminal-also first sets
      changed |= gn.instanceFirst.addAll(gn.elm.first); // Fold in the current first for this element
    } else { // Handle EBNF subrules
      firstSetsEBNFAltRec(gn);
      if (gn.elm.kind == GrammarKind.KLN || gn.elm.kind == GrammarKind.OPT) changed |= gn.instanceFirst.add(epsilonElement);
    }

    if (gn.instanceFirst.contains(epsilonElement))
      changed |= gn.instanceFirst.addAll(gn.seq.instanceFirst);// If we are a nullable slot, fold in our successor's instance first sets
    else
      seenOnlyNullable = false;

    gn.isNullableSlot = seenOnlyNullable;

    return seenOnlyNullable;
  }

  // Data access for lexers
  public LKind[] lexicalKindsArray() {
    return lexicalKindsArray;
  }

  public String[] lexicalStringsArray() {
    return lexicalStringsArray;
  }

  public LKind[] whitespacesArray() {
    return whitespacesArray;
  }

  // Atttribute-action functions from ReferenceGrammarParser.art below this line
  public GrammarNode workingNode;
  public GIFTKind workingFold = GIFTKind.NONE;
  public int workingAction = 0;

  GrammarNode mostRecentLHS;

  /* Action routines called from the script term traverser */
  public GrammarElement findElement(GrammarKind kind, String s) {
    GrammarElement candidate = new GrammarElement(kind, s);
    if (elements.get(candidate) == null) elements.put(candidate, candidate);
    return elements.get(candidate);
  }

  LinkedList<GrammarNode> stack = new LinkedList<>();

  public void lhsAction(String id) {
    GrammarElement element = findElement(GrammarKind.N, id);
    if (startNonterminal == null) startNonterminal = element;
    workingNode = rules.get(element);
    if (workingNode == null) rules.put(element, updateWorkingNode(GrammarKind.N, id));
    mostRecentLHS = rules.get(element);
  }

  public void altAction() {
    stack.push(workingNode);
    while (workingNode.alt != null) // Maintain specification order by adding new alternate at the end
      workingNode = workingNode.alt;
    workingNode = new GrammarNode(GrammarKind.ALT, "", workingAction, workingFold, null, workingNode);
    workingAction = 0;
    workingFold = GIFTKind.NONE;
  }

  public void endAction(String actions) {
    updateWorkingNode(GrammarKind.END, "");
    workingNode = stack.pop();
  }

  public GrammarNode updateWorkingNode(GrammarKind kind, String str) {
    workingNode = new GrammarNode(kind, str, workingAction, workingFold, workingNode, null);
    workingAction = 0;
    workingFold = GIFTKind.NONE;
    return workingNode;
  }

  /* Visualisation rendering */
  public String toStringDot() {
    StringBuilder sb = new StringBuilder();
    sb.append("digraph \"Reference grammar\"\n" + "{\n" + "graph[ordering=out ranksep=0.1]\n"
        + "node[fontname=Helvetica fontsize=9 shape=box height = 0 width = 0 margin= 0.04 color=gray]\n"
        + "edge[fontname=Helvetica fontsize=9 arrowsize = 0.3 color=gray]\n\n");
    for (GrammarElement n : rules.keySet())
      toStringDotRec(sb, rules.get(n));
    sb.append("}\n");
    return sb.toString();
  }

  private void toStringDotRec(StringBuilder sb, GrammarNode cs) {
    sb.append("\"" + cs.num + "\"[label=\"" + cs.toStringDot() + "\"]\n");
    if (cs.elm.kind == GrammarKind.ALT) {
      seqArrow(sb, cs);
      altArrow(sb, cs);
    } else if (cs.elm.kind != GrammarKind.END) {
      altArrow(sb, cs);
      seqArrow(sb, cs);
    }
  }

  private void altArrow(StringBuilder sb, GrammarNode cs) {
    if (cs.alt == null) return;
    sb.append(
        "\"" + cs.num + "\"->\"" + cs.alt.num + "\"" + "{rank = same; \"" + cs.num + "\"" + ";\"" + cs.alt.num + "\"" + ";" + "}" + "[label=\" a\"" + "]\n");
    if (!isLHS(cs.alt)) toStringDotRec(sb, cs.alt);
  }

  private void seqArrow(StringBuilder sb, GrammarNode cs) {
    if (cs.seq == null) return;
    sb.append("\"" + cs.num + "\"->\"" + cs.seq.num + "\"\n");
    toStringDotRec(sb, cs.seq);
  }

  /* Text rendering */

  public String toStringProperties() {
    return toStringBody(true);
  }

  @Override
  public String toString() {
    return toStringBody(false);
  }

  public String toStringBody(boolean showProperties) {
    StringBuilder sb = new StringBuilder();
    if (startNonterminal != null)
      sb.append("Grammar " + name + " with start nonterminal " + startNonterminal.str + "\n");
    else
      sb.append("Grammar " + name + " with empty start nonterminal\n");

    if (empty) return "Grammar has no rules";

    sb.append("Grammar rules:\n");
    for (GrammarElement n : rules.keySet()) {
      boolean first = true;
      for (GrammarNode production = rules.get(n).alt; production != null; production = production.alt) {
        if (first) {
          sb.append(" " + production.toStringAsProduction(" ::=\n ", null) + "\n");
          first = false;
        } else {
          sb.append(" | " + production.toStringAsRHS(null) + "\n");
        }
      }
    }

    sb.append("Grammar elements:\n");
    for (GrammarElement s : elements.keySet()) {
      sb.append(" (" + s.toStringDetailed() + ") " + s);
      if (showProperties && s.first != null) {
        sb.append(" first = {");
        appendElements(sb, s.first);
        sb.append("} follow = {");
        appendElements(sb, s.follow);
        sb.append("}");
      }
      sb.append("\n");
    }
    sb.append("Grammar nodes:\n");
    for (int i : nodesByNumber.keySet()) {
      GrammarNode gn = nodesByNumber.get(i);
      sb.append(" " + i + ": " + gn.toStringAsProduction());
      if (showProperties) {
        if (gn.isInitialSlot) sb.append(" Initial");
        if (gn.isPenultimateSlot) sb.append(" Penultimate");
        if (gn.isFinalSlot) sb.append(" Final");
        if (gn.isNullableSlot) sb.append(" Nullable");

        if (gn.instanceFirst != null && gn.elm.kind != GrammarKind.END) {
          sb.append(" instfirst = {");
          appendElements(sb, gn.instanceFirst);
          sb.append("} instfollow = {");
          appendElements(sb, gn.instanceFollow);
          sb.append("}");
        }
      }
      sb.append("\n");
    }
    sb.append("Accepting node" + (acceptingNodeNumbers.size() == 1 ? "" : "s") + ":");
    for (Integer a : acceptingNodeNumbers)
      sb.append(" " + a);
    sb.append("\nWhitespaces: " + whitespaces);
    return sb.toString();
  }

  private void appendElements(StringBuilder sb, Set<GrammarElement> elements) {
    boolean first = true;
    for (GrammarElement e : elements) {
      if (first)
        first = false;
      else
        sb.append(",");
      sb.append(e.toString());
    }
  }

  public void show(String filename) {
    PrintStream ps;
    try {
      ps = new PrintStream(new File(filename));
      ps.println(toStringDot());
      ps.close();
    } catch (FileNotFoundException e) {
      System.out.println("Unable to open visualisation file " + filename);
    }
  }

  /** Support for table driven parsers ***************************************/

  public int[] makeKindsArray() {
    int ret[] = new int[nextFreeEnumerationElement];
    for (GrammarElement gs : elements.keySet())
      ret[gs.ei] = gs.kind.ordinal();
    for (int ni : nodesByNumber.keySet())
      ret[ni] = nodesByNumber.get(ni).elm.kind.ordinal();
    return ret;
  }

  public int[][] makeAltsArray() {
    int ret[][] = new int[nextFreeEnumerationElement][];
    for (int ni : nodesByNumber.keySet()) {
      GrammarNode gn = nodesByNumber.get(ni);
      int altCount = 0;
      for (GrammarNode alt = gn.alt; alt != null; alt = alt.alt)
        altCount++;
      ret[ni] = new int[altCount + 1];
      altCount = 0;
      for (GrammarNode alt = gn.alt; alt != null; alt = alt.alt)
        ret[ni][altCount++] = alt.num;
      ret[ni][altCount] = 0;
    }
    return ret;
  }

  public int[] makeSeqsArray() {
    int ret[] = new int[nextFreeEnumerationElement];
    for (int ni : nodesByNumber.keySet()) {
      GrammarNode sn = nodesByNumber.get(ni).seq;
      ret[ni] = sn == null ? 0 : sn.num;
    }
    return ret;
  }

  public int[] makeCallTargetsArray() {
    int[] ret = new int[nextFreeEnumerationElement];
    for (int ni : nodesByNumber.keySet()) {
      GrammarNode lhs = rules.get(nodesByNumber.get(ni).elm);
      ret[ni] = (lhs == null ? 0 : lhs.num);
    }
    return ret;
  }

  public int[] makeElementOfArray() {
    int[] ret = new int[nextFreeEnumerationElement];
    for (int ni : nodesByNumber.keySet()) {
      GrammarElement el = nodesByNumber.get(ni).elm;
      ret[ni] = (el == null ? 0 : el.ei);
    }
    return ret;
  }

  /** Static methods *********************************************************/

  public static boolean isLHS(GrammarNode gn) {
    return gn.elm != null && gn.elm.kind == GrammarKind.N && gn.seq == null;
  }

}
