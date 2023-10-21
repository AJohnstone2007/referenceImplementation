package test.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.EPDSettingsShim;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
public class EPDSettingsTest {
private static final String BITS_PER_PIXEL = "monocle.epd.bitsPerPixel";
private static final String ROTATE = "monocle.epd.rotate";
private static final String Y8_INVERTED = "monocle.epd.Y8Inverted";
private static final String NO_WAIT = "monocle.epd.noWait";
private static final String WAVEFORM_MODE = "monocle.epd.waveformMode";
private static final String FLAG_ENABLE_INVERSION = "monocle.epd.enableInversion";
private static final String FLAG_FORCE_MONOCHROME = "monocle.epd.forceMonochrome";
private static final String FLAG_USE_DITHERING_Y1 = "monocle.epd.useDitheringY1";
private static final String FLAG_USE_DITHERING_Y4 = "monocle.epd.useDitheringY4";
private static final String VERIFY_ERROR = "Verify the error log message for %s=%d.";
private EPDSettingsShim settings;
@Before
public void initialize() {
System.clearProperty(BITS_PER_PIXEL);
System.clearProperty(ROTATE);
System.clearProperty(Y8_INVERTED);
System.clearProperty(NO_WAIT);
System.clearProperty(WAVEFORM_MODE);
System.clearProperty(FLAG_ENABLE_INVERSION);
System.clearProperty(FLAG_FORCE_MONOCHROME);
System.clearProperty(FLAG_USE_DITHERING_Y1);
System.clearProperty(FLAG_USE_DITHERING_Y4);
}
@Test
public void testBitsPerPixel() {
System.setProperty(BITS_PER_PIXEL, "8");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(8, settings.bitsPerPixel);
System.setProperty(BITS_PER_PIXEL, "16");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(16, settings.bitsPerPixel);
System.setProperty(BITS_PER_PIXEL, "32");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(32, settings.bitsPerPixel);
System.err.println(String.format(VERIFY_ERROR, BITS_PER_PIXEL, 64));
System.setProperty(BITS_PER_PIXEL, "64");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(32, settings.bitsPerPixel);
}
@Test
public void testRotate() {
System.setProperty(ROTATE, "0");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(0, settings.rotate);
System.setProperty(ROTATE, "1");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(1, settings.rotate);
System.setProperty(ROTATE, "2");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(2, settings.rotate);
System.setProperty(ROTATE, "3");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(3, settings.rotate);
System.err.println(String.format(VERIFY_ERROR, ROTATE, 4));
System.setProperty(ROTATE, "4");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(0, settings.rotate);
}
@Test
public void testY8Inverted() {
System.setProperty(Y8_INVERTED, "false");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(0, settings.grayscale);
System.setProperty(Y8_INVERTED, "true");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(0, settings.grayscale);
System.setProperty(BITS_PER_PIXEL, "8");
System.setProperty(Y8_INVERTED, "false");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(0x1, settings.grayscale);
System.setProperty(Y8_INVERTED, "true");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(0x2, settings.grayscale);
}
@Test
public void testNoWait() {
System.setProperty(NO_WAIT, "false");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(false, settings.noWait);
System.setProperty(NO_WAIT, "true");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(true, settings.noWait);
}
@Test
public void testWaveformMode() {
System.setProperty(WAVEFORM_MODE, "1");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(1, settings.waveformMode);
System.setProperty(WAVEFORM_MODE, "2");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(2, settings.waveformMode);
System.setProperty(WAVEFORM_MODE, "3");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(3, settings.waveformMode);
System.setProperty(WAVEFORM_MODE, "4");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(4, settings.waveformMode);
System.err.println(String.format(VERIFY_ERROR, WAVEFORM_MODE, 5));
System.setProperty(WAVEFORM_MODE, "5");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(257, settings.waveformMode);
System.setProperty(WAVEFORM_MODE, "257");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(257, settings.waveformMode);
}
@Test
public void testFlagEnableInversion() {
System.setProperty(FLAG_ENABLE_INVERSION, "false");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(0, settings.flags);
System.setProperty(FLAG_ENABLE_INVERSION, "true");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(0x01, settings.flags);
}
@Test
public void testFlagForceMonochrome() {
System.setProperty(FLAG_FORCE_MONOCHROME, "false");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(0, settings.flags);
System.setProperty(FLAG_FORCE_MONOCHROME, "true");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(0x02, settings.flags);
}
@Test
public void testFlagUseDitheringY1() {
System.setProperty(FLAG_USE_DITHERING_Y1, "false");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(0, settings.flags);
System.setProperty(FLAG_USE_DITHERING_Y1, "true");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(0x2000, settings.flags);
}
@Test
public void testFlagUseDitheringY4() {
System.setProperty(FLAG_USE_DITHERING_Y4, "false");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(0, settings.flags);
System.setProperty(FLAG_USE_DITHERING_Y4, "true");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(0x4000, settings.flags);
}
@Test
public void testAllFlags() {
System.setProperty(FLAG_ENABLE_INVERSION, "true");
System.setProperty(FLAG_FORCE_MONOCHROME, "true");
System.setProperty(FLAG_USE_DITHERING_Y1, "true");
System.setProperty(FLAG_USE_DITHERING_Y4, "true");
settings = EPDSettingsShim.newInstance();
Assert.assertEquals(0x01 | 0x02 | 0x2000 | 0x4000, settings.flags);
}
}
