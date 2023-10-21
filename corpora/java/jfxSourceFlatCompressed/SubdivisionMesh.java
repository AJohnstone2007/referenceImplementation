package com.javafx.experiments.shape3d;
import com.javafx.experiments.shape3d.symbolic.SymbolicPolygonMesh;
import com.javafx.experiments.shape3d.symbolic.SymbolicSubdivisionBuilder;
import java.util.ArrayList;
import java.util.List;
public class SubdivisionMesh extends PolygonMesh {
private final PolygonMesh originalMesh;
private int subdivisionLevel;
private BoundaryMode boundaryMode;
private MapBorderMode mapBorderMode;
private final List<SymbolicPolygonMesh> symbolicMeshes;
private boolean pointValuesDirty;
private boolean meshDirty;
private boolean subdivisionLevelDirty;
public enum BoundaryMode {
CREASE_EDGES,
CREASE_ALL
}
public enum MapBorderMode {
NOT_SMOOTH,
SMOOTH_INTERNAL,
SMOOTH_ALL
}
public SubdivisionMesh(PolygonMesh originalMesh, int subdivisionLevel, BoundaryMode boundaryMode, MapBorderMode mapBorderMode) {
this.originalMesh = originalMesh;
setSubdivisionLevelForced(subdivisionLevel);
setBoundaryModeForced(boundaryMode);
setMapBorderModeForced(mapBorderMode);
symbolicMeshes = new ArrayList<>(4);
originalMesh.getPoints().addListener((observableArray, sizeChanged, from, to) -> {
if (sizeChanged) {
meshDirty = true;
} else {
pointValuesDirty = true;
}
});
originalMesh.getTexCoords().addListener((observableArray, sizeChanged, from, to) -> meshDirty = true);
}
public void update() {
if (meshDirty) {
symbolicMeshes.clear();
symbolicMeshes.add(new SymbolicPolygonMesh(originalMesh));
pointValuesDirty = true;
subdivisionLevelDirty = true;
}
while (subdivisionLevel >= symbolicMeshes.size()) {
symbolicMeshes.add(SymbolicSubdivisionBuilder.subdivide(symbolicMeshes.get(symbolicMeshes.size()-1), boundaryMode, mapBorderMode));
pointValuesDirty = true;
subdivisionLevelDirty = true;
}
if (pointValuesDirty) {
for (int i = 0; i <= subdivisionLevel; i++) {
SymbolicPolygonMesh symbolicMesh = symbolicMeshes.get(i);
symbolicMesh.points.update();
}
}
if (pointValuesDirty || subdivisionLevelDirty) {
getPoints().setAll(symbolicMeshes.get(subdivisionLevel).points.data);
}
if (subdivisionLevelDirty) {
faces = symbolicMeshes.get(subdivisionLevel).faces;
numEdgesInFaces = -1;
getFaceSmoothingGroups().setAll(symbolicMeshes.get(subdivisionLevel).faceSmoothingGroups);
getTexCoords().setAll(symbolicMeshes.get(subdivisionLevel).texCoords);
}
meshDirty = false;
pointValuesDirty = false;
subdivisionLevelDirty = false;
}
private void setSubdivisionLevelForced(int subdivisionLevel) {
this.subdivisionLevel = subdivisionLevel;
subdivisionLevelDirty = true;
}
private void setBoundaryModeForced(SubdivisionMesh.BoundaryMode boundaryMode) {
this.boundaryMode = boundaryMode;
meshDirty = true;
}
private void setMapBorderModeForced(SubdivisionMesh.MapBorderMode mapBorderMode) {
this.mapBorderMode = mapBorderMode;
meshDirty = true;
}
public PolygonMesh getOriginalMesh() {
return originalMesh;
}
public int getSubdivisionLevel() {
return subdivisionLevel;
}
public void setSubdivisionLevel(int subdivisionLevel) {
if (subdivisionLevel != this.subdivisionLevel) {
setSubdivisionLevelForced(subdivisionLevel);
}
}
public SubdivisionMesh.BoundaryMode getBoundaryMode() {
return boundaryMode;
}
public void setBoundaryMode(SubdivisionMesh.BoundaryMode boundaryMode) {
if (boundaryMode != this.boundaryMode) {
setBoundaryModeForced(boundaryMode);
}
}
public SubdivisionMesh.MapBorderMode getMapBorderMode() {
return mapBorderMode;
}
public void setMapBorderMode(SubdivisionMesh.MapBorderMode mapBorderMode) {
if (mapBorderMode != this.mapBorderMode) {
setMapBorderModeForced(mapBorderMode);
}
}
}
