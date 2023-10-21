package com.sun.javafx.iio;
public interface ImageLoadListener {
void imageLoadProgress(ImageLoader loader, float percentageComplete);
public void imageLoadWarning(ImageLoader loader, String message);
public void imageLoadMetaData(ImageLoader loader, ImageMetadata metadata);
}
