package javafx.animation;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.util.Duration;
public final class PauseTransition extends Transition {
private ObjectProperty<Duration> duration;
private static final Duration DEFAULT_DURATION = Duration.millis(400);
public final void setDuration(Duration value) {
if ((duration != null) || (!DEFAULT_DURATION.equals(value))) {
durationProperty().set(value);
}
}
public final Duration getDuration() {
return (duration == null)? DEFAULT_DURATION : duration.get();
}
public final ObjectProperty<Duration> durationProperty() {
if (duration == null) {
duration = new ObjectPropertyBase<Duration>(DEFAULT_DURATION) {
@Override
public void invalidated() {
try {
setCycleDuration(getDuration());
} catch (IllegalArgumentException e) {
if (isBound()) {
unbind();
}
set(getCycleDuration());
throw e;
}
}
@Override
public Object getBean() {
return PauseTransition.this;
}
@Override
public String getName() {
return "duration";
}
};
}
return duration;
}
public PauseTransition(Duration duration) {
setDuration(duration);
setCycleDuration(duration);
}
public PauseTransition() {
this(DEFAULT_DURATION);
}
@Override
public void interpolate(double frac) {
}
}
