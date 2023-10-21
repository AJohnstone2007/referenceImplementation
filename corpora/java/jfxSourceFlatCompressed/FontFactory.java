package com.sun.javafx.font;
import java.io.InputStream;
public interface FontFactory {
public static final String DEFAULT_FULLNAME = "System Regular";
public PGFont createFont(String name, float size);
public PGFont createFont(String family,
boolean bold, boolean italic, float size);
public PGFont deriveFont(PGFont font,
boolean bold, boolean italic, float size);
public String[] getFontFamilyNames();
public String[] getFontFullNames();
public String[] getFontFullNames(String family);
public boolean hasPermission();
public PGFont[] loadEmbeddedFont(String name, InputStream stream,
float size, boolean register, boolean all);
public PGFont[] loadEmbeddedFont(String name, String path,
float size, boolean register, boolean all);
public boolean isPlatformFont(String name);
}
