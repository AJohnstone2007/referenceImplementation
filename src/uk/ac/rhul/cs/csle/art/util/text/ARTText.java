package uk.ac.rhul.cs.csle.art.util.text;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import uk.ac.rhul.cs.csle.art.core.ARTUncheckedException;

public class ARTText {
  ARTTextHandler handler;

  private static String asciiIdentifiers[] = { "_NUL", "_SOH", "_STX", "_ETX", "_EOT", "_ENQ", "_ACK", "_BEL", "_BS", "_HT", "_LF", "_VT", "_FF", "_CR", "_SO",
      "_SI", "_DLE", "_DC1", "_DC2", "_DC3", "_DC4", "_NAK", "_SYN", "_ETB", "_CAN", "_EM", "_SUB", "_ESC", "_FS", "_GS", "_RS", "_", "_SPACE", "_SHREIK",
      "_DBLQUOTE", "_HASH", "_DOLLAR", "_PERCENT", "_AMPERSAND", "_QUOTE", "_LPAR", "_RPAR", "_STAR", "_PLUS", "_COMMA", "_MINUS", "_PERIOD", "_SLASH", "0",
      "1", "2", "3", "4", "5", "6", "7", "8", "9", "_COLON", "_SEMICOLON", "_LT", "_EQUAL", "_GT", "_QUERY", "_AT", "A", "B", "C", "D", "E", "F", "G", "H", "I",
      "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "_LBRACK", "_BACKSLASH", "_RBRACK", "_UPARROW", "_", "_BACKQUOTE",
      "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "_LBRACE", "_BAR",
      "_RBRACE", "_TILDE", "_DEL" };

  private static String asciiLiteralStrings[] = { "\\000", "\\001", "\\002", "\\003", "\\004", "\\005", "\\006", "\\007", "\\010", "\\t", "\\n", "\\013",
      "\\014", "\\r", "\\016", "\\017", "\\020", "\\021", "\\022", "\\023", "\\024", "\\025", "\\026", "\\027", "\\030", "\\031", "\\032", "\\033", "\\034",
      "\\035", "\\036", "\\037", " ", "!", "\\\"", "#", "$", "%", "&", "'", "(", ")", "*", "+", ",", "-", ".", "/", "0", "1", "2", "3", "4", "5", "6", "7", "8",
      "9", ":", ";", "<", "=", ">", "?", "@", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
      "X", "Y", "Z", "[", "\\\\", "]", "^", "_", "`", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
      "v", "w", "x", "y", "z", "{", "|", "}", "~", "_DEL" };

