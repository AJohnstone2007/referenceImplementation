package uk.ac.rhul.cs.csle.art.term.mesh;

public class Vertex { // This might just as well be point
  public Point pos;

  Vertex(Point pos) {
    this.pos = new Point(pos);
  };

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((pos == null) ? 0 : pos.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Vertex other = (Vertex) obj;
    if (pos == null) {
      if (other.pos != null) return false;
    } else if (!pos.equals(other.pos)) return false;
    return true;
  }

  @Override
  public Vertex clone() {
    return new Vertex(pos.clone());
  }

  // public void flip() {
  // }

  public Vertex interpolate(Vertex other, double t) {
    return new Vertex(pos.lerp(other.pos, t));
  }

  @Override
  public String toString() {
    return pos.toString();
  }
}
