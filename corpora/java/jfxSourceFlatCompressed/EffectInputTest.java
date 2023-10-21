package test.javafx.scene.effect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.pgstub.StubToolkit;
import com.sun.javafx.tk.Toolkit;
import com.sun.scenario.effect.EffectHelper;
import javafx.scene.effect.Effect;
@RunWith(Parameterized.class)
public class EffectInputTest {
private String effect1Name = null;
private String effect2Name = null;
private Node n = null;
final static String[] effects = new String[] {
"Bloom", "BoxBlur", "ColorAdjust", "DisplacementMap",
"DropShadow", "GaussianBlur", "Glow", "InnerShadow",
"MotionBlur", "PerspectiveTransform",
"Reflection", "SepiaTone", "Shadow"
};
@Parameters
public static Collection parameters() {
List list = new ArrayList();
for (int i = 0; i < effects.length; i++) {
for (int j = i; j < effects.length; j++) {
list.add(new String[] { effects[i], effects[j] });
}
}
return list;
}
public EffectInputTest(final String effect1,
final String effect2) {
this.effect1Name = effect1;
this.effect2Name = effect2;
Group root = new Group();
Scene scene = new Scene(root);
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
n = new Rectangle();
root.getChildren().add(n);
}
private void pulse() {
((StubToolkit) Toolkit.getToolkit()).fireTestPulse();
}
private Method getPGInputMethod(String effectName) throws Exception {
if (effectName.equals("Shadow")) {
effectName = "GeneralShadow";
}
final Class pgEffectClass = Class.forName("com.sun.scenario.effect." + effectName);
if (effectName.equals("InnerShadow")
|| effectName.equals("DropShadow")
|| effectName.equals("DisplacementMap")) {
return pgEffectClass.getMethod("getContentInput");
} else {
return pgEffectClass.getMethod("getInput");
}
}
@Test
public void testInput() throws Exception {
final Class effect1Class = Class.forName("javafx.scene.effect." + effect1Name);
final Class effect2Class = Class.forName("javafx.scene.effect." + effect2Name);
Effect effect1 = (Effect) effect1Class.getDeclaredConstructor().newInstance();
Effect effect2 = (Effect) effect2Class.getDeclaredConstructor().newInstance();
final Method getInput1 = effect1Class.getMethod("getInput");
final Method setInput1 = effect1Class.getMethod("setInput", Effect.class);
final Method getInput2 = effect2Class.getMethod("getInput");
final Method setInput2 = effect2Class.getMethod("setInput", Effect.class);
final Method pgGetInput1 = getPGInputMethod(effect1Name);
final Method pgGetInput2 = getPGInputMethod(effect2Name);
n.setEffect(effect1);
setInput1.invoke(effect1, effect2);
assertEquals(effect2, getInput1.invoke(effect1));
pulse();
assertEquals(EffectHelper.getPeer(effect2), pgGetInput1.invoke(EffectHelper.getPeer(effect1)));
setInput1.invoke(effect1, (java.lang.Object) null);
assertEquals((java.lang.Object) null, getInput2.invoke(effect2));
pulse();
assertEquals((java.lang.Object) null, pgGetInput2.invoke(EffectHelper.getPeer(effect2)));
setInput1.invoke(effect1, effect2);
pulse();
n.setEffect(effect2);
setInput1.invoke(effect1, (java.lang.Object) null);
setInput2.invoke(effect2, effect1);
assertEquals(effect1, getInput2.invoke(effect2));
pulse();
assertEquals(EffectHelper.getPeer(effect1), pgGetInput2.invoke(EffectHelper.getPeer(effect2)));
}
@Test
public void testCycle() throws Exception {
final Class effect1Class = Class.forName("javafx.scene.effect." + effect1Name);
final Class effect2Class = Class.forName("javafx.scene.effect." + effect2Name);
Effect effect1 = (Effect) effect1Class.getDeclaredConstructor().newInstance();
Effect effect2 = (Effect) effect2Class.getDeclaredConstructor().newInstance();
final Method getInput1 = effect1Class.getMethod("getInput");
final Method setInput1 = effect1Class.getMethod("setInput", Effect.class);
final Method getInput2 = effect2Class.getMethod("getInput");
final Method setInput2 = effect2Class.getMethod("setInput", Effect.class);
try {
setInput1.invoke(effect1, effect1);
fail("IllegalArgument should have been thrown.");
} catch (InvocationTargetException e) {
assertTrue(e.getTargetException() instanceof IllegalArgumentException);
assertEquals(null, getInput1.invoke(effect1));
}
try {
setInput1.invoke(effect1, effect2);
setInput2.invoke(effect2, effect1);
fail("IllegalArgument should have been thrown.");
} catch (InvocationTargetException e) {
assertTrue(e.getTargetException() instanceof IllegalArgumentException);
assertEquals(null, getInput2.invoke(effect2));
}
}
int countIllegalArgumentException = 0;
@Test
public void testCycleForBoundInput() throws Exception {
Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
if (throwable instanceof IllegalArgumentException) {
countIllegalArgumentException++;
} else {
Thread.currentThread().getThreadGroup().uncaughtException(thread, throwable);
}
});
final Class effect1Class = Class.forName("javafx.scene.effect." + effect1Name);
final Class effect2Class = Class.forName("javafx.scene.effect." + effect2Name);
Effect effect1 = (Effect) effect1Class.getDeclaredConstructor().newInstance();
Effect effect2 = (Effect) effect2Class.getDeclaredConstructor().newInstance();
final Method getInput1 = effect1Class.getMethod("getInput");
final Method setInput2 = effect2Class.getMethod("setInput", Effect.class);
ObjectProperty v = new SimpleObjectProperty();
Method m = effect1Class.getMethod("inputProperty", new Class[] {});
((ObjectProperty)m.invoke(effect1)).bind(v);
v.set(effect1);
assertEquals(null, getInput1.invoke(effect1));
v.set(null);
((ObjectProperty)m.invoke(effect1)).bind(v);
setInput2.invoke(effect2, effect1);
v.set(effect2);
assertEquals(null, getInput1.invoke(effect1));
assertEquals("Cycle in effect chain detected, exception should occur 2 times.", 2, countIllegalArgumentException);
Thread.currentThread().setUncaughtExceptionHandler(null);
}
}
