package fx83dfeatures.utils3d;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;
public class CameraController {
private Camera3D camera;
private Node node;
private int up, down, left, right,
forward, back;
private double initialSpeed = 5;
private double maxSpeed = 200;
private double speed = initialSpeed;
private double orbitSpeed = 1.0;
private double previousX, previousY;
private double yaw = 0.0022;
private double pitch = -0.0022;
private double maxPitch = 0.95;
private AnimationTimer moveTimer;
private Rotate rotateX;
private Rotate rotateY;
private void handleKeyEvent(KeyEvent t) {
if (t.getEventType() == KeyEvent.KEY_PRESSED) {
handleKeyEvent(t, true);
} else if (t.getEventType() == KeyEvent.KEY_RELEASED) {
handleKeyEvent(t, false);
}
}
private void handleMouseEvent(MouseEvent t) {
if (t.getEventType() == MouseEvent.MOUSE_PRESSED) {
handleMousePress(t);
} else if (t.getEventType() == MouseEvent.MOUSE_DRAGGED) {
Vec2d d = getDragDelta(t);
double speedModifier = getSpeedModifier(t);
switch (t.getButton()) {
case PRIMARY:
handlePrimaryMouseDrag(t, d, speedModifier);
break;
case MIDDLE:
handleMiddleMouseDrag(t, d, speedModifier);
break;
case SECONDARY:
handleSecondaryMouseDrag(t, d, speedModifier);
break;
default:
throw new AssertionError();
}
}
}
private void setEventHandlers(Scene scene) {
scene.addEventHandler(KeyEvent.ANY, k -> handleKeyEvent(k));
scene.addEventHandler(MouseEvent.ANY, m -> handleMouseEvent(m));
}
private void setEventHandlers(SubScene subScene) {
subScene.addEventHandler(KeyEvent.ANY, k -> handleKeyEvent(k));
subScene.addEventHandler(MouseEvent.ANY, m -> handleMouseEvent(m));
}
public CameraController(Camera3D camera) {
this.camera = camera;
new AnimationTimer() {
long now = 0;
@Override
public void handle(long l) {
if (now == 0) {
now = l;
} else {
double dt = (l - now) * 1e-9;
now = l;
update(dt);
}
}
}.start();
}
public CameraController(Camera3D camera, Scene scene) {
this(camera);
setEventHandlers(scene);
}
public CameraController(Camera3D camera, SubScene subScene) {
this(camera);
setEventHandlers(subScene);
}
private boolean isNotMoving() {
return forward - back == 0 && right - left == 0 && up - down == 0;
}
private void update(double dt) {
if (isNotMoving()) {
return;
}
Vec3d shiftForward = camera.getForward();
shiftForward.mul(forward - back);
Vec3d shiftRight = camera.getRight();
shiftRight.mul(right - left);
Vec3d shiftUp = camera.getUp();
shiftUp.mul(up - down);
Vec3d shift = new Vec3d(shiftForward);
shift.add(shiftRight);
shift.add(shiftUp);
shift.mul(speed * dt);
shiftCamera(shift);
}
private void handleKeyEvent(KeyEvent event, boolean state) {
int pressed = state ? 1 : 0;
boolean wasNotMoving = isNotMoving();
switch (event.getCode()) {
case I:
if (state) {
System.out.println(camera);
}
break;
case Z:
if (state) {
camera.setTarget(0.0, 0.0, 0.0);
}
break;
case W:
case NUMPAD8:
case UP:
this.forward = pressed;
break;
case A:
case Q:
case NUMPAD4:
case LEFT:
this.left = pressed;
break;
case S:
case MINUS:
case NUMPAD2:
case NUMPAD5:
case DOWN:
this.back = pressed;
break;
case D:
case E:
case NUMPAD6:
case RIGHT:
this.right = pressed;
break;
case R:
case T:
case NUMPAD9:
case PAGE_UP:
this.up = pressed;
break;
case G:
case NUMPAD3:
case PAGE_DOWN:
this.down = pressed;
break;
case DIGIT1:
speed = 5;
break;
case DIGIT2:
speed = 10;
break;
case DIGIT3:
speed = 20;
break;
case DIGIT4:
speed = 40;
break;
case DIGIT5:
speed = 60;
break;
case DIGIT6:
speed = 80;
break;
case DIGIT7:
speed = 100;
break;
case DIGIT8:
speed = 120;
break;
case DIGIT9:
speed = 140;
break;
case DIGIT0:
speed = 160;
break;
case O:
orbitSpeed = 1.0;
break;
case OPEN_BRACKET:
break;
case CLOSE_BRACKET:
break;
}
if (isNotMoving()) {
if (moveTimer != null) {
moveTimer.stop();
moveTimer = null;
speed = initialSpeed;
}
} else if (wasNotMoving) {
moveTimer = new AnimationTimer() {
long now = 0;
@Override
public void handle(long l) {
if (now == 0) {
now = l;
} else {
double age = (l - now) * 1e-9;
if (age > 1.0) {
speed = initialSpeed * Math.pow(2, age);
if (speed > maxSpeed) {
speed = maxSpeed;
}
}
}
}
};
moveTimer.start();
}
event.consume();
}
private Vec2d getDragDelta(MouseEvent event) {
Vec2d res = new Vec2d();
res.x = event.getSceneX() - previousX;
res.y = event.getSceneY() - previousY;
previousX = event.getSceneX();
previousY = event.getSceneY();
return res;
}
private double getSpeedModifier(MouseEvent event) {
if (event.isShiftDown()) {
return 4.0;
} else if (event.isControlDown()) {
return 1.0;
} else {
return 1.0;
}
}
private void handleMousePress(MouseEvent event) {
previousX = event.getSceneX();
previousY = event.getSceneY();
event.consume();
}
private void handlePrimaryMouseDrag(MouseEvent event, Vec2d d, double localFactor) {
if (event.isAltDown()) {
orbit(d, localFactor);
event.consume();
} else if (node != null) {
rotateObject(d, localFactor);
}
}
private void handleMiddleMouseDrag(MouseEvent event, Vec2d d, double localFactor) {
if (event.isAltDown()) {
track(d, localFactor);
event.consume();
}
}
private void handleSecondaryMouseDrag(MouseEvent event, Vec2d d, double localFactor) {
if (event.isAltDown()) {
if (!event.isControlDown()) {
zoom(d, localFactor);
} else {
move(d, localFactor);
}
} else {
rotate(d, localFactor);
}
event.consume();
}
private double getLookatDist() {
Vec3d target = camera.getTarget();
target.sub(camera.getPosition());
return target.length();
}
private void shiftCamera(Vec3d shift) {
Vec3d pos = camera.getPosition();
Vec3d target = camera.getTarget();
pos.add(shift);
target.add(shift);
camera.setPos(pos.x, pos.y, pos.z);
camera.setTarget(target.x, target.y, target.z);
}
static private double clamp(double val, double min, double max) {
return val > max ? max : val < min ? min : val;
}
static private Vec3d globus(Vec2d ah) {
double ra = ah.x * Math.PI;
double rh = ah.y * Math.PI * 0.5;
double cosH = Math.cos(rh);
return new Vec3d(Math.sin(ra) * cosH, Math.sin(rh), Math.cos(ra) * cosH);
}
static private Vec2d antiGlobus(Vec3d globus) {
double rh = Math.asin(globus.y);
double ra = Math.atan2(globus.x, globus.z);
return new Vec2d(ra / Math.PI, rh * 2 / Math.PI);
}
private void orbit(Vec2d d, double speedFactor) {
Vec3d lookFromTarget = camera.getForward();
lookFromTarget.mul(-1.0);
Vec2d ah = antiGlobus(lookFromTarget);
ah.x += d.x * yaw * speedFactor;
ah.y -= d.y * Math.abs(pitch) * speedFactor;
ah.y = clamp(ah.y, -maxPitch, maxPitch);
lookFromTarget = globus(ah);
lookFromTarget.mul(getLookatDist());
Vec3d newPos = camera.getTarget();
newPos.add(lookFromTarget);
camera.setPos(newPos.x, newPos.y, newPos.z);
}
private void track(Vec2d d, double speedFactor) {
Vec3d shiftRight = camera.getRight();
shiftRight.mul(-d.x * 0.1 * orbitSpeed * speedFactor);
Vec3d shiftUp = camera.getUp();
shiftUp.mul(d.y * 0.1 * orbitSpeed * speedFactor);
shiftRight.add(shiftUp);
shiftCamera(shiftRight);
}
private void zoom(Vec2d d, double speedFactor) {
double dist = getLookatDist();
dist -= d.x * orbitSpeed * speedFactor;
dist += d.y * orbitSpeed * speedFactor;
if (dist < 0.1) {
dist = 0.1;
}
Vec3d lookFromTarget = camera.getForward();
lookFromTarget.mul(-1.0 * dist);
Vec3d newPos = camera.getTarget();
newPos.add(lookFromTarget);
camera.setPos(newPos.x, newPos.y, newPos.z);
}
private void move(Vec2d d, double speedFactor) {
Vec3d shiftForward = camera.getForward();
shiftForward.mul(d.x * .5 * orbitSpeed * speedFactor);
shiftCamera(shiftForward);
}
private void rotate(Vec2d d, double speedFactor) {
Vec2d ah = antiGlobus(camera.getForward());
ah.x += d.x * yaw * speedFactor;
ah.y += d.y * pitch * speedFactor;
ah.y = clamp(ah.y, -maxPitch, maxPitch);
Vec3d lookat = globus(ah);
lookat.mul(getLookatDist());
Vec3d pos = camera.getPosition();
pos.add(lookat);
camera.setTarget(pos.x, pos.y, pos.z);
}
private void rotateObject(Vec2d d, double speedFactor) {
double angle = rotateY.getAngle();
angle += d.x * yaw * speedFactor * 180;
rotateY.setAngle(angle % 360);
angle = rotateX.getAngle();
angle += d.y * yaw * speedFactor * 180;
rotateX.setAngle(angle % 360);
}
public void setObject(Node node) {
rotateX = new Rotate(0, new Point3D(1, 0, 0));
rotateY = new Rotate(0, new Point3D(0, 1, 0));
node.getTransforms().add(0, rotateX);
node.getTransforms().add(1, rotateY);
this.node = node;
}
public void setInvertMouse(boolean invert) {
pitch = Math.abs(pitch) * (invert ? -1 : 1);
}
public boolean getInvertMouse() {
return pitch < 0;
}
}
