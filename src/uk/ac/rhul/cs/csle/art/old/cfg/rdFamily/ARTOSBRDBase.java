package uk.ac.rhul.cs.csle.art.old.cfg.rdFamily;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class ARTOSBRDBase {
  protected String input;
  protected int cc;
  protected int co;
  int ts;
  int te;
  int oracleLength;
  protected int oracle[];
  int nextNode;
  PrintWriter dotFile;
  protected TreeNode rdt;

  public enum TreeKind {
    EPSILON, TERMINAL, BUILTIN, NONTERMINAL
  };

  public enum GIFTKind {
    NONE, FOLD_UNDER, FOLD_OVER, FOLD_ABOVE
  };

  public class TreeNode {
    String label;
    int nodeNumber;
    TreeNode child;
    TreeNode sibling;
    TreeKind kind;
    GIFTKind giftOp;
    // String attribute;

    public TreeNode(String label, TreeNode child, TreeNode previousSibling, TreeKind kind, GIFTKind giftOp) {
      if (previousSibling != null) previousSibling.sibling = this;
      this.label = label;
      this.child = child;
      this.sibling = null;
      this.kind = kind;
      this.giftOp = giftOp;
      nodeNumber = nextNode++;
    };

    public TreeNode(TreeNode old) {
      label = old.label;
      kind = old.kind;
      giftOp = old.giftOp;
      child = sibling = null;
      nodeNumber = nextNode++;
    };

    public TreeNode clone(TreeNode parent, TreeNode previousSibling) {
      TreeNode ret = new TreeNode(this);
      if (previousSibling != null)
        previousSibling.sibling = ret;
      else if (parent != null) parent.child = ret;
      TreeNode rightNode = null;
      for (TreeNode srcNode = child; srcNode != null; srcNode = srcNode.sibling)
        rightNode = srcNode.clone(ret, rightNode);
      return ret;
    }

    public TreeNode evaluateTIF(TreeNode parent, TreeNode previousSibling, boolean parentSuppressed) {

      if (parent != null && (giftOp == GIFTKind.FOLD_UNDER || giftOp == GIFTKind.FOLD_OVER)) // Special case: don't promote root node
      {
        /* Link the children in to the previousSibling's chain */
        TreeNode rightNode = null;
        if (previousSibling != null)
          rightNode = previousSibling;
        else if (parent != null) rightNode = parent.child;

        boolean suppress = giftOp == GIFTKind.FOLD_UNDER || (parentSuppressed && giftOp == GIFTKind.FOLD_OVER);

        for (TreeNode srcNode = child; srcNode != null; srcNode = srcNode.sibling)
          rightNode = srcNode.evaluateTIF(parent, rightNode, suppress);

        if (giftOp == GIFTKind.FOLD_OVER && !parentSuppressed) {
          parent.label = label;
          parent.kind = kind;
        }

        return rightNode;
      } else { /* make a new node and scan our children */
        TreeNode ret = new TreeNode(this);
        ret.giftOp = GIFTKind.NONE;
        if (previousSibling != null)
          previousSibling.sibling = ret;
        else if (parent != null) parent.child = ret;
        TreeNode rightNode = null;
        for (TreeNode srcNode = child; srcNode != null; srcNode = srcNode.sibling)
          rightNode = srcNode.evaluateTIF(ret, rightNode, false);
        return ret;
      }
    }

    public void foldunderEpsilon() {
      if (kind == TreeKind.EPSILON /* && child != null */) // remove all epsilons!
        giftOp = GIFTKind.FOLD_UNDER;
      for (TreeNode srcNode = child; srcNode != null; srcNode = srcNode.sibling)
        srcNode.foldunderEpsilon();
    }

    public void printTree(int indent) {
      System.out.printf("%d: ", nodeNumber);
      for (int temp = 0; temp < indent; temp++)
        System.out.printf("  ");
      System.out.printf("%s%s%s", labelPreString(kind), label, labelPostString(kind));
      System.out.printf("%s\n", giftString(giftOp));

      if (child != null) child.printTree(indent + 1);
      if (sibling != null) sibling.printTree(indent);
    };

    public void printTerm(int indent) {
      System.out.printf("%s%s%s", labelPreString(kind), label, labelPostString(kind));

      if (child != null) {
        System.out.print("(");
        child.printTerm(++indent);
      }
      if (sibling != null) {
        System.out.print(",");
        sibling.printTerm(indent);
      } else
        System.out.print(")");
    };

    void dotRec(TreeNode parent) {
      dotFile.printf("\nv_%d [label=\"%d: %s%s%s\"]", nodeNumber, nodeNumber, labelPreString(kind), label, labelPostString(kind));

      if (parent != null) dotFile.printf("\nv_%d->v_%d", parent.nodeNumber, nodeNumber);
      if (child != null) child.dotRec(this);
      if (sibling != null) sibling.dotRec(parent);
    };

    public void dot(String fileName) throws FileNotFoundException {
      dotFile = new PrintWriter(fileName);
      dotFile.printf("digraph rd{");
      dotFile.printf("node [fontname=\"Arial\" shape=box]");
      this.dotRec(null);
      dotFile.printf("\n}");
      dotFile.close();
    };
  };

  protected ARTOSBRDBase() {
    oracleLength = 1000;
    oracle = new int[oracleLength];
    cc = co = 0;
    nextNode = 1;
  }

  protected void postParse(TreeNode tree) {
  };

  protected String giftString(GIFTKind giftOp) {
    switch (giftOp) {
    case NONE:
      return "";
    case FOLD_UNDER:
      return "^";
    case FOLD_OVER:
      return "^^";
    case FOLD_ABOVE:
      return "^^^";
    default:
      return "!! unknown TIF operator!!";
    }
  }

  protected String labelPreString(TreeKind kind) {
    switch (kind) {
    case EPSILON:
      return "";
    case TERMINAL:
      return "'";
    case BUILTIN:
      return "&";
    case NONTERMINAL:
      return "";
    default:
      return "!! unknown TREE kind !!";
    }
  }

  protected String attributeString() {
    return input.substring(ts, te);
  }

  protected String labelPostString(TreeKind kind) {
    switch (kind) {
    case EPSILON:
      return "";
    case TERMINAL:
      return "'";
    case BUILTIN:
      return "";
    case NONTERMINAL:
      return "";
    default:
      return "!! unknown TREE kind !!";
    }
  }

  protected String readInput(String filename) throws FileNotFoundException {
    return new Scanner(new File(filename)).useDelimiter("\\Z").next() + "\0";
  }

  protected void oracleSet(int i) {
    if (co == oracleLength) {
      int oracleLengthOld = oracleLength;
      oracleLength += oracleLength / 2;
      int newOracle[] = new int[oracleLength];
      System.arraycopy(oracle, 0, newOracle, 0, oracleLengthOld);
      oracle = newOracle;
      /* DEBUG: */ System.out.printf("resized oracle from %d to %d\n", oracleLengthOld, oracleLength);
    }
    oracle[co++] = i;
  }

  protected boolean match(String s) {
    /* DEBUG System.out.printf("At %d '%c' match %s\n", cc, input.charAt(cc), s); */

    /* DEBUG System.out.printf("cc=%d s = '%s' length = %d\n", cc, s, s.length()); */

    if (input.regionMatches(cc, s, 0, s.length())) {
      cc += s.length();
      /* DEBUG System.out.printf("successful match\n"); */
      builtIn_WHITESPACE();
      return true;
    }

    return false;
  }

  protected boolean builtIn_ID() {
    /* DEBUG System.out.printf("At %d '%c' builtin_ID\n", cc, input.charAt(cc)); */
    if (!Character.isJavaIdentifierStart(input.charAt(cc))) return false;

    ts = cc++;

    while (Character.isJavaIdentifierPart(input.charAt(cc)))
      cc++;

    te = cc;

    /* DEBUG System.out.printf("Matched substring %d-%d\n", ts, te); */

    builtIn_WHITESPACE();

    return true;
  }

  protected boolean builtIn_WHITESPACE() {
    /* DEBUG System.out.printf("At %d '%c' builtin_WHITESPACE\n", cc, input.charAt(cc)); */

    while (Character.isWhitespace(input.charAt(cc)))
      cc++;

    /* DEBUG System.out.printf("Advanced to %d\n", cc); */
    return true;
  }

  protected boolean isxdigit(char c) {
    if (Character.isDigit(c)) return true;
    if (c >= 'a' && c <= 'f') return true;
    if (c >= 'A' && c <= 'F') return true;
    return false;
  }

  protected boolean builtIn_INTEGER() {
    if (!Character.isDigit(input.charAt(cc))) return false;

    ts = cc;

    /* Check for hexadecimal introducer */
    boolean hex = (input.charAt(cc) == '0' && (input.charAt(cc + 1) == 'x' || input.charAt(cc + 1) == 'X'));

    if (hex) cc += 2; // Skip over hex introducer

    /* Now collect decimal or hex digits */
    while (hex ? isxdigit(input.charAt(cc)) : Character.isDigit(input.charAt(cc)))
      cc++;

    te = cc;

    builtIn_WHITESPACE();

    return true;
  }

  protected boolean builtIn_REAL() {
    if (!Character.isDigit(input.charAt(cc))) return false;

    ts = cc;

    while (Character.isDigit(input.charAt(cc)))
      cc++;

    if (input.charAt(cc) != '.') return true;

    cc++; // skip .

    while (Character.isDigit(input.charAt(cc)))
      cc++;

    if (input.charAt(cc) == 'e' || input.charAt(cc) == 'E') {
      cc++;

      while (Character.isDigit(input.charAt(cc)))
        cc++;
    }

    te = cc;

    builtIn_WHITESPACE();

    return true;
  }

  protected boolean builtIn_CHAR_SQ() {
    if (input.charAt(cc) != '\'') return false;

    cc++;

    ts = cc;

    if (input.charAt(cc) == '\\') cc++;

    cc++;

    if (input.charAt(cc) != '\'') return false;

    te = cc;

    cc++; // skip past final delimiter

    builtIn_WHITESPACE();

    return true;
  }

  protected boolean builtIn_STRING_SQ() {
    if (input.charAt(cc) != '\'') return false;

    ts = cc + 1;

    do {
      if (input.charAt(cc) == '\\') cc++;

      cc++;
    } while (input.charAt(cc) != '\'');

    te = cc;

    cc++; // skip past final delimiter

    builtIn_WHITESPACE();

    return true;
  }

  protected boolean builtIn_STRING_DQ() {
    if (input.charAt(cc) != '"') return false;

    ts = cc + 1;

    do {
      if (input.charAt(cc) == '\\') cc++;
      cc++;
    } while (input.charAt(cc) != '"');

    te = cc;
    cc++; // skip past final delimiter

    builtIn_WHITESPACE();

    return true;
  }

  protected boolean builtIn_ACTION() {
    if (!(input.charAt(cc) == '[' && input.charAt(cc + 1) == '*')) return false;

    cc += 2;

    ts = cc;

    while (true) {
      if (input.charAt(cc) == 0) break;

      if (input.charAt(cc) == '*' && input.charAt(cc) == ']') {
        cc += 2;
        break;
      }

      cc++;
    }

    te = cc - 2;

    builtIn_WHITESPACE();

    return true;
  }
}
