package uk.ac.rhul.cs.csle.art.cfg.referenceFamily;

import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.LKind;

public class ReferenceLexer {
  protected LKind[] kinds;
  protected String[] strings;
  protected LKind[] whitespaces;

  protected String inputString;
  protected char[] input;
  protected int inputIndex, inputLength, leftIndex, longestMatchToken, longestMatchRightIndex, firstBuiltin;

  protected void processBuiltin(LKind b, String s) {
    switch (b) {
    case CHARACTER:
      match_CHARACTER(s);
      break;
    case SINGLETON_CASE_INSENSITIVE:
      match_SINGLETON_CASE_INSENSITIVE(s);
      break;
    case SINGLETON_CASE_SENSITIVE:
      match_SINGLETON_CASE_SENSITIVE(s);
      break;
    case ID:
      match_ID();
      break;
    case INTEGER:
      match_INTEGER();
      break;
    case SIGNED_INTEGER:
      match_SIGNED_INTEGER();
      break;
    case REAL:
      match_REAL();
      break;
    case SIGNED_REAL:
      match_SIGNED_REAL();
      break;
    case CHAR_BQ:
      match_CHAR_BQ();
      break;
    case STRING_DQ:
      match_STRING_DQ();
      break;
    case STRING_SQ:
      match_STRING_SQ();
      break;
    case STRING_PLAIN_SQ:
      match_STRING_PLAIN_SQ();
      break;
    case STRING_BRACE_NEST:
      match_STRING_BRACE_NEST();
      break;
    case STRING_BRACKET_NEST:
      match_STRING_BRACKET_NEST();
      break;
    case STRING_DOLLAR:
      match_STRING_DOLLAR();
      break;
    case SIMPLE_WHITESPACE:
      match_SIMPLE_WHITESPACE();
      break;
    case COMMENT_BLOCK_C:
      match_COMMENT_BLOCK_C();
      break;
    case COMMENT_LINE_C:
      match_COMMENT_LINE_C();
      break;
    case COMMENT_NEST_ART:
      match_COMMENT_NEST_ART();
      break;
    default:
      Reference.fatal("Unknown builtin " + b);
      break;
    }
  }

  protected void lexicalError(String msg) {
    System.out.print(Reference.echo(msg, inputIndex, inputString));
  }

  /******************************************************************************
   *
   * Builtin recognisers and their support functions below this line
   *
   ******************************************************************************/
  protected char peekCh() {
    if (inputIndex >= inputLength)
      return '\0';
    else
      return input[inputIndex];
  }

  protected char peekChToLower() {
    if (inputIndex >= inputLength)
      return '\0';
    else
      return Character.toLowerCase(input[inputIndex]);
  }

  protected char peekOneCh() {
    if (inputIndex + 1 >= inputLength)
      return '\0';
    else
      return input[inputIndex + 1];
  }

  protected char peekCh(int offset) {
    if (inputIndex + offset >= inputLength)
      return '\0';
    else
      return input[inputIndex + offset];
  }

  protected char getCh() {
    // System.out
    // .println("getCh() at index " + inputIndex + " character " + (int) input.charAt(inputIndex));
    if (inputIndex >= inputLength)
      return '\0';
    else
      return input[inputIndex++];
  }

  protected void seekCh(int offset) {
    inputIndex = offset;
    getCh();
  }

  public static boolean isAlpha(char c) {
    return Character.isLetter(c);
  }

  public static boolean isDigit(char c) {
    return Character.isDigit(c);
  }

  public static boolean isHexDigit(char c) {
    return isDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
  }

  public static boolean isAlphaOrDigit(char c) {
    return isAlpha(c) || isDigit(c);
  }

  public static boolean isSimpleSpace(char c) {
    return c == ' ' || c == '\t' || c == '\n' || c == '\r';
  }

  protected void match_CHARACTER(String string) {
    // System.out.print("At index " + inputIndex + " testing for character " + string + " against " + artPeekCh() + "...");
    if (string.charAt(0) != peekCh()) {
      inputIndex = leftIndex;
      // System.out.println(" reject");
      return;
    } else
      getCh();

    // System.out.println(" accept");
  }

  protected void match_SINGLETON_CASE_SENSITIVE(String string) {
    // System.out.print("At index " + inputIndex + " testing for singleton case sensitive " + string + " against " + artPeekCh() + "...");
    for (int i = 0; i < string.length(); i++)
      if (string.charAt(i) != peekCh()) {
        inputIndex = leftIndex;
        // System.out.println(" reject");
        return;
      } else
        getCh();

    // System.out.println(" accept");
  }

