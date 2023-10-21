package com.sun.javafx.util;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
public class DataURI {
public static boolean matchScheme(String uri) {
if (uri == null || uri.length() < 6) {
return false;
}
uri = uri.stripLeading();
return uri.length() > 5 && "data:".equalsIgnoreCase(uri.substring(0, 5));
}
public static DataURI tryParse(String uri) {
if (!matchScheme(uri)) {
return null;
}
uri = uri.trim();
int dataSeparator = uri.indexOf(',', 5);
if (dataSeparator < 0) {
throw new IllegalArgumentException("Invalid URI: " + uri);
}
String mimeType = "text", mimeSubtype = "plain";
boolean base64 = false;
String[] headers = uri.substring(5, dataSeparator).split(";");
Map<String, String> nameValuePairs = Collections.emptyMap();
if (headers.length > 0) {
int start = 0;
int mimeSeparator = headers[0].indexOf('/');
if (mimeSeparator > 0) {
mimeType = headers[0].substring(0, mimeSeparator);
mimeSubtype = headers[0].substring(mimeSeparator + 1);
start = 1;
}
for (int i = start; i < headers.length; ++i) {
String header = headers[i];
int separator = header.indexOf('=');
if (separator < 0) {
if (i < headers.length - 1) {
throw new IllegalArgumentException("Invalid URI: " + uri);
}
base64 = "base64".equalsIgnoreCase(headers[headers.length - 1]);
} else {
if (nameValuePairs.isEmpty()) {
nameValuePairs = new HashMap<>();
}
nameValuePairs.put(header.substring(0, separator).toLowerCase(), header.substring(separator + 1));
}
}
}
String data = uri.substring(dataSeparator + 1);
Charset charset = Charset.defaultCharset();
return new DataURI(
uri,
data,
mimeType,
mimeSubtype,
nameValuePairs,
base64,
base64 ?
Base64.getDecoder().decode(data) :
URLDecoder.decode(data.replace("+", "%2B"), charset).getBytes(charset));
}
private final String originalUri;
private final String originalData;
private final String mimeType, mimeSubtype;
private final Map<String, String> parameters;
private final boolean base64;
private final byte[] data;
private DataURI(
String originalUri,
String originalData,
String mimeType,
String mimeSubtype,
Map<String, String> parameters,
boolean base64,
byte[] decodedData) {
this.originalUri = originalUri;
this.originalData = originalData;
this.mimeType = mimeType;
this.mimeSubtype = mimeSubtype;
this.parameters = parameters;
this.base64 = base64;
this.data = decodedData;
}
public String getMimeType() {
return mimeType;
}
public String getMimeSubtype() {
return mimeSubtype;
}
public Map<String, String> getParameters() {
return parameters;
}
public boolean isBase64() {
return base64;
}
public byte[] getData() {
return data;
}
@Override
public String toString() {
if (originalData.length() < 32) {
return originalUri;
}
return originalUri.substring(0, originalUri.length() - originalData.length())
+ originalData.substring(0, 14) + "..." + originalData.substring(originalData.length() - 14);
}
@Override
public boolean equals(Object o) {
if (this == o) return true;
if (!(o instanceof DataURI)) return false;
DataURI dataURI = (DataURI)o;
return base64 == dataURI.base64
&& Objects.equals(mimeType, dataURI.mimeType)
&& Objects.equals(mimeSubtype, dataURI.mimeSubtype)
&& Arrays.equals(data, dataURI.data);
}
@Override
public int hashCode() {
int result = Objects.hash(mimeType, mimeSubtype, base64);
result = 31 * result + Arrays.hashCode(data);
return result;
}
}
