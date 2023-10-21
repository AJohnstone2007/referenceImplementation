package com.javafx.experiments.importers.maya;
import java.util.Comparator;
public class MConnection {
private MPath sourcePath;
private MPath targetPath;
public MConnection(MPath sourcePath, MPath targetPath) {
this.sourcePath = sourcePath;
this.targetPath = targetPath;
}
public MPath getSourcePath() {
return sourcePath;
}
public MPath getTargetPath() {
return targetPath;
}
public boolean equals(Object arg) {
if (!(arg instanceof MConnection)) {
return false;
}
MConnection other = (MConnection) arg;
return (sourcePath.equals(other.sourcePath) &&
targetPath.equals(other.targetPath));
}
public int hashCode() {
return sourcePath.hashCode() ^ targetPath.hashCode();
}
public static final Comparator SOURCE_PATH_COMPARATOR = (o1, o2) -> {
MConnection c1 = (MConnection) o1;
MConnection c2 = (MConnection) o2;
return c1.getSourcePath().compareTo(c2.getSourcePath());
};
public static final Comparator TARGET_PATH_COMPARATOR = (o1, o2) -> {
MConnection c1 = (MConnection) o1;
MConnection c2 = (MConnection) o2;
return c1.getTargetPath().compareTo(c2.getTargetPath());
};
}
