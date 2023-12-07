package uk.ac.rhul.cs.csle.art.term;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//TODO: check that left operand is closed
//TODO: check that the children of term variables are silently ignored
//TODO: check that patterns are linear
//TODO: implement undump !<
//TODO: implement garbage collect !^
//TODO: implement take command !@
//TODO: finish value system

public class TermTool {
  final int variableCount = 15;
  final int sequenceVariableCount = 7;

  ITerms iTerms;
  Map<String, TermToolOperand> termToolVariables = new HashMap<>();
  BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
  boolean exit = false;

  public TermTool(ITerms iterms) {
    this.iTerms = iterms;
    System.out.println("TermTool: type !?<return> for help\n");

    while (!exit) {
      System.out.print("> ");
      System.out.flush();
      try {
        iTerms.parserSetup(console.readLine());
        termToolProcessLine();
      } catch (ValueException | IOException e) {
        iTerms.syntaxError(e.getMessage());
      }

    }
  }

  class TermToolOperand {
    /*
     * In termtool, expressions may return either a term or a set of bindings
     *
     * This class encapsulates the union of those two possibilities. The type of an operand is discovered by checking to see which of name or bindings is
     * non-null
     *
     */

    String name = null;
    int[] bindings = null;

    @Override
    public String toString() {
      if (name != null) return "#" + name;
      if (term != 0) return "" + iTerms.toString(term);
      if (bindings == null) return "!!! NULL BINDINGS !!!";
      for (int i = 0; i < bindings.length; i++)
        if (bindings[i] != 0) {
          StringBuilder sb = new StringBuilder();
          sb.append("{ ");
          for (int j = 0; j < bindings.length; j++)
            if (bindings[j] != 0) sb.append("_" + j + "->" + iTerms.toString(bindings[j]) + " ");
          sb.append("}");
          return sb.toString();
        }
      return "{ }";
    }

    int term = 0;

    TermToolOperand() {
    }

    TermToolOperand(TermToolOperand op) {
      name = op.name;
      if (op.bindings == null)
        bindings = op.bindings;
      else {
        bindings = new int[op.bindings.length];
        for (int i = 0; i < bindings.length; i++)
          bindings[i] = op.bindings[i];
      }
      term = op.term;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode(bindings);
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + term;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      TermToolOperand other = (TermToolOperand) obj;
      if (!Arrays.equals(bindings, other.bindings)) return false;
      if (name == null) {
        if (other.name != null) return false;
      } else if (!name.equals(other.name)) return false;
      if (term != other.term) return false;
      return true;
    }
  }

  void termToolProcessLine() throws FileNotFoundException, ValueException, ValueException {
    if (iTerms.cc == '!') {
      iTerms.getc();
      switch (iTerms.cc) {
      case '?':
        iTerms.getc();
        help();
        break;
      case '#':
        iTerms.getc();
        showVariables();
        break;
      case '$':
        iTerms.getc();
        showStatistics();
        break;
      case '>':
        iTerms.getc();
        iTerms.ws();
        if (Character.isWhitespace(iTerms.cc)) // must be at end of line
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
        iTerms.getc();
        undump();
        break;
      case '-':
        iTerms.getc();
        try {
          clear();
        } catch (IOException e) {
          throw new ValueException("Unable to open console for confirmation");
        }
        break;
      case '^':
        iTerms.getc();
        garbageCollect();
        break;
      case '@':
        iTerms.getc();
        take();
        break;
      case '.':
        iTerms.getc();
        exit = true;
        break;
      default:
        throw new ValueException("Unknown command !");
      }
      iTerms.ws();
    } else {
      iTerms.ws();
      if (iTerms.cc != 0) System.out.println(expression()); // Ignore blank linesg
    }
  }

