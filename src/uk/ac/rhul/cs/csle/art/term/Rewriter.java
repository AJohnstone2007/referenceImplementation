package uk.ac.rhul.cs.csle.art.term;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.util.Util;

public class Rewriter {
  private final ITerms iTerms;
  public final TermTraverserText tt;

  public int rewriteAttemptCounter;
  public int rewriteStepCounter;
  public boolean rewriteDisable = false;
  public boolean rewritePure = true;
  public boolean rewritePreorder = false;
  public boolean rewritePostorder = false;
  public boolean rewriteOneStep = false;
  public boolean rewriteResume = false;
  public boolean rewriteContractum = false;
  private boolean rewriteTraverse = false;
  private boolean rewriteActive = false;
  private final Map<Integer, Set<Integer>> rewriteTerminals = new HashMap<>();
  public int startRelation = 0;
  public Map<Integer, Integer> termToEnumElementMap = new HashMap<>();
  public Map<Integer, Integer> enumElementToTermMap = new HashMap<>();
  public Map<Integer, Integer> termRewriteConstructorDefinitions = new HashMap<>(); // The number of times a constructor appears as the root of a term
  public Map<Integer, Integer> termRewriteConstructorUsages = new HashMap<>(); // The number of times a constructor appears
  public Set<Integer> functionsInUse = new HashSet<>(); // The set of functions in use

  private final Map<Integer, Set<Integer>> cycleCheck = new HashMap<>();

  private final Map<Integer, Map<Integer, List<Integer>>> trRules = new LinkedHashMap<>();
  private final Map<Integer, Map<Integer, Integer>> variableNamesByRule = new HashMap<>(); // Map from term to the variable aliases used in that term
  private final Map<Integer, Map<Integer, Integer>> reverseVariableNamesByRule = new HashMap<>(); // Map from term to the variable aliases used in that term
  private Map<Integer, Integer> variableMap;

  public Rewriter(ITerms iTerms, TermTraverserText tt) {
    this.iTerms = iTerms;
    this.tt = tt;
  }

  public String trRulesToString(Map<Integer, Map<Integer, List<Integer>>> trRules) {
    StringBuilder sb = new StringBuilder();
    for (int rel : trRules.keySet())
      for (int c : trRules.get(rel).keySet())
        for (int r : trRules.get(rel).get(c)) {
          sb.append(tt.toString(r));
          sb.append("\n");
        }
    return sb.toString();
  }

  private String bindingsToString(int[] bindings, Map<Integer, Integer> variableMap) {
    StringBuilder sb = new StringBuilder();
    boolean seen = false;
    sb.append("{ ");
    for (int i = 0; i < bindings.length; i++) {
      if (bindings[i] > 0) {
        if (seen) sb.append(", ");
        sb.append(render(iTerms.findTerm("_" + i)) + "=" + render(bindings[i]));
        seen = true;
      }
    }
    sb.append(" }");
    return sb.toString();
  }

