package test.javafx.scene.control.skin;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.skin.VirtualFlowShim;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
public class VirtualFlowSubClassTest {
private VirtualFlowShim.ArrayLinkedListShim<CellStub> list;
private CellStub a;
private CellStub b;
private CellStub c;
private SubVirtualFlow<IndexedCell> flow;
@Before public void setUp() {
list = new VirtualFlowShim.ArrayLinkedListShim<CellStub>();
a = new CellStub(flow, "A");
b = new CellStub(flow, "B");
c = new CellStub(flow, "C");
flow = new SubVirtualFlow();
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
@Test public void test_getFirstVisibleCellWithinViewport() {
assertEquals(flow.getFirstVisibleCell(), flow.getFirstVisibleCellWithinViewport());
assertEquals(0, flow.getFirstVisibleCellWithinViewport().getIndex());
flow.scrollPixels(10);
assertFalse(flow.getFirstVisibleCell().equals(flow.getFirstVisibleCellWithinViewport()));
assertEquals(1, flow.getFirstVisibleCellWithinViewport().getIndex());
}
@Test public void test_getLastVisibleCellWithinViewport() {
assertEquals(flow.getLastVisibleCell(), flow.getLastVisibleCellWithinViewport());
int lastIndex = flow.getLastVisibleCell().getIndex();
assertEquals(lastIndex, flow.getLastVisibleCellWithinViewport().getIndex());
flow.scrollPixels(10);
assertFalse(flow.getLastVisibleCell().equals(flow.getLastVisibleCellWithinViewport()));
assertEquals(lastIndex, flow.getLastVisibleCellWithinViewport().getIndex());
assertEquals(lastIndex + 1, flow.getLastVisibleCell().getIndex());
}
class SubVirtualFlow<T extends IndexedCell> extends VirtualFlowShim {
@Override
public IndexedCell getLastVisibleCellWithinViewport() {
return super.getLastVisibleCellWithinViewport();
}
@Override
public IndexedCell getFirstVisibleCellWithinViewport() {
return super.getFirstVisibleCellWithinViewport();
}
}
}
