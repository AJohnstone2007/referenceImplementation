package uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.ARTName;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstance;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceCat;
import uk.ac.rhul.cs.csle.art.old.v3.manager.module.ARTV3Module;

public class ARTGrammarElementNonterminal extends ARTGrammarElement {
  protected ARTV3Module module;
  protected String id;

  protected List<ARTGrammarInstanceCat> productions = new ArrayList<ARTGrammarInstanceCat>();
  protected Set<ARTGrammarElementAttribute> attributes = new HashSet<ARTGrammarElementAttribute>();

  protected Set<ARTGrammarElement> first = new HashSet<ARTGrammarElement>();
  protected Set<ARTGrammarElement> follow = new HashSet<ARTGrammarElement>();

  protected boolean used = false;
  protected boolean defined = false;
  public boolean hasDelayedInstances = false;
  protected boolean containsDelayedInstances = false;
  protected boolean isGatherTarget = false;
  public boolean isLexical = false;
  public ARTGrammarInstance lhsInstance; // only used in nonterminals within grammars

  public int nextInstanceNumber;

  @Override
  public String toEnumerationString() {
    return ARTText.toIdentifier(module.getId() + "_" + id);
  }

  @Override
  public String toEnumerationString(String prefix) {
    if (prefix == null)
      return ARTText.toIdentifier("ARTL_" + toEnumerationString());
    else
      return ARTText.toIdentifier("ART" + prefix + "_" + toEnumerationString());
  }

  public ARTGrammarElementNonterminal(ARTV3Module module, String id) {
    super();
    this.module = module;
    this.id = id;
  }

  public ARTGrammarElementNonterminal(ARTName key) {
    this(key.module, key.id);
  }

  public ARTV3Module getModule() {
    return module;
  }

  public void setModule(ARTV3Module module) {
    this.module = module;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<ARTGrammarInstanceCat> getProductions() {
    return productions;
  }

  public Set<ARTGrammarElementAttribute> getAttributes() {
    return attributes;
  }

  public void setAttributes(Set<ARTGrammarElementAttribute> attributes) {
    this.attributes = attributes;
  }

  public Set<ARTGrammarElement> getFirst() {
    return first;
  }

  public void setFirst(Set<ARTGrammarElement> first) {
    this.first = first;
  }

  public Set<ARTGrammarElement> getFollow() {
    return follow;
  }

  public void setFollow(Set<ARTGrammarElement> follow) {
    this.follow = follow;
  }

  public boolean isUsed() {
    return used;
  }

  public void setUsed(boolean used) {
    this.used = used;
  }

  public boolean isDefined() {
    return defined;
  }

  public void setDefined(boolean defined) {
    this.defined = defined;
  }

  public boolean isHasDelayedInstances() {
    return hasDelayedInstances;
  }

  public void setHasDelayedInstances(boolean hasDelayedInstances) {
    this.hasDelayedInstances = hasDelayedInstances;
  }

  public boolean isContainsDelayedInstances() {
    return containsDelayedInstances;
  }

  public void setContainsDelayedInstances(boolean containsDelayedInstances) {
    this.containsDelayedInstances = containsDelayedInstances;
  }

  public boolean isGatherTarget() {
    return isGatherTarget;
  }

  public void setGatherTarget(boolean isGatherTarget) {
    this.isGatherTarget = isGatherTarget;
  }

  public boolean isLexical() {
    return isLexical;
  }

  public void setLexical(boolean isLexical) {
    this.isLexical = isLexical;
  }

  @Override
  public String toString() {
    String prefix = module.getId();
    if (prefix.equals("ART"))
      return id;
    else
      return prefix + "." + id;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = this.classPriority();
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((module == null) ? 0 : module.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (getClass() != obj.getClass()) return false;
    ARTGrammarElementNonterminal other = (ARTGrammarElementNonterminal) obj;
    if (id == null) {
      if (other.id != null) return false;
    } else if (!id.equals(other.id)) return false;
    if (module == null) {
      if (other.module != null) return false;
    } else if (!module.equals(other.module)) return false;
    return true;
  }

  @Override
  public int compareTo(ARTGrammarElement that) {
    // System.out.printf("compareTo on %s against %s%n", this, that);

    int thisClassPriority = this.classPriority();
    int thatClassPriority = that.classPriority();

    if (thisClassPriority > thatClassPriority) return 1;
    if (thisClassPriority < thatClassPriority) return -1;

    // We're ccomparing ouselves to another nonterminal
    int moduleCompare = this.module.getId().compareTo(((ARTGrammarElementNonterminal) that).module.getId());
    if (moduleCompare != 0) return moduleCompare;

    return id.compareTo(((ARTGrammarElementNonterminal) that).id);
  }
}
