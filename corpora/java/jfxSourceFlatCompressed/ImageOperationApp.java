package ensemble.samples.graphics2d.images.imageoperator;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
public class ImageOperationApp extends Application {
private final DoubleProperty gridSize = new SimpleDoubleProperty(3.0);
public final DoubleProperty gridSizeProperty() {
return gridSize;
}
private final DoubleProperty hueFactor = new SimpleDoubleProperty(12.0);
public final DoubleProperty hueFactorProperty() {
return hueFactor;
}
private final DoubleProperty hueOffset = new SimpleDoubleProperty(240.0);
public final DoubleProperty hueOffsetProperty() {
return hueOffset;
}
private static void renderImage(WritableImage img, double gridSize,
double hueFactor, double hueOffset) {
PixelWriter pw = img.getPixelWriter();
double w = img.getWidth();
double h = img.getHeight();
double xRatio = 0.0;
double yRatio = 0.0;
double hue = 0.0;
for (int y = 0; y < h; y++) {
for (int x = 0; x < w; x++) {
xRatio = x/w;
yRatio = y/h;
hue = Math.sin(yRatio*(gridSize*Math.PI)) *
Math.sin(xRatio*(gridSize*Math.PI)) *
Math.tan(hueFactor / 20.0) * 360.0 + hueOffset;
Color c = Color.hsb(hue, 1.0, 1.0);
pw.setColor(x, y, c);
}
}
}
public Parent createContent() {
StackPane root = new StackPane();
final WritableImage img = new WritableImage(200, 200);
gridSize.addListener((Observable observable) -> {
renderImage(img, gridSize.doubleValue(),
hueFactor.doubleValue(), hueOffset.doubleValue());
});
hueFactor.addListener((Observable observable) -> {
renderImage(img, gridSize.doubleValue(),
hueFactor.doubleValue(), hueOffset.doubleValue());
});
hueOffset.addListener((Observable observable) -> {
renderImage(img, gridSize.doubleValue(),
hueFactor.doubleValue(), hueOffset.doubleValue());
});
renderImage(img, 3.0, 12.0, 240.0);
ImageView view = new ImageView(img);
root.getChildren().add(view);
return root;
}
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
