package ensemble.samples.media.audioclip;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
public class AudioClipApp extends Application {
public Parent createContent() {
final double xStart = 12;
final double xOffset = 30.0;
final double barWidth = 22.0;
Rectangle r1 = new Rectangle(0, 15, barWidth * 11.5, 10);
r1.setFill(new Color(0.2, 0.12, 0.1, 1.0));
Rectangle r2 = new Rectangle(0, -25, barWidth * 11.5, 10);
r2.setFill(new Color(0.2, 0.12, 0.1, 1.0));
final Group content = new Group(
r1,
r2,
createKey(Color.PURPLE, xStart + 0 * xOffset, barWidth, 1),
createKey(Color.BLUEVIOLET, xStart + 1 * xOffset, barWidth, 2),
createKey(Color.BLUE, xStart + 2 * xOffset, barWidth, 3),
createKey(Color.GREEN, xStart + 3 * xOffset, barWidth, 4),
createKey(Color.GREENYELLOW, xStart + 4 * xOffset, barWidth, 5),
createKey(Color.YELLOW, xStart + 5 * xOffset, barWidth, 6),
createKey(Color.ORANGE, xStart + 6 * xOffset, barWidth, 7),
createKey(Color.RED, xStart + 7 * xOffset, barWidth, 8));
StackPane root = new StackPane() {
@Override protected void layoutChildren() {
double scale = Math.min(
(getWidth()-20) / content.getBoundsInLocal().getWidth(),
(getHeight()-20) / content.getBoundsInLocal().getHeight()
);
content.setScaleX(scale);
content.setScaleY(scale);
super.layoutChildren();
}
};
root.getChildren().add(content);
return root;
}
private static AudioClip getNoteClip(String name) {
try {
URI baseURI = AudioClipApp.class.getResource("AudioClipApp.class").toURI();
if (baseURI.getScheme().equals("jar")) {
String basePath = baseURI.getSchemeSpecificPart();
if (basePath.contains("!/")) {
basePath = basePath.substring(0, basePath.indexOf("!/"));
}
baseURI = new URI(basePath);
}
URL noteURL = baseURI.resolve("resources/"+name).toURL();
if (noteURL.getProtocol().equals("http") || noteURL.getProtocol().equals("https")) {
HttpURLConnection urlCon = (HttpURLConnection)noteURL.openConnection();
urlCon.setRequestMethod("HEAD");
urlCon.connect();
if (urlCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
noteURL = null;
}
urlCon.disconnect();
} else if (noteURL.getProtocol().equals("file")) {
File f = new File(noteURL.getPath());
if (!f.exists() || !f.isFile()) {
noteURL = null;
}
} else {
noteURL = null;
}
if (noteURL != null) {
return new AudioClip(noteURL.toExternalForm());
}
} catch (Exception e) {}
return new AudioClip(
AudioClipApp.class.getResource("/ensemble/samples/shared-resources/"+name).toExternalForm());
}
public static Rectangle createKey(Color color, double x,
double width, int note) {
double height = 100 - ((note - 1) * 5);
final AudioClip barNote = getNoteClip("Note"+note+".wav");
Rectangle rectangle = new Rectangle(x, -(height / 2), width, height);
rectangle.setFill(color);
Lighting lighting = new Lighting(new Light.Point(-20, -20, 100, Color.WHITE));
lighting.setSurfaceScale(1);
rectangle.setEffect(lighting);
rectangle.setOnMousePressed((MouseEvent me) -> {
barNote.play();
});
return rectangle;
}
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) { launch(args); }
}
