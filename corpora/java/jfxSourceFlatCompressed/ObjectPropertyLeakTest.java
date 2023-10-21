package test.javafx.beans.property;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class ObjectPropertyLeakTest {
private static final int OBJ_COUNT = 10;
private final ArrayList<Property<?>> origList = new ArrayList<>();
private final ArrayList<Property<?>> wrappedList = new ArrayList<>();
private final ArrayList<WeakReference<Property<?>>> origRefs = new ArrayList<>();
private final ArrayList<WeakReference<Property<?>>> wrappedRefs = new ArrayList<>();
private void checkRefs(String name, int numExpected,
ArrayList<WeakReference<Property<?>>> refs) {
int count = 0;
for (var ref : refs) {
if (ref.get() != null) count++;
}
final String msg = name + " properties should "
+ (numExpected > 0 ? "NOT be GCed" : "be GCed");
assertEquals(msg, numExpected, count);
}
private void commonLeakTest(int origExpected, int wrappedExpected)
throws Exception {
for (int i = 0; i < 5; i++) {
System.gc();
Thread.sleep(50);
}
checkRefs("Original", origExpected, origRefs);
checkRefs("Wrapped", wrappedExpected, wrappedRefs);
}
private void commonLeakTest() throws Exception {
commonLeakTest(OBJ_COUNT, OBJ_COUNT);
wrappedList.clear();
commonLeakTest(OBJ_COUNT, 0);
origList.clear();
commonLeakTest(0, 0);
}
private void saveRefs(Property<?> origProp, Property<?> wrappedProp) {
origList.add(origProp);
wrappedList.add(wrappedProp);
origRefs.add(new WeakReference<>(origProp));
wrappedRefs.add(new WeakReference<>(wrappedProp));
}
@Test
public void testBooleanPropertyAsObjectLeak() throws Exception {
for (int i = 0; i < OBJ_COUNT; i++) {
final BooleanProperty origProp = new SimpleBooleanProperty(true);
final ObjectProperty<Boolean> wrappedProp = origProp.asObject();
saveRefs(origProp, wrappedProp);
}
commonLeakTest();
}
@Test
public void testObjectToBooleanLeak() throws Exception {
for (int i = 0; i < OBJ_COUNT; i++) {
final ObjectProperty<Boolean> origProp = new SimpleObjectProperty<>(true);
final BooleanProperty wrappedProp = BooleanProperty.booleanProperty(origProp);
saveRefs(origProp, wrappedProp);
}
commonLeakTest();
}
@Test
public void testDoublePropertyAsObjectLeak() throws Exception {
for (int i = 0; i < OBJ_COUNT; i++) {
final DoubleProperty origProp = new SimpleDoubleProperty(1.0);
final ObjectProperty<Double> wrappedProp = origProp.asObject();
saveRefs(origProp, wrappedProp);
}
commonLeakTest();
}
@Test
public void testObjectToDoubleLeak() throws Exception {
for (int i = 0; i < OBJ_COUNT; i++) {
final ObjectProperty<Double> origProp = new SimpleObjectProperty<>(1.0);
final DoubleProperty wrappedProp = DoubleProperty.doubleProperty(origProp);
saveRefs(origProp, wrappedProp);
}
commonLeakTest();
}
@Test
public void testFloatPropertyAsObjectLeak() throws Exception {
for (int i = 0; i < OBJ_COUNT; i++) {
final FloatProperty origProp = new SimpleFloatProperty(1.0f);
final ObjectProperty<Float> wrappedProp = origProp.asObject();
saveRefs(origProp, wrappedProp);
}
commonLeakTest();
}
@Test
public void testObjectToFloatLeak() throws Exception {
for (int i = 0; i < OBJ_COUNT; i++) {
final ObjectProperty<Float> origProp = new SimpleObjectProperty<>(1.0f);
final FloatProperty wrappedProp = FloatProperty.floatProperty(origProp);
saveRefs(origProp, wrappedProp);
}
commonLeakTest();
}
@Test
public void testIntegerPropertyAsObjectLeak() throws Exception {
for (int i = 0; i < OBJ_COUNT; i++) {
final IntegerProperty origProp = new SimpleIntegerProperty(1);
final ObjectProperty<Integer> wrappedProp = origProp.asObject();
saveRefs(origProp, wrappedProp);
}
commonLeakTest();
}
@Test
public void testObjectToIntegerLeak() throws Exception {
for (int i = 0; i < OBJ_COUNT; i++) {
final ObjectProperty<Integer> origProp = new SimpleObjectProperty<>(1);
final IntegerProperty wrappedProp = IntegerProperty.integerProperty(origProp);
saveRefs(origProp, wrappedProp);
}
commonLeakTest();
}
@Test
public void testLongPropertyAsObjectLeak() throws Exception {
for (int i = 0; i < OBJ_COUNT; i++) {
final LongProperty origProp = new SimpleLongProperty(1L);
final ObjectProperty<Long> wrappedProp = origProp.asObject();
saveRefs(origProp, wrappedProp);
}
commonLeakTest();
}
@Test
public void testObjectToLongLeak() throws Exception {
for (int i = 0; i < OBJ_COUNT; i++) {
final ObjectProperty<Long> origProp = new SimpleObjectProperty<>(1L);
final LongProperty wrappedProp = LongProperty.longProperty(origProp);
saveRefs(origProp, wrappedProp);
}
commonLeakTest();
}
}
