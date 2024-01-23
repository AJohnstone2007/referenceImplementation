package uk.ac.rhul.cs.csle.art.v3.manager.grammar.element;

// This is really a normalisation class - it just wraps a String as a ARTGrammarElement subclass. Used as payload for ARTInstanceActionValue nodes in the instance tree
public class ARTGrammarElementActionValue extends ARTGrammarElement {
  private final String value;

  public ARTGrammarElementActionValue(String value) {
    this.value = value;
  }

  @Override
  public String toEnumerationString() {
    return value;
  }

  @Override
  public String toEnumerationString(String prefix) {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }

}
