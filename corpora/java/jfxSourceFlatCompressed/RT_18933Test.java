package test.javafx.fxml;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import org.junit.Test;
public class RT_18933Test {
@Test(expected=LoadException.class)
public void testDefaultListProperty() throws IOException {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("rt_18933.fxml"));
fxmlLoader.load();
}
}
