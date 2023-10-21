package test.javafx.animation;
import javafx.util.Duration;
import com.sun.scenario.animation.AbstractPrimaryTimer;
import static org.junit.Assert.*;
public class AnimationMock extends AnimationImpl {
public static final Duration DEFAULT_DURATION = Duration.seconds(1);
public static final double DEFAULT_RATE = 1.0;
public static final int DEFAULT_CYCLE_COUNT = 1;
public static final boolean DEFAULT_AUTOREVERSE = false;
private long lastTimePulse;
public enum Command {PLAY, JUMP, NONE};
private Command lastCommand = Command.NONE;
private long lastCurrentTicks = -1;
private long lastCycleTicks = -1;
private boolean finishFlag;
public void mockStatus(Status status) {
this.setStatus(status);
}
public void mockCycleDuration(Duration duration) {
shim_setCycleDuration(duration);
}
public AnimationMock(AbstractPrimaryTimer timer, Duration cycleDuration, double rate, int cycleCount, boolean autoReverse) {
super(timer);
shim_setCycleDuration(cycleDuration);
setRate(rate);
setCycleCount(cycleCount);
setAutoReverse(autoReverse);
super.setOnFinished(event -> {
finishFlag = true;
});
}
public void check(Command lastCommand, long lastCurrentTicks, long lastCycleTicks) {
assertEquals(lastCommand, this.lastCommand);
if (lastCommand != Command.NONE) {
assertEquals(lastCurrentTicks, this.lastCurrentTicks);
assertEquals(lastCycleTicks, this.lastCycleTicks);
}
this.lastCommand = Command.NONE;
this.lastCurrentTicks = -1;
this.lastCycleTicks = -1;
}
public boolean finishCalled() {
final boolean result = finishFlag;
finishFlag = false;
return result;
}
public long getLastTimePulse() {
final long p = lastTimePulse;
lastTimePulse = 0L;
return p;
}
@Override
public void doPlayTo(long currentTicks, long cycleTicks) {
lastCommand = Command.PLAY;
lastCurrentTicks = currentTicks;
lastCycleTicks = cycleTicks;
}
@Override
public void doJumpTo(long currentTicks, long cycleTicks, boolean forceJump) {
lastCommand = Command.JUMP;
lastCurrentTicks = currentTicks;
lastCycleTicks = cycleTicks;
}
@Override
public void doTimePulse(long elapsedTime) {
super.doTimePulse(elapsedTime);
lastTimePulse = elapsedTime;
}
}
