package com.bis.lite.ogel.client;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class SpireOgelClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpireOgelClient.class);
    private String soapUrl;

    @Inject
    public SpireOgelClient(@Named("soapUrl")String soapUrl) {
        this.soapUrl = soapUrl;
    }

    public SOAPMessage executeRequest() throws SOAPException, UnsupportedEncodingException {

        SOAPConnection soapConnection = null;

        try {
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            soapConnection = soapConnectionFactory.createConnection();

            SOAPMessage request = createRequest();
            LOGGER.debug(messageAsString(request));

            SOAPMessage response = soapConnection.call(request, soapUrl);
            LOGGER.debug(messageAsString(response));

            return response;

        } finally {
            if (soapConnection != null) {
                soapConnection.close();
            }
        }
    }

    private SOAPMessage createRequest() throws SOAPException, UnsupportedEncodingException {

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("spir", "http://www.fivium.co.uk/fox/webservices/ispire/SPIRE_OGEL_TYPES/");

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        soapBody.addChildElement("getOgelTypes", "spir");

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", "http://www.fivium.co.uk/fox/webservices/ispire/SPIRE_OGEL_TYPES/" + "getCompanies");

        String authorization = Base64.getEncoder().encodeToString("bisdev.api@test.com:dev".getBytes("utf-8"));
        headers.addHeader("Authorization", "Basic " + authorization);
        soapMessage.saveChanges();

        return soapMessage;
    }


    private static String messageAsString(SOAPMessage soapMessage) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            soapMessage.writeTo(outputStream);
            return outputStream.toString();
        } catch(IOException | SOAPException e) {
            return null;
        }
    }

}
