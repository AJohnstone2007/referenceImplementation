package test.javafx.fxml;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import org.junit.Test;
public class RT_16724Test {
@Test
public void testControllerFactory() throws IOException {
FXMLLoader.load(getClass().getResource("rt_16724.fxml"), null, null,
new RT_16724ControllerFactory());
}
}
