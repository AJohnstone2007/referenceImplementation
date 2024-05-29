package uk.ac.rhul.cs.csle.art;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import uk.ac.rhul.cs.csle.art.ide.EditorWithConsoleWindow;
import uk.ac.rhul.cs.csle.art.ide.GraphicsWindow;
import uk.ac.rhul.cs.csle.art.ide.MenuBuilderARTGraphics;
import uk.ac.rhul.cs.csle.art.old.core.ARTV4;
import uk.ac.rhul.cs.csle.art.old.v3.ARTV3;
import uk.ac.rhul.cs.csle.art.term.ITermsLowLevelAPI;
import uk.ac.rhul.cs.csle.art.util.Util;

public class ART extends Application {
  static String mainArgs[]; // allow FX modes to collect arguments
  static boolean useIDE = false; // switch FX modes between batch and IDE

  // @formatter:off
  public static void main(String[] args) {
    mainArgs = args;
    if (args.length > 0) switch (args[0]) { // Test for initial special mode argument
    case "v3": new ARTV3(scriptString(mainArgs)); return;
    case "v4": new ARTV4(scriptString(mainArgs)); return;
    case "aj": new AJDebug(mainArgs); return;
    case "batch": launch(); return;
    }
    useIDE = true; launch(); // We arrive here only if none of the special modes is activated; hence use the IDE
  }

  @Override
  public void start(Stage primaryStage) {
    if (useIDE) ide(primaryStage);
    else { new ARTScriptInterpreter(new ITermsLowLevelAPI()).interpret(scriptString(mainArgs)); Platform.exit(); System.exit(0); }
  }
  // @formatter:on

  static String scriptString(String[] args) { // Construct an ART script string, processing embedded filenames accordingly
    StringBuilder scriptStringBuilder = new StringBuilder();
    final Pattern filenamePattern = Pattern.compile("[a-zA-Z0-9/\\\\]+\\.[a-zA-Z0-9]+"); // This is a very limited idea of a filename

    for (int argp = 1; argp < args.length; argp++)
      if (!filenamePattern.matcher(args[argp]).matches())
        scriptStringBuilder.append(args[argp]);
      else if (args[argp].endsWith(".art"))
        try {
          scriptStringBuilder.append(Files.readString(Paths.get((args[argp]))));
        } catch (IOException e) {
          Util.fatal("Unable to open script file " + args[argp]);
        }
      else
        scriptStringBuilder.append("!try '" + args[argp] + "'");
    return scriptStringBuilder.toString();
  }

  private void ide(Stage primaryStage) {
    System.out.println("Starting IDE");
    DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM d yyyy  hh:mm a");

    final int w10Vff = 7; // This is a fudge factor for Windows 10 where invisible framing still takes 7 pixels horizontally and 5 vertically

    Rectangle2D screen = Screen.getPrimary().getBounds();
    double windowWidth = screen.getWidth() / 3;

    var specEditor = new EditorWithConsoleWindow(new Stage(), -w10Vff, 0, windowWidth + 2 * w10Vff, screen.getHeight(), "ART specification", "Adrian was here");
    var tryEditor = new EditorWithConsoleWindow(new Stage(), windowWidth - w10Vff, 0, windowWidth + 2 * w10Vff, screen.getHeight(), "Try test",
        "A test string");
    var graphicsWindow = new GraphicsWindow(new Stage(), 2 * windowWidth - w10Vff, 0, windowWidth + 2 * w10Vff, screen.getHeight(), "ART visualiser",
        new MenuBuilderARTGraphics(), 500.0);

  }
}
