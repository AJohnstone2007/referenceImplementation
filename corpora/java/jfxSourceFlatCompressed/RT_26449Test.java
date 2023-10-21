package test.javafx.fxml;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import org.junit.Test;
public class RT_26449Test {
@Test(expected=LoadException.class)
public void testRootNotSet() throws IOException {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("rt_26449.fxml"));
fxmlLoader.load();
}
}
