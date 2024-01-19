package uk.ac.rhul.cs.csle.art;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.Chooser;
import uk.ac.rhul.cs.csle.art.cfg.ParserBase;
import uk.ac.rhul.cs.csle.art.cfg.chart.AlgX;
import uk.ac.rhul.cs.csle.art.cfg.chart.CYK;
import uk.ac.rhul.cs.csle.art.cfg.gll.GLLBaseLine;
import uk.ac.rhul.cs.csle.art.cfg.gll.GLLHashPool;
import uk.ac.rhul.cs.csle.art.cfg.grammar.GIFTKind;
import uk.ac.rhul.cs.csle.art.cfg.grammar.Grammar;
import uk.ac.rhul.cs.csle.art.cfg.grammar.GrammarKind;
import uk.ac.rhul.cs.csle.art.cfg.grammar.GrammarNode;
import uk.ac.rhul.cs.csle.art.cfg.grammar.LKind;
import uk.ac.rhul.cs.csle.art.cfg.lexer.LexerLM;
import uk.ac.rhul.cs.csle.art.cfg.rdsob.RDSOBExplicitStack;
import uk.ac.rhul.cs.csle.art.cfg.rdsob.RDSOBFunction;
import uk.ac.rhul.cs.csle.art.cfg.rdsob.RDSOBGenerator;
import uk.ac.rhul.cs.csle.art.term.ITerms;
import uk.ac.rhul.cs.csle.art.term.ITermsLowLevelAPI;
import uk.ac.rhul.cs.csle.art.term.Rewriter;
import uk.ac.rhul.cs.csle.art.term.TermTraverser;
import uk.ac.rhul.cs.csle.art.term.TermTraverserText;
import uk.ac.rhul.cs.csle.art.util.Util;

public class ARTScriptInterpreter {
  private final ITerms iTerms = new ITermsLowLevelAPI();

