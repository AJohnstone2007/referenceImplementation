package com.sun.javafx.scene.control.behavior;
import javafx.geometry.Orientation;
import javafx.scene.control.Skin;
import javafx.scene.control.Slider;
import com.sun.javafx.scene.control.inputmap.InputMap;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import com.sun.javafx.util.Utils;
import static javafx.scene.input.KeyCode.*;
public class SliderBehavior extends BehaviorBase<Slider> {
private final InputMap<Slider> sliderInputMap;
private TwoLevelFocusBehavior tlFocus;
public SliderBehavior(Slider slider) {
super(slider);
sliderInputMap = createInputMap();
addDefaultMapping(sliderInputMap,
new InputMap.KeyMapping(HOME, KeyEvent.KEY_RELEASED, e -> home()),
new InputMap.KeyMapping(END, KeyEvent.KEY_RELEASED, e -> end())
);
InputMap<Slider> horizontalMappings = new InputMap<>(slider);
horizontalMappings.setInterceptor(e -> slider.getOrientation() != Orientation.HORIZONTAL);
horizontalMappings.getMappings().addAll(
new InputMap.KeyMapping(LEFT, e -> rtl(slider, this::incrementValue, this::decrementValue)),
new InputMap.KeyMapping(KP_LEFT, e -> rtl(slider, this::incrementValue, this::decrementValue)),
new InputMap.KeyMapping(RIGHT, e -> rtl(slider, this::decrementValue, this::incrementValue)),
new InputMap.KeyMapping(KP_RIGHT, e -> rtl(slider, this::decrementValue, this::incrementValue))
);
addDefaultChildMap(sliderInputMap, horizontalMappings);
InputMap<Slider> verticalMappings = new InputMap<>(slider);
verticalMappings.setInterceptor(e -> slider.getOrientation() != Orientation.VERTICAL);
verticalMappings.getMappings().addAll(
new InputMap.KeyMapping(DOWN, e -> decrementValue()),
new InputMap.KeyMapping(KP_DOWN, e -> decrementValue()),
new InputMap.KeyMapping(UP, e -> incrementValue()),
new InputMap.KeyMapping(KP_UP, e -> incrementValue())
);
addDefaultChildMap(sliderInputMap, verticalMappings);
if (com.sun.javafx.scene.control.skin.Utils.isTwoLevelFocus()) {
tlFocus = new TwoLevelFocusBehavior(slider);
}
}
@Override public void dispose() {
if (tlFocus != null) tlFocus.dispose();
super.dispose();
}
@Override public InputMap<Slider> getInputMap() {
return sliderInputMap;
}
public void trackPress(MouseEvent e, double position) {
final Slider slider = getNode();
if (!slider.isFocused()) slider.requestFocus();
if (slider.getOrientation().equals(Orientation.HORIZONTAL)) {
slider.adjustValue(position * (slider.getMax() - slider.getMin()) + slider.getMin());
} else {
slider.adjustValue((1-position) * (slider.getMax() - slider.getMin()) + slider.getMin());
}
}
public void thumbPressed(MouseEvent e, double position) {
final Slider slider = getNode();
if (!slider.isFocused()) slider.requestFocus();
slider.setValueChanging(true);
}
public void thumbDragged(MouseEvent e, double position) {
final Slider slider = getNode();
slider.setValue(Utils.clamp(slider.getMin(), (position * (slider.getMax() - slider.getMin())) + slider.getMin(), slider.getMax()));
}
public void thumbReleased(MouseEvent e) {
final Slider slider = getNode();
slider.setValueChanging(false);
slider.adjustValue(slider.getValue());
}
void home() {
final Slider slider = getNode();
slider.adjustValue(slider.getMin());
}
void decrementValue() {
final Slider slider = getNode();
if (slider.isSnapToTicks()) {
slider.adjustValue(slider.getValue() - computeIncrement());
} else {
slider.decrement();
}
}
void end() {
final Slider slider = getNode();
slider.adjustValue(slider.getMax());
}
void incrementValue() {
final Slider slider = getNode();
if (slider.isSnapToTicks()) {
slider.adjustValue(slider.getValue()+ computeIncrement());
} else {
slider.increment();
}
}
double computeIncrement() {
final Slider slider = getNode();
double tickSpacing = 0;
if (slider.getMinorTickCount() != 0) {
tickSpacing = slider.getMajorTickUnit() / (Math.max(slider.getMinorTickCount(),0)+1);
} else {
tickSpacing = slider.getMajorTickUnit();
}
if (slider.getBlockIncrement() > 0 && slider.getBlockIncrement() < tickSpacing) {
return tickSpacing;
}
return slider.getBlockIncrement();
}
}
