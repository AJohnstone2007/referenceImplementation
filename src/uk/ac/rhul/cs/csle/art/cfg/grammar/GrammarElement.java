package uk.ac.rhul.cs.csle.art.cfg.grammar;

public class GrammarElement implements Comparable<Object> {
  public int ei;
  public final GrammarKind kind;
  public final String str;

  public GrammarElement(GrammarKind kind, String s) {
    super();
    this.kind = kind;
    this.str = s;
  }

  @Override
  public String toString() {
    return ei + ": " + kind + " " + str;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((kind == null) ? 0 : kind.hashCode());
    result = prime * result + ((str == null) ? 0 : str.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    GrammarElement other = (GrammarElement) obj;
    if (kind != other.kind) return false;
    if (str == null) {
      if (other.str != null) return false;
    } else if (!str.equals(other.str)) return false;
    return true;
  }

  @Override
  public int compareTo(Object o) {
    if (o == null) return 1;
    GrammarElement other = (GrammarElement) o;
    if (kind.ordinal() > other.kind.ordinal())
      return 1;
    else if (kind.ordinal() < other.kind.ordinal())
      return -1;
    else
      return str.compareTo(other.str);
  }
}