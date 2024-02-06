package uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance;

public class ARTGrammarInstanceDoFirst extends ARTGrammarInstance {

  public ARTGrammarInstanceDoFirst(int key) {
    super(key, null);
  }

  @Override
  public String toString() {
    return key + " DoFirst";
  }
}
