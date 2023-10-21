package com.sun.media.jfxmedia.locator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Map;
public abstract class ConnectionHolder {
private static int DEFAULT_BUFFER_SIZE = 4096;
ReadableByteChannel channel;
ByteBuffer buffer = ByteBuffer.allocateDirect(DEFAULT_BUFFER_SIZE);
static ConnectionHolder createMemoryConnectionHolder(ByteBuffer buffer) {
return new MemoryConnectionHolder(buffer);
}
static ConnectionHolder createURIConnectionHolder(URI uri, Map<String,Object> connectionProperties) throws IOException {
return new URIConnectionHolder(uri, connectionProperties);
}
static ConnectionHolder createFileConnectionHolder(URI uri) throws IOException {
return new FileConnectionHolder(uri);
}
static ConnectionHolder createHLSConnectionHolder(URI uri) throws IOException {
return new HLSConnectionHolder(uri);
}
public int readNextBlock() throws IOException {
buffer.rewind();
if (buffer.limit() < buffer.capacity()) {
buffer.limit(buffer.capacity());
}
if (null == channel) {
throw new ClosedChannelException();
}
return channel.read(buffer);
}
public ByteBuffer getBuffer() {
return buffer;
}
abstract int readBlock(long position, int size) throws IOException;
abstract boolean needBuffer();
abstract boolean isSeekable();
abstract boolean isRandomAccess();
public abstract long seek(long position);
public void closeConnection() {
try {
if (channel != null) {
channel.close();
}
} catch (IOException ioex) {}
finally {
channel = null;
}
}
int property(int prop, int value) {
return 0;
}
private static class FileConnectionHolder extends ConnectionHolder {
private RandomAccessFile file = null;
FileConnectionHolder(URI uri) throws IOException {
channel = openFile(uri);
}
boolean needBuffer() {
return false;
}
boolean isRandomAccess() {
return true;
}
boolean isSeekable() {
return true;
}
public long seek(long position) {
try {
((FileChannel)channel).position(position);
return position;
} catch(IOException ioex) {
return -1;
}
}
int readBlock(long position, int size) throws IOException {
if (null == channel) {
throw new ClosedChannelException();
}
if (buffer.capacity() < size) {
buffer = ByteBuffer.allocateDirect(size);
}
buffer.rewind().limit(size);
return ((FileChannel)channel).read(buffer, position);
}
private ReadableByteChannel openFile(final URI uri) throws IOException {
if (file != null) {
file.close();
}
file = new RandomAccessFile(new File(uri), "r");
return file.getChannel();
}
@Override
public void closeConnection() {
super.closeConnection();
if (file != null) {
try {
file.close();
} catch (IOException ex) {
} finally {
file = null;
}
}
}
}
private static class URIConnectionHolder extends ConnectionHolder {
private URI uri;
private URLConnection urlConnection;
URIConnectionHolder(URI uri, Map<String,Object> connectionProperties) throws IOException {
this.uri = uri;
urlConnection = uri.toURL().openConnection();
if (connectionProperties != null) {
for(Map.Entry<String,Object> entry : connectionProperties.entrySet()) {
Object value = entry.getValue();
if (value instanceof String) {
urlConnection.setRequestProperty(entry.getKey(), (String)value);
}
}
}
channel = openChannel(null);
}
boolean needBuffer() {
String scheme = uri.getScheme().toLowerCase();
return ("http".equals(scheme) || "https".equals(scheme));
}
boolean isSeekable() {
return (urlConnection instanceof HttpURLConnection) ||
(urlConnection instanceof JarURLConnection) ||
isJRT() || isResource();
}
boolean isRandomAccess() {
return false;
}
int readBlock(long position, int size) throws IOException {
throw new IOException();
}
public long seek(long position) {
if (urlConnection instanceof HttpURLConnection) {
URLConnection tmpURLConnection = null;
try{
tmpURLConnection = uri.toURL().openConnection();
HttpURLConnection httpConnection = (HttpURLConnection)tmpURLConnection;
httpConnection.setRequestMethod("GET");
httpConnection.setUseCaches(false);
httpConnection.setRequestProperty("Range", "bytes=" + position + "-");
if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
closeConnection();
urlConnection = tmpURLConnection;
tmpURLConnection = null;
channel = openChannel(null);
return position;
} else {
return -1;
}
} catch (IOException ioex) {
return -1;
} finally {
if (tmpURLConnection != null) {
Locator.closeConnection(tmpURLConnection);
}
}
} else if ((urlConnection instanceof JarURLConnection) || isJRT() || isResource()) {
try {
closeConnection();
urlConnection = uri.toURL().openConnection();
long skip_left = position;
InputStream inputStream = urlConnection.getInputStream();
do {
long skip = inputStream.skip(skip_left);
skip_left -= skip;
} while (skip_left > 0);
channel = openChannel(inputStream);
return position;
} catch (IOException ioex) {
return -1;
}
}
return -1;
}
@Override
public void closeConnection() {
super.closeConnection();
Locator.closeConnection(urlConnection);
urlConnection = null;
}
private ReadableByteChannel openChannel(InputStream inputStream) throws IOException {
return (inputStream == null) ?
Channels.newChannel(urlConnection.getInputStream()) :
Channels.newChannel(inputStream);
}
private boolean isJRT() {
String scheme = uri.getScheme().toLowerCase();
return "jrt".equals(scheme);
}
private boolean isResource() {
String scheme = uri.getScheme().toLowerCase();
return "resource".equals(scheme);
}
}
private static class MemoryConnectionHolder extends ConnectionHolder {
private final ByteBuffer backingBuffer;
public MemoryConnectionHolder(ByteBuffer buf) {
if (null == buf) {
throw new IllegalArgumentException("Can't connect to null buffer...");
}
if (buf.isDirect()) {
backingBuffer = buf.duplicate();
} else {
backingBuffer = ByteBuffer.allocateDirect(buf.capacity());
backingBuffer.put(buf);
}
backingBuffer.rewind();
channel = new ReadableByteChannel() {
public int read(ByteBuffer bb) throws IOException {
if (backingBuffer.remaining() <= 0) {
return -1;
}
int actual;
if (bb.equals(buffer)) {
actual = Math.min(DEFAULT_BUFFER_SIZE, backingBuffer.remaining());
if (actual > 0) {
buffer = backingBuffer.slice();
buffer.limit(actual);
}
} else {
actual = Math.min(bb.remaining(), backingBuffer.remaining());
if (actual > 0) {
backingBuffer.limit(backingBuffer.position() + actual);
bb.put(backingBuffer);
backingBuffer.limit(backingBuffer.capacity());
}
}
return actual;
}
public boolean isOpen() {
return true;
}
public void close() throws IOException {
}
};
}
@Override
int readBlock(long position, int size) throws IOException {
if (null == channel) {
throw new ClosedChannelException();
}
if ((int)position > backingBuffer.capacity()) {
return -1;
}
backingBuffer.position((int)position);
buffer = backingBuffer.slice();
int actual = Math.min(backingBuffer.remaining(), size);
buffer.limit(actual);
backingBuffer.position(backingBuffer.position() + actual);
return actual;
}
@Override
boolean needBuffer() {
return false;
}
@Override
boolean isSeekable() {
return true;
}
@Override
boolean isRandomAccess() {
return true;
}
@Override
public long seek(long position) {
if ((int)position < backingBuffer.capacity()) {
backingBuffer.limit(backingBuffer.capacity());
backingBuffer.position((int)position);
return position;
}
return -1;
}
@Override
public void closeConnection() {
channel = null;
}
}
}
