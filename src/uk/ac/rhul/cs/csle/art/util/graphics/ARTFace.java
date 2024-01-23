package uk.ac.rhul.cs.csle.art.util.graphics;

public class ARTFace {
  private final ARTCoord a, b, c;

  public ARTFace(ARTCoord a, ARTCoord b, ARTCoord c) {
    super();
    this.a = a;
    this.b = b;
    this.c = c;
  }

  public ARTCoord getA() {
    return a;
  }

  public ARTCoord getB() {
    return b;
  }

  public ARTCoord getC() {
    return c;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((a == null) ? 0 : a.hashCode());
    result = prime * result + ((b == null) ? 0 : b.hashCode());
    result = prime * result + ((c == null) ? 0 : c.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ARTFace other = (ARTFace) obj;
    if (a == null) {
      if (other.a != null) return false;
    } else if (!a.equals(other.a)) return false;
    if (b == null) {
      if (other.b != null) return false;
    } else if (!b.equals(other.b)) return false;
    if (c == null) {
      if (other.c != null) return false;
    } else if (!c.equals(other.c)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "ARTFace [a=" + a + ", b=" + b + ", c=" + c + "]";
  }
}