  protected void match_SINGLETON_CASE_INSENSITIVE(String string) {
    // System.out.print("At index " + inputIndex + " testing for singleton case insensitive " + string + " against " + artPeekCh() + "...");
    for (int i = 0; i < string.length(); i++)
      if (string.charAt(i) != peekChToLower()) {
        inputIndex = leftIndex;
        // System.out.println(" reject");
        return;
      } else
        getCh();

    // System.out.println(" accept");
  }

  protected void match_SML_COMMENT() {
    if (!(peekCh() == '(' && peekOneCh() == '*')) return;
    int nestingLevel = 0;

    do {
      if (peekCh() == '\0') {
        lexicalError("Unterminated nestable SML comment");
        return;
      }

      if (peekCh() == '(' && peekOneCh() == '*') {
        getCh();
        getCh();
        nestingLevel++;
      } else if (peekCh() == '*' && peekOneCh() == ')') {
        getCh();
        getCh();
        nestingLevel--;
      } else
        getCh();
    } while (nestingLevel > 0);
  }

  protected void match_SML_D() {
    if (isDigit(peekCh())) getCh();
  }

  protected void match_SML_INT() {

    if (!(isDigit(peekCh()) || (peekCh() == '~' && isDigit(peekOneCh())))) return;

    if (peekCh() == '~') getCh();

    /* Check for hexadecimal introducer */
    boolean hex = peekCh() == '0' && peekOneCh() == 'x';

    if (hex) {
      getCh();
      getCh(); // Skip over hex introducer
      if (!isHexDigit(peekCh())) {
        inputIndex = leftIndex;
        return;
      }
      while (isHexDigit(peekCh()))
        getCh();
    } else
      while (isDigit(peekCh()))
        getCh();
  }

  protected void match_SML_WORD() {
    if (!(peekCh() == '0' && peekOneCh() == 'w')) return;

    getCh(); // Skip leading 0w
    getCh();

    /* Check for hexadecimal introducer */
    boolean hex = peekCh() == 'x';

    if (hex) {
      getCh(); // Skip over hex introducer
      if (!isHexDigit(peekCh())) {
        inputIndex = leftIndex;
        return;
      }
      while (isHexDigit(peekCh()))
        getCh();
    } else
      while (isDigit(peekCh()))
        getCh();
  }

  protected void match_SML_REAL() {
    if (!(isDigit(peekCh()) || (peekCh() == '~' && isDigit(peekOneCh())))) return;

    boolean invalid = true;

    if (peekCh() == '~') getCh();

    while (isDigit(peekCh()))
      getCh();

    if (peekCh() == '.') {
      getCh(); // skip .

      invalid = !isDigit(peekCh());

      while (isDigit(peekCh()))
        getCh();
    }

    if (peekCh() == 'e' || peekCh() == 'E') {

      getCh(); // skip e | E

      if (!(isDigit(peekCh()) || (peekCh() == '~' && isDigit(peekOneCh())))) {
        inputIndex = leftIndex;
        return;
      }

      if (peekCh() == '~') getCh();

      invalid = !isDigit(peekCh());

      while (isDigit(peekCh()))
        getCh();
    }

    // One or other or both of the optional parts must be present for this to be a float
    if (invalid) {
      inputIndex = leftIndex;
      return;
    }
  }

  protected void match_SML_CHAR() {
    if (!(peekCh() == '#' && peekOneCh() == '"')) return;
    getCh(); // Skip #
    do {
      if (peekCh() == '\0') {
        lexicalError("Unterminated SML character");
        return;
      }
      if (getCh() == '\\') artSkipEscapeSequence();
    } while (peekCh() != '"');
    getCh(); // Skip delimiter
  }

  protected void match_SML_STRING() {
    if (peekCh() != '"') return;
    do {
      if (peekCh() == '\0') {
        lexicalError("Unterminated SML string");
        return;
      }
      if (getCh() == '\\') artSkipEscapeSequence();
    } while (peekCh() != '"');
    getCh(); // Skip delimiter
  }

  Set<Character> SML_symbolIDElements = Set.of('!', '%', '&', '$', '#', '+', '-', '/', ':', '<', '=', '>', '?', '@', '\\', '~', '`', '^', '|', '*');
  Set<Character> SML_LabelInitialDigitElements = Set.of('1', '2', '3', '4', '5', '6', '7', '8', '9');

  protected void match_SML_VID() {
    if (isAlpha(peekCh())) {
      while (isAlphaOrDigit(peekCh()) || peekCh() == '_' || peekCh() == '\'')
        getCh();
    } else
      while (SML_symbolIDElements.contains(peekCh()))
        getCh();
  }

  protected void match_SML_TYVAR() {
    if (peekCh() == '\'' && (isAlpha(peekOneCh()) || peekOneCh() == '_' || peekOneCh() == '\'')) {
      while (isAlphaOrDigit(peekCh()) || peekCh() == '_' || peekCh() == '\'')
        getCh();
    }
  }

