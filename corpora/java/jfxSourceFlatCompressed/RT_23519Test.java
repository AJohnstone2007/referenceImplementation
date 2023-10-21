package test.javafx.fxml;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import org.junit.Test;
import static org.junit.Assert.*;
public class RT_23519Test {
@Test
public void testId() throws IOException {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("rt_23519.fxml"));
fxmlLoader.load();
Button btn = (Button) fxmlLoader.getNamespace().get("fxid");
assertEquals(btn.getId(), "ButtonID");
}
}
