package ensemble;
import ensemble.control.Popover;
import ensemble.control.SearchBox;
import ensemble.control.TitledToolBar;
import ensemble.generated.Samples;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;
public class EnsembleApp extends Application {
private static final String OS_NAME = System.getProperty("ensemble.os.name", System.getProperty("os.name"));
private static final String OS_ARCH = System.getProperty("ensemble.os.arch", System.getProperty("os.arch"));
public static final boolean IS_IPHONE = false;
public static final boolean IS_IOS = "iOS".equals(OS_NAME);
public static final boolean IS_ANDROID = "android".equals(System.getProperty("javafx.platform")) || "Dalvik".equals(System.getProperty("java.vm.name"));
public static final boolean IS_EMBEDDED = "arm".equals(OS_ARCH) && !IS_IOS && !IS_ANDROID;
public static final boolean IS_DESKTOP = !IS_EMBEDDED && !IS_IOS && !IS_ANDROID;
public static final boolean IS_MAC = OS_NAME.startsWith("Mac");
public static final boolean PRELOAD_PREVIEW_IMAGES = true;
public static final boolean SHOW_HIGHLIGHTS = IS_DESKTOP;
public static final boolean SHOW_MENU = Boolean.getBoolean("ensemble.menu.show");
public static final boolean SELECT_IOS_THEME = false;
private static final int TOOL_BAR_BUTTON_SIZE = 30;
private Scene scene;
private Pane root;
private TitledToolBar toolBar;
private Button backButton;
private Button forwardButton;
private Button homeButton;
private ToggleButton listButton;
private ToggleButton searchButton;
private final SearchBox searchBox = new SearchBox();
private PageBrowser pageBrowser;
private Popover sampleListPopover;
private SearchPopover searchPopover;
private MenuBar menuBar;
static {
System.setProperty("java.net.useSystemProxies", "true");
Logger.getLogger(EnsembleApp.class.getName()).finer("IS_IPHONE = " + IS_IPHONE);
Logger.getLogger(EnsembleApp.class.getName()).finer("IS_MAC = " + IS_MAC);
Logger.getLogger(EnsembleApp.class.getName()).finer("IS_IOS = " + IS_IOS);
Logger.getLogger(EnsembleApp.class.getName()).finer("IS_ANDROID = " + IS_ANDROID);
Logger.getLogger(EnsembleApp.class.getName()).finer("IS_EMBEDDED = " + IS_EMBEDDED);
Logger.getLogger(EnsembleApp.class.getName()).finer("IS_DESKTOP = " + IS_DESKTOP);
}
@Override public void init() throws Exception {
root = new Pane() {
@Override protected void layoutChildren() {
super.layoutChildren();
final double w = getWidth();
final double h = getHeight();
final double menuHeight = SHOW_MENU ? menuBar.prefHeight(w) : 0;
final double toolBarHeight = toolBar.prefHeight(w);
if (menuBar != null) {
menuBar.resize(w, menuHeight);
}
toolBar.resizeRelocate(0, menuHeight, w, toolBarHeight);
pageBrowser.setLayoutY(toolBarHeight + menuHeight);
pageBrowser.resize(w, h-toolBarHeight);
pageBrowser.resize(w, h - toolBarHeight - menuHeight);
sampleListPopover.autosize();
Point2D listBtnBottomCenter = listButton.localToScene(listButton.getWidth()/2, listButton.getHeight());
sampleListPopover.setLayoutX((int)listBtnBottomCenter.getX()-50);
sampleListPopover.setLayoutY((int)listBtnBottomCenter.getY()+20);
Point2D searchBoxBottomCenter = searchBox.localToScene(searchBox.getWidth()/2, searchBox.getHeight());
searchPopover.setLayoutX((int)searchBoxBottomCenter.getX()-searchPopover.getLayoutBounds().getWidth()+50);
searchPopover.setLayoutY((int)searchBoxBottomCenter.getY()+20);
}
};
if (SHOW_MENU) {
menuBar = new MenuBar();
menuBar.setUseSystemMenuBar(true);
ToggleGroup screenSizeToggle = new ToggleGroup();
Menu menu = new Menu("Screen size");
menu.getItems().addAll(
screenSizeMenuItem("iPad Landscape", 1024, 768, false, screenSizeToggle),
screenSizeMenuItem("iPad Portrait", 768, 1024, false, screenSizeToggle),
screenSizeMenuItem("Beagleboard", 1024, 600, false, screenSizeToggle),
screenSizeMenuItem("iPad Retina Landscape", 2048, 1536, true, screenSizeToggle),
screenSizeMenuItem("iPad Retina Portrait", 1536, 2048, true, screenSizeToggle),
screenSizeMenuItem("iPhone Landscape", 480, 320, false, screenSizeToggle),
screenSizeMenuItem("iPhone Portrait", 320, 480, false, screenSizeToggle),
screenSizeMenuItem("iPhone 4 Landscape", 960, 640, true, screenSizeToggle),
screenSizeMenuItem("iPhone 4 Portrait", 640, 960, true, screenSizeToggle),
screenSizeMenuItem("iPhone 5 Landscape", 1136, 640, true, screenSizeToggle),
screenSizeMenuItem("iPhone 5 Portrait", 640, 1136, true, screenSizeToggle));
menuBar.getMenus().add(menu);
screenSizeToggle.selectToggle(screenSizeToggle.getToggles().get(0));
root.getChildren().add(menuBar);
}
toolBar = new TitledToolBar();
root.getChildren().add(toolBar);
backButton = new Button();
backButton.setId("back");
backButton.getStyleClass().add("left-pill");
backButton.setPrefSize(TOOL_BAR_BUTTON_SIZE, TOOL_BAR_BUTTON_SIZE);
forwardButton = new Button();
forwardButton.setId("forward");
forwardButton.getStyleClass().add("center-pill");
forwardButton.setPrefSize(TOOL_BAR_BUTTON_SIZE, TOOL_BAR_BUTTON_SIZE);
homeButton = new Button();
homeButton.setId("home");
homeButton.setPrefSize(TOOL_BAR_BUTTON_SIZE, TOOL_BAR_BUTTON_SIZE);
homeButton.getStyleClass().add("right-pill");
HBox navButtons = new HBox(0,backButton,forwardButton,homeButton);
listButton = new ToggleButton();
listButton.setId("list");
listButton.setPrefSize(TOOL_BAR_BUTTON_SIZE, TOOL_BAR_BUTTON_SIZE);
HBox.setMargin(listButton, new Insets(0, 0, 0, 7));
searchButton = new ToggleButton();
searchButton.setId("search");
searchButton.setPrefSize(TOOL_BAR_BUTTON_SIZE, TOOL_BAR_BUTTON_SIZE);
searchBox.setPrefWidth(200);
backButton.setGraphic(new Region());
forwardButton.setGraphic(new Region());
homeButton.setGraphic(new Region());
listButton.setGraphic(new Region());
searchButton.setGraphic(new Region());
toolBar.addLeftItems(navButtons,listButton);
toolBar.addRightItems(searchBox);
pageBrowser = new PageBrowser();
toolBar.titleTextProperty().bind(pageBrowser.currentPageTitleProperty());
root.getChildren().add(0, pageBrowser);
pageBrowser.goHome();
backButton.setOnAction((ActionEvent event) -> {
pageBrowser.backward();
});
backButton.disableProperty().bind(pageBrowser.backPossibleProperty().not());
forwardButton.setOnAction((ActionEvent event) -> {
pageBrowser.forward();
});
forwardButton.disableProperty().bind(pageBrowser.forwardPossibleProperty().not());
homeButton.setOnAction((ActionEvent event) -> {
pageBrowser.goHome();
});
homeButton.disableProperty().bind(pageBrowser.atHomeProperty());
sampleListPopover = new Popover();
sampleListPopover.setPrefWidth(440);
root.getChildren().add(sampleListPopover);
final SamplePopoverTreeList rootPage = new SamplePopoverTreeList(Samples.ROOT,pageBrowser);
listButton.setOnMouseClicked((MouseEvent e) -> {
if (sampleListPopover.isVisible()) {
sampleListPopover.hide();
} else {
sampleListPopover.clearPages();
sampleListPopover.pushPage(rootPage);
sampleListPopover.show(() -> {
listButton.setSelected(false);
});
}
});
if (IS_ANDROID) {
root.setOnKeyReleased(new EventHandler<KeyEvent>() {
private int exitCount = 0;
@Override
public void handle(KeyEvent event) {
if (event.getCode() == KeyCode.ESCAPE) {
if (sampleListPopover.isVisible()) {
sampleListPopover.hide();
event.consume();
return;
}
if (!backButton.isDisabled()) {
pageBrowser.backward();
event.consume();
return;
}
exitCount++;
if (exitCount == 2) {
System.exit(0);
}
} else {
exitCount = 0;
}
if (event.getCode() == KeyCode.CONTEXT_MENU) {
if (sampleListPopover.isVisible()) {
sampleListPopover.hide();
} else {
sampleListPopover.clearPages();
sampleListPopover.pushPage(rootPage);
sampleListPopover.show(() -> {
listButton.setSelected(false);
});
}
event.consume();
}
}
});
}
searchPopover = new SearchPopover(searchBox,pageBrowser);
root.getChildren().add(searchPopover);
}
private RadioMenuItem screenSizeMenuItem(String text, final int width, final int height, final boolean retina, ToggleGroup tg) {
RadioMenuItem radioMenuItem = new RadioMenuItem(text + " " + width + "x" + height);
radioMenuItem.setToggleGroup(tg);
radioMenuItem.setOnAction((ActionEvent t) -> {
double menuHeight = IS_IOS || IS_MAC || IS_ANDROID ? 0 : menuBar.prefHeight(width);
scene.getWindow().setWidth(width + scene.getWindow().getWidth() - scene.getWidth());
scene.getWindow().setHeight(height + menuHeight + scene.getWindow().getHeight() - scene.getHeight());
if (retina) {
Parent root = scene.getRoot();
if (root instanceof Pane) {
Group newRoot = new Group();
newRoot.setAutoSizeChildren(false);
scene.setRoot(newRoot);
newRoot.getChildren().add(root);
root.getTransforms().add(new Scale(2, 2, 0, 0));
root.resize(width/2, height/2);
} else {
root.getChildrenUnmodifiable().get(0).resize(width/2, height/2);
}
} else {
Parent root = scene.getRoot();
if (root instanceof Group) {
Pane oldRoot = (Pane)root.getChildrenUnmodifiable().get(0);
((Group)root).getChildren().clear();
oldRoot.getTransforms().clear();
scene.setRoot(oldRoot);
}
}
});
return radioMenuItem;
}
private void setStylesheets() {
final String EXTERNAL_STYLESHEET = "http://fonts.googleapis.com/css?family=Source+Sans+Pro:200,300,400,600";
scene.getStylesheets().setAll("/ensemble/EnsembleStylesCommon.css");
Thread backgroundThread = new Thread(() -> {
try {
URL url = new URL(EXTERNAL_STYLESHEET);
try (
ReadableByteChannel rbc = Channels.newChannel(url.openStream());
Reader newReader = Channels.newReader(rbc, "ISO-8859-1");
BufferedReader bufferedReader = new BufferedReader(newReader)
) {
bufferedReader.readLine();
}
Platform.runLater(() -> {
scene.getStylesheets().add(EXTERNAL_STYLESHEET);
});
}catch (MalformedURLException ex) {
Logger.getLogger(EnsembleApp.class.getName()).log(Level.FINE, "Failed to load external stylesheet", ex);
}catch (IOException ex) {
Logger.getLogger(EnsembleApp.class.getName()).log(Level.FINE, "Failed to load external stylesheet", ex);
}
}, "Trying to reach external styleshet");
backgroundThread.setDaemon(true);
backgroundThread.start();
}
@Override public void start(final Stage stage) throws Exception {
scene = new Scene(root, 1024, 768, Color.web("#f4f4f4"));
if (IS_EMBEDDED || IS_ANDROID) {
new ScrollEventSynthesizer(scene);
}
setStylesheets();
stage.setScene(scene);
if (PlatformFeatures.START_FULL_SCREEN) {
Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
stage.setX(primaryScreenBounds.getMinX());
stage.setY(primaryScreenBounds.getMinY());
stage.setWidth(primaryScreenBounds.getWidth());
stage.setHeight(primaryScreenBounds.getHeight());
}
stage.setTitle("Ensemble");
stage.show();
}
public static void main(String[] args) {
launch(args);
}
}
