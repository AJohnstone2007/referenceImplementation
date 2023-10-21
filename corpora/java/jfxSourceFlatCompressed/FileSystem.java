package com.sun.webkit;
import com.sun.javafx.logging.PlatformLogger;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import static java.lang.String.format;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
final class FileSystem {
private static final int TYPE_UNKNOWN = 0;
private static final int TYPE_FILE = 1;
private static final int TYPE_DIRECTORY = 2;
private final static PlatformLogger logger =
PlatformLogger.getLogger(FileSystem.class.getName());
private FileSystem() {
throw new AssertionError();
}
private static boolean fwkFileExists(String path) {
return new File(path).exists();
}
private static RandomAccessFile fwkOpenFile(String path, String mode) {
try {
return new RandomAccessFile(path, mode);
} catch (FileNotFoundException | SecurityException ex) {
logger.fine(format("Error while creating RandomAccessFile for file [%s]", path), ex);
}
return null;
}
private static void fwkCloseFile(RandomAccessFile raf) {
try {
raf.close();
} catch (IOException ex) {
logger.fine(format("Error while closing RandomAccessFile for file [%s]", raf), ex);
}
}
private static int fwkReadFromFile(RandomAccessFile raf, ByteBuffer byteBuffer) {
try {
FileChannel fc = raf.getChannel();
return fc.read(byteBuffer);
} catch (IOException ex) {
logger.fine(format("Error while reading RandomAccessFile for file [%s]", raf), ex);
}
return -1;
}
private static void fwkSeekFile(RandomAccessFile raf, long pos) {
try {
raf.seek(pos);
} catch (IOException ex) {
logger.fine(format("Error while seek RandomAccessFile for file [%s]", raf), ex);
}
}
private static long fwkGetFileSize(String path) {
try {
File file = new File(path);
if (file.exists()) {
return file.length();
}
} catch (SecurityException ex) {
logger.fine(format("Error determining size of file [%s]", path), ex);
}
return -1;
}
private static boolean fwkGetFileMetadata(String path, long[] metadataArray) {
try {
File file = new File(path);
if (file.exists()) {
metadataArray[0] = file.lastModified();
metadataArray[1] = file.length();
if (file.isDirectory()) {
metadataArray[2] = TYPE_DIRECTORY;
} else if (file.isFile()) {
metadataArray[2] = TYPE_FILE;
} else {
metadataArray[2] = TYPE_UNKNOWN;
}
return true;
}
} catch (SecurityException ex) {
logger.fine(format("Error determining Metadata for file [%s]", path), ex);
}
return false;
}
private static String fwkPathByAppendingComponent(String path,
String component)
{
return new File(path, component).getPath();
}
private static boolean fwkMakeAllDirectories(String path) {
try {
Files.createDirectories(Paths.get(path));
return true;
} catch (InvalidPathException|IOException ex) {
logger.fine(format("Error creating directory [%s]", path), ex);
return false;
}
}
private static String fwkPathGetFileName(String path) {
return new File(path).getName();
}
}
