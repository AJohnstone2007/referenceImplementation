package test.javafx.scene.image;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
public final class ImageViewConfig {
private final Image image;
private final float x;
private final float y;
private final Rectangle2D viewport;
private final float fitWidth;
private final float fitHeight;
private final boolean preserveRatio;
private ImageViewConfig(final Image image,
final float x,
final float y,
final Rectangle2D viewport,
final float fitWidth,
final float fitHeight,
final boolean preserveRatio) {
this.image = image;
this.x = x;
this.y = y;
this.viewport = viewport;
this.fitWidth = fitWidth;
this.fitHeight = fitHeight;
this.preserveRatio = preserveRatio;
}
public void applyTo(final ImageView imageView) {
imageView.setImage(image);
imageView.setX(x);
imageView.setY(y);
imageView.setViewport(viewport);
imageView.setFitWidth(fitWidth);
imageView.setFitHeight(fitHeight);
imageView.setPreserveRatio(preserveRatio);
}
public static ImageViewConfig config(final Image image,
final float x,
final float y) {
return new ImageViewConfig(image, x, y, null, 0, 0, false);
}
public static ImageViewConfig config(final Image image,
final float x,
final float y,
final float fitWidth,
final float fitHeight,
final boolean preserveRatio) {
return new ImageViewConfig(image, x, y, null, fitWidth, fitHeight,
preserveRatio);
}
public static ImageViewConfig config(final Image image,
final float x,
final float y,
final float vpX,
final float vpY,
final float vpWidth,
final float vpHeight,
final float fitWidth,
final float fitHeight,
final boolean preserveRatio) {
return new ImageViewConfig(image, x, y,
new Rectangle2D(vpX, vpY, vpWidth, vpHeight),
fitWidth, fitHeight, preserveRatio);
}
}
