package javafx.scene.layout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.css.Styleable;
import static javafx.geometry.Orientation.*;
import javafx.util.Callback;
public class FlowPane extends Pane {
private static final String MARGIN_CONSTRAINT = "flowpane-margin";
public static void setMargin(Node child, Insets value) {
setConstraint(child, MARGIN_CONSTRAINT, value);
}
public static Insets getMargin(Node child) {
return (Insets)getConstraint(child, MARGIN_CONSTRAINT);
}
private static final Callback<Node, Insets> marginAccessor = n -> getMargin(n);
public static void clearConstraints(Node child) {
setMargin(child, null);
}
public FlowPane() {
super();
}
public FlowPane(Orientation orientation) {
this();
setOrientation(orientation);
}
public FlowPane(double hgap, double vgap) {
this();
setHgap(hgap);
setVgap(vgap);
}
public FlowPane(Orientation orientation, double hgap, double vgap) {
this();
setOrientation(orientation);
setHgap(hgap);
setVgap(vgap);
}
public FlowPane(Node... children) {
super();
getChildren().addAll(children);
}
public FlowPane(Orientation orientation, Node... children) {
this();
setOrientation(orientation);
getChildren().addAll(children);
}
public FlowPane(double hgap, double vgap, Node... children) {
this();
setHgap(hgap);
setVgap(vgap);
getChildren().addAll(children);
}
public FlowPane(Orientation orientation, double hgap, double vgap, Node... children) {
this();
setOrientation(orientation);
setHgap(hgap);
setVgap(vgap);
getChildren().addAll(children);
}
public final ObjectProperty<Orientation> orientationProperty() {
if (orientation == null) {
orientation = new StyleableObjectProperty(HORIZONTAL) {
@Override
public void invalidated() {
requestLayout();
}
@Override
public CssMetaData<FlowPane, Orientation> getCssMetaData() {
return StyleableProperties.ORIENTATION;
}
@Override
public Object getBean() {
return FlowPane.this;
}
@Override
public String getName() {
return "orientation";
}
};
}
return orientation;
}
private ObjectProperty<Orientation> orientation;
public final void setOrientation(Orientation value) { orientationProperty().set(value); }
public final Orientation getOrientation() { return orientation == null ? HORIZONTAL : orientation.get(); }
public final DoubleProperty hgapProperty() {
if (hgap == null) {
hgap = new StyleableDoubleProperty() {
@Override
public void invalidated() {
requestLayout();
}
@Override
public CssMetaData<FlowPane, Number> getCssMetaData() {
return StyleableProperties.HGAP;
}
@Override
public Object getBean() {
return FlowPane.this;
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
vgap = new StyleableDoubleProperty() {
@Override
public void invalidated() {
requestLayout();
}
@Override
public CssMetaData<FlowPane, Number> getCssMetaData() {
return StyleableProperties.VGAP;
}
@Override
public Object getBean() {
return FlowPane.this;
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
public final DoubleProperty prefWrapLengthProperty() {
if (prefWrapLength == null) {
prefWrapLength = new DoublePropertyBase(400) {
@Override
protected void invalidated() {
requestLayout();
}
@Override
public Object getBean() {
return FlowPane.this;
}
@Override
public String getName() {
return "prefWrapLength";
}
};
}
return prefWrapLength;
}
private DoubleProperty prefWrapLength;
public final void setPrefWrapLength(double value) { prefWrapLengthProperty().set(value); }
public final double getPrefWrapLength() { return prefWrapLength == null ? 400 : prefWrapLength.get(); }
public final ObjectProperty<Pos> alignmentProperty() {
if (alignment == null) {
alignment = new StyleableObjectProperty<Pos>(Pos.TOP_LEFT) {
@Override
public void invalidated() {
requestLayout();
}
@Override
public CssMetaData<FlowPane, Pos> getCssMetaData() {
return StyleableProperties.ALIGNMENT;
}
@Override
public Object getBean() {
return FlowPane.this;
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
public final void setAlignment(Pos value) { alignmentProperty().set(value); }
public final Pos getAlignment() { return alignment == null ? Pos.TOP_LEFT : alignment.get(); }
private Pos getAlignmentInternal() {
Pos localPos = getAlignment();
return localPos == null ? Pos.TOP_LEFT : localPos;
}
public final ObjectProperty<HPos> columnHalignmentProperty() {
if (columnHalignment == null) {
columnHalignment = new StyleableObjectProperty<HPos>(HPos.LEFT) {
@Override
public void invalidated() {
requestLayout();
}
@Override
public CssMetaData<FlowPane, HPos> getCssMetaData() {
return StyleableProperties.COLUMN_HALIGNMENT;
}
@Override
public Object getBean() {
return FlowPane.this;
}
@Override
public String getName() {
return "columnHalignment";
}
};
}
return columnHalignment;
}
private ObjectProperty<HPos> columnHalignment;
public final void setColumnHalignment(HPos value) { columnHalignmentProperty().set(value); }
public final HPos getColumnHalignment() { return columnHalignment == null ? HPos.LEFT : columnHalignment.get(); }
private HPos getColumnHalignmentInternal() {
HPos localPos = getColumnHalignment();
return localPos == null ? HPos.LEFT : localPos;
}
public final ObjectProperty<VPos> rowValignmentProperty() {
if (rowValignment == null) {
rowValignment = new StyleableObjectProperty<VPos>(VPos.CENTER) {
@Override
public void invalidated() {
requestLayout();
}
@Override
public CssMetaData<FlowPane, VPos> getCssMetaData() {
return StyleableProperties.ROW_VALIGNMENT;
}
@Override
public Object getBean() {
return FlowPane.this;
}
@Override
public String getName() {
return "rowValignment";
}
};
}
return rowValignment;
}
private ObjectProperty<VPos> rowValignment;
public final void setRowValignment(VPos value) { rowValignmentProperty().set(value); }
public final VPos getRowValignment() { return rowValignment == null ? VPos.CENTER : rowValignment.get(); }
private VPos getRowValignmentInternal() {
VPos localPos = getRowValignment();
return localPos == null ? VPos.CENTER : localPos;
}
@Override public Orientation getContentBias() {
return getOrientation();
}
@Override protected double computeMinWidth(double height) {
if (getContentBias() == HORIZONTAL) {
double maxPref = 0;
final List<Node> children = getChildren();
for (int i=0, size=children.size(); i<size; i++) {
Node child = children.get(i);
if (child.isManaged()) {
maxPref = Math.max(maxPref, child.prefWidth(-1));
}
}
final Insets insets = getInsets();
return insets.getLeft() + snapSizeX(maxPref) + insets.getRight();
}
return computePrefWidth(height);
}
@Override protected double computeMinHeight(double width) {
if (getContentBias() == VERTICAL) {
double maxPref = 0;
final List<Node> children = getChildren();
for (int i=0, size=children.size(); i<size; i++) {
Node child = children.get(i);
if (child.isManaged()) {
maxPref = Math.max(maxPref, child.prefHeight(-1));
}
}
final Insets insets = getInsets();
return insets.getTop() + snapSizeY(maxPref) + insets.getBottom();
}
return computePrefHeight(width);
}
@Override protected double computePrefWidth(double forHeight) {
final Insets insets = getInsets();
if (getOrientation() == HORIZONTAL) {
double maxRunWidth = getPrefWrapLength();
List<Run> hruns = getRuns(maxRunWidth);
double w = computeContentWidth(hruns);
w = getPrefWrapLength() > w ? getPrefWrapLength() : w;
return insets.getLeft() + snapSizeX(w) + insets.getRight();
} else {
double maxRunHeight = forHeight != -1?
forHeight - insets.getTop() - insets.getBottom() : getPrefWrapLength();
List<Run> vruns = getRuns(maxRunHeight);
return insets.getLeft() + computeContentWidth(vruns) + insets.getRight();
}
}
@Override protected double computePrefHeight(double forWidth) {
final Insets insets = getInsets();
if (getOrientation() == HORIZONTAL) {
double maxRunWidth = forWidth != -1?
forWidth - insets.getLeft() - insets.getRight() : getPrefWrapLength();
List<Run> hruns = getRuns(maxRunWidth);
return insets.getTop() + computeContentHeight(hruns) + insets.getBottom();
} else {
double maxRunHeight = getPrefWrapLength();
List<Run> vruns = getRuns(maxRunHeight);
double h = computeContentHeight(vruns);
h = getPrefWrapLength() > h ? getPrefWrapLength() : h;
return insets.getTop() + snapSizeY(h) + insets.getBottom();
}
}
@Override public void requestLayout() {
if (!computingRuns) {
runs = null;
}
super.requestLayout();
}
private List<Run> runs = null;
private double lastMaxRunLength = -1;
boolean computingRuns = false;
private List<Run> getRuns(double maxRunLength) {
if (runs == null || maxRunLength != lastMaxRunLength) {
computingRuns = true;
lastMaxRunLength = maxRunLength;
runs = new ArrayList();
double runLength = 0;
double runOffset = 0;
Run run = new Run();
double vgap = snapSpaceY(this.getVgap());
double hgap = snapSpaceX(this.getHgap());
final List<Node> children = getChildren();
for (int i=0, size=children.size(); i<size; i++) {
Node child = children.get(i);
if (child.isManaged()) {
LayoutRect nodeRect = new LayoutRect();
nodeRect.node = child;
Insets margin = getMargin(child);
nodeRect.width = computeChildPrefAreaWidth(child, margin);
nodeRect.height = computeChildPrefAreaHeight(child, margin);
double nodeLength = getOrientation() == HORIZONTAL ? nodeRect.width : nodeRect.height;
if (runLength + nodeLength > maxRunLength && runLength > 0) {
normalizeRun(run, runOffset);
if (getOrientation() == HORIZONTAL) {
runOffset += run.height + vgap;
} else {
runOffset += run.width + hgap;
}
runs.add(run);
runLength = 0;
run = new Run();
}
if (getOrientation() == HORIZONTAL) {
nodeRect.x = runLength;
runLength += nodeRect.width + hgap;
} else {
nodeRect.y = runLength;
runLength += nodeRect.height + vgap;
}
run.rects.add(nodeRect);
}
}
normalizeRun(run, runOffset);
runs.add(run);
computingRuns = false;
}
return runs;
}
private void normalizeRun(final Run run, double runOffset) {
if (getOrientation() == HORIZONTAL) {
ArrayList<Node> rownodes = new ArrayList();
run.width = (run.rects.size()-1)*snapSpaceX(getHgap());
for (int i=0, max=run.rects.size(); i<max; i++) {
LayoutRect lrect = run.rects.get(i);
rownodes.add(lrect.node);
run.width += lrect.width;
lrect.y = runOffset;
}
run.height = computeMaxPrefAreaHeight(rownodes, marginAccessor, getRowValignment());
run.baselineOffset = getRowValignment() == VPos.BASELINE?
getAreaBaselineOffset(rownodes, marginAccessor, i -> run.rects.get(i).width, run.height, true) : 0;
} else {
run.height = (run.rects.size()-1)*snapSpaceY(getVgap());
double maxw = 0;
for (int i=0, max=run.rects.size(); i<max; i++) {
LayoutRect lrect = run.rects.get(i);
run.height += lrect.height;
lrect.x = runOffset;
maxw = Math.max(maxw, lrect.width);
}
run.width = maxw;
run.baselineOffset = run.height;
}
}
private double computeContentWidth(List<Run> runs) {
double cwidth = getOrientation() == HORIZONTAL ? 0 : (runs.size()-1)*snapSpaceX(getHgap());
for (int i=0, max=runs.size(); i<max; i++) {
Run run = runs.get(i);
if (getOrientation() == HORIZONTAL) {
cwidth = Math.max(cwidth, run.width);
} else {
cwidth += run.width;
}
}
return cwidth;
}
private double computeContentHeight(List<Run> runs) {
double cheight = getOrientation() == VERTICAL ? 0 : (runs.size()-1)*snapSpaceY(getVgap());
for (int i=0, max=runs.size(); i<max; i++) {
Run run = runs.get(i);
if (getOrientation() == VERTICAL) {
cheight = Math.max(cheight, run.height);
} else {
cheight += run.height;
}
}
return cheight;
}
@Override protected void layoutChildren() {
final Insets insets = getInsets();
final double width = getWidth();
final double height = getHeight();
final double top = insets.getTop();
final double left = insets.getLeft();
final double bottom = insets.getBottom();
final double right = insets.getRight();
final double insideWidth = width - left - right;
final double insideHeight = height - top - bottom;
final List<Run> runs = getRuns(getOrientation() == HORIZONTAL ? insideWidth : insideHeight);
for (int i=0, max=runs.size(); i<max; i++) {
final Run run = runs.get(i);
final double xoffset = left + computeXOffset(insideWidth,
getOrientation() == HORIZONTAL ? run.width : computeContentWidth(runs),
getAlignmentInternal().getHpos());
final double yoffset = top + computeYOffset(insideHeight,
getOrientation() == VERTICAL ? run.height : computeContentHeight(runs),
getAlignmentInternal().getVpos());
for (int j = 0; j < run.rects.size(); j++) {
final LayoutRect lrect = run.rects.get(j);
final double x = xoffset + lrect.x;
final double y = yoffset + lrect.y;
layoutInArea(lrect.node, x, y,
getOrientation() == HORIZONTAL? lrect.width : run.width,
getOrientation() == VERTICAL? lrect.height : run.height,
run.baselineOffset, getMargin(lrect.node),
getColumnHalignmentInternal(), getRowValignmentInternal());
}
}
}
private static class StyleableProperties {
private static final CssMetaData<FlowPane,Pos> ALIGNMENT =
new CssMetaData<FlowPane,Pos>("-fx-alignment",
new EnumConverter<Pos>(Pos.class), Pos.TOP_LEFT) {
@Override
public boolean isSettable(FlowPane node) {
return node.alignment == null || !node.alignment.isBound();
}
@Override
public StyleableProperty<Pos> getStyleableProperty(FlowPane node) {
return (StyleableProperty<Pos>)node.alignmentProperty();
}
};
private static final CssMetaData<FlowPane,HPos> COLUMN_HALIGNMENT =
new CssMetaData<FlowPane,HPos>("-fx-column-halignment",
new EnumConverter<HPos>(HPos.class), HPos.LEFT) {
@Override
public boolean isSettable(FlowPane node) {
return node.columnHalignment == null || !node.columnHalignment.isBound();
}
@Override
public StyleableProperty<HPos> getStyleableProperty(FlowPane node) {
return (StyleableProperty<HPos>)node.columnHalignmentProperty();
}
};
private static final CssMetaData<FlowPane,Number> HGAP =
new CssMetaData<FlowPane,Number>("-fx-hgap",
SizeConverter.getInstance(), 0.0){
@Override
public boolean isSettable(FlowPane node) {
return node.hgap == null || !node.hgap.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(FlowPane node) {
return (StyleableProperty<Number>)node.hgapProperty();
}
};
private static final CssMetaData<FlowPane,VPos> ROW_VALIGNMENT =
new CssMetaData<FlowPane,VPos>("-fx-row-valignment",
new EnumConverter<VPos>(VPos.class), VPos.CENTER) {
@Override
public boolean isSettable(FlowPane node) {
return node.rowValignment == null || !node.rowValignment.isBound();
}
@Override
public StyleableProperty<VPos> getStyleableProperty(FlowPane node) {
return (StyleableProperty<VPos>)node.rowValignmentProperty();
}
};
private static final CssMetaData<FlowPane,Orientation> ORIENTATION =
new CssMetaData<FlowPane,Orientation>("-fx-orientation",
new EnumConverter<Orientation>(Orientation.class),
Orientation.HORIZONTAL) {
@Override
public Orientation getInitialValue(FlowPane node) {
return node.getOrientation();
}
@Override
public boolean isSettable(FlowPane node) {
return node.orientation == null || !node.orientation.isBound();
}
@Override
public StyleableProperty<Orientation> getStyleableProperty(FlowPane node) {
return (StyleableProperty<Orientation>)node.orientationProperty();
}
};
private static final CssMetaData<FlowPane,Number> VGAP =
new CssMetaData<FlowPane,Number>("-fx-vgap",
SizeConverter.getInstance(), 0.0){
@Override
public boolean isSettable(FlowPane node) {
return node.vgap == null || !node.vgap.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(FlowPane node) {
return (StyleableProperty<Number>)node.vgapProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Region.getClassCssMetaData());
styleables.add(ALIGNMENT);
styleables.add(COLUMN_HALIGNMENT);
styleables.add(HGAP);
styleables.add(ROW_VALIGNMENT);
styleables.add(ORIENTATION);
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
private static class LayoutRect {
public Node node;
double x;
double y;
double width;
double height;
@Override public String toString() {
return "LayoutRect node id="+node.getId()+" "+x+","+y+" "+width+"x"+height;
}
}
private static class Run {
ArrayList<LayoutRect> rects = new ArrayList();
double width;
double height;
double baselineOffset;
}
}
