package uk.ac.rhul.cs.csle.art.old.v3.term;

import java.util.Arrays;

public class ITerm {
  /*
   * private nested class which describes term elements in the pool - needed for key and value in the term maps
   *
   */
  int typeNameStringIndex;
  int symbolNameStringStringIndex;
  int arity;
  int[] childTermIndices;

  ITerm(int typeNameStringIndex, int stringnameStringIndex, int arity, int[] childTermIndices) {
    this.typeNameStringIndex = typeNameStringIndex;
    this.symbolNameStringStringIndex = stringnameStringIndex;
    this.arity = arity;
    this.childTermIndices = childTermIndices;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + arity;
    result = prime * result + Arrays.hashCode(childTermIndices);
    result = prime * result + symbolNameStringStringIndex;
    result = prime * result + typeNameStringIndex;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ITerm other = (ITerm) obj;
    if (arity != other.arity) return false;
    if (!Arrays.equals(childTermIndices, other.childTermIndices)) return false;
    if (symbolNameStringStringIndex != other.symbolNameStringStringIndex) return false;
    if (typeNameStringIndex != other.typeNameStringIndex) return false;
    return true;
  }
}