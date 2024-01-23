package uk.ac.rhul.cs.csle.art.v3.alg.gll;

import java.util.HashMap;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.core.ARTUncheckedException;
import uk.ac.rhul.cs.csle.art.core.OLDDirectives;
import uk.ac.rhul.cs.csle.art.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.util.text.ARTTextHandlerSink;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.ARTChooserSet;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.ARTFold;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.element.ARTGrammarElement;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.element.ARTGrammarElementAttribute;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.element.ARTGrammarElementNonterminal;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.element.ARTGrammarElementTerminal;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.element.ARTGrammarElementTerminalBuiltin;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.element.ARTGrammarElementTerminalCaseInsensitive;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.element.ARTGrammarElementTerminalCaseSensitive;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.element.ARTGrammarElementTerminalCharacter;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstance;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstanceAction;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstanceAlt;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstanceCat;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstanceDoFirst;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstanceEpsilon;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstanceKleeneClosure;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstanceLHS;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstanceNonterminal;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstanceOptional;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstancePositiveClosure;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstanceRoot;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstanceSlot;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstanceTerminal;
import uk.ac.rhul.cs.csle.art.v3.manager.mode.ARTModeAlgorithm;
import uk.ac.rhul.cs.csle.art.v3.manager.mode.ARTModeDespatch;
import uk.ac.rhul.cs.csle.art.v3.manager.mode.ARTModeSupport;

public class ARTGLLGenerator {
  boolean evaluatorTrace = false;

  ARTGeneratorTranslate gt;
  ARTGrammar grammar;
  OLDDirectives directives;

  int lhsTemplateCount = 0;
  int epsilonTemplateCount = 0;
  int terminalTemplateCount = 0;
  int nonterminalTemplateCount = 0;
  int concatenationTemplateCount = 0;
  int alternateTemplateCount = 0;
  int doFirstTemplateCount = 0;
  int optionalNonNullableTemplateCount = 0;
  int optionalNullableTemplateCount = 0;
  int positiveNonNullableTemplateCount = 0;
  int positiveNullableTemplateCount = 0;
  int kleeneNonNullableTemplateCount = 0;
  int kleeneNullableTemplateCount = 0;

  private ARTGrammarElement artFirstTerminal;

  private final boolean mgllOrGllGeneratorPool;

  public ARTGLLGenerator(ARTGrammar grammar, OLDDirectives directives) {
    this.grammar = grammar;
    this.directives = directives;
    mgllOrGllGeneratorPool = directives.algorithmMode() == ARTModeAlgorithm.mgllGeneratorPool
        || directives.algorithmMode() == ARTModeAlgorithm.gllGeneratorPool;
  }

  public void generateParser(ARTText text) {
    // Some weirdness here. Attributes such as isCodeLabel are set as a side effect of generating the parser; but are also need to get the enumerations right
    // Hence we actually generate twice: the first time we use the data sink text driver so as to throw everything away

    // !!! This is a degenerate parser output that when run reports inadmissable !!!
    if (directives.algorithmMode() == ARTModeAlgorithm.mgllGeneratorPool && grammar.isEBNF()) { // Write out inadmissable parser
      gt = new ARTGeneratorTranslateJava(text);
      gt.fileOpen(directives.s("parserName") + ".java", directives.s("namespace"));
      gt.classOpen(directives.s("parserName"), "ARTGLLParserHashPool");
      gt.constructorOpen(directives.s("parserName"), "ARTLexerV3", "artLexer");
      gt.functionCall("this", "null", "artLexer");
      gt.assignString("artParserKind", "MGLL Gen");
      gt.assignString("artSpecificationName", grammar.getId());
      gt.constructorClose(directives.s("parserName"));
      gt.newLine();

      gt.constructorOpen(directives.s("parserName"), "ARTGrammar", "artGrammar", "ARTLexerV3", "artLexer");
      gt.functionCall("super", "artGrammar", "artLexer");
      gt.assignString("artParserKind", "MGLL Gen");
      gt.constructorClose(directives.s("parserName"));
      gt.newLine();

      gt.functionVoidOpen("artParseBody", gt.integerName(), "artStartLabel");

      gt.assign("artInadmissable", "true");
      gt.functionClose("artParseBody");
      gt.classClose(directives.s("parserName"));
      gt.fileClose(directives.s("parserName") + ".java", directives.s("namespace"));

      return;
    }

    // GLL, or MGLL with BNFbelow
    generateParserPass(new ARTText(new ARTTextHandlerSink()));
    generateParserPass(text);
  }

  private void generateParserPass(ARTText text) {
    gt = new ARTGeneratorTranslateJava(text);
    gt.indentSet(0);
    gt.fileOpen(directives.s("parserName") + ".java", directives.s("namespace"));
    for (String s : grammar.getPreludeStrings())
      gt.getText().printf("%s\n", s);
    gt.classOpen(directives.s("parserName"), "ARTGLLParserHashPool");
    writeSetInitialiserDeclarations();
    writeARTLabelEnumeration();
    writeARTNameEnumeration();
    if (directives.algorithmMode() == ARTModeAlgorithm.gllTWEGeneratorPool) {
      gt.getText().println();
      gt.indent();
    }

    writeParseGLLGenerated();
    writeSetInitialisation(grammar.getMergedSets());
    writeTableInitialise();
    writeConstructor(directives.s("parserName"));
    if (gt.targetLanguageName().equals("Java") && mgllOrGllGeneratorPool) { // evaluation presently only available for
                                                                            // Java
      // GLL parsers
      if (!directives.b("suppressSemantics")) for (String s : grammar.getSupportStrings())
        gt.getText().printf("%s\n", s);
      writeRDEvaluator();
    }
    gt.indentDown();
    gt.classClose(directives.s("parserName"));
    gt.fileClose(directives.s("parserName") + ".java", directives.s("namespace"));
  }

  public void generateLexer(ARTText text) {
    gt = new ARTGeneratorTranslateJava(text);
    gt.indentSet(0);
    gt.fileOpen(directives.s("lexerName") + ".java", directives.s("namespace"));
    gt.classOpen(directives.s("lexerName"), "ARTLexerV3");

    writeLexicaliseBuiltinInstances();
    writeLexicalisePreparseWhitespaceInstances();
    gt.classClose(directives.s("lexerName"));
    gt.fileClose(directives.s("lexerName") + ".java", directives.s("namespace"));
  }

  String inputSelect() {
    if (directives.algorithmMode() == ARTModeAlgorithm.mgllGeneratorPool)
      return "artCurrentInputPairReference";
    else
      return "artCurrentInputPairIndex";
  }

  void parserBlockOpenSelect(String label, String comment) {
    if (comment != null) {
      gt.indent();
      gt.comment(comment);
    }
    switch (directives.despatchMode()) {
    case dynamic:
    case static_:
      gt.label(label);
      break;

    case fragment:
    case state:
      gt.caseBranchOpen(label, true);
      break;

    default:
      throw new ARTUncheckedException("unknown despatch mode\n");
    }
  }

  void jumpSelect(String id) {
    switch (directives.despatchMode()) {
    case dynamic:
      gt.jumpDynamic(id);
      break;
    case static_:
      gt.jump(id);
      break;
    case fragment:
      gt.jumpFragment(id);
      break;
    case state:
      gt.jumpState(id);
      break;
    default:
      throw new ARTUncheckedException("unknown despatch mode\n");
    }
  }

  void popSel(String nonterminalString) {
    if (directives.algorithmMode() == ARTModeAlgorithm.gllTWEGeneratorPool)
      gt.functionCall("artPopRecogniser", "artCurrentGSSNode", inputSelect());
    else if (directives.b("clusteredGSS"))
      gt.functionCall("artPopClustered", nonterminalString, "artCurrentGSSNode", inputSelect(), "artCurrentSPPFNode");
    else if (directives.algorithmMode() == ARTModeAlgorithm.mgllGeneratorPool)
      gt.functionCall("artPopMGLL", "artCurrentGSSNode", inputSelect(), "artCurrentSPPFNode");
    else
      gt.functionCall("artPop", "artCurrentGSSNode", inputSelect(), "artCurrentSPPFNode");
  }

  String rootGSSNodeSelect() {
    // Kludge this away if using original formulation
    if (directives.b("clusteredGSS"))
      return "0";
    else
      return "artRootGSSNode";
  }

  String findGSSSelectName() {
    if (directives.b("clusteredGSS"))
      return "artFindGSSClustered";
    else if (directives.algorithmMode() == ARTModeAlgorithm.mgllGeneratorPool)
      return "artFindGSSMGLL";
    else
      return "artFindGSS";
  }

  void findGSSSelect(ARTGrammarInstance iftNode) {
    if (directives.algorithmMode() == ARTModeAlgorithm.gllTWEGeneratorPool)
      gt.functionAssignCall("artCurrentGSSNode", "artFindGSSRecogniser", iftNode.erL.toEnumString("L"), "artCurrentGSSNode", "artCurrentInputPairIndex");
    else
      gt.functionAssignCall("artCurrentGSSNode", findGSSSelectName(), iftNode.erL.toEnumString("L"), "artCurrentGSSNode", "artCurrentInputPairIndex",
          "artCurrentSPPFNode");

  }

  void findGSSInitialSelect() {
    if (directives.algorithmMode() == ARTModeAlgorithm.gllTWEGeneratorPool)
      gt.functionAssignCall("artRootGSSNode", "artFindGSSRecogniser", "ARTL_EOS", emptyNodeSelect(), "0");
    else if (directives.b("clusteredGSS"))
      gt.functionAssignCall("artRootGSSNode", "artFindGSSClusteredInitial", "ARTL_EOS", emptyNodeSelect(), "0", emptyNodeSelect());
    else
      gt.functionAssignCall("artRootGSSNode", "artFindGSS", "ARTL_EOS", emptyNodeSelect(), "0", emptyNodeSelect());
  }

  String findDescriptorSelect() {
    if (directives.b("clusteredGSS"))
      return "artFindDescriptorClustered";
    else
      return "artFindDescriptor";
  }

  String emptyNodeSelect() {
    if (directives.supportMode() == ARTModeSupport.ObjectOriented)
      return gt.nullName();
    else
      return "0";
  }

  void updateIsReferredLabelRec(ARTGrammarInstance node) {
    // printf("updateIsReferredLabel() at node %d labelled %s\n", graph_atom_number(node), node.id);
    if (node.isSPPFLabel || node.isCodeLabel || node.isReferredLabel || node.isTestRepeatLabel) {
      // node.lhsL.isReferredLabel = true;
      if (node.pL != node) node.pL.isReferredLabel = true;

      if (node.aL != node) node.aL.isReferredLabel = true;
    }

    for (ARTGrammarInstance child = node.getChild(); child != null; child = child.getSibling())
      updateIsReferredLabelRec(child);
  }

  void printStaticSwitchElementRec(ARTGrammarInstance node) {
    // printf("printStaticSwitchElementRec() at node %d labelled %s\n", graph_atom_number(node), node.id);
    if (node.isCodeLabel) {
      gt.caseBranchOpen(node.toEnumString("L"), true);
      jumpSelect(node.toEnumString("L"));
    }

    for (ARTGrammarInstance child = node.getChild(); child != null; child = child.getSibling())
      printStaticSwitchElementRec(child);
  }

  void printStaticSwitch() {
    gt.label("ARTX_DESPATCH_SWITCH");
    gt.caseOpen("artCurrentRestartLabel");
    printStaticSwitchElementRec((ARTGrammarInstance) grammar.getInstanceTree().getRoot());
    gt.caseBranchOpen("ARTX_DESPATCH", true);
    jumpSelect("ARTX_DESPATCH");
    gt.caseDefault();
    gt.exception("unexpectedLabel");
    gt.caseClose("artCurrentRestartLabel", true);
  }

