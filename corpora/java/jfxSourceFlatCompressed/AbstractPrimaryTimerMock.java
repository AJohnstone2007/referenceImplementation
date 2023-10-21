package test.javafx.animation;
import java.util.HashSet;
import java.util.Set;
import com.sun.javafx.animation.TickCalculation;
import com.sun.scenario.DelayedRunnable;
import com.sun.scenario.animation.AbstractPrimaryTimer;
import com.sun.scenario.animation.shared.PulseReceiver;
public class AbstractPrimaryTimerMock extends AbstractPrimaryTimer {
private final Set<PulseReceiver> targets = new HashSet<PulseReceiver>();
private long nanos;
public void setNanos(long nanos) {
this.nanos = nanos;
}
@Override
public long nanos() {
return nanos;
}
@Override
protected void postUpdateAnimationRunnable(DelayedRunnable animationRunnable) {
}
@Override
protected int getPulseDuration(int precision) {
return precision / 60;
}
@Override
public void addPulseReceiver(PulseReceiver target) {
super.addPulseReceiver(target);
targets.add(target);
}
@Override
public void removePulseReceiver(PulseReceiver target) {
super.addPulseReceiver(target);
targets.remove(target);
}
public boolean containsPulseReceiver(PulseReceiver target) {
return targets.contains(target);
}
public void pulse() {
nanos += TickCalculation.toMillis(100) * 1000000L;
for (PulseReceiver pr : targets) {
pr.timePulse(TickCalculation.fromNano(nanos));
}
}
}
