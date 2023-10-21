package ensemble.samplepage;
import ensemble.Page;
import ensemble.SampleInfo;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
public class SourcePage extends TabPane implements Page {
private final ObjectProperty<SampleInfo> sampleInfoProperty = new SimpleObjectProperty<>();
private final StringProperty titleProperty = new SimpleStringProperty();
public SourcePage() {
getStyleClass().add("source-page");
titleProperty.bind(new StringBinding() {
{ bind(sampleInfoProperty); }
@Override protected String computeValue() {
SampleInfo sample = sampleInfoProperty.get();
if (sample != null) {
return sample.name+" :: Source";
} else {
return null;
}
}
});
setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
}
public void setSampleInfo(SampleInfo sampleInfo) {
sampleInfoProperty.set(sampleInfo);
getTabs().clear();
for (SampleInfo.URL sourceURL : sampleInfo.getSources()) {
getTabs().add(new SourceTab(sourceURL));
}
}
@Override public ReadOnlyStringProperty titleProperty() {
return titleProperty;
}
@Override public String getTitle() {
return titleProperty.get();
}
@Override public String getUrl() {
return "sample-src://" + sampleInfoProperty.get().ensemblePath;
}
@Override public Node getNode() {
return this;
}
}
