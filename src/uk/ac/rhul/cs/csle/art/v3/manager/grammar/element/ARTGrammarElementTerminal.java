package uk.ac.rhul.cs.csle.art.v3.manager.grammar.element;

public abstract class ARTGrammarElementTerminal extends ARTGrammarElement {

  protected final String id;

  public String getId() {
    return id;
  }

  public ARTGrammarElementTerminal(String id) {
    super();
    this.id = id;
  }

  @Override
  public int hashCode() {
    final int prime = 31 * classPriority();
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false; // This will handle classPriority too!
    ARTGrammarElementTerminal other = (ARTGrammarElementTerminal) obj;
    if (id == null) {
      if (other.id != null) return false;
    } else if (!id.equals(other.id)) return false;
    return true;
  }

  @Override
  public int compareTo(ARTGrammarElement that) {
    // System.out.printf("compareTo on %s against %s%n", this, that);

    int thisClassPriority = this.classPriority();
    int thatClassPriority = that.classPriority();

    if (thisClassPriority > thatClassPriority) return 1;
    if (thisClassPriority < thatClassPriority) return -1;

    // We're ccomparing ouselves to another terminal
    return id.compareTo(((ARTGrammarElementTerminal) that).id);
  }
}
