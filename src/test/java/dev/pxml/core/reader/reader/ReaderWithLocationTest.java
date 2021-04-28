package dev.pxml.core.reader.reader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ReaderWithLocationTest {

    @Test
    @DisplayName ( "Test line and column" )
    public void testLocation() throws IOException {

        ReaderWithLocation r = new ReaderWithLocation ( "12\n45\r\n890\n\nABC", "qwe" );

        assertEquals ( 1, r.getCurrentLineNumber(), "line" );
        assertEquals ( 1, r.getCurrentColumnNumber(), "column" );
        assertEquals ( "qwe", r.getResource(), "resource" );

        assertEquals ( '1', r.read() );
        assertEquals ( 1, r.getCurrentLineNumber() );
        assertEquals ( 2, r.getCurrentColumnNumber() );

        assertEquals ( '2', r.read() );
        assertEquals ( 1, r.getCurrentLineNumber() );
        assertEquals ( 3, r.getCurrentColumnNumber() );

        assertEquals ( '\n', r.read() );
        assertEquals ( 2, r.getCurrentLineNumber() );
        assertEquals ( 1, r.getCurrentColumnNumber() );

        assertEquals ( '4', r.read() );
        assertEquals ( 2, r.getCurrentLineNumber() );
        assertEquals ( 2, r.getCurrentColumnNumber() );

        assertEquals ( '5', r.read() );
        assertEquals ( 2, r.getCurrentLineNumber() );
        assertEquals ( 3, r.getCurrentColumnNumber() );

        assertEquals ( '\r', r.read() );
        assertEquals ( 2, r.getCurrentLineNumber() );
        assertEquals ( 4, r.getCurrentColumnNumber() );

        assertEquals ( '\n', r.read() );
        assertEquals ( 3, r.getCurrentLineNumber() );
        assertEquals ( 1, r.getCurrentColumnNumber() );

        char[] charArray = new char[6];
        assertEquals ( 6, r.read ( charArray ) );
        assertEquals ( "890\n\nA", String.valueOf ( charArray ) );
        assertEquals ( 5, r.getCurrentLineNumber() );
        assertEquals ( 2, r.getCurrentColumnNumber() );

        assertEquals ( 2, r.read ( charArray, 1, 2 ) );
        assertEquals ( "8BC\n\nA", String.valueOf ( charArray ) );
        assertEquals ( 5, r.getCurrentLineNumber() );
        assertEquals ( 4, r.getCurrentColumnNumber() );

        assertEquals ( -1, r.read() );
        assertEquals ( 5, r.getCurrentLineNumber() );
        assertEquals ( 4, r.getCurrentColumnNumber() );
    }

    @Test
    @DisplayName ( "Test 'set mark'" )
    public void testMarks() throws IOException {

        ReaderWithLocation r = new ReaderWithLocation ( "12\n345", "qwe" );

        assertEquals ( '1', r.read() );
        assertEquals( 1, r.getCurrentLineNumber() );
        assertEquals( 2, r.getCurrentColumnNumber() );

        r.mark ( 4 );
        assertEquals ( '2', r.read() );
        assertEquals ( '\n', r.read() );
        assertEquals ( '3', r.read() );
        assertEquals ( '4', r.read() );
        assertEquals( 2, r.getCurrentLineNumber() );
        assertEquals( 3, r.getCurrentColumnNumber() );

        r.reset();
        assertEquals( 1, r.getCurrentLineNumber() );
        assertEquals( 2, r.getCurrentColumnNumber() );
    }
}
