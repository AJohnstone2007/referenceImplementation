package test.javafx.scene.control;
import org.junit.Ignore;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
@Ignore
public class TextFieldWithFormatterDefaultCancelButtonTest
extends TextFieldDefaultCancelButtonTest {
public TextFieldWithFormatterDefaultCancelButtonTest(ButtonType buttonType,
boolean consume, boolean registerAfterShowing) {
super(buttonType, consume, registerAfterShowing);
}
@Override
protected TextField createControl() {
TextField input = super.createControl();
input.setTextFormatter(new TextFormatter<>(TextFormatter.IDENTITY_STRING_CONVERTER));
return input;
}
}
