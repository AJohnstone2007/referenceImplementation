package uk.ac.rhul.cs.csle.art;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import uk.ac.rhul.cs.csle.art.util.Util;

public class ART {
  static ARTScriptInterpreter artScriptInterpreter = new ARTScriptInterpreter();

  public static void main(String[] args) {
    String scriptString = buildScriptString(args);
    if (artScriptInterpreter.iTerms.plugin.useFX())
      ARTFXWrapper.interpretUnderFX(artScriptInterpreter, scriptString);
    else
      artScriptInterpreter.interpret(scriptString);
  }

  // This version is used to process command line arguments
  private static String buildScriptString(String[] args) {
    StringBuilder sb = new StringBuilder();
    for (String a : args) {
      sb.append(" ");
      if (a.endsWith(".art"))
        try {
          sb.append(Files.readString(Paths.get((a))));
        } catch (IOException e) {
          Util.fatal("Unable to open script file " + a);
        }
      else if (a.endsWith(".str")) {
        sb.append("!try file(\"");
        sb.append(a);
        sb.append("\")");
      } else
        sb.append(a);
    }
    // System.out.println("Script string: " + scriptString);
    return sb.toString();
  }

}
