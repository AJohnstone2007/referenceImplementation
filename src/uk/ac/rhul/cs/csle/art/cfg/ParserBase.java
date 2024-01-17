package uk.ac.rhul.cs.csle.art.cfg;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;

import uk.ac.rhul.cs.csle.art.cfg.grammar.Grammar;
import uk.ac.rhul.cs.csle.art.cfg.grammar.GrammarKind;
import uk.ac.rhul.cs.csle.art.cfg.grammar.GrammarNode;
import uk.ac.rhul.cs.csle.art.util.Util;

public abstract class ParserBase {
  public int traceLevel = 0;
  public Grammar grammar;
  public String inputString;
  public String inputStringName = "";
  public int[] input;
  public int[] positions;
  protected int i;
  public boolean accepted;
  public boolean inadmissable;
  public int rightmostParseIndex;
  public boolean suppressEcho;

  // TODO: this needs to be merged with gllBaseLine.constructorOf(,)
  protected String lexemeForBuiltin(int inputIndex) {
    switch (grammar.lexicalKindsArray[input[inputIndex]]) {
    case ID: {
      int right = positions[inputIndex];
      while (right < inputString.length()
          && (Character.isAlphabetic(inputString.charAt(right)) || Character.isDigit(inputString.charAt(right)) || inputString.charAt(right) == '_'))
        right++;

      return inputString.substring(positions[inputIndex], right);
    }
    case CHARACTER: {
      int right = positions[inputIndex];
      while (!LexerBase.isSimpleSpace(inputString.charAt(right)))
        right++;

      return inputString.substring(positions[inputIndex], right);
    }
    case CHAR_BQ:
      break;
    case COMMENT_BLOCK_C:
      break;
    case COMMENT_LINE_C:
      break;
    case COMMENT_NEST_ART:
      break;
    case INTEGER: {
      int right = positions[inputIndex];
      while (right < inputString.length() && (Character.isDigit(inputString.charAt(right)) || inputString.charAt(right) == '_'))
        right++;

      return inputString.substring(positions[inputIndex], right);
    }
    case REAL: {
      int right = positions[inputIndex];
      while (!LexerBase.isSimpleSpace(inputString.charAt(right)))
        right++;

      return inputString.substring(positions[inputIndex], right);
    }

    case SIGNED_INTEGER: {
      int right = positions[inputIndex];
      while (right < inputString.length() && (Character.isDigit(inputString.charAt(right)) || inputString.charAt(right) == '_'))
        right++;

      return inputString.substring(positions[inputIndex], right);
    }

    case SIGNED_REAL: {
      int right = positions[inputIndex];
      while (!LexerBase.isSimpleSpace(inputString.charAt(right)))
        right++;

      return inputString.substring(positions[inputIndex], right);
    }

    case SIMPLE_WHITESPACE:
      break;
    case SINGLETON_CASE_INSENSITIVE:
      break;
    case SINGLETON_CASE_SENSITIVE:
      break;
    case STRING_PLAIN_SQ: {
      int right = positions[inputIndex] + 1;
      while (inputString.charAt(right) != '\'')
        right++;

      return inputString.substring(positions[inputIndex] + 1, right);
    }

    case STRING_DQ: {
      int right = positions[inputIndex] + 1;
      while (inputString.charAt(right) != '\"')
        right++;

      return inputString.substring(positions[inputIndex] + 1, right);
    }
    case STRING_BRACE_NEST:
      break;
    case STRING_BRACKET_NEST:
      break;
    case STRING_DOLLAR:
      break;
    case STRING_SQ:
      break;
    }
    return "???";
  }

  protected boolean match(GrammarNode gn) {
    return input[i] == gn.elm.ei;
  }

  public void parse() {
    System.out.println("parse() not implemented for parser class " + this.getClass().getSimpleName());
  }

  public void show() {
    System.out.println("show() not implemented for parser class " + this.getClass().getSimpleName());
  }

  public void chooseLongestMatch() {
    System.out.println("chooseLongestMatch() not implemented for parser class " + this.getClass().getSimpleName());
  }

  public void selectFirst() {
    System.out.println("selectFirst() not implemented for parser class " + this.getClass().getSimpleName());
  }

  public void selectLast() {
    System.out.println("selectLast() not implemented for parser class " + this.getClass().getSimpleName());
  }

  public int derivationAsTerm() {
    System.out.println("derivationTerm() not implemented for parser class " + this.getClass().getSimpleName());
    return 0;
  }

  protected GrammarNode lhs(GrammarNode gn) {
    return grammar.rules.get(gn.elm);
  }

  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  public String timestamp() {
    return dateFormat.format(Calendar.getInstance().getTime());
  }

  public long readClock() {
    return System.currentTimeMillis();
  }

  public double timeAsSeconds(long time) {
    double ret = time / 1.0E3;
    if (ret < 1.0E-9) ret = 0.0;
    return ret;
  }

  public long startTime, endTime;

