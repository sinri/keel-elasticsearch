package io.github.sinri.keel.integration.elasticsearch;

import io.github.sinri.keel.base.Keel;
import io.github.sinri.keel.base.configuration.ConfigTree;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * ElasticSearch API 的调用能力相关的 Mixin，适用于8.9版。
 *
 * @since 5.0.0
 */
public interface ESApiMixin {

    @NotNull
    Keel getKeel();

    @NotNull
    ElasticSearchConfig getEsConfig();

    /**
     * 针对目标 ElasticSearch 服务，根据版本要求，调整请求头。
     *
     * @param bufferHttpRequest 请求体
     */
    default void handleHeaders(@NotNull HttpRequest<Buffer> bufferHttpRequest) {
        List<Integer> version = getEsConfig().version();
        if (version != null && !version.isEmpty() && version.get(0) != null && version.get(0) < 8) {
            bufferHttpRequest.putHeader("Accept", "application/json");
            bufferHttpRequest.putHeader("Content-Type", "application/json");
        } else {
            bufferHttpRequest.putHeader("Accept", "application/vnd.elasticsearch+json");
            bufferHttpRequest.putHeader("Content-Type", "application/vnd.elasticsearch+json");
        }
    }

    /**
     * 向 ElasticSearch 服务发起请求。
     * <p>
     * 对于 Bulk API，报文请求体不是 JSON 对象，因此报文请求体需转化为字符串形式供本方法接收。
     *
     * @param httpMethod  HTTP 方法
     * @param endpoint    请求端点
     * @param queries     在 URL 上的请求内容
     * @param requestBody 字符串形式请求报文
     * @return 异步完成的请求返回报文解析得的 JSON 对象
     */
    @NotNull
    default Future<JsonObject> call(@NotNull HttpMethod httpMethod, @NotNull String endpoint, @Nullable ESApiQueries queries, @Nullable String requestBody) {
        WebClient webClient = WebClient.create(getKeel().getVertx());
        String url = null;
        try {
            url = this.getEsConfig().clusterApiUrl(endpoint);
        } catch (ConfigTree.NotConfiguredException e) {
            return Future.failedFuture(e);
        }
        HttpRequest<Buffer> bufferHttpRequest = webClient.requestAbs(httpMethod, url);

        bufferHttpRequest.basicAuthentication(getEsConfig().username(), getEsConfig().password());
        handleHeaders(bufferHttpRequest);

        String opaqueId = this.getEsConfig().opaqueId();
        if (opaqueId != null) {
            bufferHttpRequest.putHeader("X-Opaque-Id", opaqueId);
        }

        if (queries != null) {
            queries.forEach(bufferHttpRequest::addQueryParam);
        }

        return Future.succeededFuture()
                     .compose(v -> {
                         if (httpMethod == HttpMethod.GET || httpMethod == HttpMethod.DELETE) {
                             return bufferHttpRequest.send();
                         } else {
                             return bufferHttpRequest.sendBuffer(Buffer.buffer(Objects.requireNonNullElse(requestBody, "")));
                         }
                     })
                     .compose(bufferHttpResponse -> {
                         int statusCode = bufferHttpResponse.statusCode();
                         if ((statusCode >= 300 || statusCode < 200)) {
                             return Future.failedFuture(new ESApiException(
                                     statusCode, bufferHttpResponse.bodyAsString(),
                                     httpMethod,
                                     endpoint,
                                     queries,
                                     requestBody
                             ));
                         }
                         JsonObject resp;
                         try {
                             resp = bufferHttpResponse.bodyAsJsonObject();
                         } catch (DecodeException decodeException) {
                             // There are situations that use Json Array as the response body!
                             resp = new JsonObject()
                                     .put("array", new JsonArray(bufferHttpResponse.bodyAsString()));
                         }
                         return Future.succeededFuture(resp);
                     })
                     .andThen(ar -> webClient.close());
    }

    /**
     * 对采用 POST 方式提供服务的非 Bulk API进行请求。
     *
     * @param endpoint    请求端点
     * @param queries     在 URL 上的请求内容
     * @param requestBody JSON 对象形式的报文内容
     * @return 异步完成的请求返回报文解析得的 JSON 对象
     */
    @NotNull
    default Future<JsonObject> callPost(@NotNull String endpoint, @Nullable ESApiQueries queries, @NotNull JsonObject requestBody) {
        return call(HttpMethod.POST, endpoint, queries, requestBody.toString());
    }

    /**
     * 在 URL 上的请求内容
     */
    class ESApiQueries extends HashMap<String, String> {
        @NotNull
        public JsonObject toJsonObject() {
            JsonObject jsonObject = new JsonObject();
            this.forEach(jsonObject::put);
            return jsonObject;
        }
    }

    /**
     * ElasticSearch API 服务请求异常。
     */
    class ESApiException extends Exception {
        private final int statusCode;
        private final @Nullable String response;

        private final @NotNull HttpMethod httpMethod;
        private final @NotNull String endpoint;
        private final @Nullable ESApiQueries queries;
        private final @Nullable String requestBody;

        public ESApiException(
                int statusCode, @Nullable String response,
                @NotNull HttpMethod httpMethod,
                @NotNull String endpoint,
                @Nullable ESApiQueries queries,
                @Nullable String requestBody
        ) {
            this.statusCode = statusCode;
            this.response = response;

            this.httpMethod = httpMethod;
            this.endpoint = endpoint;
            this.queries = queries;
            this.requestBody = requestBody;
        }

        @Override
        public String toString() {
            return new JsonObject()
                    .put("status_code", statusCode)
                    .put("response", response)
                    .put("http_method", httpMethod.name())
                    .put("endpoint", endpoint)
                    .put("queries", (queries == null ? null : queries.toJsonObject()))
                    .put("request_body", requestBody)
                    .toString();
        }

        public int getStatusCode() {
            return statusCode;
        }

        @Nullable
        public String getResponse() {
            return response;
        }

        @NotNull
        public String getEndpoint() {
            return endpoint;
        }

        @Nullable
        public ESApiQueries getQueries() {
            return queries;
        }

        @NotNull
        public HttpMethod getHttpMethod() {
            return httpMethod;
        }

        @Nullable
        public String getRequestBody() {
            return requestBody;
        }
    }
}