  private int rewriteAttempt(int term, int relationTerm, int level) { // return rewritten term and set rewriteAttemptOutcome

    if (relationTerm == 0) Util.fatal("ESOS rewrite attempted on null relation");

    Util.trace(3, level, "Rewrite attempt " + ++rewriteAttemptCounter + ": " + render(term) + render(relationTerm));
    Util.trace(3, level, "Rewrite call " + ++rewriteAttemptCounter + " " + render(term) + render(relationTerm));
    if (!cycleCheck.containsKey(relationTerm)) cycleCheck.put(relationTerm, new HashSet<Integer>());
    Set<Integer> cycleSet = cycleCheck.get(relationTerm);
    // if (cycleSet.contains(configuration)) throw new ARTExceptionFatal("cycle detected " +iTerms.toString(configuration) +iTerms.toString(relationTerm));
    cycleSet.add(term);

    if (isTerminatingConfiguration(term, relationTerm)) {
      Util.trace(3, level + 1, "Terminal " + tt.toString(term));
      return term;
    }

    int rootTheta = thetaFromConfiguration(term);
    Map<Integer, List<Integer>> rulesForThisRelation = trRules.get(relationTerm);

    List<Integer> ruleList = null;

    if (rulesForThisRelation != null) { // There may be no rules at all in this relation!
      ruleList = rulesForThisRelation.get(iTerms.getTermSymbolIndex(rootTheta));
      if (ruleList == null) ruleList = rulesForThisRelation.get(iTerms.findString("")); // Deafult rules
    }

    if (ruleList == null) {
      Util.trace(3, level, "No rules in" + render(relationTerm) + "for constructor " + iTerms.getTermSymbolString(rootTheta));
      return term;
    }

    nextRule: for (int ruleIndex : ruleList) {
      variableMap = reverseVariableNamesByRule.get(ruleIndex);
      // System.out.println("Variable map: ");
      // for (int i : variableMap.keySet())
      // System.out.println(i + ":" + iTerms.getString(variableMap.get(i)));
      Util.trace(3, level, tt.toString(ruleIndex, false, -1, variableMap)); // Announce the next rule we are going to try
      int lhs = iTerms.getSubterm(ruleIndex, 1, 1, 0);
      int premises = iTerms.getSubterm(ruleIndex, 1, 0);
      int premiseCount = iTerms.getTermArity(premises);
      int rhs = iTerms.getSubterm(ruleIndex, 1, 1, 2);
      int[] bindings = new int[ITerms.variableCount];

      int ruleLabel = thetaFromConfiguration(ruleIndex);
      if (!iTerms.matchZeroSV(term, lhs, bindings)) {
        Util.trace(3, level, render(iTerms.getSubterm(ruleLabel, 0)) + " Theta match failed: seek another rule");
        continue nextRule;
      }
      Util.trace(5, level, render(iTerms.getSubterm(ruleLabel, 0)) + "bindings after Theta match " + bindingsToString(bindings, variableMap));

      // Now work through the premises
      for (int premiseNumber = 0; premiseNumber < premiseCount; premiseNumber++) {
        int premise = iTerms.getSubterm(premises, premiseNumber);
        Util.trace(4, level, render(iTerms.getSubterm(ruleLabel, 0)) + "premise " + (premiseNumber + 1) + " " + render(premise));
        if (iTerms.hasSymbol(premise, "trMatch")) {// |> match expressions
          if (!iTerms.matchZeroSV(iTerms.substitute(bindings, iTerms.getSubterm(premise, 0), 0), iTerms.getSubterm(premise, 1), bindings)) {
            Util.trace(4, level, render(iTerms.getSubterm(ruleLabel, 0)) + "premise " + (premiseNumber + 1) + " failed: seek another rule");
            continue nextRule;
          }
        } else { // transition
          if (iTerms.hasSymbol(premise, "trTransition")) {
            int rewriteTerm = iTerms.substitute(bindings, thetaLHSFromConfiguration(premise), 0);
            int rewriteRelation = iTerms.getSubterm(premise, 1);
            int rewrittenTerm = rewriteAttempt(rewriteTerm, rewriteRelation, level + 1);
            if (rewrittenTerm < 0) {
              Util.trace(4, level, render(iTerms.getSubterm(ruleLabel, 0)) + "premise" + (premiseNumber + 1) + " failed: seek another rule");
              continue nextRule;
            }
            if (!iTerms.matchZeroSV(rewrittenTerm, iTerms.getSubterm(premise, 2), bindings)) continue nextRule;
          } else
            Util.fatal("ESOS - unknown premise kind " + render(premise));
        }
        Util.trace(5, level,
            render(iTerms.getSubterm(ruleLabel, 0)) + "bindings after premise " + (premiseNumber + 1) + " " + bindingsToString(bindings, variableMap));
      }

      term = iTerms.substitute(bindings, rhs, 0);
      Util.trace(level == 1 ? 2 : 3, level, render(iTerms.getSubterm(ruleLabel, 0)) + "rewrites to " + render(term));
      rewriteActive = rewriteResume;

      if (rewriteContractum) {
        Util.trace(5, level, render(iTerms.getSubterm(ruleLabel, 0)) + "rewrites contractum\n");
        term = rewriteTraverse(term, relationTerm, level + 1);
      }
      return term;
    }
    // If we get here, then no rules succeeded
    return term;
  }

  private int thetaFromConfiguration(int term) {
    return iTerms.hasSymbol(term, "trConfiguration") ? iTerms.getSubterm(term, 0) : term;
  }

  private int thetaLHSFromConfiguration(int term) {
    return iTerms.getSubterm(thetaFromConfiguration(term), 0);
  }

