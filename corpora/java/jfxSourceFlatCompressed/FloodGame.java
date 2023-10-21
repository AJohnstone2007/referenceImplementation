package hello;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
public class FloodGame extends Application {
final int NUMROWS = 6;
final int NUMCOLS = 8;
static final Color[] colors = {
Color.web("#d02020"),
Color.web("#ff8010"),
Color.web("#d0e000"),
Color.web("#10c010"),
Color.web("#3030f0"),
Color.web("#d050ff"),
};
static Random rand = new Random();
private Color newColor;
private Cell[] cells = Cell.createCells();
private Set<Cell> changingCells = new HashSet<Cell>();
boolean gameWon() {
Paint iColor = cells[0].getFill();
int count = 0;
for (Cell c : cells) {
if (iColor == c.getFill()) {
count++;
}
}
return count == NUMROWS * NUMCOLS;
};
public static Color randColor() {
return colors[rand.nextInt(colors.length)];
}
private Cell cellAt(int r, int c) {
if (r < 0 || c < 0 || r >= NUMROWS || c >= NUMCOLS) {
return null;
} else {
return cells[r * NUMCOLS + c];
}
}
private Set findMatchingCells() {
Set set = new HashSet<Cell>();
flood(0, 0, (Color)cellAt(0, 0).getFill() , set);
return set;
}
private void flood(int r, int c, Color color, Set set){
Cell cell = cellAt(r, c);
if (cell == null)
return;
if (set.contains(cell))
return;
if (cell.getFill() == color) {
set.add(cell);
flood(r+1, c, color, set);
flood(r, c+1, color, set);
flood(r-1, c, color, set);
flood(r, c-1, color, set);
}
}
int numMoves = 0;
Label label ;
Label doneLabel;
public void animate() {
for (Cell cell : changingCells) {
final Cell cellf = cell;
FadeTransition fadeOut = new FadeTransition(Duration.millis(400), cell);
fadeOut.setFromValue(1);
fadeOut.setToValue(0);
fadeOut.setInterpolator(Interpolator.EASE_OUT);
fadeOut.setOnFinished( event -> cellf.setFill(newColor));
FadeTransition fadeIn = new FadeTransition(Duration.millis(300), cell);
fadeIn.setFromValue(0);
fadeIn.setToValue(1);
fadeIn.setInterpolator(Interpolator.EASE_OUT);
fadeIn.setOnFinished(event -> {
if (gameWon()) {
doneLabel.setText("YOU WON!!");
} else {
doneLabel.setText("");
}
});
SequentialTransition st = new SequentialTransition();
st.getChildren().addAll(fadeOut, fadeIn);
st.play();
}
}
@Override public void start(Stage stage) {
stage.setTitle("Flood Game in Java");
Scene scene = new Scene(new Group(), 500, 450);
Stop[] stops = new Stop[2];
stops[0] = new Stop(0.0f, Color.web("#e0e0e0"));
stops[1] = new Stop(1.0f, Color.web("#a0a0a0"));
LinearGradient lg = new LinearGradient(0f, 0f, 0f, 1f, true, CycleMethod.NO_CYCLE, stops);
scene.setFill(lg);
HBox hbox = new HBox();
((Group)scene.getRoot()).getChildren().add(hbox);
hbox.setSpacing(30);
hbox.setLayoutX(30);
hbox.setLayoutY(30);
Group group = new Group();
Rectangle outerRect = new Rectangle();
outerRect.setWidth(326);
outerRect.setHeight(250);
outerRect.setFill(Color.web("#606060"));
outerRect.setArcWidth(16);
outerRect.setArcHeight(16);
group.getChildren().add(outerRect);
Group group2 = new Group();
group2.setLayoutX(15);
group2.setLayoutY(15);
group2.getChildren().addAll(cells);
Reflection effect = new Reflection();
effect.setTopOffset(30f);
effect.setTopOpacity(0.7f);
effect.setFraction(0.5f);
group.setEffect(effect);
group.getChildren().add(group2);
hbox.getChildren().add(group);
VBox vbox = new VBox();
((Group)scene.getRoot()).getChildren().add(vbox);
vbox.setSpacing(10);
for (final Color c : colors) {
Button button = new Button();
Rectangle rect = new Rectangle();
rect.setWidth(40);
rect.setHeight(10);
rect.setFill(c);
button.setGraphic(rect);
vbox.getChildren().add(button);
button.setOnAction(e -> {
System.out.println("Action on Button");
changingCells.clear();
changingCells = findMatchingCells();
newColor = c;
animate();
numMoves += 1;
label.setText("Moves: " + numMoves);
});
}
label = new Label();
label.setText("Moves: "+numMoves);
vbox.getChildren().add(label);
doneLabel = new Label();
doneLabel.setText("");
vbox.getChildren().add(doneLabel);
hbox.getChildren().add(vbox);
stage.setScene(scene);
stage.show();
}
public static void main(String[] args) {
Application.launch(args);
}
}
class Cell extends Rectangle {
int row;
int col;
public Cell(int row, int col) {
this.row = row;
this.col = col;
}
public static Cell[] createCells() {
Cell[] cells = new Cell[48];
int cellIndex = 0;
for (int r=0; r<6; r++) {
for (int c=0; c<8; c++) {
Cell cell = new Cell(r,c);
cell.setX(c * 38);
cell.setY(r * 38);
cell.setWidth(30);
cell.setHeight(30);
cell.setFill(FloodGame.randColor());
cell.setArcWidth(8);
cell.setArcHeight(8);
cells[cellIndex] = cell;
cellIndex++;
}
}
return cells;
}
}
