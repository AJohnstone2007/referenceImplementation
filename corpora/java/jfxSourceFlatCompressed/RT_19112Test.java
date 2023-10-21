package test.javafx.fxml;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import org.junit.Test;
import static org.junit.Assert.*;
public class RT_19112Test {
@Test
public void testStaticProperty() throws IOException {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("rt_19112.fxml"));
fxmlLoader.load();
Widget widget1 = (Widget)fxmlLoader.getNamespace().get("widget1");
assertEquals(Widget.getAlignment(widget1), Alignment.LEFT);
Widget widget2 = (Widget)fxmlLoader.getNamespace().get("widget2");
assertEquals(Widget.getAlignment(widget2), Alignment.RIGHT);
}
}
