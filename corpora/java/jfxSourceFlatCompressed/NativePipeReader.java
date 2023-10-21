package com.oracle.dalvik;
import android.os.Handler;
public class NativePipeReader extends Thread {
public interface OnTextReceivedListener {
public void onTextReceived(String text);
}
public interface Client extends OnTextReceivedListener {
public int initPipe();
public void onTextReceived(String text);
public void cleanupPipe();
}
private Client client;
private Handler handler;
public NativePipeReader(Client client) {
super("NativePipeReader");
setDaemon(true);
this.client = client;
this.handler = new Handler();
}
private volatile boolean stop = false;
public void stopReading() {
stop = true;
}
public void run() {
int fd = client.initPipe();
while (!stop) {
String text = readPipe(fd);
if (text.length() > 0) {
client.onTextReceived(text);
}
}
client.cleanupPipe();
}
public static NativePipeReader
getDefaultReader(OnTextReceivedListener listener) {
return new NativePipeReader(
new StdoutStderrClient(listener));
}
private native String readPipe(int fd);
private static class StdoutStderrClient implements Client {
Handler handler;
OnTextReceivedListener listener;
public StdoutStderrClient(OnTextReceivedListener listener) {
this.handler = new Handler();
this.listener = listener;
}
private native int nativeInitPipe();
private native void nativeCleanupPipe();
public int initPipe() {
return nativeInitPipe();
}
public void onTextReceived(final String text) {
handler.post(new Runnable() {
public void run() {
listener.onTextReceived(text);
}
});
}
public void cleanupPipe() {
nativeCleanupPipe();
}
}
}
