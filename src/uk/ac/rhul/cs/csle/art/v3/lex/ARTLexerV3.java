package uk.ac.rhul.cs.csle.art.v3.lex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.rhul.cs.csle.art.core.ARTUncheckedException;
import uk.ac.rhul.cs.csle.art.core.OLDDirectives;
import uk.ac.rhul.cs.csle.art.util.bitset.ARTBitSet;
import uk.ac.rhul.cs.csle.art.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.v3.alg.ARTParserBase;
import uk.ac.rhul.cs.csle.art.v3.alg.gll.support.ARTGLLParserBase;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.element.ARTGrammarElement;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.element.ARTGrammarElementTerminal;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.element.ARTGrammarElementTerminalBuiltin;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.element.ARTGrammarElementTerminalCaseInsensitive;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.element.ARTGrammarElementTerminalCaseSensitive;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.element.ARTGrammarElementTerminalCharacter;

public class ARTLexerV3 {
  /******************************************************************************
   *
   * 1. Data
   *
   ******************************************************************************/
  private ARTGrammar grammar; // The grammar containing the lexical definitions
  protected ARTParserBase artParser;

  public OLDDirectives artDirectives;

  private final int paraterminalCount; // the number of distinct tokens (paraterminals) for the grammar we are processing
  public String artInputString; // The input as a String
  public char[] artInput; // The input as an array of characters zero terminated for compatability with C-style strings
  public int artInputLength; // length of input in characters
  public int artInputIndex; // Index into character string input used by lexers
  public int artLexemeLeftIndex; // During longest match lexing, remember start of lexeme
  public int artLexemeRightIndex; // During longest match lexing, remember start of lexeme
  private int startOffset; // The index of the first nonwhitespace terminal in the string
  private int eosWhitespacePrefix; // The length of the whitespace prefix of the the terminating eos

  private String[] labelStrings;

  public ARTTWEPairSet tweSet[] = null;
  private int overDegree[];
  private boolean touched[];
  public long chooserMillis;

  // The priority and longer relations represented as boolean vectors
  private ARTBitSet[] higher;
  private ARTBitSet[] longer;
  private ARTBitSet[] shorter;

  // Data for instumentation of TWE set behaviour
  private Set<ArrayList<Integer>> lexicalisations = new HashSet<>();
  private int[] segmentLexicalisationCardinalities;
  private int[] segmentLexicalisationLengths;
  private BigInteger[] segmentLexicalisationProductBars;
  public int artLexicalisationCount; // used by quick version

  private final static Set<String> validBuiltins = new HashSet<>(Arrays.asList("CHAR_BQ", "COMMENT_BLOCK_C", "COMMENT_LINE_C", "COMMENT_NEST_ART", "ID", "ID_A",
      "ID_AN", "ID_SOS", "INTEGER", "SIGNED_INTEGER", "REAL", "SIGNED_REAL", "STRING_BRACE", "STRING_BRACE_NEST", "STRING_BRACKET", "STRING_BRACKET_NEST",
      "STRING_DOLLAR", "STRING_DQ", "STRING_PLAIN_SQ", "STRING_SQ", "SIMPLE_WHITESPACE", "SML_COMMENT", "SML_D", "SML_INT", "SML_WORD", "SML_REAL", "SML_CHAR",
      "SML_STRING", "SML_VID", "SML_TYVAR", "SML_TYCON", "SML_LAB", "SML_STRID", "SML_SYMID"));

  /******************************************************************************
   *
   * 2. Constructors and initialisation functions
   *
   ******************************************************************************/
  public ARTLexerV3() { // Dummy constructor to satisfy generated ARTLexer
    paraterminalCount = 0;
  }

  // This constructor is called from interpreted parsers with option block paramaters
  public ARTLexerV3(ARTGrammar grammar) {
    this.grammar = grammar;
    this.artDirectives = grammar.artDirectives;
    this.higher = grammar.getChooserSet("").higher;
    this.longer = grammar.getChooserSet("").longer;
    this.shorter = grammar.getChooserSet("").shorter;
    this.paraterminalCount = grammar.getLastTerminalElementNumber() + 1;
  }

  // This constructor is called from generated parsers with explicit parameters
  public ARTLexerV3(ARTBitSet[] higher, ARTBitSet[] longer, String inputString, int inputStringLength, int eos, String[] labelStrings,
      OLDDirectives artDirectives, int paraterminalCount) {
    this.paraterminalCount = paraterminalCount;
    this.artDirectives = artDirectives;
    this.labelStrings = labelStrings;
    this.artInputLength = inputStringLength;
    if (inputString != null) artLoadInputArray(inputString);

    // Build empty TWE set from the left
    tweSet = new ARTTWEPairSet[artInputLength + 2]; // Rightmost position contains $ (zero) and then we need an empty position for successor
    touched = new boolean[tweSet.length];
    this.grammar = null;
    this.higher = higher;
    this.longer = longer;
  }

  public void ARTLexerV3Wrapup(int index) {
    eosWhitespacePrefix = tweSet.length - index;
    // if (tweSet != null) tweSetUpdateExactMakeLeftSet(0, index, tweSet.length); // Add terminating EOS
  }

  public void artSetParser(ARTParserBase artGeneratedParser) {
    this.artParser = artGeneratedParser;
  }

  public void artLoadInputArray(String inputString) {
    // Build artInput as char[]
    this.artInput = new char[inputString.length() + 2]; // Rightmost position contains $ (zero) and then we need an empty position for successor
    for (int i = 0; i < inputString.length(); i++)
      artInput[i] = inputString.charAt(i);

    artInputLength = inputString.length();
  }

  /******************************************************************************
   *
   * 3. Overload hooks and support routines for generated lexers which extend this class
   *
   ******************************************************************************/
  public int artLongestLength; // Used during longest match processing to remember the longest lexeme seen so far
  public int artLongestToken; // Used during longest match processing to remember a token which has the longest lexeme seen so far

  protected void artLexicalisePreparseWhitespaceInstances() {
  };

  protected void artLexicaliseBuiltinInstances() {
  };

  public void artLexicalisePreparseWhitespace() {
    // System.out.printf("Preparse whitespace entered at index %d", artInputIndex);
    // if (artInputIndex < artInputLength - 5) System.out.printf(" - \"%c%c%c%c%c...", artInput[artInputIndex + 0], artInput[artInputIndex + 1],
    // artInput[artInputIndex + 2], artInput[artInputIndex + 3], artInput[artInputIndex + 4]);
    // System.out.println();
    int start;
    do {
      if (artPeekCh() == '\0') break;
      start = artInputIndex;

      artLexicalisePreparseWhitespaceInstances();

    } while (artInputIndex != start);

    // System.out.printf("Preparse whitespace matched \"%s\"\n", artInputString.substring(startOrg, artInputIndex));
  }

  protected void artLexicaliseTest(int token) {
    if (artInputIndex > artLexemeRightIndex) {
      artLexemeRightIndex = artInputIndex;
      artLongestToken = token;
    }
    artInputIndex = artLexemeLeftIndex; // reset input pointer
  }

  protected void artUpdateLongestLength(int length, int tokenIndex) {
    if (length > artLongestLength) {
      artLongestLength = length;
      artLongestToken = tokenIndex;
    }
  }

  // This method is called by the 'old' GLL interface in ARTGLLParserBase
  // It attempts to match all of the constant terminals then all of the built in terminals, and it returns only longest one
  public void artMatchLongestRaw() {
    artLexemeRightIndex = artLexemeLeftIndex = artInputIndex;

    if (artPeekCh() == '\0') {
      artLexemeRightIndex = artInputLength;
      artLongestToken = ARTGLLParserBase.ARTL_EOS;
      artInputIndex = artLexemeRightIndex;
      return;
    }

    // In this loop, artCharacterStringInputIndex does not advance becase we are using regionMatches
    for (int tokenIndex = artParser.artFirstTerminalLabel; tokenIndex < ARTGLLParserBase.ARTL_EPSILON; tokenIndex++) {
      if (!artInputString.regionMatches(artParser.artTerminalCaseInsensitive[tokenIndex], artInputIndex, artParser.artLabelStrings[tokenIndex], 0,
          artParser.artLabelStrings[tokenIndex].length()))
        continue;
      artLexemeRightIndex = artInputIndex + artParser.artLabelStrings[tokenIndex].length();
      artLongestToken = tokenIndex;
    }

    // Now try the builtins
    artLexicaliseBuiltinInstances();
    if (artLexemeLeftIndex == artLexemeRightIndex) throw new ARTUncheckedException(
        (ARTText.echo("Lexical error: unexpected character '" + artInput[artInputIndex] + "' (character code " + (int) artInput[artInputIndex] + ")",
            artInputIndex, artInputString)));

    // Now we have found a lexeme, so jump the input index forward
    // System.out.println("Matched token " + artLongestToken + ": " + artParser.artLabelStrings[artLongestToken] + " with lexeme "
    // + artInputString.substring(artLexemeLeftIndex, artLexemeRightIndex) + " with extents (" + artLexemeLeftIndex + ", " + artLexemeRightIndex + ")");
    artInputIndex = artLexemeRightIndex;
  }

  public int artLexicalTrim(int startIndex) {
    artInputIndex = startIndex;
    artLexicalisePreparseWhitespace();
    return artInputIndex;
  }

