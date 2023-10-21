package test.com.sun.javafx.scene.control.infrastructure;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import static test.com.sun.javafx.scene.control.infrastructure.ControlSkinFactory.*;
import javafx.scene.control.Control;
import javafx.scene.control.ControlShim;
import javafx.scene.control.Skin;
public class ControlSkinFactoryTest {
@Test
public void testConvertToArray() {
List<Class<Control>> controls = getControlClasses();
List<Object[]> asArray = asArrays(controls);
for (int i = 0; i < controls.size(); i++) {
assertEquals(1, asArray.get(i).length);
assertSame(controls.get(i), asArray.get(i)[0]);
}
}
@Test
public void testControlClassesWithBehavior() {
List<Class<Control>> controls = getControlClassesWithBehavior();
assertEquals(controlClasses.length - withoutBehaviors.size(), controls.size());
for (Class<Control> class1 : controls) {
Control control = createControl(class1);
ControlShim.installDefaultSkin(control);
getBehavior(control.getSkin());
createBehavior(control);
}
}
@Test
public void testGetControls() {
List<Control> controls = getControls();
assertEquals(controlClasses.length, controls.size());
for (int i = 0; i < controlClasses.length; i++) {
Class<Control> controlClass = (Class<Control>) controlClasses[i][0];
assertSame(controlClass, controls.get(i).getClass());
}
}
@Test
public void testGetControlClasses() {
List<Class<Control>> controls = getControlClasses();
assertEquals(controlClasses.length, controls.size());
for (int i = 0; i < controlClasses.length; i++) {
Class<Control> controlClass = (Class<Control>) controlClasses[i][0];
assertSame(controlClass, controls.get(i));
}
}
@Test
public void testAlternativeSkinAssignable() {
for (int i = 0; i < controlClasses.length; i++) {
Class<Control> controlClass = (Class<Control>) controlClasses[i][0];
Control control = createControl(controlClass);
Skin<?> old = replaceSkin(control);
assertNotNull(control.getSkin());
assertNotSame(old, control.getSkin());
}
}
@Test
public void testControlInstantiatable() {
for (int i = 0; i < controlClasses.length; i++) {
Class<Control> controlClass = (Class<Control>) controlClasses[i][0];
Control control = createControl(controlClass);
assertSame(controlClass, control.getClass());
}
}
@Test
public void testControlsAndSkin() {
assertEquals(alternativeSkinClassMap.size(), controlClasses.length);
for (int i = 0; i < controlClasses.length; i++) {
Class<Control> controlClass = (Class<Control>) controlClasses[i][0];
assertTrue(alternativeSkinClassMap.containsKey(controlClass));
}
}
}
