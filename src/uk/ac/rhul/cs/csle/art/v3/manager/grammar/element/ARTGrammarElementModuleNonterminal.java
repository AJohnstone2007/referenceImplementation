package uk.ac.rhul.cs.csle.art.v3.manager.grammar.element;

import java.util.HashSet;
import java.util.LinkedList;

import uk.ac.rhul.cs.csle.art.v3.alg.gll.support.ARTGLLRDTVertex;
import uk.ac.rhul.cs.csle.art.v3.manager.module.ARTV3Module;

public class ARTGrammarElementModuleNonterminal extends ARTGrammarElement {
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((module == null) ? 0 : module.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    ARTGrammarElementModuleNonterminal other = (ARTGrammarElementModuleNonterminal) obj;
    if (id == null) {
      if (other.id != null) return false;
    } else if (!id.equals(other.id)) return false;
    if (module == null) {
      if (other.module != null) return false;
    } else if (!module.equals(other.module)) return false;
    return true;
  }

  private final ARTV3Module module;
  private final String id;

  // HashSet<ART_GLLRDTVertex> productions = new HashSet<ART_GLLRDTVertex>();
  private final LinkedList<ARTGLLRDTVertex> productions = new LinkedList<ARTGLLRDTVertex>();
  private final HashSet<ARTGrammarElementAttribute> attributes = new HashSet<ARTGrammarElementAttribute>();
  private final HashSet<ARTGLLRDTVertex> rdtDeleters = new HashSet<ARTGLLRDTVertex>();

  public ARTGrammarElementModuleNonterminal(ARTV3Module module, String id) {
    super();
    this.module = module;
    this.id = id;
  }

  public void addProduction(ARTGLLRDTVertex tree) {
    // System.out.println("\nNonterminal " + this + " adding production " + tree);
    productions.add(tree);
  }

  public void addAttribute(String id, String type) {
    // System.err.printf("adding attribute called %s of type %s to nonterminal %s\n", id, type, this.id);
    attributes.add(new ARTGrammarElementAttribute(id, type));
  }

  public void addDeleter(ARTGLLRDTVertex tree) {
    rdtDeleters.add(tree);
  }

  @Override
  public String toString() {
    return module.getId() + "." + id;
  }

  public void printProductions() {
    for (ARTGLLRDTVertex v : productions) {
      System.out.print(id + "::=");
      v.print();
      System.out.print("\n");
    }
  }

  public ARTV3Module getModule() {
    return module;
  }

  public String getId() {
    return id;
  }

  public LinkedList<ARTGLLRDTVertex> getProductions() {
    return productions;
  }

  public HashSet<ARTGrammarElementAttribute> getAttributes() {
    return attributes;
  }

  public HashSet<ARTGLLRDTVertex> getRdtDeleters() {
    return rdtDeleters;
  }

  @Override
  public String toEnumerationString() {
    return "ARTModuleNonterminal_" + module.getId() + "_" + id;
  }

  @Override
  public String toEnumerationString(String prefix) {
    return "prefix" + "_ARTModuleNonterminal_" + module.getId() + "_" + id;
  }

}
