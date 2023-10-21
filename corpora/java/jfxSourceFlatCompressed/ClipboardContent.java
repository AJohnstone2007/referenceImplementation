package javafx.scene.input;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.scene.image.Image;
public class ClipboardContent extends HashMap<DataFormat, Object> {
public ClipboardContent() {
}
public final boolean hasString() {
return containsKey(DataFormat.PLAIN_TEXT);
}
public final boolean putString(String s) {
if (s == null) {
remove(DataFormat.PLAIN_TEXT);
} else {
put(DataFormat.PLAIN_TEXT, s);
}
return true;
}
public final String getString() {
return (String) get(DataFormat.PLAIN_TEXT);
}
public final boolean hasUrl() {
return containsKey(DataFormat.URL);
}
public final boolean putUrl(String url) {
if (url == null) {
remove(DataFormat.URL);
} else {
put(DataFormat.URL, url);
}
return true;
}
public final String getUrl() {
return (String) get(DataFormat.URL);
}
public final boolean hasHtml() {
return containsKey(DataFormat.HTML);
}
public final boolean putHtml(String html) {
if (html == null) {
remove(DataFormat.HTML);
} else {
put(DataFormat.HTML, html);
}
return true;
}
public final String getHtml() {
return (String) get(DataFormat.HTML);
}
public final boolean hasRtf() {
return containsKey(DataFormat.RTF);
}
public final boolean putRtf(String rtf) {
if (rtf == null) {
remove(DataFormat.RTF);
} else {
put(DataFormat.RTF, rtf);
}
return true;
}
public final String getRtf() {
return (String) get(DataFormat.RTF);
}
public final boolean hasImage() {
return containsKey(DataFormat.IMAGE);
};
public final boolean putImage(Image i) {
if (i == null) {
remove(DataFormat.IMAGE);
} else {
put(DataFormat.IMAGE, i);
}
return true;
}
public final Image getImage() {
return (Image) get(DataFormat.IMAGE);
}
public final boolean hasFiles() {
return containsKey(DataFormat.FILES);
}
public final boolean putFiles(List<File> files) {
if (files == null) {
remove(DataFormat.FILES);
} else {
put(DataFormat.FILES, files);
}
return true;
}
public final boolean putFilesByPath(List<String> filePaths) {
final List<File> files = new ArrayList<File>(filePaths.size());
for (String path : filePaths) {
files.add(new File(path));
}
return putFiles(files);
}
public final List<File> getFiles() {
return (List<File>) get(DataFormat.FILES);
}
}
