/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 */
package com.frojasg1.general.xml;

import com.frojasg1.general.number.IntegerFunctions;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class JavaXmlFunctions {

    protected static JavaXmlFunctions instance = null;

    public static JavaXmlFunctions instance() {
        if (instance == null) {
            instance = new JavaXmlFunctions();
        }

        return (instance);
    }

    public Document createEmptyDocument() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document result = builder.newDocument();

        return (result);
    }

    public Document parseXml(InputStream is) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document result = db.parse(is);

        result.getDocumentElement().normalize();

        return (result);
    }

    public void writeDocument(Document xmlDoc, Writer writer) throws TransformerConfigurationException, TransformerException, IOException {
        writeDocument(xmlDoc, writer, "UTF-8", 2, true);
    }

    public void writeDocument(Document xmlDoc, Writer writer, String charsetName,
            int indent, boolean omitXmlDeclaration)
            throws TransformerConfigurationException, TransformerException, IOException {

		try( Writer writer2 = writer; )
		{
			DOMSource source = new DOMSource(xmlDoc);
			StreamResult result = new StreamResult(writer2);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", indent);

			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, charsetName);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			if (omitXmlDeclaration) {
				// to avoid <?xml version="1.0" encoding="UTF-8"?> be written
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			}

			transformer.transform(source, result);
		}
    }

    public Element getSigleChildElement(Element elem, String tag) {
        Element result = null;

        NodeList nodeList = JavaXmlFunctions.instance().getChildElementsByTagName(elem, tag);
        if (nodeList.getLength() == 1) {
            if (nodeList.item(0).getNodeType() == Node.ELEMENT_NODE) {
                result = (Element) nodeList.item(0);
            }
        }

        return (result);
    }

    public Integer getIntegerParam(Element elem, String attributeName) {
        Integer result = null;
        if ((elem != null) && (elem.hasAttribute(attributeName))) {
            result = IntegerFunctions.parseInt(elem.getAttribute(attributeName));
        }

        return (result);
    }

    public int getIntegerParamWithDefault(Element elem, String attributeName, int defaul) {
        Integer result = getIntegerParam(elem, attributeName);
        if (result == null) {
            result = defaul;
        }

        return (result);
    }

    public Boolean getBooleanParam(Element elem, String attributeName) {
        Boolean result = null;

        if ((elem != null) && (elem.hasAttribute(attributeName))) {
            String resultStr = elem.getAttribute(attributeName);
            if (resultStr != null) {
                if (resultStr.equals("true")) {
                    result = true;
                } else if (resultStr.equals("false")) {
                    result = false;
                }
            }
        }

        return (result);
    }

    public Element getElementFromNode(Node node) {
        Element result = null;
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            result = (Element) node;
        }

        return (result);
    }

	public Element createEmptyElement( Document doc, String name )
	{
		return( doc.createElement( name ) );
	}

	public String getAttribute(Element elem, String attributeName) {
        String result = null;

        if ((elem != null) && elem.hasAttribute(attributeName)) {
            result = elem.getAttribute(attributeName);
        }

        return (result);
    }

    public NodeList getChildElementsByTagName(Node iNode, String tagName) {
        NodeListImp list = new NodeListImp();

        if (iNode != null) {
            NodeList nl = iNode.getChildNodes();

            for (int ii = 0; ii < nl.getLength(); ii++) {
                Node elem = nl.item(ii);
                if ((elem.getNodeType() == Node.ELEMENT_NODE)
                        && (elem.getNodeName().equals(tagName))) {
                    list.add(elem);
                }
            }
        }

        return (list);
    }

    public NodeList getAllChildElements(Node iNode)
	{
        NodeListImp list = new NodeListImp();

        if (iNode != null) {
            NodeList nl = iNode.getChildNodes();

            for (int ii = 0; ii < nl.getLength(); ii++) {
                Node elem = nl.item(ii);
                if (elem.getNodeType() == Node.ELEMENT_NODE)
                {
                    list.add(elem);
                }
            }
        }

        return (list);
    }

    public static class NodeListImp extends ArrayList<Node> implements NodeList {

        @Override
        public Node item(int index) {
            return (get(index));
        }

        @Override
        public int getLength() {
            return (size());
        }
    }
}
