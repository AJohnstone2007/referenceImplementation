package test.javafx.scene.control.skin;
import java.util.AbstractList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import java.util.Iterator;
import java.util.LinkedList;
import javafx.beans.InvalidationListener;
import javafx.event.Event;
import javafx.scene.control.IndexedCell;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;
import test.javafx.scene.control.SkinStub;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.IndexedCellShim;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.skin.VirtualFlowShim;
import javafx.scene.control.skin.VirtualFlowShim.ArrayLinkedListShim;
public class VirtualFlowTest {
private ArrayLinkedListShim<CellStub> list;
private CellStub a;
private CellStub b;
private CellStub c;
private VirtualFlowShim<IndexedCell> flow;
@Before public void setUp() {
list = new ArrayLinkedListShim<CellStub>();
a = new CellStub(flow, "A");
b = new CellStub(flow, "B");
c = new CellStub(flow, "C");
flow = new VirtualFlowShim();
flow.setVertical(true);
flow.setCellFactory(p -> new CellStub(flow) {
@Override
protected double computeMinWidth(double height) {
return computePrefWidth(height);
}
@Override
protected double computeMaxWidth(double height) {
return computePrefWidth(height);
}
@Override
protected double computePrefWidth(double height) {
return flow.isVertical() ? (c.getIndex() == 29 ? 200 : 100) : (c.getIndex() == 29 ? 100 : 25);
}
@Override
protected double computeMinHeight(double width) {
return computePrefHeight(width);
}
@Override
protected double computeMaxHeight(double width) {
return computePrefHeight(width);
}
@Override
protected double computePrefHeight(double width) {
return flow.isVertical() ? (c.getIndex() == 29 ? 100 : 25) : (c.getIndex() == 29 ? 200 : 100);
}
});
flow.setCellCount(100);
flow.resize(300, 300);
pulse();
pulse();
}
private void pulse() {
flow.layout();
}
private void assertMatch(List<IndexedCell> control, AbstractList<IndexedCell> list) {
assertEquals("The control and list did not have the same sizes. " +
"Expected " + control.size() + " but was " + list.size(),
control.size(), list.size());
int index = 0;
Iterator<IndexedCell> itr = control.iterator();
while (itr.hasNext()) {
IndexedCell cell = itr.next();
IndexedCell cell2 = (IndexedCell)list.get(index);
assertSame("The control and list did not have the same item at " +
"index " + index + ". Expected " + cell + " but was " + cell2,
cell, cell2);
index++;
}
}
public <T extends IndexedCell> void assertMinimalNumberOfCellsAreUsed(VirtualFlowShim<T> flow) {
pulse();
IndexedCell firstCell = VirtualFlowShim.<T>cells_getFirst(flow.cells);
IndexedCell lastCell = VirtualFlowShim.<T>cells_getLast(flow.cells);
if (flow.isVertical()) {
assertTrue("There is a gap between the top of the viewport and the first cell",
firstCell.getLayoutY() <= 0);
assertTrue("There is a gap between the bottom of the last cell and the bottom of the viewport",
lastCell.getLayoutY() + lastCell.getHeight() >= flow.getViewportLength());
if (VirtualFlowShim.cells_size(flow.cells) > 3) {
IndexedCell secondLastCell = VirtualFlowShim.<T>cells_get(flow.cells, VirtualFlowShim.cells_size(flow.cells) - 2);
IndexedCell secondCell = VirtualFlowShim.<T>cells_get(flow.cells, 1);
assertFalse("There are more cells created before the start of " +
"the flow than necessary",
secondCell.getLayoutY() <= 0);
assertFalse("There are more cells created after the end of the " +
"flow than necessary",
secondLastCell.getLayoutY() + secondLastCell.getHeight() >= flow.getViewportLength());
}
} else {
assertTrue("There is a gap between the left of the viewport and the first cell",
firstCell.getLayoutX() <= 0);
assertTrue("There is a gap between the right of the last cell and the right of the viewport",
lastCell.getLayoutX() + lastCell.getWidth() >= flow.getViewportLength());
if (VirtualFlowShim.cells_size(flow.cells) > 3) {
IndexedCell secondLastCell = VirtualFlowShim.<T>cells_get(flow.cells, VirtualFlowShim.cells_size(flow.cells) - 2);
IndexedCell secondCell = VirtualFlowShim.<T>cells_get(flow.cells, 1);
assertFalse("There are more cells created before the start of " +
"the flow than necessary",
secondCell.getLayoutX() <= 0);
assertFalse("There are more cells created after the end of the " +
"flow than necessary",
secondLastCell.getLayoutX() + secondLastCell.getWidth() >= flow.getViewportLength());
}
}
}
@Test public void testGeneralLayout_NoCells() {
flow.setCellCount(0);
pulse();
assertFalse("The hbar should have been invisible", flow.shim_getHbar().isVisible());
assertFalse("The vbar should have been invisible", flow.shim_getVbar().isVisible());
assertFalse("The corner should have been invisible", flow.get_corner().isVisible());
assertEquals(flow.getWidth(), flow.get_clipView_getWidth(), 0.0);
assertEquals(flow.getHeight(), flow.get_clipView_getHeight(), 0.0);
assertMinimalNumberOfCellsAreUsed(flow);
flow.setVertical(false);
pulse();
assertFalse("The hbar should have been invisible", flow.shim_getHbar().isVisible());
assertFalse("The vbar should have been invisible", flow.shim_getVbar().isVisible());
assertFalse("The corner should have been invisible", flow.get_corner().isVisible());
assertEquals(flow.getWidth(),
flow.get_clipView_getWidth(), 0.0);
assertEquals(flow.getHeight(), flow.get_clipView_getHeight(), 0.0);
assertMinimalNumberOfCellsAreUsed(flow);
}
@Test public void testGeneralLayout_FewCells() {
flow.setCellCount(3);
pulse();
assertFalse("The hbar should have been invisible", flow.shim_getHbar().isVisible());
assertFalse("The vbar should have been invisible", flow.shim_getVbar().isVisible());
assertFalse("The corner should have been invisible", flow.get_corner().isVisible());
assertEquals(flow.getWidth(), flow.get_clipView_getWidth(), 0.0);
assertEquals(flow.getHeight(), flow.get_clipView_getHeight(), 0.0);
assertEquals(12, VirtualFlowShim.cells_size(flow.cells));
assertMinimalNumberOfCellsAreUsed(flow);
flow.setVertical(false);
pulse();
assertFalse("The hbar should have been invisible", flow.shim_getHbar().isVisible());
assertFalse("The vbar should have been invisible", flow.shim_getVbar().isVisible());
assertFalse("The corner should have been invisible", flow.get_corner().isVisible());
assertEquals(flow.getWidth(), flow.get_clipView_getWidth(), 0.0);
assertEquals(flow.getHeight(), flow.get_clipView_getHeight(), 0.0);
assertMinimalNumberOfCellsAreUsed(flow);
}
@Test public void testGeneralLayout_FewCellsButWide() {
flow.setCellCount(3);
flow.resize(50, flow.getHeight());
pulse();
assertTrue("The hbar should have been visible", flow.shim_getHbar().isVisible());
assertFalse("The vbar should have been invisible", flow.shim_getVbar().isVisible());
assertFalse("The corner should have been invisible", flow.get_corner().isVisible());
assertEquals(flow.getWidth(), flow.get_clipView_getWidth(), 0.0);
assertEquals(flow.getHeight(), flow.get_clipView_getHeight() + flow.shim_getHbar().getHeight(), 0.0);
assertEquals(flow.shim_getHbar().getLayoutY(), flow.getHeight() - flow.shim_getHbar().getHeight(), 0.0);
assertMinimalNumberOfCellsAreUsed(flow);
flow.setVertical(false);
flow.resize(300, 50);
pulse();
assertFalse("The hbar should have been invisible", flow.shim_getHbar().isVisible());
assertTrue("The vbar should have been visible", flow.shim_getVbar().isVisible());
assertFalse("The corner should have been invisible", flow.get_corner().isVisible());
assertEquals(flow.getWidth(), flow.get_clipView_getWidth() + flow.shim_getVbar().getWidth(), 0.0);
assertEquals(flow.getHeight(), flow.get_clipView_getHeight(), 0.0);
assertEquals(flow.shim_getVbar().getLayoutX(), flow.getWidth() - flow.shim_getVbar().getWidth(), 0.0);
assertMinimalNumberOfCellsAreUsed(flow);
}
@Test public void testGeneralLayout_FewCellsButWide_ThenNarrow() {
flow.setCellCount(3);
flow.resize(50, flow.getHeight());
pulse();
flow.resize(300, flow.getHeight());
pulse();
assertFalse("The hbar should have been invisible", flow.shim_getHbar().isVisible());
assertFalse("The vbar should have been invisible", flow.shim_getVbar().isVisible());
assertFalse("The corner should have been invisible", flow.get_corner().isVisible());
assertEquals(flow.getWidth(), flow.get_clipView_getWidth(), 0.0);
assertEquals(flow.getHeight(), flow.get_clipView_getHeight(), 0.0);
assertMinimalNumberOfCellsAreUsed(flow);
flow.setVertical(false);
flow.resize(300, 50);
pulse();
flow.resize(flow.getWidth(), 300);
pulse();
assertFalse("The hbar should have been invisible", flow.shim_getHbar().isVisible());
assertFalse("The vbar should have been invisible", flow.shim_getVbar().isVisible());
assertFalse("The corner should have been invisible", flow.get_corner().isVisible());
assertEquals(flow.getWidth(), flow.get_clipView_getWidth(), 0.0);
assertEquals(flow.getHeight(), flow.get_clipView_getHeight(), 0.0);
assertMinimalNumberOfCellsAreUsed(flow);
}
@Test public void testGeneralLayout_ManyCells() {
assertFalse("The hbar should have been invisible", flow.shim_getHbar().isVisible());
assertTrue("The vbar should have been visible", flow.shim_getVbar().isVisible());
assertFalse("The corner should have been invisible", flow.get_corner().isVisible());
assertEquals(flow.getWidth(), flow.get_clipView_getWidth() + flow.shim_getVbar().getWidth(), 0.0);
assertEquals(flow.getHeight(), flow.get_clipView_getHeight(), 0.0);
assertEquals(flow.shim_getVbar().getLayoutX(), flow.getWidth() - flow.shim_getVbar().getWidth(), 0.0);
assertMinimalNumberOfCellsAreUsed(flow);
flow.setVertical(false);
pulse();
assertTrue("The hbar should have been visible", flow.shim_getHbar().isVisible());
assertFalse("The vbar should have been invisible", flow.shim_getVbar().isVisible());
assertFalse("The corner should have been invisible", flow.get_corner().isVisible());
assertEquals(flow.getWidth(), flow.get_clipView_getWidth(), 0.0);
assertEquals(flow.getHeight(), flow.get_clipView_getHeight() + flow.shim_getHbar().getHeight(), 0.0);
assertEquals(flow.shim_getHbar().getLayoutY(), flow.getHeight() - flow.shim_getHbar().getHeight(), 0.0);
assertMinimalNumberOfCellsAreUsed(flow);
}
@Test public void testGeneralLayout_FewCells_ThenMany() {
flow.setCellCount(3);
pulse();
flow.setCellCount(100);
pulse();
assertFalse("The hbar should have been invisible", flow.shim_getHbar().isVisible());
assertTrue("The vbar should have been visible", flow.shim_getVbar().isVisible());
assertFalse("The corner should have been invisible", flow.get_corner().isVisible());
assertEquals(flow.getWidth(), flow.get_clipView_getWidth() + flow.shim_getVbar().getWidth(), 0.0);
assertEquals(flow.getHeight(), flow.get_clipView_getHeight(), 0.0);
assertEquals(flow.shim_getVbar().getLayoutX(), flow.getWidth() - flow.shim_getVbar().getWidth(), 0.0);
assertMinimalNumberOfCellsAreUsed(flow);
flow.setVertical(false);
flow.setCellCount(3);
pulse();
flow.setCellCount(100);
pulse();
assertTrue("The hbar should have been visible", flow.shim_getHbar().isVisible());
assertFalse("The vbar should have been invisible", flow.shim_getVbar().isVisible());
assertFalse("The corner should have been invisible", flow.get_corner().isVisible());
assertEquals(flow.getWidth(), flow.get_clipView_getWidth(), 0.0);
assertEquals(flow.getHeight(), flow.get_clipView_getHeight() + flow.shim_getHbar().getHeight(), 0.0);
assertEquals(flow.shim_getHbar().getLayoutY(), flow.getHeight() - flow.shim_getHbar().getHeight(), 0.0);
assertMinimalNumberOfCellsAreUsed(flow);
}
@Test public void testGeneralLayout_ManyCellsAndWide() {
flow.resize(50, flow.getHeight());
pulse();
assertTrue("The hbar should have been visible", flow.shim_getHbar().isVisible());
assertTrue("The vbar should have been visible", flow.shim_getVbar().isVisible());
assertTrue("The corner should have been visible", flow.get_corner().isVisible());
assertEquals(flow.getWidth(), flow.get_clipView_getWidth() + flow.shim_getVbar().getWidth(), 0.0);
assertEquals(flow.getHeight(), flow.get_clipView_getHeight() + flow.shim_getHbar().getHeight(), 0.0);
assertEquals(flow.shim_getVbar().getLayoutX(), flow.getWidth() - flow.shim_getVbar().getWidth(), 0.0);
assertEquals(flow.shim_getVbar().getWidth(), flow.get_corner().getWidth(), 0.0);
assertEquals(flow.shim_getHbar().getHeight(), flow.get_corner().getHeight(), 0.0);
assertEquals(flow.shim_getHbar().getWidth(), flow.getWidth() - flow.get_corner().getWidth(), 0.0);
assertEquals(flow.shim_getVbar().getHeight(), flow.getHeight() - flow.get_corner().getHeight(), 0.0);
assertEquals(flow.get_corner().getLayoutX(), flow.getWidth() - flow.get_corner().getWidth(), 0.0);
assertEquals(flow.get_corner().getLayoutY(), flow.getHeight() - flow.get_corner().getHeight(), 0.0);
assertMinimalNumberOfCellsAreUsed(flow);
flow.setVertical(false);
flow.resize(300, 50);
pulse();
assertTrue("The hbar should have been visible", flow.shim_getHbar().isVisible());
assertTrue("The vbar should have been visible", flow.shim_getVbar().isVisible());
assertTrue("The corner should have been visible", flow.get_corner().isVisible());
assertEquals(flow.getWidth(), flow.get_clipView_getWidth() + flow.shim_getVbar().getWidth(), 0.0);
assertEquals(flow.getHeight(), flow.get_clipView_getHeight() + flow.shim_getHbar().getHeight(), 0.0);
assertEquals(flow.shim_getVbar().getLayoutX(), flow.getWidth() - flow.shim_getVbar().getWidth(), 0.0);
assertEquals(flow.shim_getVbar().getWidth(), flow.get_corner().getWidth(), 0.0);
assertEquals(flow.shim_getHbar().getHeight(), flow.get_corner().getHeight(), 0.0);
assertEquals(flow.shim_getHbar().getWidth(), flow.getWidth() - flow.get_corner().getWidth(), 0.0);
assertEquals(flow.shim_getVbar().getHeight(), flow.getHeight() - flow.get_corner().getHeight(), 0.0);
assertEquals(flow.get_corner().getLayoutX(), flow.getWidth() - flow.get_corner().getWidth(), 0.0);
assertEquals(flow.get_corner().getLayoutY(), flow.getHeight() - flow.get_corner().getHeight(), 0.0);
assertMinimalNumberOfCellsAreUsed(flow);
}
@Test public void testGeneralLayout_VerticalChangeResultsInNeedsLayout() {
assertFalse(flow.isNeedsLayout());
flow.setVertical(false);
assertTrue(flow.isNeedsLayout());
}
@Test public void testGeneralLayout_NonVirtualScrollBarRange() {
flow.resize(50, flow.getHeight());
pulse();
assertEquals(0, flow.shim_getHbar().getMin(), 0.0);
assertEquals(flow.shim_getMaxPrefBreadth() - flow.get_clipView_getWidth(), flow.shim_getHbar().getMax(), 0.0);
assertEquals((flow.get_clipView_getWidth()/flow.shim_getMaxPrefBreadth()) * flow.shim_getHbar().getMax(), flow.shim_getHbar().getVisibleAmount(), 0.0);
flow.setPosition(.28f);
pulse();
assertEquals(0, flow.shim_getHbar().getMin(), 0.0);
assertEquals(flow.shim_getMaxPrefBreadth() - flow.get_clipView_getWidth(), flow.shim_getHbar().getMax(), 0.0);
assertEquals((flow.get_clipView_getWidth()/flow.shim_getMaxPrefBreadth()) * flow.shim_getHbar().getMax(), flow.shim_getHbar().getVisibleAmount(), 0.0);
flow.setVertical(false);
flow.setPosition(0);
flow.resize(300, 50);
pulse();
assertEquals(0, flow.shim_getVbar().getMin(), 0.0);
assertEquals(flow.shim_getMaxPrefBreadth() - flow.get_clipView_getHeight(), flow.shim_getVbar().getMax(), 0.0);
assertEquals((flow.get_clipView_getHeight()/flow.shim_getMaxPrefBreadth()) * flow.shim_getVbar().getMax(), flow.shim_getVbar().getVisibleAmount(), 0.0);
flow.setPosition(.28);
pulse();
assertEquals(0, flow.shim_getVbar().getMin(), 0.0);
assertEquals(flow.shim_getMaxPrefBreadth() - flow.get_clipView_getHeight(), flow.shim_getVbar().getMax(), 0.0);
assertEquals((flow.get_clipView_getHeight()/flow.shim_getMaxPrefBreadth()) * flow.shim_getVbar().getMax(), flow.shim_getVbar().getVisibleAmount(), 0.0);
}
@Test public void testGeneralLayout_maxPrefBreadth() {
assertEquals(100, flow.shim_getMaxPrefBreadth(), 0.0);
}
@Ignore
@Test public void testGeneralLayout_maxPrefBreadthUpdatedWhenEncounterLargerPref() {
flow.setPosition(.28);
pulse();
assertEquals(200, flow.shim_getMaxPrefBreadth(), 0.0);
}
@Ignore
@Test public void testGeneralLayout_maxPrefBreadthRemainsSameWhenEncounterSmallerPref() {
flow.setPosition(.28);
pulse();
flow.setPosition(.8);
pulse();
assertEquals(200, flow.shim_getMaxPrefBreadth(), 0.0);
}
@Test public void testGeneralLayout_VerticalChangeClearsmaxPrefBreadth() {
flow.setVertical(false);
assertEquals(-1, flow.shim_getMaxPrefBreadth(), 0.0);
}
@Ignore
@Test public void testGeneralLayout_maxPrefBreadthUnaffectedByCellCountChanges() {
flow.setCellCount(10);
pulse();
assertEquals(100, flow.shim_getMaxPrefBreadth(), 0.0);
flow.setCellCount(100);
pulse();
flow.setPosition(.28);
pulse();
assertEquals(200, flow.shim_getMaxPrefBreadth(), 0.0);
flow.setCellCount(10);
pulse();
assertEquals(200, flow.shim_getMaxPrefBreadth(), 0.0);
}
@Test public void testGeneralLayout_maxPrefBreadthScrollBarValueInteraction() {
flow.resize(50, flow.getHeight());
flow.shim_getHbar().setValue(30);
pulse();
flow.setPosition(.28);
pulse();
flow.setPosition(0);
flow.setVertical(false);
flow.setVertical(true);
pulse();
assertEquals(100, flow.shim_getMaxPrefBreadth(), 0.0);
flow.shim_getHbar().setValue(flow.shim_getHbar().getMax());
flow.setPosition(.28);
pulse();
assertEquals(flow.shim_getHbar().getMax(), flow.shim_getHbar().getValue(), 0.0);
flow.setVertical(false);
flow.setPosition(0);
flow.shim_getHbar().setValue(0);
flow.resize(300, 50);
pulse();
flow.shim_getVbar().setValue(30);
pulse();
flow.setPosition(.28);
pulse();
assertEquals(30, flow.shim_getVbar().getValue(), 0.0);
flow.setPosition(0);
flow.setVertical(true);
flow.setVertical(false);
pulse();
assertEquals(100, flow.shim_getMaxPrefBreadth(), 0.0);
flow.shim_getVbar().setValue(flow.shim_getVbar().getMax());
flow.setPosition(.28);
pulse();
assertEquals(flow.shim_getVbar().getMax(), flow.shim_getVbar().getValue(), 0.0);
}
@Test public void testGeneralLayout_ScrollToEndOfVirtual_BarStillVisible() {
assertTrue("The vbar was expected to be visible", flow.shim_getVbar().isVisible());
flow.setPosition(1);
pulse();
assertTrue("The vbar was expected to be visible", flow.shim_getVbar().isVisible());
flow.setPosition(0);
flow.setVertical(false);
pulse();
assertTrue("The hbar was expected to be visible", flow.shim_getHbar().isVisible());
flow.setPosition(1);
pulse();
assertTrue("The hbar was expected to be visible", flow.shim_getHbar().isVisible());
}
@Test public void testCellLayout_NotAllCellsAreCreated() {
assertTrue("All of the cells were created", VirtualFlowShim.cells_size(flow.cells) < flow.getCellCount());
assertMinimalNumberOfCellsAreUsed(flow);
}
@Test public void testCellLayout_CellSizes_AfterLayout() {
double offset = 0.0;
for (int i = 0; i < VirtualFlowShim.cells_size(flow.cells); i++) {
IndexedCell cell = VirtualFlowShim.<IndexedCell>cells_get(flow.cells, i);
assertEquals(25, cell.getHeight(), 0.0);
assertEquals(offset, cell.getLayoutY(), 0.0);
offset += cell.getHeight();
}
offset = 0.0;
flow.setVertical(false);
pulse();
for (int i = 0; i < VirtualFlowShim.cells_size(flow.cells); i++) {
IndexedCell cell = VirtualFlowShim.<IndexedCell>cells_get(flow.cells, i);
assertEquals(25, cell.getWidth(), 0.0);
assertEquals(offset, cell.getLayoutX(), 0.0);
offset += cell.getWidth();
}
}
@Test public void testCellLayout_ViewportWiderThanmaxPrefBreadth() {
double expected = flow.get_clipView_getWidth();
for (int i = 0; i < VirtualFlowShim.cells_size(flow.cells); i++) {
IndexedCell cell = VirtualFlowShim.<IndexedCell>cells_get(flow.cells, i);
assertEquals(expected, cell.getWidth(), 0.0);
}
flow.setVertical(false);
pulse();
expected = flow.get_clipView_getHeight();
for (int i = 0; i < VirtualFlowShim.cells_size(flow.cells); i++) {
IndexedCell cell = VirtualFlowShim.<IndexedCell>cells_get(flow.cells, i);
assertEquals(expected, cell.getHeight(), 0.0);
}
}
@Test public void testCellLayout_ViewportShorterThanmaxPrefBreadth() {
flow.resize(50, flow.getHeight());
pulse();
assertEquals(100, flow.shim_getMaxPrefBreadth(), 0.0);
for (int i = 0; i < VirtualFlowShim.cells_size(flow.cells); i++) {
IndexedCell cell = VirtualFlowShim.<IndexedCell>cells_get(flow.cells, i);
assertEquals(flow.shim_getMaxPrefBreadth(), cell.getWidth(), 0.0);
}
flow.setVertical(false);
flow.resize(flow.getWidth(), 50);
pulse();
assertEquals(100, flow.shim_getMaxPrefBreadth(), 0.0);
for (int i = 0; i < VirtualFlowShim.cells_size(flow.cells); i++) {
IndexedCell cell = VirtualFlowShim.<IndexedCell>cells_get(flow.cells, i);
assertEquals(flow.shim_getMaxPrefBreadth(), cell.getHeight(), 0.0);
}
}
@Ignore
@Test public void testCellLayout_ScrollingFindsCellWithLargemaxPrefBreadth() {
flow.resize(50, flow.getHeight());
flow.setPosition(.28);
pulse();
assertEquals(200, flow.shim_getMaxPrefBreadth(), 0.0);
for (int i = 0; i < VirtualFlowShim.cells_size(flow.cells); i++) {
IndexedCell cell = VirtualFlowShim.<IndexedCell>cells_get(flow.cells, i);
assertEquals(flow.shim_getMaxPrefBreadth(), cell.getWidth(), 0.0);
}
flow.setVertical(false);
flow.resize(flow.getWidth(), 50);
pulse();
flow.setPosition(.28);
pulse();
assertEquals(200, flow.shim_getMaxPrefBreadth(), 0.0);
for (int i = 0; i < VirtualFlowShim.cells_size(flow.cells); i++) {
IndexedCell cell = VirtualFlowShim.<IndexedCell>cells_get(flow.cells, i);
assertEquals(flow.shim_getMaxPrefBreadth(), cell.getHeight(), 0.0);
}
}
@Test public void testCellLayout_CellIndexes_FirstPage() {
for (int i = 0; i < VirtualFlowShim.cells_size(flow.cells); i++) {
assertEquals(i, VirtualFlowShim.<IndexedCell>cells_get(flow.cells, i).getIndex());
}
}
@Test public void testCellLayout_LayoutWithoutChangingThingsUsesCellsInSameOrderAsBefore() {
List<IndexedCell> cells = new LinkedList<IndexedCell>();
for (int i = 0; i < VirtualFlowShim.cells_size(flow.cells); i++) {
cells.add(VirtualFlowShim.<IndexedCell>cells_get(flow.cells, i));
}
assertMatch(cells, flow.cells);
flow.requestLayout();
pulse();
assertMatch(cells, flow.cells);
flow.setPosition(1);
pulse();
cells.clear();
for (int i = 0; i < VirtualFlowShim.cells_size(flow.cells); i++) {
cells.add(VirtualFlowShim.<IndexedCell>cells_get(flow.cells, i));
}
flow.requestLayout();
pulse();
assertMatch(cells, flow.cells);
}
@Test public void testCellLayout_BiasedCellAndLengthBar() {
flow.setCellFactory(param -> new CellStub(flow) {
@Override
protected double computeMinWidth(double height) {
return 0;
}
@Override
protected double computeMaxWidth(double height) {
return Double.MAX_VALUE;
}
@Override
protected double computePrefWidth(double height) {
return 200;
}
@Override
protected double computeMinHeight(double width) {
return 0;
}
@Override
protected double computeMaxHeight(double width) {
return Double.MAX_VALUE;
}
@Override
protected double computePrefHeight(double width) {
return getIndex() == 0 ? 100 - 5 * (Math.floorDiv((int) width - 200, 10)) : 100;
}
});
flow.setCellCount(3);
flow.recreateCells();
flow.shim_getVbar().setPrefWidth(20);
flow.requestLayout();
pulse();
assertEquals(300, VirtualFlowShim.<IndexedCell>cells_get(flow.cells, 0).getWidth(), 1e-100);
assertEquals(50, VirtualFlowShim.<IndexedCell>cells_get(flow.cells, 0).getHeight(), 1e-100);
flow.resize(200, 300);
flow.requestLayout();
pulse();
assertEquals(200, VirtualFlowShim.<IndexedCell>cells_get(flow.cells, 0).getWidth(), 1e-100);
assertEquals(100, VirtualFlowShim.<IndexedCell>cells_get(flow.cells, 0).getHeight(), 1e-100);
}
@Test public void testCellLifeCycle_CellsAreCreatedOnLayout() {
assertTrue("The cells didn't get created", VirtualFlowShim.cells_size(flow.cells) > 0);
}
@Test public void testCreateCellFunctionChangesResultInNeedsLayoutAndNoCellsAndNoAccumCell() {
assertFalse(flow.isNeedsLayout());
flow.getCellLength(49);
assertNotNull("Accum cell was null", flow.get_accumCell());
flow.setCellFactory(p -> new CellStub(flow));
assertTrue(flow.isNeedsLayout());
assertNull("accumCell didn't get cleared", flow.get_accumCell());
}
@Test public void test_getCellLength() {
assertEquals(100, flow.getCellCount());
for (int i = 0; i < 50; i++) {
if (i != 29) assertEquals(25, flow.getCellLength(i), 0.0);
}
flow.setVertical(false);
flow.requestLayout();
pulse();
assertEquals(100, flow.getCellCount());
for (int i = 0; i < 50; i++) {
if (i != 29) assertEquals("Bad index: " + i, 25, flow.getCellLength(i), 0.0);
}
}
@Test public void testInitialScrollEventActuallyScrolls() {
flow = new VirtualFlowShim();
flow.setVertical(true);
flow.setCellFactory(p -> new CellStub(flow) {
@Override
protected double computeMinWidth(double height) {
return computePrefWidth(height);
}
@Override
protected double computeMaxWidth(double height) {
return computePrefWidth(height);
}
@Override
protected double computePrefWidth(double height) {
return flow.isVertical() ? (c.getIndex() == 29 ? 200 : 100) : (c.getIndex() == 29 ? 100 : 25);
}
@Override
protected double computeMinHeight(double width) {
return computePrefHeight(width);
}
@Override
protected double computeMaxHeight(double width) {
return computePrefHeight(width);
}
@Override
protected double computePrefHeight(double width) {
return flow.isVertical() ? (c.getIndex() == 29 ? 100 : 25) : (c.getIndex() == 29 ? 200 : 100);
}
});
flow.setCellCount(100);
flow.resize(300, 300);
pulse();
double originalValue = flow.getPosition();
Event.fireEvent(flow,
new ScrollEvent(ScrollEvent.SCROLL,
0.0, -10.0, 0.0, -10.0,
false, false, false, false, true, false,
0, 0,
0, 0,
ScrollEvent.HorizontalTextScrollUnits.NONE, 0.0,
ScrollEvent.VerticalTextScrollUnits.LINES, -1.0,
0, null));
assertTrue(originalValue != flow.getPosition());
}
@Test public void test_RT_36507() {
flow = new VirtualFlowShim();
flow.setVertical(true);
flow.setCellFactory(p -> new CellStub(flow) {
@Override
protected double computeMaxHeight(double width) {
return 0;
}
@Override
protected double computePrefHeight(double width) {
return 0;
}
@Override
protected double computeMinHeight(double width) {
return 0;
}
});
flow.setCellCount(10);
flow.setViewportLength(100);
flow.addLeadingCells(1, 0);
flow.sheetChildren.addListener((InvalidationListener) (o) -> {
int count = ((List) o).size();
assertTrue(Integer.toString(count), count <= 100);
});
flow.addTrailingCells(true);
}
private int rt36556_instanceCount;
@Test public void test_rt36556() {
rt36556_instanceCount = 0;
flow = new VirtualFlowShim();
flow.setVertical(true);
flow.setCellFactory(p -> {
rt36556_instanceCount++;
return new CellStub(flow);
});
flow.setCellCount(100);
flow.resize(300, 300);
pulse();
final int cellCountAtStart = rt36556_instanceCount;
flow.scrollPixels(10000);
pulse();
assertEquals(cellCountAtStart, rt36556_instanceCount);
assertNull(flow.getVisibleCell(0));
assertMinimalNumberOfCellsAreUsed(flow);
}
@Test public void test_rt36556_scrollto() {
rt36556_instanceCount = 0;
flow = new VirtualFlowShim();
flow.setVertical(true);
flow.setCellFactory(p -> {
rt36556_instanceCount++;
return new CellStub(flow);
});
flow.setCellCount(100);
flow.resize(300, 300);
pulse();
final int cellCountAtStart = rt36556_instanceCount;
flow.scrollTo(80);
pulse();
assertEquals(cellCountAtStart, rt36556_instanceCount);
assertNull(flow.getVisibleCell(0));
assertMinimalNumberOfCellsAreUsed(flow);
}
@Test public void test_RT39035() {
flow.scrollPixels(250);
pulse();
flow.scrollPixels(500);
pulse();
assertTrue(flow.getPosition() < 1.0);
assertMinimalNumberOfCellsAreUsed(flow);
}
@Test public void test_RT37421() {
flow.setPosition(0.98);
pulse();
flow.scrollPixels(100);
pulse();
assertEquals(1.0, flow.getPosition(), 0.0);
assertMinimalNumberOfCellsAreUsed(flow);
}
@Test public void test_RT39568() {
flow.shim_getHbar().setPrefHeight(16);
flow.resize(50, flow.getHeight());
flow.setPosition(1);
pulse();
assertTrue("The hbar should have been visible", flow.shim_getHbar().isVisible());
assertMinimalNumberOfCellsAreUsed(flow);
assertEquals(flow.getViewportLength()-25.0, VirtualFlowShim.<IndexedCell>cells_getLast(flow.cells).getLayoutY(), 0.0);
}
private void assertLastCellInsideViewport(boolean vertical) {
flow.setVertical(vertical);
flow.resize(400, 400);
int total = 10000;
flow.setCellCount(total);
pulse();
int count = 9000;
flow.setPosition(0d);
pulse();
flow.setPosition(((double)count) / total);
pulse();
for (int i = 0; i < 500; i++) {
count++;
flow.scrollTo(count);
pulse();
}
IndexedCell vc = flow.getCell(count);
double cellPosition = flow.getCellPosition(vc);
double cellLength = flow.getCellLength(count);
double viewportLength = flow.getViewportLength();
assertEquals("Last cell must end on viewport size", viewportLength, (cellPosition + cellLength), 0.1);
}
@Test
public void testScrollToTopOfLastLargeCell() {
double flowHeight = 150;
int cellCount = 2;
flow = new VirtualFlowShim<>();
flow.setCellFactory(p -> new CellStub(flow) {
@Override
protected double computePrefHeight(double width) {
return getIndex() == cellCount -1 ? 200 : 100;
}
@Override
protected double computeMinHeight(double width) {
return computePrefHeight(width);
}
@Override
protected double computeMaxHeight(double width) {
return computePrefHeight(width);
}
});
flow.setVertical(true);
flow.resize(50,flowHeight);
flow.setCellCount(cellCount);
pulse();
flow.scrollToTop(cellCount - 1);
pulse();
IndexedCell<?> cell = flow.getCell(cellCount - 1);
double cellPosition = flow.getCellPosition(cell);
assertEquals("Last cell must be aligned to top of the viewport", 0, cellPosition, 0.1);
}
@Test
public void testImmediateScrollTo() {
flow.setCellCount(100);
flow.scrollTo(90);
pulse();
IndexedCell vc = flow.getVisibleCell(90);
assertNotNull(vc);
}
@Test
public void testScrollOneCell() {
assertLastCellInsideViewport(true);
}
@Test
public void testScrollOneCellHorizontal() {
assertLastCellInsideViewport(false);
}
@Test
public void testPositionCellRemainsConstant() {
flow.setVertical(true);
flow.setCellCount(20);
flow.resize(300, 300);
flow.scrollPixels(10);
pulse();
IndexedCell vc = flow.getCell(0);
double cellPosition = flow.getCellPosition(vc);
assertEquals("Wrong first cell position", -10d, cellPosition, 0d);
for (int i = 1; i < 10; i++) {
flow.setCellCount(20 + i);
pulse();
vc = flow.getCell(0);
cellPosition = flow.getCellPosition(vc);
assertEquals("Wrong first cell position after inserting " + i + " cells", -10d, cellPosition, 0d);
}
}
@Test
public void testSheetChildrenRemainsConstant() {
flow.setVertical(true);
flow.setCellCount(20);
flow.resize(300, 300);
pulse();
int sheetChildrenSize = flow.sheetChildren.size();
assertEquals("Wrong number of sheet children", 12, sheetChildrenSize);
for (int i = 1; i < 50; i++) {
flow.setCellCount(20 + i);
pulse();
sheetChildrenSize = flow.sheetChildren.size();
assertEquals("Wrong number of sheet children after inserting " + i + " items", 12, sheetChildrenSize);
}
for (int i = 1; i < 50; i++) {
flow.setCellCount(70 - i);
pulse();
sheetChildrenSize = flow.sheetChildren.size();
assertEquals("Wrong number of sheet children after removing " + i + " items", 12, sheetChildrenSize);
}
flow.setCellCount(0);
pulse();
sheetChildrenSize = flow.sheetChildren.size();
assertEquals("Wrong number of sheet children after removing all items", 12, sheetChildrenSize);
}
private ArrayLinkedListShim<GraphicalCellStub> circlelist = new ArrayLinkedListShim<GraphicalCellStub>();
private VirtualFlowShim createCircleFlow() {
VirtualFlowShim<IndexedCell> circleFlow;
circleFlow = new VirtualFlowShim();
circleFlow.setVertical(true);
circleFlow.setCellFactory(p -> new GraphicalCellStub() {
@Override
protected double computeMinWidth(double height) {
return computePrefWidth(height);
}
@Override
protected double computeMaxWidth(double height) {
return computePrefWidth(height);
}
@Override
protected double computePrefWidth(double height) {
return super.computePrefWidth(height);
}
@Override
protected double computeMinHeight(double width) {
return computePrefHeight(width);
}
@Override
protected double computeMaxHeight(double width) {
return computePrefHeight(width);
}
});
circleFlow.setCellCount(7);
circleFlow.resize(300, 300);
circleFlow.layout();
circleFlow.layout();
return circleFlow;
}
@Test public void testReverseOrder() {
double orig = flow.getPosition();
flow.scrollPixels(10);
double pos = flow.getPosition();
assertFalse("Moving in positive direction should not decrease position", pos < orig);
flow.scrollPixels(-50);
double neg = flow.getPosition();
assertFalse("Moving in negative direction should not increase position", neg > pos);
}
@Test public void testReverseOrderForCircleFlow() {
VirtualFlowShim vf = createCircleFlow();
double orig = vf.getPosition();
vf.scrollPixels(10);
double pos = vf.getPosition();
assertFalse("Moving in positive direction should not decrease position", pos < orig);
vf.scrollPixels(-50);
double neg = vf.getPosition();
assertFalse("Moving in negative direction should not increase position", neg > pos);
}
@Test public void testGradualMoveForCircleFlow() {
VirtualFlowShim vf = createCircleFlow();
vf.resize(600,400);
ScrollBar sb = vf.shim_getVbar();
double s0 = sb.getLayoutY();
double s1 = s0;
double position = vf.getPosition();
double newPosition = 0d;
double delta = 0;
double newDelta = 0;
vf.layout();
for (int i = 0; i < 50; i++) {
vf.scrollPixels(10);
vf.layout();
newPosition = vf.getPosition();
s1 = sb.getLayoutY();
newDelta = newPosition - position;
if (i > 0) {
double diff = Math.abs((newDelta-delta)/newDelta);
assertTrue("Too much variation while scrolling (from "+s0+" to "+s1+")", diff < 0.1);
}
assertFalse("Thumb moving in the wrong direction at index ", s1 < s0);
s0 = s1;
delta = newDelta;
position = newPosition;
}
}
@Test public void testAccumCellInvisible() {
VirtualFlowShim vf = createCircleFlow();
Node cell = vf.get_accumCell();
if (cell != null) assertFalse(cell.isVisible());
vf.resize(600,400);
for (int i = 0; i < 50; i++) {
vf.scrollPixels(1);
cell = vf.get_accumCell();
if (cell != null) assertFalse(cell.isVisible());
}
}
@Test public void testScrollBarClipSyncWhileInvisibleOrNoScene() {
flow.setCellCount(3);
flow.resize(50, flow.getHeight());
pulse();
flow.setVisible(true);
Scene scene = new Scene(flow);
assertEquals(flow.shim_getHbar().getValue(), flow.get_clipView_getX(), 0);
flow.shim_getHbar().setValue(42);
assertEquals(flow.shim_getHbar().getValue(), flow.get_clipView_getX(), 0);
flow.setVisible(false);
flow.shim_getHbar().setValue(21);
flow.setVisible(true);
assertEquals(flow.shim_getHbar().getValue(), flow.get_clipView_getX(), 0);
scene.setRoot(new HBox());
assertEquals(null, flow.getScene());
flow.shim_getHbar().setValue(10);
scene.setRoot(flow);
assertEquals(flow.shim_getHbar().getValue(), flow.get_clipView_getX(), 0);
}
}
class GraphicalCellStub extends IndexedCellShim<Node> {
static List<Circle> circleList = List.of(
new Circle(10),
new Circle(20),
new Circle(100),
new Circle(30),
new Circle(50),
new Circle(200),
new Circle(60)
);
private int idx = -1;
Node myItem = null;
public GraphicalCellStub() { init(); }
private void init() {
setSkin(new SkinStub<GraphicalCellStub>(this));
}
@Override
public void updateItem(Node item, boolean empty) {
super.updateItem(item, empty);
if (empty || item == null) {
setText(null);
setGraphic(null);
} else {
setGraphic(item);
}
}
@Override
public void updateIndex(int i) {
super.updateIndex(i);
if ((i > -1) && (circleList.size() > i)) {
this.idx = i;
updateItem(circleList.get(i), false);
} else {
updateItem(null, true);
}
}
@Override
protected double computePrefHeight(double width) {
double answer = super.computePrefHeight(width);
if ((idx > -1) && (idx < circleList.size())) {
answer = 2 * circleList.get(idx).getRadius() + 6;
}
return answer;
}
@Override
public String toString() {
return "GraphicCell with item = "+myItem+" at "+super.toString();
}
}
class CellStub extends IndexedCellShim {
String s;
public CellStub(VirtualFlowShim flow) { init(flow); }
public CellStub(VirtualFlowShim flow, String s) { init(flow); this.s = s; }
private void init(VirtualFlowShim flow) {
setSkin(new SkinStub<CellStub>(this));
updateItem(this, false);
}
@Override
public void updateIndex(int i) {
super.updateIndex(i);
s = "Item " + getIndex();
}
}
