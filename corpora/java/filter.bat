@echo off
echo %1
copy jfxSourceFlat\%1 uncompressed.java > nul:
java -jar \csle\dev\art\art.jar !compressWhitespaceJava uncompressed compressed
move compressed.java jfxSourceFlatCompressed\%1 > nul:
