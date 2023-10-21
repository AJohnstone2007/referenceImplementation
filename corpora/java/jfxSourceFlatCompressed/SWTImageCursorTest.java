import javafx.embed.swt.FXCanvas;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
public class SWTImageCursorTest {
static final String instructions =
"This tests that an image cursor applied to a scene embedded within an FXCanvas is properly transferred to SWT. " +
"This test passes if the cursor changes to the provided cursor.png image when entering the rectangle, and back to the default cursor when leaving it again.";
private static TextArea createInfo(String msg) {
TextArea t = new TextArea(msg);
t.setWrapText(true);
t.setEditable(false);
t.setMaxWidth(400);
t.setMaxHeight(200);
return t;
}
public static void main(String[] args) {
final Display display = new Display();
final Shell shell = new Shell(display);
shell.setText("SWTImageCursorTest");
shell.setSize(400, 200);
shell.setLayout(new FillLayout());
final FXCanvas canvas = new FXCanvas(shell, SWT.NONE);
shell.open();
Group root = new Group();
TextArea info = createInfo(instructions);
root.getChildren().add(info);
Rectangle rect = new Rectangle(100, 100, 100, 50);
rect.setStroke(Color.BLACK);
rect.setFill(Color.WHITE);
root.getChildren().add(rect);
final Scene scene = new Scene(root, 200, 200);
rect.setOnMouseEntered(mouseEvent -> {
Image cursorImage = new Image("cursor.png");
scene.setCursor(new ImageCursor(cursorImage));
});
rect.setOnMouseExited(mouseEvent -> {
scene.setCursor(null);
});
canvas.setScene(scene);
canvas.pack();
while (!shell.isDisposed()) {
if (!display.readAndDispatch()) {
display.sleep();
}
}
display.dispose();
}
}
