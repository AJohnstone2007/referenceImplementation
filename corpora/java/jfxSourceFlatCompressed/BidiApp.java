package ensemble.samples.controls.text.bidi;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
public class BidiApp extends Application {
Text text1;
Text text2;
public Parent createContent() {
Font font = new Font("Tahoma", 48);
text1 = new Text("He said \u0627\u0644\u0633\u0644\u0627\u0645");
text1.setFill(Color.RED);
text1.setFont(font);
text2 = new Text(" \u0639\u0644\u064a\u0643\u0645 to me.");
text2.setFill(Color.BLUE);
text2.setFont(font);
return new Group(new TextFlow(text1, text2));
}
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
