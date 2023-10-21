package ensemble.compiletime;
import ensemble.compiletime.Sample.URL;
import java.util.*;
import javafx.application.ConditionalFeature;
public class CodeGenerationUtils {
public static List<Sample> ALL_SAMPLES = new ArrayList<Sample>();
public static Map<String,Set<String>> DOCS_TO_SAMPLE_MAP = new HashMap<String, Set<String>>();
public static String generateSampleRef(Sample sample) {
int sampleIndex = ALL_SAMPLES.indexOf(sample);
String sampleVarName;
if (sampleIndex < 0) {
sampleIndex = ALL_SAMPLES.size();
sampleVarName = getSampleVarName(sampleIndex);
ALL_SAMPLES.add(sample);
for(String docRef: sample.apiClasspaths) {
addDocRef(docRef,sampleVarName);
}
for(URL docRef: sample.docsUrls) {
addDocRef(docRef.url, sampleVarName);
}
} else {
sampleVarName = getSampleVarName(sampleIndex);
}
return sampleVarName;
}
private static void addDocRef(String docUrl, String sampleVarName) {
Set<String> docRefList = DOCS_TO_SAMPLE_MAP.get(docUrl);
if (docRefList == null) {
docRefList = new HashSet<>();
DOCS_TO_SAMPLE_MAP.put(docUrl,docRefList);
}
docRefList.add(sampleVarName);
}
public static String getSampleVarName(int sampleIndex) {
return "SAMPLE_"+sampleIndex;
}
public static String generateCode(Sample sample) {
StringBuilder sb = new StringBuilder();
sb.append("new SampleInfo(");
sb.append(stringToCode(sample.name)); sb.append(',');
sb.append(stringToCode(sample.description)); sb.append(',');
sb.append(stringToCode(sample.ensemblePath)); sb.append(',');
sb.append(stringToCode(sample.baseUri)); sb.append(',');
sb.append(stringToCode(sample.appClass)); sb.append(',');
sb.append(stringToCode(sample.previewUrl)); sb.append(',');
sb.append(stringArrayToCode(sample.resourceUrls)); sb.append(',');
sb.append(stringArrayToCode(sample.apiClasspaths)); sb.append(',');
sb.append(urlArrayToCode(sample.docsUrls)); sb.append(',');
sb.append(stringArrayToCode(sample.relatesSamplePaths)); sb.append(',');
sb.append(stringToCode(sample.mainFileUrl)); sb.append(',');
sb.append(playgroundPropertyArrayToCode(sample.playgroundProperties)); sb.append(',');
sb.append(conditionalFeatureArrayToCode(sample.conditionalFeatures)); sb.append(',');
sb.append(Boolean.toString(sample.runsOnEmbedded));
sb.append(")");
return sb.toString();
}
public static String playgroundPropertyArrayToCode(List<Sample.PlaygroundProperty> array) {
StringBuilder sb = new StringBuilder();
sb.append("new PlaygroundProperty[]{");
for (Sample.PlaygroundProperty prop: array) {
sb.append("new PlaygroundProperty(");
sb.append(stringToCode(prop.fieldName));
sb.append(',');
sb.append(stringToCode(prop.propertyName));
for (Map.Entry<String,String> entry: prop.properties.entrySet()) {
sb.append(',');
sb.append(stringToCode(entry.getKey()));
sb.append(',');
sb.append(stringToCode(entry.getValue()));
}
sb.append("),");
}
sb.append("}");
return sb.toString();
}
public static String conditionalFeatureArrayToCode(List<ConditionalFeature> array) {
StringBuilder sb = new StringBuilder();
sb.append("new ConditionalFeature[]{");
for (ConditionalFeature feature : array) {
sb.append("ConditionalFeature.");
sb.append(feature.name());
sb.append(',');
}
sb.append("}");
return sb.toString();
}
public static String sampleArrayToCode(List<Sample> array) {
if (array == null || array.isEmpty()) return "null";
StringBuilder sb = new StringBuilder();
sb.append("new SampleInfo[]{");
for (Sample sample: array) {
sb.append(generateSampleRef(sample));
sb.append(',');
}
sb.append("}");
return sb.toString();
}
public static String stringArrayToCode(List<String> array) {
StringBuilder sb = new StringBuilder();
sb.append("new String[]{");
for (String string: array) {
sb.append(stringToCode(string));
sb.append(',');
}
sb.append("}");
return sb.toString();
}
public static String urlArrayToCode(List<URL> array) {
StringBuilder sb = new StringBuilder();
sb.append("new String[]{");
for (URL url: array) {
sb.append(stringToCode(url.url));
sb.append(',');
sb.append(stringToCode(url.name));
sb.append(',');
}
sb.append("}");
return sb.toString();
}
public static String variableNameArrayToCode(String className, Collection<String> array) {
StringBuilder sb = new StringBuilder();
sb.append("new "+className+"[]{");
for (String string: array) {
sb.append(string);
sb.append(',');
}
sb.append("}");
return sb.toString();
}
public static String stringToCode(String string) {
if (string == null) {
return "null";
} else {
return '\"' + string.replaceAll("\\\\", "\\\\\\\\") + '\"';
}
}
}
