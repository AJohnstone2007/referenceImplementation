package uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element;

public class ARTGrammarElementAttribute extends ARTGrammarElement {
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
    result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    ARTGrammarElementAttribute other = (ARTGrammarElementAttribute) obj;
    if (getId() == null) {
      if (other.getId() != null) return false;
    } else if (!getId().equals(other.getId())) return false;
    if (getType() == null) {
      if (other.getType() != null) return false;
    } else if (!getType().equals(other.getType())) return false;
    return true;
  }

  private final String id;
  private final String type;

  public ARTGrammarElementAttribute(String id, String type) {
    super();
    this.id = id;
    this.type = type;
  }

  @Override
  public String toString() {
    return getId() + ":" + getType();
  }

  @Override
  public String toEnumerationString() {
    return toString();
  }

  @Override
  public String toEnumerationString(String prefix) {
    return "ART" + prefix + "_" + toEnumerationString();
  }

  public String getType() {
    return type;
  }

  public String getId() {
    return id;
  }
}
