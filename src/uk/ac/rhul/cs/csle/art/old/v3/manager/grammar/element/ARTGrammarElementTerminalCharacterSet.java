package uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element;

import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.core.ARTUncheckedException;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;

public class ARTGrammarElementTerminalCharacterSet extends ARTGrammarElementTerminal {
  Set<Character> set = new HashSet<>();

  public ARTGrammarElementTerminalCharacterSet(String lo, String hi) {
    super(null);
    if (lo.length() > 1) throw new ARTUncheckedException("character set lo bound string length > 1");
    if (hi.length() > 1) throw new ARTUncheckedException("character set hi bound string length > 1");
    char l = lo.charAt(0), h = hi.charAt(0);
    for (char tmp = l; tmp <= h; tmp++)
      set.add(tmp);
  }

  @Override
  public String toString() {
    return "`" + set;
  }

  @Override
  public String toEnumerationString() {
    return ARTText.toIdentifier(set.toString());
  }

  @Override
  public String toEnumerationString(String prefix) {
    if (prefix == null)
      return "ARTTCS_" + toEnumerationString();
    else
      return "ART" + prefix + "_" + toEnumerationString();
  }
}
