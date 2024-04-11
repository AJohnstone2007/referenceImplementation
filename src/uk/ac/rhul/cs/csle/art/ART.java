package uk.ac.rhul.cs.csle.art;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import uk.ac.rhul.cs.csle.art.old.core.ARTV4;
import uk.ac.rhul.cs.csle.art.old.core.ARTV5Transition;
import uk.ac.rhul.cs.csle.art.old.v3.ARTV3;
import uk.ac.rhul.cs.csle.art.term.ITermsLowLevelAPI;
import uk.ac.rhul.cs.csle.art.util.Util;

public class ART {
  static final Pattern filenamePattern = Pattern.compile("[a-zA-Z0-9/\\\\]+\\.[a-zA-Z0-9]+"); // NB this is a limited idea of what a filename looks like

  public static void main(String[] args) {
    if (args.length == 0) Util.fatal("ART " + ARTVersion.version() + ": no arguments supplied");

    ARTScriptInterpreter artScriptInterpreter = new ARTScriptInterpreter(new ITermsLowLevelAPI());

    switch (args[0]) { // Test for initial special mode argument
    case "ide":
      ARTIDE.interpretUnderIDE(artScriptInterpreter, buildScriptString(args, 1));
      break;
    case "v3":
      new ARTV3(buildScriptString(args, 1));
      break;
    case "v4":
      new ARTV4(buildScriptString(args, 1));
      break;
    case "V5Transition":
      new ARTV5Transition(buildScriptString(args, 1));
      break;
    case "ajDebug":
      AJDebug ajDebug = new AJDebug(args);
      break;
    default:
      if (artScriptInterpreter.iTerms.plugin.useFX()) // does the user code need JavaFX?
        ARTFXWrapper.interpretUnderFX(artScriptInterpreter, buildScriptString(args, 0));
      else
        artScriptInterpreter.interpret(buildScriptString(args, 0));
      break;
    }
  }

  static String buildScriptString(String[] args, int startArg) {
    StringBuilder sb = new StringBuilder();

    for (int argp = startArg; argp < args.length; argp++) {
      sb.append(" ");
      if (filenamePattern.matcher(args[argp]).matches()) { // Process args that look like filenames
        // System.out.println("Argument " + argp + " " + args[argp] + " looks like a file");
        if (args[argp].endsWith(".art")) {
          /* When we have include working, replace this insertion with an !include directive */
          try {
            sb.append(Files.readString(Paths.get((args[argp]))));
          } catch (IOException e) {
            Util.fatal("Unable to open script file " + args[argp]);
          }
          // sb.append("!include '" + args[i] + "'");
        } else
          sb.append("!try '" + args[argp] + "'");
      } else
        sb.append(args[argp]);
    }
    // System.out.println(" Script string: " + sb);
    return sb.toString();
  }

}
