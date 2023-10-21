package com.sun.scenario.animation;
public class AbstractPrimaryTimerShim {
public static boolean isPaused(AbstractPrimaryTimer amt) {
return amt.isPaused();
}
public static long getTotalPausedTime(AbstractPrimaryTimer amt) {
return amt.getTotalPausedTime();
}
public static long getStartPauseTime(AbstractPrimaryTimer amt) {
return amt.getStartPauseTime();
}
}
