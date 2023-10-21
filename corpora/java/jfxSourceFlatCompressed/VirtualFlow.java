package javafx.scene.control.skin;
import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.scene.control.Logging;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.control.VirtualScrollBar;
import com.sun.javafx.scene.control.skin.Utils;
import com.sun.javafx.scene.traversal.Algorithm;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import com.sun.javafx.scene.traversal.TraversalContext;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.AccessibleRole;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Cell;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import javafx.util.Duration;
import com.sun.javafx.logging.PlatformLogger;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
public class VirtualFlow<T extends IndexedCell> extends Region {
private static final int MIN_SCROLLING_LINES_PER_PAGE = 8;
private static final String NEW_CELL = "newcell";
private static final double GOLDEN_RATIO_MULTIPLIER = 0.618033987;
private static final int DEFAULT_IMPROVEMENT = 2;
private boolean touchDetected = false;
private boolean mouseDown = false;
double lastWidth = -1;
double lastHeight = -1;
int lastCellCount = 0;
boolean lastVertical;
double lastPosition;
double lastCellBreadth = -1;
double lastCellLength = -1;
final ArrayLinkedList<T> cells = new ArrayLinkedList<T>();
final ArrayLinkedList<T> pile = new ArrayLinkedList<T>();
T accumCell;
Group accumCellParent;
final Group sheet;
final ObservableList<Node> sheetChildren;
private VirtualScrollBar hbar = new VirtualScrollBar(this);
private VirtualScrollBar vbar = new VirtualScrollBar(this);
ClippedContainer clipView;
StackPane corner;
double absoluteOffset = 0d;
double estimatedSize = -1d;
private ArrayList<Double> itemSizeCache = new ArrayList<>();
private double lastX;
private double lastY;
private boolean isPanning = false;
private boolean fixedCellSizeEnabled = false;
private boolean needsReconfigureCells = false;
private boolean needsRecreateCells = false;
private boolean needsRebuildCells = false;
private boolean needsCellsLayout = false;
private boolean sizeChanged = false;
private final BitSet dirtyCells = new BitSet();
Timeline sbTouchTimeline;
KeyFrame sbTouchKF1;
KeyFrame sbTouchKF2;
private boolean needBreadthBar;
private boolean needLengthBar;
private boolean tempVisibility = false;
public VirtualFlow() {
getStyleClass().add("virtual-flow");
setId("virtual-flow");
sheet = new Group();
sheet.getStyleClass().add("sheet");
sheet.setAutoSizeChildren(false);
sheetChildren = sheet.getChildren();
clipView = new ClippedContainer(this);
clipView.setNode(sheet);
getChildren().add(clipView);
accumCellParent = new Group();
accumCellParent.setVisible(false);
getChildren().add(accumCellParent);
final EventDispatcher blockEventDispatcher = (event, tail) -> event;
final EventDispatcher oldHsbEventDispatcher = hbar.getEventDispatcher();
hbar.setEventDispatcher((event, tail) -> {
if (event.getEventType() == ScrollEvent.SCROLL &&
!((ScrollEvent)event).isDirect()) {
tail = tail.prepend(blockEventDispatcher);
tail = tail.prepend(oldHsbEventDispatcher);
return tail.dispatchEvent(event);
}
return oldHsbEventDispatcher.dispatchEvent(event, tail);
});
final EventDispatcher oldVsbEventDispatcher = vbar.getEventDispatcher();
vbar.setEventDispatcher((event, tail) -> {
if (event.getEventType() == ScrollEvent.SCROLL &&
!((ScrollEvent)event).isDirect()) {
tail = tail.prepend(blockEventDispatcher);
tail = tail.prepend(oldVsbEventDispatcher);
return tail.dispatchEvent(event);
}
return oldVsbEventDispatcher.dispatchEvent(event, tail);
});
setOnScroll(new EventHandler<ScrollEvent>() {
@Override public void handle(ScrollEvent event) {
if (Properties.IS_TOUCH_SUPPORTED) {
if (touchDetected == false && mouseDown == false ) {
startSBReleasedAnimation();
}
}
double virtualDelta = 0.0;
if (isVertical()) {
switch(event.getTextDeltaYUnits()) {
case PAGES:
virtualDelta = event.getTextDeltaY() * lastHeight;
break;
case LINES:
double lineSize;
if (fixedCellSizeEnabled) {
lineSize = getFixedCellSize();
} else {
T lastCell = cells.getLast();
lineSize =
(getCellPosition(lastCell)
+ getCellLength(lastCell)
- getCellPosition(cells.getFirst()))
/ cells.size();
}
if (lastHeight / lineSize < MIN_SCROLLING_LINES_PER_PAGE) {
lineSize = lastHeight / MIN_SCROLLING_LINES_PER_PAGE;
}
virtualDelta = event.getTextDeltaY() * lineSize;
break;
case NONE:
virtualDelta = event.getDeltaY();
}
} else {
switch(event.getTextDeltaXUnits()) {
case CHARACTERS:
case NONE:
double dx = event.getDeltaX();
double dy = event.getDeltaY();
virtualDelta = (Math.abs(dx) > Math.abs(dy) ? dx : dy);
}
}
if (virtualDelta != 0.0) {
double result = scrollPixels(-virtualDelta);
if (result != 0.0) {
event.consume();
}
}
ScrollBar nonVirtualBar = isVertical() ? hbar : vbar;
if (needBreadthBar) {
double nonVirtualDelta = isVertical() ? event.getDeltaX() : event.getDeltaY();
if (nonVirtualDelta != 0.0) {
double newValue = nonVirtualBar.getValue() - nonVirtualDelta;
if (newValue < nonVirtualBar.getMin()) {
nonVirtualBar.setValue(nonVirtualBar.getMin());
} else if (newValue > nonVirtualBar.getMax()) {
nonVirtualBar.setValue(nonVirtualBar.getMax());
} else {
nonVirtualBar.setValue(newValue);
}
event.consume();
}
}
}
});
addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
@Override
public void handle(MouseEvent e) {
mouseDown = true;
if (Properties.IS_TOUCH_SUPPORTED) {
scrollBarOn();
}
if (isFocusTraversable()) {
boolean doFocusRequest = true;
Node focusOwner = getScene().getFocusOwner();
if (focusOwner != null) {
Parent parent = focusOwner.getParent();
while (parent != null) {
if (parent.equals(VirtualFlow.this)) {
doFocusRequest = false;
break;
}
parent = parent.getParent();
}
}
if (doFocusRequest) {
requestFocus();
}
}
lastX = e.getX();
lastY = e.getY();
isPanning = ! (vbar.getBoundsInParent().contains(e.getX(), e.getY())
|| hbar.getBoundsInParent().contains(e.getX(), e.getY()));
}
});
addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
mouseDown = false;
if (Properties.IS_TOUCH_SUPPORTED) {
startSBReleasedAnimation();
}
});
addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
if (Properties.IS_TOUCH_SUPPORTED) {
scrollBarOn();
}
if (! isPanning || ! isPannable()) return;
double xDelta = lastX - e.getX();
double yDelta = lastY - e.getY();
double virtualDelta = isVertical() ? yDelta : xDelta;
double actual = scrollPixels(virtualDelta);
if (actual != 0) {
if (isVertical()) lastY = e.getY();
else lastX = e.getX();
}
double nonVirtualDelta = isVertical() ? xDelta : yDelta;
ScrollBar nonVirtualBar = isVertical() ? hbar : vbar;
if (nonVirtualBar.isVisible()) {
double newValue = nonVirtualBar.getValue() + nonVirtualDelta;
if (newValue < nonVirtualBar.getMin()) {
nonVirtualBar.setValue(nonVirtualBar.getMin());
} else if (newValue > nonVirtualBar.getMax()) {
nonVirtualBar.setValue(nonVirtualBar.getMax());
} else {
nonVirtualBar.setValue(newValue);
if (isVertical()) lastX = e.getX();
else lastY = e.getY();
}
}
});
vbar.setOrientation(Orientation.VERTICAL);
vbar.addEventHandler(MouseEvent.ANY, event -> {
event.consume();
});
getChildren().add(vbar);
hbar.setOrientation(Orientation.HORIZONTAL);
hbar.addEventHandler(MouseEvent.ANY, event -> {
event.consume();
});
getChildren().add(hbar);
corner = new StackPane();
corner.getStyleClass().setAll("corner");
getChildren().add(corner);
InvalidationListener listenerX = valueModel -> {
updateHbar();
};
verticalProperty().addListener(listenerX);
hbar.valueProperty().addListener(listenerX);
hbar.visibleProperty().addListener(listenerX);
visibleProperty().addListener(listenerX);
sceneProperty().addListener(listenerX);
ChangeListener<Number> listenerY = (ov, t, t1) -> {
clipView.setClipY(isVertical() ? 0 : vbar.getValue());
};
vbar.valueProperty().addListener(listenerY);
super.heightProperty().addListener((observable, oldHeight, newHeight) -> {
if (oldHeight.doubleValue() == 0 && newHeight.doubleValue() > 0) {
recreateCells();
}
});
setOnTouchPressed(e -> {
touchDetected = true;
scrollBarOn();
});
setOnTouchReleased(e -> {
touchDetected = false;
startSBReleasedAnimation();
});
ParentHelper.setTraversalEngine(this, new ParentTraversalEngine(this, new Algorithm() {
Node selectNextAfterIndex(int index, TraversalContext context) {
T nextCell;
while ((nextCell = getVisibleCell(++index)) != null) {
if (nextCell.isFocusTraversable()) {
return nextCell;
}
Node n = context.selectFirstInParent(nextCell);
if (n != null) {
return n;
}
}
return null;
}
Node selectPreviousBeforeIndex(int index, TraversalContext context) {
T prevCell;
while ((prevCell = getVisibleCell(--index)) != null) {
Node prev = context.selectLastInParent(prevCell);
if (prev != null) {
return prev;
}
if (prevCell.isFocusTraversable()) {
return prevCell;
}
}
return null;
}
@Override
public Node select(Node owner, Direction dir, TraversalContext context) {
T cell;
if (cells.isEmpty()) return null;
if (cells.contains(owner)) {
cell = (T) owner;
} else {
cell = findOwnerCell(owner);
Node next = context.selectInSubtree(cell, owner, dir);
if (next != null) {
return next;
}
if (dir == Direction.NEXT) dir = Direction.NEXT_IN_LINE;
}
int cellIndex = cell.getIndex();
switch(dir) {
case PREVIOUS:
return selectPreviousBeforeIndex(cellIndex, context);
case NEXT:
Node n = context.selectFirstInParent(cell);
if (n != null) {
return n;
}
case NEXT_IN_LINE:
return selectNextAfterIndex(cellIndex, context);
}
return null;
}
private T findOwnerCell(Node owner) {
Parent p = owner.getParent();
while (!cells.contains(p)) {
p = p.getParent();
}
return (T)p;
}
@Override
public Node selectFirst(TraversalContext context) {
T firstCell = cells.getFirst();
if (firstCell == null) return null;
if (firstCell.isFocusTraversable()) return firstCell;
Node n = context.selectFirstInParent(firstCell);
if (n != null) {
return n;
}
return selectNextAfterIndex(firstCell.getIndex(), context);
}
@Override
public Node selectLast(TraversalContext context) {
T lastCell = cells.getLast();
if (lastCell == null) return null;
Node p = context.selectLastInParent(lastCell);
if (p != null) {
return p;
}
if (lastCell.isFocusTraversable()) return lastCell;
return selectPreviousBeforeIndex(lastCell.getIndex(), context);
}
}));
}
private BooleanProperty vertical;
public final void setVertical(boolean value) {
verticalProperty().set(value);
}
public final boolean isVertical() {
return vertical == null ? true : vertical.get();
}
public final BooleanProperty verticalProperty() {
if (vertical == null) {
vertical = new BooleanPropertyBase(true) {
@Override protected void invalidated() {
pile.clear();
sheetChildren.clear();
cells.clear();
lastWidth = lastHeight = -1;
setMaxPrefBreadth(-1);
setViewportBreadth(0);
setViewportLength(0);
lastPosition = 0;
hbar.setValue(0);
vbar.setValue(0);
setPosition(0.0f);
setNeedsLayout(true);
requestLayout();
}
@Override
public Object getBean() {
return VirtualFlow.this;
}
@Override
public String getName() {
return "vertical";
}
};
}
return vertical;
}
private BooleanProperty pannable = new SimpleBooleanProperty(this, "pannable", true);
public final boolean isPannable() { return pannable.get(); }
public final void setPannable(boolean value) { pannable.set(value); }
public final BooleanProperty pannableProperty() { return pannable; }
private IntegerProperty cellCount = new SimpleIntegerProperty(this, "cellCount", 0) {
private int oldCount = 0;
@Override protected void invalidated() {
int cellCount = get();
resetSizeEstimates();
recalculateEstimatedSize();
boolean countChanged = oldCount != cellCount;
oldCount = cellCount;
if (countChanged) {
VirtualScrollBar lengthBar = isVertical() ? vbar : hbar;
lengthBar.setMax(cellCount);
}
if (countChanged) {
layoutChildren();
Parent parent = getParent();
if (parent != null) parent.requestLayout();
adjustAbsoluteOffset();
}
}
};
public final int getCellCount() { return cellCount.get(); }
public final void setCellCount(int value) {
cellCount.set(value);
}
public final IntegerProperty cellCountProperty() { return cellCount; }
private DoubleProperty position = new SimpleDoubleProperty(this, "position") {
@Override public void setValue(Number v) {
super.setValue(com.sun.javafx.util.Utils.clamp(0, get(), 1));
}
@Override protected void invalidated() {
super.invalidated();
requestLayout();
}
};
public final double getPosition() { return position.get(); }
public final void setPosition(double value) {
position.set(value);
adjustAbsoluteOffset();
}
public final DoubleProperty positionProperty() { return position; }
private DoubleProperty fixedCellSize = new SimpleDoubleProperty(this, "fixedCellSize") {
@Override protected void invalidated() {
fixedCellSizeEnabled = get() > 0;
needsCellsLayout = true;
layoutChildren();
}
};
public final void setFixedCellSize(final double value) { fixedCellSize.set(value); }
public final double getFixedCellSize() { return fixedCellSize.get(); }
public final DoubleProperty fixedCellSizeProperty() { return fixedCellSize; }
private ObjectProperty<Callback<VirtualFlow<T>, T>> cellFactory;
public final void setCellFactory(Callback<VirtualFlow<T>, T> value) {
cellFactoryProperty().set(value);
}
public final Callback<VirtualFlow<T>, T> getCellFactory() {
return cellFactory == null ? null : cellFactory.get();
}
public final ObjectProperty<Callback<VirtualFlow<T>, T>> cellFactoryProperty() {
if (cellFactory == null) {
cellFactory = new SimpleObjectProperty<Callback<VirtualFlow<T>, T>>(this, "cellFactory") {
@Override protected void invalidated() {
if (get() != null) {
setNeedsLayout(true);
recreateCells();
if (getParent() != null) getParent().requestLayout();
}
if (accumCellParent != null) {
accumCellParent.getChildren().clear();
}
accumCell = null;
}
};
}
return cellFactory;
}
@Override public void requestLayout() {
super.requestLayout();
}
void adjustAbsoluteOffset() {
absoluteOffset = (estimatedSize - viewportLength) * getPosition();
}
void adjustPosition() {
if (viewportLength >= estimatedSize) {
setPosition(0.);
} else {
setPosition(absoluteOffset / (estimatedSize - viewportLength));
}
}
@Override protected void layoutChildren() {
double origAbsoluteOffset = absoluteOffset;
recalculateEstimatedSize();
if (lastCellCount != getCellCount()) {
absoluteOffset = origAbsoluteOffset;
adjustPosition();
} else {
adjustAbsoluteOffset();
}
if (needsRecreateCells) {
lastWidth = -1;
lastHeight = -1;
releaseCell(accumCell);
sheet.getChildren().clear();
for (int i = 0, max = cells.size(); i < max; i++) {
cells.get(i).updateIndex(-1);
}
cells.clear();
pile.clear();
releaseAllPrivateCells();
} else if (needsRebuildCells) {
lastWidth = -1;
lastHeight = -1;
releaseCell(accumCell);
for (int i = 0, max = cells.size(); i < max; i++) {
cells.get(i).updateIndex(-1);
}
addAllToPile();
releaseAllPrivateCells();
} else if (needsReconfigureCells) {
setMaxPrefBreadth(-1);
lastWidth = -1;
lastHeight = -1;
}
if (! dirtyCells.isEmpty()) {
int index;
final int cellsSize = cells.size();
while ((index = dirtyCells.nextSetBit(0)) != -1 && index < cellsSize) {
T cell = cells.get(index);
if (cell != null) {
cell.requestLayout();
}
dirtyCells.clear(index);
}
setMaxPrefBreadth(-1);
lastWidth = -1;
lastHeight = -1;
}
final boolean hasSizeChange = sizeChanged;
boolean recreatedOrRebuilt = needsRebuildCells || needsRecreateCells || sizeChanged;
needsRecreateCells = false;
needsReconfigureCells = false;
needsRebuildCells = false;
sizeChanged = false;
if (needsCellsLayout) {
for (int i = 0, max = cells.size(); i < max; i++) {
Cell<?> cell = cells.get(i);
if (cell != null) {
cell.requestLayout();
}
}
needsCellsLayout = false;
return;
}
final double width = getWidth();
final double height = getHeight();
final boolean isVertical = isVertical();
final double position = getPosition();
if (width <= 0 || height <= 0) {
addAllToPile();
lastWidth = width;
lastHeight = height;
hbar.setVisible(false);
vbar.setVisible(false);
corner.setVisible(false);
return;
}
boolean cellNeedsLayout = false;
boolean thumbNeedsLayout = false;
if (Properties.IS_TOUCH_SUPPORTED) {
if ((tempVisibility == true && (hbar.isVisible() == false || vbar.isVisible() == false)) ||
(tempVisibility == false && (hbar.isVisible() == true || vbar.isVisible() == true))) {
thumbNeedsLayout = true;
}
}
if (!cellNeedsLayout) {
for (int i = 0; i < cells.size(); i++) {
Cell<?> cell = cells.get(i);
cellNeedsLayout = cell.isNeedsLayout();
if (cellNeedsLayout) break;
}
}
final int cellCount = getCellCount();
final T firstCell = getFirstVisibleCell();
if (! cellNeedsLayout && !thumbNeedsLayout) {
boolean cellSizeChanged = false;
if (firstCell != null) {
double breadth = getCellBreadth(firstCell);
double length = getCellLength(firstCell);
cellSizeChanged = (breadth != lastCellBreadth) || (length != lastCellLength);
lastCellBreadth = breadth;
lastCellLength = length;
}
if (width == lastWidth &&
height == lastHeight &&
cellCount == lastCellCount &&
isVertical == lastVertical &&
position == lastPosition &&
! cellSizeChanged)
{
return;
}
}
boolean needTrailingCells = false;
boolean rebuild = cellNeedsLayout ||
isVertical != lastVertical ||
cells.isEmpty() ||
getMaxPrefBreadth() == -1 ||
position != lastPosition ||
cellCount != lastCellCount ||
hasSizeChange ||
(isVertical && height < lastHeight) || (! isVertical && width < lastWidth);
if (!rebuild) {
double maxPrefBreadth = getMaxPrefBreadth();
boolean foundMax = false;
for (int i = 0; i < cells.size(); ++i) {
double breadth = getCellBreadth(cells.get(i));
if (maxPrefBreadth == breadth) {
foundMax = true;
} else if (breadth > maxPrefBreadth) {
rebuild = true;
break;
}
}
if (!foundMax) {
rebuild = true;
}
}
if (! rebuild) {
if ((isVertical && height > lastHeight) || (! isVertical && width > lastWidth)) {
needTrailingCells = true;
}
}
initViewport();
int currentIndex = computeCurrentIndex();
if (lastCellCount != cellCount) {
if (position != 0 && position != 1 && (currentIndex >= cellCount)) {
setPosition(1.0f);
}
currentIndex = computeCurrentIndex();
}
if (rebuild) {
setMaxPrefBreadth(-1);
addAllToPile();
double offset = -computeViewportOffset(getPosition());
addLeadingCells(currentIndex, offset);
addTrailingCells(true);
} else if (needTrailingCells) {
addTrailingCells(true);
}
computeBarVisiblity();
recreatedOrRebuilt = recreatedOrRebuilt || rebuild;
updateScrollBarsAndCells(recreatedOrRebuilt);
lastWidth = getWidth();
lastHeight = getHeight();
lastCellCount = getCellCount();
lastVertical = isVertical();
lastPosition = getPosition();
recalculateEstimatedSize();
cleanPile();
}
@Override protected void setWidth(double value) {
if (value != lastWidth) {
super.setWidth(value);
sizeChanged = true;
setNeedsLayout(true);
requestLayout();
}
}
@Override protected void setHeight(double value) {
if (value != lastHeight) {
super.setHeight(value);
sizeChanged = true;
setNeedsLayout(true);
requestLayout();
}
}
protected T getAvailableCell(int prefIndex) {
T cell = null;
for (int i = 0, max = pile.size(); i < max; i++) {
T _cell = pile.get(i);
assert _cell != null;
if (getCellIndex(_cell) == prefIndex) {
cell = _cell;
pile.remove(i);
break;
}
}
if (cell == null && !pile.isEmpty()) {
cell = pile.removeLast();
}
if (cell == null) {
cell = getCellFactory().call(this);
cell.getProperties().put(NEW_CELL, null);
}
if (cell.getParent() == null) {
sheetChildren.add(cell);
}
return cell;
}
protected void addAllToPile() {
for (int i = 0, max = cells.size(); i < max; i++) {
addToPile(cells.removeFirst());
}
}
public T getVisibleCell(int index) {
if (cells.isEmpty()) return null;
T lastCell = cells.getLast();
int lastIndex = getCellIndex(lastCell);
if (index == lastIndex) return lastCell;
T firstCell = cells.getFirst();
int firstIndex = getCellIndex(firstCell);
if (index == firstIndex) return firstCell;
if (index > firstIndex && index < lastIndex) {
T cell = cells.get(index - firstIndex);
if (getCellIndex(cell) == index) return cell;
}
return null;
}
public T getLastVisibleCell() {
if (cells.isEmpty() || getViewportLength() <= 0) return null;
T cell;
for (int i = cells.size() - 1; i >= 0; i--) {
cell = cells.get(i);
if (! cell.isEmpty()) {
return cell;
}
}
return null;
}
public T getFirstVisibleCell() {
if (cells.isEmpty() || getViewportLength() <= 0) return null;
T cell = cells.getFirst();
return cell.isEmpty() ? null : cell;
}
public void scrollToTop(T firstCell) {
if (firstCell != null) {
scrollPixels(getCellPosition(firstCell));
}
}
public void scrollToBottom(T lastCell) {
if (lastCell != null) {
scrollPixels(getCellPosition(lastCell) + getCellLength(lastCell) - getViewportLength());
}
}
public void scrollTo(T cell) {
if (cell != null) {
final double start = getCellPosition(cell);
final double length = getCellLength(cell);
final double end = start + length;
final double viewportLength = getViewportLength();
if (start < 0) {
scrollPixels(start);
} else if (end > viewportLength) {
scrollPixels(end - viewportLength);
}
}
}
public void scrollTo(int index) {
T cell = getVisibleCell(index);
if (cell != null) {
scrollTo(cell);
} else {
if (tryScrollOneCell(index, true)) {
return;
} else if (tryScrollOneCell(index, false)) {
return;
}
adjustPositionToIndex(index);
addAllToPile();
requestLayout();
}
}
private boolean tryScrollOneCell(int targetIndex, boolean downOrRight) {
int indexDiff = downOrRight ? -1 : 1;
T targetCell = getVisibleCell(targetIndex + indexDiff);
if (targetCell != null) {
T cell = getAvailableCell(targetIndex);
setCellIndex(cell, targetIndex);
resizeCell(cell);
setMaxPrefBreadth(Math.max(getMaxPrefBreadth(), getCellBreadth(cell)));
cell.setVisible(true);
if (downOrRight) {
cells.addLast(cell);
scrollPixels(getCellLength(cell));
} else {
cells.addFirst(cell);
scrollPixels(-getCellLength(cell));
}
return true;
}
return false;
}
public void scrollToTop(int index) {
boolean posSet = false;
if (index > getCellCount() - 1) {
setPosition(1);
posSet = true;
} else if (index < 0) {
setPosition(0);
posSet = true;
}
if (! posSet) {
adjustPositionToIndex(index);
}
requestLayout();
}
public double scrollPixels(final double delta) {
if (delta == 0) return 0;
final boolean isVertical = isVertical();
if (((isVertical && (tempVisibility ? !needLengthBar : !vbar.isVisible())) ||
(! isVertical && (tempVisibility ? !needLengthBar : !hbar.isVisible())))) return 0;
double pos = getPosition();
if (pos == 0.0f && delta < 0) return 0;
if (pos == 1.0f && delta > 0) return 0;
recalculateEstimatedSize();
double answer = adjustByPixelAmount(delta);
if (pos == getPosition()) {
return 0;
}
if (cells.size() > 0) {
for (int i = 0; i < cells.size(); i++) {
T cell = cells.get(i);
assert cell != null;
positionCell(cell, getCellPosition(cell) - delta);
}
T firstCell = cells.getFirst();
double layoutY = firstCell == null ? 0 : getCellPosition(firstCell);
for (int i = 0; i < cells.size(); i++) {
T cell = cells.get(i);
assert cell != null;
double actualLayoutY = getCellPosition(cell);
if (Math.abs(actualLayoutY - layoutY) > 0.001) {
positionCell(cell, layoutY);
}
layoutY += getCellLength(cell);
}
cull();
firstCell = cells.getFirst();
if (firstCell != null) {
int firstIndex = getCellIndex(firstCell);
double prevIndexSize = getCellLength(firstIndex - 1);
addLeadingCells(firstIndex - 1, getCellPosition(firstCell) - prevIndexSize);
} else {
int currentIndex = computeCurrentIndex();
double offset = -computeViewportOffset(getPosition());
addLeadingCells(currentIndex, offset);
}
if (! addTrailingCells(false)) {
final T lastCell = getLastVisibleCell();
final double lastCellSize = getCellLength(lastCell);
final double cellEnd = getCellPosition(lastCell) + lastCellSize;
final double viewportLength = getViewportLength();
if (cellEnd < viewportLength) {
double emptySize = viewportLength - cellEnd;
for (int i = 0; i < cells.size(); i++) {
T cell = cells.get(i);
positionCell(cell, getCellPosition(cell) + emptySize);
}
setPosition(1.0f);
firstCell = cells.getFirst();
int firstIndex = getCellIndex(firstCell);
double prevIndexSize = getCellLength(firstIndex - 1);
addLeadingCells(firstIndex - 1, getCellPosition(firstCell) - prevIndexSize);
}
}
}
cull();
updateScrollBarsAndCells(false);
lastPosition = getPosition();
return answer;
}
@Override protected double computePrefWidth(double height) {
double w = isVertical() ? getPrefBreadth(height) : getPrefLength();
return w + vbar.prefWidth(-1);
}
@Override protected double computePrefHeight(double width) {
double h = isVertical() ? getPrefLength() : getPrefBreadth(width);
return h + hbar.prefHeight(-1);
}
public T getCell(int index) {
if (! cells.isEmpty()) {
T cell = getVisibleCell(index);
if (cell != null) return cell;
}
for (int i = 0; i < pile.size(); i++) {
T cell = pile.get(i);
if (getCellIndex(cell) == index) {
return cell;
}
}
if (accumCell == null) {
Callback<VirtualFlow<T>,T> cellFactory = getCellFactory();
if (cellFactory != null) {
accumCell = cellFactory.call(this);
accumCell.getProperties().put(NEW_CELL, null);
accumCellParent.getChildren().setAll(accumCell);
accumCell.setAccessibleRole(AccessibleRole.NODE);
accumCell.getChildrenUnmodifiable().addListener((Observable c) -> {
for (Node n : accumCell.getChildrenUnmodifiable()) {
n.setAccessibleRole(AccessibleRole.NODE);
}
});
}
}
setCellIndex(accumCell, index);
resizeCell(accumCell);
return accumCell;
}
protected void setCellIndex(T cell, int index) {
assert cell != null;
cell.updateIndex(index);
if ((cell.isNeedsLayout() && cell.getScene() != null) || cell.getProperties().containsKey(NEW_CELL)) {
cell.applyCss();
cell.getProperties().remove(NEW_CELL);
}
}
protected int getCellIndex(T cell){
return cell.getIndex();
}
protected final ScrollBar getHbar() {
return hbar;
}
protected final ScrollBar getVbar() {
return vbar;
}
private double maxPrefBreadth;
private final void setMaxPrefBreadth(double value) {
this.maxPrefBreadth = value;
}
final double getMaxPrefBreadth() {
return maxPrefBreadth;
}
private double viewportBreadth;
private final void setViewportBreadth(double value) {
this.viewportBreadth = value;
}
private final double getViewportBreadth() {
return viewportBreadth;
}
private double viewportLength;
void setViewportLength(double value) {
this.viewportLength = value;
this.absoluteOffset = getPosition() * (estimatedSize - viewportLength);
}
double getViewportLength() {
return viewportLength;
}
double getCellLength(int index) {
if (fixedCellSizeEnabled) return getFixedCellSize();
T cell = getCell(index);
double length = getCellLength(cell);
releaseCell(cell);
return length;
}
double getCellBreadth(int index) {
T cell = getCell(index);
double b = getCellBreadth(cell);
releaseCell(cell);
return b;
}
double getCellLength(T cell) {
if (cell == null) return 0;
if (fixedCellSizeEnabled) return getFixedCellSize();
return isVertical() ?
cell.getLayoutBounds().getHeight()
: cell.getLayoutBounds().getWidth();
}
double getCellBreadth(Cell cell) {
return isVertical() ?
cell.prefWidth(-1)
: cell.prefHeight(-1);
}
double getCellPosition(T cell) {
if (cell == null) return 0;
return isVertical() ?
cell.getLayoutY()
: cell.getLayoutX();
}
private void positionCell(T cell, double position) {
updateCellSize(cell);
if (isVertical()) {
cell.setLayoutX(0);
cell.setLayoutY(snapSpaceY(position));
} else {
cell.setLayoutX(snapSpaceX(position));
cell.setLayoutY(0);
}
}
protected void resizeCell(T cell) {
if (cell == null) return;
if (isVertical()) {
double width = Math.max(getMaxPrefBreadth(), getViewportBreadth());
cell.resize(width, fixedCellSizeEnabled ? getFixedCellSize() : Utils.boundedSize(cell.prefHeight(width), cell.minHeight(width), cell.maxHeight(width)));
} else {
double height = Math.max(getMaxPrefBreadth(), getViewportBreadth());
cell.resize(fixedCellSizeEnabled ? getFixedCellSize() : Utils.boundedSize(cell.prefWidth(height), cell.minWidth(height), cell.maxWidth(height)), height);
}
}
protected List<T> getCells() {
return cells;
}
protected T getLastVisibleCellWithinViewport() {
if (cells.isEmpty() || getViewportLength() <= 0) return null;
T cell;
final double max = getViewportLength();
for (int i = cells.size() - 1; i >= 0; i--) {
cell = cells.get(i);
if (cell.isEmpty()) continue;
final double cellStart = getCellPosition(cell);
final double cellEnd = cellStart + getCellLength(cell);
if (cellEnd <= (max + 2)) {
return cell;
}
}
return null;
}
protected T getFirstVisibleCellWithinViewport() {
if (cells.isEmpty() || getViewportLength() <= 0) return null;
T cell;
for (int i = 0; i < cells.size(); i++) {
cell = cells.get(i);
if (cell.isEmpty()) continue;
final double cellStart = getCellPosition(cell);
if (cellStart >= 0) {
return cell;
}
}
return null;
}
void addLeadingCells(int currentIndex, double startOffset) {
double offset = startOffset;
int index = currentIndex;
boolean first = true;
T cell = null;
if (index == getCellCount() && offset == getViewportLength()) {
index--;
first = false;
}
while (index >= 0 && (offset > 0 || first)) {
cell = getAvailableCell(index);
setCellIndex(cell, index);
resizeCell(cell);
cells.addFirst(cell);
if (first) {
first = false;
} else {
offset -= getCellLength(cell);
}
positionCell(cell, offset);
setMaxPrefBreadth(Math.max(getMaxPrefBreadth(), getCellBreadth(cell)));
cell.setVisible(true);
--index;
}
if (cells.size() > 0) {
cell = cells.getFirst();
int firstIndex = getCellIndex(cell);
double firstCellPos = getCellPosition(cell);
if (firstIndex == 0 && firstCellPos > 0) {
setPosition(0.0f);
offset = 0;
for (int i = 0; i < cells.size(); i++) {
cell = cells.get(i);
positionCell(cell, offset);
offset += getCellLength(cell);
}
}
} else {
vbar.setValue(0);
hbar.setValue(0);
}
}
boolean addTrailingCells(boolean fillEmptyCells) {
if (cells.isEmpty()) return false;
T startCell = cells.getLast();
double offset = getCellPosition(startCell) + getCellLength(startCell);
int index = getCellIndex(startCell) + 1;
final int cellCount = getCellCount();
boolean filledWithNonEmpty = index <= cellCount;
final double viewportLength = getViewportLength();
if (offset < 0 && !fillEmptyCells) {
return false;
}
final double maxCellCount = viewportLength;
while (offset < viewportLength) {
if (index >= cellCount) {
if (offset < viewportLength) filledWithNonEmpty = false;
if (! fillEmptyCells) return filledWithNonEmpty;
if (index > maxCellCount) {
final PlatformLogger logger = Logging.getControlsLogger();
if (logger.isLoggable(PlatformLogger.Level.INFO)) {
logger.info("index exceeds maxCellCount. Check size calculations for " + startCell.getClass());
}
return filledWithNonEmpty;
}
}
T cell = getAvailableCell(index);
setCellIndex(cell, index);
resizeCell(cell);
cells.addLast(cell);
positionCell(cell, offset);
setMaxPrefBreadth(Math.max(getMaxPrefBreadth(), getCellBreadth(cell)));
offset += getCellLength(cell);
cell.setVisible(true);
++index;
}
T firstCell = cells.getFirst();
index = getCellIndex(firstCell);
T lastNonEmptyCell = getLastVisibleCell();
double start = getCellPosition(firstCell);
double end = getCellPosition(lastNonEmptyCell) + getCellLength(lastNonEmptyCell);
if ((index != 0 || (index == 0 && start < 0)) && fillEmptyCells &&
lastNonEmptyCell != null && getCellIndex(lastNonEmptyCell) == cellCount - 1 && end < viewportLength) {
double prospectiveEnd = end;
double distance = viewportLength - end;
while (prospectiveEnd < viewportLength && index != 0 && (-start) < distance) {
index--;
T cell = getAvailableCell(index);
setCellIndex(cell, index);
resizeCell(cell);
cells.addFirst(cell);
double cellLength = getCellLength(cell);
start -= cellLength;
prospectiveEnd += cellLength;
positionCell(cell, start);
setMaxPrefBreadth(Math.max(getMaxPrefBreadth(), getCellBreadth(cell)));
cell.setVisible(true);
}
firstCell = cells.getFirst();
start = getCellPosition(firstCell);
double delta = viewportLength - end;
if (getCellIndex(firstCell) == 0 && delta > (-start)) {
delta = (-start);
}
for (int i = 0; i < cells.size(); i++) {
T cell = cells.get(i);
positionCell(cell, getCellPosition(cell) + delta);
}
start = getCellPosition(firstCell);
if (getCellIndex(firstCell) == 0 && start == 0) {
setPosition(0);
} else if (getPosition() != 1) {
setPosition(1);
}
}
return filledWithNonEmpty;
}
protected void reconfigureCells() {
needsReconfigureCells = true;
requestLayout();
}
protected void recreateCells() {
needsRecreateCells = true;
requestLayout();
}
protected void rebuildCells() {
needsRebuildCells = true;
requestLayout();
}
protected void requestCellLayout() {
needsCellsLayout = true;
requestLayout();
}
void setCellDirty(int index) {
dirtyCells.set(index);
requestLayout();
}
private void startSBReleasedAnimation() {
if (sbTouchTimeline == null) {
sbTouchTimeline = new Timeline();
sbTouchKF1 = new KeyFrame(Duration.millis(0), event -> {
tempVisibility = true;
requestLayout();
});
sbTouchKF2 = new KeyFrame(Duration.millis(1000), event -> {
if (touchDetected == false && mouseDown == false) {
tempVisibility = false;
requestLayout();
}
});
sbTouchTimeline.getKeyFrames().addAll(sbTouchKF1, sbTouchKF2);
}
sbTouchTimeline.playFromStart();
}
private void scrollBarOn() {
tempVisibility = true;
requestLayout();
}
void updateHbar() {
if (! isVisible() || getScene() == null) return;
if (isVertical()) {
if (needBreadthBar) {
clipView.setClipX(hbar.getValue());
} else {
clipView.setClipX(0);
hbar.setValue(0);
}
}
}
private boolean computeBarVisiblity() {
if (cells.isEmpty()) {
needLengthBar = false;
needBreadthBar = false;
return true;
}
final boolean isVertical = isVertical();
boolean barVisibilityChanged = false;
VirtualScrollBar breadthBar = isVertical ? hbar : vbar;
VirtualScrollBar lengthBar = isVertical ? vbar : hbar;
final double viewportBreadth = getViewportBreadth();
final int cellsSize = cells.size();
final int cellCount = getCellCount();
for (int i = 0; i < 2; i++) {
final boolean lengthBarVisible = getPosition() > 0
|| cellCount > cellsSize
|| (cellCount == cellsSize && (getCellPosition(cells.getLast()) + getCellLength(cells.getLast())) > getViewportLength())
|| (cellCount == cellsSize - 1 && barVisibilityChanged && needBreadthBar);
if (lengthBarVisible ^ needLengthBar) {
needLengthBar = lengthBarVisible;
barVisibilityChanged = true;
}
final boolean breadthBarVisible = (maxPrefBreadth > viewportBreadth);
if (breadthBarVisible ^ needBreadthBar) {
needBreadthBar = breadthBarVisible;
barVisibilityChanged = true;
}
}
if (!Properties.IS_TOUCH_SUPPORTED) {
updateViewportDimensions();
breadthBar.setVisible(needBreadthBar);
lengthBar.setVisible(needLengthBar);
} else {
breadthBar.setVisible(needBreadthBar && tempVisibility);
lengthBar.setVisible(needLengthBar && tempVisibility);
}
return barVisibilityChanged;
}
private void updateViewportDimensions() {
final boolean isVertical = isVertical();
final double breadthBarLength = isVertical ? snapSizeY(hbar.prefHeight(-1)) : snapSizeX(vbar.prefWidth(-1));
final double lengthBarBreadth = isVertical ? snapSizeX(vbar.prefWidth(-1)) : snapSizeY(hbar.prefHeight(-1));
if (!Properties.IS_TOUCH_SUPPORTED) {
setViewportBreadth((isVertical ? getWidth() : getHeight()) - (needLengthBar ? lengthBarBreadth : 0));
setViewportLength((isVertical ? getHeight() : getWidth()) - (needBreadthBar ? breadthBarLength : 0));
} else {
setViewportBreadth((isVertical ? getWidth() : getHeight()));
setViewportLength((isVertical ? getHeight() : getWidth()));
}
}
private void initViewport() {
final boolean isVertical = isVertical();
updateViewportDimensions();
VirtualScrollBar breadthBar = isVertical ? hbar : vbar;
VirtualScrollBar lengthBar = isVertical ? vbar : hbar;
breadthBar.setVirtual(false);
lengthBar.setVirtual(true);
}
private void updateScrollBarsAndCells(boolean recreate) {
final boolean isVertical = isVertical();
VirtualScrollBar breadthBar = isVertical ? hbar : vbar;
VirtualScrollBar lengthBar = isVertical ? vbar : hbar;
fitCells();
if (!cells.isEmpty()) {
final double currOffset = -computeViewportOffset(getPosition());
final int currIndex = computeCurrentIndex() - cells.getFirst().getIndex();
final int size = cells.size();
double offset = currOffset;
for (int i = currIndex - 1; i >= 0 && i < size; i--) {
final T cell = cells.get(i);
offset -= getCellLength(cell);
positionCell(cell, offset);
}
offset = currOffset;
for (int i = currIndex; i >= 0 && i < size; i++) {
final T cell = cells.get(i);
positionCell(cell, offset);
offset += getCellLength(cell);
}
}
corner.setVisible(breadthBar.isVisible() && lengthBar.isVisible());
double sumCellLength = 0;
double flowLength = (isVertical ? getHeight() : getWidth()) -
(breadthBar.isVisible() ? breadthBar.prefHeight(-1) : 0);
final double viewportBreadth = getViewportBreadth();
final double viewportLength = getViewportLength();
if (breadthBar.isVisible()) {
if (!Properties.IS_TOUCH_SUPPORTED) {
if (isVertical) {
hbar.resizeRelocate(0, viewportLength,
viewportBreadth, hbar.prefHeight(viewportBreadth));
} else {
vbar.resizeRelocate(viewportLength, 0,
vbar.prefWidth(viewportBreadth), viewportBreadth);
}
}
else {
if (isVertical) {
double prefHeight = hbar.prefHeight(viewportBreadth);
hbar.resizeRelocate(0, viewportLength - prefHeight,
viewportBreadth, prefHeight);
} else {
double prefWidth = vbar.prefWidth(viewportBreadth);
vbar.resizeRelocate(viewportLength - prefWidth, 0,
prefWidth, viewportBreadth);
}
}
if (getMaxPrefBreadth() != -1) {
double newMax = Math.max(1, getMaxPrefBreadth() - viewportBreadth);
if (newMax != breadthBar.getMax()) {
breadthBar.setMax(newMax);
double breadthBarValue = breadthBar.getValue();
boolean maxed = breadthBarValue != 0 && newMax == breadthBarValue;
if (maxed || breadthBarValue > newMax) {
breadthBar.setValue(newMax);
}
breadthBar.setVisibleAmount((viewportBreadth / getMaxPrefBreadth()) * newMax);
}
}
}
if (recreate && (lengthBar.isVisible() || Properties.IS_TOUCH_SUPPORTED)) {
final int cellCount = getCellCount();
int numCellsVisibleOnScreen = 0;
for (int i = 0, max = cells.size(); i < max; i++) {
T cell = cells.get(i);
if (cell != null && !cell.isEmpty()) {
sumCellLength += (isVertical ? cell.getHeight() : cell.getWidth());
if (sumCellLength > flowLength) {
break;
}
numCellsVisibleOnScreen++;
}
}
lengthBar.setMax(1);
if (numCellsVisibleOnScreen == 0 && cellCount == 1) {
lengthBar.setVisibleAmount(flowLength / sumCellLength);
} else {
lengthBar.setVisibleAmount(viewportLength / estimatedSize);
}
}
if (lengthBar.isVisible()) {
if (!Properties.IS_TOUCH_SUPPORTED) {
if (isVertical) {
vbar.resizeRelocate(viewportBreadth, 0, vbar.prefWidth(viewportLength), viewportLength);
} else {
hbar.resizeRelocate(0, viewportBreadth, viewportLength, hbar.prefHeight(-1));
}
}
else {
if (isVertical) {
double prefWidth = vbar.prefWidth(viewportLength);
vbar.resizeRelocate(viewportBreadth - prefWidth, 0, prefWidth, viewportLength);
} else {
double prefHeight = hbar.prefHeight(-1);
hbar.resizeRelocate(0, viewportBreadth - prefHeight, viewportLength, prefHeight);
}
}
}
if (corner.isVisible()) {
if (!Properties.IS_TOUCH_SUPPORTED) {
corner.resize(vbar.getWidth(), hbar.getHeight());
corner.relocate(hbar.getLayoutX() + hbar.getWidth(), vbar.getLayoutY() + vbar.getHeight());
}
else {
corner.resize(vbar.getWidth(), hbar.getHeight());
corner.relocate(hbar.getLayoutX() + (hbar.getWidth()-vbar.getWidth()), vbar.getLayoutY() + (vbar.getHeight()-hbar.getHeight()));
hbar.resize(hbar.getWidth()-vbar.getWidth(), hbar.getHeight());
vbar.resize(vbar.getWidth(), vbar.getHeight()-hbar.getHeight());
}
}
clipView.resize(snapSizeX(isVertical ? viewportBreadth : viewportLength),
snapSizeY(isVertical ? viewportLength : viewportBreadth));
if (getPosition() != lengthBar.getValue()) {
lengthBar.setValue(getPosition());
}
}
private void fitCells() {
double size = Math.max(getMaxPrefBreadth(), getViewportBreadth());
boolean isVertical = isVertical();
for (int i = 0; i < cells.size(); i++) {
Cell<?> cell = cells.get(i);
if (isVertical) {
cell.resize(size, cell.prefHeight(size));
} else {
cell.resize(cell.prefWidth(size), size);
}
}
}
private void cull() {
final double viewportLength = getViewportLength();
for (int i = cells.size() - 1; i >= 0; i--) {
T cell = cells.get(i);
double cellSize = getCellLength(cell);
double cellStart = getCellPosition(cell);
double cellEnd = cellStart + cellSize;
if (cellStart >= viewportLength || cellEnd < 0) {
addToPile(cells.remove(i));
}
}
}
private void releaseCell(T cell) {
if (accumCell != null && cell == accumCell) {
accumCell.setVisible(false);
accumCell.updateIndex(-1);
}
}
protected T getPrivateCell(int index) {
T cell = null;
if (! cells.isEmpty()) {
cell = getVisibleCell(index);
if (cell != null) {
cell.layout();
return cell;
}
}
if (cell == null) {
for (int i = 0; i < sheetChildren.size(); i++) {
T _cell = (T) sheetChildren.get(i);
if (getCellIndex(_cell) == index) {
return _cell;
}
}
}
Callback<VirtualFlow<T>, T> cellFactory = getCellFactory();
if (cellFactory != null) {
cell = cellFactory.call(this);
}
if (cell != null) {
setCellIndex(cell, index);
resizeCell(cell);
cell.setVisible(false);
sheetChildren.add(cell);
privateCells.add(cell);
}
return cell;
}
private final List<T> privateCells = new ArrayList<>();
private void releaseAllPrivateCells() {
sheetChildren.removeAll(privateCells);
privateCells.clear();
}
private void addToPile(T cell) {
assert cell != null;
pile.addLast(cell);
}
private void cleanPile() {
boolean wasFocusOwner = false;
for (int i = 0, max = pile.size(); i < max; i++) {
T cell = pile.get(i);
wasFocusOwner = wasFocusOwner || doesCellContainFocus(cell);
cell.setVisible(false);
}
if (wasFocusOwner) {
requestFocus();
}
}
private boolean doesCellContainFocus(Cell<?> c) {
Scene scene = c.getScene();
final Node focusOwner = scene == null ? null : scene.getFocusOwner();
if (focusOwner != null) {
if (c.equals(focusOwner)) {
return true;
}
Parent p = focusOwner.getParent();
while (p != null && ! (p instanceof VirtualFlow)) {
if (c.equals(p)) {
return true;
}
p = p.getParent();
}
}
return false;
}
private double getPrefBreadth(double oppDimension) {
double max = getMaxCellWidth(10);
if (oppDimension > -1) {
double prefLength = getPrefLength();
max = Math.max(max, prefLength * GOLDEN_RATIO_MULTIPLIER);
}
return max;
}
private double getPrefLength() {
double sum = 0.0;
int rows = Math.min(10, getCellCount());
for (int i = 0; i < rows; i++) {
sum += getCellLength(i);
}
return sum;
}
double getMaxCellWidth(int rowsToCount) {
double max = 0.0;
int rows = Math.max(1, rowsToCount == -1 ? getCellCount() : rowsToCount);
for (int i = 0; i < rows; i++) {
max = Math.max(max, getCellBreadth(i));
}
return max;
}
private double computeViewportOffset(double position) {
double p = com.sun.javafx.util.Utils.clamp(0, position, 1);
double bound = 0d;
double estSize = estimatedSize / getCellCount();
for (int i = 0; i < getCellCount(); i++) {
double h = getCellSize(i);
if (h < 0) h = estSize;
if (bound + h > absoluteOffset) {
return absoluteOffset - bound;
}
bound += h;
}
return 0d;
}
private void adjustPositionToIndex(int index) {
int cellCount = getCellCount();
if (cellCount <= 0) {
setPosition(0.0f);
} else {
double targetOffset = 0;
double estSize = estimatedSize/cellCount;
for (int i = 0; i < index; i++) {
double cz = getCellSize(i);
if (cz < 0) cz = estSize;
targetOffset = targetOffset+ cz;
}
this.absoluteOffset = targetOffset;
adjustPosition();
}
}
private double adjustByPixelAmount(double numPixels) {
if (numPixels == 0) return 0;
if ((absoluteOffset <= 0) && (numPixels < 0)) return 0;
double origAbsoluteOffset = this.absoluteOffset;
this.absoluteOffset = Math.max(0.d, this.absoluteOffset + numPixels);
double newPosition = Math.min(1.0d, absoluteOffset / (estimatedSize - viewportLength));
if ((numPixels > 0) && (newPosition < getPosition())) {
newPosition = getPosition()*1.01;
}
if ((numPixels < 0) && (newPosition > getPosition())) {
newPosition = getPosition()*.99;
}
if (newPosition > .95) {
int cci = computeCurrentIndex();
while (cci < getCellCount()) {
getOrCreateCellSize(cci); cci++;
}
recalculateEstimatedSize();
}
if (newPosition >= 1.d) {
absoluteOffset = estimatedSize - viewportLength;
}
setPosition(newPosition);
return absoluteOffset - origAbsoluteOffset;
}
private int computeCurrentIndex() {
double total = 0;
int currentCellCount = getCellCount();
double estSize = estimatedSize / currentCellCount;
for (int i = 0; i < currentCellCount; i++) {
double nextSize = getCellSize(i);
if (nextSize < 0) nextSize = estSize;
total = total + nextSize;
if (total > absoluteOffset) {
return i;
}
}
return currentCellCount == 0 ? 0 : currentCellCount - 1;
}
private double computeOffsetForCell(int itemIndex) {
double cellCount = getCellCount();
double p = com.sun.javafx.util.Utils.clamp(0, itemIndex, cellCount) / cellCount;
return -(getViewportLength() * p);
}
double getCellSize(int idx) {
return getOrCreateCellSize(idx, false);
}
double getOrCreateCellSize(int idx) {
return getOrCreateCellSize (idx, true);
}
private double getOrCreateCellSize (int idx, boolean create) {
if (itemSizeCache.size() > idx) {
if (itemSizeCache.get(idx) != null) {
return itemSizeCache.get(idx);
}
}
if (!create) return -1;
boolean doRelease = false;
T cell = getVisibleCell(idx);
if (cell == null) {
cell = getCell(idx);
doRelease = true;
}
while (idx >= itemSizeCache.size()) {
itemSizeCache.add(itemSizeCache.size(), null);
}
double answer = 1d;
if (isVertical()) {
answer = cell.getLayoutBounds().getHeight();
} else {
answer = cell.getLayoutBounds().getWidth();
}
itemSizeCache.set(idx, answer);
if (doRelease) {
releaseCell(cell);
}
return answer;
}
void updateCellSize(T cell) {
int cellIndex = cell.getIndex();
if (itemSizeCache.size() > cellIndex) {
if (isVertical()) {
double newh = cell.getLayoutBounds().getHeight();
itemSizeCache.set(cellIndex, newh);
} else {
double newh = cell.getLayoutBounds().getWidth();
itemSizeCache.set(cellIndex, newh);
}
}
}
private void recalculateEstimatedSize() {
recalculateAndImproveEstimatedSize(DEFAULT_IMPROVEMENT);
}
private void recalculateAndImproveEstimatedSize(int improve) {
int itemCount = getCellCount();
int cacheCount = itemSizeCache.size();
int added = 0;
while ((itemCount > itemSizeCache.size()) && (added < improve)) {
getOrCreateCellSize(itemSizeCache.size());
added++;
}
cacheCount = itemSizeCache.size();
int cnt = 0;
double tot = 0d;
for (int i = 0; (i < itemCount && i < cacheCount); i++) {
Double il = itemSizeCache.get(i);
if (il != null) {
tot = tot + il;
cnt++;
}
}
this.estimatedSize = cnt == 0 ? 1d: tot * itemCount / cnt;
}
private void resetSizeEstimates() {
itemSizeCache.clear();
this.estimatedSize = 1d;
}
static class ClippedContainer extends Region {
private Node node;
public Node getNode() { return this.node; }
public void setNode(Node n) {
this.node = n;
getChildren().clear();
getChildren().add(node);
}
public void setClipX(double clipX) {
setLayoutX(-clipX);
clipRect.setLayoutX(clipX);
}
public void setClipY(double clipY) {
setLayoutY(-clipY);
clipRect.setLayoutY(clipY);
}
private final Rectangle clipRect;
public ClippedContainer(final VirtualFlow<?> flow) {
if (flow == null) {
throw new IllegalArgumentException("VirtualFlow can not be null");
}
getStyleClass().add("clipped-container");
clipRect = new Rectangle();
clipRect.setSmooth(false);
setClip(clipRect);
super.widthProperty().addListener(valueModel -> {
clipRect.setWidth(getWidth());
});
super.heightProperty().addListener(valueModel -> {
clipRect.setHeight(getHeight());
});
}
}
static class ArrayLinkedList<T> extends AbstractList<T> {
private final ArrayList<T> array;
private int firstIndex = -1;
private int lastIndex = -1;
public ArrayLinkedList() {
array = new ArrayList<T>(50);
for (int i = 0; i < 50; i++) {
array.add(null);
}
}
public T getFirst() {
return firstIndex == -1 ? null : array.get(firstIndex);
}
public T getLast() {
return lastIndex == -1 ? null : array.get(lastIndex);
}
public void addFirst(T cell) {
if (firstIndex == -1) {
firstIndex = lastIndex = array.size() / 2;
array.set(firstIndex, cell);
} else if (firstIndex == 0) {
array.add(0, cell);
lastIndex++;
} else {
array.set(--firstIndex, cell);
}
}
public void addLast(T cell) {
if (firstIndex == -1) {
firstIndex = lastIndex = array.size() / 2;
array.set(lastIndex, cell);
} else if (lastIndex == array.size() - 1) {
array.add(++lastIndex, cell);
} else {
array.set(++lastIndex, cell);
}
}
public int size() {
return firstIndex == -1 ? 0 : lastIndex - firstIndex + 1;
}
public boolean isEmpty() {
return firstIndex == -1;
}
public T get(int index) {
if (index > (lastIndex - firstIndex) || index < 0) {
return null;
}
return array.get(firstIndex + index);
}
public void clear() {
for (int i = 0; i < array.size(); i++) {
array.set(i, null);
}
firstIndex = lastIndex = -1;
}
public T removeFirst() {
if (isEmpty()) return null;
return remove(0);
}
public T removeLast() {
if (isEmpty()) return null;
return remove(lastIndex - firstIndex);
}
public T remove(int index) {
if (index > lastIndex - firstIndex || index < 0) {
throw new ArrayIndexOutOfBoundsException();
}
if (index == 0) {
T cell = array.get(firstIndex);
array.set(firstIndex, null);
if (firstIndex == lastIndex) {
firstIndex = lastIndex = -1;
} else {
firstIndex++;
}
return cell;
} else if (index == lastIndex - firstIndex) {
T cell = array.get(lastIndex);
array.set(lastIndex--, null);
return cell;
} else {
T cell = array.get(firstIndex + index);
array.set(firstIndex + index, null);
for (int i = (firstIndex + index + 1); i <= lastIndex; i++) {
array.set(i - 1, array.get(i));
}
array.set(lastIndex--, null);
return cell;
}
}
}
}
