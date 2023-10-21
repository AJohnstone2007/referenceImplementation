package javafx.scene.control.skin;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ScrollToEvent;
import javafx.scene.control.SkinBase;
public abstract class VirtualContainerBase<C extends Control, I extends IndexedCell> extends SkinBase<C> {
private boolean itemCountDirty;
private final VirtualFlow<I> flow;
private EventHandler<? super ScrollToEvent<Integer>> scrollToEventHandler;
public VirtualContainerBase(final C control) {
super(control);
flow = createVirtualFlow();
scrollToEventHandler = event -> {
if (itemCountDirty) {
updateItemCount();
itemCountDirty = false;
}
flow.scrollToTop(event.getScrollTarget());
};
control.addEventHandler(ScrollToEvent.scrollToTopIndex(), scrollToEventHandler);
}
protected abstract int getItemCount();
protected abstract void updateItemCount();
protected VirtualFlow<I> createVirtualFlow() {
return new VirtualFlow<>();
}
@Override
public void dispose() {
if (getSkinnable() == null) return;
getSkinnable().removeEventHandler(ScrollToEvent.scrollToTopIndex(), scrollToEventHandler);
super.dispose();
}
protected final VirtualFlow<I> getVirtualFlow() {
return flow;
}
protected final void markItemCountDirty() {
itemCountDirty = true;
}
@Override protected void layoutChildren(double x, double y, double w, double h) {
checkState();
}
double getMaxCellWidth(int rowsToCount) {
return snappedLeftInset() + flow.getMaxCellWidth(rowsToCount) + snappedRightInset();
}
double getVirtualFlowPreferredHeight(int rows) {
double height = 1.0;
for (int i = 0; i < rows && i < getItemCount(); i++) {
height += flow.getCellLength(i);
}
return height + snappedTopInset() + snappedBottomInset();
}
void checkState() {
if (itemCountDirty) {
updateItemCount();
itemCountDirty = false;
}
}
void requestRebuildCells() {
flow.rebuildCells();
}
}
