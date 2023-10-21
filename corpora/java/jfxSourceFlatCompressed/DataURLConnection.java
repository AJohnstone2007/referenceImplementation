package com.sun.webkit.network.data;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.LinkedList;
final class DataURLConnection extends URLConnection {
private static final Charset US_ASCII = Charset.forName("US-ASCII");
private final String mediaType;
private final byte[] data;
private final InputStream inputStream;
DataURLConnection(URL url) throws IOException {
super(url);
String content = url.toString();
content = content.substring(content.indexOf(':') + 1);
int commaPosition = content.indexOf(',');
if (commaPosition < 0) {
throw new ProtocolException(
"Invalid URL, ',' not found in: " + getURL());
}
String metadata = content.substring(0, commaPosition);
String dataString = content.substring(commaPosition + 1);
String mimeType = null;
LinkedList<String> parameters = new LinkedList<String>();
Charset charset = null;
boolean base64 = false;
String[] components = metadata.split(";", -1);
for (int i = 0; i < components.length; i++) {
String component = components[i];
if (component.equalsIgnoreCase("base64")) {
base64 = true;
} else {
if (i == 0 && !component.contains("=")) {
mimeType = component;
} else {
parameters.add(component);
if (component.toLowerCase().startsWith("charset=")) {
try {
charset = Charset.forName(component.substring(8));
} catch (IllegalArgumentException ex) {
UnsupportedEncodingException ex2 =
new UnsupportedEncodingException();
ex2.initCause(ex);
throw ex2;
}
}
}
}
}
if (mimeType == null || mimeType.isEmpty()) {
mimeType = "text/plain";
}
if (charset == null) {
charset = US_ASCII;
if (mimeType.toLowerCase().startsWith("text/")) {
parameters.addFirst("charset=" + charset.name());
}
}
StringBuilder mediaTypeBuilder = new StringBuilder();
mediaTypeBuilder.append(mimeType);
for (String parameter : parameters) {
mediaTypeBuilder.append(';').append(parameter);
}
mediaType = mediaTypeBuilder.toString();
if (base64) {
String s = urlDecode(dataString, US_ASCII);
s = s.replaceAll("\\s+", "");
data = Base64.getMimeDecoder().decode(s);
} else {
String s = urlDecode(dataString, charset);
data = s.getBytes(charset);
}
inputStream = new ByteArrayInputStream(data);
}
@Override
public void connect() {
connected = true;
}
@Override
public InputStream getInputStream() {
return inputStream;
}
@Override
public String getContentType() {
return mediaType;
}
@Override
public String getContentEncoding() {
return null;
}
@Override
public int getContentLength() {
return data != null ? data.length : -1;
}
private static String urlDecode(String str, Charset charset) {
int length = str.length();
StringBuilder sb = new StringBuilder(length);
byte[] bytes = null;
int i = 0;
while (i < length) {
char c = str.charAt(i);
if (c == '%') {
if (bytes == null) {
bytes = new byte[(length - i) / 3];
}
int count = 0;
int proceedTo = i;
for ( ; i < length; i += 3) {
c = str.charAt(i);
if (c != '%') {
break;
}
if (i + 2 >= length) {
proceedTo = length;
break;
}
byte b;
try {
b = (byte) Integer.parseInt(
str.substring(i + 1, i + 3), 16);
} catch(NumberFormatException ex) {
proceedTo = i + 3;
break;
}
bytes[count++] = b;
}
if (count > 0) {
sb.append(new String(bytes, 0, count, charset));
}
while (i < proceedTo) {
sb.append(str.charAt(i++));
}
} else {
sb.append(c);
i++;
}
}
return sb.toString();
}
}
