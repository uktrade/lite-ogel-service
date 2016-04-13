package com.bis.lite.ogel.unmarshall;

import com.bis.lite.ogel.client.unmarshall.SpireOgelSOAPUnmarshaller;
import com.bis.lite.ogel.model.SpireOgel;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SpireOgelSOAPUnmarshallerTest {

    @Test
    public void testExecute() throws SOAPException, IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(ClassLoader.getSystemResourceAsStream("sample_ogel.xml"));
        XPath xPath =  XPathFactory.newInstance().newXPath();
        NodeList nodeList = (NodeList) xPath.compile("//OGEL_TYPES_LIST").evaluate(document, XPathConstants.NODESET);

        final List<SpireOgel> spireOgelList = new SpireOgelSOAPUnmarshaller().parseSoapBody(nodeList);
        assertTrue(!spireOgelList.isEmpty());
        assertEquals(55, spireOgelList.size());
        assertEquals("OGL0", spireOgelList.get(0).getOgelCode());
        assertTrue(spireOgelList.get(0).getDescription().contains("Access Overseas to Software"));

    }
}
