package uk.ac.rhul.cs.csle.art.old.v3.manager.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import uk.ac.rhul.cs.csle.art.old.core.ARTUncheckedException;
import uk.ac.rhul.cs.csle.art.old.core.Directives;
import uk.ac.rhul.cs.csle.art.old.term.ITerms;
import uk.ac.rhul.cs.csle.art.old.term.ITermsLowLevelAPI;
import uk.ac.rhul.cs.csle.art.old.util.bitset.ARTBitSet;
import uk.ac.rhul.cs.csle.art.old.util.graph.ARTTree;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTTextHandlerFile;
import uk.ac.rhul.cs.csle.art.old.v3.alg.gll.support.ARTGLLRDTVertex;
import uk.ac.rhul.cs.csle.art.old.v3.manager.ARTManager;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElement;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementAttribute;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementEoS;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementEpsilon;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementModuleNonterminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementNonterminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminalBuiltin;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminalCaseInsensitive;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminalCaseSensitive;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminalCharacter;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstance;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceAction;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceActionValue;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceAlt;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceAnnotation;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceCat;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceDiff;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceDoFirst;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceEpsilon;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceIter;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceKleeneClosure;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceLHS;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceNonterminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceNot;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceOptional;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstancePositiveClosure;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceRoot;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceSlot;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceTear;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceTerminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.mode.ARTModeAlgorithm;
import uk.ac.rhul.cs.csle.art.old.v3.manager.mode.ARTModeGrammarKind;
import uk.ac.rhul.cs.csle.art.old.v3.manager.module.ARTV3Module;
import uk.ac.rhul.cs.csle.art.old.v3.manager.parser.ARTV3Parser.ARTAT_ART_ID;
import uk.ac.rhul.cs.csle.art.old.v3.manager.parser.ARTV3Parser.ARTAT_ART_action;
import uk.ac.rhul.cs.csle.art.old.v3.manager.parser.ARTV3Parser.ARTAT_ART_builtinTerminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.parser.ARTV3Parser.ARTAT_ART_caseInsensitiveTerminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.parser.ARTV3Parser.ARTAT_ART_caseSensitiveTerminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.parser.ARTV3Parser.ARTAT_ART_characterTerminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.parser.ARTV3Parser.ARTAT_ART_nonterminal;
import uk.ac.rhul.cs.csle.art.old.v3.value.ARTValueString;
import uk.ac.rhul.cs.csle.art.old.v3.value.ARTValueTerm;

public class ARTGrammar {
  private final ARTManager artManager;
  private final ARTV3Module ARTV3Module;
  private final String id;
  // private final ARTDirectiveBlock optionBlock;
  public final Directives artDirectives;
  private boolean isEBNF;
  private boolean isFBNF;
  private final ARTGrammarElementNonterminal unaugmentedStartNonterminal;
  private ARTGrammarElementNonterminal defaultStartNonterminal;
  String injectInstanceString = "";
  private boolean useDefaultInjectProductionString = true;
  private ARTGrammarElementNonterminal absorbNonterminal = null;
  private final Set<ARTGrammarElementNonterminal> nonterminals = new TreeSet<>();
  private final Set<ARTGrammarElementNonterminal> paraterminals = new HashSet<>();
  private final Set<ARTGrammarElementNonterminal> usedNonterminals = new HashSet<>();
  private final Map<ARTGrammarElementNonterminal, String> paraterminalAliases = new HashMap<>();
  Set<ARTGrammarElement> parserReachable = new TreeSet<>();
  Set<ARTGrammarElement> lexerReachable = new TreeSet<>();
  Set<ARTGrammarElement> injectInstanceReachable = new TreeSet<>();
  Set<ARTGrammarElement> injectInstanceError = new HashSet<>();
  private final Map<ARTName, ARTGrammarElementNonterminal> nonterminalNameMap = new HashMap<>();
  private final Set<ARTGrammarElementTerminal> terminals = new TreeSet<ARTGrammarElementTerminal>();
  private final Set<ARTGrammarElement> whitespaces = new TreeSet<ARTGrammarElement>();
  private final Map<String, ARTGrammarElementTerminalCharacter> terminalCharacterNameMap = new HashMap<>();
  private final Map<String, ARTGrammarElementTerminalCaseSensitive> terminalCaseSensitiveNameMap = new HashMap<>();
  private final Map<String, ARTGrammarElementTerminalCaseInsensitive> terminalCaseInsensitiveNameMap = new HashMap<>();
  private final Map<String, ARTGrammarElementTerminalBuiltin> terminalBuiltinNameMap = new HashMap<>();
  private final Map<Integer, ARTGrammarElement> elementNumberMap = new HashMap<>();
  private final Map<String, ARTChooserSet> chooserSets = new HashMap<>();
  private final Map<ARTGrammarInstanceSlot, Set<ARTGrammarInstanceSlot>> chooserDerivationHigher = new TreeMap<>();
  private final Map<ARTGrammarInstanceSlot, Set<ARTGrammarInstanceSlot>> chooserDerivationLonger = new TreeMap<>();
  private final Map<ARTGrammarInstanceSlot, Set<ARTGrammarInstanceSlot>> chooserDerivationShorter = new TreeMap<>();
  private final boolean isEOSFollow = false;
  private boolean isDirty = true;
  private int firstTerminalElementNumber;
  private int lastTerminalElementNumber;
  private int firstNonterminalElementNumber;
  private int lastNonterminalElementNumber;
  // Todo: at the moment slots are not enumerated - we'll just set it to lastNonterminalElementNumber
  private int lastSlotNumber;

  private final Set<String> preludeStrings = new HashSet<String>();
  private final Set<String> supportStrings = new HashSet<String>();

  private final ARTGrammarElementEoS eoS = new ARTGrammarElementEoS();
  private final ARTGrammarElementEpsilon epsilon = new ARTGrammarElementEpsilon();

  private ARTTree instanceTree;

  private Set<ARTGrammarElementAttribute> supportAttributes = new HashSet<ARTGrammarElementAttribute>();

  private ARTGrammarInstance pendingnL;

  private int nextFreeSetNumber;
  private int nextInstanceNumber;
  private int nextTerminalInstanceNumber;

  Map<LinkedList<ARTGrammarElement>, ARTGrammarInstanceSlot> prefixStringMap = new HashMap<>();
  private final Map<ARTGrammarInstanceSlot, ARTGrammarInstanceSlot> prefixSlotMap = new HashMap<>();

  private final Set<Set<ARTGrammarElement>> usedSets = new HashSet<>();
  private final HashMap<Set<ARTGrammarElement>, Integer> mergedSets = new HashMap<>();
  private int instanceCount = 1;
  private String injectProductionString = null;

  public ARTGrammar(ARTManager artManager, String id, ARTV3Module ARTV3Module, boolean augment) {
    this(artManager, id, ARTV3Module, ARTV3Module.getDefaultStart(), augment);
  }

  public ARTGrammar(ARTManager artManager, String id, ARTV3Module ARTV3Module, Directives artDirectives) {
    this(artManager, id, ARTV3Module, ARTV3Module.getDefaultStart(), false);
  }

