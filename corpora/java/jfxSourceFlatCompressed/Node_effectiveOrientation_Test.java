package test.javafx.scene;
import test.com.sun.javafx.test.NodeOrientationTestBase;
import java.util.Arrays;
import java.util.Collection;
import javafx.geometry.NodeOrientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
@RunWith(Parameterized.class)
public final class Node_effectiveOrientation_Test
extends NodeOrientationTestBase {
private final Scene testScene;
private final String orientationUpdate;
private final String expectedOrientation;
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
private static Scene liirliPrecachedScene() {
final Scene scene =
ltrScene(
inhAutGroup(
inhAutGroup(
rtlAutGroup(
ltrAutGroup(
inhAutGroup())))));
collectOrientation(scene);
return scene;
}
private static Scene riirliPlugedPrecachedScenegraphScene() {
final Group root =
inhAutGroup(
inhAutGroup(
rtlAutGroup(
ltrAutGroup(
inhAutGroup()))));
collectOrientation(root);
final Scene scene = new Scene(new Group());
scene.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
scene.setRoot(root);
return scene;
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
{ lriiliScene(), "......", "LRRRLL" },
{ lriiliScene(), ".I....", "LLLLLL" },
{ lriiliScene(), "...L..", "LRRLLL" },
{ lriiliScene(), "....I.", "LRRRRR" },
{ lriiliScene(), "RIIIII", "RRRRRR" },
{
lriiliWithSubSceneScene(),
".......", "LRRRLL"
},
{
lriiliWithSubSceneScene(),
".L.....", "LLLLLL"
},
{ liirliPrecachedScene(), "......", "LLLRLL" },
{ liirliPrecachedScene(), "R.....", "RRRRLL" },
{ liirliPrecachedScene(), "...I..", "LLLLLL" },
{ liirliPrecachedScene(), "R..IR.", "RRRRRR" },
{
riirliPlugedPrecachedScenegraphScene(),
"......", "RRRRLL"
},
{ lrIiilScene(), "......", "LRRRRL" },
{ lrIiilScene(), ".L....", "LLLLLL" }
});
}
public Node_effectiveOrientation_Test(
final Scene testScene,
final String orientationUpdate,
final String expectedOrientation) {
this.testScene = testScene;
this.orientationUpdate = orientationUpdate;
this.expectedOrientation = expectedOrientation;
}
@Test
public void effectiveOrientationTest() {
updateOrientation(testScene, orientationUpdate);
assertOrientation(testScene, expectedOrientation);
}
private static void assertOrientation(
final Scene scene,
final String expectedOrientation) {
final String actualOrientation = collectOrientation(scene);
Assert.assertEquals("Orientation mismatch",
expectedOrientation, actualOrientation);
}
private static final StateEncoder EFFECTIVE_ORIENTATION_ENCODER =
new StateEncoder() {
@Override
public char map(final Scene scene) {
return map(scene.getEffectiveNodeOrientation());
}
@Override
public char map(final Node node) {
return map(node.getEffectiveNodeOrientation());
}
private char map(final NodeOrientation effectiveOrientation) {
switch (effectiveOrientation) {
case LEFT_TO_RIGHT:
return 'L';
case RIGHT_TO_LEFT:
return 'R';
default:
throw new IllegalArgumentException(
"Invalid orientation");
}
}
};
private static String collectOrientation(final Scene scene) {
return collectState(scene, EFFECTIVE_ORIENTATION_ENCODER);
}
private static String collectOrientation(final Node node) {
return collectState(node, EFFECTIVE_ORIENTATION_ENCODER);
}
}
