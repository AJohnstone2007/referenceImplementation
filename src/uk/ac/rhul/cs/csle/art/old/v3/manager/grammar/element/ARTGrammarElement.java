package uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element;

public abstract class ARTGrammarElement implements Comparable<ARTGrammarElement> {
  private int elementNumber;

  public int getElementNumber() {
    return elementNumber;
  }

  public void setElementNumber(int elementNumber) {
    this.elementNumber = elementNumber;
  }

  abstract public String toEnumerationString();

  abstract public String toEnumerationString(String prefix);

  public String toParaterminalString() {
    return toString();
  }

  // This is used to establish a full ordering over the ARTGrammar... concrete classes, so that maps maintain them in ART V2 normal form which allows
  // optimisation of BFS parsers
  final public int classPriority() {
    return this instanceof ARTGrammarElementEoS ? 1
        : this instanceof ARTGrammarElementTerminalBuiltin ? 2
            : this instanceof ARTGrammarElementTerminalCharacter ? 3
                : this instanceof ARTGrammarElementTerminalCaseSensitive ? 4
                    : this instanceof ARTGrammarElementTerminalCaseInsensitive ? 5
                        : this instanceof ARTGrammarElementEpsilon ? 6
                            : this instanceof ARTGrammarElementNonterminal ? 7
                                : this instanceof ARTGrammarElementSlotName ? 8 : this instanceof ARTGrammarElementAttribute ? 9 : 0;

  }

  // This is the default hashcode for all grammar elements
  // As of 23/11/16, it is overridden in ARTGrammarElementTerminal and ARTGrammarElementNonterminal
  // In the future, it will also neeed overrides in Attribute and Slotname
  @Override
  public int hashCode() {
    final int prime = 31;
    return prime * classPriority();
  }

  // This is the default equals for all grammar elements
  // As of 23/11/16, it is overridden in ARTGrammarElementTerminal and ARTGrammarElementNonterminal
  // In the future, it will also neeed overrides in Attribute and Slotname
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ARTGrammarElement other = (ARTGrammarElement) obj;
    if (classPriority() != other.classPriority()) return false;
    return true;
  }

  @Override
  public int compareTo(ARTGrammarElement that) {
    // System.out.printf("compareTo on %s against %s%n", this, that);

    int thisClassPriority = this.classPriority();
    int thatClassPriority = that.classPriority();

    if (thisClassPriority > thatClassPriority) return 1;
    if (thisClassPriority < thatClassPriority) return -1;

    return 0;
  }

}
