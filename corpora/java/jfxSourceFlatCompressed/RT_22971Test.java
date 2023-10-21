package test.javafx.fxml;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import org.junit.Test;
import static org.junit.Assert.*;
public class RT_22971Test {
@Test
public void testResourcesInjection() throws IOException {
URL location = getClass().getResource("rt_22971.fxml");
FXMLLoader fxmlLoader = new FXMLLoader(location);
fxmlLoader.load();
RT_22971Controller controller = fxmlLoader.getController();
assertEquals(controller.getFoo(), "bar");
}
}
