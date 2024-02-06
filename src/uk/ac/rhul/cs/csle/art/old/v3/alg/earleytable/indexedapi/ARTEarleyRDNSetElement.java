package uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.indexedapi;

public class ARTEarleyRDNSetElement {
  private final int nonterminal;
  private final int inputIndex;

  public ARTEarleyRDNSetElement(int nonterminal, int inputIndex) {
    this.nonterminal = nonterminal;
    this.inputIndex = inputIndex;
  }

  @Override
  public String toString() {
    return "(" + nonterminal + ", " + inputIndex + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + inputIndex;
    result = prime * result + nonterminal;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTEarleyRDNSetElement)) return false;
    ARTEarleyRDNSetElement other = (ARTEarleyRDNSetElement) obj;
    if (inputIndex != other.inputIndex) return false;
    if (nonterminal != other.nonterminal) return false;
    return true;
  }
}
