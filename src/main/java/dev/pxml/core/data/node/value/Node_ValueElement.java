package dev.pxml.core.data.node.value;

import dev.pxml.core.data.node.PXMLNode;
import dev.pxml.core.reader.reader.TextLocation;
import dev.pxml.core.utilities.annotations.NotNull;

public class Node_ValueElement extends ValueElement {

    private final PXMLNode node;
    public @NotNull
    PXMLNode getNode() { return node; }

    public TextLocation getLocation() { return node.getLocation(); }

    public Node_ValueElement( @NotNull PXMLNode node ) {
        this.node = node;
    }

    public String toString() {
        return node.toString();
    }
}
