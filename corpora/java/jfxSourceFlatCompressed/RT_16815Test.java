package test.javafx.fxml;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import org.junit.Test;
import static org.junit.Assert.*;
public class RT_16815Test {
@Test
public void testControllerFactory() throws IOException {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("rt_16815.fxml"),
ResourceBundle.getBundle("test/javafx/fxml/rt_16815"), null,
new RT_16815ControllerFactory());
Widget widget = (Widget)fxmlLoader.load();
assertEquals(widget.getName(), "My Widget");
}
}
