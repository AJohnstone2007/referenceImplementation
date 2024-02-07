package uk.ac.rhul.cs.csle.art.old.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.term.ITerms;
import uk.ac.rhul.cs.csle.art.old.term.TermTraverserText;

public class Module {
  /**
  *
  */
  public final ITerms iTerms;
  public final TermTraverserText tt;
  public final int nameTerm;
  public int defaultStartNonterminal = 0; // This is set by the first Context Free Grammar rule encountered
  public int defaultStartRelation = 0; // This is set by the first term rewrite rule encountered
  public int slotCount = 0;

  public final Set<Integer> useModules = new LinkedHashSet<>();
  public final Set<Integer> terminals = new LinkedHashSet<>();
  public final Set<Integer> nonterminals = new LinkedHashSet<>();
  public final Map<Integer, Integer> paraterminals = new LinkedHashMap<>(); // A map of paraterminals names to aliases

  final Map<Integer, List<Integer>> cfgRules = new LinkedHashMap<>(); // Map of LHS term to list of !SHARED! RHS terms over grammar elements
  final Map<Integer, List<Integer>> cfgInstances = new LinkedHashMap<>(); // Map of LHS term to !NONSHARED! list of RHS terms over instance numbers
  // final Map<Integer, InstanceAttributes> cfgInstanceMap = new LinkedHashMap<>(); // Map from instance terms to rule terms
  final int cfgInstanceRoot;
  final int epsilon;

  public final Map<Integer, Map<Integer, List<Integer>>> trRules = new LinkedHashMap<>();
  final Set<Integer> chooseRules = new LinkedHashSet<>();

  final Map<Integer, Integer> termToEnumElementMap = new HashMap<>();
  final Map<Integer, Integer> enumElementToTermMap = new HashMap<>();
  final Map<Integer, Integer> termRewriteConstructorDefinitions = new HashMap<>(); // The number of times a constructor appears as the root of a term
  final Map<Integer, Integer> termRewriteConstructorUsages = new HashMap<>(); // The number of times a constructor appears

  final Set<Integer> functionsInUse = new HashSet<>(); // The set of functions in use

  public final Map<Integer, Map<Integer, Integer>> variableNamesByRule = new HashMap<>(); // Map from term to the variable aliases used in that term
  public final Map<Integer, Map<Integer, Integer>> reverseVariableNamesByRule = new HashMap<>(); // Map from term to the variable aliases used in that term

  public Module(PipelineParamaters pp, int nameTerm) {
    this.iTerms = pp.iTerms;
    this.tt = pp.tt;
    this.nameTerm = nameTerm;
    if (pp.mainModule == null) pp.mainModule = this;
    if (pp.mainModule.isEmpty()) pp.mainModule = this;
    epsilon = iTerms.findTerm("cfgEpsilon");
    cfgInstanceRoot = 0;
  }

  private boolean isEmpty() {
    return useModules.isEmpty() && terminals.isEmpty() && nonterminals.isEmpty() && paraterminals.isEmpty() && getCfgRules().isEmpty() && trRules.isEmpty()
        && chooseRules.isEmpty();
  }

