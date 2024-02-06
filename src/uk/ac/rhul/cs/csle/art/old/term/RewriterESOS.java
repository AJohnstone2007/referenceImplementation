package uk.ac.rhul.cs.csle.art.old.term;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.core.ARTUncheckedException;
import uk.ac.rhul.cs.csle.art.old.core.OLDPipelineParamaters;

public class RewriterESOS extends Rewriter {
  private final OLDPipelineParamaters pp;

  public RewriterESOS(OLDPipelineParamaters pp) {
    this.pp = pp;
  }

  public void trace(int level, int indent, String string) {
    if (pp.traceLevel >= level) {
      for (int i = 0; i < indent; i++)
        System.out.print("  ");
      System.out.println(string);
    }
  }

  private String bindingsToString(int[] bindings, Map<Integer, Integer> variableMap) {
    StringBuilder sb = new StringBuilder();
    boolean seen = false;
    sb.append("{ ");
    for (int i = 0; i < bindings.length; i++) {
      if (bindings[i] > 0) {
        if (seen) sb.append(", ");
        sb.append(pp.render(pp.iTerms.findTerm("_" + i)) + "=" + pp.render(bindings[i]));
        seen = true;
      }
    }
    sb.append(" }");
    return sb.toString();
  }

  private int rewriteAttempt(int term, int relationTerm, int level) { // return rewritten term and set rewriteAttemptOutcome

    if (relationTerm == 0) throw new ARTUncheckedException("rewrite attempted on null relation");

    // trace(3, level, "Rewrite attempt " + ++pp.rewriteAttemptCounter + ": " + pp.render(term) + pp.render(relationTerm));
    trace(3, level, "Rewrite call " + ++pp.rewriteAttemptCounter + " " + pp.render(term) + pp.render(relationTerm));
    if (!pp.cycleCheck.containsKey(relationTerm)) pp.cycleCheck.put(relationTerm, new HashSet<Integer>());
    Set<Integer> cycleSet = pp.cycleCheck.get(relationTerm);
    // if (cycleSet.contains(configuration)) throw new ARTExceptionFatal("cycle detected " +iTerms.toString(configuration) +iTerms.toString(relationTerm));
    cycleSet.add(term);

    if (isTerminatingConfiguration(term, relationTerm)) {
      trace(3, level + 1, "Terminal " + pp.tt.toString(term));
      return term;
    }

    int rootTheta = thetaFromConfiguration(term);
    Map<Integer, List<Integer>> rulesForThisRelation = pp.mainModule.trRules.get(relationTerm);

    List<Integer> ruleList = null;

    if (rulesForThisRelation != null) { // There may be no rules at all in this relation!
      ruleList = rulesForThisRelation.get(pp.iTerms.getTermSymbolIndex(rootTheta));
      if (ruleList == null) ruleList = rulesForThisRelation.get(pp.iTerms.findString("")); // Deafult rules
    }

    if (ruleList == null) {
      trace(3, level, "No rules in" + pp.render(relationTerm) + "for constructor " + pp.iTerms.getTermSymbolString(rootTheta));
      return term;
    }

    nextRule: for (int ruleIndex : ruleList) {
      pp.variableMap = pp.mainModule.reverseVariableNamesByRule.get(ruleIndex);
      // System.out.println("Variable map: ");
      // for (int i : pp.variableMap.keySet())
      // System.out.println(i + ":" + pp.iTerms.getString(pp.variableMap.get(i)));
      trace(3, level, pp.render(ruleIndex)); // Announce the next rule we are going to try
      int lhs = pp.iTerms.getSubterm(ruleIndex, 1, 1, 0);
      int premises = pp.iTerms.getSubterm(ruleIndex, 1, 0);
      int premiseCount = pp.iTerms.getTermArity(premises);
      int rhs = pp.iTerms.getSubterm(ruleIndex, 1, 1, 2);
      int[] bindings = new int[ITerms.variableCount];

      int ruleLabel = thetaFromConfiguration(ruleIndex);
      if (!pp.iTerms.matchZeroSV(term, lhs, bindings)) {
        trace(3, level, pp.render(pp.iTerms.getSubterm(ruleLabel, 0)) + " Theta match failed: seek another rule");
        continue nextRule;
      }
      trace(5, level, pp.render(pp.iTerms.getSubterm(ruleLabel, 0)) + "bindings after Theta match " + bindingsToString(bindings, pp.variableMap));

      // Now work through the premises
      for (int premiseNumber = 0; premiseNumber < premiseCount; premiseNumber++) {
        int premise = pp.iTerms.getSubterm(premises, premiseNumber);
        trace(4, level, pp.render(pp.iTerms.getSubterm(ruleLabel, 0)) + "premise " + (premiseNumber + 1) + " " + pp.render(premise));
        if (pp.iTerms.hasSymbol(premise, "trMatch")) {// |> match expressions
          if (!pp.iTerms.matchZeroSV(pp.iTerms.substitute(bindings, pp.iTerms.getSubterm(premise, 0), 0), pp.iTerms.getSubterm(premise, 1), bindings)) {
            trace(4, level, pp.render(pp.iTerms.getSubterm(ruleLabel, 0)) + "premise " + (premiseNumber + 1) + " failed: seek another rule");
            continue nextRule;
          }
        } else { // transition
          if (pp.iTerms.hasSymbol(premise, "trTransition")) {
            int rewriteTerm = pp.iTerms.substitute(bindings, thetaLHSFromConfiguration(premise), 0);
            int rewriteRelation = pp.iTerms.getSubterm(premise, 1);
            int rewrittenTerm = rewriteAttempt(rewriteTerm, rewriteRelation, level + 1);
            if (rewrittenTerm < 0) {
              trace(4, level, pp.render(pp.iTerms.getSubterm(ruleLabel, 0)) + "premise" + (premiseNumber + 1) + " failed: seek another rule");
              continue nextRule;
            }
            if (!pp.iTerms.matchZeroSV(rewrittenTerm, pp.iTerms.getSubterm(premise, 2), bindings)) continue nextRule;
          } else
            throw new ARTUncheckedException("Unknown premise kind " + pp.render(premise));
        }
        trace(5, level,
            pp.render(pp.iTerms.getSubterm(ruleLabel, 0)) + "bindings after premise " + (premiseNumber + 1) + " " + bindingsToString(bindings, pp.variableMap));
      }

      term = pp.iTerms.substitute(bindings, rhs, 0);
      trace(level == 1 ? 2 : 3, level, pp.render(pp.iTerms.getSubterm(ruleLabel, 0)) + "rewrites to " + pp.render(term));
      pp.rewriteActive = pp.rewriteResume;

      if (pp.rewriteContractum) {
        trace(5, level, pp.render(pp.iTerms.getSubterm(ruleLabel, 0)) + "rewrites contractum\n");
        term = rewriteTraverse(term, relationTerm, level + 1);
      }
      return term;
    }
    // If we get here, then no rules succeeded
    return term;
  }

  private int thetaFromConfiguration(int term) {
    return pp.iTerms.hasSymbol(term, "trConfiguration") ? pp.iTerms.getSubterm(term, 0) : term;
  }

  private int thetaLHSFromConfiguration(int term) {
    return pp.iTerms.getSubterm(thetaFromConfiguration(term), 0);
  }

  private int rewriteTraverse(int term, int relationTerm, int level) {
    if (isTerminatingConfiguration(term, relationTerm)) {
      trace(3, level + 1, "Found" + pp.render(relationTerm) + "terminal " + pp.render(term));
      return term;
    }

    if (pp.rewriteActive && pp.rewritePreorder) term = rewriteAttempt(term, relationTerm, level);

    if (pp.rewriteTraverse) {
      int[] children = pp.iTerms.getTermChildren(term);
      for (int i = 0; i < pp.iTerms.getTermArity(term); i++)
        if (pp.rewriteActive)
          children[i] = rewriteTraverse(children[i], relationTerm, level + 1);
        else
          break;
    }

    if (pp.rewriteActive && pp.rewritePostorder) term = rewriteAttempt(term, relationTerm, level);

    return term;
  }

  public void stepper() {
    pp.rewriteTraverse = pp.rewritePreorder || pp.rewritePostorder;
    pp.rewriteActive = !pp.rewriteDisable;
    pp.rewriteAttemptCounter = pp.rewriteStepCounter = 0;

    int oldTerm = pp.inputTerm;
    int relation = pp.startRelation;
    int testTerm = pp.resultTerm;
    int newTerm;

    while (true) {
      for (int i : pp.cycleCheck.keySet())
        pp.cycleCheck.get(i).clear();
      pp.rewriteActive = true; // reset rewriteActive flag before attempting traversal
      trace(1, 0, "Step " + ++pp.rewriteStepCounter);
      if (pp.rewritePure)
        newTerm = rewriteAttempt(oldTerm, relation, 1);
      else
        newTerm = rewriteTraverse(oldTerm, relation, 1);
      if (pp.rewriteOneStep || isTerminatingConfiguration(newTerm, relation) || newTerm == oldTerm /* nothing changed */) break;
      oldTerm = newTerm;
    }

    trace(1, 0,
        (isTerminatingConfiguration(newTerm, relation) ? "Normal termination on " : "Stuck on ") + pp.render(newTerm) + " after " + pp.rewriteStepCounter
            + " step" + (pp.rewriteStepCounter == 1 ? "" : "s") + " and " + pp.rewriteAttemptCounter + " rewrite attempt"
            + (pp.rewriteAttemptCounter == 1 ? "" : "s"));

    if (testTerm != 0) {
      if (newTerm == testTerm) {
        System.out.println("Good result");
        pp.goodTest++;
      } else {
        System.out.println("Bad result: expected " + pp.render(testTerm) + " found " + pp.render(newTerm));
        pp.badTest++;
      }
    }
  }

  boolean isTerminatingConfiguration(int term, int relation) {
    int thetaRoot = thetaFromConfiguration(term);
    Set<Integer> terminals = pp.mainModule.rewriteTerminals.get(relation);
    return pp.iTerms.isSpecialTerm(thetaRoot) || (terminals != null && terminals.contains(thetaRoot));
  }
  /* End of rewriter *****************************************************************************************/

}
