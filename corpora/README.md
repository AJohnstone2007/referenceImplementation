Notes on the creation of the corpora
------------------------------------

rhulStrings is our standard set examples as used in various papers

# Bulk source files

jfx-master collected from https://github.com/openjdk/jfx 5 July 2022 15.15 BST
ml-works collected from https://github.com/Ravenbrook/mlworks 5 July 2022 15.30 BST

# Procedure for creating flattened directories

1. Unzip one of the source zip files
2. dir /b /s xyz-master\*.lll > xyzFlatten.bat
3. Use emacs to convert each line <file> of xyzSourceCopy.bat to: copy <file> xyzSource
4. Run xyzFlatten.bat

# Summary file counts after flattening

Github		Directory	In zip		After flat copy		(lost)	[proportion]
download			Files		Files	Bytes				

jfx-master	jfxSource	 4 877		4 588	43 568 050 	 (289)	[-5.9%]	
mlWorks-master	mlwSource	 1 984		1 798	13 188 297	 (186)	[-9.4%]

# Java corpus: filter comments and spaces

ART directive !compressWhitespaceJava idJAVA idJAVA replaces all strings of whitespace with either a single space, or if the string contains a newline, a single '\n'
In addition, all Unicode characters that are not ASCII are mapped to \uXXXX.

Batchfile java\filter.bat creates javaSourceCompressed from the files in javaSource

> See ART source file ARTCompressWhiteSpaceJava.java for details of the compression

# SML corpus: filter comments and spaces, and attach a terminating ;

ART directive !compressWhitespaceSML idSML idSML replaces all strings of whitespace with either a single space, or if the string contains a newline, a single '\n'

In addition, all Unicode characters that are not ASCII are mapped to \uXXXX.

Batchfile SML\filter.bat creates smlSourceCompressed from the files in smlSource

> See ART source file ARTCompressWhiteSpaceSML.java for details of the compression
