package dev.pxml.core.utilities;

import dev.pxml.core.utilities.annotations.NotNull;
import dev.pxml.core.writer.IPXMLWriter;
import dev.pxml.core.writer.PXMLWriter;
import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import javax.xml.transform.dom.DOMSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class XMLToPXMLConverter {

    public static void XMLFileToPXMLFile ( File XMLFile, File pXMLFile ) throws Exception {

        final FileReader XMLFileReader = new FileReader ( XMLFile, StandardCharsets.UTF_8 );
        final FileWriter pXMLFileWriter = new FileWriter ( pXMLFile, StandardCharsets.UTF_8 );
        pipeXMLReaderToPXMLWriter ( XMLFileReader, pXMLFileWriter );
        XMLFileReader.close();
        pXMLFileWriter.close();
    }

    // doesn't close reader nor writer
    public static void pipeXMLReaderToPXMLWriter ( Reader XMLReader, Writer pXMLWriter ) throws Exception {

        XMLInputFactory factory = XMLInputFactory.newFactory();
        XMLEventReader XMLEventReader = factory.createXMLEventReader ( XMLReader );
        XMLEventReaderToPXML ( XMLEventReader, pXMLWriter );
    }

    public static void XMLDocumentToPXML (
        @NotNull Document XMLDocument, @NotNull Writer pXMLWriter ) throws Exception {

        XMLInputFactory factory = XMLInputFactory.newFactory();
        XMLEventReader XMLEventReader = factory.createXMLEventReader ( new DOMSource ( XMLDocument ) );
        XMLEventReaderToPXML ( XMLEventReader, pXMLWriter );
    }

    private static void XMLEventReaderToPXML (
        @NotNull XMLEventReader XMLEventReader, @NotNull Writer pXMLWriter ) throws Exception {

        /*
            Using an XMLEventReader instead of SAXParser to read XML has advantages:
                provides better data about events
                optionally provides lineNumber and columnNumber
        */

        IPXMLWriter pXMLWriter_ = new PXMLWriter( pXMLWriter );
        while ( XMLEventReader.hasNext() ) {
            XMLEvent XMLEvent = XMLEventReader.nextEvent();
            handleXMLEvent ( XMLEvent, pXMLWriter_, XMLEventReader );
        }
    }

    private static void handleXMLEvent ( XMLEvent XMLEvent, IPXMLWriter pXMLWriter, XMLEventReader XMLEventReader )
        throws IOException, XMLStreamException {

        if ( XMLEvent.isStartElement() ) {
            handleStartElement ( (StartElement) XMLEvent, pXMLWriter, XMLEventReader.peek().isEndElement() );

        } else if ( XMLEvent.isEndElement() ) {
            pXMLWriter.writeNodeEndSymbol();

        } else if ( XMLEvent.isCharacters() ) {
            pXMLWriter.escapeAndWriteText( ((Characters) XMLEvent).getData() );

        } else if ( XMLEvent instanceof Comment ) {
            pXMLWriter.writeComment ( ((Comment)XMLEvent).getText() );

        } else if ( XMLEvent.isStartDocument() ) {
            pXMLWriter.startDocument();

        } else if ( XMLEvent.isEndDocument() ) {
            pXMLWriter.endDocument();

        // TODO add other events (ProcessingInstruction, etc.)

        } else {
            throw new RuntimeException ( "Event '" + XMLEvent.toString() + "' is not yet supported." );
        }

    }

    private static void handleStartElement ( StartElement XMLEvent, IPXMLWriter pXMLWriter, boolean isEmptyElement )
        throws IOException {

        QName name = XMLEvent.getName();
        if ( isEmptyElement ) {
            pXMLWriter.writeNodeStart ( name.getPrefix(), name.getLocalPart(), false );
        } else {
            pXMLWriter.writeNonEmptyNodeStart ( name.getPrefix(), name.getLocalPart() );
            // namespaces are not available as attributes
            handleNamespacesAndAttributes ( XMLEvent.getNamespaces(), XMLEvent.getAttributes(), pXMLWriter );
        }
    }

    private static void handleNamespacesAndAttributes (
        Iterator<Namespace> namespaces, Iterator<Attribute> attributes, IPXMLWriter pXMLWriter ) throws IOException {

        boolean hasNamespaces = namespaces != null && namespaces.hasNext();
        boolean hasAttributes = attributes != null && attributes.hasNext();

        if ( ! hasNamespaces && ! hasAttributes ) return;

        pXMLWriter.writeAttributesStart();

        // write namespaces as attributes
        while ( namespaces.hasNext() ) {
            Namespace namespace = namespaces.next();

            pXMLWriter.writeAttribute (
                XMLConstants.XMLNS_ATTRIBUTE, namespace.getPrefix(), namespace.getNamespaceURI(),
                namespaces.hasNext() || attributes.hasNext() );
        }

        while ( attributes.hasNext() ) {
            Attribute attribute = attributes.next();

            QName name = attribute.getName();
            pXMLWriter.writeAttribute (
                name.getPrefix(), name.getLocalPart(), attribute.getValue(), attributes.hasNext() );
        }

        pXMLWriter.writeAttributesEnd();
    }
}
