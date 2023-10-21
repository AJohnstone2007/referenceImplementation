package javafx.embed.swt;
import org.eclipse.swt.SWT;
import java.lang.reflect.Method;
import com.sun.javafx.embed.AbstractEvents;
import org.eclipse.swt.widgets.Event;
import java.lang.reflect.InvocationTargetException;
class SWTEvents {
static int mouseButtonToEmbedMouseButton(int button, int extModifiers) {
switch (button) {
case 1: return AbstractEvents.MOUSEEVENT_PRIMARY_BUTTON;
case 2: return AbstractEvents.MOUSEEVENT_MIDDLE_BUTTON;
case 3: return AbstractEvents.MOUSEEVENT_SECONDARY_BUTTON;
case 4: return AbstractEvents.MOUSEEVENT_BACK_BUTTON;
case 5: return AbstractEvents.MOUSEEVENT_FORWARD_BUTTON;
}
return AbstractEvents.MOUSEEVENT_NONE_BUTTON;
}
static double getWheelRotation(Event e) {
int divisor = 1;
if ("win32".equals(SWT.getPlatform()) && e.type == SWT.MouseVerticalWheel) {
int [] linesToScroll = new int [1];
try {
Class clazz = Class.forName("org.eclipse.swt.internal.win32.OS");
Method method = clazz.getDeclaredMethod("SystemParametersInfo", new Class []{int.class, int.class, int [].class, int.class});
method.invoke(clazz, 104 , 0, linesToScroll, 0);
} catch (IllegalAccessException iae) {
} catch (InvocationTargetException ite) {
} catch (NoSuchMethodException nme) {
} catch (ClassNotFoundException cfe) {
}
if (linesToScroll [0] != -1 ) {
divisor = linesToScroll [0];
}
} else if ("gtk".equals(SWT.getPlatform())) {
divisor = 3;
}
else if ("cocoa".equals(SWT.getPlatform())) {
divisor = Math.abs(e.count);
}
return e.count / (double) Math.max(1, divisor);
}
static int keyIDToEmbedKeyType(int id) {
switch (id) {
case SWT.KeyDown:
return AbstractEvents.KEYEVENT_PRESSED;
case SWT.KeyUp:
return AbstractEvents.KEYEVENT_RELEASED;
}
return 0;
}
static final int [] [] KeyTable = {
{0x0 , SWT.NULL},
{'\n' , SWT.CR},
{'\n' , SWT.LF},
{'\b' , SWT.BS},
{'\t' , SWT.TAB},
{0x1B , SWT.ESC},
{0x20 , 0x20},
{0x7F , SWT.DEL},
{0x9B , SWT.INSERT},
{0x9C , SWT.HELP},
{0x10 , SWT.SHIFT},
{0x11 , SWT.CONTROL},
{0x12 , SWT.ALT},
{0x020C , SWT.COMMAND},
{0x14 , SWT.CAPS_LOCK},
{0x90 , SWT.NUM_LOCK},
{0x91 , SWT.SCROLL_LOCK},
{0x21 , SWT.PAGE_UP},
{0x22 , SWT.PAGE_DOWN},
{0x23 , SWT.END},
{0x24 , SWT.HOME},
{0x25 , SWT.ARROW_LEFT},
{0x26 , SWT.ARROW_UP},
{0x27 , SWT.ARROW_RIGHT},
{0x28 , SWT.ARROW_DOWN},
{0x2C , ','},
{0x2D , '-'},
{0x2E , '.'},
{0x2F , '/'},
{0x3B , ';'},
{0x3D , '='},
{0x5B , '['},
{0x5C , '\\'},
{0x5D , ']'},
{0x6A , SWT.KEYPAD_MULTIPLY},
{0x6B , SWT.KEYPAD_ADD},
{0x6D , SWT.KEYPAD_SUBTRACT},
{0x6E , SWT.KEYPAD_DECIMAL},
{0x6F , SWT.KEYPAD_DIVIDE},
{0x96 , '@'},
{0x97 , '*'},
{0x98 , '"'},
{0x99 , '<'},
{0xa0 , '>'},
{0xa1 , '{'},
{0xa2 , '}'},
{0xC0 , '`'},
{0xDE , '\''},
{0x0200 , '@'},
{0x0201 , ':'},
{0x0202 , '^'},
{0x0203 , '$'},
{0x0205 , '!'},
{0x0207 , '('},
{0x0208 , '#'},
{0x0209 , '+'},
{0x020A , ')'},
{0x020B , '_'},
{0x30 , '0'},
{0x31 , '1'},
{0x32 , '2'},
{0x33 , '3'},
{0x34 , '4'},
{0x35 , '5'},
{0x36 , '6'},
{0x37 , '7'},
{0x38 , '8'},
{0x39 , '9'},
{0x41 , 'a'},
{0x42 , 'b'},
{0x43 , 'c'},
{0x44 , 'd'},
{0x45 , 'e'},
{0x46 , 'f'},
{0x47 , 'g'},
{0x48 , 'h'},
{0x49 , 'i'},
{0x4A , 'j'},
{0x4B , 'k'},
{0x4C , 'l'},
{0x4D , 'm'},
{0x4E , 'n'},
{0x4F , 'o'},
{0x50 , 'p'},
{0x51 , 'q'},
{0x52 , 'r'},
{0x53 , 's'},
{0x54 , 't'},
{0x55 , 'u'},
{0x56 , 'v'},
{0x57 , 'w'},
{0x58 , 'x'},
{0x59 , 'y'},
{0x5A , 'z'},
{0x60 , SWT.KEYPAD_0},
{0x61 , SWT.KEYPAD_1},
{0x62 , SWT.KEYPAD_2},
{0x63 , SWT.KEYPAD_3},
{0x64 , SWT.KEYPAD_4},
{0x65 , SWT.KEYPAD_5},
{0x66 , SWT.KEYPAD_6},
{0x67 , SWT.KEYPAD_7},
{0x68 , SWT.KEYPAD_8},
{0x69 , SWT.KEYPAD_9},
{0x70 , SWT.F1},
{0x71 , SWT.F2},
{0x72 , SWT.F3},
{0x73 , SWT.F4},
{0x74 , SWT.F5},
{0x75 , SWT.F6},
{0x76 , SWT.F7},
{0x77 , SWT.F8},
{0x78 , SWT.F9},
{0x79 , SWT.F10},
{0x7A , SWT.F11},
{0x7B , SWT.F12},
};
static int keyCodeToEmbedKeyCode(int keyCode) {
for (int i=0; i<KeyTable.length; i++) {
if (KeyTable [i] [1] == keyCode) return KeyTable [i] [0];
}
return 0;
}
static int keyModifiersToEmbedKeyModifiers(int extModifiers) {
int embedModifiers = 0;
if ((extModifiers & SWT.SHIFT) != 0) {
embedModifiers |= AbstractEvents.MODIFIER_SHIFT;
}
if ((extModifiers & SWT.CTRL) != 0) {
embedModifiers |= AbstractEvents.MODIFIER_CONTROL;
}
if ((extModifiers & SWT.ALT) != 0) {
embedModifiers |= AbstractEvents.MODIFIER_ALT;
}
if ((extModifiers & SWT.COMMAND) != 0) {
embedModifiers |= AbstractEvents.MODIFIER_META;
}
return embedModifiers;
}
}
