package uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance;

public class ARTGrammarInstanceRoot extends ARTGrammarInstance {

  public ARTGrammarInstanceRoot(int key) {
    super(key, null);
  }

  @Override
  public String toString() {
    return key + " Root";
  }
}
