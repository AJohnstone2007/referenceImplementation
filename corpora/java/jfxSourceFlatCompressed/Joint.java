package com.javafx.experiments.importers.maya;
import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
public class Joint extends Group {
public final Translate t = new Translate();
public final Rotate jox = new Rotate();
{ jox.setAxis(Rotate.X_AXIS); }
public final Rotate joy = new Rotate();
{ joy.setAxis(Rotate.Y_AXIS); }
public final Rotate joz = new Rotate();
{ joz.setAxis(Rotate.Z_AXIS); }
public final Rotate rx = new Rotate();
{ rx.setAxis(Rotate.X_AXIS); }
public final Rotate ry = new Rotate();
{ ry.setAxis(Rotate.Y_AXIS); }
public final Rotate rz = new Rotate();
{ rz.setAxis(Rotate.Z_AXIS); }
public final Scale s = new Scale();
public final Scale is = new Scale();
public Joint() {
super();
getTransforms().addAll(t, is, joz, joy, jox, rz, ry, rx, s);
}
}
