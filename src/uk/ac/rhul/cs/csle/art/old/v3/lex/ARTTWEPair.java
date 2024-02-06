package uk.ac.rhul.cs.csle.art.old.v3.lex;

public class ARTTWEPair {
  public int token;
  public int rightExtent;
  public boolean suppressed = false;

  public ARTTWEPair(int token, int rightExtent) {
    this.token = token;
    this.rightExtent = rightExtent;
  }

  @Override
  public String toString() {
    return token + (isSuppressed() ? "!" : ".") + rightExtent;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + rightExtent;
    result = prime * result + (isSuppressed() ? 1231 : 1237);
    result = prime * result + token;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ARTTWEPair other = (ARTTWEPair) obj;
    if (rightExtent != other.rightExtent) return false;
    if (isSuppressed() != other.isSuppressed()) return false;
    if (token != other.token) return false;
    return true;
  }

  public boolean isSuppressed() {
    return suppressed;
  }

  public void setSuppressed(boolean suppressed) {
    // System.out.println("Suppressing TWE pair " + this);
    this.suppressed = suppressed;
  }
}