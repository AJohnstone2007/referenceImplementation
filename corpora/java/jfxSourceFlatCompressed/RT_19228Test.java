package test.javafx.fxml;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import org.junit.Test;
import static org.junit.Assert.*;
public class RT_19228Test {
@Test
public void testConstantValue() throws IOException {
Widget widget = (Widget)FXMLLoader.load(getClass().getResource("rt_19228.fxml"));
assertEquals(widget.getNumber(), Widget.TEN);
}
}
