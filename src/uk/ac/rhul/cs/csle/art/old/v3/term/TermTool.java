package uk.ac.rhul.cs.csle.art.old.v3.term;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import uk.ac.rhul.cs.csle.art.old.core.Version;

public class TermTool {
  static final int variableCount = 15;
  static final int sequenceVariableCount = 7;

  static ITermPool itp = new ITermPool(variableCount, sequenceVariableCount);
  static Map<String, TermToolOperand> termToolVariables = new HashMap<>();
  static BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

  public static void main(String[] args) throws IOException {
    System.out.println("TermTool V" + Version.major() + "." + Version.minor() + " - build " + Version.build() + ": type !?<return> for help\n");

    while (true) {
      System.out.print("> ");
      System.out.flush();
      itp.parserSetup(console.readLine());

      try {
        termTool();
      } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (ARTExceptionTermParser e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  static void termTool() throws FileNotFoundException, ARTExceptionTermParser {
    if (itp.cc == '!') {
      itp.getc();
      switch (itp.cc) {
      case '?':
        itp.getc();
        help();
        break;
      case '#':
        itp.getc();
        showVariables();
        break;
      case '$':
        itp.getc();
        showStatistics();
        break;
      case '>':
        itp.getc();
        itp.ws();
        if (Character.isWhitespace(itp.cc)) // must be at end of line
          dump(System.out);
        else {
          String filename = termToolFilename();
          if (filename.equals(""))
            dump(System.out);
          else {
            PrintStream out = new PrintStream(filename);
            dump(out);
            out.close();
            System.out.println("Terms written to file '" + filename + "'");
          }
        }
        break;
      case '<':
        itp.getc();
        undump();
        break;
      case '-':
        itp.getc();
        try {
          clear();
        } catch (IOException e) {
          throw new ARTExceptionTermParser("Unable to open console for confirmation", itp.cp, itp.input);
        }
        break;
      case '^':
        itp.getc();
        garbageCollect();
        break;
      case '@':
        itp.getc();
        take();
        break;
      case '.':
        itp.getc();
        System.exit(0);
        break;
      default:
        throw new ARTExceptionTermParser("Unknown command !" + itp.cc, itp.cp, itp.input);
      }
      itp.ws();
    } else {
      itp.ws();
      if (itp.cc != 0) System.out.println(expression()); // Ignore blank linesg
    }
  }

  private static TermToolOperand expression() throws ARTExceptionTermParser {
    TermToolOperand left = operand(), right;
    if (itp.cc == ':') {
      itp.getc();
      if (itp.cc != '=') throw new ARTExceptionTermParser("Expecting =" + itp.cc, itp.cp, itp.input);
      if (left.name == null || left.name.charAt(0) != '#')
        throw new ARTExceptionTermParser("Left hand side of := must be a # variable" + itp.cc, itp.cp, itp.input);
      itp.getc();
      itp.ws();
      if (itp.cc == 0) {// secial case - no expression so delete variable
        right = termToolVariables.get(left.name); // we shall return what was there
        termToolVariables.remove(left.name);
      } else {
        right = expression();
        if (right.name != null) right = dereference(right.name);
        termToolVariables.put(left.name, right);
      }
      return right;
    } else if (itp.cc == '+') {
      itp.getc();
      if (itp.cc != '=') throw new ARTExceptionTermParser("Expecting =" + itp.cc, itp.cp, itp.input);
      if (left.name == null || left.name.charAt(0) != '#')
        throw new ARTExceptionTermParser("Left hand side of += must be a # variable" + itp.cc, itp.cp, itp.input);
      itp.getc();
      itp.ws();
      right = expression();
      if (right.name != null) right = dereference(right.name);
      if (right.bindings == null) throw new ARTExceptionTermParser("Right hand side of += must be a set of bindings" + itp.cc, itp.cp, itp.input);
      TermToolOperand currentVariableContents;
      if ((currentVariableContents = termToolVariables.get(left.name)) == null)
        termToolVariables.put(left.name, right); // Fresh variable on LHS else
      else {
        if (currentVariableContents.bindings == null)
          throw new ARTExceptionTermParser("Left hand side of += does not reference a set of bindings" + itp.cc, itp.cp, itp.input);
        for (int i = 0; i < currentVariableContents.bindings.length; i++) {
          if (right.bindings[i] != 0 && currentVariableContents.bindings[i] != 0)
            throw new ARTExceptionTermParser("Term variable _" + i + "is defined on both sides of +=" + itp.cc, itp.cp, itp.input);
          if (right.bindings[i] != 0) currentVariableContents.bindings[i] = right.bindings[i];
        }
      }
      return right;
    } else {
      while (itp.cc == '|' || itp.cc == '<') {
        // System.out.println("In expression while loop at cp = " + itp.cp + " and cc = '" + itp.cc + "'");
        if (itp.cc == '|') {
          itp.getc();
          if (itp.cc != ('>')) throw new ARTExceptionTermParser("Unknown operator |" + itp.cc, itp.cp, itp.input);
          itp.getc();
          itp.ws();
          right = operand();
          if (left.name != null) left = dereference(left.name);
          if (right.name != null) right = dereference(right.name);
          if (left.term == 0 || right.term == 0) throw new ARTExceptionTermParser("Both sides of |> operator must be terms", itp.cp, itp.input);
          int[] bindings = new int[16];
          boolean matched = itp.matchZeroSV(left.term, right.term, bindings);
          // System.out.println("Match " + left + " to " + right + " returned " + matched + " and bindings " + bindings);
          if (!matched) throw new ARTExceptionTermParser("|> operator failed to find match", itp.cp, itp.input);
          left.term = 0;
          left.bindings = bindings;
        } else {
          itp.getc();
          if (itp.cc != ('|')) throw new ARTExceptionTermParser("Unknown operator <" + itp.cc, itp.cp, itp.input);
          itp.getc();
          itp.ws();
          right = operand();
          if (left.name != null) left = dereference(left.name); // dereference
          if (right.name != null) right = dereference(right.name); // dereference

          if (left.bindings == null) throw new ARTExceptionTermParser("Left hand side of |> operator must be a set of bindings", itp.cp, itp.input);
          if (right.term == 0) throw new ARTExceptionTermParser("Right hand side of |> operator must be a term", itp.cp, itp.input);
          TermToolOperand tmp = new TermToolOperand(itp);
          tmp.term = itp.substitute(left.bindings, right.term);
          // System.out.println("Substitute " + left + " into " + right + " returned " + tmp);
          left = tmp;
        }
      }
      if (itp.cc != 0) throw new ARTExceptionTermParser("Unexpected '" + itp.cc + "'", itp.cp, itp.input);
      // Special case of tool variable on its own on the line
      if (left.name != null) left = dereference(left.name);
      return left;
    }
  }

  private static TermToolOperand dereference(String name) throws ARTExceptionTermParser {
    TermToolOperand ret = termToolVariables.get(name);
    if (ret == null) throw new ARTExceptionTermParser("Undefined variable " + name, itp.cp, itp.input);
    return new TermToolOperand(ret);
  }

  private static TermToolOperand operand() throws ARTExceptionTermParser {
    TermToolOperand ret = new TermToolOperand(itp);
    if (itp.cc == '(') {
      itp.ws();
      itp.getc();
      ret = expression();
      if (itp.cc == ')') throw new ARTExceptionTermParser("Expecting ')'", itp.cp, itp.input);
      itp.getc();
      itp.ws();
    } else if (itp.cc == '#') {
      ret.name = termToolName();
    } else {
      ret.term = itp.term();
    }
    return ret;
  }

  private static String termToolName() throws ARTExceptionTermParser {
    if (itp.cc != '#') throw new ARTExceptionTermParser("internal error: termToolName must begin with #", itp.cp, itp.input);
    int lexemeStart = itp.cp - 1;
    itp.getc();
    while (Character.isAlphabetic(itp.cc) || Character.isDigit(itp.cc))
      itp.getc();
    int lexemeEnd = itp.cp - 1;
    itp.ws();
    return itp.input.substring(lexemeStart, lexemeEnd);
  }

  private static String termToolFilename() {
    int lexemeStart = itp.cp - 1;
    while (!Character.isWhitespace(itp.cc) && itp.cc != (char) 0)
      itp.getc();
    int lexemeEnd = itp.cp - 1;
    itp.ws();
    return itp.input.substring(lexemeStart, lexemeEnd);
  }

  private static void help() {
    System.out.println("TermTool V" + Version.major() + "." + Version.minor() + " - build " + Version.build());
    System.out.println("\nCommand lines have a ! character in column 1");
    System.out.println("!?             Help (this message)");
    System.out.println("!#             Show variables");
    System.out.println("!$             Show table statistics");
    System.out.println("!>             Show tables");
    System.out.println("!> filename    Output tables to a dump file");
    System.out.println("!< filename    Update tables from a dump file");
    System.out.println("!-             Delete contents of tables");
    System.out.println("!^             Remove unreachable elements from tables and compact");
    System.out.println("!@ filename    Read commands from file");
    System.out.println("!.             Exit");
    System.out.println("\nExpression examples");
    System.out.println("A(B, c)");
    System.out.println("#X := A(B, c)");
    System.out.println("#Y := A(B, c) |> A(_1, _)");
    System.out.println("#Z := A(B, c) |> A(_1, _)  <| P(_1, _1)");
    System.out.println("#P := #Y <| P(_1, _1)");
    System.out.println("#Q := #Y");
    System.out.println("#Q += A(B,c) |> A(_2, _3)");
  }

  static void showStatistics() {
    System.out.println("String entries: " + itp.stringCardinality() + ", requiring " + itp.stringBytes() + " byte" + (itp.stringBytes() == 1 ? "" : "s"));
    int width = 1, pow = 2;
    while (pow < itp.termCardinality()) {
      width++;
      pow *= 2;
    }

    System.out.println(
        "Term entries: " + itp.termCardinality() + ", requiring " + itp.termBytes() + " words of minimum size " + width + " bit" + (width == 1 ? "" : "s"));
  }

  static void showVariables() {
    if (termToolVariables.isEmpty())
      System.out.println("No TermTool variables defined");
    else
      for (String k : termToolVariables.keySet())
        System.out.println(k + " = " + termToolVariables.get(k));
  }

  private static void dump(PrintStream out) {
    itp.dump(out);
  }

  private static void undump() throws ARTExceptionTermParser {
    throw new ARTExceptionTermParser("!< (undump) command not yet implemented" + itp.cc, itp.cp, itp.input);
  }

  private static void clear() throws IOException {
    System.out.println("Really clear tables? ");
    String command = console.readLine();
    if (command.length() == 1 && command.charAt(0) == 'y') {
      itp = new ITermPool(variableCount, 0);
      termToolVariables = new HashMap<>();
    } else
      System.out.println("Clear command ignored");
  }

  private static void garbageCollect() throws ARTExceptionTermParser {
    throw new ARTExceptionTermParser("!^ (garbage collect) command not yet implemented" + itp.cc, itp.cp, itp.input);
  }

  private static void take() throws ARTExceptionTermParser {
    throw new ARTExceptionTermParser("!@ (take) command not yet implemented" + itp.cc, itp.cp, itp.input);
  }
}
