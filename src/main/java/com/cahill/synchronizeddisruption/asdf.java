package com.cahill.synchronizeddisruption;

import org.apache.solr.core.SolrEventListener;
import org.apache.solr.core.SolrResourceLoader;

public class asdf {

  public static void main(String[] args) throws ClassNotFoundException {
    SolrResourceLoader resourceLoader = new SolrResourceLoader();
    SolrEventListener eventListener = resourceLoader.newInstance("com.cahill.synchronizeddisruption.SolrEventListenerGC", SolrEventListener.class);
    assert eventListener != null;

    ClassLoader classLoader = resourceLoader.getClassLoader();
    Class.forName("org.apache.solr.core.QuerySenderListener", true, classLoader).asSubclass(SolrEventListener.class);
    EventListenerGC eventListenerGC = new EventListenerGC(null);
    boolean isInstanceOf = eventListenerGC instanceof SolrEventListener;
    System.out.println(isInstanceOf);

    resourceLoader.findClass("com.cahill.synchronizeddisruption.SolrEventListenerGC", SolrEventListener.class);


//    Class<? extends SolrEventListener> eventListenerGC1 = (Class<? extends SolrEventListener>) Class.forName("com.cahill.synchronizeddisruption.EventListenerGC", true, classLoader);
//    boolean instanceOf2 = eventListenerGC1.asSubclass(SolrEventListener.class) instanceof SolrEventListener;
//    System.out.println(instanceOf2);
  }

}