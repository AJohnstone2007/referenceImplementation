package com.sun.javafx.iio;
import com.sun.javafx.iio.ImageStorage.ImageType;
import java.nio.Buffer;
import java.nio.ByteBuffer;
public class ImageFrame {
private ImageType imageType;
private ByteBuffer imageData;
private int width;
private int height;
private int stride;
private float pixelScale;
private byte[][] palette;
private ImageMetadata metadata;
public ImageFrame(ImageType imageType, ByteBuffer imageData,
int width, int height, int stride, byte[][] palette,
ImageMetadata metadata)
{
this(imageType, imageData,
width, height, stride, palette,
1.0f, metadata);
}
public ImageFrame(ImageType imageType, ByteBuffer imageData,
int width, int height, int stride, byte[][] palette,
float pixelScale, ImageMetadata metadata)
{
this.imageType = imageType;
this.imageData = imageData;
this.width = width;
this.height = height;
this.stride = stride;
this.palette = palette;
this.pixelScale = pixelScale;
this.metadata = metadata;
}
public ImageType getImageType() {
return this.imageType;
}
public Buffer getImageData() {
return this.imageData;
}
public int getWidth() {
return this.width;
}
public int getHeight() {
return this.height;
}
public int getStride() {
return this.stride;
}
public byte[][] getPalette() {
return this.palette;
}
public void setPixelScale(float pixelScale) {
this.pixelScale = pixelScale;
}
public float getPixelScale() {
return pixelScale;
}
public ImageMetadata getMetadata() {
return this.metadata;
}
}
