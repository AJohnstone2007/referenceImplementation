
package uk.ac.rhul.cs.csle.art.old.core;

import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.term.ITermsLowLevelAPI;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.old.v3.manager.parser.ARTV4Lexer;
import uk.ac.rhul.cs.csle.art.old.v3.manager.parser.ARTV4Parser;

public class ARTV4 {
  private final PipelineParamaters pp = new PipelineParamaters(new ITermsLowLevelAPI());

  private Module currentModule;

  public ARTV4(String specification) {
    // 1. Create initial main module
    currentModule = findModule(0);

    // 2. Load traverser tables
    loadModuleBuilderTraverser();
    loadTextTraverser();
    loadLaTeXTraverser();

    // 2a. Debug - load text traverser default action to print message if we encounter an unknown constructor
    // tt.addOp(-1, (Integer t) -> tt.append("??" + iTerms.toString(t) + "?? "), null, null);
    pp.tt.addAction(-1, (Integer t) -> { // Load default actions

      // Preorder
      pp.tt.appendAlias(pp.iTerms.getTermSymbolIndex(t));
      if (pp.iTerms.getTermArity(t) > 0) pp.tt.append("(");
    },

        // Inorder
        (Integer t) -> {
          pp.tt.append(", ");
        },

        // Postorder
        (Integer t) -> {
          if (pp.iTerms.getTermArity(t) > 0) pp.tt.append(")");
        });

    // 2b. Debug - print keys from text traverser tables
    // System.out.println("text traverser: " + pp.tt);

    // 3. Command line munging to support special cases of art x * and art x y * where neither x nor y start with a !

    int root = parseARTV4("---", specification);
    // System.out.println("parsed command line string:\n" + sb + "\nto term:\n" + pp.iTerms.toString(root));

    // 4. Traverse the term and recursively process merge directives, checking for cycles
    processMergeStaticDirective(root);
    // System.out.println("plain print merged term:\n" + pp.iTerms.toString(root));
    // System.out.println("pretty print merged term:\n" + pp.iTerms.toString(root));

    // 5. Collect modules and their sets and their directive lists, noting default module and default start symbols as we go
    pp.moduleBuilderTraverser.traverse(root);

    // 6. Static checks and normalisation
    if (pp.mainModule == null) pp.mainModule = currentModule; // If no main has been set, then there were no !module directives, so just use the default
    for (Integer m : pp.modules.keySet()) {
      Module module = pp.modules.get(m);
      // module.normaliseAndStaticCheckCFGRules();
      if (pp.verbosityLevel > 1) module.instanceTreePrintDot("instanceTree_" + module.nameTerm + ".dot");
      module.normaliseAndStaticCheckRewriteRules();
    }

    // System.out.println("Debug - print modules:");
    // for (Integer m : pp.modules.keySet())
    // System.out.println(pp.modules.get(m).toString(pp.tt));
    // System.out.println("End debug");

    // 7. Run the script, starting from the directive list in currentModule
    new Pipeline(pp).interpretDynamicDirectives(pp.mainModule);
  }

  Module findModule(int moduleTerm) {
    Module ret = pp.modules.get(moduleTerm);
    if (ret != null)
      throw new ARTUncheckedException("Attempt to redefine " + (moduleTerm == 0 ? " unnamed module" : " module " + pp.iTerms.toString(moduleTerm)));
    pp.modules.put(moduleTerm, (ret = new Module(pp, moduleTerm))); // set up default main module
    return ret;
  }

  ARTV4Parser parser = new ARTV4Parser(new ARTV4Lexer()); // Initialisation of V3 style parsers is suspect so make a new one each time

  int parseARTV4(String specificationFileName, String specificationInput) {
    parser.artParse(specificationInput);
    parser.artDisambiguateOrderedLongest();
    if (!parser.artIsInLanguage) throw new ARTUncheckedException("Syntax error in ART specification for input " + specificationFileName);
    if (parser.computeIsAmbiguous(null)) throw new ARTUncheckedException("Internal error: specification grammar is ambiguous");
    return parser.artDerivationAsTerm(pp.iTerms);
  }

  private void loadModuleBuilderTraverser() {
    pp.moduleBuilderTraverser.addActionBreak("directive", (Integer t) -> interpretStaticDirectives(t), null, null);

    pp.moduleBuilderTraverser.addAction("cfgRule", (Integer t) -> currentModule.buildCFGRule(t), null, null);
    pp.moduleBuilderTraverser.addAction("trRule", (Integer t) -> currentModule.buildTRRule(t), null, null);
    pp.moduleBuilderTraverser.addAction("chooseRule", (Integer t) -> currentModule.buildChooseRule(t), null, null);

    pp.moduleBuilderTraverser.addActionBreak("cfgNonterminal", (Integer t) -> currentModule.nonterminals.add(t), null, null);
    pp.moduleBuilderTraverser.addActionBreak("cfgCaseInsensitiveTerminal", (Integer t) -> currentModule.terminals.add(t), null, null);
    pp.moduleBuilderTraverser.addActionBreak("cfgCaseSensitiveTerminal", (Integer t) -> currentModule.terminals.add(t), null, null);
    pp.moduleBuilderTraverser.addActionBreak("cfgCaseCharacterTerminal", (Integer t) -> currentModule.terminals.add(t), null, null);
    pp.moduleBuilderTraverser.addActionBreak("cfgBuiltinTerminal", (Integer t) -> currentModule.terminals.add(t), null, null);
    pp.moduleBuilderTraverser.addActionBreak("cfgCharacterRangeTerminal", (Integer t) -> currentModule.buildCharacterRangeTerminal(t), null, null);
  }

