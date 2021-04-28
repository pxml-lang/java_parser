package dev.pxml.core.data.node.name;

import dev.pxml.core.utilities.annotations.NotNull;

import java.net.URI;

public class NodeName {

    private final String localName;
    public @NotNull String getLocalName() { return localName; }

    private final String namespacePrefix;
    public String getNamespacePrefix() { return namespacePrefix; }

    private final URI namespaceURI;
    public URI getNamespaceURI() { return namespaceURI; }

    public NodeName ( @NotNull String localName, String namespacePrefix, URI namespaceURI ) {
        this.localName = localName;
        this.namespacePrefix = namespacePrefix;
        this.namespaceURI = namespaceURI;
    }

    // public boolean hasNamespace() { return namespacePrefix != null; }
    public boolean hasNamespace() { return namespaceURI != null; }

    public String toString() {

        StringBuilder sb = new StringBuilder();

        if ( namespacePrefix != null ) {
            sb.append ( namespacePrefix );
            sb.append ( ":" );
        }

        sb.append ( localName );

        return sb.toString();
    }
}