  void writeCodeGLLRec(ARTGrammarInstance iftNode, ARTGrammarInstance lastLHSNode) {
    // System.out.println("Outputing GLL code for node " + iftNode + "\n");

    /* The root of the tree is labelled ART_MODULE and has as children the LHS nonterminals: we just recurse through them */
    if (iftNode instanceof ARTGrammarInstanceRoot)
      for (ARTGrammarInstance child = iftNode.getChild(); child != null; child = child.getSibling())
        writeCodeGLLRec(child, null);

    /*
     * (a) Productions of nonterminal Y: code(Y ::= a_1 | ... | a_p) is
     *
     * if testSelect(I[c_I], Y, a_1) findDescriptor_add(L_a_1, c_u, c_I, $) ... if testSelect(I[c_I], Y, a_p) findDescriptor_add(L_a_p, c_u, c_I, $) goto
     * despatch
     *
     * L_a_1: code(a_1, Y, #); if(I[c_I] \in follow(Y)) pop(c_u, c_I, c_N); goto despatch ... L_a_p: code(a_p, Y, #); if(I[c_I] \in follow(Y)) pop(c_u, c_I,
     * c_N); goto despatch
     */
    else if (iftNode instanceof ARTGrammarInstanceLHS) {
      if (directives.despatchMode() == ARTModeDespatch.fragment) {
        gt.functionVoidOpen(iftNode.toEnumString("PF"));
        gt.caseOpen("artCurrentRestartLabel");
        gt.indentUp();
      }

      lhsTemplateCount++;
      iftNode.isCodeLabel = true;
      parserBlockOpenSelect(iftNode.toEnumString("L"), " Nonterminal " + iftNode.getPayload() + " production descriptor loads");

      /* Output descriptor loads */
      for (ARTGrammarInstance childNode = iftNode.getChild(); childNode != null; childNode = childNode.getSibling()) {
        ARTGrammarInstance grandChildNode = childNode.getChild();

        if (!directives.b("suppressProductionGuard")) {
          gt.ifInSet("ARTSet" + grammar.getMergedSets().get(grandChildNode.getGuard()), gt.inputAccessToken());
          gt.newLine();
          gt.indentUp();
        }
        gt.functionCall(findDescriptorSelect(), childNode.lrL.toEnumString("L"), "artCurrentGSSNode", inputSelect(), "artDummySPPFNode");
        if (!directives.b("suppressProductionGuard")) gt.indentDown();
        childNode.lrL.isSPPFLabel = true;
      }
      gt.indent();
      jumpSelect("ARTX_DESPATCH");

      /* Output RHS bodies */
      for (ARTGrammarInstance childNode = iftNode.getChild(); childNode != null; childNode = childNode.getSibling()) {

        childNode.lrL.isCodeLabel = true;
        parserBlockOpenSelect(childNode.lrL.toEnumString("L"), " Nonterminal " + childNode.getLhsL().getPayload() + ": match production");

        writeCodeGLLRec(childNode, iftNode); // Pass LHS node in

        if (directives.algorithmMode() == ARTModeAlgorithm.gllTWEGeneratorPool) {
          if (grammar.getParaterminals().contains(iftNode.getPayload())) { // Only do this for paraterminals
            gt.indent();
            gt.getText().println(
                "tweSet.tweSetUpdateExactMakeLeftSet(" + iftNode.toEnumString("L") + ", artGSSNodeLevel(artCurrentGSSNode), artCurrentInputPairIndex);");
          }
          if (iftNode.getPayload().equals(grammar.getDefaultStartNonterminal())) {
            gt.functionCall("tweSet.ARTLexerV3Wrapup", "artCurrentInputPairIndex");
            gt.getText().println("            artIsInLanguage |= artCurrentInputPairIndex == artLexer.artInputLength - artWhitespaceEOSPrefixLength;");
          }
        }

        if (!(directives.b("predictivePops") && iftNode.isPostPredictivePop)) {
          if (!directives.b("suppressPopGuard")) {
            gt.ifNotInSet("ARTSet" + grammar.getMergedSets().get(iftNode.follow), gt.inputAccessToken());
            jumpSelect("ARTX_DESPATCH");
          }

          popSel(iftNode.toEnumString("L"));
          gt.indent();
          jumpSelect("ARTX_DESPATCH /* Top level pop */");
        }
      }

      if (directives.despatchMode() == ARTModeDespatch.fragment) {
        gt.caseClose("artCurrentRestartLabel", true);
        gt.functionClose(iftNode.toEnumString("PF"));
        gt.newLine();
      }
    }

    /*
     * (b) Base regular expressions: code(r, X, \beta)
     *
     * (i) r is epsilon #: code(r, X, \beta) is c_R := findSPPFE(c_I) c_N := findSPPF(E_r, c_N, c_R)
     */
    else if (iftNode instanceof ARTGrammarInstanceEpsilon)

    {
      epsilonTemplateCount++;
      gt.indent();
      gt.comment(" Epsilon template start ");
      if (mgllOrGllGeneratorPool) gt.functionAssignCall("artCurrentSPPFRightChildNode", "artFindSPPFEpsilon", "artCurrentInputPairIndex");
      iftNode.erL.isSPPFLabel = true;
      if (mgllOrGllGeneratorPool)
        gt.functionAssignCall("artCurrentSPPFNode", "artFindSPPF", iftNode.erL.toEnumString("L"), "artCurrentSPPFNode", "artCurrentSPPFRightChildNode");

      gt.indent();
      gt.comment(" Epsilon template end ");
    }

    /*
     * (ii) r is a terminal a: code(r, X, \beta) is c_R := findSPPFT(a, c_I) c_I = c_I + 1 c_N := findSPPF(E_r, c_N, c_R)
     */
    else if (iftNode instanceof ARTGrammarInstanceTerminal || (directives.algorithmMode() == ARTModeAlgorithm.mgllGeneratorPool
        && (iftNode instanceof ARTGrammarInstanceNonterminal && grammar.getParaterminals().contains(iftNode.getPayload())))) {
      // Terminal template: here we do different things for classical GLL, MGLL with simple templates (may generate useless descriptors) and MGLL with
      // continuation check templates

      terminalTemplateCount++;
      gt.indent();
      gt.comment(" Terminal template start ");

      if (directives.algorithmMode() == ARTModeAlgorithm.mgllGeneratorPool) { // MGLL variannt
        gt.forSuccessorPair();
        gt.newLine();
        gt.ifInSet("ARTSet" + grammar.getMergedSets().get(iftNode.erL.getGuard()), gt.inputSuccessorReferenceToken());
        gt.blockOpen();
        gt.indentUp();
        gt.newLine();

        if (mgllOrGllGeneratorPool) iftNode.isSPPFLabel = true;
        if (mgllOrGllGeneratorPool) gt.functionAssignCall("artCurrentSPPFRightChildNode", "artFindSPPFTerminal", iftNode.getPayload().toEnumerationString(null),
            "artCurrentInputPairIndex", gt.inputSuccessorReferenceLeftExtent());
        iftNode.erL.isSPPFLabel = true;
        if (mgllOrGllGeneratorPool)
          gt.functionAssignCall("artTemporarySPPFNode", "artFindSPPF", iftNode.erL.toEnumString("L"), "artCurrentSPPFNode", "artCurrentSPPFRightChildNode");

        iftNode.erL.isCodeLabel = true;
        gt.functionCall(findDescriptorSelect(), iftNode.erL.toEnumString("L"), "artCurrentGSSNode", gt.inputSuccessorReference(), "artTemporarySPPFNode");

        gt.indentDown();
        gt.indent();
        gt.blockClose();
        gt.newLine();

        gt.indent();
        jumpSelect("ARTX_DESPATCH");

        gt.newLine();
        gt.indentDown();

        parserBlockOpenSelect(iftNode.erL.toEnumString("L"), null);
      } else {
        iftNode.isSPPFLabel = true;
        gt.assign("artCurrentInputPairReference", gt.inputAccessFirstSuccessorReference());
        if (mgllOrGllGeneratorPool) gt.functionAssignCall("artCurrentSPPFRightChildNode", "artFindSPPFTerminal", iftNode.getPayload().toEnumerationString(null),
            "artCurrentInputPairIndex", gt.inputAccessLeftExtent());
        // Add to TWESet
        if (directives.algorithmMode() == ARTModeAlgorithm.gllTWEGeneratorPool && !(iftNode.getPayload() instanceof ARTGrammarElementTerminalCharacter))
          gt.getText().println("tweSet.tweSetUpdateExactMakeLeftSet(" + iftNode.getPayload().toEnumerationString(null) + ", artCurrentInputPairIndex"
              + ", artInputPairBuffer[artCurrentInputPairReference + 1]);");

        gt.assign("artCurrentInputPairIndex", gt.inputAccessLeftExtent());

        iftNode.erL.isSPPFLabel = true;
        if (mgllOrGllGeneratorPool)
          gt.functionAssignCall("artCurrentSPPFNode", "artFindSPPF", iftNode.erL.toEnumString("L"), "artCurrentSPPFNode", "artCurrentSPPFRightChildNode");
      }
      gt.indent();
      gt.comment(" Terminal template end ");
    }

    /*
     * (iii) r in a nonterminal Y: code(r, X, \beta) is c_U = findGSS_create(E_r, c_N, ) goto J_Y E_r:
     */
    else if (iftNode instanceof ARTGrammarInstanceNonterminal) {
      if (!(directives.algorithmMode() == ARTModeAlgorithm.mgllGeneratorPool && grammar.getParaterminals().contains(iftNode.getPayload()))) {
        nonterminalTemplateCount++;
        gt.indent();
        gt.comment(" Nonterminal template start ");

        iftNode.erL.isCodeLabel = true;
        findGSSSelect(iftNode);

        gt.indent();
        jumpSelect(((ARTGrammarElementNonterminal) iftNode.getPayload()).lhsInstance.toEnumString("L"));

        iftNode.erL.isCodeLabel = true;

        parserBlockOpenSelect(iftNode.erL.toEnumString("L"), null);
        gt.indent();
        gt.comment(" Nonterminal template end ");
      }
    }

    /*
     * (iv) r is a bracketed expression (v): code(r, X, \beta) is code(v, X, \beta)
     */
    else if (iftNode instanceof ARTGrammarInstanceDoFirst) {
      gt.indent();
      gt.comment(" Do-first template start ");
      doFirstTemplateCount++;
      writeCodeGLLRec(iftNode.getChild(), lastLHSNode);
      gt.indent();
      gt.comment(" Do-first template end ");
    }

    /*
     * (v) r is a bracketed expression [v] and v *=> #: code (r, X, \beta) is OLD TEMPLATE:
     *
     * if testSelect(I[c_I], X, \beta) { c_r := findSPPFE(c_I) c_t := findSPPF(E_r, c_N, c_R) // Note that c_t isn't need; the result is voided } if not
     * testSelect(I[c_I], X, (v)\beta) goto despatch code(v, X, \beta)
     *
     * NEW TEMPLATE:
     *
     * if (testSelect(I[c_I ], X; \beta)) { c_r := findSPPFE(c_I) c_t := findSPPF(E_r, c_N, c_R) findDescriptor(E_r, c_u, c_I, c_t) testRepeat(T_r, c_U, c_I,
     * c_t) } if not testSelect(I[c_I], X, (v)\beta) goto despatch code(v, X, \beta) if (testRepeat(T_r, c_U, c_I, c_N)) goto despatch E_r :
     *
     * 2016 TEMPLATE if testSelect(I[c_I], E_r) { c_t :=getBaseNode(E_s, E_r, c_I, c_N) findDescriptor_add(E_r, c_u, c_I, c_t); testRepeat(T_r, c_u, c_I, c_T) }
     * code(s); if (testRepeat(T_r, c_u, c_I, c_N) goto despatch; E_r:
     *
     */
    else if (iftNode instanceof ARTGrammarInstanceOptional) {
      ARTGrammarInstance childNode = iftNode.getChild();

      if (childNode.first.contains(grammar.getEpsilon())) {
        optionalNullableTemplateCount++;

        boolean TEMPLATE2016 = true;
        if (TEMPLATE2016) {
          /****************************************/
          // Note - iftNode corresponds to r in the paper; childNode is s

          gt.indent();
          gt.comment(" Optional, nullable body template start (2016 version)");
          gt.ifInSet("ARTSet" + grammar.getMergedSets().get(iftNode.getSibling().getGuard()), gt.inputAccessToken());
          gt.blockOpen();
          iftNode.erL.toEnumString("L");

          // 30/8/16 change from getBaseNode to getNode
          // cust.functionAssignCall("artTemporarySPPFNode", "artFindSPPFBaseNode", catNodeAsEnumerationElement(childNode.erL, "L"), catAltBuffer,
          // "artCurrentInputPairIndex", "artCurrentSPPFNode");

          if (mgllOrGllGeneratorPool) gt.functionAssignCall("artCurrentSPPFRightChildNode", "artFindSPPFEpsilon", "artCurrentInputPairIndex");
          iftNode.erL.isSPPFLabel = true;
          if (mgllOrGllGeneratorPool)
            gt.functionAssignCall("artTemporarySPPFNode", "artFindSPPF", iftNode.erL.toEnumString("L"), "artCurrentSPPFNode", "artCurrentSPPFRightChildNode");

          gt.functionCall(findDescriptorSelect(), iftNode.erL.toEnumString("L"), "artCurrentGSSNode", "artCurrentInputPairIndex", "artTemporarySPPFNode");
          iftNode.isTestRepeatLabel = true;
          gt.functionCall("artTestRepeat", iftNode.toEnumString("T"), "artCurrentGSSNode", "artCurrentInputPairIndex", "artTemporarySPPFNode");
          iftNode.isRELabel = true;
          gt.blockClose();

          gt.ifNotInSet("ARTSet" + grammar.getMergedSets().get(childNode.getGuard()), gt.inputAccessToken());
          jumpSelect("ARTX_DESPATCH");

          writeCodeGLLRec(childNode, lastLHSNode);

          if (!directives.b("suppressTestRepeat")) {
            iftNode.isTestRepeatLabel = true;
            gt.ifTestRepeat(iftNode.toEnumString("T"), "artCurrentGSSNode", "artCurrentInputPairIndex", "artCurrentSPPFNode");
            iftNode.isCodeLabel = true;
            jumpSelect("ARTX_DESPATCH");
          }
          iftNode.erL.isCodeLabel = true;

          parserBlockOpenSelect(iftNode.erL.toEnumString("L"), null);

          gt.indent();
          gt.comment(" Optional, nullable body template end (2016 version)");
          /**********************************/
        } else {
          gt.indent();
          gt.comment(" Optional, nullable body template start ");
          gt.ifInSet("ARTSet" + grammar.getMergedSets().get(iftNode.getSibling().getGuard()), gt.inputAccessToken());
          gt.blockOpen();
          if (mgllOrGllGeneratorPool) gt.functionAssignCall("artCurrentSPPFRightChildNode", "artFindSPPFEpsilon", "artCurrentInputPairIndex");
          iftNode.erL.isSPPFLabel = true;
          if (mgllOrGllGeneratorPool)
            gt.functionAssignCall("artTemporarySPPFNode", "artFindSPPF", iftNode.erL.toEnumString("L"), "artCurrentSPPFNode", "artCurrentSPPFRightChildNode");
          gt.functionCall(findDescriptorSelect(), iftNode.erL.toEnumString("L"), "artCurrentGSSNode", "artCurrentInputPairIndex", "artTemporarySPPFNode");
          if (!directives.b("suppressTestRepeat")) {
            iftNode.isTestRepeatLabel = true;
            gt.ifTestRepeat(iftNode.toEnumString("L"), "artCurrentGSSNode", "artCurrentInputPairIndex", "artTemporarySPPFNode");
            jumpSelect("ARTX_DESPATCH");
          }
          gt.blockClose();

          gt.ifNotInSet("ARTSet" + grammar.getMergedSets().get(childNode.getGuard()), gt.inputAccessToken());
          jumpSelect("ARTX_DESPATCH");

          writeCodeGLLRec(childNode, lastLHSNode);

          if (!directives.b("suppressTestRepeat")) {
            iftNode.isTestRepeatLabel = true;
            gt.ifTestRepeat(iftNode.toEnumString("L"), "artCurrentGSSNode", "artCurrentInputPairIndex", "artCurrentSPPFNode");
            jumpSelect("ARTX_DESPATCH");
          }
          iftNode.erL.isCodeLabel = true;

          parserBlockOpenSelect(iftNode.erL.toEnumString("L"), null);

          gt.indent();
          gt.comment(" Optional, nullable body template end ");
        }
      }
      /*
       * (vi) r is a bracketed expression [v] and not v *=> #: code (r, X, \beta) is if testSelect(I[c_I], X, \beta) { c_r := findSPPFE(c_I) c_t :=
       * findSPPF(E_r, c_N, c_r) findDescriptor_add(E_r, c_u, c_I, c_t) } if not testSelect(I[c_I], X, v\beta) goto despatch code(v, X, \beta) E_r:
       *
       * (or in 2016 terms)
       *
       * (vi) r is a bracketed expression [s] and not s *=> #: code (r) is if testSelect(I[c_I], E_r) { c_r := findSPPFNodeE(c_I) c_t := findSPPFNode(E_r, c_N,
       * c_r) findDescriptor_add(E_r, c_u, c_I, c_t) } if not testSelect(I[c_I], L_s) goto despatch code(s) E_r:
       *
       */
      else {
        optionalNonNullableTemplateCount++;

        gt.indent();
        gt.comment(" Optional, non-nullable body template start ");
        gt.ifInSet("ARTSet" + grammar.getMergedSets().get(iftNode.getSibling().getGuard()), gt.inputAccessToken());
        gt.blockOpen();
        if (mgllOrGllGeneratorPool) gt.functionAssignCall("artCurrentSPPFRightChildNode", "artFindSPPFEpsilon", "artCurrentInputPairIndex");
        iftNode.erL.isSPPFLabel = true;
        if (mgllOrGllGeneratorPool)
          gt.functionAssignCall("artTemporarySPPFNode", "artFindSPPF", iftNode.erL.toEnumString("L"), "artCurrentSPPFNode", "artCurrentSPPFRightChildNode");
        gt.functionCall(findDescriptorSelect(), iftNode.erL.toEnumString("L"), "artCurrentGSSNode", "artCurrentInputPairIndex", "artTemporarySPPFNode");
        gt.blockClose();
        gt.ifNotInSet("ARTSet" + grammar.getMergedSets().get(childNode.getGuard()), gt.inputAccessToken());
        jumpSelect("ARTX_DESPATCH");
        writeCodeGLLRec(childNode, lastLHSNode);
        iftNode.erL.isCodeLabel = true;

        parserBlockOpenSelect(iftNode.erL.toEnumString("L"), null);
        gt.indent();
        gt.comment(" Optional, non-nullable body template end ");
      }
    }

    /*
     * (vii) r is a bracketed expression <v> and not v *=> #: code (r, X, \beta) is c_u := findGSS_create(E_r, c_u, c_I, c_N) c_N := dummyNode C_r: if not
     * testSelect(I[c_I], X, v\beta) goto despatch code(v, X, [v]\beta) if testSelect(I[c_I], X, [\beta]) pop(c_U, c_I, c_N) goto C_r E_r:
     *
     * or in 2016 terms (vii) r is a bracketed expression <s> and not s *=> #: code (r) is c_u := findGSSNode_create(E_r, c_u, c_I, c_N) c_N := dummyNode C_r:
     * if not testSelect(I[c_I], L_s) goto despatch code(s) if testSelect(I[c_I], E_r) pop(c_U, c_I, c_N) goto C_r E_r:
     */
    else if (iftNode instanceof ARTGrammarInstancePositiveClosure) {
      ARTGrammarInstance childNode = iftNode.getChild();
      if (!childNode.first.contains(grammar.getEpsilon())) {
        positiveNonNullableTemplateCount++;

        gt.indent();
        gt.comment(" Positive closure, non-nullable body template start ");

        iftNode.erL.isCodeLabel = true;
        findGSSSelect(iftNode);
        if (mgllOrGllGeneratorPool) gt.assign("artCurrentSPPFNode", "artDummySPPFNode");
        parserBlockOpenSelect(iftNode.toEnumString("C"), null);
        iftNode.isClosureLabel = true;
        if (!childNode.first.contains(grammar.getEpsilon())) {
          gt.ifNotInSet("ARTSet" + grammar.getMergedSets().get(childNode.getGuard()), gt.inputAccessToken());
          jumpSelect("ARTX_DESPATCH");
          writeCodeGLLRec(childNode, lastLHSNode);
          gt.ifInSet("ARTSet" + grammar.getMergedSets().get(iftNode.getSibling().getGuard()), gt.inputAccessToken());
          popSel("unimplementedEBNFClusteredDescriptor");
          gt.indent();
          jumpSelect(iftNode.toEnumString("C"));
          iftNode.erL.isCodeLabel = true;
        }

        parserBlockOpenSelect(iftNode.erL.toEnumString("L"), null);

        gt.indent();
        gt.comment(" Positive closure, non-nullable body template end ");
      }
      /*
       * (viii) r is a bracketed expression <s> and s *=> #: code (r) is c_u := findGSSNode_create(E_r, c_u, c_I, c_N) c_N := dummyNode C_r: if testRepeat(T_r,
       * c_u, c_I, c_N) goto despatch if not testSelect(I[c_I], L_s) goto despatch code(s) if testSelect(I[c_I], E_r) pop(c_U, c_I, c_N) goto C_r E_r:
       *
       */
      else {
        positiveNullableTemplateCount++;

        gt.indent();
        gt.comment(" Positive closure, nullable body template start (2016 version)");

        // c_u := findGSSNode_create(E_r, c_u, c_I, c_N)
        iftNode.erL.isCodeLabel = true;
        findGSSSelect(iftNode);

        // c_N := dummyNode
        if (mgllOrGllGeneratorPool) gt.assign("artCurrentSPPFNode", "artDummySPPFNode");

        // C_r:
        parserBlockOpenSelect(iftNode.toEnumString("C"), null);
        iftNode.isClosureLabel = true;

        // if testRepeat(T_r, c_u, c_I, c_N) goto despatch
        iftNode.isTestRepeatLabel = true;
        gt.ifTestRepeat(iftNode.toEnumString("L"), "artCurrentGSSNode", "artCurrentInputPairIndex", "artCurrentSPPFNode");
        jumpSelect("ARTX_DESPATCH");

        // if not testSelect(I[c_I], L_s) goto despatch
        gt.ifNotInSet("ARTSet" + grammar.getMergedSets().get(childNode.getGuard()), gt.inputAccessToken());
        jumpSelect("ARTX_DESPATCH");

        // code(s)
        writeCodeGLLRec(childNode, lastLHSNode);

        // if testSelect(I[c_I], E_r) pop(c_U, c_I, c_N)
        // goto C_r
        gt.ifInSet("ARTSet" + grammar.getMergedSets().get(iftNode.getSibling().getGuard()), gt.inputAccessToken());
        popSel("unimplementedEBNFClusteredDescriptor");
        gt.indent();
        jumpSelect(iftNode.toEnumString("C"));
        iftNode.erL.isCodeLabel = true;

        // E_r:
        parserBlockOpenSelect(iftNode.erL.toEnumString("L"), null);

        gt.indent();
        gt.comment(" Positive closure, nullable body template end (2016 version)");
      }
    }

    /*
     * (ix) r is a bracketed expression {v} and not v *=> #: code (r, X, \beta) is
     *
     * c_u := findGSS_create(E_r, c_u, c_I, c_N)
     *
     * if testSelect(I[c_I], X, \beta) { c_N := findSPPFE(c_I) pop(c_u, c_I, c_N) }
     *
     * c_N := dummyNode
     *
     * C_r: if not testSelect(I[c_I], X, v\beta) goto despatch code(v, X, [v]\beta)
     *
     * if testSelect(I[c_I], X, \beta) pop(c_U, c_I, c_N) goto C_r E_r:
     *
     * or, in post 2016-speak
     *
     * (ix) r is a bracketed expression {s} and not s *=> #: code (r) is c_u := findGSSNode_create(E_r, c_u, c_I, c_N)
     *
     * if testSelect(I[c_I], E_r) { c_N := findSPPFNodeE(c_I) pop(c_u, c_I, c_N) } c_N := dummyNode
     *
     * C_r: if not testSelect(I[c_I], L_s) goto despatch code(s)
     *
     * if testSelect(I[c_I], E_r) pop(c_U, c_I, c_N) goto C_r E_r:
     *
     */
    else if (iftNode instanceof ARTGrammarInstanceKleeneClosure)

    {
      ARTGrammarInstance childNode = iftNode.getChild();
      if (!childNode.first.contains(grammar.getEpsilon())) {
        kleeneNonNullableTemplateCount++;

        gt.indent();
        gt.comment(" Kleene closure, non-nullable body template start ");

        iftNode.erL.isCodeLabel = true;
        findGSSSelect(iftNode);
        gt.ifInSet("ARTSet" + grammar.getMergedSets().get(iftNode.getSibling().getGuard()), gt.inputAccessToken());
        gt.blockOpen();
        if (mgllOrGllGeneratorPool) gt.functionAssignCall("artCurrentSPPFNode", "artFindSPPFEpsilon", "artCurrentInputPairIndex");
        popSel("unimplementedEBNFClusteredDescriptor");
        gt.blockClose();
        if (mgllOrGllGeneratorPool) gt.assign("artCurrentSPPFNode", "artDummySPPFNode");
        parserBlockOpenSelect(iftNode.toEnumString("C"), "L");
        iftNode.isClosureLabel = true;
        gt.ifNotInSet("ARTSet" + grammar.getMergedSets().get(childNode.getGuard()), gt.inputAccessToken());
        jumpSelect("ARTX_DESPATCH");
        writeCodeGLLRec(childNode, lastLHSNode);
        gt.ifInSet("ARTSet" + grammar.getMergedSets().get(iftNode.getSibling().getGuard()), gt.inputAccessToken());
        popSel("unimplementedEBNFClusteredDescriptor");
        gt.indent();
        jumpSelect(iftNode.toEnumString("C"));
        iftNode.erL.isCodeLabel = true;
        parserBlockOpenSelect(iftNode.erL.toEnumString("L"), null);

        gt.indent();
        gt.comment(" Kleene closure, non-nullable body template end ");

      }
      /*
       * (x) r is a bracketed expression {v} and v *=> #: code (r, X, \beta) is c_u := findGSS_create(E_r, c_u, c_I, c_N) if testSelect(I[c_I], X, \beta) c_N :=
       * getLoopSkipNode(L_r, c_I) c_N := getLoopBaseNode(E_v, c_I) C_r: if testRepeat(T_r, c_u, c_I, c_N) goto despatch if not testSelect(I[c_I], X, v\beta)
       * goto despatch code(v, X, v\beta) if testSelect(I[c_I], X, \beta) pop(c_U, c_I, c_N) goto C_r E_r:
       *
       * post 2016 (x) r is a bracketed expression {s} and s *=> #: code (r) is c_u := findGSSNode_create(E_r, c_u, c_I, c_N) c_N := getBaseNode(E_s, E_r, c_I)
       * C_r: if testRepeat(T_r, c_u, c_I, c_N) goto despatch if not testSelect(I[c_I], L_s) goto despatch code(s) if testSelect(I[c_I], E_r) pop(c_U, c_I, c_N)
       * goto C_r E_r:
       */
      else {
        kleeneNullableTemplateCount++;

        gt.indent();
        gt.comment(" Kleene closure, nullable body template start (2016 version) with experimental pop");
        // c_u := findGSSNode_create(E_r, c_u, c_I, c_N)
        iftNode.erL.isCodeLabel = true;
        findGSSSelect(iftNode);

        // c_N := getBaseNode(E_s, E_r, c_I, dummyNode)
        if (mgllOrGllGeneratorPool) gt.functionAssignCall("artCurrentSPPFNode", "artFindSPPFBaseNode", childNode.erL.toEnumString("L"),
            iftNode.erL.toEnumString("L"), "artCurrentInputPairIndex");

        // C_r:
        parserBlockOpenSelect(iftNode.toEnumString("C"), null);
        iftNode.isClosureLabel = true;

        // if testRepeat(T_r, c_u, c_I, c_N) goto despatch
        iftNode.isRELabel = true;
        gt.ifTestRepeat(iftNode.toEnumString("T"), "artCurrentGSSNode", "artCurrentInputPairIndex", "artCurrentSPPFNode");
        jumpSelect("ARTX_DESPATCH");

        // if not testSelect(I[c_I], L_s) goto despatch
        gt.ifNotInSet("ARTSet" + grammar.getMergedSets().get(childNode.getGuard()), gt.inputAccessToken());
        jumpSelect("ARTX_DESPATCH");

        // code(s)
        writeCodeGLLRec(childNode, lastLHSNode);

        // if testSelect(I[c_I], E_r) pop(c_U, c_I, c_N)
        gt.ifInSet("ARTSet" + grammar.getMergedSets().get(iftNode.getSibling().getGuard()), gt.inputAccessToken());
        popSel("unimplementedEBNFClusteredDescriptor");

        // goto C_r
        gt.indent();
        jumpSelect(iftNode.toEnumString("C"));
        iftNode.isCodeLabel = true;

        // E_r:
        parserBlockOpenSelect(iftNode.erL.toEnumString("L"), null);

        gt.indent();
        gt.comment(" Kleene closure, nullable body template end (2016 version)");

      }
    }

    /*
     * (xi) r is a concatenated expression r_1...r_d: code(r, X, \beta) is code(r_1, X, \beta) if not testSelect(I[c_I], X, r2...r_d\beta) goto despatch
     * code(r_2, X, r3..r_d\beta) ... if not testSelect(I[c_I], X, r_d\beta) goto despatch code(r_d, X, \beta)
     */
    else if (iftNode instanceof ARTGrammarInstanceCat) {
      concatenationTemplateCount++;

      gt.indent();
      gt.comment(" Cat/unary template start ");

      for (ARTGrammarInstance childNode = iftNode.getChild(); childNode != null; childNode = childNode.getSibling()) {
        if (directives.b("predictivePops") && childNode.isPredictivePop) {
          if (!directives.b("suppressPopGuard")) {
            gt.ifNotInSet("ARTSet" + grammar.getMergedSets().get(childNode.getGuard()), gt.inputAccessToken());
            jumpSelect("ARTX_DESPATCH");
          }

          popSel(lastLHSNode.toEnumString("L"));
          gt.indent();
          jumpSelect("ARTX_DESPATCH /* Predictive pop */");
        } else
          writeCodeGLLRec(childNode, lastLHSNode);
        // previousChildNode = childNode;
      }
      gt.indent();
      gt.comment(" Cat/unary template end ");
    }

    /*
     * (xii) r is an alternated expression r_1 | ... | r_d: code (r, X, \beta) is if testSelect(I[c_I], X, r_1\beta) findDescriptor_add(L_r_1, c_u, c_I, c_N)
     * ... if testSelect(I[c_I], X, r_d\beta) findDescriptor_add(L_r_d, c_u, c_I, c_N) goto despatch
     *
     * L_r_1: code (r_1, X, \beta) goto A_r ... L_r_d-1: code (r_d-1, X, \beta) goto A_r L_r_d: code (r_d, X, \beta) A_r: if testRepeat(T_r, c_u, c_I, c_N) goto
     * despatch
     */
    else if (iftNode instanceof ARTGrammarInstanceAlt) {
      alternateTemplateCount++;

      gt.indent();
      gt.comment(" Alternate template start ");

      for (ARTGrammarInstance childNode = iftNode.getChild(); childNode != null; childNode = childNode.getSibling()) {
        ARTGrammarInstance firstPosNode = grammar.leftmostElementRec(childNode);

        gt.ifInSet("ARTSet" + grammar.getMergedSets().get(firstPosNode.getGuard()), gt.inputAccessToken());
        gt.functionCall(findDescriptorSelect(), firstPosNode.toEnumString("L"), "artCurrentGSSNode", "artCurrentInputPairIndex", "artCurrentSPPFNode");
        firstPosNode.isCodeLabel = firstPosNode.isSPPFLabel = true; /* Error? Shouldn't this be isSPPFLabel */
      }

      gt.indent();
      jumpSelect("ARTX_DESPATCH");

      for (ARTGrammarInstance childNode = iftNode.getChild(); childNode != null; childNode = childNode.getSibling()) {
        ARTGrammarInstance firstPosNode = grammar.leftmostElementRec(childNode);

        firstPosNode.isCodeLabel = true;
        parserBlockOpenSelect(firstPosNode.toEnumString("L"), null);
        writeCodeGLLRec(childNode, lastLHSNode);
        if (!(directives.b("predictivePops") && iftNode.isPostPredictivePop)) {
          gt.indent();
          jumpSelect(iftNode.toEnumString("A"));
          iftNode.isAltLabel = true;
        }
      }

      if (!(directives.b("predictivePops") && iftNode.isPostPredictivePop)) { // suppress testRepeat block if predictive pops in play
        iftNode.isTestRepeatLabel = true;
        parserBlockOpenSelect(iftNode.toEnumString("A"), null);
        if (!directives.b("suppressTestRepeat")) {
          gt.ifTestRepeat(iftNode.toEnumString("L"), "artCurrentGSSNode", "artCurrentInputPairIndex", "artCurrentSPPFNode");
          jumpSelect("ARTX_DESPATCH");
        }
      }
      gt.indent();
      gt.comment(" Alternate template end ");
    }

    else if (iftNode instanceof ARTGrammarInstanceSlot) {
      if (iftNode.isSlotSelector) {

        gt.ifNotInSet("ARTSet" + grammar.getMergedSets().get(iftNode.getGuard()), gt.inputAccessToken());
        jumpSelect("ARTX_DESPATCH");
      }
    }

    else {
      throw new ARTUncheckedException("\nunknown tree node " + iftNode + " found during parser output\n");
    }
  }

