package test.javafx.fxml;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.layout.VBox;
import org.junit.Test;
import static org.junit.Assert.*;
public class RT_40335Test {
@Test public void test_rt_40335() throws Exception {
VBox pane = FXMLLoader.load(getClass().getResource("rt_40335.fxml"));
Button button = (Button) pane.getChildren().get(0);
Spinner spinner = (Spinner) pane.getChildren().get(1);
assertTrue(button.getStyleClass().contains("my-button"));
assertTrue(spinner.getStyleClass().contains("my-spinner"));
}
}
