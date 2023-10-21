package javafx.scene.media;
import com.sun.media.jfxmedia.MediaError;
public final class MediaException extends RuntimeException {
public enum Type {
MEDIA_CORRUPTED,
MEDIA_INACCESSIBLE,
MEDIA_UNAVAILABLE,
MEDIA_UNSPECIFIED,
MEDIA_UNSUPPORTED,
OPERATION_UNSUPPORTED,
PLAYBACK_ERROR,
PLAYBACK_HALTED,
UNKNOWN
};
static Type errorCodeToType(int errorCode) {
Type errorType;
if(errorCode == MediaError.ERROR_LOCATOR_CONNECTION_LOST.code()) {
errorType = Type.MEDIA_INACCESSIBLE;
} else if(errorCode == MediaError.ERROR_GSTREAMER_SOURCEFILE_NONEXISTENT.code() ||
errorCode == MediaError.ERROR_GSTREAMER_SOURCEFILE_NONREGULAR.code()) {
errorType = Type.MEDIA_UNAVAILABLE;
} else if(errorCode == MediaError.ERROR_MEDIA_AUDIO_FORMAT_UNSUPPORTED.code() ||
errorCode == MediaError.ERROR_MEDIA_UNKNOWN_PIXEL_FORMAT.code() ||
errorCode == MediaError.ERROR_MEDIA_VIDEO_FORMAT_UNSUPPORTED.code() ||
errorCode == MediaError.ERROR_LOCATOR_CONTENT_TYPE_NULL.code() ||
errorCode == MediaError.ERROR_LOCATOR_UNSUPPORTED_MEDIA_FORMAT.code() ||
errorCode == MediaError.ERROR_LOCATOR_UNSUPPORTED_TYPE.code() ||
errorCode == MediaError.ERROR_GSTREAMER_UNSUPPORTED_PROTOCOL.code() ||
errorCode == MediaError.ERROR_MEDIA_MP3_FORMAT_UNSUPPORTED.code() ||
errorCode == MediaError.ERROR_MEDIA_AAC_FORMAT_UNSUPPORTED.code() ||
errorCode == MediaError.ERROR_MEDIA_H264_FORMAT_UNSUPPORTED.code() ||
errorCode == MediaError.ERROR_MEDIA_H265_FORMAT_UNSUPPORTED.code() ||
errorCode == MediaError.ERROR_MEDIA_HLS_FORMAT_UNSUPPORTED.code()) {
errorType = Type.MEDIA_UNSUPPORTED;
} else if(errorCode == MediaError.ERROR_MEDIA_CORRUPTED.code()) {
errorType = Type.MEDIA_CORRUPTED;
} else if((errorCode & MediaError.ERROR_BASE_GSTREAMER.code()) == MediaError.ERROR_BASE_GSTREAMER.code() ||
(errorCode & MediaError.ERROR_BASE_JNI.code()) == MediaError.ERROR_BASE_JNI.code()) {
errorType = Type.PLAYBACK_ERROR;
} else {
errorType = Type.UNKNOWN;
}
return errorType;
}
static MediaException exceptionToMediaException(Exception e) {
Type errType = Type.UNKNOWN;
if (e.getCause() instanceof java.net.UnknownHostException) {
errType = Type.MEDIA_UNAVAILABLE;
} else if (e.getCause() instanceof java.lang.IllegalArgumentException) {
errType = Type.MEDIA_UNSUPPORTED;
} else if (e instanceof com.sun.media.jfxmedia.MediaException) {
com.sun.media.jfxmedia.MediaException me = (com.sun.media.jfxmedia.MediaException)e;
MediaError error = me.getMediaError();
if (error != null ) {
errType = errorCodeToType(error.code());
}
}
return new MediaException(errType, e);
}
static MediaException haltException(String message) {
return new MediaException(Type.PLAYBACK_HALTED, message);
}
static MediaException getMediaException(Object source, int errorCode, String message) {
String errorDescription = MediaError.getFromCode(errorCode).description();
String exceptionMessage = "[" + source + "] " + message + ": " + errorDescription;
Type errorType = errorCodeToType(errorCode);
return new MediaException(errorType, exceptionMessage);
}
MediaException(Type type, Throwable t) {
super(t);
this.type = type;
}
MediaException(Type type, String message, Throwable t) {
super(message, t);
this.type = type;
}
MediaException(Type type, String message) {
super(message);
this.type = type;
}
private final Type type;
public Type getType() {
return type;
}
@Override
public String toString() {
String errString = "MediaException: " + type;
if (getMessage() != null) errString += " : " + getMessage();
if (getCause() != null) errString += " : " + getCause();
return errString;
}
};