  void printTemplateCounts() {
    if (directives.i("verbosityLevel") > 0) System.out.printf(
        "\nGenerated template instance counts\n\n" + "lhs, %d, " + "epsilon, %d, " + "terminal, %d, " + "nonterminal, %d, " + "concatenation, %d, "
            + "alternate, %d, " + "doFirst, %d, " + "optionalNonNullable, %d, " + "optionalNullable, %d, " + "positiveNonNullable, %d, "
            + "positiveNullable, %d, " + "kleeneNonNullable, %d, " + "kleeneNullable, %d\n\n",
        lhsTemplateCount, epsilonTemplateCount, terminalTemplateCount, nonterminalTemplateCount, concatenationTemplateCount, alternateTemplateCount,
        doFirstTemplateCount, optionalNonNullableTemplateCount, optionalNullableTemplateCount, positiveNonNullableTemplateCount, positiveNullableTemplateCount,
        kleeneNonNullableTemplateCount, kleeneNullableTemplateCount);
  }

  void writeSetInitialiserDeclarations() {
    Object[] mergedSets = grammar.getMergedSets().values().toArray();
    java.util.Arrays.sort(mergedSets);

    for (int i = 0; i < mergedSets.length; i++)
      gt.declareBooleanArray("ARTSet" + mergedSets[i]);
  }