  protected void match_SML_TYCON() {
    if (isAlpha(peekCh())) {
      while (isAlphaOrDigit(peekCh()) || peekCh() == '_' || peekCh() == '\'')
        getCh();
    } else
      while (SML_symbolIDElements.contains(peekCh()))
        getCh();
  }

  protected void match_SML_LAB() { // labels
    if (isAlpha(peekCh())) {
      while (isAlphaOrDigit(peekCh()) || peekCh() == '_' || peekCh() == '\'')
        getCh();
    } else if (SML_LabelInitialDigitElements.contains(peekCh())) {
      while (isDigit(peekCh()))
        getCh();
    } else
      while (SML_symbolIDElements.contains(peekCh()))
        getCh();

  }

  protected void match_SML_STRID() { // alphanumeric identifiers for structure IDs
    if (isAlpha(peekCh())) {
      while (isAlphaOrDigit(peekCh()) || peekCh() == '_' || peekCh() == '`')
        getCh();
    }
  }

  protected void match_SML_SYMID() {
    while (SML_symbolIDElements.contains(peekCh()))
      getCh();
  }

  protected void match_ID() {
    if (!(isAlpha(peekCh()) || peekCh() == '_')) return;
    while (isAlphaOrDigit(peekCh()) || peekCh() == '_')
      getCh();
  }

  protected void match_ID_AN() {
    if (!isAlpha(peekCh())) return;
    while (isAlphaOrDigit(peekCh()))
      getCh();
  }

  // An identifier that only accepts alphaetic characters - no under score or digit
  protected void match_ID_A() {
    while (isAlpha(peekCh()))
      getCh();
  }

  protected void match_ID_SOS() {
    if (!(isAlpha(peekCh()) || peekCh() == '_')) return;
    while (isAlphaOrDigit(peekCh()) || peekCh() == '_' || peekCh() == '-')
      getCh();

    while (peekCh() == '\'')
      getCh();
  }

  protected void match_INTEGER() {
    if (!isDigit(peekCh())) // Integers must contain at least one leading digit
      return;

    /* Check for hexadecimal introducer */
    boolean hex = (peekCh() == '0' && (peekOneCh() == 'x' || peekOneCh() == 'X'));

    if (hex) {
      getCh();
      getCh(); // Skip over hex introducer
      if (!isHexDigit(peekCh())) {
        inputIndex = leftIndex;
        return;
      }
      while (isHexDigit(getCh()))
        ;
    } else
      while (isDigit(peekCh()))
        getCh();
  }

  protected void match_SIGNED_INTEGER() {
    if (peekCh() == '-') // Integers must contain at least one leading digit
      getCh();
    match_INTEGER();
  }

  protected void match_REAL() {
    if (!isDigit(peekCh())) // Reals must contain at least one leading digit
      return;

    while (isDigit(peekCh()))
      getCh();

    // System.out.println("Testing for real at " + artCharacterStringInputIndex + ": current characters are " + artPeekCh() + " and " + artPeekOneCh());
    if (!(peekCh() == '.' && isDigit(peekOneCh()))) {
      inputIndex = leftIndex;
      return;
    }

    getCh(); // skip .

    while (isDigit(peekCh()))
      getCh();

    if (peekCh() == 'e' || peekCh() == 'E') {
      getCh();

      while (isDigit(peekCh()))
        getCh();
    }
  }

  protected void match_SIGNED_REAL() {
    if (peekCh() == '-') // Integers must contain at least one leading digit
      getCh();
    match_REAL();
  }

  private void artSkipEscapeSequence() {
    getCh(); // Step over \element
  }

  protected void match_CHAR_SQ() {
    if (peekCh() != '\'') return;
    getCh(); // Skip delimiter
    if (getCh() == '\\') artSkipEscapeSequence();
    if (peekCh() != '\'') {
      inputIndex = leftIndex;
      return;
    } // Abort and return
    getCh(); // Skip delimiter
  }

  protected void match_CHAR_BQ() {
    if (peekCh() != '`') return;
    getCh(); // Skip delimiter
    if (getCh() == '\\') artSkipEscapeSequence();
  }

  protected void match_STRING_SQ() {
    if (peekCh() != '\'') return;
    do {
      if (peekCh() == '\0') {
        lexicalError("Unterminated ' ... ' string");
        return;
      }
      if (getCh() == '\\') artSkipEscapeSequence();
    } while (peekCh() != '\'');
    getCh(); // Skip delimiter
  }

