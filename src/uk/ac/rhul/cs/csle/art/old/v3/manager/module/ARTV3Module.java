package uk.ac.rhul.cs.csle.art.old.v3.manager.module;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.term.ITerms;
import uk.ac.rhul.cs.csle.art.old.v3.alg.gll.support.ARTGLLRDT;
import uk.ac.rhul.cs.csle.art.old.v3.alg.gll.support.ARTGLLRDTPayload;
import uk.ac.rhul.cs.csle.art.old.v3.alg.gll.support.ARTGLLRDTVertex;
import uk.ac.rhul.cs.csle.art.old.v3.manager.ARTManager;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElement;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementModuleNonterminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.parser.ARTV3Parser;
import uk.ac.rhul.cs.csle.art.old.v3.value.ARTValueTerm;

public class ARTV3Module {
  private final ARTManager artManager;
  private final String id;

  private ARTGrammarElementModuleNonterminal defaultStartNonterminal = null;
  private ARTGrammarElementModuleNonterminal injectNonterminal = null;
  private ARTGrammarElementModuleNonterminal absorbNonterminal = null;
  private final Set<ARTImport> imports = new HashSet<ARTImport>();
  private final Map<String, ARTGrammarElementModuleNonterminal> nonterminals = new HashMap<String, ARTGrammarElementModuleNonterminal>();
  private final Set<ARTGrammarElementModuleNonterminal> usedNonterminals = new HashSet<>();
  private final List<ARTGrammarElementModuleNonterminal> nonterminalList = new LinkedList<ARTGrammarElementModuleNonterminal>();
  private final Set<ARTValueTerm> whitespaceTerminals = new HashSet<>();
  private final Set<ARTValueTerm> paraterminals = new HashSet<>();
  private final Map<ARTValueTerm, String> paraterminalAliases = new HashMap<>();
  private final Map<ARTValueTerm, Set<ARTValueTerm>> derivationHigher = new HashMap<>();
  private final Map<ARTValueTerm, Set<ARTValueTerm>> derivationLonger = new HashMap<>();
  private final Map<ARTValueTerm, Set<ARTValueTerm>> derivationShorter = new HashMap<>();
  private final Set<String> preludeStrings = new HashSet<String>();
  private final Set<String> supportStrings = new HashSet<String>();
  private final boolean seenWhitespaceDeclaration = false;
  private final Set<ARTGrammarElement> elements = new HashSet<>();
  private String injectProductionString = "( `\\n | `\\r | `\\t | ` )*";
  private final Map<String, List<String>> choosers = new HashMap<>();
  public List<String> currentChooserExpressions = findChooserExpressions("");

  public ARTV3Module(ARTManager artManager, String id) {
    super();
    this.artManager = artManager;
    this.id = id;
  }

  public ARTManager getArtManager() {
    return artManager;
  }

  public String getId() {
    return id;
  }

  public Set<ARTImport> getImports() {
    return imports;
  }

  public void addImport(ARTImport anImport) {
    imports.add(anImport);
  }

  public ARTGrammarElementModuleNonterminal findNonterminal(String id) {
    ARTGrammarElementModuleNonterminal ret = nonterminals.get(id);

    if (ret == null) {
      nonterminals.put(id, ret = new ARTGrammarElementModuleNonterminal(this, id));
      nonterminalList.add(ret);
    }
    return ret;
  }

  public void checkFreshRHS(String id) {
    if (!findNonterminal(id).getProductions().isEmpty()) System.out.println(" ** Warning - nonterminal " + id + " has multiple rules in module " + this.id);
  }

  public void addProduction(String id, ARTGLLRDTVertex tree) {
    findNonterminal(id).addProduction(tree);
  }

  /* This is the hook that allows the V4 parser to create productions in a V3 module */
  public void addProduction(ITerms iTerms, Integer lhsTerm, Integer rhsterm, ARTGLLRDT artRDT) {
    String lhsId = iTerms.getString(lhsTerm);
    System.out.println("Adding production to V3 module using " + lhsId + " ::= " + iTerms.toString(rhsterm));
    addProduction(lhsId, makeTreeFromTerm(iTerms, rhsterm)); // Don't use the sentinel - the first chile is the element we want
  }

  int nextFreeNode = 1;

