package com.sun.javafx.iio;
public class ImageMetadata {
public final Float gamma;
public final Boolean blackIsZero;
public final Integer backgroundIndex;
public final Integer backgroundColor;
public final Integer delayTime;
public final Integer loopCount;
public final Integer transparentIndex;
public final Integer imageWidth;
public final Integer imageHeight;
public final Integer imageLeftPosition;
public final Integer imageTopPosition;
public final Integer disposalMethod;
public ImageMetadata(Float gamma, Boolean blackIsZero,
Integer backgroundIndex, Integer backgroundColor,
Integer transparentIndex, Integer delayTime, Integer loopCount,
Integer imageWidth, Integer imageHeight,
Integer imageLeftPosition, Integer imageTopPosition,
Integer disposalMethod) {
this.gamma = gamma;
this.blackIsZero = blackIsZero;
this.backgroundIndex = backgroundIndex;
this.backgroundColor = backgroundColor;
this.transparentIndex = transparentIndex;
this.delayTime = delayTime;
this.loopCount = loopCount;
this.imageWidth = imageWidth;
this.imageHeight = imageHeight;
this.imageLeftPosition = imageLeftPosition;
this.imageTopPosition = imageTopPosition;
this.disposalMethod = disposalMethod;
}
@Override
public String toString() {
StringBuffer sb = new StringBuffer("["+getClass().getName());
if (gamma != null) {
sb.append(" gamma: " + gamma);
}
if (blackIsZero != null) {
sb.append(" blackIsZero: " + blackIsZero);
}
if (backgroundIndex != null) {
sb.append(" backgroundIndex: " + backgroundIndex);
}
if (backgroundColor != null) {
sb.append(" backgroundColor: " + backgroundColor);
}
if (delayTime != null) {
sb.append(" delayTime: " + delayTime);
}
if (loopCount != null) {
sb.append(" loopCount: " + loopCount);
}
if (transparentIndex != null) {
sb.append(" transparentIndex: " + transparentIndex);
}
if (imageWidth != null) {
sb.append(" imageWidth: " + imageWidth);
}
if (imageHeight != null) {
sb.append(" imageHeight: " + imageHeight);
}
if (imageLeftPosition != null) {
sb.append(" imageLeftPosition: " + imageLeftPosition);
}
if (imageTopPosition != null) {
sb.append(" imageTopPosition: " + imageTopPosition);
}
if (disposalMethod != null) {
sb.append(" disposalMethod: " + disposalMethod);
}
sb.append("]");
return sb.toString();
}
}
