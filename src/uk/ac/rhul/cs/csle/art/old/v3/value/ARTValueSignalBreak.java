package uk.ac.rhul.cs.csle.art.old.v3.value;

public class ARTValueSignalBreak extends ARTValueSignal {
  @Override
  public ARTValue v() {
    return new ARTValueUndefined();
  }

}
