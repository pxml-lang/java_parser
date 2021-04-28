package dev.pxml.core.data.node;

import dev.pxml.core.reader.reader.TextLocation;
import dev.pxml.core.data.node.name.NodeName;
import dev.pxml.core.utilities.annotations.NotNull;

import java.net.URI;

public abstract class PXMLNode {

    final NodeName name;
    public @NotNull NodeName getName() { return name; }

    final TextLocation location;
    public TextLocation getLocation() { return location; }

    final boolean isAttribute;
    public boolean isAttribute() { return isAttribute; }

    PXMLNode( @NotNull NodeName name, TextLocation location, boolean isAttribute ) {
        this.name = name;
        this.location = location;
        this.isAttribute = isAttribute;
    }

    public @NotNull String getLocalName() { return name.getLocalName(); }
    public String getNamespacePrefix() { return name.getNamespacePrefix(); }
    public URI getNamespaceURI() { return name.getNamespaceURI(); }
    public boolean hasNamespace() { return name.hasNamespace(); }

    public int getLineNumber() {

        if ( location != null ) {
            return location.getLineNumber();
        } else {
            return 0;
        }
    }

    public int getColumnNumber() {

        if ( location != null ) {
            return location.getColumnNumber();
        } else {
            return 0;
        }
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append ( name.toString() );

        if ( location != null ) {
            sb.append ( " at " );
            sb.append ( location.toString() );
        }

        return sb.toString();
    }
}
