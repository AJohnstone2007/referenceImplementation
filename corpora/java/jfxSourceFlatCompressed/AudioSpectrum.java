package com.sun.media.jfxmedia.effects;
public interface AudioSpectrum {
public boolean getEnabled();
public void setEnabled(boolean enabled);
public int getBandCount();
public void setBandCount(int bands);
public double getInterval();
public void setInterval(double interval);
public int getSensitivityThreshold();
public void setSensitivityThreshold(int threshold);
public float[] getMagnitudes(float[] magnitudes);
public float[] getPhases(float[] phases);
}
