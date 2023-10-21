package ensemble.samples.graphics2d.brickbreaker;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
public class BrickBreakerApp extends Application {
private MainFrame mainFrame;
public MainFrame getMainFrame() {
return mainFrame;
}
public Parent createContent() {
Config.initialize();
Pane root = new Pane();
root.setPrefSize(960, 720);
root.setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
root.setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
mainFrame = new MainFrame(root);
mainFrame.changeState(MainFrame.SPLASH);
return root;
}
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setResizable(false);
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
@Override public void stop() {
MainFrame currentMainFrame = getMainFrame();
currentMainFrame.endGame();
}
public void play() {
MainFrame currentMainFrame = getMainFrame();
currentMainFrame.restartGame();
}
public static void main(String[] args) {
Application.launch(args);
}
public class MainFrame {
private Pane root;
private Splash splash;
private Level level;
private int lifeCount;
private int score;
private MainFrame(Pane root) {
this.root = root;
}
public int getState() {
return state;
}
public int getScore() {
return score;
}
public void setScore(int score) {
this.score = score;
}
public int getLifeCount() {
return lifeCount;
}
public void setLifeCount(int count) {
lifeCount = count;
}
public void increaseLives() {
lifeCount = Math.min(lifeCount + 1, Config.MAX_LIVES);
}
public void decreaseLives() {
lifeCount--;
}
public void startGame() {
lifeCount = 3;
score = 0;
changeState(1);
}
public void endGame() {
if (splash != null) {
splash.stop();
}
if (level != null) {
level.stop();
}
}
public void restartGame() {
if (splash != null) {
splash.start();
}
if (level != null) {
level.restart();
}
}
public static final int SPLASH = 0;
private int state = SPLASH;
public void changeState(int newState) {
this.state = newState;
if (splash != null) {
splash.stop();
}
if (level != null) {
level.stop();
}
if (state < 1 || state > LevelData.getLevelsCount()) {
root.getChildren().remove(level);
level = null;
splash = new Splash(mainFrame);
root.getChildren().add(splash);
splash.start();
} else {
root.getChildren().remove(splash);
splash = null;
level = new Level(mainFrame, state);
root.getChildren().add(level);
level.start();
}
}
}
}
