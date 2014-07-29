/*
 * LabelPrinter.java
 * Copyright (c) 2007-2013 Aaron C Spike
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

import java.applet.Applet;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.Printable;
import java.awt.print.PageFormat;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.SimpleDoc;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.PrintException;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class LabelPrinter extends Applet {
    static class LabelPrintable implements Printable {
        private String label_text = "";
        private float max_font_size = 32.0f;
        
        public LabelPrintable(String text, float size) {
            label_text = text;
            max_font_size = size;
        }
        
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D)g;
            
            Rectangle2D.Double r = (Rectangle2D.Double)g2d.getClip();
            
            g2d.translate(r.getWidth(),0.0);
            g2d.rotate(Math.PI/2.0);
            
            String[] lines = label_text.split("[\n\r\f]+");
            
            // height is width because we rotated the text vertical
            double max_height = r.getWidth();
            double line_height = max_height / lines.length;
            
            // try to find an appropriately sized font
            float points = 2f;
            float font_height = 0;
            while (font_height < line_height) {
                points += 1f;
                font_height = g2d.getFontMetrics(g2d.getFont().deriveFont(points)).getHeight();
            }
            
            points -= 1;
            
            if (points > max_font_size) {
                points = max_font_size;
            }
            
            g2d.setFont(g2d.getFont().deriveFont(points));
            
            int x_margin = 15;
            int y_margin = g2d.getFontMetrics().getAscent();
            int y_advance = g2d.getFontMetrics().getHeight();
            
            for (int i = 0; i < lines.length; i++) {
                g.drawString(lines[i], x_margin, y_margin + (i * y_advance));
            }
        }
        
        public int print(Graphics g, PageFormat pageFormat, int pageIndex) {            
            int x = (int)pageFormat.getImageableX();
            int y = (int)pageFormat.getImageableY();
            
            if (pageIndex == 0) {
                paint(g);
                return Printable.PAGE_EXISTS;
            } else {
                return Printable.NO_SUCH_PAGE;
            }
        }
    }
    
    static class PrivilegedPrinterLookup implements PrivilegedAction {
        private String printer_name;
        private DocFlavor flavor;
        
        public PrivilegedPrinterLookup(String p, DocFlavor df) {
            if (p != null) {
                printer_name = p;
            } else {
                printer_name = "";
            }
            flavor = df;
        }
        
        public PrintService run() {
            PrintService service = PrintServiceLookup.lookupDefaultPrintService();
            if (printer_name != "") { 
                PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
                for (int i = 0; i < services.length; i++) {
                    String name = services[i].getName();
                    if (name.matches(printer_name)) {
                        service = services[i];
                        break;
                    }
                }
            }
            return service;
        }
    }
        
    static class PrivilegedPrint implements PrivilegedAction {
        private DocPrintJob job;
        private Doc doc;
        private HashPrintRequestAttributeSet aset;
        
        public PrivilegedPrint(DocPrintJob j, Doc d, HashPrintRequestAttributeSet a) {
            job = j;
            doc = d;
            aset = a;
        }
        
        public Object run() {
            try {
                job.print(doc, aset);
            }  catch (PrintException e) { 
                System.err.println(e);
            }
            return null;
        }
    }
    
    public String[][] getParameterInfo() {
        String[][] info = {
            {"PrinterName",     "regex",          "string to match against the printer name"},
            {"MaxFontSize",     "float",          "maximum allowed font size"},
        };
        return info;
    }    
    
    public void print_label(String label_text) {
        System.out.println("Printing: " + label_text);
        
        String printer_name = "";
        float max_font_size = 32.0f;
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        PrintService service;
        
        // read parameters
        String printer_name_param = getParameter("PrinterName");
        if (printer_name_param != null & printer_name_param != "") {
            printer_name = printer_name_param;
        }
        String max_font_size_param = getParameter("MaxFontSize");
        if (max_font_size_param != null) {
            try {
                max_font_size = Float.parseFloat(max_font_size_param);
            } catch (NumberFormatException e) {
                //Use default
            }
        }
        
        LabelPrintable label = new LabelPrintable(label_text, max_font_size);
        
        // find printer
        PrivilegedPrinterLookup ppl = new PrivilegedPrinterLookup(printer_name, flavor);
        service = AccessController.doPrivileged((PrivilegedAction<PrintService>)ppl);
        
        if (service == null) {
            System.err.println("Unable to select printer. Name: " + printer_name + " Flavor: " + flavor);
            return;
        }
        
        System.out.println("Using printer: " + service.getName());
        
        DocPrintJob job = service.createPrintJob();
        
        Doc doc = new SimpleDoc(label, flavor, null);
        
        HashPrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        MediaPrintableArea pa = new MediaPrintableArea(.15f,.2f,1f,3.2f,MediaPrintableArea.INCH);
        aset.add(pa);
        aset.add(OrientationRequested.PORTRAIT);
        
        PrivilegedPrint pp = new PrivilegedPrint(job, doc, aset);
        AccessController.doPrivileged((PrivilegedAction<Object>)pp);
		System.out.println("Finished");
    }
}
