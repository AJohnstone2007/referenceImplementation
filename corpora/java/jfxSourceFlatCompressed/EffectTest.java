package test.javafx.scene.effect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.junit.Test;
import test.com.sun.javafx.pgstub.StubToolkit;
import com.sun.javafx.tk.Toolkit;
import com.sun.scenario.effect.EffectHelper;
import javafx.scene.effect.Blend;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DisplacementMap;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.MotionBlur;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.effect.Reflection;
import javafx.scene.effect.SepiaTone;
import javafx.scene.effect.Shadow;
public class EffectTest {
@Test
public void testAdding() {
Bloom b = new Bloom();
Group g = new Group();
g.setEffect(b);
assertEquals(b, g.getEffect());
}
@Test
public void testRemoving() {
Bloom b = new Bloom();
Group g = new Group();
g.setEffect(b);
g.setEffect(null);
assertNull(g.getEffect());
}
@Test
public void testPropertyPropagationWithChaining() {
Group root = new Group();
Scene scene = new Scene(root);
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
StubToolkit toolkit = (StubToolkit) Toolkit.getToolkit();
Rectangle n1 = new Rectangle();
Rectangle n2 = new Rectangle();
Rectangle n3 = new Rectangle();
root.getChildren().addAll(n1, n2);
Bloom bloom = new Bloom();
n1.setEffect(bloom);
Glow glow = new Glow();
bloom.setInput(glow);
glow.setLevel(1.1);
assertEquals(1.1, glow.getLevel(), 1e-100);
toolkit.fireTestPulse();
assertEquals(1.0f, (float) ((com.sun.scenario.effect.Glow) EffectHelper.getPeer(glow)).getLevel(), 1e-100);
n2.setEffect(glow);
glow.setLevel(0.5);
assertEquals(0.5, glow.getLevel(), 1e-100);
toolkit.fireTestPulse();
assertEquals(0.5f, (float)((com.sun.scenario.effect.Glow) EffectHelper.getPeer(glow)).getLevel(), 1e-100);
Bloom bloom2 = new Bloom();
glow.setInput(bloom2);
bloom2.setThreshold(0.1);
assertEquals(0.1, bloom2.getThreshold(), 1e-100);
toolkit.fireTestPulse();
assertEquals(0.1f, (float) ((com.sun.scenario.effect.Bloom) EffectHelper.getPeer(bloom2)).getThreshold(), 1e-100);
Bloom bloom3 = new Bloom();
n3.setEffect(bloom3);
bloom3.setThreshold(0.1);
root.getChildren().add(n3);
assertEquals(0.1, bloom3.getThreshold(), 1e-100);
toolkit.fireTestPulse();
assertEquals(0.1f, (float) ((com.sun.scenario.effect.Bloom) EffectHelper.getPeer(bloom3)).getThreshold(), 1e-100);
}
@Test
public void testPropertyPropagationWithChainingAndBinding() {
Group root = new Group();
Scene scene = new Scene(root);
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
StubToolkit toolkit = (StubToolkit) Toolkit.getToolkit();
Rectangle n1 = new Rectangle();
Rectangle n2 = new Rectangle();
Rectangle n3 = new Rectangle();
root.getChildren().addAll(n1, n2);
Bloom bloom = new Bloom();
n1.setEffect(bloom);
ObjectProperty ov = new SimpleObjectProperty();
bloom.inputProperty().bind(ov);
Glow glow = new Glow();
ov.set(glow);
glow.setLevel(1.1);
assertEquals(1.1, glow.getLevel(), 1e-100);
toolkit.fireTestPulse();
assertEquals(1.0f, (float) ((com.sun.scenario.effect.Glow) EffectHelper.getPeer(glow)).getLevel(), 1e-100);
n2.setEffect(glow);
glow.setLevel(0.5);
assertEquals(0.5, glow.getLevel(), 1e-100);
toolkit.fireTestPulse();
assertEquals(0.5f, (float)((com.sun.scenario.effect.Glow) EffectHelper.getPeer(glow)).getLevel(), 1e-100);
ObjectProperty ov2 = new SimpleObjectProperty();
glow.inputProperty().bind(ov2);
Bloom bloom2 = new Bloom();
ov2.set(bloom2);
bloom2.setThreshold(0.1);
assertEquals(0.1, bloom2.getThreshold(), 1e-100);
toolkit.fireTestPulse();
assertEquals(0.1f, (float) ((com.sun.scenario.effect.Bloom) EffectHelper.getPeer(bloom2)).getThreshold(), 1e-100);
Bloom bloom3 = new Bloom();
ov2.set(bloom3);
bloom3.setThreshold(0.1);
assertEquals(0.1, bloom3.getThreshold(), 1e-100);
assertTrue(EffectHelper.isEffectDirty(glow));
assertTrue(EffectHelper.isEffectDirty(bloom));
toolkit.fireTestPulse();
assertEquals(0.1f, (float) ((com.sun.scenario.effect.Bloom) EffectHelper.getPeer(bloom3)).getThreshold(), 1e-100);
bloom2.setThreshold(0.2);
assertFalse(EffectHelper.isEffectDirty(glow));
assertEquals(0.2, bloom2.getThreshold(), 1e-100);
toolkit.fireTestPulse();
assertEquals(0.1f, (float) ((com.sun.scenario.effect.Bloom) EffectHelper.getPeer(bloom2)).getThreshold(), 1e-100);
}
@Test
public void testLongCycle() {
Blend blend = new Blend();
Bloom bloom = new Bloom();
BoxBlur boxBlur = new BoxBlur();
ColorAdjust colorAdjust = new ColorAdjust();
DisplacementMap displacementMap = new DisplacementMap();
DropShadow dropShadow = new DropShadow();
GaussianBlur gaussianBlur = new GaussianBlur();
Glow glow = new Glow();
InnerShadow innerShadow = new InnerShadow();
MotionBlur motionBlur = new MotionBlur();
PerspectiveTransform perspectiveTransform = new PerspectiveTransform();
Reflection reflection = new Reflection();
SepiaTone sepiaTone = new SepiaTone();
Shadow shadow = new Shadow();
blend.setTopInput(bloom);
bloom.setInput(boxBlur);
boxBlur.setInput(colorAdjust);
colorAdjust.setInput(displacementMap);
displacementMap.setInput(dropShadow);
dropShadow.setInput(gaussianBlur);
gaussianBlur.setInput(glow);
glow.setInput(innerShadow);
innerShadow.setInput(motionBlur);
motionBlur.setInput(perspectiveTransform);
perspectiveTransform.setInput(reflection);
reflection.setInput(sepiaTone);
sepiaTone.setInput(shadow);
try {
shadow.setInput(blend);
fail("IllegalArgument should have been thrown.");
} catch (IllegalArgumentException e) {
assertEquals(null, shadow.getInput());
}
}
}
