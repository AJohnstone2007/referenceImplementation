package test.com.sun.javafx.scene.control.behavior;
import java.lang.ref.WeakReference;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.ListCellBehavior;
import com.sun.javafx.scene.control.behavior.TextFieldBehavior;
import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.inputmap.KeyBinding;
import com.sun.javafx.scene.control.inputmap.InputMap.KeyMapping;
import static com.sun.javafx.scene.control.behavior.TextBehaviorShim.*;
import static javafx.collections.FXCollections.*;
import static javafx.scene.control.skin.TextInputSkinShim.*;
import static org.junit.Assert.*;
import static test.com.sun.javafx.scene.control.infrastructure.ControlSkinFactory.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class BehaviorCleanupTest {
private Scene scene;
private Stage stage;
private Pane root;
@Test
public void testTextAreaFocusListener() {
TextArea control = new TextArea("some text");
showControl(control, true);
assertTrue("caret must be blinking if focused", isCaretBlinking(control));
Button button = new Button("dummy");
showControl(button, true);
assertFalse("caret must not be blinking if not focused", isCaretBlinking(control));
}
@Test
public void testFocusListener() {
TextField control = new TextField("some text");
showControl(control, true);
assertTrue("caret must be blinking if focused", isCaretBlinking(control));
Button button = new Button("dummy");
showControl(button, true);
assertFalse("caret must not be blinking if not focused", isCaretBlinking(control));
}
@Test
public void testFocusOwnerListenerRegisteredInitially() {
TextField control = new TextField("some text");
showControl(control, true);
assertEquals("all text selected", control.getText(), control.getSelectedText());
Button button = new Button("dummy");
showControl(button, true);
assertEquals("selection cleared", 0, control.getSelectedText().length());
}
@Test
public void testFocusOwnerListenerOnSceneChanged() {
String firstWord = "some";
String secondWord = "text";
String text = firstWord + " " + secondWord;
TextField control = new TextField(text);
showControl(control, true);
Button button = new Button("dummy");
showControl(button, false);
control.selectNextWord();
assertEquals("sanity: ", secondWord, control.getSelectedText());
root.getChildren().remove(control);
assertEquals("selection unchanged after remove", secondWord, control.getSelectedText());
Button secondButton = new Button("another dummy");
showControl(secondButton, true);
assertEquals("selection unchanged after focusOwner change in old scene",
secondWord, control.getSelectedText());
root.getChildren().add(control);
control.requestFocus();
assertEquals("selection changed on becoming scene's focusOwner",
text, control.getSelectedText());
}
@Test
public void testFocusOwnerListenerSecondStage() {
String firstWord = "some";
String secondWord = "text";
String text = firstWord + " " + secondWord;
TextField control = new TextField(text);
showControl(control, true);
Button button = new Button("dummy");
showControl(button, false);
control.selectNextWord();
assertEquals("sanity: ", secondWord, control.getSelectedText());
VBox secondRoot = new VBox(10, new Button("secondButton"));
Scene secondScene = new Scene(secondRoot);
Stage secondStage = new Stage();
secondStage.setScene(secondScene);
secondStage.show();
secondStage.requestFocus();
try {
assertTrue("sanity: ", secondStage.isFocused());
assertEquals("selection unchanged", secondWord, control.getSelectedText());
stage.requestFocus();
assertTrue("sanity: ", stage.isFocused());
assertTrue("sanity: ", control.isFocused());
assertEquals("selection unchanged", secondWord, control.getSelectedText());
} finally {
secondStage.hide();
}
}
@Test
public void testChildMapsCleared() {
TextField control = new TextField("some text");
TextFieldBehavior behavior = (TextFieldBehavior) createBehavior(control);
InputMap<?> inputMap = behavior.getInputMap();
assertFalse("sanity: inputMap has child maps", inputMap.getChildInputMaps().isEmpty());
behavior.dispose();
assertEquals("default child maps must be cleared", 0, inputMap.getChildInputMaps().size());
}
@Test
public void testDefaultMappingsCleared() {
TextField control = new TextField("some text");
TextFieldBehavior behavior = (TextFieldBehavior) createBehavior(control);
InputMap<?> inputMap = behavior.getInputMap();
assertFalse("sanity: inputMap has mappings", inputMap.getMappings().isEmpty());
behavior.dispose();
assertEquals("default mappings must be cleared", 0, inputMap.getMappings().size());
}
@Test
public void testKeyPadMapping() {
TextField control = new TextField("some text");
TextFieldBehavior behavior = (TextFieldBehavior) createBehavior(control);
InputMap<?> inputMap = behavior.getInputMap();
KeyCode expectedCode = KeyCode.KP_LEFT;
KeyMapping expectedMapping = new KeyMapping(expectedCode, null);
assertTrue(inputMap.getMappings().contains(expectedMapping));
}
@Test
public void testKeyPadMappingChildInputMap() {
TextField control = new TextField("some text");
TextFieldBehavior behavior = (TextFieldBehavior) createBehavior(control);
InputMap<?> inputMap = behavior.getInputMap();
KeyCode expectedCode = KeyCode.KP_LEFT;
InputMap<?> childInputMapMac = inputMap.getChildInputMaps().get(0);
KeyMapping expectedMac = new KeyMapping(new KeyBinding(expectedCode).shortcut(), null);
assertTrue(childInputMapMac.getMappings().contains(expectedMac));
InputMap<?> childInputMapNotMac = inputMap.getChildInputMaps().get(1);
KeyMapping expectedNotMac = new KeyMapping(new KeyBinding(expectedCode).ctrl(), null);
assertTrue(childInputMapNotMac.getMappings().contains(expectedNotMac));
}
@Test
public void testTextPropertyListener() {
TextField control = new TextField("some text");
TextFieldBehavior behavior = (TextFieldBehavior) createBehavior(control);
assertNull("sanity: initial bidi", getRawBidi(behavior));
isRTLText(behavior);
assertNotNull(getRawBidi(behavior));
control.setText("dummy");
assertNull("listener working (bidi is reset)", getRawBidi(behavior));
}
@Test
public void testTreeViewBehaviorDisposeSelect() {
TreeView<String> treeView = new TreeView<>(createRoot());
WeakReference<BehaviorBase<?>> weakRef = new WeakReference<>(createBehavior(treeView));
treeView.getSelectionModel().select(1);
weakRef.get().dispose();
treeView.getSelectionModel().select(0);
assertNull("anchor must remain cleared on selecting when disposed",
treeView.getProperties().get("anchor"));
}
@Test
public void testTreeViewBehaviorSelect() {
TreeView<String> treeView = new TreeView<>(createRoot());
createBehavior(treeView);
int last = 1;
treeView.getSelectionModel().select(last);
assertEquals("anchor must be set", last, treeView.getProperties().get("anchor"));
}
@Test
public void testTreeViewBehaviorDispose() {
TreeView<String> treeView = new TreeView<>(createRoot());
WeakReference<BehaviorBase<?>> weakRef = new WeakReference<>(createBehavior(treeView));
treeView.getSelectionModel().select(1);
weakRef.get().dispose();
assertNull("anchor must be cleared after dispose", treeView.getProperties().get("anchor"));
}
private TreeItem<String> createRoot() {
TreeItem<String> root = new TreeItem<>("root");
root.setExpanded(true);
root.getChildren().addAll(new TreeItem<>("child one"), new TreeItem<>("child two"));
return root;
}
@Test
public void testListViewBehaviorDisposeSetItems() {
ListView<String> listView = new ListView<>(observableArrayList("one", "two"));
WeakReference<BehaviorBase<?>> weakRef = new WeakReference<>(createBehavior(listView));
weakRef.get().dispose();
int last = 1;
ListCellBehavior.setAnchor(listView, last, false);
listView.setItems(observableArrayList("other", "again"));
assertEquals("sanity: anchor unchanged", last, listView.getProperties().get("anchor"));
listView.getItems().remove(0);
assertEquals("anchor must not be updated on items modification when disposed",
last, listView.getProperties().get("anchor"));
}
@Test
public void testListViewBehaviorSetItems() {
ListView<String> listView = new ListView<>(observableArrayList("one", "two"));
createBehavior(listView);
int last = 1;
ListCellBehavior.setAnchor(listView, last, false);
listView.setItems(observableArrayList("other", "again"));
assertEquals("sanity: anchor unchanged", last, listView.getProperties().get("anchor"));
listView.getItems().remove(0);
assertEquals("anchor must be updated on items modification",
last -1, listView.getProperties().get("anchor"));
}
@Test
public void testListViewBehaviorDisposeRemoveItem() {
ListView<String> listView = new ListView<>(observableArrayList("one", "two"));
WeakReference<BehaviorBase<?>> weakRef = new WeakReference<>(createBehavior(listView));
weakRef.get().dispose();
int last = 1;
ListCellBehavior.setAnchor(listView, last, false);
listView.getItems().remove(0);
assertEquals("anchor must not be updated on items modification when disposed",
last,
listView.getProperties().get("anchor"));
}
@Test
public void testListViewBehaviorRemoveItem() {
ListView<String> listView = new ListView<>(observableArrayList("one", "two"));
createBehavior(listView);
int last = 1;
ListCellBehavior.setAnchor(listView, last, false);
assertEquals("behavior must set anchor on select", last, listView.getProperties().get("anchor"));
listView.getItems().remove(0);
assertEquals("anchor must be updated on items modification",
last -1, listView.getProperties().get("anchor"));
}
@Test
public void testListViewBehaviorDisposeSelect() {
ListView<String> listView = new ListView<>(observableArrayList("one", "two"));
WeakReference<BehaviorBase<?>> weakRef = new WeakReference<>(createBehavior(listView));
listView.getSelectionModel().select(1);
weakRef.get().dispose();
listView.getSelectionModel().select(0);
assertNull("anchor must remain cleared on selecting when disposed",
listView.getProperties().get("anchor"));
}
@Test
public void testListViewBehaviorSelect() {
ListView<String> listView = new ListView<>(observableArrayList("one", "two"));
createBehavior(listView);
int last = 1;
listView.getSelectionModel().select(last);
assertEquals("anchor must be set", last, listView.getProperties().get("anchor"));
}
@Test
public void testListViewBehaviorDispose() {
ListView<String> listView = new ListView<>(observableArrayList("one", "two"));
WeakReference<BehaviorBase<?>> weakRef = new WeakReference<>(createBehavior(listView));
listView.getSelectionModel().select(1);
weakRef.get().dispose();
assertNull("anchor must be cleared after dispose", listView.getProperties().get("anchor"));
}
protected void showControl(Control control) {
showControl(control, true);
}
protected void showControl(Control control, boolean focused) {
if (root == null) {
root = new VBox();
scene = new Scene(root);
stage = new Stage();
stage.setScene(scene);
}
if (!root.getChildren().contains(control)) {
root.getChildren().add(control);
}
stage.show();
if (focused) {
stage.requestFocus();
control.requestFocus();
assertTrue(control.isFocused());
assertSame(control, scene.getFocusOwner());
}
}
@After
public void cleanup() {
if (stage != null) {
stage.hide();
}
Thread.currentThread().setUncaughtExceptionHandler(null);
}
@Before
public void setup() {
Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
if (throwable instanceof RuntimeException) {
throw (RuntimeException)throwable;
} else {
Thread.currentThread().getThreadGroup().uncaughtException(thread, throwable);
}
});
}
}
