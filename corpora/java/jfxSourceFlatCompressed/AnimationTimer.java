package javafx.animation;
import com.sun.javafx.tk.Toolkit;
import com.sun.scenario.animation.AbstractPrimaryTimer;
import com.sun.scenario.animation.shared.TimerReceiver;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
public abstract class AnimationTimer {
private class AnimationTimerReceiver implements TimerReceiver {
@SuppressWarnings("removal")
@Override public void handle(final long now) {
if (accessCtrlCtx == null) {
throw new IllegalStateException("Error: AccessControlContext not captured");
}
AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
AnimationTimer.this.handle(now);
return null;
}, accessCtrlCtx);
}
}
private final AbstractPrimaryTimer timer;
private final AnimationTimerReceiver timerReceiver = new AnimationTimerReceiver();
private boolean active;
@SuppressWarnings("removal")
private AccessControlContext accessCtrlCtx = null;
public AnimationTimer() {
timer = Toolkit.getToolkit().getPrimaryTimer();
}
AnimationTimer(AbstractPrimaryTimer timer) {
this.timer = timer;
}
public abstract void handle(long now);
@SuppressWarnings("removal")
public void start() {
if (!active) {
accessCtrlCtx = AccessController.getContext();
timer.addAnimationTimer(timerReceiver);
active = true;
}
}
public void stop() {
if (active) {
timer.removeAnimationTimer(timerReceiver);
active = false;
}
}
}
