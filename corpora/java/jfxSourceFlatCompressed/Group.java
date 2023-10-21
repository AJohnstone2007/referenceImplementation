package javafx.scene;
import com.sun.javafx.scene.GroupHelper;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import java.util.Collection;
@DefaultProperty("children")
public class Group extends Parent {
static {
GroupHelper.setGroupAccessor(new GroupHelper.GroupAccessor() {
@Override
public Bounds doComputeLayoutBounds(Node node) {
return ((Group) node).doComputeLayoutBounds();
}
});
}
{
GroupHelper.initHelper(this);
}
public Group() { }
public Group(Node... children) {
getChildren().addAll(children);
}
public Group(Collection<Node> children) {
getChildren().addAll(children);
}
private BooleanProperty autoSizeChildren;
public final void setAutoSizeChildren(boolean value){
autoSizeChildrenProperty().set(value);
}
public final boolean isAutoSizeChildren() {
return autoSizeChildren == null ? true : autoSizeChildren.get();
}
public final BooleanProperty autoSizeChildrenProperty() {
if (autoSizeChildren == null) {
autoSizeChildren = new BooleanPropertyBase(true) {
@Override
protected void invalidated() {
requestLayout();
}
@Override
public Object getBean() {
return Group.this;
}
@Override
public String getName() {
return "autoSizeChildren";
}
};
}
return autoSizeChildren;
}
@Override public ObservableList<Node> getChildren() {
return super.getChildren();
}
private Bounds doComputeLayoutBounds() {
layout();
return null;
}
@Override
public double prefWidth(double height) {
if (isAutoSizeChildren()) {
layout();
}
final double result = getLayoutBounds().getWidth();
return Double.isNaN(result) || result < 0 ? 0 : result;
}
@Override
public double prefHeight(double width) {
if (isAutoSizeChildren()) {
layout();
}
final double result = getLayoutBounds().getHeight();
return Double.isNaN(result) || result < 0 ? 0 : result;
}
@Override
public double minHeight(double width) {
return prefHeight(width);
}
@Override
public double minWidth(double height) {
return prefWidth(height);
}
@Override protected void layoutChildren() {
if (isAutoSizeChildren()) {
super.layoutChildren();
}
}
}
