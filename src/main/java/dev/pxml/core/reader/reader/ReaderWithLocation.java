package dev.pxml.core.reader.reader;

import java.io.*;

public class ReaderWithLocation extends AReaderWithLocation {

    private final Reader reader;

    private int currentLineNumber;
    public int getCurrentLineNumber() { return currentLineNumber; }

    private int currentColumnNumber;
    public int getCurrentColumnNumber() { return currentColumnNumber; }

    private final Object resource;
    public Object getResource() { return resource; }

    private int lineNumberAtMark;
    private int columnNumberAtMark;

    public ReaderWithLocation ( Reader reader, Object resource ) {

        Reader readerWithMarkSupport = reader.markSupported() ? reader : new BufferedReader ( reader );
        this.reader = readerWithMarkSupport;
        this.resource = resource;
        this.currentLineNumber = 1;
        this.currentColumnNumber = 1;

        this.lineNumberAtMark = 0;
        this.columnNumberAtMark = 0;
    }

    public ReaderWithLocation ( String string, Object resource ) {
        this ( new StringReader ( string ), resource );
    }

    public ReaderWithLocation ( String string ) {
        this ( string, null );
    }

    public ReaderWithLocation ( File file ) throws FileNotFoundException {
        this ( new FileReader ( file ), file );
    }

    public TextLocation getCurrentLocation() {
        return new TextLocation ( resource, currentLineNumber, currentColumnNumber, null );
    }

    public int read() throws IOException {

        int result = reader.read();
        if ( result < 0 ) return result;

        if ( ((char) result) == '\n' ) {
            currentLineNumber += 1;
            currentColumnNumber = 1;
        } else {
            currentColumnNumber += 1;
        }

        return result;
    }

    public int read ( char[] buffer, int offset, int length ) throws IOException {

        int i;
        for ( i = 0; i < length; i++ ) {
            int nextInt = read();
            if ( nextInt < 0 ) break;
            buffer[offset + i] = (char) nextInt;
        }
        return i;
    }

    // wrappers

    public void close() throws IOException {
        reader.close();
    }

    public void mark ( int readAheadLimit ) throws IOException {

        lineNumberAtMark = currentLineNumber;
        columnNumberAtMark = currentColumnNumber;

        reader.mark ( readAheadLimit );
    }

    public boolean markSupported() {
        // return reader.markSupported();
        return true;
    }

    public boolean ready() throws IOException {
        return reader.ready();
    }

    public void reset() throws IOException {

        currentLineNumber = lineNumberAtMark;
        currentColumnNumber = columnNumberAtMark;

        reader.reset();
    }

    public long skip ( long n ) throws IOException {
        return reader.skip ( n );
    }

    public long transferTo ( Writer out ) throws IOException {
        return reader.transferTo ( out );
    }
}
