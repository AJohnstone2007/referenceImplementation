package javafx.scene.layout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import com.sun.javafx.collections.TrackableObservableList;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import javafx.beans.Observable;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.util.Callback;
public class GridPane extends Pane {
public static final int REMAINING = Integer.MAX_VALUE;
private static final String MARGIN_CONSTRAINT = "gridpane-margin";
private static final String HALIGNMENT_CONSTRAINT = "gridpane-halignment";
private static final String VALIGNMENT_CONSTRAINT = "gridpane-valignment";
private static final String HGROW_CONSTRAINT = "gridpane-hgrow";
private static final String VGROW_CONSTRAINT = "gridpane-vgrow";
private static final String ROW_INDEX_CONSTRAINT = "gridpane-row";
private static final String COLUMN_INDEX_CONSTRAINT = "gridpane-column";
private static final String ROW_SPAN_CONSTRAINT = "gridpane-row-span";
private static final String COLUMN_SPAN_CONSTRAINT = "gridpane-column-span";
private static final String FILL_WIDTH_CONSTRAINT = "gridpane-fill-width";
private static final String FILL_HEIGHT_CONSTRAINT = "gridpane-fill-height";
public static void setRowIndex(Node child, Integer value) {
if (value != null && value < 0) {
throw new IllegalArgumentException("rowIndex must be greater or equal to 0, but was "+value);
}
setConstraint(child, ROW_INDEX_CONSTRAINT, value);
}
public static Integer getRowIndex(Node child) {
return (Integer)getConstraint(child, ROW_INDEX_CONSTRAINT);
}
public static void setColumnIndex(Node child, Integer value) {
if (value != null && value < 0) {
throw new IllegalArgumentException("columnIndex must be greater or equal to 0, but was "+value);
}
setConstraint(child, COLUMN_INDEX_CONSTRAINT, value);
}
public static Integer getColumnIndex(Node child) {
return (Integer)getConstraint(child, COLUMN_INDEX_CONSTRAINT);
}
public static void setRowSpan(Node child, Integer value) {
if (value != null && value < 1) {
throw new IllegalArgumentException("rowSpan must be greater or equal to 1, but was "+value);
}
setConstraint(child, ROW_SPAN_CONSTRAINT, value);
}
public static Integer getRowSpan(Node child) {
return (Integer)getConstraint(child, ROW_SPAN_CONSTRAINT);
}
public static void setColumnSpan(Node child, Integer value) {
if (value != null && value < 1) {
throw new IllegalArgumentException("columnSpan must be greater or equal to 1, but was "+value);
}
setConstraint(child, COLUMN_SPAN_CONSTRAINT, value);
}
public static Integer getColumnSpan(Node child) {
return (Integer)getConstraint(child, COLUMN_SPAN_CONSTRAINT);
}
public static void setMargin(Node child, Insets value) {
setConstraint(child, MARGIN_CONSTRAINT, value);
}
public static Insets getMargin(Node child) {
return (Insets)getConstraint(child, MARGIN_CONSTRAINT);
}
private double getBaselineComplementForChild(Node child) {
if (isNodePositionedByBaseline(child)) {
return rowMinBaselineComplement[getNodeRowIndex(child)];
}
return -1;
}
private static final Callback<Node, Insets> marginAccessor = n -> getMargin(n);
public static void setHalignment(Node child, HPos value) {
setConstraint(child, HALIGNMENT_CONSTRAINT, value);
}
public static HPos getHalignment(Node child) {
return (HPos)getConstraint(child, HALIGNMENT_CONSTRAINT);
}
public static void setValignment(Node child, VPos value) {
setConstraint(child, VALIGNMENT_CONSTRAINT, value);
}
public static VPos getValignment(Node child) {
return (VPos)getConstraint(child, VALIGNMENT_CONSTRAINT);
}
public static void setHgrow(Node child, Priority value) {
setConstraint(child, HGROW_CONSTRAINT, value);
}
public static Priority getHgrow(Node child) {
return (Priority)getConstraint(child, HGROW_CONSTRAINT);
}
public static void setVgrow(Node child, Priority value) {
setConstraint(child, VGROW_CONSTRAINT, value);
}
public static Priority getVgrow(Node child) {
return (Priority)getConstraint(child, VGROW_CONSTRAINT);
}
public static void setFillWidth(Node child, Boolean value) {
setConstraint(child, FILL_WIDTH_CONSTRAINT, value);
}
public static Boolean isFillWidth(Node child) {
return (Boolean) getConstraint(child, FILL_WIDTH_CONSTRAINT);
}
public static void setFillHeight(Node child, Boolean value) {
setConstraint(child, FILL_HEIGHT_CONSTRAINT, value);
}
public static Boolean isFillHeight(Node child) {
return (Boolean) getConstraint(child, FILL_HEIGHT_CONSTRAINT);
}
public static void setConstraints(Node child, int columnIndex, int rowIndex) {
setRowIndex(child, rowIndex);
setColumnIndex(child, columnIndex);
}
public static void setConstraints(Node child, int columnIndex, int rowIndex, int columnspan, int rowspan) {
setRowIndex(child, rowIndex);
setColumnIndex(child, columnIndex);
setRowSpan(child, rowspan);
setColumnSpan(child, columnspan);
}
public static void setConstraints(Node child, int columnIndex, int rowIndex, int columnspan, int rowspan,
HPos halignment, VPos valignment) {
setRowIndex(child, rowIndex);
setColumnIndex(child, columnIndex);
setRowSpan(child, rowspan);
setColumnSpan(child, columnspan);
setHalignment(child, halignment);
setValignment(child, valignment);
}
public static void setConstraints(Node child, int columnIndex, int rowIndex, int columnspan, int rowspan,
HPos halignment, VPos valignment, Priority hgrow, Priority vgrow) {
setRowIndex(child, rowIndex);
setColumnIndex(child, columnIndex);
setRowSpan(child, rowspan);
setColumnSpan(child, columnspan);
setHalignment(child, halignment);
setValignment(child, valignment);
setHgrow(child, hgrow);
setVgrow(child, vgrow);
}
public static void setConstraints(Node child, int columnIndex, int rowIndex, int columnspan, int rowspan,
HPos halignment, VPos valignment, Priority hgrow, Priority vgrow, Insets margin) {
setRowIndex(child, rowIndex);
setColumnIndex(child, columnIndex);
setRowSpan(child, rowspan);
setColumnSpan(child, columnspan);
setHalignment(child, halignment);
setValignment(child, valignment);
setHgrow(child, hgrow);
setVgrow(child, vgrow);
setMargin(child, margin);
}
public static void clearConstraints(Node child) {
setRowIndex(child, null);
setColumnIndex(child, null);
setRowSpan(child, null);
setColumnSpan(child, null);
setHalignment(child, null);
setValignment(child, null);
setHgrow(child, null);
setVgrow(child, null);
setMargin(child, null);
}
private static final Color GRID_LINE_COLOR = Color.rgb(30, 30, 30);
private static final double GRID_LINE_DASH = 3;
static void createRow(int rowIndex, int columnIndex, Node... nodes) {
for (int i = 0; i < nodes.length; i++) {
setConstraints(nodes[i], columnIndex + i, rowIndex);
}
}
static void createColumn(int columnIndex, int rowIndex, Node... nodes) {
for (int i = 0; i < nodes.length; i++) {
setConstraints(nodes[i], columnIndex, rowIndex + i);
}
}
static int getNodeRowIndex(Node node) {
Integer rowIndex = getRowIndex(node);
return rowIndex != null? rowIndex : 0;
}
private static int getNodeRowSpan(Node node) {
Integer rowspan = getRowSpan(node);
return rowspan != null? rowspan : 1;
}
static int getNodeRowEnd(Node node) {
int rowSpan = getNodeRowSpan(node);
return rowSpan != REMAINING? getNodeRowIndex(node) + rowSpan - 1 : REMAINING;
}
static int getNodeColumnIndex(Node node) {
Integer columnIndex = getColumnIndex(node);
return columnIndex != null? columnIndex : 0;
}
private static int getNodeColumnSpan(Node node) {
Integer colspan = getColumnSpan(node);
return colspan != null? colspan : 1;
}
static int getNodeColumnEnd(Node node) {
int columnSpan = getNodeColumnSpan(node);
return columnSpan != REMAINING? getNodeColumnIndex(node) + columnSpan - 1 : REMAINING;
}
private static Priority getNodeHgrow(Node node) {
Priority hgrow = getHgrow(node);
return hgrow != null? hgrow : Priority.NEVER;
}
private static Priority getNodeVgrow(Node node) {
Priority vgrow = getVgrow(node);
return vgrow != null? vgrow : Priority.NEVER;
}
private static Priority[] createPriorityArray(int length, Priority value) {
Priority[] array = new Priority[length];
Arrays.fill(array, value);
return array;
}
public GridPane() {
super();
getChildren().addListener((Observable o) -> requestLayout());
}
public final DoubleProperty hgapProperty() {
if (hgap == null) {
hgap = new StyleableDoubleProperty(0) {
@Override
public void invalidated() {
requestLayout();
}
@Override
public CssMetaData<GridPane, Number> getCssMetaData() {
return StyleableProperties.HGAP;
}
@Override
public Object getBean() {
return GridPane.this;
}
@Override
public String getName() {
return "hgap";
}
};
}
return hgap;
}
private DoubleProperty hgap;
public final void setHgap(double value) { hgapProperty().set(value); }
public final double getHgap() { return hgap == null ? 0 : hgap.get(); }
public final DoubleProperty vgapProperty() {
if (vgap == null) {
vgap = new StyleableDoubleProperty(0) {
@Override
public void invalidated() {
requestLayout();
}
@Override
public CssMetaData<GridPane, Number> getCssMetaData() {
return StyleableProperties.VGAP;
}
@Override
public Object getBean() {
return GridPane.this;
}
@Override
public String getName() {
return "vgap";
}
};
}
return vgap;
}
private DoubleProperty vgap;
public final void setVgap(double value) { vgapProperty().set(value); }
public final double getVgap() { return vgap == null ? 0 : vgap.get(); }
public final ObjectProperty<Pos> alignmentProperty() {
if (alignment == null) {
alignment = new StyleableObjectProperty<Pos>(Pos.TOP_LEFT) {
@Override
public void invalidated() {
requestLayout();
}
@Override
public CssMetaData<GridPane, Pos> getCssMetaData() {
return StyleableProperties.ALIGNMENT;
}
@Override
public Object getBean() {
return GridPane.this;
}
@Override
public String getName() {
return "alignment";
}
};
}
return alignment;
}
private ObjectProperty<Pos> alignment;
public final void setAlignment(Pos value) {
alignmentProperty().set(value);
}
public final Pos getAlignment() {
return alignment == null ? Pos.TOP_LEFT : alignment.get();
}
private Pos getAlignmentInternal() {
Pos localPos = getAlignment();
return localPos == null ? Pos.TOP_LEFT : localPos;
}
public final BooleanProperty gridLinesVisibleProperty() {
if (gridLinesVisible == null) {
gridLinesVisible = new StyleableBooleanProperty() {
@Override
protected void invalidated() {
if (get()) {
gridLines = new Group();
gridLines.setManaged(false);
getChildren().add(gridLines);
} else {
getChildren().remove(gridLines);
gridLines = null;
}
requestLayout();
}
@Override
public CssMetaData<GridPane, Boolean> getCssMetaData() {
return StyleableProperties.GRID_LINES_VISIBLE;
}
@Override
public Object getBean() {
return GridPane.this;
}
@Override
public String getName() {
return "gridLinesVisible";
}
};
}
return gridLinesVisible;
}
private BooleanProperty gridLinesVisible;
public final void setGridLinesVisible(boolean value) { gridLinesVisibleProperty().set(value); }
public final boolean isGridLinesVisible() { return gridLinesVisible == null ? false : gridLinesVisible.get(); }
private final ObservableList<RowConstraints> rowConstraints = new TrackableObservableList<RowConstraints>() {
@Override
protected void onChanged(Change<RowConstraints> c) {
while (c.next()) {
for (RowConstraints constraints : c.getRemoved()) {
if (constraints != null && !rowConstraints.contains(constraints)) {
constraints.remove(GridPane.this);
}
}
for (RowConstraints constraints : c.getAddedSubList()) {
if (constraints != null) {
constraints.add(GridPane.this);
}
}
}
requestLayout();
}
};
public final ObservableList<RowConstraints> getRowConstraints() { return rowConstraints; }
private final ObservableList<ColumnConstraints> columnConstraints = new TrackableObservableList<ColumnConstraints>() {
@Override
protected void onChanged(Change<ColumnConstraints> c) {
while(c.next()) {
for (ColumnConstraints constraints : c.getRemoved()) {
if (constraints != null && !columnConstraints.contains(constraints)) {
constraints.remove(GridPane.this);
}
}
for (ColumnConstraints constraints : c.getAddedSubList()) {
if (constraints != null) {
constraints.add(GridPane.this);
}
}
}
requestLayout();
}
};
public final ObservableList<ColumnConstraints> getColumnConstraints() { return columnConstraints; }
public void add(Node child, int columnIndex, int rowIndex) {
setConstraints(child, columnIndex, rowIndex);
getChildren().add(child);
}
public void add(Node child, int columnIndex, int rowIndex, int colspan, int rowspan) {
setConstraints(child, columnIndex, rowIndex, colspan, rowspan);
getChildren().add(child);
}
public void addRow(int rowIndex, Node... children) {
int columnIndex = 0;
final List<Node> managed = getManagedChildren();
for (int i = 0, size = managed.size(); i < size; i++) {
Node child = managed.get(i);
final int nodeRowIndex = getNodeRowIndex(child);
final int nodeRowEnd = getNodeRowEnd(child);
if (rowIndex >= nodeRowIndex &&
(rowIndex <= nodeRowEnd || nodeRowEnd == REMAINING)) {
int index = getNodeColumnIndex(child);
int end = getNodeColumnEnd(child);
columnIndex = Math.max(columnIndex, (end != REMAINING? end : index) + 1);
}
}
createRow(rowIndex, columnIndex, children);
getChildren().addAll(children);
}
public void addColumn(int columnIndex, Node... children) {
int rowIndex = 0;
final List<Node> managed = getManagedChildren();
for (int i = 0, size = managed.size(); i < size; i++) {
Node child = managed.get(i);
final int nodeColumnIndex = getNodeColumnIndex(child);
final int nodeColumnEnd = getNodeColumnEnd(child);
if (columnIndex >= nodeColumnIndex
&& (columnIndex <= nodeColumnEnd || nodeColumnEnd == REMAINING)) {
int index = getNodeRowIndex(child);
int end = getNodeRowEnd(child);
rowIndex = Math.max(rowIndex, (end != REMAINING? end : index) + 1);
}
}
createColumn(columnIndex, rowIndex, children);
getChildren().addAll(children);
}
private Group gridLines;
private Orientation bias;
private double[] rowPercentHeight;
private double rowPercentTotal = 0;
private CompositeSize rowMinHeight;
private CompositeSize rowPrefHeight;
private CompositeSize rowMaxHeight;
private List<Node>[] rowBaseline;
private double[] rowMinBaselineComplement;
private double[] rowPrefBaselineComplement;
private double[] rowMaxBaselineComplement;
private Priority[] rowGrow;
private double[] columnPercentWidth;
private double columnPercentTotal = 0;
private CompositeSize columnMinWidth;
private CompositeSize columnPrefWidth;
private CompositeSize columnMaxWidth;
private Priority[] columnGrow;
private boolean metricsDirty = true;
private boolean performingLayout = false;
private int numRows;
private int numColumns;
private int getNumberOfRows() {
computeGridMetrics();
return numRows;
}
private int getNumberOfColumns() {
computeGridMetrics();
return numColumns;
}
private boolean isNodePositionedByBaseline(Node n){
return (getRowValignment(getNodeRowIndex(n)) == VPos.BASELINE && getValignment(n) == null)
|| getValignment(n) == VPos.BASELINE;
}
private void computeGridMetrics() {
if (metricsDirty) {
numRows = rowConstraints.size();
numColumns = columnConstraints.size();
final List<Node> managed = getManagedChildren();
for (int i = 0, size = managed.size(); i < size; i++) {
Node child = managed.get(i);
int rowIndex = getNodeRowIndex(child);
int columnIndex = getNodeColumnIndex(child);
int rowEnd = getNodeRowEnd(child);
int columnEnd = getNodeColumnEnd(child);
numRows = Math.max(numRows, (rowEnd != REMAINING ? rowEnd : rowIndex) + 1);
numColumns = Math.max(numColumns, (columnEnd != REMAINING ? columnEnd : columnIndex) + 1);
}
rowPercentHeight = createDoubleArray(numRows, -1);
rowPercentTotal = 0;
columnPercentWidth = createDoubleArray(numColumns, -1);
columnPercentTotal = 0;
columnGrow = createPriorityArray(numColumns, Priority.NEVER);
rowGrow = createPriorityArray(numRows, Priority.NEVER);
rowMinBaselineComplement = createDoubleArray(numRows, -1);
rowPrefBaselineComplement = createDoubleArray(numRows, -1);
rowMaxBaselineComplement = createDoubleArray(numRows, -1);
rowBaseline = new List[numRows];
for (int i = 0, sz = numRows; i < sz; ++i) {
if (i < rowConstraints.size()) {
final RowConstraints rc = rowConstraints.get(i);
double percentHeight = rc.getPercentHeight();
Priority vGrow = rc.getVgrow();
if (percentHeight >= 0) {
rowPercentHeight[i] = percentHeight;
}
if (vGrow != null) {
rowGrow[i] = vGrow;
}
}
List<Node> baselineNodes = new ArrayList<>(numColumns);
for (int j = 0, size = managed.size(); j < size; j++) {
Node n = managed.get(j);
if (getNodeRowIndex(n) == i && isNodePositionedByBaseline(n)) {
baselineNodes.add(n);
}
}
rowMinBaselineComplement[i] = getMinBaselineComplement(baselineNodes);
rowPrefBaselineComplement[i] = getPrefBaselineComplement(baselineNodes);
rowMaxBaselineComplement[i] = getMaxBaselineComplement(baselineNodes);
rowBaseline[i] = baselineNodes;
}
for (int i = 0, sz = Math.min(numColumns, columnConstraints.size()); i < sz; ++i) {
final ColumnConstraints cc = columnConstraints.get(i);
double percentWidth = cc.getPercentWidth();
Priority hGrow = cc.getHgrow();
if (percentWidth >= 0)
columnPercentWidth[i] = percentWidth;
if (hGrow != null)
columnGrow[i] = hGrow;
}
for (int i = 0, size = managed.size(); i < size; i++) {
Node child = managed.get(i);
if (getNodeColumnSpan(child) == 1) {
Priority hg = getNodeHgrow(child);
int idx = getNodeColumnIndex(child);
columnGrow[idx] = Priority.max(columnGrow[idx], hg);
}
if (getNodeRowSpan(child) == 1) {
Priority vg = getNodeVgrow(child);
int idx = getNodeRowIndex(child);
rowGrow[idx] = Priority.max(rowGrow[idx], vg);
}
}
for (int i = 0; i < rowPercentHeight.length; i++) {
if (rowPercentHeight[i] > 0) {
rowPercentTotal += rowPercentHeight[i];
}
}
if (rowPercentTotal > 100) {
double weight = 100 / rowPercentTotal;
for (int i = 0; i < rowPercentHeight.length; i++) {
if (rowPercentHeight[i] > 0) {
rowPercentHeight[i] *= weight;
}
}
rowPercentTotal = 100;
}
for (int i = 0; i < columnPercentWidth.length; i++) {
if (columnPercentWidth[i] > 0) {
columnPercentTotal += columnPercentWidth[i];
}
}
if (columnPercentTotal > 100) {
double weight = 100 / columnPercentTotal;
for (int i = 0; i < columnPercentWidth.length; i++) {
if (columnPercentWidth[i] > 0) {
columnPercentWidth[i] *= weight;
}
}
columnPercentTotal = 100;
}
bias = null;
for (int i = 0; i < managed.size(); ++i) {
final Orientation b = managed.get(i).getContentBias();
if (b != null) {
bias = b;
if (b == Orientation.HORIZONTAL) {
break;
}
}
}
metricsDirty = false;
}
}
@Override protected double computeMinWidth(double height) {
computeGridMetrics();
performingLayout = true;
try {
final double[] heights = height == -1 ? null : computeHeightsToFit(height).asArray();
return snapSpaceX(getInsets().getLeft()) +
computeMinWidths(heights).computeTotalWithMultiSize() +
snapSpaceX(getInsets().getRight());
} finally {
performingLayout = false;
}
}
@Override protected double computeMinHeight(double width) {
computeGridMetrics();
performingLayout = true;
try {
final double[] widths = width == -1 ? null : computeWidthsToFit(width).asArray();
return snapSpaceY(getInsets().getTop()) +
computeMinHeights(widths).computeTotalWithMultiSize() +
snapSpaceY(getInsets().getBottom());
} finally {
performingLayout = false;
}
}
@Override protected double computePrefWidth(double height) {
computeGridMetrics();
performingLayout = true;
try {
final double[] heights = height == -1 ? null : computeHeightsToFit(height).asArray();
return snapSpaceX(getInsets().getLeft()) +
computePrefWidths(heights).computeTotalWithMultiSize() +
snapSpaceX(getInsets().getRight());
} finally {
performingLayout = false;
}
}
@Override protected double computePrefHeight(double width) {
computeGridMetrics();
performingLayout = true;
try {
final double[] widths = width == -1 ? null : computeWidthsToFit(width).asArray();
return snapSpaceY(getInsets().getTop()) +
computePrefHeights(widths).computeTotalWithMultiSize() +
snapSpaceY(getInsets().getBottom());
} finally {
performingLayout = false;
}
}
private VPos getRowValignment(int rowIndex) {
if (rowIndex < getRowConstraints().size()) {
RowConstraints constraints = getRowConstraints().get(rowIndex);
if (constraints.getValignment() != null) {
return constraints.getValignment();
}
}
return VPos.CENTER;
}
private HPos getColumnHalignment(int columnIndex) {
if (columnIndex < getColumnConstraints().size()) {
ColumnConstraints constraints = getColumnConstraints().get(columnIndex);
if (constraints.getHalignment() != null) {
return constraints.getHalignment();
}
}
return HPos.LEFT;
}
private double getColumnMinWidth(int columnIndex) {
if (columnIndex < getColumnConstraints().size()) {
ColumnConstraints constraints = getColumnConstraints().get(columnIndex);
return constraints.getMinWidth();
}
return USE_COMPUTED_SIZE;
}
private double getRowMinHeight(int rowIndex) {
if (rowIndex < getRowConstraints().size()) {
RowConstraints constraints = getRowConstraints().get(rowIndex);
return constraints.getMinHeight();
}
return USE_COMPUTED_SIZE;
}
private double getColumnMaxWidth(int columnIndex) {
if (columnIndex < getColumnConstraints().size()) {
ColumnConstraints constraints = getColumnConstraints().get(columnIndex);
return constraints.getMaxWidth();
}
return USE_COMPUTED_SIZE;
}
private double getColumnPrefWidth(int columnIndex) {
if (columnIndex < getColumnConstraints().size()) {
ColumnConstraints constraints = getColumnConstraints().get(columnIndex);
return constraints.getPrefWidth();
}
return USE_COMPUTED_SIZE;
}
private double getRowPrefHeight(int rowIndex) {
if (rowIndex < getRowConstraints().size()) {
RowConstraints constraints = getRowConstraints().get(rowIndex);
return constraints.getPrefHeight();
}
return USE_COMPUTED_SIZE;
}
private double getRowMaxHeight(int rowIndex) {
if (rowIndex < getRowConstraints().size()) {
RowConstraints constraints = getRowConstraints().get(rowIndex);
return constraints.getMaxHeight();
}
return USE_COMPUTED_SIZE;
}
private boolean shouldRowFillHeight(int rowIndex) {
if (rowIndex < getRowConstraints().size()) {
return getRowConstraints().get(rowIndex).isFillHeight();
}
return true;
}
private boolean shouldColumnFillWidth(int columnIndex) {
if (columnIndex < getColumnConstraints().size()) {
return getColumnConstraints().get(columnIndex).isFillWidth();
}
return true;
}
private double getTotalWidthOfNodeColumns(Node child, double[] widths) {
if (getNodeColumnSpan(child) == 1) {
return widths[getNodeColumnIndex(child)];
} else {
double total = 0;
for (int i = getNodeColumnIndex(child), last = getNodeColumnEndConvertRemaining(child); i <= last; ++i) {
total += widths[i];
}
return total;
}
}
private CompositeSize computeMaxHeights() {
if (rowMaxHeight == null) {
rowMaxHeight = createCompositeRows(Double.MAX_VALUE);
final ObservableList<RowConstraints> rowConstr = getRowConstraints();
CompositeSize prefHeights = null;
for (int i = 0; i < rowConstr.size(); ++i) {
final RowConstraints curConstraint = rowConstr.get(i);
final double constrMaxH = curConstraint.getMaxHeight();
if (constrMaxH == USE_PREF_SIZE) {
if (prefHeights == null) {
prefHeights = computePrefHeights(null);
}
rowMaxHeight.setPresetSize(i, prefHeights.getSize(i));
} else if (constrMaxH != USE_COMPUTED_SIZE) {
final double maxRowHeight = snapSizeY(constrMaxH);
final double constrMinH = curConstraint.getMinHeight();
if (constrMinH >= 0 ) {
final double min = snapSizeY(curConstraint.getMinHeight());
rowMaxHeight.setPresetSize(i, boundedSize(min, maxRowHeight, maxRowHeight));
} else {
rowMaxHeight.setPresetSize(i, maxRowHeight);
}
}
}
}
return rowMaxHeight;
}
private CompositeSize computePrefHeights(double[] widths) {
CompositeSize result;
if (widths == null) {
if (rowPrefHeight != null) {
return rowPrefHeight;
}
rowPrefHeight = createCompositeRows(0);
result = rowPrefHeight;
} else {
result = createCompositeRows(0);
}
final ObservableList<RowConstraints> rowConstr = getRowConstraints();
for (int i = 0; i < rowConstr.size(); ++i) {
final RowConstraints curConstraint = rowConstr.get(i);
final double constrMinH = curConstraint.getMinHeight();
final double constrPrefH = curConstraint.getPrefHeight();
if (constrPrefH != USE_COMPUTED_SIZE) {
final double prefRowHeight = snapSizeY(constrPrefH);
final double constrMaxH = curConstraint.getMaxHeight();
if (constrMinH >= 0 || constrMaxH >= 0) {
final double min = (constrMinH < 0 ? 0 : snapSizeY(constrMinH));
final double max = (constrMaxH < 0 ? Double.POSITIVE_INFINITY : snapSizeY(constrMaxH));
result.setPresetSize(i, boundedSize(min, prefRowHeight, max));
} else {
result.setPresetSize(i, prefRowHeight);
}
} else if (constrMinH > 0){
result.setSize(i, snapSizeY(constrMinH));
}
}
List<Node> managed = getManagedChildren();
for (int i = 0, size = managed.size(); i < size; i++) {
Node child = managed.get(i);
int start = getNodeRowIndex(child);
int end = getNodeRowEndConvertRemaining(child);
double childPrefAreaHeight = computeChildPrefAreaHeight(child, isNodePositionedByBaseline(child) ? rowPrefBaselineComplement[start] : -1, getMargin(child),
widths == null ? -1 : getTotalWidthOfNodeColumns(child, widths));
if (start == end && !result.isPreset(start)) {
double min = getRowMinHeight(start);
double max = getRowMaxHeight(start);
result.setMaxSize(start, boundedSize(min < 0 ? 0 : min, childPrefAreaHeight, max < 0 ? Double.MAX_VALUE : max));
} else if (start != end){
result.setMaxMultiSize(start, end + 1, childPrefAreaHeight);
}
}
return result;
}
private CompositeSize computeMinHeights(double[] widths) {
CompositeSize result;
if (widths == null) {
if (rowMinHeight != null) {
return rowMinHeight;
}
rowMinHeight = createCompositeRows(0);
result = rowMinHeight;
} else {
result = createCompositeRows(0);
}
final ObservableList<RowConstraints> rowConstr = getRowConstraints();
CompositeSize prefHeights = null;
for (int i = 0; i < rowConstr.size(); ++i) {
final double constrMinH = rowConstr.get(i).getMinHeight();
if (constrMinH == USE_PREF_SIZE) {
if (prefHeights == null) {
prefHeights = computePrefHeights(widths);
}
result.setPresetSize(i, prefHeights.getSize(i));
} else if (constrMinH != USE_COMPUTED_SIZE) {
result.setPresetSize(i, snapSizeY(constrMinH));
}
}
List<Node> managed = getManagedChildren();
for (int i = 0, size = managed.size(); i < size; i++) {
Node child = managed.get(i);
int start = getNodeRowIndex(child);
int end = getNodeRowEndConvertRemaining(child);
double childMinAreaHeight = computeChildMinAreaHeight(child, isNodePositionedByBaseline(child) ? rowMinBaselineComplement[start] : -1, getMargin(child),
widths == null ? -1 : getTotalWidthOfNodeColumns(child, widths));
if (start == end && !result.isPreset(start)) {
result.setMaxSize(start, childMinAreaHeight);
} else if (start != end){
result.setMaxMultiSize(start, end + 1, childMinAreaHeight);
}
}
return result;
}
private double getTotalHeightOfNodeRows(Node child, double[] heights) {
if (getNodeRowSpan(child) == 1) {
return heights[getNodeRowIndex(child)];
} else {
double total = 0;
for (int i = getNodeRowIndex(child), last = getNodeRowEndConvertRemaining(child); i <= last; ++i) {
total += heights[i];
}
return total;
}
}
private CompositeSize computeMaxWidths() {
if (columnMaxWidth == null) {
columnMaxWidth = createCompositeColumns(Double.MAX_VALUE);
final ObservableList<ColumnConstraints> columnConstr = getColumnConstraints();
CompositeSize prefWidths = null;
for (int i = 0; i < columnConstr.size(); ++i) {
final ColumnConstraints curConstraint = columnConstr.get(i);
final double constrMaxW = curConstraint.getMaxWidth();
if (constrMaxW == USE_PREF_SIZE) {
if (prefWidths == null) {
prefWidths = computePrefWidths(null);
}
columnMaxWidth.setPresetSize(i, prefWidths.getSize(i));
} else if (constrMaxW != USE_COMPUTED_SIZE) {
double maxColumnWidth = snapSizeX(constrMaxW);
final double constrMinW = curConstraint.getMinWidth();
if (constrMinW >= 0) {
final double min = snapSizeX(constrMinW);
columnMaxWidth.setPresetSize(i, boundedSize(min, maxColumnWidth, maxColumnWidth));
} else {
columnMaxWidth.setPresetSize(i, maxColumnWidth);
}
}
}
}
return columnMaxWidth;
}
private CompositeSize computePrefWidths(double[] heights) {
CompositeSize result;
if (heights == null) {
if (columnPrefWidth != null) {
return columnPrefWidth;
}
columnPrefWidth = createCompositeColumns(0);
result = columnPrefWidth;
} else {
result = createCompositeColumns(0);
}
final ObservableList<ColumnConstraints> columnConstr = getColumnConstraints();
for (int i = 0; i < columnConstr.size(); ++i) {
final ColumnConstraints curConstraint = columnConstr.get(i);
final double constrPrefW = curConstraint.getPrefWidth();
final double constrMinW = curConstraint.getMinWidth();
if (constrPrefW != USE_COMPUTED_SIZE) {
final double prefColumnWidth = snapSizeX(constrPrefW);
final double constrMaxW = curConstraint.getMaxWidth();
if (constrMinW >= 0 || constrMaxW >= 0) {
double min = (constrMinW < 0 ? 0 : snapSizeX(constrMinW));
final double max = (constrMaxW < 0 ? Double.POSITIVE_INFINITY : snapSizeX(constrMaxW));
result.setPresetSize(i, boundedSize(min < 0 ? 0 : min,
prefColumnWidth,
max < 0 ? Double.POSITIVE_INFINITY : max));
} else {
result.setPresetSize(i, prefColumnWidth);
}
} else if (constrMinW > 0){
result.setSize(i, snapSizeX(constrMinW));
}
}
List<Node> managed = getManagedChildren();
for (int i = 0, size = managed.size(); i < size; i++) {
Node child = managed.get(i);
int start = getNodeColumnIndex(child);
int end = getNodeColumnEndConvertRemaining(child);
if (start == end && !result.isPreset(start)) {
double min = getColumnMinWidth(start);
double max = getColumnMaxWidth(start);
result.setMaxSize(start, boundedSize(min < 0 ? 0 : min, computeChildPrefAreaWidth(child,
getBaselineComplementForChild(child), getMargin(child),
heights == null ? -1 : getTotalHeightOfNodeRows(child, heights), false),
max < 0 ? Double.MAX_VALUE : max));
} else if (start != end) {
result.setMaxMultiSize(start, end + 1, computeChildPrefAreaWidth(child, getBaselineComplementForChild(child),
getMargin(child),
heights == null ? -1 : getTotalHeightOfNodeRows(child, heights), false));
}
}
return result;
}
private CompositeSize computeMinWidths(double[] heights) {
CompositeSize result;
if (heights == null) {
if (columnMinWidth != null) {
return columnMinWidth;
}
columnMinWidth = createCompositeColumns(0);
result = columnMinWidth;
} else {
result = createCompositeColumns(0);
}
final ObservableList<ColumnConstraints> columnConstr = getColumnConstraints();
CompositeSize prefWidths = null;
for (int i = 0; i < columnConstr.size(); ++i) {
final double constrMinW = columnConstr.get(i).getMinWidth();
if (constrMinW == USE_PREF_SIZE) {
if (prefWidths == null) {
prefWidths = computePrefWidths(heights);
}
result.setPresetSize(i, prefWidths.getSize(i));
} else if (constrMinW != USE_COMPUTED_SIZE) {
result.setPresetSize(i, snapSizeX(constrMinW));
}
}
List<Node> managed = getManagedChildren();
for (int i = 0, size = managed.size(); i < size; i++) {
Node child = managed.get(i);
int start = getNodeColumnIndex(child);
int end = getNodeColumnEndConvertRemaining(child);
if (start == end && !result.isPreset(start)) {
result.setMaxSize(start, computeChildMinAreaWidth(child, getBaselineComplementForChild(child),
getMargin(child),
heights == null ? -1 : getTotalHeightOfNodeRows(child, heights),false));
} else if (start != end){
result.setMaxMultiSize(start, end + 1, computeChildMinAreaWidth(child, getBaselineComplementForChild(child),
getMargin(child),
heights == null ? -1 : getTotalHeightOfNodeRows(child, heights), false));
}
}
return result;
}
private CompositeSize computeHeightsToFit(double height) {
assert(height != -1);
final CompositeSize heights;
if (rowPercentTotal == 100) {
heights = createCompositeRows(0);
} else {
heights = (CompositeSize) computePrefHeights(null).clone();
}
adjustRowHeights(heights, height);
return heights;
}
private CompositeSize computeWidthsToFit(double width) {
assert(width != -1);
final CompositeSize widths;
if (columnPercentTotal == 100) {
widths = createCompositeColumns(0);
} else {
widths = (CompositeSize) computePrefWidths(null).clone();
}
adjustColumnWidths(widths, width);
return widths;
}
@Override public Orientation getContentBias() {
computeGridMetrics();
return bias;
}
@Override public void requestLayout() {
if (performingLayout) {
return;
} else if (metricsDirty) {
super.requestLayout();
return;
}
metricsDirty = true;
bias = null;
rowGrow = null;
rowMinHeight = rowPrefHeight = rowMaxHeight = null;
columnGrow = null;
columnMinWidth = columnPrefWidth = columnMaxWidth = null;
rowMinBaselineComplement = rowPrefBaselineComplement = rowMaxBaselineComplement = null;
super.requestLayout();
}
@Override protected void layoutChildren() {
performingLayout = true;
try {
final double snaphgap = snapSpaceX(getHgap());
final double snapvgap = snapSpaceY(getVgap());
final double top = snapSpaceY(getInsets().getTop());
final double bottom = snapSpaceY(getInsets().getBottom());
final double left = snapSpaceX(getInsets().getLeft());
final double right = snapSpaceX(getInsets().getRight());
final double width = getWidth();
final double height = getHeight();
final double contentHeight = height - top - bottom;
final double contentWidth = width - left - right;
double columnTotal;
double rowTotal;
computeGridMetrics();
Orientation contentBias = getContentBias();
CompositeSize heights;
final CompositeSize widths;
if (contentBias == null) {
heights = (CompositeSize) computePrefHeights(null).clone();
widths = (CompositeSize) computePrefWidths(null).clone();
rowTotal = adjustRowHeights(heights, height);
columnTotal = adjustColumnWidths(widths, width);
} else if (contentBias == Orientation.HORIZONTAL) {
widths = (CompositeSize) computePrefWidths(null).clone();
columnTotal = adjustColumnWidths(widths, width);
heights = computePrefHeights(widths.asArray());
rowTotal = adjustRowHeights(heights, height);
} else {
heights = (CompositeSize) computePrefHeights(null).clone();
rowTotal = adjustRowHeights(heights, height);
widths = computePrefWidths(heights.asArray());
columnTotal = adjustColumnWidths(widths, width);
}
final double x = left + computeXOffset(contentWidth, columnTotal, getAlignmentInternal().getHpos());
final double y = top + computeYOffset(contentHeight, rowTotal, getAlignmentInternal().getVpos());
final List<Node> managed = getManagedChildren();
double[] baselineOffsets = createDoubleArray(numRows, -1);
for (int i = 0, size = managed.size(); i < size; i++) {
final Node child = managed.get(i);
final int rowIndex = getNodeRowIndex(child);
int columnIndex = getNodeColumnIndex(child);
int colspan = getNodeColumnSpan(child);
if (colspan == REMAINING) {
colspan = widths.getLength() - columnIndex;
}
int rowspan = getNodeRowSpan(child);
if (rowspan == REMAINING) {
rowspan = heights.getLength() - rowIndex;
}
double areaX = x;
for (int j = 0; j < columnIndex; j++) {
areaX += widths.getSize(j) + snaphgap;
}
double areaY = y;
for (int j = 0; j < rowIndex; j++) {
areaY += heights.getSize(j) + snapvgap;
}
double areaW = widths.getSize(columnIndex);
for (int j = 2; j <= colspan; j++) {
areaW += widths.getSize(columnIndex + j - 1) + snaphgap;
}
double areaH = heights.getSize(rowIndex);
for (int j = 2; j <= rowspan; j++) {
areaH += heights.getSize(rowIndex + j - 1) + snapvgap;
}
HPos halign = getHalignment(child);
VPos valign = getValignment(child);
Boolean fillWidth = isFillWidth(child);
Boolean fillHeight = isFillHeight(child);
if (halign == null) {
halign = getColumnHalignment(columnIndex);
}
if (valign == null) {
valign = getRowValignment(rowIndex);
}
if (fillWidth == null) {
fillWidth = shouldColumnFillWidth(columnIndex);
}
if (fillHeight == null) {
fillHeight = shouldRowFillHeight(rowIndex);
}
double baselineOffset = 0;
if (valign == VPos.BASELINE) {
if (baselineOffsets[rowIndex] == -1) {
baselineOffsets[rowIndex] = getAreaBaselineOffset(rowBaseline[rowIndex],
marginAccessor,
t -> {
Node n = rowBaseline[rowIndex].get(t);
int c = getNodeColumnIndex(n);
int cs = getNodeColumnSpan(n);
if (cs == REMAINING) {
cs = widths.getLength() - c;
}
double w = widths.getSize(c);
for (int j = 2; j <= cs; j++) {
w += widths.getSize(c + j - 1) + snaphgap;
}
return w;
},
areaH,
t -> {
Boolean b = isFillHeight(child);
if (b != null) {
return b;
}
return shouldRowFillHeight(getNodeRowIndex(child));
}, rowMinBaselineComplement[rowIndex]
);
}
baselineOffset = baselineOffsets[rowIndex];
}
Insets margin = getMargin(child);
layoutInArea(child, areaX, areaY, areaW, areaH,
baselineOffset,
margin,
fillWidth, fillHeight,
halign, valign);
}
layoutGridLines(widths, heights, x, y, rowTotal, columnTotal);
currentHeights = heights;
currentWidths = widths;
} finally {
performingLayout = false;
}
}
private double adjustRowHeights(final CompositeSize heights, double height) {
assert(height != -1);
final double snapvgap = snapSpaceY(getVgap());
final double top = snapSpaceY(getInsets().getTop());
final double bottom = snapSpaceY(getInsets().getBottom());
final double vgaps = snapvgap * (getNumberOfRows() - 1);
final double contentHeight = height - top - bottom;
if (rowPercentTotal > 0) {
double remainder = 0;
for (int i = 0; i < rowPercentHeight.length; i++) {
if (rowPercentHeight[i] >= 0) {
double size = (contentHeight - vgaps) * (rowPercentHeight[i]/100);
double floor = Math.floor(size);
remainder += size - floor;
size = floor;
if (remainder >= 0.5) {
size++;
remainder = (-1.0) + remainder;
}
heights.setSize(i, size);
}
}
}
double rowTotal = heights.computeTotal();
if (rowPercentTotal < 100) {
double heightAvailable = height - top - bottom - rowTotal;
if (heightAvailable != 0) {
double remaining = growToMultiSpanPreferredHeights(heights, heightAvailable);
remaining = growOrShrinkRowHeights(heights, Priority.ALWAYS, remaining);
remaining = growOrShrinkRowHeights(heights, Priority.SOMETIMES, remaining);
rowTotal += (heightAvailable - remaining);
}
}
return rowTotal;
}
private double growToMultiSpanPreferredHeights(CompositeSize heights, double extraHeight) {
if (extraHeight <= 0) {
return extraHeight;
}
Set<Integer> rowsAlways = new TreeSet<>();
Set<Integer> rowsSometimes = new TreeSet<>();
Set<Integer> lastRows = new TreeSet<>();
for (Entry<Interval, Double> ms : heights.multiSizes()) {
final Interval interval = ms.getKey();
for (int i = interval.begin; i < interval.end; ++i) {
if (rowPercentHeight[i] < 0) {
switch (rowGrow[i]) {
case ALWAYS:
rowsAlways.add(i);
break;
case SOMETIMES:
rowsSometimes.add(i);
break;
}
}
}
if (rowPercentHeight[interval.end - 1] < 0) {
lastRows.add(interval.end - 1);
}
}
double remaining = extraHeight;
while (rowsAlways.size() > 0 && remaining > rowsAlways.size()) {
double rowPortion = Math.floor(remaining / rowsAlways.size());
for (Iterator<Integer> it = rowsAlways.iterator(); it.hasNext();) {
int i = it.next();
double maxOfRow = getRowMaxHeight(i);
double prefOfRow = getRowPrefHeight(i);
double actualPortion = rowPortion;
for (Entry<Interval, Double> ms : heights.multiSizes()) {
final Interval interval = ms.getKey();
if (interval.contains(i)) {
int intervalRows = 0;
for (int j = interval.begin; j < interval.end; ++j) {
if (rowsAlways.contains(j)) {
intervalRows++;
}
}
double curLength = heights.computeTotal(interval.begin, interval.end);
actualPortion = Math.min(Math.floor(Math.max(0, (ms.getValue() - curLength) / intervalRows)),
actualPortion);
}
}
final double current = heights.getSize(i);
double bounded = maxOfRow >= 0 ? boundedSize(0, current + actualPortion, maxOfRow) :
maxOfRow == USE_PREF_SIZE && prefOfRow > 0 ? boundedSize(0, current + actualPortion, prefOfRow) :
current + actualPortion;
final double portionUsed = bounded - current;
remaining -= portionUsed;
if (portionUsed != actualPortion || portionUsed == 0) {
it.remove();
}
heights.setSize(i, bounded);
}
}
while (rowsSometimes.size() > 0 && remaining > rowsSometimes.size()) {
double colPortion = Math.floor(remaining / rowsSometimes.size());
for (Iterator<Integer> it = rowsSometimes.iterator(); it.hasNext();) {
int i = it.next();
double maxOfRow = getRowMaxHeight(i);
double prefOfRow = getRowPrefHeight(i);
double actualPortion = colPortion;
for (Entry<Interval, Double> ms : heights.multiSizes()) {
final Interval interval = ms.getKey();
if (interval.contains(i)) {
int intervalRows = 0;
for (int j = interval.begin; j < interval.end; ++j) {
if (rowsSometimes.contains(j)) {
intervalRows++;
}
}
double curLength = heights.computeTotal(interval.begin, interval.end);
actualPortion = Math.min(Math.floor(Math.max(0, (ms.getValue() - curLength) / intervalRows)),
actualPortion);
}
}
final double current = heights.getSize(i);
double bounded = maxOfRow >= 0 ? boundedSize(0, current + actualPortion, maxOfRow) :
maxOfRow == USE_PREF_SIZE && prefOfRow > 0 ? boundedSize(0, current + actualPortion, prefOfRow) :
current + actualPortion;
final double portionUsed = bounded - current;
remaining -= portionUsed;
if (portionUsed != actualPortion || portionUsed == 0) {
it.remove();
}
heights.setSize(i, bounded);
}
}
while (lastRows.size() > 0 && remaining > lastRows.size()) {
double colPortion = Math.floor(remaining / lastRows.size());
for (Iterator<Integer> it = lastRows.iterator(); it.hasNext();) {
int i = it.next();
double maxOfRow = getRowMaxHeight(i);
double prefOfRow = getRowPrefHeight(i);
double actualPortion = colPortion;
for (Entry<Interval, Double> ms : heights.multiSizes()) {
final Interval interval = ms.getKey();
if (interval.end - 1 == i) {
double curLength = heights.computeTotal(interval.begin, interval.end);
actualPortion = Math.min(Math.max(0, ms.getValue() - curLength),
actualPortion);
}
}
final double current = heights.getSize(i);
double bounded = maxOfRow >= 0 ? boundedSize(0, current + actualPortion, maxOfRow) :
maxOfRow == USE_PREF_SIZE && prefOfRow > 0 ? boundedSize(0, current + actualPortion, prefOfRow) :
current + actualPortion;
final double portionUsed = bounded - current;
remaining -= portionUsed;
if (portionUsed != actualPortion || portionUsed == 0) {
it.remove();
}
heights.setSize(i, bounded);
}
}
return remaining;
}
private double growOrShrinkRowHeights(CompositeSize heights, Priority priority, double extraHeight) {
final boolean shrinking = extraHeight < 0;
List<Integer> adjusting = new ArrayList<>();
for (int i = 0; i < rowGrow.length; i++) {
if (rowPercentHeight[i] < 0 && (shrinking || rowGrow[i] == priority)) {
adjusting.add(i);
}
}
double available = extraHeight;
boolean handleRemainder = false;
double portion = 0;
final boolean wasPositive = available >= 0.0;
boolean isPositive = wasPositive;
CompositeSize limitSize = shrinking? computeMinHeights(null) :
computeMaxHeights();
while (available != 0 && wasPositive == isPositive && adjusting.size() > 0) {
if (!handleRemainder) {
portion = available > 0 ? Math.floor(available / adjusting.size()) :
Math.ceil(available / adjusting.size());
}
if (portion != 0) {
for (Iterator<Integer> i = adjusting.iterator(); i.hasNext();) {
final int index = i.next();
double limit = snapSpaceY(limitSize.getProportionalMinOrMaxSize(index, shrinking))
- heights.getSize(index);
if (shrinking && limit > 0
|| !shrinking && limit < 0) {
limit = 0;
}
final double change = Math.abs(limit) <= Math.abs(portion)? limit : portion;
heights.addSize(index, change);
available -= change;
isPositive = available >= 0.0;
if (Math.abs(change) < Math.abs(portion)) {
i.remove();
}
if (available == 0) {
break;
}
}
} else {
portion = (int)(available) % adjusting.size();
if (portion == 0) {
break;
} else {
portion = shrinking ? -1 : 1;
handleRemainder = true;
}
}
}
return available;
}
private double adjustColumnWidths(final CompositeSize widths, double width) {
assert(width != -1);
final double snaphgap = snapSpaceX(getHgap());
final double left = snapSpaceX(getInsets().getLeft());
final double right = snapSpaceX(getInsets().getRight());
final double hgaps = snaphgap * (getNumberOfColumns() - 1);
final double contentWidth = width - left - right;
if (columnPercentTotal > 0) {
double remainder = 0;
for (int i = 0; i < columnPercentWidth.length; i++) {
if (columnPercentWidth[i] >= 0) {
double size = (contentWidth - hgaps) * (columnPercentWidth[i]/100);
double floor = Math.floor(size);
remainder += size - floor;
size = floor;
if (remainder >= 0.5) {
size++;
remainder = (-1.0) + remainder;
}
widths.setSize(i, size);
}
}
}
double columnTotal = widths.computeTotal();
if (columnPercentTotal < 100) {
double widthAvailable = width - left - right - columnTotal;
if (widthAvailable != 0) {
double remaining = growToMultiSpanPreferredWidths(widths, widthAvailable);
remaining = growOrShrinkColumnWidths(widths, Priority.ALWAYS, remaining);
remaining = growOrShrinkColumnWidths(widths, Priority.SOMETIMES, remaining);
columnTotal += (widthAvailable - remaining);
}
}
return columnTotal;
}
private double growToMultiSpanPreferredWidths(CompositeSize widths, double extraWidth) {
if (extraWidth <= 0) {
return extraWidth;
}
Set<Integer> columnsAlways = new TreeSet<>();
Set<Integer> columnsSometimes = new TreeSet<>();
Set<Integer> lastColumns = new TreeSet<>();
for (Entry<Interval, Double> ms : widths.multiSizes()) {
final Interval interval = ms.getKey();
for (int i = interval.begin; i < interval.end; ++i) {
if (columnPercentWidth[i] < 0) {
switch (columnGrow[i]) {
case ALWAYS:
columnsAlways.add(i);
break;
case SOMETIMES:
columnsSometimes.add(i);
break;
}
}
}
if (columnPercentWidth[interval.end - 1] < 0) {
lastColumns.add(interval.end - 1);
}
}
double remaining = extraWidth;
while (columnsAlways.size() > 0 && remaining > columnsAlways.size()) {
double colPortion = Math.floor(remaining / columnsAlways.size());
for (Iterator<Integer> it = columnsAlways.iterator(); it.hasNext();) {
int i = it.next();
double maxOfColumn = getColumnMaxWidth(i);
double prefOfColumn = getColumnPrefWidth(i);
double actualPortion = colPortion;
for (Entry<Interval, Double> ms : widths.multiSizes()) {
final Interval interval = ms.getKey();
if (interval.contains(i)) {
int intervalColumns = 0;
for (int j = interval.begin; j < interval.end; ++j) {
if (columnsAlways.contains(j)) {
intervalColumns++;
}
}
double curLength = widths.computeTotal(interval.begin, interval.end);
actualPortion = Math.min(Math.floor(Math.max(0, (ms.getValue() - curLength) / intervalColumns)),
actualPortion);
}
}
final double current = widths.getSize(i);
double bounded = maxOfColumn >= 0 ? boundedSize(0, current + actualPortion, maxOfColumn) :
maxOfColumn == USE_PREF_SIZE && prefOfColumn > 0 ? boundedSize(0, current + actualPortion, prefOfColumn) :
current + actualPortion;
final double portionUsed = bounded - current;
remaining -= portionUsed;
if (portionUsed != actualPortion || portionUsed == 0) {
it.remove();
}
widths.setSize(i, bounded);
}
}
while (columnsSometimes.size() > 0 && remaining > columnsSometimes.size()) {
double colPortion = Math.floor(remaining / columnsSometimes.size());
for (Iterator<Integer> it = columnsSometimes.iterator(); it.hasNext();) {
int i = it.next();
double maxOfColumn = getColumnMaxWidth(i);
double prefOfColumn = getColumnPrefWidth(i);
double actualPortion = colPortion;
for (Entry<Interval, Double> ms : widths.multiSizes()) {
final Interval interval = ms.getKey();
if (interval.contains(i)) {
int intervalColumns = 0;
for (int j = interval.begin; j < interval.end; ++j) {
if (columnsSometimes.contains(j)) {
intervalColumns++;
}
}
double curLength = widths.computeTotal(interval.begin, interval.end);
actualPortion = Math.min(Math.floor(Math.max(0, (ms.getValue() - curLength) / intervalColumns)),
actualPortion);
}
}
final double current = widths.getSize(i);
double bounded = maxOfColumn >= 0 ? boundedSize(0, current + actualPortion, maxOfColumn) :
maxOfColumn == USE_PREF_SIZE && prefOfColumn > 0 ? boundedSize(0, current + actualPortion, prefOfColumn) :
current + actualPortion;
final double portionUsed = bounded - current;
remaining -= portionUsed;
if (portionUsed != actualPortion || portionUsed == 0) {
it.remove();
}
widths.setSize(i, bounded);
}
}
while (lastColumns.size() > 0 && remaining > lastColumns.size()) {
double colPortion = Math.floor(remaining / lastColumns.size());
for (Iterator<Integer> it = lastColumns.iterator(); it.hasNext();) {
int i = it.next();
double maxOfColumn = getColumnMaxWidth(i);
double prefOfColumn = getColumnPrefWidth(i);
double actualPortion = colPortion;
for (Entry<Interval, Double> ms : widths.multiSizes()) {
final Interval interval = ms.getKey();
if (interval.end - 1 == i) {
double curLength = widths.computeTotal(interval.begin, interval.end);
actualPortion = Math.min(Math.max(0, ms.getValue() - curLength),
actualPortion);
}
}
final double current = widths.getSize(i);
double bounded = maxOfColumn >= 0 ? boundedSize(0, current + actualPortion, maxOfColumn) :
maxOfColumn == USE_PREF_SIZE && prefOfColumn > 0 ? boundedSize(0, current + actualPortion, prefOfColumn) :
current + actualPortion;
final double portionUsed = bounded - current;
remaining -= portionUsed;
if (portionUsed != actualPortion || portionUsed == 0) {
it.remove();
}
widths.setSize(i, bounded);
}
}
return remaining;
}
private double growOrShrinkColumnWidths(CompositeSize widths, Priority priority, double extraWidth) {
if (extraWidth == 0) {
return 0;
}
final boolean shrinking = extraWidth < 0;
List<Integer> adjusting = new ArrayList<>();
for (int i = 0; i < columnGrow.length; i++) {
if (columnPercentWidth[i] < 0 && (shrinking || columnGrow[i] == priority)) {
adjusting.add(i);
}
}
double available = extraWidth;
boolean handleRemainder = false;
double portion = 0;
final boolean wasPositive = available >= 0.0;
boolean isPositive = wasPositive;
CompositeSize limitSize = shrinking? computeMinWidths(null) :
computeMaxWidths();
while (available != 0 && wasPositive == isPositive && adjusting.size() > 0) {
if (!handleRemainder) {
portion = available > 0 ? Math.floor(available / adjusting.size()) :
Math.ceil(available / adjusting.size());
}
if (portion != 0) {
for (Iterator<Integer> i = adjusting.iterator(); i.hasNext();) {
final int index = i.next();
double limit = snapSpaceX(limitSize.getProportionalMinOrMaxSize(index, shrinking))
- widths.getSize(index);
if (shrinking && limit > 0
|| !shrinking && limit < 0) {
limit = 0;
}
final double change = Math.abs(limit) <= Math.abs(portion)? limit : portion;
widths.addSize(index, change);
available -= change;
isPositive = available >= 0.0;
if (Math.abs(change) < Math.abs(portion)) {
i.remove();
}
if (available == 0) {
break;
}
}
} else {
portion = (int)(available) % adjusting.size();
if (portion == 0) {
break;
} else {
portion = shrinking ? -1 : 1;
handleRemainder = true;
}
}
}
return available;
}
private void layoutGridLines(CompositeSize columnWidths, CompositeSize rowHeights, double x, double y, double columnHeight, double rowWidth) {
if (!isGridLinesVisible()) {
return;
}
if (!gridLines.getChildren().isEmpty()) {
gridLines.getChildren().clear();
}
double hGap = snapSpaceX(getHgap());
double vGap = snapSpaceY(getVgap());
double linex = x;
double liney = y;
for (int i = 0; i <= columnWidths.getLength(); i++) {
gridLines.getChildren().add(createGridLine(linex, liney, linex, liney + columnHeight));
if (i > 0 && i < columnWidths.getLength() && hGap != 0) {
linex += hGap;
gridLines.getChildren().add(createGridLine(linex, liney, linex, liney + columnHeight));
}
if (i < columnWidths.getLength()) {
linex += columnWidths.getSize(i);
}
}
linex = x;
for (int i = 0; i <= rowHeights.getLength(); i++) {
gridLines.getChildren().add(createGridLine(linex, liney, linex + rowWidth, liney));
if (i > 0 && i < rowHeights.getLength() && vGap != 0) {
liney += vGap;
gridLines.getChildren().add(createGridLine(linex, liney, linex + rowWidth, liney));
}
if (i < rowHeights.getLength()) {
liney += rowHeights.getSize(i);
}
}
}
private Line createGridLine(double startX, double startY, double endX, double endY) {
Line line = new Line();
line.setStartX(startX);
line.setStartY(startY);
line.setEndX(endX);
line.setEndY(endY);
line.setStroke(GRID_LINE_COLOR);
line.setStrokeDashOffset(GRID_LINE_DASH);
return line;
}
@Override public String toString() {
return "Grid hgap="+getHgap()+", vgap="+getVgap()+", alignment="+getAlignment();
}
private CompositeSize createCompositeRows(double initSize) {
return new CompositeSize(getNumberOfRows(), rowPercentHeight, rowPercentTotal,
snapSpaceY(getVgap()), initSize);
}
private CompositeSize createCompositeColumns(double initSize) {
return new CompositeSize(getNumberOfColumns(), columnPercentWidth, columnPercentTotal,
snapSpaceX(getHgap()), initSize);
}
private int getNodeRowEndConvertRemaining(Node child) {
int rowSpan = getNodeRowSpan(child);
return rowSpan != REMAINING? getNodeRowIndex(child) + rowSpan - 1 : getNumberOfRows() - 1;
}
private int getNodeColumnEndConvertRemaining(Node child) {
int columnSpan = getNodeColumnSpan(child);
return columnSpan != REMAINING? getNodeColumnIndex(child) + columnSpan - 1 : getNumberOfColumns() - 1;
}
private CompositeSize currentHeights;
private CompositeSize currentWidths;
double[][] getGrid() {
if (currentHeights == null || currentWidths == null) {
return null;
}
return new double[][] {currentWidths.asArray(), currentHeights.asArray()};
}
private static class StyleableProperties {
private static final CssMetaData<GridPane,Boolean> GRID_LINES_VISIBLE =
new CssMetaData<GridPane,Boolean>("-fx-grid-lines-visible",
BooleanConverter.getInstance(), Boolean.FALSE) {
@Override
public boolean isSettable(GridPane node) {
return node.gridLinesVisible == null ||
!node.gridLinesVisible.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(GridPane node) {
return (StyleableProperty<Boolean>)node.gridLinesVisibleProperty();
}
};
private static final CssMetaData<GridPane,Number> HGAP =
new CssMetaData<GridPane,Number>("-fx-hgap",
SizeConverter.getInstance(), 0.0){
@Override
public boolean isSettable(GridPane node) {
return node.hgap == null || !node.hgap.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(GridPane node) {
return (StyleableProperty<Number>)node.hgapProperty();
}
};
private static final CssMetaData<GridPane,Pos> ALIGNMENT =
new CssMetaData<GridPane,Pos>("-fx-alignment",
new EnumConverter<Pos>(Pos.class), Pos.TOP_LEFT) {
@Override
public boolean isSettable(GridPane node) {
return node.alignment == null || !node.alignment.isBound();
}
@Override
public StyleableProperty<Pos> getStyleableProperty(GridPane node) {
return (StyleableProperty<Pos>)node.alignmentProperty();
}
};
private static final CssMetaData<GridPane,Number> VGAP =
new CssMetaData<GridPane,Number>("-fx-vgap",
SizeConverter.getInstance(), 0.0){
@Override
public boolean isSettable(GridPane node) {
return node.vgap == null || !node.vgap.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(GridPane node) {
return (StyleableProperty<Number>)node.vgapProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Region.getClassCssMetaData());
styleables.add(GRID_LINES_VISIBLE);
styleables.add(HGAP);
styleables.add(ALIGNMENT);
styleables.add(VGAP);
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
@Override
public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return getClassCssMetaData();
}
private static final class Interval implements Comparable<Interval> {
public final int begin;
public final int end;
public Interval(int begin, int end) {
this.begin = begin;
this.end = end;
}
@Override
public int compareTo(Interval o) {
return begin != o.begin ? begin - o.begin : end - o.end;
}
private boolean contains(int position) {
return begin <= position && position < end;
}
private int size() {
return end - begin;
}
}
private static final class CompositeSize implements Cloneable {
double singleSizes[];
private SortedMap<Interval, Double> multiSizes;
private BitSet preset;
private final double fixedPercent[];
private final double totalFixedPercent;
private final double gap;
public CompositeSize(int capacity, double fixedPercent[], double totalFixedPercent, double gap, double initSize) {
singleSizes = new double[capacity];
Arrays.fill(singleSizes, initSize);
this.fixedPercent = fixedPercent;
this.totalFixedPercent = totalFixedPercent;
this.gap = gap;
}
private void setSize(int position, double size) {
singleSizes[position] = size;
}
private void setPresetSize(int position, double size) {
setSize(position, size);
if (preset == null) {
preset = new BitSet(singleSizes.length);
}
preset.set(position);
}
private boolean isPreset(int position) {
if (preset == null) {
return false;
}
return preset.get(position);
}
private void addSize(int position, double change) {
singleSizes[position] = singleSizes[position] + change;
}
private double getSize(int position) {
return singleSizes[position];
}
private void setMaxSize(int position, double size) {
singleSizes[position] = Math.max(singleSizes[position], size);
}
private void setMultiSize(int startPosition, int endPosition, double size) {
if (multiSizes == null) {
multiSizes = new TreeMap<>();
}
Interval i = new Interval(startPosition, endPosition);
multiSizes.put(i, size);
}
private Iterable<Entry<Interval, Double>> multiSizes() {
if (multiSizes == null) {
return Collections.EMPTY_LIST;
}
return multiSizes.entrySet();
}
private void setMaxMultiSize(int startPosition, int endPosition, double size) {
if (multiSizes == null) {
multiSizes = new TreeMap<>();
}
Interval i = new Interval(startPosition, endPosition);
Double sz = multiSizes.get(i);
if (sz == null) {
multiSizes.put(i, size);
} else {
multiSizes.put(i, Math.max(size, sz));
}
}
private double getProportionalMinOrMaxSize(int position, boolean min) {
double result = singleSizes[position];
if (!isPreset(position) && multiSizes != null) {
for (Interval i : multiSizes.keySet()) {
if (i.contains(position)) {
double segment = multiSizes.get(i) / i.size();
double propSize = segment;
for (int j = i.begin; j < i.end; ++j) {
if (j != position) {
if (min ? singleSizes[j] > segment : singleSizes[j] < segment) {
propSize += segment - singleSizes[j];
}
}
}
result = min ? Math.max(result, propSize) : Math.min(result, propSize);
}
}
}
return result;
}
private double computeTotal(final int from, final int to) {
double total = gap * (to - from - 1);
for (int i = from; i < to; ++i) {
total += singleSizes[i];
}
return total;
}
private double computeTotal() {
return computeTotal(0, singleSizes.length);
}
private boolean allPreset(int begin, int end) {
if (preset == null) {
return false;
}
for (int i = begin; i < end; ++i) {
if (!preset.get(i)) {
return false;
}
}
return true;
}
private double computeTotalWithMultiSize() {
double total = computeTotal();
if (multiSizes != null) {
for (Entry<Interval, Double> e: multiSizes.entrySet()) {
final Interval i = e.getKey();
if (!allPreset(i.begin, i.end)) {
double subTotal = computeTotal(i.begin, i.end);
if (e.getValue() > subTotal) {
total += e.getValue() - subTotal;
}
}
}
}
if (totalFixedPercent > 0) {
double totalNotFixed = 0;
for (int i = 0; i < fixedPercent.length; ++i) {
if (fixedPercent[i] == 0) {
total -= singleSizes[i];
}
}
for (int i = 0; i < fixedPercent.length; ++i) {
if (fixedPercent[i] > 0) {
total = Math.max(total, singleSizes[i] * (100 / fixedPercent[i]));
} else if (fixedPercent[i] < 0){
totalNotFixed += singleSizes[i];
}
}
if (totalFixedPercent < 100) {
total = Math.max(total, totalNotFixed * 100 / (100 - totalFixedPercent));
}
}
return total;
}
private int getLength() {
return singleSizes.length;
}
@Override
protected Object clone() {
try {
CompositeSize clone = (CompositeSize) super.clone();
clone.singleSizes = clone.singleSizes.clone();
if (multiSizes != null)
clone.multiSizes = new TreeMap<>(clone.multiSizes);
return clone;
} catch (CloneNotSupportedException ex) {
throw new RuntimeException(ex);
}
}
private double[] asArray() {
return singleSizes;
}
}
public final int getRowCount() {
int nRows = this.getRowConstraints().size();
for (int i = 0; i < this.getChildren().size(); i++) {
Node child = this.getChildren().get(i);
if (child.isManaged()) {
int rowIndex = GridPane.getNodeRowIndex(child);
int rowEnd = GridPane.getNodeRowEnd(child);
nRows = Math.max(nRows, (rowEnd != GridPane.REMAINING? rowEnd : rowIndex) + 1);
}
}
return nRows;
}
public final int getColumnCount() {
int nColumns = this.getColumnConstraints().size();
for (int i = 0; i < this.getChildren().size(); i++) {
Node child = this.getChildren().get(i);
if (child.isManaged()) {
int columnIndex = GridPane.getNodeColumnIndex(child);
int columnEnd = GridPane.getNodeColumnEnd(child);
nColumns = Math.max(nColumns, (columnEnd != GridPane.REMAINING? columnEnd : columnIndex) + 1);
}
}
return nColumns;
}
public final Bounds getCellBounds(int columnIndex, int rowIndex) {
final double snaphgap = this.snapSpaceX(this.getHgap());
final double snapvgap = this.snapSpaceY(this.getVgap());
final double top = this.snapSpaceY(this.getInsets().getTop());
final double right = this.snapSpaceX(this.getInsets().getRight());
final double bottom = this.snapSpaceY(this.getInsets().getBottom());
final double left = this.snapSpaceX(this.getInsets().getLeft());
final double gridPaneHeight = this.snapSizeY(this.getHeight()) - (top + bottom);
final double gridPaneWidth = this.snapSizeX(this.getWidth()) - (left + right);
double[] columnWidths;
double[] rowHeights;
double[][] grid = this.getGrid();
if (grid == null) {
rowHeights = new double[] {0};
rowIndex = 0;
columnWidths = new double[] {0};
columnIndex = 0;
} else {
columnWidths = grid[0];
rowHeights = grid[1];
}
double rowTotal = 0;
for (int i = 0; i < rowHeights.length; i++) {
rowTotal += rowHeights[i];
}
rowTotal += ((rowHeights.length - 1) * snapvgap);
double minY = top + Region.computeYOffset(gridPaneHeight, rowTotal, this.getAlignment().getVpos());
double height = rowHeights[rowIndex];
for (int j = 0; j < rowIndex; j++) {
minY += rowHeights[j] + snapvgap;
}
double columnTotal = 0;
for (int i = 0; i < columnWidths.length; i++) {
columnTotal += columnWidths[i];
}
columnTotal += ((columnWidths.length - 1) * snaphgap);
double minX = left + Region.computeXOffset(gridPaneWidth, columnTotal, this.getAlignment().getHpos());
double width = columnWidths[columnIndex];
for (int j = 0; j < columnIndex; j++) {
minX += columnWidths[j] + snaphgap;
}
return new BoundingBox(minX, minY, width, height);
}
}
