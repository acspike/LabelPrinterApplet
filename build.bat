del Label*.class
javac -Xlint:unchecked LabelPrinter.java
jar cvf LabelPrinter.jar Label*.class
jar ufm LabelPrinter.jar addToManifest.txt
jarsigner -tsa http://timestamp.comodoca.com/rfc3161 -digestalg SHA1 -verbose -strict -keystore signing_key.pfx -storetype pkcs12 -signedjar sLabelPrinter.jar LabelPrinter.jar signing_key
