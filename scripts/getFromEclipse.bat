@echo on
rem created 18 October 2023 from the equivalent file in \csle\dev\art
del/q artForSLE23.jar
rd /s/q src
rd /s/q bin
rd /s/q examples

rem update build version
pushd \adrian\eclipse\referenceImplementation\bin
java uk.ac.rhul.cs.csle.art.ARTVersionUpdate
move ARTVersion.java.new \adrian\eclipse\referenceImplementation\src\uk\ac\rhul\cs\csle\art\ARTVersion.java
move manifest.local.new \csle\dev\referenceImplementation\manifest.local
popd

rem copy src directory from Adrian's Eclipse workspace
md src
md bin
md examples
xcopy /S \adrian\eclipse\referenceImplementation\src\*.* src
xcopy /S \adrian\eclipse\referenceImplementation\examples\*.* examples

rem compile everything
cd bin
javac -d . -cp ..\src ..\src\uk\ac\rhul\cs\csle\art\ART.java --module-path="C:\openJFX\javafx-sdk-17.0.9\lib" --add-modules=javafx.controls
rem make the jar file
jar cfm ..\artUNSTABLE.jar ..\manifest.local *

cd ..
rem rd /s/q bin
