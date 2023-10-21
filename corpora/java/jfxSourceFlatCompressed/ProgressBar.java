package javafx.scene.control;
import javafx.scene.control.skin.ProgressBarSkin;
import javafx.beans.value.WritableValue;
import javafx.css.StyleableProperty;
import javafx.scene.AccessibleAttribute;
import javafx.geometry.Orientation;
public class ProgressBar extends ProgressIndicator {
public ProgressBar() {
this(INDETERMINATE_PROGRESS);
}
public ProgressBar(double progress) {
((StyleableProperty<Boolean>)(WritableValue<Boolean>)focusTraversableProperty()).applyStyle(null, Boolean.FALSE);
setProgress(progress);
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
}
@Override protected Skin<?> createDefaultSkin() {
return new ProgressBarSkin(this);
}
private static final String DEFAULT_STYLE_CLASS = "progress-bar";
@Override protected Boolean getInitialFocusTraversable() {
return Boolean.FALSE;
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case ORIENTATION: return Orientation.HORIZONTAL;
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
}
