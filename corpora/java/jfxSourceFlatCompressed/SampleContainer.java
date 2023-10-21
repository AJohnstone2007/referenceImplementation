package ensemble.samplepage;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
public class SampleContainer extends Region {
private final boolean resizable;
private final Parent sampleNode;
public SampleContainer(Parent sampleNode) {
this.sampleNode = sampleNode;
resizable = sampleNode.isResizable() &&
(sampleNode.maxWidth(-1) == 0 || sampleNode.maxWidth(-1) > sampleNode.minWidth(-1))
&& (sampleNode.maxHeight(-1) == 0 || sampleNode.maxHeight(-1) > sampleNode.minHeight(-1));
getChildren().add(sampleNode);
getStyleClass().add("sample-container");
}
@Override protected void layoutChildren() {
super.layoutChildren();
double sw = sampleNode.getLayoutBounds().getWidth();
double sh = sampleNode.getLayoutBounds().getHeight();
double scale = Math.min(getWidth() / sw, getHeight() / sh);
if (resizable) {
sw *= scale;
sh *= scale;
if (sampleNode.maxWidth(-1) > 0) {
sw = Math.min(sw, sampleNode.maxWidth(-1));
}
if (sampleNode.maxHeight(-1) > 0) {
sh = Math.min(sh, sampleNode.maxHeight(-1));
}
sampleNode.resizeRelocate(Math.round((getWidth() - sw) / 2), Math.round((getHeight() - sh) / 2), sw, sh);
} else {
scale = Math.min(1, scale);
sampleNode.setScaleX(scale);
sampleNode.setScaleY(scale);
layoutInArea(sampleNode, 0, 0, getWidth(), getHeight(), 0, HPos.CENTER, VPos.CENTER);
}
}
@Override public double getBaselineOffset() {
return super.getBaselineOffset();
}
}
