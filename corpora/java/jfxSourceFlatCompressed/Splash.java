package ensemble.samples.graphics2d.brickbreaker;
import ensemble.samples.graphics2d.brickbreaker.BrickBreakerApp.MainFrame;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
public class Splash extends Parent {
private static final int STATE_SHOW_TITLE = 0;
private static final int STATE_SHOW_STRIKE = 1;
private static final int STATE_SUN = 2;
private ImageView background;
private ImageView brick;
private ImageView brickShadow;
private ImageView breaker;
private ImageView breakerShadow;
private Timeline timeline;
private int state;
private int stateArg;
private ImageView strike;
private ImageView strikeShadow;
private ImageView pressanykey;
private ImageView pressanykeyShadow;
private ImageView sun;
private ImageView[] NODES;
private ImageView[] NODES_SHADOWS;
private void initTimeline() {
timeline = new Timeline();
timeline.setCycleCount(Timeline.INDEFINITE);
KeyFrame kf = new KeyFrame(Duration.millis(40), (ActionEvent event) -> {
if (state == STATE_SHOW_TITLE) {
stateArg++;
int center = Config.SCREEN_WIDTH / 2;
int offset = (int)(Math.cos(stateArg / 4.0) * (40 - stateArg) / 40 * center);
brick.setTranslateX(center - brick.getImage().getWidth() / 2 + offset);
breaker.setTranslateX(center - breaker.getImage().getWidth() / 2 - offset);
if (stateArg == 40) {
stateArg = 0;
state = STATE_SHOW_STRIKE;
}
return;
}
if (state == STATE_SHOW_STRIKE) {
if (stateArg == 0) {
strike.setTranslateX(breaker.getTranslateX() + brick.getImage().getWidth());
strike.setScaleX(0);
strike.setScaleY(0);
strike.setVisible(true);
}
stateArg++;
double coef = stateArg / 30f;
brick.setTranslateX(breaker.getTranslateX() +
(breaker.getImage().getWidth() - brick.getImage().getWidth()) / 2f * (1 - coef));
strike.setScaleX(coef);
strike.setScaleY(coef);
strike.setRotate((30 - stateArg) * 2);
if (stateArg == 30) {
stateArg = 0;
state = STATE_SUN;
}
return;
}
if (pressanykey.getOpacity() < 1) {
pressanykey.setOpacity(pressanykey.getOpacity() + 0.05f);
}
stateArg--;
});
timeline.getKeyFrames().add(kf);
}
public void start() {
background.requestFocus();
timeline.play();
}
public void stop() {
timeline.stop();
}
Splash(final MainFrame mainFrame) {
state = STATE_SHOW_TITLE;
stateArg = 0;
initTimeline();
background = new ImageView();
background.setFocusTraversable(true);
background.setImage(Config.getImages().get(Config.IMAGE_BACKGROUND));
background.setFitWidth(Config.SCREEN_WIDTH);
background.setFitHeight(Config.SCREEN_HEIGHT);
background.setOnMousePressed((MouseEvent me) -> {
mainFrame.startGame();
});
background.setOnKeyPressed((KeyEvent ke) -> {
mainFrame.startGame();
});
brick = new ImageView();
brick.setImage(Config.getImages().get(Config.IMAGE_SPLASH_BRICK));
brick.setTranslateX(-1000);
brick.setTranslateY(brick.getImage().getHeight());
breaker = new ImageView();
breaker.setImage(Config.getImages().get(Config.IMAGE_SPLASH_BREAKER));
breaker.setTranslateX(-1000);
breaker.setTranslateY(brick.getTranslateY() + brick.getImage().getHeight() * 5 / 4);
strike = new ImageView();
strike.setImage(Config.getImages().get(Config.IMAGE_SPLASH_STRIKE));
strike.setTranslateY(brick.getTranslateY() -
(strike.getImage().getHeight() - brick.getImage().getHeight()) / 2);
strike.setVisible(false);
pressanykey = new ImageView();
pressanykey.setImage(Config.getImages().get(Config.IMAGE_SPLASH_PRESSANYKEY));
pressanykey.setTranslateX((Config.SCREEN_WIDTH - pressanykey.getImage().getWidth()) / 2);
double y = breaker.getTranslateY() + breaker.getImage().getHeight();
pressanykey.setTranslateY(y + (Config.SCREEN_HEIGHT - y) / 2);
pressanykey.setOpacity(0);
NODES = new ImageView[] {brick, breaker, strike, pressanykey};
Group group = new Group();
group.getChildren().add(background);
group.getChildren().addAll(NODES);
getChildren().add(group);
}
}
