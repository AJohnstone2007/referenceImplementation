
package uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.Reference;
import uk.ac.rhul.cs.csle.art.term.ITerms;

public class Grammar {
  public String name = "";
  public final ITerms iTerms;
  public final Map<GElement, GElement> elements = new TreeMap<>();
  public final Map<Integer, GElement> elementsByNumber = new TreeMap<>();
  public int lexSize;
  public final Map<Integer, GNode> nodesByNumber = new TreeMap<>();
  public final Set<LKind> whitespaces = new HashSet<>();
  public final Map<GElement, GNode> rules = new TreeMap<>(); // Map from nonterminals to list of productions represented by their LHS node
  public final GNode endOfStringNode;

  private LKind[] lexicalKindsArray;
  private String[] lexicalStringsArray;
  private LKind[] whitespacesArray;

  public GElement startNonterminal;
  public Set<Integer> acceptingNodeNumbers = new TreeSet<>(); // Set of nodes which are END nodes of the start production

  private int nextFreeEnumerationElement = 0;

  public Grammar(String name, ITerms iTerms) {
    this.name = name;
    this.iTerms = iTerms;
    endOfStringNode = new GNode(GKind.EOS, "$", this); // Note that this first GNode to be created fills in static grammar field
    endOfStringNode.seq = endOfStringNode; // trick to ensure initial call collects rootNode
    whitespaces.add(LKind.SIMPLE_WHITESPACE); // default whitespace if non declared
  }

  public void normalise() {
    numberElementsAndNodes();

    setEndNodeLinks();

    checkRules();

    computeAttributes();

    // System.out.println(this.toStringDot());
    // System.out.println(this.toString());
    // System.out.println("Grammar " + name + " has whitespace elements: " + whitespaces);
  }

  private void numberElementsAndNodes() {
    Set<GKind> lexKinds = Set.of(GKind.B, GKind.C, GKind.T, GKind.TI);
    for (GElement s : elements.keySet()) {
      s.ei = nextFreeEnumerationElement++;
      if (lexKinds.contains(s.kind)) lexSize = s.ei;
    }
    lexSize++;

    nodesByNumber.put(endOfStringNode.num = nextFreeEnumerationElement++, endOfStringNode);
    for (GElement n : rules.keySet())
      numberElementsAndNodesRec(rules.get(n));
  }

  private void numberElementsAndNodesRec(GNode node) {
    if (node != null) {
      nodesByNumber.put(node.num = nextFreeEnumerationElement++, node);
      if (node.elm.kind != GKind.END) {
        numberElementsAndNodesRec(node.seq);
        numberElementsAndNodesRec(node.alt);
      }
    }
  }

  private void setEndNodeLinks() {
    for (GElement n : rules.keySet()) {
      GNode lhs = rules.get(n);
      for (GNode production = lhs.alt; production != null; production = production.alt)
        setEndNodeLinksRec(lhs, production);
    }
  }

  private void setEndNodeLinksRec(GNode parentNode, GNode altNode) {
    GNode gn;
    for (gn = altNode.seq; gn.elm.kind != GKind.END; gn = gn.seq) {
      // System.out.println("processEndNodes at " + gn.ni + " " + gn);
      if (gn.alt != null) for (GNode alternate = gn.alt; alternate != null; alternate = alternate.alt)
        setEndNodeLinksRec(gn, alternate); // Recurse into brackets
    }
    // System.out.println("processEndNodes processing " + gn.ni + " " + gn);
    gn.alt = altNode; // We are now at the end of a production or of a bracketed alternate
    gn.seq = parentNode;
    if (parentNode == rules.get(startNonterminal)) acceptingNodeNumbers.add(gn.num);
    // System.out.println("processEndNodes updated alt and seq to " + gn.alt.ni + " " + gn.seq.ni);
  }

