package ensemble.samples.controls.progressbar;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
public class ProgressBarApp extends Application {
public Parent createContent() {
double y = 15;
final double SPACING = 15;
ProgressBar p1 = new ProgressBar();
p1.setLayoutY(y);
y += SPACING;
ProgressBar p2 = new ProgressBar();
p2.setPrefWidth(150);
p2.setLayoutY(y);
y += SPACING;
ProgressBar p3 = new ProgressBar();
p3.setPrefWidth(200);
p3.setLayoutY(y);
y = 15;
ProgressBar p4 = new ProgressBar(0.25);
p4.setLayoutX(215);
p4.setLayoutY(y);
y += SPACING;
ProgressBar p5 = new ProgressBar(0.50);
p5.setPrefWidth(150);
p5.setLayoutX(215);
p5.setLayoutY(y);
y += SPACING;
ProgressBar p6 = new ProgressBar(1);
p6.setPrefWidth(200);
p6.setLayoutX(215);
p6.setLayoutY(y);
Group group = new Group();
group.getChildren().addAll(p1,p2,p3,p4,p5,p6);
return group;
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
