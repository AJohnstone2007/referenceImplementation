package javafx.scene.control;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import com.sun.javafx.collections.UnmodifiableListSet;
import com.sun.javafx.scene.control.TabObservableList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.geometry.Side;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.css.StyleableDoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.converter.SizeConverter;
import javafx.scene.control.skin.TabPaneSkin;
import javafx.beans.DefaultProperty;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.Node;
@DefaultProperty("tabs")
public class TabPane extends Control {
private static final double DEFAULT_TAB_MIN_WIDTH = 0;
private static final double DEFAULT_TAB_MAX_WIDTH = Double.MAX_VALUE;
private static final double DEFAULT_TAB_MIN_HEIGHT = 0;
private static final double DEFAULT_TAB_MAX_HEIGHT = Double.MAX_VALUE;
public static final String STYLE_CLASS_FLOATING = "floating";
public TabPane() {
this((Tab[])null);
}
public TabPane(Tab... tabs) {
getStyleClass().setAll("tab-pane");
setAccessibleRole(AccessibleRole.TAB_PANE);
setSelectionModel(new TabPaneSelectionModel(this));
this.tabs.addListener((ListChangeListener<Tab>) c -> {
while (c.next()) {
for (Tab tab : c.getRemoved()) {
if (tab != null && !getTabs().contains(tab)) {
tab.setTabPane(null);
}
}
for (Tab tab : c.getAddedSubList()) {
if (tab != null) {
tab.setTabPane(TabPane.this);
}
}
}
});
if (tabs != null) {
getTabs().addAll(tabs);
}
Side edge = getSide();
pseudoClassStateChanged(TOP_PSEUDOCLASS_STATE, (edge == Side.TOP));
pseudoClassStateChanged(RIGHT_PSEUDOCLASS_STATE, (edge == Side.RIGHT));
pseudoClassStateChanged(BOTTOM_PSEUDOCLASS_STATE, (edge == Side.BOTTOM));
pseudoClassStateChanged(LEFT_PSEUDOCLASS_STATE, (edge == Side.LEFT));
}
private ObservableList<Tab> tabs = new TabObservableList<>(new ArrayList<>());
public final ObservableList<Tab> getTabs() {
return tabs;
}
private ObjectProperty<SingleSelectionModel<Tab>> selectionModel = new SimpleObjectProperty<SingleSelectionModel<Tab>>(this, "selectionModel");
public final void setSelectionModel(SingleSelectionModel<Tab> value) { selectionModel.set(value); }
public final SingleSelectionModel<Tab> getSelectionModel() { return selectionModel.get(); }
public final ObjectProperty<SingleSelectionModel<Tab>> selectionModelProperty() { return selectionModel; }
private ObjectProperty<Side> side;
public final void setSide(Side value) {
sideProperty().set(value);
}
public final Side getSide() {
return side == null ? Side.TOP : side.get();
}
public final ObjectProperty<Side> sideProperty() {
if (side == null) {
side = new ObjectPropertyBase<Side>(Side.TOP) {
private Side oldSide;
@Override protected void invalidated() {
oldSide = get();
pseudoClassStateChanged(TOP_PSEUDOCLASS_STATE, (oldSide == Side.TOP || oldSide == null));
pseudoClassStateChanged(RIGHT_PSEUDOCLASS_STATE, (oldSide == Side.RIGHT));
pseudoClassStateChanged(BOTTOM_PSEUDOCLASS_STATE, (oldSide == Side.BOTTOM));
pseudoClassStateChanged(LEFT_PSEUDOCLASS_STATE, (oldSide == Side.LEFT));
}
@Override
public Object getBean() {
return TabPane.this;
}
@Override
public String getName() {
return "side";
}
};
}
return side;
}
private ObjectProperty<TabClosingPolicy> tabClosingPolicy;
public final void setTabClosingPolicy(TabClosingPolicy value) {
tabClosingPolicyProperty().set(value);
}
public final TabClosingPolicy getTabClosingPolicy() {
return tabClosingPolicy == null ? TabClosingPolicy.SELECTED_TAB : tabClosingPolicy.get();
}
public final ObjectProperty<TabClosingPolicy> tabClosingPolicyProperty() {
if (tabClosingPolicy == null) {
tabClosingPolicy = new SimpleObjectProperty<TabClosingPolicy>(this, "tabClosingPolicy", TabClosingPolicy.SELECTED_TAB);
}
return tabClosingPolicy;
}
private BooleanProperty rotateGraphic;
public final void setRotateGraphic(boolean value) {
rotateGraphicProperty().set(value);
}
public final boolean isRotateGraphic() {
return rotateGraphic == null ? false : rotateGraphic.get();
}
public final BooleanProperty rotateGraphicProperty() {
if (rotateGraphic == null) {
rotateGraphic = new SimpleBooleanProperty(this, "rotateGraphic", false);
}
return rotateGraphic;
}
private DoubleProperty tabMinWidth;
public final void setTabMinWidth(double value) {
tabMinWidthProperty().setValue(value);
}
public final double getTabMinWidth() {
return tabMinWidth == null ? DEFAULT_TAB_MIN_WIDTH : tabMinWidth.getValue();
}
public final DoubleProperty tabMinWidthProperty() {
if (tabMinWidth == null) {
tabMinWidth = new StyleableDoubleProperty(DEFAULT_TAB_MIN_WIDTH) {
@Override
public CssMetaData<TabPane,Number> getCssMetaData() {
return StyleableProperties.TAB_MIN_WIDTH;
}
@Override
public Object getBean() {
return TabPane.this;
}
@Override
public String getName() {
return "tabMinWidth";
}
};
}
return tabMinWidth;
}
private DoubleProperty tabMaxWidth;
public final void setTabMaxWidth(double value) {
tabMaxWidthProperty().setValue(value);
}
public final double getTabMaxWidth() {
return tabMaxWidth == null ? DEFAULT_TAB_MAX_WIDTH : tabMaxWidth.getValue();
}
public final DoubleProperty tabMaxWidthProperty() {
if (tabMaxWidth == null) {
tabMaxWidth = new StyleableDoubleProperty(DEFAULT_TAB_MAX_WIDTH) {
@Override
public CssMetaData<TabPane,Number> getCssMetaData() {
return StyleableProperties.TAB_MAX_WIDTH;
}
@Override
public Object getBean() {
return TabPane.this;
}
@Override
public String getName() {
return "tabMaxWidth";
}
};
}
return tabMaxWidth;
}
private DoubleProperty tabMinHeight;
public final void setTabMinHeight(double value) {
tabMinHeightProperty().setValue(value);
}
public final double getTabMinHeight() {
return tabMinHeight == null ? DEFAULT_TAB_MIN_HEIGHT : tabMinHeight.getValue();
}
public final DoubleProperty tabMinHeightProperty() {
if (tabMinHeight == null) {
tabMinHeight = new StyleableDoubleProperty(DEFAULT_TAB_MIN_HEIGHT) {
@Override
public CssMetaData<TabPane,Number> getCssMetaData() {
return StyleableProperties.TAB_MIN_HEIGHT;
}
@Override
public Object getBean() {
return TabPane.this;
}
@Override
public String getName() {
return "tabMinHeight";
}
};
}
return tabMinHeight;
}
private DoubleProperty tabMaxHeight;
public final void setTabMaxHeight(double value) {
tabMaxHeightProperty().setValue(value);
}
public final double getTabMaxHeight() {
return tabMaxHeight == null ? DEFAULT_TAB_MAX_HEIGHT : tabMaxHeight.getValue();
}
public final DoubleProperty tabMaxHeightProperty() {
if (tabMaxHeight == null) {
tabMaxHeight = new StyleableDoubleProperty(DEFAULT_TAB_MAX_HEIGHT) {
@Override
public CssMetaData<TabPane,Number> getCssMetaData() {
return StyleableProperties.TAB_MAX_HEIGHT;
}
@Override
public Object getBean() {
return TabPane.this;
}
@Override
public String getName() {
return "tabMaxHeight";
}
};
}
return tabMaxHeight;
}
@Override protected Skin<?> createDefaultSkin() {
return new TabPaneSkin(this);
}
@Override public Node lookup(String selector) {
Node n = super.lookup(selector);
if (n == null) {
for(Tab tab : tabs) {
n = tab.lookup(selector);
if (n != null) break;
}
}
return n;
}
public Set<Node> lookupAll(String selector) {
if (selector == null) return null;
final List<Node> results = new ArrayList<>();
results.addAll(super.lookupAll(selector));
for(Tab tab : tabs) {
results.addAll(tab.lookupAll(selector));
}
return new UnmodifiableListSet<Node>(results);
}
private static class StyleableProperties {
private static final CssMetaData<TabPane,Number> TAB_MIN_WIDTH =
new CssMetaData<TabPane,Number>("-fx-tab-min-width",
SizeConverter.getInstance(), DEFAULT_TAB_MIN_WIDTH) {
@Override
public boolean isSettable(TabPane n) {
return n.tabMinWidth == null || !n.tabMinWidth.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(TabPane n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.tabMinWidthProperty();
}
};
private static final CssMetaData<TabPane,Number> TAB_MAX_WIDTH =
new CssMetaData<TabPane,Number>("-fx-tab-max-width",
SizeConverter.getInstance(), DEFAULT_TAB_MAX_WIDTH) {
@Override
public boolean isSettable(TabPane n) {
return n.tabMaxWidth == null || !n.tabMaxWidth.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(TabPane n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.tabMaxWidthProperty();
}
};
private static final CssMetaData<TabPane,Number> TAB_MIN_HEIGHT =
new CssMetaData<TabPane,Number>("-fx-tab-min-height",
SizeConverter.getInstance(), DEFAULT_TAB_MIN_HEIGHT) {
@Override
public boolean isSettable(TabPane n) {
return n.tabMinHeight == null || !n.tabMinHeight.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(TabPane n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.tabMinHeightProperty();
}
};
private static final CssMetaData<TabPane,Number> TAB_MAX_HEIGHT =
new CssMetaData<TabPane,Number>("-fx-tab-max-height",
SizeConverter.getInstance(), DEFAULT_TAB_MAX_HEIGHT) {
@Override
public boolean isSettable(TabPane n) {
return n.tabMaxHeight == null || !n.tabMaxHeight.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(TabPane n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.tabMaxHeightProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
styleables.add(TAB_MIN_WIDTH);
styleables.add(TAB_MAX_WIDTH);
styleables.add(TAB_MIN_HEIGHT);
styleables.add(TAB_MAX_HEIGHT);
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
private static final PseudoClass TOP_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("top");
private static final PseudoClass BOTTOM_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("bottom");
private static final PseudoClass LEFT_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("left");
private static final PseudoClass RIGHT_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("right");
static class TabPaneSelectionModel extends SingleSelectionModel<Tab> {
private final TabPane tabPane;
private ListChangeListener<Tab> itemsContentObserver;
public TabPaneSelectionModel(final TabPane t) {
if (t == null) {
throw new NullPointerException("TabPane can not be null");
}
this.tabPane = t;
itemsContentObserver = c -> {
while (c.next()) {
for (Tab tab : c.getRemoved()) {
if (tab != null && !tabPane.getTabs().contains(tab)) {
if (tab.isSelected()) {
tab.setSelected(false);
final int tabIndex = c.getFrom();
findNearestAvailableTab(tabIndex, true);
}
}
}
if (c.wasAdded() || c.wasRemoved()) {
if (getSelectedIndex() != tabPane.getTabs().indexOf(getSelectedItem())) {
clearAndSelect(tabPane.getTabs().indexOf(getSelectedItem()));
}
}
}
if (getSelectedIndex() == -1 && getSelectedItem() == null && tabPane.getTabs().size() > 0) {
findNearestAvailableTab(0, true);
} else if (tabPane.getTabs().isEmpty()) {
clearSelection();
}
};
if (this.tabPane.getTabs() != null) {
this.tabPane.getTabs().addListener(new WeakListChangeListener<>(itemsContentObserver));
}
}
@Override public void select(int index) {
if (index < 0 || (getItemCount() > 0 && index >= getItemCount()) ||
(index == getSelectedIndex() && getModelItem(index).isSelected())) {
return;
}
if (getSelectedIndex() >= 0 && getSelectedIndex() < tabPane.getTabs().size()) {
tabPane.getTabs().get(getSelectedIndex()).setSelected(false);
}
setSelectedIndex(index);
Tab tab = getModelItem(index);
if (tab != null) {
setSelectedItem(tab);
}
if (getSelectedIndex() >= 0 && getSelectedIndex() < tabPane.getTabs().size()) {
tabPane.getTabs().get(getSelectedIndex()).setSelected(true);
}
tabPane.notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_ITEM);
}
@Override public void select(Tab tab) {
final int itemCount = getItemCount();
for (int i = 0; i < itemCount; i++) {
final Tab value = getModelItem(i);
if (value != null && value.equals(tab)) {
select(i);
return;
}
}
}
@Override protected Tab getModelItem(int index) {
final ObservableList<Tab> items = tabPane.getTabs();
if (items == null) return null;
if (index < 0 || index >= items.size()) return null;
return items.get(index);
}
@Override protected int getItemCount() {
final ObservableList<Tab> items = tabPane.getTabs();
return items == null ? 0 : items.size();
}
private Tab findNearestAvailableTab(int tabIndex, boolean doSelect) {
final int tabCount = getItemCount();
int i = 1;
Tab bestTab = null;
while (true) {
int downPos = tabIndex - i;
if (downPos >= 0) {
Tab _tab = getModelItem(downPos);
if (_tab != null && ! _tab.isDisable()) {
bestTab = _tab;
break;
}
}
int upPos = tabIndex + i - 1;
if (upPos < tabCount) {
Tab _tab = getModelItem(upPos);
if (_tab != null && ! _tab.isDisable()) {
bestTab = _tab;
break;
}
}
if (downPos < 0 && upPos >= tabCount) {
break;
}
i++;
}
if (doSelect && bestTab != null) {
select(bestTab);
}
return bestTab;
}
}
public enum TabClosingPolicy {
SELECTED_TAB,
ALL_TABS,
UNAVAILABLE
}
private ObjectProperty<TabDragPolicy> tabDragPolicy;
public final ObjectProperty<TabDragPolicy> tabDragPolicyProperty() {
if (tabDragPolicy == null) {
tabDragPolicy = new SimpleObjectProperty<TabDragPolicy>(this, "tabDragPolicy", TabDragPolicy.FIXED);
}
return tabDragPolicy;
}
public final void setTabDragPolicy(TabDragPolicy value) {
tabDragPolicyProperty().set(value);
}
public final TabDragPolicy getTabDragPolicy() {
return tabDragPolicyProperty().get();
}
public enum TabDragPolicy {
FIXED,
REORDER
}
}
