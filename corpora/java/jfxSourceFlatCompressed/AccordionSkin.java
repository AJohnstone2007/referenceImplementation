package javafx.scene.control.skin;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TitledPane;
import javafx.scene.shape.Rectangle;
import com.sun.javafx.scene.control.behavior.AccordionBehavior;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class AccordionSkin extends SkinBase<Accordion> {
private TitledPane firstTitledPane;
private Rectangle clipRect;
private boolean forceRelayout = false;
private boolean relayout = false;
private double previousHeight = 0;
private TitledPane expandedPane = null;
private TitledPane previousPane = null;
private Map<TitledPane, ChangeListener<Boolean>>listeners = new HashMap<>();
private final BehaviorBase<Accordion> behavior;
public AccordionSkin(final Accordion control) {
super(control);
behavior = new AccordionBehavior(control);
control.getPanes().addListener((ListChangeListener<TitledPane>) c -> {
if (firstTitledPane != null) {
firstTitledPane.getStyleClass().remove("first-titled-pane");
}
if (!control.getPanes().isEmpty()) {
firstTitledPane = control.getPanes().get(0);
firstTitledPane.getStyleClass().add("first-titled-pane");
} else {
firstTitledPane = null;
}
getChildren().setAll(control.getPanes());
while (c.next()) {
removeTitledPaneListeners(c.getRemoved());
initTitledPaneListeners(c.getAddedSubList());
}
forceRelayout = true;
});
if (!control.getPanes().isEmpty()) {
firstTitledPane = control.getPanes().get(0);
firstTitledPane.getStyleClass().add("first-titled-pane");
}
clipRect = new Rectangle(control.getWidth(), control.getHeight());
getSkinnable().setClip(clipRect);
initTitledPaneListeners(control.getPanes());
getChildren().setAll(control.getPanes());
getSkinnable().requestLayout();
registerChangeListener(getSkinnable().widthProperty(), e -> clipRect.setWidth(getSkinnable().getWidth()));
registerChangeListener(getSkinnable().heightProperty(), e -> {
clipRect.setHeight(getSkinnable().getHeight());
relayout = true;
});
}
@Override public void dispose() {
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
@Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
double h = 0;
if (expandedPane != null) {
h += expandedPane.minHeight(width);
}
if (previousPane != null && !previousPane.equals(expandedPane)) {
h += previousPane.minHeight(width);
}
for (Node child: getChildren()) {
TitledPane pane = (TitledPane)child;
if (!pane.equals(expandedPane) && !pane.equals(previousPane)) {
final Skin<?> skin = ((TitledPane)child).getSkin();
if (skin instanceof TitledPaneSkin) {
TitledPaneSkin childSkin = (TitledPaneSkin) skin;
h += childSkin.getTitleRegionSize(width);
} else {
h += pane.minHeight(width);
}
}
}
return h + topInset + bottomInset;
}
@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
double h = 0;
if (expandedPane != null) {
h += expandedPane.prefHeight(width);
}
if (previousPane != null && !previousPane.equals(expandedPane)) {
h += previousPane.prefHeight(width);
}
for (Node child: getChildren()) {
TitledPane pane = (TitledPane)child;
if (!pane.equals(expandedPane) && !pane.equals(previousPane)) {
final Skin<?> skin = ((TitledPane)child).getSkin();
if (skin instanceof TitledPaneSkin) {
TitledPaneSkin childSkin = (TitledPaneSkin) skin;
h += childSkin.getTitleRegionSize(width);
} else {
h += pane.prefHeight(width);
}
}
}
return h + topInset + bottomInset;
}
@Override protected void layoutChildren(final double x, double y,
final double w, final double h) {
final boolean rebuild = forceRelayout || (relayout && previousHeight != h);
forceRelayout = false;
previousHeight = h;
double collapsedPanesHeight = 0;
for (TitledPane tp : getSkinnable().getPanes()) {
if (!tp.equals(expandedPane)) {
TitledPaneSkin childSkin = (TitledPaneSkin) ((TitledPane)tp).getSkin();
collapsedPanesHeight += snapSizeY(childSkin.getTitleRegionSize(w));
}
}
final double maxTitledPaneHeight = h - collapsedPanesHeight;
for (TitledPane tp : getSkinnable().getPanes()) {
Skin<?> skin = tp.getSkin();
double ph;
if (skin instanceof TitledPaneSkin) {
((TitledPaneSkin)skin).setMaxTitledPaneHeightForAccordion(maxTitledPaneHeight);
ph = snapSizeY(((TitledPaneSkin)skin).getTitledPaneHeightForAccordion());
} else {
ph = tp.prefHeight(w);
}
tp.resize(w, ph);
boolean needsRelocate = true;
if (! rebuild && previousPane != null && expandedPane != null) {
List<TitledPane> panes = getSkinnable().getPanes();
final int previousPaneIndex = panes.indexOf(previousPane);
final int expandedPaneIndex = panes.indexOf(expandedPane);
final int currentPaneIndex = panes.indexOf(tp);
if (previousPaneIndex < expandedPaneIndex) {
if (currentPaneIndex <= expandedPaneIndex) {
tp.relocate(x, y);
y += ph;
needsRelocate = false;
}
} else if (previousPaneIndex > expandedPaneIndex) {
if (currentPaneIndex <= previousPaneIndex) {
tp.relocate(x, y);
y += ph;
needsRelocate = false;
}
} else {
tp.relocate(x, y);
y += ph;
needsRelocate = false;
}
}
if (needsRelocate) {
tp.relocate(x, y);
y += ph;
}
}
}
private void initTitledPaneListeners(List<? extends TitledPane> list) {
for (final TitledPane tp: list) {
tp.setExpanded(tp == getSkinnable().getExpandedPane());
if (tp.isExpanded()) {
expandedPane = tp;
}
ChangeListener<Boolean> changeListener = expandedPropertyListener(tp);
tp.expandedProperty().addListener(changeListener);
listeners.put(tp, changeListener);
}
}
private void removeTitledPaneListeners(List<? extends TitledPane> list) {
for (final TitledPane tp: list) {
if (listeners.containsKey(tp)) {
tp.expandedProperty().removeListener(listeners.get(tp));
listeners.remove(tp);
}
}
}
private ChangeListener<Boolean> expandedPropertyListener(final TitledPane tp) {
return (observable, wasExpanded, expanded) -> {
previousPane = expandedPane;
final Accordion accordion = getSkinnable();
if (expanded) {
if (expandedPane != null) {
expandedPane.setExpanded(false);
}
if (tp != null) {
accordion.setExpandedPane(tp);
}
expandedPane = accordion.getExpandedPane();
} else {
expandedPane = null;
accordion.setExpandedPane(null);
}
};
}
}
