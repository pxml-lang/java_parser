package dev.pxml.core.utilities;

import dev.pxml.core.utilities.annotations.NotNull;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class XSLTTransformer {

    public static void transformXMLWithXMLXSLT (
        @NotNull Reader XMLDataReader, @NotNull Reader XMLXSLTReader, @NotNull Writer outputWriter ) throws Exception {

        transformWithDocuments (
            XMLUtilities.readXMLDocument ( XMLDataReader ),
            XMLUtilities.readXMLDocument ( XMLXSLTReader ),
            outputWriter );
    }

    public static void transformXMLWithPXMLXSLT (
        @NotNull Reader XMLDataReader,
        @NotNull Reader pXMLXSLTReader, Object pXMLXSLTResource,
        @NotNull Writer outputWriter ) throws Exception {

        transformWithDocuments (
            XMLUtilities.readXMLDocument ( XMLDataReader ),
            PXMLToXMLConverter.pXMLToXMLDocument ( pXMLXSLTReader, pXMLXSLTResource ),
            outputWriter );
    }

    public static void transformPXMLWithXMLXSLT (
        @NotNull Reader pXMLDataReader, Object pXMLDataResource,
        @NotNull Reader XMLXSLTReader,
        @NotNull Writer outputWriter ) throws Exception {

        transformWithDocuments (
            PXMLToXMLConverter.pXMLToXMLDocument ( pXMLDataReader, pXMLDataResource ),
            XMLUtilities.readXMLDocument ( XMLXSLTReader ),
            outputWriter );
    }

    public static void transformPXMLFileWithPXMLXSLTFile (
        @NotNull File pXMLFile, @NotNull File pXMLXSLTFile, @NotNull File outputFile ) throws Exception {

        transformPXMLWithPXMLXSLT (
            new FileReader ( pXMLFile ), pXMLFile,
            new FileReader ( pXMLXSLTFile ), pXMLXSLTFile,
            new FileWriter( outputFile ) );
    }

    public static void transformPXMLWithPXMLXSLT (
        @NotNull Reader pXMLDataReader, Object pXMLDataResource,
        @NotNull Reader pXMLXSLTReader, Object pXMLXSLTResource,
        @NotNull Writer outputWriter ) throws Exception {

        transformWithDocuments (
            PXMLToXMLConverter.pXMLToXMLDocument ( pXMLDataReader, pXMLDataResource ),
            PXMLToXMLConverter.pXMLToXMLDocument ( pXMLXSLTReader, pXMLXSLTResource ),
            outputWriter );
    }

    public static void transformWithDocuments (
        @NotNull Document XMLDocument, @NotNull Document XSLTDocument, @NotNull Writer outputWriter )
        throws Exception {

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer ( new DOMSource ( XSLTDocument ) );
        transformer.transform ( new DOMSource ( XMLDocument ), new StreamResult ( outputWriter ) );
    }
}
