package uk.ac.rhul.cs.csle.art;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import uk.ac.rhul.cs.csle.art.term.TermTraverserText;
import uk.ac.rhul.cs.csle.art.util.Util;

public class ARTScriptInterpreter {
  private final ITerms iTerms;
  private final GLLBaseLine scriptParser = new GLLBaseLine();
  private final LexerLM scriptLexer = new LexerLM();
//@formatter:off
//  private final String scriptParserTermString = "text(directive(whitespace(cfgBuiltinTerminal(SIMPLE\\_WHITESPACE), cfgBuiltinTerminal(COMMENT\\_NEST\\_ART), cfgBuiltinTerminal(COMMENT\\_LINE\\_C))), cfgRule(cfgLHS(text), cfgSeq(cfgSlot, cfgNonterminal(directive), cfgSlot, cfgFoldUnder(cfgNonterminal(text)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgRule), cfgSlot, cfgFoldUnder(cfgNonterminal(text)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(directive)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgRule)), cfgSlot)), directive(nop), cfgRule(cfgLHS(directive), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('!')), cfgSlot, cfgNonterminal(term), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgRule), cfgSeq(cfgSlot, cfgNonterminal(cfgLHS), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\:\\:=')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgLHS), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgAlts), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('|')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgAlt), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilonCarrier), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgEpsilonCarrier), cfgSeq(cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgAltNoAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgElems), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgExtended), cfgSeq(cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPrim)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgOptional)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgKleene)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPositive)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgDoFirst)), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgAnnotation), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldUnder)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldOver)), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgFoldUnder), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^')), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgFoldOver), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^^')), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgDoFirst), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgOptional), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('?')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('?')), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgPositive), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('+')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('+')), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgKleene), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\*')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\*')), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgPrim), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgNonterminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgCaseSensitiveTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgBuiltinTerminal)), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgNonterminal), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgCaseSensitiveTerminal), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_PLAIN\\_SQ), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgBuiltinTerminal), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('&')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgEpsilon), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('#')), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgActions), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgActionSeq), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgEquation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgAssignment)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgInsert), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgEquation), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('=')), cfgSlot, cfgNonterminal(term), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgAssignment), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\:=')), cfgSlot, cfgNonterminal(term), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgInsert), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^+')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgAttribute), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('.')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), directive(nop), cfgRule(cfgLHS(cfgSeq), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), directive(nop), cfgRule(cfgLHS(cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), directive(nop), cfgRule(cfgLHS(term), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgAttribute)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(INTEGER)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(REAL)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_DQ)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgBuiltinTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(arguments)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(ID)), cfgSlot)), directive(nop), cfgRule(cfgLHS(arguments), cfgSeq(cfgSlot, cfgNonterminal(term), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgFoldUnder(cfgNonterminal(arguments)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(term), cfgSlot)), nop)";
//  private final String scriptParserTermString = "rules(directive(whitespace(cfgBuiltinTerminal(SIMPLE\\_WHITESPACE), cfgBuiltinTerminal(COMMENT\\_NEST\\_ART), cfgBuiltinTerminal(COMMENT\\_LINE\\_C))), cfgRule(cfgLHS(rules), cfgSeq(cfgSlot, cfgNonterminal(rule), cfgSlot, cfgFoldUnder(cfgNonterminal(rules)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(rule)), cfgSlot)), cfgRule(cfgLHS(rule), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgRule)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(trRule)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseRule)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(directive)), cfgSlot)), cfgRule(cfgLHS(cfgRule), cfgSeq(cfgSlot, cfgNonterminal(cfgLHS), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\:\\:=')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot)), cfgRule(cfgLHS(cfgLHS), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgAlts), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('|')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot)), cfgRule(cfgLHS(cfgAlt), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilonCarrier), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot)), cfgRule(cfgLHS(cfgEpsilonCarrier), cfgSeq(cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot)), cfgRule(cfgLHS(cfgAltNoAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot)), cfgRule(cfgLHS(cfgElems), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot)), cfgRule(cfgLHS(cfgExtended), cfgSeq(cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPrim)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgOptional)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgKleene)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPositive)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgDoFirst)), cfgSlot)), cfgRule(cfgLHS(cfgAnnotation), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldUnder)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldOver)), cfgSlot)), cfgRule(cfgLHS(cfgFoldUnder), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^')), cfgSlot)), cfgRule(cfgLHS(cfgFoldOver), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^^')), cfgSlot)), cfgRule(cfgLHS(cfgDoFirst), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot)), cfgRule(cfgLHS(cfgOptional), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('?')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('?')), cfgSlot)), cfgRule(cfgLHS(cfgPositive), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('+')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('+')), cfgSlot)), cfgRule(cfgLHS(cfgKleene), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\*')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\*')), cfgSlot)), cfgRule(cfgLHS(cfgPrim), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgNonterminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgCaseSensitiveTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgCaseInsensitiveTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCharacterTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCharacterRangeTerminal), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgBuiltinTerminal)), cfgSlot)), cfgRule(cfgLHS(cfgNonterminal), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_DOLLAR), cfgSlot)), cfgRule(cfgLHS(cfgCaseSensitiveTerminal), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_PLAIN\\_SQ), cfgSlot)), cfgRule(cfgLHS(cfgCaseInsensitiveTerminal), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_DQ), cfgSlot)), cfgRule(cfgLHS(cfgCharacterTerminal), cfgSeq(cfgSlot, cfgBuiltinTerminal(CHAR\\_BQ), cfgSlot)), cfgRule(cfgLHS(cfgCharacterRangeTerminal), cfgSeq(cfgSlot, cfgBuiltinTerminal(CHAR\\_BQ), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('..')), cfgSlot, cfgBuiltinTerminal(CHAR\\_BQ), cfgSlot)), cfgRule(cfgLHS(cfgBuiltinTerminal), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('&')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgEpsilon), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('#')), cfgSlot)), cfgRule(cfgLHS(cfgSlotSymbol), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('.'), cfgSlot)), cfgRule(cfgLHS(cfgActions), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot)), cfgRule(cfgLHS(cfgActionSeq), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot)), cfgRule(cfgLHS(cfgAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgEquation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgAssignment)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgInsert), cfgSlot)), cfgRule(cfgLHS(cfgEquation), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('=')), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(cfgAssignment), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\:=')), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(cfgInsert), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^+')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgAttribute), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('.')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgSeq), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), cfgRule(cfgLHS(cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), cfgRule(cfgLHS(trRule), cfgSeq(cfgSlot, cfgNonterminal(trLabel), cfgSlot, cfgNonterminal(tr), cfgSlot)), cfgRule(cfgLHS(trLabel), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon)), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('-')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('-')), cfgSlot, cfgBuiltinTerminal(STRING\\_DQ), cfgSlot)), cfgRule(cfgLHS(tr), cfgSeq(cfgSlot, cfgNonterminal(trPremises), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('---')), cfgSlot, cfgNonterminal(trTransition), cfgSlot, cfgNonterminal(trEquations), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trPremises), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('---')), cfgSlot, cfgNonterminal(trTransition), cfgSlot)), cfgRule(cfgLHS(trPremises), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon)), cfgSeq(cfgSlot, cfgNonterminal(trTransition), cfgSlot, cfgFoldUnder(cfgNonterminal(trPremises)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trMatch), cfgSlot, cfgFoldUnder(cfgNonterminal(trPremises)), cfgSlot)), cfgRule(cfgLHS(trTransition), cfgSeq(cfgSlot, cfgNonterminal(trConfiguration), cfgSlot, cfgNonterminal(TRRELATION), cfgSlot, cfgNonterminal(trConfiguration), cfgSlot)), cfgRule(cfgLHS(trConfiguration), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(trTerm)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('<')), cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('>')), cfgSlot)), cfgRule(cfgLHS(trEntityReferences), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgNonterminal(trTerm), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgNonterminal(trNamedTerm), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgNonterminal(trNamedTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot)), cfgRule(cfgLHS(trNamedTerm), cfgSeq(cfgSlot, cfgNonterminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('=')), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(trMatch), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('|>')), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(trTerm), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_int32)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_real64)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_string)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_char)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(ID)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(trSubterms)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(ID\\_ATTRIBUTE), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgCaseSensitiveTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgCaseInsensitiveTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCharacterTerminal), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgBuiltinTerminal)), cfgSlot)), cfgRule(cfgLHS(trSubterms), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgFoldUnder(cfgNonterminal(trSubterms)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trSubterms)), cfgSlot)), cfgRule(cfgLHS(trEquations), cfgSeq(cfgSlot, cfgNonterminal(cfgEquation), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgEquation), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgFoldUnder(cfgNonterminal(trEquations)), cfgSlot)), cfgRule(cfgLHS(chooseRule), cfgSeq(cfgSlot, cfgNonterminal(chooseDiff), cfgSlot, cfgNonterminal(chooserOp), cfgSlot, cfgNonterminal(chooseDiff), cfgSlot)), cfgRule(cfgLHS(chooseDiff), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseUnionIntersection)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(chooseDiff), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('|||')), cfgSlot, cfgNonterminal(chooseUnionIntersection), cfgSlot)), cfgRule(cfgLHS(chooseUnionIntersection), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseElement)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(chooseUnionIntersection), cfgSlot, cfgFoldOver(cfgNonterminal(chooseUnion)), cfgSlot, cfgNonterminal(chooseElement), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(chooseUnionIntersection), cfgSlot, cfgFoldOver(cfgNonterminal(chooseIntersection)), cfgSlot, cfgNonterminal(chooseElement), cfgSlot)), cfgRule(cfgLHS(chooseUnion), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('|')), cfgSlot)), cfgRule(cfgLHS(chooseIntersection), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('/')), cfgSlot)), cfgRule(cfgLHS(chooseElement), cfgSeq(cfgSlot, cfgNonterminal(cfgNonterminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCharacterTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgBuiltinTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCaseInsensitiveTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCaseSensitiveTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(choosePredefinedSet), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgNonterminal(chooseDiff), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot)), cfgRule(cfgLHS(choosePredefinedSet), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyCharacterTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyBuiltinTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyCaseSensitiveTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyCaseInsensitiveTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyParaterminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyLiteralTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyNonterminal'), cfgSlot)), cfgRule(cfgLHS(chooserOp), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseHigher)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseLower)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseLonger)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseShorter)), cfgSlot)), cfgRule(cfgLHS(chooseHigher), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('>')), cfgSlot)), cfgRule(cfgLHS(chooseLower), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('<')), cfgSlot)), cfgRule(cfgLHS(chooseLonger), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('>>')), cfgSlot)), cfgRule(cfgLHS(chooseShorter), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('<<')), cfgSlot)), cfgRule(cfgLHS(directive), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('!')), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(\\_\\_char), cfgSeq(cfgSlot, cfgBuiltinTerminal(CHAR\\_BQ), cfgSlot)), cfgRule(cfgLHS(\\_\\_int32), cfgSeq(cfgSlot, cfgBuiltinTerminal(SIGNED\\_INTEGER), cfgSlot)), cfgRule(cfgLHS(\\_\\_real64), cfgSeq(cfgSlot, cfgBuiltinTerminal(SIGNED\\_REAL), cfgSlot)), cfgRule(cfgLHS(\\_\\_string), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_DQ), cfgSlot), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_SQ), cfgSlot)), cfgRule(cfgLHS(ID), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(ID)), cfgSlot)), cfgRule(cfgLHS(ID\\_ATTRIBUTE), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('.')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgLHS(TRRELATION), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('->'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('->\\*'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('=>'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('=>\\*'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('~>'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('~>\\*'), cfgSlot))";
  private final String scriptParserTermString = "rules(directive(whitespace(cfgBuiltinTerminal(SIMPLE\\_WHITESPACE), cfgBuiltinTerminal(COMMENT\\_NEST\\_ART), cfgBuiltinTerminal(COMMENT\\_LINE\\_C))), cfgRule(cfgLHS(rules), cfgSeq(cfgSlot, cfgNonterminal(rule), cfgSlot, cfgFoldUnder(cfgNonterminal(rules)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(rule), cfgSlot)), cfgRule(cfgLHS(rule), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgRule)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(trRule)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseRule)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(directive)), cfgSlot)), cfgRule(cfgLHS(cfgRule), cfgSeq(cfgSlot, cfgNonterminal(cfgLHS), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\:\\:=')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot)), cfgRule(cfgLHS(cfgLHS), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgAlts), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('|')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot)), cfgRule(cfgLHS(cfgAlt), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilonCarrier), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot)), cfgRule(cfgLHS(cfgEpsilonCarrier), cfgSeq(cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot)), cfgRule(cfgLHS(cfgAltNoAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot)), cfgRule(cfgLHS(cfgElems), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot)), cfgRule(cfgLHS(cfgExtended), cfgSeq(cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPrim)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgOptional)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgKleene)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPositive)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgDoFirst)), cfgSlot)), cfgRule(cfgLHS(cfgAnnotation), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldUnder)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldOver)), cfgSlot)), cfgRule(cfgLHS(cfgFoldUnder), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^')), cfgSlot)), cfgRule(cfgLHS(cfgFoldOver), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^^')), cfgSlot)), cfgRule(cfgLHS(cfgDoFirst), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot)), cfgRule(cfgLHS(cfgOptional), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('?')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('?')), cfgSlot)), cfgRule(cfgLHS(cfgPositive), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('+')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('+')), cfgSlot)), cfgRule(cfgLHS(cfgKleene), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\*')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\*')), cfgSlot)), cfgRule(cfgLHS(cfgPrim), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgNonterminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgCaseSensitiveTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgCaseInsensitiveTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCharacterTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCharacterRangeTerminal), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgBuiltinTerminal)), cfgSlot)), cfgRule(cfgLHS(cfgNonterminal), cfgSeq(cfgSlot, cfgNonterminal(ID), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(STRING\\_DOLLAR), cfgSlot)), cfgRule(cfgLHS(cfgCaseSensitiveTerminal), cfgSeq(cfgSlot, cfgNonterminal(STRING\\_PLAIN\\_SQ), cfgSlot)), cfgRule(cfgLHS(cfgCaseInsensitiveTerminal), cfgSeq(cfgSlot, cfgNonterminal(STRING\\_DQ), cfgSlot)), cfgRule(cfgLHS(cfgCharacterTerminal), cfgSeq(cfgSlot, cfgNonterminal(CHAR\\_BQ), cfgSlot)), cfgRule(cfgLHS(cfgCharacterRangeTerminal), cfgSeq(cfgSlot, cfgNonterminal(CHAR\\_BQ), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('..')), cfgSlot, cfgNonterminal(CHAR\\_BQ), cfgSlot)), cfgRule(cfgLHS(cfgBuiltinTerminal), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('&')), cfgSlot, cfgNonterminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgEpsilon), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('#')), cfgSlot)), cfgRule(cfgLHS(cfgSlotSymbol), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('.'), cfgSlot)), cfgRule(cfgLHS(cfgActions), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot)), cfgRule(cfgLHS(cfgActionSeq), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot)), cfgRule(cfgLHS(cfgAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgEquation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgAssignment)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgInsert), cfgSlot)), cfgRule(cfgLHS(cfgEquation), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('=')), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(cfgAssignment), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\:=')), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(cfgInsert), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^+')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgAttribute), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('.')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgSeq), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), cfgRule(cfgLHS(cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), cfgRule(cfgLHS(trRule), cfgSeq(cfgSlot, cfgNonterminal(trLabel), cfgSlot, cfgNonterminal(tr), cfgSlot)), cfgRule(cfgLHS(trLabel), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon)), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('-')), cfgSlot, cfgNonterminal(ID), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('-')), cfgSlot, cfgNonterminal(STRING\\_DQ), cfgSlot)), cfgRule(cfgLHS(tr), cfgSeq(cfgSlot, cfgNonterminal(trPremises), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('---')), cfgSlot, cfgNonterminal(trTransition), cfgSlot, cfgNonterminal(trEquations), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trPremises), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('---')), cfgSlot, cfgNonterminal(trTransition), cfgSlot)), cfgRule(cfgLHS(trPremises), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon)), cfgSeq(cfgSlot, cfgNonterminal(trTransition), cfgSlot, cfgFoldUnder(cfgNonterminal(trPremises)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trMatch), cfgSlot, cfgFoldUnder(cfgNonterminal(trPremises)), cfgSlot)), cfgRule(cfgLHS(trTransition), cfgSeq(cfgSlot, cfgNonterminal(trConfiguration), cfgSlot, cfgNonterminal(TRRELATION), cfgSlot, cfgNonterminal(trConfiguration), cfgSlot)), cfgRule(cfgLHS(trConfiguration), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(trTerm)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('<')), cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('>')), cfgSlot)), cfgRule(cfgLHS(trEntityReferences), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgNonterminal(trTerm), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgNonterminal(trNamedTerm), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgNonterminal(trNamedTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot)), cfgRule(cfgLHS(trNamedTerm), cfgSeq(cfgSlot, cfgNonterminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('=')), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(trMatch), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('|>')), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(trTerm), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_bool)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_int32)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_real64)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_string)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_char)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(ID)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(trSubterms)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(ID\\_ATTRIBUTE), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgBuiltinTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(STRING\\_PLAIN\\_SQ)), cfgSlot)), cfgRule(cfgLHS(trSubterms), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgFoldUnder(cfgNonterminal(trSubterms)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trSubterms)), cfgSlot)), cfgRule(cfgLHS(trEquations), cfgSeq(cfgSlot, cfgNonterminal(cfgEquation), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgEquation), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgFoldUnder(cfgNonterminal(trEquations)), cfgSlot)), cfgRule(cfgLHS(chooseRule), cfgSeq(cfgSlot, cfgNonterminal(chooseDiff), cfgSlot, cfgNonterminal(chooserOp), cfgSlot, cfgNonterminal(chooseDiff), cfgSlot)), cfgRule(cfgLHS(chooseDiff), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseUnionIntersection)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(chooseDiff), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('|||')), cfgSlot, cfgNonterminal(chooseUnionIntersection), cfgSlot)), cfgRule(cfgLHS(chooseUnionIntersection), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseElement)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(chooseUnionIntersection), cfgSlot, cfgFoldOver(cfgNonterminal(chooseUnion)), cfgSlot, cfgNonterminal(chooseElement), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(chooseUnionIntersection), cfgSlot, cfgFoldOver(cfgNonterminal(chooseIntersection)), cfgSlot, cfgNonterminal(chooseElement), cfgSlot)), cfgRule(cfgLHS(chooseUnion), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('|')), cfgSlot)), cfgRule(cfgLHS(chooseIntersection), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('/')), cfgSlot)), cfgRule(cfgLHS(chooseElement), cfgSeq(cfgSlot, cfgNonterminal(cfgNonterminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCharacterTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgBuiltinTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCaseInsensitiveTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCaseSensitiveTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(choosePredefinedSet), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgNonterminal(chooseDiff), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot)), cfgRule(cfgLHS(choosePredefinedSet), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyCharacterTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyBuiltinTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyCaseSensitiveTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyCaseInsensitiveTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyParaterminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyLiteralTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyNonterminal'), cfgSlot)), cfgRule(cfgLHS(chooserOp), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseHigher)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseLower)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseLonger)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseShorter)), cfgSlot)), cfgRule(cfgLHS(chooseHigher), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('>')), cfgSlot)), cfgRule(cfgLHS(chooseLower), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('<')), cfgSlot)), cfgRule(cfgLHS(chooseLonger), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('>>')), cfgSlot)), cfgRule(cfgLHS(chooseShorter), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('<<')), cfgSlot)), cfgRule(cfgLHS(directive), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('!')), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(\\_\\_bool), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('true'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('false'), cfgSlot)), cfgRule(cfgLHS(\\_\\_char), cfgSeq(cfgSlot, cfgBuiltinTerminal(CHAR\\_BQ), cfgSlot)), cfgRule(cfgLHS(\\_\\_int32), cfgSeq(cfgSlot, cfgBuiltinTerminal(SIGNED\\_INTEGER), cfgSlot)), cfgRule(cfgLHS(\\_\\_real64), cfgSeq(cfgSlot, cfgBuiltinTerminal(SIGNED\\_REAL), cfgSlot)), cfgRule(cfgLHS(\\_\\_string), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_DQ), cfgSlot), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_SQ), cfgSlot)), cfgRule(cfgLHS(ID), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(ID)), cfgSlot)), cfgRule(cfgLHS(ID\\_ATTRIBUTE), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('.')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(STRING\\_DQ), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_DQ)), cfgSlot)), cfgRule(cfgLHS(STRING\\_DOLLAR), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_DOLLAR)), cfgSlot)), cfgRule(cfgLHS(STRING\\_PLAIN\\_SQ), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_PLAIN\\_SQ)), cfgSlot)), cfgRule(cfgLHS(CHAR\\_BQ), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(CHAR\\_BQ)), cfgSlot)), cfgRule(cfgLHS(STRING\\_BRACE\\_NEST), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_BRACE\\_NEST)), cfgSlot)), cfgRule(cfgLHS(STRING\\_BRACKET\\_NEST), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_BRACKET\\_NEST)), cfgSlot)), cfgRule(cfgLHS(TRRELATION), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('->'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('->\\*'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('=>'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('=>\\*'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('~>'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('~>\\*'), cfgSlot)))";
//  private final String scriptParserTermString = "rules(directive(whitespace(cfgBuiltinTerminal(SIMPLE\\_WHITESPACE), cfgBuiltinTerminal(COMMENT\\_NEST\\_ART), cfgBuiltinTerminal(COMMENT\\_LINE\\_C))), cfgRule(cfgLHS(rules), cfgSeq(cfgSlot, cfgNonterminal(rule), cfgSlot, cfgFoldUnder(cfgNonterminal(rules)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(rule)), cfgSlot)), cfgRule(cfgLHS(rule), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgRule)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(trRule)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseRule)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(directive)), cfgSlot)), cfgRule(cfgLHS(cfgRule), cfgSeq(cfgSlot, cfgNonterminal(cfgLHS), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\:\\:=')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot)), cfgRule(cfgLHS(cfgLHS), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgAlts), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('|')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot)), cfgRule(cfgLHS(cfgAlt), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilonCarrier), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot)), cfgRule(cfgLHS(cfgEpsilonCarrier), cfgSeq(cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot)), cfgRule(cfgLHS(cfgAltNoAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot)), cfgRule(cfgLHS(cfgElems), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot)), cfgRule(cfgLHS(cfgExtended), cfgSeq(cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPrim)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgOptional)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgKleene)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPositive)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgDoFirst)), cfgSlot)), cfgRule(cfgLHS(cfgAnnotation), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldUnder)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldOver)), cfgSlot)), cfgRule(cfgLHS(cfgFoldUnder), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^')), cfgSlot)), cfgRule(cfgLHS(cfgFoldOver), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^^')), cfgSlot)), cfgRule(cfgLHS(cfgDoFirst), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot)), cfgRule(cfgLHS(cfgOptional), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('?')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('?')), cfgSlot)), cfgRule(cfgLHS(cfgPositive), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('+')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('+')), cfgSlot)), cfgRule(cfgLHS(cfgKleene), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\*')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\*')), cfgSlot)), cfgRule(cfgLHS(cfgPrim), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgNonterminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgCaseSensitiveTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgCaseInsensitiveTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCharacterTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCharacterRangeTerminal), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgBuiltinTerminal)), cfgSlot)), cfgRule(cfgLHS(cfgNonterminal), cfgSeq(cfgSlot, cfgNonterminal(ID), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(STRING\\_DOLLAR), cfgSlot)), cfgRule(cfgLHS(cfgCaseSensitiveTerminal), cfgSeq(cfgSlot, cfgNonterminal(STRING\\_PLAIN\\_SQ), cfgSlot)), cfgRule(cfgLHS(cfgCaseInsensitiveTerminal), cfgSeq(cfgSlot, cfgNonterminal(STRING\\_DQ), cfgSlot)), cfgRule(cfgLHS(cfgCharacterTerminal), cfgSeq(cfgSlot, cfgNonterminal(CHAR\\_BQ), cfgSlot)), cfgRule(cfgLHS(cfgCharacterRangeTerminal), cfgSeq(cfgSlot, cfgNonterminal(CHAR\\_BQ), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('..')), cfgSlot, cfgNonterminal(CHAR\\_BQ), cfgSlot)), cfgRule(cfgLHS(cfgBuiltinTerminal), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('&')), cfgSlot, cfgNonterminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgEpsilon), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('#')), cfgSlot)), cfgRule(cfgLHS(cfgSlotSymbol), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('.'), cfgSlot)), cfgRule(cfgLHS(cfgActions), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot)), cfgRule(cfgLHS(cfgActionSeq), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot)), cfgRule(cfgLHS(cfgAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgEquation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgAssignment)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgInsert), cfgSlot)), cfgRule(cfgLHS(cfgEquation), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('=')), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(cfgAssignment), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\:=')), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(cfgInsert), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^+')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgAttribute), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('.')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgSeq), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), cfgRule(cfgLHS(cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), cfgRule(cfgLHS(trRule), cfgSeq(cfgSlot, cfgNonterminal(trLabel), cfgSlot, cfgNonterminal(tr), cfgSlot)), cfgRule(cfgLHS(trLabel), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon)), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('-')), cfgSlot, cfgNonterminal(ID), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('-')), cfgSlot, cfgNonterminal(STRING\\_DQ), cfgSlot)), cfgRule(cfgLHS(tr), cfgSeq(cfgSlot, cfgNonterminal(trPremises), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('---')), cfgSlot, cfgNonterminal(trTransition), cfgSlot, cfgNonterminal(trEquations), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trPremises), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('---')), cfgSlot, cfgNonterminal(trTransition), cfgSlot)), cfgRule(cfgLHS(trPremises), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon)), cfgSeq(cfgSlot, cfgNonterminal(trTransition), cfgSlot, cfgFoldUnder(cfgNonterminal(trPremises)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trMatch), cfgSlot, cfgFoldUnder(cfgNonterminal(trPremises)), cfgSlot)), cfgRule(cfgLHS(trTransition), cfgSeq(cfgSlot, cfgNonterminal(trConfiguration), cfgSlot, cfgNonterminal(TRRELATION), cfgSlot, cfgNonterminal(trConfiguration), cfgSlot)), cfgRule(cfgLHS(trConfiguration), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(trTerm)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('<')), cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('>')), cfgSlot)), cfgRule(cfgLHS(trEntityReferences), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgNonterminal(trTerm), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgNonterminal(trNamedTerm), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgNonterminal(trNamedTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trEntityReferences)), cfgSlot)), cfgRule(cfgLHS(trNamedTerm), cfgSeq(cfgSlot, cfgNonterminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('=')), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(trMatch), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('|>')), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(trTerm), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_bool)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_int32)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_real64)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_string)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(\\_\\_char)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(ID)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(trSubterms)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(ID\\_ATTRIBUTE), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgBuiltinTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(STRING\\_PLAIN\\_SQ)), cfgSlot)), cfgRule(cfgLHS(trSubterms), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgFoldUnder(cfgNonterminal(trSubterms)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(trTerm), cfgSlot, cfgFoldUnder(cfgNonterminal(trSubterms)), cfgSlot)), cfgRule(cfgLHS(trEquations), cfgSeq(cfgSlot, cfgNonterminal(cfgEquation), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgEquation), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgFoldUnder(cfgNonterminal(trEquations)), cfgSlot)), cfgRule(cfgLHS(chooseRule), cfgSeq(cfgSlot, cfgNonterminal(chooseDiff), cfgSlot, cfgNonterminal(chooserOp), cfgSlot, cfgNonterminal(chooseDiff), cfgSlot)), cfgRule(cfgLHS(chooseDiff), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseUnionIntersection)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(chooseDiff), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('|||')), cfgSlot, cfgNonterminal(chooseUnionIntersection), cfgSlot)), cfgRule(cfgLHS(chooseUnionIntersection), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseElement)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(chooseUnionIntersection), cfgSlot, cfgFoldOver(cfgNonterminal(chooseUnion)), cfgSlot, cfgNonterminal(chooseElement), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(chooseUnionIntersection), cfgSlot, cfgFoldOver(cfgNonterminal(chooseIntersection)), cfgSlot, cfgNonterminal(chooseElement), cfgSlot)), cfgRule(cfgLHS(chooseUnion), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('|')), cfgSlot)), cfgRule(cfgLHS(chooseIntersection), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('/')), cfgSlot)), cfgRule(cfgLHS(chooseElement), cfgSeq(cfgSlot, cfgNonterminal(cfgNonterminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCharacterTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgBuiltinTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCaseInsensitiveTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgCaseSensitiveTerminal), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(choosePredefinedSet), cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgNonterminal(chooseDiff), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot)), cfgRule(cfgLHS(choosePredefinedSet), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyCharacterTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyBuiltinTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyCaseSensitiveTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyCaseInsensitiveTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyParaterminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyLiteralTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyTerminal'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('anyNonterminal'), cfgSlot)), cfgRule(cfgLHS(chooserOp), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseHigher)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseLower)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseLonger)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(chooseShorter)), cfgSlot)), cfgRule(cfgLHS(chooseHigher), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('>')), cfgSlot)), cfgRule(cfgLHS(chooseLower), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('<')), cfgSlot)), cfgRule(cfgLHS(chooseLonger), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('>>')), cfgSlot)), cfgRule(cfgLHS(chooseShorter), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('<<')), cfgSlot)), cfgRule(cfgLHS(directive), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('!')), cfgSlot, cfgNonterminal(trTerm), cfgSlot)), cfgRule(cfgLHS(\\_\\_bool), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('true'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('false'), cfgSlot)), cfgRule(cfgLHS(\\_\\_char), cfgSeq(cfgSlot, cfgBuiltinTerminal(CHAR\\_BQ), cfgSlot)), cfgRule(cfgLHS(\\_\\_int32), cfgSeq(cfgSlot, cfgBuiltinTerminal(SIGNED\\_INTEGER), cfgSlot)), cfgRule(cfgLHS(\\_\\_real64), cfgSeq(cfgSlot, cfgBuiltinTerminal(SIGNED\\_REAL), cfgSlot)), cfgRule(cfgLHS(\\_\\_string), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_DQ), cfgSlot), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_SQ), cfgSlot)), cfgRule(cfgLHS(ID), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(ID)), cfgSlot)), cfgRule(cfgLHS(ID\\_ATTRIBUTE), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('.')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(STRING\\_DQ), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_DQ)), cfgSlot)), cfgRule(cfgLHS(STRING\\_DOLLAR), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_DOLLAR)), cfgSlot)), cfgRule(cfgLHS(STRING\\_PLAIN\\_SQ), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_PLAIN\\_SQ)), cfgSlot)), cfgRule(cfgLHS(CHAR\\_BQ), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(CHAR\\_BQ)), cfgSlot)), cfgRule(cfgLHS(STRING\\_BRACE\\_NEST), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_BRACE\\_NEST)), cfgSlot)), cfgRule(cfgLHS(STRING\\_BRACKET\\_NEST), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_BRACKET\\_NEST)), cfgSlot)), cfgLHS(TRRELATION), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('->'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('->\\*'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('=>'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('=>\\*'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('~>'), cfgSlot), cfgSeq(cfgSlot, cfgCaseSensitiveTerminal('~>\\*'), cfgSlot))";
      //@formatter:on
  private final int scriptParserTerm;
  private final TermTraverser scriptTraverser;
  public TermTraverserText tt;
  public final TermTraverserText latexTraverser;

