package uk.ac.rhul.cs.csle.art.v3.term;

import java.util.Arrays;

public class TermToolOperand {
  /*
   * In termtool, expressions may return either a term or a set of bindings
   *
   * This class encapsulates the union of those two possibilities. The type of an operand is discovered by checking to see which of name or bindings is
   * nonb-null
   *
   */

  String name = null;
  int[] bindings = null;
  private final ITermPool iTermPool;

  @Override
  public String toString() {
    if (name != null) return "#" + name;
    if (term != 0) return "" + iTermPool.toString(term);
    if (bindings == null) return "!!! NULL BINDINGS !!!";
    for (int i = 0; i < bindings.length; i++)
      if (bindings[i] != 0) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        for (int j = 0; j < bindings.length; j++)
          if (bindings[j] != 0) sb.append("_" + j + "->" + iTermPool.toString(bindings[j]) + " ");
        sb.append("}");
        return sb.toString();
      }
    return "{ }";
  }

  int term = 0;

  TermToolOperand(ITermPool iTermPool) {
    this.iTermPool = iTermPool;
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
    iTermPool = op.iTermPool;
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
