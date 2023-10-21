package com.sun.glass.ui.ios;
import com.sun.glass.ui.Cursor;
import com.sun.glass.ui.Pixels;
final class IosCursor extends Cursor {
protected IosCursor(int type) {
super(type);
}
protected IosCursor(int x, int y, Pixels pixels) {
super(x, y, pixels);
}
@Override
native protected long _createCursor(int x, int y, Pixels pixels);
native private void _set(int type);
native private void _setCustom(long ptr);
void set() {
int type = getType();
setVisible(type != CURSOR_NONE);
switch (type) {
case CURSOR_NONE:
break;
case CURSOR_CUSTOM:
_setCustom(getNativeCursor());
break;
default:
_set(type);
break;
}
}
}
