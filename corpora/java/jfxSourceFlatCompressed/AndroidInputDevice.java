package com.sun.glass.ui.monocle;
import java.io.IOException;
class AndroidInputDevice implements Runnable, InputDevice {
private AndroidInputProcessor inputProcessor;
@Override
public void run() {
if (inputProcessor == null) {
System.err.println("Error: no input processor");
return;
}
}
@Override
public boolean isTouch() {
return true;
}
@Override
public boolean isMultiTouch() {
return true;
}
@Override
public boolean isRelative() {
return false;
}
@Override
public boolean is5Way() {
return false;
}
@Override
public boolean isFullKeyboard() {
return true;
}
void setInputProcessor(AndroidInputProcessor inputProcessor) {
this.inputProcessor = inputProcessor;
}
}