  // Bottom up tree construction
  private ARTGLLRDTVertex makeTreeFromTerm(ITerms iTerms, Integer t) {
    String id = iTerms.getString(iTerms.getTermSymbolIndex(t));
    System.out.println("makeTree from term at " + id);
    ARTGLLRDTPayload retPayload = new ARTGLLRDTPayload(null, 0, 0, 0, null); // Attribute block will be filled in below as necessary
    ARTGLLRDTVertex ret = new ARTGLLRDTVertex(nextFreeNode++, retPayload);

    int arity = iTerms.getTermArity(t);
    int[] children = iTerms.getTermChildren(t);

    switch (id) {
    case "srAlt":
      retPayload.label = ARTV3Parser.ARTL_ART_alt;
      break;
    case "srCat":
      retPayload.label = ARTV3Parser.ARTL_ART_cat;
      break;
    case "srCatTail":
      retPayload.label = ARTV3Parser.ARTL_ART_catTail;
      break;
    case "srSlot":
      retPayload.label = ARTV3Parser.ARTL_ART_slot;
      break;
    case "srKleeneClosure":
      break;
    case "srPositiveClosure":
      break;
    case "srOptional":
      break;
    case "srIter":
      break;
    case "srDiff":
      break;
    case "srUnion":
      break;
    case "srNot":
      break;
    case "srGrammarElement":
      break;
    case "srGrammarAtom":
      break;
    case "srGrammarAnnotations":
      retPayload.label = ARTV3Parser.ARTL_ART_annotations;
      break;
    case "srName":
      break;
    case "srDelay":
      retPayload.label = ARTV3Parser.ARTL_ART_delay;
      break;
    case "srFoldNone":
      break;
    case "srFoldUnder":
      break;
    case "srFoldOver":
      break;
    case "srGather":
      break;
    case "srInsert":
      break;
    case "srNativeAction":
      retPayload.label = ARTV3Parser.ARTL_ART_action;
      break;
    case "srNonterminal":
      retPayload.label = ARTV3Parser.ARTL_ART_nonterminal;
      break;
    case "srCaseSensitiveTerminal":
      retPayload.label = ARTV3Parser.ARTL_ART_caseSensitiveTerminal;
      break;
    case "srCaseInsensitiveTerminal":
      retPayload.label = ARTV3Parser.ARTL_ART_caseInsensitiveTerminal;
      break;
    case "srCharacterTerminal":
      retPayload.label = ARTV3Parser.ARTL_ART_characterTerminal;
      break;
    case "srCharacterRangeTerminal":
      // retPayload.label = ARTV3Parser.ARTL_ART_characterRangeTerminal;
      break;
    case "srEpsilon":
      retPayload.label = ARTV3Parser.ARTL_ART_epsilon;
      break;
    case "srSlotSymbol":
      break;
    case "srAttributeDefinition":
      break;
    }

    for (int i = 0; i < arity; i++)
      ret.addChild(makeTreeFromTerm(iTerms, children[i]));

    // if (arity == 0) {
    // retPayload.leftExtent = 666;
    // retPayload.rightExtent = 667;
    // } else {
    // retPayload.leftExtent = ret.getChild().getPayload().getLeftExtent();
    // retPayload.rightExtent = ret.getRightmostChild().getPayload().getRightExtent();
    // }
    return ret;
  }

  public void addAttribute(String id, String attributeID, String attributeType) {
    findNonterminal(id).addAttribute(attributeID, attributeType);
  }

  public void addDeleter(String id, ARTGLLRDTVertex tree) {
    findNonterminal(id).addDeleter(tree);
  }

  public void addWhiteSpaceTerminal(ARTValueTerm artValueTerm) {
    whitespaceTerminals.add(artValueTerm);
  }

  public void addParaterminal(ARTValueTerm artValueTerm) {
    paraterminals.add(artValueTerm);
  }

  public List<String> findChooserExpressions(String id) {
    if (getChoosers().get(id) == null) getChoosers().put(id, new LinkedList<>());
    return getChoosers().get(id);
  }

  public void addDerivationHigher(ARTValueTerm lhs, ARTValueTerm rhs) {
    // System.out.println("Extending Higher relation with " + lhs + " > " + rhs);
    if (getDerivationHigher().get(lhs) == null) getDerivationHigher().put(lhs, new HashSet<ARTValueTerm>());
    getDerivationHigher().get(lhs).add(rhs);
  }

  public void addDerivationLonger(ARTValueTerm lhs, ARTValueTerm rhs) {
    if (getDerivationLonger().get(lhs) == null) getDerivationLonger().put(lhs, new HashSet<ARTValueTerm>());
    getDerivationLonger().get(lhs).add(rhs);
  }

  public void addDerivationShorter(ARTValueTerm lhs, ARTValueTerm rhs) {
    if (getDerivationShorter().get(lhs) == null) getDerivationShorter().put(lhs, new HashSet<ARTValueTerm>());
    getDerivationShorter().get(lhs).add(rhs);
  }

