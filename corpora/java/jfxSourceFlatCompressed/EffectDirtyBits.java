package com.sun.javafx.effect;
public enum EffectDirtyBits {
EFFECT_DIRTY,
BOUNDS_CHANGED;
private int mask;
private EffectDirtyBits() {
mask = 1 << ordinal();
}
public final int getMask() {
return mask;
}
public static boolean isSet(int value, EffectDirtyBits dirtyBit) {
return (value & dirtyBit.getMask()) != 0;
}
}
