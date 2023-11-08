package uk.ac.rhul.cs.csle.art.cfg.referenceFamily;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GNode;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.Grammar;

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

  protected boolean match(GNode gn) {
    return input[i] == gn.elm.ei;
  }

  public void parse() {
    System.out.println("Parse not available");
  }

  public void visualise() {
    System.out.println("Visualisation not available");
  }

  public int derivationAsTerm() {
    System.out.println("derivationTerm() not implemented for parser class " + this.getClass().getSimpleName());
    return 0;
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

  public void report(boolean outcome) {
    System.out.println((!inadmissable && accepted == outcome ? "Good: " : "Bad: ") + this.getClass().getSimpleName() + " " + grammar.name + " "
        + (inadmissable ? "Inadmissable" : (input == null ? "Lexical error" : accepted ? "Accept" : "Reject")) + " '"
        + inputString.substring(inputString.length() < 10 ? inputString.length() : 10) + "'");
  }

  final int displayPrefixLength = 20;

  public void statistics(boolean outcome) {
    System.out.println(timestamp() + "," + this.getClass().getSimpleName() + "," + grammar.name + "," + inputStringName + ",'"
        + inputString.substring(0, Math.min(displayPrefixLength, inputString.length())).replace("\n", "\\n").replace("\r", "\\r")
        + (displayPrefixLength < inputString.length() ? "...'" : "'") + "," + inputString.length() + "," + input.length + ","
        + (inadmissable ? "Inadmissable" : accepted ? "Accept" : "Reject") + "," + (!inadmissable && accepted == outcome ? "Good," : "Bad,")
        + String.format("%.2f", intervalAsSeconds()) + "," + String.format("%.2f", input.length / intervalAsSeconds()) + "," + subStatistics());
  }

  protected String subStatistics() {
    return "";
  }

  protected void trace(int level, String msg) {
    if (level <= traceLevel) System.out.println(msg);
  }
}
