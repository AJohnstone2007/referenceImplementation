package test.com.sun.javafx.scene.control;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import com.sun.javafx.scene.control.LambdaMultiplePropertyChangeListenerHandler;
import static org.junit.Assert.*;
import static test.com.sun.javafx.scene.control.infrastructure.ControlSkinFactory.*;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
@RunWith(Parameterized.class)
public class LambdaMultipleObservableHandlerTest {
private LambdaMultiplePropertyChangeListenerHandler handler;
private boolean useChangeListener;
@Test
public void testUnregistersSingleConsumerMultipleObservables() {
IntegerProperty p = new SimpleIntegerProperty();
IntegerProperty other = new SimpleIntegerProperty();
int[] count = new int[] {0};
Consumer<ObservableValue<?>> consumer = c -> count[0]++;
registerListener(p, consumer);
registerListener(other, consumer);
unregisterListeners(other);
p.set(100);
other.set(100);
assertEquals(1, count[0]);
}
@Test
public void testUnregistersMultipleConsumers() {
IntegerProperty p = new SimpleIntegerProperty();
int[] action = new int[] {0};
int actionValue = 10;
int[] secondAction = new int[] {0};
registerListener(p, c -> action[0] = actionValue);
registerListener(p, c -> secondAction[0] = action[0]);
Consumer removedChain = unregisterListeners(p);
p.set(100);
assertEquals("none of the removed listeners must be notified", 0, action[0] + secondAction[0]);
addListener(p, removedChain);
p.set(200);
assertEquals("effect of removed consumer chain", actionValue, action[0]);
assertEquals("effect of removed consumer chain", action[0], secondAction[0]);
}
@Test
public void testUnregistersSingleConsumer() {
IntegerProperty p = new SimpleIntegerProperty();
int[] count = new int[] {0};
Consumer<Observable> consumer = c -> count[0]++;
registerListener(p, consumer);
Consumer<Observable> removed = unregisterListeners(p);
p.set(100);
assertEquals(0, count[0]);
assertSame("single registered listener must be returned", consumer, removed);
}
@Test
public void testUnregistersNotRegistered() {
IntegerProperty p = new SimpleIntegerProperty();
assertNull(unregisterListeners(p));
}
@Test
public void testUnregistersNull() {
assertNull(unregisterListeners(null));
}
@Test
public void testRegisterConsumerToMultipleObservables() {
IntegerProperty p = new SimpleIntegerProperty();
IntegerProperty other = new SimpleIntegerProperty();
int[] count = new int[] {0};
Consumer<Observable> consumer = c -> count[0]++;
registerListener(p, consumer);
registerListener(other, consumer);
p.set(100);
other.set(100);
assertEquals(2, count[0]);
}
@Test
public void testRegisterMultipleConsumerToSingleObservable() {
IntegerProperty p = new SimpleIntegerProperty();
int[] action = new int[] {0};
int actionValue = 10;
int[] secondAction = new int[] {0};
registerListener(p, c -> action[0] = actionValue);
registerListener(p, c -> secondAction[0] = action[0]);
p.set(100);
assertEquals(actionValue, action[0]);
assertEquals(action[0], secondAction[0]);
}
@Test
public void testRegister() {
IntegerProperty p = new SimpleIntegerProperty();
int[] count = new int[] {0};
registerListener(p, c -> count[0]++);
p.set(100);
assertEquals(1, count[0]);
}
@Test
public void testRegisterNullConsumer() {
IntegerProperty p = new SimpleIntegerProperty();
registerListener(p, null);
}
@Test
public void testRegisterNullObservable() {
registerListener(null, c -> {});
}
@Test
public void testDispose() {
IntegerProperty p = new SimpleIntegerProperty();
int[] count = new int[] {0};
registerListener(p, c -> count[0]++);
handler.dispose();
p.set(100);
assertEquals("listener must not be invoked after dispose", 0, count[0]);
registerListener(p, c -> count[0]++);
p.set(200);
assertEquals("listener must be invoked when re-registered after dispose", 1, count[0]);
}
@Test
public void testRegisterMemoryLeak() {
IntegerProperty p = new SimpleIntegerProperty();
int[] count = new int[] {0};
Consumer<ObservableValue<?>> consumer = c -> count[0]++;
LambdaMultiplePropertyChangeListenerHandler handler = new LambdaMultiplePropertyChangeListenerHandler();
WeakReference<LambdaMultiplePropertyChangeListenerHandler> ref =
new WeakReference<>(handler);
registerListener(handler, p, consumer);
p.setValue(100);
int notified = count[0];
assertEquals("sanity: listener invoked", notified, count[0]);
assertNotNull(ref.get());
handler = null;
attemptGC(ref);
assertNull("handler must be gc'ed", ref.get());
p.setValue(200);
assertEquals("listener must not be invoked after gc", notified, count[0]);
}
@Test
public void testRegisterBoth() {
IntegerProperty p = new SimpleIntegerProperty();
int[] count = new int[] {0};
handler.registerChangeListener(p, c -> count[0]++);
handler.registerInvalidationListener(p, c -> count[0]++);
p.set(100);
assertEquals("both listener types must be invoked", 2, count[0]);
}
@Test
public void testRegisterBothRemoveChangeListener() {
IntegerProperty p = new SimpleIntegerProperty();
int[] count = new int[] {0};
handler.registerChangeListener(p, c -> count[0]++);
handler.registerInvalidationListener(p, c -> count[0]++);
handler.unregisterChangeListeners(p);
p.set(200);
assertEquals("", 1, count[0]);
}
@Test
public void testRegisterBothRemoveInvalidationListener() {
IntegerProperty p = new SimpleIntegerProperty();
int[] count = new int[] {0};
handler.registerChangeListener(p, c -> count[0]++);
handler.registerInvalidationListener(p, c -> count[0]++);
handler.unregisterInvalidationListeners(p);
p.set(200);
assertEquals("", 1, count[0]);
}
@Test
public void testBindingInvalid() {
IntegerProperty num1 = new SimpleIntegerProperty(1);
IntegerProperty num2 = new SimpleIntegerProperty(2);
NumberBinding p = Bindings.add(num1,num2);
int[] count = new int[] {0};
handler.registerChangeListener(p, c -> count[0]++);
handler.registerInvalidationListener(p, c -> count[0]++);
handler.unregisterChangeListeners(p);
num1.set(200);
assertEquals("sanity: received invalidation", 1, count[0]);
assertFalse("binding must not be valid", p.isValid());
}
protected void registerListener(Observable p, Consumer consumer) {
registerListener(handler, p, consumer);
}
protected void registerListener(LambdaMultiplePropertyChangeListenerHandler handler, Observable p, Consumer consumer) {
if (useChangeListener) {
handler.registerChangeListener((ObservableValue<?>) p, consumer);
} else {
handler.registerInvalidationListener(p, consumer);
}
}
protected Consumer unregisterListeners(Observable p) {
return unregisterListeners(handler, p);
}
protected Consumer unregisterListeners(LambdaMultiplePropertyChangeListenerHandler handler, Observable p) {
if (useChangeListener) {
return handler.unregisterChangeListeners((ObservableValue<?>) p);
}
return handler.unregisterInvalidationListeners(p);
}
protected void addListener(ObservableValue<?> p, Consumer<Observable> consumer) {
if (useChangeListener) {
p.addListener((obs, ov, nv) -> consumer.accept(obs));
} else {
p.addListener(obs -> consumer.accept(obs));
}
}
@Parameterized.Parameters
public static Collection<Object[]> data() {
Object[][] data = new Object[][] {
{true},
{false}
};
return Arrays.asList(data);
}
public LambdaMultipleObservableHandlerTest(boolean useChangeListener) {
this.useChangeListener = useChangeListener;
}
@Before
public void setup() {
this.handler = new LambdaMultiplePropertyChangeListenerHandler();
}
}