  public ARTGrammar(ARTManager artManager, String id, ARTV3Module ARTV3Module, ARTGrammarElementModuleNonterminal start, boolean augment) {
    this.id = id;
    this.artManager = artManager;
    this.ARTV3Module = ARTV3Module;
    this.artDirectives = new Directives(artManager.artDirectives);

    ARTGrammarElementNonterminal nonterminal;
    ARTGrammarElementModuleNonterminal moduleStart = artManager.getDefaultMainModule().getDefaultStart();
    if (moduleStart == null)
      throw new ARTUncheckedException("Module " + artManager.getDefaultMainModule().getId() + " has no start symbol so cannot construct a grammar");
    defaultStartNonterminal = findNonterminal(moduleStart);

    preludeStrings.addAll(ARTV3Module.getPreludeStrings());
    supportStrings.addAll(ARTV3Module.getSupportStrings());

    // Iterate over module nonterminals, loading them into the grammar tables so as to preserve specification ordering
    for (ARTGrammarElementModuleNonterminal n : ARTV3Module.getNonterminalList()) {// Use nonterminalsList to retain specification ordering
      nonterminal = findNonterminal(n.getModule(), n.getId());
      nonterminal.getAttributes().addAll(n.getAttributes());
    }

    // Now iterate over the modules elements set, updating
    for (ARTGrammarElement e : ARTV3Module.getElements()) {
      if (e instanceof ARTGrammarElementNonterminal)
        findNonterminal(ARTV3Module, ((ARTGrammarElementNonterminal) e).getId());
      else
        terminals.add((ARTGrammarElementTerminal) e);
    }

    // Now iterate over the modules usedNonterminal set, updating
    // System.out.println("Module used nonterminals: " + ARTV3Module.getUsedNonterminals());
    for (ARTGrammarElementModuleNonterminal n : ARTV3Module.getUsedNonterminals())
      usedNonterminals.add(findNonterminal(n.getModule(), n.getId()));

    // Optionally augment the grammar
    unaugmentedStartNonterminal = defaultStartNonterminal;
    if (augment) {
      nonterminal = findNonterminal(ARTV3Module, "ART_AUGMENTED");
      ARTGrammarInstanceLHS lhs = new ARTGrammarInstanceLHS(instanceCount++, nonterminal);
      ARTGrammarInstanceCat cat = new ARTGrammarInstanceCat(instanceCount++);
      ARTGrammarInstanceSlot first = (ARTGrammarInstanceSlot) cat.addChild(new ARTGrammarInstanceSlot(instanceCount++));
      ARTGrammarInstanceNonterminal second = (ARTGrammarInstanceNonterminal) cat
          .addChild(new ARTGrammarInstanceNonterminal(instanceCount++, defaultStartNonterminal));
      ARTGrammarInstanceSlot third = (ARTGrammarInstanceSlot) cat.addChild(new ARTGrammarInstanceSlot(instanceCount++));

      lhs.lhsL = cat.lhsL = first.lhsL = second.lhsL = third.lhsL = lhs;
      cat.productionL = first.productionL = second.productionL = third.productionL = cat;

      first.prefixLength = 0;
      second.prefixLength = third.prefixLength = 1;

      nonterminal.getProductions().add(cat);

      defaultStartNonterminal = nonterminal;
    }

    // Load module productions into this grammar
    for (ARTGrammarElementModuleNonterminal n : ARTV3Module.getNonterminalList()) {
      // System.out.printf("ARTGrammar constructor processing module %s found nonterminal %s%n", ARTV3Module.getId(), moduleNonterminal);
      nonterminal = findNonterminal(n);

      for (ARTGLLRDTVertex vertex : n.getProductions()) {
        // System.out.printf("**** **** Adding production for nonterminal %s\n", nonterminal);
        addProduction(nonterminal, ARTV3Module, vertex);
      }
    }

    if (nonterminals.isEmpty()) throw new ARTUncheckedException("Induced grammar contains no nonterminals");

    // Update grammar paraterminal set from the module
    for (ARTValueTerm t : ARTV3Module.getParaterminals()) {
      getParaterminals().add(findNonterminal(ARTV3Module, t.getChild().getPayload().toString()));
      String alias = ARTV3Module.getParaterminalAliases().get(t.getChild());
      if (alias != null) getParaterminalAliases().put(findNonterminal(ARTV3Module, t.getChild().getPayload().toString()), alias);
    }
    // Now set up whitespace terminals

    for (ARTValueTerm w : ARTV3Module.getWhitespaceTerminals()) {
      if (w.getPayload().toString().equals("builtinTerminal"))
        whitespaces.add(new ARTGrammarElementTerminalBuiltin(w.getChild().getPayload().toString()));
      else if (w.getPayload().toString().equals("nonterminal"))
        whitespaces.add(new ARTGrammarElementNonterminal(ARTV3Module, w.getChild().getPayload().toString()));
      else
        throw new ARTUncheckedException("Unexpected term in whitespace list: " + w);
    }

    // If no whitespace declaration at all, then assert the default behaviour into this grammar
    if (whitespaces.isEmpty() && paraterminals.isEmpty() && artDirectives.algorithmMode() != ARTModeAlgorithm.mgllGeneratorPool
        && artDirectives.algorithmMode() != ARTModeAlgorithm.gllTWEGeneratorPool && artDirectives.algorithmMode() != ARTModeAlgorithm.grammarWrite) {
      ARTGrammarElementTerminalBuiltin tmp = new ARTGrammarElementTerminalBuiltin("SIMPLE_WHITESPACE");
      terminals.add(tmp);
      whitespaces.add(tmp);
    }

    if (artDirectives.i("verbosity") > 0) System.out.println("Whitespaces: " + whitespaces);
    Set<ARTGrammarElement> visited = new HashSet<>();

    // Now perform full sanity and reachability analysis for TWE set based parsers
    /*
     * reachability analysis
     *
     * artReachability analysis takes paramaters for lexerReachable and parserReachable, visited, a current nonterminal and a paraterminal if the paraterminal
     * is null, then the encountered symbols are added to parserReachable if the paraterminal is non-null, then the encountered symbols are added to
     * lexerReachable
     *
     * We have three roots to consider: the start symbol of the grammar; ART_InjectProduction; and ART_InjectInstance
     *
     *
     *
     *
     */

    // Main root - standard lexerReachable and parseReachable
    artReachabilityAnalysisRec(this.getDefaultStartNonterminal(), lexerReachable, parserReachable, null, visited);

    // Set up injection strings
    injectProductionString = ARTV3Module.getInjectProductionString();
    if (nonterminals.contains(new ARTGrammarElementNonterminal(ARTV3Module, "ART_InjectProduction"))) {
      useDefaultInjectProductionString = false;
      ARTGrammarElementNonterminal n = findNonterminal(ARTV3Module, "ART_InjectProduction");
      if (n.getProductions() != null) {
        visited = new HashSet<>();
        // production insertion must be under a paraterminal (use n) so no need for a parserReachable
        artReachabilityAnalysisRec(n, lexerReachable, null, n, visited);
        injectProductionString = n.getProductions().get(0).toGrammarSlotStringRec(null, "", "", false, false, "", "", null, false);
      }
    }

    if (nonterminals.contains(new ARTGrammarElementNonterminal(ARTV3Module, "ART_InjectInstance"))) {
      ARTGrammarElementNonterminal n = findNonterminal(ARTV3Module, "ART_InjectInstance");
      if (n.getProductions() != null) {
        visited = new HashSet<>();
        // Compute injectInstance reachability
        artReachabilityAnalysisRec(n, injectInstanceReachable, injectInstanceReachable, null, visited);
        injectInstanceError.addAll(injectInstanceReachable);
        injectInstanceError.retainAll(parserReachable);
        injectInstanceError.retainAll(nonterminals);
        visited = new HashSet<>();

        // Fold instance reachability in
        artReachabilityAnalysisRec(n, lexerReachable, parserReachable, null, visited);
        injectInstanceString = n.getProductions().get(0).toGrammarSlotStringRec(null, "", "", false, false, "", "", null, false);
      }
    }

    injectInstanceReachable.remove(new ARTGrammarElementNonterminal(ARTV3Module, "ART_InjectInstance"));
    injectInstanceReachable.remove(new ARTGrammarElementNonterminal(ARTV3Module, "ART_InjectProduction"));
    lexerReachable.remove(new ARTGrammarElementNonterminal(ARTV3Module, "ART_InjectInstance"));
    lexerReachable.remove(new ARTGrammarElementNonterminal(ARTV3Module, "ART_InjectProduction"));
    parserReachable.remove(new ARTGrammarElementNonterminal(ARTV3Module, "ART_InjectInstance"));
    parserReachable.remove(new ARTGrammarElementNonterminal(ARTV3Module, "ART_InjectProduction"));

    if (artDirectives.i("verbosity") > 0) {
      System.out.println("Instance injection string: " + injectInstanceString);
      System.out.println("Production injection string: " + injectProductionString);

      System.out.println("Paraterminals: " + paraterminals);
      System.out.println("Lexer reachable symbols: " + lexerReachable);
      System.out.println("Parser reachable symbols: " + parserReachable);
      System.out.println("injectInstance reachable symbols: " + injectInstanceReachable);
      System.out.println("injectInstance error symbols: " + injectInstanceError);
    }
    if (injectInstanceError.size() != 0)
      ARTText.printFatal("the injectInstance grammar and the parser grammar both use these nonterminals: " + injectInstanceError);

    if (ARTV3Module.getAbsorbNonterminal() != null) absorbNonterminal = findNonterminal(ARTV3Module.getAbsorbNonterminal());

    clean();

    // System.out.println("derivation > map: " + artModule.getDerivationHigher());
    for (ARTValueTerm t : ARTV3Module.getDerivationHigher().keySet()) {
      // System.out.println("Processing derivation chooser " + chooserTermToString(t) + " > " + artModule.getDerivationHigher().get(t));
      extendDerivationChoiceRelation(chooserDerivationHigher, ARTV3Module, t, ARTV3Module.getDerivationHigher().get(t));
    }
    // System.out.println("After processing: " + chooserDerivationHigher);

    for (ARTValueTerm t : ARTV3Module.getDerivationLonger().keySet()) {
      // System.out.println("Processing derivation chooser " + chooserTermToString(t) + " >> " + artModule.getDerivationLonger().get(t));
      extendDerivationChoiceRelation(chooserDerivationLonger, ARTV3Module, t, ARTV3Module.getDerivationLonger().get(t));
    }

    for (ARTValueTerm t : ARTV3Module.getDerivationShorter().keySet()) {
      // System.out.println("Processing derivation chooser " + chooserTermToString(t) + " << " + artModule.getDerivationShorter().get(t));
      extendDerivationChoiceRelation(chooserDerivationShorter, ARTV3Module, t, ARTV3Module.getDerivationShorter().get(t));
    }
    // System.out.println(this);
  }

  ITerms iTerms = new ITermsLowLevelAPI();

  private void updateChooser(ARTV3Module ARTV3Module, ARTBitSet[] bits, int lhsTerm, int rhsTerm) {
    for (int l = 0; l < iTerms.getTermArity(lhsTerm); l++) {
      // System.out.println("Updating chooser with: " + iTerms.toString(lhsTerm) + " relation " + iTerms.toString(rhsTerm));
      int lTerm = iTerms.getSubterm(lhsTerm, l);
      for (int r = 0; r < iTerms.getTermArity(rhsTerm); r++) {
        int rTerm = iTerms.getSubterm(rhsTerm, r);
        setChooserBit(ARTV3Module, bits, lTerm, rTerm);
      }
    }
  }

  private void setChooserBit(ARTV3Module ARTV3Module, ARTBitSet[] bits, int l, int r) {
    int lElement = convertTermToEnumerationElement(ARTV3Module, l), rElement = convertTermToEnumerationElement(ARTV3Module, r);

    if (lElement == -1 || rElement == -1) return;
    if (bits[lElement] == null) bits[lElement] = new ARTBitSet();
    bits[lElement].set(rElement);
  }

  private int convertTermToEnumerationElement(ARTV3Module module, int term) {
    String child = ITerms.unescapeMeta(iTerms.getTermSymbolString(iTerms.getSubterm(term, 0)));
    // System.out.println("Converting: " + child);

    ARTGrammarElement element;

    switch (iTerms.getTermSymbolString(term)) {
    case "srNonterminal":
      element = nonterminalNameMap.get(new ARTName(module, child));
      break;
    case "srCaseSensitiveTerminal":
      element = terminalCaseSensitiveNameMap.get(child);
      break;
    case "srCaseInsensitiveTerminal":
      element = terminalCaseInsensitiveNameMap.get(child);
      break;
    case "srBuiltinTerminal":
      element = terminalBuiltinNameMap.get(child);
      break;
    case "srCharacterTerminal":
      element = terminalCharacterNameMap.get(child);
      break;

    default:
      throw new ARTUncheckedException("Unrecognised chooser term " + iTerms.toString(term));
    }

    if (element == null) {
      System.out.println("Warning - chooser element " + child + " does not appear in any grammar rule");
      return -1;
    }

    int ret = element.getElementNumber();

    // System.out.println("Returning: " + ret);

    return ret;
  }

  private ARTChooserSet findChooserSet(String id) {
    if (chooserSets.get(id) == null) chooserSets.put(id, new ARTChooserSet(lastSlotNumber + 1));
    return chooserSets.get(id);
  }

  private void artReachabilityAnalysisRec(ARTGrammarElementNonterminal n, Set<ARTGrammarElement> lexerReachable, Set<ARTGrammarElement> parserReachable,
      ARTGrammarElementNonterminal paraterminal, Set<ARTGrammarElement> visited) {

    if (visited.contains(n)) return;
    visited.add(n);

    if (paraterminal == null)
      parserReachable.add(n);
    else
      lexerReachable.add(n);

    ARTGrammarElementNonterminal newParaterminal = paraterminal;

    if (paraterminals.contains(n)) {
      if (paraterminal != null) System.out.println("*** Warning - paraterminal " + n + " is reachable from paraterminal " + paraterminal);
      newParaterminal = n;
    }

    for (ARTGrammarInstanceCat p : n.getProductions())
      artReachabilityAnalysisRec(p, lexerReachable, parserReachable, newParaterminal, visited, n);

    // visited.remove(n);
  }

  private void artReachabilityAnalysisRec(ARTGrammarInstance instance, Set<ARTGrammarElement> lexerReachable, Set<ARTGrammarElement> parserReachable,
      ARTGrammarElementNonterminal paraterminal, Set<ARTGrammarElement> visited, ARTGrammarElementNonterminal mostRecentLHS) {
    if (instance == null) return;

    // System.out.println("reachability analysis for instance " + instance + " under paraterminal " + paraterminal);
    // System.out.println(instance);
    if (instance instanceof ARTGrammarInstanceTerminal) {

      if (instance.getPayload() instanceof ARTGrammarElementTerminalCharacter)
        lexerReachable.add(instance.getPayload());
      else
        parserReachable.add(instance.getPayload());

      if (paraterminal != null && !(instance.getPayload() instanceof ARTGrammarElementTerminalCharacter))
        throw new ARTUncheckedException("found instance of non-character terminal " + instance.toSymbolString() + " under paraterminal " + paraterminal);

      // if (paraterminal == null && instance.getPayload() instanceof ARTGrammarElementTerminalCharacter) System.out.println("Warning:"
      // + "found instance of character terminal " + instance.toSymbolString() + " in nonterminal " + mostRecentLHS + " that is not under a paraterminal");
    }

    if (instance instanceof ARTGrammarInstanceNonterminal)
      artReachabilityAnalysisRec((ARTGrammarElementNonterminal) instance.getPayload(), lexerReachable, parserReachable, paraterminal, visited);

    artReachabilityAnalysisRec(instance.getChild(), lexerReachable, parserReachable, paraterminal, visited, mostRecentLHS);
    artReachabilityAnalysisRec(instance.getSibling(), lexerReachable, parserReachable, paraterminal, visited, mostRecentLHS);
  }

  public boolean addProduction(ARTGrammarElementNonterminal nonterminal, ARTV3Module ARTV3Module, ARTGLLRDTVertex vertex) {
    boolean ret = nonterminal.getProductions()
        .add((ARTGrammarInstanceCat) buildGrammarProductionFromRDT(nonterminal, ARTV3Module, new ARTGrammarInstanceRoot(0), vertex, false));
    if (ret) isDirty = true;
    return ret;
  }

