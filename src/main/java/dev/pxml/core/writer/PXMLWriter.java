package dev.pxml.core.writer;

import dev.pxml.core.Constants;

import java.io.IOException;
import java.io.Writer;

public class PXMLWriter implements IPXMLWriter {

    private final Writer writer;

    public PXMLWriter ( Writer writer ) {
        this.writer = writer;
    }

    public void write ( String string ) throws IOException {
        writer.write ( string );
    }

    public void flush() throws IOException {
        writer.flush();
    }

    public void startDocument() {
        // do nothing
    }

    public void endDocument() throws IOException {

        // write new line at end of document
        write ( Constants.newLine );
    }

    public void writeNonEmptyNodeStart ( String nameSpacePrefix, String localName ) throws IOException {
        writeNodeStart ( nameSpacePrefix, localName, true );
    }

    public void writeNodeStart ( String nameSpacePrefix, String localName, boolean appendNameValueSeparator ) throws IOException {

        writeNodeStartSymbol();
        writeName ( nameSpacePrefix, localName );
        if ( appendNameValueSeparator ) {
            writeNameValueSeparator();
        }
    }

    public void writeNodeEndSymbol() throws IOException {
        writer.write ( Constants.nodeEnd );
    }

    public void writeNodeEndTag( String nameSpacePrefix, String localName ) throws IOException {

        writeNodeStartSymbol();
        writer.write ( Constants.nodeEndTagSymbol );
        writeName ( nameSpacePrefix, localName );
        writeNodeEndSymbol();
    }

    public void writeTextNode ( String nameSpacePrefix, String localName, String text ) throws IOException {

        writeNonEmptyNodeStart( nameSpacePrefix, localName );
        escapeAndWriteText( text );
        writeNodeEndSymbol();
    }

    public void writeEmptyNode( String nameSpacePrefix, String localName ) throws IOException {

        writeNodeStart( nameSpacePrefix, localName, false );
        writeNodeEndSymbol();
    }

    public void escapeAndWriteText( String text ) throws IOException {

        for ( int i = 0; i < text.length(); i++ ) {
            char c = text.charAt ( i );
            switch ( c ) {
                case '[' :
                    writer.write ( "\\[" );
                    break;
                case ']' :
                    writer.write ( "\\]" );
                    break;
                case '\\' :
                    writer.write ( "\\\\" );
                    break;
                default:
                    writer.write ( c );
            }
        }
    }

    public void writeAttributesStart() throws IOException {
        writer.write ( Constants.attributesStart );
    }

    public void writeAttributesEnd() throws IOException {

        writer.write ( Constants.attributesEnd );

        // TODO should be enabled/disabled by a parameter of this writer's config
        writer.write ( ' ' );
    }

    public void writeAttribute ( String nameSpacePrefix, String localName, String value, boolean appendAttributesSeparator ) throws IOException {

        writeName ( nameSpacePrefix, localName );

        writer.write ( Constants.attributeAssign );

        writeDoubleQuotedAttributeValue ( value );

        if  ( appendAttributesSeparator ) {
            writer.write ( Constants.attributesSeparator );
        }
    }

    public void writeDoubleQuotedAttributeValue ( String value ) throws IOException {

        writer.write ( Constants.attributeValueDoubleQuote );

        for ( int i = 0; i < value.length(); i++ ) {
            char c = value.charAt ( i );
            switch ( c ) {
                case '"' :
                    writer.write ( "\\\"" );
                    break;
                case '\\' :
                    writer.write ( "\\\\" );
                    break;
                default:
                    writer.write ( c );
            }
        }

        writer.write ( Constants.attributeValueDoubleQuote );
    }

    public void writeComment ( String text ) throws IOException {

        writer.write ( Constants.commentStart );
        escapeAndWriteText( text );
        writer.write ( Constants.commentEnd );
    }


    private void writeNodeStartSymbol() throws IOException {
        writer.write ( Constants.nodeStart );
    }

    private void writeName ( String nameSpacePrefix, String localName ) throws IOException {

        if ( nameSpacePrefix != null && ! nameSpacePrefix.isEmpty() ) {
            writer.write ( nameSpacePrefix );
            writer.write ( Constants.namespaceSeparator );
        }

        writer.write ( localName );
    }

    private void writeNameValueSeparator() throws IOException {
        writer.write ( Constants.nameValueSeparator );
    }
}
