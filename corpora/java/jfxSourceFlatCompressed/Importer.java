package com.javafx.experiments.importers;
import java.io.IOException;
import javafx.animation.Timeline;
import javafx.scene.Group;
public abstract class Importer {
public abstract void load(String url, boolean asPolygonMesh) throws IOException;
public abstract Group getRoot();
public abstract boolean isSupported(String supportType);
public Timeline getTimeline() {
return null;
}
}
