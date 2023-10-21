package com.sun.javafx.css;
import javafx.css.FontFace;
import javafx.css.StyleConverter.StringStore;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
final public class FontFaceImpl extends FontFace {
public static enum FontFaceSrcType {URL,LOCAL, REFERENCE}
private final Map<String,String> descriptors;
private final List<FontFaceSrc> sources;
public FontFaceImpl(Map<String, String> descriptors, List<FontFaceSrc> sources) {
this.descriptors = descriptors;
this.sources = sources;
}
public Map<String, String> getDescriptors() {
return descriptors;
}
public List<FontFaceSrc> getSources() {
return sources;
}
@Override public String toString() {
StringBuilder sb = new StringBuilder("@font-face { ");
for(Map.Entry<String,String> desc: descriptors.entrySet()) {
sb.append(desc.getKey());
sb.append(" : ");
sb.append(desc.getValue());
sb.append("; ");
}
sb.append("src : ");
for(FontFaceSrc src: sources) {
sb.append(src.getType());
sb.append(" \"");
sb.append(src.getSrc());
sb.append("\", ");
}
sb.append("; ");
sb.append(" }");
return sb.toString();
}
public final void writeBinary(final DataOutputStream os, final StringStore stringStore) throws IOException
{
Set<Map.Entry<String,String>> entrySet = getDescriptors() != null ? getDescriptors().entrySet() : null;
int nEntries = entrySet != null ? entrySet.size() : 0;
os.writeShort(nEntries);
if (entrySet != null) {
for(Map.Entry<String,String> entry : entrySet) {
int index = stringStore.addString(entry.getKey());
os.writeInt(index);
index = stringStore.addString(entry.getValue());
os.writeInt(index);
}
}
List<FontFaceSrc> fontFaceSrcs = getSources();
nEntries = fontFaceSrcs != null ? fontFaceSrcs.size() : 0;
os.writeShort(nEntries);
for (int n=0; n<nEntries; n++) {
FontFaceSrc fontFaceSrc = fontFaceSrcs.get(n);
fontFaceSrc.writeBinary(os, stringStore);
}
}
public final static FontFaceImpl readBinary(int bssVersion, DataInputStream is, String[] strings) throws IOException
{
int nEntries = is.readShort();
Map<String,String> descriptors = new HashMap(nEntries);
for (int n=0; n<nEntries; n++) {
int index = is.readInt();
String key = strings[index];
index = is.readInt();
String value = strings[index];
descriptors.put(key, value);
}
nEntries = is.readShort();
List<FontFaceSrc> fontFaceSrcs = new ArrayList<>(nEntries);
for (int n=0; n<nEntries; n++) {
FontFaceSrc fontFaceSrc = FontFaceSrc.readBinary(bssVersion, is, strings);
fontFaceSrcs.add(fontFaceSrc);
}
return new FontFaceImpl(descriptors, fontFaceSrcs);
}
public static class FontFaceSrc {
private final FontFaceSrcType type;
private final String src;
private final String format;
public FontFaceSrc(FontFaceSrcType type, String src, String format) {
this.type = type;
this.src = src;
this.format = format;
}
public FontFaceSrc(FontFaceSrcType type, String src) {
this.type = type;
this.src = src;
this.format = null;
}
public FontFaceSrcType getType() {
return type;
}
public String getSrc() {
return src;
}
public String getFormat() {
return format;
}
final void writeBinary(final DataOutputStream os, final StringStore stringStore) throws IOException
{
os.writeInt(stringStore.addString(type.name()));
os.writeInt(stringStore.addString(src));
os.writeInt(stringStore.addString(format));
}
final static FontFaceSrc readBinary(int bssVersion, DataInputStream is, String[] strings) throws IOException
{
int index = is.readInt();
FontFaceSrcType type = (strings[index] != null) ? FontFaceSrcType.valueOf(strings[index]) : null;
index = is.readInt();
String src = strings[index];
index = is.readInt();
String format = strings[index];
return new FontFaceSrc(type, src, format);
}
}
}