  private int rewriteTraverse(int term, int relationTerm, int level) {
    if (isTerminatingConfiguration(term, relationTerm)) {
      Util.trace(3, level + 1, "Found" + render(relationTerm) + "terminal " + render(term));
      return term;
    }

    if (rewriteActive && rewritePreorder) term = rewriteAttempt(term, relationTerm, level);

    if (rewriteTraverse) {
      int[] children = iTerms.getTermChildren(term);
      for (int i = 0; i < iTerms.getTermArity(term); i++)
        if (rewriteActive)
          children[i] = rewriteTraverse(children[i], relationTerm, level + 1);
        else
          break;
    }

    if (rewriteActive && rewritePostorder) term = rewriteAttempt(term, relationTerm, level);

    return term;
  }

  public int stepper(int inputTerm) {
    rewriteTraverse = rewritePreorder || rewritePostorder;
    rewriteActive = !rewriteDisable;
    rewriteAttemptCounter = rewriteStepCounter = 0;

    int oldTerm = inputTerm;
    int relation = startRelation;
    int newTerm;

    while (true) {
      for (int i : cycleCheck.keySet())
        cycleCheck.get(i).clear();
      rewriteActive = true; // reset rewriteActive flag before attempting traversal
      Util.trace(1, 0, "Step " + ++rewriteStepCounter);
      if (rewritePure)
        newTerm = rewriteAttempt(oldTerm, relation, 1);
      else
        newTerm = rewriteTraverse(oldTerm, relation, 1);
      if (rewriteOneStep || isTerminatingConfiguration(newTerm, relation) || newTerm == oldTerm /* nothing changed */) break;
      oldTerm = newTerm;
    }

    Util.trace(1, 0, (isTerminatingConfiguration(newTerm, relation) ? "Normal termination on " : "Stuck on ") + render(newTerm) + " after " + rewriteStepCounter
        + " step" + (rewriteStepCounter == 1 ? "" : "s") + " and " + rewriteAttemptCounter + " rewrite attempt" + (rewriteAttemptCounter == 1 ? "" : "s"));
    return newTerm;
  }

  boolean isTerminatingConfiguration(int term, int relation) {
    int thetaRoot = thetaFromConfiguration(term);
    Set<Integer> terminals = rewriteTerminals.get(relation);
    return iTerms.isSpecialTerm(thetaRoot) || (terminals != null && terminals.contains(thetaRoot));
  }

  public String render(int term) {
    return tt.toString(term, variableMap);
  }

