package uk.ac.rhul.cs.csle.art.v3.manager.grammar.element;

public class ARTGrammarElementSlotName extends ARTGrammarElement {
  String id;

  @Override
  public String toString() {
    return id;
  }

  @Override
  public String toEnumerationString() {
    return "SLOT_" + toString();
  }

  @Override
  public String toEnumerationString(String prefix) {
    return "ART" + prefix + "_" + toEnumerationString();
  }
}