  public String toString(TermTraverserText tt) {
    StringBuilder sb = new StringBuilder();

    if (nameTerm == 0)
      sb.append("(* Unnamed module *)\n\n");
    else
      sb.append("(* Module " + tt.toString(nameTerm) + " *)\n\n!module " + tt.toString(nameTerm) + "\n\n");

    sb.append(defaultStartNonterminal == 0 ? "(* No start nonterminal *)\n" : "!start " + tt.toString(defaultStartNonterminal) + "\n");
    sb.append(defaultStartRelation == 0 ? "(* No start relation *)\n\n" : "!start " + tt.toString(defaultStartRelation) + "\n\n");

    sb.append("(* Uses *)\n");
    for (Integer t : useModules)
      sb.append(tt.toString(t) + "\n");
    sb.append("(* Terminals *)\n");
    for (Integer t : terminals)
      sb.append(tt.toString(t) + "\n");
    sb.append("(* Paraterminals *)\n");
    for (Integer n : paraterminals.keySet())
      sb.append(tt.toString(n) + " = " + "\n");
    sb.append("(* Nonterminals *)\n");
    for (Integer n : nonterminals)
      sb.append(tt.toString(n) + "\n");
    sb.append("(* Context Free Grammar rules *)\n");
    for (Integer lc : getCfgRules().keySet()) {
      sb.append(tt.toString(lc) + " ::=\n ");
      for (Integer c : getCfgRules().get(lc))
        sb.append(tt.toString(c) + "\n");
    }
    sb.append("(* Choose rules *)\n");
    for (Integer c : chooseRules)
      sb.append(tt.toString(c));

    sb.append("(* Term rewrite rules *)\n");
    for (Integer rel : trRules.keySet()) {
      sb.append("  (* Relation " + tt.toString(rel) + " *)\n");
      for (Integer cons : trRules.get(rel).keySet()) {
        sb.append("    (* Constructor " + iTerms.getString(cons) + " *)\n");
        for (Integer rule : trRules.get(rel).get(cons))
          sb.append(tt.toString(rule));
      }
    }
    sb.append("(* Attribute equations *)\n");

    if (nameTerm == 0)
      sb.append("(* End of unnamed module *)\n\n");
    else
      sb.append("(* End of module " + tt.toString(nameTerm) + " *)\n\n");
    return sb.toString();
  }

  public Integer termAsEnumElement(Integer termIndex) {
    Integer ret = termToEnumElementMap.get(termIndex);
    if (ret == null) throw new ARTUncheckedException("attempt to map term which is not in enumeration");
    return ret;
  }

  public Integer enumElementAsTerm(Integer enumElement) {
    Integer ret = enumElementToTermMap.get(enumElement);
    if (ret == null) throw new ARTUncheckedException("attempt to enum element which is not in enumeration");
    return ret;
  }

  /* Preprocess Context Free Grammar rules **********************************************************************************************/
  // int instanceNumber;

