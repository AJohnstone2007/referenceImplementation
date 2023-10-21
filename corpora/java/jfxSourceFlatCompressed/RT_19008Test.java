package test.javafx.fxml;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import org.junit.Test;
public class RT_19008Test {
@Test(expected=NullPointerException.class)
public void testMissingResource() throws IOException {
FXMLLoader.load(getClass().getResource("rt_19008.fxml"));
}
}
