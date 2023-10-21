package test.javafx.embed.swt;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
public class SWTCursorsTest {
@Rule
public SwtRule ctx = new SwtRule();
@Test(timeout = 10000)
public void testImageCursor() throws Throwable {
final Shell shell = new Shell(Display.getCurrent());
final FXCanvas canvas = new FXCanvas(shell, SWT.NONE);
shell.open();
Scene scene = new Scene(new Group());
canvas.setScene(scene);
Image cursorImage = new Image("test/javafx/embed/swt/cursor.png");
scene.setCursor(new ImageCursor(cursorImage));
Display.getCurrent().asyncExec(() -> {
assertNotNull(canvas.getCursor());
});
}
}
