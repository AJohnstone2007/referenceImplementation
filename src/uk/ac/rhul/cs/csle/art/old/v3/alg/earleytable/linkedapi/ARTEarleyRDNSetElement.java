package uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.linkedapi;

import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementNonterminal;

public class ARTEarleyRDNSetElement {
  private final ARTGrammarElementNonterminal nonterminal;
  private final int inputIndex;

  @Override
  public String toString() {
    return "(" + nonterminal + ", " + inputIndex + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + inputIndex;
    result = prime * result + ((nonterminal == null) ? 0 : nonterminal.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTEarleyRDNSetElement)) return false;
    ARTEarleyRDNSetElement other = (ARTEarleyRDNSetElement) obj;
    if (inputIndex != other.inputIndex) return false;
    if (nonterminal == null) {
      if (other.nonterminal != null) return false;
    } else if (!nonterminal.equals(other.nonterminal)) return false;
    return true;
  }

  public ARTEarleyRDNSetElement(ARTGrammarElementNonterminal nonterminal, int inputIndex) {
    this.nonterminal = nonterminal;
    this.inputIndex = inputIndex;
  }
}
