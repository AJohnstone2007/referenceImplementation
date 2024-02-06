package uk.ac.rhul.cs.csle.art.old.cfg.extract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.core.ARTUncheckedException;

public class ExtractJLS {
  String line;
  int cci;
  String tag, outputPrefix, outputSuffix, mostRecentLHS;
  boolean seenLHS = false, seenOneOf = false;
  Set<String> parseNonterminals = new HashSet<>();
  Set<String> parseTags = new HashSet<>();
  Set<String> lexNonterminals = new HashSet<>();
  Set<String> lexTags = new HashSet<>();
  Set<String> paraterminals;
  PrintWriter extractedWriter;

  public ExtractJLS(String characterClassRulesFilename, String lexRulesFilename, String parseRulesFilename, String extractedFilename, String startSymbol) {

    try {
      extractedWriter = new PrintWriter(extractedFilename + ".art");
    } catch (FileNotFoundException e) {
      throw new ARTUncheckedException("unable to open output file " + extractedFilename + ".art");
    }

    scanCharacterClassRules(characterClassRulesFilename, lexNonterminals);
    scanRules(lexRulesFilename, lexNonterminals, lexTags);
    scanRules(parseRulesFilename, parseNonterminals, parseTags);

    /*
     * Parse level RHS tags minus parse level nonterminals is the union of parse level terminals and paraterminals Intersect that with lexical nonterminals to
     * find paraterminals
     */
    paraterminals = new HashSet<>(parseTags);
    paraterminals.removeAll(parseNonterminals);
    paraterminals.retainAll(lexNonterminals);

    Set<String> dualRules = new HashSet<>(parseNonterminals);
    dualRules.retainAll(lexNonterminals);

    if (dualRules.size() > 0) for (String s : dualRules) {
      System.out.println("** Warning - nonterminal " + s + " is defined in both parser and lexer rules");
      extractedWriter.println("// Warning - nonterminal " + s + " is defined in both parser and lexer rules");
    }

    // System.out.println("Parse tags:" + parseTags);
    // System.out.println("Parse nonterminals:" + parseNonterminals);
    // System.out.println("Lex nonterminals:" + lexNonterminals);
    // System.out.println("Paraterminals:" + paraterminals);

    extractedWriter.println("!start " + startSymbol);
    extractedWriter.println();
    for (String s : paraterminals)
      extractedWriter.println("!paraterminal " + s);
    extractedWriter.println();
    extractedWriter.println("// Extracted lexer rules");
    extractedWriter.println();
    writeExtractedRules(lexRulesFilename, true);
    extractedWriter.println();
    extractedWriter.println("// Extracted parser rules");
    extractedWriter.println();
    writeExtractedRules(parseRulesFilename, false);
    extractedWriter.println();
    extractedWriter.println("// End of extracted rules");

    extractedWriter.close();
    System.out.println("Extracted " + extractedFilename + ".art from " + characterClassRulesFilename + ".raw, " + lexRulesFilename + ".raw" + " and "
        + parseRulesFilename + ".raw with start symbol " + startSymbol);
  }

  private void scanCharacterClassRules(String filename, Set<String> nonterminals) {
    Scanner scanner;
    try {
      scanner = new Scanner(new File(filename + ".raw"));
    } catch (FileNotFoundException e) {
      throw new ARTUncheckedException("unable to open input file " + filename + ".raw");
    }
    while (scanner.hasNextLine()) {
      line = scanner.nextLine();
      // pw.println(line);
      cci = 0;
      String firstTag = getSpaceDelimitedTag();
      // System.out.println("Collected character class rule " + firstTag);
      nonterminals.add(firstTag);
    }
  }

  private void scanRules(String filename, Set<String> nonterminals, Set<String> rhsTags) {
    Scanner scanner;
    try {
      scanner = new Scanner(new File(filename + ".raw"));
    } catch (FileNotFoundException e) {
      throw new ARTUncheckedException("unable to open input file " + filename + ".raw");
    }
    while (scanner.hasNextLine()) {
      line = scanner.nextLine();
      // pw.println(line);
      cci = 0;
      String firstTag = getSpaceDelimitedTag();
      if (firstTag != null && finalCharacter(firstTag) == ':')
        nonterminals.add(subTag(firstTag, 0, 1));
      else
        rhsTags.add(firstTag);
      skipSpace();
      // Now scan the remainder of the line
      while (true) {
        tag = getSpaceDelimitedTag();

        if (tag == null) break; // end of line
        rhsTags.add(tag);
        skipSpace(); // Prepare for next tag
      }
    }
  }

