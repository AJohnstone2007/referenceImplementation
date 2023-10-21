package javafx.scene.layout;
import com.sun.javafx.binding.ExpressionHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableIntegerProperty;
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
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.css.Styleable;
import static javafx.geometry.Orientation.*;
import javafx.util.Callback;
public class TilePane extends Pane {
private static final String MARGIN_CONSTRAINT = "tilepane-margin";
private static final String ALIGNMENT_CONSTRAINT = "tilepane-alignment";
public static void setAlignment(Node node, Pos value) {
setConstraint(node, ALIGNMENT_CONSTRAINT, value);
}
public static Pos getAlignment(Node node) {
return (Pos)getConstraint(node, ALIGNMENT_CONSTRAINT);
}
public static void setMargin(Node node, Insets value) {
setConstraint(node, MARGIN_CONSTRAINT, value);
}
public static Insets getMargin(Node node) {
return (Insets)getConstraint(node, MARGIN_CONSTRAINT);
}
private static final Callback<Node, Insets> marginAccessor = n -> getMargin(n);
public static void clearConstraints(Node child) {
setAlignment(child, null);
setMargin(child, null);
}
private double _tileWidth = -1;
private double _tileHeight = -1;
public TilePane() {
super();
}
public TilePane(Orientation orientation) {
super();
setOrientation(orientation);
}
public TilePane(double hgap, double vgap) {
super();
setHgap(hgap);
setVgap(vgap);
}
public TilePane(Orientation orientation, double hgap, double vgap) {
this();
setOrientation(orientation);
setHgap(hgap);
setVgap(vgap);
}
public TilePane(Node... children) {
super();
getChildren().addAll(children);
}
public TilePane(Orientation orientation, Node... children) {
super();
setOrientation(orientation);
getChildren().addAll(children);
}
public TilePane(double hgap, double vgap, Node... children) {
super();
setHgap(hgap);
setVgap(vgap);
getChildren().addAll(children);
}
public TilePane(Orientation orientation, double hgap, double vgap, Node... children) {
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
public CssMetaData<TilePane, Orientation> getCssMetaData() {
return StyleableProperties.ORIENTATION;
}
@Override
public Object getBean() {
return TilePane.this;
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
public final IntegerProperty prefRowsProperty() {
if (prefRows == null) {
prefRows = new StyleableIntegerProperty(5) {
@Override
public void invalidated() {
requestLayout();
}
@Override
public CssMetaData<TilePane, Number> getCssMetaData() {
return StyleableProperties.PREF_ROWS;
}
@Override
public Object getBean() {
return TilePane.this;
}
@Override
public String getName() {
return "prefRows";
}
};
}
return prefRows;
}
private IntegerProperty prefRows;
public final void setPrefRows(int value) { prefRowsProperty().set(value); }
public final int getPrefRows() { return prefRows == null ? 5 : prefRows.get(); }
public final IntegerProperty prefColumnsProperty() {
if (prefColumns == null) {
prefColumns = new StyleableIntegerProperty(5) {
@Override
public void invalidated() {
requestLayout();
}
@Override
public CssMetaData<TilePane, Number> getCssMetaData() {
return StyleableProperties.PREF_COLUMNS;
}
@Override
public Object getBean() {
return TilePane.this;
}
@Override
public String getName() {
return "prefColumns";
}
};
}
return prefColumns;
}
private IntegerProperty prefColumns;
public final void setPrefColumns(int value) { prefColumnsProperty().set(value); }
public final int getPrefColumns() { return prefColumns == null ? 5 : prefColumns.get(); }
public final DoubleProperty prefTileWidthProperty() {
if (prefTileWidth == null) {
prefTileWidth = new StyleableDoubleProperty(USE_COMPUTED_SIZE) {
@Override
public void invalidated() {
requestLayout();
}
@Override
public CssMetaData<TilePane, Number> getCssMetaData() {
return StyleableProperties.PREF_TILE_WIDTH;
}
@Override
public Object getBean() {
return TilePane.this;
}
@Override
public String getName() {
return "prefTileWidth";
}
};
}
return prefTileWidth;
}
private DoubleProperty prefTileWidth;
public final void setPrefTileWidth(double value) { prefTileWidthProperty().set(value); }
public final double getPrefTileWidth() { return prefTileWidth == null ? USE_COMPUTED_SIZE : prefTileWidth.get(); }
public final DoubleProperty prefTileHeightProperty() {
if (prefTileHeight == null) {
prefTileHeight = new StyleableDoubleProperty(USE_COMPUTED_SIZE) {
@Override
public void invalidated() {
requestLayout();
}
@Override
public CssMetaData<TilePane, Number> getCssMetaData() {
return StyleableProperties.PREF_TILE_HEIGHT;
}
@Override
public Object getBean() {
return TilePane.this;
}
@Override
public String getName() {
return "prefTileHeight";
}
};
}
return prefTileHeight;
}
private DoubleProperty prefTileHeight;
public final void setPrefTileHeight(double value) { prefTileHeightProperty().set(value); }
public final double getPrefTileHeight() { return prefTileHeight == null ? USE_COMPUTED_SIZE : prefTileHeight.get(); }
public final ReadOnlyDoubleProperty tileWidthProperty() {
if (tileWidth == null) {
tileWidth = new TileSizeProperty("tileWidth", _tileWidth) {
@Override
public double compute() {
return computeTileWidth();
}
};
}
return tileWidth;
}
private TileSizeProperty tileWidth;
private void invalidateTileWidth() {
if (tileWidth != null) {
tileWidth.invalidate();
} else {
_tileWidth = -1;
}
}
public final double getTileWidth() {
if (tileWidth != null) {
return tileWidth.get();
}
if (_tileWidth == -1) {
_tileWidth = computeTileWidth();
}
return _tileWidth;
}
public final ReadOnlyDoubleProperty tileHeightProperty() {
if (tileHeight == null) {
tileHeight = new TileSizeProperty("tileHeight", _tileHeight) {
@Override
public double compute() {
return computeTileHeight();
}
};
}
return tileHeight;
}
private TileSizeProperty tileHeight;
private void invalidateTileHeight() {
if (tileHeight != null) {
tileHeight.invalidate();
} else {
_tileHeight = -1;
}
}
public final double getTileHeight() {
if (tileHeight != null) {
return tileHeight.get();
}
if (_tileHeight == -1) {
_tileHeight = computeTileHeight();
}
return _tileHeight;
}
public final DoubleProperty hgapProperty() {
if (hgap == null) {
hgap = new StyleableDoubleProperty() {
@Override
public void invalidated() {
requestLayout();
}
@Override
public CssMetaData<TilePane, Number> getCssMetaData() {
return StyleableProperties.HGAP;
}
@Override
public Object getBean() {
return TilePane.this;
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
public CssMetaData<TilePane, Number> getCssMetaData() {
return StyleableProperties.VGAP;
}
@Override
public Object getBean() {
return TilePane.this;
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
public CssMetaData<TilePane, Pos> getCssMetaData() {
return StyleableProperties.ALIGNMENT;
}
@Override
public Object getBean() {
return TilePane.this;
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
public final ObjectProperty<Pos> tileAlignmentProperty() {
if (tileAlignment == null) {
tileAlignment = new StyleableObjectProperty<Pos>(Pos.CENTER) {
@Override
public void invalidated() {
requestLayout();
}
@Override
public CssMetaData<TilePane, Pos> getCssMetaData() {
return StyleableProperties.TILE_ALIGNMENT;
}
@Override
public Object getBean() {
return TilePane.this;
}
@Override
public String getName() {
return "tileAlignment";
}
};
}
return tileAlignment;
}
private ObjectProperty<Pos> tileAlignment;
public final void setTileAlignment(Pos value) { tileAlignmentProperty().set(value); }
public final Pos getTileAlignment() { return tileAlignment == null ? Pos.CENTER : tileAlignment.get(); }
private Pos getTileAlignmentInternal() {
Pos localPos = getTileAlignment();
return localPos == null ? Pos.CENTER : localPos;
}
@Override public Orientation getContentBias() {
return getOrientation();
}
@Override public void requestLayout() {
invalidateTileWidth();
invalidateTileHeight();
super.requestLayout();
}
@Override protected double computeMinWidth(double height) {
if (getContentBias() == Orientation.HORIZONTAL) {
return getInsets().getLeft() + getTileWidth() + getInsets().getRight();
}
return computePrefWidth(height);
}
@Override protected double computeMinHeight(double width) {
if (getContentBias() == Orientation.VERTICAL) {
return getInsets().getTop() + getTileHeight() + getInsets().getBottom();
}
return computePrefHeight(width);
}
@Override protected double computePrefWidth(double forHeight) {
List<Node> managed = getManagedChildren();
final Insets insets = getInsets();
int prefCols = 0;
if (forHeight != -1) {
int prefRows = computeRows(forHeight - snapSpaceY(insets.getTop()) - snapSpaceY(insets.getBottom()), getTileHeight());
prefCols = computeOther(managed.size(), prefRows);
} else {
prefCols = getOrientation() == HORIZONTAL? getPrefColumns() : computeOther(managed.size(), getPrefRows());
}
return snapSpaceX(insets.getLeft()) +
computeContentWidth(prefCols, getTileWidth()) +
snapSpaceX(insets.getRight());
}
@Override protected double computePrefHeight(double forWidth) {
List<Node> managed = getManagedChildren();
final Insets insets = getInsets();
int prefRows = 0;
if (forWidth != -1) {
int prefCols = computeColumns(forWidth - snapSpaceX(insets.getLeft()) - snapSpaceX(insets.getRight()), getTileWidth());
prefRows = computeOther(managed.size(), prefCols);
} else {
prefRows = getOrientation() == HORIZONTAL? computeOther(managed.size(), getPrefColumns()) : getPrefRows();
}
return snapSpaceY(insets.getTop()) +
computeContentHeight(prefRows, getTileHeight()) +
snapSpaceY(insets.getBottom());
}
private double computeTileWidth() {
List<Node> managed = getManagedChildren();
double preftilewidth = getPrefTileWidth();
if (preftilewidth == USE_COMPUTED_SIZE) {
double h = -1;
boolean vertBias = false;
for (int i = 0, size = managed.size(); i < size; i++) {
Node child = managed.get(i);
if (child.getContentBias() == VERTICAL) {
vertBias = true;
break;
}
}
if (vertBias) {
h = computeMaxPrefAreaHeight(managed, marginAccessor, -1, getTileAlignmentInternal().getVpos());
}
return snapSizeX(computeMaxPrefAreaWidth(managed, marginAccessor, h, true));
}
return snapSizeX(preftilewidth);
}
private double computeTileHeight() {
List<Node> managed = getManagedChildren();
double preftileheight = getPrefTileHeight();
if (preftileheight == USE_COMPUTED_SIZE) {
double w = -1;
boolean horizBias = false;
for (int i = 0, size = managed.size(); i < size; i++) {
Node child = managed.get(i);
if (child.getContentBias() == Orientation.HORIZONTAL) {
horizBias = true;
break;
}
}
if (horizBias) {
w = computeMaxPrefAreaWidth(managed, marginAccessor);
}
return snapSizeY(computeMaxPrefAreaHeight(managed, marginAccessor, w, getTileAlignmentInternal().getVpos()));
}
return snapSizeY(preftileheight);
}
private int computeOther(int numNodes, int numCells) {
double other = (double)numNodes/(double)Math.max(1, numCells);
return (int)Math.ceil(other);
}
private int computeColumns(double width, double tilewidth) {
double snappedHgap = snapSpaceX(getHgap());
return Math.max(1,(int)((width + snappedHgap) / (tilewidth + snappedHgap)));
}
private int computeRows(double height, double tileheight) {
double snappedVgap = snapSpaceY(getVgap());
return Math.max(1, (int)((height + snappedVgap) / (tileheight + snappedVgap)));
}
private double computeContentWidth(int columns, double tilewidth) {
if (columns == 0) return 0;
return columns * tilewidth + (columns - 1) * snapSpaceX(getHgap());
}
private double computeContentHeight(int rows, double tileheight) {
if (rows == 0) return 0;
return rows * tileheight + (rows - 1) * snapSpaceY(getVgap());
}
@Override protected void layoutChildren() {
List<Node> managed = getManagedChildren();
HPos hpos = getAlignmentInternal().getHpos();
VPos vpos = getAlignmentInternal().getVpos();
double width = getWidth();
double height = getHeight();
double top = snapSpaceY(getInsets().getTop());
double left = snapSpaceX(getInsets().getLeft());
double bottom = snapSpaceY(getInsets().getBottom());
double right = snapSpaceX(getInsets().getRight());
double vgap = snapSpaceY(getVgap());
double hgap = snapSpaceX(getHgap());
double insideWidth = width - left - right;
double insideHeight = height - top - bottom;
double tileWidth = getTileWidth() > insideWidth ? insideWidth : getTileWidth();
double tileHeight = getTileHeight() > insideHeight ? insideHeight : getTileHeight();
int lastRowRemainder = 0;
int lastColumnRemainder = 0;
if (getOrientation() == HORIZONTAL) {
actualColumns = computeColumns(insideWidth, tileWidth);
actualRows = computeOther(managed.size(), actualColumns);
lastRowRemainder = hpos != HPos.LEFT?
actualColumns - (actualColumns*actualRows - managed.size()) : 0;
} else {
actualRows = computeRows(insideHeight, tileHeight);
actualColumns = computeOther(managed.size(), actualRows);
lastColumnRemainder = vpos != VPos.TOP?
actualRows - (actualColumns*actualRows - managed.size()) : 0;
}
double rowX = left + computeXOffset(insideWidth,
computeContentWidth(actualColumns, tileWidth),
hpos);
double columnY = top + computeYOffset(insideHeight,
computeContentHeight(actualRows, tileHeight),
vpos);
double lastRowX = lastRowRemainder > 0?
left + computeXOffset(insideWidth,
computeContentWidth(lastRowRemainder, tileWidth),
hpos) : rowX;
double lastColumnY = lastColumnRemainder > 0?
top + computeYOffset(insideHeight,
computeContentHeight(lastColumnRemainder, tileHeight),
vpos) : columnY;
double baselineOffset = getTileAlignmentInternal().getVpos() == VPos.BASELINE ?
getAreaBaselineOffset(managed, marginAccessor, i -> tileWidth, tileHeight, false) : -1;
int r = 0;
int c = 0;
for (int i = 0, size = managed.size(); i < size; i++) {
Node child = managed.get(i);
double xoffset = r == (actualRows - 1)? lastRowX : rowX;
double yoffset = c == (actualColumns - 1)? lastColumnY : columnY;
double tileX = xoffset + (c * (tileWidth + hgap));
double tileY = yoffset + (r * (tileHeight + vgap));
Pos childAlignment = getAlignment(child);
layoutInArea(child, tileX, tileY, tileWidth, tileHeight, baselineOffset,
getMargin(child),
childAlignment != null? childAlignment.getHpos() : getTileAlignmentInternal().getHpos(),
childAlignment != null? childAlignment.getVpos() : getTileAlignmentInternal().getVpos());
if (getOrientation() == HORIZONTAL) {
if (++c == actualColumns) {
c = 0;
r++;
}
} else {
if (++r == actualRows) {
r = 0;
c++;
}
}
}
}
private int actualRows = 0;
private int actualColumns = 0;
private static class StyleableProperties {
private static final CssMetaData<TilePane,Pos> ALIGNMENT =
new CssMetaData<TilePane,Pos>("-fx-alignment",
new EnumConverter<Pos>(Pos.class),
Pos.TOP_LEFT) {
@Override
public boolean isSettable(TilePane node) {
return node.alignment == null || !node.alignment.isBound();
}
@Override
public StyleableProperty<Pos> getStyleableProperty(TilePane node) {
return (StyleableProperty<Pos>)node.alignmentProperty();
}
};
private static final CssMetaData<TilePane,Number> PREF_COLUMNS =
new CssMetaData<TilePane,Number>("-fx-pref-columns",
SizeConverter.getInstance(), 5.0) {
@Override
public boolean isSettable(TilePane node) {
return node.prefColumns == null ||
!node.prefColumns.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(TilePane node) {
return (StyleableProperty<Number>)node.prefColumnsProperty();
}
};
private static final CssMetaData<TilePane,Number> HGAP =
new CssMetaData<TilePane,Number>("-fx-hgap",
SizeConverter.getInstance(), 0.0) {
@Override
public boolean isSettable(TilePane node) {
return node.hgap == null ||
!node.hgap.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(TilePane node) {
return (StyleableProperty<Number>)node.hgapProperty();
}
};
private static final CssMetaData<TilePane,Number> PREF_ROWS =
new CssMetaData<TilePane,Number>("-fx-pref-rows",
SizeConverter.getInstance(), 5.0) {
@Override
public boolean isSettable(TilePane node) {
return node.prefRows == null ||
!node.prefRows.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(TilePane node) {
return (StyleableProperty<Number>)node.prefRowsProperty();
}
};
private static final CssMetaData<TilePane,Pos> TILE_ALIGNMENT =
new CssMetaData<TilePane,Pos>("-fx-tile-alignment",
new EnumConverter<Pos>(Pos.class),
Pos.CENTER) {
@Override
public boolean isSettable(TilePane node) {
return node.tileAlignment == null ||
!node.tileAlignment.isBound();
}
@Override
public StyleableProperty<Pos> getStyleableProperty(TilePane node) {
return (StyleableProperty<Pos>)node.tileAlignmentProperty();
}
};
private static final CssMetaData<TilePane,Number> PREF_TILE_WIDTH =
new CssMetaData<TilePane,Number>("-fx-pref-tile-width",
SizeConverter.getInstance(), USE_COMPUTED_SIZE) {
@Override
public boolean isSettable(TilePane node) {
return node.prefTileWidth == null ||
!node.prefTileWidth.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(TilePane node) {
return (StyleableProperty<Number>)node.prefTileWidthProperty();
}
};
private static final CssMetaData<TilePane,Number> PREF_TILE_HEIGHT =
new CssMetaData<TilePane,Number>("-fx-pref-tile-height",
SizeConverter.getInstance(), USE_COMPUTED_SIZE) {
@Override
public boolean isSettable(TilePane node) {
return node.prefTileHeight == null ||
!node.prefTileHeight.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(TilePane node) {
return (StyleableProperty<Number>)node.prefTileHeightProperty();
}
};
private static final CssMetaData<TilePane,Orientation> ORIENTATION =
new CssMetaData<TilePane,Orientation>("-fx-orientation",
new EnumConverter<Orientation>(Orientation.class),
Orientation.HORIZONTAL) {
@Override
public Orientation getInitialValue(TilePane node) {
return node.getOrientation();
}
@Override
public boolean isSettable(TilePane node) {
return node.orientation == null ||
!node.orientation.isBound();
}
@Override
public StyleableProperty<Orientation> getStyleableProperty(TilePane node) {
return (StyleableProperty<Orientation>)node.orientationProperty();
}
};
private static final CssMetaData<TilePane,Number> VGAP =
new CssMetaData<TilePane,Number>("-fx-vgap",
SizeConverter.getInstance(), 0.0) {
@Override
public boolean isSettable(TilePane node) {
return node.vgap == null ||
!node.vgap.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(TilePane node) {
return (StyleableProperty<Number>)node.vgapProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Region.getClassCssMetaData());
styleables.add(ALIGNMENT);
styleables.add(HGAP);
styleables.add(ORIENTATION);
styleables.add(PREF_COLUMNS);
styleables.add(PREF_ROWS);
styleables.add(PREF_TILE_WIDTH);
styleables.add(PREF_TILE_HEIGHT);
styleables.add(TILE_ALIGNMENT);
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
private abstract class TileSizeProperty extends ReadOnlyDoubleProperty {
private final String name;
private ExpressionHelper<Number> helper;
private double value;
private boolean valid;
TileSizeProperty(String name, double initSize) {
this.name = name;
this.value = initSize;
this.valid = initSize != -1;
}
@Override
public Object getBean() {
return TilePane.this;
}
@Override
public String getName() {
return name;
}
@Override
public void addListener(InvalidationListener listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(InvalidationListener listener) {
helper = ExpressionHelper.removeListener(helper, listener);
}
@Override
public void addListener(ChangeListener<? super Number> listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(ChangeListener<? super Number> listener) {
helper = ExpressionHelper.removeListener(helper, listener);
}
@Override
public double get() {
if (!valid) {
value = compute();
valid = true;
}
return value;
}
public void invalidate() {
if (valid) {
valid = false;
ExpressionHelper.fireValueChangedEvent(helper);
}
}
public abstract double compute();
}
}
