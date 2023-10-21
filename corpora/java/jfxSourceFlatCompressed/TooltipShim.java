package javafx.scene.control;
import javafx.scene.layout.Pane;
public class TooltipShim extends Tooltip {
public TooltipShim() {
super();
}
public TooltipShim(String text) {
super(text);
}
public void shim_setActivated(boolean value) {
super.setActivated(value);
}
public Pane get_bridge() {
return bridge;
}
}
