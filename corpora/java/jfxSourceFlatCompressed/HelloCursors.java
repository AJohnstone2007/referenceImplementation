package hello;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
public class HelloCursors extends Application {
@Override public void start(Stage stage) {
stage.setTitle("Hello Cursors");
TilePane root = new TilePane(5, 5);
root.getChildren().addAll(
createBox(Cursor.DEFAULT),
createBox(Cursor.CROSSHAIR),
createBox(Cursor.TEXT),
createBox(Cursor.WAIT),
createBox(Cursor.SW_RESIZE),
createBox(Cursor.SE_RESIZE),
createBox(Cursor.NW_RESIZE),
createBox(Cursor.NE_RESIZE),
createBox(Cursor.N_RESIZE),
createBox(Cursor.S_RESIZE),
createBox(Cursor.W_RESIZE),
createBox(Cursor.E_RESIZE),
createBox(Cursor.OPEN_HAND),
createBox(Cursor.CLOSED_HAND),
createBox(Cursor.HAND),
createBox(Cursor.DISAPPEAR),
createBox(Cursor.MOVE),
createBox(Cursor.H_RESIZE),
createBox(Cursor.V_RESIZE),
createBox(Cursor.NONE)
);
Scene scene = new Scene(root, 600, 450);
stage.setScene(scene);
stage.show();
}
private Node createBox(Cursor cursor) {
Rectangle r = new Rectangle();
r.setWidth(100);
r.setHeight(100);
r.setFill(Color.color(Math.random(), Math.random(), Math.random()));
Text t = new Text();
t.setText(cursor.toString());
StackPane stack = new StackPane();
stack.setCursor(cursor);
stack.getChildren().addAll(r, t);
return stack;
}
public static void main(String[] args) {
Application.launch(args);
}
}
