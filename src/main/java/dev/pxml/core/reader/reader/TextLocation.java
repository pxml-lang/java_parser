package dev.pxml.core.reader.reader;

import dev.pxml.core.Constants;

public class TextLocation {

    private final Object resource;
    public Object getResource() { return resource; }

    private final int lineNumber;
    public int getLineNumber() { return lineNumber; }

    private final int columnNumber;
    public int getColumnNumber() { return columnNumber; }

    private final TextLocation parentLocation;
    public TextLocation getParentLocation() { return parentLocation; }

    public TextLocation( Object resource, int lineNumber, int columnNumber, TextLocation parentLocation ) {

        this.resource = resource;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.parentLocation = parentLocation;
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append ( "(" );
        sb.append ( lineNumber );
        sb.append ( ", " );
        sb.append ( columnNumber );
        sb.append ( ")" );

        if ( resource != null ) {
            sb.append ( " in " );
            sb.append ( resource.toString() );
        }

        if ( parentLocation != null ) {
            sb.append ( Constants.newLine );
            sb.append ( parentLocation.toString() );
        }

        return sb.toString();
    }
}
