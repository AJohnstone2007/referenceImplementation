package com.sun.media.jfxmediaimpl;
import com.sun.media.jfxmedia.MediaError;
import com.sun.media.jfxmedia.MediaException;
import com.sun.media.jfxmedia.events.MediaErrorListener;
import com.sun.media.jfxmedia.locator.Locator;
import com.sun.media.jfxmedia.logging.Logger;
import java.util.List;
import java.lang.ref.WeakReference;
import java.util.ListIterator;
public class MediaUtils {
public static final int MAX_FILE_SIGNATURE_LENGTH = 22;
static final String NATIVE_MEDIA_ERROR_FORMAT = "Internal media error: %d";
static final String NATIVE_MEDIA_WARNING_FORMAT = "Internal media warning: %d";
public static final String CONTENT_TYPE_AIFF = "audio/x-aiff";
public static final String CONTENT_TYPE_MP3 = "audio/mp3";
public static final String CONTENT_TYPE_MPA = "audio/mpeg";
public static final String CONTENT_TYPE_WAV = "audio/x-wav";
public static final String CONTENT_TYPE_JFX = "video/x-javafx";
public static final String CONTENT_TYPE_FLV = "video/x-flv";
public static final String CONTENT_TYPE_MP4 = "video/mp4";
public static final String CONTENT_TYPE_M4A = "audio/x-m4a";
public static final String CONTENT_TYPE_M4V = "video/x-m4v";
public static final String CONTENT_TYPE_M3U8 = "application/vnd.apple.mpegurl";
public static final String CONTENT_TYPE_M3U = "audio/mpegurl";
private static final String FILE_TYPE_AIF = "aif";
private static final String FILE_TYPE_AIFF = "aiff";
private static final String FILE_TYPE_FLV = "flv";
private static final String FILE_TYPE_FXM = "fxm";
private static final String FILE_TYPE_MPA = "mp3";
private static final String FILE_TYPE_WAV = "wav";
private static final String FILE_TYPE_MP4 = "mp4";
private static final String FILE_TYPE_M4A = "m4a";
private static final String FILE_TYPE_M4V = "m4v";
private static final String FILE_TYPE_M3U8 = "m3u8";
private static final String FILE_TYPE_M3U = "m3u";
public static String fileSignatureToContentType(byte[] buf, int size) throws MediaException {
String contentType = Locator.DEFAULT_CONTENT_TYPE;
if (size < MAX_FILE_SIGNATURE_LENGTH) {
throw new MediaException("Empty signature!");
} else if (buf.length < MAX_FILE_SIGNATURE_LENGTH) {
return contentType;
} else if ((buf[0] & 0xff) == 0x46
&& (buf[1] & 0xff) == 0x4c
&& (buf[2] & 0xff) == 0x56) {
contentType = CONTENT_TYPE_JFX;
} else if ((((buf[0] & 0xff) << 24)
| ((buf[1] & 0xff) << 16)
| ((buf[2] & 0xff) << 8)
| (buf[3] & 0xff)) == 0x52494646 &&
(((buf[8] & 0xff) << 24)
| ((buf[9] & 0xff) << 16)
| ((buf[10] & 0xff) << 8)
| (buf[11] & 0xff)) == 0x57415645 &&
(((buf[12] & 0xff) << 24)
| ((buf[13] & 0xff) << 16)
| ((buf[14] & 0xff) << 8)
| (buf[15] & 0xff)) == 0x666d7420) {
if (((buf[20] & 0xff) == 0x01 && (buf[21] & 0xff) == 0x00) || ((buf[20] & 0xff) == 0x03 && (buf[21] & 0xff) == 0x00)) {
contentType = CONTENT_TYPE_WAV;
} else {
throw new MediaException("Compressed WAVE is not supported!");
}
} else if ((((buf[0] & 0xff) << 24)
| ((buf[1] & 0xff) << 16)
| ((buf[2] & 0xff) << 8)
| (buf[3] & 0xff)) == 0x52494646 &&
(((buf[8] & 0xff) << 24)
| ((buf[9] & 0xff) << 16)
| ((buf[10] & 0xff) << 8)
| (buf[11] & 0xff)) == 0x57415645)
{
contentType = CONTENT_TYPE_WAV;
} else if ((((buf[0] & 0xff) << 24)
| ((buf[1] & 0xff) << 16)
| ((buf[2] & 0xff) << 8)
| (buf[3] & 0xff)) == 0x464f524d &&
(((buf[8] & 0xff) << 24)
| ((buf[9] & 0xff) << 16)
| ((buf[10] & 0xff) << 8)
| (buf[11] & 0xff)) == 0x41494646 &&
(((buf[12] & 0xff) << 24)
| ((buf[13] & 0xff) << 16)
| ((buf[14] & 0xff) << 8)
| (buf[15] & 0xff)) == 0x434f4d4d) {
contentType = CONTENT_TYPE_AIFF;
} else if ((buf[0] & 0xff) == 0x49
&& (buf[1] & 0xff) == 0x44
&& (buf[2] & 0xff) == 0x33) {
contentType = CONTENT_TYPE_MPA;
} else if ((buf[0] & 0xff) == 0xff && (buf[1] & 0xe0) == 0xe0 &&
(buf[2] & 0x18) != 0x08 &&
(buf[3] & 0x06) != 0x00) {
contentType = CONTENT_TYPE_MPA;
} else if ((((buf[4] & 0xff) << 24)
| ((buf[5] & 0xff) << 16)
| ((buf[6] & 0xff) << 8)
| (buf[7] & 0xff)) == 0x66747970) {
if ((buf[8] & 0xff) == 0x4D && (buf[9] & 0xff) == 0x34 && (buf[10] & 0xff) == 0x41 && (buf[11] & 0xff) == 0x20)
contentType = CONTENT_TYPE_M4A;
else if ((buf[8] & 0xff) == 0x4D && (buf[9] & 0xff) == 0x34 && (buf[10] & 0xff) == 0x56 && (buf[11] & 0xff) == 0x20)
contentType = CONTENT_TYPE_M4V;
else if ((buf[8] & 0xff) == 0x6D && (buf[9] & 0xff) == 0x70 && (buf[10] & 0xff) == 0x34 && (buf[11] & 0xff) == 0x32)
contentType = CONTENT_TYPE_MP4;
else if ((buf[8] & 0xff) == 0x69 && (buf[9] & 0xff) == 0x73 && (buf[10] & 0xff) == 0x6F && (buf[11] & 0xff) == 0x6D)
contentType = CONTENT_TYPE_MP4;
else if ((buf[8] & 0xff) == 0x4D && (buf[9] & 0xff) == 0x50 && (buf[10] & 0xff) == 0x34 && (buf[11] & 0xff) == 0x20)
contentType = CONTENT_TYPE_MP4;
} else if ((buf[0] & 0xff) == 0x23
&& (buf[1] & 0xff) == 0x45
&& (buf[2] & 0xff) == 0x58
&& (buf[3] & 0xff) == 0x54
&& (buf[4] & 0xff) == 0x4d
&& (buf[5] & 0xff) == 0x33
&& (buf[6] & 0xff) == 0x55) {
contentType = CONTENT_TYPE_M3U8;
} else {
throw new MediaException("Unrecognized file signature!");
}
return contentType;
}
public static String filenameToContentType(String filename) {
if (filename == null) {
return Locator.DEFAULT_CONTENT_TYPE;
}
int dotIndex = filename.lastIndexOf(".");
if (dotIndex != -1) {
String extension = filename.toLowerCase().substring(dotIndex + 1);
switch (extension) {
case FILE_TYPE_AIF:
case FILE_TYPE_AIFF:
return CONTENT_TYPE_AIFF;
case FILE_TYPE_FLV:
case FILE_TYPE_FXM:
return CONTENT_TYPE_JFX;
case FILE_TYPE_MPA:
return CONTENT_TYPE_MPA;
case FILE_TYPE_WAV:
return CONTENT_TYPE_WAV;
case FILE_TYPE_MP4:
return CONTENT_TYPE_MP4;
case FILE_TYPE_M4A:
return CONTENT_TYPE_M4A;
case FILE_TYPE_M4V:
return CONTENT_TYPE_M4V;
case FILE_TYPE_M3U8:
return CONTENT_TYPE_M3U8;
case FILE_TYPE_M3U:
return CONTENT_TYPE_M3U;
default:
break;
}
}
return Locator.DEFAULT_CONTENT_TYPE;
}
public static void warning(Object source, String message) {
if (source != null & message != null) {
Logger.logMsg(Logger.WARNING,
source.getClass().getName() + ": " + message);
}
}
public static void error(Object source, int errCode, String message, Throwable cause) {
if (cause != null) {
StackTraceElement[] stackTrace = cause.getStackTrace();
if (stackTrace != null && stackTrace.length > 0) {
StackTraceElement trace = stackTrace[0];
Logger.logMsg(Logger.ERROR,
trace.getClassName(), trace.getMethodName(),
"( " + trace.getLineNumber() + ") " + message);
}
}
List<WeakReference<MediaErrorListener>> listeners =
NativeMediaManager.getDefaultInstance().getMediaErrorListeners();
if (!listeners.isEmpty()) {
for (ListIterator<WeakReference<MediaErrorListener>> it = listeners.listIterator(); it.hasNext();) {
MediaErrorListener l = it.next().get();
if (l != null) {
l.onError(source, errCode, message);
} else {
it.remove();
}
}
} else {
MediaException e = cause instanceof MediaException
? (MediaException) cause : new MediaException(message, cause);
throw e;
}
}
public static void nativeWarning(Object source, int warningCode, String warningMessage) {
String message = String.format(NATIVE_MEDIA_WARNING_FORMAT, warningCode);
if (warningMessage != null) {
message += ": " + warningMessage;
}
Logger.logMsg(Logger.WARNING, message);
}
public static void nativeError(Object source, MediaError error) {
Logger.logMsg(Logger.ERROR, error.description());
List<WeakReference<MediaErrorListener>> listeners =
NativeMediaManager.getDefaultInstance().getMediaErrorListeners();
if (!listeners.isEmpty()) {
for (ListIterator<WeakReference<MediaErrorListener>> it = listeners.listIterator(); it.hasNext();) {
MediaErrorListener l = it.next().get();
if (l != null) {
l.onError(source, error.code(), error.description());
} else {
it.remove();
}
}
} else {
throw new MediaException(error.description(), null, error);
}
}
}
