package test.javafx.beans.property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyListPropertyBase;
import javafx.beans.property.ReadOnlyListPropertyBaseShim;
import test.javafx.beans.InvalidationListenerMock;
import test.javafx.beans.value.ChangeListenerMock;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.fail;
public class ReadOnlyListPropertyBaseTest {
private static final Object UNDEFINED = null;
private static final Object DEFAULT = null;
private static final ObservableList<Object> VALUE_1 = FXCollections.observableArrayList();
private static final ObservableList<Object> VALUE_2 = FXCollections.observableArrayList(new Object());
private ReadOnlyPropertyMock property;
private InvalidationListenerMock invalidationListener;
private ChangeListenerMock<Object> changeListener;
@Before
public void setUp() throws Exception {
property = new ReadOnlyPropertyMock();
invalidationListener = new InvalidationListenerMock();
changeListener = new ChangeListenerMock<Object>(UNDEFINED);
}
@Test
public void testInvalidationListener() {
property.addListener(invalidationListener);
property.get();
invalidationListener.reset();
property.set(VALUE_1);
invalidationListener.check(property, 1);
property.removeListener(invalidationListener);
invalidationListener.reset();
property.set(VALUE_2);
invalidationListener.check(null, 0);
}
@Test
public void testChangeListener() {
property.addListener(changeListener);
property.get();
changeListener.reset();
property.set(VALUE_1);
changeListener.check(property, DEFAULT, VALUE_1, 1);
property.removeListener(changeListener);
changeListener.reset();
property.set(VALUE_2);
changeListener.check(null, UNDEFINED, UNDEFINED, 0);
}
private static class ReadOnlyPropertyMock extends ReadOnlyListPropertyBase<Object> {
private ObservableList<Object> value;
@Override
public Object getBean() {
return null;
}
@Override
public String getName() {
return null;
}
private void set(ObservableList<Object> value) {
this.value = value;
ReadOnlyListPropertyBaseShim.fireValueChangedEvent(this);
}
@Override
public ObservableList<Object> get() {
return value;
}
@Override
public ReadOnlyIntegerProperty sizeProperty() {
fail("Not in use");
return null;
}
@Override
public ReadOnlyBooleanProperty emptyProperty() {
fail("Not in use");
return null;
}
}
}
