package javafx.animation;
import com.sun.scenario.animation.AbstractPrimaryTimer;
import com.sun.scenario.animation.shared.TimelineClipCore;
public class TimelineShim {
public static TimelineClipCore getClipCore(Timeline timeline) {
return timeline.clipCore;
}
public static Timeline getTimeline( AbstractPrimaryTimer timer) {
return new Timeline(timer);
}
}