  private void loadTextTraverser() {
    // -1: uncomment these to suppress types have interpreted type renditions
    pp.tt.addEmptyAction("__bool", "__char", "__int32", "__real64", "__string");
    pp.tt.addAction("__map", "{", ", ", "}");

    // 0. Directive and top level pretty print controls
    pp.tt.addEmptyAction("text", "cfgElementDeclarations", "cfgElementDeclaration", "latexDeclarations");
    pp.tt.addActionBreak("directive", (Integer t) -> processDirective(t), null, null);
    pp.tt.addAction("latexDeclaration", null, " = ", null);
    pp.tt.addActionBreak("idART", (Integer t) -> pp.tt.appendAlias(pp.tt.childSymbolIndex(t, 0)), null, null);

    // 1. Context Free Grammar pretty print controls
    pp.tt.addEmptyAction("cfgSlot");

    pp.tt.addAction("cfgCat", null, " ", null);
    pp.tt.addAction("cfgRule", null, "::=\n ", "\n");
    pp.tt.addAction("cfgRHS", null, "\n|", "\n");
    pp.tt.addAction("cfgAlt", null, " | ", null);
    pp.tt.addActionBreak("cfgNonterminal", (Integer t) -> pp.tt.appendAlias(pp.iTerms.getTermSymbolIndex(pp.iTerms.getSubterm(t, 0))), null, null);
    pp.tt.addActionBreak("cfgCaseInsensitiveTerminal", (Integer t) -> pp.tt.appendAlias(pp.iTerms.getTermSymbolIndex(pp.iTerms.getSubterm(t, 0))), null, null);
    pp.tt.addActionBreak("cfgCaseSensitiveTerminal", (Integer t) -> pp.tt.appendAlias(pp.iTerms.getTermSymbolIndex(pp.iTerms.getSubterm(t, 0))), null, null);
    pp.tt.addActionBreak("cfgCharacterTerminal", (Integer t) -> pp.tt.appendAlias(pp.iTerms.getTermSymbolIndex(pp.iTerms.getSubterm(t, 0))), null, null);
    pp.tt
        .addActionBreak("cfgCharacterRangeTerminal",
            (Integer t) -> pp.tt
                .append(pp.iTerms.getTermSymbolIndex(pp.iTerms.getSubterm(t, 0)) + ".." + pp.iTerms.getTermSymbolString(pp.iTerms.getSubterm(t, 1))),
            null, null);
    pp.tt.addAction("cfgOptional", null, null, "?");
    pp.tt.addAction("cfgKleeneClosure", null, null, "*");
    pp.tt.addAction("cfgPositiveClosure", null, null, "+");
    pp.tt.addAction("cfgDoFirst", "(", null, ")");
    pp.tt.addAction("cfgEpsilon", "#", null, null);

    // 2. Chooser pretty print controls
    pp.tt.addEmptyAction("chooseElement");
    pp.tt.addAction("chooseRule", null, null, "\n");
    pp.tt.addAction("chooseHigher", " > ", null, null);
    pp.tt.addAction("chooseLower", " < ", null, null);
    pp.tt.addAction("chooseLonger", " >> ", null, null);
    pp.tt.addAction("chooseShorter", " << ", null, null);
    pp.tt.addAction("chooseDiff", "(", " \\ ", ")");
    pp.tt.addAction("chooseUnion", "(", " | ", ")");
    pp.tt.addAction("chooseIntersection", "(", " / ", ")");
    pp.tt.addActionBreak("choosePredefinedSet", (Integer t) -> pp.tt.append(pp.tt.childStrippedSymbolString(t, 0)), null, null);

    // 3. Term rewrite pretty print controls
    pp.tt.addEmptyAction("trRule");
    pp.tt.addAction("tr", null, " --- ", null);
    pp.tt.addAction("trPremises", null, "    ", null);
    pp.tt.addActionBreak("trLabel", (Integer t) -> pp.tt.append(pp.iTerms.getTermArity(t) > 0 ? ("-" + pp.tt.childSymbolString(t, 0) + " ") : " "), null, null);
    pp.tt.addAction("trMatch", null, (Integer t) -> pp.tt.append(" |> "), null);
    pp.tt.addEmptyAction("trTransition");
    pp.tt.addActionBreak("TRRELATION", (Integer t) -> pp.tt.append(" " + pp.tt.childStrippedSymbolString(t, 0) + " "), null, null);
    // pp.tt.addAction("trConfiguration", "<", ", ", ">");
    pp.tt.addAction("trConfiguration", null, ", ", null);
    pp.tt.addActionBreak("trTerm", (Integer t) -> pp.tt.append(pp.iTerms.toString(pp.iTerms.getSubterm(t, 0))), null, null);
    pp.tt.addAction("trEntityReferences", ", ", ", ", null);
    pp.tt.addActionBreak("trNamedTerm",
        (Integer t) -> pp.tt.append(pp.iTerms.toString(pp.iTerms.getSubterm(t, 0)) + " = " + pp.iTerms.toString(pp.iTerms.getSubterm(t, 1))), null, null);

    // 4. Attribute equation pretty print controls - not fully designed yet
  }

