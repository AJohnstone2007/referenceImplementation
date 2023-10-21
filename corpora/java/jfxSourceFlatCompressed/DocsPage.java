package ensemble;
import ensemble.generated.Samples;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
public class DocsPage extends Region implements ChangeListener<String>, Page{
private final WebView webView = new WebView();
private final ScrollPane scrollPane = new ScrollPane();
private final VBox sideBar = new VBox(10);
private final Label sideBarTitle = new Label("Related Samples:");
private final PageBrowser pageBrowser;
private boolean isLocalChange = false;
private boolean showSideBar = false;
public DocsPage(PageBrowser pageBrowser) {
this.pageBrowser = pageBrowser;
getChildren().add(webView);
scrollPane.setContent(sideBar);
sideBar.setAlignment(Pos.TOP_CENTER);
sideBar.getChildren().add(sideBarTitle);
sideBarTitle.getStyleClass().add("sidebar-title");
scrollPane.setFitToWidth(true);
sideBar.setPadding(new Insets(10));
webView.getEngine().locationProperty().addListener(this);
}
@Override public ReadOnlyStringProperty titleProperty() {
return webView.getEngine().titleProperty();
}
@Override public String getTitle() {
return webView.getEngine().getTitle();
}
@Override public String getUrl() {
return webView.getEngine().getLocation();
}
@Override public Node getNode() {
return this;
}
@Override public void changed(ObservableValue<? extends String> ov, String oldLocation, String newLocation) {
if (!isLocalChange) pageBrowser.externalPageChange(newLocation);
updateSidebar(newLocation);
}
public void goToUrl(String url) {
isLocalChange = true;
webView.getEngine().load(url);
isLocalChange = false;
}
@Override protected void layoutChildren() {
final double w = getWidth();
final double h = getHeight();
if (showSideBar) {
final double sideBarWidth = sideBar.prefWidth(-1)+14;
webView.resize(w - sideBarWidth, h);
scrollPane.setLayoutX(w - sideBarWidth);
scrollPane.resize(sideBarWidth, h);
} else {
webView.resize(w,h);
}
}
private void updateSidebar(String url) {
String key = url;
if (key.startsWith("https://docs.oracle.com/javase/8/javafx/api/")) {
key = key.substring("https://docs.oracle.com/javase/8/javafx/api/".length(), key.lastIndexOf('.'));
key = key.replaceAll("/", ".");
} else if (key.startsWith("https://docs.oracle.com/javase/8/docs/api/")) {
key = key.substring("https://docs.oracle.com/javase/8/docs/api/".length(), key.lastIndexOf('.'));
key = key.replaceAll("/", ".");
}
SampleInfo[] samples = Samples.getSamplesForDoc(key);
if (samples == null || samples.length == 0) {
sideBar.getChildren().clear();
getChildren().remove(scrollPane);
showSideBar = false;
} else {
sideBar.getChildren().setAll(sideBarTitle);
for (final SampleInfo sample: samples) {
Button sampleButton = new Button(sample.name);
sampleButton.setCache(true);
sampleButton.getStyleClass().setAll("sample-button");
sampleButton.setGraphic(sample.getMediumPreview());
sampleButton.setContentDisplay(ContentDisplay.TOP);
sampleButton.setOnAction((ActionEvent actionEvent) -> {
pageBrowser.goToSample(sample);
});
sideBar.getChildren().add(sampleButton);
}
if (!showSideBar) getChildren().add(scrollPane);
showSideBar = true;
}
}
}
