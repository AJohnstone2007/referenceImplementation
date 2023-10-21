package ensemble.samplepage;
import static ensemble.samplepage.SamplePageContent.*;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
public class PlaygroundNode extends VBox {
public PlaygroundNode(SamplePage samplePage) {
PlaygroundTabs playgroundTabs = new PlaygroundTabs(samplePage);
setAlignment(Pos.TOP_LEFT);
getChildren().setAll(
title("PLAYGROUND"),
playgroundTabs);
VBox.setVgrow(playgroundTabs, Priority.ALWAYS);
getStyleClass().add("sample-page-box");
}
}