  void printCodeAndSPPFLabelRec(ARTGrammarInstance node, boolean codeOnly) {
    if (node == null) return;
    if (node instanceof ARTGrammarInstanceAction) return;
    // System.out.print("printCodeAndSPPFLabel() at node " + node.getKey() + " " + node + " as " + node.toEnumString("L") + "\n");

    // if (codeOnly) cust.enumerationElement(node.toEnumString("L"));

    // 27 October 2019 - Hacked to put eveything out
    // if (codeOnly) {
    // if (node.isCodeLabel) gt.enumerationElement(node.toEnumString("L"));
    // } else
    {
      // if (node.isCodeLabel || node.isSPPFLabel || node.isReferredLabel || node.isTestRepeatLabel || node.isGiftLabel)
      // if (!node.isCodeLabel)
      gt.enumerationElement(node.toEnumString("L"));

      if (node.isFfCE) gt.enumerationElement(node.toEnumString("N"));

      if (node.isClosureLabel) gt.enumerationElement(node.toEnumString("C"));

      if (node.isAltLabel) gt.enumerationElement(node.toEnumString("A"));

      if (node.isRELabel) gt.enumerationElement(node.toEnumString("T"));
    }

    for (ARTGrammarInstance child = node.getChild(); child != null; child = child.getSibling())
      printCodeAndSPPFLabelRec(child, codeOnly);
  }

  void writeARTLabelEnumeration() {
    gt.newLine();
    gt.enumerationOpen("artLabel");
    gt.enumerationElement(grammar.getEoS().toEnumerationString("X"));

    for (ARTGrammarElementTerminal t : grammar.getTerminals()) {
      if (t instanceof ARTGrammarElementTerminalBuiltin) {
        if (directives.algorithmMode() == ARTModeAlgorithm.gllTWEGeneratorPool || directives.algorithmMode() == ARTModeAlgorithm.mgllGeneratorPool)
          throw new ARTUncheckedException("grammar builtins are not allowed in gllTWEGeneratorPool specifications - " + t);
        // System.out.printf("Outputing enumeration for builtin terminal %s\n", t);
        gt.enumerationElement(t.toEnumerationString(null));
      }
    }

    for (ARTGrammarElementTerminal t : grammar.getTerminals()) {
      if (!(t instanceof ARTGrammarElementTerminalBuiltin)) {
        // System.out.printf("Outputing enumeration for ordinary terminal %s\n", t);
        if (artFirstTerminal == null) artFirstTerminal = t;
        gt.enumerationElement(t.toEnumerationString(null));
      }
    }

    // System.out.printf("Outputing enumeration for epsilon\n");

    gt.enumerationElement(grammar.getEpsilon().toEnumerationString("X"));

    for (ARTGrammarElementNonterminal n : grammar.getNonterminals()) {
      // System.out.printf("Outputting enumeration for nonterminal %s\n", n);
      gt.enumerationElement(n.toEnumerationString("L"));
    }

    for (ARTGrammarElementNonterminal n : grammar.getNonterminals()) {
      for (ARTGrammarInstance r : n.getProductions()) {
        // Why do we do it this way - why not just put them all out in one go? Must be something to do with data compression
        // Changed 27 October 2020 to put everything out in one go
        printCodeAndSPPFLabelRec(r, true); // Traverse the tree and print out the code labels
        // printCodeAndSPPFLabelRec(r, false); // Traverse the tree and print out the rest of the labels
      }
    }
    gt.enumerationElement("ARTX_DESPATCH");
    gt.enumerationElement("ARTX_DUMMY");
    gt.enumerationElement("ARTX_LABEL_EXTENT");
    gt.enumerationClose("artLabel");
  }

