package ensemble.util;
import ensemble.PlatformFeatures;
import ensemble.SampleCategory;
import ensemble.SampleInfo;
import java.util.ArrayList;
import java.util.List;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
public class FeatureChecker {
public static boolean isSampleSupported(SampleInfo sample) {
ConditionalFeature[] cf = sample.conditionalFeatures;
for (ConditionalFeature oneCF : cf) {
if (!Platform.isSupported(oneCF)) {
return false;
}
}
if (PlatformFeatures.USE_EMBEDDED_FILTER && !sample.runsOnEmbedded) {
return false;
}
return true;
}
public static SampleInfo[] filterSamples(SampleInfo[] samples) {
if (samples != null) {
List<SampleInfo> filteredSampleInfos = new ArrayList<>();
for (SampleInfo oneSampleInfo : samples) {
if (isSampleSupported(oneSampleInfo)) {
filteredSampleInfos.add(oneSampleInfo);
}
}
return filteredSampleInfos.toArray(new SampleInfo[filteredSampleInfos.size()]);
} else {
return null;
}
}
public static SampleCategory[] filterEmptyCategories(SampleCategory[] subCategories) {
if (subCategories != null) {
List<SampleCategory> filteredSubcategories = new ArrayList<>();
for (SampleCategory subCategory : subCategories) {
if (subCategory.samples != null && subCategory.samples.length > 0
|| subCategory.samplesAll != null && subCategory.samplesAll.length > 0) {
filteredSubcategories.add(subCategory);
}
}
return filteredSubcategories.toArray(new SampleCategory[filteredSubcategories.size()]);
} else {
return null;
}
}
}
