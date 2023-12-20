package uk.ac.rhul.cs.csle.art.term;

import java.util.HashMap;
import java.util.Map;

/* This extension to TermTraverser adds text specific functions that allow a traverser to build a String rendering of a term, taking into account aliases
*/

public class TermTraverserText extends TermTraverser {
  private final Map<Integer, Integer> globalAliases = new HashMap<>();
  private Map<Integer, Integer> localAliases;
  public final StringBuilder sb = new StringBuilder();
  private int depthLimit = -1;
  private boolean indent = false;

  public TermTraverserText(ITerms iTerms) {
    super(iTerms);
  }

  //@formatter:off
  public void addAction(String symbol, String preorder, String inorder, String postorder) {
    addAction(symbol,
        (preorder == null ? null : (Integer t) -> sb.append(preorder)),
        (inorder == null ? null : (Integer t) -> sb.append(inorder)),
        (postorder == null ? null : (Integer t) -> sb.append(postorder)));
  }
  //@formatter:on

  public void addBreakAndAction(String symbol, String preorder, String inorder, String postorder) {
    addBreak(symbol);
    addAction(symbol, preorder, inorder, postorder);
  }

  public void addGlobalAlias(Integer key, Integer value) {
    globalAliases.put(key, value);
  }

  public int childSymbolIndex(int root, int childNumber) {
    return iTerms.getTermChildren(root)[childNumber];
  }

  public String childSymbolString(int root, int childNumber) {
    return iTerms.getTermSymbolString(childSymbolIndex(root, childNumber));
  }

  public String childStrippedSymbolString(int root, int childNumber) {
    String str = childSymbolString(root, childNumber);
    return str.substring(1, str.length() - 1);
  }

  public void clear() {
    sb.setLength(0);
  }

  public void appendAlias(int stringIndex) {
    Integer candidate = null;

    // First try local aliases
    if (localAliases != null) candidate = localAliases.get(stringIndex);

    // If we haven't found a string yet, try the global aliases
    if (candidate == null && globalAliases != null) candidate = globalAliases.get(stringIndex);

    // If we haven't found an alias yet, then just use original stringIndex; append correspondong string
    sb.append(iTerms.getString(candidate == null ? stringIndex : candidate));
  }

  public void append(String string) {
    sb.append(string);
  }

  public String getString() {
    return sb.toString();
  }

  public void traverse(int termIndex, int depth) {
    // System.out.println(name + " at " + termIndex + " " + iTerms.getTermSymbolString(termIndex) + " with string index " +
    // iTerms.getTermSymbolIndex(termIndex));
    if (indent) {
      sb.append("\n");
      for (int i = 0; i < depth; i++)
        sb.append("   ");
    }
    perform(opsPreorder, termIndex);
    if (depthLimit >= 0 && depth >= depthLimit)
      sb.append("..");
    else {
      int[] children = iTerms.getTermChildren(termIndex);
      int length = children.length;
      int lengthLessOne = length - 1;
      if (!breakSet.contains(iTerms.getTermSymbolIndex(termIndex))) for (int i = 0; i < length; i++) {
        traverse(children[i], depth + 1);
        if (i < lengthLessOne) perform(opsInorder, termIndex);
      }
    }
    perform(opsPostorder, termIndex);
  }

  public String toString(Integer term) {
    return toString(term, false, -1, null);
  }

  public String toString(Integer term, Boolean indent, Integer depthLimit, Map<Integer, Integer> localAliases) {
    this.localAliases = localAliases;
    this.indent = indent;
    this.depthLimit = depthLimit;
    clear();
    traverse(term, 0);
    this.depthLimit = -1;
    this.localAliases = null;
    return sb.toString();
  }
}
