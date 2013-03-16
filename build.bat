javac LabelPrinter.java
jar cvf LabelPrinter.jar Label*.class
jarsigner -keystore keystore -signedjar sLabelPrinter.jar LabelPrinter.jar sign