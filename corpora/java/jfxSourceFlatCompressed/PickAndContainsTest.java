package test.javafx.scene;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneShim;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.junit.Test;
public class PickAndContainsTest {
@Test
public void testScenePickingRect() {
Rectangle rect = new Rectangle(50, 25, 100, 50);
Group g = new Group();
g.getChildren().add(rect);
Scene scene = new Scene(g);
assertSame(rect, SceneShim.test_pick(scene,100, 50));
assertNull(SceneShim.test_pick(scene,0, 0));
assertNull(SceneShim.test_pick(scene,160, 50));
}
@Test
public void testScenePickingCircle() {
Circle circle = new Circle(60, 60, 50);
Group g = new Group();
g.getChildren().add(circle);
Scene scene = new Scene(g);
assertSame(circle, SceneShim.test_pick(scene,100, 50));
assertNull(SceneShim.test_pick(scene,0, 0));
assertNull(SceneShim.test_pick(scene,160, 50));
}
@Test
public void testScenePickingGroup() {
Group grp;
Rectangle r0;
Rectangle r1;
Rectangle r2;
Group root = new Group();
root.getChildren().addAll(r0 = new Rectangle(100, 100, 40, 20),
grp = new Group(r1 = new Rectangle(0,0,40,20),
r2 = new Rectangle(200,200,40,20))
);
Scene scene = new Scene(root);
r1.setId("Rect 1");
r2.setId("Rect 2");
int pickX = 100;
int pickY = 100;
assertSame(r0, SceneShim.test_pick(scene,pickX, pickY));
assertFalse(grp.contains(pickX, pickY));
assertTrue(r0.contains(pickX, pickY));
assertFalse(r1.contains(pickX, pickY));
assertFalse(r2.contains(pickX, pickY));
pickX = 45;
pickY = 50;
assertNull(SceneShim.test_pick(scene,pickX, pickY));
assertFalse(grp.contains(pickX, pickY));
assertFalse(r0.contains(pickX, pickY));
assertFalse(r1.contains(pickX, pickY));
assertFalse(r2.contains(pickX, pickY));
pickX = 38;
pickY = 18;
assertSame(r1, SceneShim.test_pick(scene,pickX, pickY));
assertTrue(grp.contains(pickX, pickY));
assertFalse(r0.contains(pickX, pickY));
assertTrue(r1.contains(pickX, pickY));
assertFalse(r2.contains(pickX, pickY));
pickX = 230;
pickY = 215;
assertSame(r2, SceneShim.test_pick(scene,pickX, pickY));
assertTrue(grp.contains(pickX, pickY));
assertFalse(r0.contains(pickX, pickY));
assertFalse(r1.contains(pickX, pickY));
assertTrue(r2.contains(pickX, pickY));
pickX = 120;
pickY = 110;
assertSame(r0, SceneShim.test_pick(scene,pickX, pickY));
}
@Test
public void testScenePickingGroupAndClip() {
Group grp;
Rectangle r0;
Rectangle r1;
Rectangle r2;
Group root = new Group();
root.getChildren().addAll(r0 = new Rectangle(100, 100, 40, 20),
grp = new Group(r1 = new Rectangle(0,0,40,20),
r2 = new Rectangle(200,200,40,20))
);
Scene scene = new Scene(root);
r1.setId("Rect 1");
r1.setClip(new Circle(20,10,10));
r2.setId("Rect 2");
grp.setClip(new Circle(120,120,120));
int pickX = 38;
int pickY = 18;
assertNull(SceneShim.test_pick(scene,pickX, pickY));
assertFalse(grp.contains(pickX, pickY));
assertFalse(r0.contains(pickX, pickY));
assertFalse(r1.contains(pickX, pickY));
assertFalse(r2.contains(pickX, pickY));
pickX = 230;
pickY = 215;
assertNull(SceneShim.test_pick(scene,pickX, pickY));
assertFalse(grp.contains(pickX, pickY));
assertFalse(r0.contains(pickX, pickY));
assertFalse(r1.contains(pickX, pickY));
assertTrue(r2.contains(pickX, pickY));
pickX = 120;
pickY = 110;
assertSame(r0, SceneShim.test_pick(scene,pickX, pickY));
}
@Test
public void testScenePickingGroupAndClipWithPickOnBounds() {
Group grp;
Rectangle r0;
Rectangle r1;
Rectangle r2;
Group root = new Group();
root.getChildren().addAll(r0 = new Rectangle(100, 100, 40, 20),
grp = new Group(r1 = new Rectangle(0,0,40,20),
r2 = new Rectangle(200,200,40,20))
);
Scene scene = new Scene(root);
r1.setId("Rect 1");
r1.setClip(new Circle(20,10,10));
r2.setId("Rect 2");
grp.setClip(new Circle(120,120,120));
grp.getClip().setPickOnBounds(true);
int pickX = 38;
int pickY = 18;
assertNull(SceneShim.test_pick(scene,pickX, pickY));
assertFalse(grp.contains(pickX, pickY));
assertFalse(r0.contains(pickX, pickY));
assertFalse(r1.contains(pickX, pickY));
assertFalse(r2.contains(pickX, pickY));
pickX = 230;
pickY = 215;
assertSame(r2, SceneShim.test_pick(scene,pickX, pickY));
pickX = 120;
pickY = 110;
assertSame(r0, SceneShim.test_pick(scene,pickX, pickY));
}
@Test
public void testSceneGroupPickOnBounds() {
Group grp;
Rectangle r0;
Rectangle r1;
Rectangle r2;
Group root = new Group();
root.getChildren().addAll(r0 = new Rectangle(100, 100, 40, 20),
grp = new Group(r1 = new Rectangle(0,0,40,20),
r2 = new Rectangle(200,200,40,20))
);
Scene scene = new Scene(root);
r1.setId("Rect 1");
r2.setId("Rect 2");
int pickX = 45;
int pickY = 50;
assertNull(SceneShim.test_pick(scene,pickX, pickY));
grp.setPickOnBounds(true);
assertSame(grp, SceneShim.test_pick(scene,pickX, pickY));
assertTrue(grp.contains(pickX, pickY));
assertFalse(r0.contains(pickX, pickY));
assertFalse(r1.contains(pickX, pickY));
assertFalse(r2.contains(pickX, pickY));
}
}
