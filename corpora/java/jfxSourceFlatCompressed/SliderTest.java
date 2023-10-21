package test.javafx.scene.control;
import javafx.css.ParsedValue;
import javafx.css.CssMetaData;
import javafx.css.CssParserShim;
import test.com.sun.javafx.pgstub.StubToolkit;
import com.sun.javafx.tk.Toolkit;
import javafx.css.StyleableProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
public class SliderTest {
private Slider slider;
private Toolkit tk;
private Scene scene;
private Stage stage;
@Before public void setup() {
tk = (StubToolkit)Toolkit.getToolkit();
slider = new Slider();
}
protected void startApp() {
scene = new Scene(new StackPane(slider), 800, 600);
stage = new Stage();
stage.setScene(scene);
stage.show();
tk.firePulse();
}
@Test public void testSettingMinorTickCountViaCSS() {
startApp();
ParsedValue pv = new CssParserShim().parseExpr("-fx-minor-tick-count","2");
Object val = pv.convert(null);
try {
((StyleableProperty)slider.minorTickCountProperty()).applyStyle(null, val);
assertEquals(2, slider.getMinorTickCount(), 0.);
} catch (Exception e) {
Assert.fail(e.toString());
}
}
@Test public void testSettingTickLabelFormatter() {
slider.setShowTickLabels(true);
slider.setShowTickMarks(true);
slider.setLabelFormatter(new StringConverter<Double>() {
@Override public String toString(Double t) {
return "Ok.";
}
@Override public Double fromString(String string) {
return 10.0;
}
});
startApp();
assertEquals("Ok.", slider.getLabelFormatter().toString(10.0));
}
@Test
public void testSnapToTicks() {
startApp();
slider.setValue(5);
slider.setSnapToTicks(true);
assertEquals(6.25, slider.getValue(), 0);
}
@Test
public void testSliderHasHorizontalPseudoclassByDefault() {
Slider slider = new Slider();
assertTrue(slider.getPseudoClassStates().stream().anyMatch(c -> c.getPseudoClassName().equals("horizontal")));
assertFalse(slider.getPseudoClassStates().stream().anyMatch(c -> c.getPseudoClassName().equals("vertical")));
}
}
