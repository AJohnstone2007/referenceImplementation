package com.sun.javafx.font;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
class FontFileReader implements FontConstants {
String filename;
long filesize;
RandomAccessFile raFile;
public FontFileReader(String filename) {
this.filename = filename;
}
public String getFilename() {
return filename;
}
@SuppressWarnings("removal")
public synchronized boolean openFile() throws PrivilegedActionException {
if (raFile != null) {
return false;
}
raFile = AccessController.doPrivileged(
(PrivilegedAction<RandomAccessFile>) () -> {
try {
return new RandomAccessFile(filename, "r");
} catch (FileNotFoundException fnfe) {
return null;
}
}
);
if (raFile != null) {
try {
filesize = raFile.length();
return true;
} catch (IOException e) {
}
}
return false;
}
public synchronized void closeFile() throws IOException {
if (raFile != null) {
raFile.close();
raFile = null;
readBuffer = null;
}
}
public synchronized long getLength() {
return filesize;
}
public synchronized void reset() throws IOException {
if (raFile != null) {
raFile.seek(0);
}
}
static class Buffer {
byte[] data;
int pos;
int orig;
Buffer(byte[] data, int bufStart) {
this.orig = this.pos = bufStart;
this.data = data;
}
int getInt(int tpos) {
tpos += orig;
int val = data[tpos++]&0xff;
val <<= 8;
val |= data[tpos++]&0xff;
val <<= 8;
val |= data[tpos++]&0xff;
val <<= 8;
val |= data[tpos++]&0xff;
return val;
}
int getInt() {
int val = data[pos++]&0xff;
val <<= 8;
val |= data[pos++]&0xff;
val <<= 8;
val |= data[pos++]&0xff;
val <<= 8;
val |= data[pos++]&0xff;
return val;
}
short getShort(int tpos) {
tpos += orig;
int val = data[tpos++]&0xff;
val <<= 8;
val |= data[tpos++]&0xff;
return (short)val;
}
short getShort() {
int val = data[pos++]&0xff;
val <<= 8;
val |= data[pos++]&0xff;
return (short)val;
}
char getChar(int tpos) {
tpos += orig;
int val = data[tpos++]&0xff;
val <<= 8;
val |= data[tpos++]&0xff;
return (char)val;
}
char getChar() {
int val = data[pos++]&0xff;
val <<= 8;
val |= data[pos++]&0xff;
return (char)(val);
}
void position(int newPos) {
pos = orig + newPos;
}
int capacity() {
return data.length-orig;
}
byte get() {
return data[pos++];
}
byte get(int tpos) {
tpos += orig;
return data[tpos];
}
void skip(int nbytes) {
pos += nbytes;
}
void get(int startPos, byte[] dest, int destPos, int destLen) {
System.arraycopy(data, orig+startPos, dest, destPos, destLen);
}
}
synchronized private int readFromFile(byte[] buffer,
long seekPos, int requestedLen) {
try {
raFile.seek(seekPos);
int bytesRead = raFile.read(buffer, 0, requestedLen);
return bytesRead;
} catch (IOException e) {
if (PrismFontFactory.debugFonts) {
e.printStackTrace();
}
return 0;
}
}
private static final int READBUFFERSIZE = 1024;
private byte[] readBuffer;
private int readBufferLen;
private int readBufferStart;
synchronized public Buffer readBlock(int offset, int len) {
if (readBuffer == null) {
readBuffer = new byte[READBUFFERSIZE];
readBufferLen = 0;
}
if (len <= READBUFFERSIZE) {
if (readBufferStart <= offset &&
readBufferStart+readBufferLen >= offset+len) {
return new Buffer(readBuffer, offset - readBufferStart);
} else {
readBufferStart = offset;
readBufferLen = (offset+READBUFFERSIZE > filesize) ?
(int)filesize - offset : READBUFFERSIZE;
readFromFile(readBuffer, readBufferStart, readBufferLen);
return new Buffer(readBuffer, 0);
}
} else {
byte[] data = new byte[len];
readFromFile(data, offset, len);
return new Buffer(data, 0);
}
}
}
