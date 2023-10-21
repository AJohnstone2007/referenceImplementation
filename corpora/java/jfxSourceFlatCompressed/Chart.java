package javafx.scene.chart;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.beans.value.WritableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import com.sun.javafx.charts.ChartLayoutAnimator;
import com.sun.javafx.charts.Legend;
import com.sun.javafx.scene.NodeHelper;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
public abstract class Chart extends Region {
private static final int MIN_WIDTH_TO_LEAVE_FOR_CHART_CONTENT = 200;
private static final int MIN_HEIGHT_TO_LEAVE_FOR_CHART_CONTENT = 150;
private final Label titleLabel = new Label();
private final Pane chartContent = new Pane() {
@Override protected void layoutChildren() {
final double top = snappedTopInset();
final double left = snappedLeftInset();
final double bottom = snappedBottomInset();
final double right = snappedRightInset();
final double width = getWidth();
final double height = getHeight();
final double contentWidth = snapSizeX(width - (left + right));
final double contentHeight = snapSizeY(height - (top + bottom));
layoutChartChildren(snapPositionY(top), snapPositionX(left), contentWidth, contentHeight);
}
@Override public boolean usesMirroring() {
return useChartContentMirroring;
}
};
boolean useChartContentMirroring = true;
private final ChartLayoutAnimator animator = new ChartLayoutAnimator(chartContent);
private StringProperty title = new StringPropertyBase() {
@Override protected void invalidated() {
titleLabel.setText(get());
}
@Override
public Object getBean() {
return Chart.this;
}
@Override
public String getName() {
return "title";
}
};
public final String getTitle() { return title.get(); }
public final void setTitle(String value) { title.set(value); }
public final StringProperty titleProperty() { return title; }
private ObjectProperty<Side> titleSide = new StyleableObjectProperty<Side>(Side.TOP) {
@Override protected void invalidated() {
requestLayout();
}
@Override
public CssMetaData<Chart,Side> getCssMetaData() {
return StyleableProperties.TITLE_SIDE;
}
@Override
public Object getBean() {
return Chart.this;
}
@Override
public String getName() {
return "titleSide";
}
};
public final Side getTitleSide() { return titleSide.get(); }
public final void setTitleSide(Side value) { titleSide.set(value); }
public final ObjectProperty<Side> titleSideProperty() { return titleSide; }
private final ObjectProperty<Node> legend = new ObjectPropertyBase<Node>() {
private Node old = null;
@Override protected void invalidated() {
Node newLegend = get();
if (old != null) getChildren().remove(old);
if (newLegend != null) {
getChildren().add(newLegend);
newLegend.setVisible(isLegendVisible());
}
old = newLegend;
}
@Override
public Object getBean() {
return Chart.this;
}
@Override
public String getName() {
return "legend";
}
};
protected final Node getLegend() { return legend.getValue(); }
protected final void setLegend(Node value) { legend.setValue(value); }
protected final ObjectProperty<Node> legendProperty() { return legend; }
private final BooleanProperty legendVisible = new StyleableBooleanProperty(true) {
@Override protected void invalidated() {
requestLayout();
}
@Override
public CssMetaData<Chart,Boolean> getCssMetaData() {
return StyleableProperties.LEGEND_VISIBLE;
}
@Override
public Object getBean() {
return Chart.this;
}
@Override
public String getName() {
return "legendVisible";
}
};
public final boolean isLegendVisible() { return legendVisible.getValue(); }
public final void setLegendVisible(boolean value) { legendVisible.setValue(value); }
public final BooleanProperty legendVisibleProperty() { return legendVisible; }
private ObjectProperty<Side> legendSide = new StyleableObjectProperty<Side>(Side.BOTTOM) {
@Override protected void invalidated() {
final Side legendSide = get();
final Node legend = getLegend();
if(legend instanceof Legend) ((Legend)legend).setVertical(Side.LEFT.equals(legendSide) || Side.RIGHT.equals(legendSide));
requestLayout();
}
@Override
public CssMetaData<Chart,Side> getCssMetaData() {
return StyleableProperties.LEGEND_SIDE;
}
@Override
public Object getBean() {
return Chart.this;
}
@Override
public String getName() {
return "legendSide";
}
};
public final Side getLegendSide() { return legendSide.get(); }
public final void setLegendSide(Side value) { legendSide.set(value); }
public final ObjectProperty<Side> legendSideProperty() { return legendSide; }
private BooleanProperty animated = new SimpleBooleanProperty(this, "animated", true);
public final boolean getAnimated() { return animated.get(); }
public final void setAnimated(boolean value) { animated.set(value); }
public final BooleanProperty animatedProperty() { return animated; }
protected ObservableList<Node> getChartChildren() {
return chartContent.getChildren();
}
public Chart() {
titleLabel.setAlignment(Pos.CENTER);
titleLabel.focusTraversableProperty().bind(Platform.accessibilityActiveProperty());
getChildren().addAll(titleLabel, chartContent);
getStyleClass().add("chart");
titleLabel.getStyleClass().add("chart-title");
chartContent.getStyleClass().add("chart-content");
chartContent.setManaged(false);
}
void animate(KeyFrame...keyFrames) { animator.animate(keyFrames); }
protected void animate(Animation animation) { animator.animate(animation); }
protected void requestChartLayout() {
chartContent.requestLayout();
}
protected final boolean shouldAnimate(){
return getAnimated() && NodeHelper.isTreeShowing(this);
}
protected abstract void layoutChartChildren(double top, double left, double width, double height);
@Override protected void layoutChildren() {
double top = snappedTopInset();
double left = snappedLeftInset();
double bottom = snappedBottomInset();
double right = snappedRightInset();
final double width = getWidth();
final double height = getHeight();
if (getTitle() != null) {
titleLabel.setVisible(true);
if (getTitleSide().equals(Side.TOP)) {
final double titleHeight = snapSizeY(titleLabel.prefHeight(width-left-right));
titleLabel.resizeRelocate(left,top,width-left-right,titleHeight);
top += titleHeight;
} else if (getTitleSide().equals(Side.BOTTOM)) {
final double titleHeight = snapSizeY(titleLabel.prefHeight(width-left-right));
titleLabel.resizeRelocate(left,height-bottom-titleHeight,width-left-right,titleHeight);
bottom += titleHeight;
} else if (getTitleSide().equals(Side.LEFT)) {
final double titleWidth = snapSizeX(titleLabel.prefWidth(height-top-bottom));
titleLabel.resizeRelocate(left,top,titleWidth,height-top-bottom);
left += titleWidth;
} else if (getTitleSide().equals(Side.RIGHT)) {
final double titleWidth = snapSizeX(titleLabel.prefWidth(height-top-bottom));
titleLabel.resizeRelocate(width-right-titleWidth,top,titleWidth,height-top-bottom);
right += titleWidth;
}
} else {
titleLabel.setVisible(false);
}
final Node legend = getLegend();
if (legend != null) {
boolean shouldShowLegend = isLegendVisible();
if (shouldShowLegend) {
if (getLegendSide() == Side.TOP) {
final double legendHeight = snapSizeY(legend.prefHeight(width-left-right));
final double legendWidth = Utils.boundedSize(snapSizeX(legend.prefWidth(legendHeight)), 0, width - left - right);
legend.resizeRelocate(left + (((width - left - right)-legendWidth)/2), top, legendWidth, legendHeight);
if ((height - bottom - top - legendHeight) < MIN_HEIGHT_TO_LEAVE_FOR_CHART_CONTENT) {
shouldShowLegend = false;
} else {
top += legendHeight;
}
} else if (getLegendSide() == Side.BOTTOM) {
final double legendHeight = snapSizeY(legend.prefHeight(width-left-right));
final double legendWidth = Utils.boundedSize(snapSizeX(legend.prefWidth(legendHeight)), 0, width - left - right);
legend.resizeRelocate(left + (((width - left - right)-legendWidth)/2), height-bottom-legendHeight, legendWidth, legendHeight);
if ((height - bottom - top - legendHeight) < MIN_HEIGHT_TO_LEAVE_FOR_CHART_CONTENT) {
shouldShowLegend = false;
} else {
bottom += legendHeight;
}
} else if (getLegendSide() == Side.LEFT) {
final double legendWidth = snapSizeX(legend.prefWidth(height-top-bottom));
final double legendHeight = Utils.boundedSize(snapSizeY(legend.prefHeight(legendWidth)), 0, height - top - bottom);
legend.resizeRelocate(left,top +(((height-top-bottom)-legendHeight)/2),legendWidth,legendHeight);
if ((width - left - right - legendWidth) < MIN_WIDTH_TO_LEAVE_FOR_CHART_CONTENT) {
shouldShowLegend = false;
} else {
left += legendWidth;
}
} else if (getLegendSide() == Side.RIGHT) {
final double legendWidth = snapSizeX(legend.prefWidth(height-top-bottom));
final double legendHeight = Utils.boundedSize(snapSizeY(legend.prefHeight(legendWidth)), 0, height - top - bottom);
legend.resizeRelocate(width-right-legendWidth,top +(((height-top-bottom)-legendHeight)/2),legendWidth,legendHeight);
if ((width - left - right - legendWidth) < MIN_WIDTH_TO_LEAVE_FOR_CHART_CONTENT) {
shouldShowLegend = false;
} else {
right += legendWidth;
}
}
}
legend.setVisible(shouldShowLegend);
}
chartContent.resizeRelocate(left,top,width-left-right,height-top-bottom);
}
@Override protected double computeMinHeight(double width) { return 150; }
@Override protected double computeMinWidth(double height) { return 200; }
@Override protected double computePrefWidth(double height) { return 500.0; }
@Override protected double computePrefHeight(double width) { return 400.0; }
private static class StyleableProperties {
private static final CssMetaData<Chart,Side> TITLE_SIDE =
new CssMetaData<Chart,Side>("-fx-title-side",
new EnumConverter<Side>(Side.class),
Side.TOP) {
@Override
public boolean isSettable(Chart node) {
return node.titleSide == null || !node.titleSide.isBound();
}
@Override
public StyleableProperty<Side> getStyleableProperty(Chart node) {
return (StyleableProperty<Side>)(WritableValue<Side>)node.titleSideProperty();
}
};
private static final CssMetaData<Chart,Side> LEGEND_SIDE =
new CssMetaData<Chart,Side>("-fx-legend-side",
new EnumConverter<Side>(Side.class),
Side.BOTTOM) {
@Override
public boolean isSettable(Chart node) {
return node.legendSide == null || !node.legendSide.isBound();
}
@Override
public StyleableProperty<Side> getStyleableProperty(Chart node) {
return (StyleableProperty<Side>)(WritableValue<Side>)node.legendSideProperty();
}
};
private static final CssMetaData<Chart,Boolean> LEGEND_VISIBLE =
new CssMetaData<Chart,Boolean>("-fx-legend-visible",
BooleanConverter.getInstance(), Boolean.TRUE) {
@Override
public boolean isSettable(Chart node) {
return node.legendVisible == null || !node.legendVisible.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(Chart node) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)node.legendVisibleProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Region.getClassCssMetaData());
styleables.add(TITLE_SIDE);
styleables.add(LEGEND_VISIBLE);
styleables.add(LEGEND_SIDE);
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
}
