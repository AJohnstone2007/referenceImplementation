package uk.ac.rhul.cs.csle.art;

import uk.ac.rhul.cs.csle.art.term.ITermsLowLevelAPI;

public class ART {
  public static void main(String[] args) {
    if (new ITermsLowLevelAPI().plugin.useFX())
      ARTFXWrapper.artFXstart(args);
    else
      new ARTScriptInterpreter().interpret(args);
  }
}
