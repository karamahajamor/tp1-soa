package com.exemple;

import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class Main {

    public static void main(String[] args) {
        try {
            validerXML();
            System.out.println("Validation XML/XSD réussie.\n");

            interrogerXPath();

        } catch (Exception e) {
            System.out.println("Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void validerXML() throws Exception {
        SchemaFactory schemaFactory =
                SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        InputStream xsd = Main.class.getClassLoader()
                .getResourceAsStream("films.xsd");
        InputStream xml = Main.class.getClassLoader()
                .getResourceAsStream("films.xml");

        if (xsd == null || xml == null) {
            throw new IllegalStateException(
                    "Fichier films.xsd ou films.xml introuvable dans src/main/resources."
            );
        }

        Schema schema = schemaFactory.newSchema(new StreamSource(xsd));
        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(xml));
    }

    private static void interrogerXPath() throws Exception {
        InputStream xml = Main.class.getClassLoader()
                .getResourceAsStream("films.xml");

        if (xml == null) {
            throw new IllegalStateException("Fichier films.xml introuvable.");
        }

        DocumentBuilderFactory documentFactory =
                DocumentBuilderFactory.newInstance();

        Document document = documentFactory
                .newDocumentBuilder()
                .parse(xml);

        XPath xpath = XPathFactory.newInstance().newXPath();

        afficherResultats(
                "Tous les titres :",
                xpath.compile("/films/film/titre/text()"),
                document
        );

        afficherResultats(
                "Films sortis après 2000 :",
                xpath.compile("/films/film[annee > 2000]/titre/text()"),
                document
        );

        afficherResultats(
                "Films de Christopher Nolan :",
                xpath.compile(
                        "/films/film[realisateur='Christopher Nolan']/titre/text()"
                ),
                document
        );
    }

    private static void afficherResultats(
            String titre,
            XPathExpression expression,
            Document document
    ) throws Exception {
        NodeList resultats = (NodeList) expression.evaluate(
                document,
                XPathConstants.NODESET
        );

        System.out.println(titre);

        if (resultats.getLength() == 0) {
            System.out.println("  Aucun résultat.");
            return;
        }

        for (int i = 0; i < resultats.getLength(); i++) {
            System.out.println("  - " + resultats.item(i).getNodeValue());
        }

        System.out.println();
    }
}