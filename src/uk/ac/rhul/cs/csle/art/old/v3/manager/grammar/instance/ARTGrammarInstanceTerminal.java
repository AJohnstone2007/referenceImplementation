package uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance;

import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminal;

public class ARTGrammarInstanceTerminal extends ARTGrammarInstance {

  public ARTGrammarInstanceTerminal(int key, ARTGrammarElementTerminal payload) {
    super(key, payload);
    first.add(payload); // compute first set
  }

  @Override
  public String toString() {
    return key + " " + payload;
  }
}
