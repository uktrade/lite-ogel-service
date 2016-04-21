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

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPathExpressionException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Singleton
public class SpireOgelService {
    private SpireOgelClient client;
    private SpireOgelSOAPUnmarshaller unmarshaller;

    @Inject
    private CacheManager cacheManager;

    @Inject
    public SpireOgelService(SpireOgelClient client, SpireOgelSOAPUnmarshaller unmarshaller) {
        this.client = client;
        this.unmarshaller = unmarshaller;
    }

    private static final String CACHE_KEY = "ogelList";

    public List<SpireOgel> findOgel(String controlCode, String destinationCountryId, List<CategoryType> activityTypes) {
        Cache cache = cacheManager.getCache("ogelCache");
        try {
            List<SpireOgel> ogelsList;
            if (cache.get(CACHE_KEY) == null) {
                final SOAPMessage soapMessage = client.executeRequest();
                System.out.println("Not cached. Fetching from remote endpoint");
                ogelsList = unmarshaller.execute(soapMessage);
                cache.put(new Element(CACHE_KEY, ogelsList));
            } else {
                System.out.println("Found in the cache. Will retrieve the cached value");
                ogelsList = (List<SpireOgel>) cache.get(CACHE_KEY).getObjectValue();
            }
            return SpireOgelFilter.filterSpireOgels(ogelsList, controlCode, destinationCountryId, activityTypes);
        } catch (SOAPException e) {
            //TODO better error handling
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            //TODO better error handling
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setCacheManager(CacheManager cacheManager){
        this.cacheManager = cacheManager;
    }
}
