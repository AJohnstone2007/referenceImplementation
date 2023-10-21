package com.sun.scenario.effect.impl.hw;
import java.io.InputStream;
import com.sun.scenario.effect.Effect.AccelType;
public interface ShaderSource {
public InputStream loadSource(String name);
public AccelType getAccelType();
}
