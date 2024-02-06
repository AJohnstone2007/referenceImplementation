package uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily.gll.GLLBL;
import uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily.gll.GLLHP;
import uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily.grammar.GNode;
import uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily.grammar.Grammar;
import uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily.lexer.LexerLM;
import uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily.osbrd.OSBRDE;
import uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily.osbrd.OSBRDF;
import uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily.osbrd.OSBRDGenerator;
import uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily.script.ReferenceGrammarLexer;
import uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily.script.ReferenceGrammarParser;
import uk.ac.rhul.cs.csle.art.old.term.ITerms;
import uk.ac.rhul.cs.csle.art.old.term.ITermsLowLevelAPI;

public class Reference {

  private int good = 0;
  private int bad = 0;
  private int inadmissableCount = 0;
  private final int traceLevel = 0;
  private ReferenceParser parser = new GLLBL();
  private final LexerLM lexer = new LexerLM();
  private String testFilename = null;
  private String testString = null;
  private Grammar grammar = null;
  private final ITerms iTerms = new ITermsLowLevelAPI();

  private int scriptTerm = 0, derivationTerm = 0;

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

  private int parseScript(String input) {
    ReferenceGrammarParser parser = new ReferenceGrammarParser(new ReferenceGrammarLexer()); // Create V3 generated parser

    parser.artParse(input);
    parser.artDisambiguateOrderedLongest();
    if (!parser.artIsInLanguage) {
      fatal("syntax error in grammar specification ");
      System.exit(1);
    }

    if (parser.computeIsAmbiguous(null)) fatal("internal error: specification grammar is ambiguous");
    int ret = parser.artDerivationAsTerm(iTerms);
    System.out.println("Script term:\n" + iTerms.toString(ret, false, -1, null));
    return ret;
  }

