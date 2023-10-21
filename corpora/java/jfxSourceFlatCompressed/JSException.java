package netscape.javascript;
public class JSException extends RuntimeException {
public static final int EXCEPTION_TYPE_EMPTY = -1;
public static final int EXCEPTION_TYPE_VOID = 0;
public static final int EXCEPTION_TYPE_OBJECT = 1;
public static final int EXCEPTION_TYPE_FUNCTION = 2;
public static final int EXCEPTION_TYPE_STRING = 3;
public static final int EXCEPTION_TYPE_NUMBER = 4;
public static final int EXCEPTION_TYPE_BOOLEAN = 5;
public static final int EXCEPTION_TYPE_ERROR = 6;
public JSException() {
this(null);
}
public JSException(String s) {
this(s, null, -1, null, -1);
}
public JSException(String s, String filename, int lineno, String source,
int tokenIndex) {
super(s);
this.message = s;
this.filename = filename;
this.lineno = lineno;
this.source = source;
this.tokenIndex = tokenIndex;
this.wrappedExceptionType = EXCEPTION_TYPE_EMPTY;
}
public JSException(int wrappedExceptionType, Object wrappedException) {
this();
this.wrappedExceptionType = wrappedExceptionType;
this.wrappedException = wrappedException;
}
protected String message = null;
protected String filename = null;
protected int lineno = -1;
protected String source = null;
protected int tokenIndex = -1;
private int wrappedExceptionType = -1;
private Object wrappedException = null;
public int getWrappedExceptionType() {
return wrappedExceptionType;
}
public Object getWrappedException() {
return wrappedException;
}
}
