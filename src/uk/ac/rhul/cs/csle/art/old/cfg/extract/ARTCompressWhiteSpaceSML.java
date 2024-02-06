package uk.ac.rhul.cs.csle.art.old.cfg.extract;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;

public class ARTCompressWhiteSpaceSML {
  private final int idMax = Integer.MAX_VALUE;
  private final int stringMax = Integer.MAX_VALUE;

  private final String input;
  private PrintWriter pw = null;
  private boolean seenNewline = false;
  private boolean compression = false;
  private boolean seenUncompressedOutput = false;
  private int i = 0;

  private char peekc() {
    char ret = 0;
    if (i < input.length()) ret = input.charAt(i);
    return ret;
  }

  private char peekc1() {
    char ret = 0;
    if (i < input.length() - 1) ret = input.charAt(i + 1);
    return ret;
  }

  char lastEchoedCharacter = ' ';

  private void echo() {
    if (i < input.length()) {
      lastEchoedCharacter = input.charAt(i++);
      if (lastEchoedCharacter < 128)
        pw.print(lastEchoedCharacter);
      else // dirty unicode escaping...
        pw.print("\\u" + String.format("%04X", (int) lastEchoedCharacter));
    }
  }

  private void echo2() {
    echo();
    echo();
  }

  private void skip() {
    if (i < input.length()) i++;
  }

  private void skip2() {
    skip();
    skip();
  }

  void skipOverStrings(char c) { // Skip over strings
    seenUncompressedOutput = true;
    int length = 0;
    echo(); // echo delimiter
    while (true) {
      if (peekc() == '\\' && (peekc1() == c || peekc1() == '\\'))
        echo2(); // echo escaped
      else if (peekc() == c) {
        echo();
        break;
      } else if (peekc() == 0)
        break;
      else {
        if (++length < stringMax)
          echo();
        else
          skip();
      }
    }
  }

  void skipOverIdentifiers() { // Skip over strings
    seenUncompressedOutput = true;
    int length = 0;
    while (Character.isAlphabetic(peekc()) || Character.isDigit(peekc())) {
      if (++length < idMax)
        echo();
      else
        skip();
    }
  }

  private void echoCompression() {
    if (seenUncompressedOutput && compression) {
      if (seenNewline) {
        pw.println();
      } else
        pw.print(" ");
    }
    compression = false;
    seenNewline = false;
  }

  public ARTCompressWhiteSpaceSML(String inputFilename, String outputFilename) {
    inputFilename += ".sml"; // Parser will have suppressed filetype
    outputFilename += ".sml";

    input = ARTText.readFile(inputFilename);
    try {
      pw = new PrintWriter(outputFilename);
    } catch (FileNotFoundException e) {
      System.err.println("Unable to open SML compreseed whitespace output file " + outputFilename);
    }
    while (true) {
      if (Character.isAlphabetic(peekc()))
        skipOverIdentifiers();
      else if (peekc() == '"')
        skipOverStrings('"');
      else if (peekc() != 0 && Character.isWhitespace(peekc()) || peekc() == '(' && peekc1() == '*') {
        while (peekc() != 0 && Character.isWhitespace(peekc()) || peekc() == '(' && peekc1() == '*') { // skip sequences of whitespace and comments
          while (peekc() != 0 && Character.isWhitespace(peekc())) { // skip one sequence of whitespace
            compression = true;
            seenNewline |= peekc() == '\n';
            i++;
          }
          if (peekc() == '(' && peekc1() == '*') { // we are now in a comment
            i += 2;
            compression = true;
            for (int commentLevel = 1; commentLevel > 0;) {
              seenNewline |= peekc() == '\n';
              if (peekc() == '(' && peekc1() == '*') {
                i += 2;
                commentLevel++;
              } else if (peekc() == '*' && peekc1() == ')') {
                i += 2;
                commentLevel--;
              } else
                i++;
            }
          }
        }
        echoCompression();
      } else {
        if (i >= input.length()) break;
        seenUncompressedOutput = true;
        echo();
      }
    }
    if (lastEchoedCharacter != ';') pw.println(';');
    pw.close();
    System.out.println("** " + inputFilename + " whitespace compressed to " + outputFilename);
  }
}
