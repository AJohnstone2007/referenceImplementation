package com.sun.glass.ui.monocle;
import com.sun.glass.events.TouchEvent;
import com.sun.glass.ui.GestureSupport;
import com.sun.glass.ui.TouchInputSupport;
import com.sun.glass.ui.View;
import com.sun.glass.ui.Window;
import java.security.AccessController;
import java.security.PrivilegedAction;
class TouchInput {
@SuppressWarnings("removal")
private final int touchRadius = AccessController.doPrivileged(
(PrivilegedAction<Integer>) () -> Integer.getInteger(
"monocle.input.touchRadius", 20)
);
private static TouchInput instance = new TouchInput();
private TouchPipeline basePipeline;
private TouchState state = new TouchState();
private final GestureSupport gestures = new GestureSupport(false);
private final TouchInputSupport touches =
new TouchInputSupport(gestures.createTouchCountListener(), false);
static TouchInput getInstance() {
return instance;
}
private TouchInput() {
}
TouchPipeline getBasePipeline() {
if (basePipeline == null) {
basePipeline = new TouchPipeline();
@SuppressWarnings("removal")
String[] touchFilterNames = AccessController.doPrivileged(
(PrivilegedAction<String>) () -> System.getProperty(
"monocle.input.touchFilters",
"SmallMove")
).split(",");
if (touchFilterNames != null) {
for (String touchFilterName : touchFilterNames) {
basePipeline.addNamedFilter(touchFilterName.trim());
}
}
}
return basePipeline;
}
void getState(TouchState result) {
state.copyTo(result);
}
void setState(TouchState newState) {
if (MonocleSettings.settings.traceEvents) {
MonocleTrace.traceEvent("Set %s", newState);
}
newState.sortPointsByID();
newState.assignPrimaryID();
MonocleWindow oldWindow = state.getWindow(false, null);
boolean recalculateWindow = state.getPointCount() == 0;
MonocleWindow window = newState.getWindow(recalculateWindow, oldWindow);
View oldView = oldWindow == null ? null : oldWindow.getView();
View view = window == null ? null : window.getView();
if (!newState.equalsSorted(state)) {
if (view != oldView) {
postTouchEvent(state, TouchEvent.TOUCH_RELEASED);
postTouchEvent(newState,
TouchEvent.TOUCH_PRESSED);
} else if (view != null) {
postTouchEvent(window, view, newState);
}
MouseInputSynthesizer.getInstance().setState(newState);
}
newState.copyTo(state);
newState.clearWindow();
}
private void dispatchPoint(Window window, View view, int state,
int id, int x, int y) {
touches.notifyNextTouchEvent(view, state, id,
x - window.getX(), y - window.getY(),
x, y);
}
private void postPoints(Window window, View view,
int[] states, int[] ids, int[] xs, int[] ys) {
RunnableProcessor.runLater(() -> {
touches.notifyBeginTouchEvent(view, 0, true, states.length);
for (int i = 0; i < states.length; i++) {
dispatchPoint(window, view, states[i], ids[i],
xs[i], ys[i]);
}
touches.notifyEndTouchEvent(view);
});
}
private void postPoint(Window window, View view,
int state, TouchState.Point p) {
int id = p.id;
int x = p.x;
int y = p.y;
RunnableProcessor.runLater(() -> {
touches.notifyBeginTouchEvent(view, 0, true, 1);
dispatchPoint(window, view, state, id, x, y);
touches.notifyEndTouchEvent(view);
});
}
private void postNoPoints(View view) {
RunnableProcessor.runLater(() -> {
touches.notifyBeginTouchEvent(view, 0, true, 0);
touches.notifyEndTouchEvent(view);
});
}
private void postTouchEvent(TouchState state, int eventType) {
Window window = state.getWindow(false, null);
View view = window == null ? null : window.getView();
if (view != null) {
switch (state.getPointCount()) {
case 0:
postNoPoints(view);
break;
case 1:
postPoint(window, view, eventType, state.getPoint(0));
break;
default: {
int count = state.getPointCount();
int[] states = new int[count];
int[] ids = new int[count];
int[] xs = new int[count];
int[] ys = new int[count];
for (int i = 0; i < count; i++) {
states[i] = eventType;
TouchState.Point p = state.getPoint(i);
ids[i] = p.id;
xs[i] = p.x;
ys[i] = p.y;
}
postPoints(window, view, states, ids, xs, ys);
}
}
}
}
private void postTouchEvent(MonocleWindow window,
View view,
TouchState newState) {
int count = countEvents(newState);
switch (count) {
case 0:
postNoPoints(view);
break;
case 1:
if (state.getPointCount() == 1) {
TouchState.Point oldPoint = state.getPoint(0);
TouchState.Point newPoint = newState.getPointForID(
oldPoint.id);
if (newPoint != null) {
if (newPoint.x == oldPoint.x
&& newPoint.y == oldPoint.y) {
postPoint(window, view, TouchEvent.TOUCH_STILL, newPoint);
} else {
postPoint(window, view, TouchEvent.TOUCH_MOVED, newPoint);
}
} else {
postPoint(window, view, TouchEvent.TOUCH_RELEASED, oldPoint);
}
} else {
postPoint(window, view, TouchEvent.TOUCH_PRESSED, newState.getPoint(0));
}
break;
default: {
int[] states = new int[count];
int[] ids = new int[count];
int[] xs = new int[count];
int[] ys = new int[count];
for (int i = 0; i < state.getPointCount(); i++) {
TouchState.Point oldPoint = state.getPoint(i);
TouchState.Point newPoint = newState.getPointForID(
oldPoint.id);
if (newPoint != null) {
ids[i] = newPoint.id;
xs[i] = newPoint.x;
ys[i] = newPoint.y;
if (newPoint.x == oldPoint.x
&& newPoint.y == oldPoint.y) {
states[i] = TouchEvent.TOUCH_STILL;
} else {
states[i] = TouchEvent.TOUCH_MOVED;
}
} else {
states[i] = TouchEvent.TOUCH_RELEASED;
ids[i] = oldPoint.id;
xs[i] = oldPoint.x;
ys[i] = oldPoint.y;
}
}
for (int i = 0, j = state.getPointCount();
i < newState.getPointCount(); i++) {
TouchState.Point newPoint = newState.getPoint(i);
TouchState.Point oldPoint = state.getPointForID(
newPoint.id);
if (oldPoint == null) {
states[j] = TouchEvent.TOUCH_PRESSED;
ids[j] = newPoint.id;
xs[j] = newPoint.x;
ys[j] = newPoint.y;
j++;
}
}
postPoints(window, view, states, ids, xs, ys);
}
}
}
private int countEvents(TouchState newState) {
int count = state.getPointCount();
for (int i = 0; i < newState.getPointCount(); i++) {
TouchState.Point newPoint = newState.getPoint(i);
TouchState.Point oldPoint = state.getPointForID(newPoint.id);
if (oldPoint == null) {
count ++;
}
}
return count;
}
int getTouchRadius() {
return touchRadius;
}
}
