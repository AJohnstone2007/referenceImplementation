package ensemble.samples.scenegraph.events.cursor;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
public class CursorApp extends Application {
public Parent createContent() {
TilePane tilePaneRoot = new TilePane(5, 5);
tilePaneRoot.setMinSize(TilePane.USE_PREF_SIZE, TilePane.USE_PREF_SIZE);
tilePaneRoot.setMaxSize(TilePane.USE_PREF_SIZE, TilePane.USE_PREF_SIZE);
tilePaneRoot.setHgap(2);
tilePaneRoot.setVgap(2);
tilePaneRoot.getChildren().addAll(
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
createBox(Cursor.NONE));
return tilePaneRoot;
}
private Node createBox(Cursor cursor) {
Label label = new Label(cursor.toString());
label.setAlignment(Pos.CENTER);
label.setPrefSize(85, 65);
label.setStyle("-fx-border-color: #aaaaaa; -fx-background-color: #dddddd;");
label.setCursor(cursor);
return label;
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
