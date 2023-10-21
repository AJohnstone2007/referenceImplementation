package com.sun.glass.ui.ios;
final class IosDnDClipboard extends IosSystemClipboard {
public IosDnDClipboard(String name) {
super(name);
}
static IosDnDClipboard getInstance() {
return (IosDnDClipboard)get(DND);
}
@Override public String toString() {
return "iOS DnD Clipboard";
}
}
