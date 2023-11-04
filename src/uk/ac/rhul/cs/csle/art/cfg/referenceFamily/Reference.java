package uk.ac.rhul.cs.csle.art.cfg.referenceFamily;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.gll.GLLBaseLine;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.gll.GLLHashPool;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GNode;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.Grammar;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.lexer.LexerLM;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.rdsob.RDSOBExplicitStack;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.rdsob.RDSOBFunction;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.rdsob.RDSOBGenerator;
import uk.ac.rhul.cs.csle.art.term.ITerms;
import uk.ac.rhul.cs.csle.art.term.ITermsLowLevelAPI;

public class Reference {
  private int good = 0;
  private int bad = 0;
  private int inadmissableCount = 0;
  private final int traceLevel = 0;
  private Grammar grammar = null;
  private final ITerms iTerms = new ITermsLowLevelAPI();

  private final GLLBaseLine scriptParser = new GLLBaseLine();
  private final LexerLM scriptLexer = new LexerLM();
  private final String scriptTermString = "cfgRules(cfgRule(cfgLHS(cfgRules), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgRule)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgRule), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgRules)), cfgSlot)), cfgRule(cfgLHS(cfgRule), cfgSeq(cfgSlot, cfgNonterminal(cfgLHS), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\:\\:=')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot)), cfgRule(cfgLHS(cfgLHS), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgAlts), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAlt), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('|')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot)), cfgRule(cfgLHS(cfgAlt), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgEpsilonCarrier), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot, cfgNonterminal(cfgActions), cfgSlot)), cfgRule(cfgLHS(cfgEpsilonCarrier), cfgSeq(cfgSlot, cfgNonterminal(cfgEpsilon), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot)), cfgRule(cfgLHS(cfgAltNoAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSeq)), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgNonterminal(cfgSlot), cfgSlot)), cfgRule(cfgLHS(cfgElems), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgActions), cfgSlot, cfgNonterminal(cfgExtended), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgElems)), cfgSlot)), cfgRule(cfgLHS(cfgExtended), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPrim)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgPrim), cfgSlot, cfgFoldOver(cfgNonterminal(cfgAnnotation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgDoFirst)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgOptional)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgKleene)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgPositive)), cfgSlot)), cfgRule(cfgLHS(cfgAnnotation), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldUnder)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgFoldOver)), cfgSlot)), cfgRule(cfgLHS(cfgFoldUnder), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^')), cfgSlot)), cfgRule(cfgLHS(cfgFoldOver), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^^')), cfgSlot)), cfgRule(cfgLHS(cfgDoFirst), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot)), cfgRule(cfgLHS(cfgOptional), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('?')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('?')), cfgSlot)), cfgRule(cfgLHS(cfgPositive), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('+')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('+')), cfgSlot)), cfgRule(cfgLHS(cfgKleene), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgAlts)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\*')), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAltNoAction), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\*')), cfgSlot)), cfgRule(cfgLHS(cfgPrim), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgNonterminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgCaseSensitiveTerminal)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgBuiltinTerminal)), cfgSlot)), cfgRule(cfgLHS(cfgNonterminal), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgCaseSensitiveTerminal), cfgSeq(cfgSlot, cfgBuiltinTerminal(STRING\\_PLAIN\\_SQ), cfgSlot)), cfgRule(cfgLHS(cfgBuiltinTerminal), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('&')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgEpsilon), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('#')), cfgSlot)), cfgRule(cfgLHS(cfgActions), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgSlot)), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot)), cfgRule(cfgLHS(cfgActionSeq), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgAction), cfgSlot, cfgFoldUnder(cfgNonterminal(cfgActionSeq)), cfgSlot)), cfgRule(cfgLHS(cfgAction), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgEquation)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgAssignment)), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(cfgInsert), cfgSlot)), cfgRule(cfgLHS(cfgEquation), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('=')), cfgSlot, cfgNonterminal(term), cfgSlot)), cfgRule(cfgLHS(cfgAssignment), cfgSeq(cfgSlot, cfgNonterminal(cfgAttribute), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\:=')), cfgSlot, cfgNonterminal(term), cfgSlot)), cfgRule(cfgLHS(cfgInsert), cfgSeq(cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('^+')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgAttribute), cfgSeq(cfgSlot, cfgBuiltinTerminal(ID), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('.')), cfgSlot, cfgBuiltinTerminal(ID), cfgSlot)), cfgRule(cfgLHS(cfgSeq), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), cfgRule(cfgLHS(cfgSlot), cfgSeq(cfgSlot, cfgFoldUnder(cfgEpsilon))), cfgRule(cfgLHS(term), cfgSeq(cfgSlot, cfgFoldOver(cfgNonterminal(cfgAttribute)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(INTEGER)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(REAL)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(STRING\\_DQ)), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot), cfgSeq(cfgSlot, cfgFoldOver(cfgBuiltinTerminal(ID)), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\(')), cfgSlot, cfgNonterminal(arguments), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\)')), cfgSlot)), cfgLHS(arguments), cfgSeq(cfgSlot, cfgNonterminal(term), cfgSlot), cfgSeq(cfgSlot, cfgNonterminal(term), cfgSlot, cfgFoldUnder(cfgCaseSensitiveTerminal('\\,')), cfgSlot, cfgFoldUnder(cfgNonterminal(arguments)), cfgSlot))";

