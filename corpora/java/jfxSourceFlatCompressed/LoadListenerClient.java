package com.sun.webkit;
import java.lang.annotation.Native;
public interface LoadListenerClient {
@Native public final static int PAGE_STARTED = 0;
@Native public final static int PAGE_FINISHED = 1;
@Native public final static int PAGE_REDIRECTED = 2;
@Native public final static int PAGE_REPLACED = 3;
@Native public final static int LOAD_FAILED = 5;
@Native public final static int LOAD_STOPPED = 6;
@Native public final static int CONTENT_RECEIVED = 10;
@Native public final static int TITLE_RECEIVED = 11;
@Native public final static int ICON_RECEIVED = 12;
@Native public final static int CONTENTTYPE_RECEIVED = 13;
@Native public final static int DOCUMENT_AVAILABLE = 14;
@Native public final static int RESOURCE_STARTED = 20;
@Native public final static int RESOURCE_REDIRECTED = 21;
@Native public final static int RESOURCE_FINISHED = 22;
@Native public final static int RESOURCE_FAILED = 23;
@Native public final static int PROGRESS_CHANGED = 30;
@Native public final static int UNKNOWN_HOST = 1;
@Native public final static int MALFORMED_URL = 2;
@Native public final static int SSL_HANDSHAKE = 3;
@Native public final static int CONNECTION_REFUSED = 4;
@Native public final static int CONNECTION_RESET = 5;
@Native public final static int NO_ROUTE_TO_HOST = 6;
@Native public final static int CONNECTION_TIMED_OUT = 7;
@Native public final static int PERMISSION_DENIED = 8;
@Native public final static int INVALID_RESPONSE = 9;
@Native public final static int TOO_MANY_REDIRECTS = 10;
@Native public final static int FILE_NOT_FOUND = 11;
@Native public final static int UNKNOWN_ERROR = 99;
public void dispatchLoadEvent(long frame, int state,
String url, String contentType,
double progress, int errorCode);
public void dispatchResourceLoadEvent(long frame, int state,
String url, String contentType,
double progress, int errorCode);
}
