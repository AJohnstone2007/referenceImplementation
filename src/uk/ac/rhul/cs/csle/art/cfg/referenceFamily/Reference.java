package uk.ac.rhul.cs.csle.art.cfg.referenceFamily;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.gll.GLLBaseLine;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.gll.GLLHashPool;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GIFTKind;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GKind;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GNode;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.Grammar;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.LKind;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.lexer.LexerLM;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.rdsob.RDSOBExplicitStack;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.rdsob.RDSOBFunction;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.rdsob.RDSOBGenerator;
import uk.ac.rhul.cs.csle.art.term.ITerms;
import uk.ac.rhul.cs.csle.art.term.ITermsLowLevelAPI;
import uk.ac.rhul.cs.csle.art.term.TermTraverser;

public class Reference {
  private int good = 0;
  private int bad = 0;
  private int inadmissableCount = 0;
  private final int traceLevel = 0;
  private Grammar workingGrammar = null;
  private final ITerms iTerms = new ITermsLowLevelAPI();

  private ReferenceParser workingParser = new GLLBaseLine(); // default user parser
  private final LexerLM workingLexer = new LexerLM(); // default user lexer
  private int workingDerivationTerm = 0;

  public static void main(String[] args) {
    new Reference(args);
  }

  public Reference(String[] args) {
    // 1. Build script string
    StringBuilder scriptString = new StringBuilder();
    for (String a : args) {
      scriptString.append("\n");
      if (a.endsWith(".art"))
        try {
          scriptString.append(Files.readString(Paths.get((a))));
        } catch (IOException e) {
          fatal("Unable to open script file " + a);
        }
      else if (a.indexOf(".") != -1) {
        scriptString.append("!try (file(\"");
        scriptString.append(a);
        scriptString.append("\"))");
      } else
        scriptString.append(a);
    }
    // System.out.println("Script string: " + scriptString);

    // 2. Initialise scriptTraverser
    TermTraverser scriptTraverser = new TermTraverser(iTerms);
    scriptTraverser.addActionBreak("directive", (Integer t) -> directiveAction(t), null, null);

    scriptTraverser.addActionBreak("cfgLHS", (Integer t) -> workingGrammar.lhsAction(childSymbolString(t)), null, null);
    scriptTraverser.addAction("cfgSeq", (Integer t) -> workingGrammar.altAction(), null, (Integer t) -> workingGrammar.endAction(""));

    scriptTraverser.addAction("cfgEpsilon", (Integer t) -> workingGrammar.updateWorkingNode(GKind.EPS, "#"), null, null);
    scriptTraverser.addActionBreak("cfgNonterminal", (Integer t) -> workingGrammar.updateWorkingNode(GKind.N, childSymbolString(t)), null, null);
    scriptTraverser.addActionBreak("cfgCaseSensitiveTerminal", (Integer t) -> workingGrammar.updateWorkingNode(GKind.T, childSymbolStringStrip(t)), null, null);
    scriptTraverser.addActionBreak("cfgBuiltinTerminal", (Integer t) -> workingGrammar.updateWorkingNode(GKind.B, childSymbolString(t)), null, null);

    scriptTraverser.addAction("cfgDoFirst", (Integer t) -> workingGrammar.updateWorkingNode(GKind.DO, ""), null, null);
    scriptTraverser.addAction("cfgOptional", (Integer t) -> workingGrammar.updateWorkingNode(GKind.OPT, ""), null, null);
    scriptTraverser.addAction("cfgKleene", (Integer t) -> workingGrammar.updateWorkingNode(GKind.KLN, ""), null, null);
    scriptTraverser.addAction("cfgPositive", (Integer t) -> workingGrammar.updateWorkingNode(GKind.POS, ""), null, null);

    scriptTraverser.addAction("cfgFoldNone", (Integer t) -> workingGrammar.workingFold = GIFTKind.NONE, null, null);
    scriptTraverser.addAction("cfgFoldUnder", (Integer t) -> workingGrammar.workingFold = GIFTKind.UNDER, null, null);
    scriptTraverser.addAction("cfgFoldOver", (Integer t) -> workingGrammar.workingFold = GIFTKind.OVER, null, null);
    scriptTraverser.addAction("cfgFoldTear", (Integer t) -> workingGrammar.workingFold = GIFTKind.TEAR, null, null);

    scriptTraverser.addAction("cfgSlot", (Integer t) -> workingGrammar.workingAction = t, null, null);

    // 3. Build the script parser's grammar by traversing the scriptParserTerm
    final GLLBaseLine scriptParser = new GLLBaseLine();
    final LexerLM scriptLexer = new LexerLM();
  //@formatter:off
//    final String scriptParserTermString = "text(directive(whitespace(cfgBuiltinTerminal(SIMPLE\\_WHITESPACE), cfgBuiltinTerminal(COMMENT\\_NEST\\_ART), cfgBuiltinTerminal(COMMENT\\_LINE\\_C))), cfgRule(cfgLHS(text), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(directive)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(directive), cfgSlot, cfgFoldUnder(cfgNonterminal(text)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgRule)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgRule), cfgSlot, cfgFoldUnder(cfgNonterminal(text)), cfgSlot)), cfgRule(cfgLHS(directive), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('!')), cfgSlot, cfgNonterminal(term), cfgSlot)), cfgRule(cfgLHS(cfgRule), cfgSeq(cfgSlot, cfgNonterminal(cfgLHS), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\:\\:=')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot)), cfgRule(cfgLHS(cfgLHS), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgAlts), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('|')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot)), cfgRule(cfgLHS(cfgAlt), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilonCarrier), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot)), cfgRule(cfgLHS(cfgEpsilonCarrier), cfgSeq(cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot)), cfgRule(cfgLHS(cfgAltNoAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot)), cfgRule(cfgLHS(cfgElems), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot)), cfgRule(cfgLHS(cfgExtended), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPrim)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgDoFirst)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgOptional)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgKleene)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPositive)), cfgSlot)), cfgRule(cfgLHS(cfgAnnotation), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldUnder)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldOver)), cfgSlot)), cfgRule(cfgLHS(cfgFoldUnder), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^')), cfgSlot)), cfgRule(cfgLHS(cfgFoldOver), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^^')), cfgSlot)), cfgRule(cfgLHS(cfgDoFirst), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot)), cfgRule(cfgLHS(cfgOptional), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('?')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('?')), cfgSlot)), cfgRule(cfgLHS(cfgPositive), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('+')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('+')), cfgSlot)), cfgRule(cfgLHS(cfgKleene), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\*')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\*')), cfgSlot)), cfgRule(cfgLHS(cfgPrim), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgNonterminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgCaseSensitiveTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgBuiltinTerminal)), cfgSlot)), cfgRule(cfgLHS(cfgNonterminal), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgCaseSensitiveTerminal), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_PLAIN\\_SQ), cfgSlot)), cfgRule(cfgLHS(cfgBuiltinTerminal), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('&')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgEpsilon), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('#')), cfgSlot)), cfgRule(cfgLHS(cfgActions), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot)), cfgRule(cfgLHS(cfgActionSeq), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot)), cfgRule(cfgLHS(cfgAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgEquation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgAssignment)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgInsert), cfgSlot)), cfgRule(cfgLHS(cfgEquation), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('=')), cfgSlot, cfgNonterminal(term), cfgSlot)), cfgRule(cfgLHS(cfgAssignment), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\:=')), cfgSlot, cfgNonterminal(term), cfgSlot)), cfgRule(cfgLHS(cfgInsert), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^+')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgAttribute), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('.')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgSeq), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), cfgRule(cfgLHS(cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), cfgRule(cfgLHS(term), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgAttribute)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(INTEGER)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(REAL)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_DQ)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgBuiltinTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(ID)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(arguments)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot)), cfgLHS(arguments), cfgSeq(cfgSlot, cfgNonterminal(term), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(term), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgFoldUnder(cfgNonterminal(arguments)), cfgSlot))";
    final String scriptParserTermString = "text(directive(whitespace(cfgBuiltinTerminal(SIMPLE\\_WHITESPACE), cfgBuiltinTerminal(COMMENT\\_NEST\\_ART), cfgBuiltinTerminal(COMMENT\\_LINE\\_C))), cfgRule(cfgLHS(text), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(directive)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(directive), cfgSlot, cfgFoldUnder(cfgNonterminal(text)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgRule)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgRule), cfgSlot, cfgFoldUnder(cfgNonterminal(text)), cfgSlot)), cfgRule(cfgLHS(directive), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('!')), cfgSlot, cfgNonterminal(term), cfgSlot)), cfgRule(cfgLHS(cfgRule), cfgSeq(cfgSlot, cfgNonterminal(cfgLHS), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\:\\:=')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot)), cfgRule(cfgLHS(cfgLHS), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgAlts), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('|')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot)), cfgRule(cfgLHS(cfgAlt), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilonCarrier), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot)), cfgRule(cfgLHS(cfgEpsilonCarrier), cfgSeq(cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot)), cfgRule(cfgLHS(cfgAltNoAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot)), cfgRule(cfgLHS(cfgElems), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot)), cfgRule(cfgLHS(cfgExtended), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPrim)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgDoFirst)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgOptional)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgKleene)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPositive)), cfgSlot)), cfgRule(cfgLHS(cfgAnnotation), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldUnder)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldOver)), cfgSlot)), cfgRule(cfgLHS(cfgFoldUnder), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^')), cfgSlot)), cfgRule(cfgLHS(cfgFoldOver), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^^')), cfgSlot)), cfgRule(cfgLHS(cfgDoFirst), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot)), cfgRule(cfgLHS(cfgOptional), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('?')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('?')), cfgSlot)), cfgRule(cfgLHS(cfgPositive), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('+')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('+')), cfgSlot)), cfgRule(cfgLHS(cfgKleene), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\*')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\*')), cfgSlot)), cfgRule(cfgLHS(cfgPrim), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgNonterminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgCaseSensitiveTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgBuiltinTerminal)), cfgSlot)), cfgRule(cfgLHS(cfgNonterminal), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgCaseSensitiveTerminal), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_PLAIN\\_SQ), cfgSlot)), cfgRule(cfgLHS(cfgBuiltinTerminal), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('&')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgEpsilon), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('#')), cfgSlot)), cfgRule(cfgLHS(cfgActions), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot)), cfgRule(cfgLHS(cfgActionSeq), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot)), cfgRule(cfgLHS(cfgAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgEquation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgAssignment)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgInsert), cfgSlot)), cfgRule(cfgLHS(cfgEquation), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('=')), cfgSlot, cfgNonterminal(term), cfgSlot)), cfgRule(cfgLHS(cfgAssignment), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\:=')), cfgSlot, cfgNonterminal(term), cfgSlot)), cfgRule(cfgLHS(cfgInsert), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^+')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgAttribute), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('.')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgSeq), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), cfgRule(cfgLHS(cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), cfgRule(cfgLHS(term), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgAttribute)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(INTEGER)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(REAL)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_DQ)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgBuiltinTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(ID)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(arguments)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot)), cfgLHS(arguments), cfgSeq(cfgSlot, cfgNonterminal(term), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(term), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgFoldUnder(cfgNonterminal(arguments)), cfgSlot))";
    //@formatter:on

    final int scriptParserTerm = iTerms.findTerm(scriptParserTermString);

    scriptParser.grammar = workingGrammar = new Grammar("Script grammar", iTerms);
    scriptTraverser.traverse(scriptParserTerm); // Construct the scipt parser grammar by walking the script parser term
    workingGrammar.normalise();
    // System.out.println("Working grammar " + workingGrammar);

    // 4. Lex the script string
    scriptParser.inputString = scriptString.toString();
    scriptParser.accepted = false;
    scriptLexer.lex(scriptParser.inputString, scriptParser.grammar.lexicalKindsArray(), scriptParser.grammar.lexicalStringsArray(),
        scriptParser.grammar.whitespacesArray(), false);

    if (scriptLexer.tokens == null) fatal("Script lexical error");

    // 5. Parse the script lexicalisation
    scriptParser.input = scriptLexer.tokens;
    scriptParser.positions = scriptLexer.positions;
    scriptParser.parse();

    int scriptTerm = scriptParser.accepted ? scriptParser.derivationAsTerm() : 0;

    // System.out.println("Script term:\n" + iTerms.toString(scriptTerm, true, -1, null));
    if (scriptTerm == 0) fatal("Script syntax error");

    // 6. Execute the script by traversing the script term
    workingGrammar = new Grammar("Working grammar", iTerms);
    scriptTraverser.traverse(scriptTerm);

    // 7. (Development only) Show the working derivation and check bootstrap condition
    System.out.println("Working derivation term:\n" + iTerms.toString(workingDerivationTerm, false, -1, null));
    if (scriptParserTerm == workingDerivationTerm) System.out.println("Bootstrap achieved: script parser term and working derivation term identical");
  }

