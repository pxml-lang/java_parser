package dev.pxml.core.reader.reader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StackedReaderWithLocationTest {

    @Test
    @DisplayName ( "Test line and column" )
    public void testLocation() throws IOException {

        StackedReaderWithLocation r = new StackedReaderWithLocation ( "123" );
        assertEquals ( '1', r.read() );
        assertEquals ( '2', r.read() );
        assertEquals ( '3', r.read() );
        assertEquals ( -1, r.read() );

        r = new StackedReaderWithLocation ( "123", "resource 1" );
        assertEquals ( '1', r.read() );
        assertEquals ( '2', r.read() );
        r.push ( "ab", "resource 2" );
        // System.out.println ( r.getCurrentLocation() );
        assertEquals ( 'a', r.read() );
        assertEquals ( 'b', r.read() );
        assertEquals ( '3', r.read() );
        assertEquals ( -1, r.read() );

        r = new StackedReaderWithLocation ( "123", "resource 1" );
        r.push ( "abc", "resource 2" );
        r.push ( "456", "resource 3" );
        System.out.println ( r.getCurrentLocation() );
        char[] charArray = new char[9];
        assertEquals ( 9, r.read ( charArray ) );
        assertEquals ( "456abc123", String.valueOf ( charArray ) );
        assertEquals ( -1, r.read() );
    }
}
