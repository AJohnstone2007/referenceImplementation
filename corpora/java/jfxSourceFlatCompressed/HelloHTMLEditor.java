package hello;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
public class HelloHTMLEditor extends Application {
private HTMLEditor htmlEditor = null;
@Override
public void start(Stage stage) {
stage.setTitle("Hello HTMLEditor");
stage.setWidth(800);
stage.setHeight(600);
Scene scene = new Scene(new Group());
scene.setFill(Color.GHOSTWHITE);
FlowPane root = new FlowPane();
root.setOrientation(Orientation.VERTICAL);
scene.setRoot(root);
root.setPadding(new Insets(8, 8, 8, 8));
root.setVgap(8);
htmlEditor = new HTMLEditor();
root.getChildren().add(htmlEditor);
Button dumpHTMLButton = new Button("Dump HTML");
dumpHTMLButton.setOnAction(arg0 -> System.out.println(htmlEditor.getHtmlText()));
root.getChildren().add(dumpHTMLButton);
htmlEditor.setHtmlText("<html><body>Hello, World!</body></html>");
stage.setScene(scene);
stage.show();
}
public static void main(String[] args) {
Application.launch(HelloHTMLEditor.class, args);
}
}
