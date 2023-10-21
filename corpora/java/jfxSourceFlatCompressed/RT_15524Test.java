package test.javafx.fxml;
import java.io.IOException;
import java.util.Arrays;
import javafx.fxml.FXMLLoader;
import org.junit.Test;
import static org.junit.Assert.*;
public class RT_15524Test {
@Test
public void testListAndArray() throws IOException {
Widget widget = FXMLLoader.load(getClass().getResource("rt_15524.fxml"));
assertEquals(widget.getStyles(), Arrays.asList(new String[]{"a", "b", "c"}));
assertTrue(Arrays.equals(widget.getRatios(), new float[] {1.0f, 2.0f, 3.0f}));
}
}
