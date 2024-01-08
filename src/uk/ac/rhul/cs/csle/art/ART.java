package uk.ac.rhul.cs.csle.art;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import uk.ac.rhul.cs.csle.art.term.ITerms;
import uk.ac.rhul.cs.csle.art.term.ITermsLowLevelAPI;
import uk.ac.rhul.cs.csle.art.util.Util;

public class ART {
  public static void main(String[] args) {
    final ITerms iTerms = new ITermsLowLevelAPI();

    StringBuilder scriptString = new StringBuilder();
    for (String a : args) {
      scriptString.append("\n");
      if (a.endsWith(".art"))
        try {
          scriptString.append(Files.readString(Paths.get((a))));
        } catch (IOException e) {
          Util.fatal("Unable to open script file " + a);
        }
      else if (a.indexOf(".") != -1) {
        scriptString.append("!try (file(\"");
        scriptString.append(a);
        scriptString.append("\"))");
      } else
        scriptString.append(a);
    }
    System.out.println("Script string: " + scriptString);

    new ARTScriptInterpreter(iTerms).interpret(scriptString.toString());
  }
}