  private TermToolOperand expression() throws ValueException, ValueException {
    TermToolOperand left = operand(), right;
    if (iTerms.cc == ':') {
      iTerms.getc();
      if (iTerms.cc != '=') throw new ValueException("Expecting =");
      if (left.name == null || left.name.charAt(0) != '#') throw new ValueException("Left hand side of := must be a # variable");
      iTerms.getc();
      iTerms.ws();
      if (iTerms.cc == 0) {// special case - no expression so delete variable
        right = termToolVariables.get(left.name); // we shall return what was there
        termToolVariables.remove(left.name);
      } else {
        right = expression();
        if (right.name != null) right = dereference(right.name);
        termToolVariables.put(left.name, right);
      }
      return right;
    } else if (iTerms.cc == '+') {
      iTerms.getc();
      if (iTerms.cc != '=') throw new ValueException("Expecting =");
      if (left.name == null || left.name.charAt(0) != '#') throw new ValueException("Left hand side of += must be a # variable");
      iTerms.getc();
      iTerms.ws();
      right = expression();
      if (right.name != null) right = dereference(right.name);
      if (right.bindings == null) throw new ValueException("Right hand side of += must be a set of bindings");
      TermToolOperand currentVariableContents;
      if ((currentVariableContents = termToolVariables.get(left.name)) == null)
        termToolVariables.put(left.name, right); // Fresh variable on LHS else
      else {
        if (currentVariableContents.bindings == null) throw new ValueException("Left hand side of += does not reference a set of bindings");
        for (int i = 0; i < currentVariableContents.bindings.length; i++) {
          if (right.bindings[i] != 0 && currentVariableContents.bindings[i] != 0)
            throw new ValueException("Term variable _" + i + "is defined on both sides of +=");
          if (right.bindings[i] != 0) currentVariableContents.bindings[i] = right.bindings[i];
        }
      }
      return right;
    } else {
      while (iTerms.cc == '|' || iTerms.cc == '<') {
        // System.out.println("In expression while loop at cp = " + itp.cp + " and cc = '" + itp.cc + "'");
        if (iTerms.cc == '|') {
          iTerms.getc();
          if (iTerms.cc != ('>')) throw new ValueException("Unknown operator |");
          iTerms.getc();
          iTerms.ws();
          right = operand();
          if (left.name != null) left = dereference(left.name);
          if (right.name != null) right = dereference(right.name);
          if (left.term == 0 || right.term == 0) throw new ValueException("Both sides of |> operator must be terms");
          int[] bindings = new int[16];
          boolean matched = iTerms.matchZeroSV(left.term, right.term, bindings);
          // System.out.println("Match " + left + " to " + right + " returned " + matched + " and bindings " + bindings);
          if (!matched) throw new ValueException("|> operator failed to find match");
          left.term = 0;
          left.bindings = bindings;
        } else {
          iTerms.getc();
          if (iTerms.cc != ('|')) throw new ValueException("Unknown operator <");
          iTerms.getc();
          iTerms.ws();
          right = operand();
          if (left.name != null) left = dereference(left.name); // dereference
          if (right.name != null) right = dereference(right.name); // dereference

          if (left.bindings == null) throw new ValueException("Left hand side of <| operator must be a set of bindings");
          if (right.term == 0) throw new ValueException("Right hand side of <| operator must be a term");
          TermToolOperand tmp = new TermToolOperand();
          tmp.term = iTerms.substitute(left.bindings, right.term, 0);
          System.out.println("Substituting " + left + " into " + right + " yields " + tmp);
          left = tmp;
        }
      }
      if (iTerms.cc != 0) throw new ValueException("Unexpected '" + iTerms.cc + "'");
      // Special case of tool variable on its own on the line
      if (left.name != null) left = dereference(left.name);
      return left;
    }
  }

  private TermToolOperand dereference(String name) throws ValueException {
    TermToolOperand ret = termToolVariables.get(name);
    if (ret == null) throw new ValueException("Undefined variable " + name);
    return new TermToolOperand(ret);
  }

  private TermToolOperand operand() throws ValueException, ValueException {
    TermToolOperand ret = new TermToolOperand();
    if (iTerms.cc == '(') {
      iTerms.ws();
      iTerms.getc();
      ret = expression();
      if (iTerms.cc == ')') throw new ValueException("Expecting ')'");
      iTerms.getc();
      iTerms.ws();
    } else if (iTerms.cc == '#') {
      ret.name = termToolName();
    } else {
      ret.term = iTerms.term();
    }
    return ret;
  }

  private String termToolName() throws ValueException {
    if (iTerms.cc != '#') throw new ValueException("internal error: termToolName must begin with #");
    int lexemeStart = iTerms.cp - 1;
    iTerms.getc();
    while (Character.isAlphabetic(iTerms.cc) || Character.isDigit(iTerms.cc))
      iTerms.getc();
    int lexemeEnd = iTerms.cp - 1;
    iTerms.ws();
    return iTerms.input.substring(lexemeStart, lexemeEnd);
  }

  private String termToolFilename() {
    int lexemeStart = iTerms.cp - 1;
    while (!Character.isWhitespace(iTerms.cc) && iTerms.cc != (char) 0)
      iTerms.getc();
    int lexemeEnd = iTerms.cp - 1;
    iTerms.ws();
    return iTerms.input.substring(lexemeStart, lexemeEnd);
  }

  private void help() {
    System.out.println("TermTool");
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
    System.out.println("#Z := A |> A");
    System.out.println("#Z <| __add(__int32(10), __int32(4))");
  }

  void showStatistics() {
    System.out.println("String entries: " + iTerms.stringCardinality() + ", requiring " + iTerms.getStringTotalBytes() + " byte"
        + (iTerms.getStringTotalBytes() == 1 ? "" : "s"));
    int width = 1, pow = 2;
    while (pow < iTerms.termCardinality()) {
      width++;
      pow *= 2;
    }

    System.out.println("Term entries: " + iTerms.termCardinality() + ", requiring " + iTerms.termBytes() + " words of minimum size " + width + " bit"
        + (width == 1 ? "" : "s"));
  }

  void showVariables() {
    if (termToolVariables.isEmpty())
      System.out.println("No TermTool variables defined");
    else
      for (String k : termToolVariables.keySet())
        System.out.println(k + " = " + termToolVariables.get(k));
  }

  private void dump(PrintStream out) {
    iTerms.dump(out);
  }

  private void undump() throws ValueException {
    throw new ValueException("!< (undump) command not yet implemented");
  }

  private void clear() throws IOException {
    System.out.println("Really clear tables? ");
    String command = console.readLine();
    if (command.length() == 1 && command.charAt(0) == 'y') {
      iTerms = new ITermsLowLevelAPI();
      termToolVariables = new HashMap<>();
    } else
      System.out.println("Clear command ignored");
  }

  private void garbageCollect() throws ValueException {
    throw new ValueException("!^ (garbage collect) command not yet implemented");
  }

  private void take() throws ValueException {
    throw new ValueException("!@ (take) command not yet implemented");
  }
}
