package uk.ac.rhul.cs.csle.art.cfg;

import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.grammar.Grammar;
import uk.ac.rhul.cs.csle.art.term.ITerms;

public class Chooser {
  private final ITerms iTerms;

  public Chooser(ITerms iTerms) {
    this.iTerms = iTerms;
  }

  public void normalise(Grammar grammar, Set<Integer> chooseRules) {
    // Create an array of nine bindings that will be used to 'evaluate' chooser expressions using the value system
    int[] bindings = new int[9];
    final int anyCharacterTerminal = 1, anyBuiltinTerminal = 2, anyCaseSensitiveTerminal = 3, anyCaseInsensitiveTerminal = 4, anyParaterminal = 5,
        anyNonterminal = 6, anyLiteralTerminal = 7, anyTerminal = 8;
    //
    // String anyCharacterStr = "__set( ", anyBuiltinStr = "__set( ", anyCaseSensitiveStr = "__set( ", anyCaseInsensitiveStr = "__set( ", anyParaStr = "__set(
    // ",
    // anyNonStr = "__set( ", anyLiteralStr = "__set( ", anyStr = "__set( ";
    //
    // for (ARTGrammarElementTerminal t : terminals) {
    // if (t instanceof ARTGrammarElementTerminalCharacter) {
    // anyCharacterStr += "srCharacterTerminal(" + ITerms.escapeMeta(t.getId()) + "),";
    // anyStr += "srCharacterTerminal(" + ITerms.escapeMeta(t.getId()) + "),";
    // }
    //
    // if (t instanceof ARTGrammarElementTerminalBuiltin) {
    // anyBuiltinStr += "srBuiltinTerminal(" + ITerms.escapeMeta(t.getId()) + "),";
    // anyStr += "srBuiltinTerminal(" + ITerms.escapeMeta(t.getId()) + "),";
    // }
    //
    // if (t instanceof ARTGrammarElementTerminalCaseInsensitive) {
    // anyCaseInsensitiveStr += "srCaseInsensitiveTerminal(" + ITerms.escapeMeta(t.getId()) + "),";
    // anyLiteralStr += "srCaseInsensitiveTerminal(" + ITerms.escapeMeta(t.getId()) + "),";
    // anyStr += "srCaseInsensitiveTerminal(" + ITerms.escapeMeta(t.getId()) + "),";
    // }
    //
    // if (t instanceof ARTGrammarElementTerminalCaseSensitive) {
    // anyCaseSensitiveStr += "srCaseSensitiveTerminal(" + ITerms.escapeMeta(t.getId()) + "),";
    // anyLiteralStr += "srCaseSensitiveTerminal(" + ITerms.escapeMeta(t.getId()) + "),";
    // anyStr += "srCaseSensitiveTerminal(" + ITerms.escapeMeta(t.getId()) + "),";
    // }
    // }
    //
    // for (ARTGrammarElementNonterminal p : paraterminals) {
    // anyParaStr += "srNonterminal(" + ITerms.escapeMeta(p.getId()) + "),";
    // anyStr += "srNonterminal(" + ITerms.escapeMeta(p.getId()) + "),";
    // }
    //
    // for (ARTGrammarElementNonterminal n : nonterminals) {
    // anyNonStr += "srNonterminal(" + ITerms.escapeMeta(n.getId()) + "),";
    // }
    //
    // anyCharacterStr = anyCharacterStr.substring(0, anyCharacterStr.length() - 1) + ")";
    // anyBuiltinStr = anyBuiltinStr.substring(0, anyBuiltinStr.length() - 1) + ")";
    // anyCaseSensitiveStr = anyCaseSensitiveStr.substring(0, anyCaseSensitiveStr.length() - 1) + ")";
    // anyCaseInsensitiveStr = anyCaseInsensitiveStr.substring(0, anyCaseInsensitiveStr.length() - 1) + ")";
    // anyParaStr = anyParaStr.substring(0, anyParaStr.length() - 1) + ")";
    // anyNonStr = anyNonStr.substring(0, anyNonStr.length() - 1) + ")";
    //
    // anyLiteralStr = anyLiteralStr.substring(0, anyLiteralStr.length() - 1) + ")";
    // anyStr = anyStr.substring(0, anyStr.length() - 1) + ")";
    //
    // bindings[anyCharacterTerminal] = iTerms.findTerm(anyCharacterStr);
    // bindings[anyBuiltinTerminal] = iTerms.findTerm(anyBuiltinStr);
    // bindings[anyCaseSensitiveTerminal] = iTerms.findTerm(anyCaseSensitiveStr);
    // bindings[anyCaseInsensitiveTerminal] = iTerms.findTerm(anyCaseInsensitiveStr);
    // bindings[anyParaterminal] = iTerms.findTerm(anyParaStr);
    // bindings[anyNonterminal] = iTerms.findTerm(anyNonStr);
    //
    // bindings[anyLiteralTerminal] = iTerms.findTerm(anyLiteralStr);
    // bindings[anyTerminal] = iTerms.findTerm(anyStr);
    //
    // // System.out.println("Characters: " + iTerms.toString(bindings[1]));
    // // System.out.println("Builtins: " + iTerms.toString(bindings[2]));
    // // System.out.println("CaseSensitives: " + iTerms.toString(bindings[3]));
    // // System.out.println("CaseInsensitives: " + iTerms.toString(bindings[4]));
    // // System.out.println("Paras: " + iTerms.toString(bindings[5]));
    // // System.out.println("Nons: " + iTerms.toString(bindings[6]));
    // // System.out.println("Literals: " + iTerms.toString(bindings[7]));
    // // System.out.println("Any: " + iTerms.toString(bindings[8]));
    //
    // for (String chooserSetID : ARTV3Module.getChoosers().keySet()) {
    // List<String> chooseExpressionList = ARTV3Module.getChoosers().get(chooserSetID);
    // ARTChooserSet chooserSet = findChooserSet(chooserSetID);
    //
    // for (String expression : chooseExpressionList) {
    // // System.out.println("Evaluating chooser expression:" + expression);
    //
    // int root = iTerms.findTerm(expression);
    //
    // if (iTerms.getTermSymbolString(iTerms.getSubterm(root, 0)).equals("chooseSPPF")
    // || iTerms.getTermSymbolString(iTerms.getSubterm(root, 1)).equals("chooseSPPF"))
    // if (!(iTerms.getTermSymbolString(iTerms.getSubterm(root, 0)).equals("chooseSPPF")
    // && iTerms.getTermSymbolString(iTerms.getSubterm(root, 1)).equals("chooseSPPF"))) {
    // System.out.println("SPPF choosers can only use productions: skipping");
    // continue;
    // }
    //
    // int evaluated;
    // evaluated = iTerms.substitute(bindings, root, 0);
    //
    // switch (iTerms.getTermSymbolString(evaluated)) {
    // case "chooseHigher":
    // updateChooser(ARTV3Module, chooserSet.higher, iTerms.getSubterm(evaluated, 0), iTerms.getSubterm(evaluated, 1));
    // break;
    // case "chooseLonger":
    // updateChooser(ARTV3Module, chooserSet.longer, iTerms.getSubterm(evaluated, 0), iTerms.getSubterm(evaluated, 1));
    // break;
    // case "chooseShorter":
    // updateChooser(ARTV3Module, chooserSet.shorter, iTerms.getSubterm(evaluated, 0), iTerms.getSubterm(evaluated, 1));
    // break;
    // }
    // }
    // }
    //
    // // System.out.println("Used nonterminals " + usedNonterminals);
    // // System.out.println("Paraterminals " + paraterminals);
    //
    // for (ARTGrammarElementNonterminal n : nonterminals)
    // if (n.getProductions().isEmpty() && usedNonterminals.contains(n))
    // if (!paraterminals.contains(n)) System.out.println("*** Warning - undefined nonterminal " + n);
  }

}
