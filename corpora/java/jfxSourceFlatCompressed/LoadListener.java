package javafx.fxml;
public interface LoadListener {
public void readImportProcessingInstruction(String target);
public void readLanguageProcessingInstruction(String language);
public void readComment(String comment);
public void beginInstanceDeclarationElement(Class<?> type);
public void beginUnknownTypeElement(String name);
public void beginIncludeElement();
public void beginReferenceElement();
public void beginCopyElement();
public void beginRootElement();
public void beginPropertyElement(String name, Class<?> sourceType);
public void beginUnknownStaticPropertyElement(String name);
public void beginScriptElement();
public void beginDefineElement();
public void readInternalAttribute(String name, String value);
public void readPropertyAttribute(String name, Class<?> sourceType, String value);
public void readUnknownStaticPropertyAttribute(String name, String value);
public void readEventHandlerAttribute(String name, String value);
public void endElement(Object value);
}
