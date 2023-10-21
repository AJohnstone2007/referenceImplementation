package com.javafx.experiments.importers.maya.values.impl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.javafx.experiments.importers.maya.types.MPolyFaceType;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.MPolyFace;
public class MPolyFaceImpl extends MDataImpl implements MPolyFace {
private List<FaceData> faces;
public MPolyFaceImpl(MPolyFaceType type) {
super(type);
}
public void addFace(FaceData face) {
if (faces == null) {
faces = new ArrayList<FaceData>();
}
faces.add(face);
}
@Override
public MData getData(int start, int end) {
return this;
}
public List<FaceData> getFaces() {
return faces;
}
public void parse(Iterator<String> values) {
new Parser(values).parse();
}
class Parser {
private Iterator<String> curArgs;
Parser(Iterator<String> args) {
curArgs = args;
}
public void parse() {
MPolyFace.FaceData curFace = null;
while (moreArgs()) {
String tok = nextArg();
if (tok.equals("f")) {
if (curFace != null) {
addFace(curFace);
}
curFace = new MPolyFace.FaceData();
curFace.setFaceEdges(nextIntArray());
} else if (tok.equals("h")) {
curFace.setHoleEdges(nextIntArray());
} else if (tok.equals("mu")) {
int uvSet = nextInt();
curFace.setUVData(uvSet, nextIntArray());
} else if (tok.equals("fc")) {
curFace.setFaceColors(nextIntArray());
}
}
if (curFace != null) {
addFace(curFace);
}
}
private boolean moreArgs() {
return curArgs.hasNext();
}
private String nextArg() {
return curArgs.next();
}
private int nextInt() {
return Integer.parseInt(nextArg());
}
private int[] nextIntArray() {
int num = nextInt();
int[] res = new int[num];
for (int i = 0; i < num; i++) {
res[i] = nextInt();
}
return res;
}
}
public String toString() {
StringBuffer result = new StringBuffer();
result.append(getType().getName());
if (faces == null) {
result.append(" ");
result.append(faces);
} else {
for (FaceData fd : faces) {
result.append(" ");
result.append(fd);
}
}
return result.toString();
}
}
