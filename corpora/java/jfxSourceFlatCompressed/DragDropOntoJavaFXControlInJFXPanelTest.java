import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
public class DragDropOntoJavaFXControlInJFXPanelTest {
public static void main(final String... pArguments) {
SwingUtilities.invokeLater(new Runnable() {
@Override
public void run() {
JFrame frame = new JFrame("DnDTest");
frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
JRootPane rootPane = frame.getRootPane();
rootPane.setLayout(new FlowLayout());
JLabel label = new JLabel("Drag me");
label.setTransferHandler(new TransferHandler() {
@Override
protected Transferable createTransferable(final JComponent pComponent) {
return new StringSelection("Drag text");
}
@Override
public int getSourceActions(final JComponent pComponent) {
return DnDConstants.ACTION_COPY;
}
});
MouseAdapter dragGestureRecognizer = new MouseAdapter() {
private Point mPoint;
@Override
public void mousePressed(MouseEvent pEvent) {
mPoint = pEvent.getPoint();
}
@Override
public void mouseReleased(MouseEvent pEvent) {
mPoint = null;
}
@Override
public void mouseDragged(MouseEvent pEvent) {
if (mPoint == null) {
mPoint = pEvent.getPoint();
}
double distance = pEvent.getPoint().distance(mPoint);
if (distance > DragSource.getDragThreshold()) {
JComponent component = (JComponent) pEvent.getComponent();
TransferHandler transferHandler = component.getTransferHandler();
transferHandler.exportAsDrag(component, pEvent, DnDConstants.ACTION_COPY);
}
}
};
label.addMouseListener(dragGestureRecognizer);
label.addMouseMotionListener(dragGestureRecognizer);
rootPane.add(label);
final JFXPanel panel = new JFXPanel();
Platform.runLater(new Runnable() {
@Override
public void run() {
TextField textField = new TextField("Drop here");
Button passButton = new Button("Pass");
Button failButton = new Button("Fail");
passButton.setOnAction(e -> frame.dispose());
failButton.setOnAction(e -> {
frame.dispose();
throw new AssertionError("Drag / drop onto a JavaFX control in a JFXPanel not working");
});
HBox hBox1 = new HBox();
HBox.setHgrow(textField, Priority.ALWAYS);
hBox1.getChildren().add(textField);
VBox rootNode = new VBox(6, hBox1,
new Label("1. This is a test for drag / drop onto a JavaFX control in a JFXPanel."),
new Label("2. Drag JLabel \"Drag Me\" text and drop into \"Drop here\" JavaFX textfield."),
new Label("3. If \"Drag text\" text is added to existing text in JavaFX TextField control, click on Pass or else click on Fail"),
new Label(""),
new HBox(10, passButton, failButton));
Scene scene = new Scene(rootNode);
panel.setScene(scene);
}
});
rootPane.add(panel);
frame.setSize(700, 400);
frame.setLocationRelativeTo(null);
frame.setVisible(true);
}
});
}
}
