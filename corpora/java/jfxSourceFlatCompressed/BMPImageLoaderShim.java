package com.sun.javafx.iio.bmp;
public class BMPImageLoaderShim {
public static boolean checkDisjointMasks(int m1, int m2, int m3) {
return BMPImageLoader.checkDisjointMasks(m1, m2, m3);
}
public static boolean isPow2Minus1(int i) {
return BMPImageLoader.isPow2Minus1(i);
}
}
