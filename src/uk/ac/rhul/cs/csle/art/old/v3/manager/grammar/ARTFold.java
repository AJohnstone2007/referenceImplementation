package uk.ac.rhul.cs.csle.art.old.v3.manager.grammar;

public enum ARTFold {
  EMPTY, NONE, OVER, UNDER, TEAR;

  @Override
  public String toString() {
    switch (this) {
    case EMPTY:
      return "";
    case NONE:
      return "^_";
    case UNDER:
      return "^";
    case OVER:
      return "^^";
    case TEAR:
      return "^^^";
    default:
      return "???";
    }
  }
}
