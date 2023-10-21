package modena;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
public class SamplePageNavigation extends BorderPane {
private SamplePage samplePage = new SamplePage();
private ScrollPane scrollPane = new ScrollPane(samplePage);
private boolean isLocalChange = false;
private SamplePage.Section currentSection;
public SamplePageNavigation() {
scrollPane.setId("SamplePageScrollPane");
setCenter(scrollPane);
ToolBar toolBar = new ToolBar();
toolBar.setId("SamplePageToolBar");
toolBar.getStyleClass().add("bottom");
toolBar.getItems().add(new Label("Go to section:"));
final ChoiceBox<SamplePage.Section> sectionChoiceBox = new ChoiceBox<>();
sectionChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> setCurrentSection(newValue));
List<SamplePage.Section> sections = new ArrayList<>(samplePage.getSections());
Collections.sort(sections, (o1, o2) -> o1.name.compareTo(o2.name));
sectionChoiceBox.getItems().addAll(sections);
toolBar.getItems().add(sectionChoiceBox);
setBottom(toolBar);
scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
if (!isLocalChange) {
isLocalChange = true;
double posPixels = samplePage.getLayoutBounds().getHeight() * newValue.doubleValue();
posPixels -= scrollPane.getLayoutBounds().getHeight() * newValue.doubleValue();
posPixels += scrollPane.getLayoutBounds().getHeight() * 0.5;
currentSection = null;
for (SamplePage.Section section: samplePage.getSections()) {
if (section.box.getBoundsInParent().getMaxY() > posPixels ) {
currentSection = section;
break;
}
}
sectionChoiceBox.getSelectionModel().select(currentSection);
isLocalChange = false;
}
});
}
public SamplePage.Section getCurrentSection() {
return currentSection;
}
public void setCurrentSection(SamplePage.Section currentSection) {
this.currentSection = currentSection;
if (!isLocalChange) {
isLocalChange = true;
double pos = 0;
if (currentSection != null) {
double sectionBoxCenterY = currentSection.box.getBoundsInParent().getMinY()
+ (currentSection.box.getBoundsInParent().getHeight()/2);
pos -= scrollPane.getLayoutBounds().getHeight() * 0.5;
pos += scrollPane.getLayoutBounds().getHeight() * (sectionBoxCenterY / samplePage.getLayoutBounds().getHeight());
pos = sectionBoxCenterY / samplePage.getLayoutBounds().getHeight();
}
scrollPane.setVvalue(pos);
isLocalChange = false;
}
}
public SamplePage getSamplePage() {
return samplePage;
}
}
