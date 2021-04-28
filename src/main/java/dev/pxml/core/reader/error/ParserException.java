package dev.pxml.core.reader.error;

public class ParserException extends RuntimeException {

    public ParserException ( String message, Throwable cause ) {
        super ( message, cause );
    }

    public ParserException ( String message ) {
        this ( message, null );
    }

    public ParserException ( Throwable cause ) {
        this ( cause.getMessage(), cause );
    }
}
