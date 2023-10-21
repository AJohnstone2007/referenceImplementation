package test.javafx.scene.control;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceBoxShim;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.skin.ChoiceBoxSkin;
import javafx.scene.control.skin.ChoiceBoxSkinNodesShim;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class ChoiceBoxSelectionTest {
private Scene scene;
private Stage stage;
private Pane root;
private ChoiceBox<String> box;
private String uncontained;
@Test
public void testBaseToggleInitialSelectOpenPopup() {
SingleSelectionModel<String> sm = box.getSelectionModel();
int selectedIndex = box.getItems().size() - 1;
sm.select(selectedIndex);
showChoiceBox();
box.show();
assertToggleSelected(selectedIndex);
}
@Test
public void testBaseToggleInitialSelect() {
SingleSelectionModel<String> sm = box.getSelectionModel();
int selectedIndex = box.getItems().size() - 1;
sm.select(selectedIndex);
showChoiceBox();
assertToggleSelected(selectedIndex);
}
@SuppressWarnings({ "rawtypes", "unchecked" })
@Test
public void testBaseToggleSeparator() {
ChoiceBox box = new ChoiceBox(FXCollections.observableArrayList(
"Apple", "Banana", new Separator(), "Orange"));
int separatorIndex = 2;
showControl(box);
SingleSelectionModel<?> sm = box.getSelectionModel();
int selectedIndex = 1;
sm.select(selectedIndex);
sm.select(separatorIndex);
assertToggleSelected(box, -1);
}
@Test
public void testNullSelectionModel() {
box.setSelectionModel(null);
showChoiceBox();
}
@Test
public void testBaseToggleClearSelection() {
SingleSelectionModel<String> sm = box.getSelectionModel();
sm.select(2);
showChoiceBox();
sm.clearSelection();
assertToggleSelected(-1);
}
@Test
public void testBaseToggleMinusIndex() {
SingleSelectionModel<String> sm = box.getSelectionModel();
sm.select(2);
showChoiceBox();
sm.select(-1);
assertToggleSelected(-1);
}
@Test
public void testBaseToggleNullItem() {
SingleSelectionModel<String> sm = box.getSelectionModel();
sm.select(2);
showChoiceBox();
sm.select(null);
assertToggleSelected(-1);
}
@Test
public void testBaseToggleNullValue() {
SingleSelectionModel<String> sm = box.getSelectionModel();
sm.select(2);
showChoiceBox();
box.setValue(null);
assertToggleSelected(-1);
}
@Test
public void testBaseToggleChangeIndex() {
SingleSelectionModel<String> sm = box.getSelectionModel();
sm.select(2);
showChoiceBox();
int other = 1;
sm.select(other);
assertToggleSelected(other);
}
@Test
public void testBaseToggleChangeItem() {
SingleSelectionModel<String> sm = box.getSelectionModel();
sm.select(2);
showChoiceBox();
int other = 1;
String otherItem = box.getItems().get(other);
sm.select(otherItem);
assertToggleSelected(other);
}
@Test
public void testBaseToggleChangeValue() {
SingleSelectionModel<String> sm = box.getSelectionModel();
sm.select(2);
showChoiceBox();
int other = 1;
String otherItem = box.getItems().get(other);
box.setValue(otherItem);
assertToggleSelected(other);
}
@Test
public void testBaseToggleSetValue() {
showChoiceBox();
int selectedIndex = box.getItems().size() - 1;
box.setValue(box.getItems().get(selectedIndex));
assertToggleSelected(selectedIndex);
}
@Test
public void testBaseToggleSelectItem() {
showChoiceBox();
SingleSelectionModel<String> sm = box.getSelectionModel();
int selectedIndex = box.getItems().size() - 1;
sm.select(box.getItems().get(selectedIndex));
assertToggleSelected(selectedIndex);
}
@Test
public void testBaseToggleSelectIndex() {
showChoiceBox();
SingleSelectionModel<String> sm = box.getSelectionModel();
int selectedIndex = box.getItems().size() - 1;
sm.select(selectedIndex);
assertToggleSelected(selectedIndex);
}
protected void assertToggleSelected(ChoiceBox<?> box, int selectedIndex) {
boolean isSelected = selectedIndex >= 0;
ContextMenu popup = ChoiceBoxSkinNodesShim.getChoiceBoxPopup((ChoiceBoxSkin<?>) box.getSkin());
for (int i = 0; i < popup.getItems().size(); i++) {
boolean shouldBeSelected = isSelected ? selectedIndex == i : false;
MenuItem item = popup.getItems().get(i);
if (item instanceof RadioMenuItem) {
RadioMenuItem selectedToggle = (RadioMenuItem) popup.getItems().get(i);
assertEquals("toggle " + selectedToggle.getText() + " at index: " + i + " must be selected: " + shouldBeSelected,
shouldBeSelected,
selectedToggle.isSelected());
}
}
}
protected void assertToggleSelected(int selectedIndex) {
assertToggleSelected(box, selectedIndex);
}
@Test
public void testSyncedToggleUncontainedValue() {
SingleSelectionModel<String> sm = box.getSelectionModel();
sm.select(2);
showChoiceBox();
box.setValue(uncontained);
assertToggleSelected(-1);
}
@Test
public void testSyncedSelectedIndexUncontained() {
box.setValue(box.getItems().get(1));
box.setValue(uncontained);
assertEquals("selectedIndex for uncontained value ", -1, box.getSelectionModel().getSelectedIndex());
}
@Test
public void testSyncedSelectedOnPreselectedThenUncontained() {
box.setValue(box.getItems().get(1));
box.setValue(uncontained);
box.getSelectionModel().clearSelection();
assertEquals("uncontained value must be unchanged after clearSelection", uncontained, box.getValue());
}
@Test
public void testSyncedClearSelectionUncontained() {
box.setValue(uncontained);
box.getSelectionModel().clearSelection();
assertEquals(uncontained, box.getValue());
}
@Test
public void testSyncedContainedValueReplaceSMEmpty() {
box.setValue(box.getItems().get(1));
SingleSelectionModel<String> replaceSM = ChoiceBoxShim.get_ChoiceBoxSelectionModel(box);
assertNull(replaceSM.getSelectedItem());
box.setSelectionModel(replaceSM);
assertEquals(replaceSM.getSelectedItem(), box.getValue());
}
@Test
public void testSyncedUncontainedValueReplaceSMEmpty() {
box.setValue(uncontained);
SingleSelectionModel<String> replaceSM = ChoiceBoxShim.get_ChoiceBoxSelectionModel(box);
assertNull(replaceSM.getSelectedItem());
box.setSelectionModel(replaceSM);
assertEquals(replaceSM.getSelectedItem(), box.getValue());
}
@Test
public void testSyncedBoundValueReplaceSMEmpty() {
StringProperty valueSource = new SimpleStringProperty("stickyValue");
box.valueProperty().bind(valueSource);
SingleSelectionModel<String> replaceSM = ChoiceBoxShim.get_ChoiceBoxSelectionModel(box);
assertNull(replaceSM.getSelectedItem());
box.setSelectionModel(replaceSM);
assertEquals(valueSource.get(), box.getValue());
}
@Test
public void testSetupState() {
assertNotNull(box);
showChoiceBox();
List<Node> expected = List.of(box);
assertEquals(expected, root.getChildren());
}
protected void showChoiceBox() {
showControl(box);
}
protected void showControl(Control box) {
if (!root.getChildren().contains(box)) {
root.getChildren().add(box);
}
stage.show();
stage.requestFocus();
box.requestFocus();
assertTrue(box.isFocused());
assertSame(box, scene.getFocusOwner());
}
@After
public void cleanup() {
stage.hide();
}
@Before
public void setup() {
uncontained = "uncontained";
root = new VBox();
scene = new Scene(root);
stage = new Stage();
stage.setScene(scene);
box = new ChoiceBox<>(FXCollections.observableArrayList("Apple", "Banana", "Orange"));
root.getChildren().addAll(box);
}
}