  public Reference(String[] args) {
    // interpretedTests();
    // interpretedTestsSmall();
    for (String a : args) {
      String[] parts = a.split("\\.");
      if (parts.length == 2) {
        if (parts[1].equals("art"))
          grammar = new Grammar(a, iTerms, scriptTerm = parseScriptFile(a));
        else {
          testFilename = a;
          try {
            testString = Files.readString(Paths.get(a));
          } catch (IOException e) {
            fatal("unable to open string file " + a);
          }
          parseString(testString, true, grammar, parser, lexer);
        }
      } else
        switch (a) {
        case "!statisticsTitle":
          System.out.println(
              "Timestamp,Algorithm,Grammar,String name,String,Characters,Tokens,Outcome,Status,CPUs,Tok/s,Desc,GSS N,GSS E,Pop,SPPF S/I,SPPF P,SPPF E,Ambig,Heap,Heap/tok,Exact,Exact/tok,Tables,Tables/tok,Pool,Pool/tok,H0,H1,H2,H3,H4+");
          break;
        case "!gllhp":
          parser = new GLLHP();
          break;
        case "!gllbl":
          parser = new GLLBL();
          break;
        case "!osbrdgenerator":
          new OSBRDGenerator(grammar, "OSBRDG");
          return;
        case "!osbrdf":
          parser = new OSBRDF();
          break;
        case "!osbrde":
          parser = new OSBRDE();
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
        case "!statistics":
          parser.statistics(true);
          break;
        case "!compareTerms":
          if (!parser.accepted) {
            System.out.println("Skipping !compareTerms");
            break;
          }
          if (scriptTerm == derivationTerm)
            System.out.println("Script term and derivation term identical ");
          else
            System.out.println("Script term and derivation term different:\n" + "Script term:     " + iTerms.toString(scriptTerm) + "\nDerivation term: "
                + iTerms.toString(derivationTerm));
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
    System.err.println("Fatal error: " + msg);
    System.exit(1);
  }

  private void parseString(String inputString, boolean outcome, Grammar grammar, ReferenceParser parser, LexerLM lexer) {
    parser.traceLevel = traceLevel;
    parser.inputString = inputString;
    parser.accepted = false;
    parser.setGrammar(grammar);
    parser.stringName = testFilename;
    lexer.lex(inputString, grammar.lexicalKindsArray(), grammar.lexicalStringsArray(), grammar.whitespacesArray());
    parser.input = lexer.tokens;
    parser.positions = lexer.positions;
    if (parser.input != null) parser.parse();
    if (parser.inadmissable)
      inadmissableCount++;
    else if (parser.accepted == outcome)
      good++;
    else
      bad++;
    parser.report(outcome);
    derivationTerm = parser.accepted ? parser.derivationTerm() : 0;
    System.out.println("Derivation term:\n" + grammar.iTerms.toString(derivationTerm, false, -1, null));
  }

  private void interpretedTestsSmall() {
    // grammar = new Grammar("ajTest1", iTerms, parseScript("S ::= 'a' 'b' | X X::='c' 'd' "));
    // parseString("cd", true, grammar, parser, lexer);
    //
    // grammar = new Grammar("ajTest2", iTerms, parseScript("S ::= 'b' | 'a' X 'z' X ::= 'x' X | #"));
    // // grammar.visualise("grammar.dot");
    // parseString("b", true, grammar, parser, lexer);
    // parseString("axxxz", true, grammar, parser, lexer);
    // parseString("axxxb", false, grammar, parser, lexer);
    //
    grammar = new Grammar("aLc_HRb", iTerms, parseScript("S ::= 'a' ('c' | #) 'b'  "));
    parseString("ab", true, grammar, parser, lexer);

    reportSummary();
  }

  private void reportSummary() {
    System.out.println("Totals: good: " + good + " bad: " + bad + " inadmissable: " + inadmissableCount);
  }

  private void interpretedTests() {
    grammar = new Grammar("a", iTerms, parseScript("S ::= 'a' "));
    parseString("a", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    grammar = new Grammar("aa", iTerms, parseScript("S ::= 'a' 'a' "));
    parseString("aa", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    grammar = new Grammar("aaa", iTerms, parseScript("S ::= 'a' 'a' 'a' "));
    parseString("aaa", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    grammar = new Grammar("a_b", iTerms, parseScript("S ::= 'a' | 'b' "));
    parseString("a", true, grammar, parser, lexer);
    parseString("b", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    grammar = new Grammar("aa_b", iTerms, parseScript("S ::= 'a' 'a' | 'b' "));
    parseString("aa", true, grammar, parser, lexer);
    parseString("b", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("aaa_b", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b' "));
    parseString("aaa", true, grammar, parser, lexer);
    parseString("b", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("a_bb", iTerms, parseScript("S ::= 'a' | 'b' 'b' "));
    parseString("a", true, grammar, parser, lexer);
    parseString("bb", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("aa_bb", iTerms, parseScript("S ::= 'a' 'a' | 'b' 'b' "));
    parseString("aa", true, grammar, parser, lexer);
    parseString("bb", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("aaa_bb", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' "));
    parseString("aaa", true, grammar, parser, lexer);
    parseString("bb", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("a_bbb", iTerms, parseScript("S ::= 'a' | 'b' 'b' 'b' "));
    parseString("a", true, grammar, parser, lexer);
    parseString("bbb", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("aa_bbb", iTerms, parseScript("S ::= 'a' 'a' | 'b' 'b' 'b' "));
    parseString("aa", true, grammar, parser, lexer);
    parseString("bbb", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("aaa_bbb", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' 'b' "));
    parseString("aaa", true, grammar, parser, lexer);
    parseString("bbb", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("a_c", iTerms, parseScript("S ::= 'a' | 'c' "));
    parseString("a", true, grammar, parser, lexer);
    parseString("c", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("aa_c", iTerms, parseScript("S ::= 'a' 'a' | 'c' "));
    parseString("aa", true, grammar, parser, lexer);
    parseString("c", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("aaa_c", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'c' "));
    parseString("aaa", true, grammar, parser, lexer);
    parseString("c", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("a_b_c", iTerms, parseScript("S ::= 'a' | 'b' | 'c' "));
    parseString("a", true, grammar, parser, lexer);
    parseString("b", true, grammar, parser, lexer);
    parseString("c", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("aa_b_c", iTerms, parseScript("S ::= 'a' 'a' | 'b' | 'c' "));
    parseString("aa", true, grammar, parser, lexer);
    parseString("b", true, grammar, parser, lexer);
    parseString("c", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("aaa_b_c", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b' | 'c' "));
    parseString("aaa", true, grammar, parser, lexer);
    parseString("b", true, grammar, parser, lexer);
    parseString("c", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("a_bb_c", iTerms, parseScript("S ::= 'a' | 'b' 'b' | 'c' "));
    parseString("a", true, grammar, parser, lexer);
    parseString("bb", true, grammar, parser, lexer);
    parseString("c", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("aa_bb_c", iTerms, parseScript("S ::= 'a' 'a' | 'b' 'b' | 'c' "));
    parseString("aa", true, grammar, parser, lexer);
    parseString("bb", true, grammar, parser, lexer);
    parseString("c", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("aaa_bb_c", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' | 'c' "));
    parseString("aaa", true, grammar, parser, lexer);
    parseString("bb", true, grammar, parser, lexer);
    parseString("c", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("a_bbb_c", iTerms, parseScript("S ::= 'a' | 'b' 'b' 'b' | 'c' "));
    parseString("a", true, grammar, parser, lexer);
    parseString("bbb", true, grammar, parser, lexer);
    parseString("c", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("aa_bbb_c", iTerms, parseScript("S ::= 'a' 'a' | 'b' 'b' 'b' | 'c' "));
    parseString("aa", true, grammar, parser, lexer);
    parseString("bbb", true, grammar, parser, lexer);
    parseString("c", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("aaa_bbb_c", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' 'b' | 'c' "));
    parseString("aaa", true, grammar, parser, lexer);
    parseString("bbb", true, grammar, parser, lexer);
    parseString("c", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("a_cc", iTerms, parseScript("S ::= 'a' | 'c' 'c' "));
    parseString("a", true, grammar, parser, lexer);
    parseString("cc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("aa_cc", iTerms, parseScript("S ::= 'a' 'a' | 'c' 'c' "));
    parseString("aa", true, grammar, parser, lexer);
    parseString("cc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("aaa_cc", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'c' 'c' "));
    parseString("aaa", true, grammar, parser, lexer);
    parseString("cc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("a_b_cc", iTerms, parseScript("S ::= 'a' | 'b' | 'c' 'c' "));
    parseString("a", true, grammar, parser, lexer);
    parseString("b", true, grammar, parser, lexer);
    parseString("cc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("aa_b_cc", iTerms, parseScript("S ::= 'a' 'a' | 'b' | 'c' 'c' "));
    parseString("aa", true, grammar, parser, lexer);
    parseString("b", true, grammar, parser, lexer);
    parseString("cc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("aaa_b_cc", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b' | 'c' 'c' "));
    parseString("aaa", true, grammar, parser, lexer);
    parseString("b", true, grammar, parser, lexer);
    parseString("cc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("a_bb_cc", iTerms, parseScript("S ::= 'a' | 'b' 'b' | 'c' 'c' "));
    parseString("a", true, grammar, parser, lexer);
    parseString("bb", true, grammar, parser, lexer);
    parseString("cc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("aa_bb_cc", iTerms, parseScript("S ::= 'a' 'a' | 'b' 'b' | 'c' 'c' "));
    parseString("aa", true, grammar, parser, lexer);
    parseString("bb", true, grammar, parser, lexer);
    parseString("cc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("aaa_bb_cc", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' | 'c' 'c' "));
    parseString("aaa", true, grammar, parser, lexer);
    parseString("bb", true, grammar, parser, lexer);
    parseString("cc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("a_bbb_cc", iTerms, parseScript("S ::= 'a' | 'b' 'b' 'b' | 'c' 'c' "));
    parseString("a", true, grammar, parser, lexer);
    parseString("bbb", true, grammar, parser, lexer);
    parseString("cc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("aa_bbb_cc", iTerms, parseScript("S ::= 'a' 'a' | 'b' 'b' 'b' | 'c' 'c' "));
    parseString("aa", true, grammar, parser, lexer);
    parseString("bbb", true, grammar, parser, lexer);
    parseString("cc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("aaa_bbb_cc", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' 'b' | 'c' 'c' "));
    parseString("aaa", true, grammar, parser, lexer);
    parseString("bbb", true, grammar, parser, lexer);
    parseString("cc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("ccc", false, grammar, parser, lexer);
    grammar = new Grammar("a_ccc", iTerms, parseScript("S ::= 'a' | 'c' 'c' 'c' "));
    parseString("a", true, grammar, parser, lexer);
    parseString("ccc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    grammar = new Grammar("aa_ccc", iTerms, parseScript("S ::= 'a' 'a' | 'c' 'c' 'c' "));
    parseString("aa", true, grammar, parser, lexer);
    parseString("ccc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    grammar = new Grammar("aaa_ccc", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'c' 'c' 'c' "));
    parseString("aaa", true, grammar, parser, lexer);
    parseString("ccc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    grammar = new Grammar("a_b_ccc", iTerms, parseScript("S ::= 'a' | 'b' | 'c' 'c' 'c' "));
    parseString("a", true, grammar, parser, lexer);
    parseString("b", true, grammar, parser, lexer);
    parseString("ccc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    grammar = new Grammar("aa_b_ccc", iTerms, parseScript("S ::= 'a' 'a' | 'b' | 'c' 'c' 'c' "));
    parseString("aa", true, grammar, parser, lexer);
    parseString("b", true, grammar, parser, lexer);
    parseString("ccc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    grammar = new Grammar("aaa_b_ccc", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b' | 'c' 'c' 'c' "));
    parseString("aaa", true, grammar, parser, lexer);
    parseString("b", true, grammar, parser, lexer);
    parseString("ccc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    grammar = new Grammar("a_bb_ccc", iTerms, parseScript("S ::= 'a' | 'b' 'b' | 'c' 'c' 'c' "));
    parseString("a", true, grammar, parser, lexer);
    parseString("bb", true, grammar, parser, lexer);
    parseString("ccc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    grammar = new Grammar("aa_bb_ccc", iTerms, parseScript("S ::= 'a' 'a' | 'b' 'b' | 'c' 'c' 'c' "));
    parseString("aa", true, grammar, parser, lexer);
    parseString("bb", true, grammar, parser, lexer);
    parseString("ccc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    grammar = new Grammar("aaa_bb_ccc", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' | 'c' 'c' 'c' "));
    parseString("aaa", true, grammar, parser, lexer);
    parseString("bb", true, grammar, parser, lexer);
    parseString("ccc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bbb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    grammar = new Grammar("a_bbb_ccc", iTerms, parseScript("S ::= 'a' | 'b' 'b' 'b' | 'c' 'c' 'c' "));
    parseString("a", true, grammar, parser, lexer);
    parseString("bbb", true, grammar, parser, lexer);
    parseString("ccc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    grammar = new Grammar("aa_bbb_ccc", iTerms, parseScript("S ::= 'a' 'a' | 'b' 'b' 'b' | 'c' 'c' 'c' "));
    parseString("aa", true, grammar, parser, lexer);
    parseString("bbb", true, grammar, parser, lexer);
    parseString("ccc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aaa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    grammar = new Grammar("aaa_bbb_ccc", iTerms, parseScript("S ::= 'a' 'a' 'a' | 'b' 'b' 'b' | 'c' 'c' 'c' "));
    parseString("aaa", true, grammar, parser, lexer);
    parseString("bbb", true, grammar, parser, lexer);
    parseString("ccc", true, grammar, parser, lexer);
    parseString("", false, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);
    parseString("b", false, grammar, parser, lexer);
    parseString("bb", false, grammar, parser, lexer);
    parseString("c", false, grammar, parser, lexer);
    parseString("cc", false, grammar, parser, lexer);
    grammar = new Grammar("H", iTerms, parseScript("S ::= # "));
    parseString("", true, grammar, parser, lexer);
    parseString("a", false, grammar, parser, lexer);
    parseString("aa", false, grammar, parser, lexer);

    grammar = new Grammar("deliberatelyBad", iTerms, parseScript("S ::= 'a' 'b' | 'c' 'd' "));
    parseString("acd", true, grammar, parser, lexer);
    parseString("cd", false, grammar, parser, lexer);

    // grammar = new Grammar("aLHR", iTerms, parseScript("S ::= 'a' (#) "));
    // parseString("a", true, grammar, parser, lexer);
    // parseString("", false, grammar, parser, lexer);
    // parseString("aa", false, grammar, parser, lexer);
    // grammar = new Grammar("aLHRb", iTerms, parseScript("S ::= 'a' (#) 'b' "));
    // parseString("ab", true, grammar, parser, lexer);
    // parseString("", false, grammar, parser, lexer);
    // parseString("aa", false, grammar, parser, lexer);
    // parseString("abb", false, grammar, parser, lexer);
    // grammar = new Grammar("aLc_HRb", iTerms, parseScript("S ::= 'a' ('c' | #) 'b' "));
    // parseString("ab", true, grammar, parser, lexer);
    // parseString("acb", true, grammar, parser, lexer);
    // parseString("", false, grammar, parser, lexer);
    // parseString("aa", false, grammar, parser, lexer);
    // parseString("ac", false, grammar, parser, lexer);
    // parseString("abb", false, grammar, parser, lexer);
    // grammar = new Grammar("SLaSR_H", iTerms, parseScript("S::= S ('a' S) | # "));
    // tryString("a", true, grammar, parser, lexer);
    // tryString("aa", true, grammar, parser, lexer);
    // tryString("", true, grammar, parser, lexer);
    // tryString("", true, grammar, parser, lexer);
    reportSummary();
  }
}