  private ReferenceParser currentParser = new GLLBaseLine(); // default current parser is GLL base line - change to MGLL when available
  private final LexerLM currentLexer = new LexerLM(); // default current lexer is longest match - change to TWE set lexer when available
  private Grammar currentGrammar; // scriptTraverser builds grammar rules into this grammar
  private int currentDerivationTerm = 0;

  private int goodCount = 0;
  private int badCount = 0;
  private int inadmissableCount = 0;

  public int verbosityLevel = 5;
  public int traceLevel = 3;
  public int statisticsLevel = 5;
  public int defaultDepthLimit = 5;

  public int inputTerm = 0;
  public int resultTerm = 0;

  public int rewriteAttemptCounter = 0;
  public int rewriteStepCounter = 0;
  public boolean rewriteDisable = false;
  public boolean rewritePure = true;
  public boolean rewritePreorder = false;
  public boolean rewritePostorder = false;
  public boolean rewriteOneStep = false;
  public boolean rewriteResume = false;
  public boolean rewriteContractum = false;
  public boolean rewriteTraverse = false;
  public boolean rewriteActive = false;
  public final Map<Integer, Set<Integer>> rewriteTerminals = new HashMap<>();;

  public final Map<Integer, Map<Integer, List<Integer>>> trRules = new LinkedHashMap<>();
  public int defaultStartNonterminal = 0; // This is set by the first Context Free Grammar rule encountered
  public int defaultStartRelation = 0; // This is set by the first term rewrite rule encountered
  public int startRelation = 0;
  final Map<Integer, Integer> termToEnumElementMap = new HashMap<>();
  final Map<Integer, Integer> enumElementToTermMap = new HashMap<>();
  final Map<Integer, Integer> termRewriteConstructorDefinitions = new HashMap<>(); // The number of times a constructor appears as the root of a term
  final Map<Integer, Integer> termRewriteConstructorUsages = new HashMap<>(); // The number of times a constructor appears
  public final Map<Integer, Set<Integer>> cycleCheck = new HashMap<>();

