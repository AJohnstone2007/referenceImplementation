package test.javafx.fxml;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import org.junit.Test;
import static org.junit.Assert.*;
public class RT_20082Test {
@Test
public void testInheritedClassLoader() throws IOException {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("rt_20082.fxml"));
fxmlLoader.setClassLoader(new RT_20082ClassLoader());
fxmlLoader.load();
assertEquals(RT_20082ClassLoader.loadCount, 2);
}
}
