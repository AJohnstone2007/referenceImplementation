package uk.ac.rhul.cs.csle.art.old.util.bsr;

import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminal;

public class ARTTerminalWithRightExtent {
  ARTGrammarElementTerminal terminal;
  int rightExtent;

  public ARTTerminalWithRightExtent(ARTGrammarElementTerminal terminal, int rightExtent) {
    this.terminal = terminal;
    this.rightExtent = rightExtent;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + rightExtent;
    result = prime * result + ((terminal == null) ? 0 : terminal.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTTerminalWithRightExtent)) return false;
    ARTTerminalWithRightExtent other = (ARTTerminalWithRightExtent) obj;
    if (rightExtent != other.rightExtent) return false;
    if (terminal == null) {
      if (other.terminal != null) return false;
    } else if (!terminal.equals(other.terminal)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "(" + terminal + ", " + rightExtent + ")";
  }

}
