package javafx.scene.control.skin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.CssMetaData;
import javafx.css.converter.SizeConverter;
import com.sun.javafx.scene.control.behavior.TreeCellBehavior;
import javafx.css.Styleable;
public class TreeCellSkin<T> extends CellSkinBase<TreeCell<T>> {
private static final Map<TreeView<?>, Double> maxDisclosureWidthMap = new WeakHashMap<TreeView<?>, Double>();
private boolean disclosureNodeDirty = true;
private TreeItem<?> treeItem;
private final BehaviorBase<TreeCell<T>> behavior;
public TreeCellSkin(TreeCell<T> control) {
super(control);
behavior = new TreeCellBehavior<>(control);
updateTreeItem();
registerChangeListener(control.treeItemProperty(), e -> {
updateTreeItem();
disclosureNodeDirty = true;
getSkinnable().requestLayout();
});
registerChangeListener(control.textProperty(), e -> getSkinnable().requestLayout());
}
private DoubleProperty indent = null;
public final void setIndent(double value) { indentProperty().set(value); }
public final double getIndent() { return indent == null ? 10.0 : indent.get(); }
public final DoubleProperty indentProperty() {
if (indent == null) {
indent = new StyleableDoubleProperty(10.0) {
@Override public Object getBean() {
return TreeCellSkin.this;
}
@Override public String getName() {
return "indent";
}
@Override public CssMetaData<TreeCell<?>,Number> getCssMetaData() {
return StyleableProperties.INDENT;
}
};
}
return indent;
}
@Override public void dispose() {
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
@Override protected void updateChildren() {
super.updateChildren();
updateDisclosureNode();
}
@Override protected void layoutChildren(double x, final double y,
double w, final double h) {
TreeView<T> tree = getSkinnable().getTreeView();
if (tree == null) return;
if (disclosureNodeDirty) {
updateDisclosureNode();
disclosureNodeDirty = false;
}
Node disclosureNode = getSkinnable().getDisclosureNode();
int level = tree.getTreeItemLevel(treeItem);
if (! tree.isShowRoot()) level--;
double leftMargin = getIndent() * level;
x += leftMargin;
boolean disclosureVisible = disclosureNode != null && treeItem != null && ! treeItem.isLeaf();
final double defaultDisclosureWidth = maxDisclosureWidthMap.containsKey(tree) ?
maxDisclosureWidthMap.get(tree) : 18;
double disclosureWidth = defaultDisclosureWidth;
if (disclosureVisible) {
if (disclosureNode == null || disclosureNode.getScene() == null) {
updateChildren();
}
if (disclosureNode != null) {
disclosureWidth = disclosureNode.prefWidth(h);
if (disclosureWidth > defaultDisclosureWidth) {
maxDisclosureWidthMap.put(tree, disclosureWidth);
}
double ph = disclosureNode.prefHeight(disclosureWidth);
disclosureNode.resize(disclosureWidth, ph);
positionInArea(disclosureNode, x, y,
disclosureWidth, ph, 0,
HPos.CENTER, VPos.CENTER);
}
}
final int padding = treeItem != null && treeItem.getGraphic() == null ? 0 : 3;
x += disclosureWidth + padding;
w -= (leftMargin + disclosureWidth + padding);
Node graphic = getSkinnable().getGraphic();
if (graphic != null && !getChildren().contains(graphic)) {
getChildren().add(graphic);
}
layoutLabelInArea(x, y, w, h);
}
@Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
double fixedCellSize = getFixedCellSize();
if (fixedCellSize > 0) {
return fixedCellSize;
}
double pref = super.computeMinHeight(width, topInset, rightInset, bottomInset, leftInset);
Node d = getSkinnable().getDisclosureNode();
return (d == null) ? pref : Math.max(d.minHeight(-1), pref);
}
@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
double fixedCellSize = getFixedCellSize();
if (fixedCellSize > 0) {
return fixedCellSize;
}
final TreeCell<T> cell = getSkinnable();
final double pref = super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
final Node d = cell.getDisclosureNode();
final double prefHeight = (d == null) ? pref : Math.max(d.prefHeight(-1), pref);
return snapSizeY(Math.max(cell.getMinHeight(), prefHeight));
}
@Override protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
double fixedCellSize = getFixedCellSize();
if (fixedCellSize > 0) {
return fixedCellSize;
}
return super.computeMaxHeight(width, topInset, rightInset, bottomInset, leftInset);
}
@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
double labelWidth = super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
double pw = snappedLeftInset() + snappedRightInset();
TreeView<T> tree = getSkinnable().getTreeView();
if (tree == null) return pw;
if (treeItem == null) return pw;
pw = labelWidth;
int level = tree.getTreeItemLevel(treeItem);
if (! tree.isShowRoot()) level--;
pw += getIndent() * level;
Node disclosureNode = getSkinnable().getDisclosureNode();
double disclosureNodePrefWidth = disclosureNode == null ? 0 : disclosureNode.prefWidth(-1);
final double defaultDisclosureWidth = maxDisclosureWidthMap.containsKey(tree) ?
maxDisclosureWidthMap.get(tree) : 0;
pw += Math.max(defaultDisclosureWidth, disclosureNodePrefWidth);
return pw;
}
private double getFixedCellSize() {
TreeView<?> treeView = getSkinnable().getTreeView();
return treeView != null ? treeView.getFixedCellSize() : Region.USE_COMPUTED_SIZE;
}
private void updateTreeItem() {
treeItem = getSkinnable().getTreeItem();
}
private void updateDisclosureNode() {
if (getSkinnable().isEmpty()) return;
Node disclosureNode = getSkinnable().getDisclosureNode();
if (disclosureNode == null) return;
boolean disclosureVisible = treeItem != null && ! treeItem.isLeaf();
disclosureNode.setVisible(disclosureVisible);
if (! disclosureVisible) {
getChildren().remove(disclosureNode);
} else if (disclosureNode.getParent() == null) {
getChildren().add(disclosureNode);
disclosureNode.toFront();
} else {
disclosureNode.toBack();
}
if (disclosureNode.getScene() != null) {
disclosureNode.applyCss();
}
}
private static class StyleableProperties {
private static final CssMetaData<TreeCell<?>,Number> INDENT =
new CssMetaData<TreeCell<?>,Number>("-fx-indent",
SizeConverter.getInstance(), 10.0) {
@Override public boolean isSettable(TreeCell<?> n) {
DoubleProperty p = ((TreeCellSkin<?>) n.getSkin()).indentProperty();
return p == null || !p.isBound();
}
@Override public StyleableProperty<Number> getStyleableProperty(TreeCell<?> n) {
final TreeCellSkin<?> skin = (TreeCellSkin<?>) n.getSkin();
return (StyleableProperty<Number>)(WritableValue<Number>)skin.indentProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(CellSkinBase.getClassCssMetaData());
styleables.add(INDENT);
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
