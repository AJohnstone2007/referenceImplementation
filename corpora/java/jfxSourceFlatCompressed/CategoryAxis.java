package javafx.scene.chart;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Dimension2D;
import javafx.geometry.Side;
import javafx.util.Duration;
import com.sun.javafx.charts.ChartLayoutAnimator;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.SizeConverter;
import java.util.Collections;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
public final class CategoryAxis extends Axis<String> {
private List<String> allDataCategories = new ArrayList<String>();
private boolean changeIsLocal = false;
private final DoubleProperty firstCategoryPos = new SimpleDoubleProperty(this, "firstCategoryPos", 0);
private Object currentAnimationID;
private final ChartLayoutAnimator animator = new ChartLayoutAnimator(this);
private ListChangeListener<String> itemsListener = c -> {
while (c.next()) {
if(!c.getAddedSubList().isEmpty()) {
for (String addedStr : c.getAddedSubList())
checkAndRemoveDuplicates(addedStr);
}
if (!isAutoRanging()) {
allDataCategories.clear();
allDataCategories.addAll(getCategories());
rangeValid = false;
}
requestAxisLayout();
}
};
private DoubleProperty startMargin = new StyleableDoubleProperty(5) {
@Override protected void invalidated() {
requestAxisLayout();
}
@Override public CssMetaData<CategoryAxis,Number> getCssMetaData() {
return StyleableProperties.START_MARGIN;
}
@Override
public Object getBean() {
return CategoryAxis.this;
}
@Override
public String getName() {
return "startMargin";
}
};
public final double getStartMargin() { return startMargin.getValue(); }
public final void setStartMargin(double value) { startMargin.setValue(value); }
public final DoubleProperty startMarginProperty() { return startMargin; }
private DoubleProperty endMargin = new StyleableDoubleProperty(5) {
@Override protected void invalidated() {
requestAxisLayout();
}
@Override public CssMetaData<CategoryAxis,Number> getCssMetaData() {
return StyleableProperties.END_MARGIN;
}
@Override
public Object getBean() {
return CategoryAxis.this;
}
@Override
public String getName() {
return "endMargin";
}
};
public final double getEndMargin() { return endMargin.getValue(); }
public final void setEndMargin(double value) { endMargin.setValue(value); }
public final DoubleProperty endMarginProperty() { return endMargin; }
private BooleanProperty gapStartAndEnd = new StyleableBooleanProperty(true) {
@Override protected void invalidated() {
requestAxisLayout();
}
@Override public CssMetaData<CategoryAxis,Boolean> getCssMetaData() {
return StyleableProperties.GAP_START_AND_END;
}
@Override
public Object getBean() {
return CategoryAxis.this;
}
@Override
public String getName() {
return "gapStartAndEnd";
}
};
public final boolean isGapStartAndEnd() { return gapStartAndEnd.getValue(); }
public final void setGapStartAndEnd(boolean value) { gapStartAndEnd.setValue(value); }
public final BooleanProperty gapStartAndEndProperty() { return gapStartAndEnd; }
private ObjectProperty<ObservableList<String>> categories = new ObjectPropertyBase<ObservableList<String>>() {
ObservableList<String> old;
@Override protected void invalidated() {
if (getDuplicate() != null) {
throw new IllegalArgumentException("Duplicate category added; "+getDuplicate()+" already present");
}
final ObservableList<String> newItems = get();
if (old != newItems) {
if (old != null) old.removeListener(itemsListener);
if (newItems != null) newItems.addListener(itemsListener);
old = newItems;
}
}
@Override
public Object getBean() {
return CategoryAxis.this;
}
@Override
public String getName() {
return "categories";
}
};
public final void setCategories(ObservableList<String> value) {
categories.set(value);
if (!changeIsLocal) {
setAutoRanging(false);
allDataCategories.clear();
allDataCategories.addAll(getCategories());
}
requestAxisLayout();
}
private void checkAndRemoveDuplicates(String category) {
if (getDuplicate() != null) {
getCategories().remove(category);
throw new IllegalArgumentException("Duplicate category ; "+category+" already present");
}
}
private String getDuplicate() {
if (getCategories() != null) {
for (int i = 0; i < getCategories().size(); i++) {
for (int j = 0; j < getCategories().size(); j++) {
if (getCategories().get(i).equals(getCategories().get(j)) && i != j) {
return getCategories().get(i);
}
}
}
}
return null;
}
public final ObservableList<String> getCategories() {
return categories.get();
}
private final ReadOnlyDoubleWrapper categorySpacing = new ReadOnlyDoubleWrapper(this, "categorySpacing", 1);
public final double getCategorySpacing() {
return categorySpacing.get();
}
public final ReadOnlyDoubleProperty categorySpacingProperty() {
return categorySpacing.getReadOnlyProperty();
}
public CategoryAxis() {
changeIsLocal = true;
setCategories(FXCollections.<String>observableArrayList());
changeIsLocal = false;
}
public CategoryAxis(ObservableList<String> categories) {
setCategories(categories);
}
private double calculateNewSpacing(double length, List<String> categories) {
final Side side = getEffectiveSide();
double newCategorySpacing = 1;
if(categories != null) {
double bVal = (isGapStartAndEnd() ? (categories.size()) : (categories.size() - 1));
newCategorySpacing = (bVal == 0) ? 1 : (length-getStartMargin()-getEndMargin()) / bVal;
}
if (!isAutoRanging()) categorySpacing.set(newCategorySpacing);
return newCategorySpacing;
}
private double calculateNewFirstPos(double length, double catSpacing) {
final Side side = getEffectiveSide();
double newPos = 1;
double offset = ((isGapStartAndEnd()) ? (catSpacing / 2) : (0));
if (side.isHorizontal()) {
newPos = 0 + getStartMargin() + offset;
} else {
newPos = length - getStartMargin() - offset;
}
if (!isAutoRanging()) firstCategoryPos.set(newPos);
return newPos;
}
@Override protected Object getRange() {
return new Object[]{ getCategories(), categorySpacing.get(), firstCategoryPos.get(), getEffectiveTickLabelRotation() };
}
@Override protected void setRange(Object range, boolean animate) {
Object[] rangeArray = (Object[]) range;
@SuppressWarnings({"unchecked"}) List<String> categories = (List<String>)rangeArray[0];
double newCategorySpacing = (Double)rangeArray[1];
double newFirstCategoryPos = (Double)rangeArray[2];
setEffectiveTickLabelRotation((Double)rangeArray[3]);
changeIsLocal = true;
setCategories(FXCollections.<String>observableArrayList(categories));
changeIsLocal = false;
if (animate) {
animator.stop(currentAnimationID);
currentAnimationID = animator.animate(
new KeyFrame(Duration.ZERO,
new KeyValue(firstCategoryPos, firstCategoryPos.get()),
new KeyValue(categorySpacing, categorySpacing.get())
),
new KeyFrame(Duration.millis(1000),
new KeyValue(firstCategoryPos,newFirstCategoryPos),
new KeyValue(categorySpacing,newCategorySpacing)
)
);
} else {
categorySpacing.set(newCategorySpacing);
firstCategoryPos.set(newFirstCategoryPos);
}
}
@Override protected Object autoRange(double length) {
final Side side = getEffectiveSide();
final double newCategorySpacing = calculateNewSpacing(length,allDataCategories);
final double newFirstPos = calculateNewFirstPos(length, newCategorySpacing);
double tickLabelRotation = getTickLabelRotation();
if (length >= 0) {
double requiredLengthToDisplay = calculateRequiredSize(side.isVertical(), tickLabelRotation);
if (requiredLengthToDisplay > length) {
if (side.isHorizontal() && tickLabelRotation != 90) {
tickLabelRotation = 90;
}
if (side.isVertical() && tickLabelRotation != 0) {
tickLabelRotation = 0;
}
}
}
return new Object[]{allDataCategories, newCategorySpacing, newFirstPos, tickLabelRotation};
}
private double calculateRequiredSize(boolean axisVertical, double tickLabelRotation) {
double maxReqTickGap = 0;
double last = 0;
boolean first = true;
for (String category: allDataCategories) {
Dimension2D textSize = measureTickMarkSize(category, tickLabelRotation);
double size = (axisVertical || (tickLabelRotation != 0)) ? textSize.getHeight() : textSize.getWidth();
if (first) {
first = false;
last = size/2;
} else {
maxReqTickGap = Math.max(maxReqTickGap, last + 6 + (size/2) );
}
}
return getStartMargin() + maxReqTickGap*allDataCategories.size() + getEndMargin();
}
@Override protected List<String> calculateTickValues(double length, Object range) {
Object[] rangeArray = (Object[]) range;
return (List<String>)rangeArray[0];
}
@Override protected String getTickMarkLabel(String value) {
return value;
}
@Override protected Dimension2D measureTickMarkSize(String value, Object range) {
final Object[] rangeArray = (Object[]) range;
final double tickLabelRotation = (Double)rangeArray[3];
return measureTickMarkSize(value,tickLabelRotation);
}
@Override public void invalidateRange(List<String> data) {
super.invalidateRange(data);
List<String> categoryNames = new ArrayList<String>();
categoryNames.addAll(allDataCategories);
for(String cat : allDataCategories) {
if (!data.contains(cat)) categoryNames.remove(cat);
}
for (int i = 0; i < data.size(); i++) {
int len = categoryNames.size();
if (!categoryNames.contains(data.get(i))) categoryNames.add((i > len) ? len : i, data.get(i));
}
allDataCategories.clear();
allDataCategories.addAll(categoryNames);
}
final List<String> getAllDataCategories() {
return allDataCategories;
}
@Override public double getDisplayPosition(String value) {
final ObservableList<String> cat = getCategories();
if (!cat.contains(value)) {
return Double.NaN;
}
if (getEffectiveSide().isHorizontal()) {
return firstCategoryPos.get() + cat.indexOf(value) * categorySpacing.get();
} else {
return firstCategoryPos.get() + cat.indexOf(value) * categorySpacing.get() * -1;
}
}
@Override public String getValueForDisplay(double displayPosition) {
if (getEffectiveSide().isHorizontal()) {
if (displayPosition < 0 || displayPosition > getWidth()) return null;
double d = (displayPosition - firstCategoryPos.get()) / categorySpacing.get();
return toRealValue(d);
} else {
if (displayPosition < 0 || displayPosition > getHeight()) return null;
double d = (displayPosition - firstCategoryPos.get()) / (categorySpacing.get() * -1);
return toRealValue(d);
}
}
@Override public boolean isValueOnAxis(String value) {
return getCategories().indexOf("" + value) != -1;
}
@Override public double toNumericValue(String value) {
return getCategories().indexOf(value);
}
@Override public String toRealValue(double value) {
int index = (int)Math.round(value);
List<String> categories = getCategories();
if (index >= 0 && index < categories.size()) {
return getCategories().get(index);
} else {
return null;
}
}
@Override public double getZeroPosition() {
return Double.NaN;
}
private static class StyleableProperties {
private static final CssMetaData<CategoryAxis,Number> START_MARGIN =
new CssMetaData<CategoryAxis,Number>("-fx-start-margin",
SizeConverter.getInstance(), 5.0) {
@Override
public boolean isSettable(CategoryAxis n) {
return n.startMargin == null || !n.startMargin.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(CategoryAxis n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.startMarginProperty();
}
};
private static final CssMetaData<CategoryAxis,Number> END_MARGIN =
new CssMetaData<CategoryAxis,Number>("-fx-end-margin",
SizeConverter.getInstance(), 5.0) {
@Override
public boolean isSettable(CategoryAxis n) {
return n.endMargin == null || !n.endMargin.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(CategoryAxis n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.endMarginProperty();
}
};
private static final CssMetaData<CategoryAxis,Boolean> GAP_START_AND_END =
new CssMetaData<CategoryAxis,Boolean>("-fx-gap-start-and-end",
BooleanConverter.getInstance(), Boolean.TRUE) {
@Override
public boolean isSettable(CategoryAxis n) {
return n.gapStartAndEnd == null || !n.gapStartAndEnd.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(CategoryAxis n) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)n.gapStartAndEndProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Axis.getClassCssMetaData());
styleables.add(START_MARGIN);
styleables.add(END_MARGIN);
styleables.add(GAP_START_AND_END);
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
