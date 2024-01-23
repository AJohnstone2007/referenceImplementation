package uk.ac.rhul.cs.csle.art.v3.alg.gll.support;

public class ARTGLLRDTHandle {
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + element;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTGLLRDTHandle)) return false;
    ARTGLLRDTHandle other = (ARTGLLRDTHandle) obj;
    if (element != other.element) return false;
    return true;
  }

  public int element;

  public ARTGLLRDTHandle(int element) {
    this.element = element;
  }
}
