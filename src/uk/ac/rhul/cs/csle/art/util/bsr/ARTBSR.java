package uk.ac.rhul.cs.csle.art.util.bsr;

import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstance;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstanceCat;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstanceSlot;

public class ARTBSR {
  private ARTGrammarInstance instance;
  final private int i, j, k;

  public ARTGrammarInstance getInstance() {
    return instance;
  }

  public int getI() {
    return i;
  }

  public int getJ() {
    return j;
  }

  public int getK() {
    return k;
  }

  public ARTBSR(ARTGrammarInstanceSlot instance, int i, int k, int j) {
    this.instance = instance;
    this.i = i;
    this.j = j;
    this.k = k;
  }

  public ARTBSR(ARTGrammarInstanceCat instance, int i, int k, int j) {
    this.instance = instance;
    this.i = i;
    this.j = j;
    this.k = k;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + i;
    result = prime * result + ((instance == null) ? 0 : instance.hashCode());
    result = prime * result + j;
    result = prime * result + k;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTBSR)) return false;
    ARTBSR other = (ARTBSR) obj;
    if (i != other.i) return false;
    if (instance == null) {
      if (other.instance != null) return false;
    } else if (!instance.equals(other.instance)) return false;
    if (j != other.j) return false;
    if (k != other.k) return false;
    return true;
  }

  @Override
  public String toString() {
    if (instance instanceof ARTGrammarInstanceCat)
      return "<" + instance.toGrammarString() + ", " + i + ", " + k + ", " + j + ">";
    else
      return "<" + instance.toGrammarPrefixString() + ", " + i + ", " + k + ", " + j + ">";
  }

  public ARTBSR setSlotAndReturnThis(ARTGrammarInstanceCat c) {
    instance = c;
    return this;
  }
}