  final Set<Integer> functionsInUse = new HashSet<>(); // The set of functions in use

  public final Map<Integer, Map<Integer, Integer>> variableNamesByRule = new HashMap<>(); // Map from term to the variable aliases used in that term
  public final Map<Integer, Map<Integer, Integer>> reverseVariableNamesByRule = new HashMap<>(); // Map from term to the variable aliases used in that term
  private Map<Integer, Integer> variableMap;

  public ARTScriptInterpreter(ITerms iTerms) {
    this.iTerms = iTerms;

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
    scriptParser.grammar = currentGrammar;
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

    scriptTraverser.addAction("trRule", (Integer t) -> buildTRRule(t), null, null);
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

  private void directiveAction(int term) {
    // System.out.println("Processing " + iTerms.toString(term));
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
    case "tryTerm":
      normaliseAndStaticCheckRewriteRules();
      // System.out.println(trRulesToString(trRules));
      int tryTerm = iTerms.getSubterm(term, 0);
      int tryTermArity = iTerms.getTermArity(tryTerm);
      if (tryTermArity > 0 && iTerms.getSubterm(tryTerm, 0) != 0) inputTerm = iTerms.getSubterm(tryTerm, 0); // If no children, or if first child is zero, then
                                                                                                             // use previous
      if (tryTermArity > 1)
        startRelation = iTerms.getSubterm(tryTerm, 1);
      else
        startRelation = defaultStartRelation;
      if (tryTermArity > 2)
        resultTerm = iTerms.getSubterm(tryTerm, 2);
      else
        resultTerm = 0;
      System.out.println("inputTerm " + iTerms.toString(inputTerm));
      System.out.println("startRelation " + iTerms.toString(startRelation));
      System.out.println("resultTerm " + iTerms.toString(resultTerm));
      stepper();
      break;
    case "try":
      switch (iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, 0))) {
      case "__string":
        doTry("", iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, 0, 0)), true, true);
        break;
      case "file":
        try {
          // System.out.println("Attempting to open file " + strip(iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, 0, 0, 0))));
          doTry("", Files.readString(Paths.get(strip(iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, 0, 0, 0))))), true, true);
        } catch (IOException e) {
          Util.fatal("Unable to open try file; skipping " + iTerms.toString(term));
        }
        break;
      default:
        Util.fatal("Unknown try kind " + iTerms.toString(term));
      }
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
    // currentLexer.report();
    currentParser.input = currentLexer.tokens;
    currentParser.positions = currentLexer.positions;
    currentParser.suppressEcho = false;
    if (currentParser.input != null) currentParser.parse();
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
      // System.out.println("current derivation term: [" + currentDerivationTerm + "]\n" + iTerms.toString(currentDerivationTerm, false, -1, null));
      // if (currentDerivationTerm == scriptParserTerm) System.out.println("Bootstrap achieved");
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

  public void trace(int level, int indent, String string) {
    if (traceLevel >= level) {
      for (int i = 0; i < indent; i++)
        System.out.print("  ");
      System.out.println(string);
    }
  }

  private String trRulesToString(Map<Integer, Map<Integer, List<Integer>>> trRules) {
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

    // trace(3, level, "Rewrite attempt " + ++rewriteAttemptCounter + ": " + render(term) + render(relationTerm));
    trace(3, level, "Rewrite call " + ++rewriteAttemptCounter + " " + render(term) + render(relationTerm));
    if (!cycleCheck.containsKey(relationTerm)) cycleCheck.put(relationTerm, new HashSet<Integer>());
    Set<Integer> cycleSet = cycleCheck.get(relationTerm);
    // if (cycleSet.contains(configuration)) throw new ARTExceptionFatal("cycle detected " +iTerms.toString(configuration) +iTerms.toString(relationTerm));
    cycleSet.add(term);

    if (isTerminatingConfiguration(term, relationTerm)) {
      trace(3, level + 1, "Terminal " + tt.toString(term));
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
      trace(3, level, "No rules in" + render(relationTerm) + "for constructor " + iTerms.getTermSymbolString(rootTheta));
      return term;
    }

    nextRule: for (int ruleIndex : ruleList) {
      variableMap = reverseVariableNamesByRule.get(ruleIndex);
      // System.out.println("Variable map: ");
      // for (int i : variableMap.keySet())
      // System.out.println(i + ":" + iTerms.getString(variableMap.get(i)));
      trace(3, level, tt.toString(ruleIndex, false, -1, variableMap)); // Announce the next rule we are going to try
      int lhs = iTerms.getSubterm(ruleIndex, 1, 1, 0);
      int premises = iTerms.getSubterm(ruleIndex, 1, 0);
      int premiseCount = iTerms.getTermArity(premises);
      int rhs = iTerms.getSubterm(ruleIndex, 1, 1, 2);
      int[] bindings = new int[ITerms.variableCount];

      int ruleLabel = thetaFromConfiguration(ruleIndex);
      if (!iTerms.matchZeroSV(term, lhs, bindings)) {
        trace(3, level, render(iTerms.getSubterm(ruleLabel, 0)) + " Theta match failed: seek another rule");
        continue nextRule;
      }
      trace(5, level, render(iTerms.getSubterm(ruleLabel, 0)) + "bindings after Theta match " + bindingsToString(bindings, variableMap));

      // Now work through the premises
      for (int premiseNumber = 0; premiseNumber < premiseCount; premiseNumber++) {
        int premise = iTerms.getSubterm(premises, premiseNumber);
        trace(4, level, render(iTerms.getSubterm(ruleLabel, 0)) + "premise " + (premiseNumber + 1) + " " + render(premise));
        if (iTerms.hasSymbol(premise, "trMatch")) {// |> match expressions
          if (!iTerms.matchZeroSV(iTerms.substitute(bindings, iTerms.getSubterm(premise, 0), 0), iTerms.getSubterm(premise, 1), bindings)) {
            trace(4, level, render(iTerms.getSubterm(ruleLabel, 0)) + "premise " + (premiseNumber + 1) + " failed: seek another rule");
            continue nextRule;
          }
        } else { // transition
          if (iTerms.hasSymbol(premise, "trTransition")) {
            int rewriteTerm = iTerms.substitute(bindings, thetaLHSFromConfiguration(premise), 0);
            int rewriteRelation = iTerms.getSubterm(premise, 1);
            int rewrittenTerm = rewriteAttempt(rewriteTerm, rewriteRelation, level + 1);
            if (rewrittenTerm < 0) {
              trace(4, level, render(iTerms.getSubterm(ruleLabel, 0)) + "premise" + (premiseNumber + 1) + " failed: seek another rule");
              continue nextRule;
            }
            if (!iTerms.matchZeroSV(rewrittenTerm, iTerms.getSubterm(premise, 2), bindings)) continue nextRule;
          } else
            Util.fatal("ESOS - unknown premise kind " + render(premise));
        }
        trace(5, level,
            render(iTerms.getSubterm(ruleLabel, 0)) + "bindings after premise " + (premiseNumber + 1) + " " + bindingsToString(bindings, variableMap));
      }

      term = iTerms.substitute(bindings, rhs, 0);
      trace(level == 1 ? 2 : 3, level, render(iTerms.getSubterm(ruleLabel, 0)) + "rewrites to " + render(term));
      rewriteActive = rewriteResume;

      if (rewriteContractum) {
        trace(5, level, render(iTerms.getSubterm(ruleLabel, 0)) + "rewrites contractum\n");
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
      trace(3, level + 1, "Found" + render(relationTerm) + "terminal " + render(term));
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

  public void stepper() {
    rewriteTraverse = rewritePreorder || rewritePostorder;
    rewriteActive = !rewriteDisable;
    rewriteAttemptCounter = rewriteStepCounter = 0;

    int oldTerm = inputTerm;
    int relation = startRelation;
    int testTerm = resultTerm;
    int newTerm;

    while (true) {
      for (int i : cycleCheck.keySet())
        cycleCheck.get(i).clear();
      rewriteActive = true; // reset rewriteActive flag before attempting traversal
      trace(1, 0, "Step " + ++rewriteStepCounter);
      if (rewritePure)
        newTerm = rewriteAttempt(oldTerm, relation, 1);
      else
        newTerm = rewriteTraverse(oldTerm, relation, 1);
      if (rewriteOneStep || isTerminatingConfiguration(newTerm, relation) || newTerm == oldTerm /* nothing changed */) break;
      oldTerm = newTerm;
    }

    trace(1, 0, (isTerminatingConfiguration(newTerm, relation) ? "Normal termination on " : "Stuck on ") + render(newTerm) + " after " + rewriteStepCounter
        + " step" + (rewriteStepCounter == 1 ? "" : "s") + " and " + rewriteAttemptCounter + " rewrite attempt" + (rewriteAttemptCounter == 1 ? "" : "s"));

    if (testTerm != 0) {
      if (newTerm == testTerm) {
        System.out.println("Good result");
        goodCount++;
      } else {
        System.out.println("Bad result: expected " + render(testTerm) + " found " + render(newTerm));
        badCount++;
      }
    }
  }

  boolean isTerminatingConfiguration(int term, int relation) {
    int thetaRoot = thetaFromConfiguration(term);
    Set<Integer> terminals = rewriteTerminals.get(relation);
    return iTerms.isSpecialTerm(thetaRoot) || (terminals != null && terminals.contains(thetaRoot));
  }

  public String render(int term) {
    return tt.toString(term, false, defaultDepthLimit, variableMap);
  }

  private void normaliseAndStaticCheckRewriteRules() {
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

  /* End of rewriter *****************************************************************************************/

  private TermTraverserText loadTextTraverser() {
    TermTraverserText ret = new TermTraverserText(iTerms);
    // -1: uncomment these to suppress types have interpreted type renditions
    ret.addEmptyAction("__bool", "__char", "__int32", "__real64", "__string");
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
    ret.addActionBreak("choosePredefinedSet", (Integer t) -> ret.append(ret.childStrippedSymbolString(t, 0)), null, null);

    // 3. Term rewrite pretty print controls
    ret.addEmptyAction("trRule");
    ret.addAction("tr", null, " --- ", null);
    ret.addAction("trPremises", null, "    ", null);
    ret.addActionBreak("trLabel", (Integer t) -> ret.append(iTerms.getTermArity(t) > 0 ? ("-" + ret.childSymbolString(t, 0) + " ") : " "), null, null);
    ret.addAction("trMatch", null, (Integer t) -> ret.append(" |> "), null);
    ret.addEmptyAction("trTransition");
    ret.addActionBreak("TRRELATION", (Integer t) -> ret.append(" " + ret.childStrippedSymbolString(t, 0) + " "), null, null);
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
    tt.append(tt.childStrippedSymbolString(t, 0) + " ");
    if (iTerms.getTermArity(t) == 2) tt.traverse(iTerms.getSubterm(t, 1));
  }

  private TermTraverserText loadLaTeXTraverser() {
    return new TermTraverserText(iTerms);
  }
}