  /******************************************************************************
   *
   * 4. The new TWE set lexer
   *
   ******************************************************************************/
  // This creates an array based version of the current TWE set
  // First dimension is the index into the input
  // Second dimension is the token
  // Third dimension is the sequence of right extents
  public int[][][] constructIndexedTWESet(int paraterminalCount) {

    int[][][] ret = new int[tweSet.length][][];
    for (int i = 0; i < tweSet.length; i++) {
      ret[i] = new int[paraterminalCount][];
      for (int token = 0; token < paraterminalCount; token++) {
        int tokenCount = 0;
        if (tweSet[i] != null) for (ARTTWEPair tweElement : tweSet[i].map.keySet())
          if (!tweElement.isSuppressed() && tweElement.token == token) tokenCount++;
        if (tokenCount > 0) {
          ret[i][token] = new int[tokenCount];
          tokenCount = 0;
          for (ARTTWEPair tweElement : tweSet[i].map.keySet())
            if (!tweElement.isSuppressed() && tweElement.token == token) ret[i][token][tokenCount++] = tweElement.rightExtent;
        }
      }
    }
    return ret;
  }

  public int[][][] lexicaliseToIndexedTWESet(String inputString) {
    lexicaliseToLinkedTWESet(inputString);
    int[][][] ret = constructIndexedTWESet(grammar.getLastNonterminalElementNumber() + 1);

    if (artDirectives.b("twePrint")) printIndexedTWESet(ret);
    return ret;
  }

  public void lexicaliseToLinkedTWESet(String inputString) {

    artLoadInputArray(inputString);

    // Build empty TWE set from the left
    tweSet = new ARTTWEPairSet[inputString.length() + 2]; // Rightmost position contains $ (zero) and then we need an empty position for successor
    touched = new boolean[tweSet.length]; // updated by traverser whenever it lands somewhere

    artInputIndex = 0;

    while (true) {
      int startIndex = artInputIndex;
      for (ARTGrammarElement w : grammar.getWhitespaces()) {
        if (w instanceof ARTGrammarElementTerminal) {
          ARTGrammarElementTerminal t = (ARTGrammarElementTerminal) w;
          artInputIndex = startIndex;
          if (t.getId().equals("COMMENT_NEST_ART")) {
            artBuiltin_COMMENT_NEST_ART();
            if (artInputIndex > startIndex) break;
          } else if (t.getId().equals("COMMENT_LINE_C")) {
            artBuiltin_COMMENT_LINE_C();
            if (artInputIndex > startIndex) break;
          } else if (t.getId().equals("COMMENT_BLOCK_C")) {
            artBuiltin_COMMENT_BLOCK_C();
            if (artInputIndex > startIndex) break;
          } else if (t.getId().equals("SIMPLE_WHITESPACE")) {
            artBuiltin_SIMPLE_WHITESPACE();
            if (artInputIndex > startIndex) break;
          }
        }
      }
      if (artInputIndex <= startIndex) break;
    }

    if (artInputIndex > 0) {
      startOffset = artInputIndex;
    } else
      startOffset = 0;

    tweSet[startOffset] = new ARTTWEPairSet();

    for (int i = startOffset; i < tweSet.length; i++)
      if (tweSet[i] != null) runRecognisers(i);

    // Normalise the TWE set by putting an empty set into each null position
    for (int i = 0; i < tweSet.length; i++)
      if (tweSet[i] == null) tweSet[i] = new ARTTWEPairSet();

    tweSetUpdateExactMakeRightSet(grammar.getEoS().getElementNumber(), artInput.length - 2, artInput.length - 1); // Add terminating EOS
    applyChoosers();
  }

