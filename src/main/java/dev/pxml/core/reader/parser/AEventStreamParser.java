package dev.pxml.core.reader.parser;

import dev.pxml.core.reader.parser.eventHandler.IParserEventsHandler;
import dev.pxml.core.reader.tokenizer.ITokenizer;
import dev.pxml.core.reader.tokenizer.Tokenizer;
import dev.pxml.core.utilities.annotations.NotNull;

import java.io.File;
import java.io.Reader;

public abstract class AEventStreamParser<N, R> {

    public abstract void parse (
        @NotNull ITokenizer tokenizer, @NotNull IParserEventsHandler<N, R> eventHandler ) throws Exception;

    public void parse (
        @NotNull Reader reader, @NotNull IParserEventsHandler<N, R> eventHandler, Object resource ) throws Exception {

        parse ( new Tokenizer( reader, resource ), eventHandler );
    }

    public void parse (
        @NotNull String string, @NotNull IParserEventsHandler<N, R> eventHandler, Object resource ) throws Exception {

        parse ( new Tokenizer ( string, resource ), eventHandler );
    }

    public void parse (
        @NotNull File file, @NotNull IParserEventsHandler<N, R> eventHandler ) throws Exception {

        parse ( new Tokenizer ( file ), eventHandler );
    }
}
