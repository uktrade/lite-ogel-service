package uk.gov.bis.lite.ogel.client;

import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.database.exception.SOAPParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

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

  public SOAPMessage executeRequest() {

    SOAPConnectionFactory soapConnectionFactory;
    try {
      soapConnectionFactory = SOAPConnectionFactory.newInstance();
      SOAPConnection soapConnection = soapConnectionFactory.createConnection();

      SOAPMessage request = createRequest();
      LOGGER.debug(messageAsString(request));

      final Stopwatch stopwatch = Stopwatch.createStarted();
      SOAPMessage response = soapConnection.call(request, soapUrl);
      stopwatch.stop();
      System.out.println("New Ogel list has been retrieved from Spire in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds ");
      LOGGER.debug(messageAsString(response));
      return response;
    } catch (SOAPException e) {
      throw new SOAPParseException("An error occurred establishing the connection with SOAP client", e);
    }
  }

  private SOAPMessage createRequest() {

    MessageFactory messageFactory;
    try {
      messageFactory = MessageFactory.newInstance();

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

      String authorization = Base64.getEncoder().encodeToString((soapClientUserName + ":" + soapClientPassword).getBytes("utf-8"));
      headers.addHeader("Authorization", "Basic " + authorization);
      soapMessage.saveChanges();

      return soapMessage;
    } catch (SOAPException e) {
      throw new SOAPParseException("An error occurred creating the SOAP request for retrieving Spire Ogels ", e);
    } catch (UnsupportedEncodingException e) {
      throw new SOAPParseException("Unsupported Encoding type", e);
    }
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