  void runRecognisers(int i) {
    artLexemeLeftIndex = i;
    if (!artDirectives.b("tweWSSuffix")) for (ARTGrammarElement w : grammar.getWhitespaces()) {
      if (w instanceof ARTGrammarElementTerminal) {
        ARTGrammarElementTerminal t = (ARTGrammarElementTerminal) w;
        artInputIndex = i;
        // System.out.println("Testing at index " + i + " whitespace terminal " + w);
        if (t.getId().equals("COMMENT_NEST_ART")) {
          artBuiltin_COMMENT_NEST_ART();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("COMMENT_LINE_C")) {
          artBuiltin_COMMENT_LINE_C();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("COMMENT_BLOCK_C")) {
          artBuiltin_COMMENT_BLOCK_C();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("SIMPLE_WHITESPACE")) {
          artBuiltin_SIMPLE_WHITESPACE();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
        }
        if (artInputIndex > i) return; // We found a whitespaceterminal
      }
    }

    for (ARTGrammarElementTerminal t : grammar.getTerminals()) {
      // System.out.println("Testing at index " + i + " terminal " + t);
      artInputIndex = i;

      if (t instanceof ARTGrammarElementTerminalCaseSensitive) {
        artBuiltin_SINGLETON_CASE_SENSITIVE(t.getId());
        if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
      }

      else if (t instanceof ARTGrammarElementTerminalCaseInsensitive) {
        artBuiltin_SINGLETON_CASE_INSENSITIVE(t.getId());
        if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
      }

      else if (t instanceof ARTGrammarElementTerminalCharacter) {
        artBuiltin_SINGLETON_CASE_SENSITIVE(t.getId());
        if (testMatch()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
      }

      // These are here for backwards compatibility and will be removed whenbuiltins are taken out
      else if (t instanceof ARTGrammarElementTerminalBuiltin) {
        if (t.getId().equals("STRING_SQ")) {
          artBuiltin_STRING_SQ();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("STRING_DQ")) {
          artBuiltin_STRING_DQ();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("ID")) {
          artBuiltin_ID();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateWithPrefixes(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("INTEGER")) {
          artBuiltin_INTEGER();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateWithPrefixes(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("REAL")) {
          artBuiltin_REAL();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateWithPrefixes(t.getElementNumber(), i, artInputIndex, '.');
        } else if (t.getId().equals("COMMENT_NEST_ART")) {
          artBuiltin_COMMENT_NEST_ART();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("COMMENT_LINE_C")) {
          artBuiltin_COMMENT_LINE_C();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("COMMENT_BLOCK_C")) {
          artBuiltin_COMMENT_BLOCK_C();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
        } else if (t.getId().equals("SIMPLE_WHITESPACE")) {
          artBuiltin_SIMPLE_WHITESPACE();
          if (testMatchAndOptionallyConsumeWS()) tweSetUpdateExactMakeRightSet(t.getElementNumber(), i, artInputIndex);
        }
      }
    }
  }

  private boolean testMatch() {
    return artInputIndex > artLexemeLeftIndex;
  }

  private boolean testMatchAndOptionallyConsumeWS() {
    if (artInputIndex <= artLexemeLeftIndex) return false;
    if (artDirectives.b("tweWSSuffix")) { // consume until all fail
      while (true) {
        int startIndex = artInputIndex;
        for (ARTGrammarElement w : grammar.getWhitespaces()) {
          if (w instanceof ARTGrammarElementTerminal) {
            ARTGrammarElementTerminal t = (ARTGrammarElementTerminal) w;
            artInputIndex = startIndex;
            if (t.getId().equals("COMMENT_NEST_ART")) {
              artBuiltin_COMMENT_NEST_ART();
              if (artInputIndex > startIndex) break;
            } else if (t.getId().equals("COMMENT_LINE_C")) {
              artBuiltin_COMMENT_LINE_C();
              if (artInputIndex > startIndex) break;
            } else if (t.getId().equals("COMMENT_BLOCK_C")) {
              artBuiltin_COMMENT_BLOCK_C();
              if (artInputIndex > startIndex) break;
            } else if (t.getId().equals("SIMPLE_WHITESPACE")) {
              artBuiltin_SIMPLE_WHITESPACE();
              if (artInputIndex > startIndex) break;
            }
          }
        }
        if (artInputIndex <= startIndex) break;
      }
    }
    return true;
  }

  public void applyChoosers() {
    if (artDirectives.b("tweLongest")) System.out.println("TWE longest suppression");
    if (artDirectives.b("twePriority")) System.out.println("TWE priority suppression");

    chooserMillis = System.currentTimeMillis();
    // Apply longest match across tokens

    if (artDirectives.b("tweLongest") && artDirectives.b("twePriority")) {
      for (int i = tweSet.length - 3; i >= 0; i--)

        if (tweSet[i] != null && tweSet[i].map.size() > 0) for (ARTTWEPair e : tweSet[i].map.keySet())
          for (ARTTWEPair f : tweSet[i].map.keySet()) {
            if ((e.rightExtent > f.rightExtent) && longer[e.token] != null && longer[e.token].get(f.token)) f.setSuppressed(true);
            if ((e.rightExtent == f.rightExtent) && higher[e.token] != null && higher[e.token].get(f.token)) f.setSuppressed(true);
          }
    } else {
      if (artDirectives.b("tweLongest")) {
        for (int i = tweSet.length - 3; i >= 0; i--)

          if (tweSet[i] != null && tweSet[i].map.size() > 0) for (ARTTWEPair e : tweSet[i].map.keySet())
            for (ARTTWEPair f : tweSet[i].map.keySet())
              if ((e.rightExtent > f.rightExtent) && longer[e.token] != null && longer[e.token].get(f.token)) {
                f.setSuppressed(true);
                // System.out.println("longest suppressing " + tokenToString(f.token) + ", " + i + ", " + f.rightExtent + " against " +
                // tokenToString(e.token)
                // + ", " + i + ", " + e.rightExtent);
              }
      }
      // Apply priorities
      if (artDirectives.b("twePriority")) {
        // System.out.println("Priority relation is: ");
        // for (int i = 0; i < higher.length; i++) {
        // System.out.println(tokenToString(i) + ">");
        // if (higher[i] == null) {
        // System.out.println(" null");
        // continue;
        // }
        // for (int j = 0; j < higher[i].length(); j++)
        // if (higher[i].get(j)) System.out.println(tokenToString(j));
        // }

        for (int i = tweSet.length - 3; i >= 0; i--) {
          if (tweSet[i] != null && tweSet[i].map.size() > 0) for (ARTTWEPair e : tweSet[i].map.keySet())
            for (ARTTWEPair f : tweSet[i].map.keySet()) {
              if (e.rightExtent == f.rightExtent && higher[e.token] != null && higher[e.token].get(f.token)) {
                // System.out.println("priority suppressing " + tokenToString(f.token) + ", " + i + ", " + f.rightExtent + " against " + tokenToString(e.token)
                // + ", " + i + ", " + e.rightExtent);
                f.setSuppressed(true);
              }
              // else
              // System.out.println("priority NOT suppressing " + tokenToString(f.token) + ", " + i + ", " + f.rightExtent + " against " +
              // tokenToString(e.token)
              // + ", " + i + ", " + e.rightExtent);

            }
        }
      }
    }
    // Remove dead paths
    if (artDirectives.b("tweDead")) processTWEDead();

    chooserMillis = System.currentTimeMillis() - chooserMillis;
  }

  private void processTWEDead() {
    System.out.println("TWE dead path suppression");
    int inDegree[] = new int[tweSet.length + 1];
    int outDegree[] = new int[tweSet.length + 1];
    for (int i = 0; i < tweSet.length; i++)
      if (tweSet[i] != null) for (ARTTWEPair e : tweSet[i].map.keySet())
        if (!e.isSuppressed()) {
          outDegree[i]++;
          inDegree[e.rightExtent]++;
        }
    for (int i = tweSet.length - 3; i >= 0; i--)
      if (outDegree[i] != 0) {
        for (ARTTWEPair e : tweSet[i].map.keySet())
          if (!e.isSuppressed() && outDegree[e.rightExtent] == 0) {
            e.setSuppressed(true);
            outDegree[i]--;
            inDegree[e.rightExtent]--;
          }
      }
    for (int i = 1; i < tweSet.length; i++) {
      if (outDegree[i] != 0) {
        for (ARTTWEPair e : tweSet[i].map.keySet())
          if (!e.isSuppressed() && inDegree[i] == 0) {
            e.setSuppressed(true);
            outDegree[i]--;
            inDegree[e.rightExtent]--;
          }
      }
    }
  }

  /******************************************************************************
   *
   * 4. TWE set maintenance and debug
   *
   ******************************************************************************/
  public boolean tweSetContains(int token, int left, int right) {
    if (tweSet[left] == null) return false;

    ARTTWEPair p = new ARTTWEPair(token, right);

    return tweSet[left].map.get(p) != null;
  }

  public void tweSetUpdateExactMakeLeftSet(int token, int left, int right) {
    // System.out.println("Update exact " + left + ", " + token + ", " + right);
    if (tweSet[left] == null) tweSet[left] = new ARTTWEPairSet();
    ARTTWEPair p = new ARTTWEPair(token, right);
    tweSet[left].map.put(p, p);
  }

  public void tweSetUpdateExactMakeRightSet(int token, int left, int right) {
    // System.out.println("Update exact right");
    ARTTWEPair p = new ARTTWEPair(token, right);
    tweSet[left].map.put(p, p);
    if (tweSet[right] == null) tweSet[right] = new ARTTWEPairSet();
  }

  private void tweSetUpdateWithPrefixes(int token, int left, int right) {
    // System.out.println("Update with prefixes");
    /* Modification 21-June-2019: do not add prefixes in the token has been declared as longest match */
    if (artDirectives.b("tweLongest")) if (longer[token] != null && longer[token].get(token)) {
      tweSetUpdateExactMakeRightSet(token, left, right);
      return;
    }

    do {
      tweSetUpdateExactMakeRightSet(token, left, right--);
    } while (left < right);
  }

  private void tweSetUpdateWithPrefixes(int token, int left, int right, char stopChar) {
    // System.out.println("Update with prefixes");
    /* Modification 21-June-2019: do not add prefixes in the token has been declared as longest match */
    if (artDirectives.b("tweLongest")) if (longer[token] != null && longer[token].get(token)) {
      tweSetUpdateExactMakeRightSet(token, left, right);
      return;
    }

    do {
      tweSetUpdateExactMakeRightSet(token, left, right--);
    } while (left < right && artInput[right] != stopChar);
  }

  public boolean isSuppressed(int token, int left, int right) {
    ARTTWEPair p = new ARTTWEPair(token, right);
    return tweSet[left].map.get(p).isSuppressed();
  }

  public boolean isIn(int token, int left, int right) {
    ARTTWEPair p = new ARTTWEPair(token, right);
    return tweSet[left].map.get(p) != null;
  }

  private boolean isVacuous(ARTTWEPairSet artTWEPairSet) {
    if (artTWEPairSet != null) {
      for (ARTTWEPair e : artTWEPairSet.map.keySet())
        if (e.rightExtent >= 0) return false;
    }
    return true;
  }

  /******************************************************************************
   *
   * 5. Lexer data computations
   *
   ******************************************************************************/
  public void printTWELexicalisationsQuick() {
    processTWEDead();
    boolean many = false;
    artLexicalisationCount = 0;
    int index = 0;
    while (index < tweSet.length - 1) { // stop on $
      if (tweSet[index] == null) {
        System.out.println("Zero lexicalisations on empty index " + index); // there are no pairs at this location
        return;
      }
      int nextIndex = -1;
      for (ARTTWEPair e : tweSet[index].map.keySet())
        if (!e.isSuppressed()) {
          if (nextIndex != -1) many = true;
          nextIndex = e.rightExtent;
        }
      if (nextIndex == -1) {
        System.out.println("Zero lexicalisations on all suppressed at index " + index); // there are pairs, but they are all suppressed
        return;
      }
      index = nextIndex;
    }
    if (many)
      System.out.println("Many lexicalisations");
    else
      System.out.println("One lexicalisation");

    artLexicalisationCount = many ? 2 : 1;
  }

  BigInteger iterativeLexicalisationCounts[];

  public void printTWELexicalisations() {
    BigInteger segmentLexicalisationCount = null;
    BigInteger segmentLexicalisationLength = BigInteger.ZERO;

    if (artDirectives.b("tweSegments")) {
      segmentLexicalisationCount = runSegments();

      // Compute lexicalisationsPrime
      for (int i = 0; i < tweSet.length; i++)
        if (segmentLexicalisationCardinalities[i] != 0) {
          segmentLexicalisationProductBars[i] = BigInteger.ONE;
          for (int j = 0; j < tweSet.length; j++)
            if (i != j && segmentLexicalisationCardinalities[j] != 0)
              segmentLexicalisationProductBars[i] = segmentLexicalisationProductBars[i].multiply(new BigInteger("" + segmentLexicalisationCardinalities[j]));
        }

      // Compute lexicalisation length
      for (int i = 0; i < tweSet.length; i++)
        if (segmentLexicalisationCardinalities[i] != 0) segmentLexicalisationLength = segmentLexicalisationLength
            .add(new BigInteger("" + segmentLexicalisationLengths[i]).multiply(segmentLexicalisationProductBars[i]));
    }

    // Now collect lexicalisations iteratively
    iterativeLexicalisationCounts = new BigInteger[tweSet.length];
    BigInteger iterativeLexicalisationLengths[] = new BigInteger[tweSet.length];

    iterativeLexicalisationCounts[tweSet.length - 2] = BigInteger.ONE;
    for (int i = tweSet.length - 3; i >= 0; i--) {
      iterativeLexicalisationCounts[i] = BigInteger.ZERO;
      if (tweSet[i] != null) for (ARTTWEPair t : tweSet[i].map.keySet())
        if (!t.isSuppressed()) iterativeLexicalisationCounts[i] = iterativeLexicalisationCounts[i].add(iterativeLexicalisationCounts[t.rightExtent]);
    }

    for (int i = 0; i < iterativeLexicalisationLengths.length; i++)
      iterativeLexicalisationLengths[i] = BigInteger.ZERO;

    for (int i = tweSet.length - 3; i >= 0; i--) {
      if (tweSet[i] != null) for (ARTTWEPair t : tweSet[i].map.keySet())
        if (!t.isSuppressed()) iterativeLexicalisationLengths[i] = iterativeLexicalisationLengths[i]
            .add(iterativeLexicalisationLengths[t.rightExtent].add(iterativeLexicalisationCounts[t.rightExtent]));
    }

    System.out.println("Found (iterate) " + iterativeLexicalisationCounts[0] + " token-with-extent lexicalisation"
        + (iterativeLexicalisationCounts[0].equals(BigInteger.ONE) ? "" : "s") + " which is approximately "
        + iterativeLexicalisationCounts[0].toString().charAt(0) + " x 10^" + (iterativeLexicalisationCounts[0].toString().length() - 1));

    System.out.println("Found (iterate) sum over token-with-extent lexicalisation lengths " + iterativeLexicalisationLengths[0] + " which is approximately "
        + iterativeLexicalisationLengths[0].toString().charAt(0) + " x 10^" + (iterativeLexicalisationLengths[0].toString().length() - 1));

    if (iterativeLexicalisationCounts[0].equals(BigInteger.ZERO)) // Eeek: something went wrong
      for (int firstNonEmptyIndex = 0; firstNonEmptyIndex < tweSet.length; firstNonEmptyIndex++)
      if (!iterativeLexicalisationCounts[firstNonEmptyIndex].equals(BigInteger.ZERO)) {
        System.out.println("First nonempty lexicalisation index " + firstNonEmptyIndex);
        break;
      }

    if (artDirectives.b("tweSegments"))
      System.out.println("Found (segment) " + segmentLexicalisationCount + (artDirectives.b("tweExtents") ? " token-with-extent" : " token-only")
          + " lexicalisation" + (segmentLexicalisationCount.equals(BigInteger.ONE) ? "" : "s") + " which is approximately "
          + segmentLexicalisationCount.toString().charAt(0) + " x 10^" + (segmentLexicalisationCount.toString().length() - 1));

    if (artDirectives.b("tweSegments")) System.out.println("Found (segment) sum over" + (artDirectives.b("tweExtents") ? " token-with-extent" : " token-only")
        + " lexicalisation lengths " + segmentLexicalisationLength + " which is approximately " + segmentLexicalisationLength.toString().charAt(0) + " x 10^"
        + (segmentLexicalisationLength.toString().length() - 1));

    // Now collect the tweicalisations by recursively traversing the full set
    if (artDirectives.b("tweRecursive")) {
      lex = new ArrayList<>(2 * tweSet.length);

      traverseTWESetCollectLexicalisationsRec(0, tweSet.length - 2, lexicalisations);
      System.out.println("Found (recurse) " + lexicalisations.size() + (artDirectives.b("tweExtents") ? " token-with-extent" : " token-only")
          + " lexicalisation" + (lexicalisations.size() == 1 ? "" : "s"));
      if (lexicalisations.size() < 50) for (ArrayList<Integer> l : lexicalisations) {
        if (artDirectives.b("tweExtents"))
          for (int k = 0; k < l.size(); k += 2)
            System.out.print(tokenToString(l.get(k)) + "." + l.get(k + 1) + " ");
        else
          for (int k = 0; k < l.size(); k += 1)
            System.out.print(tokenToString(l.get(k)) + " ");

        System.out.println();
      }
    }
  }

  private BigInteger runSegments() {
    // Compute overdegree
    overDegree = new int[tweSet.length];
    for (int i = 0; i < tweSet.length; i++)
      if (tweSet[i] != null) for (ARTTWEPair t : tweSet[i].map.keySet())
        for (int k = i + 1; k < t.rightExtent; k++)
          overDegree[k]++;

    // Compute dominator points
    segmentLexicalisationCardinalities = new int[tweSet.length];
    segmentLexicalisationLengths = new int[tweSet.length];
    segmentLexicalisationProductBars = new BigInteger[tweSet.length];

    for (int i = 0; i < tweSet.length - 1; i++)
      if (tweSet[i] != null && tweSet[i].map.keySet() != null && overDegree[i] == 0) {
        segmentLexicalisationCardinalities[i] = 1;
      }

    // Compute lexical segment sets
    BigInteger segmentLexicalisationCount = BigInteger.ONE;

    for (int i = 0; i < tweSet.length - 2; i++) {
      if (segmentLexicalisationCardinalities[i] == 0) continue;
      int j = i + 1;
      while (j < tweSet.length - 2 && segmentLexicalisationCardinalities[j] == 0)
        j++;
      lexicalisations = new HashSet<>();
      // System.out.print("Computing lexicalisations for segment " + i + "..." + j + ": ");
      // for (int k = i; k < j; k++)
      // System.out.print(ARTText.toLiteralString(Character.toString(artInput[k])));
      // System.out.println();
      traverseTWESetCollectLexicalisationsRec(i, j, lexicalisations);
      segmentLexicalisationCardinalities[i] = lexicalisations.size();
      segmentLexicalisationLengths[i] = 0;

      for (ArrayList<Integer> l : lexicalisations)
        segmentLexicalisationLengths[i] += l.size() / (artDirectives.b("tweExtents") ? 2 : 1);

      // System.out.println("found " + segmentLexicalisationCardinalities[i] + " lexicalisation" + (segmentLexicalisationCardinalities[i] == 1 ? "" : "s")
      // + " with total length " + segmentLexicalisationLengths[i]);

      lexicalisations.clear();
      segmentLexicalisationCount = segmentLexicalisationCount.multiply(BigInteger.valueOf(segmentLexicalisationCardinalities[i]));
    }
    return segmentLexicalisationCount;
  }

  /* recusive traversal of TWESet and lexicalisation builder */
  ArrayList<Integer> lex = new ArrayList<>();

  private void traverseTWESetCollectLexicalisationsRec(int i, int j, Set<ArrayList<Integer>> lexicalisations) {
    // System.out.println("Traverse TWE set to collect lexicalisations over interval: " + i + " - " + j);
    touched[i] = true;
    if (tweSet[i] != null) for (ARTTWEPair e : tweSet[i].map.keySet()) {
      // System.out.println("At " + i + ", processing pair " + e);
      if (i == j) {
        if (!lexicalisations.contains(lex)) lexicalisations.add(new ArrayList<Integer>(lex));
      } else {
        lex.add(e.token);
        if (artDirectives.b("tweExtents")) lex.add(e.rightExtent);
        if (!e.isSuppressed()) traverseTWESetCollectLexicalisationsRec(e.rightExtent, j, lexicalisations); // Ignore supressed tokens
        lex.remove(lex.size() - 1); // Pop
        if (artDirectives.b("tweExtents")) lex.remove(lex.size() - 1);
      }
    }
  }

  /******************************************************************************
   *
   * 6. Output functions
   *
   ******************************************************************************/
  String tokenToString(int token) {
    if (grammar != null) return grammar.getElement(token).toString();

    if (labelStrings != null) return labelStrings[token];

    return Integer.toString(token);
  }

  public void printTWESet(PrintStream ps, boolean showSuppressed) {
    for (int i = 0; i < tweSet.length; i++) {
      if (tweSet[i] == null) continue;

      ps.print(i + ": " + (artInput != null ? ARTText.toLiteralString(Character.toString(artInput[i])) : " - ") + " { ");
      for (ARTTWEPair e : tweSet[i].map.keySet())
        if (showSuppressed || !e.isSuppressed()) ps.print(tokenToString(e.token) + (e.isSuppressed() ? "!" : ".") + e.rightExtent + " ");
      ps.print("}");

      if (artDirectives.b("tweSegments") && overDegree != null) {
        ps.print("^" + overDegree[i]);
        if (segmentLexicalisationCardinalities[i] != 0) ps.print(" Segment lexicalisation count = " + segmentLexicalisationCardinalities[i]
            + ", sum of lengths = " + segmentLexicalisationLengths[i] + ", lexBar = " + segmentLexicalisationProductBars[i]);
      }
      ps.println();
    }
  }

  public void printAmbiguityClasses() {

    Set<Integer> ambiguityClass = new TreeSet<>();
    Set<Set<Integer>> ambiguityClasses = new HashSet<>();
    for (int i = 0; i < tweSet.length; i++) {
      if (tweSet[i] == null) continue;
      ambiguityClass.clear();

      for (ARTTWEPair e : tweSet[i].map.keySet())
        if (!e.isSuppressed()) ambiguityClass.add(e.token);

      if (ambiguityClass.size() > 1) ambiguityClasses.add(new TreeSet<>(ambiguityClass));
    }

    if (!ambiguityClasses.isEmpty()) {
      System.out.println("TWE set ambiguity classes");
      int n = 0;
      for (Set<Integer> s : ambiguityClasses) {
        System.out.print("AC" + n++ + ": ");
        for (Integer t : s)
          System.out.print(tokenToString(t) + " ");
        System.out.println();
      }
    }
  }

  public void printIndexedTWESet(int[][][] indexedTWESet) {
    // Now output the TWE set
    int cardinality = 0;
    for (int i = 0; i < indexedTWESet.length; i++) {
      System.out.print(i + ": " + (artInput != null ? ARTText.toLiteralString(Character.toString(artInput[i])) : " - ") + " { ");
      for (int t = 0; t < indexedTWESet[i].length; t++) {
        if (indexedTWESet[i][t] != null) {
          cardinality += indexedTWESet[i][t].length;

          for (int e : indexedTWESet[i][t])
            System.out.print(tokenToString(t) + "." + e + " ");
        }
      }
      System.out.print("}\n");
    }
    System.out.println("Indexed TWE set cardinality = " + cardinality);

  }

  // Output an indexed TWESet in a form that is convenient to read back in
  // FORMAT
  // Line 1: (length of input string in characters) (maximum token value) (amount of whiespace before EOS)
  // Subsequent lines are triples i t e
  // If e<0 then allocate -e elements to set[i][t]
  // Else insert e onto [i][t] array
  public void writeIndexedTWESet(String filename, int[][][] indexedTWESet) throws FileNotFoundException {
    PrintStream ps = new PrintStream(filename);
    ps.println(indexedTWESet.length + " " + indexedTWESet[0].length + " " + eosWhitespacePrefix);
    for (int i = 0; i < indexedTWESet.length; i++) {
      // System.out.println("Writing TWE slice " + i);
      for (int t = 0; t < indexedTWESet[i].length; t++) {
        if (indexedTWESet[i][t] != null) {
          ps.println(i + " " + t + " " + -indexedTWESet[i][t].length);
          for (int e : indexedTWESet[i][t])
            ps.println(i + " " + t + " " + e);
        }
      }
    }
    ps.close();
  }

  public int[][][] readIndexedTWESet(String filename) throws FileNotFoundException {
    int[][][] ret = null;
    Scanner s = new Scanner(new File(filename));
    int i = s.nextInt();
    int t = s.nextInt();
    int e = s.nextInt();
    this.eosWhitespacePrefix = e;

    ret = new int[i][][];

    for (int x = 0; x < i; x++)
      ret[x] = new int[t][];

    int eCount = 0;

    while (s.hasNext()) {
      i = s.nextInt();
      t = s.nextInt();
      e = s.nextInt();

      if (e < 0) {
        eCount = 0;
        ret[i][t] = new int[-e];
      } else
        ret[i][t][eCount++] = e;

      // System.out.println("**: " + i + " " + t + " " + e);
    }
    s.close();
    return ret;
  }

  public void readAfterWriteIndexedTWESet(String filename, int[][][] indexedTWESet) throws FileNotFoundException {
    writeIndexedTWESet(filename, indexedTWESet);
    int[][][] readback = readIndexedTWESet(filename);
    if (readback.length != indexedTWESet.length) throw new ARTUncheckedException("readAfterWriteIndexSet: dimension 1 differs");
    for (int x = 0; x < readback.length; x++) {
      // System.out.println("Checking TWE set slice " + x);
      if (readback[x] == null) continue;
      if (readback[x].length != indexedTWESet[x].length) throw new ARTUncheckedException("readAfterWriteIndexSet: dimension 2 differs at [" + x + "]");
      for (int y = 0; y < readback[x].length; y++) {
        if (readback[x][y] == null) continue;
        if (readback[x][y].length != indexedTWESet[x][y].length)
          throw new ARTUncheckedException("readAfterWriteIndexSet: dimension 3 differs at [" + x + "][" + y + "]");
        for (int z = 0; z < readback[x][y].length; z++)
          if (readback[x][y][z] != indexedTWESet[x][y][z])
            throw new ARTUncheckedException("readAfterWriteIndexSet: element differs at [" + x + "][" + y + "][" + z + "]");
      }
    }
  }

  void printInputSlice(int leftExtent, int rightExtent) {
    for (int i = leftExtent; i < rightExtent; i++)
      tokText.print(Character.toString(artInput[i]));
  }

  void printInputSliceStripped(int leftExtent, int rightExtent) {
    for (int i = leftExtent; i < rightExtent; i++)
      if (Character.isWhitespace(artInput[i]))
        tokText.print(Character.toString(artInput[i]));
      else
        tokText.print(" ");
  }

  void printTokenisedFirstTWESetElement() {
    for (ARTTWEPair e : tweSet[tokIndex].map.keySet()) {
      if (!e.isSuppressed()) {
        ARTGrammarElement tokenElement = grammar.getElement(e.token);
        if (tokenElement instanceof ARTGrammarElementTerminalBuiltin && !((ARTGrammarElementTerminalBuiltin) tokenElement).getId().equals("SIMPLE_WHITESPACE"))
          tokText.print(((ARTGrammarElementTerminal) tokenElement).getId());
        else
          printInputSlice(tokIndex, e.rightExtent);

        tokIndex = e.rightExtent;
        return;
      }
    }
    System.out.println("Error when outputting input.tok - no unsuppressed token found at input position " + tokIndex);
    tokIndex = artInputLength + 1;
  }

  void printStrippedFirstTWESetElement() {
    for (ARTTWEPair e : tweSet[tokIndex].map.keySet()) {
      if (!e.isSuppressed()) {
        ARTGrammarElement tokenElement = grammar.getElement(e.token);
        if (tokenElement instanceof ARTGrammarElementTerminalBuiltin && (((ARTGrammarElementTerminalBuiltin) tokenElement).getId().equals("SIMPLE_WHITESPACE")
            || ((ARTGrammarElementTerminalBuiltin) tokenElement).getId().equals("COMMENT_BLOCK_C")
            || ((ARTGrammarElementTerminalBuiltin) tokenElement).getId().equals("COMMENT_LINE_C")))
          printInputSliceStripped(tokIndex, e.rightExtent);
        else
          printInputSlice(tokIndex, e.rightExtent);

        tokIndex = e.rightExtent;
        return;
      }
    }
    System.out.println("Error when outputting input.tok - no unsuppressed token found at input position " + tokIndex);
    tokIndex = artInputLength + 1;
  }

  /******************************************************************************
   *
   * 7. Postprocessing
   *
   ******************************************************************************/

  public void postProcess() {
    applyChoosers();

    if (artDirectives.b("twePrint")) printTWESet(System.out, false);

    if (artDirectives.b("twePrintFull")) printTWESet(System.out, true);

    if (artDirectives.b("tweCounts")) printTWECounts();

    if (artDirectives.b("tweAmbiguityClasses")) printAmbiguityClasses();

    if (artDirectives.b("tweLexicalisations")) printTWELexicalisations();

    if (artDirectives.b("tweLexicalisationsQuick")) printTWELexicalisationsQuick();

    if (artDirectives.b("tweDump")) {
      try {
        readAfterWriteIndexedTWESet("ARTTWE.twe", constructIndexedTWESet(paraterminalCount));
      } catch (FileNotFoundException e) {
        throw new ARTUncheckedException("Unable to open output file 'ARTTWE.twe'");
      }
    }
  }

  private void printTWECounts() {
    int cardinality = 0, suppressedCount = 0;
    for (int i = 0; i < tweSet.length; i++) {
      if (tweSet[i] == null) continue;
      cardinality += tweSet[i].map.size();
      for (ARTTWEPair e : tweSet[i].map.keySet())
        if (e.isSuppressed()) suppressedCount++;
    }

    System.out.println(
        "TWE set cardinality " + (cardinality - suppressedCount) + " with " + suppressedCount + " suppressed elements hence total size " + cardinality);
  }

  /******************************************************************************
   *
   * 8. Builtin recognisers and their support functions
   *
   ******************************************************************************/
  int tokIndex;
  ARTText tokText;

  static public boolean isValidBuiltin(String name) {
    return validBuiltins.contains(name);
  }

  protected char artPeekCh() {
    if (artInputIndex >= artInputLength)
      return '\0';
    else
      return artInput[artInputIndex];
  }

  protected char artPeekChToLower() {
    if (artInputIndex >= artInputLength)
      return '\0';
    else
      return Character.toLowerCase(artInput[artInputIndex]);
  }

  protected char artPeekOneCh() {
    if (artInputIndex + 1 >= artInputLength)
      return '\0';
    else
      return artInput[artInputIndex + 1];
  }

  protected char artPeekCh(int offset) {
    if (artInputIndex + offset >= artInputLength)
      return '\0';
    else
      return artInput[artInputIndex + offset];
  }

  protected char artGetCh() {
    // System.out
    // .println("artGetCh() at index " + artInputIndex + " character " + (int) artInput.charAt(artInputIndex));
    if (artInputIndex >= artInputLength)
      return '\0';
    else
      return artInput[artInputIndex++];
  }

  private void artSeekCh(int offset) {
    artInputIndex = offset;
    artGetCh();
  }

  private boolean artIsAlpha(char c) {
    return Character.isLetter(c);
  }

  private boolean artIsDigit(char c) {
    return Character.isDigit(c);
  }

  private boolean artIsHexDigit(char c) {
    return artIsDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
  }

  private boolean artIsAlphaOrDigit(char c) {
    return artIsAlpha(c) || artIsDigit(c);
  }

  private boolean artIsSimpleSpace(char c) {
    return c == ' ' || c == '\t' || c == '\n' || c == '\r';
  }

  protected void artBuiltin_SINGLETON_CASE_SENSITIVE(String string) {
    // System.out.print("At index " + artInputIndex + " testing for singleton case sensitive " + string + " against " + artPeekCh() + "...");
    for (int i = 0; i < string.length(); i++)
      if (string.charAt(i) != artPeekCh()) {
        artInputIndex = artLexemeLeftIndex;
        // System.out.println(" reject");
        return;
      } else
        artGetCh();

    // System.out.println(" accept");
  }

  protected void artBuiltin_SINGLETON_CASE_INSENSITIVE(String string) {
    // System.out.print("At index " + artInputIndex + " testing for singleton case insensitive " + string + " against " + artPeekCh() + "...");
    for (int i = 0; i < string.length(); i++)
      if (string.charAt(i) != artPeekChToLower()) {
        artInputIndex = artLexemeLeftIndex;
        // System.out.println(" reject");
        return;
      } else
        artGetCh();

    // System.out.println(" accept");
  }

  public void artBuiltin_SML_COMMENT() {
    if (!(artPeekCh() == '(' && artPeekOneCh() == '*')) return;
    int nestingLevel = 0;

    do {
      if (artPeekCh() == '\0') {
        ARTText.echo("Unterminated nestable SML comment at position " + artLexicalTrim(artLexemeLeftIndex), artLexicalTrim(artLexemeLeftIndex), artInputString);
        return;
      }

      if (artPeekCh() == '(' && artPeekOneCh() == '*') {
        artGetCh();
        artGetCh();
        nestingLevel++;
      } else if (artPeekCh() == '*' && artPeekOneCh() == ')') {
        artGetCh();
        artGetCh();
        nestingLevel--;
      } else
        artGetCh();
    } while (nestingLevel > 0);
  }

  protected void artBuiltin_SML_D() {
    if (artIsDigit(artPeekCh())) artGetCh();
  }

  protected void artBuiltin_SML_INT() {

    if (!(artIsDigit(artPeekCh()) || (artPeekCh() == '~' && artIsDigit(artPeekOneCh())))) return;

    if (artPeekCh() == '~') artGetCh();

    /* Check for hexadecimal introducer */
    boolean hex = artPeekCh() == '0' && artPeekOneCh() == 'x';

    if (hex) {
      artGetCh();
      artGetCh(); // Skip over hex introducer
      if (!artIsHexDigit(artPeekCh())) {
        artInputIndex = artLexemeLeftIndex;
        return;
      }
      while (artIsHexDigit(artPeekCh()))
        artGetCh();
    } else
      while (artIsDigit(artPeekCh()))
        artGetCh();
  }

  protected void artBuiltin_SML_WORD() {
    if (!(artPeekCh() == '0' && artPeekOneCh() == 'w')) return;

    artGetCh(); // Skip leading 0w
    artGetCh();

    /* Check for hexadecimal introducer */
    boolean hex = artPeekCh() == 'x';

    if (hex) {
      artGetCh(); // Skip over hex introducer
      if (!artIsHexDigit(artPeekCh())) {
        artInputIndex = artLexemeLeftIndex;
        return;
      }
      while (artIsHexDigit(artPeekCh()))
        artGetCh();
    } else
      while (artIsDigit(artPeekCh()))
        artGetCh();
  }

  protected void artBuiltin_SML_REAL() {
    if (!(artIsDigit(artPeekCh()) || (artPeekCh() == '~' && artIsDigit(artPeekOneCh())))) return;

    boolean invalid = true;

    if (artPeekCh() == '~') artGetCh();

    while (artIsDigit(artPeekCh()))
      artGetCh();

    if (artPeekCh() == '.') {
      artGetCh(); // skip .

      invalid = !artIsDigit(artPeekCh());

      while (artIsDigit(artPeekCh()))
        artGetCh();
    }

    if (artPeekCh() == 'e' || artPeekCh() == 'E') {

      artGetCh(); // skip e | E

      if (!(artIsDigit(artPeekCh()) || (artPeekCh() == '~' && artIsDigit(artPeekOneCh())))) {
        artInputIndex = artLexemeLeftIndex;
        return;
      }

      if (artPeekCh() == '~') artGetCh();

      invalid = !artIsDigit(artPeekCh());

      while (artIsDigit(artPeekCh()))
        artGetCh();
    }

    // One or other or both of the optional parts must be present for this to be a float
    if (invalid) {
      artInputIndex = artLexemeLeftIndex;
      return;
    }
  }

  protected void artBuiltin_SML_CHAR() {
    if (!(artPeekCh() == '#' && artPeekOneCh() == '"')) return;
    artGetCh(); // Skip #
    do {
      if (artPeekCh() == '\0') {
        ARTText.echo("Unterminated SML character at position " + artLexicalTrim(artLexemeLeftIndex), artLexicalTrim(artLexemeLeftIndex), artInputString);
        return;
      }
      if (artGetCh() == '\\') artSkipEscapeSequence();
    } while (artPeekCh() != '"');
    artGetCh(); // Skip delimiter
  }

  protected void artBuiltin_SML_STRING() {
    if (artPeekCh() != '"') return;
    do {
      if (artPeekCh() == '\0') {
        ARTText.echo("Unterminated SML string at position " + artLexicalTrim(artLexemeLeftIndex), artLexicalTrim(artLexemeLeftIndex), artInputString);
        return;
      }
      if (artGetCh() == '\\') artSkipEscapeSequence();
    } while (artPeekCh() != '"');
    artGetCh(); // Skip delimiter
  }

  Set<Character> SML_symbolIDElements = Set.of('!', '%', '&', '$', '#', '+', '-', '/', ':', '<', '=', '>', '?', '@', '\\', '~', '`', '^', '|', '*');
  Set<Character> SML_LabelInitialDigitElements = Set.of('1', '2', '3', '4', '5', '6', '7', '8', '9');

  protected void artBuiltin_SML_VID() {
    if (artIsAlpha(artPeekCh())) {
      while (artIsAlphaOrDigit(artPeekCh()) || artPeekCh() == '_' || artPeekCh() == '\'')
        artGetCh();
    } else
      while (SML_symbolIDElements.contains(artPeekCh()))
        artGetCh();
  }

  protected void artBuiltin_SML_TYVAR() {
    if (artPeekCh() == '\'' && (artIsAlpha(artPeekOneCh()) || artPeekOneCh() == '_' || artPeekOneCh() == '\'')) {
      while (artIsAlphaOrDigit(artPeekCh()) || artPeekCh() == '_' || artPeekCh() == '\'')
        artGetCh();
    }
  }

  protected void artBuiltin_SML_TYCON() {
    if (artIsAlpha(artPeekCh())) {
      while (artIsAlphaOrDigit(artPeekCh()) || artPeekCh() == '_' || artPeekCh() == '\'')
        artGetCh();
    } else
      while (SML_symbolIDElements.contains(artPeekCh()))
        artGetCh();
  }

  protected void artBuiltin_SML_LAB() { // labels
    if (artIsAlpha(artPeekCh())) {
      while (artIsAlphaOrDigit(artPeekCh()) || artPeekCh() == '_' || artPeekCh() == '\'')
        artGetCh();
    } else if (SML_LabelInitialDigitElements.contains(artPeekCh())) {
      while (artIsDigit(artPeekCh()))
        artGetCh();
    } else
      while (SML_symbolIDElements.contains(artPeekCh()))
        artGetCh();

  }

  protected void artBuiltin_SML_STRID() { // alphanumeric identifiers for structure IDs
    if (artIsAlpha(artPeekCh())) {
      while (artIsAlphaOrDigit(artPeekCh()) || artPeekCh() == '_' || artPeekCh() == '`')
        artGetCh();
    }
  }

  protected void artBuiltin_SML_SYMID() {
    while (SML_symbolIDElements.contains(artPeekCh()))
      artGetCh();
  }

  protected void artBuiltin_ID() {
    if (!(artIsAlpha(artPeekCh()) || artPeekCh() == '_')) return;
    while (artIsAlphaOrDigit(artPeekCh()) || artPeekCh() == '_')
      artGetCh();
  }

  protected void artBuiltin_ID_AN() {
    if (!artIsAlpha(artPeekCh())) return;
    while (artIsAlphaOrDigit(artPeekCh()))
      artGetCh();
  }

  // An identifier that only accepts alphaetic characters - no under score or digit
  protected void artBuiltin_ID_A() {
    while (artIsAlpha(artPeekCh()))
      artGetCh();
  }

  protected void artBuiltin_ID_SOS() {
    if (!(artIsAlpha(artPeekCh()) || artPeekCh() == '_')) return;
    while (artIsAlphaOrDigit(artPeekCh()) || artPeekCh() == '_' || artPeekCh() == '-')
      artGetCh();

    while (artPeekCh() == '\'')
      artGetCh();
  }

  protected void artBuiltin_INTEGER() {
    if (!artIsDigit(artPeekCh())) // Integers must contain at least one leading digit
      return;

    /* Check for hexadecimal introducer */
    boolean hex = (artPeekCh() == '0' && (artPeekOneCh() == 'x' || artPeekOneCh() == 'X'));

    if (hex) {
      artGetCh();
      artGetCh(); // Skip over hex introducer
      if (!artIsHexDigit(artPeekCh())) {
        artInputIndex = artLexemeLeftIndex;
        return;
      }
      while (artIsHexDigit(artGetCh()))
        ;
    } else
      while (artIsDigit(artPeekCh()))
        artGetCh();
  }

  protected void artBuiltin_SIGNED_INTEGER() {
    if (artPeekCh() == '-') // Integers must contain at least one leading digit
      artGetCh();
    artBuiltin_INTEGER();
  }

  protected void artBuiltin_REAL() {
    if (!artIsDigit(artPeekCh())) // Reals must contain at least one leading digit
      return;

    while (artIsDigit(artPeekCh()))
      artGetCh();

    // System.out.println("Testing for real at " + artCharacterStringInputIndex + ": current characters are " + artPeekCh() + " and " + artPeekOneCh());
    if (!(artPeekCh() == '.' && artIsDigit(artPeekOneCh()))) {
      artInputIndex = artLexemeLeftIndex;
      return;
    }

    artGetCh(); // skip .

    while (artIsDigit(artPeekCh()))
      artGetCh();

    if (artPeekCh() == 'e' || artPeekCh() == 'E') {
      artGetCh();

      while (artIsDigit(artPeekCh()))
        artGetCh();
    }
  }

  protected void artBuiltin_SIGNED_REAL() {
    if (artPeekCh() == '-') // Integers must contain at least one leading digit
      artGetCh();
    artBuiltin_REAL();
  }

  private void artSkipEscapeSequence() {
    artGetCh(); // Step over \element
  }

  protected void artBuiltin_CHAR_SQ() {
    if (artPeekCh() != '\'') return;
    artGetCh(); // Skip delimiter
    if (artGetCh() == '\\') artSkipEscapeSequence();
    if (artPeekCh() != '\'') {
      artInputIndex = artLexemeLeftIndex;
      return;
    } // Abort and return
    artGetCh(); // Skip delimiter
  }

  protected void artBuiltin_CHAR_BQ() {
    if (artPeekCh() != '`') return;
    artGetCh(); // Skip delimiter
    if (artGetCh() == '\\') artSkipEscapeSequence();
  }

  protected void artBuiltin_STRING_SQ() {
    if (artPeekCh() != '\'') return;
    do {
      if (artPeekCh() == '\0') {
        ARTText.echo("Unterminated ' ... ' string at position " + artLexicalTrim(artLexemeLeftIndex), artLexicalTrim(artLexemeLeftIndex), artInputString);
        return;
      }
      if (artGetCh() == '\\') artSkipEscapeSequence();
    } while (artPeekCh() != '\'');
    artGetCh(); // Skip delimiter
  }

  protected void artBuiltin_STRING_PLAIN_SQ() {
    if (artPeekCh() != '\'') return;
    do {
      if (artPeekCh() == '\n') {
        ARTText.echo("Line end in ' ... ' string at position " + artLexicalTrim(artLexemeLeftIndex), artLexicalTrim(artLexemeLeftIndex), artInputString);
        return;
      }
      if (artPeekCh() == '\0') {
        ARTText.echo("Unterminated ' ... ' string at position " + artLexicalTrim(artLexemeLeftIndex), artLexicalTrim(artLexemeLeftIndex), artInputString);
        return;
      }
      artGetCh();
    } while (artPeekCh() != '\'');
    artGetCh(); // Skip delimiter
  }

  protected void artBuiltin_STRING_DQ() {
    if (artPeekCh() != '"') return;
    do {
      if (artPeekCh() == '\0') {
        ARTText.echo("Unterminated \" ... \" string at position " + artLexicalTrim(artLexemeLeftIndex), artLexicalTrim(artLexemeLeftIndex), artInputString);
        return;
      }
      if (artGetCh() == '\\') artSkipEscapeSequence();
    } while (artPeekCh() != '"');
    artGetCh(); // Skip delimiter
  }

  protected void artBuiltin_STRING_BQ() {
    if (artPeekCh() != '`') return;
    do {
      if (artPeekCh() == '\0') {
        ARTText.echo("Unterminated ` ... ` string at position " + artLexicalTrim(artLexemeLeftIndex), artLexicalTrim(artLexemeLeftIndex), artInputString);
        return;
      }
      if (artGetCh() == '\\') artSkipEscapeSequence();
    } while (artPeekCh() != '`');
    artGetCh(); // Skip delimiter
  }

  protected void artBuiltin_STRING_DOLLAR() {
    if (artPeekCh() != '$') return;
    do {
      if (artPeekCh() == '\0') {
        ARTText.echo("Unterminated $ ... $ string at position " + artLexicalTrim(artLexemeLeftIndex), artLexicalTrim(artLexemeLeftIndex), artInputString);
        return;
      }
      if (artGetCh() == '\\') artSkipEscapeSequence();
    } while (artPeekCh() != '$');
    artGetCh(); // Skip delimiter
  }

  protected void artBuiltin_STRING_BRACKET_NEST() {
    if (artPeekCh() != '[') return;
    int nestLevel = 0;
    do {
      if (artPeekCh() == '[') nestLevel++;
      if (artPeekCh() == ']') nestLevel--;
      if (artPeekCh() == '\0') {
        ARTText.echo("Unterminated nestable [ ... ] string at position " + artLexicalTrim(artLexemeLeftIndex), artLexicalTrim(artLexemeLeftIndex),
            artInputString);
        return;
      }

      if (artGetCh() == '\\') artSkipEscapeSequence();
    } while (nestLevel > 0);
  }

  protected void artBuiltin_STRING_BRACKET() {
    if (artPeekCh() != '[') return;
    do {
      if (artPeekCh() == '\0') {
        ARTText.echo("Unterminated [ ... ] string at position " + artLexicalTrim(artLexemeLeftIndex), artLexicalTrim(artLexemeLeftIndex), artInputString);
        return;
      }
      if (artGetCh() == '\\') artSkipEscapeSequence();
    } while (artPeekCh() != ']');
    artGetCh(); // Skip delimiter
  }

  protected void artBuiltin_STRING_BRACE() {
    if (artPeekCh() != '{') return;
    do {
      if (artPeekCh() == '\0') {
        ARTText.echo("Unterminated { ... } string at position " + artLexicalTrim(artLexemeLeftIndex), artLexicalTrim(artLexemeLeftIndex), artInputString);
        return;
      }
      if (artGetCh() == '\\') artSkipEscapeSequence();
    } while (artPeekCh() != '}');
    artGetCh(); // Skip delimiter
  }

  protected void artBuiltin_STRING_BRACE_NEST() {
    if (artPeekCh() != '{') return;
    int nestLevel = 0;
    do {
      if (artPeekCh() == '{') nestLevel++;
      if (artPeekCh() == '}') nestLevel--;
      if (artPeekCh() == '\0') {
        ARTText.echo("Unterminated nestable { ... } string at position " + artLexicalTrim(artLexemeLeftIndex), artLexicalTrim(artLexemeLeftIndex),
            artInputString);
        return;
      }

      if (artGetCh() == '\\') artSkipEscapeSequence();
    } while (nestLevel > 0);
  }

  protected void artBuiltin_STRING_BB() {
    if (!((artPeekCh() == '[') && (artPeekOneCh() == '['))) return;
    do {
      if (artPeekCh() == '\0') {
        ARTText.echo("Unterminated [[ ... ]] string at position " + artLexicalTrim(artLexemeLeftIndex), artLexicalTrim(artLexemeLeftIndex), artInputString);
        return;
      }
      if (artGetCh() == '\\') artSkipEscapeSequence();
    } while (!((artPeekCh() == ']') && (artPeekOneCh() == ']')));
    artGetCh(); // Skip delimiter
    artGetCh(); // Skip delimiter
  }

  public void artBuiltin_SIMPLE_WHITESPACE() {
    while (artIsSimpleSpace(artPeekCh()))
      artGetCh();
  }

  public void artBuiltin_COMMENT_NEST_ART() {
    if (!(artPeekCh() == '(' && artPeekOneCh() == '*')) return;
    int nestingLevel = 0;

    do {
      if (artPeekCh() == '\0') ARTText.echo("Unterminated nestable (* ... *) comment at position " + artLexicalTrim(artLexemeLeftIndex),
          artLexicalTrim(artLexemeLeftIndex), artInputString);

      if (artPeekCh() == '(' && artPeekOneCh() == '*') {
        artGetCh();
        artGetCh();
        nestingLevel++;
      } else if (artPeekCh() == '*' && artPeekOneCh() == ')') {
        artGetCh();
        artGetCh();
        nestingLevel--;
      } else
        artGetCh();
    } while (nestingLevel > 0);
  }

  protected void artBuiltin_COMMENT_BLOCK_C() {
    if (!((artPeekCh() == '/') && (artPeekOneCh() == '*'))) return;

    do {
      if (artPeekCh() == '\0') {
        ARTText.echo("Unterminated /* ... */ comment at position " + artLexicalTrim(artLexemeLeftIndex), artLexicalTrim(artLexemeLeftIndex), artInputString);
        return;
      }
      artGetCh();
    } while (!(artPeekCh() == '*' && artPeekOneCh() == '/'));
    artGetCh();
    artGetCh();
  }

  public void artBuiltin_COMMENT_LINE_C() {
    if (!((artPeekCh() == '/') && (artPeekOneCh() == '/'))) return;

    while (artPeekCh() != '\n' && artPeekCh() != '\0') // Quietly accept an input file with no \n at the end.
      artGetCh();
  }

  public int getEosWhitespacePrefix() {
    return eosWhitespacePrefix;
  }

  /******************************************************************************
   *
   * 9. DFA based lexing
   *
   ******************************************************************************/
  // CFGNFA nfa;// , nfaReverse, nfaMinimised;
  // CFGDFA dfa;// , dfaReverse, dfaMinimised;
  // ARTGraphVertex root, mostRecentVertex;
  // Integer stateNumber = 0;
  // Set<Integer> emptySet = new HashSet<>();
  //
  // public void buildDFA() {
  //
  // final long NFAstartTime = System.currentTimeMillis();
  //
  // /* First build the NFA */
  // if (grammar.getParaterminals().isEmpty()) throw new ARTUncheckedException("!lexDFA called on module with no paraterminals");
  //
  // nfa = new CFGNFA("Lexer NFA");
  // dfa = new CFGDFA("Lexer DFA");
  //
  // root = mostRecentVertex = nfa.addVertex(++stateNumber, "start");
  // nfa.getRoots().add(root);
  // for (ARTGrammarElementNonterminal pt : grammar.getParaterminals()) {
  //
  // if (pt.getProductions().isEmpty())
  // System.out.println("Warning from lexer NFA builder: paraterminal " + pt + " has no rules - skipping");
  // else {
  // // System.out.println("Extending NFA using paraterminal " + pt);
  // ARTGraphVertex finalVertex = buildNFARec(root, pt.lhsInstance);
  // // System.out.println("Marking accepting state " + finalVertex.getKey() + " for paraterminal " + pt);
  // nfa.addAccepting(finalVertex, pt);
  // }
  // }
  //
  // final long NFAstopTime = System.currentTimeMillis();
  // System.out.println("** !lexDFA NFA states: " + nfa.vertexCount() + ", " + (NFAstopTime - NFAstartTime) + "ms");
  //
  // final long DFAstartTime = System.currentTimeMillis();
  // // Now build the DFA
  // nfa.subsetConstruction(dfa);
  // dfa.updateAcceptingStates(nfa);
  // final long DFAstopTime = System.currentTimeMillis();
  // System.out.println("** !lexDFA DFA states: " + dfa.vertexCount() + ", " + (DFAstopTime - DFAstartTime) + "ms");
  //
  // nfa.printDot("artLexerNFA.dot");
  // dfa.printDot("artLexerDFA.dot");
  //
  // // System.out.println();
  // }
  //
  // private ARTGraphVertex buildNFARec(ARTGraphVertex src, ARTGrammarInstance pt) {
  //
  // ARTGraphVertex ret;
  //
  // // System.out.println("buildNFARec caled with src " + src + " and instance class " + pt.getClass());
  //
  // if (pt instanceof ARTGrammarInstanceLHS || pt instanceof ARTGrammarInstanceAlt) {
  //
  // ARTGraphVertex head = nfa.addVertex(++stateNumber, "alt head");
  // nfa.addEdge(src, head);
  // ARTGraphVertex tail = nfa.addVertex(++stateNumber, "alt tail");
  //
  // for (ARTGrammarInstance childNode = pt.getChild(); childNode != null; childNode = childNode.getSibling()) {
  // ARTGraphVertex tmp = nfa.addVertex(++stateNumber, "alt branch");
  // nfa.addEdge(head, tmp);
  // tmp = buildNFARec(tmp, childNode);
  // nfa.addEdge(tmp, tail);
  // }
  //
  // return tail;
  // }
  //
  // if (pt instanceof ARTGrammarInstanceCat) {
  // ARTGraphVertex base = src;
  // for (ARTGrammarInstance childNode = pt.getChild(); childNode != null; childNode = childNode.getSibling()) {
  // if (childNode instanceof ARTGrammarInstanceSlot) continue;
  // ARTGraphVertex tmp = buildNFARec(base, childNode);
  // if (!(childNode.getSibling() == null || childNode.getSibling().getSibling() == null)) {
  // base = nfa.addVertex(++stateNumber, "cat splice");
  // nfa.addEdge(tmp, base);
  // } else
  // base = tmp;
  // }
  // return base;
  // }
  //
  // if (pt instanceof ARTGrammarInstanceNonterminal) {
  //
  // ret = buildNFARec(src, ((ARTGrammarElementNonterminal) pt.getPayload()).lhsInstance);
  // ARTGraphVertex tail = nfa.addVertex(++stateNumber, "");
  // nfa.addEdge(ret, tail);
  //
  // return tail;
  //
  // }
  //
  // if (pt instanceof ARTGrammarInstanceTerminal) {
  //
  // nfa.addEdge(src, ret = nfa.addVertex(++stateNumber, "terminal"), pt.getPayload());
  // return ret;
  // }
  //
  // if (pt instanceof ARTGrammarInstanceEpsilon) {
  // nfa.addEdge(src, ret = nfa.addVertex(++stateNumber, "epsilon"));
  // return ret;
  // }
  //
  // if (pt instanceof ARTGrammarInstanceDoFirst) {
  // return buildNFARec(src, pt.getChild());
  // }
  //
  // if (pt instanceof ARTGrammarInstancePositiveClosure || pt instanceof ARTGrammarInstanceKleeneClosure) {
  // ARTGraphVertex head = nfa.addVertex(++stateNumber, ((pt instanceof ARTGrammarInstancePositiveClosure) ? "Positive" : "Kleene") + " head");
  // nfa.addEdge(src, head);
  //
  // ret = nfa.addVertex(++stateNumber, "closure tail");
  //
  // if (pt instanceof ARTGrammarInstanceKleeneClosure) nfa.addEdge(head, ret);
  //
  // ARTGraphVertex body = nfa.addVertex(++stateNumber, "Closure body");
  // nfa.addEdge(head, body);
  //
  // ARTGraphVertex loop = buildNFARec(body, pt.getChild());
  //
  // nfa.addEdge(loop, body);
  //
  // nfa.addEdge(loop, ret);
  //
  // return ret;
  // }
  //
  // if (pt instanceof ARTGrammarInstanceOptional) {
  // ARTGraphVertex head = nfa.addVertex(++stateNumber, "Optional head");
  // nfa.addEdge(src, head);
  //
  // ret = nfa.addVertex(++stateNumber, "Optional tail");
  //
  // nfa.addEdge(head, ret);
  //
  // ARTGraphVertex body = nfa.addVertex(++stateNumber, "Optional body");
  // nfa.addEdge(head, body);
  //
  // ARTGraphVertex loop = buildNFARec(body, pt.getChild());
  //
  // nfa.addEdge(loop, ret);
  //
  // return ret;
  // }
  //
  // throw new ARTUncheckedException("lexer NFA builder found unexpected instance " + pt);
  //
  // }
  //
  // public void lexicaliseUsingDFAToLinkedTWESetViaMap(String inputString) {
  //
  // artLoadInputArray(inputString);
  //
  // for (int reps = 0; reps < 30; reps++) {
  // // Build empty TWE set from the left
  // tweSet = new ARTTWEPairSet[inputString.length() + 2]; // Rightmost position contains $ (zero) and then we need an empty position for successor
  // touched = new boolean[tweSet.length]; // updated by traverser whenever it lands somewhere
  // tweSet[0] = new ARTTWEPairSet(); // Seed the first position with an empty set so as to kick off the recogniser
  // touched[0] = true;
  //
  // long startTime = System.currentTimeMillis();
  //
  // for (int artInputIndex = 0; artInputIndex < tweSet.length; artInputIndex++)
  // if (touched[artInputIndex]) for (ARTAbstractVertex r : dfa.getRoots()) {
  // ARTGraphVertex v = (ARTGraphVertex) r;
  //
  // int rightExtent = artInputIndex;
  //
  // step: while (true) {
  // // System.out.println(artInputIndex + ": S" + v.getPayload());
  // @SuppressWarnings("unchecked")
  // Set<ARTGraphVertex> dfaLabel = (Set<ARTGraphVertex>) v.getKey();
  // for (ARTGraphVertex nfaV : dfaLabel)
  // if (nfa.getAcceptingStates().containsKey(nfaV)) {
  // for (ARTGrammarElementNonterminal t : nfa.getAcceptingStates().get(nfaV)) {
  // // System.out.println("Accept " + artInputIndex + ", " + rightExtent + " " + t);
  // tweSetUpdateExactMakeRightSet(t.getElementNumber(), artInputIndex, rightExtent);
  // touched[rightExtent] = true;
  // }
  // }
  //
  // for (ARTGraphEdge e : v.getOutEdges()) {
  // char edgeLabel = ((ARTGrammarElementTerminalCharacter) e.getPayload()).getId().charAt(0);
  // // System.out
  // // .println("Checking edge " + e.getSrc().getPayload() + " -> " + e.getDst().getPayload() + " " + e.getPayload() + " with label " + edgeLabel);
  // if (artInput[rightExtent] == edgeLabel) {
  // // System.out.println("Transition");
  // v = e.getDst();
  // rightExtent++;
  // continue step;
  // }
  // }
  // // System.out.println("Edges exhausted without transition");
  // break step;
  // }
  // }
  // final long lexTime = System.currentTimeMillis() - startTime;
  //
  // // Normalise the TWE set by putting an empty set into each null position
  // for (int i = 0; i < tweSet.length; i++)
  // if (tweSet[i] == null) tweSet[i] = new ARTTWEPairSet();
  //
  // ARTChooserSet chooserSet = grammar.getChooserSet("lexTWE");
  // if (chooserSet != null) {
  // this.higher = chooserSet.higher;
  // this.longer = chooserSet.longer;
  // this.shorter = chooserSet.shorter;
  // }
  //
  // tweSetUpdateExactMakeRightSet(grammar.getEoS().getElementNumber(), artInput.length - 2, artInput.length - 1); // Add terminating EOS
  // startTime = System.currentTimeMillis();
  // postProcess();
  // long chooseTime = System.currentTimeMillis() - startTime;
  //
  // // Check TWE accceptance
  // System.out.println((tweCheckAcceptance() ? "** Accept" : "** Reject") + ",test.str" + "," + inputString.length() + "," + lexTime + "," + chooseTime);
  // }
  //
  // }
  //
  // private boolean tweCheckAcceptance() {
  //
  // for (int i = 0; i < tweSet.length; i++)
  // if (tweSet[i] != null) for (ARTTWEPair s : tweSet[i].map.keySet())
  // if (s.rightExtent == artInput.length - 2) return true;
  // return false;
  // }

  /******************************************************************************
   *
   * 10. Old lexers
   *
   ******************************************************************************/
  // This lexer was moved over from ARTParserBase and is used by CNPIndexed and CNPGenerated variants
  public static int[] lexicaliseToArrayOfIntForCNP(String input, int startIndex, String[] symbolStrings, int epsilon) {
    int eoS = 0;
    ArrayList<Integer> ret = new ArrayList<>();

    int stringStart = 0, longest, retIndex = 0;

    for (int i = startIndex; i > 0; i--)
      ret.add(retIndex++, eoS); // Dummy EoS at element zero which is not used for Earley

    int longestTerminal = 0;

    while (stringStart < input.length() && Character.isWhitespace(input.charAt(stringStart)))
      stringStart++;

    while (stringStart < input.length()) {
      longest = 0;
      for (int t = 1; t < epsilon; t++)
        if (input.regionMatches(stringStart, symbolStrings[t], 0, symbolStrings[t].length()) && symbolStrings[t].length() > longest) {
          longest = symbolStrings[t].length();
          longestTerminal = t;
        }
      ret.add(retIndex, longestTerminal);
      // System.out.println("After lexing:" + ret + " with retIndex = " + retIndex);
      if (longest == 0) return null; // lexicalisation error
      stringStart += longest;
      // !! We don't have kinds in the slots at the moment
      // if (!(ret.get(retIndex) instanceof ARTGrammarElementTerminalCharacter))
      while (stringStart < input.length() && Character.isWhitespace(input.charAt(stringStart)))
        stringStart++;
      retIndex++;
    }
    // set a_{n+1} = $
    ret.add(retIndex, eoS);
    // System.out.println("Completed lexing:" + ret);
    int[] retArray = new int[ret.size()];

    for (int i = 0; i < ret.size(); i++)
      retArray[i] = ret.get(i);

    return retArray;
  }

  // This is a quick and dirty lexicaliser which does not support ART's special lexical features - first token at element [1]
  // Used by CNPLinkedAPI and Earley
  public ArrayList<ARTGrammarElementTerminal> lexicaliseToArrayListOfTerminals(String input, int startIndex) {
    ArrayList<ARTGrammarElementTerminal> ret = new ArrayList<>();

    int stringStart = 0, longest, retIndex = 0;

    for (int i = startIndex; i > 0; i--)
      ret.add(retIndex++, grammar.getEoS()); // Dummy EoS at element zero which is not used for Earley

    ARTGrammarElementTerminal longestTerminal = null;

    while (stringStart < input.length() && Character.isWhitespace(input.charAt(stringStart)))
      stringStart++;

    while (stringStart < input.length()) {
      longest = 0;
      for (ARTGrammarElementTerminal t : grammar.getTerminals())
        if (input.regionMatches(stringStart, t.getId(), 0, t.getId().length()) && t.getId().length() > longest) {
          longest = t.getId().length();
          longestTerminal = t;
        }
      ret.add(retIndex, longestTerminal);
      // System.out.println("After lexing:" + ret + " with retIndex = " + retIndex);
      if (longest == 0) return null; // lexicalisation error
      stringStart += longest;
      if (!(ret.get(retIndex) instanceof ARTGrammarElementTerminalCharacter))
        while (stringStart < input.length() && Character.isWhitespace(input.charAt(stringStart)))
        stringStart++;
      retIndex++;
    }
    // set a_{n+1} = $
    ret.add(retIndex, grammar.getEoS());
    // System.out.println("Completed lexing:" + ret);
    return ret;
  }

  public BigInteger getLexicalisationCount() {
    if (iterativeLexicalisationCounts == null)
      return BigInteger.valueOf(-1);
    else
      return iterativeLexicalisationCounts[0];
  }

}
