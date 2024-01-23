package uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance;

public class ARTGrammarInstanceAlt extends ARTGrammarInstance {

  public ARTGrammarInstanceAlt(int key) {
    super(key, null);
  }

  @Override
  public String toString() {
    return key + " Alt";
  }
}
