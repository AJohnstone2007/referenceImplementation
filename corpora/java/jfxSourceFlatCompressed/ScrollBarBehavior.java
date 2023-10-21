package com.sun.javafx.scene.control.behavior;
import com.sun.javafx.util.Utils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Skin;
import com.sun.javafx.scene.control.inputmap.InputMap;
import javafx.util.Duration;
import static javafx.scene.input.KeyCode.*;
import static javafx.scene.input.KeyEvent.KEY_RELEASED;
public class ScrollBarBehavior extends BehaviorBase<ScrollBar> {
private final InputMap<ScrollBar> inputMap;
public ScrollBarBehavior(ScrollBar scrollBar) {
super(scrollBar);
inputMap = createInputMap();
addDefaultMapping(inputMap,
new InputMap.KeyMapping(HOME, KEY_RELEASED, e -> home()),
new InputMap.KeyMapping(END, KEY_RELEASED, e -> end())
);
InputMap<ScrollBar> horizontalInputMap = new InputMap<>(scrollBar);
horizontalInputMap.setInterceptor(e -> scrollBar.getOrientation() != Orientation.HORIZONTAL);
horizontalInputMap.getMappings().addAll(
new InputMap.KeyMapping(LEFT, e -> rtl(scrollBar, this::incrementValue, this::decrementValue)),
new InputMap.KeyMapping(KP_LEFT, e -> rtl(scrollBar, this::incrementValue, this::decrementValue)),
new InputMap.KeyMapping(RIGHT, e -> rtl(scrollBar, this::decrementValue, this::incrementValue)),
new InputMap.KeyMapping(KP_RIGHT, e -> rtl(scrollBar, this::decrementValue, this::incrementValue))
);
addDefaultChildMap(inputMap, horizontalInputMap);
InputMap<ScrollBar> verticalInputMap = new InputMap<>(scrollBar);
verticalInputMap.setInterceptor(e -> scrollBar.getOrientation() != Orientation.VERTICAL);
verticalInputMap.getMappings().addAll(
new InputMap.KeyMapping(UP, e -> decrementValue()),
new InputMap.KeyMapping(KP_UP, e -> decrementValue()),
new InputMap.KeyMapping(DOWN, e -> incrementValue()),
new InputMap.KeyMapping(KP_DOWN, e -> incrementValue())
);
addDefaultChildMap(inputMap, verticalInputMap);
}
@Override public InputMap<ScrollBar> getInputMap() {
return inputMap;
}
private void home() {
getNode().setValue(getNode().getMin());
}
private void decrementValue() {
getNode().adjustValue(0);
}
private void end() {
getNode().setValue(getNode().getMax());
}
private void incrementValue() {
getNode().adjustValue(1);
}
Timeline timeline;
public void trackPress(double position) {
if (timeline != null) return;
final ScrollBar bar = getNode();
if (!bar.isFocused() && bar.isFocusTraversable()) bar.requestFocus();
final double pos = position;
final boolean incrementing = (pos > ((bar.getValue() - bar.getMin())/(bar.getMax() - bar.getMin())));
timeline = new Timeline();
timeline.setCycleCount(Timeline.INDEFINITE);
final EventHandler<ActionEvent> step =
event -> {
boolean i = (pos > ((bar.getValue() - bar.getMin())/(bar.getMax() - bar.getMin())));
if (incrementing == i) {
bar.adjustValue(pos);
}
else {
stopTimeline();
}
};
final KeyFrame kf = new KeyFrame(Duration.millis(200), step);
timeline.getKeyFrames().add(kf);
timeline.play();
step.handle(null);
}
public void trackRelease() {
stopTimeline();
}
public void decButtonPressed() {
final ScrollBar bar = getNode();
if (!bar.isFocused() && bar.isFocusTraversable()) bar.requestFocus();
stopTimeline();
timeline = new Timeline();
timeline.setCycleCount(Timeline.INDEFINITE);
final EventHandler<ActionEvent> dec =
event -> {
if (bar.getValue() > bar.getMin()) {
bar.decrement();
}
else {
stopTimeline();
}
};
final KeyFrame kf = new KeyFrame(Duration.millis(200), dec);
timeline.getKeyFrames().add(kf);
timeline.play();
dec.handle(null);
}
public void decButtonReleased() {
stopTimeline();
}
public void incButtonPressed() {
final ScrollBar bar = getNode();
if (!bar.isFocused() && bar.isFocusTraversable()) bar.requestFocus();
stopTimeline();
timeline = new Timeline();
timeline.setCycleCount(Timeline.INDEFINITE);
final EventHandler<ActionEvent> inc =
event -> {
if (bar.getValue() < bar.getMax()) {
bar.increment();
}
else {
stopTimeline();
}
};
final KeyFrame kf = new KeyFrame(Duration.millis(200), inc);
timeline.getKeyFrames().add(kf);
timeline.play();
inc.handle(null);
}
public void incButtonReleased() {
stopTimeline();
}
public void thumbDragged(double position) {
final ScrollBar scrollbar = getNode();
stopTimeline();
if (!scrollbar.isFocused() && scrollbar.isFocusTraversable()) scrollbar.requestFocus();
double newValue = (position * (scrollbar.getMax() - scrollbar.getMin())) + scrollbar.getMin();
if (!Double.isNaN(newValue)) {
scrollbar.setValue(Utils.clamp(scrollbar.getMin(), newValue, scrollbar.getMax()));
}
}
private void stopTimeline() {
if (timeline != null) {
timeline.stop();
timeline = null;
}
}
}
