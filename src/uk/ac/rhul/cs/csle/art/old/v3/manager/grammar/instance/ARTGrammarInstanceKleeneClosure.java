package uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance;

public class ARTGrammarInstanceKleeneClosure extends ARTGrammarInstance {

  public ARTGrammarInstanceKleeneClosure(int key) {
    super(key, null);
  }

  @Override
  public String toString() {
    return key + " *";
  }
}
