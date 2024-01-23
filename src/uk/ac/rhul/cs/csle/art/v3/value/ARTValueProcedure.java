package uk.ac.rhul.cs.csle.art.v3.value;

import java.util.Map;

import uk.ac.rhul.cs.csle.art.v3.alg.gll.support.ARTGLLRDTHandle;

public class ARTValueProcedure extends ARTValue {

  private final ARTValueEnvironment parameters;
  private final ARTGLLRDTHandle body;

  public ARTValueProcedure(ARTValueEnvironment parameters, ARTGLLRDTHandle body) {
    super();
    this.parameters = parameters;
    this.body = body;
    ;
  }

  @Override
  public ARTValueEnvironment parameters() {
    return parameters;
  }

  public ARTGLLRDTHandle getBody() {
    return body;
  }

  @Override
  public String toString() {
    return "ARTValueProcedure(" + parameters + ")";
  }

  @Override
  public String toLatexString(Map<String, String> map) {
    return mapString("ARTValueProcedure", map) + mapString("(", map) + parameters.toLatexString(map) + mapString(")", map);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((body == null) ? 0 : body.hashCode());
    result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTValueProcedure)) return false;
    ARTValueProcedure other = (ARTValueProcedure) obj;
    if (getBody() == null) {
      if (other.getBody() != null) return false;
    } else if (!getBody().equals(other.getBody())) return false;
    if (parameters == null) {
      if (other.parameters != null) return false;
    } else if (!parameters.equals(other.parameters)) return false;
    return true;
  }

  @Override
  public Object getPayload() {
    return null;
  }
}
