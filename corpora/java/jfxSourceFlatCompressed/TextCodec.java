package com.sun.webkit.text;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
final class TextCodec {
private final Charset charset;
private static final Map<String, String> RE_MAP = Map.of(
"ISO-10646-UCS-2", "UTF-16");
private TextCodec(String encoding) {
charset = Charset.forName(encoding);
}
private byte[] encode(char[] data) {
ByteBuffer bb = charset.encode(CharBuffer.wrap(data));
byte[] encoded = new byte[bb.remaining()];
bb.get(encoded);
return encoded;
}
private String decode(byte[] data) {
CharBuffer cb = charset.decode(ByteBuffer.wrap(data));
char[] decoded = new char[cb.remaining()];
cb.get(decoded);
return new String(decoded);
}
private static String[] getEncodings() {
List<String> encodings = new ArrayList<String>();
Map<String, Charset> ac = Charset.availableCharsets();
for (Map.Entry<String, Charset> entry: ac.entrySet()) {
String e = entry.getKey();
encodings.add(e);
encodings.add(e);
Charset c = entry.getValue();
for (String a : c.aliases()) {
if (a.equals("8859_1")) continue;
encodings.add(a);
String r = RE_MAP.get(a);
encodings.add(r == null ? e : r);
}
}
return encodings.toArray(new String[0]);
}
}