  private final GLLBaseLine scriptParser = new GLLBaseLine();
  private final LexerLM scriptLexer = new LexerLM();
//@formatter:off
  private final String scriptParserTermString = "rules(directive(whitespace(cfgBuiltinTerminal(SIMPLE\\_WHITESPACE), cfgBuiltinTerminal(COMMENT\\_NEST\\_ART), cfgBuiltinTerminal(COMMENT\\_LINE\\_C))), cfgRule(cfgLHS(rules), cfgSeq(cfgSlot, cfgNonterminal(rule), cfgSlot, cfgFoldUnder(cfgNonterminal(rules)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(rule), cfgSlot)), cfgRule(cfgLHS(rule), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgRule)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(trRule)), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(choose)), cfgSlot, cfgFoldUnder(cfgNonterminal(chooseRules)), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(!)), cfgSlot, cfgFoldOver(cfgNonterminal(directive)), cfgSlot)), cfgRule(cfgLHS(cfgRule), cfgSeq(cfgSlot, cfgNonterminal(cfgLHS), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\:\\:=)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot)), cfgRule(cfgLHS(cfgLHS), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgAlts), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(|)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot)), cfgRule(cfgLHS(cfgAlt), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilonCarrier), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot)), cfgRule(cfgLHS(cfgEpsilonCarrier), cfgSeq(cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot)), cfgRule(cfgLHS(cfgAltNoAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot)), cfgRule(cfgLHS(cfgElems), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot)), cfgRule(cfgLHS(cfgExtended), cfgSeq(cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPrim)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgOptional)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgKleene)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPositive)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgDoFirst)), cfgSlot)), cfgRule(cfgLHS(cfgAnnotation), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldUnder)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldOver)), cfgSlot)), cfgRule(cfgLHS(cfgFoldUnder), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(^)), cfgSlot)), cfgRule(cfgLHS(cfgFoldOver), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(^^)), cfgSlot)), cfgRule(cfgLHS(cfgDoFirst), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\()), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\))), cfgSlot)), cfgRule(cfgLHS(cfgOptional), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\()), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\))), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(?)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(?)), cfgSlot)), cfgRule(cfgLHS(cfgPositive), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\()), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\))), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(+)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(+)), cfgSlot)), cfgRule(cfgLHS(cfgKleene), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\()), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\))), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\*)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\*)), cfgSlot)), cfgRule(cfgLHS(cfgPrim), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgNonterminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgCaseSensitiveTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgCaseInsensitiveTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCharacterTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCharacterRangeTerminal), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgBuiltinTerminal)), cfgSlot)), cfgRule(cfgLHS(cfgNonterminal), cfgSeq(cfgSlot, cfgNonterminal(ID), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(STRING\\_DOLLAR), cfgSlot)), cfgRule(cfgLHS(cfgCaseSensitiveTerminal), cfgSeq(cfgSlot, cfgNonterminal(STRING\\_PLAIN\\_SQ), cfgSlot)), cfgRule(cfgLHS(cfgCaseInsensitiveTerminal), cfgSeq(cfgSlot, cfgNonterminal(STRING\\_DQ), cfgSlot)), cfgRule(cfgLHS(cfgCharacterTerminal), cfgSeq(cfgSlot, cfgNonterminal(CHAR\\_BQ), cfgSlot)), cfgRule(cfgLHS(cfgCharacterRangeTerminal), cfgSeq(cfgSlot, cfgNonterminal(CHAR\\_BQ), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(..)), cfgSlot, cfgNonterminal(CHAR\\_BQ), cfgSlot)), cfgRule(cfgLHS(cfgBuiltinTerminal), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(&)), cfgSlot, cfgNonterminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgEpsilon), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(#)), cfgSlot)), cfgRule(cfgLHS(cfgSlotSymbol), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(.), cfgSlot)), cfgRule(cfgLHS(cfgActions), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot)), cfgRule(cfgLHS(cfgActionSeq), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot)), cfgRule(cfgLHS(cfgAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgEquation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgAssignment)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgInsert), cfgSlot)), cfgRule(cfgLHS(cfgEquation), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(=)), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(cfgAssignment), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\:=)), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(cfgInsert), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(^+)), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgAttribute), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(.)), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgSeq), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), cfgRule(cfgLHS(cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), cfgRule(cfgLHS(trRule), cfgSeq(cfgSlot, cfgNonterminal(trLabel), cfgSlot, cfgNonterminal(tr), cfgSlot)), cfgRule(cfgLHS(trLabel), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon)), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(-)), cfgSlot, cfgNonterminal(ID), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(-)), cfgSlot, cfgNonterminal(STRING\\_DQ), cfgSlot)), cfgRule(cfgLHS(tr), cfgSeq(cfgSlot, cfgNonterminal(trPremises), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(---)), cfgSlot, cfgNonterminal(trTransition), cfgSlot, cfgNonterminal(trEquations), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trPremises), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(---)), cfgSlot, cfgNonterminal(trTransition), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trPremises), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(---)), cfgSlot, cfgNonterminal(trNoTransition), cfgSlot, cfgNonterminal(trEquations), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trPremises), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(---)), cfgSlot, cfgNonterminal(trNoTransition), cfgSlot)), cfgRule(cfgLHS(trPremises), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon)), cfgSeq(cfgSlot, cfgNonterminal(trTransition), cfgSlot, cfgFoldUnder(cfgNonterminal(trPremises)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trMatch), cfgSlot, cfgFoldUnder(cfgNonterminal(trPremises)), cfgSlot)), cfgRule(cfgLHS(trTransition), cfgSeq(cfgSlot, cfgNonterminal(trConfiguration), cfgSlot, cfgNonterminal(TRRELATION), cfgSlot, cfgNonterminal(trConfiguration), cfgSlot)), cfgRule(cfgLHS(trNoTransition), cfgSeq(cfgSlot, cfgNonterminal(trConfiguration), cfgSlot)), cfgRule(cfgLHS(trConfiguration), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(<)), cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(>)), cfgSlot)), cfgRule(cfgLHS(trEntityReferences), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgNonterminal(trTerm), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgNonterminal(trNamedTerm), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgNonterminal(trNamedTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot)), cfgRule(cfgLHS(trNamedTerm), cfgSeq(cfgSlot, cfgNonterminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(=)), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(trMatch), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(|>)), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(trTerm), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_bool)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_int32)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_real64)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_string)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_char)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(ID)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\()), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\))), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\()), cfgSlot, cfgFoldUnder(cfgNonterminal(trSubterms)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\))), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(ID\\_ATTRIBUTE), cfgSlot)), cfgRule(cfgLHS(trSubterms), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgFoldUnder(cfgNonterminal(trSubterms)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trSubterms)), cfgSlot)), cfgRule(cfgLHS(trEquations), cfgSeq(cfgSlot, cfgNonterminal(cfgEquation), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgEquation), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgFoldUnder(cfgNonterminal(trEquations)), cfgSlot)), cfgRule(cfgLHS(chooseRules), cfgSeq(cfgSlot, cfgNonterminal(chooseRule), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(chooseRule), cfgSlot, cfgFoldUnder(cfgNonterminal(chooseRules)), cfgSlot)), cfgRule(cfgLHS(chooseRule), cfgSeq(cfgSlot, cfgNonterminal(chooseDiff), cfgSlot, cfgFoldOver(cfgNonterminal(chooserOp)), cfgSlot, cfgNonterminal(chooseDiff), cfgSlot)), cfgRule(cfgLHS(chooseDiff), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseUnionIntersection)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(chooseDiff), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(||)), cfgSlot, cfgNonterminal(chooseUnionIntersection), cfgSlot)), cfgRule(cfgLHS(chooseUnionIntersection), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseElement)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(chooseUnionIntersection), cfgSlot, cfgFoldOver(cfgNonterminal(chooseUnion)), cfgSlot, cfgFoldUnder(cfgNonterminal(chooseElement)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(chooseUnionIntersection), cfgSlot, cfgFoldOver(cfgNonterminal(chooseIntersection)), cfgSlot, cfgFoldUnder(cfgNonterminal(chooseElement)), cfgSlot)), cfgRule(cfgLHS(chooseUnion), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(|)), cfgSlot)), cfgRule(cfgLHS(chooseIntersection), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(/)), cfgSlot)), cfgRule(cfgLHS(chooseElement), cfgSeq(cfgSlot, cfgNonterminal(cfgNonterminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCharacterTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgBuiltinTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCaseInsensitiveTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCaseSensitiveTerminal), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgNonterminal(choosePredefinedSet)), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\()), cfgSlot, cfgNonterminal(chooseDiff), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\))), cfgSlot)), cfgRule(cfgLHS(choosePredefinedSet), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(anyCharacterTerminal), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(anyBuiltinTerminal), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(anyCaseSensitiveTerminal), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(anyCaseInsensitiveTerminal), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(anyParaterminal), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(anyLiteralTerminal), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(anyTerminal), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(anyNonterminal), cfgSlot)), cfgRule(cfgLHS(chooserOp), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseHigher)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseLower)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseLonger)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseShorter)), cfgSlot)), cfgRule(cfgLHS(chooseHigher), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(>)), cfgSlot)), cfgRule(cfgLHS(chooseLower), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(<)), cfgSlot)), cfgRule(cfgLHS(chooseLonger), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(>>)), cfgSlot)), cfgRule(cfgLHS(chooseShorter), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(<<)), cfgSlot)), cfgRule(cfgLHS(directive), cfgSeq(cfgSlot, cfgNonterminal(directiveElement), cfgSlot)), cfgRule(cfgLHS(directiveElement), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(lexer)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(#)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(lexer)), cfgSlot, cfgNonterminal(lexerElements), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(whitespace)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(#)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(whitespace)), cfgSlot, cfgFoldUnder(cfgNonterminal(whiteSpaceElements)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(whitespace)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\()), cfgSlot, cfgFoldUnder(cfgNonterminal(whiteSpaceElements)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\))), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(paraterminal)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(#)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(paraterminal)), cfgSlot, cfgFoldUnder(cfgNonterminal(paraterminalElements)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(parser)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(#)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(parser)), cfgSlot, cfgFoldUnder(cfgNonterminal(parserElements)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(start)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(#)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(start)), cfgSlot, cfgNonterminal(cfgNonterminal), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(rewriter)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(#)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(rewriter)), cfgSlot, cfgFoldUnder(cfgNonterminal(rewriterElements)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(configuration)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(#)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(configuration)), cfgSlot, cfgNonterminal(TRRELATION), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(configuration)), cfgSlot, cfgNonterminal(TRRELATION), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgNonterminal(trConfiguration), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(trace)), cfgSlot, cfgNonterminal(\\_\\_int32), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(print)), cfgSlot, cfgFoldUnder(cfgNonterminal(displayElements)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(show)), cfgSlot, cfgFoldUnder(cfgNonterminal(displayElements)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(log)), cfgSlot, cfgFoldUnder(cfgNonterminal(displayElements)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(try)), cfgSlot, cfgNonterminal(file), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(try)), cfgSlot, cfgNonterminal(trTerm), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(try)), cfgSlot, cfgNonterminal(file), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(=)), cfgSlot, cfgNonterminal(trConfiguration), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(try)), cfgSlot, cfgNonterminal(trConfiguration), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(=)), cfgSlot, cfgNonterminal(trConfiguration), cfgSlot)), cfgRule(cfgLHS(file), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_PLAIN\\_SQ), cfgSlot)), cfgRule(cfgLHS(lexerElements), cfgSeq(cfgSlot, cfgNonterminal(lexerElement), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(lexerElement), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgNonterminal(lexerElements), cfgSlot)), cfgRule(cfgLHS(lexerElement), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(LongestMatch)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(twe)), cfgSlot)), cfgRule(cfgLHS(whiteSpaceElements), cfgSeq(cfgSlot, cfgNonterminal(whiteSpaceElement), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(whiteSpaceElement), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgFoldUnder(cfgNonterminal(whiteSpaceElements)), cfgSlot)), cfgRule(cfgLHS(whiteSpaceElement), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgNonterminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgBuiltinTerminal)), cfgSlot)), cfgRule(cfgLHS(paraterminalElements), cfgSeq(cfgSlot, cfgNonterminal(paraterminalElement), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(paraterminalElement), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgFoldUnder(cfgNonterminal(paraterminalElements)), cfgSlot)), cfgRule(cfgLHS(paraterminalElement), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgNonterminal)), cfgSlot)), cfgRule(cfgLHS(parserElements), cfgSeq(cfgSlot, cfgNonterminal(parserElement), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(parserElement), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgFoldUnder(cfgNonterminal(parserElements)), cfgSlot)), cfgRule(cfgLHS(parserElement), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(gllBaseLine)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(rdsobFunction)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(algX)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(cyk)), cfgSlot)), cfgRule(cfgLHS(rewriterElements), cfgSeq(cfgSlot, cfgNonterminal(rewriterElement), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(rewriterElement), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgFoldUnder(cfgNonterminal(rewriterElements)), cfgSlot)), cfgRule(cfgLHS(rewriterElement), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(eSOS)), cfgSlot)), cfgRule(cfgLHS(displayElements), cfgSeq(cfgSlot, cfgNonterminal(displayElement), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(displayElement), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgFoldUnder(cfgNonterminal(displayElements)), cfgSlot)), cfgRule(cfgLHS(displayElement), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(scriptDerivationTerm)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(input)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(tokens)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(sppf)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(derivationTerm)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(instanceTerm)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(outcome)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(statistics)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_string)), cfgSlot)), cfgRule(cfgLHS(\\_\\_bool), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(true), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(false), cfgSlot)), cfgRule(cfgLHS(\\_\\_char), cfgSeq(cfgSlot, cfgBuiltinTerminal(CHAR\\_BQ), cfgSlot)), cfgRule(cfgLHS(\\_\\_int32), cfgSeq(cfgSlot, cfgBuiltinTerminal(SIGNED\\_INTEGER), cfgSlot)), cfgRule(cfgLHS(\\_\\_real64), cfgSeq(cfgSlot, cfgBuiltinTerminal(SIGNED\\_REAL), cfgSlot)), cfgRule(cfgLHS(\\_\\_string), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_DQ), cfgSlot), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_SQ), cfgSlot)), cfgRule(cfgLHS(ID), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(ID)), cfgSlot)), cfgRule(cfgLHS(ID\\_ATTRIBUTE), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(.)), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(STRING\\_DQ), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_DQ)), cfgSlot)), cfgRule(cfgLHS(STRING\\_DOLLAR), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_DOLLAR)), cfgSlot)), cfgRule(cfgLHS(STRING\\_PLAIN\\_SQ), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_PLAIN\\_SQ)), cfgSlot)), cfgRule(cfgLHS(CHAR\\_BQ), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(CHAR\\_BQ)), cfgSlot)), cfgRule(cfgLHS(STRING\\_BRACE\\_NEST), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_BRACE\\_NEST)), cfgSlot)), cfgRule(cfgLHS(STRING\\_BRACKET\\_NEST), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_BRACKET\\_NEST)), cfgSlot)), cfgRule(cfgLHS(TRRELATION), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(->), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(->\\*), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(=>), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(=>\\*), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(~>), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(~>\\*), cfgSlot)))";
//  private final String scriptParserTermString = "rules(directive(whitespace(cfgBuiltinTerminal(SIMPLE\\_WHITESPACE), cfgBuiltinTerminal(COMMENT\\_NEST\\_ART), cfgBuiltinTerminal(COMMENT\\_LINE\\_C))), cfgRule(cfgLHS(rules), cfgSeq(cfgSlot, cfgNonterminal(rule), cfgSlot, cfgFoldUnder(cfgNonterminal(rules)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(rule), cfgSlot)), cfgRule(cfgLHS(rule), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgRule)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(trRule)), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(choose)), cfgSlot, cfgFoldUnder(cfgNonterminal(chooseRules)), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(!)), cfgSlot, cfgFoldOver(cfgNonterminal(directive)), cfgSlot)), cfgRule(cfgLHS(cfgRule), cfgSeq(cfgSlot, cfgNonterminal(cfgLHS), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\:\\:=)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot)), cfgRule(cfgLHS(cfgLHS), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgAlts), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(|)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot)), cfgRule(cfgLHS(cfgAlt), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilonCarrier), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot)), cfgRule(cfgLHS(cfgEpsilonCarrier), cfgSeq(cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot)), cfgRule(cfgLHS(cfgAltNoAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot)), cfgRule(cfgLHS(cfgElems), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot)), cfgRule(cfgLHS(cfgExtended), cfgSeq(cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPrim)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgOptional)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgKleene)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPositive)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgDoFirst)), cfgSlot)), cfgRule(cfgLHS(cfgAnnotation), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldUnder)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldOver)), cfgSlot)), cfgRule(cfgLHS(cfgFoldUnder), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(^)), cfgSlot)), cfgRule(cfgLHS(cfgFoldOver), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(^^)), cfgSlot)), cfgRule(cfgLHS(cfgDoFirst), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\()), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\))), cfgSlot)), cfgRule(cfgLHS(cfgOptional), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\()), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\))), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(?)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(?)), cfgSlot)), cfgRule(cfgLHS(cfgPositive), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\()), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\))), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(+)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(+)), cfgSlot)), cfgRule(cfgLHS(cfgKleene), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\()), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\))), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\*)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\*)), cfgSlot)), cfgRule(cfgLHS(cfgPrim), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgNonterminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgCaseSensitiveTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgCaseInsensitiveTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCharacterTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCharacterRangeTerminal), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgBuiltinTerminal)), cfgSlot)), cfgRule(cfgLHS(cfgNonterminal), cfgSeq(cfgSlot, cfgNonterminal(ID), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(STRING\\_DOLLAR), cfgSlot)), cfgRule(cfgLHS(cfgCaseSensitiveTerminal), cfgSeq(cfgSlot, cfgNonterminal(STRING\\_PLAIN\\_SQ), cfgSlot)), cfgRule(cfgLHS(cfgCaseInsensitiveTerminal), cfgSeq(cfgSlot, cfgNonterminal(STRING\\_DQ), cfgSlot)), cfgRule(cfgLHS(cfgCharacterTerminal), cfgSeq(cfgSlot, cfgNonterminal(CHAR\\_BQ), cfgSlot)), cfgRule(cfgLHS(cfgCharacterRangeTerminal), cfgSeq(cfgSlot, cfgNonterminal(CHAR\\_BQ), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(..)), cfgSlot, cfgNonterminal(CHAR\\_BQ), cfgSlot)), cfgRule(cfgLHS(cfgBuiltinTerminal), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(&)), cfgSlot, cfgNonterminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgEpsilon), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(#)), cfgSlot)), cfgRule(cfgLHS(cfgSlotSymbol), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(.), cfgSlot)), cfgRule(cfgLHS(cfgActions), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot)), cfgRule(cfgLHS(cfgActionSeq), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot)), cfgRule(cfgLHS(cfgAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgEquation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgAssignment)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgInsert), cfgSlot)), cfgRule(cfgLHS(cfgEquation), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(=)), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(cfgAssignment), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\:=)), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(cfgInsert), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(^+)), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgAttribute), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(.)), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgSeq), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), cfgRule(cfgLHS(cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), cfgRule(cfgLHS(trRule), cfgSeq(cfgSlot, cfgNonterminal(trLabel), cfgSlot, cfgNonterminal(tr), cfgSlot)), cfgRule(cfgLHS(trLabel), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon)), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(-)), cfgSlot, cfgNonterminal(ID), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(-)), cfgSlot, cfgNonterminal(STRING\\_DQ), cfgSlot)), cfgRule(cfgLHS(tr), cfgSeq(cfgSlot, cfgNonterminal(trPremises), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(---)), cfgSlot, cfgNonterminal(trTransition), cfgSlot, cfgNonterminal(trEquations), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trPremises), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(---)), cfgSlot, cfgNonterminal(trTransition), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trPremises), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(---)), cfgSlot, cfgNonterminal(trNoTransition), cfgSlot, cfgNonterminal(trEquations), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trPremises), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(---)), cfgSlot, cfgNonterminal(trNoTransition), cfgSlot)), cfgRule(cfgLHS(trPremises), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon)), cfgSeq(cfgSlot, cfgNonterminal(trTransition), cfgSlot, cfgFoldUnder(cfgNonterminal(trPremises)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trMatch), cfgSlot, cfgFoldUnder(cfgNonterminal(trPremises)), cfgSlot)), cfgRule(cfgLHS(trTransition), cfgSeq(cfgSlot, cfgNonterminal(trConfiguration), cfgSlot, cfgNonterminal(TRRELATION), cfgSlot, cfgNonterminal(trConfiguration), cfgSlot)), cfgRule(cfgLHS(trNoTransition), cfgSeq(cfgSlot, cfgNonterminal(trConfiguration), cfgSlot)), cfgRule(cfgLHS(trConfiguration), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(<)), cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(>)), cfgSlot)), cfgRule(cfgLHS(trEntityReferences), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgNonterminal(trTerm), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgNonterminal(trNamedTerm), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgNonterminal(trNamedTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot)), cfgRule(cfgLHS(trNamedTerm), cfgSeq(cfgSlot, cfgNonterminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(=)), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(trMatch), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(|>)), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(trTerm), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_bool)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_int32)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_real64)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_string)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_char)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(ID)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\()), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\))), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\()), cfgSlot, cfgFoldUnder(cfgNonterminal(trSubterms)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\))), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(ID\\_ATTRIBUTE), cfgSlot)), cfgRule(cfgLHS(trSubterms), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgFoldUnder(cfgNonterminal(trSubterms)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trSubterms)), cfgSlot)), cfgRule(cfgLHS(trEquations), cfgSeq(cfgSlot, cfgNonterminal(cfgEquation), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgEquation), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgFoldUnder(cfgNonterminal(trEquations)), cfgSlot)), cfgRule(cfgLHS(chooseRules), cfgSeq(cfgSlot, cfgNonterminal(chooseRule), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(chooseRule), cfgSlot, cfgFoldUnder(cfgNonterminal(chooseRules)), cfgSlot)), cfgRule(cfgLHS(chooseRule), cfgSeq(cfgSlot, cfgNonterminal(chooseDiff), cfgSlot, cfgFoldOver(cfgNonterminal(chooserOp)), cfgSlot, cfgNonterminal(chooseDiff), cfgSlot)), cfgRule(cfgLHS(chooseDiff), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseUnionIntersection)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(chooseDiff), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(||)), cfgSlot, cfgNonterminal(chooseUnionIntersection), cfgSlot)), cfgRule(cfgLHS(chooseUnionIntersection), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseElement)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(chooseUnionIntersection), cfgSlot, cfgFoldOver(cfgNonterminal(chooseUnion)), cfgSlot, cfgFoldUnder(cfgNonterminal(chooseElement)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(chooseUnionIntersection), cfgSlot, cfgFoldOver(cfgNonterminal(chooseIntersection)), cfgSlot, cfgFoldUnder(cfgNonterminal(chooseElement)), cfgSlot)), cfgRule(cfgLHS(chooseUnion), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(|)), cfgSlot)), cfgRule(cfgLHS(chooseIntersection), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(/)), cfgSlot)), cfgRule(cfgLHS(chooseElement), cfgSeq(cfgSlot, cfgNonterminal(cfgNonterminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCharacterTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgBuiltinTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCaseInsensitiveTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCaseSensitiveTerminal), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgNonterminal(choosePredefinedSet)), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\()), cfgSlot, cfgNonterminal(chooseDiff), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\))), cfgSlot)), cfgRule(cfgLHS(choosePredefinedSet), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(anyCharacterTerminal), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(anyBuiltinTerminal), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(anyCaseSensitiveTerminal), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(anyCaseInsensitiveTerminal), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(anyParaterminal), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(anyLiteralTerminal), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(anyTerminal), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(anyNonterminal), cfgSlot)), cfgRule(cfgLHS(chooserOp), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseHigher)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseLower)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseLonger)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseShorter)), cfgSlot)), cfgRule(cfgLHS(chooseHigher), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(>)), cfgSlot)), cfgRule(cfgLHS(chooseLower), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(<)), cfgSlot)), cfgRule(cfgLHS(chooseLonger), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(>>)), cfgSlot)), cfgRule(cfgLHS(chooseShorter), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(<<)), cfgSlot)), cfgRule(cfgLHS(directive), cfgSeq(cfgSlot, cfgNonterminal(directiveElement), cfgSlot)), cfgRule(cfgLHS(directiveElement), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(lexer)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(#)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(lexer)), cfgSlot, cfgNonterminal(lexerElements), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(whitespace)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(#)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(whitespace)), cfgSlot, cfgFoldUnder(cfgNonterminal(whiteSpaceElements)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(whitespace)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\()), cfgSlot, cfgFoldUnder(cfgNonterminal(whiteSpaceElements)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\))), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(paraterminal)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(#)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(paraterminal)), cfgSlot, cfgFoldUnder(cfgNonterminal(paraterminalElements)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(parser)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(#)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(parser)), cfgSlot, cfgFoldUnder(cfgNonterminal(parserElements)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(start)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(#)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(start)), cfgSlot, cfgNonterminal(cfgNonterminal), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(rewriter)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(#)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(rewriter)), cfgSlot, cfgFoldUnder(cfgNonterminal(rewriterElements)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(configuration)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(#)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(configuration)), cfgSlot, cfgNonterminal(TRRELATION), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(configuration)), cfgSlot, cfgNonterminal(TRRELATION), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgNonterminal(trConfiguration), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(trace)), cfgSlot, cfgBuiltinTerminal(SIGNED\\_INTEGER), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(print)), cfgSlot, cfgFoldUnder(cfgNonterminal(displayElements)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(show)), cfgSlot, cfgFoldUnder(cfgNonterminal(displayElements)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(log)), cfgSlot, cfgFoldUnder(cfgNonterminal(displayElements)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(try)), cfgSlot, cfgNonterminal(file), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(try)), cfgSlot, cfgNonterminal(trTerm), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(try)), cfgSlot, cfgNonterminal(file), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(=)), cfgSlot, cfgNonterminal(trConfiguration), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(try)), cfgSlot, cfgNonterminal(trConfiguration), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(=)), cfgSlot, cfgNonterminal(trConfiguration), cfgSlot)), cfgRule(cfgLHS(file), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_PLAIN\\_SQ), cfgSlot)), cfgRule(cfgLHS(lexerElements), cfgSeq(cfgSlot, cfgNonterminal(lexerElement), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(lexerElement), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgNonterminal(lexerElements), cfgSlot)), cfgRule(cfgLHS(lexerElement), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(LongestMatch)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(twe)), cfgSlot)), cfgRule(cfgLHS(whiteSpaceElements), cfgSeq(cfgSlot, cfgNonterminal(whiteSpaceElement), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(whiteSpaceElement), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgFoldUnder(cfgNonterminal(whiteSpaceElements)), cfgSlot)), cfgRule(cfgLHS(whiteSpaceElement), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgNonterminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgBuiltinTerminal)), cfgSlot)), cfgRule(cfgLHS(paraterminalElements), cfgSeq(cfgSlot, cfgNonterminal(paraterminalElement), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(paraterminalElement), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgFoldUnder(cfgNonterminal(paraterminalElements)), cfgSlot)), cfgRule(cfgLHS(paraterminalElement), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgNonterminal)), cfgSlot)), cfgRule(cfgLHS(parserElements), cfgSeq(cfgSlot, cfgNonterminal(parserElement), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(parserElement), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgFoldUnder(cfgNonterminal(parserElements)), cfgSlot)), cfgRule(cfgLHS(parserElement), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(gllBaseLine)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(rdsobFunction)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(algX)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(cyk)), cfgSlot)), cfgRule(cfgLHS(rewriterElements), cfgSeq(cfgSlot, cfgNonterminal(rewriterElement), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(rewriterElement), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgFoldUnder(cfgNonterminal(rewriterElements)), cfgSlot)), cfgRule(cfgLHS(rewriterElement), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(eSOS)), cfgSlot)), cfgRule(cfgLHS(displayElements), cfgSeq(cfgSlot, cfgNonterminal(displayElement), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(displayElement), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(\\,)), cfgSlot, cfgFoldUnder(cfgNonterminal(displayElements)), cfgSlot)), cfgRule(cfgLHS(displayElement), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(scriptDerivationTerm)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(input)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(tokens)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(sppf)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(derivationTerm)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(instanceTerm)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(outcome)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgCaseSensitiveTerminal(statistics)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_string)), cfgSlot)), cfgRule(cfgLHS(\\_\\_bool), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(true), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(false), cfgSlot)), cfgRule(cfgLHS(\\_\\_char), cfgSeq(cfgSlot, cfgBuiltinTerminal(CHAR\\_BQ), cfgSlot)), cfgRule(cfgLHS(\\_\\_int32), cfgSeq(cfgSlot, cfgBuiltinTerminal(SIGNED\\_INTEGER), cfgSlot)), cfgRule(cfgLHS(\\_\\_real64), cfgSeq(cfgSlot, cfgBuiltinTerminal(SIGNED\\_REAL), cfgSlot)), cfgRule(cfgLHS(\\_\\_string), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_DQ), cfgSlot), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_SQ), cfgSlot)), cfgRule(cfgLHS(ID), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(ID)), cfgSlot)), cfgRule(cfgLHS(ID\\_ATTRIBUTE), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal(.)), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(STRING\\_DQ), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_DQ)), cfgSlot)), cfgRule(cfgLHS(STRING\\_DOLLAR), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_DOLLAR)), cfgSlot)), cfgRule(cfgLHS(STRING\\_PLAIN\\_SQ), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_PLAIN\\_SQ)), cfgSlot)), cfgRule(cfgLHS(CHAR\\_BQ), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(CHAR\\_BQ)), cfgSlot)), cfgRule(cfgLHS(STRING\\_BRACE\\_NEST), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_BRACE\\_NEST)), cfgSlot)), cfgRule(cfgLHS(STRING\\_BRACKET\\_NEST), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_BRACKET\\_NEST)), cfgSlot)), cfgRule(cfgLHS(TRRELATION), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(->), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(->\\*), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(=>), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(=>\\*), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(~>), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal(~>\\*), cfgSlot)))";
  //@formatter:on
  private final int scriptParserTerm;