  private void artComputeFfCELocalRec(ARTGrammarInstance instance) {
    if (instance instanceof ARTGrammarInstanceOptional) {
      instance.getSibling().isFfCE = true;
      artComputeFfCELocalRec(instance.getChild());
    } else if (instance instanceof ARTGrammarInstanceDoFirst)
      artComputeFfCELocalRec(instance.getChild());

    else if (instance instanceof ARTGrammarInstanceAlt)
      for (ARTGrammarInstance i = instance.getChild(); i != null; i = i.getSibling())
        artComputeFfCELocalRec(i);

    else if (instance instanceof ARTGrammarInstanceCat)
      artComputeFfCELocalRec(instance.getChild().getSibling());

    else if (instance instanceof ARTGrammarInstancePositiveClosure || instance instanceof ARTGrammarInstanceKleeneClosure
        || instance instanceof ARTGrammarInstanceTerminal || instance instanceof ARTGrammarInstanceNonterminal || instance instanceof ARTGrammarInstanceEpsilon)
      instance.getSibling().isFfCE = true;
    else
      throw new ARTUncheckedException("artComputerFfCELocalRec: U=unexpected node " + instance);
  }

  private void artComputeFfCERec(ARTGrammarInstance instance) {
    // artManager.getText().print("artComputeFfCERec() visiting node instance of " + instance.getClass() + " labelled " + instance + "\n");

    if (instance instanceof ARTGrammarInstancePositiveClosure) {
      // artManager.getText().print("Detected positive closure\n");
      artComputeFfCELocalRec(instance.getChild());
    }

    for (ARTGrammarInstance i = instance.getChild(); i != null; i = i.getSibling())
      artComputeFfCERec(i);
  }

  private void artSetInstanceNumberRec(ARTGrammarElementNonterminal nonterminal, ARTGrammarInstance instance) {
    if (instance.getPayload() == nonterminal) instance.instanceNumberWithinProduction = nextInstanceNumber++;
    if (instance instanceof ARTGrammarInstanceTerminal && instance.instanceNumberWithinProduction == 0)
      instance.instanceNumberWithinProduction = nextTerminalInstanceNumber++;
    for (ARTGrammarInstance child = instance.getChild(); child != null; child = child.getSibling())
      artSetInstanceNumberRec(nonterminal, child);
  }

  // the belowClosure flag is set by instances of * + and ? and is used to control
  private ARTGrammarInstance buildGrammarProductionFromRDT(ARTGrammarElementNonterminal lhs, ARTV3Module ARTV3Module, ARTGrammarInstance parent,
      ARTGLLRDTVertex vertex, boolean belowClosure) {
    // System.out.printf("buildGrammarProduction() at %s with parent %s and belowClosure %s\n", vertex, parent, belowClosure);
    ARTGrammarInstance newParent = parent;
    boolean newBelowClosure = false;
    boolean recurse = true;

    isDirty = true;
    if (vertexLabel(vertex, "(")) {
      if (belowClosure) {
        newBelowClosure = true; // propogate to solitary elements that may need slots adding
        newParent = parent; // Do not instantiate ( below *, + or ?
      } else
        newParent = parent.addChild(new ARTGrammarInstanceDoFirst(instanceCount++));

      /*
       * Hazard - the concrete ART syntax allows user to write, say, a* instead of (a)*. We use processAbbreviation to instantiate the right tree in those cases
       */
    } else if (vertexLabel(vertex, "?")) {
      newBelowClosure = true;
      newParent = parent.addChild(new ARTGrammarInstanceOptional(instanceCount++));
      if (processAbbreviation(ARTV3Module, newParent, vertex)) return newParent; // Abbreviations only allowed on BNF instances, so all done
    } else if (vertexLabel(vertex, "*")) {
      newBelowClosure = true;
      newParent = parent.addChild(new ARTGrammarInstanceKleeneClosure(instanceCount++));
      if (processAbbreviation(ARTV3Module, newParent, vertex)) return newParent; // Abbreviations only allowed on BNF instances, so all done
    } else if (vertexLabel(vertex, "+")) {
      newBelowClosure = true;
      newParent = parent.addChild(new ARTGrammarInstancePositiveClosure(instanceCount++));
      if (processAbbreviation(ARTV3Module, newParent, vertex)) return newParent; // Abbreviations only allowed on BNF instances, so all done
    } else if (vertexLabel(vertex, "not"))
      newParent = parent.addChild(new ARTGrammarInstanceNot(instanceCount++));
    else if (vertexLabel(vertex, "iter"))
      newParent = parent.addChild(new ARTGrammarInstanceIter(instanceCount++));
    else if (vertexLabel(vertex, "diff"))
      newParent = parent.addChild(new ARTGrammarInstanceDiff(instanceCount++));
    else if (vertexLabel(vertex, "alt"))
      newParent = parent.addChild(new ARTGrammarInstanceAlt(instanceCount++));
    else if (vertexLabel(vertex, "cat"))
      newParent = parent.addChild(new ARTGrammarInstanceCat(instanceCount++));
    else if (vertexLabel(vertex, "slot")) {
      newParent = parent.addChild(new ARTGrammarInstanceSlot(instanceCount++));
    } else if (vertexLabel(vertex, "action")) {
      recurse = false;
      newParent = parent.addChild(new ARTGrammarInstanceAction(instanceCount++));
      newParent = newParent.addChild(new ARTGrammarInstanceActionValue(instanceCount++, ((ARTAT_ART_action) vertex.getPayload().getAttributes()).v));
    } else { // non recursing labels
      recurse = false;
      if (belowClosure) {
        parent = parent.addChild(new ARTGrammarInstanceCat(instanceCount++));
        parent.addChild(new ARTGrammarInstanceSlot(instanceCount++));
      } else { // annotatable nodes
        if (vertexLabel(vertex, "nonterminal"))
          newParent = parent.addChild(new ARTGrammarInstanceNonterminal(instanceCount++,
              findNonterminal(ARTV3Module, ((ARTAT_ART_nonterminal) vertex.getPayload().getAttributes()).v)));
        else if (vertexLabel(vertex, "characterTerminal"))
          newParent = parent.addChild(
              new ARTGrammarInstanceTerminal(instanceCount++, findTerminalCharacter(((ARTAT_ART_characterTerminal) vertex.getPayload().getAttributes()).v)));
        else if (vertexLabel(vertex, "caseSensitiveTerminal")) {
          String pattern = ((ARTAT_ART_caseSensitiveTerminal) vertex.getPayload().getAttributes()).v;
          if (pattern.replaceFirst("\\s", "").length() != pattern.length()) throw new ARTUncheckedException(
              "In production for nonterminal " + lhs.getId() + ", case sensitive terminal has embedded whitespace: '" + pattern + "'");
          newParent = parent.addChild(new ARTGrammarInstanceTerminal(instanceCount++, findTerminalCaseSensitive(pattern)));
        } else if (vertexLabel(vertex, "caseInsensitiveTerminal")) {
          String pattern = ((ARTAT_ART_caseInsensitiveTerminal) vertex.getPayload().getAttributes()).v;
          if (pattern.replaceAll("\\s", "").length() != pattern.length()) throw new ARTUncheckedException(
              "In production for nonterminal " + lhs.getId() + ", case insensitive terminal has embedded whitespace: \"" + pattern + "\"");
          newParent = parent.addChild(new ARTGrammarInstanceTerminal(instanceCount++, findTerminalCaseInsensitive(pattern)));
        } else if (vertexLabel(vertex, "builtinTerminal"))
          newParent = parent.addChild(
              new ARTGrammarInstanceTerminal(instanceCount++, findTerminalBuiltin(((ARTAT_ART_builtinTerminal) vertex.getPayload().getAttributes()).v)));
        else if (vertexLabel(vertex, "epsilon"))
          newParent = parent.addChild(new ARTGrammarInstanceEpsilon(instanceCount++, epsilon));
        else
          throw new ARTUncheckedException("Unknown node label " + artManager.getParser().artLabelStrings[vertex.getPayload().getLabel()]
              + " encountered in RDT during grammar tree construction");

        processAnnotations(lhs, newParent, vertex);
      }
      if (belowClosure) parent.addChild(new ARTGrammarInstanceSlot(instanceCount++));

    }

    if (recurse) for (ARTGLLRDTVertex child = vertex.getChild(); child != null; child = child.getSibling())
      buildGrammarProductionFromRDT(lhs, ARTV3Module, newParent, child, newBelowClosure);

    return newParent;
  }

  /* buildInstanceTree() stitches together the production trees reachable from the start symbol with new LHS nodes and a new root node */
  private void buildInstanceTree(ARTGrammarElementNonterminal start) {
    boolean changed = true;
    // 0 Instantiate left hand side nodes and connect to production nodes
    int lhsInstanceCount = 0;
    instanceTree = new ARTTree("Grammar" + id);
    ARTGrammarInstanceRoot root = new ARTGrammarInstanceRoot(--lhsInstanceCount);
    instanceTree.setRoot(root);
    for (ARTGrammarElementNonterminal nonterminal : nonterminals) {
      nonterminal.lhsInstance = new ARTGrammarInstanceLHS(--lhsInstanceCount, nonterminal);
      nonterminal.lhsInstance.isLHS = true;
      nonterminal.lhsInstance.setPayload(nonterminal);
      root.addChild(nonterminal.lhsInstance);
      for (ARTGrammarInstance productionRoot : nonterminal.getProductions())
        nonterminal.lhsInstance.addChild(productionRoot);
    }

    // Set the instance numbers
    for (ARTGrammarElementNonterminal nonterminal : nonterminals)
      for (ARTGrammarInstance production : nonterminal.getProductions())
        for (ARTGrammarElementNonterminal innerNonterminal : nonterminals) { // This is very inefficient
          nextInstanceNumber = nextTerminalInstanceNumber = 1;
          artSetInstanceNumberRec(innerNonterminal, production);
        }

    if (artDirectives.i("verbosity") > 0) instanceTree.printDot("GrammarInstanceTreeBeforeSetComputations.dot");

    // 1 Adding EoS to nonterminal follow sets
    if (isEOSFollow) {
      for (ARTGrammarElementNonterminal n : nonterminals)
        n.getFollow().add(eoS);
    } else
      start.getFollow().add(eoS);

    // 2 first and follow set computations
    while (changed) {
      changed = false;
      changed |= (computeSetsRec((ARTGrammarInstance) instanceTree.getRoot(), 0, null, null));
    }

    // 3 Main attribute computation
    changed = true;
    // int pass = 0;
    while (changed) {
      // pass++;
      // artManager.text.print("Main attribute computation pass\n");
      changed = false;
      pendingnL = null;
      changed |= (computeAttributesRec((ARTGrammarInstance) instanceTree.getRoot(), null, null, null, 0));
      changed |= (computePlAttributeRec((ARTGrammarInstance) instanceTree.getRoot()));
    }

    // 4 fiR and predictive pops
    // artManager.text.print("Computing fiR and predictive pops\n");
    computefiRRec((ARTGrammarInstance) instanceTree.getRoot());
    artComputeFfCERec((ARTGrammarInstance) instanceTree.getRoot());
    computePredictivePopRec((ARTGrammarInstance) instanceTree.getRoot());

    // 5 isFBNF and isEBNF
    isFBNF = true;
    isEBNF = false;
    // artManager.text.print("Computing isFBNF and is EBNF\n");
    for (ARTGrammarElementNonterminal nonterminal : nonterminals)
      for (ARTGrammarInstance production : nonterminal.getProductions()) {
        /*
         * computeIsEFBNFRec treats instances of epsilon as inidcating EBNF because they are assummed to be embedded.
         *
         * We therefore DON'T call it on simple BNF epsilon productions
         *
         */
        // System.out.println("compute EBNF at production " + production.toGrammarString());
        // System.out.println("First non-slot is " + production.getChild().getSibling());
        // System.out.println("Second non-slot is " + production.getChild().getSibling().getSibling().getSibling());
        if (production.getChild().getSibling() instanceof ARTGrammarInstanceEpsilon) {
          if (production.getChild().getSibling().getSibling().getSibling() != null) computeIsEFBNFRec(production.getChild());
          // System.out.println("Calling computeIsEFBNFRec");
        } else
          computeIsEFBNFRec(production.getChild());

      }
    // System.out.println("isEBNF: " + isEBNF + " isFBNF: " + isFBNF);

    // 6 Compute merged sets
    nextFreeSetNumber = 1;

    computeMergedSets(root);

    // System.out.println(mergedSets);

    // 7 Compute prefix lengths and map for BSRset algorithms
    computeSlotPrefixMap();

    // if (artDirectives.i("verbosity") > 0)
    instanceTree.printDot("GrammarInstanceTreeAfterSetComputations.dot");

    isDirty = false;
  }

