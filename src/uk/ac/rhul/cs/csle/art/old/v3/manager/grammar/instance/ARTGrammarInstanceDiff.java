package uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance;

public class ARTGrammarInstanceDiff extends ARTGrammarInstance {

  public ARTGrammarInstanceDiff(int key) {
    super(key, null);
  }

  @Override
  public String toString() {
    return key + " Diff";
  }
}
