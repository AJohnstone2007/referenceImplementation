package test.javafx.fxml;
import javafx.fxml.FXMLLoader;
import org.junit.Test;
import static org.junit.Assert.*;
public class RT_16977Test {
@Test
public void testEmptyArrayProperty() throws Exception {
Widget widget = FXMLLoader.load(getClass().getResource("rt_16977.fxml"));
assertEquals(widget.getRatios().length, 0);
}
}
