package test.javafx.scene.control;
import java.time.LocalDate;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CellShim;
import javafx.scene.control.DateCell;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import test.com.sun.javafx.scene.control.infrastructure.ControlTestUtils;
import static org.junit.Assert.*;
public class DateCellTest {
private DateCell cell;
private Class type;
private final LocalDate today = LocalDate.now();
private final LocalDate tomorrow = today.plusDays(1);
@Before public void setup() throws Exception {
cell = new DateCell();
}
@Test public void cellsShouldBeNonFocusableByDefault() {
assertFalse(cell.isFocusTraversable());
assertFalse(cell.isFocused());
}
@Test public void styleClassShouldDefaultTo_cell_and_datecell() {
ControlTestUtils.assertStyleClassContains(cell, "cell");
ControlTestUtils.assertStyleClassContains(cell, "date-cell");
}
@Test public void pseudoClassStateShouldBe_empty_ByDefault() {
ControlTestUtils.assertPseudoClassExists(cell, "empty");
ControlTestUtils.assertPseudoClassDoesNotExist(cell, "filled");
ControlTestUtils.assertPseudoClassDoesNotExist(cell, "selected");
ControlTestUtils.assertPseudoClassDoesNotExist(cell, "focused");
}
@Test public void updatingItemAffectsBothItemAndEmpty() {
CellShim.updateItem(cell, today, false);
assertEquals(today, cell.getItem());
assertFalse(cell.isEmpty());
}
@Test public void updatingItemWithEmptyTrueAndItemNotNullIsWeirdButOK() {
CellShim.updateItem(cell, today, true);
assertEquals(today, cell.getItem());
assertTrue(cell.isEmpty());
}
@Test public void updatingItemWithEmptyFalseAndNullItemIsOK() {
CellShim.updateItem(cell, null, false);
assertNull(cell.getItem());
assertFalse(cell.isEmpty());
}
@Test public void selectingANonEmptyCellIsOK() {
CellShim.updateItem(cell, today, false);
cell.updateSelected(true);
assertTrue(cell.isSelected());
}
@Test public void unSelectingANonEmptyCellIsOK() {
CellShim.updateItem(cell, today, false);
cell.updateSelected(true);
cell.updateSelected(false);
assertFalse(cell.isSelected());
}
public void selectingAnEmptyCellResultsInNoChange() {
CellShim.updateItem(cell, null, true);
cell.updateSelected(true);
assertFalse(cell.isSelected());
}
@Test public void updatingASelectedCellToBeEmptyClearsSelection() {
CellShim.updateItem(cell, today, false);
cell.updateSelected(true);
CellShim.updateItem(cell, null, true);
assertFalse(cell.isSelected());
}
@Test public void updatingItemWithEmptyTrueResultsIn_empty_pseudoClassAndNot_filled() {
CellShim.updateItem(cell, null, true);
ControlTestUtils.assertPseudoClassExists(cell, "empty");
ControlTestUtils.assertPseudoClassDoesNotExist(cell, "filled");
}
@Test public void updatingItemWithEmptyFalseResultsIn_filled_pseudoClassAndNot_empty() {
CellShim.updateItem(cell, null, false);
ControlTestUtils.assertPseudoClassExists(cell, "filled");
ControlTestUtils.assertPseudoClassDoesNotExist(cell, "empty");
}
@Test public void updatingSelectedToTrueResultsIn_selected_pseudoClass() {
CellShim.updateItem(cell, today, false);
cell.updateSelected(true);
ControlTestUtils.assertPseudoClassExists(cell, "selected");
}
@Test public void updatingSelectedToFalseResultsInNo_selected_pseudoClass() {
CellShim.updateItem(cell, today, false);
cell.updateSelected(true);
cell.updateSelected(false);
ControlTestUtils.assertPseudoClassDoesNotExist(cell, "selected");
}
@Test public void editableIsTrueByDefault() {
assertTrue(cell.isEditable());
assertTrue(cell.editableProperty().get());
}
@Test public void editableCanBeSet() {
cell.setEditable(false);
assertFalse(cell.isEditable());
}
@Test public void editableSetToNonDefaultValueIsReflectedInModel() {
cell.setEditable(false);
assertFalse(cell.editableProperty().get());
}
@Test public void editableCanBeCleared() {
cell.setEditable(false);
cell.setEditable(true);
assertTrue(cell.isEditable());
}
@Test public void editableCanBeBound() {
BooleanProperty other = new SimpleBooleanProperty(false);
cell.editableProperty().bind(other);
assertFalse(cell.isEditable());
other.set(true);
assertTrue(cell.isEditable());
}
@Test public void cannotSpecifyEditableViaCSS() {
cell.setStyle("-fx-editable: false;");
cell.applyCss();
assertTrue(cell.isEditable());
cell.setEditable(false);
assertFalse(cell.isEditable());
cell.setStyle("-fx-editable: true;");
cell.applyCss();
assertFalse(cell.isEditable());
}
public void editingAnEmptyCellResultsInNoChange() {
cell.startEdit();
assertFalse(cell.isEditing());
}
public void editingAnEmptyCellResultsInNoChange2() {
CellShim.updateItem(cell, null, false);
CellShim.updateItem(cell, null, true);
cell.startEdit();
assertFalse(cell.isEditing());
}
@Test public void updatingACellBeingEditedDoesNotResultInACancelOfEdit() {
CellShim.updateItem(cell, today, false);
cell.startEdit();
CellShim.updateItem(cell, tomorrow, false);
assertTrue(cell.isEditing());
}
@Test public void updatingACellBeingEditedResultsInFirstACancelOfEdit2() {
CellShim.updateItem(cell, today, false);
cell.startEdit();
CellShim.updateItem(cell, null, true);
assertTrue(cell.isEditing());
}
@Test public void startEditWhenEditableIsTrue() {
CellShim.updateItem(cell, today, false);
cell.startEdit();
assertTrue(cell.isEditing());
}
@Test public void startEditWhenEditableIsFalse() {
CellShim.updateItem(cell, today, false);
cell.setEditable(false);
cell.startEdit();
assertFalse(cell.isEditing());
}
@Test public void startEditWhileAlreadyEditingIsIgnored() {
CellShim.updateItem(cell, today, false);
cell.startEdit();
cell.startEdit();
assertTrue(cell.isEditing());
}
@Test public void cancelEditWhenEditableIsTrue() {
CellShim.updateItem(cell, today, false);
cell.startEdit();
cell.cancelEdit();
assertFalse(cell.isEditing());
}
@Test public void cancelEditWhenEditableIsFalse() {
CellShim.updateItem(cell, today, false);
cell.setEditable(false);
cell.startEdit();
cell.cancelEdit();
assertFalse(cell.isEditing());
}
@Test public void commitEditWhenEditableIsTrue() {
CellShim.updateItem(cell, today, false);
cell.startEdit();
cell.commitEdit(tomorrow);
assertFalse(cell.isEditing());
}
@Test public void commitEditWhenEditableIsFalse() {
CellShim.updateItem(cell, today, false);
cell.setEditable(false);
cell.startEdit();
cell.commitEdit(tomorrow);
assertFalse(cell.isEditing());
}
@Test public void getBeanIsCorrectForItemProperty() {
assertSame(cell, cell.itemProperty().getBean());
}
@Test public void getNameIsCorrectForItemProperty() {
assertEquals("item", cell.itemProperty().getName());
}
@Test public void getBeanIsCorrectForEmptyProperty() {
assertSame(cell, cell.emptyProperty().getBean());
}
@Test public void getNameIsCorrectForEmptyProperty() {
assertEquals("empty", cell.emptyProperty().getName());
}
@Test public void getBeanIsCorrectForSelectedProperty() {
assertSame(cell, cell.selectedProperty().getBean());
}
@Test public void getNameIsCorrectForSelectedProperty() {
assertEquals("selected", cell.selectedProperty().getName());
}
@Test public void getBeanIsCorrectForEditingProperty() {
assertSame(cell, cell.editingProperty().getBean());
}
@Test public void getNameIsCorrectForEditingProperty() {
assertEquals("editing", cell.editingProperty().getName());
}
@Test public void getBeanIsCorrectForEditableProperty() {
assertSame(cell, cell.editableProperty().getBean());
}
@Test public void getNameIsCorrectForEditableProperty() {
assertEquals("editable", cell.editableProperty().getName());
}
@Test public void loseFocusWhileEditing() {
Button other = new Button();
Group root = new Group(other, cell);
Scene scene = new Scene(root);
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
stage.requestFocus();
Toolkit.getToolkit().firePulse();
CellShim.updateItem(cell, today, false);
cell.startEdit();
cell.requestFocus();
Toolkit.getToolkit().firePulse();
assertTrue(cell.isEditing());
other.requestFocus();
Toolkit.getToolkit().firePulse();
assertFalse(cell.isEditing());
stage.hide();
}
}