  private int scriptDerivationTerm;
  private final TermTraverser scriptTraverser;
  public TermTraverserText tt;
  public final TermTraverserText latexTraverser;

  private ParserBase currentParser = new GLLBaseLine(); // default current parser is GLL base line - change to MGLL when available
  private final LexerLM currentLexer = new LexerLM(); // default current lexer is longest match - change to TWE set lexer when available
  private Grammar currentGrammar; // scriptTraverser builds grammar rules into this grammar
  private int currentDerivationTerm = 0;
  private int currentConfiguration = 0;
  private final Chooser currentChooser;
  private final Rewriter currentRewriter = new Rewriter(iTerms, tt);

  private int goodCount = 0;
  private int badCount = 0;
  private int inadmissableCount = 0;

  public int verbosityLevel = 5;
  public int statisticsLevel = 5;
  public int defaultDepthLimit = 5;

  public final Set<Integer> chooseRules = new LinkedHashSet<>();
  public final Map<Integer, Map<Integer, List<Integer>>> trRules = new LinkedHashMap<>();
  public int defaultStartRelation = 0; // This is set by the first term rewrite rule encountered

  public ARTScriptInterpreter() {
    tt = loadTextTraverser();
    // 2a. Debug - load text traverser default action to print message if we encounter an unknown constructor
    // tt.addOp(-1, (Integer t) -> tt.append("??" + iTerms.toString(t) + "?? "), null, null);
    tt.addAction(-1, (Integer t) -> { // Load default actions

      // Preorder
      tt.appendAlias(iTerms.getTermSymbolIndex(t));
      if (iTerms.getTermArity(t) > 0) tt.append("(");
    },

        // Inorder
        (Integer t) -> {
          tt.append(", ");
        },

        // Postorder
        (Integer t) -> {
          if (iTerms.getTermArity(t) > 0) tt.append(")");
        });

    // 2b. Debug - print keys from text traverser tables
    // System.out.println("text traverser: " + pp.tt);

    latexTraverser = loadLaTeXTraverser();

    scriptTraverser = new TermTraverser(iTerms);
    initialiseScriptTraverser();
    scriptParserTerm = iTerms.findTerm(scriptParserTermString);

    // Bootstrap in action: build the script parser's grammar by traversing the scriptParserTerm and normalising, then making the result the script parser
    currentGrammar = new Grammar("Script grammar", iTerms);
    scriptTraverser.traverse(scriptParserTerm); // Construct the script parser grammar by walking the script parser term from the last bootstrap
    currentGrammar.normalise();
    currentChooser = new Chooser(iTerms);
    currentChooser.normalise(currentGrammar, chooseRules);
    scriptParser.grammar = currentGrammar;
  }

