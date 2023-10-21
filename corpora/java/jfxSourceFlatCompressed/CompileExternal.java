import com.sun.scenario.effect.compiler.JSLC;
import com.sun.scenario.effect.compiler.JSLC.JSLCInfo;
public class CompileExternal {
public static void main(String[] args) throws Exception {
JSLCInfo jslcinfo = new JSLCInfo("<filename>");
int index = jslcinfo.parseArgs(args);
if (index >= args.length) {
jslcinfo.error("No JSL file specified");
}
while (index < args.length) {
jslcinfo.shaderName = args[index++];
JSLC.compile(jslcinfo, jslcinfo.getJSLFile());
}
}
}
