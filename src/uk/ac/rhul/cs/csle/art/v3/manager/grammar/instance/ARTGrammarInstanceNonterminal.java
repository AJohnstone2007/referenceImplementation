package uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance;

import uk.ac.rhul.cs.csle.art.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.element.ARTGrammarElement;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.element.ARTGrammarElementNonterminal;

public class ARTGrammarInstanceNonterminal extends ARTGrammarInstance {

  public ARTGrammarInstanceNonterminal(int key, ARTGrammarElement payload) {
    super(key, payload);
    first.add(payload);
  }

  @Override
  public String toString() {
    return ARTText.toIdentifier(key + " " + payload);
  }

  @Override
  public String instanceString() {
    return ARTText.toIdentifier(((ARTGrammarElementNonterminal) payload).getId() + instanceNumberWithinProduction);
  }
}
