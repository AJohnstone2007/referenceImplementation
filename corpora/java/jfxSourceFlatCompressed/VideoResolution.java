package com.sun.media.jfxmedia.track;
public class VideoResolution {
public int width;
public int height;
public VideoResolution(int width, int height) {
if (width <= 0)
throw new IllegalArgumentException("width <= 0");
if (height <= 0)
throw new IllegalArgumentException("height <= 0");
this.width = width;
this.height = height;
}
public int getWidth() {
return width;
}
public int getHeight() {
return height;
}
public String toString() {
return "VideoResolution {width: "+width+" height: "+height+"}";
}
}
