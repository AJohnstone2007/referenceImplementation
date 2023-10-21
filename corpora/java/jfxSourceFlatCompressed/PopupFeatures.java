package javafx.scene.web;
import javafx.beans.NamedArg;
public final class PopupFeatures {
private final boolean menu, status, toolbar, resizable;
public PopupFeatures(
@NamedArg("menu") boolean menu, @NamedArg("status") boolean status, @NamedArg("toolbar") boolean toolbar, @NamedArg("resizable") boolean resizable) {
this.menu = menu;
this.status = status;
this.toolbar = toolbar;
this.resizable = resizable;
}
public final boolean hasMenu() {
return menu;
}
public final boolean hasStatus() {
return status;
}
public final boolean hasToolbar() {
return toolbar;
}
public final boolean isResizable() {
return resizable;
}
}
