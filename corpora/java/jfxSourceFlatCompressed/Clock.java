package ensemble.samples.graphics2d.digitalclock;
import java.util.Calendar;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
public class Clock extends Parent {
private Calendar calendar = Calendar.getInstance();
private Digit[] digits;
private Timeline delayTimeline, secondTimeline;
public Clock(Color onColor, Color offColor) {
Glow onEffect = new Glow(1.7f);
onEffect.setInput(new InnerShadow());
Glow onDotEffect = new Glow(1.7f);
onDotEffect.setInput(new InnerShadow(5, Color.BLACK));
InnerShadow offEffect = new InnerShadow();
digits = new Digit[7];
for (int i = 0; i < 6; i++) {
Digit digit = new Digit(onColor, offColor, onEffect, offEffect);
digit.setLayoutX(i * 80 + ((i + 1) % 2) * 20);
digits[i] = digit;
getChildren().add(digit);
}
Group dots = new Group(
new Circle(80 + 54 + 20, 44, 6, onColor),
new Circle(80 + 54 + 17, 64, 6, onColor),
new Circle((80 * 3) + 54 + 20, 44, 6, onColor),
new Circle((80 * 3) + 54 + 17, 64, 6, onColor));
dots.setEffect(onDotEffect);
getChildren().add(dots);
refreshClocks();
}
private void refreshClocks() {
calendar.setTimeInMillis(System.currentTimeMillis());
int hours = calendar.get(Calendar.HOUR_OF_DAY);
int minutes = calendar.get(Calendar.MINUTE);
int seconds = calendar.get(Calendar.SECOND);
digits[0].showNumber(hours / 10);
digits[1].showNumber(hours % 10);
digits[2].showNumber(minutes / 10);
digits[3].showNumber(minutes % 10);
digits[4].showNumber(seconds / 10);
digits[5].showNumber(seconds % 10);
}
public void play() {
delayTimeline = new Timeline();
delayTimeline.getKeyFrames().add(
new KeyFrame(new Duration(1000 - (System.currentTimeMillis() % 1000)), (ActionEvent event) -> {
if (secondTimeline != null) {
secondTimeline.stop();
}
secondTimeline = new Timeline();
secondTimeline.setCycleCount(Timeline.INDEFINITE);
secondTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), (ActionEvent event1) -> {
refreshClocks();
}));
secondTimeline.play();
}));
delayTimeline.play();
}
public void stop() {
delayTimeline.stop();
if (secondTimeline != null) {
secondTimeline.stop();
}
}
}
