package a11y;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
public class HelloText extends Application {
public static void main(String[] args) {
Application.launch(args);
}
@Override public void start(Stage stage) {
final Text text = new Text("01234");
text.selectionFillProperty().set(Color.BLUE);
text.setFont(Font.font(50));
Label l1 = new Label("name");
TextField tf1 = new TextField("Hello JavaFX Accessiblity");
l1.setLabelFor(tf1);
HBox box1 = new HBox(10, l1, tf1);
Label l2 = new Label("family");
TextField tf2 = new TextField("james");
tf2.setEditable(false);
l2.setLabelFor(tf2);
HBox box2 = new HBox(10, l2, tf2);
TextArea ta = new TextArea("TextArea can many lines.\nLine1.\nLine2 is longer very long very long and can wrap. This is sentence belongs to the paragraph.\nLine 3 is not.");
ta.setWrapText(true);
Scene scene = new Scene(new VBox(text, box1, box2, ta), 300, 300);
stage.setScene(scene);
stage.show();
}
}
