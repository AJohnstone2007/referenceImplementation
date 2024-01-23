package uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance;

public class ARTGrammarInstanceIter extends ARTGrammarInstance {

  public ARTGrammarInstanceIter(int key) {
    super(key, null);
  }

  @Override
  public String toString() {
    return key + " Iter";
  }
}
