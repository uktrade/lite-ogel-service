package com.bis.lite.ogel.service;

import com.bis.lite.ogel.client.SpireOgelClient;
import com.bis.lite.ogel.client.unmarshall.SpireOgelSOAPUnmarshaller;
import com.bis.lite.ogel.model.CategoryType;
import com.bis.lite.ogel.model.SpireOgel;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPathExpressionException;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Singleton
public class SpireOgelService {
    private SpireOgelClient client;
    private SpireOgelSOAPUnmarshaller unmarshaller;

    final static Logger logger = Logger.getLogger(SpireOgelService.class);

    @Inject
    private CacheManager cacheManager;

    @Inject
    public SpireOgelService(SpireOgelClient client, SpireOgelSOAPUnmarshaller unmarshaller) {
        this.client = client;
        this.unmarshaller = unmarshaller;
    }

    private static final String CACHE_KEY = "ogelList";

    public List<SpireOgel> findOgel(String controlCode, String destinationCountryId, List<CategoryType> activityTypes) {
        List<SpireOgel> ogelsList = null;
        try {
            if (cacheManager != null) {
                logger.info("cache Manager is not null!");
                Cache cache = cacheManager.getCache("ogelCache");
                if (cache.get(CACHE_KEY) == null) {
                    final SOAPMessage soapMessage = client.executeRequest();
                    ogelsList = unmarshaller.execute(soapMessage);
                    cache.put(new Element(CACHE_KEY, ogelsList));
                } else {
                    ogelsList = (List<SpireOgel>) cache.get(CACHE_KEY).getObjectValue();
                }
            }
            return SpireOgelFilter.filterSpireOgels(ogelsList, controlCode, destinationCountryId, activityTypes);
        } catch (SOAPException e) {
            logger.error("An error occurred while trying to handle SOAP messaging", e);
        } catch (UnsupportedEncodingException e) {
            logger.error("An error occurred while trying to create the requesting SOAP message", e);
        } catch (XPathExpressionException e) {
            logger.error("An error occurred while trying to parse the SOAP response message", e);
        }
        return null;
    }
}