  protected void match_STRING_PLAIN_SQ() {
    if (peekCh() != '\'') return;
    do {
      if (peekCh() == '\n') {
        lexicalError("Newline character in ' ... ' string");
        return;
      }
      if (peekCh() == '\0') {
        lexicalError("Unterminated ' ... ' string");
        return;
      }
      getCh();
    } while (peekCh() != '\'');
    getCh(); // Skip delimiter
  }

  protected void match_STRING_DQ() {
    if (peekCh() != '"') return;
    do {
      if (peekCh() == '\0') {
        lexicalError("Unterminated \" ... \" string");
        return;
      }
      if (getCh() == '\\') artSkipEscapeSequence();
    } while (peekCh() != '"');
    getCh(); // Skip delimiter
  }

  protected void match_STRING_BQ() {
    if (peekCh() != '`') return;
    do {
      if (peekCh() == '\0') {
        lexicalError("Unterminated ` ... ` string");
        return;
      }
      if (getCh() == '\\') artSkipEscapeSequence();
    } while (peekCh() != '`');
    getCh(); // Skip delimiter
  }

  protected void match_STRING_DOLLAR() {
    if (peekCh() != '$') return;
    do {
      if (peekCh() == '\0') {
        lexicalError("Unterminated $ ... $ string");
        return;
      }
      if (getCh() == '\\') artSkipEscapeSequence();
    } while (peekCh() != '$');
    getCh(); // Skip delimiter
  }

  protected void match_STRING_BRACKET_NEST() {
    if (peekCh() != '[') return;
    int nestLevel = 0;
    do {
      if (peekCh() == '[') nestLevel++;
      if (peekCh() == ']') nestLevel--;
      if (peekCh() == '\0') {
        lexicalError("Unterminated nestable [ ... ] string");
        return;
      }

      if (getCh() == '\\') artSkipEscapeSequence();
    } while (nestLevel > 0);
  }

  protected void match_STRING_BRACKET() {
    if (peekCh() != '[') return;
    do {
      if (peekCh() == '\0') {
        lexicalError("Unterminated [ ... ] string");
        return;
      }
      if (getCh() == '\\') artSkipEscapeSequence();
    } while (peekCh() != ']');
    getCh(); // Skip delimiter
  }

  protected void match_STRING_BRACE() {
    if (peekCh() != '{') return;
    do {
      if (peekCh() == '\0') {
        lexicalError("Unterminated { ... } string");
        return;
      }
      if (getCh() == '\\') artSkipEscapeSequence();
    } while (peekCh() != '}');
    getCh(); // Skip delimiter
  }

  protected void match_STRING_BRACE_NEST() {
    if (peekCh() != '{') return;
    int nestLevel = 0;
    do {
      if (peekCh() == '{') nestLevel++;
      if (peekCh() == '}') nestLevel--;
      if (peekCh() == '\0') {
        lexicalError("Unterminated nestable { ... } string");
        return;
      }

      if (getCh() == '\\') artSkipEscapeSequence();
    } while (nestLevel > 0);
  }

  protected void match_STRING_BB() {
    if (!((peekCh() == '[') && (peekOneCh() == '['))) return;
    do {
      if (peekCh() == '\0') {
        lexicalError("Unterminated [[ ... ]] string");
        return;
      }
      if (getCh() == '\\') artSkipEscapeSequence();
    } while (!((peekCh() == ']') && (peekOneCh() == ']')));
    getCh(); // Skip delimiter
    getCh(); // Skip delimiter
  }

  protected void match_SIMPLE_WHITESPACE() {
    while (isSimpleSpace(peekCh()))
      getCh();
  }

  protected void match_COMMENT_NEST_ART() {
    if (!(peekCh() == '(' && peekOneCh() == '*')) return;
    int nestingLevel = 0;

    do {
      if (peekCh() == '\0') {
        lexicalError("Unterminated nestable (* ... *) comment");
        return;
      }

      if (peekCh() == '(' && peekOneCh() == '*') {
        getCh();
        getCh();
        nestingLevel++;
      } else if (peekCh() == '*' && peekOneCh() == ')') {
        getCh();
        getCh();
        nestingLevel--;
      } else
        getCh();
    } while (nestingLevel > 0);
  }

  protected void match_COMMENT_BLOCK_C() {
    if (!((peekCh() == '/') && (peekOneCh() == '*'))) return;

    do {
      if (peekCh() == '\0') {
        System.out.println("Unterminated /* ... */ comment");
        return;
      }
      getCh();
    } while (!(peekCh() == '*' && peekOneCh() == '/'));
    getCh();
    getCh();
  }

  protected void match_COMMENT_LINE_C() {
    if (!((peekCh() == '/') && (peekOneCh() == '/'))) return;

    while (peekCh() != '\n' && peekCh() != '\0') // Quietly accept an input file with no \n at the end.
      getCh();
  }

}
