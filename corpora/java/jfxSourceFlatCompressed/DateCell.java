package javafx.scene.control;
import java.time.LocalDate;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.control.skin.DateCellSkin;
public class DateCell extends Cell<LocalDate> {
public DateCell() {
getStyleClass().add(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.TEXT);
}
@Override public void updateItem(LocalDate item, boolean empty) {
super.updateItem(item, empty);
}
@Override protected Skin<?> createDefaultSkin() {
return new DateCellSkin(this);
}
private static final String DEFAULT_STYLE_CLASS = "date-cell";
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case TEXT: {
if (isFocused()) {
return getText();
}
return "";
}
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
}