package dev.pxml.core.reader.parser.eventHandler;

import dev.pxml.core.Constants;
import dev.pxml.core.data.node.EmptyPXMLNode;
import dev.pxml.core.data.node.PXMLNode;
import dev.pxml.core.reader.reader.TextLocation;
import dev.pxml.core.reader.parser.EventStreamParserUtilities;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class WriteXML_ParserEventHandler implements IParserEventsHandler<PXMLNode, String> {

    private final XMLStreamWriter XMLWriter;

    public WriteXML_ParserEventHandler ( Writer writer ) throws XMLStreamException {

        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        // factory.setProperty ( XMLOutputFactory.IS_REPAIRING_NAMESPACES, true );
        this.XMLWriter = factory.createXMLStreamWriter ( writer );
    }

    public WriteXML_ParserEventHandler ( XMLStreamWriter XMLWriter ) {
        this.XMLWriter = XMLWriter;
    }

    public void onStart() throws XMLStreamException {
        XMLWriter.writeStartDocument ( StandardCharsets.UTF_8.name(), "1.0" );
        XMLWriter.writeCharacters ( Constants.newLine );
    }

    public void onStop() throws XMLStreamException {
        XMLWriter.writeEndDocument();
        XMLWriter.writeCharacters ( Constants.newLine );
    }

    public PXMLNode onRootNodeStart ( PXMLNode node ) throws Exception {
        return onNodeStart ( node, null );
    }

    public void onRootNodeEnd ( PXMLNode node ) throws XMLStreamException {
        onNodeEnd ( node );
    }

    public PXMLNode onNodeStart ( PXMLNode node, PXMLNode parentNode ) throws Exception {

        if ( node.isAttribute() ) {

            if ( parentNode.isAttribute() ) {
                EventStreamParserUtilities.throwNestedMetaDataNotAllowedInXML (
                        node.getLineNumber(), node.getColumnNumber() );
            }

            // Attribute will be written when 'onText' is encountered
            return node;
        }

        if ( ! node.hasNamespace() ) {
            if ( node instanceof EmptyPXMLNode ) {
                XMLWriter.writeEmptyElement ( node.getLocalName() );
            } else {
                XMLWriter.writeStartElement ( node.getLocalName() );
            }

        } else {
            if ( node instanceof EmptyPXMLNode ) {
                XMLWriter.writeEmptyElement (
                    node.getNamespacePrefix(),
                    node.getLocalName(),
                    node.getNamespaceURI().toString() );
            } else {
                XMLWriter.writeStartElement(
                    node.getNamespacePrefix(),
                    node.getLocalName(),
                    node.getNamespaceURI().toString() );
            }
        }

        return node;
    }

    public void onNodeEnd ( PXMLNode node ) throws XMLStreamException {

        if ( ! node.isAttribute() && ! (node instanceof EmptyPXMLNode ) ) {
            XMLWriter.writeEndElement();
        }
    }

    public void onText ( String text, PXMLNode parentNode, TextLocation location )
        throws XMLStreamException {

        if ( ! parentNode.isAttribute() ) {
            XMLWriter.writeCharacters ( text );

        } else {
            if ( ! parentNode.getName().hasNamespace() ) {
                XMLWriter.writeAttribute ( parentNode.getName().getLocalName(), text );
            } else {
                XMLWriter.writeAttribute (
                    parentNode.getName().getNamespacePrefix(),
                    parentNode.getName().getNamespaceURI().toString(),
                    parentNode.getName().getLocalName(),
                    text );
            }
        }
    }

    public void onComment ( String comment, PXMLNode parentNode, TextLocation location )
        throws XMLStreamException {

        XMLWriter.writeComment (EventStreamParserUtilities.stripStartAndEndFromComment ( comment ) );
    }

    public String getResult() { return "foo"; }
}
