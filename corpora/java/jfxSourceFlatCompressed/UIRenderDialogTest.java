package test.javafx.scene;
import com.sun.javafx.PlatformUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
public class UIRenderDialogTest {
private static CountDownLatch startupLatch;
private static volatile Stage stage;
private static volatile Alert alert;
private static final double scale = 1.75;
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) throws Exception {
final Button button = new Button("Show Dialog");
button.setOnAction(e -> {
final Alert alert = new Alert(Alert.AlertType.NONE);
alert.initOwner(primaryStage);
alert.getButtonTypes().add(ButtonType.OK);
final HBox box = new HBox();
box.setAlignment(Pos.CENTER);
box.setPadding(new Insets(8));
box.setSpacing(8);
for (int i = 0; i < 4; i++) {
box.getChildren().add(new CheckBox("Check"));
}
alert.getDialogPane().setContent(box);
UIRenderDialogTest.alert = alert;
alert.show();
});
Scene scene = new Scene(new StackPane(button));
primaryStage.setScene(scene);
stage = primaryStage;
stage.addEventHandler(WindowEvent.WINDOW_SHOWN,
e -> Platform.runLater(startupLatch::countDown));
stage.show();
button.fire();
}
}
@BeforeClass
public static void setupOnce() throws Exception {
System.setProperty("glass.win.uiScale", String.valueOf(scale));
System.setProperty("glass.gtk.uiScale", String.valueOf(scale));
startupLatch = new CountDownLatch(1);
new Thread(() -> Application.launch(TestApp.class, (String[])null)).start();
assertTrue("Timeout waiting for FX runtime to start",
startupLatch.await(15, TimeUnit.SECONDS));
}
@Test
public void testCheckBoxTextInDialogDoesNotHaveEllipsis() {
assumeTrue(PlatformUtil.isLinux() || PlatformUtil.isWindows());
Assert.assertEquals("Wrong render scale", scale,
stage.getRenderScaleY(), 0.0001);
Assert.assertNotNull(alert);
assertTrue(alert.isShowing());
for (Node node : ((HBox) alert.getDialogPane().getContent()).getChildrenUnmodifiable()) {
CheckBox box = (CheckBox) node;
Assert.assertEquals("Wrong text", "Check", ((Text) box.lookup(".text")).getText());
}
}
@AfterClass
public static void teardown() {
Platform.runLater(() -> {
if (alert != null) {
alert.hide();
}
stage.hide();
});
Platform.exit();
}
}
