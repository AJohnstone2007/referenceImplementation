package hello;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
public class HelloWindowAbuse extends Application {
int WIDTH = 300;
int HEIGHT = 300;
static final int WindowCount = 16;
enum StageState { CLOSED, OPEN, HIDDEN };
Stage[] stages;
StageState[] stagesState;
Group topGroup;
Stage topStage;
Runnable flicker;
Random random = new Random();
boolean running = true;
final Object lock = new Object();
CountDownLatch latch;
static boolean verbose = System.getProperty("DEBUG", "false").equalsIgnoreCase("true");
static boolean openCloseOnly = System.getProperty("OpenClose", "false").equalsIgnoreCase("true");
static int delay = Integer.parseInt(System.getProperty("delay", "0"));
static int count = Integer.parseInt(System.getProperty("count", "16"));
@Override
public void start(Stage stage) throws Exception {
System.out.println("HelloWindowAbuse!");
stage.setTitle("Window Abuse 2");
stage.setWidth(WIDTH);
stage.setHeight(HEIGHT);
stage.setResizable(true);
topStage = stage;
Group group = new Group();
ObservableList<Node> content = group.getChildren();
final Label nameLabel = new Label("Root Window Create Abuse");
nameLabel.setLayoutX(50);
nameLabel.setLayoutY(HEIGHT / 2);
content.add(nameLabel);
final Button button = new Button("Close");
button.setLayoutX(10);
button.setLayoutY(20);
button.setOnAction(new EventHandler<ActionEvent>() {
@Override
public void handle(ActionEvent t) {
System.out.println("Setting running = false");
running = false;
topStage.close();
}
});
content.add(button);
Scene scene = new Scene(group);
stage.setScene(scene);
stage.onCloseRequestProperty().addListener(new ChangeListener<EventHandler<WindowEvent>>() {
@Override
public void changed(ObservableValue<? extends EventHandler<WindowEvent>> ov, EventHandler<WindowEvent> t, EventHandler<WindowEvent> t1) {
System.out.println("Exiting");
running = false;
}
});
stage.show();
int i;
stages = new Stage[count];
stagesState = new StageState[count];
for (i = 0; i < stages.length; i++) {
stages[i] = createStage(i);
stagesState[i] = StageState.OPEN;
}
if (openCloseOnly) {
System.out.println("Using the Open/Close only method");
flicker = new Runnable() {
@Override
public void run() {
int i;
for (i = 0; i < stages.length; i++) {
boolean on = running ? random.nextBoolean() : false;
if (on) {
if (stages[i] == null) {
if (verbose) {
System.out.println("creating stage " + i);
}
stages[i] = createStage(i);
}
} else {
if (stages[i] != null) {
if (verbose) {
System.out.println("closing stage " + i);
}
stages[i].hide();
stages[i].close();
stages[i] = null;
}
}
}
latch.countDown();
}
};
} else {
flicker = new Runnable() {
@Override
public void run() {
int i;
for (i = 0; i < stages.length; i++) {
int rand = random.nextInt(100);
if (stages[i] == null) {
if (rand > 66) {
stages[i] = createStage(i);
stagesState[i] = StageState.OPEN;
if (verbose) {
System.out.println("CREATE");
}
}
} else {
if (rand < 20) {
if (verbose) {
System.out.println("LEAVE ALONE");
}
continue;
}
if (stagesState[i] == StageState.HIDDEN) {
if (rand < 50) {
if (verbose) {
System.out.println("SHOW");
}
stages[i].show();
stagesState[i] = StageState.OPEN;
continue;
}
}
rand = random.nextInt(100);
if (rand < 40) {
if (verbose) {
System.out.println("CLOSE");
}
stages[i].hide();
stages[i].close();
stages[i] = null;
stagesState[i] = StageState.CLOSED;
} else if (rand < 60) {
double w = 50;
if (random.nextBoolean()) {
w = -50;
}
double h = 50;
if (random.nextBoolean()) {
h = -50;
}
w = Math.max(stages[i].getWidth() + w, 50);
h = Math.max(stages[i].getHeight() + h, 50);
stages[i].setWidth(w);
stages[i].setHeight(h);
if (verbose) {
System.out.println("RESIZE");
}
} else if (rand < 80) {
int xdirection = 50;
if (random.nextBoolean()) {
xdirection = -50;
}
int ydirection = 50;
if (random.nextBoolean()) {
ydirection = -50;
}
stages[i].setX(
stages[i].getX() + xdirection);
stages[i].setY(
stages[i].getY() + ydirection);
if (verbose) {
System.out.println("MOVE");
}
} else if (rand < 90) {
stages[i].hide();
stagesState[i] = StageState.HIDDEN;
if (verbose) {
System.out.println("HIDE");
}
} else
if (random.nextBoolean()) {
stages[i].toFront();
} else {
stages[i].toBack();
}
if (verbose) {
System.out.println("Z");
}
}
}
latch.countDown();
}
};
}
Thread animator = new Thread(new Runnable() {
@Override
public void run() {
try {
System.out.println("Waiting 6 sec to get on screen");
Thread.sleep(6000);
if (delay > 0) {
System.out.println("Using a delay of "+delay+" ms between pulses");
}
} catch (Exception e) {
e.printStackTrace();
}
int count = 0;
while (true) {
latch = new CountDownLatch(1);
try {
count++;
Platform.runLater(flicker);
latch.await();
if (delay > 0) {
if (verbose)
System.out.println("Delaying..."+delay+" ms");
Thread.sleep(delay);
}
if (count % 100 == 0) {
System.out.println("\t" + count + " ...");
}
} catch (Exception e) {
e.printStackTrace();
}
}
}
});
animator.setDaemon(true);
animator.start();
}
Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.BEIGE,
Color.CHOCOLATE, Color.CORAL, Color.CORNFLOWERBLUE};
public Stage createStage(int index) {
Stage stage = new Stage();
stage.setTitle("Window abuse");
stage.setWidth(WIDTH);
stage.setHeight(HEIGHT);
Group group = new Group();
ObservableList<Node> content = group.getChildren();
final Rectangle rect = new Rectangle(WIDTH, HEIGHT);
rect.setFill(colors[index % colors.length]);
content.add(rect);
final Label nameLabel = new Label("Window " + index);
nameLabel.setLayoutX(50);
nameLabel.setLayoutY(HEIGHT / 2);
content.add(nameLabel);
Scene scene = new Scene(group);
stage.setScene(scene);
stage.setResizable(false);
double topX = topStage.getX();
double topY = topStage.getY();
int x = random.nextInt(10) * 10;
int y = random.nextInt(10) * 10;
stage.setX(topX + x + 30);
stage.setY(topY + y + 30);
stage.show();
return stage;
}
public static void main(String[] args) {
Application.launch(args);
}
}
