package com.cahill.synchronizeddisruption;

import org.apache.solr.common.cloud.SolrZkClient;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrEventListener;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class SolrEventListenerCloudGC implements SolrEventListener {
    private final static Logger logger = LoggerFactory.getLogger(SolrEventListenerCloudGC.class);

    private static final String epochToRun = "/epochtorun";
    private static final MyExecutor myExecutor = new MyExecutor();

    public SolrEventListenerCloudGC(SolrCore core) {
        CoreContainer coreContainer = core.getCoreDescriptor().getCoreContainer();
        if (coreContainer.isZooKeeperAware()) {
            logger.info("Cahill: Is ZK Aware");
            int timeTillInitialRunGC = 30;
            try {
                //Check zk for epochtorun
                SolrZkClient solrZkClient = coreContainer.getZkController().getZkClient();
                if (solrZkClient.exists(epochToRun,true)) {
                    logger.info("Cahill: epoch exists");
                    //if there, schedule for next run time
                    byte[] data = solrZkClient.getData(epochToRun,null, new Stat(), true);
//                    Map m = (Map) Utils.fromJSON(data);
                    String dateString = new String(data);
                    long epoch = Long.parseLong(dateString);
                    logger.info("Cahill: Epoch is " + epoch);
                    long currentTimeMillis = System.currentTimeMillis();
                                        if (epoch < currentTimeMillis) {
                        solrZkClient.setData(epochToRun, Long.toString(System.currentTimeMillis() + TimeUnit.SECONDS.convert(30, TimeUnit.SECONDS)).getBytes(), true);
                    } else {
                        timeTillInitialRunGC = Math.toIntExact(TimeUnit.MILLISECONDS.convert(epoch - System.currentTimeMillis(), TimeUnit.SECONDS));
                    }
                } else {
                    //If not there, create for 30 seconds out and schedule for then
                    solrZkClient.create(epochToRun, Long.toString(System.currentTimeMillis() + TimeUnit.SECONDS.convert(30, TimeUnit.SECONDS)).getBytes(), CreateMode.PERSISTENT, true);
                }

            } catch (KeeperException e) {
                logger.error("KeeperException", e);
            } catch (InterruptedException e) {
                logger.error("InterruptedException", e);
            }
            logger.info("Cahill: Time Till Initial Run GC is " + timeTillInitialRunGC);

            int value = myExecutor.submit(() -> {
                                    logger.info("Cahill: GC");
                    System.gc();
                    SolrZkClient solrZkClient = coreContainer.getZkController().getZkClient();
                    try {
                        long nextRun = System.currentTimeMillis() + TimeUnit.SECONDS.convert(30, TimeUnit.SECONDS);
                        logger.info("Cahill: Next run in Zk is :" + nextRun);
                        solrZkClient.setData(epochToRun, Long.toString(nextRun).getBytes(), true);
                    } catch (KeeperException e) {
                        logger.error("KeeperException: GC Epoch Time Not Set", e);
                    } catch (InterruptedException e) {
                        logger.error("InterruptedException: GC Epoch Time Not Set", e);
                    }

            },timeTillInitialRunGC, 30, TimeUnit.SECONDS);

            logger.info("Cahill: Submit Value: " + value);
        } else {
            logger.error("This is meant for SolrCloud");
        }
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

    @Override
    public void init(NamedList namedList) {
        logger.info("Cahill: init");
    }
}
