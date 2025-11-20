module io.github.sinri.keel.integration.elasticsearch {
    requires io.github.sinri.keel.base;
    requires io.vertx.core;
    requires io.vertx.web.client;
    requires static org.jetbrains.annotations;

    exports io.github.sinri.keel.integration.elasticsearch;
    exports io.github.sinri.keel.integration.elasticsearch.document;
    exports io.github.sinri.keel.integration.elasticsearch.index;
    exports io.github.sinri.keel.integration.elasticsearch.search;
}