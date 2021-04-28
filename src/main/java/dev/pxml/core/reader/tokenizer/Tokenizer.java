package dev.pxml.core.reader.tokenizer;

import dev.pxml.core.Constants;
import dev.pxml.core.reader.reader.AReaderWithLocation;
import dev.pxml.core.reader.reader.ReaderWithLocation;
import dev.pxml.core.reader.reader.TextLocation;
import dev.pxml.core.reader.error.PXMLDataException;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Tokenizer implements ITokenizer {

    private final AReaderWithLocation reader;

    private boolean isEof;
    private char currentChar;

    private int currentLine;
    private int currentColumn;

    // constructors

    public Tokenizer ( Reader reader, Object resource ) throws IOException {

        // ensure mark() is supported
        // Reader readerWithMarkSupport = reader.markSupported() ? reader : new BufferedReader( reader );
        // this.reader = new ReaderWithLocation ( readerWithMarkSupport, resource );
        this.reader = new ReaderWithLocation ( reader, resource );
        assert this.reader.markSupported();

        this.isEof = false;
        this.currentChar = 0;

        this.currentLine = 0;
        this.currentColumn = 0;

        readNextChar();
    }

    public Tokenizer ( String string, Object resource ) throws IOException {
        this ( new StringReader ( string ), resource );
    }

    public Tokenizer ( String string ) throws IOException {
        this ( string, string );
    }

    public Tokenizer ( File file ) throws IOException {
        this ( new FileReader ( file, StandardCharsets.UTF_8 ), file );
    }


    // queries

    public boolean isEof() { return isEof; }

    public char currentChar() { return currentChar; }

    public Object getResource() { return reader.getResource(); }

    public int currentLineNumber() { return currentLine; }

    public int currentColumnNumber() { return currentColumn; }

    public TextLocation currentLocation() {
        return new TextLocation (
            reader.getResource(), currentLine, currentColumn, reader.getCurrentLocation().getParentLocation() );
    }

    public boolean isAtChar ( char c ) {
        if ( isEof ) return false;
        return ( currentChar == c );
    }

    /*
    public boolean isAtString ( String s ) throws IOException {

        boolean localIsEof = isEof;
        char localCurrentChar = currentChar;
        boolean result = true;
        setMark ( s.length() );

        for ( int i = 0; i < s.length(); i++ ) {
            if ( localIsEof ) {
                result = false;
                break;
            }
            if ( localCurrentChar != s.charAt ( i ) ) {
                result = false;
                break;
            }

            int nextInt = reader.read();
            if ( nextInt < 0 ) localIsEof = true;
            localCurrentChar = (char) nextInt;
        }
        goBackToMark();
        return result;
    }
    */

    public boolean isNextChar ( char c ) throws IOException {
        return peekNextChar() == c;
    }

    public char peekNextChar() throws IOException {

        if ( isEof ) return 0;

        setMark ( 1 );
        int peekedInt = reader.read();
        goBackToMark();

        if ( peekedInt < 0 ) {
            return 0;
        } else {
            return (char) peekedInt;
        }
    }

    public String peekCurrentNChars ( int n ) throws IOException {
        if ( n <= 0 ) throw new IllegalArgumentException ( "n must be > 0, but is" + n );

        if ( isEof ) return null;

        StringBuilder sb = new StringBuilder();
        sb.append ( currentChar );

        setMark ( n );
        for ( int i = 1; i < n; i++ ) {
            if ( isEof ) break;
            int peekedInt = reader.read();
            if ( peekedInt == -1 ) break;
            sb.append ( (char) peekedInt );
        }
        goBackToMark();

        if ( sb.length() == 0 ) return null;

        return sb.toString();
    }

    /*
    public char peekFirstCharAfterWhiteSpace ( int lookAhead ) throws IOException {

        if ( ! isAtWhiteSpace() ) return currentChar;

        char result = 0;

        setMark ( lookAhead );

        for ( int i = 1; i < lookAhead; i++ ) {
            int peekedInt = reader.read();
            if ( peekedInt == -1 ) break;
            char peekedChar = (char) peekedInt;
            if ( ! isWhiteSpaceChar ( peekedChar ) ) {
                result = peekedChar;
                break;
            }
        }

        goBackToMark();

        return result;
    }
    */


    //

    public boolean acceptChar ( char c ) throws IOException {

        boolean isAccepted = isAtChar ( c );

        if ( isAccepted ) {
            readNextChar();
        }

        return isAccepted;
    }

    public char readNextChar() throws IOException {

        if ( isEof ) return 0;

        currentLine = reader.getCurrentLineNumber();
        currentColumn = reader.getCurrentColumnNumber();

        int nextInt = reader.read();
        if ( nextInt < 0 ) isEof = true;

        if ( ! isEof ) {
            currentChar = (char) nextInt;
        } else {
            currentChar = 0;
        }

        return currentChar;
    }


    // name

    // https://www.w3schools.com/xml/xml_elements.asp

    public String readName() throws IOException {

        StringBuilder sb = new StringBuilder();
        while ( true ) {
            if ( isEof ) break;

            boolean acceptChar = false;
            if ( Character.isLetter ( currentChar ) ) {
                acceptChar = true;
            } else if ( currentChar == '_' ) {
                acceptChar = true;
            } else if ( sb.length() >= 1 ) {
                if ( Character.isDigit ( currentChar )
                    || currentChar == '-'
                    || currentChar == '.' ) {
                    acceptChar = true;
                }
            }
            if ( ! acceptChar ) break;

            sb.append ( currentChar );
            readNextChar();
        }
        if ( sb.length() == 0 ) return null;

        return sb.toString();
    }


    // text

    public String readText() throws IOException, PXMLDataException {

        boolean inEscapeMode = false;
        StringBuilder result = new StringBuilder();

        while ( true ) {
            if ( isEof ) {
                if ( inEscapeMode ) {
                    throw errorAtCurrentLocation (
                        "Expecting another character after the escape character '" + Constants.escapeCharacter +
                        "' at the end of the document." );
                }
                break;
            }

            if ( isAtNodeStartOrEnd() && ! inEscapeMode ) break;

            if ( ! inEscapeMode ) {
                if ( ! isEscapeCharacter ( currentChar ) ) {
                    result.append ( currentChar );
                } else {
                    inEscapeMode = true;
                }
            } else {
                switch ( currentChar ) {
                    case '[':
                    case ']':
                    case '\\':
                        result.append ( currentChar );
                        break;
                    case 't':
                        result.append ( '\t' );
                        break;
                    case 'r':
                        result.append ( '\r' );
                        break;
                    case 'n':
                        result.append ( '\n' );
                        break;
                    case 'u':
                        result.append ( readUnicodeEscapeSequence() );
                        break;
                    default:
                        throw errorAtCurrentLocation (
                            "Invalid character escape sequence \"" + Constants.escapeCharacter + currentChar + "\"." );
                }
                inEscapeMode = false;
            }

            readNextChar();
        }

        return result.length() == 0 ? null : result.toString();
    }


    // attributes

    public String readAttributeValue() throws IOException, PXMLDataException {

        String result = readQuotedAttributeValue();
        if ( result != null ) return result;

        return readUnquotedAttributeValue();
    }

    public String readQuotedAttributeValue() throws IOException, PXMLDataException {

        if ( currentChar != Constants.attributeValueDoubleQuote &&
            currentChar != Constants.attributeValueSingleQuote ) return null;

        TextLocation startLocation = currentLocation();

        char quote = currentChar;
        readNextChar();

        StringBuilder result = new StringBuilder();
        boolean inEscapeMode = false;

        while ( true ) {

            if ( isEof ) throw errorAtCurrentLocation ( "Missing " + quote + " to end the value started at " + startLocation );

            if ( currentChar == quote && ! inEscapeMode ) {
                readNextChar();
                break;
            }

            if ( ! inEscapeMode ) {
                if ( ! isEscapeCharacter ( currentChar ) ) {
                    result.append ( currentChar );
                } else {
                    inEscapeMode = true;
                }
            } else {
                switch ( currentChar ) {
                    case '[':
                    case ']':
                    case '\\':
                    case '"':
                    case '\'':
                        result.append ( currentChar );
                        break;
                    case 't':
                        result.append ( '\t' );
                        break;
                    case 'r':
                        result.append ( '\r' );
                        break;
                    case 'n':
                        result.append ( '\n' );
                        break;
                    case 'u':
                        result.append ( readUnicodeEscapeSequence() );
                        break;
                    default:
                        throw errorAtCurrentLocation (
                            "Invalid character escape sequence '" + Constants.escapeCharacter + currentChar + "'." );
                }
                inEscapeMode = false;
            }

            readNextChar();
        }

        return result.length() == 0 ? "" : result.toString();
    }

    public String readUnquotedAttributeValue() throws IOException {

        if ( isEof ) return null;

        StringBuilder result = new StringBuilder();

        boolean endFound = false;
        while ( ! endFound ) {

            switch ( currentChar ) {
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                case '[':
                case ']':
                case '(':
                case ')':
                // case '\\':
                case '"':
                case '\'':
                    endFound = true;
                    break;
                default:
                    result.append ( currentChar );
                    readNextChar();
                    endFound = isEof;
            }
        }

        return result.length() == 0 ? null : result.toString();
    }


    // whitespace

    /*
    public String readWhiteSpace() throws IOException {

        if ( ! isAtWhiteSpace() ) return null;

        StringBuilder sb = new StringBuilder();
        while ( isAtWhiteSpace() ) {
            sb.append ( currentChar );
            readNextChar();
        }
        return sb.toString();
    }
    */

    public boolean skipWhiteSpace() throws IOException {

        boolean result = isAtWhiteSpace();
        while ( isAtWhiteSpace() ) {
            readNextChar();
        }
        return result;
    }

    public boolean skipSpacesAndTabs() throws IOException {

        boolean result = isAtSpaceOrTab();
        while ( isAtSpaceOrTab() ) {
            readNextChar();
        }
        return result;
    }

    public boolean isAtWhiteSpace() {

        if ( isEof ) return false;
        return isWhiteSpaceChar ( currentChar );
    }

    public boolean isAtSpaceOrTab() {

        if ( isEof ) return false;
        return isSpaceOrTabChar ( currentChar );
    }

    private boolean isWhiteSpaceChar ( char c ) {

        return c == ' '
            || c == '\n'
            || c == '\r'
            || c == '\t';
    }

    private boolean isSpaceOrTabChar ( char c ) {

        return c == ' '
            || c == '\t';
    }



    // comment

    public String readComment() throws IOException, PXMLDataException {

        if ( ! isAtStartOfComment() ) return null;

        StringBuilder result = new StringBuilder();
        readCommentSnippet ( result );

        return result.toString();
    }

    public boolean skipComment() throws IOException, PXMLDataException {

        // can be made faster by writing a specific version that doesn't use a StringBuilder
        // to build and return the comment's content
        return readComment() != null;
    }

    private void readCommentSnippet ( StringBuilder result ) throws IOException, PXMLDataException {

        TextLocation location = currentLocation();

        // we are at the start of a comment
        result.append ( currentChar );
        result.append ( readNextChar() );
        readNextChar();

        while ( true ) {
            if ( isEof ) throw errorAtLocation (
        "The comment starting at line " + location.getLineNumber() +
                ", column " + location.getColumnNumber() + " is never closed.",
                location );

            if ( isAtEndOfComment() ) {
                result.append ( currentChar );
                result.append ( readNextChar() );
                readNextChar();
                return;

            } else if ( isAtStartOfComment() ) {
                readCommentSnippet ( result ); // recursive call for nested comments

            } else {
                result.append ( currentChar );
                readNextChar();
            }
        }
    }

    private boolean isAtStartOfComment() throws IOException {

        // return isAtString ( Constants.commentStart );
        return isAtChar ( Constants.nodeStart ) && isNextChar ( Constants.commentSymbol );
    }

    private boolean isAtEndOfComment() throws IOException {

        // return isAtString ( Constants.commentEnd );
        return isAtChar ( Constants.commentSymbol ) && isNextChar ( Constants.nodeEnd );
    }


    // private helper methods

    private boolean isAtNodeStartOrEnd() {

        return isAtChar ( Constants.nodeStart ) || isAtChar ( Constants.nodeEnd );
    }

    private boolean isEscapeCharacter ( char c ) {

        return c == Constants.escapeCharacter;
    }

    private char readUnicodeEscapeSequence() throws IOException, PXMLDataException, NumberFormatException {

        // now positioned at the u of \u1234

        TextLocation location = currentLocation();

        StringBuilder sb = new StringBuilder();
        for ( int i = 1; i <= 4; i++ ) {
            readNextChar();
            if ( isEof ) throw errorAtLocation (
        "Expecting four hex digits to define a Unicode escape sequence. But found only " + (i - 1) + ".",
                location );
            sb.append ( requireHexChar() );
        }

        // NumberFormatException should never happen because the validity has been checked already
        return (char) Integer.parseInt ( sb.toString(), 16 );
    }

    private char requireHexChar() throws PXMLDataException {

        if ( ( currentChar >= '0' && currentChar <= '9' )
            || ( currentChar >= 'a' && currentChar <= 'f' )
            || ( currentChar >= 'A' && currentChar <= 'F' ) ) {
            return currentChar;
        } else {
            throw errorAtCurrentLocation (
                "Invalid hexadecimal character '" + currentChar + "'. Only 0..9, a..f, and A..F are allowed." );
        }
    }

    private void setMark ( int readAheadLimit ) {

        try {
            reader.mark ( readAheadLimit );
        } catch ( IOException e ) {
            // should never happen because markSupported() is ensured in constructor
            throw new RuntimeException ( e );
        }
    }

    private void goBackToMark() {

        try {
            reader.reset();
        } catch ( IOException e ) {
            // should never happen because markSupported() is ensured in constructor
            throw new RuntimeException ( e );
        }
    }

    // errors

    private PXMLDataException errorAtCurrentLocation ( String message ) {
        return errorAtLocation ( message, currentLocation() );
    }

    private PXMLDataException errorAtLocation ( String message, TextLocation location ) {
        return new PXMLDataException ( message, location );
    }
}
