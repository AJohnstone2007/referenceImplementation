package com.javafx.experiments.height2normal;
import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
public class Height2NormalConverter {
public static Image convertToNormals(Image heightMap, boolean invert, double scale) {
final int w = (int)heightMap.getWidth();
final int h = (int)heightMap.getHeight();
final byte[] heightPixels = new byte[w*h*4];
final byte[] normalPixels = new byte[w*h*4];
final PixelReader reader = heightMap.getPixelReader();
reader.getPixels(0, 0, w, h, PixelFormat.getByteBgraInstance(),heightPixels,0,w*4);
if (invert) {
for (int y=0; y<h; y++) {
for (int x=0; x<w; x++) {
final int pixelIndex = (y*w*4) + (x*4);
heightPixels[pixelIndex] = (byte)(255-Byte.toUnsignedInt(heightPixels[pixelIndex]));
heightPixels[pixelIndex+1] = (byte)(255-Byte.toUnsignedInt(heightPixels[pixelIndex+1]));
heightPixels[pixelIndex+2] = (byte)(255-Byte.toUnsignedInt(heightPixels[pixelIndex+2]));
heightPixels[pixelIndex+3] = heightPixels[pixelIndex+3];
}
}
}
for (int y=0; y<h; y++) {
for (int x=0; x<w; x++) {
final int yAbove = Math.max(0,y-1);
final int yBelow = Math.min(h - 1, y + 1);
final int xLeft = Math.max(0, x - 1);
final int xRight = Math.min(w - 1, x + 1);
final int pixelIndex = (y*w*4) + (x*4);
final int pixelAboveIndex = (yAbove*w*4) + (x*4);
final int pixelBelowIndex = (yBelow*w*4) + (x*4);
final int pixelLeftIndex = (y*w*4) + (xLeft*4);
final int pixelRightIndex = (y*w*4) + (xRight*4);
final int pixelAboveHeight = Byte.toUnsignedInt(heightPixels[pixelAboveIndex]);
final int pixelBelowHeight = Byte.toUnsignedInt(heightPixels[pixelBelowIndex]);
final int pixelLeftHeight = Byte.toUnsignedInt(heightPixels[pixelLeftIndex]);
final int pixelRightHeight = Byte.toUnsignedInt(heightPixels[pixelRightIndex]);
Point3D pixelAbove = new Point3D(x,yAbove,pixelAboveHeight);
Point3D pixelBelow = new Point3D(x,yBelow,pixelBelowHeight);
Point3D pixelLeft = new Point3D(xLeft,y,pixelLeftHeight);
Point3D pixelRight = new Point3D(xRight,y,pixelRightHeight);
Point3D H = pixelLeft.subtract(pixelRight);
Point3D V = pixelAbove.subtract(pixelBelow);
Point3D normal = H.crossProduct(V);
normal = new Point3D(
normal.getX()/w,
normal.getY()/h,
normal.getZ()
);
normalPixels[pixelIndex] = (byte)(255-(normal.getZ() * scale));
normalPixels[pixelIndex+1] = (byte)((normal.getY()*128)+128);
normalPixels[pixelIndex+2] = (byte)((normal.getX()*128)+128);
normalPixels[pixelIndex+3] = (byte)255;
}
}
final WritableImage outImage = new WritableImage(w,h);
outImage.getPixelWriter().setPixels(0,0,w,h,PixelFormat.getByteBgraInstance(),normalPixels,0,w*4);
return outImage;
}
}
