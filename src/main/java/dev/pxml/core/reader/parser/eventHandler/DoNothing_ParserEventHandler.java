package dev.pxml.core.reader.parser.eventHandler;

import dev.pxml.core.data.node.PXMLNode;
import dev.pxml.core.reader.reader.TextLocation;

public class DoNothing_ParserEventHandler implements IParserEventsHandler<PXMLNode, String> {

    public DoNothing_ParserEventHandler() {}

    public void onStart() {}

    public void onStop() {}

    public PXMLNode onRootNodeStart ( PXMLNode node ) { return node; }

    public void onRootNodeEnd ( PXMLNode node ) {}

    public PXMLNode onNodeStart ( PXMLNode node, PXMLNode parentNode ) { return node; }

    public void onNodeEnd ( PXMLNode node ) {}

    public void onText ( String text, PXMLNode parentNode, TextLocation location ) {}

    public void onComment ( String comment, PXMLNode parentNode, TextLocation location ) {}

    public String getResult() { return "nothing"; }
}