  private void writeExtractedRules(String filename, boolean isLexicalRule) {
    Scanner scanner;

    try {
      scanner = new Scanner(new File(filename + ".raw"));
    } catch (FileNotFoundException e) {
      throw new ARTUncheckedException("unable to open input file " + filename + ".raw");
    }
    while (scanner.hasNextLine()) {
      line = scanner.nextLine();
      cci = 0;

      if (isBlankLine()) {
        extractedWriter.println();
        continue;
      }

      if (line.indexOf('\u00a7') != -1) { // Comment line contains Unicode para symbol
        extractedWriter.println("//" + line);
        continue;
      }

      seenOneOf |= isOneOfLine();
      if (isOneOfLine()) continue; // Skip without output to pw

      // First entry on line is LHS if followed by : and nothing else
      tag = getSpaceDelimitedTag();
      if (tag != null && finalCharacter(tag) == ':') {
        mostRecentLHS = tag.substring(0, tag.length() - (penultimateCharacter(tag) == ':' ? 2 : 1));
        // pw.print("\n" + mostRecentLHS + " ::=\n ");
        extractedWriter.println();
        extractedWriter.println(mostRecentLHS + " ::=");
        extractedWriter.print("  ");

        seenLHS = true;
        seenOneOf = false;
        continue; // next line
      } else
        cci = 0; // Reset to start of line

      if (!seenLHS) extractedWriter.print("| ");
      seenLHS = false;

      boolean first = true;

      while (true) {
        if (testForComment("but not")) break;
        if (mostRecentLHS.equals("EscapeSequence") && testForComment("(")) break;

        tag = getSpaceDelimitedTag();

        if (tag == null) break; // end of line
        skipSpace(); // Prepare for next tag

        // Now analyse for EBNF and terminals

        if (tag.length() != 1) {
          while (tag.length() > 1 && (initialCharacter(tag) == '{' || initialCharacter(tag) == '[')) {
            outputPrefix += "(";
            tag = subTag(tag, 1, 0);
          }

          while (tag.length() > 1 && (finalCharacter(tag) == '}' || finalCharacter(tag) == ']')) { // don't process the leftmost character
            if (finalCharacter(tag) == '}')
              outputSuffix += ")*";
            else
              outputSuffix += ")?";
            tag = subTag(tag, 0, 1);
          }
        }

        if (seenOneOf && !first) outputPrefix += "| ";
        extractedWriter.print(outputPrefix + tagAsARTTerminal(tag, isLexicalRule ? lexNonterminals : parseNonterminals, isLexicalRule) + outputSuffix + " ");
        first = false;
      }
      extractedWriter.println();
    }
  }

  private boolean testForComment(String str) {
    if (line.startsWith(str, cci)) {
      extractedWriter.println("// " + line.substring(cci, line.length()));
      return true;
    }
    return false;
  }

  private String tagAsARTTerminal(String tag, Set<String> nonterminals, boolean isLexicalRule) {
    if (nonterminals.contains(tag) || paraterminals.contains(tag)) return tag;

    if (isLexicalRule) {
      String ret = "";
      for (int i = 0; i < tag.length(); i++) {
        ret += "`" + tag.charAt(i);
        if (tag.charAt(i) == '\\') ret += '\\'; // Special case - escape the escape character
      }
      return ret;
    }
    return "'" + tag + "'";
  }

  private char initialCharacter(String str) {
    if (str.length() < 1) return ' ';
    return str.charAt(0);
  }

  private char finalCharacter(String str) {
    if (str.length() < 1) return ' ';
    return str.charAt(str.length() - 1);
  }

  private char penultimateCharacter(String str) {
    if (str.length() < 2) return ' ';
    return str.charAt(str.length() - 2);
  }

  private String subTag(String tag, int leftOffset, int rightOffset) {
    return tag.substring(leftOffset, tag.length() - rightOffset);
  }

  private boolean isBlankLine() {
    for (int i = 0; i < line.length(); i++)
      if (!Character.isWhitespace(line.charAt(i))) return false;

    return true;
  }

  private boolean isOneOfLine() {
    return line.equals("(one of)");
  }

  private void skipSpace() {
    while (cci < line.length() && Character.isWhitespace(line.charAt(cci)))
      cci++;
  }

  private String getSpaceDelimitedTag() {
    outputPrefix = "";
    outputSuffix = "";
    if (cci >= line.length()) return null;
    String ret = "";

    while (cci < line.length() && !Character.isWhitespace(line.charAt(cci)))
      ret += line.charAt(cci++);

    return ret;
  }
}
