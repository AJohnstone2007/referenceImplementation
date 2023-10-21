package test.robot.javafx.application;
import com.sun.javafx.PlatformUtil;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.robot.Robot;
import test.util.Util;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
public class KeyLockedTest {
private static final CountDownLatch startupLatch = new CountDownLatch(1);
private static Robot robot;
@BeforeClass
public static void initFX() throws Exception {
Platform.setImplicitExit(false);
Platform.startup(startupLatch::countDown);
assertTrue("Timeout waiting for FX runtime to start",
startupLatch.await(15, TimeUnit.SECONDS));
if (PlatformUtil.isWindows()) {
Util.runAndWait(() -> robot = new Robot());
}
}
@AfterClass
public static void cleanupFX() {
if (robot != null) {
Platform.runLater(() -> {
Optional<Boolean> capsLockState = Platform.isKeyLocked(KeyCode.CAPS);
capsLockState.ifPresent(state -> {
if (state) {
robot.keyPress(KeyCode.CAPS);
robot.keyRelease(KeyCode.CAPS);
}
});
});
}
Platform.exit();
}
@Test(expected = IllegalStateException.class)
public void testCallOnTestThread() {
Optional<Boolean> capsLockState = Platform.isKeyLocked(KeyCode.CAPS);
}
@Test(expected = IllegalArgumentException.class)
public void testIllegalKeyCode() {
Util.runAndWait(() -> {
Optional<Boolean> capsLockState = Platform.isKeyLocked(KeyCode.A);
});
}
@Test
public void testCanReadCapsLockState() {
Util.runAndWait(() -> {
Optional<Boolean> capsLockState = Platform.isKeyLocked(KeyCode.CAPS);
assertNotNull(capsLockState);
assertTrue(capsLockState.isPresent());
});
}
@Test
public void testCanReadNumLockState() {
Util.runAndWait(() -> {
Optional<Boolean> numLockState = Platform.isKeyLocked(KeyCode.NUM_LOCK);
assertNotNull(numLockState);
if (PlatformUtil.isWindows() || PlatformUtil.isLinux()) {
assertTrue(numLockState.isPresent());
}
if (PlatformUtil.isMac()) {
assertFalse(numLockState.isPresent());
}
});
}
@Test
public void testCapsLockState() {
assumeTrue(PlatformUtil.isWindows());
final AtomicBoolean initialCapsLock = new AtomicBoolean(false);
Util.runAndWait(() -> {
Optional<Boolean> capsLockState = Platform.isKeyLocked(KeyCode.CAPS);
assertNotNull(capsLockState);
assertTrue(capsLockState.isPresent());
initialCapsLock.set(capsLockState.get());
robot.keyPress(KeyCode.CAPS);
robot.keyRelease(KeyCode.CAPS);
});
Util.sleep(500);
Util.runAndWait(() -> {
Optional<Boolean> capsLockState = Platform.isKeyLocked(KeyCode.CAPS);
assertNotNull(capsLockState);
assertTrue(capsLockState.isPresent());
assertTrue(initialCapsLock.get() != capsLockState.get());
});
}
}
