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
import javafx.scene.SpotLight;
import javafx.scene.paint.Color;
import test.util.Util;
public class SpotLightAttenuationTest extends LightingTest {
private static final double INNER_ANGLE = 20;
private static final double OUTER_ANGLE = 40;
private static final int INSIDE_ANGLE_SAMPLE = 18;
private static final int MIDDLE_ANGLE_SAMPLE = 30;
private static final int OUTSIDE_ANGLE_SAMPLE = 42;
private static final double[] FALLOFF_FACTORS = new double[] {0.5, 1, 1.5};
private static final SpotLight LIGHT = new SpotLight(Color.BLUE);
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
public void testSpotlightAttenuation() {
Util.runAndWait(() -> {
LIGHT.setInnerAngle(INNER_ANGLE);
LIGHT.setOuterAngle(OUTER_ANGLE);
for (double falloff : FALLOFF_FACTORS) {
LIGHT.setFalloff(falloff);
var snapshot = snapshot();
int innerX = angleToDist(INSIDE_ANGLE_SAMPLE);
double spotFactor = 1;
double sampledBlue = snapshot.getPixelReader().getColor(innerX, 0).getBlue();
assertEquals(FAIL_MESSAGE, calculateLambertTerm(innerX) * spotFactor, sampledBlue, DELTA);
int middleX = angleToDist(MIDDLE_ANGLE_SAMPLE);
spotFactor = calculateSpotlightFactor(MIDDLE_ANGLE_SAMPLE);
sampledBlue = snapshot.getPixelReader().getColor(middleX, 0).getBlue();
assertEquals(FAIL_MESSAGE, calculateLambertTerm(middleX) * spotFactor, sampledBlue, DELTA);
int outerX = angleToDist(OUTSIDE_ANGLE_SAMPLE);
spotFactor = 0;
sampledBlue = snapshot.getPixelReader().getColor(outerX, 0).getBlue();
assertEquals(FAIL_MESSAGE, calculateLambertTerm(outerX) * spotFactor, sampledBlue, DELTA);
}
});
}
private double calculateSpotlightFactor(double degrees) {
double numerator = degCos(degrees) - degCos(LIGHT.getOuterAngle());
double denom = degCos(LIGHT.getInnerAngle()) - degCos(LIGHT.getOuterAngle());
return Math.pow(numerator / denom, LIGHT.getFalloff());
}
private double degCos(double degrees) {
return Math.cos(Math.toRadians(degrees));
}
private int angleToDist(double degrees) {
return (int) (LIGHT_DIST * Math.tan(Math.toRadians(degrees)));
}
}
