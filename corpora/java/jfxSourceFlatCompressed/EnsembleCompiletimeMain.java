package ensemble.compiletime;
import ensemble.compiletime.search.BuildEnsembleSearchIndex;
import java.io.File;
import java.util.Arrays;
import java.util.List;
public class EnsembleCompiletimeMain {
static {
System.setProperty("java.net.useSystemProxies", "true");
}
public static void main(String[] args) {
System.out.println("==================================================================");
System.out.println("                 Ensemble Compile Time Build");
System.out.println("args = "+Arrays.toString(args));
System.out.println("==================================================================");
File ensembleDir = new File(System.getProperty("user.dir"));
System.out.println("ensembleDir = " + ensembleDir+" - "+ensembleDir.exists());
File generatedSrcDir = new File(ensembleDir,"src/generated/java/ensemble/generated");
System.out.println("generatedSrcDir = " + generatedSrcDir+" - "+generatedSrcDir.exists());
generatedSrcDir.mkdirs();
System.out.println("generatedSrcDir = " + generatedSrcDir.getAbsolutePath());
File samplesDir = new File(ensembleDir,"src/samples/java");
System.out.println("samplesDir = " + samplesDir.getAbsolutePath());
File resourcesDir = new File(ensembleDir,"src/samples/resources");
System.out.println("resourcesDir = " + resourcesDir.getAbsolutePath());
boolean buildSearchIndex = false, buildSampleClass = false;
for (int a=0; a< args.length; a++) {
if (args[a].equalsIgnoreCase("index")) buildSearchIndex = true;
if (args[a].equalsIgnoreCase("samples")) buildSampleClass = true;
}
System.out.println("buildSearchIndex = " + buildSearchIndex);
System.out.println("buildSampleClass = " + buildSampleClass);
List<Sample> allSamples = BuildSamplesList.build(samplesDir, resourcesDir, buildSampleClass ? new File(generatedSrcDir,"Samples.java") : null);
System.out.println("TOTAL SAMPLES = " + allSamples.size());
if (buildSampleClass) {
System.out.println("==================================================================");
System.out.println("                 Written Samples.java class file");
System.out.println("==================================================================");
}
System.out.println("buildSearchIndex = " + buildSearchIndex);
if(buildSearchIndex) {
System.out.println("==================================================================");
System.out.println("                     Building Search Index");
System.out.println("==================================================================");
File indexDir = new File(ensembleDir,"src/generated/resources/ensemble/search/index");
indexDir.mkdirs();
BuildEnsembleSearchIndex.buildSearchIndex(
allSamples,
"https://docs.oracle.com/javase/8/javafx/api/",
"https://docs.oracle.com/javafx/index.html",
indexDir);
}
}
}
