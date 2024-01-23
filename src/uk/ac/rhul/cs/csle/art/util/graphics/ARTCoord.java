package uk.ac.rhul.cs.csle.art.util.graphics;

public class ARTCoord {

  private Float x, y, z;

  ARTCoord(Float x, Float y, Float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public ARTCoord() {
    this.x = 0.0f;
    this.y = 0.0f;
    this.z = 0.0f;
  }

  public Float getX() {
    return x;
  }

  public void setX(Float x) {
    this.x = x;
  }

  public Float getY() {
    return y;
  }

  public void setY(Float y) {
    this.y = y;
  }

  public Float getZ() {
    return z;
  }

  public void setZ(Float z) {
    this.z = z;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((x == null) ? 0 : x.hashCode());
    result = prime * result + ((y == null) ? 0 : y.hashCode());
    result = prime * result + ((z == null) ? 0 : z.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ARTCoord other = (ARTCoord) obj;
    if (x == null) {
      if (other.x != null) return false;
    } else if (!x.equals(other.x)) return false;
    if (y == null) {
      if (other.y != null) return false;
    } else if (!y.equals(other.y)) return false;
    if (z == null) {
      if (other.z != null) return false;
    } else if (!z.equals(other.z)) return false;
    return true;
  }

  @Override
  public String toString() {
    return ("(" + x + "," + y + "," + z + ")");
  }
}
