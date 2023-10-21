package test.launchertest;
import javafx.application.Application;
import static test.launchertest.Constants.*;
public class TestHeadlessApp {
public static void main(String[] args) {
try {
Application.launch(TestAppNoMain.class, args);
} catch (UnsupportedOperationException ex) {
System.exit(ERROR_NONE);
} catch (Throwable t) {
t.printStackTrace();
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
System.exit(ERROR_LAUNCH_SUCCEEDED);
}
}
