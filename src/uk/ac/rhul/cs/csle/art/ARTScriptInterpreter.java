package uk.ac.rhul.cs.csle.art;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.ReferenceParser;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.gll.GLLBaseLine;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.gll.GLLHashPool;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GIFTKind;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.Grammar;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GrammarKind;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GrammarNode;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.LKind;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.lexer.LexerLM;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.rdsob.RDSOBExplicitStack;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.rdsob.RDSOBFunction;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.rdsob.RDSOBGenerator;
import uk.ac.rhul.cs.csle.art.term.ITerms;
import uk.ac.rhul.cs.csle.art.term.TermTraverser;
import uk.ac.rhul.cs.csle.art.util.Util;

public class ARTScriptInterpreter {
  private final int traceLevel = 0;
  private final ITerms iTerms;
  private final GLLBaseLine scriptParser = new GLLBaseLine();
  private final LexerLM scriptLexer = new LexerLM();
//@formatter:off
//  private final String scriptParserTermString = "text(directive(whitespace(cfgBuiltinTerminal(SIMPLE\\_WHITESPACE), cfgBuiltinTerminal(COMMENT\\_NEST\\_ART), cfgBuiltinTerminal(COMMENT\\_LINE\\_C))), cfgRule(cfgLHS(text), cfgSeq(cfgSlot, cfgNonterminal(directive), cfgSlot, cfgFoldUnder(cfgNonterminal(text)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgRule), cfgSlot, cfgFoldUnder(cfgNonterminal(text)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(directive)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgRule)), cfgSlot)), directive(nop), cfgRule(cfgLHS(directive), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('!')), cfgSlot, cfgNonterminal(term), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgRule), cfgSeq(cfgSlot, cfgNonterminal(cfgLHS), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\:\\:=')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgLHS), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgAlts), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('|')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgAlt), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilonCarrier), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgEpsilonCarrier), cfgSeq(cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgAltNoAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgElems), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgExtended), cfgSeq(cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPrim)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgOptional)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgKleene)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPositive)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgDoFirst)), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgAnnotation), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldUnder)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldOver)), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgFoldUnder), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^')), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgFoldOver), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^^')), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgDoFirst), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgOptional), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('?')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('?')), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgPositive), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('+')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('+')), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgKleene), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\*')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\*')), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgPrim), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgNonterminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgCaseSensitiveTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgBuiltinTerminal)), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgNonterminal), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgCaseSensitiveTerminal), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_PLAIN\\_SQ), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgBuiltinTerminal), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('&')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgEpsilon), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('#')), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgActions), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgActionSeq), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgEquation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgAssignment)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgInsert), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgEquation), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('=')), cfgSlot, cfgNonterminal(term), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgAssignment), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\:=')), cfgSlot, cfgNonterminal(term), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgInsert), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^+')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgAttribute), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('.')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgSeq), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), directive(nop), cfgRule(cfgLHS(cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), directive(nop), cfgRule(cfgLHS(term), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgAttribute)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(INTEGER)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(REAL)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_DQ)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgBuiltinTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(arguments)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(ID)), cfgSlot)), directive(nop), cfgRule(cfgLHS(arguments), cfgSeq(cfgSlot, cfgNonterminal(term), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgFoldUnder(cfgNonterminal(arguments)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(term), cfgSlot)), nop)";
  private final String scriptParserTermString = "rules(directive(whitespace(cfgBuiltinTerminal(SIMPLE\\_WHITESPACE), cfgBuiltinTerminal(COMMENT\\_NEST\\_ART), cfgBuiltinTerminal(COMMENT\\_LINE\\_C))), cfgRule(cfgLHS(rules), cfgSeq(cfgSlot, cfgNonterminal(rule), cfgSlot, cfgFoldUnder(cfgNonterminal(rules)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(rule)), cfgSlot)), cfgRule(cfgLHS(rule), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgRule)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(trRule)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseRule)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(directive)), cfgSlot)), cfgRule(cfgLHS(cfgRule), cfgSeq(cfgSlot, cfgNonterminal(cfgLHS), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\:\\:=')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot)), cfgRule(cfgLHS(cfgLHS), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgAlts), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('|')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot)), cfgRule(cfgLHS(cfgAlt), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilonCarrier), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot)), cfgRule(cfgLHS(cfgEpsilonCarrier), cfgSeq(cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot)), cfgRule(cfgLHS(cfgAltNoAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot)), cfgRule(cfgLHS(cfgElems), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot)), cfgRule(cfgLHS(cfgExtended), cfgSeq(cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPrim)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgOptional)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgKleene)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPositive)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgDoFirst)), cfgSlot)), cfgRule(cfgLHS(cfgAnnotation), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldUnder)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldOver)), cfgSlot)), cfgRule(cfgLHS(cfgFoldUnder), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^')), cfgSlot)), cfgRule(cfgLHS(cfgFoldOver), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^^')), cfgSlot)), cfgRule(cfgLHS(cfgDoFirst), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot)), cfgRule(cfgLHS(cfgOptional), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('?')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('?')), cfgSlot)), cfgRule(cfgLHS(cfgPositive), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('+')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('+')), cfgSlot)), cfgRule(cfgLHS(cfgKleene), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\*')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\*')), cfgSlot)), cfgRule(cfgLHS(cfgPrim), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgNonterminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgCaseSensitiveTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgCaseInsensitiveTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCharacterTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCharacterRangeTerminal), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgBuiltinTerminal)), cfgSlot)), cfgRule(cfgLHS(cfgNonterminal), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_DOLLAR), cfgSlot)), cfgRule(cfgLHS(cfgCaseSensitiveTerminal), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_PLAIN\\_SQ), cfgSlot)), cfgRule(cfgLHS(cfgCaseInsensitiveTerminal), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_DQ), cfgSlot)), cfgRule(cfgLHS(cfgCharacterTerminal), cfgSeq(cfgSlot, cfgBuiltinTerminal(CHAR\\_BQ), cfgSlot)), cfgRule(cfgLHS(cfgCharacterRangeTerminal), cfgSeq(cfgSlot, cfgBuiltinTerminal(CHAR\\_BQ), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('..')), cfgSlot, cfgBuiltinTerminal(CHAR\\_BQ), cfgSlot)), cfgRule(cfgLHS(cfgBuiltinTerminal), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('&')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgEpsilon), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('#')), cfgSlot)), cfgRule(cfgLHS(cfgSlotSymbol), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('.'), cfgSlot)), cfgRule(cfgLHS(cfgActions), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot)), cfgRule(cfgLHS(cfgActionSeq), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot)), cfgRule(cfgLHS(cfgAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgEquation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgAssignment)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgInsert), cfgSlot)), cfgRule(cfgLHS(cfgEquation), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('=')), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(cfgAssignment), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\:=')), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(cfgInsert), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^+')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgAttribute), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('.')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgSeq), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), cfgRule(cfgLHS(cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), cfgRule(cfgLHS(trRule), cfgSeq(cfgSlot, cfgNonterminal(trLabel), cfgSlot, cfgNonterminal(tr), cfgSlot)), cfgRule(cfgLHS(trLabel), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon)), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('-')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('-')), cfgSlot, cfgBuiltinTerminal(STRING\\_DQ), cfgSlot)), cfgRule(cfgLHS(tr), cfgSeq(cfgSlot, cfgNonterminal(trPremises), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('---')), cfgSlot, cfgNonterminal(trTransition), cfgSlot, cfgNonterminal(trEquations), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trPremises), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('---')), cfgSlot, cfgNonterminal(trTransition), cfgSlot)), cfgRule(cfgLHS(trPremises), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon)), cfgSeq(cfgSlot, cfgNonterminal(trTransition), cfgSlot, cfgFoldUnder(cfgNonterminal(trPremises)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trMatch), cfgSlot, cfgFoldUnder(cfgNonterminal(trPremises)), cfgSlot)), cfgRule(cfgLHS(trTransition), cfgSeq(cfgSlot, cfgNonterminal(trConfiguration), cfgSlot, cfgNonterminal(TRRELATION), cfgSlot, cfgNonterminal(trConfiguration), cfgSlot)), cfgRule(cfgLHS(trConfiguration), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(trTerm)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('<')), cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('>')), cfgSlot)), cfgRule(cfgLHS(trEntityReferences), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgNonterminal(trTerm), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgNonterminal(trNamedTerm), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgNonterminal(trNamedTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot)), cfgRule(cfgLHS(trNamedTerm), cfgSeq(cfgSlot, cfgNonterminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('=')), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(trMatch), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('|>')), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(trTerm), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_int32)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_real64)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_string)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_char)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(ID)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(trSubterms)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(ID\\_ATTRIBUTE), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgCaseSensitiveTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgCaseInsensitiveTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCharacterTerminal), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgBuiltinTerminal)), cfgSlot)), cfgRule(cfgLHS(trSubterms), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgFoldUnder(cfgNonterminal(trSubterms)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trSubterms)), cfgSlot)), cfgRule(cfgLHS(trEquations), cfgSeq(cfgSlot, cfgNonterminal(cfgEquation), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgEquation), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgFoldUnder(cfgNonterminal(trEquations)), cfgSlot)), cfgRule(cfgLHS(chooseRule), cfgSeq(cfgSlot, cfgNonterminal(chooseDiff), cfgSlot, cfgNonterminal(chooserOp), cfgSlot, cfgNonterminal(chooseDiff), cfgSlot)), cfgRule(cfgLHS(chooseDiff), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseUnionIntersection)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(chooseDiff), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('|||')), cfgSlot, cfgNonterminal(chooseUnionIntersection), cfgSlot)), cfgRule(cfgLHS(chooseUnionIntersection), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseElement)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(chooseUnionIntersection), cfgSlot, cfgFoldOver(cfgNonterminal(chooseUnion)), cfgSlot, cfgNonterminal(chooseElement), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(chooseUnionIntersection), cfgSlot, cfgFoldOver(cfgNonterminal(chooseIntersection)), cfgSlot, cfgNonterminal(chooseElement), cfgSlot)), cfgRule(cfgLHS(chooseUnion), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('|')), cfgSlot)), cfgRule(cfgLHS(chooseIntersection), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('/')), cfgSlot)), cfgRule(cfgLHS(chooseElement), cfgSeq(cfgSlot, cfgNonterminal(cfgNonterminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCharacterTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgBuiltinTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCaseInsensitiveTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCaseSensitiveTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(choosePredefinedSet), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgNonterminal(chooseDiff), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot)), cfgRule(cfgLHS(choosePredefinedSet), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyCharacterTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyBuiltinTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyCaseSensitiveTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyCaseInsensitiveTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyParaterminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyLiteralTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyNonterminal'), cfgSlot)), cfgRule(cfgLHS(chooserOp), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseHigher)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseLower)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseLonger)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseShorter)), cfgSlot)), cfgRule(cfgLHS(chooseHigher), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('>')), cfgSlot)), cfgRule(cfgLHS(chooseLower), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('<')), cfgSlot)), cfgRule(cfgLHS(chooseLonger), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('>>')), cfgSlot)), cfgRule(cfgLHS(chooseShorter), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('<<')), cfgSlot)), cfgRule(cfgLHS(directive), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('!')), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(\\_\\_char), cfgSeq(cfgSlot, cfgBuiltinTerminal(CHAR\\_BQ), cfgSlot)), cfgRule(cfgLHS(\\_\\_int32), cfgSeq(cfgSlot, cfgBuiltinTerminal(SIGNED\\_INTEGER), cfgSlot)), cfgRule(cfgLHS(\\_\\_real64), cfgSeq(cfgSlot, cfgBuiltinTerminal(SIGNED\\_REAL), cfgSlot)), cfgRule(cfgLHS(\\_\\_string), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_DQ), cfgSlot), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_SQ), cfgSlot)), cfgRule(cfgLHS(ID), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(ID)), cfgSlot)), cfgRule(cfgLHS(ID\\_ATTRIBUTE), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('.')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgLHS(TRRELATION), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('->'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('->\\*'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('=>'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('=>\\*'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('~>'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('~>\\*'), cfgSlot))";
  //@formatter:on
  private final int scriptParserTerm;
  private final TermTraverser scriptTraverser;

