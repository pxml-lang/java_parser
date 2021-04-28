package dev.pxml.core.reader.parser;

import dev.pxml.core.Constants;
import dev.pxml.core.data.node.EmptyPXMLNode;
import dev.pxml.core.data.node.PXMLNode;
import dev.pxml.core.data.node.NonEmptyPXMLNode;
import dev.pxml.core.reader.reader.TextLocation;
import dev.pxml.core.data.node.name.NodeName;
import dev.pxml.core.reader.error.PXMLDataException;
import dev.pxml.core.reader.parser.eventHandler.IParserEventsHandler;
import dev.pxml.core.reader.tokenizer.ITokenizer;
import dev.pxml.core.utilities.XMLUtilities;

import javax.xml.XMLConstants;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventStreamParser<N, R> extends AEventStreamParser<N, R> {

    private ITokenizer tokenizer;
    private IParserEventsHandler<N, R> eventHandler;
    private Map<String, String> namespacePrefixURIMap;

    public EventStreamParser() {}


    // public methods

    public void parse ( ITokenizer tokenizer, IParserEventsHandler<N, R> eventHandler ) throws Exception {

        this.tokenizer = tokenizer;
        this.eventHandler = eventHandler;
        this.namespacePrefixURIMap = new HashMap<>();

        startParsing();
    }


    // private methods

    private void startParsing() throws Exception {

        eventHandler.onStart();
        requireRootNode();
        eventHandler.onStop();
    }

    private void requireRootNode() throws Exception {

        skipWhitespaceAndComments();

        PXMLNode rootNode = requireNode();
        if ( rootNode.isAttribute() ) throw errorAtLocation (
            "The root node cannot be meta data.", rootNode.getLocation() );
        N eventRootNode = eventHandler.onRootNodeStart( rootNode );

        parseNodeContent ( rootNode, eventRootNode );

        skipWhitespaceAndComments();
        if ( ! tokenizer.isEof() ) throw errorAtCurrentLocation ( "Expecting end of document. More text is not allowed." );
    }

    private PXMLNode requireNode() throws IOException, PXMLDataException, URISyntaxException {

        TextLocation location = currentLocation();

        if ( ! acceptChar ( Constants.nodeStart ) ) throw errorAtCurrentLocation (
            "Expecting character '" + Constants.nodeStart + "' to start a node, but found '" + currentChar() + "'." );

        final boolean isAttribute = false;
        NodeName name = requireName( isAttribute );
        boolean isEmptyNode = currentChar() == Constants.nodeEnd;

        if ( ! isEmptyNode ) {
            return new NonEmptyPXMLNode( name, location, isAttribute );
        } else {
            return new EmptyPXMLNode( name, location, isAttribute );
        }
    }

    private NodeName requireName( boolean isAttribute ) throws IOException, PXMLDataException, URISyntaxException {

        final TextLocation location = currentLocation();

        final String namespacePrefixOrLocalName = requireName();
        final boolean hasNameSpace = acceptChar ( Constants.namespaceSeparator );

        String localName;
        String namespacePrefix;
        URI namespaceURI;

        if ( ! hasNameSpace ) {
            localName = namespacePrefixOrLocalName;
            namespacePrefix = null;
            namespaceURI = null;

        } else {
            namespacePrefix = namespacePrefixOrLocalName;
            if ( ! isAttribute ) {
                if ( namespacePrefix.equals( XMLConstants.XMLNS_ATTRIBUTE ) ) {
                    throw errorAtLocation(
                            "An XML namespace must be declared with an attribute. It cannot be declared with a tag.",
                            location );
                }
            }

            localName = requireName();

            namespaceURI = requireNamespaceURI ( namespacePrefix, location );
        }

        if ( ! isAttribute ) {
            if ( localName.length() >= 3 && localName.substring ( 0, 3 ).equalsIgnoreCase ( "xml" ) ) {
                throw errorAtLocation (
                    "XML names cannot start with \"xml\" (lowercase or uppercase).",
                    location );
            }
        }

        return new NodeName ( localName, namespacePrefix, namespaceURI );
    }

    private URI requireNamespaceURI ( String namespacePrefix, TextLocation location )
        throws URISyntaxException, IOException, PXMLDataException {

        String URIString = XMLUtilities.getOfficialNamespaceURI ( namespacePrefix );

        if ( URIString == null ) {
            URIString = namespacePrefixURIMap.get ( namespacePrefix );
        }

        if ( URIString == null ) {
            // hack to find the namespace URI in the upcoming code
            // (remove when only new syntax for namespaces is allowed)
            String nextCode = tokenizer.peekCurrentNChars( 500 );
            // Pattern nodePattern = Pattern.compile ( "\\[#?xmlns:" + namespacePrefix + "\\s+([^\\]]+)\\s*\\]" );
            // Pattern attributePattern = Pattern.compile ( "xmlns:" + namespacePrefix + "\\s*=\\s*([^ )]+)" );
            Pattern attributePattern = Pattern.compile ( XMLConstants.XMLNS_ATTRIBUTE + ":" + namespacePrefix + "\\s*=\\s*([^ )]+)" );
            Matcher matcher = attributePattern.matcher ( nextCode );
            if ( matcher.find() ) {
                URIString = matcher.group ( 1 );
                if ( URIString.charAt ( 0 ) == Constants.attributeValueDoubleQuote ||
                    URIString.charAt ( 0 ) == Constants.attributeValueSingleQuote ) {
                    // remove quotes
                    URIString = URIString.substring ( 1, URIString.length() - 1 );
                }
            } else {
                throw errorAtLocation (
                    "The namespace declaration for the namespace prefix '" + namespacePrefix + "' could not be found.",
                    location );
            }
        }

        // System.out.println ( "URIString: [" + URIString + "] " + location.toString() );
        return new URI ( URIString );
    }

    private void parseNodeContent ( PXMLNode PXMLParentNode, N handlerParentNode ) throws Exception {

        // we are positioned just after the node name

        if ( acceptChar ( Constants.nodeEnd ) ) {
            // empty node
            eventHandler.onNodeEnd ( handlerParentNode );
            return;
        }

        // boolean hasNameValueSeparator = acceptChar ( Constants.nameValueSeparator ) || isAtEndOfLine();
        boolean hasNameValueSeparator = acceptChar ( Constants.nameValueSeparator )
            || isAtChar ( Constants.attributesStart )
            || isAtEndOfLine();
        if ( ! hasNameValueSeparator ) {
            throw errorAtCurrentLocation (
                "Expecting character '" + Constants.nameValueSeparator + "' (a space) after the node name." );
        }

        parseAttributes ( PXMLParentNode, handlerParentNode );

        while ( true ) {

            // node end?
            if ( acceptChar ( Constants.nodeEnd ) ) {
                eventHandler.onNodeEnd ( handlerParentNode );
                return;

            // node start?
            } else if ( isAtChar ( Constants.nodeStart ) ) {

                if ( tokenizer.isNextChar ( Constants.commentSymbol ) ) {
                    final int lineNumber = currentLineNumber();
                    final int columnNumber = currentColumnNumber();
                    String comment = tokenizer.readComment();
                    eventHandler.onComment ( comment, handlerParentNode, currentLocation() );

                } else {
                    PXMLNode node = requireNode();
                    N eventNode = eventHandler.onNodeStart ( node, handlerParentNode );
                    // recursively parse child nodes
                    parseNodeContent ( node, eventNode );
                }

            // text
            } else {
                final TextLocation location = currentLocation();

                String text = tokenizer.readText();
                assert text != null;
                eventHandler.onText ( text, handlerParentNode, currentLocation() );
            }
        }

    }

    private void parseAttributes ( PXMLNode PXMLParentNode, N handlerParentNode ) throws Exception {

        /*
        if ( tokenizer.peekFirstCharAfterWhiteSpace ( 500 ) != Constants.attributesStart ) return;

        skipWhiteSpace();
        assert acceptChar ( Constants.attributesStart );
        */

        // TODO? skip whitespace and cache it for next text

        // '(' must appear immediately after the node name or after the space after the node name
        if ( ! acceptChar ( Constants.attributesStart ) ) return;

        while ( true ) {

            skipWhiteSpace();

            if ( acceptChar ( Constants.attributesEnd ) ) {
                // allow an optional space after )
                acceptChar ( ' ' );
                break;
            }

            if ( isEof() ) throw errorAtCurrentLocation ( "Missing attributes end symbol '" + Constants.attributesEnd + "'." );

            // name
            TextLocation location = currentLocation();
            final boolean isAttribute = true;
            NodeName name = requireName ( isAttribute );
            NonEmptyPXMLNode node = new NonEmptyPXMLNode( name, location, isAttribute );
            N handlerNode = eventHandler.onNodeStart ( node, handlerParentNode );

            // =
            skipWhiteSpace();
            if ( ! acceptChar ( Constants.attributeAssign ) ) throw errorAtCurrentLocation (
                "Expecting character '" + Constants.attributeAssign + "' to assign a value to attribute '" + name +
                "', but found '" + currentChar() + "'." );
            skipWhiteSpace();

            // value
            TextLocation valueLocation = currentLocation();
            String value = tokenizer.readAttributeValue();
            if ( value == null ) {
                throw errorAtCurrentLocation ( "Expecting an attribute value." );
            }
            eventHandler.onText ( value, handlerNode, valueLocation );
            if ( ! tokenizer.isAtWhiteSpace() && ! isAtChar ( Constants.attributesEnd ) ) {
                throw errorAtCurrentLocation (
                    "An attribute value must be followed by a space, tab, new line, or '" + Constants.attributesEnd + "'." );
            }

            // check if namespace definition
            String namespacePrefix = name.getNamespacePrefix();
            if ( namespacePrefix != null && namespacePrefix.equals ( XMLConstants.XMLNS_ATTRIBUTE ) ) {
                registerNewNamespace ( name.getLocalName(), value, location );
            }
        }
    }

    private void registerNewNamespace ( String namespacePrefix, String namespaceURI, TextLocation location )
        throws PXMLDataException {

        if ( namespacePrefixURIMap.containsKey ( namespacePrefix ) ) {
            throw errorAtLocation(
                "The namespace prefix '" + namespacePrefix + "' has already been declared",
                location );
        }

        namespacePrefixURIMap.put ( namespacePrefix, namespaceURI );
    }

    private TextLocation currentLocation() {
        return new TextLocation( tokenizer.getResource(), currentLineNumber(), currentColumnNumber(), null );
    }


    // tokenizer helpers

    private boolean isEof() {
        return tokenizer.isEof();
    }

    private void skipWhitespaceAndComments() throws IOException, PXMLDataException {
        while ( tokenizer.skipWhiteSpace() || tokenizer.skipComment() ) {}
    }

    private String requireName() throws IOException, PXMLDataException {

        final String name = acceptName();
        if ( name != null ) {
            return name;
        } else {
            throw errorAtCurrentLocation ( "Expecting a valid name. A name cannot start with '" + currentChar() + "'." );
        }
    }

    private String acceptName() throws IOException, PXMLDataException {
        return tokenizer.readName();
    }

    private boolean acceptChar ( char c ) throws IOException {
        return tokenizer.acceptChar ( c );
    }

    private char currentChar() {
        return tokenizer.currentChar();
    }

    private boolean isAtChar ( char c ) {
        return tokenizer.isAtChar ( c );
    }

    private boolean isAtEndOfLine() {
        return isAtChar ( '\n' ) || isAtChar ( '\r' );
    }

    private int currentLineNumber() {
        return tokenizer.currentLineNumber();
    }

    private int currentColumnNumber() {
        return tokenizer.currentColumnNumber();
    }

    private void skipWhiteSpace() throws IOException {
        tokenizer.skipWhiteSpace();
    }

    private void skipSpacesAndTabs() throws IOException {
        tokenizer.skipSpacesAndTabs();
    }

    // errors

    private PXMLDataException errorAtCurrentLocation ( String message ) {
        return errorAtLocation ( message, currentLocation() );
    }

    private PXMLDataException errorAtLocation ( String message, TextLocation location ) {
        return new PXMLDataException ( message, location );
    }
}
