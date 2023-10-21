package javafx.scene.control;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
public class PasswordField extends TextField {
public PasswordField() {
getStyleClass().add("password-field");
setAccessibleRole(AccessibleRole.PASSWORD_FIELD);
}
@Override public void cut() {
}
@Override public void copy() {
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case TEXT: return null;
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
}
