package dev.pxml.core.reader.parser.eventHandler;

import dev.pxml.core.Constants;
import dev.pxml.core.data.node.PXMLNode;
import dev.pxml.core.reader.reader.TextLocation;

import java.io.PrintWriter;
import java.io.Writer;

public class Logger_ParserEventHandler implements IParserEventsHandler<String, String> {

    final private Writer writer;

    public Logger_ParserEventHandler( Writer writer ) {
        this.writer = writer;
    }

    public Logger_ParserEventHandler() {
        this ( new PrintWriter( System.out ) );
    }

    public void onStart() throws Exception {
        writeLine ( "onStart" );
    }

    public void onStop() throws Exception {
        writeLine ( "onStop" );
        writer.flush();
    }

    public String getResult() throws Exception {
        writeLine ( "getResult" );
        return "No result provided";
    }

    public String onRootNodeStart ( PXMLNode node ) throws Exception {

        String name = node.getName().toString();

        writeEvent ( "onRootNodeStart", node.getLocation() );
        writeLine ( name );

        return name;
    }

    public void onRootNodeEnd ( String rootNode ) throws Exception {

        writeEvent ( "onRootNodeEnd", null );
        writeLine ( rootNode );
    }

    public String onNodeStart ( PXMLNode node, String parentNode ) throws Exception {

        String name = node.getName().toString();

        writeEvent ( "onNodeStart", node.getLocation() );
        writeLine ( name );

        return name;
    }

    public void onNodeEnd ( String node ) throws Exception {

        writeEvent ( "onNodeEnd", null );
        writeLine ( node );
    }

    public void onText ( String text, String parentNode, TextLocation location ) throws Exception {

        writeEvent ( "onText", location );
        writeLine ( text );
    }

    public void onComment ( String comment, String parentNode, TextLocation location ) throws Exception {

        writeEvent ( "onComment", location );
        writeLine ( comment );
    }


    // private helpers

    private void writeLine ( String string ) throws Exception {

        writer.write ( string );
        writer.write (Constants.newLine );
        writer.flush();
    }

    private void write ( String string ) throws Exception {
        writer.write ( string );
    }

    private void writeEvent ( String eventName, TextLocation location ) throws Exception {

        writer.write ( eventName );

        if ( location != null ) {
            writer.write ( location.toString() );
        }

        writer.write ( ": " );
    }
}
