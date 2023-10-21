package test.javafx.scene.control.skin;
import static org.junit.Assert.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import org.junit.Before;
import org.junit.Test;
public class ComboBoxListViewSkinTest {
private ComboBox<String> comboBox;
private SelectionModel<String> sm;
private ListView<String> listView;
private SelectionModel<String> listSm;
private ComboBoxListViewSkin<String> skin;
@Before public void setup() {
comboBox = new ComboBox();
skin = new ComboBoxListViewSkin(comboBox);
comboBox.setSkin(skin);
sm = comboBox.getSelectionModel();
listView = (ListView)skin.getPopupContent();
listSm = listView.getSelectionModel();
}
@Test public void testListViewSelectionEqualsComboBox() {
comboBox.getItems().addAll("Apple", "Orange", "Banana");
sm.select("Orange");
assertEquals("Orange", comboBox.getValue());
assertEquals("Orange", sm.getSelectedItem());
assertEquals("Orange", listSm.getSelectedItem());
}
@Test public void test_rt19431_selectionRemainsWhileEditableChanges_true() {
comboBox.getItems().addAll("Apple", "Orange", "Banana");
sm.select("Orange");
comboBox.setEditable(true);
assertEquals("Orange", comboBox.getValue());
assertEquals("Orange", sm.getSelectedItem());
assertEquals("Orange", listSm.getSelectedItem());
}
@Test public void test_rt19431_selectionRemainsWhileEditableChanges_false() {
comboBox.setEditable(true);
comboBox.getItems().addAll("Apple", "Orange", "Banana");
sm.select("Orange");
comboBox.setEditable(false);
assertEquals("Orange", comboBox.getValue());
assertEquals("Orange", sm.getSelectedItem());
assertEquals("Orange", listSm.getSelectedItem());
}
@Test public void test_rt19431_selectionRemainsWhileEditableChanges_true_notInList() {
comboBox.getItems().addAll("Apple", "Orange", "Banana");
sm.select("Kiwifruit");
comboBox.setEditable(true);
assertEquals("Kiwifruit", comboBox.getValue());
assertEquals("Kiwifruit", sm.getSelectedItem());
assertNull(listSm.getSelectedItem());
}
@Test public void test_rt19431_selectionRemainsWhileEditableChanges_false_notInList() {
comboBox.setEditable(true);
comboBox.getItems().addAll("Apple", "Orange", "Banana");
sm.select("Kiwifruit");
comboBox.setEditable(false);
assertEquals("Kiwifruit", comboBox.getValue());
assertEquals("Kiwifruit", sm.getSelectedItem());
assertNull(listSm.getSelectedItem());
}
}
