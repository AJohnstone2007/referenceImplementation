package ensemble.samplepage;
import ensemble.EnsembleApp;
import ensemble.PlatformFeatures;
import ensemble.SampleInfo;
import ensemble.SampleInfo.URL;
import ensemble.generated.Samples;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import static ensemble.samplepage.SamplePage.INDENT;
import static ensemble.samplepage.SamplePageContent.title;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
public class Description extends VBox {
private static final Image ORANGE_ARROW = new Image(EnsembleApp.class.getResource("images/orange-arrrow.png").toExternalForm());
private final SamplePage samplePage;
private final Label description;
private final VBox relatedDocumentsList;
private final VBox relatedSamples;
public Description(final SamplePage samplePage) {
this.samplePage = samplePage;
getStyleClass().add("sample-page-box");
Text descriptionTitle = title("DESCRIPTION");
description = new Label();
description.setWrapText(true);
description.setMinHeight(Label.USE_PREF_SIZE);
description.setPadding(new Insets(8, 0, 8, 0));
getChildren().addAll(descriptionTitle,description);
Hyperlink sourceBtn = new Hyperlink("VIEW SOURCE");
sourceBtn.getStyleClass().add("sample-page-box-title");
sourceBtn.setGraphic(new ImageView(ORANGE_ARROW));
sourceBtn.setContentDisplay(ContentDisplay.RIGHT);
sourceBtn.setOnAction((ActionEvent ev) -> {
samplePage.pageBrowser.goToPage(samplePage.getUrl().replaceFirst("sample://", "sample-src://"));
});
if (PlatformFeatures.LINK_TO_SOURCE) getChildren().add(sourceBtn);
if (Platform.isSupported(ConditionalFeature.WEB)) {
GridPane gridPane = new GridPane();
getChildren().add(gridPane);
gridPane.setVgap(INDENT);
gridPane.setHgap(INDENT);
ColumnConstraints leftColumn = new ColumnConstraints();
leftColumn.setPercentWidth(50);
ColumnConstraints rightColumn = new ColumnConstraints();
rightColumn.setPercentWidth(50);
gridPane.getColumnConstraints().addAll(leftColumn, rightColumn);
Text relatedDocumentsTitle = title("RELATED DOCUMENTS");
GridPane.setConstraints(relatedDocumentsTitle, 0, 0);
relatedDocumentsList = new VBox();
ScrollPane relatedDocumentsScrollPane = new ScrollPane(relatedDocumentsList);
relatedDocumentsScrollPane.setPrefSize(50, 20);
GridPane.setConstraints(relatedDocumentsScrollPane, 0, 1);
relatedDocumentsScrollPane.setFitToHeight(true);
relatedDocumentsScrollPane.setFitToWidth(true);
relatedDocumentsScrollPane.prefHeightProperty().bind(heightProperty());
relatedDocumentsScrollPane.getStyleClass().clear();
Text relatedSamplesTitle = title("RELATED SAMPLES");
GridPane.setConstraints(relatedSamplesTitle, 1, 0);
relatedSamples = new VBox();
GridPane.setConstraints(relatedSamples, 1, 1);
gridPane.getChildren().addAll(
relatedDocumentsTitle,
relatedDocumentsScrollPane,
relatedSamplesTitle,
relatedSamples);
} else {
Text relatedSamplesTitle = title("RELATED SAMPLES");
relatedSamples = new VBox();
getChildren().addAll(relatedSamplesTitle, relatedSamples);
relatedDocumentsList = null;
}
samplePage.registerSampleInfoUpdater((SampleInfo sampleInfo) -> {
update(sampleInfo);
return null;
});
}
private void update(SampleInfo sampleInfo) {
if (PlatformFeatures.WEB_SUPPORTED) {
relatedDocumentsList.getChildren().clear();
for (final URL docUrl : sampleInfo.getDocURLs()) {
Hyperlink link = new Hyperlink(docUrl.getName());
link.setOnAction((ActionEvent ev) -> {
samplePage.pageBrowser.goToPage(docUrl.getURL());
});
link.setTooltip(new Tooltip(docUrl.getName()));
relatedDocumentsList.getChildren().add(link);
}
for (final String classpath : sampleInfo.apiClasspaths) {
Hyperlink link = new Hyperlink(classpath);
link.setOnAction((ActionEvent ev) -> {
samplePage.pageBrowser.goToPage(samplePage.apiClassToUrl(classpath));
});
relatedDocumentsList.getChildren().add(link);
}
}
relatedSamples.getChildren().clear();
for (final SampleInfo.URL sampleURL : sampleInfo.getRelatedSampleURLs()) {
if (Samples.ROOT.sampleForPath(sampleURL.getURL()) != null) {
Hyperlink sampleLink = new Hyperlink(sampleURL.getName());
sampleLink.setOnAction((ActionEvent t) -> {
samplePage.pageBrowser.goToPage("sample://" + sampleURL.getURL());
});
sampleLink.setPrefWidth(1000);
relatedSamples.getChildren().add(sampleLink);
}
}
description.setText(sampleInfo.description);
}
}
