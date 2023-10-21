package javafx.scene.input;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import com.sun.javafx.util.WeakReferenceQueue;
import javafx.beans.NamedArg;
public class DataFormat {
private static final WeakReferenceQueue<DataFormat> DATA_FORMAT_LIST = new WeakReferenceQueue<DataFormat>();
public static final DataFormat PLAIN_TEXT = new DataFormat("text/plain");
public static final DataFormat HTML = new DataFormat("text/html");
public static final DataFormat RTF = new DataFormat("text/rtf");
public static final DataFormat URL = new DataFormat("text/uri-list");
public static final DataFormat IMAGE = new DataFormat("application/x-java-rawimage");
public static final DataFormat FILES = new DataFormat("application/x-java-file-list", "java.file-list");
private static final DataFormat DRAG_IMAGE = new DataFormat("application/x-java-drag-image");
private static final DataFormat DRAG_IMAGE_OFFSET = new DataFormat("application/x-java-drag-image-offset");
private final Set<String> identifier;
public DataFormat(@NamedArg("ids") String... ids) {
DATA_FORMAT_LIST.cleanup();
if (ids != null) {
for (String id : ids) {
if (lookupMimeType(id) != null) {
throw new IllegalArgumentException("DataFormat '" + id +
"' already exists.");
}
}
this.identifier = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(ids)));
} else {
this.identifier = Collections.<String>emptySet();
}
DATA_FORMAT_LIST.add(this);
}
public final Set<String> getIdentifiers() {
return identifier;
}
@Override public String toString() {
if (identifier.isEmpty()) {
return "[]";
} else if (identifier.size() == 1) {
StringBuilder sb = new StringBuilder("[");
sb.append(identifier.iterator().next());
return (sb.append("]").toString());
} else {
StringBuilder b = new StringBuilder("[");
Iterator<String> itr = identifier.iterator();
while (itr.hasNext()) {
b = b.append(itr.next());
if (itr.hasNext()) {
b = b.append(", ");
}
}
b = b.append("]");
return b.toString();
}
}
@Override public int hashCode() {
int hash = 7;
for (String id : identifier) {
hash = 31 * hash + id.hashCode();
}
return hash;
}
@Override public boolean equals(Object obj) {
if (obj == null || ! (obj instanceof DataFormat)) {
return false;
}
DataFormat otherDataFormat = (DataFormat) obj;
if (identifier.equals(otherDataFormat.identifier)) {
return true;
}
return false;
}
public static DataFormat lookupMimeType(String mimeType) {
if (mimeType == null || mimeType.length() == 0) {
return null;
}
Iterator itr = DATA_FORMAT_LIST.iterator();
while (itr.hasNext()) {
DataFormat dataFormat = (DataFormat) itr.next();
if (dataFormat.getIdentifiers().contains(mimeType)) {
return dataFormat;
}
}
return null;
}
}
