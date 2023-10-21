package javafx.scene.paint;
import com.sun.javafx.beans.event.AbstractNotifyListener;
import com.sun.javafx.tk.Toolkit;
public abstract class Paint {
static {
Toolkit.setPaintAccessor(new Toolkit.PaintAccessor() {
@Override
public boolean isMutable(Paint paint) {
return paint.acc_isMutable();
}
@Override
public Object getPlatformPaint(Paint paint) {
return paint.acc_getPlatformPaint();
}
@Override
public void addListener(Paint paint, AbstractNotifyListener platformChangeListener) {
paint.acc_addListener(platformChangeListener);
}
@Override
public void removeListener(Paint paint, AbstractNotifyListener platformChangeListener) {
paint.acc_removeListener(platformChangeListener);
}
});
}
Paint() { }
boolean acc_isMutable() {
return false;
}
abstract Object acc_getPlatformPaint();
void acc_addListener(AbstractNotifyListener platformChangeListener) {
throw new UnsupportedOperationException("Not Supported.");
}
void acc_removeListener(AbstractNotifyListener platformChangeListener) {
throw new UnsupportedOperationException("Not Supported.");
}
public abstract boolean isOpaque();
public static Paint valueOf(String value) {
if (value == null) {
throw new NullPointerException("paint must be specified");
}
if (value.startsWith("linear-gradient(")) {
return LinearGradient.valueOf(value);
} else if (value.startsWith("radial-gradient(")) {
return RadialGradient.valueOf(value);
} else {
return Color.valueOf(value);
}
}
}
