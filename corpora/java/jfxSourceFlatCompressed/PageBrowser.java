package ensemble;
import static ensemble.PlatformFeatures.WEB_SUPPORTED;
import ensemble.samplepage.SourcePage;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import java.util.LinkedList;
import ensemble.generated.Samples;
import ensemble.samplepage.SamplePage;
public class PageBrowser extends Region {
public static final String HOME_URL = "home";
private HomePage homePage;
private Page currentPage;
private SamplePage samplePage;
private SourcePage sourcePage;
private String currentPageUrl;
private DocsPage docsPage;
private LinkedList<String> pastHistory = new LinkedList<>();
private LinkedList<String> futureHistory = new LinkedList<>();
private BooleanProperty forwardPossible = new SimpleBooleanProperty(false);
public ReadOnlyBooleanProperty forwardPossibleProperty() { return forwardPossible; }
public boolean isForwardPossible() { return forwardPossible.get(); }
private BooleanProperty backPossible = new SimpleBooleanProperty(false);
public ReadOnlyBooleanProperty backPossibleProperty() { return backPossible; }
public boolean isBackPossible() { return backPossible.get(); }
private BooleanProperty atHome = new SimpleBooleanProperty(false);
public ReadOnlyBooleanProperty atHomeProperty() { return atHome; }
public boolean isAtHome() { return atHome.get(); }
private StringProperty currentPageTitle = new SimpleStringProperty(null);
public ReadOnlyStringProperty currentPageTitleProperty() { return currentPageTitle; };
public String getCurrentPageTitle() { return currentPageTitle.get(); }
public void forward() {
String newUrl = futureHistory.pop();
if (newUrl != null) {
pastHistory.push(getCurrentPageUrl());
goToPage(newUrl, null, false);
}
}
public void backward() {
String newUrl = pastHistory.pop();
if (newUrl != null) {
futureHistory.push(getCurrentPageUrl());
goToPage(newUrl, null, false);
}
}
public void goToSample(SampleInfo sample) {
goToPage("sample://"+sample.ensemblePath, sample, true);
}
public void goToPage(String url) {
goToPage(url, null, true);
}
public void goHome() {
goToPage(HOME_URL, null, true);
}
public void externalPageChange(String newUrl) {
if (currentPageUrl != null) {
pastHistory.push(getCurrentPageUrl());
}
futureHistory.clear();
currentPageUrl = newUrl;
}
private void goToPage(String url, SampleInfo sample, boolean updateHistory) {
Page nextPage = null;
if (url.equals(HOME_URL)) {
nextPage = getHomePage();
} else if (url.startsWith("http://") || url.startsWith("https://")) {
if (WEB_SUPPORTED) {
nextPage = updateDocsPage(url);
} else {
System.err.println("Web pages are not supported and links to them should be disabled!");
}
} else if (sample != null) {
nextPage = updateSamplePage(sample, url);
} else if (url.startsWith("sample://")) {
String samplePath = url.substring("sample://".length());
if (samplePath.contains("?")) {
samplePath = samplePath.substring(0, samplePath.indexOf('?') - 1);
}
sample = Samples.ROOT.sampleForPath(samplePath);
if (sample != null) {
nextPage = updateSamplePage(sample, url);
} else {
throw new UnsupportedOperationException("Unknown sample url ["+url+"]");
}
} else if (url.startsWith("sample-src://")) {
String samplePath = url.substring("sample-src://".length());
if (samplePath.contains("?")) {
samplePath = samplePath.substring(0, samplePath.indexOf('?') - 1);
}
sample = Samples.ROOT.sampleForPath(samplePath);
if (sample != null) {
nextPage = updateSourcePage(sample);
} else {
System.err.println("Unknown sample url [" + url + "]");
}
} else {
System.err.println("Unknown ensemble page url [" + url + "]");
}
if (nextPage != null) {
if (updateHistory) {
if (currentPageUrl != null) {
pastHistory.push(getCurrentPageUrl());
}
futureHistory.clear();
}
currentPageUrl = url;
if (currentPage != null) {
getChildren().remove((Node) currentPage);
}
currentPage = nextPage;
getChildren().add(currentPage.getNode());
atHome.set(url.equals(HOME_URL));
forwardPossible.set(!futureHistory.isEmpty());
backPossible.set(!pastHistory.isEmpty());
currentPageTitle.bind(currentPage.titleProperty());
}
}
@Override protected void layoutChildren() {
if (currentPage != null) {
currentPage.getNode().resize(getWidth(), getHeight());
}
}
public String getCurrentPageUrl() {
return currentPageUrl;
}
private SamplePage updateSamplePage(SampleInfo sample, String url) {
if (samplePage == null) {
samplePage = new SamplePage(sample, url, this);
} else {
samplePage.update(sample, url);
}
return samplePage;
}
private SourcePage updateSourcePage(SampleInfo sample) {
if (sourcePage == null) {
sourcePage = new SourcePage();
}
sourcePage.setSampleInfo(sample);
return sourcePage;
}
private Page getHomePage() {
if (homePage == null) {
homePage = new HomePage(this);
}
return homePage;
}
private DocsPage updateDocsPage(String url) {
if (docsPage == null) {
docsPage = new DocsPage(this);
}
docsPage.goToUrl(url);
return docsPage;
}
}