  private void initialiseScriptTraverser() { // Initialise scriptTraverser

    scriptTraverser.addActionBreak("directive", (Integer t) -> directiveAction(t), null, null);

    scriptTraverser.addActionBreak("cfgLHS", (Integer t) -> currentGrammar.lhsAction(childSymbolString(t)), null, null);
    scriptTraverser.addAction("cfgSeq", (Integer t) -> currentGrammar.altAction(), null, (Integer t) -> currentGrammar.endAction(""));

    scriptTraverser.addAction("cfgEpsilon", (Integer t) -> currentGrammar.updateWorkingNode(GrammarKind.EPS, "#"), null, null);
    scriptTraverser.addActionBreak("cfgNonterminal", (Integer t) -> currentGrammar.updateWorkingNode(GrammarKind.N, childSymbolString(t)), null, null);
    scriptTraverser.addActionBreak("cfgCaseSensitiveTerminal", (Integer t) -> currentGrammar.updateWorkingNode(GrammarKind.T, childSymbolString(t)), null,
        null);
    scriptTraverser.addActionBreak("cfgBuiltinTerminal", (Integer t) -> currentGrammar.updateWorkingNode(GrammarKind.B, childSymbolString(t)), null, null);

    scriptTraverser.addAction("cfgDoFirst", (Integer t) -> currentGrammar.updateWorkingNode(GrammarKind.DO, ""), null, null);
    scriptTraverser.addAction("cfgOptional", (Integer t) -> currentGrammar.updateWorkingNode(GrammarKind.OPT, ""), null, null);
    scriptTraverser.addAction("cfgKleene", (Integer t) -> currentGrammar.updateWorkingNode(GrammarKind.KLN, ""), null, null);
    scriptTraverser.addAction("cfgPositive", (Integer t) -> currentGrammar.updateWorkingNode(GrammarKind.POS, ""), null, null);

    scriptTraverser.addAction("cfgFoldNone", (Integer t) -> currentGrammar.workingFold = GIFTKind.NONE, null, null);
    scriptTraverser.addAction("cfgFoldUnder", (Integer t) -> currentGrammar.workingFold = GIFTKind.UNDER, null, null);
    scriptTraverser.addAction("cfgFoldOver", (Integer t) -> currentGrammar.workingFold = GIFTKind.OVER, null, null);
    scriptTraverser.addAction("cfgFoldTear", (Integer t) -> currentGrammar.workingFold = GIFTKind.TEAR, null, null);

    scriptTraverser.addAction("cfgSlot", (Integer t) -> currentGrammar.workingAction = t, null, null);
    scriptTraverser.addAction("chooseRule", (Integer t) -> buildChooseRule(t), null, null);

    scriptTraverser.addAction("trRule", (Integer t) -> buildTRRule(t), null, null);
  }

