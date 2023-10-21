package javafx.scene.media;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
public abstract class Track {
private String name;
private long trackID;
private Locale locale;
private Map<String,Object> metadata;
public final String getName() {
return name;
}
public final Locale getLocale() {
return locale;
}
public final long getTrackID() {
return trackID;
}
public final Map<String,Object> getMetadata() {
return metadata;
}
Track(long trackID, Map<String,Object> metadata) {
this.trackID = trackID;
Object value = metadata.get("name");
if (null != value && value instanceof String) {
name = (String)value;
}
value = metadata.get("locale");
if (null != value && value instanceof Locale) {
locale = (Locale)value;
}
this.metadata = Collections.unmodifiableMap(metadata);
}
private String description;
@Override
public final String toString() {
synchronized(this) {
if (null == description) {
StringBuilder sb = new StringBuilder();
Map<String,Object> md = getMetadata();
sb.append(this.getClass().getName());
sb.append("[ track id = ");
sb.append(trackID);
for (Map.Entry<String,Object> entry : md.entrySet()) {
Object value = entry.getValue();
if (null != value) {
sb.append(", ");
sb.append(entry.getKey());
sb.append(" = ");
sb.append(value.toString());
}
}
sb.append("]");
description = sb.toString();
}
}
return description;
}
}
