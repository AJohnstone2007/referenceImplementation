package test.javafx.stage;
import java.util.ArrayList;
import javafx.scene.image.Image;
import com.sun.javafx.stage.WindowHelper;
import javafx.scene.Group;
import javafx.scene.Scene;
import test.com.sun.javafx.pgstub.StubStage;
import test.com.sun.javafx.pgstub.StubToolkit;
import test.com.sun.javafx.pgstub.StubToolkit.ScreenConfiguration;
import com.sun.javafx.tk.Toolkit;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
public class StageTest {
private StubToolkit toolkit;
private Stage s;
private StubStage peer;
private int initialNumTimesSetSizeAndLocation;
@Before
public void setUp() {
toolkit = (StubToolkit) Toolkit.getToolkit();
s = new Stage();
s.show();
peer = (StubStage) WindowHelper.getPeer(s);
initialNumTimesSetSizeAndLocation = peer.numTimesSetSizeAndLocation;
}
@After
public void tearDown() {
s.hide();
}
private void pulse() {
toolkit.fireTestPulse();
}
public @Test void testMovingStage() {
s.setX(100);
pulse();
assertEquals(100f, peer.x);
assertEquals(1, peer.numTimesSetSizeAndLocation - initialNumTimesSetSizeAndLocation);
}
public @Test void testResizingStage() {
s.setWidth(100);
s.setHeight(100);
pulse();
assertEquals(100f, peer.width);
assertEquals(100f, peer.height);
assertEquals(1, peer.numTimesSetSizeAndLocation - initialNumTimesSetSizeAndLocation);
}
public @Test void testMovingAndResizingStage() {
s.setX(101);
s.setY(102);
s.setWidth(103);
s.setHeight(104);
pulse();
assertEquals(101f, peer.x);
assertEquals(102f, peer.y);
assertEquals(103f, peer.width);
assertEquals(104f, peer.height);
assertEquals(1, peer.numTimesSetSizeAndLocation - initialNumTimesSetSizeAndLocation);
}
public @Test void testResizingTooSmallStage() {
s.setWidth(60);
s.setHeight(70);
s.setMinWidth(150);
s.setMinHeight(140);
pulse();
assertEquals(150.0, peer.width, 0.0001);
assertEquals(140.0, peer.height, 0.0001);
}
public @Test void testResizingTooBigStage() {
s.setWidth(100);
s.setHeight(100);
s.setMaxWidth(60);
s.setMaxHeight(70);
pulse();
assertEquals(60.0, peer.width, 0.0001);
assertEquals(70.0, peer.height, 0.0001);
}
public @Test void testSizeAndLocationChangedOverTime() {
pulse();
assertTrue((peer.numTimesSetSizeAndLocation - initialNumTimesSetSizeAndLocation) <= 1);
initialNumTimesSetSizeAndLocation = peer.numTimesSetSizeAndLocation;
s.setWidth(300);
s.setHeight(400);
pulse();
assertEquals(300f, peer.width);
assertEquals(400f, peer.height);
assertEquals(1, peer.numTimesSetSizeAndLocation - initialNumTimesSetSizeAndLocation);
s.setY(200);
pulse();
assertEquals(200f, peer.y);
assertEquals(2, peer.numTimesSetSizeAndLocation - initialNumTimesSetSizeAndLocation);
s.setX(100);
pulse();
assertEquals(100f, peer.x);
assertEquals(3, peer.numTimesSetSizeAndLocation - initialNumTimesSetSizeAndLocation);
}
@Test
public void testSecondCenterOnScreenNotIgnored() {
s.centerOnScreen();
s.setX(0);
s.setY(0);
s.centerOnScreen();
pulse();
assertTrue(Math.abs(peer.x) > 0.0001);
assertTrue(Math.abs(peer.y) > 0.0001);
}
@Test
public void testSecondSizeToSceneNotIgnored() {
final Scene scene = new Scene(new Group(), 200, 100);
s.setScene(scene);
s.sizeToScene();
s.setWidth(400);
s.setHeight(300);
s.sizeToScene();
pulse();
assertTrue(Math.abs(peer.width - 400) > 0.0001);
assertTrue(Math.abs(peer.height - 300) > 0.0001);
}
@Test
public void testCenterOnScreenForWindowOnSecondScreen() {
toolkit.setScreens(
new ScreenConfiguration(0, 0, 1920, 1200, 0, 0, 1920, 1172, 96),
new ScreenConfiguration(1920, 160, 1440, 900,
1920, 160, 1440, 900, 96));
try {
s.setX(1920);
s.setY(160);
s.setWidth(300);
s.setHeight(200);
s.centerOnScreen();
pulse();
assertTrue(peer.x > 1930);
assertTrue(peer.y > 170);
} finally {
toolkit.resetScreens();
}
}
@Test
public void testCenterOnScreenForOwnerOnSecondScreen() {
toolkit.setScreens(
new ScreenConfiguration(0, 0, 1920, 1200, 0, 0, 1920, 1172, 96),
new ScreenConfiguration(1920, 160, 1440, 900,
1920, 160, 1440, 900, 96));
try {
s.setX(1920);
s.setY(160);
s.setWidth(300);
s.setHeight(200);
final Stage childStage = new Stage();
childStage.setWidth(100);
childStage.setHeight(100);
childStage.initOwner(s);
childStage.show();
childStage.centerOnScreen();
assertTrue(childStage.getX() > 1930);
assertTrue(childStage.getY() > 170);
} finally {
toolkit.resetScreens();
}
}
@Test
public void testSwitchSceneWithFixedSize() {
Scene scene = new Scene(new Group(), 200, 100);
s.setScene(scene);
s.setWidth(400);
s.setHeight(300);
pulse();
assertEquals(400, peer.width, 0.0001);
assertEquals(300, peer.height, 0.0001);
assertEquals(400, scene.getWidth(), 0.0001);
assertEquals(300, scene.getHeight(), 0.0001);
s.setScene(scene = new Scene(new Group(), 220, 110));
pulse();
assertEquals(400, peer.width, 0.0001);
assertEquals(300, peer.height, 0.0001);
assertEquals(400, scene.getWidth(), 0.0001);
assertEquals(300, scene.getHeight(), 0.0001);
}
@Test
public void testSetBoundsNotLostForAsyncNotifications() {
s.setX(20);
s.setY(50);
s.setWidth(400);
s.setHeight(300);
peer.holdNotifications();
pulse();
s.setX(40);
s.setY(70);
s.setWidth(380);
s.setHeight(280);
peer.releaseNotifications();
pulse();
assertEquals(40.0, peer.x, 0.0001);
assertEquals(70.0, peer.y, 0.0001);
assertEquals(380.0, peer.width, 0.0001);
assertEquals(280.0, peer.height, 0.0001);
}
@Test
public void testFullscreenNotLostForAsyncNotifications() {
peer.holdNotifications();
s.setFullScreen(true);
assertTrue(s.isFullScreen());
s.setFullScreen(false);
assertFalse(s.isFullScreen());
peer.releaseSingleNotification();
assertTrue(s.isFullScreen());
peer.releaseNotifications();
assertFalse(s.isFullScreen());
}
@Test
public void testFullScreenNotification() {
peer.setFullScreen(true);
assertTrue(s.isFullScreen());
peer.setFullScreen(false);
assertFalse(s.isFullScreen());
}
@Test
public void testResizableNotLostForAsyncNotifications() {
peer.holdNotifications();
s.setResizable(false);
assertFalse(s.isResizable());
s.setResizable(true);
assertTrue(s.isResizable());
peer.releaseSingleNotification();
assertFalse(s.isResizable());
peer.releaseNotifications();
assertTrue(s.isResizable());
}
@Test
public void testResizableNotification() {
peer.setResizable(false);
assertFalse(s.isResizable());
peer.setResizable(true);
assertTrue(s.isResizable());
}
@Test
public void testIconifiedNotLostForAsyncNotifications() {
peer.holdNotifications();
s.setIconified(true);
assertTrue(s.isIconified());
s.setIconified(false);
assertFalse(s.isIconified());
peer.releaseSingleNotification();
assertTrue(s.isIconified());
peer.releaseNotifications();
assertFalse(s.isIconified());
}
@Test
public void testIconifiedNotification() {
peer.setIconified(true);
assertTrue(s.isIconified());
peer.setIconified(false);
assertFalse(s.isIconified());
}
@Test
public void testMaximixedNotLostForAsyncNotifications() {
peer.holdNotifications();
s.setMaximized(true);
assertTrue(s.isMaximized());
s.setMaximized(false);
assertFalse(s.isMaximized());
peer.releaseSingleNotification();
assertTrue(s.isMaximized());
peer.releaseNotifications();
assertFalse(s.isMaximized());
}
@Test
public void testMaximizedNotification() {
peer.setMaximized(true);
assertTrue(s.isMaximized());
peer.setMaximized(false);
assertFalse(s.isMaximized());
}
@Test
public void testAlwaysOnTopNotLostForAsyncNotifications() {
peer.holdNotifications();
s.setAlwaysOnTop(true);
assertTrue(s.isAlwaysOnTop());
s.setAlwaysOnTop(false);
assertFalse(s.isAlwaysOnTop());
peer.releaseSingleNotification();
assertTrue(s.isAlwaysOnTop());
peer.releaseNotifications();
assertFalse(s.isAlwaysOnTop());
}
@Test
public void testAlwaysOnTopNotification() {
peer.setAlwaysOnTop(true);
assertTrue(s.isAlwaysOnTop());
peer.setAlwaysOnTop(false);
assertFalse(s.isAlwaysOnTop());
}
@Test
public void testBoundsSetAfterPeerIsRecreated() {
s.setX(20);
s.setY(50);
s.setWidth(400);
s.setHeight(300);
pulse();
assertEquals(20.0, peer.x, 0.0001);
assertEquals(50.0, peer.y, 0.0001);
assertEquals(400.0, peer.width, 0.0001);
assertEquals(300.0, peer.height, 0.0001);
s.hide();
s.show();
pulse();
peer = (StubStage) WindowHelper.getPeer(s);
assertEquals(20.0, peer.x, 0.0001);
assertEquals(50.0, peer.y, 0.0001);
assertEquals(400.0, peer.width, 0.0001);
assertEquals(300.0, peer.height, 0.0001);
}
@Test
public void testAddAndSetNullIcon() {
String failMessage = "NullPointerException is expected.";
ArrayList<Image> imageList = new ArrayList<>();
imageList.add(null);
try {
s.getIcons().add(null);
throw new Exception();
} catch (Exception e) {
assertTrue(failMessage, e instanceof NullPointerException);
}
try {
s.getIcons().add(0, null);
throw new Exception();
} catch (Exception e) {
assertTrue(failMessage, e instanceof NullPointerException);
}
try {
s.getIcons().addAll(null, null);
throw new Exception();
} catch (Exception e) {
assertTrue(failMessage, e instanceof NullPointerException);
}
try {
s.getIcons().addAll(imageList);
throw new Exception();
} catch (Exception e) {
assertTrue(failMessage, e instanceof NullPointerException);
}
try {
s.getIcons().addAll(0, imageList);
throw new Exception();
} catch (Exception e) {
assertTrue(failMessage, e instanceof NullPointerException);
}
try {
s.getIcons().set(0, null);
throw new Exception();
} catch (Exception e) {
assertTrue(failMessage, e instanceof NullPointerException);
}
try {
s.getIcons().setAll(imageList);
throw new Exception();
} catch (Exception e) {
assertTrue(failMessage, e instanceof NullPointerException);
}
try {
s.getIcons().setAll(null, null);
throw new Exception();
} catch (Exception e) {
assertTrue(failMessage, e instanceof NullPointerException);
}
}
}
