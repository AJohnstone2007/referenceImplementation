package com.javafx.experiments.jfx3dviewer;
import javafx.util.Duration;
public class Frame extends Duration {
static double FPS = 24.0;
static double EPSILON = 0.000001;
Frame(double millis) {
super(millis);
}
public static Duration frame(int frame) {
return Duration.seconds(frame / FPS + EPSILON);
}
public static Duration frame(long frame) {
return Duration.seconds(frame / FPS + EPSILON);
}
public static long toFrame(Duration tion) {
return Math.round(tion.toSeconds() * FPS);
}
public static int toFrameAsInt(Duration tion) {
return (int) Math.round(tion.toSeconds() * FPS);
}
public static double toFrameAsDouble(Duration tion) {
return (tion.toSeconds() * FPS);
}
}