  public void clean() {
    if (isDirty) {
      numberSymbols();
      buildInstanceTree(defaultStartNonterminal);
    }

    // Now rebuild chooser sets
    /* Little bit of V4 sneaking in here */

    /*
     * First set up the bindings
     */

    int[] bindings = new int[9];
    final int anyCharacterTerminal = 1;
    final int anyBuiltinTerminal = 2;
    final int anyCaseSensitiveTerminal = 3;
    final int anyCaseInsensitiveTerminal = 4;
    final int anyParaterminal = 5;
    final int anyNonterminal = 6;

    final int anyLiteralTerminal = 7;
    final int anyTerminal = 8;

    String anyCharacterStr = "__set( ", anyBuiltinStr = "__set( ", anyCaseSensitiveStr = "__set( ", anyCaseInsensitiveStr = "__set( ", anyParaStr = "__set( ",
        anyNonStr = "__set( ", anyLiteralStr = "__set( ", anyStr = "__set( ";

    for (ARTGrammarElementTerminal t : terminals) {
      if (t instanceof ARTGrammarElementTerminalCharacter) {
        anyCharacterStr += "srCharacterTerminal(" + ITerms.escapeMeta(t.getId()) + "),";
        anyStr += "srCharacterTerminal(" + ITerms.escapeMeta(t.getId()) + "),";
      }

      if (t instanceof ARTGrammarElementTerminalBuiltin) {
        anyBuiltinStr += "srBuiltinTerminal(" + ITerms.escapeMeta(t.getId()) + "),";
        anyStr += "srBuiltinTerminal(" + ITerms.escapeMeta(t.getId()) + "),";
      }

      if (t instanceof ARTGrammarElementTerminalCaseInsensitive) {
        anyCaseInsensitiveStr += "srCaseInsensitiveTerminal(" + ITerms.escapeMeta(t.getId()) + "),";
        anyLiteralStr += "srCaseInsensitiveTerminal(" + ITerms.escapeMeta(t.getId()) + "),";
        anyStr += "srCaseInsensitiveTerminal(" + ITerms.escapeMeta(t.getId()) + "),";
      }

      if (t instanceof ARTGrammarElementTerminalCaseSensitive) {
        anyCaseSensitiveStr += "srCaseSensitiveTerminal(" + ITerms.escapeMeta(t.getId()) + "),";
        anyLiteralStr += "srCaseSensitiveTerminal(" + ITerms.escapeMeta(t.getId()) + "),";
        anyStr += "srCaseSensitiveTerminal(" + ITerms.escapeMeta(t.getId()) + "),";
      }
    }

    for (ARTGrammarElementNonterminal p : paraterminals) {
      anyParaStr += "srNonterminal(" + ITerms.escapeMeta(p.getId()) + "),";
      anyStr += "srNonterminal(" + ITerms.escapeMeta(p.getId()) + "),";
    }

    for (ARTGrammarElementNonterminal n : nonterminals) {
      anyNonStr += "srNonterminal(" + ITerms.escapeMeta(n.getId()) + "),";
    }

    anyCharacterStr = anyCharacterStr.substring(0, anyCharacterStr.length() - 1) + ")";
    anyBuiltinStr = anyBuiltinStr.substring(0, anyBuiltinStr.length() - 1) + ")";
    anyCaseSensitiveStr = anyCaseSensitiveStr.substring(0, anyCaseSensitiveStr.length() - 1) + ")";
    anyCaseInsensitiveStr = anyCaseInsensitiveStr.substring(0, anyCaseInsensitiveStr.length() - 1) + ")";
    anyParaStr = anyParaStr.substring(0, anyParaStr.length() - 1) + ")";
    anyNonStr = anyNonStr.substring(0, anyNonStr.length() - 1) + ")";

    anyLiteralStr = anyLiteralStr.substring(0, anyLiteralStr.length() - 1) + ")";
    anyStr = anyStr.substring(0, anyStr.length() - 1) + ")";

    bindings[anyCharacterTerminal] = iTerms.findTerm(anyCharacterStr);
    bindings[anyBuiltinTerminal] = iTerms.findTerm(anyBuiltinStr);
    bindings[anyCaseSensitiveTerminal] = iTerms.findTerm(anyCaseSensitiveStr);
    bindings[anyCaseInsensitiveTerminal] = iTerms.findTerm(anyCaseInsensitiveStr);
    bindings[anyParaterminal] = iTerms.findTerm(anyParaStr);
    bindings[anyNonterminal] = iTerms.findTerm(anyNonStr);

    bindings[anyLiteralTerminal] = iTerms.findTerm(anyLiteralStr);
    bindings[anyTerminal] = iTerms.findTerm(anyStr);

    // System.out.println("Characters: " + iTerms.toString(bindings[1]));
    // System.out.println("Builtins: " + iTerms.toString(bindings[2]));
    // System.out.println("CaseSensitives: " + iTerms.toString(bindings[3]));
    // System.out.println("CaseInsensitives: " + iTerms.toString(bindings[4]));
    // System.out.println("Paras: " + iTerms.toString(bindings[5]));
    // System.out.println("Nons: " + iTerms.toString(bindings[6]));
    // System.out.println("Literals: " + iTerms.toString(bindings[7]));
    // System.out.println("Any: " + iTerms.toString(bindings[8]));

    for (String chooserSetID : ARTV3Module.getChoosers().keySet()) {
      List<String> chooseExpressionList = ARTV3Module.getChoosers().get(chooserSetID);
      ARTChooserSet chooserSet = findChooserSet(chooserSetID);

      for (String expression : chooseExpressionList) {
        // System.out.println("Evaluating chooser expression:" + expression);

        int root = iTerms.findTerm(expression);

        if (iTerms.getTermSymbolString(iTerms.getSubterm(root, 0)).equals("chooseSPPF")
            || iTerms.getTermSymbolString(iTerms.getSubterm(root, 1)).equals("chooseSPPF"))
          if (!(iTerms.getTermSymbolString(iTerms.getSubterm(root, 0)).equals("chooseSPPF")
              && iTerms.getTermSymbolString(iTerms.getSubterm(root, 1)).equals("chooseSPPF"))) {
                System.out.println("SPPF choosers can only use productions: skipping");
                continue;
              }

        int evaluated;
        evaluated = iTerms.substitute(bindings, root, 0);

        switch (iTerms.getTermSymbolString(evaluated)) {
        case "chooseHigher":
          updateChooser(ARTV3Module, chooserSet.higher, iTerms.getSubterm(evaluated, 0), iTerms.getSubterm(evaluated, 1));
          break;
        case "chooseLonger":
          updateChooser(ARTV3Module, chooserSet.longer, iTerms.getSubterm(evaluated, 0), iTerms.getSubterm(evaluated, 1));
          break;
        case "chooseShorter":
          updateChooser(ARTV3Module, chooserSet.shorter, iTerms.getSubterm(evaluated, 0), iTerms.getSubterm(evaluated, 1));
          break;
        }
      }
    }

    // System.out.println("Used nonterminals " + usedNonterminals);
    // System.out.println("Paraterminals " + paraterminals);

    for (ARTGrammarElementNonterminal n : nonterminals)
      if (n.getProductions().isEmpty() && usedNonterminals.contains(n))
        if (!paraterminals.contains(n)) System.out.println("*** Warning - undefined nonterminal " + n);
  }

  private boolean computeAttributesRec(ARTGrammarInstance instance, ARTGrammarInstance parentInstance, ARTGrammarInstanceLHS LHSInstance,
      ARTGrammarInstanceCat productionInstance, int level) {
    if (instance == null) return false;
    // System.out.printf("artComputeAttributes entering node %d at level %d with LHSInstance %d and productionInstance %d\n", instance.getKey(), level,
    // LHSInstance == null ? 0 : LHSInstance.getKey(), productionInstance == null ? 0 : productionInstance.getKey());

    boolean changed = false;

    if (instance instanceof ARTGrammarInstanceLHS) LHSInstance = (ARTGrammarInstanceLHS) instance;
    if (level == 2) productionInstance = (ARTGrammarInstanceCat) instance;

    if (!(instance instanceof ARTGrammarInstanceSlot)) // Do not recurse below POS nodes
      for (ARTGrammarInstance tmp = instance.getChild(); tmp != null; tmp = tmp.getSibling())
        changed |= (computeAttributesRec(tmp, instance, LHSInstance, productionInstance, level + 1));
    else
      instance.isSlotParentLabel = instance.getChild() != null;

    // System.out.printf("artComputeAttributes after recursion from node %d at level %d with LHSInstance %d and productionInstance %d\n", instance.getKey(),
    // level,
    // LHSInstance == null ? 0 : LHSInstance.getKey(), productionInstance == null ? 0 : productionInstance.getKey());

    // Initialisations
    if (instance.getLhsL() == null && LHSInstance != null) {
      changed |= (instance.getLhsL() != LHSInstance);
      instance.lhsL = LHSInstance;
    }

    changed |= (instance.getProductionL() != productionInstance);
    instance.productionL = productionInstance;

    if (instance.niL == null) {
      changed |= (instance.niL != instance);
      instance.niL = instance;
    }
    if (instance.nL == null) {
      changed |= (instance.nL != instance);
      instance.nL = instance;
    } // Some elision here - the paper doesn't define nL for the last node in a
    // nonterminal's productions: let's make it L
    if (instance.aL == null) {
      changed |= (instance.aL != instance);
      instance.aL = instance;
    }

    if (instance.pL == null) {
      changed |= (instance.pL != instance);
      // DEBUG - uncommenting this line breaks termination - why?
      // instance.pL = instance;
    }
    if (instance.lrL == null) {
      changed |= (instance.lrL != instance);
      instance.lrL = instance;
    }
    if (instance.erL == null) {
      changed |= (instance.erL != instance);
      instance.erL = instance;
    }

    // Boolean attribute calculations
    // EoR
    changed |= (instance.isEoR != (instance instanceof ARTGrammarInstanceSlot && level == 3 && instance.getSibling() == null));
    instance.isEoR = (instance instanceof ARTGrammarInstanceSlot && level == 3 && instance.getSibling() == null);

    // EoOP
    if (instance instanceof ARTGrammarInstanceOptional || instance instanceof ARTGrammarInstanceDoFirst) {
      changed |= (rightmostElementRec(instance).isEoOP != true);
      rightmostElementRec(instance).isEoOP = true;
    }

    if (instance.aL.isEoOP) {
      changed |= (instance.isEoOP != true);
      instance.isEoOP = true;
    }

    // EoD
    if (instance instanceof ARTGrammarInstanceDoFirst) {
      changed |= (rightmostElementRec(instance).isEoD != true);
      rightmostElementRec(instance).isEoD = true;
    }

    // EoO
    if (instance instanceof ARTGrammarInstanceOptional) {
      changed |= (rightmostElementRec(instance).isEoO != true);
      rightmostElementRec(instance).isEoO = true;
    }

    // EoP
    if (instance instanceof ARTGrammarInstancePositiveClosure) {
      changed |= (rightmostElementRec(instance).isEoP != true);
      rightmostElementRec(instance).isEoP = true;
    }

    // EoK
    if (instance instanceof ARTGrammarInstanceKleeneClosure) {
      changed |= (rightmostElementRec(instance).isEoK != true);
      rightmostElementRec(instance).isEoK = true;
    }

    // fiR now processed in final pass - see below

    // niL
    if (instance instanceof ARTGrammarInstanceNonterminal) {
      changed |= (instance.getSibling().niL != instance);
      instance.getSibling().niL = instance;
    }

    // nL
    if (instance instanceof ARTGrammarInstanceSlot && pendingnL != null) {
      if (pendingnL.isEoR) {
        changed |= (pendingnL.nL != pendingnL);
        pendingnL.nL = pendingnL;
      } else {
        changed |= (pendingnL.nL != instance);
        pendingnL.nL = instance;
      }
      pendingnL = null;
    }

    if (instance instanceof ARTGrammarInstanceSlot) pendingnL = instance;

    // aL
    if (instance instanceof ARTGrammarInstanceAlt) {
      ARTGrammarInstance E_r_n = rightmostElementRec(instance);
      for (ARTGrammarInstance tmp = instance.getChild(); tmp != null; tmp = tmp.getSibling()) {
        ARTGrammarInstance endNode = rightmostElementRec(tmp);

        changed |= (endNode.aL != E_r_n);
        endNode.aL = E_r_n;
        // if (endNode != E_r_n) // In this update we have extended isEoA to be true for the end of all alternate including the one without a | following
        changed |= (endNode.isEoA != true);
        endNode.isEoA = true;
      }
    }

    // isEof

    // isPopD
    if (instance instanceof ARTGrammarInstanceSlot && instance.getSibling() == null) {
      changed |= (instance.isPopD != true);
      instance.isPopD = true;
    }

    if (instance.getSibling() != null && instance.getSibling().isPopD
        && (instance instanceof ARTGrammarInstanceSlot || instance instanceof ARTGrammarInstanceEpsilon || instance instanceof ARTGrammarInstanceTerminal)) {
      changed |= (instance.isPopD != true);
      instance.isPopD = true;
    }

    // isPosSelector;
    if (instance instanceof ARTGrammarInstanceSlot) if (parentInstance.getChild() != instance && instance.getSibling() != null) {
      changed |= (instance.isSlotSelector != true);
      instance.isSlotSelector = true;
    }

    // lrL
    if (instance instanceof ARTGrammarInstanceLHS || (instance instanceof ARTGrammarInstanceAlt && level == 1))
      for (ARTGrammarInstance childNode = instance.getChild(); childNode != null; childNode = childNode.getSibling()) {
        changed |= (childNode.lrL != leftmostElementRec(childNode));
        childNode.lrL = leftmostElementRec(childNode);
      }
    else if (instance instanceof ARTGrammarInstanceSlot) if (instance.getSibling() != null) {
      changed |= (instance.getSibling().lrL != instance);
      instance.getSibling().lrL = instance;
    } else if (instance instanceof ARTGrammarInstanceCat || instance instanceof ARTGrammarInstanceAlt) {
      changed |= (instance.lrL != leftmostElementRec(instance));
      instance.lrL = leftmostElementRec(instance);
    }

    // erL
    if (instance instanceof ARTGrammarInstanceEpsilon || instance instanceof ARTGrammarInstanceNonterminal || instance instanceof ARTGrammarInstanceTerminal
        || instance instanceof ARTGrammarInstanceDoFirst || instance instanceof ARTGrammarInstanceOptional
        || instance instanceof ARTGrammarInstancePositiveClosure || instance instanceof ARTGrammarInstanceKleeneClosure) {
      changed |= (instance.erL != instance.getSibling());
      instance.erL = instance.getSibling();
    }
    if (instance instanceof ARTGrammarInstanceCat || instance instanceof ARTGrammarInstanceAlt) {
      changed |= (instance.erL != rightmostElementRec(instance));
      instance.erL = rightmostElementRec(instance);
    }

    // System.out.printf("Leaving node %d with changed %s\n", instance.getKey(), changed ? "true" : "false");

    return changed;
  }

