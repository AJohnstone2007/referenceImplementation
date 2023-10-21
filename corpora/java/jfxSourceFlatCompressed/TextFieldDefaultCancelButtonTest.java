package test.javafx.scene.control;
import javafx.scene.control.TextField;
public class TextFieldDefaultCancelButtonTest extends DefaultCancelButtonTestBase<TextField> {
public TextFieldDefaultCancelButtonTest(ButtonType buttonType, boolean consume,
boolean registerAfterShowing) {
super(buttonType, consume, registerAfterShowing);
}
@Override
protected TextField createControl() {
return new TextField();
}
}
