package uk.ac.rhul.cs.csle.art.old.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uk.ac.rhul.cs.csle.art.old.v3.manager.mode.ARTModeAlgorithm;
import uk.ac.rhul.cs.csle.art.old.v3.manager.mode.ARTModeDespatch;
import uk.ac.rhul.cs.csle.art.old.v3.manager.mode.ARTModeSupport;

public class Directives {
  public final ArrayList<String> inputs = new ArrayList<>();
  public final ArrayList<String> inputFilenames = new ArrayList<>();

  private final Map<String, Object> directives;

  public Directives() {
    directives = new HashMap<>();
    directives.put("verbosity", 0);
    directives.put("trace", false);
    directives.put("log", 1);

    directives.put("input", 0);
    directives.put("inputPrint", false);
    directives.put("inputCounts", false);

    directives.put("tweShortest", false);
    directives.put("tweLongest", false);
    directives.put("twePriority", false);
    directives.put("tweDead", false);
    directives.put("tweSelectOne", false);

    directives.put("twePrint", false);
    directives.put("twePrintFull", false);
    directives.put("tweCounts", false);
    directives.put("tweAmbiguityClasses", false);

    directives.put("tweLexicalisations", false);
    directives.put("tweLexicalisationsQuick", false);
    directives.put("tweExtents", false);
    directives.put("tweSegments", false);
    directives.put("tweRecursive", false);

    directives.put("tweDump", false);
    directives.put("tweTokenWrite", false);

    directives.put("parseCounts", false);
    directives.put("outputDirectory", ".");
    directives.put("namespace", "");
    directives.put("lexerName", "ARTGeneratedLexer");
    directives.put("parserName", "ARTGeneratedParser");

    directives.put("predictivePops", false);
    directives.put("FIFODescriptors", false);
    directives.put("suppressPopGuard", false);
    directives.put("suppressProductionGuard", false);
    directives.put("suppressTestRepeat", false);
    directives.put("suppressSemantics", false);
    directives.put("clusteredGSS", false);

    directives.put("despatchMode", ARTModeDespatch.fragment);
    directives.put("algorithmMode", ARTModeAlgorithm.gllGeneratorPool);

    directives.put("sppfChooseCounts", false);
    directives.put("sppfAmbiguityAnalysis", false);
    directives.put("sppfAmbiguityAnalysisFull", false);

    directives.put("sppfShortest", false);
    directives.put("sppfLongest", false);
    directives.put("sppfPriority", false);
    directives.put("sppfDead", false);

    directives.put("sppfOrderedLongest", false);

    directives.put("sppfSelectOne", false);

    directives.put("sppfCountSentences", false);

    directives.put("sppfCycleDetect", false);
    directives.put("smlCycleBreak", false);

    directives.put("tweFromSPPF", false);

    directives.put("sppfShow", false);
    directives.put("sppfShowFull", false);

    directives.put("gssShow", false);
    directives.put("treeShow", false);
    directives.put("treePrint", false);
    directives.put("treePrintLevel", 3);
    directives.put("termPrint", false);
    directives.put("termWrite", false);

    directives.put("treePrint", false);
    directives.put("termPrint", false);
    directives.put("termWrite", false);

    directives.put("rewriteConfiguration", false);
    directives.put("rewriteDisable", false);

    directives.put("rewritePure", true);
    directives.put("rewritePreorder", false);
    directives.put("rewritePostorder", false);

    directives.put("rewriteOneStep", false);
    directives.put("rewriteResume", true);
    directives.put("rewriteContractum", true);

    directives.put("actionSuppress", false);
  }

  public Directives(Directives artDirectives) {
    directives = new HashMap<>(artDirectives.directives);
  }

  public Boolean b(String key) {
    Object v = directives.get(key);
    if (v == null) {
      System.out.println("Ignoring unknown directive !" + key);
      return false;
    }
    if (!(v instanceof Boolean)) throw new ARTUncheckedException("internal - type mismatch on directive " + key);
    return (Boolean) v;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ARTDirectives [inputs=");
    builder.append(inputs);
    builder.append(", inputFilenames=");
    builder.append(inputFilenames);
    builder.append(", directives=");
    builder.append(directives);
    builder.append("]");
    return builder.toString();
  }

  public String s(String key) {
    Object v = directives.get(key);
    if (v == null) {
      System.out.println("Ignoring unknown directive !" + key);
      return "";
    }
    if (!(v instanceof String)) throw new ARTUncheckedException("internal - type mismatch on directive " + key);
    return (String) v;
  }

  public Integer i(String key) {
    Object v = directives.get(key);
    if (v == null) {
      System.out.println("Ignoring unknown directive !" + key);
      return 0;
    }
    if (!(v instanceof Integer)) throw new ARTUncheckedException("internal - type mismatch on directive " + key);
    return (Integer) v;
  }

  public void set(String key, Object o) {
    Object v = directives.get(key);
    if (v == null) {
      System.out.println("Ignoring unknown directive !" + key);
      return;
    }
    if (!(v.getClass().equals(o.getClass()))) throw new ARTUncheckedException("internal -  type mismatch on directive " + key);
    directives.put(key, o);
  }

  public ARTModeAlgorithm algorithmMode() {
    return (ARTModeAlgorithm) directives.get("algorithmMode");
  }

  public ARTModeDespatch despatchMode() {
    return (ARTModeDespatch) directives.get("despatchMode");
  }

  public ARTModeSupport supportMode() {
    return (ARTModeSupport) directives.get("supportMode");
  }
}
