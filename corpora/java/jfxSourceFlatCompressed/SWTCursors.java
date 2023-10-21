package javafx.embed.swt;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.image.Image;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import com.sun.javafx.cursor.CursorFrame;
import com.sun.javafx.cursor.ImageCursorFrame;
class SWTCursors {
private static Cursor createCustomCursor(Display display, ImageCursorFrame cursorFrame) {
Image image = Toolkit.getImageAccessor().fromPlatformImage(cursorFrame.getPlatformImage());
ImageData imageData = SWTFXUtils.fromFXImage(image, null);
return new org.eclipse.swt.graphics.Cursor(
display, imageData, (int) cursorFrame.getHotspotX(), (int) cursorFrame.getHotspotY());
}
static Cursor embedCursorToCursor(CursorFrame cursorFrame) {
Display display = Display.getCurrent();
if (display == null) {
return null;
}
int id = SWT.CURSOR_ARROW;
switch (cursorFrame.getCursorType()) {
case DEFAULT: id = SWT.CURSOR_ARROW; break;
case CROSSHAIR: id = SWT.CURSOR_CROSS; break;
case TEXT: id = SWT.CURSOR_IBEAM; break;
case WAIT: id = SWT.CURSOR_WAIT; break;
case SW_RESIZE: id = SWT.CURSOR_SIZESW; break;
case SE_RESIZE: id = SWT.CURSOR_SIZESE; break;
case NW_RESIZE: id = SWT.CURSOR_SIZENW; break;
case NE_RESIZE: id = SWT.CURSOR_SIZENE; break;
case N_RESIZE: id = SWT.CURSOR_SIZEN; break;
case S_RESIZE: id = SWT.CURSOR_SIZES; break;
case W_RESIZE: id = SWT.CURSOR_SIZEW; break;
case E_RESIZE: id = SWT.CURSOR_SIZEE; break;
case OPEN_HAND:
case CLOSED_HAND:
case HAND: id = SWT.CURSOR_HAND; break;
case MOVE: id = SWT.CURSOR_SIZEALL; break;
case DISAPPEAR:
break;
case H_RESIZE: id = SWT.CURSOR_SIZEWE; break;
case V_RESIZE: id = SWT.CURSOR_SIZENS; break;
case NONE:
return null;
case IMAGE:
return createCustomCursor(display, (ImageCursorFrame) cursorFrame);
}
return display.getSystemCursor(id);
}
}
