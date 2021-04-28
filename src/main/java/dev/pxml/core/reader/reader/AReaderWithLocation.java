package dev.pxml.core.reader.reader;

import java.io.Reader;

public abstract class AReaderWithLocation extends Reader {

    public abstract int getCurrentLineNumber();
    public abstract int getCurrentColumnNumber();
    public abstract Object getResource();

    public abstract TextLocation getCurrentLocation();
}
