package test.javafx.scene.chart;
import org.junit.Before;
import test.com.sun.javafx.pgstub.StubToolkit;
import com.sun.javafx.tk.Toolkit;
import java.util.Arrays;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.Chart;
import javafx.stage.Stage;
import javafx.scene.layout.Region;
import javafx.scene.shape.*;
import static org.junit.Assert.assertTrue;
public abstract class ChartTestBase {
private Scene scene;
private Stage stage;
StubToolkit toolkit = (StubToolkit) Toolkit.getToolkit();
private Chart chart;
@Before
public void setUp() {
chart = createChart();
chart.setAnimated(false);
}
protected void startApp() {
scene = new Scene(chart,800,600);
stage = new Stage();
stage.setScene(scene);
stage.show();
toolkit.setAnimationTime(0);
pulse();
}
protected void pulse() {
toolkit.fireTestPulse();
}
protected Scene getTestScene() {
return this.scene;
}
protected void setTestScene(Scene scene) {
this.scene = scene;
}
protected Stage getTestStage() {
return this.stage;
}
protected void setTestStage(Stage stage) {
this.stage = stage;
}
protected abstract Chart createChart();
String computeSVGPath(Path line) {
StringBuilder str = new StringBuilder();
for (PathElement pe : line.getElements()) {
if (pe instanceof LineTo) {
str.append("L").append(((LineTo)pe).getX()).append(" ")
.append(((LineTo)pe).getY()).append(" ");
}
}
return str.toString();
}
String computeBoundsString(Region r1, Region r2, Region r3) {
StringBuilder str = new StringBuilder();
str.append(Math.round(r1.getLayoutX())).append(" ")
.append(Math.round(r1.getLayoutY())).append(" ")
.append(Math.round(r1.getWidth())).append(" ")
.append(Math.round(r1.getHeight())).append(" ");
str.append(Math.round(r2.getLayoutX())).append(" ")
.append(Math.round(r2.getLayoutY())).append(" ")
.append(Math.round(r2.getWidth())).append(" ")
.append(Math.round(r2.getHeight())).append(" ");
str.append(Math.round(r3.getLayoutX())).append(" ")
.append(Math.round(r3.getLayoutY())).append(" ")
.append(Math.round(r3.getWidth())).append(" ")
.append(Math.round(r3.getHeight())).append(" ");
return str.toString();
}
void checkStyleClass(Node item, String... styleClass) {
assertTrue("\"" + item.getStyleClass() + "\" doesn't contain all of the " +
Arrays.toString(styleClass),
item.getStyleClass().containsAll(Arrays.asList(styleClass)));
}
}
