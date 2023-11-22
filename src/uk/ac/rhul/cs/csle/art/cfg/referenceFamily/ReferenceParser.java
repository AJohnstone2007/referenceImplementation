package uk.ac.rhul.cs.csle.art.cfg.referenceFamily;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.Grammar;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GrammarNode;

public abstract class ReferenceParser {
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

  protected String lexemeForBuiltin(int inputIndex) {
    switch (grammar.lexicalKindsArray[input[inputIndex]]) {
    case ID: {
      int right = positions[inputIndex];
      while (right < inputString.length()
          && (Character.isAlphabetic(inputString.charAt(right)) || Character.isDigit(inputString.charAt(right)) || inputString.charAt(right) == '_'))
        right++;

      return inputString.substring(positions[inputIndex], right);
    }
    case CHARACTER:
      break;
    case CHAR_BQ:
      break;
    case COMMENT_BLOCK_C:
      break;
    case COMMENT_LINE_C:
      break;
    case COMMENT_NEST_ART:
      break;
    case INTEGER:
      break;
    case REAL:
      break;
    case SIGNED_INTEGER:
      break;
    case SIGNED_REAL:
      break;
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

      return inputString.substring(positions[inputIndex], right + 1);
    }

    case STRING_DQ: {
      int right = positions[inputIndex] + 1;
      while (inputString.charAt(right) != '\"')
        right++;

      return inputString.substring(positions[inputIndex], right + 1);
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
    System.out.println("Parse not available");
  }

  public void show() {
    System.out.println("Visualisation not available");
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
}
