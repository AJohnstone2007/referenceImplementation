package uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element;

import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;

public class ARTGrammarElementTerminalRule extends ARTGrammarElementTerminal {

  public ARTGrammarElementTerminalRule(String id) {
    super(id);
  }

  @Override
  public String toEnumerationString() {
    return ARTText.toIdentifier(id);
  }

  @Override
  public String toEnumerationString(String prefix) {
    if (prefix == null)
      return "ARTTR_" + toEnumerationString();
    else
      return "ART" + prefix + "_" + toEnumerationString();
  }
}
