package uk.ac.rhul.cs.csle.art.term;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OLDPipelineParamaters {
  public final ITerms iTerms;

  public final TermTraverserText moduleBuilderTraverser;
  public final TermTraverserText tt;
  public final TermTraverserText latexTraverser;
  public final Map<String, String> latexAliases = new HashMap<>();
  public final Map<Integer, Set<Integer>> cycleCheck = new HashMap<>();
  public final List<Integer> dynamicDirectives = new LinkedList<>();
  public Map<Integer, Integer> variableMap;
  // public final RewriterESOS eSOS;

  public int verbosityLevel = 5;
  public int traceLevel = 3;
  public int statisticsLevel = 5;
  public int defaultDepthLimit = 3;

  public int startNonterminal = 0;
  public int startRelation = 0;

  public String inputString = null;
  public int inputTerm = 0;
  public boolean inLanguage;

  public int resultTerm = 0;
  public int goodTest = 0;
  public int badTest = 0;

  public int rewriteAttemptCounter = 0;
  public int rewriteStepCounter = 0;
  public boolean rewriteDisable = false;
  public boolean rewritePure = true;
  public boolean rewritePreorder = false;
  public boolean rewritePostorder = false;
  public boolean rewriteOneStep = false;
  public boolean rewriteResume = false;
  public boolean rewriteContractum = false;
  public boolean rewriteTraverse = false;
  public boolean rewriteActive = false;

  public OLDPipelineParamaters(ITerms iTerms) {
    this.iTerms = iTerms;
    moduleBuilderTraverser = null; // new TermTraverserText(iTerms, "module builder traverser");
    tt = null; // new TermTraverserText(iTerms, "text traverser");
    latexTraverser = null; // new TermTraverserText(iTerms, "latex traverser");
    // eSOS = new RewriterESOS(this);
  }

  public String render(int term) {
    return this.tt.toString(term, false, defaultDepthLimit, variableMap);
  }
}
