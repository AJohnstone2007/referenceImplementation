package fxmediaplayer;
import java.io.File;
import java.util.List;
import javafx.scene.input.Dragboard;
public class FXMediaPlayerUtils {
public static String secondsToString(long seconds) {
long elapsedHours = seconds / (60 * 60);
long elapsedMinutes = (seconds - elapsedHours * 60 * 60) / 60;
long elapsedSeconds = seconds - elapsedHours * 60 * 60 - elapsedMinutes * 60;
if (elapsedHours > 0) {
return String.format("%d:%02d:%02d",
elapsedHours, elapsedMinutes, elapsedSeconds);
} else {
return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);
}
}
public static String secondsToString(double seconds) {
if (seconds == Double.POSITIVE_INFINITY) {
return "Inf";
} else {
return FXMediaPlayerUtils.secondsToString((long) seconds);
}
}
public static String millisToString(long millis) {
long seconds = millis / 1000;
long elapsedHours = seconds / (60 * 60);
long elapsedMinutes = (seconds - elapsedHours * 60 * 60) / 60;
long elapsedSeconds = seconds - elapsedHours * 60 * 60 - elapsedMinutes * 60;
long elapsedMillis = millis - (seconds * 1000);
if (elapsedHours > 0) {
return String.format("%d:%02d:%02d:%03d",
elapsedHours, elapsedMinutes, elapsedSeconds, elapsedMillis);
} else {
return String.format("%02d:%02d:%03d",
elapsedMinutes, elapsedSeconds, elapsedMillis);
}
}
public static String millisToString(double millis) {
if (millis == Double.POSITIVE_INFINITY) {
return "Inf";
} else {
return FXMediaPlayerUtils.millisToString((long) millis);
}
}
public static String getSourceFromDragboard(Dragboard db) {
if (db.hasString()) {
String source = db.getString();
if (source.startsWith("http://") || source.startsWith("https://")) {
return source;
}
} else if (db.hasFiles()) {
List<File> files = db.getFiles();
if (!files.isEmpty()) {
String source = files.get(0).getPath();
source = source.replace("\\", "/");
return "file:///" + source;
}
}
return null;
}
}
