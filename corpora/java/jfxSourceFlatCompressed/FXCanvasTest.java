package test.javafx.embed.swt;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Group;
import javafx.scene.Scene;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.assertSame;
public class FXCanvasTest {
@Rule
public SwtRule ctx = new SwtRule();
@Test(timeout = 10000)
public void getFXCanvas() throws Throwable {
final Shell shell = new Shell(Display.getCurrent());
final FXCanvas canvas = new FXCanvas(shell, SWT.NONE);
shell.open();
Scene scene = new Scene(new Group());
canvas.setScene(scene);
assertSame(canvas, FXCanvas.getFXCanvas(canvas.getScene()));
}
}
