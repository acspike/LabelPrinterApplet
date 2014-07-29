del Label*.class
javac -Xlint:unchecked LabelPrinter.java
jar cvf LabelPrinter.jar Label*.class
jarsigner -keystore keystore -signedjar sLabelPrinter.jar LabelPrinter.jar sign