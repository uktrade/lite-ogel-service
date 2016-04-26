package com.bis.lite.ogel.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.bis.lite.ogel.client.SpireOgelClient;
import com.bis.lite.ogel.client.unmarshall.SpireOgelSOAPUnmarshaller;
import com.bis.lite.ogel.model.CategoryType;
import com.bis.lite.ogel.model.SpireOgel;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPathExpressionException;

@Singleton
public class SpireOgelService {
    private SpireOgelClient client;
    private SpireOgelSOAPUnmarshaller unmarshaller;

    final static Logger logger = Logger.getLogger(SpireOgelService.class);

    @Inject
    public SpireOgelService(SpireOgelClient client, SpireOgelSOAPUnmarshaller unmarshaller) {
        this.client = client;
        this.unmarshaller = unmarshaller;
    }

    private static final String CACHE_KEY = "ogelList";

    public List<SpireOgel> findOgel(String controlCode, String destinationCountryId, List<CategoryType> activityTypes) {
        List<SpireOgel> ogelsList = null;
        final CacheManager cacheManager = CacheManager.getInstance();
        Ehcache cache = cacheManager.getEhcache("ogelCache");
        if (cache.get(CACHE_KEY) != null) {
            ogelsList = (List<SpireOgel>) cache.get(CACHE_KEY).getObjectValue();
            return SpireOgelFilter.filterSpireOgels(ogelsList, controlCode, destinationCountryId, activityTypes);
        }
        return null;
    }

    public List<SpireOgel> getAllOgels() throws XPathExpressionException, SOAPException, UnsupportedEncodingException {
        final SOAPMessage soapMessage = client.executeRequest();
        return unmarshaller.execute(soapMessage);
    }
}