  private String childSymbolStringStrip(int t) {
    return strip(childSymbolString(t));
  }

  private String strip(String str) {
    return str.substring(1, str.length() - 1);
  }

  private String childSymbolString(int t) {
    return iTerms.getTermSymbolString(iTerms.getSubterm(t, 0));
  }

  public void directiveAction(int term) {
    // System.out.println("Processing directive " + iTerms.toString(term));
    switch (iTerms.getTermSymbolString(iTerms.getSubterm(term, 0))) {
    case "whitespace":
      workingGrammar.whitespaces.clear();
      for (int i = 0; i < iTerms.getTermArity(iTerms.getSubterm(term, 0)); i++)
        if (iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, i)).equals("cfgBuiltinTerminal"))
          switch (iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, i, 0))) {
          case "SIMPLE_WHITESPACE":
          workingGrammar.whitespaces.add(LKind.SIMPLE_WHITESPACE);
          break;
          case "COMMENT_NEST_ART":
          workingGrammar.whitespaces.add(LKind.COMMENT_NEST_ART);
          break;
          case "COMMENT_LINE_C":
          workingGrammar.whitespaces.add(LKind.COMMENT_LINE_C);
          break;
          case "COMMENT_BLOCK_C":
          workingGrammar.whitespaces.add(LKind.COMMENT_BLOCK_C);
          break;
          default:
          Reference.fatal("Unexpected !whitespace element " + iTerms.toString(iTerms.getSubterm(term, 0, i, 0)));
          }
      break;
    case "builtinTests":
      builtinTests();
      break;
    case "torture":
      torture();
      break;
    case "gllhp":
      workingParser = new GLLHashPool();
      break;
    case "gllbl":
      workingParser = new GLLBaseLine();
      break;
    case "rdsobgenerator":
      new RDSOBGenerator(workingGrammar, "OSBRDG");
      return;
    case "rdsobFunction":
      workingParser = new RDSOBFunction();
      break;
    case "rdsobExplicitStack":
      workingParser = new RDSOBExplicitStack();
      break;
    case "report":
      workingParser.report(true);
      break;
    case "visualise":
      // System.out.println(grammar);
      workingGrammar.visualise("grammar.dot");
      GNode.caseSensitiveTerminalStrop = "";
      workingParser.visualise();
      break;
    case "statisticsTitle":
      System.out.println(
          "Timestamp,Algorithm,Grammar,String name,String,Characters,Tokens,Outcome,Status,CPUs,Tok/s,Desc,GSS N,GSS E,Pop,SPPF S/I,SPPF P,SPPF E,Ambig,Heap,Heap/tok,Exact,Exact/tok,Tables,Tables/tok,Pool,Pool/tok,H0,H1,H2,H3,H4+");
      break;
    case "statistics":
      workingParser.statistics(true);
      break;
    case "try":
      String inputString = "a b";
      String tryOp = iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, 0));
      String tryOperand = iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, 0, 0));
      System.out.println("try " + tryOp + " on " + tryOperand);
      if (tryOp.equals("file")) try {
        inputString = Files.readString(Paths.get(strip(tryOperand)));
      } catch (IOException e) {
        fatal("Unable to open try file '" + tryOperand);
      }
      workingDerivationTerm = doTry("try", inputString, true, workingGrammar, workingParser, workingLexer, false);
      break;
    default:
      fatal("unknown directive " + iTerms.toString(term));
    }
  }

  /* Support for echoing the current line */
  public static int lineNumber(int index, String buffer) {
    if (buffer == null || index < 0) return 0;
    if (index >= buffer.length()) index = buffer.length() - 1;
    int lineCount = 1;
    for (int tmp = 0; tmp < index; tmp++)
      if (buffer.charAt(tmp) == '\n') lineCount++;
    return lineCount;

  }

  public static int columnNumber(int index, String buffer) { // Return x coordinate: note that the first column is column zero!
    int columnCount = 0;
    if (buffer == null || index < 0) return 0;
    if (index >= buffer.length()) index = buffer.length() - 1;
    if (index == 0) return 0;
    do {
      index--;
      columnCount++;
    } while (index > 0 && buffer.charAt(index) != '\n');

    if (index != 0) columnCount--; // If we did not terminate on start of buffer, then we must have terminated on \n so step forward 1
    return columnCount;
  }

  private static void appendLine(StringBuilder sb, int lineNumber, String buffer) {
    if (lineNumber < 1) return;
    int length = buffer.length();
    int tmp = lineNumber;
    int i;
    for (i = 0; tmp > 1 && i < length; i++)
      if (buffer.charAt(i) == '\n') tmp--;
    if (i >= length) return;

    sb.append(String.format("%5d: ", lineNumber));
    for (; i < length && buffer.charAt(i) != '\n'; i++)
      sb.append(buffer.charAt(i));
  }

  public static String echo(String message, int index, String buffer) {
    StringBuilder sb = new StringBuilder();
    int lineNumber = lineNumber(index, buffer), columnNumber = columnNumber(index, buffer);
    sb.append(lineNumber + "," + columnNumber + " " + message + "\n");
    if (buffer == null || index < 0) return sb.toString();

    // sb.append(String.format("%5d: ", lineNumber));

    int echoColumn = columnNumber;

    appendLine(sb, lineNumber - 1, buffer);
    appendLine(sb, lineNumber, buffer);
    // int echoIndex = index - echoColumn;
    // do {
    // sb.append(buffer.charAt(echoIndex++));
    // } while (echoIndex < buffer.length() && buffer.charAt(echoIndex) != '\n' && buffer.charAt(echoIndex) != '\0');

    sb.append("\n-------"); // Print pointer line
    for (int tmp = 0; tmp < echoColumn; tmp++)
      sb.append("-");
    sb.append("^\n");
    appendLine(sb, lineNumber + 1, buffer);
    sb.append("\n");

    return sb.toString();
  }

  public static void fatal(String msg) {
    System.out.println("Fatal error: " + msg);
    System.exit(1);
  }

  private int doTry(String inputStringName, String inputString, boolean outcome, Grammar grammar, ReferenceParser parser, LexerLM lexer,
      boolean suppressOutput) {
    parser.traceLevel = traceLevel;
    parser.inputString = inputString;
    parser.suppressEcho = suppressOutput;
    parser.accepted = false;
    parser.grammar = grammar;
    parser.inputStringName = inputStringName;

    grammar.normalise();
    lexer.lex(inputString, grammar.lexicalKindsArray(), grammar.lexicalStringsArray(), grammar.whitespacesArray(), suppressOutput);
    parser.input = lexer.tokens;
    parser.positions = lexer.positions;
    if (parser.input != null) parser.parse();
    if (parser.inadmissable)
      inadmissableCount++;
    else if (parser.accepted == outcome)
      good++;
    else
      bad++;
    if (!suppressOutput) {
      return parser.accepted ? parser.derivationAsTerm() : 0;
    }
    return 0;
  }

  private void reportSummary() {
    System.out.println("Totals: good: " + good + " bad: " + bad + " inadmissable: " + inadmissableCount);
  }

  private void builtinTests() {
    /*
     * grammar = new Grammar("a", parseScript("S ::= 'a'")); doTry("", "a", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer,
     * true); doTry("", "aa", false, grammar, parser, lexer, true); doTry("", "b", false, grammar, parser, lexer, true); grammar = new Grammar("aa",
     * parseScript("S ::= 'a' 'a'")); doTry("", "aa", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "a",
     * false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser, lexer, true); doTry("", "b", false, grammar, parser, lexer, true); grammar
     * = new Grammar("aaa", parseScript("S ::= 'a' 'a' 'a'")); doTry("", "aaa", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser,
     * lexer, true); doTry("", "a", false, grammar, parser, lexer, true); doTry("", "aa", false, grammar, parser, lexer, true); doTry("", "b", false, grammar,
     * parser, lexer, true); grammar = new Grammar("a | b", parseScript("S ::= 'a' | 'b'")); doTry("", "a", true, grammar, parser, lexer, true); doTry("", "b",
     * true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "aa", false, grammar, parser, lexer, true); doTry("",
     * "bb", false, grammar, parser, lexer, true); doTry("", "c", false, grammar, parser, lexer, true); grammar = new Grammar("aa | b",
     * parseScript("S ::= 'a' 'a' | 'b'")); doTry("", "aa", true, grammar, parser, lexer, true); doTry("", "b", true, grammar, parser, lexer, true); doTry("",
     * "", false, grammar, parser, lexer, true); doTry("", "a", false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser, lexer, true);
     * doTry("", "bb", false, grammar, parser, lexer, true); doTry("", "bbb", false, grammar, parser, lexer, true); doTry("", "c", false, grammar, parser,
     * lexer, true); doTry("", "cc", false, grammar, parser, lexer, true); doTry("", "ccc", false, grammar, parser, lexer, true); grammar = new
     * Grammar("aaa | b", parseScript("S ::= 'a' 'a' 'a' | 'b'")); doTry("", "aaa", true, grammar, parser, lexer, true); doTry("", "b", true, grammar, parser,
     * lexer, true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "a", false, grammar, parser, lexer, true); doTry("", "aa", false, grammar,
     * parser, lexer, true); doTry("", "bb", false, grammar, parser, lexer, true); doTry("", "bbb", false, grammar, parser, lexer, true); doTry("", "c", false,
     * grammar, parser, lexer, true); doTry("", "cc", false, grammar, parser, lexer, true); doTry("", "ccc", false, grammar, parser, lexer, true); grammar = new
     * Grammar("a | bb", parseScript("S ::= 'a' | 'b' 'b'")); doTry("", "a", true, grammar, parser, lexer, true); doTry("", "bb", true, grammar, parser, lexer,
     * true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "aa", false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser,
     * lexer, true); doTry("", "b", false, grammar, parser, lexer, true); doTry("", "bbb", false, grammar, parser, lexer, true); doTry("", "c", false, grammar,
     * parser, lexer, true); doTry("", "cc", false, grammar, parser, lexer, true); doTry("", "ccc", false, grammar, parser, lexer, true); grammar = new
     * Grammar("aa | bb", parseScript("S ::= 'a' 'a' | 'b' 'b'")); doTry("", "aa", true, grammar, parser, lexer, true); doTry("", "bb", true, grammar, parser,
     * lexer, true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "a", false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar,
     * parser, lexer, true); doTry("", "b", false, grammar, parser, lexer, true); doTry("", "bbb", false, grammar, parser, lexer, true); doTry("", "c", false,
     * grammar, parser, lexer, true); doTry("", "cc", false, grammar, parser, lexer, true); doTry("", "ccc", false, grammar, parser, lexer, true); grammar = new
     * Grammar("aaa | bb", parseScript("S ::= 'a' 'a' 'a' | 'b' 'b'")); doTry("", "aaa", true, grammar, parser, lexer, true); doTry("", "bb", true, grammar,
     * parser, lexer, true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "a", false, grammar, parser, lexer, true); doTry("", "aa", false,
     * grammar, parser, lexer, true); doTry("", "b", false, grammar, parser, lexer, true); doTry("", "bbb", false, grammar, parser, lexer, true); doTry("", "c",
     * false, grammar, parser, lexer, true); doTry("", "cc", false, grammar, parser, lexer, true); doTry("", "ccc", false, grammar, parser, lexer, true);
     * grammar = new Grammar("a | bbb", parseScript("S ::= 'a' | 'b' 'b' 'b'")); doTry("", "a", true, grammar, parser, lexer, true); doTry("", "bbb", true,
     * grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "aa", false, grammar, parser, lexer, true); doTry("", "aaa",
     * false, grammar, parser, lexer, true); doTry("", "b", false, grammar, parser, lexer, true); doTry("", "bb", false, grammar, parser, lexer, true);
     * doTry("", "c", false, grammar, parser, lexer, true); doTry("", "cc", false, grammar, parser, lexer, true); doTry("", "ccc", false, grammar, parser,
     * lexer, true); grammar = new Grammar("aa | bbb", parseScript("S ::= 'a' 'a' | 'b' 'b' 'b'")); doTry("", "aa", true, grammar, parser, lexer, true);
     * doTry("", "bbb", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "a", false, grammar, parser, lexer,
     * true); doTry("", "aaa", false, grammar, parser, lexer, true); doTry("", "b", false, grammar, parser, lexer, true); doTry("", "bb", false, grammar,
     * parser, lexer, true); doTry("", "c", false, grammar, parser, lexer, true); doTry("", "cc", false, grammar, parser, lexer, true); doTry("", "ccc", false,
     * grammar, parser, lexer, true); grammar = new Grammar("aaa | bbb", parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' 'b'")); doTry("", "aaa", true, grammar,
     * parser, lexer, true); doTry("", "bbb", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "a", false,
     * grammar, parser, lexer, true); doTry("", "aa", false, grammar, parser, lexer, true); doTry("", "b", false, grammar, parser, lexer, true); doTry("", "bb",
     * false, grammar, parser, lexer, true); doTry("", "c", false, grammar, parser, lexer, true); doTry("", "cc", false, grammar, parser, lexer, true);
     * doTry("", "ccc", false, grammar, parser, lexer, true); grammar = new Grammar("a | c", parseScript("S ::= 'a' | 'c'")); doTry("", "a", true, grammar,
     * parser, lexer, true); doTry("", "c", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "aa", false,
     * grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser, lexer, true); doTry("", "b", false, grammar, parser, lexer, true); doTry("",
     * "bb", false, grammar, parser, lexer, true); doTry("", "bbb", false, grammar, parser, lexer, true); doTry("", "cc", false, grammar, parser, lexer, true);
     * doTry("", "ccc", false, grammar, parser, lexer, true); grammar = new Grammar("aa | c", parseScript("S ::= 'a' 'a' | 'c'")); doTry("", "aa", true,
     * grammar, parser, lexer, true); doTry("", "c", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "a",
     * false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser, lexer, true); doTry("", "b", false, grammar, parser, lexer, true);
     * doTry("", "bb", false, grammar, parser, lexer, true); doTry("", "bbb", false, grammar, parser, lexer, true); doTry("", "cc", false, grammar, parser,
     * lexer, true); doTry("", "ccc", false, grammar, parser, lexer, true); grammar = new Grammar("aaa | c", parseScript("S ::= 'a' 'a' 'a' | 'c'")); doTry("",
     * "aaa", true, grammar, parser, lexer, true); doTry("", "c", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer, true);
     * doTry("", "a", false, grammar, parser, lexer, true); doTry("", "aa", false, grammar, parser, lexer, true); doTry("", "b", false, grammar, parser, lexer,
     * true); doTry("", "bb", false, grammar, parser, lexer, true); doTry("", "bbb", false, grammar, parser, lexer, true); doTry("", "cc", false, grammar,
     * parser, lexer, true); doTry("", "ccc", false, grammar, parser, lexer, true); grammar = new Grammar("a | b | c", parseScript("S ::= 'a' | 'b' | 'c'"));
     * doTry("", "a", true, grammar, parser, lexer, true); doTry("", "b", true, grammar, parser, lexer, true); doTry("", "c", true, grammar, parser, lexer,
     * true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "aa", false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser,
     * lexer, true); doTry("", "bb", false, grammar, parser, lexer, true); doTry("", "bbb", false, grammar, parser, lexer, true); doTry("", "cc", false,
     * grammar, parser, lexer, true); doTry("", "ccc", false, grammar, parser, lexer, true); grammar = new Grammar("aa | b | c",
     * parseScript("S ::= 'a' 'a' | 'b' | 'c'")); doTry("", "aa", true, grammar, parser, lexer, true); doTry("", "b", true, grammar, parser, lexer, true);
     * doTry("", "c", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "a", false, grammar, parser, lexer,
     * true); doTry("", "aaa", false, grammar, parser, lexer, true); doTry("", "bb", false, grammar, parser, lexer, true); doTry("", "bbb", false, grammar,
     * parser, lexer, true); doTry("", "cc", false, grammar, parser, lexer, true); doTry("", "ccc", false, grammar, parser, lexer, true); grammar = new
     * Grammar("aaa | b | c", parseScript("S ::= 'a' 'a' 'a' | 'b' | 'c'")); doTry("", "aaa", true, grammar, parser, lexer, true); doTry("", "b", true, grammar,
     * parser, lexer, true); doTry("", "c", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "a", false,
     * grammar, parser, lexer, true); doTry("", "aa", false, grammar, parser, lexer, true); doTry("", "bb", false, grammar, parser, lexer, true); doTry("",
     * "bbb", false, grammar, parser, lexer, true); doTry("", "cc", false, grammar, parser, lexer, true); doTry("", "ccc", false, grammar, parser, lexer, true);
     * grammar = new Grammar("a | bb | c", parseScript("S ::= 'a' | 'b' 'b' | 'c'")); doTry("", "a", true, grammar, parser, lexer, true); doTry("", "bb", true,
     * grammar, parser, lexer, true); doTry("", "c", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "aa",
     * false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser, lexer, true); doTry("", "b", false, grammar, parser, lexer, true);
     * doTry("", "bbb", false, grammar, parser, lexer, true); doTry("", "cc", false, grammar, parser, lexer, true); doTry("", "ccc", false, grammar, parser,
     * lexer, true); grammar = new Grammar("aa | bb | c", parseScript("S ::= 'a' 'a' | 'b' 'b' | 'c'")); doTry("", "aa", true, grammar, parser, lexer, true);
     * doTry("", "bb", true, grammar, parser, lexer, true); doTry("", "c", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer,
     * true); doTry("", "a", false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser, lexer, true); doTry("", "b", false, grammar, parser,
     * lexer, true); doTry("", "bbb", false, grammar, parser, lexer, true); doTry("", "cc", false, grammar, parser, lexer, true); doTry("", "ccc", false,
     * grammar, parser, lexer, true); grammar = new Grammar("aaa | bb | c", parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' | 'c'")); doTry("", "aaa", true, grammar,
     * parser, lexer, true); doTry("", "bb", true, grammar, parser, lexer, true); doTry("", "c", true, grammar, parser, lexer, true); doTry("", "", false,
     * grammar, parser, lexer, true); doTry("", "a", false, grammar, parser, lexer, true); doTry("", "aa", false, grammar, parser, lexer, true); doTry("", "b",
     * false, grammar, parser, lexer, true); doTry("", "bbb", false, grammar, parser, lexer, true); doTry("", "cc", false, grammar, parser, lexer, true);
     * doTry("", "ccc", false, grammar, parser, lexer, true); grammar = new Grammar("a | bbb | c", parseScript("S ::= 'a' | 'b' 'b' 'b' | 'c'")); doTry("", "a",
     * true, grammar, parser, lexer, true); doTry("", "bbb", true, grammar, parser, lexer, true); doTry("", "c", true, grammar, parser, lexer, true); doTry("",
     * "", false, grammar, parser, lexer, true); doTry("", "aa", false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser, lexer, true);
     * doTry("", "b", false, grammar, parser, lexer, true); doTry("", "bb", false, grammar, parser, lexer, true); doTry("", "cc", false, grammar, parser, lexer,
     * true); doTry("", "ccc", false, grammar, parser, lexer, true); grammar = new Grammar("aa | bbb | c", parseScript("S ::= 'a' 'a' | 'b' 'b' 'b' | 'c'"));
     * doTry("", "aa", true, grammar, parser, lexer, true); doTry("", "bbb", true, grammar, parser, lexer, true); doTry("", "c", true, grammar, parser, lexer,
     * true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "a", false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser,
     * lexer, true); doTry("", "b", false, grammar, parser, lexer, true); doTry("", "bb", false, grammar, parser, lexer, true); doTry("", "cc", false, grammar,
     * parser, lexer, true); doTry("", "ccc", false, grammar, parser, lexer, true); grammar = new Grammar("aaa | bbb | c",
     * parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' 'b' | 'c'")); doTry("", "aaa", true, grammar, parser, lexer, true); doTry("", "bbb", true, grammar, parser,
     * lexer, true); doTry("", "c", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "a", false, grammar,
     * parser, lexer, true); doTry("", "aa", false, grammar, parser, lexer, true); doTry("", "b", false, grammar, parser, lexer, true); doTry("", "bb", false,
     * grammar, parser, lexer, true); doTry("", "cc", false, grammar, parser, lexer, true); doTry("", "ccc", false, grammar, parser, lexer, true); grammar = new
     * Grammar("a | cc", parseScript("S ::= 'a' | 'c' 'c'")); doTry("", "a", true, grammar, parser, lexer, true); doTry("", "cc", true, grammar, parser, lexer,
     * true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "aa", false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser,
     * lexer, true); doTry("", "b", false, grammar, parser, lexer, true); doTry("", "bb", false, grammar, parser, lexer, true); doTry("", "bbb", false, grammar,
     * parser, lexer, true); doTry("", "c", false, grammar, parser, lexer, true); doTry("", "ccc", false, grammar, parser, lexer, true); grammar = new
     * Grammar("aa | cc", parseScript("S ::= 'a' 'a' | 'c' 'c'")); doTry("", "aa", true, grammar, parser, lexer, true); doTry("", "cc", true, grammar, parser,
     * lexer, true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "a", false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar,
     * parser, lexer, true); doTry("", "b", false, grammar, parser, lexer, true); doTry("", "bb", false, grammar, parser, lexer, true); doTry("", "bbb", false,
     * grammar, parser, lexer, true); doTry("", "c", false, grammar, parser, lexer, true); doTry("", "ccc", false, grammar, parser, lexer, true); grammar = new
     * Grammar("aaa | cc", parseScript("S ::= 'a' 'a' 'a' | 'c' 'c'")); doTry("", "aaa", true, grammar, parser, lexer, true); doTry("", "cc", true, grammar,
     * parser, lexer, true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "a", false, grammar, parser, lexer, true); doTry("", "aa", false,
     * grammar, parser, lexer, true); doTry("", "b", false, grammar, parser, lexer, true); doTry("", "bb", false, grammar, parser, lexer, true); doTry("",
     * "bbb", false, grammar, parser, lexer, true); doTry("", "c", false, grammar, parser, lexer, true); doTry("", "ccc", false, grammar, parser, lexer, true);
     * grammar = new Grammar("a | b | cc", parseScript("S ::= 'a' | 'b' | 'c' 'c'")); doTry("", "a", true, grammar, parser, lexer, true); doTry("", "b", true,
     * grammar, parser, lexer, true); doTry("", "cc", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "aa",
     * false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser, lexer, true); doTry("", "bb", false, grammar, parser, lexer, true);
     * doTry("", "bbb", false, grammar, parser, lexer, true); doTry("", "c", false, grammar, parser, lexer, true); doTry("", "ccc", false, grammar, parser,
     * lexer, true); grammar = new Grammar("aa | b | cc", parseScript("S ::= 'a' 'a' | 'b' | 'c' 'c'")); doTry("", "aa", true, grammar, parser, lexer, true);
     * doTry("", "b", true, grammar, parser, lexer, true); doTry("", "cc", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer,
     * true); doTry("", "a", false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser, lexer, true); doTry("", "bb", false, grammar,
     * parser, lexer, true); doTry("", "bbb", false, grammar, parser, lexer, true); doTry("", "c", false, grammar, parser, lexer, true); doTry("", "ccc", false,
     * grammar, parser, lexer, true); grammar = new Grammar("aaa | b | cc", parseScript("S ::= 'a' 'a' 'a' | 'b' | 'c' 'c'")); doTry("", "aaa", true, grammar,
     * parser, lexer, true); doTry("", "b", true, grammar, parser, lexer, true); doTry("", "cc", true, grammar, parser, lexer, true); doTry("", "", false,
     * grammar, parser, lexer, true); doTry("", "a", false, grammar, parser, lexer, true); doTry("", "aa", false, grammar, parser, lexer, true); doTry("", "bb",
     * false, grammar, parser, lexer, true); doTry("", "bbb", false, grammar, parser, lexer, true); doTry("", "c", false, grammar, parser, lexer, true);
     * doTry("", "ccc", false, grammar, parser, lexer, true); grammar = new Grammar("a | bb | cc", parseScript("S ::= 'a' | 'b' 'b' | 'c' 'c'")); doTry("", "a",
     * true, grammar, parser, lexer, true); doTry("", "bb", true, grammar, parser, lexer, true); doTry("", "cc", true, grammar, parser, lexer, true); doTry("",
     * "", false, grammar, parser, lexer, true); doTry("", "aa", false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser, lexer, true);
     * doTry("", "b", false, grammar, parser, lexer, true); doTry("", "bbb", false, grammar, parser, lexer, true); doTry("", "c", false, grammar, parser, lexer,
     * true); doTry("", "ccc", false, grammar, parser, lexer, true); grammar = new Grammar("aa | bb | cc", parseScript("S ::= 'a' 'a' | 'b' 'b' | 'c' 'c'"));
     * doTry("", "aa", true, grammar, parser, lexer, true); doTry("", "bb", true, grammar, parser, lexer, true); doTry("", "cc", true, grammar, parser, lexer,
     * true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "a", false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser,
     * lexer, true); doTry("", "b", false, grammar, parser, lexer, true); doTry("", "bbb", false, grammar, parser, lexer, true); doTry("", "c", false, grammar,
     * parser, lexer, true); doTry("", "ccc", false, grammar, parser, lexer, true); grammar = new Grammar("aaa | bb | cc",
     * parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' | 'c' 'c'")); doTry("", "aaa", true, grammar, parser, lexer, true); doTry("", "bb", true, grammar, parser,
     * lexer, true); doTry("", "cc", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "a", false, grammar,
     * parser, lexer, true); doTry("", "aa", false, grammar, parser, lexer, true); doTry("", "b", false, grammar, parser, lexer, true); doTry("", "bbb", false,
     * grammar, parser, lexer, true); doTry("", "c", false, grammar, parser, lexer, true); doTry("", "ccc", false, grammar, parser, lexer, true); grammar = new
     * Grammar("a | bbb | cc", parseScript("S ::= 'a' | 'b' 'b' 'b' | 'c' 'c'")); doTry("", "a", true, grammar, parser, lexer, true); doTry("", "bbb", true,
     * grammar, parser, lexer, true); doTry("", "cc", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "aa",
     * false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser, lexer, true); doTry("", "b", false, grammar, parser, lexer, true);
     * doTry("", "bb", false, grammar, parser, lexer, true); doTry("", "c", false, grammar, parser, lexer, true); doTry("", "ccc", false, grammar, parser,
     * lexer, true); grammar = new Grammar("aa | bbb | cc", parseScript("S ::= 'a' 'a' | 'b' 'b' 'b' | 'c' 'c'")); doTry("", "aa", true, grammar, parser, lexer,
     * true); doTry("", "bbb", true, grammar, parser, lexer, true); doTry("", "cc", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser,
     * lexer, true); doTry("", "a", false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser, lexer, true); doTry("", "b", false, grammar,
     * parser, lexer, true); doTry("", "bb", false, grammar, parser, lexer, true); doTry("", "c", false, grammar, parser, lexer, true); doTry("", "ccc", false,
     * grammar, parser, lexer, true); grammar = new Grammar("aaa | bbb | cc", parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' 'b' | 'c' 'c'")); doTry("", "aaa", true,
     * grammar, parser, lexer, true); doTry("", "bbb", true, grammar, parser, lexer, true); doTry("", "cc", true, grammar, parser, lexer, true); doTry("", "",
     * false, grammar, parser, lexer, true); doTry("", "a", false, grammar, parser, lexer, true); doTry("", "aa", false, grammar, parser, lexer, true);
     * doTry("", "b", false, grammar, parser, lexer, true); doTry("", "bb", false, grammar, parser, lexer, true); doTry("", "c", false, grammar, parser, lexer,
     * true); doTry("", "ccc", false, grammar, parser, lexer, true); grammar = new Grammar("a | ccc", parseScript("S ::= 'a' | 'c' 'c' 'c'")); doTry("", "a",
     * true, grammar, parser, lexer, true); doTry("", "ccc", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer, true); doTry("",
     * "aa", false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser, lexer, true); doTry("", "b", false, grammar, parser, lexer, true);
     * doTry("", "bb", false, grammar, parser, lexer, true); doTry("", "bbb", false, grammar, parser, lexer, true); doTry("", "c", false, grammar, parser,
     * lexer, true); doTry("", "cc", false, grammar, parser, lexer, true); grammar = new Grammar("aa | ccc", parseScript("S ::= 'a' 'a' | 'c' 'c' 'c'"));
     * doTry("", "aa", true, grammar, parser, lexer, true); doTry("", "ccc", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer,
     * true); doTry("", "a", false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser, lexer, true); doTry("", "b", false, grammar, parser,
     * lexer, true); doTry("", "bb", false, grammar, parser, lexer, true); doTry("", "bbb", false, grammar, parser, lexer, true); doTry("", "c", false, grammar,
     * parser, lexer, true); doTry("", "cc", false, grammar, parser, lexer, true); grammar = new Grammar("aaa | ccc",
     * parseScript("S ::= 'a' 'a' 'a' | 'c' 'c' 'c'")); doTry("", "aaa", true, grammar, parser, lexer, true); doTry("", "ccc", true, grammar, parser, lexer,
     * true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "a", false, grammar, parser, lexer, true); doTry("", "aa", false, grammar, parser,
     * lexer, true); doTry("", "b", false, grammar, parser, lexer, true); doTry("", "bb", false, grammar, parser, lexer, true); doTry("", "bbb", false, grammar,
     * parser, lexer, true); doTry("", "c", false, grammar, parser, lexer, true); doTry("", "cc", false, grammar, parser, lexer, true); grammar = new
     * Grammar("a | b | ccc", parseScript("S ::= 'a' | 'b' | 'c' 'c' 'c'")); doTry("", "a", true, grammar, parser, lexer, true); doTry("", "b", true, grammar,
     * parser, lexer, true); doTry("", "ccc", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "aa", false,
     * grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser, lexer, true); doTry("", "bb", false, grammar, parser, lexer, true); doTry("",
     * "bbb", false, grammar, parser, lexer, true); doTry("", "c", false, grammar, parser, lexer, true); doTry("", "cc", false, grammar, parser, lexer, true);
     * grammar = new Grammar("aa | b | ccc", parseScript("S ::= 'a' 'a' | 'b' | 'c' 'c' 'c'")); doTry("", "aa", true, grammar, parser, lexer, true); doTry("",
     * "b", true, grammar, parser, lexer, true); doTry("", "ccc", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer, true);
     * doTry("", "a", false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser, lexer, true); doTry("", "bb", false, grammar, parser,
     * lexer, true); doTry("", "bbb", false, grammar, parser, lexer, true); doTry("", "c", false, grammar, parser, lexer, true); doTry("", "cc", false, grammar,
     * parser, lexer, true); grammar = new Grammar("aaa | b | ccc", parseScript("S ::= 'a' 'a' 'a' | 'b' | 'c' 'c' 'c'")); doTry("", "aaa", true, grammar,
     * parser, lexer, true); doTry("", "b", true, grammar, parser, lexer, true); doTry("", "ccc", true, grammar, parser, lexer, true); doTry("", "", false,
     * grammar, parser, lexer, true); doTry("", "a", false, grammar, parser, lexer, true); doTry("", "aa", false, grammar, parser, lexer, true); doTry("", "bb",
     * false, grammar, parser, lexer, true); doTry("", "bbb", false, grammar, parser, lexer, true); doTry("", "c", false, grammar, parser, lexer, true);
     * doTry("", "cc", false, grammar, parser, lexer, true); grammar = new Grammar("a | bb | ccc", parseScript("S ::= 'a' | 'b' 'b' | 'c' 'c' 'c'")); doTry("",
     * "a", true, grammar, parser, lexer, true); doTry("", "bb", true, grammar, parser, lexer, true); doTry("", "ccc", true, grammar, parser, lexer, true);
     * doTry("", "", false, grammar, parser, lexer, true); doTry("", "aa", false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser, lexer,
     * true); doTry("", "b", false, grammar, parser, lexer, true); doTry("", "bbb", false, grammar, parser, lexer, true); doTry("", "c", false, grammar, parser,
     * lexer, true); doTry("", "cc", false, grammar, parser, lexer, true); grammar = new Grammar("aa | bb | ccc",
     * parseScript("S ::= 'a' 'a' | 'b' 'b' | 'c' 'c' 'c'")); doTry("", "aa", true, grammar, parser, lexer, true); doTry("", "bb", true, grammar, parser, lexer,
     * true); doTry("", "ccc", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "a", false, grammar, parser,
     * lexer, true); doTry("", "aaa", false, grammar, parser, lexer, true); doTry("", "b", false, grammar, parser, lexer, true); doTry("", "bbb", false,
     * grammar, parser, lexer, true); doTry("", "c", false, grammar, parser, lexer, true); doTry("", "cc", false, grammar, parser, lexer, true); grammar = new
     * Grammar("aaa | bb | ccc", parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' | 'c' 'c' 'c'")); doTry("", "aaa", true, grammar, parser, lexer, true); doTry("",
     * "bb", true, grammar, parser, lexer, true); doTry("", "ccc", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer, true);
     * doTry("", "a", false, grammar, parser, lexer, true); doTry("", "aa", false, grammar, parser, lexer, true); doTry("", "b", false, grammar, parser, lexer,
     * true); doTry("", "bbb", false, grammar, parser, lexer, true); doTry("", "c", false, grammar, parser, lexer, true); doTry("", "cc", false, grammar,
     * parser, lexer, true); grammar = new Grammar("a | bbb | ccc", parseScript("S ::= 'a' | 'b' 'b' 'b' | 'c' 'c' 'c'")); doTry("", "a", true, grammar, parser,
     * lexer, true); doTry("", "bbb", true, grammar, parser, lexer, true); doTry("", "ccc", true, grammar, parser, lexer, true); doTry("", "", false, grammar,
     * parser, lexer, true); doTry("", "aa", false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser, lexer, true); doTry("", "b", false,
     * grammar, parser, lexer, true); doTry("", "bb", false, grammar, parser, lexer, true); doTry("", "c", false, grammar, parser, lexer, true); doTry("", "cc",
     * false, grammar, parser, lexer, true); grammar = new Grammar("aa | bbb | ccc", parseScript("S ::= 'a' 'a' | 'b' 'b' 'b' | 'c' 'c' 'c'")); doTry("", "aa",
     * true, grammar, parser, lexer, true); doTry("", "bbb", true, grammar, parser, lexer, true); doTry("", "ccc", true, grammar, parser, lexer, true);
     * doTry("", "", false, grammar, parser, lexer, true); doTry("", "a", false, grammar, parser, lexer, true); doTry("", "aaa", false, grammar, parser, lexer,
     * true); doTry("", "b", false, grammar, parser, lexer, true); doTry("", "bb", false, grammar, parser, lexer, true); doTry("", "c", false, grammar, parser,
     * lexer, true); doTry("", "cc", false, grammar, parser, lexer, true); grammar = new Grammar("aaa | bbb | ccc",
     * parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' 'b' | 'c' 'c' 'c'")); doTry("", "aaa", true, grammar, parser, lexer, true); doTry("", "bbb", true, grammar,
     * parser, lexer, true); doTry("", "ccc", true, grammar, parser, lexer, true); doTry("", "", false, grammar, parser, lexer, true); doTry("", "a", false,
     * grammar, parser, lexer, true); doTry("", "aa", false, grammar, parser, lexer, true); doTry("", "b", false, grammar, parser, lexer, true); doTry("", "bb",
     * false, grammar, parser, lexer, true); doTry("", "c", false, grammar, parser, lexer, true); doTry("", "cc", false, grammar, parser, lexer, true); grammar
     * = new Grammar("H", parseScript("S ::= # ")); doTry("", "", true, grammar, parser, lexer, true); doTry("", "a", false, grammar, parser, lexer, true);
     * doTry("", "aa", false, grammar, parser, lexer, true);
     *
     * grammar = new Grammar("deliberatelyBad", parseScript("S ::= 'a' 'b' | 'c' 'd'")); doTry("", "acd", true, grammar, parser, lexer, true); doTry("", "cd",
     * false, grammar, parser, lexer, true);
     *
     * reportSummary();
     */
  }

  private void torture() {
    /*
     * grammar = new Grammar("torture test", parseScript("S ::= 'b' | S S | S S S"));
     *
     * String str = "b"; for (int i = 1; i < 100; i++) { doTry("", str, true, grammar, parser, lexer, false); str += "b"; }
     */}
}
