package hello;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Dimension2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
public class HelloImage extends Application {
private static final String imageURL = "hello/JavaFX.png";
private static final String slowImageURL =
"http://duke.kenai.com/misc/DrinkingBeer.jpg";
private static final String animImageURL = "hello/animated_89_c.gif";
private static final String animCursorURL = "hello/javafx-loading-32x32.gif";
@Override public void start(Stage stage) {
stage.setTitle("Hello Image");
Scene scene = new Scene(new Group(), 600, 450);
scene.setFill(Color.LIGHTGRAY);
ObservableList<Node> seq = ((Group)scene.getRoot()).getChildren();
Dimension2D d = ImageCursor.getBestSize(1,1);
System.err.println("BestCursor Size ="+d);
Image animImage = new Image(animImageURL);
addImageToObservableList(seq, 160, 20, 420, 120, new Image(imageURL),
createImageCursor(animImageURL, 16, 16));
final Image slowImage = new Image(slowImageURL, true);
addImageToObservableList(seq, 20, 160, 560, 250, slowImage, Cursor.CROSSHAIR);
addImageToObservableList(seq, 20, 20, 120, 120, animImage,
createImageCursor(slowImageURL, 1862*0.4f, 0));
stage.getIcons().add(slowImage);
stage.setScene(scene);
stage.show();
}
private static void addImageToObservableList(ObservableList<Node> seq,
int x, int y,
int w, int h,
Image image,
Cursor cursor) {
ImageView imageView = new ImageView();
imageView.setX(x);
imageView.setY(y);
imageView.setFitWidth(w);
imageView.setFitHeight(h);
imageView.setPreserveRatio(true);
imageView.setImage(image);
imageView.setCursor(cursor);
seq.add(imageView);
}
private static Cursor createImageCursor(final String url,
final float hotspotX,
final float hotspotY) {
final Image cursorImage = new Image(url, 32, 32, false, true, true);
return new ImageCursor(cursorImage, hotspotX, hotspotY);
}
public static void main(String[] args) {
Application.launch(args);
}
}
