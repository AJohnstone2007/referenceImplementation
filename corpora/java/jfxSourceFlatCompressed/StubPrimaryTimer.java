package test.com.sun.javafx.pgstub;
import com.sun.javafx.tk.Toolkit;
import com.sun.scenario.DelayedRunnable;
import com.sun.scenario.animation.AbstractPrimaryTimer;
public class StubPrimaryTimer extends AbstractPrimaryTimer {
private long currentTimeMillis;
protected StubPrimaryTimer() {
}
protected int getPulseDuration(int precision) {
return precision / 60;
}
protected void postUpdateAnimationRunnable(DelayedRunnable animationRunnable) {
Toolkit.getToolkit().setAnimationRunnable(animationRunnable);
}
public void setCurrentTime(long millis) {
currentTimeMillis = millis;
}
@Override
public long nanos() {
return currentTimeMillis * 1000000;
}
@Override
public void pause() { }
@Override
public void resume() { }
}
