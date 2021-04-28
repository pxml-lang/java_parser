package dev.pxml.core.writer;

import dev.pxml.core.utilities.annotations.NotNull;

import java.io.IOException;

public interface IPXMLWriter {

    // string is not escaped
    void write ( String string ) throws IOException;

    void flush() throws IOException;
    // void close() throws IOException;

    void startDocument() throws IOException;
    void endDocument() throws IOException;

    void writeNonEmptyNodeStart ( String nameSpacePrefix, @NotNull String localName ) throws IOException;
    void writeNodeStart ( String nameSpacePrefix, String localName, boolean appendNameValueSeparator ) throws IOException;
    void writeNodeEndSymbol() throws IOException;
    void writeNodeEndTag ( String nameSpacePrefix, @NotNull String localName ) throws IOException;
    void writeTextNode ( String nameSpacePrefix, @NotNull String localName, @NotNull String text ) throws IOException;
    void writeEmptyNode ( String nameSpacePrefix, @NotNull String localName ) throws IOException;

    void writeAttributesStart() throws IOException;
    void writeAttributesEnd() throws IOException;
    void writeAttribute ( String nameSpacePrefix, @NotNull String localName, String value, boolean appendAttributesSeparator ) throws IOException;

    void escapeAndWriteText ( String text ) throws IOException;

    void writeComment ( String text ) throws IOException;
}
