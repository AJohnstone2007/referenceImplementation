package uk.ac.rhul.cs.csle.art;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import uk.ac.rhul.cs.csle.art.util.Util;

public class ART {
  public static void main(String[] args) {
    /* This state machine composes a script sring from the CLI arguments */
    boolean useIDE = false;
    final Pattern filenamePattern = Pattern.compile("[a-zA-Z0-9/\\\\]+.[a-zA-Z0-9]+"); // NB this is a limited idea of what a filename looks like

    StringBuilder sb = new StringBuilder();
    final int ideState = 0, scriptFilenameState = 1, testFilenameState = 2, appendState = 3;
    int state = ideState;
    int i = 0;
    while (i < args.length) {
      System.out.println("State " + state + " testing args[" + i + "]: " + args[i]);
      sb.append(" ");
      switch (state) {
      case ideState:
        if (args[i].equals("ide")) {
          useIDE = true;
          i++;
        }
        state = scriptFilenameState;
        break;
      case scriptFilenameState:
        if (filenamePattern.matcher(args[i]).matches() && args[i].endsWith(".art")) {
          /* When we have include working, replace this insertion with an !include directive */
          try {
            sb.append(Files.readString(Paths.get((args[i++]))));
          } catch (IOException e) {
            Util.fatal("Unable to open script file " + args[i]);
          }

          // sb.append("!include '" + args[i++] + "'");
          state = testFilenameState;
        } else
          state = appendState;
        break;
      case testFilenameState:
        if (filenamePattern.matcher(args[i]).matches()) sb.append("!try '" + args[i++] + "'");
        state = appendState;
        break;
      case appendState:
        sb.append(args[i++]); // We stay in this state forever
        break;
      default:
        Util.fatal("unexpected state in CLI processor");
      }
    }

    System.out.println("IDE: " + useIDE + " Script string: " + sb);

    // Now run the interpeter, optionally under Java FX
    ARTScriptInterpreter artScriptInterpreter = new ARTScriptInterpreter();

    if (useIDE)
      ARTIDE.interpet(artScriptInterpreter, sb.toString());
    else if (artScriptInterpreter.iTerms.plugin.useFX())
      ARTFXWrapper.interpretUnderFX(artScriptInterpreter, sb.toString());
    else
      artScriptInterpreter.interpret(sb.toString());
  }
}
