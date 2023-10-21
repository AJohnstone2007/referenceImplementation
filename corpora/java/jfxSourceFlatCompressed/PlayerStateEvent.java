package com.sun.media.jfxmedia.events;
import com.sun.media.jfxmediaimpl.MediaUtils;
public class PlayerStateEvent extends PlayerEvent {
public enum PlayerState {
UNKNOWN, READY, PLAYING, PAUSED, STOPPED, STALLED, FINISHED, HALTED
};
private PlayerState playerState;
private double playerTime;
private String message;
public PlayerStateEvent(PlayerState state, double time) {
if (state == null) {
throw new IllegalArgumentException("state == null!");
} else if (time < 0.0) {
throw new IllegalArgumentException("time < 0.0!");
}
this.playerState = state;
this.playerTime = time;
}
public PlayerStateEvent(PlayerState state, double time, String message) {
this(state, time);
this.message = message;
}
public PlayerState getState() {
return playerState;
}
public double getTime() {
return playerTime;
}
public String getMessage() {
return message;
}
}
