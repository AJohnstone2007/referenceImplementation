package uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance;

import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementNonterminal;

public class ARTGrammarInstanceSlot extends ARTGrammarInstance {

  public ARTGrammarInstanceSlot(int key) {
    super(key, null);
  }

  @Override
  public String toString() {
    return /* key.toString() + " " + */ toGrammarString(".");
  }

  public String toString(String instanceString, Set<ARTGrammarElementNonterminal> paraterminals) {
    return /* key.toString() + " " + */ toGrammarString(".", instanceString, paraterminals);
  }

}
