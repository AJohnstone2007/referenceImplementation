package test.javafx.css.imagecacheleaktest;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.lang.ref.WeakReference;
import static test.javafx.css.imagecacheleaktest.Constants.*;
public class ImageCacheLeakApp extends Application {
WeakReference<Image> img1Ref;
WeakReference<Image> img2Ref;
int err = ERROR_NONE;
ImageView imageView;
Group root;
Scene scene;
@Override
public void start(Stage stage) throws Exception {
imageView = new ImageView();
root = new Group();
root.getChildren().add(imageView);
scene = new Scene(root);
stage.setScene(scene);
scene.getStylesheets().add(ImageCacheLeakApp.class.getResource("css.css").toExternalForm());
stage.show();
imageView.applyCss();
imageView.getStyleClass().add("image1");
imageView.applyCss();
img1Ref = new WeakReference<Image>(imageView.getImage());
imageView.getStyleClass().remove("image1");
imageView.applyCss();
imageView.getStyleClass().add("image2");
imageView.applyCss();
img2Ref = new WeakReference<Image>(imageView.getImage());
if (img1Ref.get() == null || img2Ref.get() == null) {
stage.hide();
System.exit(ERROR_IMAGE_VIEW);
}
try {
byte[] buf = new byte[1024 * 1024 * 20];
} catch (Exception e) {
} finally {
if (img1Ref.get() != null) {
err = ERROR_LEAK;
}
if (img2Ref.get() == null) {
err = ERROR_INCORRECT_GC;
}
stage.hide();
System.exit(err);
}
}
public static void main(String[] args) {
Application.launch(args);
}
}
