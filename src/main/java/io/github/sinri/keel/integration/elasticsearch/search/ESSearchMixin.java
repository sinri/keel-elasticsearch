package io.github.sinri.keel.integration.elasticsearch.search;

import io.github.sinri.keel.integration.elasticsearch.ESApiMixin;
import io.github.sinri.keel.integration.elasticsearch.ESApiQueries;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * ElasticSearch API 的搜索调用能力相关的 Mixin，适用于8.9版。
 *
 * @since 5.0.0
 */
@NullMarked
public interface ESSearchMixin extends ESApiMixin {
    /**
     * 通过同步查询接口查询
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/8.9/search-search.html">Search API</a>
     */
    default Future<ESSearchResponse> searchSync(String indexName, @Nullable ESApiQueries queries, JsonObject requestBody) {
        return callPost("/" + indexName + "/_search", queries, requestBody)
                .compose(resp -> Future.succeededFuture(new ESSearchResponse(resp)));
    }
}
