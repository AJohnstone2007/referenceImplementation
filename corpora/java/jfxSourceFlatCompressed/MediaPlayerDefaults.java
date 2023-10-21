package fxmediaplayer;
import java.io.File;
public class MediaPlayerDefaults {
private static final String SETTINGS_FOLDER
= System.getProperty("user.home") + File.separator + ".FXMediaPlayer";
public static final String PLAYLIST_FILE
= SETTINGS_FOLDER + File.separator + "playlist.xml";
static {
new File(SETTINGS_FOLDER).mkdirs();
}
private static final String[] PLAYLIST_DEFAULT = {
"https://download.oracle.com/otndocs/products/javafx/oow2010-2.mp4",
"https://download.oracle.com/otndocs/javafx/"
+ "JavaRap_ProRes_H264_768kbit_Widescreen.mp4"
};
public static final String[] PLAYLIST = PLAYLIST_DEFAULT;
public static final String DEFAULT_SOURCE = PLAYLIST_DEFAULT[0];
}
