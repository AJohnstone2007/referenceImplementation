package uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily.grammar.GNode;
import uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily.grammar.Grammar;

public abstract class ReferenceParser {
  public int traceLevel = 0;
  public Grammar grammar;
  public String inputString;
  public String stringName = "";
  public int[] input;
  public int[] positions;
  protected int i;
  public boolean accepted;
  public boolean inadmissable;
  public int rightmostParseIndex;

  protected void trace(int level, String msg) {
    if (level <= traceLevel) System.out.println(msg);
  }

  protected boolean match(GNode gn) {
    return input[i] == gn.elm.ei;
  }

  public void parse() {
    System.out.println("Parse not available");
  }

  public void visualise() {
    System.out.println("Visualisation not available");
  }

  public void report(boolean outcome) {
    System.out.println((!inadmissable && accepted == outcome ? "Good: " : "Bad: ") + this.getClass().getSimpleName() + " " + grammar.grammarName + " "
        + (inadmissable ? "Inadmissable" : (input == null ? "Lexical error" : accepted ? "Accept" : "Reject")));
  }

  public void statistics(boolean outcome) {
    if (accepted) System.out.println(timestamp() + "," + this.getClass().getSimpleName() + "," + grammar.grammarName + "," + stringName + ",'"
        + inputString.substring(0, Math.min(10, inputString.length())).replace("\n", "\\n").replace("\r", "\\r") + "'," + inputString.length() + ","
        + input.length + "," + (inadmissable ? "Inadmissable" : accepted ? "Accept" : "Reject") + ","
        + (!inadmissable && accepted == outcome ? "Good," : "Bad,") + intervalAsSeconds() + "," + String.format("%.2f", input.length / intervalAsSeconds())
        + "," + subStatistics());
  }

  protected String subStatistics() {
    return "";
  }

  protected GNode lhs(GNode gn) {
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

  public int derivationTerm() {
    System.out.println("derivationTerm() not implemented for parser class " + this.getClass().getSimpleName());
    return 0;
  }

  protected void setGrammar(Grammar grammar) {
    this.grammar = grammar;
  }
}
