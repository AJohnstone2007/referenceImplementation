package uk.ac.rhul.cs.csle.art.cfg.referenceFamily;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

public class ReferenceScriptInterpreter {
  private final int traceLevel = 0;
  private final ITerms iTerms;
  private final GLLBaseLine scriptParser = new GLLBaseLine();
  private final LexerLM scriptLexer = new LexerLM();
//@formatter:off
  private final String scriptParserTermString = "text(directive(whitespace(cfgBuiltinTerminal(SIMPLE\\_WHITESPACE), cfgBuiltinTerminal(COMMENT\\_NEST\\_ART), cfgBuiltinTerminal(COMMENT\\_LINE\\_C))), cfgRule(cfgLHS(text), cfgSeq(cfgSlot, cfgNonterminal(directive), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(directive), cfgSlot, cfgFoldUnder(cfgNonterminal(text)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgRule), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgRule), cfgSlot, cfgFoldUnder(cfgNonterminal(text)), cfgSlot)), cfgRule(cfgLHS(directive), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('!')), cfgSlot, cfgNonterminal(term), cfgSlot)), cfgRule(cfgLHS(cfgRule), cfgSeq(cfgSlot, cfgNonterminal(cfgLHS), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\:\\:=')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot)), cfgRule(cfgLHS(cfgLHS), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgAlts), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('|')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot)), cfgRule(cfgLHS(cfgAlt), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilonCarrier), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot)), cfgRule(cfgLHS(cfgEpsilonCarrier), cfgSeq(cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot)), cfgRule(cfgLHS(cfgAltNoAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot)), cfgRule(cfgLHS(cfgElems), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot)), cfgRule(cfgLHS(cfgExtended), cfgSeq(cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPrim)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgOptional)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgKleene)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPositive)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgDoFirst)), cfgSlot)), cfgRule(cfgLHS(cfgAnnotation), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldUnder)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldOver)), cfgSlot)), cfgRule(cfgLHS(cfgFoldUnder), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^')), cfgSlot)), cfgRule(cfgLHS(cfgFoldOver), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^^')), cfgSlot)), cfgRule(cfgLHS(cfgDoFirst), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot)), cfgRule(cfgLHS(cfgOptional), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('?')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('?')), cfgSlot)), cfgRule(cfgLHS(cfgPositive), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('+')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('+')), cfgSlot)), cfgRule(cfgLHS(cfgKleene), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\*')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\*')), cfgSlot)), cfgRule(cfgLHS(cfgPrim), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgNonterminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgCaseSensitiveTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgBuiltinTerminal)), cfgSlot)), cfgRule(cfgLHS(cfgNonterminal), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgCaseSensitiveTerminal), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_PLAIN\\_SQ), cfgSlot)), cfgRule(cfgLHS(cfgBuiltinTerminal), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('&')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgEpsilon), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('#')), cfgSlot)), cfgRule(cfgLHS(cfgActions), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot)), cfgRule(cfgLHS(cfgActionSeq), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot)), cfgRule(cfgLHS(cfgAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgEquation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgAssignment)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgInsert), cfgSlot)), cfgRule(cfgLHS(cfgEquation), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('=')), cfgSlot, cfgNonterminal(term), cfgSlot)), cfgRule(cfgLHS(cfgAssignment), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\:=')), cfgSlot, cfgNonterminal(term), cfgSlot)), cfgRule(cfgLHS(cfgInsert), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^+')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgAttribute), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('.')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgSeq), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), cfgRule(cfgLHS(cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), cfgRule(cfgLHS(term), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgAttribute)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(INTEGER)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(REAL)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_DQ)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgBuiltinTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(arguments)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(ID)), cfgSlot)), cfgRule(cfgLHS(arguments), cfgSeq(cfgSlot, cfgNonterminal(term), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(term), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgFoldUnder(cfgNonterminal(arguments)), cfgSlot)))  ";
//@formatter:on
  private final int scriptParserTerm;
  private final TermTraverser scriptTraverser;

  private ReferenceParser workingParser = new GLLBaseLine(); // default user parser
  private final LexerLM workingLexer = new LexerLM(); // default user lexer
  private final Grammar workingGrammar; // scriptTraverser builds grammar rules into this grammar
  private int workingDerivationTerm = 0;

  private int goodCount = 0;
  private int badCount = 0;
  private int inadmissableCount = 0;

