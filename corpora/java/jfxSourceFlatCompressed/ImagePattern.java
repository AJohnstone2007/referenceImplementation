package javafx.scene.paint;
import javafx.beans.NamedArg;
import javafx.scene.image.Image;
import com.sun.javafx.beans.event.AbstractNotifyListener;
import com.sun.javafx.tk.Toolkit;
public final class ImagePattern extends Paint {
private Image image;
public final Image getImage() {
return image;
}
private double x;
public final double getX() {
return x;
}
private double y;
public final double getY() {
return y;
}
private double width = 1f;
public final double getWidth() {
return width;
}
private double height = 1f;
public final double getHeight() {
return height;
}
private boolean proportional = true;
public final boolean isProportional() {
return proportional;
}
@Override public final boolean isOpaque() {
return ((com.sun.prism.paint.ImagePattern)acc_getPlatformPaint()).isOpaque();
}
private Object platformPaint;
public ImagePattern(@NamedArg("image") Image image) {
if (image == null) {
throw new NullPointerException("Image must be non-null.");
} else if (image.getProgress() < 1.0) {
throw new IllegalArgumentException("Image not yet loaded");
}
this.image = image;
}
public ImagePattern(@NamedArg("image") Image image, @NamedArg("x") double x, @NamedArg("y") double y, @NamedArg("width") double width,
@NamedArg("height") double height, @NamedArg("proportional") boolean proportional) {
if (image == null) {
throw new NullPointerException("Image must be non-null.");
} else if (image.getProgress() < 1.0) {
throw new IllegalArgumentException("Image not yet loaded");
}
this.image = image;
this.x = x;
this.y = y;
this.width = width;
this.height = height;
this.proportional = proportional;
}
@Override
boolean acc_isMutable() {
return Toolkit.getImageAccessor().isAnimation(image);
}
@Override
void acc_addListener(AbstractNotifyListener platformChangeListener) {
Toolkit.getImageAccessor().getImageProperty(image)
.addListener(platformChangeListener);
}
@Override
void acc_removeListener(AbstractNotifyListener platformChangeListener) {
Toolkit.getImageAccessor().getImageProperty(image)
.removeListener(platformChangeListener);
}
@Override Object acc_getPlatformPaint() {
if (acc_isMutable() || platformPaint == null) {
platformPaint = Toolkit.getToolkit().getPaint(this);
assert platformPaint != null;
}
return platformPaint;
}
}
