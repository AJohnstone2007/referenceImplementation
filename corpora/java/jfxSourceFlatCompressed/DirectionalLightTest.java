package test.javafx.scene.lighting3D;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.DirectionalLight;
import javafx.scene.paint.Color;
import test.util.Util;
public class DirectionalLightTest extends LightingTest {
private static final Point3D[] DIRECTIONS = { new Point3D(0, 0, 1), new Point3D(0, 1, 1), new Point3D(0, 0, -1) };
private static final DirectionalLight LIGHT = new DirectionalLight(Color.BLUE);
public static void main(String[] args) throws Exception {
initFX();
}
@BeforeClass
public static void initFX() throws Exception {
startupLatch = new CountDownLatch(1);
LightingTest.light = LIGHT;
new Thread(() -> Application.launch(TestApp.class, (String[]) null)).start();
assertTrue("Timeout waiting for FX runtime to start", startupLatch.await(15, TimeUnit.SECONDS));
}
@Before
public void setupEach() {
assumeTrue(Platform.isSupported(ConditionalFeature.SCENE3D));
}
@Test
public void testDirectionalLight() {
Util.runAndWait(() -> {
for (Point3D direction : DIRECTIONS) {
LIGHT.setDirection(direction);
double sampledBlue = snapshot().getPixelReader().getColor(0, 0).getBlue();
assertEquals(FAIL_MESSAGE, dotProduct(direction), sampledBlue, DELTA);
}
});
}
private double dotProduct(Point3D direction) {
double value = -direction.normalize().dotProduct(0, 0, -1);
return value < 0 ? 0 : value;
}
}
