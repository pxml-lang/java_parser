package dev.pxml.core;

public class Constants {

    // node
    public static final char nodeStart = '[';
    public static final char nodeEnd = ']';
    public static final char namespaceSeparator = ':';
    public static final char nameValueSeparator = ' ';
    public static final char nodeEndTagSymbol = '/';

    // attribute
    public static final char attributesStart = '(';
    public static final char attributesEnd = ')';
    public static final char attributeAssign = '=';
    public static final char attributeValueDoubleQuote = '"';
    public static final char attributeValueSingleQuote = '\'';
    public static final char attributesSeparator = ' ';

    // comment
    public static final String commentStart = "[-";
    public static final String commentEnd = "-]";
    public static final char commentSymbol = '-';

    public static final char escapeCharacter = '\\';

    public static final String newLine = System.getProperty ( "line.separator" );
}
