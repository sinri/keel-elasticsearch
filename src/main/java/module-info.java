module io.github.sinri.keel.integration.elasticsearch {
    requires transitive io.github.sinri.keel.base;
    requires transitive io.vertx.core;
    requires transitive io.vertx.web.client;
    requires static org.jspecify;

    exports io.github.sinri.keel.integration.elasticsearch;
    exports io.github.sinri.keel.integration.elasticsearch.document;
    exports io.github.sinri.keel.integration.elasticsearch.index;
    exports io.github.sinri.keel.integration.elasticsearch.search;
}