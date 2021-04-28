package dev.pxml.core.utilities;

import dev.pxml.core.data.node.PXMLNode;
import dev.pxml.core.reader.parser.EventStreamParser;
import dev.pxml.core.reader.parser.eventHandler.CreateDOM_ParserEventHandler;
import dev.pxml.core.reader.parser.eventHandler.IParserEventsHandler;
import dev.pxml.core.reader.parser.eventHandler.WriteXML_ParserEventHandler;
import dev.pxml.core.utilities.annotations.NotNull;
import org.w3c.dom.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class PXMLToXMLConverter {

    public static void pXMLFileToXMLFile ( @NotNull File pXMLFile, @NotNull File XMLFile ) throws Exception {

        final FileReader PXMLFileReader = new FileReader ( pXMLFile, StandardCharsets.UTF_8 );
        final FileWriter XMLFileWriter = new FileWriter ( XMLFile, StandardCharsets.UTF_8 );
        pipePXMLReaderToXMLWriter ( PXMLFileReader, XMLFileWriter, pXMLFile.toString() );
        PXMLFileReader.close();
        XMLFileWriter.close();
    }

    public static void pXMLFileToXMLFile ( @NotNull Path pXMLFilePath, @NotNull Path XMLFilePath ) throws Exception {
        pXMLFileToXMLFile( pXMLFilePath.toFile(), XMLFilePath.toFile() );
    }

    public static void pXMLFileToXMLFile ( @NotNull String pXMLFilePath, @NotNull String XMLFilePath ) throws Exception {
        pXMLFileToXMLFile( new File ( pXMLFilePath ), new File ( XMLFilePath ) );
    }

    // doesn't close reader nor writer
    public static void pipePXMLReaderToXMLWriter ( @NotNull Reader pXMLReader, @NotNull Writer XMLWriter, Object pXMLResource ) throws Exception {

        IParserEventsHandler<PXMLNode, String> eventHandler = new WriteXML_ParserEventHandler( XMLWriter );
        EventStreamParser<PXMLNode, String> parser = new EventStreamParser<>();
        parser.parse ( pXMLReader, eventHandler, pXMLResource );
    }

    public static Document pXMLFileToXMLDocument ( @NotNull File pXMLFile ) throws Exception {
        return pXMLToXMLDocument( new FileReader ( pXMLFile ), pXMLFile.toString() );
    }

    public static Document pXMLToXMLDocument ( @NotNull Reader pXMLReader, Object pXMLResource ) throws Exception {

        // long startTimeNanos = System.nanoTime();

        IParserEventsHandler<Node, Document> eventHandler = new CreateDOM_ParserEventHandler();
        EventStreamParser<Node, Document> parser = new EventStreamParser<>();
        parser.parse ( pXMLReader, eventHandler, pXMLResource );

        /*
        long endTimeNanos = System.nanoTime();
        long time = endTimeNanos - startTimeNanos;
        long micros = TimeUnit.NANOSECONDS.toMicros ( time );
        System.out.println ( "PXMLToXMLDocument time: " + String.valueOf ( micros ) + " microseconds" );
        */

        return eventHandler.getResult();
    }
}
