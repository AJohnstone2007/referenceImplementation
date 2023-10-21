package javafx.stage;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import com.sun.javafx.tk.ScreenConfigurationAccessor;
import com.sun.javafx.tk.Toolkit;
public final class Screen {
private static final AtomicBoolean configurationDirty =
new AtomicBoolean(true);
private static final ScreenConfigurationAccessor accessor;
private static Screen primary;
private static final ObservableList<Screen> screens =
FXCollections.<Screen>observableArrayList();
private static final ObservableList<Screen> unmodifiableScreens =
FXCollections.unmodifiableObservableList(screens);
static {
accessor = Toolkit.getToolkit().setScreenConfigurationListener(() -> updateConfiguration());
}
private Screen() {
}
private static void checkDirty() {
if (configurationDirty.compareAndSet(true, false)) {
updateConfiguration();
}
}
private static void updateConfiguration() {
Object primaryScreen = Toolkit.getToolkit().getPrimaryScreen();
Screen screenTmp = nativeToScreen(primaryScreen, Screen.primary);
if (screenTmp != null) {
Screen.primary = screenTmp;
}
List<?> screens = Toolkit.getToolkit().getScreens();
ObservableList<Screen> newScreens = FXCollections.<Screen>observableArrayList();
boolean canKeepOld = (Screen.screens.size() == screens.size());
for (int i = 0; i < screens.size(); i++) {
Object obj = screens.get(i);
Screen origScreen = null;
if (canKeepOld) {
origScreen = Screen.screens.get(i);
}
Screen newScreen = nativeToScreen(obj, origScreen);
if (newScreen != null) {
if (canKeepOld) {
canKeepOld = false;
newScreens.setAll(Screen.screens.subList(0, i));
}
newScreens.add(newScreen);
}
}
if (!canKeepOld) {
Screen.screens.setAll(newScreens);
}
configurationDirty.set(false);
}
private static Screen nativeToScreen(Object obj, Screen screen) {
int minX = accessor.getMinX(obj);
int minY = accessor.getMinY(obj);
int width = accessor.getWidth(obj);
int height = accessor.getHeight(obj);
int visualMinX = accessor.getVisualMinX(obj);
int visualMinY = accessor.getVisualMinY(obj);
int visualWidth = accessor.getVisualWidth(obj);
int visualHeight = accessor.getVisualHeight(obj);
double dpi = accessor.getDPI(obj);
float outScaleX = accessor.getRecommendedOutputScaleX(obj);
float outScaleY = accessor.getRecommendedOutputScaleY(obj);
if ((screen == null) ||
(screen.bounds.getMinX() != minX) ||
(screen.bounds.getMinY() != minY) ||
(screen.bounds.getWidth() != width) ||
(screen.bounds.getHeight() != height) ||
(screen.visualBounds.getMinX() != visualMinX) ||
(screen.visualBounds.getMinY() != visualMinY) ||
(screen.visualBounds.getWidth() != visualWidth) ||
(screen.visualBounds.getHeight() != visualHeight) ||
(screen.dpi != dpi) ||
(screen.outputScaleX != outScaleX) ||
(screen.outputScaleY != outScaleY))
{
Screen s = new Screen();
s.bounds = new Rectangle2D(minX, minY, width, height);
s.visualBounds = new Rectangle2D(visualMinX, visualMinY, visualWidth, visualHeight);
s.dpi = dpi;
s.outputScaleX = outScaleX;
s.outputScaleY = outScaleY;
return s;
} else {
return null;
}
}
static Screen getScreenForNative(Object obj) {
double x = accessor.getMinX(obj);
double y = accessor.getMinY(obj);
double w = accessor.getWidth(obj);
double h = accessor.getHeight(obj);
Screen intScr = null;
for (int i = 0; i < screens.size(); i++) {
Screen scr = screens.get(i);
if (scr.bounds.contains(x, y, w, h)) {
return scr;
}
if (intScr == null && scr.bounds.intersects(x, y, w, h)) {
intScr = scr;
}
}
return (intScr == null) ? getPrimary() : intScr;
}
public static Screen getPrimary() {
checkDirty();
return primary;
}
public static ObservableList<Screen> getScreens() {
checkDirty();
return unmodifiableScreens;
}
public static ObservableList<Screen> getScreensForRectangle(
double x, double y, double width, double height)
{
checkDirty();
ObservableList<Screen> results = FXCollections.<Screen>observableArrayList();
for (Screen screen : screens) {
if (screen.bounds.intersects(x, y, width, height)) {
results.add(screen);
}
}
return results;
}
public static ObservableList<Screen> getScreensForRectangle(Rectangle2D r) {
checkDirty();
return getScreensForRectangle(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
}
private Rectangle2D bounds = Rectangle2D.EMPTY;
public final Rectangle2D getBounds() {
return bounds;
}
private Rectangle2D visualBounds = Rectangle2D.EMPTY;
public final Rectangle2D getVisualBounds() {
return visualBounds;
}
private double dpi;
public final double getDpi() {
return dpi;
}
private float outputScaleX;
public final double getOutputScaleX() {
return outputScaleX;
}
private float outputScaleY;
public final double getOutputScaleY() {
return outputScaleY;
}
@Override public int hashCode() {
long bits = 7L;
bits = 37L * bits + bounds.hashCode();
bits = 37L * bits + visualBounds.hashCode();
bits = 37L * bits + Double.doubleToLongBits(dpi);
bits = 37L * bits + Float.floatToIntBits(outputScaleX);
bits = 37L * bits + Float.floatToIntBits(outputScaleY);
return (int) (bits ^ (bits >> 32));
}
@Override public boolean equals(Object obj) {
if (obj == this) return true;
if (obj instanceof Screen) {
Screen other = (Screen) obj;
return (bounds == null ? other.bounds == null : bounds.equals(other.bounds))
&& (visualBounds == null ? other.visualBounds == null : visualBounds.equals(other.visualBounds))
&& other.dpi == dpi
&& other.outputScaleX == outputScaleX && other.outputScaleY == outputScaleY;
} else return false;
}
@Override public String toString() {
return super.toString() + " bounds:" + bounds + " visualBounds:" + visualBounds + " dpi:"
+ dpi + " outputScale:(" + outputScaleX + "," + outputScaleY + ")";
}
}