  private void computefiRRec(ARTGrammarInstance node) {
    // System.out.printf("artComputefiRRec() visiting node %d %s\n", node.getKey(), node.getClass());

    if (node instanceof ARTGrammarInstanceRoot)
      for (ARTGrammarInstance tmp = node.getChild(); tmp != null; tmp = tmp.getSibling())
        computefiRRec(tmp);
    else if (node instanceof ARTGrammarInstanceLHS)
      for (ARTGrammarInstance tmp = node.getChild(); tmp != null; tmp = tmp.getSibling())
        computefiRRec(tmp);
    else if (node instanceof ARTGrammarInstanceDoFirst)
      computefiRRec(node.getChild());
    else if (node instanceof ARTGrammarInstancePositiveClosure || node instanceof ARTGrammarInstanceKleeneClosure || node instanceof ARTGrammarInstanceOptional)
      ;
    else if (node instanceof ARTGrammarInstanceAlt)
      for (ARTGrammarInstance tmp = node.getChild(); tmp != null; tmp = tmp.getSibling())
        computefiRRec(tmp);

    else if (node instanceof ARTGrammarInstanceCat && node.getChild().getSibling().getSibling().getSibling() == null) // test for unary
    {
      ARTGrammarInstance child2 = node.getChild().getSibling();
      if (!(child2 instanceof ARTGrammarInstanceNonterminal || child2 instanceof ARTGrammarInstanceTerminal)) computefiRRec(child2);
    }

    else if (node instanceof ARTGrammarInstanceCat)
      computefiRRec(node.getChild().getSibling());

    else if (node instanceof ARTGrammarInstanceNonterminal) {
      // Mods to account for Liz's refinement of fiR
      boolean twins = false;

      if (node.getSibling().getSibling() != null) {
        ARTGrammarInstance successorGrammarNode = node.getSibling().getSibling();
        if (successorGrammarNode instanceof ARTGrammarInstanceNonterminal && successorGrammarNode.getPayload() == node.getPayload()) twins = true;
      }
      // End of refinement

      if (!(twins && node.first.contains(epsilon))) node.getSibling().isFiR = true;
    }

    else if (node instanceof ARTGrammarInstanceTerminal)
      node.getSibling().isFiR = true;

    else if (node instanceof ARTGrammarInstanceEpsilon || node instanceof ARTGrammarInstanceSlot || node instanceof ARTGrammarInstanceTear
        || node instanceof ARTGrammarInstanceAnnotation)
      ;

    else
      throw new ARTUncheckedException("Unexpected node " + node.getKey() + "encountered during artComputefiRRec()\n");

  }

  private void computeIsEFBNFRec(ARTGrammarInstance instance) {
    // System.out.println("computeIsEFBNFRec at " + instance);
    if (instance == null) return;

    if (instance instanceof ARTGrammarInstanceEpsilon || instance instanceof ARTGrammarInstanceKleeneClosure
        || instance instanceof ARTGrammarInstancePositiveClosure || instance instanceof ARTGrammarInstanceOptional || instance instanceof ARTGrammarInstanceIter
        || (instance instanceof ARTGrammarInstanceDoFirst) && !(instance.isPredictivePop || instance.isPostPredictivePop)) {
      // System.out.println("Resetting isFBNF");
      isFBNF = false;
    }

    if (instance instanceof ARTGrammarInstanceEpsilon || instance instanceof ARTGrammarInstanceKleeneClosure
        || instance instanceof ARTGrammarInstancePositiveClosure || instance instanceof ARTGrammarInstanceOptional
        || instance instanceof ARTGrammarInstanceDoFirst || instance instanceof ARTGrammarInstanceIter) {
      // System.out.println("Setting isEBNF");
      isEBNF = true;
    }

    computeIsEFBNFRec(instance.getChild());
    computeIsEFBNFRec(instance.getSibling());
  }

  private void computeMergedSets(ARTGrammarInstance instance) {
    mergeSet(instance.first);
    mergeSet(instance.follow);
    mergeSet(instance.getGuard());

    for (ARTGrammarInstance tmp = instance.getChild(); tmp != null; tmp = tmp.getSibling())
      computeMergedSets(tmp);
  }

  private boolean computePlAttributeRec(ARTGrammarInstance node) {
    if (node == null) return false;

    boolean changed = false;

    if (!(node instanceof ARTGrammarInstanceSlot)) // Do not recurse through POS nodes
      for (ARTGrammarInstance tmp = node.getChild(); tmp != null; tmp = tmp.getSibling())
      computePlAttributeRec(tmp);

    changed |= (node.pL != pL(node));
    node.pL = pL(node);
    // System.out.println(changed);
    return changed;
  }

  private void computePredictivePopRec(ARTGrammarInstance node) {
    node.isPostPredictivePop = true;
    if (node.getSibling() != null) node.getSibling().isPostPredictivePop = true;

    if (node instanceof ARTGrammarInstanceRoot || node instanceof ARTGrammarInstanceLHS || node instanceof ARTGrammarInstanceAlt
        || node instanceof ARTGrammarInstanceDoFirst) // Propogate
      // to all
      // children
      for (ARTGrammarInstance tmp = node.getChild(); tmp != null; tmp = tmp.getSibling())
        computePredictivePopRec(tmp);
    else if (node instanceof ARTGrammarInstanceCat) { // Popogate to final child only as long as it is a terminal, nonterminal or do-first bracket
      ARTGrammarInstance lastNonPosChildNode = null;

      for (ARTGrammarInstance childNode = node.getChild(); childNode != null; childNode = childNode.getSibling()) { // locate last atom instance
        if (!(childNode instanceof ARTGrammarInstanceSlot)) lastNonPosChildNode = childNode;
      }

      if (lastNonPosChildNode instanceof ARTGrammarInstanceDoFirst)
        computePredictivePopRec(lastNonPosChildNode);
      else
        lastNonPosChildNode.getSibling().isPredictivePop = true;
    }

  }

  private boolean computeSetsRec(ARTGrammarInstance node, int level, ARTGrammarElementNonterminal lhs, ARTGrammarInstance bracketNode) {
    boolean changed = false;

    if (node == null) return changed;

    // artManager.text.printf("artComputesetsRec() visiting node %d at level %d with lhs %s and bracketNode %s\n", node.getKey(), level, lhs, bracketNode);

    changed |= (computeSetsRec(node.getSibling(), level, lhs, bracketNode));

    ARTGrammarInstance newBracketNode = bracketNode;

    if (node instanceof ARTGrammarInstanceDoFirst || node instanceof ARTGrammarInstanceOptional || node instanceof ARTGrammarInstancePositiveClosure
        || node instanceof ARTGrammarInstanceKleeneClosure)
      newBracketNode = node;

    if (node instanceof ARTGrammarInstanceRoot) {
      ; // Nothing to do
    }

    else if (node instanceof ARTGrammarInstanceLHS) {
      changed |= node.follow.addAll(((ARTGrammarElementNonterminal) node.getPayload()).getFollow());
      for (ARTGrammarInstance tmp = node.getChild(); tmp != null; tmp = tmp.getSibling())
        changed |= node.first.addAll(tmp.first);

      changed |= (((ARTGrammarElementNonterminal) node.getPayload()).getFirst()).addAll(node.first);
    }

    else if (node instanceof ARTGrammarInstanceAlt) {
      for (ARTGrammarInstance tmp = node.getChild(); tmp != null; tmp = tmp.getSibling())
        changed |= node.first.addAll(tmp.first);
    }

    else if (node instanceof ARTGrammarInstanceCat) {
      // Walk the children of a cat node until we find a non-nullable symbol skipping slot nodes
      for (ARTGrammarInstance child = node.getChild().getSibling(); child != null; child = child.getSibling().getSibling()) {
        HashSet<ARTGrammarElement> tmp = new HashSet<ARTGrammarElement>(child.first);
        if (child.getSibling().getSibling() != null) tmp.remove(epsilon);

        changed |= node.first.addAll(tmp);
        if (!child.first.contains(epsilon)) break;
      }
    }

    else if (node instanceof ARTGrammarInstanceSlot) {
      if (node.getSibling() == null) { // \beta is \epsilon
        if (newBracketNode == null)
          changed |= node.first.add(epsilon);
        else {
          changed |= node.first.addAll(newBracketNode.getSibling().first); // fold in follow for this bracket
          if (newBracketNode instanceof ARTGrammarInstanceKleeneClosure || newBracketNode instanceof ARTGrammarInstancePositiveClosure)
            changed |= node.first.addAll(newBracketNode.first);
        }
      } else { // \beta is not epsilon so there will be an X (a terminal or a nonterminal) following, then another pos slot
        HashSet<ARTGrammarElement> tmp = new HashSet<ARTGrammarElement>(node.getSibling().first);
        tmp.remove(epsilon);

        changed |= node.first.addAll(tmp);

        if (node.getSibling().first.contains(epsilon)) changed |= node.first.addAll(node.getSibling().getSibling().first); // bring over first (alpha X . beta)
      }

      // Guard set computation for slots
      HashSet<ARTGrammarElement> tmp = new HashSet<ARTGrammarElement>(node.first);
      if (tmp.contains(epsilon)) if (newBracketNode == null)
        tmp.addAll(lhs.getFollow());
      else {
        tmp.addAll(newBracketNode.getSibling().getGuard());
        // For loops, we need the first of the body as well
        if (newBracketNode instanceof ARTGrammarInstanceKleeneClosure || newBracketNode instanceof ARTGrammarInstancePositiveClosure)
          tmp.addAll(newBracketNode.getGuard());
      }

      tmp.remove(epsilon);
      changed |= node.getGuard().addAll(tmp);

      return changed; // Do not recurse into actions!
    }

    else if (node instanceof ARTGrammarInstanceNonterminal) {
      ARTGrammarElementNonterminal nonterminal = (ARTGrammarElementNonterminal) node.getPayload();

      changed |= node.first.addAll(nonterminal.getFirst());

      HashSet<ARTGrammarElement> tmp = new HashSet<ARTGrammarElement>();
      tmp.addAll(node.getSibling().first);
      tmp.remove(epsilon);

      changed |= node.follow.addAll(tmp);

      if (node.getSibling().first.contains(epsilon)) // are we at the end of a rule?
        changed |= node.follow.addAll(lhs.getFollow());

      changed |= nonterminal.getFollow().addAll(node.follow);
    }

    else if (node instanceof ARTGrammarInstanceTerminal)
      ; // Nothing to do: first set is computed in constructor for ARTInstanceTerminal

    else if (node instanceof ARTGrammarInstanceEpsilon)
      ; // Nothing to do: first set is computed in constructor for ARTInstanceEpsilon

    else if (node instanceof ARTGrammarInstanceDoFirst || node instanceof ARTGrammarInstanceOptional || node instanceof ARTGrammarInstancePositiveClosure
        || node instanceof ARTGrammarInstanceKleeneClosure) {
      changed |= node.first.addAll(node.getChild().first);
      if (node instanceof ARTGrammarInstanceOptional || node instanceof ARTGrammarInstanceKleeneClosure) changed |= node.first.add(epsilon);
      if (!(node instanceof ARTGrammarInstanceDoFirst)) {// Do not compute for ( for consistency with V2 although the template does not use them
        HashSet<ARTGrammarElement> tmp = new HashSet<ARTGrammarElement>(node.getChild().first);
        if (tmp.contains(epsilon)) tmp.addAll(node.getSibling().getGuard());

        tmp.remove(epsilon);
        changed |= node.getChild().getGuard().addAll(tmp);

      }
    }

    else
      throw new ARTUncheckedException("unsupported node type " + node.getClass().toString() + " during set evaluation\n");

    changed |= (computeSetsRec(node.getChild(), level + 1, level == 1 ? (ARTGrammarElementNonterminal) node.getPayload() : lhs,
        node.bracketInstance(newBracketNode)));
    return changed;
  }

