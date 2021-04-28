package dev.pxml.core.reader.parser.eventHandler;

import dev.pxml.core.data.node.PXMLNode;
import dev.pxml.core.reader.reader.TextLocation;
import dev.pxml.core.reader.parser.EventStreamParserUtilities;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class CreateDOM_ParserEventHandler implements IParserEventsHandler<Node, Document> {

    Document document;

    public CreateDOM_ParserEventHandler() {}

    public void onStart() throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();

        this.document = docBuilder.newDocument();
        // this.document.setXmlStandalone ( true );
    }

    public void onStop() {
        // do nothing
    }

    public Node onRootNodeStart ( PXMLNode rootNode ) throws Exception {

        Element rootElement = createElement ( rootNode );
        document.appendChild ( rootElement );
        return rootElement;
    }

    public void onRootNodeEnd ( Node rootElement ) {
        // do nothing
    }

    public Node onNodeStart ( PXMLNode rootNode, Node parentElement ) throws Exception {

        if ( parentElement instanceof Attr ) {
            EventStreamParserUtilities.throwNestedMetaDataNotAllowedInXML (
                rootNode.getLineNumber(), rootNode.getColumnNumber() );
        }

        if ( ! rootNode.isAttribute() ) {
            Element element = createElement ( rootNode );
            parentElement.appendChild ( element );
            return element;

        } else {
            Attr attribute;
            Element element = (Element) parentElement;
            if ( ! rootNode.hasNamespace() ) {
                attribute = document.createAttribute ( rootNode.getLocalName() );
                element.setAttributeNode ( attribute );
            } else {
                attribute = document.createAttributeNS (
                    rootNode.getNamespaceURI().toString(), rootNode.getName().toString() );
                element.setAttributeNodeNS(attribute);
            }
            return attribute;
        }
    }

    public void onNodeEnd ( Node element ) throws Exception {
        // do nothing
    }

    public void onText ( String text, Node parentElement, TextLocation location ) throws Exception {

        if ( parentElement instanceof Element ) {
            Text textElement = document.createTextNode ( text );
            parentElement.appendChild ( textElement );

        } else if ( parentElement instanceof Attr ) {
            Attr attribute = (Attr) parentElement;
            attribute.setValue ( text );

        } else {
            throw new RuntimeException ( "Unexpected parentElement " + parentElement.toString() );
        }
    }

    public void onComment ( String comment, Node parentElement, TextLocation location ) throws Exception {

        String stripped = EventStreamParserUtilities.stripStartAndEndFromComment ( comment );
        Comment comment_ = document.createComment ( stripped );
        parentElement.appendChild ( comment_ );
    }

    public Document getResult() {
        return document;
    }

    private Element createElement ( PXMLNode node ) {

        if ( ! node.hasNamespace() ) {
            return document.createElement ( node.getLocalName() );
        } else {
            return document.createElementNS(
                node.getNamespaceURI().toString(), node.getName().toString() );
        }
    }
}
