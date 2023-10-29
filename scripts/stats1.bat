rem run gllbl and gllhp once on %1 and %2

java -jar ..\referenceImplementation.jar !gllbl %1 %2 !statistics >> log.csv
java -jar ..\referenceImplementation.jar !gllhp %1 %2 !statistics >> log.csv
