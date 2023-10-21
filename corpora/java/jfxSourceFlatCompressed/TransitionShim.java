package javafx.animation;
import com.sun.scenario.animation.AbstractPrimaryTimer;
import javafx.scene.Node;
public abstract class TransitionShim extends Transition {
protected TransitionShim() {
super();
}
protected TransitionShim(double targetFramerate) {
super(targetFramerate);
}
protected TransitionShim(AbstractPrimaryTimer timer) {
super(timer);
}
@Override
public void doPause() {
super.doPause();
}
@Override
public void sync(boolean forceSync) {
super.sync(forceSync);
}
@Override
public void doJumpTo(long currentTicks, long cycleTicks, boolean forceJump) {
super.doJumpTo(currentTicks, cycleTicks, forceJump);
}
public void shim_impl_finished() {
super.finished();
}
@Override
public Node getParentTargetNode() {
return super.getParentTargetNode();
}
@Override
public void doStart(boolean forceSync) {
super.doStart(forceSync);
}
@Override
public boolean startable(boolean forceSync) {
return super.startable(forceSync);
}
@Override
public void doPlayTo(long currentTicks, long cycleTicks) {
super.doPlayTo(currentTicks, cycleTicks);
}
@Override
public Interpolator getCachedInterpolator() {
return super.getCachedInterpolator();
}
public static void interpolate(Transition t, double frac) {
t.interpolate(frac);
}
}
