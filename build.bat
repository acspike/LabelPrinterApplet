del Label*.class
javac -Xlint:unchecked LabelPrinter.java
jar cvf LabelPrinter.jar Label*.class
jarsigner -tsa http://timestamp.digicert.com -keystore keystore -signedjar sLabelPrinter.jar LabelPrinter.jar sign