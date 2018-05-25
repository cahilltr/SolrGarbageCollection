package org.apache.solr.core;

import org.apache.solr.common.util.NamedList;
import org.apache.solr.search.SolrIndexSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SolrEventListenerGC implements SolrEventListener {
  private final static Logger logger = LoggerFactory.getLogger(SolrEventListenerGC.class);

  private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

  public SolrEventListenerGC(SolrCore core) {};

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

  @Override
  public void init(NamedList namedList) {
    executorService.scheduleAtFixedRate(() -> {
      logger.info("Cahill: GC");
      System.gc();
    }, 30, 30, TimeUnit.SECONDS);
  }
}

