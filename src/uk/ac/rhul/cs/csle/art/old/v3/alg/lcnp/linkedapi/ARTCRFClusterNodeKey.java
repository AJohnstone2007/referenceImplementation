package uk.ac.rhul.cs.csle.art.old.v3.alg.lcnp.linkedapi;

import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementNonterminal;

/* Implementation of the elements of a Call-Return-Forest */
public class ARTCRFClusterNodeKey {

  private final ARTGrammarElementNonterminal nonterminal;
  private final int h;

  public ARTCRFClusterNodeKey(ARTGrammarElementNonterminal x, int h) {
    this.nonterminal = x;
    this.h = h;
  }

  public ARTGrammarElementNonterminal getNonterminal() {
    return nonterminal;
  }

  public int getH() {
    return h;
  }

  @Override
  public String toString() {
    return "(" + nonterminal + ", " + h + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + h;
    result = prime * result + ((nonterminal == null) ? 0 : nonterminal.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTCRFClusterNodeKey)) return false;
    ARTCRFClusterNodeKey other = (ARTCRFClusterNodeKey) obj;
    if (h != other.h) return false;
    if (nonterminal == null) {
      if (other.nonterminal != null) return false;
    } else if (!nonterminal.equals(other.nonterminal)) return false;
    return true;
  }

}