  void writeARTNameEnumeration() {
    // Scan and print names
    gt.newLine();
    gt.enumerationOpen("artName");
    gt.enumerationElement("ARTNAME_NONE");
    // We haven't handled names yet in the front end, so comment this body out
    // for (ARTElementName n : grammar.names())
    // cust.enumerationElement(catSymbolAsEnumElement(lhsSymbol));
    gt.enumerationElement("ARTNAME_EXTENT");
    gt.enumerationClose("artName");
  }

  void writeLexicaliseBuiltinInstances() {
    gt.functionVoidOpen("artLexicaliseBuiltinInstances");
    for (ARTGrammarElementTerminal t : grammar.getTerminals())
      if (t instanceof ARTGrammarElementTerminalBuiltin) gt.lexerBuiltInInstance(t.getId(), directives.s("parserName") + "." + t.toEnumerationString(null));
    gt.functionClose("artLexicaliseBuiltinInstances");
    gt.newLine();
  }

  void writeLexicalisePreparseWhitespaceInstances() {
    gt.functionVoidOpen("artLexicalisePreparseWhitespaceInstances");
    for (ARTGrammarElement b : grammar.getWhitespaces())
      if (b instanceof ARTGrammarElementTerminalBuiltin) gt.lexerWhitespaceBuiltinInstance(((ARTGrammarElementTerminalBuiltin) b).getId());

    gt.functionClose("artLexicalisePreparseWhitespaceInstances");
    gt.newLine();
  }

  void writeCodeBodiesGLL() {
    for (ARTGrammarElementNonterminal n : grammar.getNonterminals())
      // if (directives.algorithmMode() == ARTModeAlgorithm.mgllGeneratorPool && grammar.getParaterminals().contains(n))
      // gt.getText().println(" // Skipped paraterminal " + n + "\n");
      // else
      writeCodeGLLRec(n.lhsInstance, null);
  }

  void writeParseGLLGenerated() {
    if (directives.despatchMode() == ARTModeDespatch.fragment) {
      // Write fragment forward declarations
      for (ARTGrammarElementNonterminal n : grammar.getNonterminals())
        gt.functionVoidForward("ARTPF_" + n.toString());

      // Now write our the fragments
      writeCodeBodiesGLL();
    }

    gt.functionVoidOpen("artParseBody", gt.integerName(), "artStartLabel");
    if (directives.algorithmMode() == ARTModeAlgorithm.gllTWEGeneratorPool) {
      gt.indent();
      gt.getText().printf(
          "tweSet = new ARTLexerV3(artHigher, artLonger, artInputString, artInputString.length(), ARTL_EOS, artLabelInternalStrings, artDirectives, %d);\n\n",
          666);
      gt.indent();
      gt.getText().printf("tweSet.tweSetUpdateExactMakeLeftSet(0, artInputString.length(), artInputString.length() + 1); // Add terminating EOS");
    }
    gt.functionAssignCall("artSetupCompleteTime", "artReadClock");
    gt.assignString("artSpecificationName", grammar.getId());
    gt.assign("artStartSymbolLabel", "artStartLabel");
    gt.assign("artIsInLanguage", "false");
    Integer x = grammar.getLastNonterminalElementNumber() + 1;
    gt.assign("artTokenExtent", x.toString());
    if (directives.algorithmMode() == ARTModeAlgorithm.gllTWEGeneratorPool) gt.functionCall("artLexicaliseForV3GLL", "artInputString", "null");
    if (directives.algorithmMode() == ARTModeAlgorithm.gllGeneratorPool) gt.functionCall("artLexicaliseForV3GLL", "artInputString", null);
    if (directives.algorithmMode() == ARTModeAlgorithm.mgllGeneratorPool) {
      // gt.getText().println("ARTTRACE = true;\n ARTTRACETWE = true;\n");
      gt.functionCall("artLexBuildTriplesFromFile", "\"ARTTWE.twe\"");
      gt.functionCall("artLexBuildSuccessorSets");
    }
    gt.functionAssignCall("artLexCompleteTime", "artReadClock");

    if (mgllOrGllGeneratorPool) gt.functionAssignCall("artDummySPPFNode", "artFindSPPFInitial", "ARTL_DUMMY", "0", "0");
    if (mgllOrGllGeneratorPool) gt.assign("artCurrentSPPFNode", "artDummySPPFNode");
    findGSSInitialSelect();
    gt.assign("artCurrentGSSNode", rootGSSNodeSelect());
    if (directives.algorithmMode() == ARTModeAlgorithm.mgllGeneratorPool) gt.functionCall("artLoadDescriptorInitialMGLL");

    if (directives.despatchMode() == ARTModeDespatch.state || directives.despatchMode() == ARTModeDespatch.fragment
        || directives.despatchMode() == ARTModeDespatch.static_) {
      if (directives.algorithmMode() == ARTModeAlgorithm.mgllGeneratorPool)
        gt.assign("artCurrentRestartLabel", "ARTX_DESPATCH");
      else
        gt.assign("artCurrentRestartLabel", "artStartSymbolLabel");
    }
    gt.assign("artCurrentInputPairIndex", "0");
    gt.assign("artCurrentInputPairReference", "0");

    grammar.getDefaultStartNonterminal().lhsInstance.isCodeLabel = true;
    if (directives.despatchMode() == ARTModeDespatch.static_) {
      gt.indent();
      jumpSelect("ARTX_DESPATCH_SWITCH");
      gt.indentUp();
    }

    if (directives.despatchMode() == ARTModeDespatch.dynamic) {
      gt.indent();
      jumpSelect(grammar.getDefaultStartNonterminal().lhsInstance.toEnumString("L"));
      gt.indentUp();
    }

    if (directives.despatchMode() == ARTModeDespatch.state) {
      gt.whileTrue("artSelectState");
      gt.caseOpen(directives.despatchMode() == ARTModeDespatch.state ? "artCurrentRestartLabel" : "artlhsL[artCurrentRestartLabel]");
      gt.indentUp();
    }

    if (directives.despatchMode() == ARTModeDespatch.fragment) {
      gt.whileTrue(null);
      gt.caseOpen(directives.despatchMode() == ARTModeDespatch.state ? "artCurrentRestartLabel" : "artlhsL[artCurrentRestartLabel]");
      gt.indentUp();
      for (ARTGrammarElementNonterminal n : grammar.getNonterminals())
        if (n.lhsInstance != null) {
          if (directives.algorithmMode() == ARTModeAlgorithm.mgllGeneratorPool && grammar.getParaterminals().contains(n))
            gt.getText().println("   // Skipping paraterminal " + n);
          else {
            parserBlockOpenSelect(n.lhsInstance.toEnumString("L"), null);
            gt.functionCall(n.toEnumerationString("PF"));
            gt.brk();
          }
        }
    } else
      writeCodeBodiesGLL();

    parserBlockOpenSelect("ARTX_DESPATCH", null);
    gt.ifFunction("artNoDescriptors");
    gt.blockOpen();
    gt.newLine();
    gt.indentUp();
    if (mgllOrGllGeneratorPool) gt.functionCall("artCheckAcceptance");

    gt.functionAssignCall("artParseCompleteTime", "artReadClock");
    gt.functionAssignCall("artParseEndMemory", "artMemoryUsed");

    gt.ret();
    gt.indentDown();
    gt.indent();
    gt.blockClose();
    if (directives.algorithmMode() == ARTModeAlgorithm.mgllGeneratorPool)
      gt.functionCall("artUnloadDescriptorMGLL");
    else
      gt.functionCall("artUnloadDescriptor");

    if (directives.despatchMode() == ARTModeDespatch.static_) printStaticSwitch();

    if (directives.despatchMode() == ARTModeDespatch.state || directives.despatchMode() == ARTModeDespatch.fragment) {
      gt.caseClose("artCurrentRestartLabel", true);
    }

    gt.indentDown();
    gt.functionClose("artParse");
    gt.newLine();
  }

  void writeSetInitialisation(HashMap<Set<ARTGrammarElement>, Integer> mergedSets) {
    for (Set<ARTGrammarElement> set : mergedSets.keySet()) {
      // System.out.println("Writing merged set " + mergedSets.get(set) + " to parser" + set);
      gt.functionVoidOpen("ARTSet" + mergedSets.get(set) + "initialise");

      gt.allocateBooleanArray("ARTSet" + mergedSets.get(set), "artSetExtent");
      gt.functionCall("artInitialiseBooleanArray", "ARTSet" + mergedSets.get(set), "0", "artSetExtent", "false");

      for (ARTGrammarElement e : set) {
        if (e instanceof ARTGrammarElementTerminal) {
          // System.out.printf("Writing merged set element %s number %d to parser%n", e.toString(),

          // e.getElementNumber());
          if (e.equals(grammar.getEoS()))
            gt.assignBooleanArrayElement("ARTSet" + mergedSets.get(set), ((ARTGrammarElementTerminal) e).toEnumerationString("X"), "true");
          else
            gt.assignBooleanArrayElement("ARTSet" + mergedSets.get(set), ((ARTGrammarElementTerminal) e).toEnumerationString(null), "true");
        } else if (e instanceof ARTGrammarElementNonterminal)
          gt.assignBooleanArrayElement("ARTSet" + mergedSets.get(set), ((ARTGrammarElementNonterminal) e).toEnumerationString(null), "true");
      }
      gt.functionClose("ARTSet" + mergedSets.get(set) + "initialise");
      gt.newLine();
    }
    gt.functionVoidOpen("artSetInitialise");
    for (Set<ARTGrammarElement> element : mergedSets.keySet())
      gt.functionCall("ARTSet" + mergedSets.get(element) + "initialise");
    gt.functionClose("artSetInitialise");
    gt.newLine();
  }

  void printLookupTablesNewRec(ARTGrammarInstance node) {
    // printf("printLookupTablesNewRec() at node %d labelled %s\n", graph_atom_number(node), node.id);

    // Care - only slots are in the SPPF for RHS nodes, so attach the fold to the slot AFTER the element. Watch out for initial elements and for #!
    if (node.fold != ARTFold.EMPTY) gt.assignIntegerArrayElement("artFolds", node.getSibling().toEnumString("L"), "ARTFOLD_" + node.fold.name());

    if (node.isSPPFLabel || node.isCodeLabel || node.isReferredLabel || node.isTestRepeatLabel) {
      if (node.isLHS)
        gt.assignStringArrayElement("artLabelInternalStrings", node.toEnumString("L"), ARTText.toLiteralString(node.getPayload().toString()));
      else
        gt.assignStringArrayElement("artLabelInternalStrings", node.toEnumString("L"), ARTText.toLiteralString(node.toGrammarString(".")));

      if (node.isLHS) {
        String alias = grammar.getParaterminalAliases().get(node.getPayload());
        gt.assignStringArrayElement("artLabelStrings", node.toEnumString("L"), alias == null ? ARTText.toLiteralString(node.getPayload().toString()) : alias);
      } else
        gt.assignStringArrayElement("artLabelStrings", node.toEnumString("L"), "");

      if (node.isFfCE) gt.assignStringArrayElement("artLabelInternalStrings", node.toEnumString("N"), node.toGrammarString(":"));
    }

    if (node.getLhsL() != node) if (node.isCodeLabel || node.isSPPFLabel || node.isReferredLabel || node.isTestRepeatLabel || node.isSlotParentLabel
        || node.isClosureLabel || node.isAltLabel) {
          if (node.isClosureLabel)
            gt.assignEnumerationArrayElement("artlhsL", node.toEnumString("C"), node.getLhsL().toEnumString("L"));

          else if (node.isAltLabel)
            gt.assignEnumerationArrayElement("artlhsL", node.toEnumString("A"), node.getLhsL().toEnumString("L"));

          else
            gt.assignEnumerationArrayElement("artlhsL", node.toEnumString("L"), node.getLhsL().toEnumString("L"));
        }

    if (node.isSPPFLabel || node.isCodeLabel || node.isReferredLabel || node.isTestRepeatLabel) {
      if (node instanceof ARTGrammarInstanceSlot && node.instanceName != null)
        gt.assignIntegerArrayElement("artUserNameOfs", node.toEnumString("L"), node.instanceName);

      if (node instanceof ARTGrammarInstanceSlot && node.niL != node) {
        // The niL node is a reference to a preceding nonterminal instance. We have to look up the lhs symbol node for that nonterminal
        gt.assignIntegerArrayElement("artSlotInstanceOfs", node.toEnumString("L"),
            ((ARTGrammarElementNonterminal) node.niL.getPayload()).lhsInstance.toEnumString("L"));
      }

      if (node instanceof ARTGrammarInstanceSlot) gt.assignIntegerArrayElement("artKindOfs", node.toEnumString("L"), "ARTK_INTERMEDIATE");

      if (node instanceof ARTGrammarInstanceLHS) gt.assignIntegerArrayElement("artKindOfs", node.toEnumString("L"), "ARTK_NONTERMINAL");

      if (node.pL != node) gt.assignEnumerationArrayElement("artpL", node.toEnumString("L"), node.pL.toEnumString("L"));

      if (node.aL != node) gt.assignEnumerationArrayElement("artaL", node.toEnumString("L"), node.aL != null ? node.aL.toEnumString("L") : "ARTX_LABEL_EXTENT");

      if (node.isEoOP) gt.assignEnumerationArrayElement("arteoOPL", node.toEnumString("L"), "true");

      if (node.isFiR) gt.assignEnumerationArrayElement("artfiRL", node.toEnumString("L"), "true");

      if (node.isFfCE) {
        gt.assignEnumerationArrayElement("artfiPCL", node.toEnumString("L"), "true");

        gt.assignEnumerationArrayElement("artcolonL", node.toEnumString("L"), node.toEnumString("N"));
      }

      if (node.isEoR) gt.assignEnumerationArrayElement("arteoRL", node.toEnumString("L"), "true");

      if (node.pL.isEoR) gt.assignEnumerationArrayElement("arteoR_pL", node.toEnumString("L"), "true");

      if (node.isPopD) gt.assignEnumerationArrayElement("artPopD", node.toEnumString("L"), "true");
    }

    for (ARTGrammarInstance child = node.getChild(); child != null; child = child.getSibling())
      printLookupTablesNewRec(child);
  }

