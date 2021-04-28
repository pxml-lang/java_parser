package dev.pxml.core.data.node;

import dev.pxml.core.reader.reader.TextLocation;
import dev.pxml.core.data.node.name.NodeName;
import dev.pxml.core.utilities.annotations.NotNull;

public class EmptyPXMLNode extends PXMLNode {

    public EmptyPXMLNode( @NotNull NodeName name, TextLocation location, boolean isAttribute ) {
        super ( name, location, isAttribute );
    }
}
