package test.javafx.fxml;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;
public class AlertTest {
@Test
public void testAlertButtons() throws Exception {
Alert alert = FXMLLoader.load(getClass().getResource("alert.fxml"));
assertArrayEquals(new ButtonType[] {ButtonType.YES, ButtonType.NO}, alert.getButtonTypes().toArray());
}
}
