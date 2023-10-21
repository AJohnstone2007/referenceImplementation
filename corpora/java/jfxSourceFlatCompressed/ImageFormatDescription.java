package com.sun.javafx.iio;
import java.util.Arrays;
import java.util.List;
public interface ImageFormatDescription {
String getFormatName();
List<String> getExtensions();
List<Signature> getSignatures();
List<String> getMIMESubtypes();
public final class Signature {
private final byte[] bytes;
public Signature(final byte... bytes) {
this.bytes = bytes;
}
public int getLength() {
return bytes.length;
}
public boolean matches(final byte[] streamBytes) {
if (streamBytes.length < bytes.length) {
return false;
}
for (int i = 0; i < bytes.length; i++) {
if (streamBytes[i] != bytes[i]) {
return false;
}
}
return true;
}
@Override
public int hashCode() {
return Arrays.hashCode(bytes);
}
@Override
public boolean equals(final Object other) {
if (this == other) {
return true;
}
if (!(other instanceof Signature)) {
return false;
}
return Arrays.equals(bytes, ((Signature) other).bytes);
}
}
}