  public void normaliseAndStaticCheckRewriteRules() {
    Map<Integer, Integer> constructorCount = new HashMap<>(); // The number of defined rules for each constructor Map<Integer, Integer>

    // Stage one - collect information
    termRewriteConstructorDefinitions.put(iTerms.findString("_"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("_*"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("->"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("->*"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("->>"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("=>"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("=>*"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("=>>"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("~>"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("~>*"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("~>>"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("true"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("false"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("trLabel"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("trTransition"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("trMatch"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("trPremises"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("tr"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("trConfiguration"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("trRule"), 1);

    termRewriteConstructorDefinitions.put(iTerms.findString("TRRELATION"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("trPrimaryTerm"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("trEntityReferences"), 1);

    // System.out.println("IndexToTerm:" + ((ITermsLowLevelAPI) iTerms).getIndexToTerm());
    for (Integer scanRelationIndex : trRules.keySet()) { // Step through the relations
      // System.out.println("Scanning rules for relation " + tt.toString(scanRelationIndex));

      // Note: rule root is a symbol not a term

      for (Integer ruleRoot : trRules.get(scanRelationIndex).keySet()) { // Step through constructor symbol strings
        // System.out.println("Processing constructor " + iTerms.getString(ruleRoot));
        if (ruleRoot == iTerms.findString("")) { // Add in the 'empty' constructer rules at the end of this list
          List<Integer> emptyConstructorSet = trRules.get(scanRelationIndex).get(iTerms.findString(""));
          if (emptyConstructorSet != null) for (Integer emptyConstructorRule : emptyConstructorSet) {
            trRules.get(scanRelationIndex).get(ruleRoot).add(emptyConstructorRule);
            // System.out.println("Adding empty constructor rule " + tt.toString(emptyConstructorRule));
          }
        }
        // Collect the map of rules for this relation
        for (Integer ruleIndex : trRules.get(scanRelationIndex).get(ruleRoot)) {// Step through the list of rules
          if (termRewriteConstructorDefinitions.get(ruleRoot) == null)
            termRewriteConstructorDefinitions.put(ruleRoot, 1);
          else
            termRewriteConstructorDefinitions.put(ruleRoot, termRewriteConstructorDefinitions.get(ruleRoot) + 1);

          reportInvalidFunctionCallsRec(ruleIndex, iTerms.getSubterm(ruleIndex, 1, 1, 0));

          Map<Integer, Integer> variableNumbers = new HashMap<>();
          Set<Integer> numericVariablesInUse = new HashSet<>(); // The set of functions in use
          nextFreeVariableNumber = 2;
          collectVariablesAndConstructorsRec(ruleIndex, variableNumbers, constructorCount, functionsInUse, numericVariablesInUse, ruleIndex);

          if (numericVariablesInUse.size() > 0 && variableNumbers.size() > 0)
            System.out.println("*** Error - mix of numeric and alphanumeric variables in " + tt.toString(ruleIndex));
          for (int v : numericVariablesInUse)
            if (!iTerms.isVariableSymbol(v))
              System.out.println("*** Error - variable outside available range of _1 to _" + ITerms.variableCount + " in " + tt.toString(ruleIndex));
          if (variableNumbers.size() > ITerms.variableCount)
            System.out.println("*** Error - more than " + ITerms.variableCount + " variables used in " + tt.toString(ruleIndex));

          Map<Integer, Integer> reverseVariableNumbers = new HashMap<>();
          for (int v : variableNumbers.keySet())
            reverseVariableNumbers.put(variableNumbers.get(v), v);
          variableNamesByRule.put(ruleIndex, variableNumbers);
          reverseVariableNamesByRule.put(ruleIndex, reverseVariableNumbers);
        }
      }
    }
    for (int c : constructorCount.keySet())
      if (termRewriteConstructorDefinitions.get(c) == null) {

        String label = iTerms.getString(c);

        if (label.charAt(0) == '"') continue;

        boolean isNumber = true;
        int i = 0;
        if (label.charAt(i) == '-') i++;
        for (; i < label.length(); i++) {
          char ch = label.charAt(i);
          if (!Character.isDigit(ch) && ch != '.') isNumber = false;
        }

        if (isNumber) continue;

        System.err.println("*** Warning: constructor " + label + " has no rule definitions");
      }
    // Stage two - rewrite the rules to use only only numeric variables to normalise the configurations
    for (int normaliseRelationIndex : trRules.keySet()) { // Step through the relations
      for (Integer thetaRoot : trRules.get(normaliseRelationIndex).keySet()) { // Collect the map of rules for this relation
        List<Integer> newRuleList = new LinkedList<>();
        for (Integer ruleIndex : trRules.get(normaliseRelationIndex).get(thetaRoot)) {// Step through the list of rules
          // System.out.println("Normalising rule: " +tt.toString(ruleIndex, null));
          int rewrittenRule = normaliseRuleRec(ruleIndex, variableNamesByRule.get(ruleIndex));
          // System.out.println("Rewritten to: " +tt.toString(rewrittenRule, null));
          newRuleList.add(rewrittenRule);
          // Add in map entries for the rewritten term, which will be the same as for the original rule!
          variableNamesByRule.put(rewrittenRule, variableNamesByRule.get(ruleIndex));
          reverseVariableNamesByRule.put(rewrittenRule, reverseVariableNamesByRule.get(ruleIndex));
        }
        trRules.get(normaliseRelationIndex).put(thetaRoot, newRuleList);
      }
    }
  }

  /* Variable and function mapping ****************************************************************************/
  private int unlabeledRuleNumber = 1;

  private int normaliseRuleRec(Integer ruleIndex, Map<Integer, Integer> variableNameMap) {
    // System.out.println("normaliseRuleRec at " + iTerms.toString(ruleIndex));
    int arity = iTerms.getTermArity(ruleIndex);
    int ruleStringIndex = iTerms.getTermSymbolIndex(ruleIndex);
    // Special case processing for unlabelled rules - generate a label ofthe form Rx
    if (arity == 0 && iTerms.hasSymbol(ruleIndex, "trLabel")) {
      // System.out.println("Generating new label R" + unlabeledRuleNumber);
      int[] newChildren = new int[1];
      newChildren[0] = iTerms.findTerm("R" + unlabeledRuleNumber++);
      return iTerms.findTerm(ruleStringIndex, newChildren);
    }

    int[] newChildren = new int[arity];

    if (variableNameMap.get(ruleStringIndex) != null) {
      // System.out.println(" rewriting " + iTerms.getString(ruleStringIndex) + " to " + iTerms.getString(variableNameMap.get(ruleStringIndex)));
      ruleStringIndex = variableNameMap.get(ruleStringIndex);
    }

    for (int i = 0; i < arity; i++)
      newChildren[i] = normaliseRuleRec(iTerms.getSubterm(ruleIndex, i), variableNameMap);

    return iTerms.findTerm(ruleStringIndex, newChildren);
  }

  private int nextFreeVariableNumber = 1;

  private void collectVariablesAndConstructorsRec(int parentRewriteTermIndex, Map<Integer, Integer> variableNumbers, Map<Integer, Integer> constructorCount,
      Set<Integer> functionsInUse, Set<Integer> numericVariablesInUse, Integer termIndex) {
    // System.out.println("collectVariablesAndConstructorsRec() at " +tt.toString(termIndex, null));

    int termStringIndex = iTerms.getTermSymbolIndex(termIndex);
    if (iTerms.hasSymbol(termIndex, "trLabel")) return; // Do not go down into labels
    String termSymbolString = iTerms.getTermSymbolString(termIndex);

    if (termSymbolString.length() > 1 && termSymbolString.charAt(0) == '_' && termSymbolString.charAt(1) != '_') { // Variable
      if (iTerms.getTermArity(termIndex) > 0)
        System.out.println("*** Error: non-leaf variable " + termSymbolString + " in " + tt.toString(parentRewriteTermIndex));
      boolean isNumeric = true;
      for (int i = 1; i < termSymbolString.length(); i++)
        if (termSymbolString.charAt(i) < '0' || termSymbolString.charAt(i) > '9') isNumeric = false;
      if (isNumeric) {
        // System.out.println("Updating numericVariablesInUse with " + termSymbolString);
        numericVariablesInUse.add(termStringIndex);
      } else if (variableNumbers.get(termStringIndex) == null) {
        // System.out.println("Updating variableNumbers with " + termSymbolString + " mapped to " + nextFreeVariableNumber);
        // variableNumbers.put(nextFreeVariableNumber++, termStringIndex);
        variableNumbers.put(termStringIndex, nextFreeVariableNumber++);
      }
    } else if (termSymbolString.length() > 1 && termSymbolString.charAt(0) == '_' && termSymbolString.charAt(1) == '_') { // Function
      // System.out.println("Updating functionsInUse with " + termSymbolString);
      functionsInUse.add(termStringIndex);
    } else { // Normal constructor
      if (constructorCount.get(termStringIndex) == null) constructorCount.put(termStringIndex, 0);
      // System.out.println("Updating constructor counts for " + iTerms.getString(termStringIndex));
      constructorCount.put(termStringIndex, constructorCount.get(termStringIndex) + 1);
    }

    for (int i = 0; i < iTerms.getTermArity(termIndex); i++)
      collectVariablesAndConstructorsRec(parentRewriteTermIndex, variableNumbers, constructorCount, functionsInUse, numericVariablesInUse,
          iTerms.getSubterm(termIndex, i));
  }

  private void reportInvalidFunctionCallsRec(int parentRewriteTermIndex, int termIndex) {
    String termSymbolString = iTerms.getTermSymbolString(termIndex);
    int termStringIndex = iTerms.getTermSymbolIndex(termIndex);
    if (termSymbolString.length() > 0 && termSymbolString.charAt(0) != '_') {
      if (termRewriteConstructorUsages.get(termStringIndex) == null)
        termRewriteConstructorUsages.put(termStringIndex, 1);
      else
        termRewriteConstructorUsages.put(termStringIndex, termRewriteConstructorUsages.get(termStringIndex) + 1);
    }

    for (int i = 0; i < iTerms.getTermArity(termIndex); i++)
      reportInvalidFunctionCallsRec(parentRewriteTermIndex, iTerms.getSubterm(termIndex, i));
  }
  /* End of variable and function mapping ****************************************************************************/

  public int rewrite(int currentDerivationTerm) {
    if (currentDerivationTerm != 0 && startRelation != 0) {
      // System.out.println(
      // "Rewriting under relation " + iTerms.getTermSymbolString(iTerms.getSubterm(startRelation, 0)) + " " + iTerms.toString(currentDerivationTerm));
      normaliseAndStaticCheckRewriteRules();
      // System.out.println("inputTerm " + iTerms.toString(inputTerm));
      // System.out.println("startRelation " + iTerms.toString(startRelation));
      return stepper(currentDerivationTerm);
    } else
      return currentDerivationTerm;
  }
}
