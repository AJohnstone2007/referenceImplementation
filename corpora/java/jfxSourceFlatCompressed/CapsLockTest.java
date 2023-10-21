import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
public class CapsLockTest {
private static BufferedReader reader;
public static class App extends Application {
private void checkCapsLock(boolean expected) throws Exception {
Optional<Boolean> capsLock = Platform.isKeyLocked(KeyCode.CAPS);
if (capsLock.isPresent()) {
System.out.println("isKeyLocked(CAPS) is " + capsLock.get());
if (capsLock.get() != expected) {
System.out.println("TEST FAILED");
System.exit(1);
}
} else {
System.out.println("ERROR: isKeyLocked(CAPS) is empty");
System.out.println("TEST FAILED");
System.exit(1);
}
}
@Override
public void start(Stage stage) throws Exception {
checkCapsLock(true);
System.out.println("Disable Caps Lock on your system then press ENTER");
reader.readLine();
checkCapsLock(false);
Platform.exit();
}
}
public static void main(String[] args) {
System.out.println("Enable Caps Lock on your system then press ENTER");
try {
reader = new BufferedReader(new InputStreamReader(System.in));
reader.readLine();
Application.launch(App.class, args);
} catch (Exception ex) {
ex.printStackTrace(System.out);
System.out.println("TEST FAILED");
System.exit(1);
}
System.out.println();
System.out.println("TEST PASSED");
}
}