  private void processDirective(Integer t) {
    pp.tt.append(pp.tt.childStrippedSymbolString(t, 0) + " ");
    if (pp.iTerms.getTermArity(t) == 2) pp.tt.traverse(pp.iTerms.getSubterm(t, 1));
  }

  private void loadLaTeXTraverser() {
  }

  public void interpretStaticDirectives(int term) {
    // System.out.println("Building directive " + pp.iTerms.toString(term));
    int firstChild = pp.iTerms.getTermChildren(term)[0];
    int secondChild = 0;
    String firstChildString = pp.iTerms.getTermSymbolString(firstChild);
    if (pp.iTerms.getTermChildren(term).length > 1) secondChild = pp.iTerms.getTermChildren(term)[1];

    // Either directly handle static directives, or add to module dynamicDirectives list
    if (firstChildString.equals("'module'")) {
      currentModule = findModule(secondChild);
    } else if (firstChildString.equals("'use'")) {
      int[] children = pp.iTerms.getTermChildren(secondChild);
      for (int i = 0; i < children.length; i++)
        currentModule.useModules.add(children[i]);
    } else if (firstChildString.equals("'paraterminal'")) {
      System.out.println("Updating paraterminals with: " + pp.iTerms.toString(term));
      int[] children = pp.iTerms.getTermChildren(term);
      for (int i = 1; i < children.length; i++) {
        System.out.println("Putting paraterminals: " + pp.iTerms.toString(children[i]));
        int alias;
        if (pp.iTerms.getTermArity(children[i]) == 1)
          alias = 0;
        else
          alias = pp.iTerms.getSubterm(children[i], 1);
        currentModule.paraterminals.put(pp.iTerms.getSubterm(children[i], 0), alias);
      }
    } else if (firstChildString.equals("cfgElements")) {
      int[] children = pp.iTerms.getTermChildren(secondChild);
      for (int i = 0; i < children.length; i++)
        if (pp.iTerms.getTermSymbolString(pp.iTerms.getTermChildren(children[i])[0]).equals("cfgNonterminal"))
          currentModule.nonterminals.add(children[i]);
        else
          currentModule.terminals.add(children[i]);
    } else if (firstChildString.equals("latex")) {
      int[] children = pp.iTerms.getTermChildren(secondChild);
      for (int i = 0; i < children.length; i++)
        pp.latexAliases.put(strip(pp.iTerms.getTermSymbolString(pp.iTerms.getSubterm(children[i], 0, 0))),
            strip(pp.iTerms.getTermSymbolString(pp.iTerms.getSubterm(children[i], 1, 0))));
    } else
      pp.dynamicDirectives.add(term);

  }

  String strip(String str) {
    return str.substring(1, str.length() - 1);
  }

  /* A hand crafted rewriter that does preOrder recursive rewriting of !merge x to the contents of file x, checking for recursion */
  private final int mergePattern = pp.iTerms.findTerm("directive('merge', _)"); // use wildcard in pattern for ID and iTerms.subTerm to pull it out
  Set<Integer> mergeAncestors = new HashSet<>(); // A set of previously merged terms (not filenames!)

  private int processMergeStaticDirective(int root) {
    int newRoot, oldRoot = root;
    mergeAncestors.clear();
    while (true)
      if ((newRoot = processMerges(oldRoot)) == oldRoot)
        return newRoot;
      else
        oldRoot = newRoot;
  }

  private int processMerges(int root) {
    int[] children = pp.iTerms.getTermChildren(root);

    for (int i = 0; i < children.length; i++) {

      if (pp.iTerms.matchZeroSV(children[i], mergePattern, null)) { // match? No bindings needed as only wildcard in pattern
        String filename = pp.iTerms.getTermSymbolString(pp.iTerms.getSubterm(root, 0, 1, 0)) + ".art"; // Note parser will have removed file type

        // System.out.println("Merging " + filename);
        int parsedTerm = parseARTV4(filename, ARTText.readFile(filename));

        if (mergeAncestors.contains(parsedTerm)) throw new ARTUncheckedException("recursive !merge content on file " + filename);

        mergeAncestors.add(parsedTerm); // Push parsed term to ancestor set
        children[i] = processMerges(parsedTerm); // recurse
        mergeAncestors.remove(parsedTerm); // Pop parsed term from ancestor set - it may be used further along
      } else
        processMerges(children[i]); // all done, so next sibling
    }

    return pp.iTerms.findTerm(pp.iTerms.getTermSymbolIndex(root), children);
  }
  /* End of !merge rewriter ******************************************************************************************/
}
