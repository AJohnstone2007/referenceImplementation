package ensemble.samplepage;
import ensemble.PlatformFeatures;
import ensemble.SampleInfo;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.util.Callback;
import static ensemble.samplepage.SamplePage.INDENT;
class SamplePageContent extends Region {
private Node playground;
private Description description;
private SampleContainer sampleContainer;
private boolean needsPlayground;
final SamplePage samplePage;
SamplePageContent(final SamplePage samplePage) {
this.samplePage = samplePage;
playground = new PlaygroundNode(samplePage);
description = new Description(samplePage);
samplePage.registerSampleInfoUpdater((SampleInfo sampleInfo) -> {
update(sampleInfo);
return null;
});
}
@Override protected void layoutChildren() {
super.layoutChildren();
double maxWidth = getWidth() - 2 * INDENT;
double maxHeight = getHeight() - 2 * INDENT;
boolean landscape = getWidth() >= getHeight();
boolean wide = getWidth() >= getHeight() * 1.5;
if (wide) {
double x = Math.round(getWidth() / 2 + INDENT / 2);
double w = getWidth() - INDENT - x;
sampleContainer.resizeRelocate(x, INDENT, (getWidth() - 3 * INDENT) / 2, maxHeight);
if (needsPlayground) {
double h = (getHeight() - INDENT * 3) / 2;
description.resizeRelocate(INDENT, INDENT, w, h);
playground.resizeRelocate(INDENT, Math.round(INDENT * 2 + h), w, h);
} else {
description.resizeRelocate(INDENT, INDENT, w, maxHeight);
}
} else {
sampleContainer.resizeRelocate(INDENT, INDENT, maxWidth, (getHeight() - 3 * INDENT) / 2);
double y = Math.round(getHeight() / 2 + INDENT / 2);
if (landscape) {
double h = getHeight() - INDENT - y;
if (needsPlayground) {
double w = (getWidth() - INDENT * 3) / 2;
playground.resizeRelocate(INDENT, y, w, h);
description.resizeRelocate(Math.round(INDENT * 2 + w), y, w, h);
} else {
description.resizeRelocate(INDENT, y, maxWidth, h);
}
} else {
double w = getWidth() - INDENT * 2;
if (needsPlayground) {
double h = (getHeight() - INDENT * 2 - y) / 2;
playground.resizeRelocate(INDENT, y, w, h);
description.resizeRelocate(INDENT, Math.round(y + h + INDENT), w, h);
} else {
double h = getHeight() - INDENT - y;
description.resizeRelocate(INDENT, y, w, h);
}
}
}
}
static Text title(String text) {
Text title = new Text(text);
title.getStyleClass().add("sample-page-box-title");
return title;
}
private void update(SampleInfo sampleInfo) {
sampleContainer = new SampleContainer(samplePage.sampleRuntimeInfoProperty.get().getSampleNode());
sampleContainer.getStyleClass().add("sample-page-sample-node");
if (!PlatformFeatures.DISPLAY_PLAYGROUND) {
needsPlayground = false;
} else {
needsPlayground = sampleInfo.needsPlayground();
}
if (needsPlayground) {
getChildren().setAll(sampleContainer, playground, description);
} else {
getChildren().setAll(sampleContainer, description);
}
}
}
