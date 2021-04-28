package dev.pxml.core;

import dev.pxml.core.utilities.PXMLToXMLConverter;
import dev.pxml.core.utilities.XMLToPXMLConverter;

import java.io.File;
import java.io.PrintStream;

public class PXMLConverter {

    public static void main ( String[] args ) {

        try {
            start ( args );
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit ( 1 );
        }
    }

    private static void start ( String[] args ) throws Exception {

        if ( args.length != 3 ) {
            usage();
        }

        String command = args[0];
        String sourcePath = args[1];
        String targetPath = args[2];

        if ( command.equalsIgnoreCase ( "pxml_to_xml") ) {
// TODO
            PXMLToXMLConverter.pXMLFileToXMLFile ( new File ( sourcePath ), new File ( targetPath ) );
//            EventStreamParserUtilities.logPXMLFileParseEventsToOSOut ( new File ( sourcePath ) );
//            EventStreamParserUtilities.measureParseTimeForPXMLFile ( new File ( sourcePath ) );

        } else if ( command.equalsIgnoreCase ( "xml_to_pxml") ) {
            XMLToPXMLConverter.XMLFileToPXMLFile ( new File ( sourcePath ), new File ( targetPath ) );

        } else {
            System.err.println ( "Error: command '" + command + "' is not supported." );
            System.err.println();
            usage();
        }
    }

    private static void usage() {

        PrintStream err = System.err;
        err.println ( "Usage:" );
        err.println ( "pxml <command> <inputFile> <outputFile>" );
        err.println ( "<command>: pxml_to_xml or xml_to_pxml" );
        err.println ( "<inputFile>,<outputFile>: relative or absolute file path" );
        err.println();

        err.println ( "Example (convert a pXML file to an XML file):" );
        err.println ( "pxml pxml_to_xml input/data.pxml output/data.xml" );
        err.println();

        System.exit ( 1 );
    }
}