  private void computeSlotPrefixMap() {
    // System.out.println("Computing prefix entries");
    // Build the prefix string map
    for (ARTGrammarElementNonterminal n : nonterminals) { // Iterate n over nonterminals
      for (ARTGrammarInstance p : n.getProductions()) { // Iterate p over the production roots of n
        int prefixLength = 0;
        LinkedList<ARTGrammarElement> prefixString = new LinkedList<>();
        // Modified 30 Oct 2017 to ensure that only proper prefixes are mapped
        for (ARTGrammarInstance e = p.getChild(); e != null; e = e.getSibling()) { // Iterate e over the sequence of elements
          // production root p
          if (e.getSibling() == null)
            e.prefixLength = prefixLength;
          else if (e instanceof ARTGrammarInstanceSlot) {
            e.prefixLength = prefixLength;
            // System.out.println("Building prefix entries for " + e + " prefix length " + e.prefixLength);
            if (prefixStringMap.get(prefixString) == null) prefixStringMap.put(new LinkedList<>(prefixString), (ARTGrammarInstanceSlot) e);
            getPrefixSlotMap().put((ARTGrammarInstanceSlot) e, prefixStringMap.get(prefixString));
            // System.out.println(prefixString + " mapped to " + prefixStringMap.get(prefixString));
          } else {
            e.prefixLength = ++prefixLength;
            if (!(e instanceof ARTGrammarInstanceEpsilon)) prefixString.add(e.getPayload());
          }
        }
      }
    }
    // System.out.println(prefixStringMap);
  }

  private Integer elementNumberFromTerm(ARTV3Module ARTV3Module, ARTValueTerm t) {
    if (t == null) return 0;

    if (((ARTValueString) t.getPayload()).toString().equals("slot")) return -1;

    if (((ARTValueString) t.getPayload()).toString().equals("nonterminal")) {
      ARTGrammarElement e = getNonterminalNameMap().get(new ARTName(ARTV3Module, termFirstChildLabel(t)));
      return (e == null) ? 0 : e.getElementNumber();
    }

    if (((ARTValueString) t.getPayload()).toString().equals("characterTerminal")) {
      ARTGrammarElement e = terminalCharacterNameMap.get(termFirstChildLabel(t));
      return (e == null) ? 0 : e.getElementNumber();
    }

    if (((ARTValueString) t.getPayload()).toString().equals("caseSensitiveTerminal")) {
      ARTGrammarElement e = terminalCaseSensitiveNameMap.get(termFirstChildLabel(t));
      return (e == null) ? 0 : e.getElementNumber();
    }
    if (((ARTValueString) t.getPayload()).toString().equals("caseInsensitiveTerminal")) {
      ARTGrammarElementTerminalCaseInsensitive e = terminalCaseInsensitiveNameMap.get(termFirstChildLabel(t));
      return (e == null) ? 0 : e.getElementNumber();
    }
    if (((ARTValueString) t.getPayload()).toString().equals("builtinTerminal")) {
      ARTGrammarElementTerminalBuiltin e = terminalBuiltinNameMap.get(termFirstChildLabel(t));
      return (e == null) ? 0 : e.getElementNumber();
    }
    throw new ARTUncheckedException("Unknown term constructor " + t.getPayload() + " in elementFromTerm()");
    // System.out.println("elementNumberFromTerm (" + ARTV3Module.getId() + ", " + t + ") returns " + ret);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ARTGrammar other = (ARTGrammar) obj;
    if (id == null) {
      if (other.id != null) return false;
    } else if (!id.equals(other.id)) return false;
    return true;
  }

  private void extendDerivationChoiceRelation(Map<ARTGrammarInstanceSlot, Set<ARTGrammarInstanceSlot>> map, ARTV3Module artModule, ARTValueTerm tLHS,
      Set<ARTValueTerm> set) {
    ARTGrammarInstanceSlot lhsSlot = matchSlot(artModule, tLHS);

    for (ARTValueTerm s : set) {
      ARTGrammarInstanceSlot rhsSlot = matchSlot(artModule, s);
      // System.out.println(chooserTermToString(tLHS) + ", " + chooserTermToString(s) + " matches " + lhsSlot + ", " + rhsSlot);
      if (lhsSlot != null && rhsSlot != null) {
        if (map.get(lhsSlot) == null) map.put(lhsSlot, new TreeSet<>());
        map.get(lhsSlot).add(rhsSlot);

        if (((String) tLHS.getPayload().getPayload()).equals("chooseDerivationAll"))
          for (ARTGrammarInstance ps = rhsSlot; ps.getLeftSibling() != null; ps = ps.getLeftSibling().getLeftSibling()) {
            // System.out.println(" loading slot " + ps);
            if (map.get(ps) == null) map.put((ARTGrammarInstanceSlot) ps, new TreeSet<>());
            map.get(ps).add((ARTGrammarInstanceSlot) ps);
          }
      }

    }
  }

  private ARTGrammarInstanceSlot matchSlot(ARTV3Module artModule, ARTValueTerm chooserTerm) {
    ARTGrammarElementNonterminal nonterminal = getNonterminalNameMap().get(new ARTName(artModule, chooserTerm.getChild().getChild().getPayload().toString()));
    if (nonterminal == null) {
      System.out.println("LHS of chooser term has no productions: " + chooserTermToString(chooserTerm));
      return null;
    }

    for (ARTGrammarInstanceCat p : nonterminal.getProductions()) {
      ARTGrammarInstance instance = p.getChild();
      ARTValueTerm term = chooserTerm.getChild().getSibling();

      ARTGrammarInstanceSlot matchSlot = null, mostRecentSlot = null;

      // Walk boththe chooserTerm and the production in lock step
      while (true) {
        int termElementNumber = elementNumberFromTerm(artModule, term);

        if (termElementNumber == -1) {
          if (!(instance instanceof ARTGrammarInstanceSlot)) throw new ARTUncheckedException("Internal error - expected slot whilst matching chooser term");
          matchSlot = (ARTGrammarInstanceSlot) instance;
          // System.out.println("AT slot");
          term = term.getSibling();
          termElementNumber = elementNumberFromTerm(artModule, term);
        }

        if (instance != null && instance instanceof ARTGrammarInstanceSlot) {
          mostRecentSlot = (ARTGrammarInstanceSlot) instance;
          instance = instance.getSibling();
        }

        // Special case for epsilon
        if (instance instanceof ARTGrammarInstanceEpsilon) {
          if (term != null) break;
          mostRecentSlot = (ARTGrammarInstanceSlot) instance.getSibling(); // Parse uses slot X ::= # .
          instance = null; // Fake up end of rule
        }

        if (instance == null || term == null) break;

        // System.out.println("Testing " + instance + " against " + term);
        if (instance.getPayload().getElementNumber() != termElementNumber) {
          break;
        }

        // System.out.println("Accept element");
        instance = instance.getSibling();
        term = term.getSibling();
      }

      if (matchSlot == null) matchSlot = mostRecentSlot;
      if (instance == null && term == null) return matchSlot;
    }
    return null;
  }

  private String chooserTermToString(ARTValueTerm chooserTerm) {
    String chooserRule = chooserTerm.getChild().getChild().toString() + " ::= ";

    for (ARTValueTerm c = chooserTerm.getChild().getSibling(); c != null; c = c.getSibling())
      if (c.getChild() == null)
        chooserRule += ". ";
      else
        chooserRule += c.getChild().toString() + " ";
    return chooserRule;
  }

  public ARTGrammarElementNonterminal findNonterminal(ARTV3Module ARTV3Module, String id) {
    return findNonterminal(new ARTName(ARTV3Module, id));
  }

  public ARTGrammarElementNonterminal findNonterminal(ARTGrammarElementModuleNonterminal moduleNonterminal) {
    return findNonterminal(moduleNonterminal.getModule(), moduleNonterminal.getId());
  }

  public ARTGrammarElementNonterminal findNonterminal(ARTName key) {
    ARTGrammarElementNonterminal ret = getNonterminalNameMap().get(key);
    if (ret == null) {
      getNonterminalNameMap().put(key, ret = new ARTGrammarElementNonterminal(key)); // Note that we are making a new one so that this grammar is independent of
                                                                                     // the
      // module
      nonterminals.add(ret);
      isDirty = true;
    }
    return ret;
  }

  public ARTGrammarElementTerminalBuiltin findTerminalBuiltin(String key) {
    ARTGrammarElementTerminalBuiltin ret = terminalBuiltinNameMap.get(key);
    if (ret == null) {
      terminalBuiltinNameMap.put(key, ret = new ARTGrammarElementTerminalBuiltin(key)); // Note that we are making a new one so that this grammar is
                                                                                        // independent of the
      // module
      terminals.add(ret);
      isDirty = true;
    }
    return ret;
  }

  public ARTGrammarElementTerminalCaseInsensitive findTerminalCaseInsensitive(String key) {
    ARTGrammarElementTerminalCaseInsensitive ret = terminalCaseInsensitiveNameMap.get(key);
    if (ret == null) {
      terminalCaseInsensitiveNameMap.put(key, ret = new ARTGrammarElementTerminalCaseInsensitive(key)); // Note that we are making a new one so that this
                                                                                                        // grammar is independent of the
      // module
      terminals.add(ret);
      isDirty = true;
    }
    return ret;
  }

