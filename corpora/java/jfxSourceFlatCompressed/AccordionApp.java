package ensemble.samples.controls.accordion;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
public class AccordionApp extends Application {
public Parent createContent() {
Accordion accordion = new Accordion();
accordion.getPanes().addAll(new TitledPane("Control",
new Button("Press")),
new TitledPane("String",
new Text("Hello World.")),
new TitledPane("Shape",
new Rectangle(120, 50,
Color.RED)));
accordion.setMinSize(100, 100);
accordion.setPrefSize(100, 200);
return accordion;
}
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
