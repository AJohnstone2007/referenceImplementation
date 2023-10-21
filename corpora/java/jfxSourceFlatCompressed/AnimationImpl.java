package test.javafx.animation;
import com.sun.scenario.animation.AbstractPrimaryTimer;
import com.sun.scenario.animation.shared.ClipEnvelope;
import javafx.animation.AnimationShim;
public class AnimationImpl extends AnimationShim {
public AnimationImpl(AbstractPrimaryTimer timer, ClipEnvelope clipEnvelope, int resolution) {
super(timer, clipEnvelope, resolution);
}
public AnimationImpl() {
super();
}
public AnimationImpl(AbstractPrimaryTimer timer) {
super(timer);
}
@Override
public void doPlayTo(long currentTicks, long cycleTicks) {
}
@Override
public void doJumpTo(long currentTicks, long cycleTicks, boolean forceJump) {
setCurrentTicks(currentTicks);
}
}
