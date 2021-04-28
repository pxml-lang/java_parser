package dev.pxml.core.data.node.value;

import dev.pxml.core.reader.reader.TextLocation;
import dev.pxml.core.utilities.annotations.NotNull;

public class Text_ValueElement extends ValueElement {

    private final String text;
    public @NotNull String getText() { return text; }

    private final TextLocation location;
    public TextLocation getLocation() { return location; }

    public Text_ValueElement( @NotNull String text, TextLocation location ) {
        this.text = text;
        this.location = location;
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();

        if ( text.length() <= 20 ) {
            sb.append( text );
        } else {
            sb.append ( text.substring ( 0, 17 ) );
            sb.append ( "..." );
        }

        if ( location != null ) {
            sb.append ( " at " );
            sb.append ( location.toString() );
        }

        return sb.toString();
    }
}
