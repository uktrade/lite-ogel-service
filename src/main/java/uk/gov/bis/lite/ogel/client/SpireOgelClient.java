package uk.gov.bis.lite.ogel.client;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

public class SpireOgelClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(SpireOgelClient.class);
  private String soapUrl;
  private String soapClientUserName;
  private String soapClientPassword;

  @Inject
  public SpireOgelClient(@Named("soapUrl") String soapUrl, @Named("soapUserName") String clientUserName,
                         @Named("soapPassword") String clientPassword) {
    this.soapUrl = soapUrl;
    this.soapClientUserName = clientUserName;
    this.soapClientPassword = clientPassword;
  }

  public SOAPMessage executeRequest() throws SOAPException, UnsupportedEncodingException {

    SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
    SOAPConnection soapConnection = soapConnectionFactory.createConnection();

    SOAPMessage request = createRequest();
    LOGGER.debug(messageAsString(request));

    final long tStart = System.currentTimeMillis();
    SOAPMessage response = soapConnection.call(request, soapUrl);
    long tEnd = System.currentTimeMillis();
    long tDelta = tEnd - tStart;
    System.out.println("New Ogel list has been retrieved from Spire in " + tDelta / 1000.0 + " seconds ");
    LOGGER.debug(messageAsString(response));

    return response;
  }

  private SOAPMessage createRequest() throws SOAPException, UnsupportedEncodingException {

    MessageFactory messageFactory = MessageFactory.newInstance();
    SOAPMessage soapMessage = messageFactory.createMessage();
    final long tStart = System.currentTimeMillis();
    SOAPPart soapPart = soapMessage.getSOAPPart();
    long tEnd = System.currentTimeMillis();
    long tDelta = tEnd - tStart;
    System.out.println("Getting the soap body out of soap response took " + tDelta / 1000.0 + " seconds ");

    // SOAP Envelope
    SOAPEnvelope envelope = soapPart.getEnvelope();
    envelope.addNamespaceDeclaration("spir", "http://www.fivium.co.uk/fox/webservices/ispire/SPIRE_OGEL_TYPES/");

    // SOAP Body
    SOAPBody soapBody = envelope.getBody();
    soapBody.addChildElement("getOgelTypes", "spir");

    MimeHeaders headers = soapMessage.getMimeHeaders();
    headers.addHeader("SOAPAction", "http://www.fivium.co.uk/fox/webservices/ispire/SPIRE_OGEL_TYPES/" + "getCompanies");

    String authorization = Base64.getEncoder().encodeToString((soapClientUserName + ":" + soapClientPassword).getBytes("utf-8"));
    headers.addHeader("Authorization", "Basic " + authorization);
    soapMessage.saveChanges();

    return soapMessage;
  }


  private static String messageAsString(SOAPMessage soapMessage) {
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      soapMessage.writeTo(outputStream);
      return outputStream.toString();
    } catch (IOException | SOAPException e) {
      return null;
    }
  }

}