  private ReferenceParser parser = new GLLBaseLine(); // default user parser
  private final LexerLM lexer = new LexerLM(); // default user lexer

  public static void main(String[] args) {
    new Reference(args);
  }

  private int parseScriptFile(String filename) {
    try {
      return parseScript(Files.readString(Paths.get(filename)));
    } catch (IOException e) {
      fatal("unable to open script file " + filename);
    }
    return 0;
  }

  private int parseScript(String specification) {
    scriptParser.inputString = specification;
    scriptParser.accepted = false;

    scriptLexer.lex(scriptParser.inputString, scriptParser.grammar.lexicalKindsArray(), scriptParser.grammar.lexicalStringsArray(),
        scriptParser.grammar.whitespacesArray(), false);
    if (scriptLexer.tokens != null) {
      scriptParser.input = scriptLexer.tokens;
      scriptParser.positions = scriptLexer.positions;
      scriptParser.parse();
    }
    int derivationTerm = scriptParser.accepted ? scriptParser.derivationAsTerm() : 0;

    return derivationTerm;
  }

  public Reference(String[] args) {
    scriptParser.grammar = new Grammar("Reference script grammar", iTerms, iTerms.findTerm(scriptTermString));
    for (String a : args) {
      if (a.endsWith(".art"))
        grammar = new Grammar(a, iTerms, parseScriptFile(a));
      else if (a.indexOf(".") != -1)
        try {
          doTry(a, Files.readString(Paths.get(a)), true, grammar, parser, lexer, false);
        } catch (IOException e) {
          fatal("unable to open string file " + a);
        }

      else
        switch (a) {
        case "!builtinTests":
          builtinTests();
          break;
        case "!torture":
          torture();
          break;
        case "!gllhp":
          parser = new GLLHashPool();
          break;
        case "!gllbl":
          parser = new GLLBaseLine();
          break;
        case "!rdsobgenerator":
          new RDSOBGenerator(grammar, "OSBRDG");
          return;
        case "!rdsobFunction":
          parser = new RDSOBFunction();
          break;
        case "!rdsobExplicitStack":
          parser = new RDSOBExplicitStack();
          break;
        case "!report":
          parser.report(true);
          break;
        case "!visualise":
          // System.out.println(grammar);
          grammar.visualise("grammar.dot");
          GNode.caseSensitiveTerminalStrop = "";
          parser.visualise();
          break;
        case "!statisticsTitle":
          System.out.println(
              "Timestamp,Algorithm,Grammar,String name,String,Characters,Tokens,Outcome,Status,CPUs,Tok/s,Desc,GSS N,GSS E,Pop,SPPF S/I,SPPF P,SPPF E,Ambig,Heap,Heap/tok,Exact,Exact/tok,Tables,Tables/tok,Pool,Pool/tok,H0,H1,H2,H3,H4+");
          break;
        case "!statistics":
          parser.statistics(true);
          break;
        default:
          fatal("unknown option " + a);
        }
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

  private void doTry(String inputStringName, String inputString, boolean outcome, Grammar grammar, ReferenceParser parser, LexerLM lexer,
      boolean suppressOutput) {
    parser.traceLevel = traceLevel;
    parser.inputString = inputString;
    parser.suppressEcho = suppressOutput;
    parser.accepted = false;
    parser.grammar = grammar;
    parser.inputStringName = inputStringName;
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
      // derivationTerm = parser.accepted ? parser.derivationTerm() : 0;
      // System.out.println("Derivation term:\n" + grammar.iTerms.toString(derivationTerm, false, -1, null));
    }
  }

  private void reportSummary() {
    System.out.println("Totals: good: " + good + " bad: " + bad + " inadmissable: " + inadmissableCount);
  }

  private void builtinTests() {
    grammar = new Grammar("a", iTerms, parseScript("S ::= 'a'"));
    doTry("", "a", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    grammar = new Grammar("aa", iTerms, parseScript("S ::= 'a' 'a'"));
    doTry("", "aa", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    grammar = new Grammar("aaa", iTerms, parseScript("S ::= 'a' 'a' 'a'"));
    doTry("", "aaa", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    grammar = new Grammar("a | b", iTerms, parseScript("S ::= 'a' | 'b'"));
    doTry("", "a", true, grammar, parser, lexer, true);
    doTry("", "b", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    grammar = new Grammar("aa | b", iTerms, parseScript("S ::= 'a' 'a' | 'b'"));
    doTry("", "aa", true, grammar, parser, lexer, true);
    doTry("", "b", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aaa | b", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b'"));
    doTry("", "aaa", true, grammar, parser, lexer, true);
    doTry("", "b", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("a | bb", iTerms, parseScript("S ::= 'a' | 'b' 'b'"));
    doTry("", "a", true, grammar, parser, lexer, true);
    doTry("", "bb", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aa | bb", iTerms, parseScript("S ::= 'a' 'a' | 'b' 'b'"));
    doTry("", "aa", true, grammar, parser, lexer, true);
    doTry("", "bb", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aaa | bb", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b' 'b'"));
    doTry("", "aaa", true, grammar, parser, lexer, true);
    doTry("", "bb", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("a | bbb", iTerms, parseScript("S ::= 'a' | 'b' 'b' 'b'"));
    doTry("", "a", true, grammar, parser, lexer, true);
    doTry("", "bbb", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aa | bbb", iTerms, parseScript("S ::= 'a' 'a' | 'b' 'b' 'b'"));
    doTry("", "aa", true, grammar, parser, lexer, true);
    doTry("", "bbb", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aaa | bbb", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' 'b'"));
    doTry("", "aaa", true, grammar, parser, lexer, true);
    doTry("", "bbb", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("a | c", iTerms, parseScript("S ::= 'a' | 'c'"));
    doTry("", "a", true, grammar, parser, lexer, true);
    doTry("", "c", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aa | c", iTerms, parseScript("S ::= 'a' 'a' | 'c'"));
    doTry("", "aa", true, grammar, parser, lexer, true);
    doTry("", "c", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aaa | c", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'c'"));
    doTry("", "aaa", true, grammar, parser, lexer, true);
    doTry("", "c", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("a | b | c", iTerms, parseScript("S ::= 'a' | 'b' | 'c'"));
    doTry("", "a", true, grammar, parser, lexer, true);
    doTry("", "b", true, grammar, parser, lexer, true);
    doTry("", "c", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aa | b | c", iTerms, parseScript("S ::= 'a' 'a' | 'b' | 'c'"));
    doTry("", "aa", true, grammar, parser, lexer, true);
    doTry("", "b", true, grammar, parser, lexer, true);
    doTry("", "c", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aaa | b | c", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b' | 'c'"));
    doTry("", "aaa", true, grammar, parser, lexer, true);
    doTry("", "b", true, grammar, parser, lexer, true);
    doTry("", "c", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("a | bb | c", iTerms, parseScript("S ::= 'a' | 'b' 'b' | 'c'"));
    doTry("", "a", true, grammar, parser, lexer, true);
    doTry("", "bb", true, grammar, parser, lexer, true);
    doTry("", "c", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aa | bb | c", iTerms, parseScript("S ::= 'a' 'a' | 'b' 'b' | 'c'"));
    doTry("", "aa", true, grammar, parser, lexer, true);
    doTry("", "bb", true, grammar, parser, lexer, true);
    doTry("", "c", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aaa | bb | c", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' | 'c'"));
    doTry("", "aaa", true, grammar, parser, lexer, true);
    doTry("", "bb", true, grammar, parser, lexer, true);
    doTry("", "c", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("a | bbb | c", iTerms, parseScript("S ::= 'a' | 'b' 'b' 'b' | 'c'"));
    doTry("", "a", true, grammar, parser, lexer, true);
    doTry("", "bbb", true, grammar, parser, lexer, true);
    doTry("", "c", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aa | bbb | c", iTerms, parseScript("S ::= 'a' 'a' | 'b' 'b' 'b' | 'c'"));
    doTry("", "aa", true, grammar, parser, lexer, true);
    doTry("", "bbb", true, grammar, parser, lexer, true);
    doTry("", "c", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aaa | bbb | c", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' 'b' | 'c'"));
    doTry("", "aaa", true, grammar, parser, lexer, true);
    doTry("", "bbb", true, grammar, parser, lexer, true);
    doTry("", "c", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("a | cc", iTerms, parseScript("S ::= 'a' | 'c' 'c'"));
    doTry("", "a", true, grammar, parser, lexer, true);
    doTry("", "cc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aa | cc", iTerms, parseScript("S ::= 'a' 'a' | 'c' 'c'"));
    doTry("", "aa", true, grammar, parser, lexer, true);
    doTry("", "cc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aaa | cc", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'c' 'c'"));
    doTry("", "aaa", true, grammar, parser, lexer, true);
    doTry("", "cc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("a | b | cc", iTerms, parseScript("S ::= 'a' | 'b' | 'c' 'c'"));
    doTry("", "a", true, grammar, parser, lexer, true);
    doTry("", "b", true, grammar, parser, lexer, true);
    doTry("", "cc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aa | b | cc", iTerms, parseScript("S ::= 'a' 'a' | 'b' | 'c' 'c'"));
    doTry("", "aa", true, grammar, parser, lexer, true);
    doTry("", "b", true, grammar, parser, lexer, true);
    doTry("", "cc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aaa | b | cc", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b' | 'c' 'c'"));
    doTry("", "aaa", true, grammar, parser, lexer, true);
    doTry("", "b", true, grammar, parser, lexer, true);
    doTry("", "cc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("a | bb | cc", iTerms, parseScript("S ::= 'a' | 'b' 'b' | 'c' 'c'"));
    doTry("", "a", true, grammar, parser, lexer, true);
    doTry("", "bb", true, grammar, parser, lexer, true);
    doTry("", "cc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aa | bb | cc", iTerms, parseScript("S ::= 'a' 'a' | 'b' 'b' | 'c' 'c'"));
    doTry("", "aa", true, grammar, parser, lexer, true);
    doTry("", "bb", true, grammar, parser, lexer, true);
    doTry("", "cc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aaa | bb | cc", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' | 'c' 'c'"));
    doTry("", "aaa", true, grammar, parser, lexer, true);
    doTry("", "bb", true, grammar, parser, lexer, true);
    doTry("", "cc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("a | bbb | cc", iTerms, parseScript("S ::= 'a' | 'b' 'b' 'b' | 'c' 'c'"));
    doTry("", "a", true, grammar, parser, lexer, true);
    doTry("", "bbb", true, grammar, parser, lexer, true);
    doTry("", "cc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aa | bbb | cc", iTerms, parseScript("S ::= 'a' 'a' | 'b' 'b' 'b' | 'c' 'c'"));
    doTry("", "aa", true, grammar, parser, lexer, true);
    doTry("", "bbb", true, grammar, parser, lexer, true);
    doTry("", "cc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aaa | bbb | cc", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' 'b' | 'c' 'c'"));
    doTry("", "aaa", true, grammar, parser, lexer, true);
    doTry("", "bbb", true, grammar, parser, lexer, true);
    doTry("", "cc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "ccc", false, grammar, parser, lexer, true);
    grammar = new Grammar("a | ccc", iTerms, parseScript("S ::= 'a' | 'c' 'c' 'c'"));
    doTry("", "a", true, grammar, parser, lexer, true);
    doTry("", "ccc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aa | ccc", iTerms, parseScript("S ::= 'a' 'a' | 'c' 'c' 'c'"));
    doTry("", "aa", true, grammar, parser, lexer, true);
    doTry("", "ccc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aaa | ccc", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'c' 'c' 'c'"));
    doTry("", "aaa", true, grammar, parser, lexer, true);
    doTry("", "ccc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    grammar = new Grammar("a | b | ccc", iTerms, parseScript("S ::= 'a' | 'b' | 'c' 'c' 'c'"));
    doTry("", "a", true, grammar, parser, lexer, true);
    doTry("", "b", true, grammar, parser, lexer, true);
    doTry("", "ccc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aa | b | ccc", iTerms, parseScript("S ::= 'a' 'a' | 'b' | 'c' 'c' 'c'"));
    doTry("", "aa", true, grammar, parser, lexer, true);
    doTry("", "b", true, grammar, parser, lexer, true);
    doTry("", "ccc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aaa | b | ccc", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b' | 'c' 'c' 'c'"));
    doTry("", "aaa", true, grammar, parser, lexer, true);
    doTry("", "b", true, grammar, parser, lexer, true);
    doTry("", "ccc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    grammar = new Grammar("a | bb | ccc", iTerms, parseScript("S ::= 'a' | 'b' 'b' | 'c' 'c' 'c'"));
    doTry("", "a", true, grammar, parser, lexer, true);
    doTry("", "bb", true, grammar, parser, lexer, true);
    doTry("", "ccc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aa | bb | ccc", iTerms, parseScript("S ::= 'a' 'a' | 'b' 'b' | 'c' 'c' 'c'"));
    doTry("", "aa", true, grammar, parser, lexer, true);
    doTry("", "bb", true, grammar, parser, lexer, true);
    doTry("", "ccc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aaa | bb | ccc", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' | 'c' 'c' 'c'"));
    doTry("", "aaa", true, grammar, parser, lexer, true);
    doTry("", "bb", true, grammar, parser, lexer, true);
    doTry("", "ccc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bbb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    grammar = new Grammar("a | bbb | ccc", iTerms, parseScript("S ::= 'a' | 'b' 'b' 'b' | 'c' 'c' 'c'"));
    doTry("", "a", true, grammar, parser, lexer, true);
    doTry("", "bbb", true, grammar, parser, lexer, true);
    doTry("", "ccc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aa | bbb | ccc", iTerms, parseScript("S ::= 'a' 'a' | 'b' 'b' 'b' | 'c' 'c' 'c'"));
    doTry("", "aa", true, grammar, parser, lexer, true);
    doTry("", "bbb", true, grammar, parser, lexer, true);
    doTry("", "ccc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aaa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    grammar = new Grammar("aaa | bbb | ccc", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' 'b' | 'c' 'c' 'c'"));
    doTry("", "aaa", true, grammar, parser, lexer, true);
    doTry("", "bbb", true, grammar, parser, lexer, true);
    doTry("", "ccc", true, grammar, parser, lexer, true);
    doTry("", "", false, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);
    doTry("", "b", false, grammar, parser, lexer, true);
    doTry("", "bb", false, grammar, parser, lexer, true);
    doTry("", "c", false, grammar, parser, lexer, true);
    doTry("", "cc", false, grammar, parser, lexer, true);
    grammar = new Grammar("H", iTerms, parseScript("S ::= # "));
    doTry("", "", true, grammar, parser, lexer, true);
    doTry("", "a", false, grammar, parser, lexer, true);
    doTry("", "aa", false, grammar, parser, lexer, true);

    grammar = new Grammar("deliberatelyBad", iTerms, parseScript("S ::= 'a' 'b' | 'c' 'd'"));
    doTry("", "acd", true, grammar, parser, lexer, true);
    doTry("", "cd", false, grammar, parser, lexer, true);

    reportSummary();
  }

  private void torture() {
    grammar = new Grammar("torture test", iTerms, parseScript("S ::= 'b' | S S | S S S"));
    String str = "b";
    for (int i = 1; i < 100; i++) {
      doTry("", str, true, grammar, parser, lexer, false);
      str += "b";
    }
  }
}
