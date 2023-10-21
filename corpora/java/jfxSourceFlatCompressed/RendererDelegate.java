package com.sun.scenario.effect.impl.sw;
import com.sun.scenario.effect.Effect.AccelType;
public interface RendererDelegate {
public AccelType getAccelType();
public String getPlatformPeerName(String name, int unrollCount);
}
