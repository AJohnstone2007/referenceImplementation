package com.sun.glass.ui.monocle;
class DispmanScreen extends FBDevScreen {
private native void wrapNativeSymbols();
DispmanScreen() {
super();
wrapNativeSymbols();
}
}
