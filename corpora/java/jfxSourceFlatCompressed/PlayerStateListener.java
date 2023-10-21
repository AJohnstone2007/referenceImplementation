package com.sun.media.jfxmedia.events;
public interface PlayerStateListener
{
public void onReady(PlayerStateEvent evt);
public void onPlaying(PlayerStateEvent evt);
public void onPause(PlayerStateEvent evt);
public void onStop(PlayerStateEvent evt);
public void onStall(PlayerStateEvent evt);
public void onFinish(PlayerStateEvent evt);
public void onHalt(PlayerStateEvent evt);
}
