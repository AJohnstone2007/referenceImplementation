package test.javafx.animation;
import javafx.util.Duration;
public class AnimationDummy extends AnimationImpl {
public AnimationDummy(Duration duration) {
super.setCycleDuration(duration);
}
@Override
public void doPlayTo(long currentTicks, long cycleTicks) {
}
@Override
public void doJumpTo(long currentTicks, long cycleTicks, boolean forceJump) {
}
}