  void writeTableInitialise() {
    for (ARTGrammarElementNonterminal n : grammar.getNonterminals()) {
      gt.functionVoidOpen(ARTText.toIdentifier("artTableInitialiser_" + n.getModule().getId() + "_" + n.getId()));
      printLookupTablesNewRec(n.lhsInstance);
      gt.functionClose(ARTText.toIdentifier("artTableInitialiser_" + n.getModule().getId() + "_" + n.getId()));
      gt.newLine();
    }

    // Do the allocations and special cases
    gt.functionVoidOpen("artTableInitialise");
    gt.allocateStringArray("artLabelInternalStrings", "ARTX_LABEL_EXTENT + 1");
    gt.allocateStringArray("artLabelStrings", "ARTX_LABEL_EXTENT + 1");
    gt.assignStringArrayElement("artLabelInternalStrings", "ARTL_EOS", "ART$");
    gt.assignStringArrayElement("artLabelStrings", "ARTL_EOS", " EOS $");
    gt.assignStringArrayElement("artLabelInternalStrings", "ARTX_DESPATCH", "ARTX_DESPATCH");
    gt.assignStringArrayElement("artLabelStrings", "ARTX_DESPATCH", " DESPATCH");
    gt.assignStringArrayElement("artLabelInternalStrings", "ARTL_DUMMY", "ARTL_DUMMY");
    gt.assignStringArrayElement("artLabelStrings", "ARTL_DUMMY", " DUMMY");
    gt.assignStringArrayElement("artLabelInternalStrings", "ARTX_LABEL_EXTENT", "!!ILLEGAL!!");
    gt.assignStringArrayElement("artLabelStrings", "ARTX_LABEL_EXTENT", " ILLEGAL");
    gt.assignStringArrayElement("artLabelStrings", "ARTL_EPSILON", "#");
    gt.assignStringArrayElement("artLabelInternalStrings", "ARTL_EPSILON", "#");
    gt.newLine();
    gt.allocateBooleanArray("artTerminalRequiresWhiteSpace", "ARTL_EPSILON");
    gt.functionCall("artInitialiseBooleanArray", "artTerminalRequiresWhiteSpace", "0", "ARTL_EPSILON", "false");
    gt.newLine();
    gt.allocateBooleanArray("artTerminalCaseInsensitive", "ARTL_EPSILON");
    gt.functionCall("artInitialiseBooleanArray", "artTerminalCaseInsensitive", "0", "ARTL_EPSILON", "false");
    gt.newLine();

    gt.allocateEnumerationArray("artLabel", "artlhsL", "ARTX_LABEL_EXTENT");
    gt.functionCall("artInitialiseIntegerArray", "artlhsL", "0", "ARTX_LABEL_EXTENT");
    gt.assignIntegerArrayElement("artlhsL", "ARTX_DESPATCH", "ARTX_DESPATCH");
    gt.newLine();
    gt.allocateIntegerArray("artKindOfs", "ARTX_LABEL_EXTENT + 1");
    gt.assignIntegerArrayElement("artKindOfs", "ARTL_EOS", "ARTK_EOS");
    gt.assignIntegerArrayElement("artKindOfs", "ARTL_EPSILON", "ARTK_EPSILON");
    gt.newLine();
    gt.allocateBitSetArray("artHigher", "ARTX_LABEL_EXTENT + 1");

    ARTChooserSet chooserSet = grammar.getChooserSet("");

    for (int h = 0; h < chooserSet.higher.length; h++)
      for (int e = 0; e < chooserSet.higher.length; e++)
        if (chooserSet.higher[h] != null && chooserSet.higher[h].get(e))
          gt.assignBitSetArrayElement("artHigher", grammar.getElementNumberMap().get(h).toEnumerationString(null),
              grammar.getElementNumberMap().get(e).toEnumerationString(null), "ARTX_LABEL_EXTENT + 1");

    for (ARTGrammarInstanceSlot k : grammar.getDerivationHigher().keySet())
      for (ARTGrammarInstanceSlot kk : grammar.getDerivationHigher().get(k))
        gt.assignBitSetArrayElement("artHigher", k.toEnumString("L"), kk.toEnumString("L"), "ARTX_LABEL_EXTENT + 1");

    gt.newLine();

    gt.allocateBitSetArray("artLonger", "ARTX_LABEL_EXTENT + 1");
    for (int l = 0; l < chooserSet.longer.length; l++)
      for (int e = 0; e < chooserSet.longer.length; e++)
        if (chooserSet.longer[l] != null && chooserSet.longer[l].get(e))
          gt.assignBitSetArrayElement("artLonger", grammar.getElementNumberMap().get(l).toEnumerationString(null),
              grammar.getElementNumberMap().get(e).toEnumerationString(null), "ARTX_LABEL_EXTENT + 1");

    for (ARTGrammarInstanceSlot k : grammar.getDerivationLonger().keySet())
      for (ARTGrammarInstanceSlot kk : grammar.getDerivationLonger().get(k))
        gt.assignBitSetArrayElement("artLonger", k.toEnumString("L"), kk.toEnumString("L"), "ARTX_LABEL_EXTENT + 1");

    gt.newLine();

    gt.allocateBitSetArray("artShorter", "ARTX_LABEL_EXTENT + 1");
    for (int l = 0; l < chooserSet.shorter.length; l++)
      for (int e = 0; e < chooserSet.shorter.length; e++)
        if (chooserSet.shorter[l] != null && chooserSet.shorter[l].get(e))
          gt.assignBitSetArrayElement("artShorter", grammar.getElementNumberMap().get(l).toEnumerationString(null),
              grammar.getElementNumberMap().get(e).toEnumerationString(null), "ARTX_LABEL_EXTENT + 1");

    for (ARTGrammarInstanceSlot k : grammar.getDerivationShorter().keySet())
      for (ARTGrammarInstanceSlot kk : grammar.getDerivationShorter().get(k))
        gt.assignBitSetArrayElement("artShorter", k.toEnumString("L"), kk.toEnumString("L"), "ARTX_LABEL_EXTENT + 1");

    gt.newLine();

    if (mgllOrGllGeneratorPool || directives.algorithmMode() == ARTModeAlgorithm.gllTWEGeneratorPool) {
      gt.allocateEnumerationArray("artLabel", "artPreSlots", "ARTX_LABEL_EXTENT");
      gt.functionCall("artInitialiseIntegerArray", "artPreSlots", "0", "ARTX_LABEL_EXTENT");
      gt.newLine();
      gt.allocateEnumerationArray("artLabel", "artPostSlots", "ARTX_LABEL_EXTENT");
      gt.functionCall("artInitialiseIntegerArray", "artPostSlots", "0", "ARTX_LABEL_EXTENT");
      gt.newLine();
      gt.allocateEnumerationArray("artLabel", "artInstanceOfs", "ARTX_LABEL_EXTENT");
      gt.functionCall("artInitialiseIntegerArray", "artInstanceOfs", "0", "ARTX_LABEL_EXTENT");
      gt.newLine();
      gt.allocateEnumerationArray("artLabel", "artSlotInstanceOfs", "ARTX_LABEL_EXTENT");
      gt.functionCall("artInitialiseIntegerArray", "artSlotInstanceOfs", "0", "ARTX_LABEL_EXTENT");
      gt.newLine();
      gt.allocateEnumerationArray("artLabel", "artUserNameOfs", "ARTX_LABEL_EXTENT + 1");
      gt.newLine();
      gt.allocateEnumerationArray("artLabel", "artGathers", "ARTX_LABEL_EXTENT");
      gt.functionCall("artInitialiseIntegerArray", "artGathers", "0", "ARTX_LABEL_EXTENT");
      gt.newLine();
      gt.allocateEnumerationArray("artFold", "artFolds", "ARTX_LABEL_EXTENT");
      gt.functionCall("artInitialiseIntegerArray", "artFolds", "0", "ARTX_LABEL_EXTENT", "0");
      gt.newLine();
      gt.allocateEnumerationArray("artLabel", "artpL", "ARTX_LABEL_EXTENT");
      gt.functionCall("artInitialiseIntegerArray", "artpL", "0", "ARTX_LABEL_EXTENT");
      gt.newLine();
      gt.allocateEnumerationArray("artLabel", "artaL", "ARTX_LABEL_EXTENT");
      gt.functionCall("artInitialiseIntegerArray", "artaL", "0", "ARTX_LABEL_EXTENT");
      gt.newLine();
      gt.allocateEnumerationArray("artLabel", "artcolonL", "ARTX_LABEL_EXTENT");
      gt.functionCall("artInitialiseIntegerArray", "artcolonL", "0", "ARTX_LABEL_EXTENT");
      gt.newLine();
      gt.allocateBooleanArray("arteoOPL", "ARTX_LABEL_EXTENT");
      gt.functionCall("artInitialiseBooleanArray", "arteoOPL", "0", "ARTX_LABEL_EXTENT", "false");
      gt.newLine();
      gt.allocateBooleanArray("artfiRL", "ARTX_LABEL_EXTENT");
      gt.functionCall("artInitialiseBooleanArray", "artfiRL", "0", "ARTX_LABEL_EXTENT", "false");
      gt.newLine();
      gt.allocateBooleanArray("artfiPCL", "ARTX_LABEL_EXTENT");
      gt.functionCall("artInitialiseBooleanArray", "artfiPCL", "0", "ARTX_LABEL_EXTENT", "false");
      gt.newLine();
      gt.allocateBooleanArray("arteoRL", "ARTX_LABEL_EXTENT");
      gt.functionCall("artInitialiseBooleanArray", "arteoRL", "0", "ARTX_LABEL_EXTENT", "false");
      gt.newLine();
      gt.allocateBooleanArray("arteoR_pL", "ARTX_LABEL_EXTENT");
      gt.functionCall("artInitialiseBooleanArray", "arteoR_pL", "0", "ARTX_LABEL_EXTENT", "false");
      gt.newLine();
      gt.allocateBooleanArray("artPopD", "ARTX_LABEL_EXTENT");
      gt.functionCall("artInitialiseBooleanArray", "artPopD", "0", "ARTX_LABEL_EXTENT", "false");
      gt.newLine();
    }

    // Call the individual nonterminal's initialisers and initislaise strings for terminals
    for (ARTGrammarElementTerminal t : grammar.getTerminals()) {
      gt.assignStringArrayElement("artLabelStrings", t.toEnumerationString(null), ARTText.toLiteralString(t.getId()));
      gt.assignStringArrayElement("artLabelInternalStrings", t.toEnumerationString(null), ARTText.toLiteralString(t.toString()));

      if (t instanceof ARTGrammarElementTerminalBuiltin) gt.assignIntegerArrayElement("artKindOfs", t.toEnumerationString(null), "ARTK_BUILTIN_TERMINAL");

      if (t instanceof ARTGrammarElementTerminalCharacter) gt.assignIntegerArrayElement("artKindOfs", t.toEnumerationString(null), "ARTK_CHARACTER_TERMINAL");

      if (t instanceof ARTGrammarElementTerminalCaseSensitive)
        gt.assignIntegerArrayElement("artKindOfs", t.toEnumerationString(null), "ARTK_CASE_SENSITIVE_TERMINAL");

      if (t instanceof ARTGrammarElementTerminalCaseInsensitive)
        gt.assignIntegerArrayElement("artKindOfs", t.toEnumerationString(null), "ARTK_CASE_INSENSITIVE_TERMINAL");

      if (t instanceof ARTGrammarElementTerminalBuiltin || t instanceof ARTGrammarElementTerminalCaseSensitive
          || t instanceof ARTGrammarElementTerminalCaseInsensitive)
        gt.assignBooleanArrayElement("artTerminalRequiresWhiteSpace", t.toEnumerationString(null), "true");

      if (t instanceof ARTGrammarElementTerminalCaseInsensitive)
        gt.assignBooleanArrayElement("artTerminalCaseInsensitive", t.toEnumerationString(null), "true");
    }
    for (ARTGrammarElementNonterminal n : grammar.getNonterminals()) {
      gt.functionCall(ARTText.toIdentifier("artTableInitialiser_" + n.getModule().getId() + "_" + n.getId()));
      if (n.isLexical) gt.assignBooleanArrayElement("artisLexical", n.toEnumerationString(), "true");
    }

    gt.functionClose("artTableInitialise");
    gt.newLine();
  }