  void checkRules() {
    Set<GElement> tmp = new HashSet<>();
    for (GElement e : elements.keySet())
      if (e.kind == GKind.N && rules.get(e) == null) tmp.add(e);

    if (tmp.size() > 0) {
      StringBuilder sb = new StringBuilder();
      sb.append("*** Context Free Grammar error - nonterminal" + (tmp.size() == 1 ? " " : "s ") + "used but not defined: ");
      for (GElement n : tmp)
        sb.append(n.str + " ");
      Reference.fatal(sb.toString());
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
    for (GElement e : elements.keySet()) {
      // System.out.println("Computing lexical arrays for " + e);
      switch (e.kind) {
      case B:
        try {
          lexicalKindsArray[token] = LKind.valueOf(e.str);
        } catch (IllegalArgumentException ex) {
          Reference.fatal("Unknown builtin &" + e.str);
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
  public GNode workingNode;
  public GIFTKind workingFold = GIFTKind.NONE;
  public int workingAction = 0;

  GNode mostRecentLHS;

  /* Action routines called from the script term traverser */
  public GElement findElement(GKind kind, String s) {
    GElement candidate = new GElement(kind, s);
    if (elements.get(candidate) == null) elements.put(candidate, candidate);
    return elements.get(candidate);
  }

  LinkedList<GNode> stack = new LinkedList<>();

  public void lhsAction(String id) {
    GElement element = findElement(GKind.N, id);
    if (startNonterminal == null) startNonterminal = element;
    workingNode = rules.get(element);
    if (workingNode == null) rules.put(element, updateWorkingNode(GKind.N, id));
    mostRecentLHS = rules.get(element);
  }

  public void altAction() {
    stack.push(workingNode);
    while (workingNode.alt != null) // Maintain specification order by adding new alternate at the end
      workingNode = workingNode.alt;
    workingNode = new GNode(GKind.ALT, "", workingAction, workingFold, null, workingNode);
    workingAction = 0;
    workingFold = GIFTKind.NONE;
  }

  public void endAction(String actions) {
    updateWorkingNode(GKind.END, "");
    workingNode = stack.pop();
  }

  public GNode updateWorkingNode(GKind kind, String str) {
    workingNode = new GNode(kind, str, workingAction, workingFold, workingNode, null);
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
    for (GElement n : rules.keySet())
      toStringDotRec(sb, rules.get(n));
    sb.append("}\n");
    return sb.toString();
  }

  private void toStringDotRec(StringBuilder sb, GNode cs) {
    sb.append("\"" + cs.num + "\"[label=\"" + cs.toStringDot() + "\"]\n");
    if (cs.elm.kind == GKind.ALT) {
      seqArrow(sb, cs);
      altArrow(sb, cs);
    } else if (cs.elm.kind != GKind.END) {
      altArrow(sb, cs);
      seqArrow(sb, cs);
    }
  }

  private void altArrow(StringBuilder sb, GNode cs) {
    if (cs.alt == null) return;
    sb.append(
        "\"" + cs.num + "\"->\"" + cs.alt.num + "\"" + "{rank = same; \"" + cs.num + "\"" + ";\"" + cs.alt.num + "\"" + ";" + "}" + "[label=\" a\"" + "]\n");
    if (!isLHS(cs.alt)) toStringDotRec(sb, cs.alt);
  }

  private void seqArrow(StringBuilder sb, GNode cs) {
    if (cs.seq == null) return;
    sb.append("\"" + cs.num + "\"->\"" + cs.seq.num + "\"\n");
    toStringDotRec(sb, cs.seq);
  }

  /* Text rendering */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Grammar with start nonterminal " + startNonterminal.str + "\n");
    sb.append("Grammar rules:\n");
    for (GElement n : rules.keySet()) {
      // sb.append("Rule " + n + "\n");
      boolean first = true;
      for (GNode production = rules.get(n).alt; production != null; production = production.alt) {
        // sb.append("alt " + production + "\n");
        if (first) {
          sb.append("  " + production.toStringAsProduction(" ::=\n    ", null) + "\n");
          first = false;
        } else {
          sb.append("  | " + production.toStringAsRHS(null) + "\n");
        }
      }
    }

    sb.append("Grammar elements:\n");
    for (GElement s : elements.keySet())
      sb.append("  " + s + "\n");

    sb.append("Grammar nodes:\n");
    for (int i : nodesByNumber.keySet())
      sb.append("  " + i + ": " + nodesByNumber.get(i).toStringAsProduction() + "\n");
    sb.append("Accepting node" + (acceptingNodeNumbers.size() == 1 ? "" : "s") + ":");
    for (Integer a : acceptingNodeNumbers)
      sb.append(" " + a);
    sb.append("\nWhitespaces: " + whitespaces);
    return sb.toString();
  }

  public void visualise(String filename) {
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
    for (GElement gs : elements.keySet())
      ret[gs.ei] = gs.kind.ordinal();
    for (int ni : nodesByNumber.keySet())
      ret[ni] = nodesByNumber.get(ni).elm.kind.ordinal();
    return ret;
  }

  public int[][] makeAltsArray() {
    int ret[][] = new int[nextFreeEnumerationElement][];
    for (int ni : nodesByNumber.keySet()) {
      GNode gn = nodesByNumber.get(ni);
      int altCount = 0;
      for (GNode alt = gn.alt; alt != null; alt = alt.alt)
        altCount++;
      ret[ni] = new int[altCount + 1];
      altCount = 0;
      for (GNode alt = gn.alt; alt != null; alt = alt.alt)
        ret[ni][altCount++] = alt.num;
      ret[ni][altCount] = 0;
    }
    return ret;
  }

  public int[] makeSeqsArray() {
    int ret[] = new int[nextFreeEnumerationElement];
    for (int ni : nodesByNumber.keySet()) {
      GNode sn = nodesByNumber.get(ni).seq;
      ret[ni] = sn == null ? 0 : sn.num;
    }
    return ret;
  }

  public int[] makeCallTargetsArray() {
    int[] ret = new int[nextFreeEnumerationElement];
    for (int ni : nodesByNumber.keySet()) {
      GNode lhs = rules.get(nodesByNumber.get(ni).elm);
      ret[ni] = (lhs == null ? 0 : lhs.num);
    }
    return ret;
  }

  public int[] makeElementOfArray() {
    int[] ret = new int[nextFreeEnumerationElement];
    for (int ni : nodesByNumber.keySet()) {
      GElement el = nodesByNumber.get(ni).elm;
      ret[ni] = (el == null ? 0 : el.ei);
    }
    return ret;
  }

  /** Static methods *********************************************************/

  public static boolean isLHS(GNode gn) {
    return gn.elm.kind == GKind.N && gn.seq == null;
  }
}
