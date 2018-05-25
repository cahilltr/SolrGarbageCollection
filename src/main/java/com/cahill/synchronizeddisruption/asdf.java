package com.cahill.synchronizeddisruption;

import org.apache.solr.core.SolrEventListener;
import org.apache.solr.core.SolrResourceLoader;

public class asdf {

  public static void main(String[] args) {
    SolrResourceLoader resourceLoader = new SolrResourceLoader();
    SolrEventListener eventListener = resourceLoader.newInstance("com.cahill.synchronizeddisruption.SolrEventListenerGC", SolrEventListener.class);
    assert eventListener != null;
  }

}