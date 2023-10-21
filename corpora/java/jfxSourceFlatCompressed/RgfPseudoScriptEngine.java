package pseudoScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.AbstractScriptEngine;
import javax.script.SimpleScriptContext;
import javax.script.SimpleBindings;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.Instant;
public class RgfPseudoScriptEngine extends AbstractScriptEngine {
static final boolean bDebug = false;
static final ArrayList<RgfPseudoScriptEngine> enginesUsed = new ArrayList();
public static ArrayList<RgfPseudoScriptEngine> getEnginesUsed() {
return enginesUsed;
}
public RgfPseudoScriptEngine() {
enginesUsed.add(this);
}
public ScriptEngineFactory getFactory() {
return new RgfPseudoScriptEngineFactory();
}
final ArrayList<InvocationInfos> invocationList = new ArrayList();
public ArrayList<InvocationInfos> getInvocationList() {
return invocationList;
}
public Bindings createBindings() {
return new SimpleBindings();
}
public Object eval(Reader reader, ScriptContext context) {
if (bDebug) System.err.println("[debug: " + this + ".eval(Reader,ScriptContext), ScriptContext=" + context + "]");
return eval(readReader(reader), context);
}
public Object eval(String script, ScriptContext context) {
if (bDebug) System.err.print("[debug: " + this + ".eval(String,ScriptContext), ScriptContext=" + context + "]");
TreeMap<Integer,TreeMap> bindings = new TreeMap();
for (Integer scope : context.getScopes()) {
Bindings binding = context.getBindings(scope);
bindings.put(scope, binding == null ? new TreeMap<String,Object>() : new TreeMap<String,Object>(binding));
}
invocationList.add(new InvocationInfos(script,context));
if (bDebug) System.err.println(" | invocationList.size()=" + invocationList.size());
return invocationList;
}
String readReader(Reader reader) {
if (reader == null) {
return "";
}
BufferedReader bufferedReader = new BufferedReader(reader);
StringBuilder sb = new StringBuilder();
try {
try {
char[] charBuffer = new char[1024];
int r = 0;
while ((r = bufferedReader.read(charBuffer)) != -1) {
sb.append(charBuffer, 0, r);
}
} finally {
bufferedReader.close();
}
} catch (IOException ioe) {
throw new RuntimeException(ioe.getMessage(), ioe);
}
return sb.toString();
}
}
