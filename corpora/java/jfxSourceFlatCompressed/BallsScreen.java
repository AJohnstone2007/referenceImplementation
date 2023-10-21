package ensemble.samples.graphics2d.bouncingballs;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.control.Button;
import javafx.stage.Screen;
import javafx.stage.Stage;
import static ensemble.samples.graphics2d.bouncingballs.Constants.*;
public class BallsScreen extends Parent {
private final Line line = createLine();
private final BallsPane ballsPane = createBallsPane();
public BallsPane getPane(){
return ballsPane;
}
public BallsScreen() {
getChildren().addAll(line, ballsPane);
}
private Line createLine() {
final Line line = new Line();
line.setEndX(WIDTH);
line.setTranslateY(HEIGHT / 2 + INFOPANEL_HEIGHT + BALL_RADIUS);
line.setStrokeWidth(5f);
line.setStroke(Color.BLACK);
return line;
}
private BallsPane createBallsPane() {
final BallsPane ballsPane = new BallsPane();
return ballsPane;
}
}
