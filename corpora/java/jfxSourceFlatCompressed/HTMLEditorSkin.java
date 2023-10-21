package javafx.scene.web;
import java.util.ResourceBundle;
import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.scene.traversal.Algorithm;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import com.sun.javafx.scene.traversal.TraversalContext;
import javafx.css.PseudoClass;
import javafx.geometry.Orientation;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.StyleableProperty;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;
import com.sun.javafx.scene.control.skin.FXVK;
import com.sun.javafx.scene.web.behavior.HTMLEditorBehavior;
import com.sun.webkit.WebPage;
import com.sun.javafx.webkit.Accessor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.ListChangeListener;
import static javafx.geometry.NodeOrientation.*;
import javafx.print.PrinterJob;
import static javafx.scene.web.HTMLEditorSkin.Command.*;
public class HTMLEditorSkin extends SkinBase<HTMLEditor> {
private GridPane gridPane;
private ToolBar toolbar1;
private ToolBar toolbar2;
private Button cutButton;
private Button copyButton;
private Button pasteButton;
private Button insertHorizontalRuleButton;
private ToggleGroup alignmentToggleGroup;
private ToggleButton alignLeftButton;
private ToggleButton alignCenterButton;
private ToggleButton alignRightButton;
private ToggleButton alignJustifyButton;
private ToggleButton bulletsButton;
private ToggleButton numbersButton;
private Button indentButton;
private Button outdentButton;
private ComboBox<String> formatComboBox;
private Map<String, String> formatStyleMap;
private Map<String, String> styleFormatMap;
private ComboBox<String> fontFamilyComboBox;
private ComboBox<String> fontSizeComboBox;
private Map<String, String> fontSizeMap;
private Map<String, String> sizeFontMap;
private ToggleButton boldButton;
private ToggleButton italicButton;
private ToggleButton underlineButton;
private ToggleButton strikethroughButton;
private ColorPicker fgColorButton;
private ColorPicker bgColorButton;
private WebView webView;
private WebPage webPage;
private ParentTraversalEngine engine;
private boolean resetToolbarState = false;
private String cachedHTMLText = "<html><head></head><body contenteditable=\"true\"></body></html>";
private ResourceBundle resources;
private boolean enableAtomicityCheck = false;
private int atomicityCount = 0;
private boolean isFirstRun = true;
private static final int FONT_FAMILY_MENUBUTTON_WIDTH = 150;
private static final int FONT_FAMILY_MENU_WIDTH = 100;
private static final int FONT_SIZE_MENUBUTTON_WIDTH = 80;
private static final Color DEFAULT_BG_COLOR = Color.WHITE;
private static final Color DEFAULT_FG_COLOR = Color.BLACK;
private static final String FORMAT_PARAGRAPH = "<p>";
private static final String FORMAT_HEADING_1 = "<h1>";
private static final String FORMAT_HEADING_2 = "<h2>";
private static final String FORMAT_HEADING_3 = "<h3>";
private static final String FORMAT_HEADING_4 = "<h4>";
private static final String FORMAT_HEADING_5 = "<h5>";
private static final String FORMAT_HEADING_6 = "<h6>";
private static final String SIZE_XX_SMALL = "1";
private static final String SIZE_X_SMALL = "2";
private static final String SIZE_SMALL = "3";
private static final String SIZE_MEDIUM = "4";
private static final String SIZE_LARGE = "5";
private static final String SIZE_X_LARGE = "6";
private static final String SIZE_XX_LARGE = "7";
private static final String[][] DEFAULT_FORMAT_MAPPINGS = {
{ FORMAT_PARAGRAPH, "", SIZE_SMALL },
{ FORMAT_HEADING_1, BOLD.getCommand(), SIZE_X_LARGE },
{ FORMAT_HEADING_2, BOLD.getCommand(), SIZE_LARGE },
{ FORMAT_HEADING_3, BOLD.getCommand(), SIZE_MEDIUM },
{ FORMAT_HEADING_4, BOLD.getCommand(), SIZE_SMALL },
{ FORMAT_HEADING_5, BOLD.getCommand(), SIZE_X_SMALL },
{ FORMAT_HEADING_6, BOLD.getCommand(), SIZE_XX_SMALL },
};
private static PseudoClass CONTAINS_FOCUS_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("contains-focus");
private ListChangeListener<Node> itemsListener = c -> {
while (c.next()) {
if (c.getRemovedSize() > 0) {
for (Node n : c.getList()) {
if (n instanceof WebView) {
webPage.dispose();
}
}
}
}
};
public HTMLEditorSkin(HTMLEditor control) {
super(control);
HTMLEditorBehavior behavior = new HTMLEditorBehavior(control);
getChildren().clear();
gridPane = new GridPane();
gridPane.getStyleClass().add("grid");
getChildren().addAll(gridPane);
toolbar1 = new ToolBar();
toolbar1.getStyleClass().add("top-toolbar");
gridPane.add(toolbar1, 0, 0);
toolbar2 = new ToolBar();
toolbar2.getStyleClass().add("bottom-toolbar");
gridPane.add(toolbar2, 0, 1);
webView = new WebView();
gridPane.add(webView, 0, 2);
ColumnConstraints column = new ColumnConstraints();
column.setHgrow(Priority.ALWAYS);
gridPane.getColumnConstraints().add(column);
webPage = Accessor.getPageFor(webView.getEngine());
webView.addEventHandler(MouseEvent.MOUSE_RELEASED, event2 -> {
Platform.runLater(new Runnable() {
@Override public void run() {
enableAtomicityCheck = true;
updateToolbarState(true);
enableAtomicityCheck = false;
}
});
});
webView.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
applyTextFormatting();
if (event.getCode() == KeyCode.CONTROL || event.getCode() == KeyCode.META) {
return;
}
if (event.getCode() == KeyCode.TAB && !event.isControlDown()) {
if (!event.isShiftDown()) {
if (getCommandState(BULLETS.getCommand()) || getCommandState(NUMBERS.getCommand())) {
executeCommand(INDENT.getCommand(), null);
}
else {
executeCommand(INSERT_TAB.getCommand(), null);
}
}
else {
if (getCommandState(BULLETS.getCommand()) || getCommandState(NUMBERS.getCommand())) {
executeCommand(OUTDENT.getCommand(), null);
}
}
return;
}
if ((fgColorButton != null && fgColorButton.isShowing()) ||
(bgColorButton != null && bgColorButton.isShowing())) {
return;
}
Platform.runLater(() -> {
if (webPage.getClientSelectedText().isEmpty()) {
if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN ||
event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT ||
event.getCode() == KeyCode.HOME || event.getCode() == KeyCode.END) {
updateToolbarState(true);
} else if (event.isControlDown() || event.isMetaDown()) {
if (event.getCode() == KeyCode.B) {
performCommand(BOLD);
} else if (event.getCode() == KeyCode.I) {
performCommand(ITALIC);
} else if (event.getCode() == KeyCode.U) {
performCommand(UNDERLINE);
}
updateToolbarState(true);
} else {
resetToolbarState = event.getCode() == KeyCode.ENTER;
if (resetToolbarState) {
if (getCommandState(BOLD.getCommand()) != boldButton.selectedProperty().getValue()) {
executeCommand(BOLD.getCommand(), boldButton.selectedProperty().getValue().toString());
}
}
updateToolbarState(false);
}
resetToolbarState = false;
} else if (event.isShiftDown() &&
(event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN ||
event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT ||
event.getCode() == KeyCode.HOME || event.getCode() == KeyCode.END)) {
enableAtomicityCheck = true;
updateToolbarState(true);
enableAtomicityCheck = false;
} else if ((event.isControlDown() || event.isMetaDown()) &&
event.getCode() == KeyCode.A) {
enableAtomicityCheck = true;
updateToolbarState(true);
enableAtomicityCheck = false;
}
});
});
webView.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
if (event.getCode() == KeyCode.CONTROL || event.getCode() == KeyCode.META) {
return;
}
if ((fgColorButton != null && fgColorButton.isShowing()) ||
(bgColorButton != null && bgColorButton.isShowing())) {
return;
}
Platform.runLater(() -> {
if (webPage.getClientSelectedText().isEmpty()) {
if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN ||
event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT ||
event.getCode() == KeyCode.HOME || event.getCode() == KeyCode.END) {
updateToolbarState(true);
} else if (event.isControlDown() || event.isMetaDown()) {
if (event.getCode() == KeyCode.B) {
performCommand(BOLD);
} else if (event.getCode() == KeyCode.I) {
performCommand(ITALIC);
} else if (event.getCode() == KeyCode.U) {
performCommand(UNDERLINE);
}
updateToolbarState(true);
} else {
resetToolbarState = event.getCode() == KeyCode.ENTER;
if (!resetToolbarState) {
updateToolbarState(false);
}
}
resetToolbarState = false;
}
});
});
getSkinnable().focusedProperty().addListener((observable, oldValue, newValue) -> {
Platform.runLater(new Runnable() {
@Override public void run() {
if (newValue) {
webView.requestFocus();
}
}
});
});
webView.focusedProperty().addListener((observable, oldValue, newValue) -> {
pseudoClassStateChanged(CONTAINS_FOCUS_PSEUDOCLASS_STATE, newValue);
Platform.runLater(new Runnable() {
@Override public void run() {
updateToolbarState(true);
if (PlatformImpl.isSupported(ConditionalFeature.VIRTUAL_KEYBOARD)) {
Scene scene = getSkinnable().getScene();
if (newValue) {
FXVK.attach(webView);
} else if (scene == null ||
scene.getWindow() == null ||
!scene.getWindow().isFocused() ||
!(scene.getFocusOwner() instanceof TextInputControl
)) {
FXVK.detach();
}
}
}
});
});
webView.getEngine().getLoadWorker().workDoneProperty().addListener((observable, oldValue, newValue) -> {
Platform.runLater(() -> {
webView.requestLayout();
});
double totalWork = webView.getEngine().getLoadWorker().getTotalWork();
if (newValue.doubleValue() == totalWork) {
cachedHTMLText = null;
Platform.runLater(() -> {
setContentEditable(true);
updateToolbarState(true);
updateNodeOrientation();
executeCommand(STYLEWITHCSS.getCommand(), "true");
});
}
});
enableToolbar(true);
setHTMLText(cachedHTMLText);
engine = new ParentTraversalEngine(getSkinnable(), new Algorithm() {
@Override
public Node select(Node owner, Direction dir, TraversalContext context) {
return cutButton;
}
@Override
public Node selectFirst(TraversalContext context) {
return cutButton;
}
@Override
public Node selectLast(TraversalContext context) {
return cutButton;
}
});
ParentHelper.setTraversalEngine(getSkinnable(), engine);
webView.setFocusTraversable(true);
gridPane.getChildren().addListener(itemsListener);
}
public void performCommand(final Command command) {
switch (command) {
case BOLD: boldButton.fire(); break;
case ITALIC: italicButton.setSelected(!italicButton.isSelected()); break;
case UNDERLINE: underlineButton.setSelected(!underlineButton.isSelected()); break;
}
}
@Override protected void layoutChildren(final double x, final double y,
final double w, final double h) {
if (isFirstRun) {
populateToolbars();
isFirstRun = false;
}
super.layoutChildren(x,y,w,h);
double toolbarWidth = Math.max(toolbar1.prefWidth(-1), toolbar2.prefWidth(-1));
toolbar1.setMinWidth(toolbarWidth);
toolbar1.setPrefWidth(toolbarWidth);
toolbar2.setMinWidth(toolbarWidth);
toolbar2.setPrefWidth(toolbarWidth);
}
final String getHTMLText() {
return cachedHTMLText != null ? cachedHTMLText : webPage.getHtml(webPage.getMainFrame());
}
final void setHTMLText(String htmlText) {
cachedHTMLText = htmlText;
webPage.load(webPage.getMainFrame(), htmlText, "text/html");
Platform.runLater(() -> {
updateToolbarState(true);
});
}
private void populateToolbars() {
resources = ResourceBundle.getBundle(HTMLEditorSkin.class.getName());
cutButton = addButton(toolbar1, resources.getString("cutIcon"), resources.getString("cut"), CUT.getCommand(), "html-editor-cut");
copyButton = addButton(toolbar1, resources.getString("copyIcon"), resources.getString("copy"), COPY.getCommand(), "html-editor-copy");
pasteButton = addButton(toolbar1, resources.getString("pasteIcon"), resources.getString("paste"), PASTE.getCommand(), "html-editor-paste");
toolbar1.getItems().add(new Separator(Orientation.VERTICAL));
alignmentToggleGroup = new ToggleGroup();
alignLeftButton = addToggleButton(toolbar1, alignmentToggleGroup,
resources.getString("alignLeftIcon"), resources.getString("alignLeft"), ALIGN_LEFT.getCommand(), "html-editor-align-left");
alignCenterButton = addToggleButton(toolbar1, alignmentToggleGroup,
resources.getString("alignCenterIcon"), resources.getString("alignCenter"), ALIGN_CENTER.getCommand(), "html-editor-align-center");
alignRightButton = addToggleButton(toolbar1, alignmentToggleGroup,
resources.getString("alignRightIcon"), resources.getString("alignRight"), ALIGN_RIGHT.getCommand(), "html-editor-align-right");
alignJustifyButton = addToggleButton(toolbar1, alignmentToggleGroup,
resources.getString("alignJustifyIcon"), resources.getString("alignJustify"), ALIGN_JUSTIFY.getCommand(), "html-editor-align-justify");
toolbar1.getItems().add(new Separator(Orientation.VERTICAL));
outdentButton = addButton(toolbar1, resources.getString("outdentIcon"), resources.getString("outdent"), OUTDENT.getCommand(), "html-editor-outdent");
if (outdentButton.getGraphic() != null) outdentButton.getGraphic().setNodeOrientation(NodeOrientation.INHERIT);
indentButton = addButton(toolbar1, resources.getString("indentIcon"), resources.getString("indent"), INDENT.getCommand(), "html-editor-indent");
if (indentButton.getGraphic() != null) indentButton.getGraphic().setNodeOrientation(NodeOrientation.INHERIT);
toolbar1.getItems().add(new Separator(Orientation.VERTICAL));
ToggleGroup listStyleToggleGroup = new ToggleGroup();
bulletsButton = addToggleButton(toolbar1, listStyleToggleGroup,
resources.getString("bulletsIcon"), resources.getString("bullets"), BULLETS.getCommand(), "html-editor-bullets");
if (bulletsButton.getGraphic() != null) bulletsButton.getGraphic().setNodeOrientation(NodeOrientation.INHERIT);
numbersButton = addToggleButton(toolbar1, listStyleToggleGroup,
resources.getString("numbersIcon"), resources.getString("numbers"), NUMBERS.getCommand(), "html-editor-numbers");
toolbar1.getItems().add(new Separator(Orientation.VERTICAL));
formatComboBox = new ComboBox<String>();
formatComboBox.getStyleClass().add("font-menu-button");
formatComboBox.setFocusTraversable(false);
formatComboBox.setMinWidth(Region.USE_PREF_SIZE);
toolbar2.getItems().add(formatComboBox);
formatStyleMap = new HashMap<String, String>();
styleFormatMap = new HashMap<String, String>();
createFormatMenuItem(FORMAT_PARAGRAPH, resources.getString("paragraph"));
Platform.runLater(() -> {
formatComboBox.setValue(resources.getString("paragraph"));
});
createFormatMenuItem(FORMAT_HEADING_1, resources.getString("heading1"));
createFormatMenuItem(FORMAT_HEADING_2, resources.getString("heading2"));
createFormatMenuItem(FORMAT_HEADING_3, resources.getString("heading3"));
createFormatMenuItem(FORMAT_HEADING_4, resources.getString("heading4"));
createFormatMenuItem(FORMAT_HEADING_5, resources.getString("heading5"));
createFormatMenuItem(FORMAT_HEADING_6, resources.getString("heading6"));
formatComboBox.setTooltip(new Tooltip(resources.getString("format")));
formatComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
if (newValue == null) {
formatComboBox.setValue(null);
} else {
String formatValue = formatStyleMap.get(newValue);
executeCommand(FORMAT.getCommand(), formatValue);
updateToolbarState(false);
for (int i = 0; i < DEFAULT_FORMAT_MAPPINGS.length; i++) {
String[] mapping = DEFAULT_FORMAT_MAPPINGS[i];
if (mapping[0].equalsIgnoreCase(formatValue)) {
executeCommand(FONT_SIZE.getCommand(), mapping[2]);
updateToolbarState(false);
break;
}
}
}
});
fontFamilyComboBox = new ComboBox<String>();
fontFamilyComboBox.getStyleClass().add("font-menu-button");
fontFamilyComboBox.setMinWidth(FONT_FAMILY_MENUBUTTON_WIDTH);
fontFamilyComboBox.setPrefWidth(FONT_FAMILY_MENUBUTTON_WIDTH);
fontFamilyComboBox.setMaxWidth(FONT_FAMILY_MENUBUTTON_WIDTH);
fontFamilyComboBox.setFocusTraversable(false);
fontFamilyComboBox.setTooltip(new Tooltip(resources.getString("fontFamily")));
toolbar2.getItems().add(fontFamilyComboBox);
fontFamilyComboBox.getProperties().put("comboBoxRowsToMeasureWidth", 0);
fontFamilyComboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
@Override public ListCell<String> call(ListView<String> param) {
final ListCell<String> cell = new ListCell<String>() {
@Override public void updateItem(String item, boolean empty) {
super.updateItem(item, empty);
if (item != null) {
setText(item);
setFont(new Font(item, 12));
}
}
};
cell.setMinWidth(FONT_FAMILY_MENU_WIDTH);
cell.setPrefWidth(FONT_FAMILY_MENU_WIDTH);
cell.setMaxWidth(FONT_FAMILY_MENU_WIDTH);
return cell;
}
});
Platform.runLater(() -> {
final ObservableList<String> fonts = FXCollections.observableArrayList(Font.getFamilies());
fonts.add(0, "");
for (String fontFamily : fonts) {
fontFamilyComboBox.setValue("");
fontFamilyComboBox.setItems(fonts);
}
});
fontFamilyComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
executeCommand(FONT_FAMILY.getCommand(), "'" + newValue + "'");
});
fontSizeComboBox = new ComboBox<String>();
fontSizeComboBox.getStyleClass().add("font-menu-button");
fontSizeComboBox.setFocusTraversable(false);
toolbar2.getItems().add(fontSizeComboBox);
fontSizeMap = new HashMap<String, String>();
sizeFontMap = new HashMap<String, String>();
createFontSizeMenuItem(SIZE_XX_SMALL, resources.getString("extraExtraSmall"));
createFontSizeMenuItem(SIZE_X_SMALL, resources.getString("extraSmall"));
createFontSizeMenuItem(SIZE_SMALL, resources.getString("small"));
Platform.runLater(() -> {
fontSizeComboBox.setValue(resources.getString("small"));
});
createFontSizeMenuItem(SIZE_MEDIUM, resources.getString("medium"));
createFontSizeMenuItem(SIZE_LARGE, resources.getString("large"));
createFontSizeMenuItem(SIZE_X_LARGE, resources.getString("extraLarge"));
createFontSizeMenuItem(SIZE_XX_LARGE, resources.getString("extraExtraLarge"));
fontSizeComboBox.setTooltip(new Tooltip(resources.getString("fontSize")));
fontSizeComboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
@Override public ListCell<String> call(ListView<String> param) {
final ListCell<String> cell = new ListCell<String>() {
@Override public void updateItem(String item, boolean empty) {
super.updateItem(item, empty);
if (item != null) {
setText(item);
String size = item.replaceFirst("[^0-9.].*$", "");
setFont(new Font((String)fontFamilyComboBox.getValue(), Double.valueOf(size)));
}
}
};
return cell;
}
});
fontSizeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
Object fontSizeValue = getCommandValue(FONT_SIZE.getCommand());
if (!newValue.equals(fontSizeValue)) {
executeCommand(FONT_SIZE.getCommand(), fontSizeMap.get(newValue));
}
});
toolbar2.getItems().add(new Separator(Orientation.VERTICAL));
boldButton = addToggleButton(toolbar2, null,
resources.getString("boldIcon"), resources.getString("bold"), BOLD.getCommand(), "html-editor-bold");
boldButton.setOnAction(event1 -> {
if ("<p>".equals(formatStyleMap.get(formatComboBox.getValue()))) {
executeCommand(BOLD.getCommand(), boldButton.selectedProperty().getValue().toString());
}
});
italicButton = addToggleButton(toolbar2, null,
resources.getString("italicIcon"), resources.getString("italic"), ITALIC.getCommand(), "html-editor-italic");
underlineButton = addToggleButton(toolbar2, null,
resources.getString("underlineIcon"), resources.getString("underline"), UNDERLINE.getCommand(), "html-editor-underline");
strikethroughButton = addToggleButton(toolbar2, null,
resources.getString("strikethroughIcon"), resources.getString("strikethrough"), STRIKETHROUGH.getCommand(), "html-editor-strike");
toolbar2.getItems().add(new Separator(Orientation.VERTICAL));
insertHorizontalRuleButton = addButton(toolbar2, resources.getString("insertHorizontalRuleIcon"),
resources.getString("insertHorizontalRule"), INSERT_HORIZONTAL_RULE.getCommand(), "html-editor-hr");
insertHorizontalRuleButton.setOnAction(event -> {
executeCommand(INSERT_NEW_LINE.getCommand(), null);
executeCommand(INSERT_HORIZONTAL_RULE.getCommand(), null);
updateToolbarState(false);
});
fgColorButton = new ColorPicker();
fgColorButton.getStyleClass().add("html-editor-foreground");
fgColorButton.setFocusTraversable(false);
toolbar1.getItems().add(fgColorButton);
fgColorButton.setValue(DEFAULT_FG_COLOR);
fgColorButton.setTooltip(new Tooltip(resources.getString("foregroundColor")));
fgColorButton.setOnAction(ev1 -> {
Color newValue = fgColorButton.getValue();
if (newValue != null) {
executeCommand(FOREGROUND_COLOR.getCommand(), colorValueToRGBA(newValue));
fgColorButton.hide();
}
});
bgColorButton = new ColorPicker();
bgColorButton.getStyleClass().add("html-editor-background");
bgColorButton.setFocusTraversable(false);
toolbar1.getItems().add(bgColorButton);
bgColorButton.setValue(DEFAULT_BG_COLOR);
bgColorButton.setTooltip(new Tooltip(resources.getString("backgroundColor")));
bgColorButton.setOnAction(ev -> {
Color newValue = bgColorButton.getValue();
if (newValue != null) {
executeCommand(BACKGROUND_COLOR.getCommand(), colorValueToRGBA(newValue));
bgColorButton.hide();
}
});
}
private String colorValueToRGBA(Color c) {
return String.format((Locale)null, "rgba(%d, %d, %d, %.5f)",
Math.round(c.getRed() * 255),
Math.round(c.getGreen() * 255),
Math.round(c.getBlue() * 255),
c.getOpacity());
}
private Button addButton(ToolBar toolbar, final String iconName, String tooltipText,
final String command, final String styleClass) {
Button button = new Button();
button.setFocusTraversable(false);
button.getStyleClass().add(styleClass);
toolbar.getItems().add(button);
@SuppressWarnings("removal")
Image icon = AccessController.doPrivileged((PrivilegedAction<Image>) () -> new Image(HTMLEditorSkin.class.getResource(iconName).toString()));
((StyleableProperty)button.graphicProperty()).applyStyle(null, new ImageView(icon));
button.setTooltip(new Tooltip(tooltipText));
button.setOnAction(event -> {
executeCommand(command, null);
updateToolbarState(false);
});
return button;
}
private ToggleButton addToggleButton(ToolBar toolbar, ToggleGroup toggleGroup,
final String iconName, String tooltipText, final String command, final String styleClass) {
ToggleButton toggleButton = new ToggleButton();
toggleButton.setUserData(command);
toggleButton.setFocusTraversable(false);
toggleButton.getStyleClass().add(styleClass);
toolbar.getItems().add(toggleButton);
if (toggleGroup != null) {
toggleButton.setToggleGroup(toggleGroup);
}
@SuppressWarnings("removal")
Image icon = AccessController.doPrivileged((PrivilegedAction<Image>) () -> new Image(HTMLEditorSkin.class.getResource(iconName).toString()));
((StyleableProperty)toggleButton.graphicProperty()).applyStyle(null, new ImageView(icon));
toggleButton.setTooltip(new Tooltip(tooltipText));
if (!BOLD.getCommand().equals(command)) {
toggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
if (getCommandState(command) != newValue.booleanValue()) {
executeCommand(command, null);
}
});
}
return toggleButton;
}
private void createFormatMenuItem(String formatValue, String label) {
formatComboBox.getItems().add(label);
formatStyleMap.put(label, formatValue);
styleFormatMap.put(formatValue, label);
}
private void createFontSizeMenuItem(String fontSizeValue, String label) {
fontSizeComboBox.getItems().add(label);
fontSizeMap.put(label, fontSizeValue);
sizeFontMap.put(fontSizeValue, label);
}
private void updateNodeOrientation() {
NodeOrientation orientation = getSkinnable().getEffectiveNodeOrientation();
HTMLDocument htmlDocument = (HTMLDocument)webPage.getDocument(webPage.getMainFrame());
HTMLElement htmlDocumentElement = (HTMLElement)htmlDocument.getDocumentElement();
if (htmlDocumentElement.getAttribute("dir") == null) {
htmlDocumentElement.setAttribute("dir", (orientation == RIGHT_TO_LEFT) ? "rtl" : "ltr");
}
}
private void updateToolbarState(final boolean updateAlignment) {
if (!webView.isFocused()) {
return;
}
atomicityCount++;
copyButton.setDisable(!isCommandEnabled(CUT.getCommand()));
cutButton.setDisable(!isCommandEnabled(COPY.getCommand()));
pasteButton.setDisable(!isCommandEnabled(PASTE.getCommand()));
insertHorizontalRuleButton.setDisable(!isCommandEnabled(INSERT_HORIZONTAL_RULE.getCommand()));
if (updateAlignment) {
alignLeftButton.setDisable(!isCommandEnabled(ALIGN_LEFT.getCommand()));
alignLeftButton.setSelected(getCommandState(ALIGN_LEFT.getCommand()));
alignCenterButton.setDisable(!isCommandEnabled(ALIGN_CENTER.getCommand()));
alignCenterButton.setSelected(getCommandState(ALIGN_CENTER.getCommand()));
alignRightButton.setDisable(!isCommandEnabled(ALIGN_RIGHT.getCommand()));
alignRightButton.setSelected(getCommandState(ALIGN_RIGHT.getCommand()));
alignJustifyButton.setDisable(!isCommandEnabled(ALIGN_JUSTIFY.getCommand()));
alignJustifyButton.setSelected(getCommandState(ALIGN_JUSTIFY.getCommand()));
} else {
if (alignmentToggleGroup.getSelectedToggle() != null) {
String command = alignmentToggleGroup.getSelectedToggle().getUserData().toString();
if (isCommandEnabled(command) && !getCommandState(command) ) {
executeCommand(command, null);
}
}
}
if (alignmentToggleGroup.getSelectedToggle() == null
&& webPage.getClientSelectedText().isEmpty()) {
alignmentToggleGroup.selectToggle(alignLeftButton);
}
bulletsButton.setDisable(!isCommandEnabled(BULLETS.getCommand()));
bulletsButton.setSelected(getCommandState(BULLETS.getCommand()));
numbersButton.setDisable(!isCommandEnabled(NUMBERS.getCommand()));
numbersButton.setSelected(getCommandState(NUMBERS.getCommand()));
indentButton.setDisable(!isCommandEnabled(INDENT.getCommand()));
outdentButton.setDisable(!isCommandEnabled(OUTDENT.getCommand()));
formatComboBox.setDisable(!isCommandEnabled(FORMAT.getCommand()));
String formatValue = getCommandValue(FORMAT.getCommand());
if (formatValue != null) {
String htmlTag = "<" + formatValue + ">";
String comboFormatValue = styleFormatMap.get(htmlTag);
String format = formatComboBox.getValue();
if ((resetToolbarState || htmlTag.equals("<>") || htmlTag.equalsIgnoreCase("<div>") || htmlTag.equalsIgnoreCase("<blockquote>"))) {
formatComboBox.setValue(resources.getString("paragraph"));
} else if (format != null && ! format.equalsIgnoreCase(comboFormatValue)) {
formatComboBox.setValue(comboFormatValue);
}
}
fontFamilyComboBox.setDisable(!isCommandEnabled(FONT_FAMILY.getCommand()));
final String fontFamilyValue = getCommandValue(FONT_FAMILY.getCommand());
if (fontFamilyValue != null) {
String fontFamilyStr = fontFamilyValue;
if (fontFamilyStr.startsWith("'")) {
fontFamilyStr = fontFamilyStr.substring(1);
}
if (fontFamilyStr.endsWith("'")) {
fontFamilyStr = fontFamilyStr.substring(0,fontFamilyStr.length() - 1);
}
Object selectedFont = fontFamilyComboBox.getValue();
if (selectedFont instanceof String) {
if (!selectedFont.equals(fontFamilyStr)) {
ObservableList<String> fontFamilyItems = fontFamilyComboBox.getItems();
String selectedComboFont = null;
for (String comboFontFamilyValue : fontFamilyItems) {
if (comboFontFamilyValue.equals(fontFamilyStr)) {
selectedComboFont = comboFontFamilyValue;
break;
}
if (comboFontFamilyValue.equals("") && fontFamilyStr.equals("Dialog")) {
selectedComboFont = comboFontFamilyValue;
break;
}
}
if (selectedComboFont != null) {
fontFamilyComboBox.setValue(selectedComboFont);
}
}
}
}
fontSizeComboBox.setDisable(!isCommandEnabled(FONT_SIZE.getCommand()));
String fontSizeValue = getCommandValue(FONT_SIZE.getCommand());
if (resetToolbarState && fontSizeValue == null) {
fontSizeComboBox.setValue(sizeFontMap.get(SIZE_SMALL));
} else {
if (fontSizeValue != null) {
if (!fontSizeComboBox.getValue().equals(sizeFontMap.get(fontSizeValue))) {
fontSizeComboBox.setValue(sizeFontMap.get(fontSizeValue));
}
}
else {
if ((fontSizeComboBox.getValue() == null) || !fontSizeComboBox.getValue().equals(sizeFontMap.get(SIZE_SMALL))) {
fontSizeComboBox.setValue(sizeFontMap.get(SIZE_SMALL));
}
}
}
boldButton.setDisable(!isCommandEnabled(BOLD.getCommand()));
boldButton.setSelected(getCommandState(BOLD.getCommand()));
italicButton.setDisable(!isCommandEnabled(ITALIC.getCommand()));
italicButton.setSelected(getCommandState(ITALIC.getCommand()));
underlineButton.setDisable(!isCommandEnabled(UNDERLINE.getCommand()));
underlineButton.setSelected(getCommandState(UNDERLINE.getCommand()));
strikethroughButton.setDisable(!isCommandEnabled(STRIKETHROUGH.getCommand()));
strikethroughButton.setSelected(getCommandState(STRIKETHROUGH.getCommand()));
fgColorButton.setDisable(!isCommandEnabled(FOREGROUND_COLOR.getCommand()));
String foregroundColorValue = getCommandValue(FOREGROUND_COLOR.getCommand());
if (foregroundColorValue != null) {
fgColorButton.setValue(getColor(foregroundColorValue));
}
bgColorButton.setDisable(!isCommandEnabled(BACKGROUND_COLOR.getCommand()));
String backgroundColorValue = getCommandValue(BACKGROUND_COLOR.getCommand());
if (backgroundColorValue != null) {
bgColorButton.setValue(getColor(backgroundColorValue));
}
atomicityCount = atomicityCount == 0 ? 0 : --atomicityCount;
}
private void enableToolbar(final boolean enable) {
Platform.runLater(() -> {
if (copyButton == null) return;
if (enable) {
copyButton.setDisable(!isCommandEnabled(COPY.getCommand()));
cutButton.setDisable(!isCommandEnabled(CUT.getCommand()));
pasteButton.setDisable(!isCommandEnabled(PASTE.getCommand()));
} else {
copyButton.setDisable(true);
cutButton.setDisable(true);
pasteButton.setDisable(true);
}
insertHorizontalRuleButton.setDisable(!enable);
alignLeftButton.setDisable(!enable);
alignCenterButton.setDisable(!enable);
alignRightButton.setDisable(!enable);
alignJustifyButton.setDisable(!enable);
bulletsButton.setDisable(!enable);
numbersButton.setDisable(!enable);
indentButton.setDisable(!enable);
outdentButton.setDisable(!enable);
formatComboBox.setDisable(!enable);
fontFamilyComboBox.setDisable(!enable);
fontSizeComboBox.setDisable(!enable);
boldButton.setDisable(!enable);
italicButton.setDisable(!enable);
underlineButton.setDisable(!enable);
strikethroughButton.setDisable(!enable);
fgColorButton.setDisable(!enable);
bgColorButton.setDisable(!enable);
});
}
private boolean executeCommand(String command, String value) {
if (!enableAtomicityCheck || (enableAtomicityCheck && atomicityCount == 0)) {
return webPage.executeCommand(command, value);
}
return false;
}
private boolean isCommandEnabled(String command) {
return webPage.queryCommandEnabled(command);
}
private void setContentEditable(boolean b) {
HTMLDocument htmlDocument = (HTMLDocument)webPage.getDocument(webPage.getMainFrame());
HTMLElement htmlDocumentElement = (HTMLElement)htmlDocument.getDocumentElement();
HTMLElement htmlBodyElement = (HTMLElement)htmlDocumentElement.getElementsByTagName("body").item(0);
htmlBodyElement.setAttribute("contenteditable", Boolean.toString(b));
}
private boolean getCommandState(String command) {
return webPage.queryCommandState(command);
}
private String getCommandValue(String command) {
return webPage.queryCommandValue(command);
}
private Color getColor(String value) {
Color color = Color.web(value);
if (color.equals(Color.TRANSPARENT)) {
color = Color.WHITE;
}
return color;
}
private void applyTextFormatting() {
if (getCommandState(BULLETS.getCommand()) || getCommandState(NUMBERS.getCommand())) {
return;
}
if (webPage.getClientCommittedTextLength() == 0) {
String format = formatStyleMap.get(formatComboBox.getValue());
String font = fontFamilyComboBox.getValue().toString();
executeCommand(FORMAT.getCommand(), format);
executeCommand(FONT_FAMILY.getCommand(), "'" + font + "'");
}
}
void print(PrinterJob job) {
webView.getEngine().print(job);
}
public enum Command {
CUT("cut"),
COPY("copy"),
PASTE("paste"),
UNDO("undo"),
REDO("redo"),
INSERT_HORIZONTAL_RULE("inserthorizontalrule"),
ALIGN_LEFT("justifyleft"),
ALIGN_CENTER("justifycenter"),
ALIGN_RIGHT("justifyright"),
ALIGN_JUSTIFY("justifyfull"),
BULLETS("insertUnorderedList"),
NUMBERS("insertOrderedList"),
INDENT("indent"),
OUTDENT("outdent"),
FORMAT("formatblock"),
FONT_FAMILY("fontname"),
FONT_SIZE("fontsize"),
BOLD("bold"),
ITALIC("italic"),
UNDERLINE("underline"),
STRIKETHROUGH("strikethrough"),
FOREGROUND_COLOR("forecolor"),
BACKGROUND_COLOR("backcolor"),
STYLEWITHCSS("styleWithCSS"),
INSERT_NEW_LINE("insertnewline"),
INSERT_TAB("inserttab");
private final String command;
Command(String command) {
this.command = command;
}
public String getCommand() {
return command;
}
}
}
