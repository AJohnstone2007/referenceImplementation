package test.javafx.fxml;
import java.io.IOException;
import java.util.Arrays;
import javafx.fxml.FXMLLoader;
import org.junit.Test;
import static org.junit.Assert.*;
public class RT_19139Test {
@Test
public void testStaticProperty() throws IOException {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("rt_19139.fxml"));
Widget widget = (Widget)fxmlLoader.load();
assertEquals(widget.getValues(), Arrays.asList(new String[] {"One", "Two", "Three"}));
}
}
