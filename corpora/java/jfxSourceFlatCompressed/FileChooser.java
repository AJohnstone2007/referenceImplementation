package javafx.stage;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.sun.glass.ui.CommonDialogs;
import com.sun.glass.ui.CommonDialogs.FileChooserResult;
import com.sun.javafx.tk.FileChooserType;
import com.sun.javafx.tk.Toolkit;
public final class FileChooser {
public static final class ExtensionFilter {
private final String description;
private final List<String> extensions;
public ExtensionFilter(final String description,
final String... extensions) {
validateArgs(description, extensions);
this.description = description;
this.extensions = Collections.unmodifiableList(
Arrays.asList(extensions.clone()));
}
public ExtensionFilter(final String description,
final List<String> extensions) {
final String[] extensionsArray =
(extensions != null) ? extensions.toArray(
new String[extensions.size()])
: null;
validateArgs(description, extensionsArray);
this.description = description;
this.extensions = Collections.unmodifiableList(
Arrays.asList(extensionsArray));
}
public String getDescription() {
return description;
}
public List<String> getExtensions() {
return extensions;
}
private static void validateArgs(final String description,
final String[] extensions) {
if (description == null) {
throw new NullPointerException("Description must not be null");
}
if (description.isEmpty()) {
throw new IllegalArgumentException(
"Description must not be empty");
}
if (extensions == null) {
throw new NullPointerException("Extensions must not be null");
}
if (extensions.length == 0) {
throw new IllegalArgumentException(
"At least one extension must be defined");
}
for (String extension : extensions) {
if (extension == null) {
throw new NullPointerException(
"Extension must not be null");
}
if (extension.isEmpty()) {
throw new IllegalArgumentException(
"Extension must not be empty");
}
}
}
}
private StringProperty title;
public FileChooser() {
}
public final void setTitle(final String value) {
titleProperty().set(value);
}
public final String getTitle() {
return (title != null) ? title.get() : null;
}
public final StringProperty titleProperty() {
if (title == null) {
title = new SimpleStringProperty(this, "title");
}
return title;
}
private ObjectProperty<File> initialDirectory;
public final void setInitialDirectory(final File value) {
initialDirectoryProperty().set(value);
}
public final File getInitialDirectory() {
return (initialDirectory != null) ? initialDirectory.get() : null;
}
public final ObjectProperty<File> initialDirectoryProperty() {
if (initialDirectory == null) {
initialDirectory =
new SimpleObjectProperty<File>(this, "initialDirectory");
}
return initialDirectory;
}
private ObjectProperty<String> initialFileName;
public final void setInitialFileName(final String value) {
initialFileNameProperty().set(value);
}
public final String getInitialFileName() {
return (initialFileName != null) ? initialFileName.get() : null;
}
public final ObjectProperty<String> initialFileNameProperty() {
if (initialFileName == null) {
initialFileName =
new SimpleObjectProperty<String>(this, "initialFileName");
}
return initialFileName;
}
private ObservableList<ExtensionFilter> extensionFilters =
FXCollections.<ExtensionFilter>observableArrayList();
public ObservableList<ExtensionFilter> getExtensionFilters() {
return extensionFilters;
}
private ObjectProperty<ExtensionFilter> selectedExtensionFilter;
public final ObjectProperty<ExtensionFilter> selectedExtensionFilterProperty() {
if (selectedExtensionFilter == null) {
selectedExtensionFilter =
new SimpleObjectProperty<ExtensionFilter>(this,
"selectedExtensionFilter");
}
return selectedExtensionFilter;
}
public final void setSelectedExtensionFilter(ExtensionFilter filter) {
selectedExtensionFilterProperty().setValue(filter);
}
public final ExtensionFilter getSelectedExtensionFilter() {
return (selectedExtensionFilter != null)
? selectedExtensionFilter.get()
: null;
}
public File showOpenDialog(final Window ownerWindow) {
final List<File> selectedFiles =
showDialog(ownerWindow, FileChooserType.OPEN);
return ((selectedFiles != null) && (selectedFiles.size() > 0))
? selectedFiles.get(0) : null;
}
public List<File> showOpenMultipleDialog(final Window ownerWindow) {
final List<File> selectedFiles =
showDialog(ownerWindow, FileChooserType.OPEN_MULTIPLE);
return ((selectedFiles != null) && (selectedFiles.size() > 0))
? Collections.unmodifiableList(selectedFiles)
: null;
}
public File showSaveDialog(final Window ownerWindow) {
final List<File> selectedFiles =
showDialog(ownerWindow, FileChooserType.SAVE);
return ((selectedFiles != null) && (selectedFiles.size() > 0))
? selectedFiles.get(0) : null;
}
private ExtensionFilter findSelectedFilter(CommonDialogs.ExtensionFilter filter) {
if (filter != null) {
String description = filter.getDescription();
List<String> extensions = filter.getExtensions();
for (ExtensionFilter ef : extensionFilters) {
if (description.equals(ef.getDescription())
&& extensions.equals(ef.getExtensions())) {
return ef;
}
}
}
return null;
}
private List<File> showDialog(final Window ownerWindow,
final FileChooserType fileChooserType) {
FileChooserResult result = Toolkit.getToolkit().showFileChooser(
(ownerWindow != null) ? ownerWindow.getPeer() : null,
getTitle(),
getInitialDirectory(),
getInitialFileName(),
fileChooserType,
extensionFilters,
getSelectedExtensionFilter());
if (result == null) {
return null;
}
List<File> files = result.getFiles();
if (files != null && files.size() > 0) {
selectedExtensionFilterProperty().set(
findSelectedFilter(result.getExtensionFilter()));
}
return files;
}
}
