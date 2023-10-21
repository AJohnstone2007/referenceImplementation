package test.javafx.fxml;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.junit.Test;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import static org.junit.Assert.*;
public class FXMLLoader_EventsTest {
@Test
public void testListEvents() throws Exception {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("list_map_set_events_test.fxml"));
Widget widget = (Widget)fxmlLoader.load();
ListMapSetEventsTestController controller = (ListMapSetEventsTestController)fxmlLoader.getController();
assertFalse(controller.listWithParamCalled);
assertFalse(controller.listNoParamCalled);
widget.getChildren().add(new Widget("Widget 4"));
assertTrue(controller.listWithParamCalled);
assertFalse(controller.listNoParamCalled);
}
@Test
public void testMapEvents() throws Exception {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("list_map_set_events_test.fxml"));
Widget widget = (Widget)fxmlLoader.load();
ListMapSetEventsTestController controller = (ListMapSetEventsTestController)fxmlLoader.getController();
assertFalse(controller.mapWithParamCalled);
assertFalse(controller.mapNoParamCalled);
widget.getProperties().put("d", 1000);
assertTrue(controller.mapWithParamCalled);
assertFalse(controller.mapNoParamCalled);
}
@Test
public void testSetEvents() throws Exception {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("list_map_set_events_test.fxml"));
Widget widget = (Widget)fxmlLoader.load();
ListMapSetEventsTestController controller = (ListMapSetEventsTestController)fxmlLoader.getController();
assertFalse(controller.setWithParamCalled);
assertFalse(controller.setNoParamCalled);
widget.getSet().add("x");
assertTrue(controller.setWithParamCalled);
assertFalse(controller.setNoParamCalled);
}
@Test
public void testListEvents_NoParam() throws Exception {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("list_map_set_events_test.fxml"), null, null, new Callback<Class<?>, Object>() {
@Override
public Object call(Class<?> param) {
return new ListMapSetEventsTestController2();
}
});
Widget widget = (Widget)fxmlLoader.load();
ListMapSetEventsTestController2 controller = (ListMapSetEventsTestController2)fxmlLoader.getController();
assertFalse(controller.listNoParamCalled);
widget.getChildren().add(new Widget("Widget 4"));
assertTrue(controller.listNoParamCalled);
}
@Test
public void testMapEvents_NoParam() throws Exception {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("list_map_set_events_test.fxml"), null, null, new Callback<Class<?>, Object>() {
@Override
public Object call(Class<?> param) {
return new ListMapSetEventsTestController2();
}
});
Widget widget = (Widget)fxmlLoader.load();
ListMapSetEventsTestController2 controller = (ListMapSetEventsTestController2)fxmlLoader.getController();
assertFalse(controller.mapNoParamCalled);
widget.getProperties().put("d", 1000);
assertTrue(controller.mapNoParamCalled);
}
@Test
public void testSetEvents_NoParam() throws Exception {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("list_map_set_events_test.fxml"), null, null, new Callback<Class<?>, Object>() {
@Override
public Object call(Class<?> param) {
return new ListMapSetEventsTestController2();
}
});
Widget widget = (Widget)fxmlLoader.load();
ListMapSetEventsTestController2 controller = (ListMapSetEventsTestController2)fxmlLoader.getController();
assertFalse(controller.setNoParamCalled);
widget.getSet().add("x");
assertTrue(controller.setNoParamCalled);
}
@Test
public void testPropertyEvents_jfx2_deprecated() throws IOException {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("property_events_test.fxml"));
fxmlLoader.setController(new PropertyEventsTestDeprecatedController());
fxmlLoader.load();
PropertyEventsTestDeprecatedController controller = fxmlLoader.getController();
assertEquals(controller.getRootName(), "abc");
assertEquals(controller.getChildName(), "def");
final String rootName = "123";
controller.getRoot().setName(rootName);
assertEquals(controller.getRootName(), rootName);
final String childName = "456";
controller.getChild().setName(childName);
assertEquals(controller.getChildName(), childName);
}
@Test
public void testPropertyEvents() throws IOException {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("property_events_test.fxml"));
fxmlLoader.setController(new PropertyEventsTestController());
fxmlLoader.load();
PropertyEventsTestController controller = fxmlLoader.getController();
assertEquals(controller.getRootName(), "abc");
assertEquals(controller.getChildName(), "def");
final String rootName = "123";
controller.getRoot().setName(rootName);
assertEquals(controller.getRootName(), rootName);
final String childName = "456";
controller.getChild().setName(childName);
assertEquals(controller.getChildName(), childName);
}
@Test
public void testPropertyEvents_testNewValue() throws IOException {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("property_events_test_value.fxml"));
fxmlLoader.load();
PropertyEventsTestValueController controller = fxmlLoader.getController();
assertEquals(controller.getRoot().getName(), controller.getRootName());
assertEquals(controller.getChild().getName(), controller.getChildName());
}
@Test
public void testPropertyEvents_testExpressionHandler() throws IOException {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("property_events_test_expression.fxml"));
final boolean[] ref = new boolean[] { false };
fxmlLoader.getNamespace().put("manualAction", new ChangeListener<Object>() {
@Override
public void changed(ObservableValue<? extends Object> ov, Object o, Object n) {
ref[0] = true;
}
});
fxmlLoader.load();
Widget w = fxmlLoader.getRoot();
assertFalse(ref[0]);
w.setName("abc");
assertTrue(ref[0]);
}
@Test(expected = LoadException.class)
public void testPropertyEvents_testExpressionHandler_NA() throws IOException {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("property_events_test_expression_na.fxml"));
fxmlLoader.load();
}
@Test
public void testEvents() throws IOException{
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("events_test.fxml"));
Widget button = fxmlLoader.load();
EventsTestController controller = fxmlLoader.getController();
assertFalse(controller.called);
button.fire();
assertTrue(controller.called);
}
}
