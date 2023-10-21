package javafx.scene.control.skin;
import javafx.geometry.Orientation;
import javafx.scene.control.Control;
import javafx.scene.control.Separator;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Region;
public class SeparatorSkin extends SkinBase<Separator> {
private static final double DEFAULT_LENGTH = 10;
private final Region line;
public SeparatorSkin(Separator control) {
super(control);
line = new Region();
line.getStyleClass().setAll("line");
getChildren().add(line);
registerChangeListener(control.orientationProperty(), e -> getSkinnable().requestLayout());
registerChangeListener(control.halignmentProperty(), e -> getSkinnable().requestLayout());
registerChangeListener(control.valignmentProperty(), e -> getSkinnable().requestLayout());
}
@Override
public void dispose() {
getChildren().remove(line);
super.dispose();
}
@Override protected void layoutChildren(final double x, final double y,
final double w, final double h) {
final Separator sep = getSkinnable();
if (sep.getOrientation() == Orientation.HORIZONTAL) {
line.resize(w, line.prefHeight(-1));
} else {
line.resize(line.prefWidth(-1), h);
}
positionInArea(line, x, y, w, h, 0, sep.getHalignment(), sep.getValignment());
}
@Override protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
}
@Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
}
@Override protected double computePrefWidth(double h, double topInset, double rightInset, double bottomInset, double leftInset) {
final Separator sep = getSkinnable();
double w = sep.getOrientation() == Orientation.VERTICAL ? line.prefWidth(-1) : DEFAULT_LENGTH;
return w + leftInset + rightInset;
}
@Override protected double computePrefHeight(double w, double topInset, double rightInset, double bottomInset, double leftInset) {
final Separator sep = getSkinnable();
double h = sep.getOrientation() == Orientation.VERTICAL ? DEFAULT_LENGTH : line.prefHeight(-1);
return h + topInset + bottomInset;
}
@Override protected double computeMaxWidth(double h, double topInset, double rightInset, double bottomInset, double leftInset) {
final Separator sep = getSkinnable();
return sep.getOrientation() == Orientation.VERTICAL ? sep.prefWidth(h) : Double.MAX_VALUE;
}
@Override protected double computeMaxHeight(double w, double topInset, double rightInset, double bottomInset, double leftInset) {
final Separator sep = getSkinnable();
return sep.getOrientation() == Orientation.VERTICAL ? Double.MAX_VALUE : sep.prefHeight(w);
}
}
