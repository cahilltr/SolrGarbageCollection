package com.cahill.synchronizeddisruption;

import org.apache.solr.core.AbstractSolrEventListener;
import org.apache.solr.core.SolrCore;
import org.apache.solr.search.SolrIndexSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EventListenerGC extends AbstractSolrEventListener {
    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public EventListenerGC(SolrCore core) {
        super(core);
        executorService.scheduleAtFixedRate(() -> {
            logger.info("Cahill: GC");
            System.gc();
        }, 30, 30, TimeUnit.SECONDS);
    }

    @Override
    public void postCommit() {
        logger.info("Cahill: PostCommit");
    }

    @Override
    public void postSoftCommit() {
        logger.info("Cahill: PostSoftCommit");
    }

    @Override
    public void newSearcher(SolrIndexSearcher solrIndexSearcher, SolrIndexSearcher solrIndexSearcher1) {
        logger.info("Cahill: New Searcher");
    }
}
