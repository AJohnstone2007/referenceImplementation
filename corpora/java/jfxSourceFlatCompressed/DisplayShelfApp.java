package ensemble.samples.graphics2d.displayshelf;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
public class DisplayShelfApp extends Application {
private static final double WIDTH = 450, HEIGHT = 480;
private static final String[] urls = {
"/ensemble/samples/shared-resources/Animal1.jpg",
"/ensemble/samples/shared-resources/Animal2.jpg",
"/ensemble/samples/shared-resources/Animal3.jpg",
"/ensemble/samples/shared-resources/Animal4.jpg",
"/ensemble/samples/shared-resources/Animal5.jpg",
"/ensemble/samples/shared-resources/Animal6.jpg",
"/ensemble/samples/shared-resources/Animal7.jpg",
"/ensemble/samples/shared-resources/Animal8.jpg",
"/ensemble/samples/shared-resources/Animal9.jpg",
"/ensemble/samples/shared-resources/Animal10.jpg",
"/ensemble/samples/shared-resources/Animal11.jpg",
"/ensemble/samples/shared-resources/Animal12.jpg",
"/ensemble/samples/shared-resources/Animal13.jpg",
"/ensemble/samples/shared-resources/Animal14.jpg"
};
private Timeline animation;
public Parent createContent() {
Image[] images = new Image[14];
for (int i = 0; i < 14; i++) {
String url = getClass().getResource(urls[i]).toExternalForm();
images[i] = new Image(url);
}
DisplayShelf displayShelf = new DisplayShelf(images);
displayShelf.setPrefSize(WIDTH, HEIGHT);
String css = getClass().getResource("DisplayShelf.css").toExternalForm();
displayShelf.getStylesheets().add(css);
return displayShelf;
}
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setResizable(false);
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
