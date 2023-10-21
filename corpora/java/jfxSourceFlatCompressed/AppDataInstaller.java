package com.oracle.dalvik;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import android.util.Log;
import android.content.res.AssetManager;
public class AppDataInstaller {
private static final String TAG = "AppDataInstaller";
String storageDir;
AssetManager assetManager;
public AppDataInstaller(String storageDir, AssetManager assetManager) {
this.storageDir = storageDir;
this.assetManager = assetManager;
}
public void handleAssetZipFile(String zipFileName, String dstDirName) {
try {
InputStream is = assetManager.open(zipFileName);
ZipInputStream zis = new ZipInputStream(is);
ZipEntry entry = null;
while ((entry = zis.getNextEntry()) != null) {
String innerFileName = dstDirName + File.separator
+ entry.getName();
File innerFile = new File(innerFileName);
if (innerFile.exists()) {
innerFile.delete();
}
if (entry.isDirectory()) {
innerFile.mkdirs();
} else {
FileOutputStream outputStream =
new FileOutputStream(innerFileName);
final int BUFFER_SIZE = 2048;
BufferedOutputStream bufferedOutputStream =
new BufferedOutputStream(outputStream, BUFFER_SIZE);
int count = 0;
byte[] data = new byte[BUFFER_SIZE];
while ((count = zis.read(data, 0, BUFFER_SIZE)) != -1) {
bufferedOutputStream.write(data, 0, count);
}
bufferedOutputStream.flush();
bufferedOutputStream.close();
}
zis.closeEntry();
}
zis.close();
} catch (IOException e) {
Log.v(TAG, "handleAssetZipFile: IOException: " + e);
e.printStackTrace();
}
}
public void copyAssetsTree(String src, String dst) {
Log.v(TAG, "AppDataInstaller.copyAssetsTree(): copyDir: src="
+ src + " dst=" + dst);
try {
String[] fileList = assetManager.list(src);
Log.v(TAG, "Sample.onCreate: # of assets in " + src
+ ": " + fileList.length);
if (fileList.length <= 0) {
Log.v(TAG, "copyAssetsTree: Copy file " + src
+ " to " + dst);
InputStream is = assetManager.open(src);
File dstFile = new File(dst);
OutputStream os = new FileOutputStream(dstFile);
final int BUFFER_SIZE = 2048;
BufferedOutputStream bufferedOutputStream =
new BufferedOutputStream(os, BUFFER_SIZE);
byte[] buf = new byte[BUFFER_SIZE];
int len;
while ((len = is.read(buf)) > 0) {
bufferedOutputStream.write(buf, 0, len);
}
is.close();
bufferedOutputStream.flush();
bufferedOutputStream.close();
} else {
Log.v(TAG,
"copyAssetsTree: Creating dir: " + dst);
File dstFile = new File(dst);
if (dstFile.exists()) {
if (dstFile.isDirectory()) {
Log.v(TAG,
"copyAssetsTree: " + dst + " already exists");
} else {
Log.v(TAG,
"copyAssetsTree: " + dst + " exists but is not a dierctory");
return;
}
} else {
if (!dstFile.mkdirs()) {
Log.v(TAG,
"copyAssetsTree: Failed creating dir: " + dst);
return;
} else {
}
}
final String ZIPPED_RENAMED_JPG_SUFFIX = ".ZIPPED.RENAMED.jpg";
final String RENAMED_JPG_SUFFIX = ".RENAMED.jpg";
for (String fileName: fileList) {
if (fileName.endsWith(ZIPPED_RENAMED_JPG_SUFFIX)) {
String newSrc = src + "/" + fileName;
handleAssetZipFile(newSrc, dst);
} else if (fileName.length() > RENAMED_JPG_SUFFIX.length()
&& fileName.endsWith(RENAMED_JPG_SUFFIX)) {
String newSrc = src + "/" + fileName;
String newDst = dst + "/"
+ fileName.substring(0, fileName.length()
- RENAMED_JPG_SUFFIX.length());
copyAssetsTree(newSrc, newDst);
} else {
String newSrc = src + "/" + fileName;
String newDst = dst + "/" + fileName;
copyAssetsTree(newSrc, newDst);
}
}
}
} catch (IOException e) {
Log.v(TAG,
"copyAssetsTree: IOException: " + e);
e.printStackTrace();
}
}
public void install() {
String doneFileName = storageDir + "/installation.done";
File doneFile = new File(doneFileName);
if (doneFile.exists()) {
Log.v(TAG,
"install: JVM data already installed. Skipping");
} else {
Log.v(TAG,
"install: Installing JVM data");
copyAssetsTree("storage", storageDir);
Log.v(TAG,
"install: Write file " + doneFileName);
try {
OutputStream os = new FileOutputStream(doneFile);
os.write(1);
os.close();
} catch (FileNotFoundException e) {
e.printStackTrace();
} catch (IOException e) {
e.printStackTrace();
}
}
}
}
