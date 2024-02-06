package uk.ac.rhul.cs.csle.art.old.v3.value;

public class ARTValueDespatcher extends ARTValue {

  public ARTValueDespatcher() {
  }

  public ARTValue despatch(ARTValue value) throws ARTValueException {
    System.out.println("ARTValueDespatch receives " + value);

    return value;
  }

  public ARTValue despatch(ARTValue value1, ARTValue value2) throws ARTValueException {
    System.out.println("ARTValueDespatch receives " + value1 + ", " + value2);

    return value2;
  }

  @Override
  public ARTValue close() {
    System.out.println("Closing despatcher");
    return new ARTValueNull();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 13;
    result = prime * result;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTValueDespatcher)) return false;
    return true;
  }

  @Override
  public Object getPayload() {
    return new ARTValueNull();
  }
}