  private static String asciiQuotedLiteralStrings[] = { "\\\\\\\\000", "\\\\\\\\001", "\\\\\\\\002", "\\\\\\\\003", "\\\\\\\\004", "\\\\\\\\005", "\\\\\\\\006",
      "\\\\\\\\007", "\\\\\\\\010", "\\\\\\\\t", "\\\\\\\\n", "\\\\\\\\013", "\\\\\\\\014", "\\\\\\\\r", "\\\\\\\\016", "\\\\\\\\017", "\\\\\\\\020",
      "\\\\\\\\021", "\\\\\\\\022", "\\\\\\\\023", "\\\\\\\\024", "\\\\\\\\025", "\\\\\\\\026", "\\\\\\\\027", "\\\\\\\\030", "\\\\\\\\031", "\\\\\\\\032",
      "\\\\\\\\033", "\\\\\\\\034", "\\\\\\\\035", "\\\\\\\\036", "\\\\\\\\037", " ", "!", "\\\\\\\"", "#", "$", "%", "&", "\\\\\\\\'", "(", ")", "*", "+", ",",
      "-", ".", "/", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ":", ";", "<", "=", ">", "?", "@", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
      "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "[", "\\\\\\\\\\\\\\\\", "]", "^", "_", "\\\\\\\\`", "a", "b", "c", "d", "e",
      "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "{", "|", "}", "~", "\\\\\\\\0177" };

  private static String toString(String string, String[] table) {
    String ret = "";

    if (string != null) for (int i = 0; i < string.length(); i++) {
      char c = string.charAt(i);
      if (c > 0 && c < 128)
        ret += table[c];
      else
        ret += ' ';
    }

    return ret;
  }

  public static String toIdentifier(String string) {
    return toString(string, asciiIdentifiers);
  }

  public static String toLiteralString(String string) {
    return toString(string, asciiLiteralStrings);
  }

  public static String toQuotedLiteralString(String string) {
    return toString(string, asciiQuotedLiteralStrings);
  }

  public static String readFile(String filename) {
    String inputString = "";
    try {
      byte[] encoded = Files.readAllBytes(Paths.get(filename));
      inputString = Charset.forName("ISO-8859-1").newDecoder().decode(ByteBuffer.wrap(encoded)).toString();
    } catch (FileNotFoundException ex) {
      throw new ARTUncheckedException("Unable to open input file " + filename + "\n");
    } catch (CharacterCodingException e) {
      throw new ARTUncheckedException("Encoding error in input file " + filename + "\n");
    } catch (IOException e) {
      throw new ARTUncheckedException("I/O error whilst attempting to read input file " + filename + "\n");
    }
    return inputString;
  }

  @SuppressWarnings("resource")
  public static void writeFile(String path, String filename, String fileContents) {
    PrintWriter printWriter;

    try {
      printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
    } catch (FileNotFoundException e) {
      throw new ARTUncheckedException("Unable to open text handler on file '" + filename + "'");
    } catch (UnsupportedEncodingException e) {
      throw new ARTUncheckedException("UTF-8 unsupported for writing to file '" + filename + "'");
    }
    printWriter.print(fileContents);
    printWriter.close();
  }

  public static void printFatal(String message) {
    System.out.print("\nFatal error: " + message + "\n");
    System.exit(1);
  }

  public ARTText() {
    handler = new ARTTextHandlerConsole();
  }

  public ARTText(ARTTextHandler handler) {
    this.handler = handler;
  }

  public void close() {
    handler.close();
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

  public static String echo(String message, int index, String buffer) {
    String ret = lineNumber(index, buffer) + "," + columnNumber(index, buffer) + " " + message + "\n";
    if (buffer == null || index < 0) return ret;

    ret += String.format("%5d: ", lineNumber(index, buffer));

    int echoColumn = columnNumber(index, buffer);

    int echoIndex = index - echoColumn;
    do {
      ret += buffer.charAt(echoIndex++);
    } while (echoIndex < buffer.length() && buffer.charAt(echoIndex) != '\n' && buffer.charAt(echoIndex) != '\0');

    ret += String.format("\n-------");

    for (int tmp = 1; tmp < echoColumn; tmp++)
      ret += "-";

    ret += "^\n";

    return ret;
  }

  public String conditionalEcho(ARTTextLevel level, int index, String buffer) {
    if (level == ARTTextLevel.FATAL_ECHO || level == ARTTextLevel.ERROR_ECHO || level == ARTTextLevel.WARNING_ECHO || level == ARTTextLevel.INFO_ECHO
        || level == ARTTextLevel.TRACE_ECHO || level == ARTTextLevel.OUTPUT_ECHO)
      return echo("", index, buffer);
    else
      return "";
  }

  /* Output functions with buffer and index paramaters intended to allow error location in messages */

  public void print(ARTTextLevel level, int index, String buffer, String msg) {
    conditionalEcho(level, index, buffer);
    handler.text(level, index, buffer, msg);
  }

  public void println(ARTTextLevel level, int index, String buffer, String msg) {
    conditionalEcho(level, index, buffer);
    handler.text(level, index, buffer, msg + "\n");
  }

  public void printf(ARTTextLevel level, int index, String buffer, String formatString, Object... args) {
    conditionalEcho(level, index, buffer);
    handler.text(level, index, buffer, String.format(formatString, args));
  }

  /* Output functions for simple messages */

  public void print(ARTTextLevel level, String msg) {
    printf(level, 0, null, "%s", msg);
  }

  public void println(ARTTextLevel level, String msg) {
    printf(level, 0, null, "%s", msg + "\n");
  }

  public void printf(ARTTextLevel level, String formatString, Object... args) {
    printf(level, 0, null, formatString, args);
  }

  /* Convenience functions that just send strings to the standard output */

  public void print(String msg) {
    printf(ARTTextLevel.OUTPUT, "%s", msg);
  }

  public void println(String msg) {
    printf(ARTTextLevel.OUTPUT, "%s", msg + "\n");
  }

  public void printf(String formatString, Object... args) {
    print(ARTTextLevel.OUTPUT, String.format(formatString, args));
  }

  public void println() {
    printf(ARTTextLevel.OUTPUT, "%s", "\n");
  }

}