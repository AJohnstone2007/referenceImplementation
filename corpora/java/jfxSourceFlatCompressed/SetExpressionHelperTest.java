package test.com.sun.javafx.binding;
import com.sun.javafx.binding.SetExpressionHelper;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import org.junit.Before;
import org.junit.Test;
import test.javafx.beans.InvalidationListenerMock;
import test.javafx.beans.value.ChangeListenerMock;
import test.javafx.collections.MockSetObserver;
public class SetExpressionHelperTest {
private static final ObservableSet<Object> UNDEFINED = null;
private Object value;
private ObservableSet<Object> data1;
private ObservableSet<Object> data2;
private SetExpressionHelper helper;
private SetProperty<Object> observable;
private InvalidationListenerMock[] invalidationListener;
private ChangeListenerMock<? super ObservableSet<Object>>[] changeListener;
private MockSetObserver<Object>[] setChangeListener;
@Before
public void setUp() {
value = new Object();
data1 = FXCollections.observableSet();
data2 = FXCollections.observableSet(value);
helper = null;
observable = new SimpleSetProperty<>(data1);
invalidationListener = new InvalidationListenerMock[]{
new InvalidationListenerMock(), new InvalidationListenerMock(), new InvalidationListenerMock()
};
changeListener = new ChangeListenerMock[]{
new ChangeListenerMock(UNDEFINED), new ChangeListenerMock(UNDEFINED), new ChangeListenerMock(UNDEFINED)
};
setChangeListener = new MockSetObserver[]{
new MockSetObserver<>(), new MockSetObserver<>(), new MockSetObserver<>()
};
}
@Test
public void testRemoveInvalidation() {
helper = SetExpressionHelper.addListener(helper, observable, changeListener[0]);
helper = SetExpressionHelper.addListener(helper, observable, setChangeListener[0]);
helper = SetExpressionHelper.removeListener(helper, invalidationListener[1]);
helper = SetExpressionHelper.addListener(helper, observable, invalidationListener[0]);
helper = SetExpressionHelper.removeListener(helper, invalidationListener[1]);
helper = SetExpressionHelper.addListener(helper, observable, invalidationListener[2]);
helper = SetExpressionHelper.addListener(helper, observable, invalidationListener[1]);
helper = SetExpressionHelper.removeListener(helper, invalidationListener[0]);
SetExpressionHelper.fireValueChangedEvent(helper);
invalidationListener[0].check(null, 0);
invalidationListener[1].check(observable, 1);
invalidationListener[2].check(observable, 1);
helper = SetExpressionHelper.removeListener(helper, invalidationListener[1]);
SetExpressionHelper.fireValueChangedEvent(helper);
invalidationListener[0].check(null, 0);
invalidationListener[1].check(null, 0);
invalidationListener[2].check(observable, 1);
helper = SetExpressionHelper.removeListener(helper, invalidationListener[2]);
SetExpressionHelper.fireValueChangedEvent(helper);
invalidationListener[0].check(null, 0);
invalidationListener[1].check(null, 0);
invalidationListener[2].check(null, 0);
}
@Test
public void testRemoveInvalidation8136465() {
helper = SetExpressionHelper.addListener(helper, observable, invalidationListener[0]);
helper = SetExpressionHelper.addListener(helper, observable, invalidationListener[1]);
SetExpressionHelper.fireValueChangedEvent(helper);
invalidationListener[0].check(observable, 1);
invalidationListener[1].check(observable, 1);
helper = SetExpressionHelper.removeListener(helper, invalidationListener[0]);
SetExpressionHelper.fireValueChangedEvent(helper);
invalidationListener[0].check(null, 0);
invalidationListener[1].check(observable, 1);
}
@Test
public void testRemoveChange() {
helper = SetExpressionHelper.addListener(helper, observable, invalidationListener[0]);
helper = SetExpressionHelper.addListener(helper, observable, setChangeListener[0]);
helper = SetExpressionHelper.removeListener(helper, changeListener[1]);
helper = SetExpressionHelper.addListener(helper, observable, changeListener[0]);
helper = SetExpressionHelper.removeListener(helper, changeListener[1]);
helper = SetExpressionHelper.addListener(helper, observable, changeListener[2]);
helper = SetExpressionHelper.addListener(helper, observable, changeListener[1]);
helper = SetExpressionHelper.removeListener(helper, changeListener[0]);
observable.set(data2);
SetExpressionHelper.fireValueChangedEvent(helper);
changeListener[0].check(null, UNDEFINED, UNDEFINED, 0);
changeListener[1].check(observable, data1, data2, 1);
changeListener[2].check(observable, data1, data2, 1);
helper = SetExpressionHelper.removeListener(helper, changeListener[1]);
observable.set(data1);
SetExpressionHelper.fireValueChangedEvent(helper);
changeListener[0].check(null, UNDEFINED, UNDEFINED, 0);
changeListener[1].check(null, UNDEFINED, UNDEFINED, 0);
changeListener[2].check(observable, data2, data1, 1);
helper = SetExpressionHelper.removeListener(helper, changeListener[2]);
observable.set(data2);
SetExpressionHelper.fireValueChangedEvent(helper);
changeListener[0].check(null, UNDEFINED, UNDEFINED, 0);
changeListener[1].check(null, UNDEFINED, UNDEFINED, 0);
changeListener[2].check(null, UNDEFINED, UNDEFINED, 0);
}
@Test
public void testRemoveChange8136465() {
helper = SetExpressionHelper.addListener(helper, observable, changeListener[0]);
helper = SetExpressionHelper.addListener(helper, observable, changeListener[1]);
observable.set(data2);
SetExpressionHelper.fireValueChangedEvent(helper);
changeListener[0].check(observable, data1, data2, 1);
changeListener[1].check(observable, data1, data2, 1);
helper = SetExpressionHelper.removeListener(helper, changeListener[0]);
observable.set(data1);
SetExpressionHelper.fireValueChangedEvent(helper);
changeListener[0].check(null, UNDEFINED, UNDEFINED, 0);
changeListener[1].check(observable, data2, data1, 1);
}
@Test
public void testRemoveMapChange() {
helper = SetExpressionHelper.addListener(helper, observable, invalidationListener[0]);
helper = SetExpressionHelper.addListener(helper, observable, changeListener[0]);
helper = SetExpressionHelper.removeListener(helper, setChangeListener[1]);
helper = SetExpressionHelper.addListener(helper, observable, setChangeListener[0]);
helper = SetExpressionHelper.removeListener(helper, setChangeListener[1]);
helper = SetExpressionHelper.addListener(helper, observable, setChangeListener[2]);
helper = SetExpressionHelper.addListener(helper, observable, setChangeListener[1]);
helper = SetExpressionHelper.removeListener(helper, setChangeListener[0]);
observable.set(data2);
SetExpressionHelper.fireValueChangedEvent(helper);
setChangeListener[0].check0();
setChangeListener[1].assertAdded(MockSetObserver.Tuple.tup(value));
setChangeListener[1].clear();
setChangeListener[2].assertAdded(MockSetObserver.Tuple.tup(value));
setChangeListener[2].clear();
helper = SetExpressionHelper.removeListener(helper, setChangeListener[1]);
observable.set(data1);
SetExpressionHelper.fireValueChangedEvent(helper);
setChangeListener[0].check0();
setChangeListener[1].check0();
setChangeListener[2].assertRemoved(MockSetObserver.Tuple.tup(value));
setChangeListener[2].clear();
helper = SetExpressionHelper.removeListener(helper, setChangeListener[2]);
observable.set(data2);
SetExpressionHelper.fireValueChangedEvent(helper);
setChangeListener[0].check0();
setChangeListener[1].check0();
setChangeListener[2].check0();
}
@Test
public void testRemoveSetChange8136465() {
helper = SetExpressionHelper.addListener(helper, observable, setChangeListener[0]);
helper = SetExpressionHelper.addListener(helper, observable, setChangeListener[1]);
observable.set(data2);
SetExpressionHelper.fireValueChangedEvent(helper);
setChangeListener[0].assertAdded(MockSetObserver.Tuple.tup(value));
setChangeListener[0].clear();
setChangeListener[1].assertAdded(MockSetObserver.Tuple.tup(value));
setChangeListener[1].clear();
helper = SetExpressionHelper.removeListener(helper, setChangeListener[0]);
observable.set(data1);
SetExpressionHelper.fireValueChangedEvent(helper);
setChangeListener[0].check0();
setChangeListener[1].assertRemoved(MockSetObserver.Tuple.tup(value));
setChangeListener[1].clear();
}
}