  public ARTGrammarElementTerminalCaseSensitive findTerminalCaseSensitive(String key) {
    ARTGrammarElementTerminalCaseSensitive ret = terminalCaseSensitiveNameMap.get(key);
    if (ret == null) {
      terminalCaseSensitiveNameMap.put(key, ret = new ARTGrammarElementTerminalCaseSensitive(key)); // Note that we are making a new one so that this grammar
                                                                                                    // is independent of the
      // module
      terminals.add(ret);
      isDirty = true;
    }
    return ret;
  }

  public ARTGrammarElementTerminalCharacter findTerminalCharacter(String key) {
    ARTGrammarElementTerminalCharacter ret = terminalCharacterNameMap.get(key);
    if (ret == null) {
      terminalCharacterNameMap.put(key, ret = new ARTGrammarElementTerminalCharacter(key)); // Note that we are making a new one so that this grammar is
                                                                                            // independent of the
      // module
      terminals.add(ret);
      isDirty = true;
    }
    return ret;
  }

  public ARTGrammarElementNonterminal getDefaultStartNonterminal() {
    return defaultStartNonterminal;
  }

  public ARTGrammarElement getElement(int n) {
    ;
    return elementNumberMap.get(n);
  }

  public Map<Integer, ARTGrammarElement> getElementNumberMap() {
    return elementNumberMap;
  }

  public ARTGrammarElementEoS getEoS() {
    return eoS;
  }

  public ARTGrammarElementEpsilon getEpsilon() {
    return epsilon;
  }

  public int getFirstNonterminalElementNumber() {
    return firstNonterminalElementNumber;
  }

  public int getFirstTerminalElementNumber() {
    return firstTerminalElementNumber;
  }

  public ARTModeGrammarKind getGrammarKind() {
    return isEBNF && !isFBNF ? ARTModeGrammarKind.EBNF : isEBNF ? ARTModeGrammarKind.FBNF : ARTModeGrammarKind.BNF;
  }

  public String getId() {
    return id;
  }

  public ARTTree getInstanceTree() {
    return instanceTree;
  }

  public int getLastNonterminalElementNumber() {
    return lastNonterminalElementNumber;
  }

  public int getLastTerminalElementNumber() {
    return lastTerminalElementNumber;
  }

  public Map<ARTGrammarInstanceSlot, Set<ARTGrammarInstanceSlot>> getDerivationHigher() {
    return chooserDerivationHigher;
  }

  public Map<ARTGrammarInstanceSlot, Set<ARTGrammarInstanceSlot>> getDerivationLonger() {
    return chooserDerivationLonger;
  }

  public Map<ARTGrammarInstanceSlot, Set<ARTGrammarInstanceSlot>> getDerivationShorter() {
    return chooserDerivationShorter;
  }

  public HashMap<Set<ARTGrammarElement>, Integer> getMergedSets() {
    return mergedSets;
  }

  public Set<ARTGrammarElementNonterminal> getNonterminals() {
    return nonterminals;
  }

  public Map<ARTGrammarInstanceSlot, ARTGrammarInstanceSlot> getPrefixSlotMap() {
    return prefixSlotMap;
  }

  public Map<LinkedList<ARTGrammarElement>, ARTGrammarInstanceSlot> getPrefixStringMap() {
    return prefixStringMap;
  }

  public Set<String> getPreludeStrings() {
    return preludeStrings;
  }

  public Set<ARTGrammarElementAttribute> getSupportAttributes() {
    return supportAttributes;
  }

  public Set<String> getSupportStrings() {
    return supportStrings;
  }

  public Set<ARTGrammarElementTerminal> getTerminals() {
    return terminals;
  }

