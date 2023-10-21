package ensemble.search;
import ensemble.generated.Samples;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.grouping.GroupDocs;
import org.apache.lucene.search.grouping.SearchGroup;
import org.apache.lucene.search.grouping.TermGroupSelector;
import org.apache.lucene.search.grouping.TopGroups;
import org.apache.lucene.search.grouping.TopGroupsCollector;
import org.apache.lucene.util.BytesRef;
public class IndexSearcher {
private final static List<SearchGroup<BytesRef>> searchGroups = new ArrayList<>();
static {
for (DocumentType dt: DocumentType.values()){
SearchGroup<BytesRef> searchGroup = new SearchGroup();
searchGroup.groupValue = new BytesRef(dt.toString());
searchGroup.sortValues = new Comparable[]{5f};
searchGroups.add(searchGroup);
}
}
private org.apache.lucene.search.IndexSearcher searcher;
private final Analyzer analyzer;
private final MultiFieldQueryParser parser;
public IndexSearcher() {
try {
searcher = new org.apache.lucene.search.IndexSearcher(DirectoryReader.open(new ClasspathDirectory()));
} catch (IOException e) {
e.printStackTrace();
}
analyzer = new StandardAnalyzer();
parser = new MultiFieldQueryParser(new String[]{"name","bookTitle","chapter","description"}, analyzer);
}
public Map<DocumentType, List<SearchResult>> search(String searchString) throws ParseException {
Map<DocumentType, List<SearchResult>> resultMap = new EnumMap<>(DocumentType.class);
try {
Query query = parser.parse(searchString);
final TopGroupsCollector<BytesRef> collector = new TopGroupsCollector(
new TermGroupSelector("documentType"), searchGroups,
Sort.RELEVANCE, Sort.RELEVANCE, 10, true, false, true);
searcher.search(query, collector);
final TopGroups<BytesRef> groups = collector.getTopGroups(0);
for (GroupDocs<BytesRef> groupDocs : groups.groups) {
DocumentType docType = DocumentType.valueOf(groupDocs.groupValue.utf8ToString());
List<SearchResult> results = new ArrayList<>();
for (ScoreDoc scoreDoc : groupDocs.scoreDocs) {
if ((Platform.isSupported(ConditionalFeature.WEB)) || (docType != DocumentType.DOC)) {
Document doc = searcher.doc(scoreDoc.doc);
SearchResult result = new SearchResult(
docType,
doc.get("name"),
doc.get("url"),
doc.get("className"),
doc.get("package"),
doc.get("ensemblePath"),
docType == DocumentType.DOC
? doc.get("bookTitle") == null ? doc.get("chapter") : doc.get("bookTitle")
: doc.get("shortDescription").trim()
);
if (docType == DocumentType.SAMPLE) {
if (Samples.ROOT.sampleForPath(result.getEnsemblePath().substring(9).trim()) == null) {
continue;
}
if (results.size() == 5) {
break;
}
}
results.add(result);
}
}
resultMap.put(docType, results);
}
} catch (IOException e) {
e.printStackTrace();
}
return resultMap;
}
public static void main(String[] args) throws Exception {
BufferedReader in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
IndexSearcher indexSearcher = new IndexSearcher();
while (true) {
System.out.println("Enter query: ");
String line = in.readLine();
if (line == null || line.length() == -1) break;
line = line.trim();
if (line.length() == 0) break;
Map<DocumentType, List<SearchResult>> results = indexSearcher.search(line);
for (Map.Entry<DocumentType, List<SearchResult>> entry : results.entrySet()) {
System.out.println("--------- "+entry.getKey()+" ["+entry.getValue().size()+"] --------------------------------");
for(SearchResult result: entry.getValue()) {
System.out.println(result.toString());
}
}
}
}
}