  public double intervalAsSeconds() {
    double ret = (endTime - startTime) / 1.0E3;
    if (ret < 1.0E-9) ret = 0.0;
    return ret;
  }

  public long startMemory, endMemory;

  public long memoryUsed() {
    System.gc();
    return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
  }

  public void report(boolean outcome) {
    System.out.println((!inadmissable && accepted == outcome ? "Good: " : "Bad: ") + this.getClass().getSimpleName() + " " + grammar.name + " "
        + (inadmissable ? "Inadmissable" : (input == null ? "Lexical error" : accepted ? "Accept" : "Reject")) + " '"
        + inputString.substring(inputString.length() < 10 ? inputString.length() : 10) + "'");
  }

  final int displayPrefixLength = 20;

  public void statistics(boolean outcome) {
    int inputLength = input == null ? 0 : input.length;
    System.out.println(timestamp() + "," + this.getClass().getSimpleName() + "," + grammar.name + "," + inputStringName + ",'"
        + inputString.substring(0, Math.min(displayPrefixLength, inputString.length())).replace("\n", "\\n").replace("\r", "\\r")
        + (displayPrefixLength < inputString.length() ? "...'" : "'") + "," + inputString.length() + "," + inputLength + ","
        + (inadmissable ? "Inadmissable" : accepted ? "Accept" : "Reject") + "," + (!inadmissable && accepted == outcome ? "Good," : "Bad,")
        + String.format("%.2f", intervalAsSeconds()) + "," + String.format("%.2f", inputLength / intervalAsSeconds()) + "," + subStatistics());
  }

  protected String subStatistics() {
    return "";
  }

  protected void trace(int level, String msg) {
    if (level <= traceLevel) System.out.println(msg);
  }

  public class DerivationSingletonNode {
    public GrammarNode gn;
    public DerivationSingletonNode next;

    public DerivationSingletonNode(GrammarNode gn, DerivationSingletonNode next) {
      super();
      this.gn = gn;
      this.next = next;
    }

    @Override
    public String toString() {
      return gn.toString();
    }
  }

  /* Singleton derivation support below this line */
  protected DerivationSingletonNode dnRoot, dn;
  private int derivationSingletonInputIndex = 0;

  public int derivationSingletonAsTerm() {
    int element = 0;
    for (DerivationSingletonNode tmp = dnRoot; tmp != null; tmp = tmp.next)
      System.out.println(element++ + " " + tmp.gn.toStringAsProduction());
    LinkedList<Integer> tmp = new LinkedList<>();
    derivationSingletonInputIndex = 0;
    dn = dnRoot.next;
    derivationSingletonAsTermRec(false, grammar.startNonterminal.str, tmp); // Initial call builds term into first element of tmp
    return tmp.getFirst();
  }

  // Some care is required due to phasing around promotion operators
  // We pass in the label for the called nonterminal and our children
  // If this nonterminal is being promoted, nodes are added to childrenFromParent, and any promoted names are passed back
  // If this nonterminal is not being promoted, a new children list is created and a complete new term is added to our children
  private String derivationSingletonAsTermRec(boolean promoted, String nonterminalName, LinkedList<Integer> childrenFromParent) {
    // System.out.println("** derivationAsTermRec() with parent label " + parentLabel + " and derivation node " + dn.gn.toStringAsProduction());

    LinkedList<Integer> children = promoted ? childrenFromParent : new LinkedList<>(); // If we are not promoted, then make new children list
    String ret = nonterminalName; // by default, pass back our own name

    for (GrammarNode s = dn.gn.seq;; s = s.seq) {
      System.out.println("Processing grammar element " + s.toStringAsProduction());
      String label = s.elm.str;
      switch (s.elm.kind) {
      case B: // Note flow through
        label = lexemeForBuiltin(derivationSingletonInputIndex);
      case C, T, TI, EPS:
        switch (s.giftKind) {
        case OVER:
          ret = label; // No children to add but promote this label
          break;
        case UNDER:
          break; // no children to add and no promotion either
        default: // no promotion operators so just add us to the children
          children.add(grammar.iTerms.findTerm(label, new int[0])); // This is a slightly unexpected construction: if we just findTerm on the string then
                                                                    // the term parser will be used, and we'd need to escape the metacharacters
        }
        if (s.elm.kind != GrammarKind.EPS) derivationSingletonInputIndex++;
        break;

      case N:
        dn = dn.next;
        switch (s.giftKind) {
        case OVER: // overwrite parent node label; note flowthrough to next case
          ret = label;
        case UNDER: // add children onto our children
          ret = derivationSingletonAsTermRec(true, ret, children);
          break;
        default: // no promotion operators so make a complete new term and add to our children
          derivationSingletonAsTermRec(false, s.elm.str, children);
        }
        break;

      case END:
        if (!promoted) childrenFromParent.add(grammar.iTerms.findTerm(ret, children)); // At end of rule make new term
        return ret;

      case ALT, DO, EOS, KLN, OPT, POS:
        Util.fatal("Unexpected grammar node in RDSOB derivation builder " + s);
        break;
      }
    }
  }
}
