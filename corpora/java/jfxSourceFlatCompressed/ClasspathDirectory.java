package ensemble.search;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Lock;
public class ClasspathDirectory extends Directory {
private String[] allFiles;
private final Map<String,Long> fileLengthMap = new HashMap<>();
public ClasspathDirectory() {
try {
BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("index/listAll.txt")));
String line;
List<String> fileNames = new ArrayList<>();
while ((line = reader.readLine()) != null) {
String[] parts = line.split(":");
fileNames.add(parts[0]);
fileLengthMap.put(parts[0], Long.parseLong(parts[1]));
}
reader.close();
allFiles = fileNames.toArray(new String[fileNames.size()]);
} catch (IOException e) {
e.printStackTrace();
}
}
@Override public String[] listAll() throws IOException {
return allFiles;
}
@Override public IndexInput openInput(String s, IOContext ioc) throws IOException {
return new ClassPathIndexInput(
s,
getClass().getResourceAsStream("index/"+s),
fileLengthMap.get(s).intValue()
);
}
private static class ClassPathIndexInput extends IndexInput {
private byte[] data;
private int pointer = 0;
private int length;
private ClassPathIndexInput(String resourceDescription, InputStream in, int length) throws IOException {
super(resourceDescription);
this.length = length;
data = new byte[length];
final byte[] buf = new byte[1024*20];
int offset = 0, remaining = length, read;
do {
read = in.read(buf,0,Math.min(remaining, buf.length));
if (read > 0) {
System.arraycopy(buf, 0, data, offset, read);
offset += read;
remaining -= read;
}
} while (read != -1 && remaining > 0);
in.close();
}
private ClassPathIndexInput(String resourceDescription, byte[] data) {
super(resourceDescription);
this.data = data;
this.pointer = 0;
this.length = data.length;
}
@Override public byte readByte() throws IOException {
return data[pointer ++];
}
@Override public void readBytes(byte[] bytes, int offset, int len) throws IOException {
System.arraycopy(data, pointer, bytes, offset, len);
pointer += len;
}
@Override public void close() throws IOException {}
@Override public long getFilePointer() { return pointer; }
@Override public void seek(long l) throws IOException { pointer = (int)l; }
@Override public long length() { return length; }
@Override
public IndexInput slice(String sliceDescription, long offset, long length) throws IOException {
int o = (int) offset;
int l = (int) length;
byte[] sliceData = new byte[l];
System.arraycopy(data, o, sliceData, 0, l);
return new ClassPathIndexInput(sliceDescription, sliceData);
}
}
@Override public void close() throws IOException {}
@Override public void deleteFile(String s) throws IOException { throw new UnsupportedOperationException("Not implemented"); }
@Override public long fileLength(String s) throws IOException { throw new UnsupportedOperationException("Not implemented"); }
@Override
public IndexOutput createOutput(String string, IOContext ioc) throws IOException {
throw new UnsupportedOperationException("Not implemented");
}
@Override
public IndexOutput createTempOutput(String string, String string1, IOContext ioc) throws IOException {
throw new UnsupportedOperationException("Not implemented");
}
@Override
public void sync(Collection<String> clctn) throws IOException {
throw new UnsupportedOperationException("Not implemented");
}
@Override
public void rename(String string, String string1) throws IOException {
throw new UnsupportedOperationException("Not implemented");
}
@Override
public void syncMetaData() throws IOException {
throw new UnsupportedOperationException("Not implemented");
}
@Override
public Lock obtainLock(String string) throws IOException {
throw new UnsupportedOperationException("Not implemented");
}
}
