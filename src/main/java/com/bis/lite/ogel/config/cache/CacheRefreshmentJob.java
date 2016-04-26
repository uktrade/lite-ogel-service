package com.bis.lite.ogel.config.cache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class CacheRefreshmentJob implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheRefreshmentJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        final CacheManager cacheManager = CacheManager.getInstance();
        SelfPopulatingCache cache = (SelfPopulatingCache) cacheManager.getEhcache("ogelCache");
        if (cache != null) {
            try {
                LOGGER.info("Cache Refresment Job is Executing.");
                cache.refresh();
            } catch (CacheException e) {
                LOGGER.error("An error occurred while trying to call EhCache Refresh ", e);
            }
        }
    }
}
