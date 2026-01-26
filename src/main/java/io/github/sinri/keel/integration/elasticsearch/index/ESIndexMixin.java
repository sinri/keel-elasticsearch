package io.github.sinri.keel.integration.elasticsearch.index;

import io.github.sinri.keel.integration.elasticsearch.ESApiMixin;
import io.github.sinri.keel.integration.elasticsearch.ESApiQueries;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * ElasticSearch API 的索引调用能力相关的 Mixin。
 *
 * @since 5.0.0
 */
@NullMarked
public interface ESIndexMixin extends ESApiMixin {
    /**
     * @see <a
     *         href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-create-index.html#indices-create-api-path-params">Create
     *         Index - Path Parameters - Index Name</a>
     */
    static boolean isLegalIndexName(@Nullable String indexName) {
        if (indexName == null) {
            return false;
        }
        return (indexName.getBytes(StandardCharsets.UTF_8).length < 255)
                && Objects.equals(indexName.toLowerCase(), indexName)
                && (!indexName.contains("/") && !indexName.contains("\\")
                && !indexName.contains("*") && !indexName.contains("?")
                && !indexName.contains("\"") && !indexName.contains("<") && !indexName.contains(">")
                && !indexName.contains("|") && !indexName.contains(" ")
                && !indexName.contains(",") && !indexName.contains("#") && !indexName.contains(":"))
                && (!indexName.startsWith("_") && !indexName.startsWith("-")
                && !indexName.startsWith("+") && !indexName.startsWith("."));
    }

    /**
     * @param indexName (Required, string) Comma-separated list of data streams, indices, and aliases used to limit the
     *                  request. Supports wildcards (*). To target all data streams and indices, omit this parameter or
     *                  use * or _all.
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/8.9/indices-get-index.html">Get index
     *         API</a>
     *         If the Elasticsearch security features are enabled, you must have the view_index_metadata or manage index
     *         privilege for the target data stream, index, or alias.
     */
    default Future<ESIndexGetResponse> indexGet(String indexName, @Nullable ESApiQueries queries) {
        return call(HttpMethod.GET, "/" + indexName, queries, null)
                .compose(resp -> Future.succeededFuture(new ESIndexGetResponse(resp)));
    }

    /**
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/8.9/indices-create-index.html">Create
     *         index API</a>
     */
    default Future<ESIndexCreateResponse> indexCreate(String indexName, @Nullable ESApiQueries queries, JsonObject requestBody) {
        return call(HttpMethod.PUT, "/" + indexName, queries, requestBody.toString())
                .compose(resp -> Future.succeededFuture(new ESIndexCreateResponse(resp)));
    }

    /**
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/8.9/indices-delete-index.html">Delete
     *         index API</a>
     */
    default Future<ESIndexDeleteResponse> indexDelete(String indexName, @Nullable ESApiQueries queries) {
        return call(HttpMethod.DELETE, "/" + indexName, queries, null)
                .compose(resp -> Future.succeededFuture(new ESIndexDeleteResponse(resp)));
    }
}