  ReferenceScriptInterpreter(ITerms iTerms) {
    this.iTerms = iTerms;
    scriptTraverser = new TermTraverser(iTerms);
    initialiseScriptTraverser();
    scriptParserTerm = iTerms.findTerm(scriptParserTermString);

    // Bootstrap in action: build the script parser's grammar by traversing the scriptParserTerm and normalising, then making the result the script parser
    workingGrammar = new Grammar("Script grammar", iTerms);
    scriptTraverser.traverse(scriptParserTerm); // Construct the script parser grammar by walking the script parser term from the last bootstrap
    workingGrammar.normalise();
    scriptParser.grammar = workingGrammar;
  }

  void initialiseScriptTraverser() { // Initialise scriptTraverser
    scriptTraverser.addActionBreak("directive", (Integer t) -> directiveAction(t), null, null);

    scriptTraverser.addActionBreak("cfgLHS", (Integer t) -> workingGrammar.lhsAction(childSymbolString(t)), null, null);
    scriptTraverser.addAction("cfgSeq", (Integer t) -> workingGrammar.altAction(), null, (Integer t) -> workingGrammar.endAction(""));

    scriptTraverser.addAction("cfgEpsilon", (Integer t) -> workingGrammar.updateWorkingNode(GrammarKind.EPS, "#"), null, null);
    scriptTraverser.addActionBreak("cfgNonterminal", (Integer t) -> workingGrammar.updateWorkingNode(GrammarKind.N, childSymbolString(t)), null, null);
    scriptTraverser.addActionBreak("cfgCaseSensitiveTerminal", (Integer t) -> workingGrammar.updateWorkingNode(GrammarKind.T, childSymbolStringStrip(t)), null,
        null);
    scriptTraverser.addActionBreak("cfgBuiltinTerminal", (Integer t) -> workingGrammar.updateWorkingNode(GrammarKind.B, childSymbolString(t)), null, null);

    scriptTraverser.addAction("cfgDoFirst", (Integer t) -> workingGrammar.updateWorkingNode(GrammarKind.DO, ""), null, null);
    scriptTraverser.addAction("cfgOptional", (Integer t) -> workingGrammar.updateWorkingNode(GrammarKind.OPT, ""), null, null);
    scriptTraverser.addAction("cfgKleene", (Integer t) -> workingGrammar.updateWorkingNode(GrammarKind.KLN, ""), null, null);
    scriptTraverser.addAction("cfgPositive", (Integer t) -> workingGrammar.updateWorkingNode(GrammarKind.POS, ""), null, null);

    scriptTraverser.addAction("cfgFoldNone", (Integer t) -> workingGrammar.workingFold = GIFTKind.NONE, null, null);
    scriptTraverser.addAction("cfgFoldUnder", (Integer t) -> workingGrammar.workingFold = GIFTKind.UNDER, null, null);
    scriptTraverser.addAction("cfgFoldOver", (Integer t) -> workingGrammar.workingFold = GIFTKind.OVER, null, null);
    scriptTraverser.addAction("cfgFoldTear", (Integer t) -> workingGrammar.workingFold = GIFTKind.TEAR, null, null);

    scriptTraverser.addAction("cfgSlot", (Integer t) -> workingGrammar.workingAction = t, null, null);
  }

