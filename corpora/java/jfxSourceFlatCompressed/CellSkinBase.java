package javafx.scene.control.skin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.css.StyleableDoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.StyleOrigin;
import javafx.css.converter.SizeConverter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;
public class CellSkinBase<C extends Cell> extends LabeledSkinBase<C> {
public CellSkinBase(final C control) {
super (control);
consumeMouseEvents(false);
}
private DoubleProperty cellSize;
public final double getCellSize() {
return cellSize == null ? DEFAULT_CELL_SIZE : cellSize.get();
}
public final ReadOnlyDoubleProperty cellSizeProperty() {
return cellSizePropertyImpl();
}
private DoubleProperty cellSizePropertyImpl() {
if (cellSize == null) {
cellSize = new StyleableDoubleProperty(DEFAULT_CELL_SIZE) {
@Override
public void applyStyle(StyleOrigin origin, Number value) {
double size = value == null ? DEFAULT_CELL_SIZE : value.doubleValue();
super.applyStyle(origin, size <= 0 ? DEFAULT_CELL_SIZE : size);
}
@Override public void set(double value) {
super.set(value);
getSkinnable().requestLayout();
}
@Override public Object getBean() {
return CellSkinBase.this;
}
@Override public String getName() {
return "cellSize";
}
@Override public CssMetaData<Cell<?>, Number> getCssMetaData() {
return StyleableProperties.CELL_SIZE;
}
};
}
return cellSize;
}
static final double DEFAULT_CELL_SIZE = 24.0;
private static class StyleableProperties {
private final static CssMetaData<Cell<?>,Number> CELL_SIZE =
new CssMetaData<Cell<?>,Number>("-fx-cell-size",
SizeConverter.getInstance(), DEFAULT_CELL_SIZE) {
@Override
public boolean isSettable(Cell<?> n) {
final CellSkinBase<?> skin = (CellSkinBase<?>) n.getSkin();
return skin.cellSize == null || !skin.cellSize.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(Cell<?> n) {
final CellSkinBase<?> skin = (CellSkinBase<?>) n.getSkin();
return (StyleableProperty<Number>)(WritableValue<Number>)skin.cellSizePropertyImpl();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(SkinBase.getClassCssMetaData());
styleables.add(CELL_SIZE);
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
@Override public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return getClassCssMetaData();
}
}
