package uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element;

import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;

public class ARTGrammarElementTerminalCharacter extends ARTGrammarElementTerminal {

  public ARTGrammarElementTerminalCharacter(String id) {
    super(id);
  }

  @Override
  public String toString() {
    return "`" + ARTText.toLiteralString(id);
  }

  @Override
  public String toEnumerationString() {
    return ARTText.toIdentifier(id);
  }

  @Override
  public String toEnumerationString(String prefix) {
    if (prefix == null)
      return "ARTTC_" + toEnumerationString();
    else
      return "ART" + prefix + "_" + toEnumerationString();
  }
}
