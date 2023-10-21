package javafx.scene.control.skin;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
public class ScrollPaneSkinShim extends ScrollPaneSkin {
public ScrollPaneSkinShim(ScrollPane control) {
super(control);
}
public ScrollBar get_hsb() {
return super.hsb;
}
public ScrollBar get_vsb() {
return super.vsb;
}
}
