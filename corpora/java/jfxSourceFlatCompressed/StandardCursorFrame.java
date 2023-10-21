package com.sun.javafx.cursor;
public final class StandardCursorFrame extends CursorFrame {
private CursorType cursorType;
public StandardCursorFrame(final CursorType cursorType) {
this.cursorType = cursorType;
}
@Override
public CursorType getCursorType() {
return cursorType;
}
}
