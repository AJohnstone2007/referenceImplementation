package ensemble;
import ensemble.playground.PlaygroundProperty;
import ensemble.samplepage.SamplePage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.ConditionalFeature;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
public class SampleInfo {
public final String name;
public final String description;
public final String ensemblePath;
public final String baseUri;
public final String[] resourceUrls;
public final String mainFileUrl;
public final String appClass;
public final String previewUrl;
public final PlaygroundProperty[] playgroundProperties;
public final ConditionalFeature[] conditionalFeatures;
public final boolean runsOnEmbedded;
public final String[] apiClasspaths;
private final String[] docsUrls;
public final String[] relatesSamplePaths;
public SampleInfo(String name, String description, String ensemblePath, String baseUri, String appClass,
String previewUrl, String[] resourceUrls, String[] apiClasspaths,
String[] docsUrls, String[] relatesSamplePaths, String mainFileUrl,
PlaygroundProperty[] playgroundProperties, ConditionalFeature[] conditionalFeatures,
boolean runsOnEmbedded) {
this.name = name;
this.description = description;
this.ensemblePath = ensemblePath;
this.baseUri = baseUri;
this.appClass = appClass;
this.resourceUrls = resourceUrls;
this.mainFileUrl = mainFileUrl;
this.apiClasspaths = apiClasspaths;
this.docsUrls = docsUrls;
this.relatesSamplePaths = relatesSamplePaths;
this.playgroundProperties = playgroundProperties;
this.conditionalFeatures = conditionalFeatures;
this.runsOnEmbedded = runsOnEmbedded;
if (EnsembleApp.PRELOAD_PREVIEW_IMAGES) {
if (PlatformFeatures.USE_EMBEDDED_FILTER && !runsOnEmbedded) {
} else if (null == previewUrl) {
System.err.println("null previewUrl for : " + name);
} else {
java.net.URL url = getClass().getResource(previewUrl);
if (url != null) {
getImage(url.toExternalForm());
} else {
System.out.println("Note: Sample preview "+ensemblePath+" not found");
previewUrl = null;
}
}
}
this.previewUrl = previewUrl;
}
@Override public String toString() {
return name;
}
public Node getLargePreview() {
return new LargePreviewRegion();
}
public Node getMediumPreview() {
Region label = new Region();
if (previewUrl != null) {
String url = getClass().getResource(previewUrl).toExternalForm();
label.setBackground(
new Background(
new BackgroundFill[]{
new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)
},
new BackgroundImage[]{
new BackgroundImage(
getImage(url),
BackgroundRepeat.NO_REPEAT,
BackgroundRepeat.NO_REPEAT,
new BackgroundPosition(Side.LEFT,5,false, Side.TOP,5,false),
new BackgroundSize(206, 152, false, false, false, false)
)
}
));
}
label.getStyleClass().add("sample-medium-preview");
label.setMinSize(216, 162);
label.setPrefSize(216, 162);
label.setMaxSize(216, 162);
return label;
}
public SampleRuntimeInfo buildSampleNode() {
try {
Method play = null;
Method stop = null;
Class clz = Class.forName(appClass);
final Object app = clz.getDeclaredConstructor().newInstance();
Parent root = (Parent) clz.getMethod("createContent").invoke(app);
for (Method m : clz.getMethods()) {
switch(m.getName()) {
case "play":
play = m;
break;
case "stop":
stop = m;
break;
}
}
final Method fPlay = play;
final Method fStop = stop;
root.sceneProperty().addListener((ObservableValue<? extends Scene> ov, Scene oldScene, Scene newScene) -> {
try {
if (oldScene != null && fStop != null) {
fStop.invoke(app);
}
if (newScene != null && fPlay != null) {
fPlay.invoke(app);
}
} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
Logger.getLogger(SamplePage.class.getName()).log(Level.SEVERE, null, ex);
}
});
return new SampleRuntimeInfo(root, app, clz);
} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
Logger.getLogger(SamplePage.class.getName()).log(Level.SEVERE, null, ex);
}
return new SampleRuntimeInfo(new Pane(), new Object(), Object.class);
}
private static final Image SAMPLE_BACKGROUND = getImage(
SampleInfo.class.getResource("images/sample-background.png").toExternalForm());
private class LargePreviewRegion extends Region {
private final Node sampleNode = buildSampleNode().getSampleNode();
private final Label label = new Label();
private final ImageView background = new ImageView(SAMPLE_BACKGROUND);
public LargePreviewRegion() {
getStyleClass().add("sample-large-preview");
label.setText(name);
label.getStyleClass().add("sample-large-preview-label");
label.setAlignment(Pos.BOTTOM_CENTER);
label.setWrapText(true);
getChildren().addAll(background,sampleNode,label);
}
@Override protected double computeMinWidth(double height) { return 460; }
@Override protected double computeMinHeight(double width) { return 345; }
@Override protected double computePrefWidth(double height) { return 460; }
@Override protected double computePrefHeight(double width) { return 345; }
@Override protected void layoutChildren() {
double labelHeight = label.prefHeight(440);
background.setLayoutX(5);
background.setLayoutY(5);
background.setFitWidth(450);
background.setFitHeight(335);
sampleNode.setLayoutX(10);
sampleNode.setLayoutY(10);
sampleNode.resize(440, 315-labelHeight);
label.setLayoutX(10);
label.setLayoutY(345 - 15 - labelHeight);
label.resize(440, labelHeight);
}
}
private List<URL> relatedSampleURLs = new AbstractList<URL>() {
@Override
public URL get(final int index) {
return new URL() {
@Override
public String getURL() {
return relatesSamplePaths[index];
}
@Override
public String getName() {
String url = getURL();
return url.substring(url.lastIndexOf('/') + 1);
}
};
}
@Override
public int size() {
return relatesSamplePaths.length;
}
};
public List<URL> getRelatedSampleURLs() {
return relatedSampleURLs;
}
private List<URL> docURLs = new AbstractList<URL>() {
@Override
public URL get(final int index) {
return new URL() {
@Override
public String getURL() {
return docsUrls[index * 2];
}
@Override
public String getName() {
return docsUrls[index * 2 + 1];
}
};
}
@Override
public int size() {
return docsUrls.length / 2;
}
};
public List<URL> getDocURLs() {
return docURLs;
}
private List<URL> sources = new AbstractList<URL>() {
@Override
public URL get(final int index) {
return new URL() {
@Override
public String getURL() {
return resourceUrls[index];
}
@Override
public String getName() {
String url = getURL();
return url.substring(url.lastIndexOf('/') + 1);
}
};
}
@Override
public int size() {
return resourceUrls.length;
}
};
public List<URL> getSources() {
return sources;
}
public boolean needsPlayground() {
return playgroundProperties.length > 0;
}
public static interface URL {
String getURL();
String getName();
}
private static Map<String, Image> imageCache;
private static Image getImage(String url) {
if (imageCache == null) {
imageCache = new WeakHashMap<>();
}
Image image = imageCache.get(url);
if (image == null) {
image = new Image(url);
imageCache.put(url, image);
}
return image;
}
public static class SampleRuntimeInfo {
private final Parent sampleNode;
private final Object app;
private final Class clz;
public SampleRuntimeInfo(Parent sampleNode, Object app, Class clz) {
this.sampleNode = sampleNode;
this.app = app;
this.clz = clz;
}
public Object getApp() {
return app;
}
public Class getClz() {
return clz;
}
public Parent getSampleNode() {
return sampleNode;
}
}
}
