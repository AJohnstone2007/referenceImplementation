package test.javafx.fxml;
import org.junit.Test;
import static org.junit.Assert.*;
public class RT_19870Test {
@Test
public void testCustomWidget() {
MyWidget myWidget = new MyWidget();
assertTrue(myWidget.getChildWidgetEnabledChanged());
}
}
