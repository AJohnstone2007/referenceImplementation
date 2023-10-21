package ensemble.compiletime.search;
import ensemble.compiletime.Sample;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
public class BuildEnsembleSearchIndex {
public static void buildSearchIndex(List<Sample> allSamples, String javaDocBaseUrl, String javafxDocumentationHome, File indexDir){
try {
List<Document> docs = new ArrayList<>();
List<Callable<List<Document>>> tasks = new ArrayList<>();
System.out.println("Creating Documents for Samples...");
docs.addAll(indexSamples(allSamples));
System.out.println("Creating tasks for getting all documentation...");
System.out.println("javaDocBaseUrl = " + javaDocBaseUrl);
System.out.println("javafxDocumentationHome = " + javafxDocumentationHome);
tasks.addAll(indexJavaDocAllClasses(javaDocBaseUrl));
tasks.addAll(indexAllDocumentation(javafxDocumentationHome));
System.out.println("Executing tasks getting all documentation...");
try {
ThreadPoolExecutor executor = new ThreadPoolExecutor(32,32,30, TimeUnit.SECONDS,new LinkedBlockingQueue());
executor.setThreadFactory(new ThreadFactory() {
int index = 0;
@Override public Thread newThread(Runnable r) {
Thread thread = new Thread(r,"Thread-"+(++index));
thread.setDaemon(true);
return thread;
}
});
List<Future<List<Document>>> results = executor.invokeAll(tasks);
for(Future<List<Document>> future : results) {
docs.addAll(future.get());
}
} catch (ExecutionException | InterruptedException ex) {
Logger.getLogger(BuildEnsembleSearchIndex.class.getName()).log(Level.SEVERE, null, ex);
}
System.out.println("Indexing to directory '" + indexDir + "'...");
Directory dir = FSDirectory.open(indexDir.toPath());
Analyzer analyzer = new StandardAnalyzer();
IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
iwc.setOpenMode(OpenMode.CREATE);
try (IndexWriter writer = new IndexWriter(dir, iwc)) {
System.out.println("Writing ["+docs.size()+"] documents to index....");
writer.addDocuments(docs);
System.out.println("NUMBER OF INDEXED DOCUMENTS = ["+writer.numDocs()+"]");
}
try (FileWriter listAllOut = new FileWriter(new File(indexDir,"listAll.txt"))) {
for (String fileName: dir.listAll()) {
if (!"listAll.txt".equals(fileName) && !"write.lock".equals(fileName)) {
Long length = dir.fileLength(fileName);
listAllOut.write(fileName);
listAllOut.write(':');
listAllOut.write(length.toString());
listAllOut.write('\n');
}
}
listAllOut.flush();
}
System.out.println("Finished writing search index to directory '" + indexDir);
} catch (IOException ex) {
Logger.getLogger(BuildEnsembleSearchIndex.class.getName()).log(Level.SEVERE, null, ex);
}
}
private static List<Callable<List<Document>>> indexAllDocumentation(String javafxDocumentationHome) throws IOException{
List<Callable<List<Document>>> tasks = new ArrayList<>();
CharSequence content = grabWebPage(javafxDocumentationHome);
String baseUrl = javafxDocumentationHome.substring(0,javafxDocumentationHome.lastIndexOf('/')+1);
Matcher matcher = docsHomeLink.matcher(content);
System.out.println("Building a list of documentation to index");
while (matcher.find()) {
String foundUrl = matcher.group(1);
final String docPageUrl = (foundUrl.startsWith("http") ? foundUrl : baseUrl + foundUrl);
if ("https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html".equals(docPageUrl) ||
"https://docs.oracle.com/javafx/2/api/index.html".equals(docPageUrl) ||
"http://www.oracle.com/technetwork/java/javafx/downloads/supportedconfigurations-1506746.html".equals(docPageUrl) ||
"http://www.oracle.com/technetwork/java/javase/downloads/".equals(docPageUrl) ||
"https://docs.oracle.com/javafx/2/api/javafx/fxml/doc-files/introduction_to_fxml.html".equals(docPageUrl)) {
continue;
}
System.out.println(docPageUrl);
tasks.add((Callable<List<Document>>) () -> indexDocumentationPage(docPageUrl));
}
System.out.println(" --- end of list ---");
return tasks;
}
private static List<Document> indexDocumentationPage(String docPageUrl) throws IOException{
List<Document> docs = new ArrayList<>();
try {
DocumentationIndexer.DocPage docPage = DocumentationIndexer.parseDocsPage(docPageUrl, grabWebPage(docPageUrl).toString());
for (DocumentationIndexer.Section section: docPage.sections) {
if (section.name == null) {
System.out.println("section.name = "+section.name+" docPage.bookTitle="+docPage.bookTitle+"    "+docPageUrl);
}
docs.add(createDocument(DocumentType.DOC,
new TextField("bookTitle", docPage.bookTitle, Field.Store.YES),
new TextField("chapter", docPage.chapter==null? "" : docPage.chapter, Field.Store.YES),
new TextField("name", section.name, Field.Store.YES),
new TextField("description", section.content, Field.Store.NO),
new StringField("ensemblePath", section.url, Field.Store.YES)
));
}
if (docPage.nextUrl != null) {
docs.addAll(indexDocumentationPage(docPage.nextUrl));
}
} catch (Exception ex) {
System.out.println("FAILED TO PARSE DOCS PAGE SO IGNORED: ["+docPageUrl+"]");
ex.printStackTrace(System.out);
}
return docs;
}
private static List<Callable<List<Document>>> indexJavaDocAllClasses(final String javaDocBaseUrl) throws IOException{
CharSequence content = grabWebPage(javaDocBaseUrl+"allclasses-noframe.html");
List<Callable<List<Document>>> tasks = new ArrayList<>();
Matcher matcher = findClassUrl.matcher(content);
while (matcher.find()) {
final String classUrl = javaDocBaseUrl+matcher.group(1);
tasks.add((Callable<List<Document>>) () -> indexApiDocs(classUrl));
}
return tasks;
}
private static List<Document> indexSamples(List<Sample> allSamples) throws IOException {
List<Document> docs = new ArrayList<>();
for (Sample sample: allSamples) {
docs.add(createDocument(DocumentType.SAMPLE,
new TextField("name", sample.name, Field.Store.YES),
new TextField("description", sample.description, Field.Store.NO),
new StringField("shortDescription", sample.description.substring(0, Math.min(160, sample.description.length())),
Field.Store.YES),
new StringField("ensemblePath", "sample://"+sample.ensemblePath, Field.Store.YES)
));
}
return docs;
}
private static List<Document> indexApiDocs(String url) throws IOException {
final List<Document> docs = new ArrayList<>();
CharSequence content = grabWebPage(url);
Matcher packageAndClassMatcher = PACKAGE_AND_CLASS.matcher(content);
if (!packageAndClassMatcher.find()) {
return docs;
} else {
}
String packageName = packageAndClassMatcher.group(1);
String classType = packageAndClassMatcher.group(2).toLowerCase();
String className = packageAndClassMatcher.group(3);
DocumentType documentType = DocumentType.CLASS;
if ("enum".equals(classType)) {
documentType = DocumentType.ENUM;
}
Matcher classDescriptionMatcher = CLASS_DESCRIPTION.matcher(content);
String classDescription = "";
if (classDescriptionMatcher.find()) {
classDescription = cleanHTML(classDescriptionMatcher.group(1));
}
docs.add(createDocument(documentType,
new TextField("name", className, Field.Store.YES),
new TextField("description", classDescription, Field.Store.NO),
new StringField("shortDescription", classDescription.substring(0,Math.min(160,classDescription.length())),
Field.Store.YES),
new TextField("package", packageName, Field.Store.YES),
new StringField("url", url, Field.Store.YES),
new StringField("ensemblePath", url, Field.Store.YES)
));
Matcher propertySummaryMatcher = PROPERTY_SUMMARY.matcher(content);
if (propertySummaryMatcher.find()) {
String propertySummaryTable = propertySummaryMatcher.group(1);
Matcher propertyMatcher = PROPERTY.matcher(propertySummaryTable);
while (propertyMatcher.find()) {
String propUrl = propertyMatcher.group(1);
String propertyName = propertyMatcher.group(2);
String description = cleanHTML(propertyMatcher.group(3));
propUrl = url + "#" + propertyName;
docs.add(createDocument(DocumentType.PROPERTY,
new TextField("name", propertyName, Field.Store.YES),
new TextField("description", description, Field.Store.NO),
new StringField("shortDescription", description.substring(0,Math.min(160,description.length())),
Field.Store.YES),
new StringField("url", propUrl, Field.Store.YES),
new StringField("className", className, Field.Store.YES),
new StringField("package", packageName, Field.Store.YES),
new StringField("ensemblePath", url + "#" + propertyName, Field.Store.YES)
));
}
}
Matcher methodSummaryMatcher = METHOD_SUMMARY.matcher(content);
if (methodSummaryMatcher.find()) {
String methodSummaryTable = methodSummaryMatcher.group(1);
Matcher methodMatcher = PROPERTY.matcher(methodSummaryTable);
while (methodMatcher.find()) {
String methodUrl = methodMatcher.group(1);
String methodName = methodMatcher.group(2);
String description = cleanHTML(methodMatcher.group(3));
methodUrl = url + "#" + methodName+"()";
docs.add(createDocument(DocumentType.METHOD,
new TextField("name", methodName, Field.Store.YES),
new TextField("description", description, Field.Store.NO),
new StringField("shortDescription", description.substring(0,Math.min(160,description.length())),
Field.Store.YES),
new StringField("url", methodUrl, Field.Store.YES),
new StringField("className", className, Field.Store.YES),
new StringField("package", packageName, Field.Store.YES),
new StringField("ensemblePath", url + "#" + methodName + "()", Field.Store.YES)
));
}
}
Matcher fieldSummaryMatcher = FIELD_SUMMARY.matcher(content);
if (fieldSummaryMatcher.find()) {
String fieldSummaryTable = fieldSummaryMatcher.group(1);
Matcher fieldMatcher = PROPERTY.matcher(fieldSummaryTable);
while (fieldMatcher.find()) {
String fieldUrl = fieldMatcher.group(1);
String fieldName = fieldMatcher.group(2);
String description = cleanHTML(fieldMatcher.group(3));
fieldUrl = url + "#" + fieldName;
docs.add(createDocument(DocumentType.FIELD,
new TextField("name", fieldName, Field.Store.YES),
new TextField("description", description, Field.Store.NO),
new StringField("shortDescription", description.substring(0,Math.min(160,description.length())),
Field.Store.YES),
new StringField("url", fieldUrl, Field.Store.YES),
new StringField("className", className, Field.Store.YES),
new StringField("package", packageName, Field.Store.YES),
new StringField("ensemblePath", url + "#" + fieldName, Field.Store.YES)
));
}
}
Matcher enumSummaryMatcher = ENUM_SUMMARY.matcher(content);
if (enumSummaryMatcher.find()) {
String enumSummaryTable = enumSummaryMatcher.group(1);
Matcher enumMatcher = PROPERTY.matcher(enumSummaryTable);
while (enumMatcher.find()) {
String enumUrl = enumMatcher.group(1);
String enumName = enumMatcher.group(2);
String description = cleanHTML(enumMatcher.group(3));
enumUrl = url + "#" + enumName;
docs.add(createDocument(DocumentType.ENUM,
new TextField("name", enumName, Field.Store.YES),
new TextField("description", description, Field.Store.NO),
new StringField("shortDescription", description.substring(0,Math.min(160,description.length())),
Field.Store.YES),
new StringField("url", enumUrl, Field.Store.YES),
new StringField("className", className, Field.Store.YES),
new StringField("package", packageName, Field.Store.YES),
new StringField("ensemblePath", url+ "#" + enumName, Field.Store.YES)
));
}
}
return docs;
}
private static Document createDocument(DocumentType documentType, Field... fields) throws IOException {
Document doc = new Document();
doc.add(new StringField("documentType", documentType.toString(), Field.Store.YES));
doc.add(new SortedDocValuesField("documentType", new BytesRef(documentType.toString())));
if (fields != null) {
for (Field field : fields) {
doc.add(field);
}
}
return doc;
}
private static void addDocument(IndexWriter writer, DocumentType documentType, Field... fields) throws IOException {
Document doc = new Document();
doc.add(new StringField("documentType", documentType.toString(), Field.Store.YES));
doc.add(new SortedDocValuesField("documentType", new BytesRef(documentType.toString())));
if (fields != null) {
for (Field field : fields) {
doc.add(field);
}
}
writer.addDocument(doc);
}
private static String cleanHTML(String html) {
html = html.replaceAll("(&nbsp;|\\s|[ ])+", " ").trim();
html = html.replaceAll("<.*?>", " ");
html = html.replaceAll("&lt;", "<");
html = html.replaceAll("&gt;", ">");
html = html.replaceAll("&quot;", "\"");
html = html.replaceAll("&apos;", "\'");
html = html.replaceAll("&amp;", "&");
return html;
}
static CharSequence grabWebPage(String url) throws IOException {
StringBuilder builder = new StringBuilder();
try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
String line;
while((line = reader.readLine()) != null) {
builder.append(line);
builder.append('\n');
}
}
return builder;
}
private static final Pattern docsHomeLink = Pattern.compile("<p\\s+class=\\\"fxblurblink\\\"\\s*>.*<a\\s*href=\\\"([^\\\"]+)");
private static final Pattern bookTitle = Pattern.compile( "<div\\s+id=\\\"bookTitle\\\"\\s*>\\s*<h1>([^<]+)");
private static final Pattern chapter = Pattern.compile("<h1\\s+class=\\\"chapter\\\"\\s*>([^<]+)");
private static final Pattern findClassUrl = Pattern.compile("a\\s+href=\\\"([^\\\"]+)\\\"");
private static Pattern PACKAGE_AND_CLASS = Pattern.compile("<div class=\"subTitle\">\\s*([^<]+)</div>\\s*<h2 title=\"(Class|Interface|Enum) ([^<&]+).*?\"\\sclass=\"title\">(Class|Interface|Enum) ([^<&]+).*?</h2>",Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
private static Pattern CLASS_DESCRIPTION = Pattern.compile("<div class=\"description\">.*?<[pP]>(.*?)</div>",Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
private static Pattern PROPERTY_SUMMARY = Pattern.compile("<h3>Property Summary</h3>.*?<table[^>]+>(.*?)</table>",Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
private static Pattern METHOD_SUMMARY = Pattern.compile("<h3>Method Summary</h3>.*?<table[^>]+>(.*?)</table>",Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
private static Pattern ENUM_SUMMARY = Pattern.compile("<h3>Enum Constant Summary</h3>.*?<table[^>]+>(.*?)</table>",Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
private static Pattern FIELD_SUMMARY = Pattern.compile("<h3>Field Summary</h3>.*?<table[^>]+>(.*?)</table>",Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
private static Pattern PROPERTY = Pattern.compile("<td class=\"colFirst\">.*?<a href=\"([^\"]*)\">([^<]*)</a>(.*?)</td>",Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
}
