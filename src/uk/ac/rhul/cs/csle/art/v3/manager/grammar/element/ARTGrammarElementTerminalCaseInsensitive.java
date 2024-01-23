package uk.ac.rhul.cs.csle.art.v3.manager.grammar.element;

import uk.ac.rhul.cs.csle.art.core.ARTUncheckedException;
import uk.ac.rhul.cs.csle.art.util.text.ARTText;

public class ARTGrammarElementTerminalCaseInsensitive extends ARTGrammarElementTerminal {

  public ARTGrammarElementTerminalCaseInsensitive(String id) {
    super(id);
    if (id.length() == 0) throw new ARTUncheckedException("empty case insensitive terminal " + id + " is not allowed - the empty string is denoted by #");
  }

  @Override
  public String toString() {
    return "\"" + id + "\"";
  }

  @Override
  public String toEnumerationString() {
    return ARTText.toIdentifier(id);
  }

  @Override
  public String toEnumerationString(String prefix) {
    if (prefix == null)
      return "ARTTI_" + toEnumerationString();
    else
      return "ART" + prefix + "_" + toEnumerationString();
  }

  @Override
  public String toParaterminalString() {
    return "ART_CIP_" + ARTText.toIdentifier(toString().substring(1, toString().length() - 1));
  }
}
