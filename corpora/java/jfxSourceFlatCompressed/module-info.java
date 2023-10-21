module mymod {
requires javafx.controls;
requires javafx.fxml;
requires java.scripting;
provides javax.script.ScriptEngineFactory with pseudoScriptEngineCompilable.RgfPseudoScriptEngineCompilableFactory;
exports pseudoScriptEngineCompilable;
exports myapp2;
}
