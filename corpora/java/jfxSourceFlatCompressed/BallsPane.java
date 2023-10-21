package ensemble.samples.graphics2d.bouncingballs;
import static ensemble.samples.graphics2d.bouncingballs.Constants.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Parent;
import javafx.scene.effect.Reflection;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
public class BallsPane extends Parent{
private List<Ball> balls;
public BallsPane(){
init();
}
private void init(){
balls = new ArrayList<Ball>();
for (int i = 0; i < NR_OF_BALLS; i++) {
balls.add(new Ball(i));
}
getChildren().add(createBackground());
getChildren().addAll(balls);
createReflectionEffect();
}
private void createReflectionEffect() {
final Reflection reflection = new Reflection();
setEffect(reflection);
}
private Rectangle createBackground(){
final Rectangle rectangle = new Rectangle();
rectangle.setWidth(WIDTH);
rectangle.setHeight(HEIGHT/2 + BALL_RADIUS);
rectangle.setTranslateY(INFOPANEL_HEIGHT);
rectangle.setFill(Color.TRANSPARENT);
return rectangle;
}
public void resetBalls(){
for(Ball b: balls){
b.reset();
}
}
}
