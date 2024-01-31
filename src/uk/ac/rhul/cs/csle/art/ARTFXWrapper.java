package uk.ac.rhul.cs.csle.art;

import javafx.application.Application;
import javafx.stage.Stage;
import uk.ac.rhul.cs.csle.art.term.__string;

public class ARTFXWrapper extends Application {

  private static String[] argsField = {};

  public static void artFXstart(String[] args) {
    argsField = args;
    Application.launch(ARTFXWrapper.class, args);
  }

  @Override
  public void start(Stage primaryStage) {
    System.out.println("Running under FX");
    ARTScriptInterpreter interpreter = new ARTScriptInterpreter();
    interpreter.interpret(argsField);
    interpreter.iTerms.plugin.plugin(new __string("Adrian was here"));
    // demo3D(primaryStage);
  }
}
