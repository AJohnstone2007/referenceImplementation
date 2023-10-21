package ensemble;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.scene.control.Skin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import ensemble.generated.Samples;
import static ensemble.EnsembleApp.SHOW_HIGHLIGHTS;
public class HomePage extends ListView<HomePage.HomePageRow> implements Callback<ListView<HomePage.HomePageRow>, ListCell<HomePage.HomePageRow>>, ChangeListener<Number>, Page{
private static final int ITEM_WIDTH = 216;
private static final int ITEM_HEIGHT = 162;
private static final int ITEM_GAP = 32;
private static final int MIN_MARGIN = 20;
private static enum RowType{Highlights,Title,Samples};
private int numberOfColumns = -1;
private final HomePageRow HIGHLIGHTS_ROW = new HomePageRow(RowType.Highlights,null,null);
private final PageBrowser pageBrowser;
private final ReadOnlyStringProperty titleProperty = new ReadOnlyStringWrapper();
public HomePage(PageBrowser pageBrowser) {
this.pageBrowser = pageBrowser;
setId("HomePage");
getStyleClass().clear();
widthProperty().addListener(this);
setCellFactory(this);
}
@Override public ReadOnlyStringProperty titleProperty() {
return titleProperty;
}
@Override public String getTitle() {
return titleProperty.get();
}
@Override public String getUrl() {
return PageBrowser.HOME_URL;
}
@Override public Node getNode() {
return this;
}
@Override public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newWidth) {
double width = newWidth.doubleValue();
width -= 60;
final int newNumOfColumns = Math.max(1, (int) (width / (ITEM_WIDTH + ITEM_GAP)));
if (numberOfColumns != newNumOfColumns) {
numberOfColumns = newNumOfColumns;
rebuild();
}
}
@Override public ListCell<HomePageRow> call(ListView<HomePageRow> homePageRowListView) {
return new HomeListCell();
}
private void rebuild() {
List<HomePageRow> newItems = new ArrayList<>();
if (SHOW_HIGHLIGHTS) {
newItems.add(HIGHLIGHTS_ROW);
}
addSampleRows(newItems,Samples.ROOT.samples);
for(SampleCategory category: Samples.ROOT.subCategories) {
newItems.add(new HomePageRow(RowType.Title,category.name,null));
addSampleRows(newItems,category.samplesAll);
}
getItems().setAll(newItems);
}
private void addSampleRows(List<HomePageRow> items, SampleInfo[] samples) {
if(samples == null) return;
for(int row = 0; row < Math.ceil((double) samples.length / numberOfColumns); row++) {
int sampleIndex = row*numberOfColumns;
SampleInfo[] sampleInfos = Arrays.copyOfRange(samples,sampleIndex, Math.min(sampleIndex+numberOfColumns,samples.length));
items.add(new HomePageRow(RowType.Samples, null, sampleInfos));
}
}
private Reference<Pagination> paginationCache;
private ImageView highlightRibbon;
private Map<String, SectionRibbon> ribbonsCache = new WeakHashMap<>();
private Map<SampleInfo, Button> buttonCache = new WeakHashMap<>();
private static int cellCount = 0;
private static final PseudoClass TITLE_PSEUDO_CLASS = PseudoClass.getPseudoClass(RowType.Title.toString());
private class HomeListCell extends ListCell<HomePageRow> implements Callback<Integer,Node>, Skin<HomeListCell> {
private static final double HIGHLIGHTS_HEIGHT = 430;
private static final double RIBBON_HEIGHT = 60;
private static final double DEFAULT_HEIGHT = 230;
private static final double DEFAULT_WIDTH = 100;
private double height = DEFAULT_HEIGHT;
int cellIndex;
private RowType oldRowType = null;
private HBox box = new HBox(ITEM_GAP);
private HomeListCell() {
super();
getStyleClass().clear();
cellIndex = cellCount++;
box.getStyleClass().add("home-page-cell");
setSkin(this);
}
@Override protected double computeMaxHeight(double d) {
return height;
}
@Override protected double computePrefHeight(double d) {
return height;
}
@Override protected double computeMinHeight(double d) {
return height;
}
@Override protected double computeMaxWidth(double height) {
return Double.MAX_VALUE;
}
@Override protected double computePrefWidth(double height) {
return DEFAULT_WIDTH;
}
@Override protected double computeMinWidth(double height) {
return DEFAULT_WIDTH;
}
@Override protected void updateItem(HomePageRow item, boolean empty) {
super.updateItem(item, empty);
box.pseudoClassStateChanged(TITLE_PSEUDO_CLASS,item !=null && item.rowType == RowType.Title);
if (item == null) {
oldRowType = null;
box.getChildren().clear();
height = DEFAULT_HEIGHT;
} else {
switch (item.rowType) {
case Highlights:
if (oldRowType != RowType.Highlights) {
height = HIGHLIGHTS_HEIGHT;
Pagination pagination = paginationCache == null ? null
: paginationCache.get();
if (pagination == null) {
pagination = new Pagination(Samples.HIGHLIGHTS.length);
pagination.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
pagination.setMaxWidth(USE_PREF_SIZE);
pagination.setMaxHeight(USE_PREF_SIZE);
pagination.setPageFactory(this);
paginationCache = new WeakReference<>(pagination);
}
if (highlightRibbon == null) {
highlightRibbon = new ImageView(new Image(getClass().getResource("images/highlights-ribbon.png").toExternalForm()));
highlightRibbon.setManaged(false);
highlightRibbon.layoutXProperty().bind(pagination.layoutXProperty().add(5));
highlightRibbon.layoutYProperty().bind(pagination.layoutYProperty().add(5));
}
box.setAlignment(Pos.CENTER);
box.getChildren().setAll(pagination, highlightRibbon);
}
break;
case Title:
height = RIBBON_HEIGHT;
SectionRibbon ribbon = ribbonsCache.get(item.title);
if (ribbon == null) {
ribbon = new SectionRibbon(item.title.toUpperCase());
ribbonsCache.put(item.title, ribbon);
}
box.getChildren().setAll(ribbon);
box.setAlignment(Pos.CENTER);
break;
case Samples:
height = DEFAULT_HEIGHT;
box.setAlignment(Pos.CENTER);
box.getChildren().clear();
for (int i = 0; i < item.samples.length; i++) {
final SampleInfo sample = item.samples[i];
Button sampleButton = buttonCache.get(sample);
if (sampleButton == null) {
sampleButton = new Button();
sampleButton.getStyleClass().setAll("sample-button");
sampleButton.setContentDisplay(ContentDisplay.TOP);
sampleButton.setText(sample.name);
sampleButton.setGraphic(sample.getMediumPreview());
sampleButton.setOnAction((ActionEvent actionEvent) -> {
pageBrowser.goToSample(sample);
});
buttonCache.put(sample, sampleButton);
}
if (sampleButton.getParent() != null) {
((HBox) sampleButton.getParent()).getChildren().remove(sampleButton);
}
box.getChildren().add(sampleButton);
}
break;
}
oldRowType = item.rowType;
}
}
@Override public HomeListCell getSkinnable() { return this; }
@Override public Node getNode() { return box; }
@Override public void dispose() {}
@Override public Node call(final Integer highlightIndex) {
Button sampleButton = new Button();
sampleButton.getStyleClass().setAll("sample-button");
sampleButton.setGraphic(Samples.HIGHLIGHTS[highlightIndex].getLargePreview());
sampleButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
sampleButton.setOnAction((ActionEvent actionEvent) -> {
pageBrowser.goToSample(Samples.HIGHLIGHTS[highlightIndex]);
});
return sampleButton;
}
}
public static class HomePageRow {
public final RowType rowType;
public final String title;
public final SampleInfo[] samples;
private HomePageRow(RowType rowType, String title, SampleInfo[] samples) {
this.rowType = rowType;
this.title = title;
this.samples = samples;
}
@Override
public String toString() {
return "HomePageRow{" + "rowType=" + rowType + ", title=" + title + ", samples=" + samples + '}';
}
}
private static class SectionRibbon extends Text {
public SectionRibbon(String text) {
super(text);
getStyleClass().add("section-ribbon-text");
}
}
}