  public Map<String, ARTGrammarElementModuleNonterminal> getNonterminals() {
    return nonterminals;
  }

  public List<ARTGrammarElementModuleNonterminal> getNonterminalList() {
    return nonterminalList;
  }

  public ARTGrammarElementModuleNonterminal getDefaultStart() {
    return defaultStartNonterminal;
  }

  public void setDefaultStart(String id) {
    this.defaultStartNonterminal = findNonterminal(id);
  }

  public void setDefaultStart(ARTGrammarElementModuleNonterminal start) {
    this.defaultStartNonterminal = start;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ARTV3Module other = (ARTV3Module) obj;
    if (id == null) {
      if (other.id != null) return false;
    } else if (!id.equals(other.id)) return false;
    return true;
  }

  public void addPreludeString(String preludeString) {
    preludeStrings.add(preludeString);
  }

  public void addSupportString(String supportString) {
    getSupportStrings().add(supportString);
  }

  public Set<String> getPreludeStrings() {
    return preludeStrings;
  }

  public Set<String> getSupportStrings() {
    return supportStrings;
  }

  public ARTGrammarElementModuleNonterminal getDefaultStartNonterminal() {
    return defaultStartNonterminal;
  }

  public void setDefaultStartNonterminal(ARTGrammarElementModuleNonterminal defaultStartNonterminal) {
    this.defaultStartNonterminal = defaultStartNonterminal;
  }

  public Set<ARTValueTerm> getWhitespaceTerminals() {
    return whitespaceTerminals;
  }

  public Set<ARTValueTerm> getParaterminals() {
    return paraterminals;
  }

  @Override
  public String toString() {
    return id + "\ndefaultStartNonterminal=" + defaultStartNonterminal + "\nimports=" + imports + "\nnonterminals=" + nonterminals + "\nwhitespaceTerminals="
        + whitespaceTerminals + "\nparaterminals=" + paraterminals + "\npreludeStrings=" + preludeStrings + "\nsupportStrings=" + supportStrings + "\n";
  }

  public Map<ARTValueTerm, Set<ARTValueTerm>> getDerivationHigher() {
    return derivationHigher;
  }

  public Map<ARTValueTerm, Set<ARTValueTerm>> getDerivationLonger() {
    return derivationLonger;
  }

  public Map<ARTValueTerm, Set<ARTValueTerm>> getDerivationShorter() {
    return derivationShorter;
  }

  public void addElement(ARTGrammarElement element) {
    getElements().add(element);
  }

  public Set<ARTGrammarElement> getElements() {
    return elements;
  }

  public void setInjectNonterminal(String v) {
    injectNonterminal = findNonterminal(v);
  }

  public void setAbsorbNonterminal(String v) {
    absorbNonterminal = findNonterminal(v);
  }

  public ARTGrammarElementModuleNonterminal getInjectNonterminal() {
    return injectNonterminal;
  }

  public ARTGrammarElementModuleNonterminal getAbsorbNonterminal() {
    return absorbNonterminal;
  }

  public String getInjectProductionString() {
    return injectProductionString;
  }

  public void setInjectProductionString(String injectProductionString) {
    this.injectProductionString = injectProductionString;
  }

  private void printProductionTreesRec(int indent, ARTGLLRDTVertex vertex) {
    if (vertex == null) return;
    for (int i = 0; i < indent; i++)
      System.out.print("  ");
    System.out.println(vertex + " " + artManager.getParser().artLabelStrings[vertex.getPayload().label]);
    printProductionTreesRec(indent + 1, vertex.getChild());
    printProductionTreesRec(indent, vertex.getSibling());

  }

  public void printProductionTrees() {
    for (String n : artManager.getDefaultMainModule().getNonterminals().keySet()) {
      System.out.println(n + ": ");

      for (ARTGLLRDTVertex p : artManager.getDefaultMainModule().getNonterminals().get(n).getProductions())
        printProductionTreesRec(0, p);
    }
  }

  public void addParaterminalAlias(ARTValueTerm artValueTerm, String v) {
    getParaterminalAliases().put(artValueTerm, v);
  }

  public Map<ARTValueTerm, String> getParaterminalAliases() {
    return paraterminalAliases;
  }

  public Map<String, List<String>> getChoosers() {
    return choosers;
  }

  public void addUsedNonterminal(String nonterminalID) {
    getUsedNonterminals().add(this.findNonterminal(nonterminalID));
  }

  public Set<ARTGrammarElementModuleNonterminal> getUsedNonterminals() {
    return usedNonterminals;
  }

}