  public ARTGrammarElementNonterminal getUnaugmentedStartNonterminal() {
    return unaugmentedStartNonterminal;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  public boolean isDirty() {
    return isDirty;
  }

  public boolean isEBNF() {
    return isEBNF;
  }

  public boolean isEoS(int n) {
    return elementNumberMap.get(n) instanceof ARTGrammarElementEoS;
  }

  public boolean isEpsilon(int n) {
    return elementNumberMap.get(n) instanceof ARTGrammarElementEpsilon;
  }

  public boolean isFBNF() {
    return isFBNF;
  }

  public boolean isNonterminal(int n) {
    return elementNumberMap.get(n) instanceof ARTGrammarElementNonterminal;
  }

  public boolean isOutOfRange(int n) {
    return elementNumberMap.get(n) == null;
  }

  public boolean isTerminal(int n) {
    return elementNumberMap.get(n) instanceof ARTGrammarElementTerminal;
  }

  // leftmostElementRec is used by the generator, hence package private
  public ARTGrammarInstance leftmostElementRec(ARTGrammarInstance node) {
    ARTGrammarInstance ret;

    if (node.getChild() == null || node instanceof ARTGrammarInstanceSlot) // Only consider down to POS nodes!
      ret = node;
    else
      ret = leftmostElementRec(node.getChild());

    return ret;
  }

  private void mergeSet(Set<ARTGrammarElement> set) {
    if (set != null && !mergedSets.containsKey(set)) {
      // System.out.println("Merging set " + nextFreeSetNumber + " " + set);
      mergedSets.put(set, nextFreeSetNumber++);
    }
  }

  private void numberSymbols() {
    getElementNumberMap().clear();
    int nextFreeSymbol = 0;

    eoS.setElementNumber(nextFreeSymbol++);
    getElementNumberMap().put(eoS.getElementNumber(), eoS);
    firstTerminalElementNumber = nextFreeSymbol;
    for (ARTGrammarElementTerminal t : terminals) {
      t.setElementNumber(nextFreeSymbol++);
      getElementNumberMap().put(t.getElementNumber(), t);
    }
    lastTerminalElementNumber = nextFreeSymbol - 1;
    epsilon.setElementNumber(nextFreeSymbol++);
    getElementNumberMap().put(epsilon.getElementNumber(), epsilon);

    firstNonterminalElementNumber = nextFreeSymbol;
    for (ARTGrammarElementNonterminal n : nonterminals) {
      n.setElementNumber(nextFreeSymbol++);
      getElementNumberMap().put(n.getElementNumber(), n);
    }
    lastNonterminalElementNumber = nextFreeSymbol - 1;

    // TODO: grammar generation - enumerate slots in grammar
    lastSlotNumber = lastNonterminalElementNumber + 1;
    // System.out.println(" Renumbered symbols to : " + getElementNumberMap());
  }

  private ARTGrammarInstance pL(ARTGrammarInstance node) {
    // We only want the slots immediately before a | (see definition in paper), that is EoA (end of alternate) unless we are at the end of the last sequence in
    // a bracket
    if (node.isEoA && !(node.isEoD || node.isEoO || node.isEoP || node.isEoK)) if (node.aL == node)
      return node;
    else
      return pL(node.aL);
    if (node.isEoOP) return pL(node.nL);
    return node;
  }

  public void prettyPrint(String filename, boolean characterGrammar, boolean lexerGrammar, boolean parserGrammar, boolean prettyGrammar, boolean tokenGrammar) {
    ARTText pp = new ARTText(new ARTTextHandlerFile(filename));
    boolean first;

    if (characterGrammar)
      pp.println("(* Character grammar *)\n!paraterminal dummy // Switch off automatic whitespace");
    else if (lexerGrammar)
      pp.println("(* Lexical grammar *)\n");
    else if (parserGrammar)
      pp.println("(* Parser grammar *)\n");
    else if (prettyGrammar)
      pp.println("(* Pretty printed grammar *)\n");
    else if (tokenGrammar)
      pp.println("(* Token grammar *)");
    else
      throw new ARTUncheckedException("Pretty printer called with unknown mode");

    // Compute and print the elements directive
    Set<ARTGrammarElement> characters = new TreeSet<>();

    // First scan reachable for terminals, and add their characters
    for (ARTGrammarElement n : parserReachable) {
      if (n instanceof ARTGrammarElementTerminalCaseSensitive || n instanceof ARTGrammarElementTerminalCaseInsensitive) {
        String id = n.toString();
        for (int i = 1; i < id.length() - 1; i++)
          characters.add(new ARTGrammarElementTerminalCharacter(id.charAt(i) + ""));
      }
    }

    if (useDefaultInjectProductionString) {
      characters.add(new ARTGrammarElementTerminalCharacter("\n"));
      characters.add(new ARTGrammarElementTerminalCharacter("\r"));
      characters.add(new ARTGrammarElementTerminalCharacter("\t"));
      characters.add(new ARTGrammarElementTerminalCharacter(" "));
    }

    // System.out.println("Characters induced from implicit paraterminals symbols: " + characters);

    // Fold characters back into reachable set
    lexerReachable.addAll(characters);
    // System.out.println("Lexer reachable after union with induced characters: " + lexerReachable);

    Set<ARTGrammarElement> elements = new TreeSet<>(parserReachable);
    elements.addAll(lexerReachable);
    elements.addAll(injectInstanceReachable);

    // System.out.println("Elements: " + elements);

    if (lexerGrammar || parserGrammar) {
      pp.println("!element");
      // Now output all of the elements that are not case(in)sensitives
      for (ARTGrammarElement e : elements)
        if (e instanceof ARTGrammarElementTerminalCaseSensitive || e instanceof ARTGrammarElementTerminalCaseInsensitive)
          pp.println(" " + e.toParaterminalString() + ",");
        else
          pp.println(" " + e.toString() + ",");

      pp.println(" ARTLexerStart");
    }

    if (lexerGrammar || parserGrammar) {
      first = true;
      pp.print("\n!paraterminal\n");

      for (ARTGrammarElement e : elements)
        if (e instanceof ARTGrammarElementTerminalCaseSensitive || e instanceof ARTGrammarElementTerminalCaseInsensitive || paraterminals.contains(e)) {
          if (first)
            first = false;
          else
            pp.print(",\n");
          pp.print(" " + e.toParaterminalString());
          if (e instanceof ARTGrammarElementTerminal) pp.print(" = \"" + ((ARTGrammarElementTerminal) e).getId() + "\"");
        }
    }

    if (!lexerGrammar) if (!chooserDerivationHigher.isEmpty() || !chooserDerivationLonger.isEmpty() || !chooserDerivationShorter.isEmpty()) {
      pp.println("\n\n!choose");
      for (ARTGrammarInstanceSlot l : chooserDerivationHigher.keySet())
        for (ARTGrammarInstanceSlot r : chooserDerivationHigher.get(l))
          pp.println(l.lhsL.getPayload() + " ::= " + l.getProductionL().toGrammarSlotStringRec(null, "", "", false, true, "", "", paraterminals, false) + " > "
              + r.getProductionL().toGrammarSlotStringRec(null, "", "", false, true, "", "", paraterminals, false));
      for (ARTGrammarInstanceSlot l : chooserDerivationLonger.keySet())
        for (ARTGrammarInstanceSlot r : chooserDerivationLonger.get(l))
          pp.println(l.lhsL.getPayload() + " ::= " + l.getProductionL().toGrammarSlotStringRec(null, "", "", false, true, "", "", paraterminals, false) + " >> "
              + r.getProductionL().toGrammarSlotStringRec(null, "", "", false, true, "", "", paraterminals, false));
      for (ARTGrammarInstanceSlot l : chooserDerivationShorter.keySet())
        for (ARTGrammarInstanceSlot r : chooserDerivationShorter.get(l))
          pp.println(l.lhsL.getPayload() + " ::= " + l.getProductionL().toGrammarSlotStringRec(null, "", "", false, true, "", "", paraterminals, false) + " << "
              + r.getProductionL().toGrammarSlotStringRec(null, "", "", false, true, "", "", paraterminals, false));
    }
    // print start rule
    if (!lexerGrammar)
      pp.println("\n!start " + defaultStartNonterminal);
    else {
      pp.print("\n\n!start ARTLexerStart\n\nARTLexerStart ::=\n (");
      first = true;
      for (ARTGrammarElement e : elements)
        if (e instanceof ARTGrammarElementTerminalCaseSensitive || e instanceof ARTGrammarElementTerminalCaseInsensitive || paraterminals.contains(e)) {
          if (first)
            first = false;
          else
            pp.print("\n |");
          pp.print(" " + e.toParaterminalString());
        }
      pp.println("\n )*");
    }

    // Print productions
    for (ARTGrammarElement e : elements) {
      if (e instanceof ARTGrammarElementNonterminal) {
        ARTGrammarElementNonterminal n = (ARTGrammarElementNonterminal) e;

        if (n.lhsInstance != null) {
          if ((characterGrammar || lexerGrammar) && (paraterminals.contains(n) || lexerReachable.contains(n))) pp.println("\n" + n + " ::= "
              + n.lhsInstance.toGrammarSlotStringRec(null, "", "", false, false, "", paraterminals.contains(n) ? injectProductionString : "", null, false));
          if ((characterGrammar || parserGrammar || tokenGrammar) && parserReachable.contains(n) && !paraterminals.contains(n)) {
            pp.println("\n" + n + " ::= " + n.lhsInstance.toGrammarSlotStringRec(null, "", "", false, true,
                injectInstanceReachable.contains(n) ? "" : injectInstanceString, "", paraterminals, tokenGrammar));
          }
        }
      }

      if ((characterGrammar || lexerGrammar)
          && (e instanceof ARTGrammarElementTerminalCaseSensitive || e instanceof ARTGrammarElementTerminalCaseInsensitive)) {
        pp.print("\n" + e.toParaterminalString() + " ::=");
        String id = e.toString();
        for (int i = 1; i < id.length() - 1; i++)
          pp.print(" `" + id.charAt(i));
        pp.println(" " + injectProductionString);
      }

    }

    pp.close();
  }

  public ARTChooserSet getChooserSet(String string) {
    return chooserSets.get(string);
  }

  public void prettyPrintAllChoosers() {
    for (String id : chooserSets.keySet())
      prettyPrintChooserSets(id);
  }

  public void prettyPrintChooserSets(String id) {
    ARTChooserSet chooserSet = chooserSets.get(id);
    if (chooserSet == null) throw new ARTUncheckedException("Attempt to pretty print unknown chooser set " + id);
    ARTText pp = new ARTText(new ARTTextHandlerFile("ARTChoose" + id + ".art"));
    if (chooserSet.empty())
      pp.println("(* Empty chooser set *)");
    else {
      pp.println("\n!choose ");
      for (int lhs = 0; lhs < chooserSet.higher.length; lhs++)
        for (int rhs = 0; rhs < chooserSet.higher.length; rhs++)
          if (chooserSet.higher[lhs] != null && chooserSet.higher[lhs].get(rhs))
            pp.println(" " + toMaybeParaterminalElementString(lhs) + " > " + toMaybeParaterminalElementString(rhs));

      for (int lhs = 0; lhs < chooserSet.longer.length; lhs++)
        for (int rhs = 0; rhs < chooserSet.longer.length; rhs++)
          if (chooserSet.longer[lhs] != null && chooserSet.longer[lhs].get(rhs))
            pp.println(" " + toMaybeParaterminalElementString(lhs) + " >> " + toMaybeParaterminalElementString(rhs));

      for (int lhs = 0; lhs < chooserSet.shorter.length; lhs++)
        for (int rhs = 0; rhs < chooserSet.shorter.length; rhs++)
          if (chooserSet.shorter[lhs] != null && chooserSet.shorter[lhs].get(rhs))
            pp.println(" " + toMaybeParaterminalElementString(lhs) + " << " + toMaybeParaterminalElementString(rhs));
    }
    pp.close();
  }

  private String toMaybeParaterminalElementString(int lhs) {
    ARTGrammarElement element = getElement(lhs);
    if (element instanceof ARTGrammarElementTerminal)
      return element.toParaterminalString();
    else
      return element.toString();
  }

  private boolean processAbbreviation(ARTV3Module ARTV3Module, ARTGrammarInstance parent, ARTGLLRDTVertex abbreviationVertex) {
    // System.out.println("Processing abbreviation node " + abbreviationVertex + " with label "
    // + artManager.getParser().artGetLabelString(abbreviationVertex.getPayload().getLabel()));
    ARTGLLRDTVertex vertex = abbreviationVertex.getChild();

    if (vertexLabel(vertex, "(")) return false;

    parent = parent.addChild(new ARTGrammarInstanceCat(instanceCount++));
    parent.addChild(new ARTGrammarInstanceSlot(instanceCount++));
    if (vertexLabel(vertex, "nonterminal"))
      parent.addChild(
          new ARTGrammarInstanceNonterminal(instanceCount++, findNonterminal(ARTV3Module, ((ARTAT_ART_nonterminal) vertex.getPayload().getAttributes()).v)));
    else if (vertexLabel(vertex, "characterTerminal"))
      parent.addChild(
          new ARTGrammarInstanceTerminal(instanceCount++, findTerminalCharacter(((ARTAT_ART_characterTerminal) vertex.getPayload().getAttributes()).v)));
    else if (vertexLabel(vertex, "caseSensitiveTerminal"))
      parent.addChild(new ARTGrammarInstanceTerminal(instanceCount++,
          findTerminalCaseSensitive(((ARTAT_ART_caseSensitiveTerminal) vertex.getPayload().getAttributes()).v)));
    else if (vertexLabel(vertex, "caseInsensitiveTerminal"))
      parent.addChild(new ARTGrammarInstanceTerminal(instanceCount++,
          findTerminalCaseInsensitive(((ARTAT_ART_caseInsensitiveTerminal) vertex.getPayload().getAttributes()).v)));
    else if (vertexLabel(vertex, "builtinTerminal"))
      parent
          .addChild(new ARTGrammarInstanceTerminal(instanceCount++, findTerminalBuiltin(((ARTAT_ART_builtinTerminal) vertex.getPayload().getAttributes()).v)));
    else if (vertexLabel(vertex, "epsilon"))
      parent.addChild(new ARTGrammarInstanceEpsilon(instanceCount++, epsilon));
    else
      throw new ARTUncheckedException("Found abbreviation over unknown tree instance labelled" + vertex.getPayload());
    parent.addChild(new ARTGrammarInstanceSlot(instanceCount++));
    return true;
  }

  private void processAnnotations(ARTGrammarElementNonterminal lhs, ARTGrammarInstance instance, ARTGLLRDTVertex vertex) {
    if (vertex.getChild() == null) return;
    // System.err.printf("Processing annotations for grammar %s and RDT node %d - %s\n", instance, vertex.getKey(), vertex.toString());
    if (!vertexLabel(vertex.getChild(), "annotations")) throw new ARTUncheckedException("Expecting node labelled annotations in tree");
    for (ARTGLLRDTVertex annotation = vertex.getChild().getChild(); annotation != null; annotation = annotation.getSibling())
      if (vertexLabel(annotation, "name")) {
        instance.instanceName = ((ARTAT_ART_ID) annotation.getChild().getPayload().attributes).v;
      } else if (vertexLabel(annotation, "delay")) {
        instance.isDelayed = true;
        if (!(instance.getPayload() instanceof ARTGrammarElementNonterminal))
          throw new ARTUncheckedException("Only nonterminal nodes may be delayed (" + vertex + ")");
        ((ARTGrammarElementNonterminal) instance.getPayload()).hasDelayedInstances = true;
        lhs.setContainsDelayedInstances(true);
      } else if (vertexLabel(annotation, "gather")) {
        if (instance.gatherName != null) throw new ARTUncheckedException("Only one gather annotation allowed at node " + vertex);
        instance.gatherName = ((ARTAT_ART_ID) annotation.getChild().getPayload().attributes).v;
      } else if (vertexLabel(annotation, "fold")) {
        ARTGLLRDTVertex fold = annotation.getChild();
        if (instance.fold != ARTFold.EMPTY)
          System.out.println("Only one fold annotation allowed at node " + vertex + " on RDT node " + instance + " under LHS " + lhs);
        if (vertexLabel(fold, "^_"))
          instance.fold = ARTFold.NONE;
        else if (vertexLabel(fold, "^"))
          instance.fold = ARTFold.UNDER;
        else if (vertexLabel(fold, "^^"))
          instance.fold = ARTFold.OVER;
        else if (vertexLabel(fold, "^^^"))
          instance.fold = ARTFold.TEAR;
        else
          throw new ARTUncheckedException("Unknown fold annotation " + fold.getPayload() + " on RDT node " + instance + " under LHS " + lhs);
      }
  }

  private ARTGrammarInstance rightmostElementRec(ARTGrammarInstance node) {
    if (node == null) return null;
    // System.out.println("rightMostElementRec visiting null node!");
    // System.out.println("rightMostElementRec visiting node " + node.getKey());
    ARTGrammarInstance ret = null;

    if (node.getChild() == null || node instanceof ARTGrammarInstanceSlot) // Only consider down to POS nodes!
      ret = node;
    else
      for (ARTGrammarInstance tmp = node.getChild(); tmp != null; tmp = tmp.getSibling())
        ret = rightmostElementRec(tmp);

    return ret;
  }

  public void setSupportAttributes(Set<ARTGrammarElementAttribute> supportAttributes) {
    this.supportAttributes = supportAttributes;
  }

  private String termFirstChildLabel(ARTValueTerm t) {
    return t.getChild().getPayload().toString();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("** Grammar ");
    builder.append("'" + id + "'\n");
    builder.append(artDirectives);
    builder.append("\nisEBNF=");
    builder.append(isEBNF);
    builder.append(", isFBNF=");
    builder.append(isFBNF);
    builder.append("\nunaugmentedStartNonterminal=");
    builder.append(unaugmentedStartNonterminal);
    builder.append(", defaultStartNonterminal=");
    builder.append(defaultStartNonterminal);
    builder.append("\nnonterminals=");
    builder.append(nonterminals);
    builder.append("\nterminals=");
    builder.append(terminals);
    builder.append("\nwhitespaces=");
    builder.append(getWhitespaces());

    for (ARTGrammarElementNonterminal n : nonterminals) {
      builder.append("\nN ");
      builder.append(n.getId());
      builder.append(" first={");
      builder.append(n.getFirst());
      builder.append("} follow={");
      builder.append(n.getFollow());
      builder.append("}");
    }
    return builder.toString();
  }

  private boolean vertexLabel(ARTGLLRDTVertex vertex, String string) {
    // System.out.println("Checking vertex " + vertex + " against " + string);
    String[] strings = artManager.getParser().artLabelStrings;
    int label = vertex.getPayload().getLabel();
    String lstring = strings[label];
    return lstring.equals(string);
  }

  public Directives getDirectives() {
    return artDirectives;
  }

  public Map<ARTName, ARTGrammarElementNonterminal> getNonterminalNameMap() {
    return nonterminalNameMap;
  }

  public Set<ARTGrammarElement> getWhitespaces() {
    return whitespaces;
  }

  public int getLastSlotNumber() {
    return lastSlotNumber;
  }

  public ARTManager getARTManager() {
    return artManager;
  }

  public Set<ARTGrammarElementNonterminal> getParaterminals() {
    return paraterminals;
  }

  public Map<ARTGrammarElementNonterminal, String> getParaterminalAliases() {
    return paraterminalAliases;
  }

  public void generateStrings(boolean breadthFirst, boolean random, int maxIterations) {
    if (breadthFirst) {
      System.out.println("Breadth first generation not yet implemnented");
    } else {
      ArrayList<ARTGrammarElement> sententialForm = null;

      for (int iterationCount = 0; iterationCount < maxIterations; iterationCount++) {
        if (sententialForm == null) {
          sententialForm = new ArrayList<>();
          sententialForm.add(defaultStartNonterminal);
        }
        int leftmostNonterminalIndex = -1;
        for (int stringIndex = 0; stringIndex < sententialForm.size(); stringIndex++)
          if (sententialForm.get(stringIndex) instanceof ARTGrammarElementNonterminal) {
            leftmostNonterminalIndex = stringIndex;
            break;
          }

        if (leftmostNonterminalIndex == -1) {
          System.out.println(iterationCount + ": " + sententialForm);
          sententialForm = null;
        }

      }
    }
  }
}