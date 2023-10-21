@echo off
echo %1
copy smlSourceFlat\%1 uncompressed.sml > nul:
java -jar \csle\dev\art\art.jar !compressWhitespaceSML uncompressed compressed
move compressed.sml smlSourceFlatCompressed\%1 > nul:
