package test.javafx.scene.control;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.SceneHelper;
import com.sun.javafx.scene.control.behavior.ListCellBehavior;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import java.util.List;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.util.Utils;
import test.com.sun.javafx.scene.control.behavior.ListViewAnchorRetriever;
import test.com.sun.javafx.scene.control.infrastructure.ControlTestUtils;
import test.com.sun.javafx.scene.control.infrastructure.KeyEventFirer;
import test.com.sun.javafx.scene.control.infrastructure.KeyModifier;
import test.com.sun.javafx.scene.control.infrastructure.StageLoader;
import test.com.sun.javafx.scene.control.infrastructure.VirtualFlowTestUtils;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.control.FocusModel;
import javafx.scene.control.FocusModelShim;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
public class ListViewKeyInputTest {
private ListView<String> listView;
private MultipleSelectionModel<String> sm;
private FocusModel<String> fm;
private KeyEventFirer keyboard;
private StageLoader stageLoader;
@Before public void setup() {
listView = new ListView<>();
sm = listView.getSelectionModel();
fm = listView.getFocusModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
keyboard = new KeyEventFirer(listView);
stageLoader = new StageLoader(listView);
listView.getItems().setAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
}
@After public void tearDown() {
listView.getSkin().dispose();
stageLoader.dispose();
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
if (! sm.isSelected(index)) {
System.out.println("Index " + index + " is not selected, but it is expected to be");
return false;
}
}
return true;
}
private boolean isNotSelected(int... indices) {
for (int index : indices) {
if (sm.isSelected(index)) {
System.out.println("Index " + index + " is selected, but it is not expected to be");
return false;
}
}
return true;
}
private int getAnchor() {
return ListViewAnchorRetriever.getAnchor(listView);
}
private boolean isAnchor(int index) {
return getAnchor() == index;
}
@Test public void testInitialState() {
assertTrue(sm.getSelectedIndices().isEmpty());
assertTrue(sm.getSelectedItems().isEmpty());
}
@Test public void testDownArrowChangesSelection() {
sm.clearAndSelect(0);
keyboard.doDownArrowPress();
assertFalse(sm.isSelected(0));
assertTrue(sm.isSelected(1));
}
@Test public void testDownArrowDoesNotChangeSelectionWhenAtLastIndex() {
int endIndex = listView.getItems().size() - 1;
sm.clearAndSelect(endIndex);
assertTrue(sm.isSelected(endIndex));
keyboard.doDownArrowPress();
assertTrue(sm.isSelected(endIndex));
}
@Test public void testUpArrowDoesNotChangeSelectionWhenAt0Index() {
sm.clearAndSelect(0);
keyboard.doUpArrowPress();
assertTrue(sm.isSelected(0));
assertEquals(1, sm.getSelectedIndices().size());
assertEquals(1, sm.getSelectedItems().size());
}
@Test public void testUpArrowChangesSelection() {
sm.clearAndSelect(1);
keyboard.doUpArrowPress();
assertFalse(sm.isSelected(1));
assertTrue(sm.isSelected(0));
}
@Test public void testLeftArrowDoesNotChangeState() {
keyboard.doLeftArrowPress();
testInitialState();
}
@Test public void testRightArrowDoesNotChangeState() {
keyboard.doRightArrowPress();
testInitialState();
}
@Test public void testCtrlDownMovesFocusButLeavesSelectionAlone() {
sm.clearAndSelect(0);
assertTrue(fm.isFocused(0));
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
assertTrue(fm.isFocused(1));
assertTrue(sm.isSelected(0));
assertFalse(sm.isSelected(1));
}
@Test public void testCtrlUpDoesNotMoveFocus() {
sm.clearAndSelect(0);
assertTrue(fm.isFocused(0));
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
assertTrue(fm.isFocused(0));
assertTrue(sm.isSelected(0));
}
@Test public void testCtrlLeftDoesNotMoveFocus() {
sm.clearAndSelect(0);
assertTrue(fm.isFocused(0));
keyboard.doLeftArrowPress(KeyModifier.getShortcutKey());
assertTrue(fm.isFocused(0));
assertTrue(sm.isSelected(0));
}
@Test public void testCtrlRightDoesNotMoveFocus() {
sm.clearAndSelect(0);
assertTrue(fm.isFocused(0));
keyboard.doRightArrowPress(KeyModifier.getShortcutKey());
assertTrue(debug(), fm.isFocused(0));
assertTrue(sm.isSelected(0));
}
@Test public void testCtrlUpMovesFocus() {
sm.clearAndSelect(1);
assertTrue(fm.isFocused(1));
assertTrue(sm.isSelected(1));
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
assertTrue(fm.isFocused(0));
}
@Test public void testCtrlDownDoesNotMoveFocusWhenAtLastIndex() {
int endIndex = listView.getItems().size() - 1;
sm.clearAndSelect(endIndex);
assertTrue(fm.isFocused(endIndex));
assertTrue(sm.isSelected(endIndex));
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
assertTrue(fm.isFocused(endIndex));
assertTrue(sm.isSelected(endIndex));
}
@Test public void testCtrlDownArrowWithSpaceChangesAnchor() {
sm.clearAndSelect(0);
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
assertTrue(isSelected(0, 2));
assertTrue(isNotSelected(1));
assertTrue(isAnchor(2));
}
@Test public void testCtrlUpArrowWithSpaceChangesAnchor() {
sm.clearAndSelect(2);
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
assertTrue(isSelected(0, 2));
assertTrue(isNotSelected(1));
assertTrue(isAnchor(0));
}
@Test public void testHomeKey() {
sm.clearAndSelect(3);
keyboard.doKeyPress(KeyCode.HOME);
assertTrue(debug(), isSelected(0));
assertTrue(isNotSelected(1,2,3));
}
@Test public void testEndKey() {
sm.clearAndSelect(3);
keyboard.doKeyPress(KeyCode.END);
assertTrue(isSelected(listView.getItems().size() - 1));
assertTrue(isNotSelected(1,2,3));
}
@Test public void testCtrlHome() {
sm.clearAndSelect(5);
keyboard.doKeyPress(KeyCode.HOME, KeyModifier.getShortcutKey());
assertTrue(isSelected(5));
assertTrue(fm.isFocused(0));
}
@Test public void testCtrlEnd() {
sm.clearAndSelect(5);
keyboard.doKeyPress(KeyCode.END, KeyModifier.getShortcutKey());
assertTrue(isSelected(5));
assertTrue(fm.isFocused(listView.getItems().size() - 1));
}
@Test public void testCtrlSpaceToClearSelection() {
sm.clearAndSelect(5);
assertTrue(isSelected(5));
assertTrue(fm.isFocused(5));
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
assertTrue(isNotSelected(5));
assertTrue(debug(), fm.isFocused(5));
assertTrue(isAnchor(5));
}
@Test public void testShiftDownArrowIncreasesSelection() {
sm.clearAndSelect(0);
keyboard.doDownArrowPress(KeyModifier.SHIFT);
assertTrue(sm.isSelected(0));
assertTrue(sm.isSelected(1));
}
@Test public void testShiftDownArrowDoesNotChangeSelectionWhenAtLastIndex() {
int endIndex = listView.getItems().size() - 1;
sm.clearAndSelect(endIndex);
assertTrue(sm.isSelected(endIndex));
keyboard.doDownArrowPress(KeyModifier.SHIFT);
assertTrue(sm.isSelected(endIndex));
}
@Test public void testShiftUpArrowIncreasesSelection() {
sm.clearAndSelect(1);
keyboard.doUpArrowPress(KeyModifier.SHIFT);
assertTrue(sm.isSelected(0));
assertTrue(sm.isSelected(1));
}
@Test public void testShiftUpArrowWhenAt0Index() {
sm.clearAndSelect(0);
keyboard.doUpArrowPress(KeyModifier.SHIFT);
assertTrue(sm.isSelected(0));
}
@Test public void testShiftLeftArrowWhenAt0Index() {
sm.clearAndSelect(0);
keyboard.doLeftArrowPress(KeyModifier.SHIFT);
assertTrue(sm.isSelected(0));
assertFalse(sm.isSelected(1));
}
@Test public void testShiftRightArrowWhenAt0Index() {
sm.clearAndSelect(0);
keyboard.doRightArrowPress(KeyModifier.SHIFT);
assertTrue(sm.isSelected(0));
assertFalse(sm.isSelected(1));
}
@Test public void testShiftDownTwiceThenShiftUp() {
sm.clearAndSelect(0);
keyboard.doDownArrowPress(KeyModifier.SHIFT);
keyboard.doDownArrowPress(KeyModifier.SHIFT);
keyboard.doUpArrowPress(KeyModifier.SHIFT);
assertTrue(debug(), sm.isSelected(0));
assertTrue(sm.isSelected(1));
assertFalse(sm.isSelected(2));
}
@Test public void testShiftUpTwiceThenShiftDownFrom0Index() {
sm.clearAndSelect(0);
keyboard.doUpArrowPress(KeyModifier.SHIFT);
keyboard.doUpArrowPress(KeyModifier.SHIFT);
keyboard.doDownArrowPress(KeyModifier.SHIFT);
assertTrue(sm.isSelected(0));
assertTrue(sm.isSelected(1));
assertFalse(sm.isSelected(2));
}
@Test public void testShiftLeftTwiceThenShiftRight() {
sm.clearAndSelect(0);
keyboard.doLeftArrowPress(KeyModifier.SHIFT);
keyboard.doLeftArrowPress(KeyModifier.SHIFT);
keyboard.doRightArrowPress(KeyModifier.SHIFT);
assertTrue(sm.isSelected(0));
assertFalse(sm.isSelected(1));
assertFalse(sm.isSelected(2));
}
@Test public void testShiftRightTwiceThenShiftLeft() {
sm.clearAndSelect(0);
keyboard.doRightArrowPress(KeyModifier.SHIFT);
keyboard.doRightArrowPress(KeyModifier.SHIFT);
keyboard.doLeftArrowPress(KeyModifier.SHIFT);
assertTrue(sm.isSelected(0));
assertFalse(sm.isSelected(1));
assertFalse(sm.isSelected(2));
}
@Test public void testShiftUpTwiceThenShiftDown() {
sm.clearAndSelect(2);
keyboard.doUpArrowPress(KeyModifier.SHIFT);
keyboard.doUpArrowPress(KeyModifier.SHIFT);
keyboard.doDownArrowPress(KeyModifier.SHIFT);
assertFalse(sm.isSelected(0));
assertTrue(sm.isSelected(1));
assertTrue(sm.isSelected(2));
assertFalse(sm.isSelected(3));
}
@Test public void testShiftDownTwiceThenShiftUpWhenAtLastIndex() {
int endIndex = listView.getItems().size() - 1;
sm.clearAndSelect(endIndex);
keyboard.doDownArrowPress(KeyModifier.SHIFT);
keyboard.doDownArrowPress(KeyModifier.SHIFT);
keyboard.doUpArrowPress(KeyModifier.SHIFT);
assertTrue(sm.isSelected(endIndex));
assertTrue(sm.isSelected(endIndex - 1));
assertFalse(sm.isSelected(endIndex - 2));
}
@Test public void testCtrlDownArrowWithSpaceChangesAnchor_extended() {
sm.clearAndSelect(0);
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
assertTrue(isSelected(2));
assertTrue(isNotSelected(0, 1));
assertTrue(isAnchor(0));
}
@Test public void testCtrlUpArrowWithSpaceChangesAnchor_extended() {
sm.clearAndSelect(2);
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
Utils.isMac() ? KeyModifier.CTRL : null);
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
Utils.isMac() ? KeyModifier.CTRL : null);
assertTrue(isSelected(0));
assertTrue(isNotSelected(1, 2));
assertTrue(isAnchor(2));
}
@Test public void testCtrlDownArrowWithSpaceChangesAnchor_extended2() {
sm.clearAndSelect(0);
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
Utils.isMac() ? KeyModifier.CTRL : null);
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
Utils.isMac() ? KeyModifier.CTRL : null);
assertTrue(isSelected(0, 2, 4));
assertTrue(isNotSelected(1, 3, 5));
assertTrue(isAnchor(4));
}
@Test public void testCtrlUpArrowWithSpaceChangesAnchor_extended2() {
sm.clearAndSelect(4);
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
Utils.isMac() ? KeyModifier.CTRL : null);
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
Utils.isMac() ? KeyModifier.CTRL : null);
assertTrue(isSelected(0, 2, 4));
assertTrue(isNotSelected(1, 3));
assertTrue(isAnchor(0));
}
@Test public void testCtrlDownArrowThenShiftSpaceToSelectRange() {
sm.clearAndSelect(0);
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.SHIFT);
assertTrue(isSelected(0, 1, 2));
assertTrue(isNotSelected(3));
assertTrue(isAnchor(0));
}
@Test public void testCtrlUpArrowThenShiftSpaceToSelectRange() {
sm.clearAndSelect(2);
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.SHIFT);
assertTrue(isSelected(0, 1, 2));
assertTrue(isNotSelected(3));
assertTrue(debug(), isAnchor(2));
}
@Test public void testCtrlDownArrowThenSpaceToChangeSelection() {
sm.clearAndSelect(0);
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
assertTrue(isSelected(0, 2));
assertTrue(isNotSelected(1, 3));
assertTrue(isAnchor(2));
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.SHIFT);
assertTrue(isSelected(2, 3, 4));
assertTrue(isNotSelected(0, 1));
assertTrue(isAnchor(2));
}
@Test public void testCtrlUpArrowThenSpaceToChangeSelection() {
sm.clearAndSelect(4);
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
assertTrue(isSelected(2, 4));
assertTrue(isNotSelected(0, 1, 3));
assertTrue(isAnchor(2));
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.SHIFT);
assertTrue(isSelected(0, 1, 2));
assertTrue(isNotSelected(3, 4));
assertTrue(debug(), isAnchor(2));
}
@Test public void testCtrlDownTwiceThenShiftDown() {
sm.clearAndSelect(0);
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.SHIFT);
assertTrue(isSelected(0, 1, 2, 3));
}
@Test public void testCtrlUpThriceThenShiftDown() {
sm.clearAndSelect(3);
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.SHIFT);
assertTrue(debug(), isSelected(1, 2, 3));
assertTrue(isNotSelected(0));
}
@Test public void testCtrlDownThriceThenShiftUp() {
sm.clearAndSelect(0);
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.UP, KeyModifier.SHIFT);
assertTrue(isSelected(0, 1, 2));
assertTrue(isNotSelected(3, 4));
}
@Test public void testCtrlUpTwiceThenShiftUp() {
sm.clearAndSelect(3);
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.UP, KeyModifier.SHIFT);
assertTrue(isSelected(0, 1, 2, 3));
assertTrue(isNotSelected(4));
}
@Test public void testCtrlDownTwiceThenSpace_extended() {
sm.clearAndSelect(0);
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
assertTrue(isSelected(0, 2));
assertTrue(isNotSelected(1, 3));
assertTrue(isAnchor(2));
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.SHIFT);
assertTrue(isSelected(2, 3, 4, 5));
assertTrue(isNotSelected(0, 1));
assertTrue(isAnchor(2));
}
@Test public void testCtrlUpTwiceThenSpace_extended() {
sm.clearAndSelect(5);
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
assertTrue(isSelected(3,5));
assertTrue(isNotSelected(0,1,2,4));
assertTrue(isAnchor(3));
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.SHIFT);
assertTrue(isSelected(1,2,3));
assertTrue(isNotSelected(0,4,5));
assertTrue(isAnchor(3));
}
@Test public void testCtrlDownTwiceThenSpace_extended2() {
sm.clearAndSelect(0);
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
assertTrue(isSelected(0,2));
assertTrue(isNotSelected(1,3,4));
assertTrue(isAnchor(2));
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.SHIFT);
assertTrue(isSelected(2,3,4));
assertTrue(isNotSelected(0,1,5));
assertTrue(isAnchor(2));
}
@Test public void testCtrlDownThenShiftHome() {
sm.clearAndSelect(0);
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
assertTrue(isSelected(0,2));
assertTrue(isNotSelected(1,3,4));
assertTrue(isAnchor(2));
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.HOME, KeyModifier.SHIFT);
assertTrue(isSelected(0,1,2));
assertTrue(isNotSelected(3,4));
assertTrue(debug(),isAnchor(2));
}
@Test public void testCtrlUpThenShiftEnd() {
sm.clearAndSelect(5);
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
assertTrue(isSelected(3,5));
assertTrue(isNotSelected(1,2,4));
assertTrue(isAnchor(3));
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.END, KeyModifier.SHIFT);
assertTrue(isSelected(3,4,5,6,7,8,9));
assertTrue(isNotSelected(0,1,2));
assertTrue(debug(),isAnchor(3));
}
@Test public void testCtrlUpTwiceThenSpace_extended2() {
sm.clearAndSelect(5);
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
assertTrue(isSelected(3,5));
assertTrue(isNotSelected(0,1,2,4));
assertTrue(isAnchor(3));
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.SHIFT);
assertTrue(isSelected(0,1,2,3));
assertTrue(isNotSelected(4,5));
assertTrue(isAnchor(3));
}
@Test public void testHomeKey_withSelectedItems() {
sm.clearSelection();
sm.selectRange(4, 11);
keyboard.doKeyPress(KeyCode.HOME);
assertTrue(isSelected(0));
assertTrue(isNotSelected(1,2,3,4,5,6,7,8,9,10,11));
}
@Test public void testEndKey_withSelectedItems() {
sm.clearSelection();
sm.selectRange(4, 11);
keyboard.doKeyPress(KeyCode.END);
assertTrue(isSelected(listView.getItems().size() - 1));
assertTrue(isNotSelected(1,2,3,4,5,6,7,8));
}
@Test public void testShiftHome() {
sm.clearAndSelect(3);
keyboard.doKeyPress(KeyCode.HOME, KeyModifier.SHIFT);
assertTrue(isSelected(0,1,2,3));
assertTrue(isNotSelected(4,5));
assertTrue(debug(), isAnchor(3));
}
@Test public void testShiftEnd() {
sm.clearAndSelect(3);
keyboard.doKeyPress(KeyCode.END, KeyModifier.SHIFT);
assertTrue(isSelected(3,4,5,6,7,8,9));
assertTrue(isNotSelected(0,1,2));
assertTrue(isAnchor(3));
}
@Test public void testShiftHomeThenShiftEnd() {
sm.clearAndSelect(5);
keyboard.doKeyPress(KeyCode.HOME, KeyModifier.SHIFT);
assertTrue(isSelected(0,1,2,3,4,5));
assertTrue(isAnchor(5));
keyboard.doKeyPress(KeyCode.END, KeyModifier.SHIFT);
assertTrue(isSelected(5,6,7,8,9));
assertTrue(isAnchor(5));
}
@Test public void testShiftPageUp() {
sm.clearAndSelect(0);
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
assertTrue(isSelected(0,2));
assertTrue(isAnchor(2));
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.PAGE_UP, KeyModifier.SHIFT);
assertTrue(isSelected(0,1,2));
assertTrue(isAnchor(2));
}
@Test public void testCtrlAToSelectAll() {
sm.clearAndSelect(5);
keyboard.doKeyPress(KeyCode.A, KeyModifier.getShortcutKey());
assertTrue(isSelected(0,1,2,3,4,5,6,7,8,9));
}
@Test public void test_rt18593_1() {
sm.clearAndSelect(0);
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
assertTrue(isSelected(0,2));
assertTrue(isAnchor(2));
keyboard.doDownArrowPress(KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
keyboard.doDownArrowPress(KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
assertTrue(debug(),isSelected(0,2,3,4));
assertTrue(isAnchor(2));
}
@Test public void test_rt18593_2() {
sm.clearAndSelect(5);
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
assertTrue(isSelected(3,5));
assertTrue(isAnchor(3));
keyboard.doUpArrowPress(KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
keyboard.doUpArrowPress(KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
assertTrue(isSelected(1,2,3,5));
assertTrue(isAnchor(3));
}
@Test public void test_rt18593_3() {
sm.clearAndSelect(0);
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
assertTrue(isSelected(0,2));
assertTrue(isAnchor(2));
keyboard.doDownArrowPress(KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
keyboard.doDownArrowPress(KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
assertTrue(isSelected(0,2,3,4));
assertTrue(isAnchor(2));
keyboard.doUpArrowPress(KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
keyboard.doUpArrowPress(KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
keyboard.doUpArrowPress(KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
assertTrue(isSelected(0,1,2,3,4));
assertTrue(isAnchor(2));
}
@Test public void test_rt18593_4() {
sm.clearAndSelect(5);
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
assertTrue(isSelected(3,5));
assertTrue(isAnchor(3));
keyboard.doUpArrowPress(KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
keyboard.doUpArrowPress(KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
assertTrue(isSelected(1,2,3,5));
assertTrue(isAnchor(3));
keyboard.doDownArrowPress(KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
keyboard.doDownArrowPress(KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
keyboard.doDownArrowPress(KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
assertTrue(isSelected(1,2,3,4,5));
assertTrue(isAnchor(3));
}
@Test public void test_rt18593_9() {
sm.clearAndSelect(0);
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
assertTrue(isSelected(0,2));
assertTrue(isAnchor(2));
keyboard.doKeyPress(KeyCode.END, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
assertTrue(isSelected(0,2,3,4,5,6,7,8,9));
assertTrue(isAnchor(2));
}
@Test public void test_rt18593_10() {
sm.clearAndSelect(9);
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
assertTrue(isSelected(7,9));
assertTrue(isAnchor(7));
keyboard.doKeyPress(KeyCode.HOME, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
assertTrue(isSelected(0,1,2,3,4,5,6,7,9));
assertTrue(isAnchor(7));
}
@Test public void test_rt18593_11() {
sm.clearAndSelect(5);
keyboard.doKeyPress(KeyCode.HOME, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
assertTrue(isSelected(0,1,2,3,4,5));
assertTrue(isAnchor(5));
keyboard.doKeyPress(KeyCode.END, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
assertTrue(isSelected(0,1,2,3,4,5,6,7,8,9));
assertTrue(isAnchor(5));
}
@Test public void test_rt18593_12() {
sm.clearAndSelect(0);
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
assertTrue(isSelected(0,2));
assertTrue(isAnchor(2));
keyboard.doDownArrowPress(KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
keyboard.doDownArrowPress(KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
assertTrue(isSelected(0,2,3,4));
assertTrue(isAnchor(2));
keyboard.doUpArrowPress(KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
keyboard.doUpArrowPress(KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
keyboard.doUpArrowPress(KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
assertTrue(isSelected(0,1,2,3,4));
assertTrue(isAnchor(2));
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
assertTrue(isSelected(1,2,3,4));
assertTrue(fm.isFocused(0));
assertTrue(isAnchor(0));
}
@Test public void testF2EntersEditModeAndEscapeCancelsEdit_part1() {
listView.setEditable(true);
sm.clearAndSelect(0);
assertEquals(-1, listView.getEditingIndex());
keyboard.doKeyPress(KeyCode.F2);
assertEquals(0, listView.getEditingIndex());
keyboard.doKeyPress(KeyCode.ESCAPE);
assertEquals(-1, listView.getEditingIndex());
}
@Test public void test_rt18642() {
sm.clearAndSelect(1);
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE,
KeyModifier.getShortcutKey(),
(Utils.isMac() ? KeyModifier.CTRL : null));
assertTrue(isSelected(1, 3, 5));
assertTrue(isNotSelected(0, 2, 4));
keyboard.doUpArrowPress(KeyModifier.SHIFT);
assertTrue(isSelected(4, 5));
assertTrue(isNotSelected(0, 1, 2, 3));
}
@Test public void test_rt14451_1() {
sm.clearAndSelect(5);
keyboard.doKeyPress(KeyCode.HOME, KeyModifier.SHIFT);
assertTrue(isSelected(0,1,2,3,4,5));
assertTrue(isNotSelected(6,7,8,9));
keyboard.doKeyPress(KeyCode.END, KeyModifier.SHIFT);
assertTrue(isNotSelected(0,1,2,3,4));
assertTrue(isSelected(5,6,7,8,9));
keyboard.doKeyPress(KeyCode.HOME, KeyModifier.SHIFT);
assertTrue(isSelected(0,1,2,3,4,5));
assertTrue(debug(), isNotSelected(6,7,8,9));
}
@Test public void test_rt14451_2() {
sm.clearAndSelect(5);
keyboard.doKeyPress(KeyCode.END, KeyModifier.SHIFT);
assertTrue(isNotSelected(0,1,2,3,4));
assertTrue(isSelected(5,6,7,8,9));
keyboard.doKeyPress(KeyCode.HOME, KeyModifier.SHIFT);
assertTrue(isSelected(0,1,2,3,4,5));
assertTrue(debug(), isNotSelected(6,7,8,9));
keyboard.doKeyPress(KeyCode.END, KeyModifier.SHIFT);
assertTrue(isNotSelected(0,1,2,3,4));
assertTrue(isSelected(5,6,7,8,9));
}
@Test public void test_rt26835_1() {
sm.clearAndSelect(5);
keyboard.doKeyPress(KeyCode.HOME, KeyModifier.getShortcutKey());
assertTrue(fm.isFocused(0));
}
@Test public void test_rt26835_2() {
sm.clearAndSelect(5);
keyboard.doKeyPress(KeyCode.END, KeyModifier.getShortcutKey());
assertTrue(debug(), fm.isFocused(listView.getItems().size() - 1));
}
@Test public void test_rt27175() {
sm.clearAndSelect(5);
keyboard.doKeyPress(KeyCode.HOME, KeyModifier.SHIFT, KeyModifier.getShortcutKey());
assertTrue(debug(), fm.isFocused(0));
assertTrue(isSelected(0,1,2,3,4,5));
}
@Test public void test_rt28065() {
sm.setSelectionMode(SelectionMode.MULTIPLE);
listView.getItems().setAll("Apple", "Orange", "Banana");
listView.getSelectionModel().select(0);
assertEquals(0, listView.getSelectionModel().getSelectedIndex());
assertEquals("Apple", listView.getSelectionModel().getSelectedItem());
assertEquals(0, listView.getFocusModel().getFocusedIndex());
assertEquals("Apple", listView.getFocusModel().getFocusedItem());
keyboard.doKeyPress(KeyCode.A, KeyModifier.getShortcutKey());
assertEquals(0, listView.getSelectionModel().getSelectedIndex());
assertEquals("Apple", listView.getSelectionModel().getSelectedItem());
assertEquals(0, listView.getFocusModel().getFocusedIndex());
assertEquals("Apple", listView.getFocusModel().getFocusedItem());
}
@Test public void test_rt29930() {
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(0);
keyboard.doDownArrowPress(KeyModifier.SHIFT);
keyboard.doDownArrowPress(KeyModifier.SHIFT);
assertTrue(isSelected(0,1,2));
assertEquals(2, fm.getFocusedIndex());
assertEquals(0, getAnchor());
keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey(), PlatformUtil.isMac() ? KeyModifier.CTRL : null);
assertTrue(isSelected(0,1));
assertEquals(2, fm.getFocusedIndex());
assertEquals(2, getAnchor());
keyboard.doDownArrowPress(KeyModifier.SHIFT);
assertTrue(isSelected(2,3));
assertTrue(isNotSelected(0,1));
assertEquals(3, fm.getFocusedIndex());
assertEquals(2, getAnchor());
}
private int rt29849_start_count = 0;
private int rt29849_cancel_count = 0;
@Test public void test_rt29849() {
listView.setEditable(true);
listView.setOnEditStart(t -> {
rt29849_start_count++;
});
listView.setOnEditCancel(t -> {
rt29849_cancel_count++;
});
assertEquals(0, rt29849_start_count);
assertEquals(0, rt29849_cancel_count);
IndexedCell cell = VirtualFlowTestUtils.getCell(listView, 0);
assertTrue(cell.isEditable());
assertFalse(cell.isEditing());
assertEquals(0, cell.getIndex());
listView.edit(0);
assertTrue(cell.isEditing());
assertEquals(1, rt29849_start_count);
assertEquals(0, rt29849_cancel_count);
keyboard.doKeyPress(KeyCode.ESCAPE);
assertFalse(cell.isEditing());
assertEquals(1, rt29849_start_count);
assertEquals(1, rt29849_cancel_count);
}
private int rt31577_count = 0;
@Test public void test_rt31577() {
final MultipleSelectionModel sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.SINGLE);
sm.clearSelection();
sm.selectedItemProperty().addListener(observable -> {
rt31577_count++;
});
assertTrue(sm.getSelectedItems().isEmpty());
assertFalse(sm.isSelected(1));
assertEquals(0, rt31577_count);
keyboard.doKeyPress(KeyCode.KP_DOWN);
assertEquals(1, sm.getSelectedItems().size());
assertTrue(sm.isSelected(0));
assertTrue(sm.getSelectedItems().contains("1"));
assertEquals("1", sm.getSelectedItem());
assertEquals(1, rt31577_count);
keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.CTRL,
Utils.isMac() ? KeyModifier.getShortcutKey() : null);
assertTrue(sm.getSelectedItems().isEmpty());
assertFalse(sm.isSelected(1));
assertNull(sm.getSelectedItem());
assertEquals(2, rt31577_count);
}
@Test public void test_rt32383_pageDown() {
listView.getItems().clear();
for (int i = 0; i < 100; i++) {
listView.getItems().add("Row " + i);
}
final MultipleSelectionModel sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.SINGLE);
sm.clearAndSelect(0);
final String initialFocusOwner = fm.getFocusedItem();
keyboard.doKeyPress(KeyCode.PAGE_DOWN, KeyModifier.getShortcutKey());
Toolkit.getToolkit().firePulse();
final String newFocusOwner = fm.getFocusedItem();
assertNotSame(initialFocusOwner, newFocusOwner);
keyboard.doKeyPress(KeyCode.PAGE_DOWN, KeyModifier.getShortcutKey());
Toolkit.getToolkit().firePulse();
final String nextFocusOwner = fm.getFocusedItem();
assertNotSame(initialFocusOwner, nextFocusOwner);
assertNotSame(newFocusOwner, nextFocusOwner);
}
@Test public void test_rt32383_pageUp() {
listView.getItems().clear();
for (int i = 0; i < 100; i++) {
listView.getItems().add("Row " + i);
}
final int lastIndex = 99;
final MultipleSelectionModel sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.SINGLE);
sm.clearAndSelect(lastIndex);
listView.scrollTo(lastIndex);
Toolkit.getToolkit().firePulse();
assertEquals(lastIndex, sm.getSelectedIndex());
assertEquals(lastIndex, fm.getFocusedIndex());
final String initialFocusOwner = fm.getFocusedItem();
keyboard.doKeyPress(KeyCode.PAGE_UP, KeyModifier.getShortcutKey());
Toolkit.getToolkit().firePulse();
final String newFocusOwner = fm.getFocusedItem();
assertNotSame(initialFocusOwner, newFocusOwner);
keyboard.doKeyPress(KeyCode.PAGE_UP, KeyModifier.getShortcutKey());
Toolkit.getToolkit().firePulse();
final String nextFocusOwner = fm.getFocusedItem();
assertNotSame(initialFocusOwner, nextFocusOwner);
assertNotSame(newFocusOwner, nextFocusOwner);
}
@Test public void test_rt19053_pageUp() {
final int items = 8;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
final int middleIndex = items / 2;
final MultipleSelectionModel sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.SINGLE);
sm.clearAndSelect(middleIndex);
assertEquals(middleIndex, sm.getSelectedIndex());
final Object initialSelectionOwner = sm.getSelectedItem();
keyboard.doKeyPress(KeyCode.PAGE_DOWN, KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
final Object newSelectionOwner = sm.getSelectedItem();
assertNotSame(initialSelectionOwner, newSelectionOwner);
keyboard.doKeyPress(KeyCode.PAGE_UP, KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
assertEquals(0, fm.getFocusedIndex());
assertEquals(0, sm.getSelectedIndex());
final Object nextSelectionOwner = sm.getSelectedItem();
assertNotSame(initialSelectionOwner, nextSelectionOwner);
assertNotSame(newSelectionOwner, nextSelectionOwner);
}
@Test public void test_rt19053_pageDown() {
final int items = 8;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
final int middleIndex = items / 2;
final MultipleSelectionModel sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.SINGLE);
sm.clearAndSelect(middleIndex);
assertEquals(middleIndex, sm.getSelectedIndex());
final Object initialSelectionOwner = sm.getSelectedItem();
keyboard.doKeyPress(KeyCode.PAGE_UP, KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
final Object newSelectionOwner = sm.getSelectedItem();
assertNotSame(initialSelectionOwner, newSelectionOwner);
keyboard.doKeyPress(KeyCode.PAGE_DOWN, KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
assertEquals(items - 1, fm.getFocusedIndex());
assertEquals(items - 1, sm.getSelectedIndex());
final Object nextSelectionOwner = sm.getSelectedItem();
assertNotSame(initialSelectionOwner, nextSelectionOwner);
assertNotSame(newSelectionOwner, nextSelectionOwner);
}
@Ignore("Fix not yet implemented")
@Test public void test_rt20641_pageUp() {
final int items = 20;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
final MultipleSelectionModel sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(0);
assertEquals(0, sm.getSelectedIndex());
final int selectedIndex0 = sm.getSelectedIndex();
keyboard.doKeyPress(KeyCode.PAGE_DOWN, KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
keyboard.doKeyPress(KeyCode.PAGE_DOWN, KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
final int selectedIndex1 = sm.getSelectedIndex();
assertNotSame(selectedIndex0, selectedIndex1);
assertTrue(selectedIndex0 < selectedIndex1);
keyboard.doKeyPress(KeyCode.PAGE_UP, KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
final int selectedIndex2 = sm.getSelectedIndex();
assertNotSame(selectedIndex0, selectedIndex1);
assertNotSame(selectedIndex0, selectedIndex2);
assertTrue(selectedIndex2 < selectedIndex1);
assertTrue(selectedIndex0 < selectedIndex2);
}
@Ignore("Fix not yet implemented")
@Test public void test_rt20641_pageDown() {
final int items = 100;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
final MultipleSelectionModel sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(items - 1);
FocusModelShim.setFocusedIndex(fm, items - 1);
assertEquals(items - 1, sm.getSelectedIndex());
assertEquals(items - 1, fm.getFocusedIndex());
final int selectedIndex0 = sm.getSelectedIndex();
listView.scrollTo(items - 1);
Toolkit.getToolkit().firePulse();
keyboard.doKeyPress(KeyCode.PAGE_UP, KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
keyboard.doKeyPress(KeyCode.PAGE_UP, KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
final int selectedIndex1 = sm.getSelectedIndex();
assertNotSame(selectedIndex0, selectedIndex1);
assertTrue(selectedIndex0 > selectedIndex1);
keyboard.doKeyPress(KeyCode.PAGE_DOWN, KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
final int selectedIndex2 = sm.getSelectedIndex();
assertNotSame(selectedIndex0, selectedIndex1);
assertNotSame(selectedIndex0, selectedIndex2);
assertTrue(selectedIndex2 > selectedIndex1);
assertTrue(selectedIndex0 > selectedIndex2);
}
@Test public void test_rt21375_scenario_1a_down() {
final int items = 8;
listView.getItems().clear();
for (int i = 1; i <= items; i++) {
listView.getItems().add("Row " + i);
}
final MultipleSelectionModel sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(0);
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
assertTrue(isSelected(0,1,2,3));
assertEquals(4, sm.getSelectedItems().size());
}
@Test public void test_rt21375_scenario_1b_down() {
final int items = 8;
listView.getItems().clear();
for (int i = 1; i <= items; i++) {
listView.getItems().add("Row " + i);
}
final MultipleSelectionModel sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(0);
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
assertTrue(isSelected(0,1,2,3));
assertEquals(4, sm.getSelectedItems().size());
}
@Test public void test_rt21375_scenario_2_down() {
final int items = 8;
listView.getItems().clear();
for (int i = 1; i <= items; i++) {
listView.getItems().add("Row " + i);
}
final MultipleSelectionModel sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(0);
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey(), PlatformUtil.isMac() ? KeyModifier.CTRL : null);
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
assertTrue(isSelected(2,3,4));
assertEquals(3, sm.getSelectedItems().size());
}
@Test public void test_rt21375_scenario_3_down() {
final int items = 8;
listView.getItems().clear();
for (int i = 1; i <= items; i++) {
listView.getItems().add("Row " + i);
}
final MultipleSelectionModel sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(0);
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey(), PlatformUtil.isMac() ? KeyModifier.CTRL : null);
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
assertTrue(isSelected(0,2,3,4));
assertEquals(4, sm.getSelectedItems().size());
}
@Test public void test_rt21375_scenario_1a_up() {
final int items = 8;
listView.getItems().clear();
for (int i = 1; i <= items; i++) {
listView.getItems().add("Row " + i);
}
final MultipleSelectionModel sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(7);
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.UP, KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
assertTrue(isSelected(7,6,5,4));
assertEquals(4, sm.getSelectedItems().size());
}
@Test public void test_rt21375_scenario_1b_up() {
final int items = 8;
listView.getItems().clear();
for (int i = 1; i <= items; i++) {
listView.getItems().add("Row " + i);
}
final MultipleSelectionModel sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(7);
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
assertTrue(isSelected(7,6,5,4));
assertEquals(4, sm.getSelectedItems().size());
}
@Test public void test_rt21375_scenario_2_up() {
final int items = 8;
listView.getItems().clear();
for (int i = 1; i <= items; i++) {
listView.getItems().add("Row " + i);
}
final MultipleSelectionModel sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(7);
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey(), PlatformUtil.isMac() ? KeyModifier.CTRL : null);
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.UP, KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
assertTrue(isSelected(5,4,3));
assertEquals(3, sm.getSelectedItems().size());
}
@Test public void test_rt21375_scenario_3_up() {
final int items = 8;
listView.getItems().clear();
for (int i = 1; i <= items; i++) {
listView.getItems().add("Row " + i);
}
final MultipleSelectionModel sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(7);
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey(), PlatformUtil.isMac() ? KeyModifier.CTRL : null);
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
assertTrue(isSelected(7,5,4,3));
assertEquals(4, sm.getSelectedItems().size());
}
@Test public void test_rt33301_multipleSelection_down() {
final int items = 5;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
final FocusModel fm = listView.getFocusModel();
final MultipleSelectionModel sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(2);
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
assertTrue(isNotSelected(0,1));
assertTrue(isSelected(2,3,4));
assertEquals(3, sm.getSelectedItems().size());
assertTrue(fm.isFocused(4));
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
assertTrue(isNotSelected(0,1));
assertTrue(isSelected(2,3,4));
assertEquals(3, sm.getSelectedItems().size());
assertTrue("Focus index incorrectly at: " + fm.getFocusedIndex(), fm.isFocused(4));
}
@Test public void test_rt33301_multipleSelection_up() {
final int items = 5;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
final FocusModel fm = listView.getFocusModel();
final MultipleSelectionModel sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(2);
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
assertTrue(isNotSelected(3,4));
assertTrue(isSelected(0,1,2));
assertEquals(3, sm.getSelectedItems().size());
assertTrue(fm.isFocused(0));
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
assertTrue(isNotSelected(3,4));
assertTrue(isSelected(0,1,2));
assertEquals(3, sm.getSelectedItems().size());
assertTrue(fm.isFocused(0));
}
@Test public void test_rt33301_singleSelection_down() {
final int items = 5;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
final FocusModel fm = listView.getFocusModel();
final MultipleSelectionModel sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.SINGLE);
sm.clearAndSelect(2);
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
assertTrue(isNotSelected(0,1,2,3));
assertTrue(isSelected(4));
assertEquals(1, sm.getSelectedItems().size());
assertTrue(fm.isFocused(4));
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
assertTrue(isNotSelected(0,1,2,3));
assertTrue(isSelected(4));
assertEquals(1, sm.getSelectedItems().size());
assertTrue(fm.isFocused(4));
}
@Test public void test_rt33301_singleSelection_up() {
final int items = 5;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
final FocusModel fm = listView.getFocusModel();
final MultipleSelectionModel sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.SINGLE);
sm.clearAndSelect(2);
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
assertTrue(isNotSelected(1,2,3,4));
assertTrue(isSelected(0));
assertEquals(1, sm.getSelectedItems().size());
assertTrue(fm.isFocused(0));
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
assertTrue(isNotSelected(1,2,3,4));
assertTrue(isSelected(0));
assertEquals(1, sm.getSelectedItems().size());
assertTrue(fm.isFocused(0));
}
@Test public void test_rt20915() {
final FocusModel fm = listView.getFocusModel();
final MultipleSelectionModel sm = listView.getSelectionModel();
sm.clearAndSelect(0);
assertEquals(0, getAnchor());
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey());
Toolkit.getToolkit().firePulse();
assertTrue(isNotSelected(1,2,3));
assertTrue(isSelected(0));
assertEquals(1, sm.getSelectedItems().size());
assertTrue(fm.isFocused(3));
keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
assertTrue(isSelected(0,1,2,3));
assertEquals(4, sm.getSelectedItems().size());
assertTrue(fm.isFocused(3));
}
@Test public void test_rt34200() {
final int items = 100;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
sm.clearAndSelect(99);
listView.scrollTo(99);
assertEquals(99, getAnchor());
assertEquals(99, fm.getFocusedIndex());
keyboard.doKeyPress(KeyCode.PAGE_UP, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
assertEquals(99, getAnchor());
assertTrue(fm.getFocusedIndex() < 99);
}
@Test public void test_rt34369() {
final int items = 100;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
sm.clearAndSelect(99);
listView.scrollTo(99);
assertEquals(99, getAnchor());
assertEquals(99, fm.getFocusedIndex());
keyboard.doKeyPress(KeyCode.PAGE_UP, KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
assertEquals(99, getAnchor());
assertTrue(fm.getFocusedIndex() < 99);
}
@Test public void test_rt33894() {
final int items = 5;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
sm.clearAndSelect(1);
assertEquals(1, getAnchor());
assertEquals(1, fm.getFocusedIndex());
assertEquals(1, sm.getSelectedIndex());
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey());
Toolkit.getToolkit().firePulse();
assertEquals(1, getAnchor());
assertEquals(2, fm.getFocusedIndex());
assertEquals(1, sm.getSelectedIndex());
keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
assertEquals(2, getAnchor());
assertEquals(2, fm.getFocusedIndex());
assertEquals(2, sm.getSelectedIndex());
assertTrue(isSelected(1, 2));
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey());
keyboard.doKeyPress(KeyCode.UP, KeyModifier.getShortcutKey());
Toolkit.getToolkit().firePulse();
assertEquals(2, getAnchor());
assertEquals(0, fm.getFocusedIndex());
assertEquals(2, sm.getSelectedIndex());
assertTrue(isSelected(1, 2));
keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
Toolkit.getToolkit().firePulse();
assertEquals(0, getAnchor());
assertEquals(0, fm.getFocusedIndex());
assertEquals(0, sm.getSelectedIndex());
assertTrue(isSelected(0, 1, 2));
}
@Test public void test_rt34425() {
final int items = 5;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
sm.clearAndSelect(1);
assertEquals(1, getAnchor());
assertEquals(1, fm.getFocusedIndex());
assertEquals(1, sm.getSelectedIndex());
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.getShortcutKey());
Toolkit.getToolkit().firePulse();
assertEquals(1, getAnchor());
assertEquals(2, fm.getFocusedIndex());
assertEquals(1, sm.getSelectedIndex());
keyboard.doKeyPress(KeyCode.SPACE);
Toolkit.getToolkit().firePulse();
assertEquals(2, getAnchor());
assertEquals(2, fm.getFocusedIndex());
assertEquals(2, sm.getSelectedIndex());
assertTrue(isSelected(1, 2));
}
@Test public void test_rt34407_down_down_up() {
final int items = 100;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
listView.setPrefHeight(130);
StageLoader sl = new StageLoader(listView);
final FocusModel fm = listView.getFocusModel();
final MultipleSelectionModel sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(0);
fm.focus(0);
assertEquals(0, getAnchor());
assertTrue(fm.isFocused(0));
assertTrue(sm.isSelected(0));
assertFalse(sm.isSelected(1));
keyboard.doKeyPress(KeyCode.PAGE_DOWN, KeyModifier.SHIFT);
final int leadSelectedIndex = sm.getSelectedIndex();
final int selectedIndicesCount = sm.getSelectedIndices().size();
assertEquals(6, leadSelectedIndex);
assertEquals(6, fm.getFocusedIndex());
assertEquals(7, selectedIndicesCount);
keyboard.doKeyPress(KeyCode.PAGE_DOWN, KeyModifier.SHIFT);
assertEquals(leadSelectedIndex * 2, sm.getSelectedIndex());
assertEquals(leadSelectedIndex * 2, fm.getFocusedIndex());
assertEquals(selectedIndicesCount * 2 - 1, sm.getSelectedIndices().size());
keyboard.doKeyPress(KeyCode.PAGE_UP, KeyModifier.SHIFT);
assertEquals(leadSelectedIndex, sm.getSelectedIndex());
assertEquals(leadSelectedIndex, fm.getFocusedIndex());
assertEquals(selectedIndicesCount, sm.getSelectedIndices().size());
sl.dispose();
}
@Test public void test_rt34407_up_up_down() {
final int items = 100;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
listView.setPrefHeight(130);
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(99);
fm.focus(99);
listView.scrollTo(99);
Toolkit.getToolkit().firePulse();
assertEquals(99, getAnchor());
assertTrue(fm.isFocused(99));
assertTrue(sm.isSelected(99));
assertFalse(sm.isSelected(98));
keyboard.doKeyPress(KeyCode.PAGE_UP, KeyModifier.SHIFT);
final int leadSelectedIndex = sm.getSelectedIndex();
final int selectedIndicesCount = sm.getSelectedIndices().size();
final int diff = 99 - leadSelectedIndex;
assertEquals(99 - diff, leadSelectedIndex);
assertEquals(99 - diff, fm.getFocusedIndex());
assertEquals(8, selectedIndicesCount);
keyboard.doKeyPress(KeyCode.PAGE_UP, KeyModifier.SHIFT);
assertEquals(99 - diff * 2 + 1, sm.getSelectedIndex());
assertEquals(selectedIndicesCount * 2 - 2, sm.getSelectedIndices().size());
keyboard.doKeyPress(KeyCode.PAGE_DOWN, KeyModifier.SHIFT);
assertEquals(leadSelectedIndex, sm.getSelectedIndex());
assertEquals(selectedIndicesCount, sm.getSelectedIndices().size());
}
@Test public void test_rt34768() {
listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
listView.getItems().clear();
keyboard.doKeyPress(KeyCode.A, KeyModifier.getShortcutKey());
}
@Test public void test_rt35853_multipleSelection_shiftDown() {
final int items = 10;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(5);
assertEquals(5, getAnchor());
assertTrue(fm.isFocused(5));
assertTrue(sm.isSelected(5));
sm.selectedIndexProperty().addListener(observable -> {
assertEquals(4, sm.getSelectedIndex());
});
keyboard.doKeyPress(KeyCode.UP, KeyModifier.SHIFT);
assertEquals(5, getAnchor());
assertTrue(fm.isFocused(4));
assertTrue(sm.isSelected(4));
assertTrue(sm.isSelected(5));
}
@Test public void test_rt35853_multipleSelection_noShiftDown() {
final int items = 10;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(5);
assertEquals(5, getAnchor());
assertTrue(fm.isFocused(5));
assertTrue(sm.isSelected(5));
sm.selectedIndexProperty().addListener(observable -> {
assertEquals(4, sm.getSelectedIndex());
});
keyboard.doKeyPress(KeyCode.UP);
assertEquals(4, getAnchor());
assertTrue(fm.isFocused(4));
assertTrue(sm.isSelected(4));
assertFalse(sm.isSelected(5));
}
@Test public void test_rt35853_singleSelection_shiftDown() {
final int items = 10;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
sm.setSelectionMode(SelectionMode.SINGLE);
sm.clearAndSelect(5);
assertEquals(5, getAnchor());
assertTrue(fm.isFocused(5));
assertTrue(sm.isSelected(5));
sm.selectedIndexProperty().addListener(observable -> {
assertEquals(4, sm.getSelectedIndex());
});
keyboard.doKeyPress(KeyCode.UP, KeyModifier.SHIFT);
assertEquals(4, getAnchor());
assertTrue(fm.isFocused(4));
assertTrue(sm.isSelected(4));
assertFalse(sm.isSelected(5));
}
@Test public void test_rt35853_singleSelection_noShiftDown() {
final int items = 10;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
sm.setSelectionMode(SelectionMode.SINGLE);
sm.clearAndSelect(5);
assertEquals(5, getAnchor());
assertTrue(fm.isFocused(5));
assertTrue(sm.isSelected(5));
sm.selectedIndexProperty().addListener(observable -> {
assertEquals(4, sm.getSelectedIndex());
});
keyboard.doKeyPress(KeyCode.UP);
assertEquals(4, getAnchor());
assertTrue(fm.isFocused(4));
assertTrue(sm.isSelected(4));
assertFalse(sm.isSelected(5));
}
@Test public void test_rt36800() {
final int items = 10;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
sm.setSelectionMode(SelectionMode.SINGLE);
sm.clearAndSelect(5);
assertEquals(5, getAnchor());
assertTrue(fm.isFocused(5));
assertTrue(sm.isSelected(5));
keyboard.doKeyPress(KeyCode.UP, KeyModifier.SHIFT);
keyboard.doKeyPress(KeyCode.UP, KeyModifier.SHIFT);
keyboard.doKeyPress(KeyCode.UP, KeyModifier.SHIFT);
keyboard.doKeyPress(KeyCode.UP, KeyModifier.SHIFT);
keyboard.doKeyPress(KeyCode.UP, KeyModifier.SHIFT);
ControlTestUtils.runWithExceptionHandler(() -> {
keyboard.doKeyPress(KeyCode.UP, KeyModifier.SHIFT);
});
assertEquals(0, getAnchor());
assertTrue(fm.isFocused(0));
assertTrue(sm.isSelected(0));
assertFalse(sm.isSelected(1));
assertFalse(sm.isSelected(2));
assertFalse(sm.isSelected(3));
assertFalse(sm.isSelected(4));
assertFalse(sm.isSelected(5));
}
@Test public void test_rt_36942() {
final int items = 3;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
MultipleSelectionModel<String> sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
ObservableList<String> selectedItems = sm.getSelectedItems();
ListView<String> selectedItemsListView = new ListView<>(selectedItems);
HBox root = new HBox(5, listView, selectedItemsListView);
StageLoader sl = new StageLoader(root);
sm.select(0);
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.SHIFT);
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.SHIFT);
ControlTestUtils.runWithExceptionHandler(() -> {
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.SHIFT);
});
sl.dispose();
}
@Test public void test_rt_37130_pageUpAtTop() {
final int items = 100;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
MultipleSelectionModel<String> sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
StageLoader sl = new StageLoader(listView);
sm.select(5);
keyboard.doKeyPress(KeyCode.PAGE_UP, KeyModifier.SHIFT);
keyboard.doKeyPress(KeyCode.PAGE_UP, KeyModifier.SHIFT);
sl.dispose();
}
@Test public void test_rt_37130_pageUpAtBottom() {
final int items = 100;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
MultipleSelectionModel<String> sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
StageLoader sl = new StageLoader(listView);
sm.select(95);
keyboard.doKeyPress(KeyCode.PAGE_UP, KeyModifier.SHIFT);
keyboard.doKeyPress(KeyCode.PAGE_UP, KeyModifier.SHIFT);
sl.dispose();
}
@Test public void test_rt_37130_pageDownAtTop() {
final int items = 100;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
MultipleSelectionModel<String> sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
StageLoader sl = new StageLoader(listView);
sm.select(5);
keyboard.doKeyPress(KeyCode.PAGE_DOWN, KeyModifier.SHIFT);
keyboard.doKeyPress(KeyCode.PAGE_DOWN, KeyModifier.SHIFT);
sl.dispose();
}
@Test public void test_rt_37130_pageDownAtBottom() {
final int items = 100;
listView.getItems().clear();
for (int i = 0; i < items; i++) {
listView.getItems().add("Row " + i);
}
MultipleSelectionModel<String> sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
StageLoader sl = new StageLoader(listView);
sm.select(95);
keyboard.doKeyPress(KeyCode.PAGE_DOWN, KeyModifier.SHIFT);
keyboard.doKeyPress(KeyCode.PAGE_DOWN, KeyModifier.SHIFT);
sl.dispose();
}
private int rt_39088_indices_event_count = 0;
private int rt_39088_items_event_count = 0;
@Test public void test_rt_39088() {
listView.getItems().clear();
for (int i = 0; i < 4; i++) {
listView.getItems().add("Row " + i);
}
MultipleSelectionModel<String> sm = listView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
ObservableList<Integer> indices = sm.getSelectedIndices();
ObservableList<String> items = sm.getSelectedItems();
indices.addListener((ListChangeListener<Integer>) change -> rt_39088_indices_event_count++);
items.addListener((ListChangeListener<String>) change -> rt_39088_items_event_count++);
StageLoader sl = new StageLoader(listView);
assertEquals(0, rt_39088_indices_event_count);
assertEquals(0, rt_39088_items_event_count);
assertEquals(0, indices.size());
assertEquals(0, items.size());
sm.select(3);
assertEquals(1, rt_39088_indices_event_count);
assertEquals(1, rt_39088_items_event_count);
assertEquals(1, indices.size());
assertEquals(1, items.size());
keyboard.doKeyPress(KeyCode.UP, KeyModifier.SHIFT);
assertEquals(2, rt_39088_indices_event_count);
assertEquals(2, rt_39088_items_event_count);
assertEquals(2, indices.size());
assertEquals(2, items.size());
keyboard.doKeyPress(KeyCode.UP, KeyModifier.SHIFT);
assertEquals(3, rt_39088_indices_event_count);
assertEquals(3, rt_39088_items_event_count);
assertEquals(3, indices.size());
assertEquals(3, items.size());
sl.dispose();
}
@Test public void test_rt_27709_singleSelection_rowSelection() {
test_rt_27709(SelectionMode.SINGLE, false);
}
@Test public void test_rt_27709_multipleSelection_rowSelection() {
test_rt_27709(SelectionMode.MULTIPLE, false);
}
@Test public void test_rt_27709_singleSelection_rowSelection_resetSelection() {
test_rt_27709(SelectionMode.SINGLE, true);
}
@Test public void test_rt_27709_multipleSelection_rowSelection_resetSelection() {
test_rt_27709(SelectionMode.MULTIPLE, true);
}
private void test_rt_27709(SelectionMode mode, boolean resetSelection) {
ObservableList<String> itemsList = FXCollections.observableArrayList();
for (int i = 0; i < 10; i++) {
itemsList.add("Row " + i);
}
listView.setItems(itemsList);
MultipleSelectionModel<String> sm = listView.getSelectionModel();
sm.setSelectionMode(mode);
ObservableList<Integer> indices = sm.getSelectedIndices();
int expectedSize = mode == SelectionMode.SINGLE ? 1 : 10;
int lookupIndex = mode == SelectionMode.SINGLE ? 0 : 9;
sm.select(0);
assertEquals(1, indices.size());
keyboard.doKeyPress(KeyCode.END, KeyModifier.SHIFT);
assertEquals(expectedSize, indices.size());
assertEquals(9, (int) indices.get(lookupIndex));
if (resetSelection) {
sm.clearAndSelect(9);
int anchor = ListCellBehavior.getAnchor(listView, null);
assertEquals(9, anchor);
} else {
expectedSize = 1;
}
keyboard.doKeyPress(KeyCode.HOME, KeyModifier.SHIFT);
assertEquals(expectedSize, indices.size());
assertTrue(debug(),sm.isSelected(0));
if (resetSelection) {
sm.clearAndSelect(0);
int anchor = ListCellBehavior.getAnchor(listView, null);
assertEquals(0, anchor);
} else {
expectedSize = mode == SelectionMode.SINGLE ? 1 : 10;
}
keyboard.doKeyPress(KeyCode.END, KeyModifier.SHIFT);
assertEquals(expectedSize, indices.size());
assertTrue(sm.isSelected(9));
}
@Test public void test_rt_24865_moveDownwards() {
listView.getItems().clear();
for (int i = 0; i < 100; i++) {
listView.getItems().add("Row " + i);
}
Toolkit.getToolkit().firePulse();
ObservableList<Integer> indices = sm.getSelectedIndices();
sm.select(0);
assertTrue(isSelected(0));
assertTrue(fm.isFocused(0));
assertEquals(1, indices.size());
assertEquals(0, (int) ListCellBehavior.getAnchor(listView, -1));
keyboard.doDownArrowPress(KeyModifier.SHIFT);
keyboard.doDownArrowPress(KeyModifier.SHIFT);
keyboard.doDownArrowPress(KeyModifier.SHIFT);
assertTrue(isSelected(0, 1, 2, 3));
assertTrue(fm.isFocused(3));
assertEquals(4, indices.size());
assertEquals(0, (int) ListCellBehavior.getAnchor(listView, -1));
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
assertTrue(isSelected(0, 1, 2, 3));
assertTrue(isNotSelected(4, 5, 6, 7, 8, 9));
assertTrue(fm.isFocused(6));
assertEquals(4, indices.size());
assertEquals(0, (int) ListCellBehavior.getAnchor(listView, -1));
keyboard.doKeyPress(KeyCode.PAGE_DOWN, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
final int selectedRowCount = indices.size();
for (int i = 0; i < selectedRowCount; i++) {
assertTrue(isSelected(i));
}
assertTrue(fm.isFocused(selectedRowCount - 1));
assertEquals(0, (int) ListCellBehavior.getAnchor(listView, -1));
keyboard.doDownArrowPress(KeyModifier.SHIFT);
int newSelectedRowCount = selectedRowCount + 1;
for (int i = 0; i < newSelectedRowCount; i++) {
assertTrue(isSelected(i));
}
assertTrue(fm.isFocused(newSelectedRowCount - 1));
assertEquals(0, (int) ListCellBehavior.getAnchor(listView, -1));
}
@Test public void test_rt_24865_moveUpwards() {
listView.getItems().clear();
for (int i = 0; i < 100; i++) {
listView.getItems().add("Row " + i);
}
Toolkit.getToolkit().firePulse();
ObservableList<Integer> indices = sm.getSelectedIndices();
sm.select(50);
listView.scrollTo(50);
Toolkit.getToolkit().firePulse();
assertTrue(isSelected(50));
assertTrue(fm.isFocused(50));
assertEquals(1, indices.size());
assertEquals(50, (int) ListCellBehavior.getAnchor(listView, -1));
keyboard.doUpArrowPress(KeyModifier.SHIFT);
keyboard.doUpArrowPress(KeyModifier.SHIFT);
keyboard.doUpArrowPress(KeyModifier.SHIFT);
assertTrue(isSelected(50, 49, 48, 47));
assertTrue(fm.isFocused(47));
assertEquals(4, indices.size());
assertEquals(50, (int) ListCellBehavior.getAnchor(listView, -1));
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
assertTrue(isSelected(50, 49, 48, 47));
assertTrue(isNotSelected(46, 45, 44, 43, 42, 41));
assertTrue(fm.isFocused(44));
assertEquals(4, indices.size());
assertEquals(50, (int) ListCellBehavior.getAnchor(listView, -1));
keyboard.doKeyPress(KeyCode.PAGE_UP, KeyModifier.getShortcutKey(), KeyModifier.SHIFT);
final int selectedRowCount = indices.size();
for (int i = 0; i < selectedRowCount; i++) {
assertTrue(isSelected(50 - i));
}
assertTrue(fm.isFocused(50 - selectedRowCount + 1));
assertEquals(50, (int) ListCellBehavior.getAnchor(listView, -1));
keyboard.doUpArrowPress(KeyModifier.SHIFT);
int newSelectedRowCount = selectedRowCount + 1;
for (int i = 0; i < newSelectedRowCount; i++) {
assertTrue(isSelected(50 - i));
}
assertTrue(fm.isFocused(50 - newSelectedRowCount + 1));
assertEquals(50, (int) ListCellBehavior.getAnchor(listView, -1));
}
@Test public void test_jdk_8160858() {
listView.getItems().clear();
for (int i = 0; i < 10; i++) {
listView.getItems().add("Row " + i);
}
Button btn = new Button("Button");
((Group)listView.getScene().getRoot()).getChildren().add(btn);
listView.requestFocus();
Toolkit.getToolkit().firePulse();
assertEquals(stageLoader.getStage().getScene().getFocusOwner(), listView);
assertEquals(-1, sm.getSelectedIndex());
assertEquals(0, fm.getFocusedIndex());
keyboard.doDownArrowPress();
assertEquals(1, sm.getSelectedIndex());
assertEquals(1, fm.getFocusedIndex());
btn.requestFocus();
Toolkit.getToolkit().firePulse();
assertEquals(stageLoader.getStage().getScene().getFocusOwner(), btn);
listView.requestFocus();
Toolkit.getToolkit().firePulse();
assertEquals(stageLoader.getStage().getScene().getFocusOwner(), listView);
assertEquals(1, sm.getSelectedIndex());
assertEquals(1, fm.getFocusedIndex());
}
}
