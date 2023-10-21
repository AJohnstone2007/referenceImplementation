package javafx.scene.control.skin;
import com.sun.javafx.scene.control.ContextMenuContent;
import com.sun.javafx.scene.control.EmbeddedTextContextMenuContent;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Menu;
import javafx.scene.control.Skin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.stage.WindowEvent;
import com.sun.javafx.scene.control.behavior.TwoLevelFocusPopupBehavior;
public class ContextMenuSkin implements Skin<ContextMenu> {
private ContextMenu popupMenu;
private final Region root;
private TwoLevelFocusPopupBehavior tlFocus;
private double prefHeight;
private double shiftY;
private double prefWidth;
private double shiftX;
private final EventHandler<KeyEvent> keyListener = new EventHandler<KeyEvent>() {
@Override public void handle(KeyEvent event) {
if (event.getEventType() != KeyEvent.KEY_PRESSED) return;
if (! root.isFocused()) return;
final KeyCode code = event.getCode();
switch (code) {
case ENTER:
case SPACE: popupMenu.hide(); return;
default: return;
}
}
};
public ContextMenuSkin(final ContextMenu control) {
this.popupMenu = control;
popupMenu.addEventHandler(Menu.ON_SHOWING, new EventHandler<Event>() {
@Override public void handle(Event event) {
prefHeight = root.prefHeight(-1);
prefWidth = root.prefWidth(-1);
}
});
popupMenu.addEventHandler(Menu.ON_SHOWN, new EventHandler<Event>() {
@Override public void handle(Event event) {
Node cmContent = popupMenu.getSkin().getNode();
if (cmContent != null) {
if (cmContent instanceof ContextMenuContent) {
Node accMenu = ((ContextMenuContent)cmContent).getItemsContainer();
accMenu.notifyAccessibleAttributeChanged(AccessibleAttribute.VISIBLE);
}
}
root.addEventHandler(KeyEvent.KEY_PRESSED, keyListener);
performPopupShifts();
}
});
popupMenu.addEventHandler(Menu.ON_HIDDEN, new EventHandler<Event>() {
@Override public void handle(Event event) {
Node cmContent = popupMenu.getSkin().getNode();
if (cmContent != null) cmContent.requestFocus();
root.removeEventHandler(KeyEvent.KEY_PRESSED, keyListener);
}
});
popupMenu.addEventFilter(WindowEvent.WINDOW_HIDING, new EventHandler<Event>() {
@Override public void handle(Event event) {
Node cmContent = popupMenu.getSkin().getNode();
if (cmContent instanceof ContextMenuContent) {
Node accMenu = ((ContextMenuContent)cmContent).getItemsContainer();
accMenu.notifyAccessibleAttributeChanged(AccessibleAttribute.VISIBLE);
}
}
});
if (Properties.IS_TOUCH_SUPPORTED &&
popupMenu.getStyleClass().contains("text-input-context-menu")) {
root = new EmbeddedTextContextMenuContent(popupMenu);
} else {
root = new ContextMenuContent(popupMenu);
}
root.idProperty().bind(popupMenu.idProperty());
root.styleProperty().bind(popupMenu.styleProperty());
root.getStyleClass().addAll(popupMenu.getStyleClass());
if (Utils.isTwoLevelFocus()) {
tlFocus = new TwoLevelFocusPopupBehavior(popupMenu);
}
}
@Override public ContextMenu getSkinnable() {
return popupMenu;
}
@Override public Node getNode() {
return root;
}
@Override public void dispose() {
root.idProperty().unbind();
root.styleProperty().unbind();
if (tlFocus != null) tlFocus.dispose();
}
private void performPopupShifts() {
final ContextMenu contextMenu = getSkinnable();
final Node ownerNode = contextMenu.getOwnerNode();
if (ownerNode == null) return;
final Bounds ownerBounds = ownerNode.localToScreen(ownerNode.getLayoutBounds());
if (ownerBounds == null) return;
final double rootPrefHeight = root.prefHeight(-1);
shiftY = prefHeight - rootPrefHeight;
if (shiftY > 0 && (contextMenu.getY() + rootPrefHeight) < ownerBounds.getMinY()) {
contextMenu.setY(contextMenu.getY() + shiftY);
}
final double rootPrefWidth = root.prefWidth(-1);
shiftX = prefWidth - rootPrefWidth;
if (shiftX > 0 && (contextMenu.getX() + rootPrefWidth) < ownerBounds.getMinX()) {
contextMenu.setX(contextMenu.getX() + shiftX);
}
}
}
