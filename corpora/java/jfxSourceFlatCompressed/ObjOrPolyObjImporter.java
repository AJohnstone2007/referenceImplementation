package com.javafx.experiments.importers.obj;
import com.javafx.experiments.importers.Importer;
import java.io.IOException;
import javafx.scene.Group;
public class ObjOrPolyObjImporter extends Importer {
final Group res = new Group();
@Override
public void load(String fileUrl, boolean asPolygonMesh) throws IOException {
if (asPolygonMesh) {
PolyObjImporter reader = new PolyObjImporter(fileUrl);
for (String mesh : reader.getMeshes()) {
res.getChildren().add(reader.buildPolygonMeshView(mesh));
};
} else {
ObjImporter reader = new ObjImporter(fileUrl);
for (String mesh : reader.getMeshes()) {
res.getChildren().add(reader.buildMeshView(mesh));
};
}
}
@Override
public Group getRoot() {
return res;
}
@Override
public boolean isSupported(String extension) {
return extension != null && extension.equals("obj");
}
}
