package com.sun.media.jfxmediaimpl.platform.java;
import com.sun.media.jfxmediaimpl.MetadataParserImpl;
import java.io.IOException;
import java.util.Arrays;
import com.sun.media.jfxmedia.locator.Locator;
import com.sun.media.jfxmedia.logging.Logger;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
final class ID3MetadataParser extends MetadataParserImpl {
private static final int ID3_VERSION_MIN = 2;
private static final int ID3_VERSION_MAX = 4;
private static final String CHARSET_UTF_8 = "UTF-8";
private static final String CHARSET_ISO_8859_1 = "ISO-8859-1";
private static final String CHARSET_UTF_16 = "UTF-16";
private static final String CHARSET_UTF_16BE = "UTF-16BE";
private int COMMCount = 0;
private int TXXXCount = 0;
private int version = 3;
private boolean unsynchronized = false;
public ID3MetadataParser(Locator locator) {
super(locator);
}
protected void parse() {
try {
if (!Charset.isSupported(CHARSET_ISO_8859_1)) {
throw new UnsupportedCharsetException(CHARSET_ISO_8859_1);
}
byte[] buf = getBytes(10);
version = (int)(buf[3] & 0xFF);
if (buf[0] == 0x49 && buf[1] == 0x44 && buf[2] == 0x33 &&
(version >= ID3_VERSION_MIN && version <= ID3_VERSION_MAX)) {
int flags = buf[5] & 0xFF;
if ((flags & 0x80) == 0x80) {
unsynchronized = true;
}
int tagSize = 0;
for (int i = 6, shift = 21; i < 10; i++) {
tagSize += (buf[i] & 0x7f) << shift;
shift -= 7;
}
startRawMetadata(tagSize + 10);
stuffRawMetadata(buf, 0, 10);
readRawMetadata(tagSize);
setParseRawMetadata(true);
skipBytes(10);
while (getStreamPosition() < tagSize) {
int frameSize;
byte[] idBytes;
if (2 == version) {
idBytes = getBytes(3);
frameSize = getU24();
} else {
idBytes = getBytes(4);
frameSize = getFrameSize();
skipBytes(2);
}
if (0 == idBytes[0]) {
if (Logger.canLog(Logger.DEBUG)) {
Logger.logMsg(Logger.DEBUG, "ID3MetadataParser", "parse",
"ID3 parser: zero padding detected at "
+getStreamPosition()+", terminating");
}
break;
}
String frameID = new String(idBytes, Charset.forName(CHARSET_ISO_8859_1));
if (Logger.canLog(Logger.DEBUG)) {
Logger.logMsg(Logger.DEBUG, "ID3MetadataParser", "parse",
getStreamPosition()+"\\"+tagSize
+": frame ID "+frameID+", size "+frameSize);
}
if (frameID.equals("APIC") || frameID.equals("PIC")) {
byte[] data = getBytes(frameSize);
if (unsynchronized) {
data = unsynchronizeBuffer(data);
}
byte[] image = frameID.equals("PIC") ? getImageFromPIC(data) : getImageFromAPIC(data);
if (image != null) {
addMetadataItem("image", image);
}
} else if (frameID.startsWith("T") && !frameID.equals("TXXX")) {
String encoding = getEncoding();
byte[] data = getBytes(frameSize - 1);
if (unsynchronized) {
data = unsynchronizeBuffer(data);
}
String value = new String(data, encoding);
String[] tag = getTagFromFrameID(frameID);
if (tag != null) {
for (int i = 0; i < tag.length; i++) {
Object tagValue = convertValue(tag[i], value);
if (tagValue != null) {
addMetadataItem(tag[i], tagValue);
}
}
}
} else if (frameID.equals("COMM") || frameID.equals("COM")) {
String encoding = getEncoding();
byte[] data = getBytes(3);
if (unsynchronized) {
data = unsynchronizeBuffer(data);
}
String language = new String(data, Charset.forName(CHARSET_ISO_8859_1));
data = getBytes(frameSize - 4);
if (unsynchronized) {
data = unsynchronizeBuffer(data);
}
String value = new String(data, encoding);
if (value != null) {
int index = value.indexOf(0x00);
String content = "";
String comment;
if (index == 0) {
if (isTwoByteEncoding(encoding)) {
comment = value.substring(2);
} else {
comment = value.substring(1);
}
} else {
content = value.substring(0, index);
if (isTwoByteEncoding(encoding)) {
comment = value.substring(index + 2);
} else {
comment = value.substring(index + 1);
}
}
String[] tag = getTagFromFrameID(frameID);
if (tag != null) {
for (int i = 0; i < tag.length; i++) {
addMetadataItem(tag[i] + "-" + COMMCount, content + "[" + language + "]=" + comment);
COMMCount++;
}
}
}
} else if (frameID.equals("TXX") || frameID.equals("TXXX")) {
String encoding = getEncoding();
byte[] data = getBytes(frameSize-1);
if (unsynchronized) {
data = unsynchronizeBuffer(data);
}
String value = new String(data, encoding);
if (null != value){
int index = value.indexOf(0x00);
String description = (index != 0) ? value.substring(0, index) : "";
String text = isTwoByteEncoding(encoding) ? value.substring(index+2) : value.substring(index+1);
String[] tag = getTagFromFrameID(frameID);
if (tag != null) {
for (int i = 0; i < tag.length; i++) {
if (description.equals("")) {
addMetadataItem(tag[i] + "-" + TXXXCount, text);
} else {
addMetadataItem(tag[i] + "-" + TXXXCount, description + "=" + text);
}
TXXXCount++;
}
}
}
} else {
skipBytes(frameSize);
}
}
}
} catch (Exception ex) {
if (Logger.canLog(Logger.WARNING)) {
Logger.logMsg(Logger.WARNING, "ID3MetadataParser", "parse",
"Exception while processing ID3v2 metadata: "+ex);
}
} finally {
if (null != rawMetaBlob) {
setParseRawMetadata(false);
addRawMetadata(RAW_ID3_METADATA_NAME);
disposeRawMetadata();
}
done();
}
}
private int getFrameSize() throws IOException {
if (version == 4) {
byte[] buf = getBytes(4);
int size = 0;
for (int i = 0, shift = 21; i < 4; i++) {
size += (buf[i] & 0x7f) << shift;
shift -= 7;
}
return size;
} else {
return getInteger();
}
}
private String getEncoding() throws IOException {
byte encodingType = getNextByte();
if (encodingType == 0x00) {
return CHARSET_ISO_8859_1;
} else if (encodingType == 0x01) {
return CHARSET_UTF_16;
} else if (encodingType == 0x02) {
return CHARSET_UTF_16BE;
} else if (encodingType == 0x03) {
return CHARSET_UTF_8;
} else {
throw new IllegalArgumentException();
}
}
private boolean isTwoByteEncoding(String encoding) {
if (encoding.equals(CHARSET_ISO_8859_1) || encoding.equals(CHARSET_UTF_8)) {
return false;
} else if (encoding.equals(CHARSET_UTF_16) || encoding.equals(CHARSET_UTF_16BE)) {
return true;
} else {
throw new IllegalArgumentException();
}
}
private String[] getTagFromFrameID(String frameID) {
if (frameID.equals("TPE2") || frameID.equals("TP2")) {
return new String[]{MetadataParserImpl.ALBUMARTIST_TAG_NAME};
} else if (frameID.equals("TALB") || frameID.equals("TAL")) {
return new String[]{MetadataParserImpl.ALBUM_TAG_NAME};
} else if (frameID.equals("TPE1") || frameID.equals("TP1")) {
return new String[]{MetadataParserImpl.ARTIST_TAG_NAME};
} else if (frameID.equals("COMM") || frameID.equals("COM")) {
return new String[]{MetadataParserImpl.COMMENT_TAG_NAME};
} else if (frameID.equals("TCOM") || frameID.equals("TCM")) {
return new String[]{MetadataParserImpl.COMPOSER_TAG_NAME};
} else if (frameID.equals("TLEN") || frameID.equals("TLE")) {
return new String[]{MetadataParserImpl.DURATION_TAG_NAME};
} else if (frameID.equals("TCON") || frameID.equals("TCO")) {
return new String[]{MetadataParserImpl.GENRE_TAG_NAME};
} else if (frameID.equals("TIT2") || frameID.equals("TT2")) {
return new String[]{MetadataParserImpl.TITLE_TAG_NAME};
} else if (frameID.equals("TRCK") || frameID.equals("TRK")) {
return new String[]{MetadataParserImpl.TRACKNUMBER_TAG_NAME, MetadataParserImpl.TRACKCOUNT_TAG_NAME};
} else if (frameID.equals("TPOS") || frameID.equals("TPA")) {
return new String[]{MetadataParserImpl.DISCNUMBER_TAG_NAME, MetadataParserImpl.DISCCOUNT_TAG_NAME};
} else if (frameID.equals("TYER") || frameID.equals("TDRC")) {
return new String[]{MetadataParserImpl.YEAR_TAG_NAME};
} else if (frameID.equals("TXX") || frameID.equals("TXXX")) {
return new String[]{MetadataParserImpl.TEXT_TAG_NAME};
}
return null;
}
private byte[] getImageFromPIC(byte[] data) {
int imgOffset = 5;
while (0 != data[imgOffset] && imgOffset < data.length) {
imgOffset++;
}
if (imgOffset == data.length) {
return null;
}
String type = new String(data, 1, 3, Charset.forName(CHARSET_ISO_8859_1));
if (Logger.canLog(Logger.DEBUG)) {
Logger.logMsg(Logger.DEBUG, "ID3MetadataParser", "getImageFromPIC",
"PIC type: "+type);
}
if (type.equalsIgnoreCase("PNG") || type.equalsIgnoreCase("JPG")) {
return Arrays.copyOfRange(data, imgOffset+1, data.length);
}
if (Logger.canLog(Logger.WARNING)) {
Logger.logMsg(Logger.WARNING, "ID3MetadataParser", "getImageFromPIC",
"Unsupported picture type found \""+type+"\"");
}
return null;
}
private byte[] getImageFromAPIC(byte[] data) {
boolean isImageJPEG = false;
boolean isImagePNG = false;
int maxIndex = data.length - 10;
int offset = 0;
for (int j = 0; j < maxIndex; j++) {
if (data[j] == 'i'
&& data[j + 1] == 'm'
&& data[j + 2] == 'a'
&& data[j + 3] == 'g'
&& data[j + 4] == 'e'
&& data[j + 5] == '/') {
j += 6;
if (data[j] == 'j'
&& data[j + 1] == 'p'
&& data[j + 2] == 'e'
&& data[j + 3] == 'g') {
isImageJPEG = true;
offset = j + 4;
break;
}
else if (data[j] == 'p'
&& data[j + 1] == 'n'
&& data[j + 2] == 'g') {
isImagePNG = true;
offset = j + 3;
break;
}
}
}
if (isImageJPEG) {
boolean isSignatureFound = false;
int upperBound = data.length - 1;
for (int j = offset; j < upperBound; j++) {
if (-1 == data[j] && -40 == data[j + 1]) {
isSignatureFound = true;
offset = j;
break;
}
}
if (isSignatureFound) {
return Arrays.copyOfRange(data, offset, data.length);
}
}
if (isImagePNG) {
boolean isSignatureFound = false;
int upperBound = data.length - 7;
for (int j = offset; j < upperBound; j++) {
if (-119 == data[j]
&& 80 == data[j + 1]
&& 78 == data[j + 2]
&& 71 == data[j + 3]
&& 13 == data[j + 4]
&& 10 == data[j + 5]
&& 26 == data[j + 6]
&& 10 == data[j + 7])
{
isSignatureFound = true;
offset = j;
break;
}
}
if (isSignatureFound) {
return Arrays.copyOfRange(data, offset, data.length);
}
}
return null;
}
private byte[] unsynchronizeBuffer(byte[] data) {
byte[] udata = new byte[data.length];
int udatalen = 0;
for (int i = 0; i < data.length; i++) {
if (((data[i] & 0xFF) == 0xFF && data[i + 1] == 0x00 && data[i + 2] == 0x00)
|| ((data[i] & 0xFF) == 0xFF && data[i + 1] == 0x00 && (data[i + 2] & 0xE0) == 0xE0)) {
udata[udatalen] = data[i];
udatalen++;
udata[udatalen] = data[i + 2];
udatalen++;
i += 2;
} else {
udata[udatalen] = data[i];
udatalen++;
}
}
return Arrays.copyOf(udata, udatalen);
}
}
