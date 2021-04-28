package dev.pxml.core.utilities;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class XMLUtilities {

    public static String getOfficialNamespaceURI ( String namespacePrefix ) {

        if ( namespacePrefix.equals ( XMLConstants.XMLNS_ATTRIBUTE ) ) {
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        } else if ( namespacePrefix.equals ( XMLConstants.XML_NS_PREFIX ) ) {
            return XMLConstants.XML_NS_URI;
        } else {
            return null;
        }
    }

    public static Document XMLFileToXMLDocument( File XMLFile ) throws Exception {
        return readXMLDocument ( new FileReader ( XMLFile ) );
    }

    public static Document readXMLDocument ( Reader reader ) throws Exception {

        // long startTimeNanos = System.nanoTime();

        // DocumentBuilderFactory factory = DocumentBuilderFactory.newNSInstance(); // Java version >= 13
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware ( true ); // important!

        DocumentBuilder builder = factory.newDocumentBuilder();

        /*
        Document result = builder.parse ( new InputSource( reader ) );
        long endTimeNanos = System.nanoTime();
        long time = endTimeNanos - startTimeNanos;
        long micros = TimeUnit.NANOSECONDS.toMicros ( time );
        System.out.println ( "readXMLDocument time " + String.valueOf ( micros ) + " microseconds" );
        return result;
        */

        return builder.parse ( new InputSource( reader ) );
    }

    public static void writeXMLDocumentToFile ( Document document, File XMLFile ) throws Exception {
        writeXMLDocument ( document, new FileWriter( XMLFile, StandardCharsets.UTF_8 ) );
    }

    public static void writeXMLDocumentToOSOut ( Document document ) throws Exception {
        writeXMLDocument ( document, new PrintWriter( System.out ) );
    }

    public static void writeXMLDocument ( Document document, Writer writer ) throws Exception {

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        DOMSource source = new DOMSource ( document );

        // transformer.setOutputProperty ( OutputKeys.INDENT, "yes" );
        // transformer.setOutputProperty ( "{http://xml.apache.org/xslt}indent-amount", "4" );
        // transformer.setOutputProperty ( OutputKeys.OMIT_XML_DECLARATION, "yes" );

        transformer.transform ( source, new StreamResult ( writer ) );
    }
}
