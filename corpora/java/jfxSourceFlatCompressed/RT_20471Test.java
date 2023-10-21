package test.javafx.fxml;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import org.junit.Test;
import static org.junit.Assert.*;
public class RT_20471Test {
@Test
public void testControllerInjection() throws IOException {
URL location = getClass().getResource("rt_20471.fxml");
ResourceBundle resources = ResourceBundle.getBundle("test.javafx.fxml.rt_20471");
FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
fxmlLoader.load();
RT_20471Controller controller = fxmlLoader.getController();
assertEquals(controller.getLocation(), location);
assertEquals(controller.getResources(), resources);
assertTrue(controller.isInitialized());
}
}
