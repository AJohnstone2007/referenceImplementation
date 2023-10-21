package com.javafx.experiments.importers.maya;
public abstract class MObject {
final String name;
MEnv env;
public String toString() {
return super.toString() + " MObject.name: " + name;
}
public MObject(MEnv env, String name) {
this.env = env; this.name = name;
}
public String getName() {
return name;
}
public MEnv getEnv() {
return env;
}
public abstract void accept(MEnv.Visitor visitor);
}
