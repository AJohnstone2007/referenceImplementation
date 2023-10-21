package test.javafx.scene;
import test.com.sun.javafx.test.NodeOrientationTestBase;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.Node;
import javafx.scene.NodeShim;
import javafx.scene.Scene;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
@RunWith(Parameterized.class)
public final class Node_hasMirroring_Test extends NodeOrientationTestBase {
private final Scene testScene;
private final String orientationUpdate;
private final String expectedMirroring;
private static Scene lriiliScene() {
return ltrScene(
rtlAutGroup(
inhAutGroup(
inhAutGroup(
ltrAutGroup(
inhAutGroup())))));
}
private static Scene lriiliWithSubSceneScene() {
return ltrScene(
rtlAutGroup(
inhSubScene(
inhAutGroup(
ltrAutGroup(
inhAutGroup())))));
}
private static Scene lrIiliScene() {
return ltrScene(
rtlAutGroup(
inhManGroup(
inhAutGroup(
ltrAutGroup(
inhAutGroup())))));
}
private static Scene lrLRlrScene() {
return ltrScene(
rtlAutGroup(
ltrManGroup(
rtlManGroup(
ltrAutGroup(
rtlAutGroup())))));
}
private static Scene lrIiilScene() {
return ltrScene(
rtlAutGroup(
inhManGroup(
inhAutGroup(
inhAutGroup(
ltrAutGroup())))));
}
@Parameters
public static Collection data() {
return Arrays.asList(
new Object[][] {
{ lriiliScene(), "......", ".M..M." },
{ lriiliScene(), ".I....", "......" },
{ lriiliScene(), "...L..", ".M.M.." },
{ lriiliScene(), "....I.", ".M...." },
{ lriiliScene(), "RIIIII", ".M...." },
{
lriiliWithSubSceneScene(),
"......", ".M..M."
},
{ lrIiliScene(), "......", ".MMMM." },
{ lrLRlrScene(), "......", ".MM..M" },
{ lrIiilScene(), "...R..", ".MMM.M" },
});
}
public Node_hasMirroring_Test(
final Scene testScene,
final String orientationUpdate,
final String expectedMirroring) {
this.testScene = testScene;
this.orientationUpdate = orientationUpdate;
this.expectedMirroring = expectedMirroring;
}
@Test
public void hasMirroringTest() {
updateOrientation(testScene, orientationUpdate);
assertMirroring(testScene, expectedMirroring);
}
private static void assertMirroring(
final Scene scene,
final String expectedMirroring) {
final String actualMirroring = collectMirroring(scene);
Assert.assertEquals("Mirroring mismatch",
expectedMirroring, actualMirroring);
}
private static final StateEncoder HAS_MIRRORING_ENCODER =
new StateEncoder() {
@Override
public char map(final Scene scene) {
return map(false);
}
@Override
public char map(final Node node) {
return map(NodeShim.hasMirroring(node));
}
private char map(final boolean hasMirroring) {
return hasMirroring ? 'M' : '.';
}
};
private static String collectMirroring(final Scene scene) {
return collectState(scene, HAS_MIRRORING_ENCODER);
}
}
