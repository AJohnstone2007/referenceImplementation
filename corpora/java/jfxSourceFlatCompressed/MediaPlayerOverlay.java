package com.sun.media.jfxmedia.control;
public interface MediaPlayerOverlay {
public void setOverlayX(final double x);
public void setOverlayY(final double y);
public void setOverlayVisible(final boolean visible);
public void setOverlayWidth(final double width);
public void setOverlayHeight(final double height);
public void setOverlayPreserveRatio(final boolean preserveRatio);
public void setOverlayOpacity(final double opacity);
public void setOverlayTransform(
final double mxx, final double mxy, final double mxz, final double mxt,
final double myx, final double myy, final double myz, final double myt,
final double mzx, final double mzy, final double mzz, final double mzt);
}
