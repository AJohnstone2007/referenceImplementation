package pseudoScriptEngineCompilable;
import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
public class RgfPseudoCompiledScript extends CompiledScript {
String code = null;
ScriptEngine scriptEngine = null;
RgfPseudoCompiledScript(String code, ScriptEngine scriptEngine) {
this.code = code;
this.scriptEngine = scriptEngine;
}
public Object eval(Bindings bindings) throws ScriptException {
return scriptEngine.eval("RgfPseudoCompiledScript.eval(Bindings bindings): " + code, bindings);
}
public Object eval(ScriptContext context) throws ScriptException {
return scriptEngine.eval("RgfPseudoCompiledScript.eval(ScriptContext context): " + code, context);
}
public Object eval() throws ScriptException {
return scriptEngine.eval("RgfPseudoCompiledScript.eval(): " + code );
}
public ScriptEngine getEngine() {
return scriptEngine;
}
}
