package uk.ac.rhul.cs.csle.art.old.cfg.extract;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;

public class ARTCompressWhiteSpaceJava {
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

  private void echo() {
    if (i < input.length()) {
      char c = input.charAt(i++);
      if (c < 128)
        pw.print(c);
      else // dirty unicode escaping...
        pw.print("\\u" + String.format("%04X", (int) c));
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
    echo(); // echo delimiter
    while (true) {
      if (peekc() == '\\' && (peekc1() == c || peekc1() == '\\'))
        echo2(); // echo escaped
      else if (peekc() == c) {
        echo();
        break;
      } else if (peekc() == 0)
        break;
      else
        echo();
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

  public ARTCompressWhiteSpaceJava(String inputFilename, String outputFilename) {
    inputFilename += ".java"; // Parser will have suppressed filetype
    outputFilename += ".java";

    input = ARTText.readFile(inputFilename);
    try {
      pw = new PrintWriter(outputFilename);
    } catch (FileNotFoundException e) {
      System.err.println("Unable to open compressed whitespace file " + outputFilename);
      return;
    }
    while (true) {
      if (peekc() == '"')
        skipOverStrings('"');
      else if (peekc() == '\'')
        skipOverStrings('\'');
      else if (peekc() != 0 && Character.isWhitespace(peekc()) || peekc() == '/' && (peekc1() == '/' || peekc1() == '*')) {
        while (peekc() != 0 && Character.isWhitespace(peekc()) || peekc() == '/' && (peekc1() == '/' || peekc1() == '*')) {
          while (peekc() != 0 && Character.isWhitespace(peekc())) {
            compression = true;
            seenNewline |= peekc() == '\n';
            i++;
          }
          if (peekc() == '/') {
            if (peekc1() == '/') {
              i += 2; // skip line comment delimiter
              compression = true;
              while (peekc() != '\n')
                i++;
              seenNewline = true;
              i++; // skip closing newline

            } else if (peekc1() == '*') {
              i += 2; // skip block comment delimiter
              compression = true;
              while (!(peekc() == '*' && peekc1() == '/')) {
                seenNewline |= peekc() == '\n';
                i++;
              }
              i += 2; // skip close delimiter
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
    pw.close();
    System.out.println("** " + inputFilename + " whitespace compressed to " + outputFilename);
  }
}
