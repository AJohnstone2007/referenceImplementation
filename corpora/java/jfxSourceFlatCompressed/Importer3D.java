package com.javafx.experiments.importers;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.util.Pair;
import java.util.ServiceLoader;
public final class Importer3D {
public static String[] getSupportedFormatExtensionFilters() {
return new String[]{"*.ma", "*.ase", "*.obj", "*.fxml", "*.dae"};
}
public static Node load(String fileUrl) throws IOException {
return load(fileUrl,false);
}
public static Node load(String fileUrl, boolean asPolygonMesh) throws IOException {
return loadIncludingAnimation(fileUrl,asPolygonMesh).getKey();
}
public static Pair<Node,Timeline> loadIncludingAnimation(String fileUrl, boolean asPolygonMesh) throws IOException {
final int dot = fileUrl.lastIndexOf('.');
if (dot <= 0) {
throw new IOException("Unknown 3D file format, url missing extension [" + fileUrl + "]");
}
final String extension = fileUrl.substring(dot + 1, fileUrl.length()).toLowerCase();
ImporterFinder finder = new ImporterFinder();
URLClassLoader classLoader = finder.addUrlToClassPath();
ServiceLoader<Importer> servantLoader = ServiceLoader.load(Importer.class, classLoader);
Importer importer = null;
for (Importer plugin : servantLoader) {
if (plugin.isSupported(extension)) {
importer = plugin;
break;
}
}
if ((importer == null) && (!extension.equals("fxml"))){
String [] names = {
"com.javafx.experiments.importers.dae.DaeImporter",
"com.javafx.experiments.importers.max.MaxLoader",
"com.javafx.experiments.importers.maya.MayaImporter",
"com.javafx.experiments.importers.obj.ObjOrPolyObjImporter",
};
boolean fail = true;
for (String name : names) {
try {
Class<?> clazz = Class.forName(name);
Object obj = clazz.getDeclaredConstructor().newInstance();
if (obj instanceof Importer) {
Importer plugin = (Importer) obj;
if (plugin.isSupported(extension)) {
importer = plugin;
fail = false;
break;
}
}
} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
| InvocationTargetException | NoSuchMethodException e) {
}
}
if (fail) throw new IOException("Unknown 3D file format [" + extension + "]");
}
if (extension.equals("fxml")) {
final Object fxmlRoot = FXMLLoader.load(new URL(fileUrl));
if (fxmlRoot instanceof Node) {
return new Pair<>((Node) fxmlRoot, null);
} else if (fxmlRoot instanceof TriangleMesh) {
return new Pair<>(new MeshView((TriangleMesh) fxmlRoot), null);
}
throw new IOException("Unknown object in FXML file [" + fxmlRoot.getClass().getName() + "]");
} else {
importer.load(fileUrl, asPolygonMesh);
return new Pair<>(importer.getRoot(), importer.getTimeline());
}
}
}
