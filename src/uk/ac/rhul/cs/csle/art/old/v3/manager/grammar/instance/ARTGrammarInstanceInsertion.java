package uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance;

import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElement;

public class ARTGrammarInstanceInsertion extends ARTGrammarInstance {

  public ARTGrammarInstanceInsertion(int key, ARTGrammarElement payload) {
    super(key, payload);
  }

  @Override
  public String toString() {
    return key + " Insertion";
  }

}
