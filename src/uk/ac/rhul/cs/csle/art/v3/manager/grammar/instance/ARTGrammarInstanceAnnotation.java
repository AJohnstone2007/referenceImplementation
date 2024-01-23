package uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance;

import uk.ac.rhul.cs.csle.art.v3.manager.grammar.element.ARTGrammarElement;

public class ARTGrammarInstanceAnnotation extends ARTGrammarInstance {

  public ARTGrammarInstanceAnnotation(int key, ARTGrammarElement payload) {
    super(key, payload);
  }

  @Override
  public String toString() {
    return key + " Annotation";
  }
}
