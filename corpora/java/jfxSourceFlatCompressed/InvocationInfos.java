package pseudoScriptEngineCompilable;
import javax.script.Bindings;
import javax.script.ScriptContext;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
public class InvocationInfos {
public String script;
public TreeMap<Integer,TreeMap<String,Object>> bindings;
public Instant dateTime;
InvocationInfos(String script, ScriptContext context) {
this.dateTime = Instant.now();
this.script = script;
this.bindings = new TreeMap();
for (Integer scope : context.getScopes()) {
Bindings binding = context.getBindings(scope);
bindings.put(scope, binding == null ? new TreeMap<String,Object>() : new TreeMap<String,Object>(binding));
}
}
public String toDebugFormat(String indentation) {
StringBuilder sb = new StringBuilder();
String indent = (indentation == null ? "\t\t" : indentation);
sb.append(indent).append("at:     [").append(dateTime.toString()).append("]\n");
sb.append(indent).append("script: [").append(script) .append("]\n");
for (Integer scope : (Set<Integer>) bindings.keySet()) {
sb.append(indent).append("Bindings for scope # ").append(scope);
if (scope == 100) sb.append(" (ENGINE_SCOPE):");
else if (scope == 200) sb.append(" (GLOBAL_SCOPE):");
else sb.append(':');
sb.append('\n');
TreeMap<String,Object> treeMap = bindings.get(scope);
for (String k : (Set<String>) treeMap.keySet()) {
sb.append(indent).append("\t[").append(k).append("]:\t[").append(treeMap.get(k)).append("]\n");
}
}
return sb.toString();
}
}
