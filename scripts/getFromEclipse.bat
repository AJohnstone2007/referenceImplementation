@echo on
rem created 18 October 2023 from the equivalent file in \csle\dev\art
del/q artForSLE23.jar
rd /s/q src
rd /s/q bin
rd /s/q cpp

rem copy src directory from Adrian's Eclipse workspace
md src
md bin
xcopy /S \adrian\eclipse\referenceImplementation\src\*.* src

rem compile everything
cd bin
javac -d . -cp ..\src ..\src\uk\ac\rhul\cs\csle\art\ART.java
javac -d . -cp ..\src ..\src\uk\ac\rhul\cs\csle\art\ARTFX.java --module-path="C:\openJFX\javafx-sdk-17.0.9\lib" --add-modules=javafx.controls
rem make the jar file
jar cfm ..\art.jar ..\manifest.local *

cd ..
rem rd /s/q bin
