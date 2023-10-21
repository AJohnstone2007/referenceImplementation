package com.sun.javafx.iio.ios;
import com.sun.javafx.iio.common.ImageDescriptor;
public class IosDescriptor extends ImageDescriptor {
private static final String formatName = "PNGorJPEGorBMP";
private static final String[] extensions = { "bmp", "png", "jpg", "jpeg", "gif" };
private static final Signature[] signatures = {
new Signature((byte) 0xff, (byte) 0xD8),
new Signature((byte) 137, (byte) 80, (byte) 78, (byte) 71,
(byte) 13, (byte) 10, (byte) 26, (byte) 10),
new Signature((byte)0x42, (byte)0x4D),
new Signature(new byte[] {'G', 'I', 'F', '8', '7', 'a'}),
new Signature(new byte[] {'G', 'I', 'F', '8', '9', 'a'})
};
private static final String[] mimeSubtypes = { "bmp", "png", "x-png", "jpeg", "gif"};
private static ImageDescriptor theInstance = null;
private IosDescriptor() {
super(formatName, extensions, signatures, mimeSubtypes);
}
public static synchronized ImageDescriptor getInstance() {
if (theInstance == null) {
theInstance = new IosDescriptor();
}
return theInstance;
}
}
