package uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance;

import uk.ac.rhul.cs.csle.art.v3.manager.grammar.element.ARTGrammarElementActionValue;

public class ARTGrammarInstanceActionValue extends ARTGrammarInstance {
  String value;

  public ARTGrammarInstanceActionValue(int key, String value) {
    super(key, new ARTGrammarElementActionValue(value));
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
