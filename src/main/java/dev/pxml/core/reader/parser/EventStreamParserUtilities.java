package dev.pxml.core.reader.parser;

import dev.pxml.core.data.node.PXMLNode;
import dev.pxml.core.reader.reader.TextLocation;
import dev.pxml.core.reader.error.PXMLDataException;
import dev.pxml.core.reader.parser.eventHandler.Logger_ParserEventHandler;
import dev.pxml.core.reader.parser.eventHandler.IParserEventsHandler;
import dev.pxml.core.reader.parser.eventHandler.Timer_ParserEventHandler;

import java.io.*;

public class EventStreamParserUtilities {

    public static void logPXMLFileParseEventsToOSOut ( File PXMLFile ) throws Exception {
        logPXMLParserEvents ( new FileReader( PXMLFile ), new PrintWriter( System.out ), PXMLFile.toString() );
    }

    public static void measureParseTimeForPXMLFile ( File PXMLFile ) throws Exception {
        measureParseTime ( new FileReader( PXMLFile ), PXMLFile.toString() );
    }

    public static void logPXMLParserEvents ( Reader PXMLReader, Writer logWriter, Object PXMLResource ) throws Exception {

        EventStreamParser<String, String> parser = new EventStreamParser<>();
        IParserEventsHandler<String, String> eventHandler = new Logger_ParserEventHandler( logWriter );
        parser.parse ( PXMLReader, eventHandler, PXMLResource );
    }

    public static void measureParseTime ( Reader PXMLReader, Object PXMLResource ) throws Exception {

        EventStreamParser<PXMLNode, String> parser = new EventStreamParser<>();
        IParserEventsHandler<PXMLNode, String> eventHandler = new Timer_ParserEventHandler();
        parser.parse ( PXMLReader, eventHandler, PXMLResource );
    }

    public static String stripStartAndEndFromComment ( String comment ) {
        return comment.substring ( 2, comment.length() - 2 );
    }

    public static void throwNestedMetaDataNotAllowedInXML ( int lineNumber, int columnNumber ) throws PXMLDataException {

        throw new PXMLDataException(
            "A pXML metadata element that contains child-elements cannot be converted to XML, because XML uses attribute for metadata, and attributes can only contain strings.",
            new TextLocation( null, lineNumber, columnNumber, null ) );
    }
}