  private void buildChooseRule(int term) {
    // System.out.println("Building choose rule " + iTerms.toString(term));
    chooseRules.add(term);
  }

  private void buildTRRule(int term) {
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

  private void directiveAction(int term) {
    // System.out.println("Processing " + iTerms.toString(term));
    switch (iTerms.getTermSymbolString(iTerms.getSubterm(term, 0))) {
    case "trace":
      Util.traceLevel = (int) iTerms.valueFromTerm(iTerms.getSubterm(term, 0, 0)).javaValue();
      System.out.println("Trace level set to " + Util.traceLevel);
      break;
    case "configuration":
      currentConfiguration = iTerms.getSubterm(term, 0);
      break;
    case "whitespace":
      currentGrammar.whitespaces.clear();
      for (int i = 0; i < iTerms.getTermArity(iTerms.getSubterm(term, 0)); i++)
        if (iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, i)).equals("cfgBuiltinTerminal"))
          switch (iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, i, 0))) {
          case "SIMPLE_WHITESPACE":
          currentGrammar.whitespaces.add(LKind.SIMPLE_WHITESPACE);
          break;
          case "COMMENT_NEST_ART":
          currentGrammar.whitespaces.add(LKind.COMMENT_NEST_ART);
          break;
          case "COMMENT_LINE_C":
          currentGrammar.whitespaces.add(LKind.COMMENT_LINE_C);
          break;
          case "COMMENT_BLOCK_C":
          currentGrammar.whitespaces.add(LKind.COMMENT_BLOCK_C);
          break;
          default:
          Util.fatal("Unexpected !whitespace element " + iTerms.toString(iTerms.getSubterm(term, 0, i, 0)));
          }
      break;

    case "clearRules":
      currentGrammar = new Grammar("Current grammar", iTerms);
      break;
    // case "adl":
    // if (currentDerivationTerm == 0) break;
    // ADL adl = new ADL(iTerms);
    // __mapChain env = new __mapChain();
    // Value result = iTerms.valueBottom;
    // try {
    // result = adl.interpret(currentDerivationTerm, env);
    // } catch (ValueException e) {
    // System.out.println("Value exception: " + e.getMessage());
    // }
    //
    // System.out.println("ADL interpreter returns " + result + " with environment " + env);
    // break;
    case "torture":
      // grammar = new Grammar("torture test", parseScript("S ::= 'b' | S S | S S S"));
      // String str = "b";
      // for (int i = 1; i < 100; i++) {
      // doTry("", str, true, grammar, parser, lexer, false);
      // str += "b";
      // }

      break;
    case "lexer":
      break;
    case "parser":
      for (int i = 0; i < iTerms.getTermArity(iTerms.getSubterm(term, 0)); i++)
        switch (iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, i))) {
        case "algX":
          currentParser = new AlgX(iTerms);
          break;
        case "cyk":
          currentParser = new CYK(iTerms);
          break;
        case "gllhp":
          currentParser = new GLLHashPool();
          break;
        case "gllBaseLine":
          currentParser = new GLLBaseLine();
          break;
        case "rdsobgenerator":
          new RDSOBGenerator(currentGrammar, "OSBRDG");
          return;
        case "rdsobFunction":
          currentParser = new RDSOBFunction();
          break;
        case "rdsobExplicitStack":
          currentParser = new RDSOBExplicitStack();
          break;
        default:
          Util.fatal("Unexpected !parser argument " + iTerms.toString(iTerms.getSubterm(term, 0, i)));
        }
      break;

    case "show":
      for (int i = 0; i < iTerms.getTermArity(iTerms.getSubterm(term, 0)); i++)
        switch (iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, i))) {
        case "grammar":
          currentGrammar.normalise();
          currentGrammar.show("grammar.dot");
          break;
        }
      GrammarNode.caseSensitiveTerminalStrop = ""; // Switch off stropping to reduce clutter in display
      currentParser.show();
      GrammarNode.caseSensitiveTerminalStrop = "\'"; // Restore stropping for text output
      break;
    case "print":
      for (int i = 0; i < iTerms.getTermArity(iTerms.getSubterm(term, 0)); i++)
        switch (iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, i))) {
        case "grammar":
          currentGrammar.normalise();
          System.out.println("current Grammar: " + currentGrammar);
          break;

        case "derivationTerm":
          // Switch comments if you wanted one line or indented derivations
          System.out.println("Current derivation term: [" + currentDerivationTerm + "]\n" + iTerms.toString(currentDerivationTerm, false, -1, null));
          // System.out.println("current derivation term: [" + currentDerivationTerm + "]\n" + iTerms.toString(currentDerivationTerm, true, -1, null));
          if (scriptParserTerm == currentDerivationTerm) System.out.println("Bootstrap achieved: script parser term and current derivation term identical");
          break;

        case "scriptDerivationTerm":
          // Switch comments if you wanted one line or indented derivations
          // System.out.println("script derivation term: [" + scriptDerivationTerm + "]\n" + iTerms.toString(scriptDerivationTerm, false, -1, null));
          System.out.println("script derivation term: [" + scriptDerivationTerm + "]\n" + iTerms.toString(scriptDerivationTerm, true, -1, null));
          break;

        case "rewriteRules":
          System.out.println(currentRewriter.trRulesToString(trRules));
          break;

        case "__string":
          System.out.println(iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, 0, 0)));
          break;
        default:
          Util.fatal("Unexpected !print argument " + iTerms.toString(iTerms.getSubterm(term, 0, i)));
        }
      break;
    case "try":
      switch (iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, 0))) {
      case "__string":
        doParse("", iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, 0, 0)), true, true);
        break;
      case "file":
        try {
          String filename = iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, 0, 0, 0));
          // System.out.println("Attempting to open file " + filename);
          doParse(filename, Files.readString(Paths.get(filename)), true, true);
        } catch (IOException e) {
          Util.fatal("Unable to open try file; skipping " + iTerms.toString(term));
        }
        break;
      default:
        currentDerivationTerm = iTerms.getSubterm(term, 0, 0); // Go straight to the rewrite stage
      }

      int resultTerm = currentRewriter.rewrite(currentDerivationTerm);
      if (iTerms.getTermArity(iTerms.getSubterm(term, 0, 0)) == 2) if (resultTerm == iTerms.getSubterm(term, 0, 0, 1))
        System.out.println("Good");
      else
        System.out.println("Bad");
      break;
    case "nop": // No operation
      break;
    default:
      Util.fatal("Unknown directive !" + iTerms.toString(iTerms.getSubterm(term, 0)));
    }
  }

  private String childSymbolString(int t) {
    return iTerms.getTermSymbolString(iTerms.getSubterm(t, 0));
  }

  private void doParse(String inputStringName, String inputString, boolean outcome, boolean suppressOutput) {
    // System.out.println("Try parse on input \"" + inputString + "\"");
    currentGrammar.normalise();
    if (!currentGrammar.empty) {
      currentParser.traceLevel = Util.traceLevel;
      currentParser.inputStringName = inputStringName;
      currentParser.inputString = inputString;
      currentParser.grammar = currentGrammar;
      currentDerivationTerm = 0;
      currentParser.accepted = false;
      currentLexer.lex(inputString, currentGrammar.lexicalKindsArray(), currentGrammar.lexicalStringsArray(), currentGrammar.whitespacesArray());
      // currentLexer.report();
      currentParser.input = currentLexer.tokens;
      currentParser.positions = currentLexer.positions;
      if (currentParser.input != null) currentParser.parse();
      Util.trace(3, 0, currentParser.accepted ? "Accept" : "Reject");
      // System.out.println("Parse complete: accept " + currentParser.accepted);
      if (currentParser.inadmissable)
        inadmissableCount++;
      else if (currentParser.accepted == outcome)
        goodCount++;
      else
        badCount++;
      if (currentParser.accepted) {
        currentParser.chooseLongestMatch();
        currentParser.selectFirst();
        currentDerivationTerm = currentParser.derivationAsTerm();
      } else
        System.out.println("Syntax error in try");
    }
  }

  public void interpret(String scriptString) {
    currentGrammar = new Grammar("Current grammar", iTerms); // Set up a fresh grammar for the
    // Lex the script string
    scriptParser.inputString = scriptString;
    scriptParser.accepted = false;
    scriptLexer.lex(scriptParser.inputString, scriptParser.grammar.lexicalKindsArray(), scriptParser.grammar.lexicalStringsArray(),
        scriptParser.grammar.whitespacesArray());
    // scriptLexer.report();
    if (scriptLexer.tokens == null) Util.fatal("Script lexical error");
    scriptParser.input = scriptLexer.tokens;
    scriptParser.positions = scriptLexer.positions;
    scriptParser.parse();
    if (!scriptParser.accepted) Util.fatal("Script syntax error");
    scriptParser.selectFirst();
    scriptDerivationTerm = scriptParser.derivationAsTerm();
    // System.out.println("Script term:\n" + iTerms.toString(scriptTerm, true, -1, null));
    scriptTraverser.traverse(scriptDerivationTerm);
  }

  public Grammar getGrammar() {
    return currentGrammar;
  }

  private TermTraverserText loadTextTraverser() {
    TermTraverserText ret = new TermTraverserText(iTerms);
    // -1: uncomment these to suppress types have interpreted type renditions
    // ret.addEmptyAction("__bool", "__char", "__int32", "__real64", "__string");
    ret.addAction("__map", "{", ", ", "}");

    // 0. Directive and top level pretty print controls
    ret.addEmptyAction("text", "cfgElementDeclarations", "cfgElementDeclaration", "latexDeclarations");
    ret.addActionBreak("directive", (Integer t) -> processDirective(t), null, null);
    ret.addAction("latexDeclaration", null, " = ", null);
    ret.addActionBreak("idART", (Integer t) -> ret.appendAlias(ret.childSymbolIndex(t, 0)), null, null);

    // 1. Context Free Grammar pretty print controls
    ret.addEmptyAction("cfgSlot");

    ret.addAction("cfgCat", null, " ", null);
    ret.addAction("cfgRule", null, "::=\n ", "\n");
    ret.addAction("cfgRHS", null, "\n|", "\n");
    ret.addAction("cfgAlt", null, " | ", null);
    ret.addActionBreak("cfgNonterminal", (Integer t) -> ret.appendAlias(iTerms.getTermSymbolIndex(iTerms.getSubterm(t, 0))), null, null);
    ret.addActionBreak("cfgCaseInsensitiveTerminal", (Integer t) -> ret.appendAlias(iTerms.getTermSymbolIndex(iTerms.getSubterm(t, 0))), null, null);
    ret.addActionBreak("cfgCaseSensitiveTerminal", (Integer t) -> ret.appendAlias(iTerms.getTermSymbolIndex(iTerms.getSubterm(t, 0))), null, null);
    ret.addActionBreak("cfgCharacterTerminal", (Integer t) -> ret.appendAlias(iTerms.getTermSymbolIndex(iTerms.getSubterm(t, 0))), null, null);
    ret.addActionBreak("cfgCharacterRangeTerminal",
        (Integer t) -> ret.append(iTerms.getTermSymbolIndex(iTerms.getSubterm(t, 0)) + ".." + iTerms.getTermSymbolString(iTerms.getSubterm(t, 1))), null, null);
    ret.addAction("cfgOptional", null, null, "?");
    ret.addAction("cfgKleeneClosure", null, null, "*");
    ret.addAction("cfgPositiveClosure", null, null, "+");
    ret.addAction("cfgDoFirst", "(", null, ")");
    ret.addAction("cfgEpsilon", "#", null, null);

    // 2. Chooser pretty print controls
    ret.addEmptyAction("chooseElement");
    ret.addAction("chooseRule", null, null, "\n");
    ret.addAction("chooseHigher", " > ", null, null);
    ret.addAction("chooseLower", " < ", null, null);
    ret.addAction("chooseLonger", " >> ", null, null);
    ret.addAction("chooseShorter", " << ", null, null);
    ret.addAction("chooseDiff", "(", " \\ ", ")");
    ret.addAction("chooseUnion", "(", " | ", ")");
    ret.addAction("chooseIntersection", "(", " / ", ")");
    ret.addActionBreak("choosePredefinedSet", (Integer t) -> ret.append(ret.childSymbolString(t, 0)), null, null);

    // 3. Term rewrite pretty print controls
    ret.addEmptyAction("trRule");
    ret.addAction("tr", null, " --- ", null);
    ret.addAction("trPremises", null, "    ", null);
    ret.addActionBreak("trLabel", (Integer t) -> ret.append(iTerms.getTermArity(t) > 0 ? ("-" + ret.childSymbolString(t, 0) + " ") : " "), null, null);
    ret.addAction("trMatch", null, (Integer t) -> ret.append(" |> "), null);
    ret.addEmptyAction("trTransition");
    ret.addActionBreak("TRRELATION", (Integer t) -> ret.append(" " + ret.childSymbolString(t, 0) + " "), null, null);
    // tt.addAction("trConfiguration", "<", ", ", ">");
    ret.addAction("trConfiguration", null, ", ", null);
    ret.addActionBreak("trTerm", (Integer t) -> ret.append(iTerms.toString(iTerms.getSubterm(t, 0))), null, null);
    ret.addAction("trEntityReferences", ", ", ", ", null);
    ret.addActionBreak("trNamedTerm", (Integer t) -> ret.append(iTerms.toString(iTerms.getSubterm(t, 0)) + " = " + iTerms.toString(iTerms.getSubterm(t, 1))),
        null, null);

    // 4. Attribute equation pretty print controls - not fully designed yet
    return ret;
  }

  private void processDirective(Integer t) {
    tt.append(tt.childSymbolString(t, 0) + " ");
    if (iTerms.getTermArity(t) == 2) tt.traverse(iTerms.getSubterm(t, 1));
  }

  private TermTraverserText loadLaTeXTraverser() {
    return new TermTraverserText(iTerms);
  }

}
