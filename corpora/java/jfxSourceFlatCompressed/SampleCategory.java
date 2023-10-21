package ensemble.compiletime;
import java.util.ArrayList;
import java.util.List;
import static ensemble.compiletime.CodeGenerationUtils.sampleArrayToCode;
import static ensemble.compiletime.CodeGenerationUtils.stringToCode;
public class SampleCategory {
public final String name;
public final String ensemblePath;
public final List<Sample> samples = new ArrayList<Sample>();
public final List<Sample> samplesAll = new ArrayList<Sample>();
public final List<SampleCategory> subCategories = new ArrayList<SampleCategory>();
public final SampleCategory parent;
public SampleCategory(String name, String ensemblePath, SampleCategory parent) {
this.name = name;
this.ensemblePath = ensemblePath;
this.parent = parent;
}
public void addSample(Sample sample) {
samples.add(sample);
System.out.println("******** FINDING TOP CATEGORY FOR ["+name+"]");
SampleCategory topCategory = this;
System.out.println("            topCategory = "+topCategory.name);
while (topCategory.parent != null && topCategory.parent.parent != null) {
topCategory = topCategory.parent;
System.out.println("            topCategory = "+topCategory.name);
}
System.out.println("            FINAL topCategory = "+topCategory.name);
topCategory.samplesAll.add(sample);
}
public String generateCode() {
StringBuilder sb = new StringBuilder();
sb.append("new SampleCategory(");
sb.append(stringToCode(name)); sb.append(',');
sb.append(sampleArrayToCode(samples)); sb.append(',');
sb.append(sampleArrayToCode(samplesAll)); sb.append(',');
categoryArrayToCode(sb, subCategories);
sb.append(")");
return sb.toString();
}
private void categoryArrayToCode(StringBuilder sb, List<SampleCategory> array) {
if (array == null || array.isEmpty()) {
sb.append("null");
} else {
sb.append("new SampleCategory[]{");
for (SampleCategory category: array) {
sb.append(category.generateCode());
sb.append(',');
}
sb.append("}");
}
}
}
