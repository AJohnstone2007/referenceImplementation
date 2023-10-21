package com.sun.glass.ui.mac;
import com.sun.glass.ui.Application;
import java.io.File;
public final class MacFileNSURL extends File {
private native static void _initIDs();
static {
_initIDs();
}
private long ptr;
private MacFileNSURL(String name, long ptr) {
super(name);
this.ptr = ptr;
Application.checkEventThread();
}
private void checkNotDisposed() {
if (ptr == 0L) {
throw new RuntimeException("The NSURL object has been diposed already");
}
}
native private void _dispose(long ptr);
public void dispose() {
Application.checkEventThread();
checkNotDisposed();
_dispose(ptr);
ptr = 0L;
}
native private boolean _startAccessingSecurityScopedResource(long ptr);
public boolean startAccessingSecurityScopedResource() {
Application.checkEventThread();
checkNotDisposed();
return _startAccessingSecurityScopedResource(ptr);
}
native private void _stopAccessingSecurityScopedResource(long ptr);
public void stopAccessingSecurityScopedResource() {
Application.checkEventThread();
checkNotDisposed();
_stopAccessingSecurityScopedResource(ptr);
}
native private byte[] _getBookmark(long ptr, long baseDocumentPtr);
public byte[] getBookmark() {
Application.checkEventThread();
checkNotDisposed();
return _getBookmark(ptr, 0L);
}
native private static MacFileNSURL _createFromBookmark(byte[] data, long baseDocumentPtr);
public static MacFileNSURL createFromBookmark(byte[] data) {
Application.checkEventThread();
if (data == null) {
throw new NullPointerException("data must not be null");
}
if (!MacCommonDialogs.isFileNSURLEnabled()) {
throw new RuntimeException("The system property glass.macosx.enableFileNSURL is not 'true'");
}
return _createFromBookmark(data, 0L);
}
public byte[] getDocumentScopedBookmark(MacFileNSURL baseDocument) {
Application.checkEventThread();
checkNotDisposed();
return _getBookmark(ptr, baseDocument.ptr);
}
public static MacFileNSURL createFromDocumentScopedBookmark(byte[] data, MacFileNSURL baseDocument) {
Application.checkEventThread();
if (data == null) {
throw new NullPointerException("data must not be null");
}
if (!MacCommonDialogs.isFileNSURLEnabled()) {
throw new RuntimeException("The system property glass.macosx.enableFileNSURL is not 'true'");
}
return _createFromBookmark(data, baseDocument.ptr);
}
}
