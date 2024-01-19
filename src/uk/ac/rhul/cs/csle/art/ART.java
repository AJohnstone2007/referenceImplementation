package uk.ac.rhul.cs.csle.art;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import uk.ac.rhul.cs.csle.art.util.Util;

public class ART {
  public static void main(String[] args) {

    StringBuilder scriptString = new StringBuilder();
    for (String a : args) {
      scriptString.append(" ");
      if (a.endsWith(".art"))
        try {
          scriptString.append(Files.readString(Paths.get((a))));
        } catch (IOException e) {
          Util.fatal("Unable to open script file " + a);
        }
      else if (a.endsWith(".str")) {
        scriptString.append("!try file(\"");
        scriptString.append(a);
        scriptString.append("\")");
      } else
        scriptString.append(a);
    }
    // System.out.println("Script string: " + scriptString);

    new ARTScriptInterpreter().interpret(scriptString.toString());
  }
}
