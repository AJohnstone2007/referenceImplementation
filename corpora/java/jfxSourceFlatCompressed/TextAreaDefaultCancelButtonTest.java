package test.javafx.scene.control;
import javafx.scene.control.TextArea;
public class TextAreaDefaultCancelButtonTest extends DefaultCancelButtonTestBase<TextArea> {
public TextAreaDefaultCancelButtonTest(ButtonType buttonType,
boolean consume, boolean registerAfterShowing) {
super(buttonType, consume, registerAfterShowing);
}
@Override
public void testFallbackFilter() {
if (isEnter()) return;
super.testFallbackFilter();
}
@Override
public void testFallbackHandler() {
if (isEnter()) return;
super.testFallbackHandler();
}
@Override
public void testFallbackSingletonHandler() {
if (isEnter()) return;
super.testFallbackSingletonHandler();
}
@Override
public void testFallbackNoHandler() {
if (isEnter()) return;
super.testFallbackNoHandler();
}
@Override
protected TextArea createControl() {
return new TextArea();
}
}
