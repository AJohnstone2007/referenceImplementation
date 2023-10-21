package com.javafx.experiments.importers.maya;
import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
public class MayaGroup extends Group {
Translate t = new Translate();
Translate rpt = new Translate();
Translate rp = new Translate();
Translate rpi = new Translate();
Translate spt = new Translate();
Translate sp = new Translate();
Translate spi = new Translate();
Rotate rx = new Rotate(0, Rotate.X_AXIS);
Rotate ry = new Rotate(0, Rotate.Y_AXIS);
Rotate rz = new Rotate(0, Rotate.Z_AXIS);
Scale s = new Scale();
public MayaGroup() {
initTransforms();
}
public MayaGroup(MayaGroup mayaGroup) {
t = mayaGroup.t.clone();
rpt = mayaGroup.rpt.clone();
rp = mayaGroup.rp.clone();
rpi = mayaGroup.rpi.clone();
sp = mayaGroup.sp.clone();
spi = mayaGroup.spi.clone();
rx = mayaGroup.rx.clone();
ry = mayaGroup.ry.clone();
rz = mayaGroup.rz.clone();
s = mayaGroup.s.clone();
setId(mayaGroup.getId());
setDepthTest(mayaGroup.getDepthTest());
setVisible(mayaGroup.isVisible());
initTransforms();
}
private void initTransforms() {
getTransforms().setAll(t, rpt, rp, rz, ry, rx, rpi, spt, sp, s, spi);
}
}
