package uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.indexedapi;

/* The NFA tracersing Earley parser works by building sets of state, index pairs
 * In this implementation, ARTEarleyParseConfirguration represents such a pair
 */

public class ARTEarleyConfiguration {
  private final int nfaVertex;
  private final int inputIndex;

  public ARTEarleyConfiguration(int nfaVertex, int inputIndex) {
    super();
    this.nfaVertex = nfaVertex;
    this.inputIndex = inputIndex;
  }

  public int getInputIndex() {
    return inputIndex;
  }

  public int getNfaVertex() {
    return nfaVertex;
  }

  @Override
  public String toString() {
    return "(G" + nfaVertex + ", " + inputIndex + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + inputIndex;
    result = prime * result + nfaVertex;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTEarleyConfiguration)) return false;
    ARTEarleyConfiguration other = (ARTEarleyConfiguration) obj;
    if (inputIndex != other.inputIndex) return false;
    if (nfaVertex != other.nfaVertex) return false;
    return true;
  }
}
