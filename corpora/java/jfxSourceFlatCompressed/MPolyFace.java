package com.javafx.experiments.importers.maya.values;
import java.util.List;
public interface MPolyFace extends MData {
public static class FaceData {
private int[] faceEdges;
private int[] holeEdges;
private int[][] uvData;
private int[] faceColors;
public void setFaceEdges(int[] faceEdges) { this.faceEdges = faceEdges; }
public void setHoleEdges(int[] holeEdges) { this.holeEdges = holeEdges; }
public void setUVData(int index, int[] data) {
if (uvData == null || index >= uvData.length) {
int[][] newUVData = new int[index + 1][];
if (uvData != null) {
System.arraycopy(uvData, 0, newUVData, 0, uvData.length);
}
uvData = newUVData;
}
uvData[index] = data;
}
public void setFaceColors(int[] faceColors) { this.faceColors = faceColors; }
public int[] getFaceEdges() { return faceEdges; }
public int[] getHoleEdges() { return holeEdges; }
public int[][] getUVData() {
return uvData;
}
public int[] getFaceColors() { return faceColors; }
public String toString() {
StringBuffer buf = new StringBuffer();
buf.append("[FaceData faceEdges: ");
appendIntArray(buf, faceEdges);
buf.append(" holeEdges: ");
appendIntArray(buf, holeEdges);
buf.append(" uvData: ");
append2DIntArray(buf, uvData);
buf.append(" faceColors: ");
appendIntArray(buf, faceColors);
buf.append("]");
return buf.toString();
}
private void appendIntArray(StringBuffer buf, int[] array) {
if (array == null) {
buf.append(array);
} else {
buf.append("[");
for (int i = 0; i < array.length; i++) {
buf.append(" ");
buf.append(array[i]);
}
}
}
private void append2DIntArray(StringBuffer buf, int[][] array) {
if (array == null) {
buf.append(array);
} else {
buf.append("[");
for (int i = 0; i < array.length; i++) {
appendIntArray(buf, array[i]);
}
buf.append("]");
}
}
}
public void addFace(FaceData face);
public List<FaceData> getFaces();
}
