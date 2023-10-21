package javafx.stage;
import com.sun.javafx.tk.Toolkit;
import java.io.File;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
public final class DirectoryChooser {
private StringProperty title;
public DirectoryChooser() {
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
public File showDialog(final Window ownerWindow) {
return Toolkit.getToolkit().showDirectoryChooser(
(ownerWindow != null) ? ownerWindow.getPeer() : null,
getTitle(),
getInitialDirectory());
}
}
