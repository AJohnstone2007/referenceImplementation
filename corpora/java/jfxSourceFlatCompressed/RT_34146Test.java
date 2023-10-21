package test.javafx.fxml;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import org.junit.Assert;
import org.junit.Test;
public final class RT_34146Test {
@Test
public void testCanCallAbstractMethodFromPrivateClass() throws IOException {
final FXMLLoader fxmlLoader =
new FXMLLoader(getClass().getResource("rt_34146.fxml"));
final ConcreteController concreteController =
new ConcreteController();
fxmlLoader.setController(concreteController);
final Widget widget = fxmlLoader.<Widget>load();
widget.fire();
Assert.assertTrue(concreteController.getActionHandlerCalled());
}
private static abstract class AbstractController {
@FXML
protected abstract void actionHandler();
}
private static final class ConcreteController extends AbstractController {
private boolean actionHandlerCalled;
@Override
protected void actionHandler() {
actionHandlerCalled = true;
}
boolean getActionHandlerCalled() {
return actionHandlerCalled;
}
}
}
