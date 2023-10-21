package test.javafx.scene.control;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import test.com.sun.javafx.scene.control.infrastructure.MouseEventFirer;
import com.sun.javafx.tk.Toolkit;
import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.FocusModel;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.com.sun.javafx.scene.control.behavior.TreeViewAnchorRetriever;
import test.com.sun.javafx.scene.control.infrastructure.KeyModifier;
import test.com.sun.javafx.scene.control.infrastructure.StageLoader;
import test.com.sun.javafx.scene.control.infrastructure.VirtualFlowTestUtils;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
public class TreeViewMouseInputTest {
private TreeView<String> treeView;
private MultipleSelectionModel<TreeItem<String>> sm;
private FocusModel<TreeItem<String>> fm;
private TreeItem<String> root;
private TreeItem<String> child1;
private TreeItem<String> child2;
private TreeItem<String> child3;
private TreeItem<String> subchild1;
private TreeItem<String> subchild2;
private TreeItem<String> subchild3;
private TreeItem<String> child4;
private TreeItem<String> child5;
private TreeItem<String> child6;
private TreeItem<String> child7;
private TreeItem<String> child8;
private TreeItem<String> child9;
private TreeItem<String> child10;
@Before public void setup() {
root = new TreeItem<String>("Root");
child1 = new TreeItem<String>("Child 1");
child2 = new TreeItem<String>("Child 2");
child3 = new TreeItem<String>("Child 3");
subchild1 = new TreeItem<String>("Subchild 1");
subchild2 = new TreeItem<String>("Subchild 2");
subchild3 = new TreeItem<String>("Subchild 3");
child4 = new TreeItem<String>("Child 4");
child5 = new TreeItem<String>("Child 5");
child6 = new TreeItem<String>("Child 6");
child7 = new TreeItem<String>("Child 7");
child8 = new TreeItem<String>("Child 8");
child9 = new TreeItem<String>("Child 9");
child10 = new TreeItem<String>("Child 10");
root.getChildren().clear();
root.setExpanded(true);
root.getChildren().setAll(child1, child2, child3, child4, child5, child6, child7, child8, child9, child10 );
child1.getChildren().clear();
child1.setExpanded(false);
child2.getChildren().clear();
child2.setExpanded(false);
child3.getChildren().clear();
child3.setExpanded(true);
child3.getChildren().setAll(subchild1, subchild2, subchild3);
child4.getChildren().clear();
child4.setExpanded(false);
child5.getChildren().clear();
child5.setExpanded(false);
child6.getChildren().clear();
child6.setExpanded(false);
child7.getChildren().clear();
child7.setExpanded(false);
child8.getChildren().clear();
child8.setExpanded(false);
child9.getChildren().clear();
child9.setExpanded(false);
child10.getChildren().clear();
child10.setExpanded(false);
treeView = new TreeView<String>();
treeView.setRoot(root);
sm = treeView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
fm = treeView.getFocusModel();
}
@After public void tearDown() {
treeView.getSkin().dispose();
}
private String debug() {
StringBuilder sb = new StringBuilder("Selected Indices: [");
List<Integer> indices = sm.getSelectedIndices();
for (Integer index : indices) {
sb.append(index);
sb.append(", ");
}
sb.append("] \nFocus: " + fm.getFocusedIndex());
sb.append(" \nAnchor: " + getAnchor());
return sb.toString();
}
private boolean isSelected(int... indices) {
for (int index : indices) {
if (! sm.isSelected(index)) return false;
}
return true;
}
private boolean isNotSelected(int... indices) {
for (int index : indices) {
if (sm.isSelected(index)) return false;
}
return true;
}
private int getAnchor() {
return TreeViewAnchorRetriever.getAnchor(treeView);
}
private boolean isAnchor(int index) {
return getAnchor() == index;
}
private int getItemCount() {
return root.getChildren().size() + child3.getChildren().size();
}
@Test public void test_rt29833_mouse_select_upwards() {
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(9);
VirtualFlowTestUtils.clickOnRow(treeView, 7, KeyModifier.SHIFT);
assertTrue(debug(), isSelected(7,8,9));
VirtualFlowTestUtils.clickOnRow(treeView, 5, KeyModifier.SHIFT);
assertTrue(debug(),isSelected(5,6,7,8,9));
}
@Test public void test_rt29833_mouse_select_downwards() {
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(5);
VirtualFlowTestUtils.clickOnRow(treeView, 7, KeyModifier.SHIFT);
assertTrue(debug(), isSelected(5,6,7));
VirtualFlowTestUtils.clickOnRow(treeView, 9, KeyModifier.SHIFT);
assertTrue(debug(),isSelected(5,6,7,8,9));
}
private int rt30394_count = 0;
@Test public void test_rt30394() {
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearSelection();
final FocusModel fm = treeView.getFocusModel();
fm.focus(-1);
fm.focusedIndexProperty().addListener((observable, oldValue, newValue) -> {
rt30394_count++;
assertEquals(0, fm.getFocusedIndex());
});
assertEquals(0,rt30394_count);
assertFalse(fm.isFocused(0));
VirtualFlowTestUtils.clickOnRow(treeView, 0, KeyModifier.SHIFT);
assertEquals(1, rt30394_count);
assertTrue(fm.isFocused(0));
}
@Test public void test_rt32119() {
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearSelection();
VirtualFlowTestUtils.clickOnRow(treeView, 2);
VirtualFlowTestUtils.clickOnRow(treeView, 4, KeyModifier.SHIFT);
assertFalse(sm.isSelected(1));
assertTrue(sm.isSelected(2));
assertTrue(sm.isSelected(3));
assertTrue(sm.isSelected(4));
assertFalse(sm.isSelected(5));
VirtualFlowTestUtils.clickOnRow(treeView, 2, KeyModifier.SHIFT);
assertFalse(sm.isSelected(1));
assertTrue(sm.isSelected(2));
assertFalse(sm.isSelected(3));
assertFalse(sm.isSelected(4));
assertFalse(sm.isSelected(5));
}
@Test public void test_rt21444_up() {
final int items = 8;
root.getChildren().clear();
for (int i = 0; i < items; i++) {
root.getChildren().add(new TreeItem<>("Row " + i));
}
final int selectRow = 3;
treeView.setShowRoot(false);
final MultipleSelectionModel<TreeItem<String>> sm = treeView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(selectRow);
assertEquals(selectRow, sm.getSelectedIndex());
assertEquals("Row 3", sm.getSelectedItem().getValue());
VirtualFlowTestUtils.clickOnRow(treeView, selectRow - 1, KeyModifier.SHIFT);
assertEquals(2, sm.getSelectedItems().size());
assertEquals("Row 2", sm.getSelectedItem().getValue());
assertEquals("Row 2", sm.getSelectedItems().get(0).getValue());
}
@Test public void test_rt21444_down() {
final int items = 8;
root.getChildren().clear();
for (int i = 0; i < items; i++) {
root.getChildren().add(new TreeItem<>("Row " + i));
}
final int selectRow = 3;
treeView.setShowRoot(false);
final MultipleSelectionModel<TreeItem<String>> sm = treeView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(selectRow);
assertEquals(selectRow, sm.getSelectedIndex());
assertEquals("Row 3", sm.getSelectedItem().getValue());
VirtualFlowTestUtils.clickOnRow(treeView, selectRow + 1, KeyModifier.SHIFT);
assertEquals(2, sm.getSelectedItems().size());
assertEquals("Row 4", sm.getSelectedItem().getValue());
assertEquals("Row 4", sm.getSelectedItems().get(1).getValue());
}
@Test public void test_rt32560() {
final int items = 8;
root.getChildren().clear();
for (int i = 0; i < items; i++) {
root.getChildren().add(new TreeItem<>("Row " + i));
}
final MultipleSelectionModel sm = treeView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(0);
assertEquals(0, sm.getSelectedIndex());
assertEquals(root, sm.getSelectedItem());
assertEquals(0, fm.getFocusedIndex());
VirtualFlowTestUtils.clickOnRow(treeView, 5, KeyModifier.SHIFT);
assertEquals(5, sm.getSelectedIndex());
assertEquals(5, fm.getFocusedIndex());
assertEquals(6, sm.getSelectedItems().size());
VirtualFlowTestUtils.clickOnRow(treeView, 0, KeyModifier.SHIFT);
assertEquals(0, sm.getSelectedIndex());
assertEquals(0, fm.getFocusedIndex());
assertEquals(1, sm.getSelectedItems().size());
}
@Test public void test_rt_32963() {
final int items = 8;
root.getChildren().clear();
root.setExpanded(true);
for (int i = 0; i < items; i++) {
root.getChildren().add(new TreeItem<>("Row " + i));
}
treeView.setRoot(root);
treeView.setShowRoot(true);
final MultipleSelectionModel sm = treeView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(0);
assertEquals(9, treeView.getExpandedItemCount());
assertEquals(0, sm.getSelectedIndex());
assertEquals(0, fm.getFocusedIndex());
assertEquals(root, sm.getSelectedItem());
assertEquals(1, sm.getSelectedItems().size());
VirtualFlowTestUtils.clickOnRow(treeView, 5, KeyModifier.SHIFT);
assertEquals("Actual selected index: " + sm.getSelectedIndex(), 5, sm.getSelectedIndex());
assertEquals("Actual focused index: " + fm.getFocusedIndex(), 5, fm.getFocusedIndex());
assertTrue("Selected indices: " + sm.getSelectedIndices(), sm.getSelectedIndices().contains(0));
assertTrue("Selected items: " + sm.getSelectedItems(), sm.getSelectedItems().contains(root));
assertEquals(6, sm.getSelectedItems().size());
}
private int rt_30626_count = 0;
@Test public void test_rt_30626() {
final int items = 8;
root.getChildren().clear();
root.setExpanded(true);
for (int i = 0; i < items; i++) {
root.getChildren().add(new TreeItem<>("Row " + i));
}
treeView.setRoot(root);
treeView.setShowRoot(true);
final MultipleSelectionModel sm = treeView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(0);
treeView.getSelectionModel().getSelectedItems().addListener((ListChangeListener) c -> {
while (c.next()) {
rt_30626_count++;
}
});
assertEquals(0, rt_30626_count);
VirtualFlowTestUtils.clickOnRow(treeView, 1);
assertEquals(1, rt_30626_count);
VirtualFlowTestUtils.clickOnRow(treeView, 1);
assertEquals(1, rt_30626_count);
}
@Test public void test_rt_34649() {
final int items = 8;
root.getChildren().clear();
root.setExpanded(true);
for (int i = 0; i < items; i++) {
root.getChildren().add(new TreeItem<>("Row " + i));
}
treeView.setRoot(root);
final MultipleSelectionModel sm = treeView.getSelectionModel();
final FocusModel fm = treeView.getFocusModel();
sm.setSelectionMode(SelectionMode.SINGLE);
assertFalse(sm.isSelected(4));
assertFalse(fm.isFocused(4));
VirtualFlowTestUtils.clickOnRow(treeView, 4, KeyModifier.getShortcutKey());
assertTrue(sm.isSelected(4));
assertTrue(fm.isFocused(4));
VirtualFlowTestUtils.clickOnRow(treeView, 4, KeyModifier.getShortcutKey());
assertFalse(sm.isSelected(4));
assertTrue(fm.isFocused(4));
}
@Test public void test_rt_36509() {
final int items = 8;
root.getChildren().clear();
root.setExpanded(false);
for (int i = 0; i < items; i++) {
root.getChildren().add(new TreeItem<>("Row " + i));
}
treeView.setRoot(root);
assertFalse(root.isExpanded());
VirtualFlowTestUtils.clickOnRow(treeView, 0, 2);
assertTrue(root.isExpanded());
VirtualFlowTestUtils.clickOnRow(treeView, 0, 2);
assertFalse(root.isExpanded());
VirtualFlowTestUtils.clickOnRow(treeView, 0, 2);
assertTrue(root.isExpanded());
}
@Test public void test_rt_37069() {
final int items = 8;
root.getChildren().clear();
root.setExpanded(false);
for (int i = 0; i < items; i++) {
root.getChildren().add(new TreeItem<>("Row " + i));
}
treeView.setRoot(root);
treeView.setFocusTraversable(false);
Button btn = new Button("Button");
VBox vbox = new VBox(btn, treeView);
StageLoader sl = new StageLoader(vbox);
sl.getStage().requestFocus();
btn.requestFocus();
Toolkit.getToolkit().firePulse();
Scene scene = sl.getStage().getScene();
assertTrue(btn.isFocused());
assertFalse(treeView.isFocused());
ScrollBar vbar = VirtualFlowTestUtils.getVirtualFlowVerticalScrollbar(treeView);
MouseEventFirer mouse = new MouseEventFirer(vbar);
mouse.fireMousePressAndRelease();
assertTrue(btn.isFocused());
assertFalse(treeView.isFocused());
sl.dispose();
}
@Test public void test_jdk_8147823() {
final int items = 3;
root.getChildren().clear();
root.setExpanded(false);
for (int i = 0; i < items; i++) {
root.getChildren().add(new TreeItem<>("Row " + i));
}
treeView.setRoot(root);
treeView.setShowRoot(false);
treeView.setFocusTraversable(false);
sm.setSelectionMode(SelectionMode.MULTIPLE);
AtomicInteger hitCount = new AtomicInteger();
sm.getSelectedItems().addListener((ListChangeListener<TreeItem<String>>) c -> {
hitCount.incrementAndGet();
List<TreeItem<String>> copy = new ArrayList<>(sm.getSelectedItems());
assertFalse(copy.contains(null));
});
VirtualFlowTestUtils.clickOnRow(treeView, 0, KeyModifier.getShortcutKey());
assertEquals(1, hitCount.get());
VirtualFlowTestUtils.clickOnRow(treeView, 1, KeyModifier.getShortcutKey());
assertEquals(2, hitCount.get());
VirtualFlowTestUtils.clickOnRow(treeView, 2, KeyModifier.getShortcutKey());
assertEquals(3, hitCount.get());
assertEquals(3, sm.getSelectedIndices().size());
isSelected(0,1,2);
VirtualFlowTestUtils.clickOnRow(treeView, 1, KeyModifier.getShortcutKey());
assertEquals(4, hitCount.get());
VirtualFlowTestUtils.clickOnRow(treeView, 0, KeyModifier.getShortcutKey());
assertEquals(5, hitCount.get());
assertEquals(1, sm.getSelectedIndices().size());
isSelected(2);
isNotSelected(-1, 0, 1);
assertNotNull(sm.getSelectedItems().get(0));
assertNotNull(sm.getSelectedItem());
}
}
