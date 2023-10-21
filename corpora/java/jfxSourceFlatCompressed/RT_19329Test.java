package test.javafx.fxml;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import org.junit.Test;
public class RT_19329Test {
@Test(expected=IOException.class)
public void testIncludeException() throws IOException {
FXMLLoader.load(getClass().getResource("rt_19329.fxml"));
}
}
