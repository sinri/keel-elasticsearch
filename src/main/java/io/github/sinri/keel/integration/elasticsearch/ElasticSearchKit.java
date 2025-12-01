package io.github.sinri.keel.integration.elasticsearch;

import io.github.sinri.keel.base.Keel;
import io.github.sinri.keel.integration.elasticsearch.index.ESIndexMixin;
import org.jetbrains.annotations.NotNull;


/**
 * ElasticSearch API 的调用器。
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/8.9/rest-apis.html">
 *         ElasticSearch Restful API 8.9
 *         </a> Here only JSON over HTTP(s) supported.
 * @since 5.0.0
 */
public class ElasticSearchKit implements ESIndexMixin {
    @NotNull
    private final ElasticSearchConfig esConfig;
    @NotNull
    private final Keel keel;

    public ElasticSearchKit(@NotNull Keel keel, @NotNull ElasticSearchConfig esConfig) {
        this.esConfig = esConfig;
        this.keel = keel;
    }

    @Override
    public @NotNull Keel getKeel() {
        return keel;
    }

    public @NotNull ElasticSearchConfig getEsConfig() {
        return esConfig;
    }
}
