package dev.pxml.core.reader.tokenizer;

import dev.pxml.core.reader.reader.TextLocation;
import dev.pxml.core.reader.error.PXMLDataException;

import java.io.IOException;

public interface ITokenizer {

    // queries

    boolean isEof();
    char currentChar();
    Object getResource();
    int currentLineNumber();
    int currentColumnNumber();
    TextLocation currentLocation();

    boolean isAtChar ( char c );
    // boolean isAtString ( String s ) throws IOException;

    boolean isNextChar ( char c ) throws IOException;
    char peekNextChar() throws IOException;

    String peekCurrentNChars ( int n ) throws IOException;

    // char peekFirstCharAfterWhiteSpace ( int lookAhead ) throws IOException;

    //

    boolean acceptChar ( char c ) throws IOException;

    char readNextChar() throws IOException;

    // XML name
    String readName() throws IOException, PXMLDataException;

    // text
    String readText() throws IOException, PXMLDataException;
    // TODO? PXMLToken readRawTextToken() throws IOException, PXMLDataException; // without unescaping -> faster

    // attributes
    String readAttributeValue() throws IOException, PXMLDataException;
    String readQuotedAttributeValue() throws IOException, PXMLDataException;
    String readUnquotedAttributeValue() throws IOException;

    // whitespace
    // String readWhiteSpace() throws IOException;
    boolean skipWhiteSpace() throws IOException;
    boolean skipSpacesAndTabs() throws IOException;
    boolean isAtWhiteSpace();
    boolean isAtSpaceOrTab();

    // comment
    String readComment() throws IOException, PXMLDataException;
    boolean skipComment() throws IOException, PXMLDataException;
}

