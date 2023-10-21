import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.awt.image.BufferedImage;
public class DndTestDragViewRawImage extends Application {
Image image = createImage(240, 240);
public static void main(String[] args) {
Application.launch(args);
}
@Override
public void start(Stage stage) {
ImageView imageView = new ImageView(image);
imageView.setOnDragDetected(event -> {
ClipboardContent content = new ClipboardContent();
content.putImage(image);
Dragboard dragboard = imageView.startDragAndDrop(TransferMode.ANY);
dragboard.setContent(content);
dragboard.setDragView(image);
});
Label label = new Label("Click the image and drag. " +
"The drag image displayed with the cursor (drag view) " +
"should match the source image");
VBox vBox = new VBox(label, imageView);
vBox.setSpacing(5.0);
vBox.setAlignment(Pos.CENTER);
stage.setScene(new Scene(vBox, 480, 480));
stage.setTitle("Drag View Image Colors");
stage.show();
}
private static Image createImage(int width, int height) {
BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
for (int y = 0; y < height; y++) {
for (int x = 0; x < width; x++) {
if (x < width * 0.33) {
image.setRGB(x, y, 0xFF0000);
} else if (x < width * 0.66) {
image.setRGB(x, y, 0x00FF00);
} else {
image.setRGB(x, y, 0x0000FF);
}
}
}
return SwingFXUtils.toFXImage(image, null);
}
}
