package com.javafx.experiments.importers;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
public class ImporterFinder {
public URLClassLoader addUrlToClassPath() {
final Class<?> referenceClass = ImporterFinder.class;
final URL url = referenceClass.getProtectionDomain().getCodeSource().getLocation();
File libDir = null;
try {
File currentDir = new File(url.toURI()).getParentFile();
libDir = new File(currentDir, "lib");
} catch (URISyntaxException ue) {
ue.printStackTrace();
throw new RuntimeException("Could not import library. Failed to determine library location. URL = " + url.getPath());
}
if (libDir != null) {
File[] files = libDir.listFiles();
final List<URL> urlList = new ArrayList<>();
if (files != null) {
for (File file : files) {
try {
urlList.add(file.toURI().toURL());
} catch (MalformedURLException me) {
me.printStackTrace();
}
}
}
URLClassLoader cl = new URLClassLoader((URL[]) urlList.toArray(new URL[0]), this.getClass().getClassLoader());
return cl;
} else {
throw new RuntimeException("Could not import library. Failed to determine importer library location ");
}
}
}