  private ReferenceParser currentParser = new GLLBaseLine(); // default current parser is GLL base line - change to MGLL when available
  private final LexerLM currentLexer = new LexerLM(); // default current lexer is longest match - change to TWE set lexer when available
  private Grammar currentGrammar; // scriptTraverser builds grammar rules into this grammar
  private int currentDerivationTerm = 0;

  private int goodCount = 0;
  private int badCount = 0;
  private int inadmissableCount = 0;

  public ARTScriptInterpreter(ITerms iTerms) {
    this.iTerms = iTerms;
    scriptTraverser = new TermTraverser(iTerms);
    initialiseScriptTraverser();
    scriptParserTerm = iTerms.findTerm(scriptParserTermString);

    // Bootstrap in action: build the script parser's grammar by traversing the scriptParserTerm and normalising, then making the result the script parser
    currentGrammar = new Grammar("Script grammar", iTerms);
    scriptTraverser.traverse(scriptParserTerm); // Construct the script parser grammar by walking the script parser term from the last bootstrap
    currentGrammar.normalise();
    scriptParser.grammar = currentGrammar;
    System.out.println("Scriptparser initialised");
  }

  private void initialiseScriptTraverser() { // Initialise scriptTraverser
    scriptTraverser.addActionBreak("directive", (Integer t) -> directiveAction(t), null, null);

    scriptTraverser.addActionBreak("cfgLHS", (Integer t) -> currentGrammar.lhsAction(childSymbolString(t)), null, null);
    scriptTraverser.addAction("cfgSeq", (Integer t) -> currentGrammar.altAction(), null, (Integer t) -> currentGrammar.endAction(""));

    scriptTraverser.addAction("cfgEpsilon", (Integer t) -> currentGrammar.updateWorkingNode(GrammarKind.EPS, "#"), null, null);
    scriptTraverser.addActionBreak("cfgNonterminal", (Integer t) -> currentGrammar.updateWorkingNode(GrammarKind.N, childSymbolString(t)), null, null);
    scriptTraverser.addActionBreak("cfgCaseSensitiveTerminal", (Integer t) -> currentGrammar.updateWorkingNode(GrammarKind.T, childSymbolStringStrip(t)), null,
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
  }

  private void directiveAction(int term) {
    System.out.println("Processing " + iTerms.toString(term));
    switch (iTerms.getTermSymbolString(iTerms.getSubterm(term, 0))) {
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
        case "gllhp":
          currentParser = new GLLHashPool();
          break;
        case "gllbl":
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

    case "report":
      currentParser.report(true);
      System.out.println("Running totals - Good: " + goodCount + " Bad: " + badCount + " Inadmissable " + inadmissableCount);
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

        case "derivation":
          System.out.println("current derivation term: [" + currentDerivationTerm + "]\n" + iTerms.toString(currentDerivationTerm, false, -1, null));
          // System.out.println("current derivation term: [" + currentDerivationTerm + "]\n" + iTerms.toString(currentDerivationTerm, true, -1, null));
          if (scriptParserTerm == currentDerivationTerm) System.out.println("Bootstrap achieved: script parser term and current derivation term identical");
          break;

        default:
          Util.fatal("Unexpected !print argument " + iTerms.toString(iTerms.getSubterm(term, 0, i)));
        }
      break;
    case "statisticsTitle":
      System.out.println(
          "Timestamp,Algorithm,Grammar,String name,String,Characters,Tokens,Outcome,Status,CPUs,Tok/s,Desc,GSS N,GSS E,Pop,SPPF S/I,SPPF P,SPPF E,Ambig,Heap,Heap/tok,Exact,Exact/tok,Tables,Tables/tok,Pool,Pool/tok,H0,H1,H2,H3,H4+");
      break;
    case "statistics":
      currentParser.statistics(true);
      break;
    case "try":
      switch (iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, 0))) {
      case "__string":
        doTry("", iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, 0, 0)), true, true);
        break;
      case "file":
        try {
          System.out.println("Attempting to open file " + strip(iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, 0, 0, 0))));
          doTry("", Files.readString(Paths.get(strip(iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, 0, 0, 0))))), true, true);
        } catch (IOException e) {
          Util.fatal("Unable to open try file; skipping " + iTerms.toString(term));
        }
        break;
      default:
        Util.fatal("Unknown try kind " + iTerms.toString(term));
      }
      System.out.println("** Try complete");
      break;
    case "nop": // No operation
      break;
    default:
      Util.fatal("Unknown directive !" + iTerms.toString(iTerms.getSubterm(term, 0)));
    }
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

  private void doTry(String inputStringName, String inputString, boolean outcome, boolean suppressOutput) {
    currentParser.traceLevel = traceLevel;
    currentParser.inputStringName = inputStringName;
    currentParser.inputString = inputString;
    currentParser.suppressEcho = false; // suppressOutput;
    currentGrammar.normalise();
    currentParser.grammar = currentGrammar;
    currentDerivationTerm = 0;
    currentParser.accepted = false;

    currentLexer.lex(inputString, currentGrammar.lexicalKindsArray(), currentGrammar.lexicalStringsArray(), currentGrammar.whitespacesArray(), suppressOutput);
    System.out.println("doTry - lexer complete");
    currentLexer.report();
    currentParser.input = currentLexer.tokens;
    currentParser.positions = currentLexer.positions;
    currentParser.suppressEcho = false;
    if (currentParser.input != null) currentParser.parse();
    System.out.println("Parse complete: accept " + currentParser.accepted);
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
      System.out.println("current derivation term: [" + currentDerivationTerm + "]\n" + iTerms.toString(currentDerivationTerm, false, -1, null));

      if (currentDerivationTerm == scriptParserTerm) System.out.println("Boostrap achieved");
    }
  }

  public void interpret(String scriptString) {
    currentGrammar = new Grammar("Current grammar", iTerms); // Set up a fresh grammar for the
    // Lex the script string
    scriptParser.inputString = scriptString;
    scriptParser.accepted = false;
    scriptLexer.lex(scriptParser.inputString, scriptParser.grammar.lexicalKindsArray(), scriptParser.grammar.lexicalStringsArray(),
        scriptParser.grammar.whitespacesArray(), false);
    if (scriptLexer.tokens == null) Util.fatal("Script lexical error");
    scriptParser.input = scriptLexer.tokens;
    scriptParser.positions = scriptLexer.positions;
    scriptParser.parse();
    if (!scriptParser.accepted) Util.fatal("Script syntax error");
    scriptParser.selectFirst();
    int scriptTerm = scriptParser.derivationAsTerm();
    // System.out.println("Script term:\n" + iTerms.toString(scriptTerm, true, -1, null));
    scriptTraverser.traverse(scriptTerm);
  }

  public Grammar getGrammar() {
    return currentGrammar;
  }
}
