package test.com.sun.javafx.application;
import javafx.application.Application;
import javafx.stage.Stage;
import junit.framework.Assert;
import test.util.Util;
public class InitializeJavaFXBase {
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) throws Exception {
}
}
public void doTestInitializeThenLaunchInFX() throws Exception {
Util.runAndWait(() ->{
try {
Application.launch(TestApp.class);
Assert.fail("Error: No Exception was thrown - expected IllegalStateException");
} catch (IllegalStateException e) {
}
});
}
public void doTestInitializeThenSecondLaunch() throws Exception {
try {
Application.launch(TestApp.class);
Assert.fail("Error: No Exception was thrown - expected IllegalStateException");
} catch (IllegalStateException e) {
}
}
}
