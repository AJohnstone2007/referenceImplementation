package uk.ac.rhul.cs.csle.art;

import javafx.application.Application;
import javafx.stage.Stage;
import uk.ac.rhul.cs.csle.art.term.__string;

public class ARTFXWrapper extends Application {

  private static String thisScriptString = "";
  private static ARTScriptInterpreter thisInterpreter = null;

  public static void interpretUnderFX(ARTScriptInterpreter interpreter, String scriptString) {
    thisScriptString = scriptString;
    thisInterpreter = interpreter;
    launch();
  }

  @Override
  public void start(Stage primaryStage) {
    System.out.println("Running under FX");
    thisInterpreter.interpret(thisScriptString);
    thisInterpreter.iTerms.plugin.plugin(new __string("Adrian was here")); // debug - open a window to prove that we're working
  }
}
