package dev.pxml.core.reader.error;

import dev.pxml.core.Constants;
import dev.pxml.core.reader.reader.TextLocation;

public class PXMLDataException extends Exception {

    private final TextLocation location;
    public TextLocation getLocation() { return location; }

    public PXMLDataException ( String message, TextLocation location ) {

        super ( message + Constants.newLine + location.toString() );

        this.location = location;
    }
}
