package test.com.sun.javafx.pgstub;
import javafx.geometry.Dimension2D;
public abstract class CursorSizeConverter {
public static final CursorSizeConverter NO_CURSOR_SUPPORT =
createConstantConverter(0, 0);
public static final CursorSizeConverter IDENTITY_CONVERTER =
new IdentityConverter();
protected CursorSizeConverter() {
}
public abstract Dimension2D getBestCursorSize(
int preferredWidth,
int preferredHeight);
public static CursorSizeConverter createConstantConverter(
final int width, final int height) {
return new ConstantConverter(width, height);
}
private static final class ConstantConverter extends CursorSizeConverter {
final Dimension2D constantSize;
public ConstantConverter(final int width, final int height) {
constantSize = new Dimension2D(width, height);
}
@Override
public Dimension2D getBestCursorSize(
final int preferredWidth,
final int preferredHeight) {
return constantSize;
}
}
private static final class IdentityConverter extends CursorSizeConverter {
@Override
public Dimension2D getBestCursorSize(
final int preferredWidth,
final int preferredHeight) {
return new Dimension2D(preferredWidth, preferredHeight);
}
}
}
