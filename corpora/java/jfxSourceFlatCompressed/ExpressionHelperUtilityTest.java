package test.com.sun.javafx.binding;
import java.util.Arrays;
import java.util.Collections;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableListValue;
import javafx.beans.value.ObservableMapValue;
import javafx.beans.value.ObservableSetValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.SetChangeListener;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
public class ExpressionHelperUtilityTest {
private ObservableValue observableValue;
private ObservableListValue observableList;
private ObservableMapValue observableMap;
private ObservableSetValue observableSet;
private InvalidationListener invalidationListener1;
private InvalidationListener invalidationListener2;
private ChangeListener changeListener1;
private ChangeListener changeListener2;
private ListChangeListener listChangeListener1;
private ListChangeListener listChangeListener2;
private MapChangeListener mapChangeListener1;
private MapChangeListener mapChangeListener2;
private SetChangeListener setChangeListener1;
private SetChangeListener setChangeListener2;
@Before
public void setUp() {
observableValue = new SimpleStringProperty();
observableList = new SimpleListProperty();
observableMap = new SimpleMapProperty();
observableSet = new SimpleSetProperty();
invalidationListener1 = new EmptyInvalidationListener();
invalidationListener2 = new EmptyInvalidationListener();
changeListener1 = new EmptyChangeListener();
changeListener2 = new EmptyChangeListener();
listChangeListener1 = new EmptyListChangeListener();
listChangeListener2 = new EmptyListChangeListener();
mapChangeListener1 = new EmptyMapChangeListener();
mapChangeListener2 = new EmptyMapChangeListener();
setChangeListener1 = new EmptySetChangeListener();
setChangeListener2 = new EmptySetChangeListener();
}
@Test
public void testGetInvalidationListenerFromValue() {
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getInvalidationListeners(observableValue));
observableValue.addListener(invalidationListener1);
assertEquals(Collections.singletonList(invalidationListener1), ExpressionHelperUtility.getInvalidationListeners(observableValue));
observableValue.removeListener(invalidationListener1);
observableValue.addListener(changeListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getInvalidationListeners(observableValue));
observableValue.removeListener(changeListener1);
observableValue.addListener(changeListener1);
observableValue.addListener(changeListener2);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getInvalidationListeners(observableValue));
observableValue.addListener(invalidationListener1);
assertEquals(Collections.singletonList(invalidationListener1), ExpressionHelperUtility.getInvalidationListeners(observableValue));
observableValue.addListener(invalidationListener2);
assertTrue(Arrays.asList(invalidationListener1, invalidationListener2).equals(ExpressionHelperUtility.getInvalidationListeners(observableValue))
|| Arrays.asList(invalidationListener2, invalidationListener1).equals(ExpressionHelperUtility.getInvalidationListeners(observableValue)));
}
@Test
public void testGetChangeListenerFromValue() {
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getChangeListeners(observableValue));
observableValue.addListener(invalidationListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getChangeListeners(observableValue));
observableValue.removeListener(invalidationListener1);
observableValue.addListener(changeListener1);
assertEquals(Collections.singletonList(changeListener1), ExpressionHelperUtility.getChangeListeners(observableValue));
observableValue.removeListener(changeListener1);
observableValue.addListener(invalidationListener1);
observableValue.addListener(invalidationListener2);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getChangeListeners(observableValue));
observableValue.addListener(changeListener1);
assertEquals(Collections.singletonList(changeListener1), ExpressionHelperUtility.getChangeListeners(observableValue));
observableValue.addListener(changeListener2);
assertTrue(Arrays.asList(changeListener1, changeListener2).equals(ExpressionHelperUtility.getChangeListeners(observableValue))
|| Arrays.asList(changeListener2, changeListener1).equals(ExpressionHelperUtility.getChangeListeners(observableValue)));
}
@Test
public void testGetInvalidationListenerFromList() {
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getInvalidationListeners(observableList));
observableList.addListener(invalidationListener1);
assertEquals(Collections.singletonList(invalidationListener1), ExpressionHelperUtility.getInvalidationListeners(observableList));
observableList.removeListener(invalidationListener1);
observableList.addListener(changeListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getInvalidationListeners(observableList));
observableList.removeListener(changeListener1);
observableList.addListener(listChangeListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getInvalidationListeners(observableList));
observableList.removeListener(listChangeListener1);
observableList.addListener(changeListener1);
observableList.addListener(listChangeListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getInvalidationListeners(observableList));
observableList.addListener(invalidationListener1);
assertEquals(Collections.singletonList(invalidationListener1), ExpressionHelperUtility.getInvalidationListeners(observableList));
observableList.addListener(invalidationListener2);
assertTrue(Arrays.asList(invalidationListener1, invalidationListener2).equals(ExpressionHelperUtility.getInvalidationListeners(observableList))
|| Arrays.asList(invalidationListener2, invalidationListener1).equals(ExpressionHelperUtility.getInvalidationListeners(observableList)));
}
@Test
public void testGetChangeListenerFromList() {
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getChangeListeners(observableList));
observableList.addListener(invalidationListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getChangeListeners(observableList));
observableList.removeListener(invalidationListener1);
observableList.addListener(changeListener1);
assertEquals(Collections.singletonList(changeListener1), ExpressionHelperUtility.getChangeListeners(observableList));
observableList.removeListener(changeListener1);
observableList.addListener(listChangeListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getChangeListeners(observableList));
observableList.removeListener(listChangeListener1);
observableList.addListener(invalidationListener1);
observableList.addListener(listChangeListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getChangeListeners(observableList));
observableList.addListener(changeListener1);
assertEquals(Collections.singletonList(changeListener1), ExpressionHelperUtility.getChangeListeners(observableList));
observableList.addListener(changeListener2);
assertTrue(Arrays.asList(changeListener1, changeListener2).equals(ExpressionHelperUtility.getChangeListeners(observableList))
|| Arrays.asList(changeListener2, changeListener1).equals(ExpressionHelperUtility.getChangeListeners(observableList)));
}
@Test
public void testGetListChangeListenerFromList() {
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getListChangeListeners(observableList));
observableList.addListener(invalidationListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getListChangeListeners(observableList));
observableList.removeListener(invalidationListener1);
observableList.addListener(changeListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getListChangeListeners(observableList));
observableList.removeListener(changeListener1);
observableList.addListener(listChangeListener1);
assertEquals(Collections.singletonList(listChangeListener1), ExpressionHelperUtility.getListChangeListeners(observableList));
observableList.removeListener(listChangeListener1);
observableList.addListener(invalidationListener1);
observableList.addListener(changeListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getListChangeListeners(observableList));
observableList.addListener(listChangeListener1);
assertEquals(Collections.singletonList(listChangeListener1), ExpressionHelperUtility.getListChangeListeners(observableList));
observableList.addListener(listChangeListener2);
assertTrue(Arrays.asList(listChangeListener1, listChangeListener2).equals(ExpressionHelperUtility.getListChangeListeners(observableList))
|| Arrays.asList(listChangeListener2, listChangeListener1).equals(ExpressionHelperUtility.getListChangeListeners(observableList)));
}
@Test
public void testGetInvalidationListenerFromMap() {
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getInvalidationListeners(observableMap));
observableMap.addListener(invalidationListener1);
assertEquals(Collections.singletonList(invalidationListener1), ExpressionHelperUtility.getInvalidationListeners(observableMap));
observableMap.removeListener(invalidationListener1);
observableMap.addListener(changeListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getInvalidationListeners(observableMap));
observableMap.removeListener(changeListener1);
observableMap.addListener(mapChangeListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getInvalidationListeners(observableMap));
observableMap.removeListener(mapChangeListener1);
observableMap.addListener(changeListener1);
observableMap.addListener(mapChangeListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getInvalidationListeners(observableMap));
observableMap.addListener(invalidationListener1);
assertEquals(Collections.singletonList(invalidationListener1), ExpressionHelperUtility.getInvalidationListeners(observableMap));
observableMap.addListener(invalidationListener2);
assertTrue(Arrays.asList(invalidationListener1, invalidationListener2).equals(ExpressionHelperUtility.getInvalidationListeners(observableMap))
|| Arrays.asList(invalidationListener2, invalidationListener1).equals(ExpressionHelperUtility.getInvalidationListeners(observableMap)));
}
@Test
public void testGetChangeListenerFromMap() {
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getChangeListeners(observableMap));
observableMap.addListener(invalidationListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getChangeListeners(observableMap));
observableMap.removeListener(invalidationListener1);
observableMap.addListener(changeListener1);
assertEquals(Collections.singletonList(changeListener1), ExpressionHelperUtility.getChangeListeners(observableMap));
observableMap.removeListener(changeListener1);
observableMap.addListener(mapChangeListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getChangeListeners(observableMap));
observableMap.removeListener(mapChangeListener1);
observableMap.addListener(invalidationListener1);
observableMap.addListener(mapChangeListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getChangeListeners(observableMap));
observableMap.addListener(changeListener1);
assertEquals(Collections.singletonList(changeListener1), ExpressionHelperUtility.getChangeListeners(observableMap));
observableMap.addListener(changeListener2);
assertTrue(Arrays.asList(changeListener1, changeListener2).equals(ExpressionHelperUtility.getChangeListeners(observableMap))
|| Arrays.asList(changeListener2, changeListener1).equals(ExpressionHelperUtility.getChangeListeners(observableMap)));
}
@Test
public void testGetMapChangeListenerFromMap() {
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getMapChangeListeners(observableMap));
observableMap.addListener(invalidationListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getMapChangeListeners(observableMap));
observableMap.removeListener(invalidationListener1);
observableMap.addListener(changeListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getMapChangeListeners(observableMap));
observableMap.removeListener(changeListener1);
observableMap.addListener(mapChangeListener1);
assertEquals(Collections.singletonList(mapChangeListener1), ExpressionHelperUtility.getMapChangeListeners(observableMap));
observableMap.removeListener(mapChangeListener1);
observableMap.addListener(invalidationListener1);
observableMap.addListener(changeListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getMapChangeListeners(observableMap));
observableMap.addListener(mapChangeListener1);
assertEquals(Collections.singletonList(mapChangeListener1), ExpressionHelperUtility.getMapChangeListeners(observableMap));
observableMap.addListener(mapChangeListener2);
assertTrue(Arrays.asList(mapChangeListener1, mapChangeListener2).equals(ExpressionHelperUtility.getMapChangeListeners(observableMap))
|| Arrays.asList(mapChangeListener2, mapChangeListener1).equals(ExpressionHelperUtility.getMapChangeListeners(observableMap)));
}
@Test
public void testGetInvalidationListenerFromSet() {
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getInvalidationListeners(observableSet));
observableSet.addListener(invalidationListener1);
assertEquals(Collections.singletonList(invalidationListener1), ExpressionHelperUtility.getInvalidationListeners(observableSet));
observableSet.removeListener(invalidationListener1);
observableSet.addListener(changeListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getInvalidationListeners(observableSet));
observableSet.removeListener(changeListener1);
observableSet.addListener(setChangeListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getInvalidationListeners(observableSet));
observableSet.removeListener(setChangeListener1);
observableSet.addListener(changeListener1);
observableSet.addListener(setChangeListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getInvalidationListeners(observableSet));
observableSet.addListener(invalidationListener1);
assertEquals(Collections.singletonList(invalidationListener1), ExpressionHelperUtility.getInvalidationListeners(observableSet));
observableSet.addListener(invalidationListener2);
assertTrue(Arrays.asList(invalidationListener1, invalidationListener2).equals(ExpressionHelperUtility.getInvalidationListeners(observableSet))
|| Arrays.asList(invalidationListener2, invalidationListener1).equals(ExpressionHelperUtility.getInvalidationListeners(observableSet)));
}
@Test
public void testGetChangeListenerFromSet() {
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getChangeListeners(observableSet));
observableSet.addListener(invalidationListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getChangeListeners(observableSet));
observableSet.removeListener(invalidationListener1);
observableSet.addListener(changeListener1);
assertEquals(Collections.singletonList(changeListener1), ExpressionHelperUtility.getChangeListeners(observableSet));
observableSet.removeListener(changeListener1);
observableSet.addListener(setChangeListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getChangeListeners(observableSet));
observableSet.removeListener(setChangeListener1);
observableSet.addListener(invalidationListener1);
observableSet.addListener(setChangeListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getChangeListeners(observableSet));
observableSet.addListener(changeListener1);
assertEquals(Collections.singletonList(changeListener1), ExpressionHelperUtility.getChangeListeners(observableSet));
observableSet.addListener(changeListener2);
assertTrue(Arrays.asList(changeListener1, changeListener2).equals(ExpressionHelperUtility.getChangeListeners(observableSet))
|| Arrays.asList(changeListener2, changeListener1).equals(ExpressionHelperUtility.getChangeListeners(observableSet)));
}
@Test
public void testGetSetChangeListenerFromSet() {
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getSetChangeListeners(observableSet));
observableSet.addListener(invalidationListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getSetChangeListeners(observableSet));
observableSet.removeListener(invalidationListener1);
observableSet.addListener(changeListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getSetChangeListeners(observableSet));
observableSet.removeListener(changeListener1);
observableSet.addListener(setChangeListener1);
assertEquals(Collections.singletonList(setChangeListener1), ExpressionHelperUtility.getSetChangeListeners(observableSet));
observableSet.removeListener(setChangeListener1);
observableSet.addListener(invalidationListener1);
observableSet.addListener(changeListener1);
assertEquals(Collections.emptyList(), ExpressionHelperUtility.getSetChangeListeners(observableSet));
observableSet.addListener(setChangeListener1);
assertEquals(Collections.singletonList(setChangeListener1), ExpressionHelperUtility.getSetChangeListeners(observableSet));
observableSet.addListener(setChangeListener2);
assertTrue(Arrays.asList(setChangeListener1, setChangeListener2).equals(ExpressionHelperUtility.getSetChangeListeners(observableSet))
|| Arrays.asList(setChangeListener2, setChangeListener1).equals(ExpressionHelperUtility.getSetChangeListeners(observableSet)));
}
private static class EmptyInvalidationListener implements InvalidationListener {
@Override public void invalidated(Observable observable) { }
}
private static class EmptyChangeListener implements ChangeListener<Object> {
@Override public void changed(ObservableValue<? extends Object> observableValue, Object oldValue, Object newValue) { }
}
private static class EmptyListChangeListener implements ListChangeListener<Object> {
@Override public void onChanged(Change<? extends Object> change) { }
}
private static class EmptyMapChangeListener implements MapChangeListener<Object, Object> {
@Override public void onChanged(Change<? extends Object, ? extends Object> change) { }
}
private static class EmptySetChangeListener implements SetChangeListener<Object> {
@Override public void onChanged(Change<? extends Object> change) { }
}
}
