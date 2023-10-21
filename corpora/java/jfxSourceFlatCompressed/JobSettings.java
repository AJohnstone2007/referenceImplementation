package javafx.print;
import java.util.Set;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
public final class JobSettings {
private PrinterJob job;
private Printer printer;
private PrinterAttributes printerCaps;
JobSettings(Printer printer) {
this.printer = printer;
printerCaps = printer.getPrinterAttributes();
}
void setPrinterJob(PrinterJob job) {
this.job = job;
}
private boolean isJobNew() {
return job == null || job.isJobNew();
}
private boolean defaultCopies = true;
private boolean hasOldCopies = false;
private int oldCopies;
private boolean defaultSides = true;
private boolean hasOldSides = false;
private PrintSides oldSides;
private boolean defaultCollation = true;
private boolean hasOldCollation = false;
private Collation oldCollation;
private boolean defaultPrintColor = true;
private boolean hasOldPrintColor = false;
private PrintColor oldPrintColor;
private boolean defaultPrintQuality = true;
private boolean hasOldPrintQuality = false;
private PrintQuality oldPrintQuality;
private boolean defaultPrintResolution = true;
private boolean hasOldPrintResolution = false;
private PrintResolution oldPrintResolution;
private boolean defaultPaperSource = true;
private boolean hasOldPaperSource = false;
private PaperSource oldPaperSource;
private boolean defaultPageLayout = true;
private boolean hasOldPageLayout = false;
private PageLayout oldPageLayout;
void updateForPrinter(Printer printer) {
this.printer = printer;
this.printerCaps = printer.getPrinterAttributes();
if (defaultCopies) {
if (getCopies() != printerCaps.getDefaultCopies()) {
setCopies(printerCaps.getDefaultCopies());
defaultCopies = true;
}
} else {
int copies = getCopies();
if (hasOldCopies && oldCopies > copies) {
copies = oldCopies;
}
int maxCopies = printerCaps.getMaxCopies();
if (!hasOldCopies && getCopies() > maxCopies) {
hasOldCopies = true;
oldCopies = getCopies();
}
if (copies > maxCopies) copies = maxCopies;
setCopies(copies);
}
PrintSides currSides = getPrintSides();
PrintSides defSides = printerCaps.getDefaultPrintSides();
Set<PrintSides> suppSides = printerCaps.getSupportedPrintSides();
if (defaultSides) {
if (currSides != defSides) {
setPrintSides(defSides);
defaultSides = true;
}
} else {
if (hasOldSides) {
if (suppSides.contains(oldSides)) {
setPrintSides(oldSides);
hasOldSides = false;
} else {
setPrintSides(defSides);
}
} else if (!suppSides.contains(currSides)) {
hasOldSides = true;
oldSides = currSides;
setPrintSides(defSides);
}
}
Collation currColl = getCollation();
Collation defColl = printerCaps.getDefaultCollation();
Set<Collation> suppColl = printerCaps.getSupportedCollations();
if (defaultCollation) {
if (currColl != defColl) {
setCollation(defColl);
defaultCollation = true;
}
} else {
if (hasOldCollation) {
if (suppColl.contains(oldCollation)) {
setCollation(oldCollation);
hasOldCollation = false;
} else {
setCollation(defColl);
}
} else if (!suppColl.contains(currColl)) {
hasOldCollation = true;
oldCollation = currColl;
setCollation(defColl);
}
}
PrintColor currColor = getPrintColor();
PrintColor defColor = printerCaps.getDefaultPrintColor();
Set<PrintColor> suppColors = printerCaps.getSupportedPrintColors();
if (defaultPrintColor) {
if (currColor != defColor) {
setPrintColor(defColor);
defaultPrintColor = true;
}
} else {
if (hasOldPrintColor) {
if (suppColors.contains(oldPrintColor)) {
setPrintColor(oldPrintColor);
hasOldPrintColor = false;
} else {
setPrintColor(defColor);
}
} else if (!suppColors.contains(currColor)) {
hasOldPrintColor = true;
oldPrintColor = currColor;
setPrintColor(defColor);
}
}
PrintQuality currQuality = getPrintQuality();
PrintQuality defQuality = printerCaps.getDefaultPrintQuality();
Set<PrintQuality> suppQuality = printerCaps.getSupportedPrintQuality();
if (defaultPrintQuality) {
if (currQuality != defQuality) {
setPrintQuality(defQuality);
defaultPrintQuality = true;
}
} else {
if (hasOldPrintQuality) {
if (suppQuality.contains(oldPrintQuality)) {
setPrintQuality(oldPrintQuality);
hasOldPrintQuality = false;
} else {
setPrintQuality(defQuality);
}
} else if (!suppQuality.contains(currQuality)) {
hasOldPrintQuality = true;
oldPrintQuality = currQuality;
setPrintQuality(defQuality);
}
}
PrintResolution currRes = getPrintResolution();
PrintResolution defResolution = printerCaps.getDefaultPrintResolution();
Set<PrintResolution> suppRes =
printerCaps.getSupportedPrintResolutions();
if (defaultPrintResolution) {
if (currRes != defResolution) {
setPrintResolution(defResolution);
defaultPrintResolution = true;
}
} else {
if (hasOldPrintResolution) {
if (suppRes.contains(oldPrintResolution)) {
setPrintResolution(oldPrintResolution);
hasOldPrintResolution = false;
} else {
setPrintResolution(defResolution);
}
} else if (!suppRes.contains(currRes)) {
hasOldPrintResolution = true;
oldPrintResolution = currRes;
setPrintResolution(defResolution);
}
}
PaperSource currSource = getPaperSource();
PaperSource defSource = printerCaps.getDefaultPaperSource();
Set<PaperSource> suppSources = printerCaps.getSupportedPaperSources();
if (defaultPaperSource) {
if (currSource != defSource) {
setPaperSource(defSource);
defaultPaperSource = true;
}
} else {
if (hasOldPaperSource) {
if (suppSources.contains(oldPaperSource)) {
setPaperSource(oldPaperSource);
hasOldPaperSource = false;
} else {
setPaperSource(defSource);
}
} else if (!suppSources.contains(currSource)) {
hasOldPaperSource = true;
oldPaperSource = currSource;
setPaperSource(defSource);
}
}
PageLayout currPageLayout = getPageLayout();
PageLayout defPageLayout = printer.getDefaultPageLayout();
if (defaultPageLayout) {
if (!currPageLayout.equals(defPageLayout)) {
setPageLayout(defPageLayout);
defaultPageLayout = true;
}
} else {
if (hasOldPageLayout) {
PageLayout valPageLayout =
job.validatePageLayout(oldPageLayout);
if (valPageLayout.equals(oldPageLayout)) {
setPageLayout(oldPageLayout);
hasOldPageLayout = false;
} else {
setPageLayout(defPageLayout);
}
} else {
PageLayout valPageLayout =
job.validatePageLayout(currPageLayout);
if (!valPageLayout.equals(currPageLayout)) {
hasOldPageLayout = true;
oldPageLayout = currPageLayout;
setPageLayout(defPageLayout);
}
}
}
}
private static final String DEFAULT_JOBNAME = "JavaFX Print Job";
private SimpleStringProperty jobName;
public final StringProperty jobNameProperty() {
if (jobName == null) {
jobName = new SimpleStringProperty(JobSettings.this, "jobName",
DEFAULT_JOBNAME) {
@Override
public void set(String value) {
if (!isJobNew()) {
return;
}
if (value == null) {
value = DEFAULT_JOBNAME;
}
super.set(value);
}
@Override
public void bind(ObservableValue<? extends String>
rawObservable) {
throw new
RuntimeException("Jobname property cannot be bound");
}
@Override
public void bindBidirectional(Property<String> other) {
throw new
RuntimeException("Jobname property cannot be bound");
}
@Override
public String toString() {
return get();
}
};
}
return jobName;
}
public final String getJobName() {
return jobNameProperty().get();
}
public final void setJobName(String name) {
jobNameProperty().set(name);
}
private SimpleStringProperty outputFile;
public final StringProperty outputFileProperty() {
if (outputFile == null) {
outputFile =
new SimpleStringProperty(JobSettings.this, "outputFile", "") {
@Override
public void set(String value) {
if (!isJobNew()) {
return;
}
if (value == null) {
value = "";
}
super.set(value);
}
@Override
public void bind(ObservableValue<? extends String>
rawObservable) {
throw new
RuntimeException("OutputFile property cannot be bound");
}
@Override
public void bindBidirectional(Property<String> other) {
throw new
RuntimeException("OutputFile property cannot be bound");
}
@Override
public String toString() {
return get();
}
};
}
return outputFile;
}
public final String getOutputFile() {
return outputFileProperty().get();
}
public final void setOutputFile(String filePath) {
outputFileProperty().set(filePath);
}
private IntegerProperty copies;
public final IntegerProperty copiesProperty() {
if (copies == null) {
copies =
new SimpleIntegerProperty(JobSettings.this, "copies",
printerCaps.getDefaultCopies()) {
@Override
public void set(int value) {
if (!isJobNew()) {
return;
}
if (value <= 0) {
if (defaultCopies) {
return;
} else {
super.set(printerCaps.getDefaultCopies());
defaultCopies = true;
return;
}
}
super.set(value);
defaultCopies = false;
}
@Override
public void bind(ObservableValue<? extends Number>
rawObservable) {
throw new
RuntimeException("Copies property cannot be bound");
}
@Override
public void bindBidirectional(Property<Number> other) {
throw new
RuntimeException("Copies property cannot be bound");
}
@Override
public String toString() {
return "" + get();
}
};
}
return copies;
}
public final int getCopies() {
return copiesProperty().get();
}
public final void setCopies(int nCopies) {
copiesProperty().set(nCopies);
}
private ObjectProperty<PageRange[]> pageRanges = null;
public final ObjectProperty pageRangesProperty() {
if (pageRanges == null) {
pageRanges = new SimpleObjectProperty(JobSettings.this,
"pageRanges", null) {
@Override
public void set(Object o) {
try {
set((PageRange[])o);
} catch (ClassCastException e) {
return;
}
}
public void set(PageRange[] value) {
if (!isJobNew()) {
return;
}
if (value == null || value.length == 0 ||
value[0] == null) {
value = null;
} else {
int len = value.length;
PageRange[] arr = new PageRange[len];
int curr = 0;
for (int i=0; i<len; i++) {
PageRange r = value[i];
if (r == null || curr >= r.getStartPage()) {
return;
}
curr = r.getEndPage();
arr[i] = r;
}
value = arr;
}
super.set(value);
}
@Override
public void bind(ObservableValue rawObservable) {
throw new RuntimeException
("PageRanges property cannot be bound");
}
@Override
public void bindBidirectional(Property other) {
throw new RuntimeException
("PageRanges property cannot be bound");
}
@Override
public String toString() {
PageRange[] ranges = (PageRange[])get();
if (ranges == null || ranges.length == 0) {
return "null";
}
String s = "";
int len = ranges.length;
for (int r=0; r<len; r++) {
s += ranges[r];
if ((r+1) < len) {
s += ", ";
} else {
s += ".";
}
}
return s;
}
};
}
return pageRanges;
}
public final PageRange[] getPageRanges() {
return (PageRange[])(pageRangesProperty().get());
}
public final void setPageRanges(PageRange... pages) {
pageRangesProperty().set((PageRange[])pages);
}
private ObjectProperty<PrintSides> sides = null;
public final ObjectProperty<PrintSides> printSidesProperty() {
if (sides == null) {
sides = new SimpleObjectProperty<PrintSides>
(JobSettings.this, "printSides",
printerCaps.getDefaultPrintSides()) {
@Override
public void set(PrintSides value) {
if (!isJobNew()) {
return;
}
if (value == null) {
if (defaultSides) {
return;
} else {
super.set(printerCaps.getDefaultPrintSides());
defaultSides = true;
}
}
if (printerCaps.getSupportedPrintSides().contains(value)) {
super.set(value);
defaultSides = false;
}
}
@Override
public void bind(ObservableValue<? extends PrintSides>
rawObservable) {
throw new RuntimeException
("PrintSides property cannot be bound");
}
@Override
public void bindBidirectional(Property<PrintSides> other) {
throw new RuntimeException
("PrintSides property cannot be bound");
}
@Override
public String toString() {
return get().toString();
}
};
}
return sides;
}
public final PrintSides getPrintSides() {
return printSidesProperty().get();
}
public final void setPrintSides(PrintSides sides) {
if (sides == getPrintSides()) {
return;
}
printSidesProperty().set(sides);
}
private ObjectProperty<Collation> collation = null;
public final ObjectProperty<Collation> collationProperty() {
if (collation == null) {
Collation coll = printerCaps.getDefaultCollation();
collation = new SimpleObjectProperty<Collation>
(JobSettings.this, "collation", coll) {
@Override
public void set(Collation value) {
if (!isJobNew()) {
return;
}
if (value == null) {
if (defaultCollation) {
return;
} else {
super.set(printerCaps.getDefaultCollation());
defaultCollation = true;
return;
}
}
if (printerCaps.getSupportedCollations().contains(value)) {
super.set(value);
defaultCollation = false;
}
}
@Override
public void bind(ObservableValue<? extends Collation>
rawObservable) {
throw new RuntimeException
("Collation property cannot be bound");
}
@Override
public void bindBidirectional(Property<Collation> other) {
throw new RuntimeException
("Collation property cannot be bound");
}
@Override
public String toString() {
return get().toString();
}
};
}
return collation;
}
public final Collation getCollation() {
return collationProperty().get();
}
public final void setCollation(Collation collation) {
if (collation == getCollation()) {
return;
}
collationProperty().set(collation);
}
private ObjectProperty<PrintColor> color = null;
public final ObjectProperty<PrintColor> printColorProperty() {
if (color == null) {
color = new SimpleObjectProperty<PrintColor>
(JobSettings.this, "printColor",
printerCaps.getDefaultPrintColor()) {
@Override
public void set(PrintColor value) {
if (!isJobNew()) {
return;
}
if (value == null) {
if (defaultPrintColor) {
return;
} else {
super.set(printerCaps.getDefaultPrintColor());
defaultPrintColor = true;
}
}
if (printerCaps.
getSupportedPrintColors().contains(value)) {
super.set(value);
defaultPrintColor = false;
}
}
@Override
public void bind(ObservableValue<? extends PrintColor>
rawObservable) {
throw new RuntimeException
("PrintColor property cannot be bound");
}
@Override
public void bindBidirectional(Property<PrintColor> other) {
throw new RuntimeException
("PrintColor property cannot be bound");
}
@Override
public String toString() {
return get().toString();
}
};
}
return color;
}
public final PrintColor getPrintColor() {
return printColorProperty().get();
}
public final void setPrintColor(PrintColor color) {
if (color == getPrintColor()) {
return;
}
printColorProperty().set(color);
}
private ObjectProperty<PrintQuality> quality = null;
public final ObjectProperty<PrintQuality> printQualityProperty() {
if (quality == null) {
quality = new SimpleObjectProperty<PrintQuality>
(JobSettings.this, "printQuality",
printerCaps.getDefaultPrintQuality()) {
@Override
public void set(PrintQuality value) {
if (!isJobNew()) {
return;
}
if (value == null) {
if (defaultPrintQuality) {
return;
} else {
super.set(printerCaps.getDefaultPrintQuality());
defaultPrintQuality = true;
}
}
if (printerCaps.
getSupportedPrintQuality().contains(value)) {
super.set(value);
defaultPrintQuality = false;
}
}
@Override
public void bind(ObservableValue<? extends PrintQuality>
rawObservable) {
throw new RuntimeException
("PrintQuality property cannot be bound");
}
@Override
public void bindBidirectional(Property<PrintQuality> other) {
throw new RuntimeException
("PrintQuality property cannot be bound");
}
@Override
public String toString() {
return get().toString();
}
};
}
return quality;
}
public final PrintQuality getPrintQuality() {
return printQualityProperty().get();
}
public final void setPrintQuality(PrintQuality quality) {
if (quality == getPrintQuality()) {
return;
}
printQualityProperty().set(quality);
}
private ObjectProperty<PrintResolution> resolution = null;
public final ObjectProperty<PrintResolution> printResolutionProperty() {
if (resolution == null) {
resolution = new SimpleObjectProperty<PrintResolution>
(JobSettings.this, "printResolution",
printerCaps.getDefaultPrintResolution()) {
@Override
public void set(PrintResolution value) {
if (!isJobNew()) {
return;
}
if (value == null) {
if (defaultPrintResolution) {
return;
} else {
super.set(printerCaps.getDefaultPrintResolution());
defaultPrintResolution = true;
}
}
if (printerCaps.getSupportedPrintResolutions().
contains(value))
{
super.set(value);
defaultPrintResolution = false;
}
}
@Override
public void bind(ObservableValue<? extends PrintResolution>
rawObservable) {
throw new RuntimeException
("PrintResolution property cannot be bound");
}
@Override
public void bindBidirectional(Property<PrintResolution> other)
{
throw new RuntimeException
("PrintResolution property cannot be bound");
}
@Override
public String toString() {
return get().toString();
}
};
}
return resolution;
}
public final PrintResolution getPrintResolution() {
return printResolutionProperty().get();
}
public final void setPrintResolution(PrintResolution resolution) {
if (resolution == null || resolution == getPrintResolution()) {
return;
}
printResolutionProperty().set(resolution);
}
private ObjectProperty<PaperSource> paperSource = null;
public final ObjectProperty<PaperSource> paperSourceProperty() {
if (paperSource == null) {
paperSource = new SimpleObjectProperty<PaperSource>
(JobSettings.this, "paperSource",
printerCaps.getDefaultPaperSource()) {
@Override
public void set(PaperSource value) {
if (!isJobNew()) {
return;
}
if (value == null) {
if (defaultPaperSource) {
return;
} else {
super.set(printerCaps.getDefaultPaperSource());
defaultPaperSource = true;
}
}
if (printerCaps.
getSupportedPaperSources().contains(value)) {
super.set(value);
defaultPaperSource = false;
}
}
@Override
public void bind(ObservableValue<? extends PaperSource>
rawObservable) {
throw new RuntimeException
("PaperSource property cannot be bound");
}
@Override
public void bindBidirectional(Property<PaperSource> other) {
throw new RuntimeException
("PaperSource property cannot be bound");
}
@Override
public String toString() {
return get().toString();
}
};
}
return paperSource;
}
public final PaperSource getPaperSource() {
return paperSourceProperty().get();
}
public final void setPaperSource(PaperSource value) {
paperSourceProperty().set(value);
}
private ObjectProperty<PageLayout> layout = null;
public final ObjectProperty<PageLayout> pageLayoutProperty() {
if (layout == null) {
layout = new SimpleObjectProperty<PageLayout>
(JobSettings.this, "pageLayout",
printer.getDefaultPageLayout()) {
@Override
public void set(PageLayout value) {
if (!isJobNew()) {
return;
}
if (value == null) {
return;
}
defaultPageLayout = false;
super.set(value);
}
@Override
public void bind(ObservableValue<? extends PageLayout>
rawObservable) {
throw new RuntimeException
("PageLayout property cannot be bound");
}
@Override
public void bindBidirectional(Property<PageLayout> other) {
throw new RuntimeException
("PageLayout property cannot be bound");
}
@Override
public String toString() {
return get().toString();
}
};
}
return layout;
}
public final PageLayout getPageLayout() {
return pageLayoutProperty().get();
}
public final void setPageLayout(PageLayout pageLayout) {
pageLayoutProperty().set(pageLayout);
}
@Override
public String toString() {
String nl = System.lineSeparator();
return
" Collation = " + getCollation() + nl +
" Copies = " + getCopies() + nl +
" Sides = " + getPrintSides() + nl +
" JobName = " + getJobName() + nl +
" Output file = " + getOutputFile() + nl +
" Page ranges = " + pageRangesProperty().toString() + nl +
" Print color = " + getPrintColor() + nl +
" Print quality = " + getPrintQuality() + nl +
" Print resolution = " + getPrintResolution() + nl +
" Paper source = " + getPaperSource() + nl +
" Page layout = " + getPageLayout();
}
}
