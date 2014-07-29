del Label*.class
javac -Xlint:unchecked LabelPrinter.java
jar cvf LabelPrinter.jar Label*.class
jar ufm LabelPrinter.jar addToManifest.txt
jarsigner -tsa http://timestamp.digicert.com -keystore keystore -signedjar sLabelPrinter.jar LabelPrinter.jar sign