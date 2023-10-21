package com.sun.javafx.iio.png;
import com.sun.javafx.iio.common.ImageTools;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
public class PNGIDATChunkInputStream extends InputStream {
static final int IDAT_TYPE = 0x49444154;
private DataInputStream source;
private int numBytesAvailable = 0;
private boolean foundAllIDATChunks = false;
private int nextChunkLength = 0;
private int nextChunkType = 0;
PNGIDATChunkInputStream(DataInputStream input, int firstIDATChunkLength) throws IOException {
if (firstIDATChunkLength < 0) {
throw new IOException("Invalid chunk length");
}
this.source = input;
this.numBytesAvailable = firstIDATChunkLength;
}
private void nextChunk() throws IOException {
if (!foundAllIDATChunks) {
ImageTools.skipFully(source, 4);
int chunkLength = source.readInt();
if (chunkLength < 0) {
throw new IOException("Invalid chunk length");
}
int chunkType = source.readInt();
if (chunkType == IDAT_TYPE) {
numBytesAvailable += chunkLength;
} else {
foundAllIDATChunks = true;
nextChunkLength = chunkLength;
nextChunkType = chunkType;
}
}
}
boolean isFoundAllIDATChunks() {
return foundAllIDATChunks;
}
int getNextChunkLength() {
return nextChunkLength;
}
int getNextChunkType() {
return nextChunkType;
}
@Override
public int read() throws IOException {
if (numBytesAvailable == 0) {
nextChunk();
}
if (numBytesAvailable == 0) {
return -1;
} else {
--numBytesAvailable;
return source.read();
}
}
@Override
public int read(byte[] b, int off, int len) throws IOException {
if (numBytesAvailable == 0) {
nextChunk();
if (numBytesAvailable == 0) {
return -1;
}
}
int totalRead = 0;
while (numBytesAvailable > 0 && len > 0) {
int numToRead = len < numBytesAvailable ? len : numBytesAvailable;
int numRead = source.read(b, off, numToRead);
if (numRead == -1) {
throw new EOFException();
}
numBytesAvailable -= numRead;
off += numRead;
len -= numRead;
totalRead += numRead;
if (numBytesAvailable == 0 && len > 0) {
nextChunk();
}
}
return totalRead;
}
}
