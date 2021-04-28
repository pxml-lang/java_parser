package dev.pxml.core.utilities;

import dev.pxml.core.utilities.annotations.NotNull;
import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;

public class XMLSchemaValidator {

    public static void validateXMLDataWithXMLSchema (
        @NotNull Reader XMLDataReader, @NotNull Reader XMLSchemaReader ) throws Exception {

        validateDocumentWithSchemaDocument (
            XMLUtilities.readXMLDocument ( XMLDataReader ),
            XMLUtilities.readXMLDocument ( XMLSchemaReader ) );
    }

    public static void validateXMLDataWithPXMLSchema (
        @NotNull Reader XMLDataReader, @NotNull Reader pXMLSchemaReader, Object pXMLSchemaResource ) throws Exception {

        validateDocumentWithSchemaDocument (
            XMLUtilities.readXMLDocument ( XMLDataReader ),
            PXMLToXMLConverter.pXMLToXMLDocument ( pXMLSchemaReader, pXMLSchemaResource ) );
    }

    public static void validatePXMLDataWithXMLSchema (
        @NotNull Reader pXMLDataReader, Object pXMLDataResource, @NotNull Reader XMLSchemaReader ) throws Exception {

        validateDocumentWithSchemaDocument (
            PXMLToXMLConverter.pXMLToXMLDocument ( pXMLDataReader, pXMLDataResource ),
            XMLUtilities.readXMLDocument ( XMLSchemaReader ) );
    }

    public static void validatePXMLFileWithPXMLSchemaFile (
        @NotNull File pXMLDataFile, @NotNull File pXMLSchemaFile ) throws Exception {

        validatePXMLDataWithPXMLSchema (
            new FileReader ( pXMLDataFile ), pXMLDataFile, new FileReader ( pXMLSchemaFile ), pXMLSchemaFile );
    }

    public static void validatePXMLDataWithPXMLSchema (
        @NotNull Reader pXMLDataReader, Object pXMLDataResource, @NotNull Reader pXMLSchemaReader, Object pXMLSchemaResource ) throws Exception {

        validateDocumentWithSchemaDocument (
            PXMLToXMLConverter.pXMLToXMLDocument ( pXMLDataReader, pXMLDataResource ),
            PXMLToXMLConverter.pXMLToXMLDocument ( pXMLSchemaReader, pXMLSchemaResource ) );
    }

    public static void validateDocumentWithSchemaDocument (
        Document XMLDocument, Document XMLSchemaDocument ) throws Exception {

        SchemaFactory factory = SchemaFactory.newInstance ( XMLConstants.W3C_XML_SCHEMA_NS_URI );
        Schema schema = factory.newSchema ( new DOMSource( XMLSchemaDocument ) );
        javax.xml.validation.Validator validator = schema.newValidator();
        validator.validate ( new DOMSource ( XMLDocument ) );
    }
}
