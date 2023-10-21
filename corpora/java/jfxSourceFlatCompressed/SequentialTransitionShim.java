package javafx.animation;
import com.sun.scenario.animation.AbstractPrimaryTimer;
public class SequentialTransitionShim {
public static SequentialTransition getSequentialTransition(AbstractPrimaryTimer timer) {
return new SequentialTransition(timer);
}
}
