package javafx.print;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import static javafx.print.PageOrientation.*;
import com.sun.javafx.print.PrinterImpl;
public final class PrinterAttributes {
private PrinterImpl impl;
PrinterAttributes(PrinterImpl impl) {
this.impl = impl;
}
public int getDefaultCopies() {
return impl.defaultCopies();
}
public int getMaxCopies() {
return impl.maxCopies();
}
public boolean supportsPageRanges() {
return impl.supportsPageRanges();
}
public Collation getDefaultCollation() {
return impl.defaultCollation();
}
public Set<Collation> getSupportedCollations() {
return impl.supportedCollations();
}
public PrintSides getDefaultPrintSides() {
return impl.defaultSides();
}
public Set<PrintSides> getSupportedPrintSides() {
return impl.supportedSides();
}
public PrintColor getDefaultPrintColor() {
return impl.defaultPrintColor();
}
public Set<PrintColor> getSupportedPrintColors() {
return impl.supportedPrintColor();
}
public PrintQuality getDefaultPrintQuality() {
return impl.defaultPrintQuality();
}
public Set<PrintQuality> getSupportedPrintQuality() {
return impl.supportedPrintQuality();
}
public PrintResolution getDefaultPrintResolution() {
return impl.defaultPrintResolution();
}
public Set<PrintResolution> getSupportedPrintResolutions() {
return impl.supportedPrintResolution();
}
public PageOrientation getDefaultPageOrientation() {
return impl.defaultOrientation();
}
public Set<PageOrientation> getSupportedPageOrientations() {
return impl.supportedOrientation();
}
public Paper getDefaultPaper() {
return impl.defaultPaper();
}
public Set<Paper> getSupportedPapers() {
return impl.supportedPapers();
}
public PaperSource getDefaultPaperSource() {
return impl.defaultPaperSource();
}
public Set<PaperSource> getSupportedPaperSources() {
return impl.supportedPaperSources();
}
}
