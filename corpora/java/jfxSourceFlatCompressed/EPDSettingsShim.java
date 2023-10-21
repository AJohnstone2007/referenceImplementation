package com.sun.glass.ui.monocle;
public class EPDSettingsShim {
public final int bitsPerPixel;
public final int rotate;
public final int waveformMode;
public final boolean noWait;
public final int grayscale;
public final int flags;
public static EPDSettingsShim newInstance() {
return new EPDSettingsShim(EPDSettings.newInstance());
}
private EPDSettingsShim(EPDSettings settings) {
bitsPerPixel = settings.bitsPerPixel;
rotate = settings.rotate;
waveformMode = settings.waveformMode;
noWait = settings.noWait;
grayscale = settings.grayscale;
flags = settings.flags;
}
}
