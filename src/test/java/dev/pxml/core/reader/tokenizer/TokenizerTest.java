package dev.pxml.core.reader.tokenizer;

import dev.pxml.core.reader.error.PXMLDataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TokenizerTest {

    @Test
    @DisplayName ( "Basics should work" )
    public void testBasics() throws IOException {

        Tokenizer t = new Tokenizer ( "12\n45\r\n8" );

        assertFalse ( t.isEof(), "eof" );
        assertEquals ( '1', t.currentChar() );
        assertTrue ( t.isAtChar ( '1' ) );
        assertFalse ( t.isAtChar ( '2' ) );
        assertEquals ( 1, t.currentLineNumber(), "line" );
        assertEquals ( 1, t.currentColumnNumber(), "column" );
        // assertEquals ( 1, t.currentIndex(), "index" );
        /*
        assertTrue ( t.isAtString ( "12\n45" ) );
        assertTrue ( t.isAtString ( "12\n4" ) );
        assertTrue ( t.isAtString ( "12" ) );
        assertTrue ( t.isAtString ( "1" ) );
        */
        assertEquals ( '2', t.peekNextChar() );
        assertTrue ( t.isNextChar ( '2' ) );
        assertFalse ( t.isNextChar ( '3' ) );

        t.readNextChar();
        assertFalse ( t.isEof(), "eof" );
        assertEquals ( '2', t.currentChar() );
        assertTrue ( t.isAtChar ( '2' ) );
        assertEquals ( 1, t.currentLineNumber(), "line" );
        assertEquals ( 2, t.currentColumnNumber(), "column" );
        // assertEquals ( 2, t.currentIndex(), "index" );
        /*
        assertTrue ( t.isAtString ( "2\n45" ) );
        assertFalse ( t.isAtString ( "45" ) );
        */

        t.readNextChar();
        assertFalse ( t.isEof(), "eof" );
        assertEquals ( '\n', t.currentChar() );
        assertTrue ( t.isAtChar ( '\n' ) );
        assertEquals ( 1, t.currentLineNumber(), "line" );
        assertEquals ( 3, t.currentColumnNumber(), "column" );
        // assertEquals ( 3, t.currentIndex(), "index" );
        /*
        assertTrue ( t.isAtString ( "\n45" ) );
        assertTrue ( t.isAtString ( "\n" ) );
        */

        t.readNextChar();
        assertFalse ( t.isEof(), "eof" );
        assertEquals ( '4', t.currentChar() );
        assertTrue ( t.isAtChar ( '4' ) );
        assertEquals ( 2, t.currentLineNumber(), "line" );
        assertEquals ( 1, t.currentColumnNumber(), "column" );
        // assertEquals ( 4, t.currentIndex(), "index" );
        /*
        assertTrue ( t.isAtString ( "45" ) );
        assertTrue ( t.isAtString ( "4" ) );
        assertFalse ( t.isAtString ( "5" ) );
        assertFalse ( t.isAtString ( "456" ) );
        */

        t.readNextChar();
        assertFalse ( t.isEof(), "eof" );
        assertEquals ( '5', t.currentChar() );
        assertTrue ( t.isAtChar ( '5' ) );
        assertEquals ( 2, t.currentLineNumber(), "line" );
        assertEquals ( 2, t.currentColumnNumber(), "column" );
        // assertEquals ( 5, t.currentIndex(), "index" );
        /*
        assertTrue ( t.isAtString ( "5" ) );
        assertFalse ( t.isAtString ( "56" ) );
        */

        t.readNextChar();
        assertEquals ( 2, t.currentLineNumber(), "line" );
        assertEquals ( 3, t.currentColumnNumber(), "column" );
        t.readNextChar();
        assertEquals ( 2, t.currentLineNumber(), "line" );
        assertEquals ( 4, t.currentColumnNumber(), "column" );
        t.readNextChar();
        assertFalse ( t.isEof(), "eof" );
        assertEquals ( '8', t.currentChar() );
        assertTrue ( t.isAtChar ( '8' ) );
        assertEquals ( 3, t.currentLineNumber(), "line" );
        assertEquals ( 1, t.currentColumnNumber(), "column" );
        // assertEquals ( 8, t.currentIndex(), "index" );
        /*
        assertTrue ( t.isAtString ( "8" ) );
        assertFalse ( t.isAtString ( "89" ) );
        */

        t.readNextChar();
        assertTrue ( t.isEof(), "eof" );
        assertEquals ( 0, t.currentChar() );
        assertFalse ( t.isAtChar ( '8' ) );
        assertEquals ( 3, t.currentLineNumber(), "line" );
        assertEquals ( 2, t.currentColumnNumber(), "column" );
        // assertEquals ( 0, t.currentIndex(), "index" );
        /*
        assertFalse ( t.isAtString ( "8" ) );
        assertFalse ( t.isAtString ( "89" ) );
        */
    }

    @Test
    @DisplayName ( "Node names should work according to XML rules" )
    public void testNodeNames() throws IOException, PXMLDataException {

        Tokenizer t = new Tokenizer ( "div item_12 _ab1-c.3 a A _ 1a AB* xm xml XML1", "tests/foo.pxml" );

        assertEquals ( "div", t.readName() );
        t.skipWhiteSpace();

        assertEquals ( "item_12", t.readName() );
        t.skipWhiteSpace();

        assertEquals ( "_ab1-c.3", t.readName() );
        t.skipWhiteSpace();

        assertEquals ( "a", t.readName() );
        t.skipWhiteSpace();

        assertEquals ( "A", t.readName() );
        t.skipWhiteSpace();

        assertEquals ( "_", t.readName() );
        t.skipWhiteSpace();

        assertNull ( t.readName() ); // 1a is invalid
        t.readNextChar();
        t.readNextChar();
        t.skipWhiteSpace();

        assertEquals ( "AB", t.readName() ); // "*" is invalid
        t.readNextChar();
        t.skipWhiteSpace();

        assertEquals ( "xm", t.readName() );
        t.skipWhiteSpace();

        /*
        assertThrows ( PXMLDataException.class, t::readName );
        t.skipWhiteSpace();

        Exception exception = assertThrows ( PXMLDataException.class, t::readName );
        // System.out.println ( exception.toString() );
        assertTrue ( exception.getMessage().contains ( "cannot start with \"xml\"" ) );
        */

        assertEquals ( "xml", t.readName() );
        t.skipWhiteSpace();

        assertEquals ( "XML1", t.readName() );
    }

    @Test
    @DisplayName ( "Reading text should work properly, including escape sequences" )
    public void testText() throws IOException, PXMLDataException {

        Tokenizer t = new Tokenizer ( "text text 123" );
        assertEquals("text text 123", t.readText());

        // text limiters
        t = new Tokenizer ( "text 123 [qwe][" );
        assertEquals("text 123 ", t.readText());
        t.readNextChar();
        assertEquals("qwe", t.readText());
        assertNull( t.readText());

        // escape sequences
        // 111\[\]222\t333\r\n444\\555
        t = new Tokenizer ( "111\\[\\]222\\t333\\r\\n444\\\\555" );
        assertEquals("111[]222\t333\r\n444\\555", t.readText());

        // Unicode escape sequences
        t = new Tokenizer ( "\\u0048\\u0065ll\\u006f" );
        assertEquals("Hello", t.readText());

        // invalid escapes

        t = new Tokenizer ( "\\5" );
        Exception exception = assertThrows ( PXMLDataException.class, t::readText );
        // System.out.println ( exception.toString() );

        t = new Tokenizer ( "\\u00za" );
        exception = assertThrows ( PXMLDataException.class, t::readText );
        // System.out.println ( exception.toString() );

        t = new Tokenizer ( "\\u00a" );
        exception = assertThrows ( PXMLDataException.class, t::readText );
        // System.out.println ( exception.toString() );

        t = new Tokenizer ( "\\u" );
        exception = assertThrows ( PXMLDataException.class, t::readText );
        // System.out.println ( exception.toString() );

        t = new Tokenizer ( "\\" );
        exception = assertThrows ( PXMLDataException.class, t::readText );
        // System.out.println ( exception.toString() );
    }

    @Test
    @DisplayName ( "Reading attribute values should work properly, including escape sequences" )
    public void testAttributeValue() throws IOException, PXMLDataException {

        // unquoted
        Tokenizer t = new Tokenizer( "a abc C:\\foo\\bar.txt" );
        assertEquals ("a", t.readUnquotedAttributeValue() );
        assertNull ( t.readUnquotedAttributeValue() );
        t.skipWhiteSpace();
        assertEquals ("abc", t.readUnquotedAttributeValue() );
        t.skipWhiteSpace();
        assertEquals ("C:\\foo\\bar.txt", t.readUnquotedAttributeValue() );
        assertNull ( t.readUnquotedAttributeValue() );

        // quoted with "
        t = new Tokenizer( "\"a\" \"abc\" \"a b\" \"a b \\\" ' \\u0048\\u0065ll\\u006f\"" );
        assertEquals ("a", t.readQuotedAttributeValue() );
        assertNull ( t.readQuotedAttributeValue() );
        t.skipWhiteSpace();
        assertEquals ("abc", t.readQuotedAttributeValue() );
        t.skipWhiteSpace();
        assertEquals ("a b", t.readQuotedAttributeValue() );
        t.skipWhiteSpace();
        assertEquals ("a b \" ' Hello", t.readQuotedAttributeValue() );

        // unclosed
        t = new Tokenizer( "\"a" );
        assertThrows ( PXMLDataException.class, t::readQuotedAttributeValue );

        // invalid escape \y
        t = new Tokenizer( "\"ab\\ycd\"" );
        assertThrows ( PXMLDataException.class, t::readQuotedAttributeValue );

        // quoted with '
        t = new Tokenizer( "'a' 'abc' 'a b \" \\' c '" );
        assertEquals ("a", t.readQuotedAttributeValue() );
        assertNull ( t.readQuotedAttributeValue() );
        t.skipWhiteSpace();
        assertEquals ("abc", t.readQuotedAttributeValue() );
        t.skipWhiteSpace();
        assertEquals ("a b \" ' c ", t.readQuotedAttributeValue() );
        assertNull ( t.readQuotedAttributeValue() );

        // unclosed
        t = new Tokenizer( "'a" );
        assertThrows ( PXMLDataException.class, t::readQuotedAttributeValue );

        // mixed
        t = new Tokenizer( "a 'ab' \"abc\"" );
        assertEquals ("a", t.readAttributeValue() );
        assertNull ( t.readAttributeValue() );
        t.skipWhiteSpace();
        assertEquals ("ab", t.readAttributeValue() );
        t.skipWhiteSpace();
        assertEquals ("abc", t.readAttributeValue() );
        assertNull ( t.readAttributeValue() );
    }

    @Test
    @DisplayName ( "Reading comments should work properly, including nested comments" )
    public void testComments() throws IOException, PXMLDataException {

        Tokenizer t = new Tokenizer("[- comment -]" );
        assertEquals("[- comment -]", t.readComment());

        t = new Tokenizer("[- [name value-\\]] -]" );
        assertEquals("[- [name value-\\]] -]", t.readComment());

        t = new Tokenizer("   [-a-]qwe" );
        t.skipWhiteSpace();
        assertEquals("[-a-]", t.readComment());

        t = new Tokenizer("[--]qwe" );
        assertEquals("[--]", t.readComment());

        // nested
        t = new Tokenizer("[- comment [- nested -] -]" );
        assertEquals("[- comment [- nested -] -]", t.readComment());

        t = new Tokenizer("[-[--]-]" );
        assertEquals("[-[--]-]", t.readComment());
    }

    @Test
    @DisplayName ( "peekNextNChars() should work properly" )
    public void testPeekNextNChars() throws IOException, PXMLDataException {

        Tokenizer t = new Tokenizer( "1234567890" );
        assertEquals ("1", t.peekCurrentNChars( 1 ) );
        assertEquals ("12", t.peekCurrentNChars( 2 ) );
        assertEquals ("1234567890", t.peekCurrentNChars( 10 ) );
        assertEquals ("1234567890", t.peekCurrentNChars( 11 ) );

        t.readNextChar();
        assertEquals ("2", t.peekCurrentNChars( 1 ) );
        assertEquals ("23", t.peekCurrentNChars( 2 ) );
        assertEquals ("234567890", t.peekCurrentNChars( 10 ) );
        assertEquals ("234567890", t.peekCurrentNChars( 100 ) );

        t.readNextChar();
        t.readNextChar();
        assertEquals ("4", t.peekCurrentNChars( 1 ) );
        assertEquals ("4567890", t.peekCurrentNChars( 100 ) );
    }
}
