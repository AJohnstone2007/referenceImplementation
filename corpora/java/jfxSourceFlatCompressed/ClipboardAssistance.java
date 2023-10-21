package com.sun.glass.ui;
import java.util.HashMap;
public class ClipboardAssistance {
private final HashMap <String, Object> cacheData = new HashMap <String, Object> ();
private final Clipboard clipboard;
private int supportedActions = Clipboard.ACTION_ANY;
public ClipboardAssistance (String cipboardName) {
Application.checkEventThread();
clipboard = Clipboard.get(cipboardName);
clipboard.add(this);
}
public void close () {
Application.checkEventThread();
clipboard.remove(this);
}
public void flush () {
Application.checkEventThread();
clipboard.flush(this, cacheData, supportedActions);
}
public void emptyCache () {
Application.checkEventThread();
cacheData.clear();
}
public boolean isCacheEmpty() {
Application.checkEventThread();
return cacheData.isEmpty();
}
public void setData (String mimeType, Object data) {
Application.checkEventThread();
cacheData.put(mimeType, data);
}
public Object getData (String mimeType) {
Application.checkEventThread();
return clipboard.getData(mimeType);
}
public void setSupportedActions(int supportedActions) {
Application.checkEventThread();
this.supportedActions = supportedActions;
}
public int getSupportedSourceActions() {
Application.checkEventThread();
return clipboard.getSupportedSourceActions();
}
public void setTargetAction (int actionDone) {
Application.checkEventThread();
clipboard.setTargetAction(actionDone);
}
public void contentChanged () {}
public void actionPerformed (int action) {}
public String[] getMimeTypes () {
Application.checkEventThread();
return clipboard.getMimeTypes();
}
@Override
public String toString () {
return "ClipboardAssistance[" + clipboard + "]" ;
}
}
