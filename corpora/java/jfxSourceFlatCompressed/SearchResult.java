package ensemble.search;
public class SearchResult {
private DocumentType documentType;
private String name;
private String url;
private String className;
private String packageName;
private String ensemblePath;
private String shortDescription;
public SearchResult(DocumentType documentType, String name, String url, String className, String packageName, String ensemblePath, String shortDescription) {
this.documentType = documentType;
this.name = name;
this.url = url;
this.className = className;
this.packageName = packageName;
this.ensemblePath = ensemblePath;
this.shortDescription = shortDescription;
}
public DocumentType getDocumentType() {
return documentType;
}
public String getName() {
return name;
}
public String getUrl() {
return url;
}
public String getClassName() {
return className;
}
public String getPackageName() {
return packageName;
}
public String getEnsemblePath() {
return ensemblePath;
}
public String getShortDescription() {
return shortDescription;
}
@Override public String toString() {
return documentType +"::"+name+
"\n" + shortDescription;
}
public String debugToString() {
return "SearchResult{" +
"\n     documentType=" + documentType +
"\n     name='" + name + '\'' +
"\n     url='" + url + '\'' +
"\n     className='" + className + '\'' +
"\n     packageName='" + packageName + '\'' +
"\n     ensemblePath='" + ensemblePath + '\'' +
"\n     shortDescription='" + shortDescription + '\'' +
"\n}";
}
}
