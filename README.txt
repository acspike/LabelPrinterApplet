LabelPrinter.java is a concise Java applet developed to print mailing labels 
from web applications to Seiko SmartLabel printers. The text of the label is
specified at print time via JavaScript. Printer names can be specified as a 
regular expression to allow matching against various models.

LabelPrinter.java is released under an MIT license.

Commands similar to the following may be useful:
    #compile
    javac LabelPrinter.java
    
    #package
    jar cvf LabelPrinter.jar Label*.class
    
    #create a selfcert that is valid for 50 whopping years!!!
    keytool -selfcert -validity 18250 -genkey -alias sign -keystore keystore
    
    #sign the jar
    jarsigner -keystore keystore -signedjar sLabelPrinter.jar LabelPrinter.jar sign
