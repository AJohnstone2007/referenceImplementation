package javafx.scene.control;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
public class CustomMenuItem extends MenuItem {
public CustomMenuItem() {
this(null, true);
}
public CustomMenuItem(Node node) {
this(node, true);
}
public CustomMenuItem(Node node, boolean hideOnClick) {
getStyleClass().add(DEFAULT_STYLE_CLASS);
setContent(node);
setHideOnClick(hideOnClick);
}
private ObjectProperty<Node> content;
public final void setContent(Node value) {
contentProperty().set(value);
}
public final Node getContent() {
return content == null ? null : content.get();
}
public final ObjectProperty<Node> contentProperty() {
if (content == null) {
content = new SimpleObjectProperty<Node>(this, "content");
}
return content;
}
private BooleanProperty hideOnClick;
public final void setHideOnClick(boolean value) {
hideOnClickProperty().set(value);
}
public final boolean isHideOnClick() {
return hideOnClick == null ? true : hideOnClick.get();
}
public final BooleanProperty hideOnClickProperty() {
if (hideOnClick == null) {
hideOnClick = new SimpleBooleanProperty(this, "hideOnClick", true);
}
return hideOnClick;
}
private static final String DEFAULT_STYLE_CLASS = "custom-menu-item";
}
