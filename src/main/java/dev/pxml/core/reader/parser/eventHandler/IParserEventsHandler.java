package dev.pxml.core.reader.parser.eventHandler;

import dev.pxml.core.data.node.PXMLNode;
import dev.pxml.core.reader.reader.TextLocation;
import dev.pxml.core.utilities.annotations.NotNull;

public interface IParserEventsHandler<N, R> {

    void onStart() throws Exception;
    void onStop() throws Exception;

    N onRootNodeStart ( @NotNull PXMLNode rootNode ) throws Exception;
    void onRootNodeEnd ( N rootNode ) throws Exception;

    N onNodeStart ( @NotNull PXMLNode node, @NotNull N parentNode ) throws Exception;
    void onNodeEnd ( N node ) throws Exception;

    void onText ( @NotNull String text, @NotNull N parentNode, TextLocation location ) throws Exception;

    // [- and -] is included in comment
    void onComment ( String comment, @NotNull N parentNode, TextLocation location ) throws Exception;

    R getResult() throws Exception;
}
