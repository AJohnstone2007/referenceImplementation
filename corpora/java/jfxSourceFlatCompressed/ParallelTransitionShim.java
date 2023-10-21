package javafx.animation;
import com.sun.scenario.animation.AbstractPrimaryTimer;
public class ParallelTransitionShim {
public static ParallelTransition getParallelTransition(
AbstractPrimaryTimer timer) {
return new ParallelTransition(timer);
}
}
