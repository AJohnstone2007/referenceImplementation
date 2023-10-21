package com.javafx.experiments.importers.max;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Point3D;
public class MaxData {
public static class MappingChannel {
public int ntPoints;
public float tPoints[];
public int faces[];
}
public static class Mesh {
public String name;
public int nPoints;
public float points[];
public int nFaces;
public int faces[];
public MappingChannel mapping[];
}
public static class NodeTM {
public String name;
public Point3D pos;
public Point3D tm[] = new Point3D[3];
}
public static class Material {
public String name;
public String diffuseMap;
public Point3D ambientColor;
public Point3D diffuseColor;
public Point3D specularColor;
}
public static class Node {
public String name;
public NodeTM nodeTM;
public Node parent;
public List<Node> children;
}
public static class LightNode extends Node {
public float intensity;
public float r, g, b;
}
public static class CameraNode extends Node {
public NodeTM target;
public float near, far, fov;
}
public static class GeomNode extends Node {
public Mesh mesh;
public int materialRef;
}
public Material materials[];
public Map<String, Node> nodes = new HashMap<>();
public Map<String, Node> roots = new HashMap<>();
}
