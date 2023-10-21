package javafx.scene.control.skin;
import java.util.List;
import com.sun.javafx.scene.control.VirtualScrollBar;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Skin;
import javafx.scene.layout.StackPane;
public class VirtualFlowShim<T extends IndexedCell> extends VirtualFlow<T> {
public final ArrayLinkedList<T> cells = super.cells;
public final ObservableList<Node> sheetChildren = super.sheetChildren;
@Override
public double getViewportLength() {
return super.getViewportLength();
}
@Override
public void setViewportLength(double value) {
super.setViewportLength(value);
}
@Override
public double getCellLength(int index) {
return super.getCellLength(index);
}
@Override
public double getCellPosition(T cell) {
return super.getCellPosition(cell);
}
@Override
public void recreateCells() {
super.recreateCells();
}
public double shim_getMaxPrefBreadth() {
return super.getMaxPrefBreadth();
}
public ScrollBar shim_getHbar() {
return super.getHbar();
}
public ScrollBar shim_getVbar() {
return super.getVbar();
}
public ClippedContainer get_clipView() {
return super.clipView;
}
public double get_clipView_getWidth() {
return super.clipView.getWidth();
}
public double get_clipView_getHeight() {
return super.clipView.getHeight();
}
public double get_clipView_getX() {
return - super.clipView.getLayoutX();
}
public StackPane get_corner() {
return super.corner;
}
public T get_accumCell() {
return super.accumCell;
}
@Override
public boolean addTrailingCells(boolean fillEmptyCells) {
return super.addTrailingCells(fillEmptyCells);
}
@Override
public void addLeadingCells(int currentIndex, double startOffset) {
super.addLeadingCells(currentIndex, startOffset);
}
public static <T extends IndexedCell<?>> VirtualFlow<T> getVirtualFlow(Skin<?> skin) {
return ((VirtualContainerBase<?, T>) skin).getVirtualFlow();
}
public static <T extends IndexedCell<?>> List<T> getCells(VirtualFlow<T> flow) {
return flow.getCells();
}
public static ScrollBar getVBar(VirtualFlow<?> flow) {
return flow.getVbar();
}
public static ScrollBar getHBar(VirtualFlow<?> flow) {
return flow.getHbar();
}
public static <T> T cells_getFirst(VirtualFlow.ArrayLinkedList<T> list) {
return list.getFirst();
}
public static <T> T cells_getLast(VirtualFlow.ArrayLinkedList<T> list) {
return list.getLast();
}
public static <T> T cells_get(VirtualFlow.ArrayLinkedList<T> list, int i) {
return list.get(i);
}
public static int cells_size(VirtualFlow.ArrayLinkedList<?> list) {
return list.size();
}
public static class ArrayLinkedListShim<T> extends VirtualFlow.ArrayLinkedList<T> {
@Override
public T getFirst() {
return super.getFirst();
}
@Override
public T getLast() {
return super.getLast();
}
@Override
public void addFirst(T cell) {
super.addFirst(cell);
}
@Override
public void addLast(T cell) {
super.addLast(cell);
}
@Override
public int size() {
return super.size();
}
@Override
public boolean isEmpty() {
return super.isEmpty();
}
@Override
public T get(int index) {
return super.get(index);
}
@Override
public void clear() {
super.clear();
}
@Override
public T removeFirst() {
return super.removeFirst();
}
@Override
public T removeLast() {
return super.removeLast();
}
@Override
public T remove(int index) {
return super.remove(index);
}
}
}
