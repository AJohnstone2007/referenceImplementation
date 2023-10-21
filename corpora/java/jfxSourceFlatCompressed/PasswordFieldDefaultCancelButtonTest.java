package test.javafx.scene.control;
import javafx.scene.control.PasswordField;
public class PasswordFieldDefaultCancelButtonTest extends DefaultCancelButtonTestBase<PasswordField> {
public PasswordFieldDefaultCancelButtonTest(ButtonType buttonType,
boolean consume, boolean registerAfterShowing) {
super(buttonType, consume, registerAfterShowing);
}
@Override
protected PasswordField createControl() {
return new PasswordField();
}
}
