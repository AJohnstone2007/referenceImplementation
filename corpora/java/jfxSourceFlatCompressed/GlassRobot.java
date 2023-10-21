package com.sun.glass.ui;
import java.lang.annotation.Native;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import com.sun.javafx.image.PixelUtils;
public abstract class GlassRobot {
@Native public static final int MOUSE_LEFT_BTN = 1 << 0;
@Native public static final int MOUSE_RIGHT_BTN = 1 << 1;
@Native public static final int MOUSE_MIDDLE_BTN = 1 << 2;
@Native public static final int MOUSE_BACK_BTN = 1 << 3;
@Native public static final int MOUSE_FORWARD_BTN = 1 << 4;
public abstract void create();
public abstract void destroy();
public abstract void keyPress(KeyCode keyCode);
public abstract void keyRelease(KeyCode keyCode);
public abstract double getMouseX();
public abstract double getMouseY();
public abstract void mouseMove(double x, double y);
public abstract void mousePress(MouseButton... buttons);
public abstract void mouseRelease(MouseButton... buttons);
public abstract void mouseWheel(int wheelAmt);
public abstract Color getPixelColor(double x, double y);
public void getScreenCapture(int x, int y, int width, int height, int[] data, boolean scaleToFit) {
throw new InternalError("not implemented");
}
public WritableImage getScreenCapture(WritableImage image, double x, double y, double width,
double height, boolean scaleToFit) {
if (width <= 0) {
throw new IllegalArgumentException("width must be > 0");
}
if (height <= 0) {
throw new IllegalArgumentException("height must be > 0");
}
Screen primaryScreen = Screen.getPrimary();
Objects.requireNonNull(primaryScreen);
double outputScaleX = primaryScreen.getOutputScaleX();
double outputScaleY = primaryScreen.getOutputScaleY();
int data[];
int dw, dh;
if (outputScaleX == 1.0f && outputScaleY == 1.0f) {
data = new int[(int) (width * height)];
getScreenCapture((int) x, (int) y, (int) width, (int) height, data, scaleToFit);
dw = (int) width;
dh = (int) height;
} else {
int pminx = (int) Math.floor(x * outputScaleX);
int pminy = (int) Math.floor(y * outputScaleY);
int pmaxx = (int) Math.ceil((x + width) * outputScaleX);
int pmaxy = (int) Math.ceil((y + height) * outputScaleY);
int pwidth = pmaxx - pminx;
int pheight = pmaxy - pminy;
int tmpdata[] = new int[pwidth * pheight];
getScreenCapture(pminx, pminy, pwidth, pheight, tmpdata, scaleToFit);
dw = pwidth;
dh = pheight;
if (!scaleToFit) {
data = tmpdata;
} else {
data = new int[(int) (width * height)];
int index = 0;
for (int iy = 0; iy < height; iy++) {
double rely = ((y + iy + 0.5f) * outputScaleY) - (pminy + 0.5f);
int irely = (int) Math.floor(rely);
int fracty = (int) ((rely - irely) * 256);
for (int ix = 0; ix < width; ix++) {
double relx = ((x + ix + 0.5f) * outputScaleX) - (pminx + 0.5f);
int irelx = (int) Math.floor(relx);
int fractx = (int) ((relx - irelx) * 256);
data[index++] = interp(tmpdata, irelx, irely, pwidth, pheight, fractx, fracty);
}
}
dw = (int) width;
dh = (int) height;
}
}
return convertFromPixels(image, Application.GetApplication().createPixels(dw, dh, IntBuffer.wrap(data)));
}
public static int convertToRobotMouseButton(MouseButton[] buttons) {
int ret = 0;
for (MouseButton button : buttons) {
switch (button) {
case PRIMARY: ret |= MOUSE_LEFT_BTN; break;
case SECONDARY: ret |= MOUSE_RIGHT_BTN; break;
case MIDDLE: ret |= MOUSE_MIDDLE_BTN; break;
case BACK: ret |= MOUSE_BACK_BTN; break;
case FORWARD: ret |= MOUSE_FORWARD_BTN; break;
default: throw new IllegalArgumentException("MouseButton: " + button + " not supported by Robot");
}
}
return ret;
}
public static Color convertFromIntArgb(int color) {
int alpha = (color >> 24) & 0xFF;
int red = (color >> 16) & 0xFF;
int green = (color >> 8) & 0xFF;
int blue = color & 0xFF;
return new Color(red / 255d, green / 255d, blue / 255d, alpha / 255d);
}
protected static WritableImage convertFromPixels(WritableImage image, Pixels pixels) {
Objects.requireNonNull(pixels);
int width = pixels.getWidth();
int height = pixels.getHeight();
if (image == null || image.getWidth() != width || image.getHeight() != height) {
image = new WritableImage(width, height);
}
int bytesPerComponent = pixels.getBytesPerComponent();
if (bytesPerComponent == 4) {
IntBuffer intBuffer = (IntBuffer) pixels.getPixels();
writeIntBufferToImage(intBuffer, image);
} else if (bytesPerComponent == 1) {
ByteBuffer byteBuffer = (ByteBuffer) pixels.getPixels();
writeByteBufferToImage(byteBuffer, image);
} else {
throw new IllegalArgumentException("bytesPerComponent must be either 4 or 1 but was: " +
bytesPerComponent);
}
return image;
}
private static void writeIntBufferToImage(IntBuffer intBuffer, WritableImage image) {
Objects.requireNonNull(image);
PixelWriter pixelWriter = image.getPixelWriter();
double width = image.getWidth();
double height = image.getHeight();
for (int y = 0; y < height; y++) {
for (int x = 0; x < width; x++) {
int argb = intBuffer.get();
pixelWriter.setArgb(x, y, argb);
}
}
}
private static void writeByteBufferToImage(ByteBuffer byteBuffer, WritableImage image) {
Objects.requireNonNull(image);
PixelWriter pixelWriter = image.getPixelWriter();
double width = image.getWidth();
double height = image.getHeight();
int format = Pixels.getNativeFormat();
for (int y = 0; y < height; y++) {
for (int x = 0; x < width; x++) {
if (format == Pixels.Format.BYTE_BGRA_PRE) {
pixelWriter.setArgb(x, y, PixelUtils.PretoNonPre(bgraPreToRgbaPre(byteBuffer.getInt())));
} else if (format == Pixels.Format.BYTE_ARGB) {
pixelWriter.setArgb(x, y, byteBuffer.getInt());
} else {
throw new IllegalArgumentException("format must be either BYTE_BGRA_PRE or BYTE_ARGB");
}
}
}
}
private static int bgraPreToRgbaPre(int bgraPre) {
return Integer.reverseBytes(bgraPre);
}
private static int interp(int pixels[], int x, int y, int w, int h, int fractx1, int fracty1) {
int fractx0 = 256 - fractx1;
int fracty0 = 256 - fracty1;
int i = y * w + x;
int rgb00 = (x < 0 || y < 0 || x >= w || y >= h) ? 0 : pixels[i];
if (fracty1 == 0) {
if (fractx1 == 0) {
return rgb00;
}
int rgb10 = (y < 0 || x+1 >= w || y >= h) ? 0 : pixels[i+1];
return interp(rgb00, rgb10, fractx0, fractx1);
} else if (fractx1 == 0) {
int rgb01 = (x < 0 || x >= w || y+1 >= h) ? 0 : pixels[i+w];
return interp(rgb00, rgb01, fracty0, fracty1);
} else {
int rgb10 = (y < 0 || x+1 >= w || y >= h) ? 0 : pixels[i+1];
int rgb01 = (x < 0 || x >= w || y+1 >= h) ? 0 : pixels[i+w];
int rgb11 = (x+1 >= w || y+1 >= h) ? 0 : pixels[i+w+1];
return interp(interp(rgb00, rgb10, fractx0, fractx1),
interp(rgb01, rgb11, fractx0, fractx1),
fracty0, fracty1);
}
}
private static int interp(int rgb0, int rgb1, int fract0, int fract1) {
int a0 = (rgb0 >> 24) & 0xff;
int r0 = (rgb0 >> 16) & 0xff;
int g0 = (rgb0 >> 8) & 0xff;
int b0 = (rgb0 ) & 0xff;
int a1 = (rgb1 >> 24) & 0xff;
int r1 = (rgb1 >> 16) & 0xff;
int g1 = (rgb1 >> 8) & 0xff;
int b1 = (rgb1 ) & 0xff;
int a = (a0 * fract0 + a1 * fract1) >> 8;
int r = (r0 * fract0 + r1 * fract1) >> 8;
int g = (g0 * fract0 + g1 * fract1) >> 8;
int b = (b0 * fract0 + b1 * fract1) >> 8;
return (a << 24) | (r << 16) | (g << 8) | b;
}
}