  void writeConstructor(String id) {
    gt.constructorOpen(id, "ARTLexerV3", "artLexer");
    gt.functionCall("this", "null", "artLexer");
    gt.constructorClose(id);
    gt.newLine();

    gt.constructorOpen(id, "ARTGrammar", "artGrammar", "ARTLexerV3", "artLexer");
    gt.functionCall("super", "artGrammar", "artLexer");
    gt.assignString("artParserKind",
        directives.algorithmMode() == ARTModeAlgorithm.mgllGeneratorPool ? "MGLL Gen"
            : directives.algorithmMode() == ARTModeAlgorithm.gllGeneratorPool ? "GLL Gen"
                : directives.algorithmMode() == ARTModeAlgorithm.gllTWEGeneratorPool ? "GLLTWE Gen" : "Unkown generated algorithm");

    // First go through the statically visible functions and make sure their values are appropriately labelled
    updateIsReferredLabelRec((ARTGrammarInstance) grammar.getInstanceTree().getRoot());

    if (artFirstTerminal == null)
      gt.assign("artFirstTerminalLabel", "ARTX_EPSILON");
    else
      gt.assign("artFirstTerminalLabel", artFirstTerminal.toEnumerationString(null));
    gt.assign("artFirstUnusedLabel", "ARTX_LABEL_EXTENT + 1");
    Integer tmp = grammar.getLastNonterminalElementNumber() + 1;
    gt.assign("artSetExtent", tmp.toString());
    gt.assign("ARTL_EOS", "ARTX_EOS");
    gt.assign("ARTL_EPSILON", "ARTX_EPSILON");
    gt.assign("ARTL_DUMMY", "ARTX_DUMMY");
    gt.assign("artGrammarKind", "ARTModeGrammarKind." + grammar.getGrammarKind().toString());

    gt.assign("artDefaultStartSymbolLabel", grammar.getDefaultStartNonterminal().toEnumerationString("L"));

    gt.assignString("artBuildDirectives", ARTText.toLiteralString(directives.toString()));

    gt.assign("artFIFODescriptors", directives.b("FIFODescriptors") ? "true" : "false");

    // Call initialisers
    gt.functionCall("artSetInitialise");
    gt.functionCall("artTableInitialise");
    gt.constructorClose(id);
    gt.newLine();
  }

  /***********************************************************************************************************************************************************
   *
   * Attribute evaluator code below here
   *
   ***********************************************************************************************************************************************************/

  void writeRDEvaluatorInstanceName(ARTGrammarInstance node) {
    if (node.instanceName != null)
      gt.text.printf("%s", node.instanceName);
    else
      gt.text.print(node.instanceString());
  }

  /* Print the code to handle RDT's at the four different levels */
  void writeRDEvaluatorLabelArguments(ARTGrammarInstance iftNode, boolean isLeftChild, String attributes) {
    gt.text.printf(
        "new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNode%sChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNode%sChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNode%sChild(artPackedNode)), %s)",
        isLeftChild ? "Left" : "Right", isLeftChild ? "Left" : "Right", isLeftChild ? "Left" : "Right", attributes);
  }

  boolean writeRDEvaluatorTreeNode(ARTGrammarInstance iftNode, boolean isNonterminal, boolean isLeftChild, boolean hasAttributes) {
    // System.err.printf("writeRDEvaluatorTreeNode(node %d, %s, %s, %s)\n", iftNode.getKey(), isNonterminal ? "isNonterminal" : "",
    // isLeftChild ? "isLeftChild" : "", hasAttributes ? "hasAttributes" : "");
    // return false iff a node has been single hatted away
    switch (directives.i("treePrintLevel")) {
    case 0: /* no tree to be generated */
      return true;
    case 1: /* basic derivation tree */
      gt.indent();
      if (isNonterminal) gt.text.printf("artNewParent = ");
      gt.text.printf("artParent.addChild(artNextFreeNodeNumber++, ");
      writeRDEvaluatorLabelArguments(iftNode, isLeftChild, hasAttributes ? iftNode.instanceString() : "null");
      gt.text.printf(");\n");
      break;
    case 2: { /* Basic derivation tree but showing GIFT annotations in the labels */
      gt.indent();
      if (isNonterminal) gt.text.printf("artNewParent = ");
      gt.text.printf("artParent.addChild(artNextFreeNodeNumber++, ");
      writeRDEvaluatorLabelArguments(iftNode, isLeftChild, hasAttributes ? iftNode.instanceString() : "null");
      gt.text.printf(");\n");
    }
      break;
    case 3: {
      gt.indent();
      switch (iftNode.fold) {
      case EMPTY:
      case NONE:
        if (isNonterminal) gt.text.printf("artNewWriteable = true; artNewParent = ");
        gt.text.printf("artParent.addChild(artNextFreeNodeNumber++, ");
        writeRDEvaluatorLabelArguments(iftNode, isLeftChild, hasAttributes ? iftNode.instanceString() : "null");
        gt.text.printf(");\n");
        break;

      case UNDER:
        if (isNonterminal) gt.text.printf("artNewWriteable = false; artNewParent = artParent;\n");
        return false;

      case OVER:
        if (isNonterminal) gt.text.printf("artNewWriteable = artWriteable; artNewParent = artParent;");
        gt.indent();
        gt.text.printf("if (artWriteable) artParent.setPayload(");
        writeRDEvaluatorLabelArguments(iftNode, isLeftChild, hasAttributes ? iftNode.instanceString() : "null");
        gt.text.printf(");\n");
        break;

      case TEAR:
        throw new ARTUncheckedException("Tree builder: tear not yet implemented");

      default:
        throw new ARTUncheckedException("Tree builder: unknown fold value found whilst writing tree builder");
      }
    }
      break;
    default:
      throw new ARTUncheckedException("Tree builder: unknown tree level");
    }

    return true;
  }

  void writeRDEvaluatorLocalDefines(ARTGrammarElementNonterminal sym, boolean useNulls) {
    // Reset counters
    terminalInstanceNumber = 0;
    for (ARTGrammarElementNonterminal n : grammar.getNonterminals())
      n.nextInstanceNumber = 0;

    writeRDEvaluatorCollectDeclarationsRec(sym.lhsInstance);

    for (ARTGrammarElementNonterminal n : grammar.getNonterminals())
      for (int tmp = 1; tmp <= n.nextInstanceNumber; tmp++) {
        if (useNulls)
          gt.text.printf(", null");
        else
          gt.text.printf(", ARTAT_%s_%s %s%d", n.getModule().getId(), n.getId(), n.getId(), tmp);
      }
  }

  void writeRDEvaluatorLocalUses(ARTGrammarElementNonterminal sym) {
    // Reset counters
    terminalInstanceNumber = 0;
    for (ARTGrammarElementNonterminal n : grammar.getNonterminals())
      n.nextInstanceNumber = 0;

    writeRDEvaluatorCollectDeclarationsRec(sym.lhsInstance);

    for (ARTGrammarElementNonterminal n : grammar.getNonterminals())
      for (int tmp = 1; tmp <= n.nextInstanceNumber; tmp++) {
        gt.text.printf(", %s%d", n.getId(), tmp);
      }
  }

  void writeRDEvaluatorLocalNull(ARTGrammarElementNonterminal sym) {
    // Reset counters
    terminalInstanceNumber = 0;
    for (ARTGrammarElementNonterminal n : grammar.getNonterminals())
      n.nextInstanceNumber = 0;

    writeRDEvaluatorCollectDeclarationsRec(sym.lhsInstance);

    for (ARTGrammarElementNonterminal n : grammar.getNonterminals())
      for (int tmp = 1; tmp <= n.nextInstanceNumber; tmp++) {
        gt.text.printf(", %s", gt.nullName());
      }
  }

  void writeRDEvaluatorAttributeUse(ARTGrammarElementNonterminal sym, int instanceNumber) {
    if (!sym.getAttributes().isEmpty() || sym.isContainsDelayedInstances()) {
      gt.text.printf(", %s", sym.getId());
      if (instanceNumber != 0) gt.text.printf("%d", instanceNumber);
    }
  }

  void writeRDEvaluatorLocalAllocates(ARTGrammarElementNonterminal sym) {
    // Reset counters
    terminalInstanceNumber = 0;
    for (ARTGrammarElementNonterminal n : grammar.getNonterminals())
      n.nextInstanceNumber = 0;

    writeRDEvaluatorCollectDeclarationsRec(sym.lhsInstance);

    for (ARTGrammarElementNonterminal n : grammar.getNonterminals())
      for (int tmp = 1; tmp <= n.nextInstanceNumber; tmp++) {
        gt.indent();
        gt.text.printf("%s%d = new ARTAT_%s_%s();\n", n.getId(), tmp, n.getModule().getId(), n.getId());
      }
  }

  void writeRDEvaluatorCallInternal(ARTGrammarElementNonterminal sym, String element) {
    gt.indent();
    gt.text.printf("ARTRD_%s(%s, artParent, artWriteable", ARTText.toIdentifier(sym.getId()), element);
    writeRDEvaluatorAttributeUse(sym, 0);
    writeRDEvaluatorLocalUses(sym);
    gt.text.printf(");\n");
  }

  void writeRDEvaluatorCallTopLevel(ARTGrammarElementNonterminal sym, String element, int instanceNumber) {
    gt.indent();
    gt.text.printf("ARTRD_%s(%s, artNewParent, artNewWriteable", ARTText.toIdentifier(sym.getId()), element);
    writeRDEvaluatorAttributeUse(sym, instanceNumber);
    writeRDEvaluatorLocalNull(sym);
    gt.text.printf(");\n");
  }

  void writeRDEvaluatorProcessSemantics(ARTGrammarInstanceSlot node) {
    gt.indent();
    // cust.text.printf("System.out.println(\"");
    // cust.text.printf("%s", node.toGrammarSlotString("."));
    // cust.text.printf("\");\n");

    for (ARTGrammarInstance child = node.getChild(); child != null; child = child.getSibling()) {
      if (child instanceof ARTGrammarInstanceAction && !directives.b("suppressSemantics")) gt.text.printf("%s\n", child.getChild().getPayload());
    }
  }

