package dev.pxml.core.reader.reader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class ReaderWithLocationZero extends AReaderWithLocation {

    private final Reader reader;

    public int getCurrentLineNumber() { return 0; }

    public int getCurrentColumnNumber() { return 0; }

    private final Object resource;
    public Object getResource() { return resource; }

    public ReaderWithLocationZero( Reader reader, Object resource ) {

        this.reader = reader;
        this.resource = resource;
    }

    public TextLocation getCurrentLocation() {
        return new TextLocation ( resource, 0, 0, null );
    }

    public int read ( char[] buffer, int offset, int length ) throws IOException {
        return reader.read ( buffer, offset, length );
    }

    // wrappers

    public void close() throws IOException {
        reader.close();
    }

    public void mark ( int readAheadLimit ) throws IOException {
        reader.mark ( readAheadLimit );
    }

    public boolean markSupported() {
        return reader.markSupported();
    }

    public boolean ready() throws IOException {
        return reader.ready();
    }

    public void reset() throws IOException {
        reader.reset();
    }

    public long skip ( long n ) throws IOException {
        return reader.skip ( n );
    }

    public long transferTo ( Writer out ) throws IOException {
        return reader.transferTo ( out );
    }
}
