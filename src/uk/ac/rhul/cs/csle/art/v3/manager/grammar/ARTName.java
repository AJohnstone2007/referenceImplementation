package uk.ac.rhul.cs.csle.art.v3.manager.grammar;

import uk.ac.rhul.cs.csle.art.v3.manager.module.ARTV3Module;

public class ARTName {
  @Override
  public String toString() {
    return "ARTName [module=" + module.getId() + ", id=" + id + "]";
  }

  public ARTV3Module module;
  public String id;

  public ARTName(ARTV3Module artModule, String id) {
    this.module = artModule;
    this.id = id;
  }

  public ARTName(String id) {
    this(null, id);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((module == null) ? 0 : module.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ARTName other = (ARTName) obj;
    if (module == null) {
      if (other.module != null) return false;
    } else if (!module.equals(other.module)) return false;
    if (id == null) {
      if (other.id != null) return false;
    } else if (!id.equals(other.id)) return false;
    return true;
  }

  public ARTV3Module getArtModule() {
    return module;
  }

  public String getId() {
    return id;
  }
}
