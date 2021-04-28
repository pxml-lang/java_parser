package dev.pxml.core.reader.reader;

import dev.pxml.core.utilities.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.Stack;

// TODO reading ahead (with 'mark(i)') doesn't work beyond the 'currentReader'
// (i.e. i is greater than the remaining characters available in 'currentReader')

public class StackedReaderWithLocation extends AReaderWithLocation {

    private final @NotNull Stack<AReaderWithLocation> readers;
    private @NotNull AReaderWithLocation currentReader;

    public int getCurrentLineNumber() { return currentReader.getCurrentLineNumber(); }

    public int getCurrentColumnNumber() { return currentReader.getCurrentColumnNumber(); }

    public Object getResource() { return currentReader.getResource(); }

    public TextLocation getCurrentLocation() {

        TextLocation result = null;
        for ( AReaderWithLocation reader : readers ) {
            result = new TextLocation (
                reader.getResource(), reader.getCurrentLineNumber(), reader.getCurrentColumnNumber(), result );
        }
        return result;
    }

    public StackedReaderWithLocation ( @NotNull AReaderWithLocation initialReader ) {

        this.readers = new Stack<>();
        push ( initialReader );
    }

    public StackedReaderWithLocation ( @NotNull String string, Object resource ) {
        this ( new ReaderWithLocation ( string, resource ) );
    }

    public StackedReaderWithLocation ( @NotNull String string ) {
        this ( new ReaderWithLocation ( string ) );
    }

    public StackedReaderWithLocation ( @NotNull File file ) throws FileNotFoundException {
        this ( new ReaderWithLocation ( file ) );
    }

    public void push ( @NotNull AReaderWithLocation reader ) {

        readers.push ( reader );
        currentReader = reader;
    }

    public void push ( @NotNull String string, Object resource ) {
        push ( new ReaderWithLocation ( string, resource ) );
    }

    public void push ( @NotNull String string ) {
        push ( new ReaderWithLocation ( string ) );
    }

    public void push ( @NotNull File file ) throws FileNotFoundException {
        push ( new ReaderWithLocation ( file ) );
    }

    public int read ( char[] buffer, int offset, int length ) throws IOException {

        int doneCount = 0;
        int remainingCount = length;

        while ( remainingCount > 0 ) {
            int currentCount = currentReader.read ( buffer, offset + doneCount, remainingCount );
            if ( currentCount > 0 ) {
                doneCount += currentCount;
                remainingCount -= currentCount;
            }
            if ( remainingCount > 0 ) {
                if ( ! readers.empty() ) {
                    currentReader = readers.pop();
                } else {
                    break;
                }
            }
        }

        if ( doneCount == 0 ) {
            return -1;
        } else {
            return doneCount;
        }
    }

    // wrappers

    public void close() throws IOException {

        for ( AReaderWithLocation reader : readers ) {
            reader.close();
        }
    }

    public void mark ( int readAheadLimit ) throws IOException {
        currentReader.mark ( readAheadLimit );
    }

    public boolean markSupported() {
        return currentReader.markSupported();
    }

    public boolean ready() throws IOException {
        return currentReader.ready();
    }

    public void reset() throws IOException {
        currentReader.reset();
    }

    public long skip ( long n ) throws IOException {
        return currentReader.skip ( n );
    }

    public long transferTo ( Writer out ) throws IOException {
        return currentReader.transferTo ( out );
    }
}
