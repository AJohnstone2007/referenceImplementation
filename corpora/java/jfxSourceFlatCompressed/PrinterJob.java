package javafx.print;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.stage.Window;
import com.sun.javafx.print.PrinterJobImpl;
import com.sun.javafx.tk.PrintPipeline;
public final class PrinterJob {
private PrinterJobImpl jobImpl;
private ObjectProperty<Printer> printer;
private JobSettings settings;
public static final PrinterJob createPrinterJob() {
@SuppressWarnings("removal")
SecurityManager security = System.getSecurityManager();
if (security != null) {
security.checkPrintJobAccess();
}
Printer printer = Printer.getDefaultPrinter();
if (printer == null) {
return null;
} else {
return new PrinterJob(printer);
}
}
public static final PrinterJob createPrinterJob(Printer printer) {
@SuppressWarnings("removal")
SecurityManager security = System.getSecurityManager();
if (security != null) {
security.checkPrintJobAccess();
}
return new PrinterJob(printer);
}
private PrinterJob(Printer printer) {
this.printer = createPrinterProperty(printer);
settings = printer.getDefaultJobSettings();
settings.setPrinterJob(this);
createImplJob(printer, settings);
}
synchronized private PrinterJobImpl createImplJob(Printer printer,
JobSettings settings) {
if (jobImpl == null) {
jobImpl = PrintPipeline.getPrintPipeline().createPrinterJob(this);
}
return jobImpl;
}
boolean isJobNew() {
return getJobStatus() == JobStatus.NOT_STARTED;
}
private ObjectProperty<Printer> createPrinterProperty(Printer printer) {
return new SimpleObjectProperty<Printer>(printer) {
@Override
public void set(Printer value) {
if (value == get() || !isJobNew()) {
return;
}
if (value == null) {
value = Printer.getDefaultPrinter();
}
super.set(value);
jobImpl.setPrinterImpl(value.getPrinterImpl());
settings.updateForPrinter(value);
}
@Override
public void bind(ObservableValue<? extends Printer> rawObservable) {
throw new RuntimeException("Printer property cannot be bound");
}
@Override
public void bindBidirectional(Property<Printer> other) {
throw new RuntimeException("Printer property cannot be bound");
}
@Override
public Object getBean() {
return PrinterJob.this;
}
@Override
public String getName() {
return "printer";
}
};
}
public final ObjectProperty<Printer> printerProperty() {
return printer;
}
public synchronized final Printer getPrinter() {
return printerProperty().get();
}
public synchronized final void setPrinter(Printer printer) {
printerProperty().set(printer);
}
public synchronized JobSettings getJobSettings() {
return settings;
}
public synchronized boolean showPrintDialog(Window owner) {
if (!isJobNew()) {
return false;
} else {
return jobImpl.showPrintDialog(owner);
}
}
public synchronized boolean showPageSetupDialog(Window owner) {
if (!isJobNew()) {
return false;
} else {
return jobImpl.showPageDialog(owner);
}
}
synchronized PageLayout validatePageLayout(PageLayout pageLayout) {
if (pageLayout == null) {
throw new NullPointerException("pageLayout cannot be null");
}
return jobImpl.validatePageLayout(pageLayout);
}
public synchronized boolean printPage(PageLayout pageLayout, Node node) {
if (jobStatus.get().ordinal() > JobStatus.PRINTING.ordinal()) {
return false;
}
if (jobStatus.get() == JobStatus.NOT_STARTED) {
jobStatus.set(JobStatus.PRINTING);
}
if (pageLayout == null || node == null) {
jobStatus.set(JobStatus.ERROR);
throw new NullPointerException("Parameters cannot be null");
}
boolean rv = jobImpl.print(pageLayout, node);
if (!rv) {
jobStatus.set(JobStatus.ERROR);
}
return rv;
}
public synchronized boolean printPage(Node node) {
return printPage(settings.getPageLayout(), node);
}
public static enum JobStatus {
NOT_STARTED,
PRINTING,
CANCELED,
ERROR,
DONE
};
private ReadOnlyObjectWrapper<JobStatus> jobStatus =
new ReadOnlyObjectWrapper(JobStatus.NOT_STARTED);
public final ReadOnlyObjectProperty<JobStatus> jobStatusProperty() {
return jobStatus.getReadOnlyProperty();
}
public final JobStatus getJobStatus() {
return jobStatus.get();
}
public void cancelJob() {
if (jobStatus.get().ordinal() <= JobStatus.PRINTING.ordinal()) {
jobStatus.set(JobStatus.CANCELED);
jobImpl.cancelJob();
}
}
public synchronized boolean endJob() {
if (jobStatus.get() == JobStatus.NOT_STARTED) {
cancelJob();
return false;
} else if (jobStatus.get() == JobStatus.PRINTING) {
boolean rv = jobImpl.endJob();
jobStatus.set(rv ? JobStatus.DONE : JobStatus.ERROR);
return rv;
} else {
return false;
}
}
@Override
public String toString() {
return "JavaFX PrinterJob " +
getPrinter() + "\n" +
getJobSettings() + "\n" +
"Job Status = " + getJobStatus();
}
}
