package test.javafx.fxml;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import org.junit.Test;
public class RT_24380Test {
@Test
public void testProtectedInitializeMethod() throws IOException {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("rt_24380.fxml"));
fxmlLoader.load();
}
}
