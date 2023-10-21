package javafx.scene.control;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.DefaultProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.css.CssMetaData;
import javafx.css.StyleableIntegerProperty;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.util.Callback;
import javafx.css.converter.SizeConverter;
import javafx.scene.control.skin.PaginationSkin;
@DefaultProperty("pages")
public class Pagination extends Control {
private static final int DEFAULT_MAX_PAGE_INDICATOR_COUNT = 10;
public static final String STYLE_CLASS_BULLET = "bullet";
public static final int INDETERMINATE = Integer.MAX_VALUE;
public Pagination(int pageCount, int pageIndex) {
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.PAGINATION);
setPageCount(pageCount);
setCurrentPageIndex(pageIndex);
}
public Pagination(int pageCount) {
this(pageCount, 0);
}
public Pagination() {
this(INDETERMINATE, 0);
}
private int oldMaxPageIndicatorCount = DEFAULT_MAX_PAGE_INDICATOR_COUNT;
private IntegerProperty maxPageIndicatorCount;
public final void setMaxPageIndicatorCount(int value) { maxPageIndicatorCountProperty().set(value); }
public final int getMaxPageIndicatorCount() {
return maxPageIndicatorCount == null ? DEFAULT_MAX_PAGE_INDICATOR_COUNT : maxPageIndicatorCount.get();
}
public final IntegerProperty maxPageIndicatorCountProperty() {
if (maxPageIndicatorCount == null) {
maxPageIndicatorCount = new StyleableIntegerProperty(DEFAULT_MAX_PAGE_INDICATOR_COUNT) {
@Override protected void invalidated() {
if (!maxPageIndicatorCount.isBound()) {
if (getMaxPageIndicatorCount() < 1 || getMaxPageIndicatorCount() > getPageCount()) {
setMaxPageIndicatorCount(oldMaxPageIndicatorCount);
}
oldMaxPageIndicatorCount = getMaxPageIndicatorCount();
}
}
@Override
public CssMetaData<Pagination,Number> getCssMetaData() {
return StyleableProperties.MAX_PAGE_INDICATOR_COUNT;
}
@Override
public Object getBean() {
return Pagination.this;
}
@Override
public String getName() {
return "maxPageIndicatorCount";
}
};
}
return maxPageIndicatorCount;
}
private int oldPageCount = INDETERMINATE;
private IntegerProperty pageCount = new SimpleIntegerProperty(this, "pageCount", INDETERMINATE) {
@Override protected void invalidated() {
if (!pageCount.isBound()) {
if (getPageCount() < 1) {
setPageCount(oldPageCount);
}
oldPageCount = getPageCount();
}
}
};
public final void setPageCount(int value) { pageCount.set(value); }
public final int getPageCount() { return pageCount.get(); }
public final IntegerProperty pageCountProperty() { return pageCount; }
private final IntegerProperty currentPageIndex = new SimpleIntegerProperty(this, "currentPageIndex", 0) {
@Override protected void invalidated() {
if (!currentPageIndex.isBound()) {
if (getCurrentPageIndex() < 0) {
setCurrentPageIndex(0);
} else if (getCurrentPageIndex() > getPageCount() - 1) {
setCurrentPageIndex(getPageCount() - 1);
}
}
}
@Override
public void bind(ObservableValue<? extends Number> rawObservable) {
throw new UnsupportedOperationException("currentPageIndex supports only bidirectional binding");
}
};
public final void setCurrentPageIndex(int value) { currentPageIndex.set(value); }
public final int getCurrentPageIndex() { return currentPageIndex.get(); }
public final IntegerProperty currentPageIndexProperty() { return currentPageIndex; }
private ObjectProperty<Callback<Integer, Node>> pageFactory =
new SimpleObjectProperty<Callback<Integer, Node>>(this, "pageFactory");
public final void setPageFactory(Callback<Integer, Node> value) { pageFactory.set(value); }
public final Callback<Integer, Node> getPageFactory() {return pageFactory.get(); }
public final ObjectProperty<Callback<Integer, Node>> pageFactoryProperty() { return pageFactory; }
@Override protected Skin<?> createDefaultSkin() {
return new PaginationSkin(this);
}
private static final String DEFAULT_STYLE_CLASS = "pagination";
private static class StyleableProperties {
private static final CssMetaData<Pagination,Number> MAX_PAGE_INDICATOR_COUNT =
new CssMetaData<Pagination,Number>("-fx-max-page-indicator-count",
SizeConverter.getInstance(), DEFAULT_MAX_PAGE_INDICATOR_COUNT) {
@Override
public boolean isSettable(Pagination n) {
return n.maxPageIndicatorCount == null || !n.maxPageIndicatorCount.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(Pagination n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.maxPageIndicatorCountProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
styleables.add(MAX_PAGE_INDICATOR_COUNT);
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
@Override
public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
return getClassCssMetaData();
}
}