  void writeRDEvaluatorProcessLeaf(ARTGrammarInstance node, boolean isLeftChild, ARTGrammarInstanceLHS lhsNode) {
    if (node instanceof ARTGrammarInstanceEpsilon) {
      writeRDEvaluatorTreeNode(node, false, isLeftChild, false);
      // cust.indent();
      // cust.text.printf("artText.println(\"#, %s\");\n", isLeftChild ? "Left" : "Right");
    } else if (node instanceof ARTGrammarInstanceTerminal) {
      writeRDEvaluatorTreeNode(node, false, isLeftChild, false);
      // cust.indent();
      // cust.text.printf("artText.println(\"%s, %s\");\n", ((ARTGrammarElementTerminal) node.getPayload()).toEnumerationString(), isLeftChild ? "Left" :
      // "Right");
    } else if (node instanceof ARTGrammarInstanceNonterminal) {
      ARTGrammarInstanceNonterminal n = (ARTGrammarInstanceNonterminal) node;
      ARTGrammarElementNonterminal nE = (ARTGrammarElementNonterminal) n.getPayload();
      // cust.indent();
      // cust.text.printf("artText.println(\"Entering %s, %s\");\n", n.toString(), isLeftChild ? "Left" : "Right");
      for (ARTGrammarElementAttribute tmp : nE.getAttributes()) {
        if (tmp.getId().equals("leftExtent")) {
          gt.indent();
          writeRDEvaluatorInstanceName(node);
          gt.text.printf(".leftExtent = artSPPFNodeLeftExtent(%s);\n",
              isLeftChild ? "artSPPFPackedNodeLeftChild(artPackedNode)" : "artSPPFPackedNodeRightChild(artPackedNode)");
        }
        if (tmp.getId().equals("rightExtent")) {
          gt.indent();
          writeRDEvaluatorInstanceName(node);
          gt.text.printf(".rightExtent = artSPPFNodeRightExtent(%s);\n",
              isLeftChild ? "artSPPFPackedNodeLeftChild(artPackedNode)" : "artSPPFPackedNodeRightChild(artPackedNode)");
        }
      }

      writeRDEvaluatorTreeNode(node, true, isLeftChild, !((ARTGrammarElementNonterminal) node.getPayload()).getAttributes().isEmpty());
      if (node.isDelayed) {
        gt.indent();
        gt.text.printf("%s.", ((ARTGrammarElementNonterminal) lhsNode.getPayload()).getId());
        writeRDEvaluatorInstanceName(node);
        gt.text.printf(" = new ARTGLLRDTHandle(%s);\n",
            isLeftChild ? "artSPPFPackedNodeLeftChild(artPackedNode)" : "artSPPFPackedNodeRightChild(artPackedNode)");
      } else
        writeRDEvaluatorCallTopLevel(nE,
            isLeftChild ? (String) "artSPPFPackedNodeLeftChild(artPackedNode)" : (String) "artSPPFPackedNodeRightChild(artPackedNode)",
            node.instanceNumberWithinProduction);
      // cust.indent();
      // cust.text.printf("artText.println(\"Leaving %s, %s\");\n", nE.toEnumerationString(), isLeftChild ? "Left" : "Right");
    }
  }

  void writeRDEvaluatorRec(ARTGrammarInstance iftNode, ARTGrammarElementNonterminal currentNonterminal) {
    if (!gt.targetLanguageName().equals("Java")) return;

    if (iftNode instanceof ARTGrammarInstanceLHS)
      for (ARTGrammarInstance child = iftNode.getChild(); child != null; child = child.getSibling())
        writeRDEvaluatorRec(child, (ARTGrammarElementNonterminal) iftNode.getPayload());

    else if (iftNode instanceof ARTGrammarInstanceDoFirst)
      writeRDEvaluatorRec(iftNode.getChild(), currentNonterminal);

    else if (iftNode instanceof ARTGrammarInstanceOptional)
      gt.comment("optional node");

    else if (iftNode instanceof ARTGrammarInstancePositiveClosure)
      gt.comment("positiveClosure node");

    else if (iftNode instanceof ARTGrammarInstanceKleeneClosure)
      gt.comment("kleeneClosure node");

    else if (iftNode instanceof ARTGrammarInstanceCat) {
      // Rules:
      // 1. Add a call to the semantics for X ::= . \alpha immediately before processing slot X ::= \alpha .
      // 2. There will be no case for X ::= . \alpha
      // 3. For top level cat's only, the slot X ::= y . \alpha (y \in N \cup T) is FiR
      // 4. There will be no case for FiR slots (X ::= y . \alpha (y \in N \cup T))
      // 5. For slots of the form X ::= y z . \alpha (x,y \en N \cup T) this writer function is called recursively on y and z
      // For slots that are not case 1, 4 or 5, the semantics evaluator calls itself recursively on the left child before processing the right child

      ARTGrammarInstance fiRNode = null, fiRPreviousChildNode = null, previousChildNode = null, childNode = null;

      for (childNode = iftNode.getChild().getSibling(); childNode != null; childNode = childNode.getSibling()) {

        if (childNode instanceof ARTGrammarInstanceDoFirst || childNode instanceof ARTGrammarInstanceOptional
            || childNode instanceof ARTGrammarInstancePositiveClosure || childNode instanceof ARTGrammarInstanceKleeneClosure)
          writeRDEvaluatorRec(childNode.getChild(), currentNonterminal);
        else if (childNode instanceof ARTGrammarInstanceSlot) if (childNode.isFiR) {
          fiRNode = childNode;
          fiRPreviousChildNode = previousChildNode;
        } else {
          gt.indent();
          gt.comment(childNode.toGrammarString("."));
          gt.caseBranchOpen(childNode.toEnumString("L"), false); // Rule 2 and 3: there will be a case branch for all others
          childNode.isCodeLabel = true; // This is the extra codeLabel that is missing from the enumeration

          if (childNode.getSibling() == null) { // Rule 1: initialise attribute variables and call the initial semantics before we process this slot
            writeRDEvaluatorLocalAllocates(currentNonterminal);
            writeRDEvaluatorProcessSemantics((ARTGrammarInstanceSlot) (iftNode.getChild()));
          }

          if (fiRNode != null) { // Rule 5: process y and output the semantics for the FiR node
            writeRDEvaluatorProcessLeaf(fiRPreviousChildNode, true, iftNode.getLhsL());
            writeRDEvaluatorProcessSemantics((ARTGrammarInstanceSlot) fiRNode);
            fiRNode = null; // switch off fIr processing for this cat
          } else // Not rule 5: descend left child so that we are processing RHS in postorder
            writeRDEvaluatorCallInternal(currentNonterminal, "artSPPFPackedNodeLeftChild(artPackedNode)");

          writeRDEvaluatorProcessLeaf(previousChildNode, false, iftNode.getLhsL());
          writeRDEvaluatorProcessSemantics((ARTGrammarInstanceSlot) childNode);

          gt.brk();
          gt.indentDown();
        }
        previousChildNode = childNode; // Remember left sibling of this child
      }
    }

    else if (iftNode instanceof ARTGrammarInstanceAlt)
      gt.comment("alt node");

    else if (iftNode instanceof ARTGrammarInstanceSlot)
      throw new ARTUncheckedException("Evaluator builder: unexpected SLOT node");

    else
      throw new ARTUncheckedException("Evaluator builder: unknown tree node type " + iftNode);
  }

  int terminalInstanceNumber;

  void writeRDEvaluatorCollectDeclarationsRec(ARTGrammarInstance node) {
    if (node instanceof ARTGrammarInstanceNonterminal) {
      ARTGrammarElementNonterminal nE = ((ARTGrammarElementNonterminal) node.getPayload());
      if ((!nE.getAttributes().isEmpty() || nE.isContainsDelayedInstances() || node.isDelayed) && node.instanceNumberWithinProduction > nE.nextInstanceNumber)
        nE.nextInstanceNumber = node.instanceNumberWithinProduction;
    }

    if (node instanceof ARTGrammarInstanceTerminal) {
      if (node.instanceNumberWithinProduction > terminalInstanceNumber) terminalInstanceNumber = node.instanceNumberWithinProduction;
    }

    for (ARTGrammarInstance c = node.getChild(); c != null; c = c.getSibling())
      writeRDEvaluatorCollectDeclarationsRec(c);
  }

  void writeRDEvaluatorBodies(ARTGrammarElementNonterminal sym) {
    gt.text.printf(
        "    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {\n"
            + "      if (artSPPFPackedNodeSelected(artPackedNode)) {\n" + "        switch (artSPPFPackedNodeLabel(artPackedNode)) {\n");
    gt.indentUp();
    gt.indentUp();

    writeRDEvaluatorRec(sym.lhsInstance, sym);

    if (gt.targetLanguageName().equals("Java")) gt.text.printf("        default: ; }}}\n");
    gt.indentDown();
    gt.indentDown();
    gt.functionClose(ARTText.toIdentifier("ARTRD_" + sym.getId()));
    gt.newLine();
  }

  void writeRDEvaluator() {
    // Write attribute block classes
    for (ARTGrammarElementNonterminal sym : grammar.getNonterminals()) {
      if (sym.isContainsDelayedInstances() || sym.isHasDelayedInstances() || !sym.getAttributes().isEmpty()) {
        gt.indent();
        gt.text.printf("public static class ARTAT_%s_%s extends ARTGLLAttributeBlock {\n", sym.getModule().getId(), sym.getId());
        gt.indentUp();
        for (ARTGrammarElementAttribute tmp : grammar.getSupportAttributes()) {
          gt.indent();
          gt.text.printf("public %s %s;\n", tmp.getType(), tmp.getId());
        }
        for (ARTGrammarElementAttribute tmp : sym.getAttributes()) {
          gt.indent();
          gt.text.printf("public %s %s;\n", tmp.getType(), tmp.getId());
        }

        for (ARTGrammarElementNonterminal n : grammar.getNonterminals())
          n.nextInstanceNumber = 0;

        writeRDEvaluatorCollectDeclarationsRec(sym.lhsInstance);

        for (ARTGrammarElementNonterminal thisSymbol : grammar.getNonterminals())
          for (int tmp = 1; tmp <= thisSymbol.nextInstanceNumber; tmp++) {
            gt.indent();
            gt.text.printf("ARTGLLRDTHandle %s%d;\n", thisSymbol.getId(), tmp);
          }

        gt.indent();
        gt.text.printf("public String toString() {\n");
        gt.indent();
        gt.text.printf("  String ret = \"\";\n");
        for (ARTGrammarElementAttribute tmp : sym.getAttributes()) {
          gt.indent();
          gt.text.printf("ret += \" %s=\" + %s;\n", tmp.getId(), tmp.getId());
        }

        gt.indent();
        gt.text.printf("  return ret + \" \";\n}\n");

        gt.indentDown();
        gt.indent();
        gt.text.printf("}\n\n");
      }
    }
    // Write fragment forward declarations - nothing to do for Java

    // Now write out the fragments
    for (ARTGrammarElementNonterminal sym : grammar.getNonterminals()) {
      if (sym.getAttributes().isEmpty() && !sym.isContainsDelayedInstances()) {
        gt.indent();
        gt.text.printf("public void %s(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable", "ARTRD_" + ARTText.toIdentifier(sym.getId()));
      } else {
        gt.indent();
        gt.text.printf("public void %s(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_%s_%s %s",
            ARTText.toIdentifier("ARTRD_" + sym.getId()), ARTText.toIdentifier(sym.getModule().getId()), ARTText.toIdentifier(sym.getId()),
            ARTText.toIdentifier(sym.getId()));
      }
      writeRDEvaluatorLocalDefines(sym, false);
      gt.text.printf(")  {\n");
      gt.indent();
      gt.text.printf("ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;\n");
      writeRDEvaluatorBodies(sym);

    }
    // Write out the indirect evaluator despatch function
    gt.indent();
    gt.text.printf(
        "public void artEvaluate(ARTGLLRDTHandle artElement, ARTGLLAttributeBlock artAttributes, ARTGLLRDTVertex artParent, Boolean artWriteable)  {\n");
    gt.indentUp();
    gt.indent();
    gt.text.printf("switch (artSPPFNodeLabel(artElement.element)) {\n");
    gt.indentUp();

    for (ARTGrammarElementNonterminal sym : grammar.getNonterminals()) {
      if (sym.getAttributes().isEmpty() && !sym.isContainsDelayedInstances()) {
        gt.indent();
        gt.text.printf("case %s: ", sym.toEnumerationString("L"));
        gt.text.printf("%s(artElement.element, artParent, artWriteable", ARTText.toIdentifier("ARTRD_" + sym.getId()));
      } else {
        gt.indent();
        gt.text.printf("case %s: ", sym.toEnumerationString("L"));
        gt.text.printf(" %s(artElement.element, artParent, artWriteable, (ARTAT_%s_%s) artAttributes", ARTText.toIdentifier("ARTRD_" + sym.getId()),
            ARTText.toIdentifier(sym.getModule().getId()), ARTText.toIdentifier(sym.getId()));
      }
      writeRDEvaluatorLocalDefines(sym, true);
      gt.text.printf("); break;\n");
    }

    gt.indentDown();
    gt.indent();
    gt.text.printf("}\n");
    gt.indentDown();
    gt.indent();
    gt.text.printf("}\n\n");
  }
}