  // public void normaliseAndStaticCheckCFGRules() {
  // // 1. Create instance term versions of the rule terms
  // instanceNumber = 1;
  // cfgInstanceMap.clear();
  // cfgInstances.clear();
  //
  // for (Integer n : cfgRules.keySet()) {
  // if (cfgInstances.get(n) == null) cfgInstances.put(n, new LinkedList<>());
  // for (Integer p : cfgRules.get(n)) {
  // cfgInstances.get(n).add(constructInstanceTermRec(p));
  // cfgInstanceMap.put(instanceNumber, new InstanceAttributes());
  // cfgInstanceMap.get(instanceNumber++).ruleTerm = p;
  // }
  // }
  //
  // // 2. Compute first and follow sets
  // boolean changed = true;
  // while (changed) {
  // changed = false;
  // changed |= (computeSetsRec(cfgInstanceRoot, 0, null, 0));
  // }
  //
  // }
  //
  // // Traverse production term p, making a matching instance term i
  // private Integer constructInstanceTermRec(Integer p) {
  // final int ourInstanceNumber = instanceNumber++;
  //
  // cfgInstanceMap.put(ourInstanceNumber, new InstanceAttributes());
  // cfgInstanceMap.get(ourInstanceNumber).ruleTerm = p;
  //
  // int[] pChildren = iTerms.getTermChildren(p);
  // int[] ourChildren = new int[iTerms.getTermArity(p)];
  // for (int c = 0; c < ourChildren.length; c++)
  // ourChildren[c] = constructInstanceTermRec(pChildren[c]);
  //
  // return iTerms.findTerm("" + ourInstanceNumber, ourChildren);
  // }
  //
  // private boolean computeSetsRec(int node, int level, ARTGrammarElementNonterminal lhs, int bracketNode) {
  // boolean changed = false;
  // //
  // // if (node == 0) return changed;
  // //
  // // // artManager.text.printf("artComputesetsRec() visiting node %d at level %d with lhs %s and bracketNode %s\n", node.getKey(), level, lhs, bracketNode);
  // //
  // // changed |= (computeSetsRec(node.getSibling(), level, lhs, bracketNode));
  // //
  // // int newBracketNode = bracketNode;
  // //
  // // if (iTerms.hasSymbol(node, "cfgDoFirst") || iTerms.hasSymbol(node, "cfgOptional") || iTerms.hasSymbol(node, "cfgPositiveClosure") ||
  // // iTerms.hasSymbol(node, "cfgKleeneClosure"))
  // // newBracketNode = node;
  // //
  // // if (iTerms.hasSymbol(node, "cfgRoot")) {
  // // ; // Nothing to do
  // // }
  // //
  // // else if (iTerms.hasSymbol(node, "cfgLHS")) {
  // // changed |= node.follow.addAll(((ARTGrammarElementNonterminal) node.getPayload()).getFollow());
  // // for (ARTGrammarInstance tmp = node.getChild(); tmp != null; tmp = tmp.getSibling())
  // // changed |= node.first.addAll(tmp.first);
  // //
  // // changed |= (((ARTGrammarElementNonterminal) node.getPayload()).getFirst()).addAll(node.first);
  // // }
  // //
  // // else if (iTerms.hasSymbol(node, "cfgAlt")) {
  // // for (ARTGrammarInstance tmp = node.getChild(); tmp != null; tmp = tmp.getSibling())
  // // changed |= node.first.addAll(tmp.first);
  // // }
  // //
  // // else if (iTerms.hasSymbol(node, "cfgCat")) {
  // // // Walk the children of a cat node until we find a non-nullable symbol skipping slot nodes
  // // for (ARTGrammarInstance child = node.getChild().getSibling(); child != null; child = child.getSibling().getSibling()) {
  // // HashSet<ARTGrammarElement> tmp = new HashSet<ARTGrammarElement>(child.first);
  // // if (child.getSibling().getSibling() != null) tmp.remove(epsilon);
  // //
  // // changed |= node.first.addAll(tmp);
  // // if (!child.first.contains(epsilon)) break;
  // // }
  // // }
  // //
  // // else if (iTerms.hasSymbol(node, "cfgSlot")) {
  // // if (node.getSibling() == null) { // \beta is \epsilon
  // // if (newBracketNode == 0)
  // // changed |= node.first.add(epsilon);
  // // else {
  // // changed |= node.first.addAll(newBracketNode.getSibling().first); // fold in follow for this bracket
  // // if (newBracketNode instanceof ARTGrammarInstanceKleeneClosure || newBracketNode instanceof ARTGrammarInstancePositiveClosure)
  // // changed |= node.first.addAll(newBracketNode.first);
  // // }
  // // } else { // \beta is not epsilon so there will be an X (a terminal or a nonterminal) following, then another pos slot
  // // HashSet<ARTGrammarElement> tmp = new HashSet<ARTGrammarElement>(node.getSibling().first);
  // // tmp.remove(epsilon);
  // //
  // // changed |= node.first.addAll(tmp);
  // //
  // // if (node.getSibling().first.contains(epsilon)) changed |= node.first.addAll(node.getSibling().getSibling().first); // bring over first (alpha X . beta)
  // // }
  // //
  // // // Guard set computation for slots
  // // HashSet<ARTGrammarElement> tmp = new HashSet<ARTGrammarElement>(node.first);
  // // if (tmp.contains(epsilon)) if (newBracketNode == 0)
  // // tmp.addAll(lhs.getFollow());
  // // else {
  // // tmp.addAll(newBracketNode.getSibling().getGuard());
  // // // For loops, we need the first of the body as well
  // // if (newBracketNode instanceof ARTGrammarInstanceKleeneClosure || newBracketNode instanceof ARTGrammarInstancePositiveClosure)
  // // tmp.addAll(newBracketNode.getGuard());
  // // }
  // //
  // // tmp.remove(epsilon);
  // // changed |= node.getGuard().addAll(tmp);
  // //
  // // return changed; // Do not recurse into actions!
  // // }
  // //
  // // else if (iTerms.hasSymbol(node, "cfgNonterminal")) {
  // // ARTGrammarElementNonterminal nonterminal = (ARTGrammarElementNonterminal) node.getPayload();
  // //
  // // changed |= node.first.addAll(nonterminal.getFirst());
  // //
  // // HashSet<ARTGrammarElement> tmp = new HashSet<ARTGrammarElement>();
  // // tmp.addAll(node.getSibling().first);
  // // tmp.remove(epsilon);
  // //
  // // changed |= node.follow.addAll(tmp);
  // //
  // // if (node.getSibling().first.contains(epsilon)) // are we at the end of a rule?
  // // changed |= node.follow.addAll(lhs.getFollow());
  // //
  // // changed |= nonterminal.getFollow().addAll(node.follow);
  // // }
  // //
  // // else if (iTerms.hasSymbol(node, "cfgTerminal")) ; // Nothing to do: first set is computed in constructor for ARTInstanceTerminal
  // //
  // // else if (iTerms.hasSymbol(node, "cfgEpsilon")) ; // Nothing to do: first set is computed in constructor for ARTInstanceEpsilon
  // //
  // // else if (iTerms.hasSymbol(node, "cfgDoFirst") || iTerms.hasSymbol(node, "cfgOptional") || iTerms.hasSymbol(node, "cfgPositiveClosure") ||
  // // iTerms.hasSymbol(node, "cfgKleeneClosure")) {
  // // changed |= node.first.addAll(node.getChild().first);
  // // if (iTerms.hasSymbol(node, "cfgOptional") || iTerms.hasSymbol(node, "cfgKleeneClosure")) changed |= node.first.add(epsilon);
  // // if (!(iTerms.hasSymbol(node, "cfgDoFirst")) {// Do not compute for ( for consistency with V2 although the template does not use them
  // // HashSet<ARTGrammarElement> tmp = new HashSet<ARTGrammarElement>(node.getChild().first);
  // // if (tmp.contains(epsilon)) tmp.addAll(node.getSibling().getGuard());
  // //
  // // tmp.remove(epsilon);
  // // changed |= node.getChild().getGuard().addAll(tmp);
  // //
  // // }
  // // }
  // //
  // // else throw new ARTUncheckedException("unsupported node type "+iTerms.getTermSymbolString(node)+" during set evaluation\n");
  // //
  // // changed|=(computeSetsRec(node.getChild(), level + 1, level == 1 ? (ARTGrammarElementNonterminal) node.getPayload() : lhs,
  // // node.bracketInstance(newBracketNode)));
  // return changed;
  // }

