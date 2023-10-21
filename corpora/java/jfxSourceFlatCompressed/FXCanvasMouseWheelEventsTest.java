import javafx.embed.swt.FXCanvas;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
public class FXCanvasMouseWheelEventsTest {
static final String instructions =
"This tests that SWT mouse wheel events are properly transferred to SWT. " +
"It passes if both, vertical and horizontal mouse wheel events are both recognized properly.";
private static TextArea createInfo(String msg) {
TextArea t = new TextArea(msg);
t.setWrapText(true);
t.setEditable(false);
t.setMaxWidth(400);
t.setMaxHeight(100);
return t;
}
public static void main(String[] args) {
final Display display = new Display();
final Shell shell = new Shell(display);
shell.setText("FXCanvasMouseWheelEventsTest");
shell.setSize(400, 200);
shell.setLayout(new FillLayout());
final FXCanvas canvas = new FXCanvas(shell, SWT.NONE);
shell.open();
Group root = new Group();
TextArea info = createInfo(instructions);
Label output = new Label("No events yet...");
VBox vbox = new VBox();
vbox.getChildren().addAll(info, output);
root.getChildren().add(vbox);
final Scene scene = new Scene(root, 200, 200);
final int[] eventCount = {0};
root.setOnScroll(scrollEvent -> {
output.setText("Scroll event #" + eventCount[0]++ + ": deltaX: " + scrollEvent.getDeltaX() + ", deltaY: " + scrollEvent.getDeltaY());
});
canvas.setScene(scene);
while (!shell.isDisposed()) {
if (!display.readAndDispatch()) {
display.sleep();
}
}
display.dispose();
}
}
