package uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.linkedapi;

import uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.support.ARTEarleyNFAVertex;

/* The NFA tracersing Earley parser works by building sets of state, index pairs
 * In this implementation, ARTEarleyParseConfirguration represenst such a pair
 */

public class ARTEarleyConfiguration {
  private final ARTEarleyNFAVertex nfaVertex;
  private final int inputIndex;

  public ARTEarleyConfiguration(ARTEarleyNFAVertex nfaVertex, int inputIndex) {
    super();
    this.nfaVertex = nfaVertex;
    this.inputIndex = inputIndex;
  }

  public int getInputIndex() {
    return inputIndex;
  }

  public ARTEarleyNFAVertex getNfaVertex() {
    return nfaVertex;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + inputIndex;
    result = prime * result + ((nfaVertex == null) ? 0 : nfaVertex.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTEarleyConfiguration)) return false;
    ARTEarleyConfiguration other = (ARTEarleyConfiguration) obj;
    if (inputIndex != other.inputIndex) return false;
    if (nfaVertex == null) {
      if (other.nfaVertex != null) return false;
    } else if (!nfaVertex.equals(other.nfaVertex)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "(G" + nfaVertex.getNumber() + ", " + inputIndex + ")";
  }
}
