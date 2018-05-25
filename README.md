# SolrGarbageCollection
Work on syncronized disruption in Solr


curl -X POST -H "Content-Type: application/json" localhost:8983/solr/gettingstarted/config -d '{"add-listener":{"event":"postCommit", "class":"com.cahill.synchronizeddisruption.SolrEventListenerGC", "name":"listener"}}'

Jar must be in the library that is specified by solrconfig.xml