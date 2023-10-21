package javafx.animation;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javafx.beans.NamedArg;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
public final class KeyFrame {
private static final EventHandler<ActionEvent> DEFAULT_ON_FINISHED = null;
private static final String DEFAULT_NAME = null;
public Duration getTime() {
return time;
}
private final Duration time;
public Set<KeyValue> getValues() {
return values;
}
private final Set<KeyValue> values;
public EventHandler<ActionEvent> getOnFinished() {
return onFinished;
}
private final EventHandler<ActionEvent> onFinished;
public String getName() {
return name;
}
private final String name;
public KeyFrame(@NamedArg("time") Duration time, @NamedArg("name") String name,
@NamedArg("onFinished") EventHandler<ActionEvent> onFinished, @NamedArg("values") Collection<KeyValue> values) {
if (time == null) {
throw new NullPointerException("The time has to be specified");
}
if (time.lessThan(Duration.ZERO) || time.equals(Duration.UNKNOWN)) {
throw new IllegalArgumentException("The time is invalid.");
}
this.time = time;
this.name = name;
if (values != null) {
final Set<KeyValue> set = new CopyOnWriteArraySet<KeyValue>(values);
set.remove(null);
this.values = (set.size() == 0) ? Collections.<KeyValue> emptySet()
: (set.size() == 1) ? Collections.<KeyValue> singleton(set
.iterator().next()) : Collections
.unmodifiableSet(set);
} else {
this.values = Collections.<KeyValue> emptySet();
}
this.onFinished = onFinished;
}
public KeyFrame(@NamedArg("time") Duration time, @NamedArg("name") String name,
@NamedArg("onFinished") EventHandler<ActionEvent> onFinished, @NamedArg("values") KeyValue... values) {
if (time == null) {
throw new NullPointerException("The time has to be specified");
}
if (time.lessThan(Duration.ZERO) || time.equals(Duration.UNKNOWN)) {
throw new IllegalArgumentException("The time is invalid.");
}
this.time = time;
this.name = name;
if (values != null) {
final Set<KeyValue> set = new CopyOnWriteArraySet<KeyValue>();
for (final KeyValue keyValue : values) {
if (keyValue != null) {
set.add(keyValue);
}
}
this.values = (set.size() == 0) ? Collections.<KeyValue> emptySet()
: (set.size() == 1) ? Collections.<KeyValue> singleton(set
.iterator().next()) : Collections
.unmodifiableSet(set);
} else {
this.values = Collections.emptySet();
}
this.onFinished = onFinished;
}
public KeyFrame(@NamedArg("time") Duration time, @NamedArg("onFinished") EventHandler<ActionEvent> onFinished,
@NamedArg("values") KeyValue... values) {
this(time, DEFAULT_NAME, onFinished, values);
}
public KeyFrame(@NamedArg("time") Duration time, @NamedArg("name") String name, @NamedArg("values") KeyValue... values) {
this(time, name, DEFAULT_ON_FINISHED, values);
}
public KeyFrame(@NamedArg("time") Duration time, @NamedArg("values") KeyValue... values) {
this(time, DEFAULT_NAME, DEFAULT_ON_FINISHED, values);
}
@Override
public String toString() {
return "KeyFrame [time=" + time + ", values=" + values
+ ", onFinished=" + onFinished + ", name=" + name + "]";
}
@Override
public int hashCode() {
assert (time != null) && (values != null);
final int prime = 31;
int result = 1;
result = prime * result + time.hashCode();
result = prime * result + ((name == null) ? 0 : name.hashCode());
result = prime * result
+ ((onFinished == null) ? 0 : onFinished.hashCode());
result = prime * result + values.hashCode();
return result;
}
@Override
public boolean equals(Object obj) {
if (this == obj) {
return true;
}
if (obj instanceof KeyFrame) {
final KeyFrame kf = (KeyFrame) obj;
assert (time != null) && (values != null) && (kf.time != null)
&& (kf.values != null);
return time.equals(kf.time)
&& ((name == null) ? kf.name == null : name.equals(kf.name))
&& ((onFinished == null) ? kf.onFinished == null
: onFinished.equals(kf.onFinished))
&& values.equals(kf.values);
}
return false;
}
}
