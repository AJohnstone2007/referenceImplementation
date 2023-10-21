package com.javafx.experiments.importers.maya;
import com.javafx.experiments.importers.Importer;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.DepthTest;
import javafx.scene.Node;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
public class MayaImporter extends Importer {
public static final boolean DEBUG = Loader.DEBUG;
public static final boolean WARN = Loader.WARN;
MayaGroup root = new MayaGroup();
Timeline timeline;
Set<Node> meshParents = new HashSet();
@Override
public MayaGroup getRoot() {
return root;
}
@Override
public Timeline getTimeline() {
return timeline;
}
public Set<Node> getMeshParents() {
return meshParents;
}
@Override
public void load(String url, boolean asPolygonMesh) {
try {
Loader loader = new Loader();
loader.load(new java.net.URL(url), asPolygonMesh);
int nodeCount = 0;
for (Node n : loader.loaded.values()) {
if (n != null) {
if (n.getParent() == null) {
if (Loader.DEBUG) {
System.out.println("Adding top level node " + n.getId() + " to root!");
}
n.setDepthTest(DepthTest.ENABLE);
if (!(n instanceof MeshView) || ((TriangleMesh)((MeshView)n).getMesh()).getPoints().size() > 0) {
root.getChildren().add(n);
}
}
nodeCount++;
}
}
if (Loader.DEBUG) { System.out.println("There are " + nodeCount + " nodes."); }
timeline = new Timeline();
int count = 0;
for (final Map.Entry<Float, List<KeyValue>> e : loader.keyFrameMap.entrySet()) {
timeline.getKeyFrames().add
(
new KeyFrame(
javafx.util.Duration.millis(e.getKey() * 1000f),
(KeyValue[]) e.getValue().toArray(new KeyValue[e.getValue().size()])));
count++;
}
if (Loader.DEBUG) { System.out.println("Loaded " + count + " key frames."); }
} catch (Exception e) {
throw new RuntimeException(e);
}
}
@Override
public boolean isSupported(String extension) {
return extension != null && extension.equals("ma");
}
}
