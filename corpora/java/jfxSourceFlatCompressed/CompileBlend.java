import com.sun.scenario.effect.compiler.JSLC;
import com.sun.scenario.effect.compiler.JSLC.JSLCInfo;
import com.sun.scenario.effect.Blend;
import java.io.File;
import java.util.Locale;
public class CompileBlend {
public static void main(String[] args) throws Exception {
JSLCInfo jslcinfo = new JSLCInfo();
jslcinfo.shaderName = "Blend";
jslcinfo.parseArgs(args);
File mainFile = jslcinfo.getJSLFile();
String main = CompileJSL.readFile(mainFile);
long blendtime = mainFile.lastModified();
for (Blend.Mode mode : Blend.Mode.values()) {
String funcname = mode.name().toLowerCase(Locale.ENGLISH);
String modename = jslcinfo.shaderName + "_" + mode.name();
File funcFile = jslcinfo.getJSLFile(modename);
String func = CompileJSL.readFile(funcFile);
long modeTime = funcFile.lastModified();
String source = String.format(main, func, funcname);
long sourcetime = Math.max(blendtime, modeTime);
jslcinfo.peerName = modename;
JSLC.compile(jslcinfo, source, sourcetime);
}
}
}
