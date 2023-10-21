package javafx.scene;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import javafx.scene.image.Image;
import com.sun.javafx.cursor.CursorFrame;
import com.sun.javafx.cursor.CursorType;
import com.sun.javafx.cursor.StandardCursorFrame;
public abstract class Cursor {
public static final Cursor DEFAULT =
new StandardCursor("DEFAULT", CursorType.DEFAULT);
public static final Cursor CROSSHAIR =
new StandardCursor("CROSSHAIR", CursorType.CROSSHAIR);
public static final Cursor TEXT =
new StandardCursor("TEXT", CursorType.TEXT);
public static final Cursor WAIT =
new StandardCursor("WAIT", CursorType.WAIT);
public static final Cursor SW_RESIZE =
new StandardCursor("SW_RESIZE", CursorType.SW_RESIZE);
public static final Cursor SE_RESIZE =
new StandardCursor("SE_RESIZE", CursorType.SE_RESIZE);
public static final Cursor NW_RESIZE =
new StandardCursor("NW_RESIZE", CursorType.NW_RESIZE);
public static final Cursor NE_RESIZE =
new StandardCursor("NE_RESIZE", CursorType.NE_RESIZE);
public static final Cursor N_RESIZE =
new StandardCursor("N_RESIZE", CursorType.N_RESIZE);
public static final Cursor S_RESIZE =
new StandardCursor("S_RESIZE", CursorType.S_RESIZE);
public static final Cursor W_RESIZE =
new StandardCursor("W_RESIZE", CursorType.W_RESIZE);
public static final Cursor E_RESIZE =
new StandardCursor("E_RESIZE", CursorType.E_RESIZE);
public static final Cursor OPEN_HAND =
new StandardCursor("OPEN_HAND", CursorType.OPEN_HAND);
public static final Cursor CLOSED_HAND =
new StandardCursor("CLOSED_HAND", CursorType.CLOSED_HAND);
public static final Cursor HAND =
new StandardCursor("HAND", CursorType.HAND);
public static final Cursor MOVE =
new StandardCursor("MOVE", CursorType.MOVE);
public static final Cursor DISAPPEAR =
new StandardCursor("DISAPPEAR", CursorType.DISAPPEAR);
public static final Cursor H_RESIZE =
new StandardCursor("H_RESIZE", CursorType.H_RESIZE);
public static final Cursor V_RESIZE =
new StandardCursor("V_RESIZE", CursorType.V_RESIZE);
public static final Cursor NONE =
new StandardCursor("NONE", CursorType.NONE);
private String name = "CUSTOM";
Cursor() { }
Cursor(String name) {
this.name = name;
}
abstract CursorFrame getCurrentFrame();
void activate() {
}
void deactivate() {
}
@Override public String toString() {
return name;
}
public static Cursor cursor(final String identifier) {
if (identifier == null) {
throw new NullPointerException(
"The cursor identifier must not be null");
}
if (isUrl(identifier)) {
return new ImageCursor(new Image(identifier));
}
String uName = identifier.toUpperCase(Locale.ROOT);
if (uName.equals(DEFAULT.name)) {
return DEFAULT;
} else if(uName.equals(CROSSHAIR.name)) {
return CROSSHAIR;
} else if (uName.equals(TEXT.name)) {
return TEXT;
} else if (uName.equals(WAIT.name)) {
return WAIT;
} else if (uName.equals(MOVE.name)) {
return MOVE;
} else if (uName.equals(SW_RESIZE.name)) {
return SW_RESIZE;
} else if (uName.equals(SE_RESIZE.name)) {
return SE_RESIZE;
} else if (uName.equals(NW_RESIZE.name)) {
return NW_RESIZE;
} else if (uName.equals(NE_RESIZE.name)) {
return NE_RESIZE;
} else if (uName.equals(N_RESIZE.name)) {
return N_RESIZE;
} else if (uName.equals(S_RESIZE.name)) {
return S_RESIZE;
} else if (uName.equals(W_RESIZE.name)) {
return W_RESIZE;
} else if (uName.equals(E_RESIZE.name)) {
return E_RESIZE;
} else if (uName.equals(OPEN_HAND.name)) {
return OPEN_HAND;
} else if (uName.equals(CLOSED_HAND.name)) {
return CLOSED_HAND;
} else if (uName.equals(HAND.name)) {
return HAND;
} else if (uName.equals(H_RESIZE.name)) {
return H_RESIZE;
} else if (uName.equals(V_RESIZE.name)) {
return V_RESIZE;
} else if (uName.equals(DISAPPEAR.name)) {
return DISAPPEAR;
} else if (uName.equals(NONE.name)) {
return NONE;
}
throw new IllegalArgumentException("Invalid cursor specification");
}
private static boolean isUrl(final String identifier) {
try {
new URL(identifier);
} catch (final MalformedURLException e) {
return false;
}
return true;
}
private static final class StandardCursor extends Cursor {
private final CursorFrame singleFrame;
public StandardCursor(final String name, final CursorType type) {
super(name);
singleFrame = new StandardCursorFrame(type);
}
@Override
CursorFrame getCurrentFrame() {
return singleFrame;
}
}
}
