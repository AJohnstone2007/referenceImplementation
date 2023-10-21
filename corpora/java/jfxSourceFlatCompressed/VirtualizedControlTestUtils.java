package test.com.sun.javafx.scene.control.infrastructure;
import com.sun.javafx.tk.Toolkit;
import static javafx.scene.control.skin.VirtualFlowShim.*;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.skin.VirtualContainerBase;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.Region;
public class VirtualizedControlTestUtils {
public static void fireMouseOnVerticalTrack(Control control) {
ScrollBar scrollBar = getVerticalScrollBar(control);
Region track = (Region) scrollBar.lookup(".track");
MouseEventFirer firer = new MouseEventFirer(track, true);
firer.fireMousePressAndRelease();
Toolkit.getToolkit().firePulse();
}
public static void fireMouseOnHorizontalTrack(Control control) {
ScrollBar scrollBar = getHorizontalScrollBar(control);
Region track = (Region) scrollBar.lookup(".track");
MouseEventFirer firer = new MouseEventFirer(track, true);
firer.fireMousePressAndRelease();
Toolkit.getToolkit().firePulse();
}
public static ScrollBar getVerticalScrollBar(Control control) {
if (control.getSkin() instanceof VirtualContainerBase) {
VirtualFlow<?> flow = getVirtualFlow((VirtualContainerBase<?, ?>) control.getSkin());
return getVBar(flow);
}
throw new IllegalStateException("control's skin must be of type VirtualContainerBase but was: " + control.getSkin());
}
public static ScrollBar getHorizontalScrollBar(Control control) {
if (control.getSkin() instanceof VirtualContainerBase) {
VirtualFlow<?> flow = getVirtualFlow((VirtualContainerBase<?, ?>) control.getSkin());
return getHBar(flow);
}
throw new IllegalStateException("control's skin must be of type VirtualContainerBase but was: " + control.getSkin());
};
private VirtualizedControlTestUtils() {}
}
