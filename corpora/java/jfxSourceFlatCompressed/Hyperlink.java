package javafx.scene.control;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.value.WritableValue;
import javafx.event.ActionEvent;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.css.PseudoClass;
import javafx.scene.control.skin.HyperlinkSkin;
import javafx.css.StyleableProperty;
public class Hyperlink extends ButtonBase {
public Hyperlink() {
initialize();
}
public Hyperlink(String text) {
super(text);
initialize();
}
public Hyperlink(String text, Node graphic) {
super(text, graphic);
initialize();
}
private void initialize() {
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.HYPERLINK);
((StyleableProperty<Cursor>)(WritableValue<Cursor>)cursorProperty()).applyStyle(null, Cursor.HAND);
}
public final BooleanProperty visitedProperty() {
if (visited == null) {
visited = new BooleanPropertyBase() {
@Override protected void invalidated() {
pseudoClassStateChanged(PSEUDO_CLASS_VISITED, get());
}
@Override
public Object getBean() {
return Hyperlink.this;
}
@Override
public String getName() {
return "visited";
}
};
}
return visited;
}
private BooleanProperty visited;
public final void setVisited(boolean value) {
visitedProperty().set(value);
}
public final boolean isVisited() {
return visited == null ? false : visited.get();
}
@Override public void fire() {
if (!isDisabled()) {
if (visited == null || !visited.isBound()) {
setVisited(true);
}
fireEvent(new ActionEvent());
}
}
@Override protected Skin<?> createDefaultSkin() {
return new HyperlinkSkin(this);
}
private static final String DEFAULT_STYLE_CLASS = "hyperlink";
private static final PseudoClass PSEUDO_CLASS_VISITED =
PseudoClass.getPseudoClass("visited");
@Override protected Cursor getInitialCursor() {
return Cursor.HAND;
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case VISITED: return isVisited();
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
}
