package dev.pxml.core.data.node;

import dev.pxml.core.reader.reader.TextLocation;
import dev.pxml.core.data.node.name.NodeName;
import dev.pxml.core.data.node.value.Node_ValueElement;
import dev.pxml.core.data.node.value.Text_ValueElement;
import dev.pxml.core.data.node.value.ValueElement;
import dev.pxml.core.utilities.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NonEmptyPXMLNode extends PXMLNode {

    private final List<ValueElement> value;
    public @NotNull
    List<ValueElement> getValue() { return value; }

    public NonEmptyPXMLNode( @NotNull NodeName name, List<ValueElement> value, TextLocation location, boolean isAttribute ) {
        super ( name, location, isAttribute );
        this.value = value;
    }

    public NonEmptyPXMLNode( @NotNull NodeName name, TextLocation location, boolean isAttribute ) {
        super ( name, location, isAttribute );
        this.value = new ArrayList<>();
    }

    public boolean hasValue() {
        return value != null && ! value.isEmpty();
    }

    public void addChildNode ( PXMLNode child ) {

        value.add ( new Node_ValueElement( child ) );
    }

    public void addChildText ( String text, TextLocation location ) {

        value.add ( new Text_ValueElement( text, location ) );
    }
}
