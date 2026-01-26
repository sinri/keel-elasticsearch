package io.github.sinri.keel.integration.elasticsearch;

import io.github.sinri.keel.base.async.Keel;
import io.github.sinri.keel.integration.elasticsearch.index.ESIndexMixin;
import org.jspecify.annotations.NullMarked;


/**
 * ElasticSearch API 的调用器。
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/8.9/rest-apis.html">
 *         ElasticSearch Restful API 8.9
 *         </a> Here only JSON over HTTP(s) supported.
 * @since 5.0.0
 */
@NullMarked
public class ElasticSearchKit implements ESIndexMixin {

    private final ElasticSearchConfig esConfig;

    private final Keel keel;

    public ElasticSearchKit(Keel keel, ElasticSearchConfig esConfig) {
        this.esConfig = esConfig;
        this.keel = keel;
    }

    public ElasticSearchConfig getEsConfig() {
        return esConfig;
    }

    @Override
    public Keel getKeel() {
        return keel;
    }
}
