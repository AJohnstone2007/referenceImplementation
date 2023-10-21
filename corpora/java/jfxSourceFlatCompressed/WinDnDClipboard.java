package com.sun.glass.ui.win;
final class WinDnDClipboard extends WinSystemClipboard {
public WinDnDClipboard(String name) {
super(name);
}
@Override protected void create() {}
@Override protected native void dispose();
@Override protected boolean isOwner() {
return getDragButton() != 0;
}
@Override protected void pushTargetActionToSystem(int actionDone) {
throw new UnsupportedOperationException(
"[Target Action] not supported! Override View.handleDragDrop instead.");
}
@Override protected native void push(Object[] keys, int supportedActions);
@Override protected boolean pop() {
return getPtr() != 0L;
}
private static WinDnDClipboard getInstance() {
return (WinDnDClipboard)get(DND);
}
@Override public String toString() {
return "Windows DnD Clipboard";
}
private static int dragButton = 0;
public int getDragButton() {
return dragButton;
}
private void setDragButton(int dragButton) {
this.dragButton = dragButton;
}
private int sourceSupportedActions = 0;
@Override protected final int supportedSourceActionsFromSystem() {
return sourceSupportedActions != 0
? sourceSupportedActions
: super.supportedSourceActionsFromSystem();
}
private void setSourceSupportedActions(int sourceSupportedActions) {
this.sourceSupportedActions = sourceSupportedActions;
}
}
