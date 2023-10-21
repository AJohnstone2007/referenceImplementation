package javafx.scene.control;
import javafx.geometry.Orientation;
public class SeparatorMenuItem extends CustomMenuItem {
public SeparatorMenuItem() {
super(new Separator(Orientation.HORIZONTAL), false);
getStyleClass().add(DEFAULT_STYLE_CLASS);
}
private static final String DEFAULT_STYLE_CLASS = "separator-menu-item";
}
