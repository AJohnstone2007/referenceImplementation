package ensemble.compiletime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.application.ConditionalFeature;
public class Sample {
public String name;
public String description;
public String ensemblePath;
public String baseUri;
public List<String> resourceUrls = new ArrayList<>();
public String mainFileUrl;
public String appClass;
public String previewUrl;
public final List<PlaygroundProperty> playgroundProperties = new ArrayList<>();
public final List<ConditionalFeature> conditionalFeatures = new ArrayList<>();
public boolean runsOnEmbedded = false;
public List<String> apiClasspaths = new ArrayList<>();
public List<URL> docsUrls = new ArrayList<>();
public List<String> relatesSamplePaths = new ArrayList<>();
@Override public String toString() {
return "Sample{" +
"\n         name                 =" + name +
",\n         description          =" + description +
",\n         ensemblePath         =" + ensemblePath +
",\n         previewUrl           =" + previewUrl +
",\n         baseUri              =" + baseUri +
",\n         resourceUrls         =" + resourceUrls +
",\n         mainFileUrl          =" + mainFileUrl +
",\n         appClass             =" + appClass +
",\n         apiClasspaths        =" + apiClasspaths +
",\n         docsUrls             =" + docsUrls +
",\n         relatesSamplePaths   =" + relatesSamplePaths +
",\n         playgroundProperties =" + playgroundProperties +
",\n         conditionalFeatures  =" + conditionalFeatures +
",\n         runsOnEmbedded       =" + runsOnEmbedded +
'}';
}
public static class URL {
public final String url;
public final String name;
public URL(String url, String name) {
this.url = url;
this.name = name;
}
public URL(String raw) {
int index = raw.indexOf(' ');
if (index == -1) {
name = url = raw;
} else {
url = raw.substring(0, index);
name = raw.substring(index + 1);
}
}
}
public static class PlaygroundProperty {
public final String fieldName;
public final String propertyName;
public final Map<String,String> properties;
public PlaygroundProperty(String fieldName, String propertyName, Map<String,String> properties) {
this.fieldName = fieldName;
this.propertyName = propertyName;
this.properties = properties;
}
@Override public String toString() {
return fieldName+"."+propertyName+" ("+properties+")";
}
}
}