  public void directiveAction(int term) {
    // System.out.println("Processing " + iTerms.toString(term));
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
          Util.fatal("Unexpected !whitespace element " + iTerms.toString(iTerms.getSubterm(term, 0, i, 0)));
          }
      break;
    // case "adl":
    // if (workingDerivationTerm == 0) break;
    // ADL adl = new ADL(iTerms);
    // __mapChain env = new __mapChain();
    // Value result = iTerms.valueBottom;
    // try {
    // result = adl.interpret(workingDerivationTerm, env);
    // } catch (ValueException e) {
    // System.out.println("Value exception: " + e.getMessage());
    // }
    //
    // System.out.println("ADL interpreter returns " + result + " with environment " + env);
    // break;
    case "builtinTests":
      builtinTests();
      break;
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
        default:
          Util.fatal("Unexpected !parser argument " + iTerms.toString(iTerms.getSubterm(term, 0, i)));
        }
      break;

    case "report":
      workingParser.report(true);
      break;
    case "show":
      for (int i = 0; i < iTerms.getTermArity(iTerms.getSubterm(term, 0)); i++)
        switch (iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, i))) {
        case "grammar":
          workingGrammar.normalise();
          workingGrammar.show("grammar.dot");
          break;
        }
      GrammarNode.caseSensitiveTerminalStrop = "";
      workingParser.show();
      break;
    case "print":
      for (int i = 0; i < iTerms.getTermArity(iTerms.getSubterm(term, 0)); i++)
        switch (iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, i))) {
        case "grammar":
          workingGrammar.normalise();
          System.out.println("Working Grammar: " + workingGrammar);
          break;

        case "derivation":
          // System.out.println("Working derivation term: [" + workingDerivationTerm + "]\n" + iTerms.toString(workingDerivationTerm, false, -1, null));
          System.out.println("Working derivation term: [" + workingDerivationTerm + "]\n" + iTerms.toString(workingDerivationTerm, true, -1, null));
          if (scriptParserTerm == workingDerivationTerm) System.out.println("Bootstrap achieved: script parser term and working derivation term identical");
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
      workingParser.statistics(true);
      break;
    case "try":
      String inputString = "a b";
      String tryOp = iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, 0));
      String tryOperand = iTerms.getTermSymbolString(iTerms.getSubterm(term, 0, 0, 0));
      // System.out.println("try " + tryOp + " on " + tryOperand);
      if (tryOp.equals("file")) try {
        inputString = Files.readString(Paths.get(strip(tryOperand)));
      } catch (IOException e) {
        Util.fatal("Unable to open try file '" + tryOperand);
      }
      workingDerivationTerm = doTry("try", inputString, true, workingGrammar, workingParser, workingLexer, false);
      break;
    case "bootstrapTest":
      // Add code to test bootstrapTest.art against itself
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
    // lexer.report();
    parser.input = lexer.tokens;
    parser.positions = lexer.positions;
    if (parser.input != null) parser.parse();
    if (parser.inadmissable)
      inadmissableCount++;
    else if (parser.accepted == outcome)
      goodCount++;
    else
      badCount++;
    if (!suppressOutput) if (parser.accepted) {
      parser.chooseLongestMatch();
      parser.selectFirst();
      return parser.derivationAsTerm();
    }
    return 0;
  }

  private void builtinTests() {
    Grammar workingGrammar;
    workingGrammar = new Grammar("a", parseScript("S ::= 'a'"));
    doTry("", "a", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aa", parseScript("S ::= 'a' 'a'"));
    doTry("", "aa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aaa", parseScript("S ::= 'a' 'a' 'a'"));
    doTry("", "aaa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("a | b", parseScript("S ::= 'a' | 'b'"));
    doTry("", "a", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aa | b", parseScript("S ::= 'a' 'a' | 'b'"));
    doTry("", "aa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aaa | b", parseScript("S ::= 'a' 'a' 'a' | 'b'"));
    doTry("", "aaa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("a | bb", parseScript("S ::= 'a' | 'b' 'b'"));
    doTry("", "a", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aa | bb", parseScript("S ::= 'a' 'a' | 'b' 'b'"));
    doTry("", "aa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aaa | bb", parseScript("S ::= 'a' 'a' 'a' | 'b' 'b'"));
    doTry("", "aaa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("a | bbb", parseScript("S ::= 'a' | 'b' 'b' 'b'"));
    doTry("", "a", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aa | bbb", parseScript("S ::= 'a' 'a' | 'b' 'b' 'b'"));
    doTry("", "aa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aaa | bbb", parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' 'b'"));
    doTry("", "aaa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("a | c", parseScript("S ::= 'a' | 'c'"));
    doTry("", "a", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aa | c", parseScript("S ::= 'a' 'a' | 'c'"));
    doTry("", "aa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aaa | c", parseScript("S ::= 'a' 'a' 'a' | 'c'"));
    doTry("", "aaa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("a | b | c", parseScript("S ::= 'a' | 'b' | 'c'"));
    doTry("", "a", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aa | b | c", parseScript("S ::= 'a' 'a' | 'b' | 'c'"));
    doTry("", "aa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aaa | b | c", parseScript("S ::= 'a' 'a' 'a' | 'b' | 'c'"));
    doTry("", "aaa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("a | bb | c", parseScript("S ::= 'a' | 'b' 'b' | 'c'"));
    doTry("", "a", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aa | bb | c", parseScript("S ::= 'a' 'a' | 'b' 'b' | 'c'"));
    doTry("", "aa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aaa | bb | c", parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' | 'c'"));
    doTry("", "aaa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("a | bbb | c", parseScript("S ::= 'a' | 'b' 'b' 'b' | 'c'"));
    doTry("", "a", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aa | bbb | c", parseScript("S ::= 'a' 'a' | 'b' 'b' 'b' | 'c'"));
    doTry("", "aa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aaa | bbb | c", parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' 'b' | 'c'"));
    doTry("", "aaa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("a | cc", parseScript("S ::= 'a' | 'c' 'c'"));
    doTry("", "a", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aa | cc", parseScript("S ::= 'a' 'a' | 'c' 'c'"));
    doTry("", "aa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aaa | cc", parseScript("S ::= 'a' 'a' 'a' | 'c' 'c'"));
    doTry("", "aaa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("a | b | cc", parseScript("S ::= 'a' | 'b' | 'c' 'c'"));
    doTry("", "a", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aa | b | cc", parseScript("S ::= 'a' 'a' | 'b' | 'c' 'c'"));
    doTry("", "aa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aaa | b | cc", parseScript("S ::= 'a' 'a' 'a' | 'b' | 'c' 'c'"));
    doTry("", "aaa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("a | bb | cc", parseScript("S ::= 'a' | 'b' 'b' | 'c' 'c'"));
    doTry("", "a", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aa | bb | cc", parseScript("S ::= 'a' 'a' | 'b' 'b' | 'c' 'c'"));
    doTry("", "aa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aaa | bb | cc", parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' | 'c' 'c'"));
    doTry("", "aaa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("a | bbb | cc", parseScript("S ::= 'a' | 'b' 'b' 'b' | 'c' 'c'"));
    doTry("", "a", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aa | bbb | cc", parseScript("S ::= 'a' 'a' | 'b' 'b' 'b' | 'c' 'c'"));
    doTry("", "aa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aaa | bbb | cc", parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' 'b' | 'c' 'c'"));
    doTry("", "aaa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("a | ccc", parseScript("S ::= 'a' | 'c' 'c' 'c'"));
    doTry("", "a", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aa | ccc", parseScript("S ::= 'a' 'a' | 'c' 'c' 'c'"));
    doTry("", "aa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aaa | ccc", parseScript("S ::= 'a' 'a' 'a' | 'c' 'c' 'c'"));
    doTry("", "aaa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("a | b | ccc", parseScript("S ::= 'a' | 'b' | 'c' 'c' 'c'"));
    doTry("", "a", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aa | b | ccc", parseScript("S ::= 'a' 'a' | 'b' | 'c' 'c' 'c'"));
    doTry("", "aa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aaa | b | ccc", parseScript("S ::= 'a' 'a' 'a' | 'b' | 'c' 'c' 'c'"));
    doTry("", "aaa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("a | bb | ccc", parseScript("S ::= 'a' | 'b' 'b' | 'c' 'c' 'c'"));
    doTry("", "a", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aa | bb | ccc", parseScript("S ::= 'a' 'a' | 'b' 'b' | 'c' 'c' 'c'"));
    doTry("", "aa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aaa | bb | ccc", parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' | 'c' 'c' 'c'"));
    doTry("", "aaa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("a | bbb | ccc", parseScript("S ::= 'a' | 'b' 'b' 'b' | 'c' 'c' 'c'"));
    doTry("", "a", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aa | bbb | ccc", parseScript("S ::= 'a' 'a' | 'b' 'b' 'b' | 'c' 'c' 'c'"));
    doTry("", "aa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aaa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("aaa | bbb | ccc", parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' 'b' | 'c' 'c' 'c'"));
    doTry("", "aaa", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bbb", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "ccc", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "b", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "bb", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "c", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cc", false, workingGrammar, workingParser, workingLexer, true);
    workingGrammar = new Grammar("H", parseScript("S ::= # "));
    doTry("", "", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "a", false, workingGrammar, workingParser, workingLexer, true);
    doTry("", "aa", false, workingGrammar, workingParser, workingLexer, true);

    workingGrammar = new Grammar("deliberatelyBad", parseScript("S ::= 'a' 'b' | 'c' 'd'"));
    doTry("", "acd", true, workingGrammar, workingParser, workingLexer, true);
    doTry("", "cd", false, workingGrammar, workingParser, workingLexer, true);

    System.out.println("Totals: good: " + goodCount + " bad: " + badCount + " inadmissable: " + inadmissableCount);
  }

  private ITerms parseScript(String string) {
    // TODO Auto-generated method stub
    return null;
  }

  public void interpret(String scriptString) {
    // Lex the script string
    scriptParser.inputString = scriptString;
    scriptParser.accepted = false;
    scriptLexer.lex(scriptParser.inputString, scriptParser.grammar.lexicalKindsArray(), scriptParser.grammar.lexicalStringsArray(),
        scriptParser.grammar.whitespacesArray(), false);

    if (scriptLexer.tokens == null) Util.fatal("Script lexical error");

    // Parse the script lexicalisation
    scriptParser.input = scriptLexer.tokens;
    scriptParser.positions = scriptLexer.positions;
    scriptParser.parse();
    scriptParser.selectFirst();

    int scriptTerm = scriptParser.accepted ? scriptParser.derivationAsTerm() : 0;

    // System.out.println("Script term:\n" + iTerms.toString(scriptTerm, true, -1, null));
    if (scriptTerm == 0) Util.fatal("Script syntax error");

    scriptTraverser.traverse(scriptTerm);
  }
}
