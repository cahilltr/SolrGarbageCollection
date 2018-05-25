package com.cahill.synchronizeddisruption;

import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.cloud.overseer.ZkStateWriter;
import org.apache.solr.common.cloud.ClusterState;
import org.apache.solr.common.cloud.SolrZkClient;
import org.apache.solr.common.cloud.ZkCoreNodeProps;
import org.apache.solr.common.cloud.ZkNodeProps;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.Utils;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrEventListener;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SolrEventListenerCloudGC implements SolrEventListener {
    private final static Logger logger = LoggerFactory.getLogger(SolrEventListenerCloudGC.class);

    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private static final String epochToRun = "epochtorun";

    public SolrEventListenerCloudGC(SolrCore core) {
        //TODO how to update the value
        CoreContainer coreContainer = core.getCoreDescriptor().getCoreContainer();
        if (coreContainer.isZooKeeperAware()) {
            int timeTillIntialRunGC = 30;
            try {
                //Check zk for epochtorun
                SolrZkClient solrZkClient = new SolrZkClient();
                if (solrZkClient.exists(epochToRun,true)) {
                    //if there, schedule for next run time
                    byte[] data = solrZkClient.getData(epochToRun,null, new Stat(), true);
//                    Map m = (Map) Utils.fromJSON(data);
                    String dateString = new String(data);
                    long epoch = Long.parseLong(dateString);
                    long currentTimeMillis = System.currentTimeMillis();
                    if (epoch < currentTimeMillis) {
                        solrZkClient.setData(epochToRun, Long.toString(System.currentTimeMillis() + TimeUnit.SECONDS.convert(30, TimeUnit.SECONDS)).getBytes(), true);
                    } else {
                        timeTillIntialRunGC = Math.toIntExact(TimeUnit.MILLISECONDS.convert(epoch - System.currentTimeMillis(), TimeUnit.SECONDS));
                    }
                } else {
                    //If not there, create for 30 seconds out and schedule for then
                    solrZkClient.setData(epochToRun, Long.toString(System.currentTimeMillis() + TimeUnit.SECONDS.convert(30, TimeUnit.SECONDS)).getBytes(), true);
                }

            } catch (KeeperException e) {
                logger.error("KeeperException", e);
            } catch (InterruptedException e) {
                logger.error("InterruptedException", e);
            }

            executorService.scheduleAtFixedRate(() -> {
                logger.info("Cahill: GC");
                System.gc();
            }, timeTillIntialRunGC, 30, TimeUnit.SECONDS);
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
