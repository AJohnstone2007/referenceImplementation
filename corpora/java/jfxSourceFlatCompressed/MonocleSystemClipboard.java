package com.sun.glass.ui.monocle;
import com.sun.glass.ui.Clipboard;
import com.sun.glass.ui.SystemClipboard;
import java.util.HashMap;
final class MonocleSystemClipboard extends SystemClipboard {
MonocleSystemClipboard() {
super(Clipboard.SYSTEM);
}
protected boolean isOwner() {
return true;
}
protected void pushToSystem(HashMap<String, Object> cacheData,
int supportedActions) {
}
protected void pushTargetActionToSystem(int actionDone) {
}
protected Object popFromSystem(String mimeType) {
return null;
}
protected int supportedSourceActionsFromSystem() {
return Clipboard.ACTION_NONE;
}
protected String[] mimesFromSystem() {
return new String[0];
}
}