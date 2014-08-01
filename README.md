#LabelPrinterApplet

LabelPrinter.java is a concise Java applet developed to print mailing labels 
from web applications to Seiko SmartLabel printers. The text of the label is
specified at print time via JavaScript. Printer names can be specified as a 
regular expression to allow matching against various models.

## License

LabelPrinter.java is released under an MIT license.

## Windows Building How-To

1. Edit path in set_path.bat
2. Edit domain in addToManifest.txt from addToManifest.txt.sample
3. Obtain signing certificate
   
   [K Software](http://codesigning.ksoftware.net/) is a reasonably
   priced source of trusted code signing certificates. If possible, Internet 
   Explorer is recommended for purchasing and collecting the certificate to
   avoid certificate path chaining issues.
   
4. build.bat

## Commands similar to the following may be useful:
    #compile
    javac LabelPrinter.java
    
    #package
    jar cvf LabelPrinter.jar Label*.class
    
    #create a selfcert that is valid for 50 whopping years!!!
    keytool -selfcert -validity 18250 -genkey -alias sign -keystore keystore
    
    #self-sign the jar
    jarsigner -keystore keystore -signedjar sLabelPrinter.jar LabelPrinter.jar sign
	
	#list certficates to obtain the alias
	keytool -list -verbose -keystore signing_key.pfx -storetype pkcs12
	
	#sign the jar
	jarsigner -tsa http://timestamp.comodoca.com/rfc3161 -digestalg SHA1 -verbose -strict -keystore signing_key.pfx -storetype pkcs12 -signedjar sLabelPrinter.jar LabelPrinter.jar signing_key
