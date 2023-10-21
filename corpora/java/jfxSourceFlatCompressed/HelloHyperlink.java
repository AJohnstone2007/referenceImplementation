package hello;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
public class HelloHyperlink extends Application {
private static final String animImageURL = "hello/animated_89_c.gif";
public static void main(String[] args) {
Application.launch(args);
}
@Override public void start(Stage stage) {
stage.setTitle("Hello Hyperlink");
Scene scene = new Scene(new Group(), 600, 450);
scene.setFill(Color.GHOSTWHITE);
Hyperlink link = new Hyperlink();
link.setLayoutX(25);
link.setLayoutY(40);
link.setText("I am a hyperlink!");
((Group)scene.getRoot()).getChildren().add(link);
Hyperlink animatedLink = new Hyperlink();
animatedLink.setLayoutX(25);
animatedLink.setLayoutY(100);
animatedLink.setText("I am a hyperlink with an animated Image!");
animatedLink.setGraphic(imageView(animImageURL, 0, 0, 16, 16));
((Group)scene.getRoot()).getChildren().add(animatedLink);
stage.setScene(scene);
stage.show();
}
private static ImageView imageView(String url, int x, int y, int w, int h) {
ImageView imageView = new ImageView();
imageView.setX(x);
imageView.setY(y);
imageView.setFitWidth(w);
imageView.setFitHeight(h);
imageView.setPreserveRatio(true);
imageView.setImage(new Image(url));
return imageView;
}
}
