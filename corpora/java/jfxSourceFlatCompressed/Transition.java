package javafx.animation;
import com.sun.scenario.animation.AbstractPrimaryTimer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
public abstract class Transition extends Animation {
private ObjectProperty<Interpolator> interpolator;
private static final Interpolator DEFAULT_INTERPOLATOR = Interpolator.EASE_BOTH;
public final void setInterpolator(Interpolator value) {
if ((interpolator != null) || (!DEFAULT_INTERPOLATOR.equals(value))) {
interpolatorProperty().set(value);
}
}
public final Interpolator getInterpolator() {
return (interpolator == null) ? DEFAULT_INTERPOLATOR : interpolator.get();
}
public final ObjectProperty<Interpolator> interpolatorProperty() {
if (interpolator == null) {
interpolator = new SimpleObjectProperty<Interpolator>(
this, "interpolator", DEFAULT_INTERPOLATOR
);
}
return interpolator;
}
private Interpolator cachedInterpolator;
protected Interpolator getCachedInterpolator() {
return cachedInterpolator;
}
public Transition(double targetFramerate) {
super(targetFramerate);
}
public Transition() {
}
Transition(AbstractPrimaryTimer timer) {
super(timer);
}
protected Node getParentTargetNode() {
return (parent != null && parent instanceof Transition) ?
((Transition)parent).getParentTargetNode() : null;
}
protected abstract void interpolate(double frac);
private double calculateFraction(long currentTicks, long cycleTicks) {
final double frac = cycleTicks <= 0 ? 1.0 : (double) currentTicks / cycleTicks;
return cachedInterpolator.interpolate(0.0, 1.0, frac);
}
@Override
boolean startable(boolean forceSync) {
return super.startable(forceSync)
&& ((getInterpolator() != null) || (!forceSync && (cachedInterpolator != null)));
}
@Override
void sync(boolean forceSync) {
super.sync(forceSync);
if (forceSync || (cachedInterpolator == null)) {
cachedInterpolator = getInterpolator();
}
}
@Override
void doPlayTo(long currentTicks, long cycleTicks) {
setCurrentTicks(currentTicks);
interpolate(calculateFraction(currentTicks, cycleTicks));
}
@Override
void doJumpTo(long currentTicks, long cycleTicks, boolean forceJump) {
setCurrentTicks(currentTicks);
if (getStatus() != Status.STOPPED || forceJump) {
sync(false);
interpolate(calculateFraction(currentTicks, cycleTicks));
}
}
}
