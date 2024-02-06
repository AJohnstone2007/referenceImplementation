package uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.support;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

public class ARTChiBSRSet {
  private final Set<ARTChiBSR> set = new HashSet<>();
  private final ARTEarleyTableDataLinked nfaLinked; // only used for rendering strings
  private final ARTEarleyTableDataIndexed nfaIndexed; // only used for rendering strings

  public ARTChiBSRSet(ARTEarleyTableDataLinked nfa) {
    this.nfaLinked = nfa;
    this.nfaIndexed = null;
  }

  public ARTChiBSRSet(ARTEarleyTableDataIndexed nfaIndexed) {
    this.nfaLinked = null;
    this.nfaIndexed = nfaIndexed;
  }

  public void add(ARTChiBSR bsr) {
    set.add(bsr);
  }

  public Set<ARTChiBSR> getSet() {
    return set;
  }

  @Override
  public String toString() {
    StringWriter stringWriter = new StringWriter();
    if (nfaLinked == null) {
      for (ARTChiBSR element : set)
        stringWriter.append("[" + element.getChiSetIndex() + "]: { " + nfaIndexed.chiSetCache[element.getChiSetIndex()] + " } " + element.getI() + ", "
            + element.getJ() + ", " + element.getK() + "\n");
    } else
      for (ARTChiBSR element : set)
        stringWriter.append("[" + element.getChiSetIndex() + "]:" + nfaLinked.getChiSet(element.getChiSetIndex()) + " " + element.getI() + ", " + element.getJ()
            + ", " + element.getK() + "\n");
    return stringWriter.toString();
  }

}
