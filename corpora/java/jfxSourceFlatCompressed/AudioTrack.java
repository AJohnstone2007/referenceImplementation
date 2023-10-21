package javafx.scene.media;
import java.util.Locale;
import java.util.Map;
public final class AudioTrack extends Track {
@Deprecated
public final String getLanguage() {
Locale l = getLocale();
return (null == l) ? null : l.getLanguage();
}
AudioTrack(long trackID, Map<String,Object> metadata) {
super(trackID, metadata);
}
}
