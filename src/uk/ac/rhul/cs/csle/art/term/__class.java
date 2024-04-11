package uk.ac.rhul.cs.csle.art.term;

import java.util.LinkedList;

public class __class extends Value {
  private final LinkedList<Value> superClasses;
  private final int bodyTerm;

  LinkedList<Value> getSuperClasses() {
    return superClasses;
  }

  public int getBodyTerm() {
    return bodyTerm;
  }

  public __class(LinkedList<Value> superClasses, int bodyTerm) {
    // System.out.println("Creating __class with superClasses " + superClasses + " and bodyTerm " + bodyTerm);
    this.superClasses = superClasses;
    this.bodyTerm = bodyTerm;
  }

  @Override
  public Object javaValue() {
    return null;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("__class(");
    boolean notFirst = false;
    for (Value p : superClasses) {
      if (notFirst)
        sb.append(", ");
      else
        notFirst = true;
      sb.append(p);
    }
    sb.append(") {");
    sb.append(iTerms.toString(bodyTerm));
    sb.append("}!!!");
    return sb.toString();
  }
}
