package ensemble.samples.scenegraph.events.keystrokemotion;
import java.util.Random;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.VPos;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
public class LettersPane extends Region {
private Text pressText;
public LettersPane() {
setId("LettersPane");
setPrefSize(240, 240);
setFocusTraversable(true);
setOnMousePressed((MouseEvent me) -> {
requestFocus();
me.consume();
});
setOnKeyPressed((KeyEvent ke) -> {
createLetter(ke.getText());
ke.consume();
});
pressText = new Text("Press Keys");
pressText.setTextOrigin(VPos.TOP);
pressText.setFont(new Font(Font.getDefault().getFamily(), 20));
pressText.setLayoutY(5);
pressText.setFill(Color.rgb(80, 80, 80));
DropShadow effect = new DropShadow();
effect.setRadius(0);
effect.setOffsetY(1);
effect.setColor(Color.WHITE);
pressText.setEffect(effect);
getChildren().add(pressText);
}
@Override
protected void layoutChildren() {
pressText.setLayoutX((getWidth() -
pressText.getLayoutBounds().getWidth()) / 2);
}
private void createLetter(String c) {
final Font font = new Font(Font.getDefault().getFamily(), 200);
final Text letter = new Text(c);
letter.setFill(Color.BLACK);
letter.setFont(font);
letter.setTextOrigin(VPos.TOP);
letter.setTranslateX((getWidth() -
letter.getBoundsInLocal().getWidth()) / 2);
letter.setTranslateY((getHeight() -
letter.getBoundsInLocal().getHeight()) / 2);
getChildren().add(letter);
final Interpolator interp = Interpolator.SPLINE(0.295, 0.800,
0.305, 1.000);
final Timeline timeline = new Timeline();
timeline.getKeyFrames().add(
new KeyFrame(Duration.seconds(3), (ActionEvent event) -> {
getChildren().remove(letter);
},
new KeyValue(letter.translateXProperty(),
getRandom(0.0f, getWidth() -
letter.getBoundsInLocal().getWidth()),
interp),
new KeyValue(letter.translateYProperty(),
getRandom(0.0f, getHeight() -
letter.getBoundsInLocal().getHeight()),
interp),
new KeyValue(letter.opacityProperty(), 0f)));
timeline.play();
}
private static final Random RANDOM = new Random();
private static float getRandom(double min, double max) {
return (float) (RANDOM.nextFloat() * (max - min) + min);
}
}
