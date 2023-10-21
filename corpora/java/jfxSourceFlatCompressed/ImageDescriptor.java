package com.sun.javafx.iio.common;
import com.sun.javafx.iio.ImageFormatDescription;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
public class ImageDescriptor implements ImageFormatDescription {
private final String formatName;
private final List<String> extensions;
private final List<Signature> signatures;
private final List<String> mimeSubtypes;
public ImageDescriptor(String formatName, String[] extensions, Signature[] signatures, String[] mimeSubtypes) {
this.formatName = formatName;
this.extensions = Collections.unmodifiableList(
Arrays.asList(extensions));
this.signatures = Collections.unmodifiableList(
Arrays.asList(signatures));
this.mimeSubtypes = Collections.unmodifiableList(
Arrays.asList(mimeSubtypes));
}
public String getFormatName() {
return formatName;
}
public List<String> getExtensions() {
return extensions;
}
public List<Signature> getSignatures() {
return signatures;
}
public List<String> getMIMESubtypes() {
return mimeSubtypes;
}
}
