package dev.pxml.core.reader.parser.eventHandler;

import dev.pxml.core.data.node.PXMLNode;
import dev.pxml.core.data.node.NonEmptyPXMLNode;
import dev.pxml.core.reader.reader.TextLocation;

public class CreateAST_ParserEventHandler implements IParserEventsHandler<PXMLNode, PXMLNode> {

    private PXMLNode rootNode;

    public CreateAST_ParserEventHandler() {}

    public void onStart() {}

    public void onStop() {}

    public PXMLNode onRootNodeStart ( PXMLNode rootNode ) {

        this.rootNode = rootNode;
        return rootNode;
    }

    public void onRootNodeEnd ( PXMLNode rootNode ) {}

    public PXMLNode onNodeStart ( PXMLNode node, PXMLNode parentNode ) {

        ((NonEmptyPXMLNode) parentNode).addChildNode ( node );
        return node;
    }

    public void onNodeEnd ( PXMLNode node ) {}

    public void onText ( String text, PXMLNode parentNode, TextLocation location ) {
        ((NonEmptyPXMLNode) parentNode).addChildText ( text, location );
    }

    public void onComment ( String comment, PXMLNode parentNode, TextLocation location ) {
        // do nothing
    }

    public PXMLNode getResult() {
        return rootNode;
    }
}
