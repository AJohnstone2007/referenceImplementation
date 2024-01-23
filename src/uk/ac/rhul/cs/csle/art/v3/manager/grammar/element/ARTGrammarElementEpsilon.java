package uk.ac.rhul.cs.csle.art.v3.manager.grammar.element;

public class ARTGrammarElementEpsilon extends ARTGrammarElement {

  @Override
  public String toString() {
    return "#";
  }

  @Override
  public String toEnumerationString() {
    return "EPSILON";
  }

  @Override
  public String toEnumerationString(String prefix) {
    return "ART" + prefix + "_" + toEnumerationString();
  }

  @Override
  public int hashCode() {
    final int prime = 31 * classPriority();
    int result = 1;
    result = prime * result;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    return true;
  }

  @Override
  public int compareTo(ARTGrammarElement that) {
    // System.out.printf("compareTo on %s against %s%n", this, that);

    int thisClassPriority = this.classPriority();
    int thatClassPriority = that.classPriority();

    if (thisClassPriority > thatClassPriority) return 1;
    if (thisClassPriority < thatClassPriority) return -1;

    // We're ccomparing ouselves to another Epsilon
    return 0;
  }
}
