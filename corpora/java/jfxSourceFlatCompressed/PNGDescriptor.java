package com.sun.javafx.iio.png;
import com.sun.javafx.iio.common.ImageDescriptor;
public class PNGDescriptor extends ImageDescriptor {
private static final String formatName = "PNG";
private static final String[] extensions = { "png" };
private static final Signature[] signatures = {
new Signature((byte) 137, (byte) 80, (byte) 78, (byte) 71,
(byte) 13, (byte) 10, (byte) 26, (byte) 10)
};
private static final String[] mimeSubtypes = { "png", "x-png" };
private static ImageDescriptor theInstance = null;
private PNGDescriptor() {
super(formatName, extensions, signatures, mimeSubtypes);
}
public static synchronized ImageDescriptor getInstance() {
if (theInstance == null) {
theInstance = new PNGDescriptor();
}
return theInstance;
}
}
