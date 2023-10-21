package javafx.scene.media;
import java.util.Map;
public final class VideoTrack extends Track {
private int width;
public final int getWidth() {
return width;
}
private int height;
public final int getHeight() {
return height;
}
VideoTrack(long trackID, Map<String,Object> metadata) {
super(trackID, metadata);
Object value = metadata.get("video width");
if (null != value && value instanceof Number) {
this.width = ((Number)value).intValue();
}
value = metadata.get("video height");
if (null != value && value instanceof Number) {
this.height = ((Number)value).intValue();
}
}
}