  /* End of preprocess Context Free Grammar rules **********************************************************************************************/

  /* Preprocess term rewriter rules **********************************************************************************************/
  public final Map<Integer, Set<Integer>> rewriteTerminals = new HashMap<>();;

  public void normaliseAndStaticCheckRewriteRules() {
    Map<Integer, Integer> constructorCount = new HashMap<>(); // The number of defined rules for each constructor Map<Integer, Integer>

    // Stage one - collect information
    termRewriteConstructorDefinitions.put(iTerms.findString("_"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("_*"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("'->'"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("'->*'"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("'->>'"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("'=>'"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("'=>*'"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("'=>>'"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("'~>'"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("'~>*'"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("'~>>'"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("True"), 1);
    termRewriteConstructorDefinitions.put(iTerms.findString("False"), 1);
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
          // // String tmp = iTerms.getString(ruleRoot);
          // // System.out.println("Scanning rule: " +tt.toString(ruleIndex, null) + " with rule root " + tmp + " - " + tmp);
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
  /* End of preprocess rewrite rules ***************************************************************************************/

  /* Variable and function mapping ****************************************************************************/
  private int unlabeledRuleNumber = 1;

  private int normaliseRuleRec(Integer ruleIndex, Map<Integer, Integer> variableNameMap) {
    // System.out.println("normaliseRule at " + iTerms.toString(ruleIndex));
    int arity = iTerms.getTermArity(ruleIndex);
    int ruleStringIndex = iTerms.getTermSymbolIndex(ruleIndex);
    String ruleString = iTerms.getString(ruleStringIndex);

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

  /* Module builder support ******************************************************************************************/
  public Map<Integer, Integer> getParaterminals() {
    return paraterminals;
  }

  public void buildCharacterRangeTerminal(int term) {
    String lo = iTerms.getTermSymbolString(iTerms.getSubterm(term, 0));
    String hi = iTerms.getTermSymbolString(iTerms.getSubterm(term, 1));

    char loC = lo.charAt(1);
    char hiC = hi.charAt(1);

    for (char x = loC; x <= hiC; x++)
      terminals.add(iTerms.findTerm("cfgCharacterTerminal(`" + x + ")"));
  }

  public void buildCFGRule(int term) {
    System.out.println("Building CFG rule " + iTerms.toString(term));
    int lhsTerm = iTerms.getSubterm(term, 0), rhsTerm = iTerms.getSubterm(term, 1);
    if (getCfgRules().get(lhsTerm) == null) getCfgRules().put(lhsTerm, new LinkedList<>());
    getCfgRules().get(lhsTerm).add(rhsTerm);
    if (defaultStartNonterminal == 0) defaultStartNonterminal = lhsTerm;
  }

  public void buildTRRule(int term) {
    int relation = iTerms.getSubterm(term, 1, 1, 1);
    int constructorIndex = iTerms.getTermSymbolIndex((iTerms.getSubterm(term, 1, 1, 0, 0)));
    // System.out.println("Building TR rule " + iTerms.toString(term) + "\nwith relation " + iTerms.toString(relation) + "\nand constructor "
    // + iTerms.getString(constructorIndex));
    if (trRules.get(relation) == null) trRules.put(relation, new HashMap<>());
    Map<Integer, List<Integer>> map = trRules.get(relation);
    if (map.get(constructorIndex) == null) map.put(constructorIndex, new LinkedList<>());
    map.get(constructorIndex).add(term);
    if (defaultStartRelation == 0) defaultStartRelation = relation;
  }

  public void buildChooseRule(int term) {
    // System.out.println("Building choose rule " + iTerms.toString(term));
    chooseRules.add(term);
  }
  /* End of module builder support ******************************************************************************************/

  public Map<Integer, List<Integer>> getCfgRules() {
    return cfgRules;
  }

  public void instanceTreePrintDot(String filename) {
    File file = new File(filename);
    // System.out.println("cfgInstanceMap: " + cfgInstanceMap);
    PrintWriter printWriter;
    try {
      printWriter = new PrintWriter(file);
    } catch (FileNotFoundException e) {
      throw new ARTUncheckedException("Unable to open " + filename + " for writing");
    }
    printWriter.println("digraph \"" + filename + "\" {\nnode[fontname=Helvetica fontsize=9 shape=box height = 0 width = 0 margin= 0.04]");
    printWriter.println("graph[ordering=out]");
    printWriter.println("edge[arrowsize = 0.3]");
    printWriter.println("\"-1\"  [label=\"0: root\"]");
    int nonterminalCount = -1;
    for (Integer n : cfgInstances.keySet()) {
      printWriter.println("\"" + nonterminalCount + "\"" + "[label=\"" + nonterminalCount + " " + tt.toString(n) + "\"]");
      printWriter.println("0->\"" + nonterminalCount + "\"");
      for (Integer p : cfgInstances.get(n))
        printWriter.println("\"" + nonterminalCount + "\"->\"" + instanceTreePrintDotRec(printWriter, p) + "\"");
      nonterminalCount--;
    }

    printWriter.print("\n}");
    printWriter.close();
  }

  private int instanceTreePrintDotRec(PrintWriter printWriter, Integer p) {
    int instanceNumber = Integer.parseInt(iTerms.getTermSymbolString(p));
    System.out.println("At instance " + instanceNumber);
    // printWriter.println(
    // "\"" + instanceNumber + "\"" + "[label=\"" + instanceNumber + " " + iTerms.getTermSymbolString(cfgInstanceMap.get(instanceNumber).ruleTerm) + "\"]");

    for (int i = 0; i < iTerms.getTermArity(p); i++)
      printWriter.println("\"" + instanceNumber + "\"->\"" + instanceTreePrintDotRec(printWriter, iTerms.getSubterm(p, i)) + "\"");

    return instanceNumber;
  }

}
