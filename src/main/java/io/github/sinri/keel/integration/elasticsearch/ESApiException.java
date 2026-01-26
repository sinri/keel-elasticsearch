package io.github.sinri.keel.integration.elasticsearch;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * ElasticSearch API 服务请求异常。
 */
@NullMarked
public class ESApiException extends Exception {
    private final int statusCode;
    private final @Nullable String response;

    private final HttpMethod httpMethod;
    private final String endpoint;
    private final @Nullable ESApiQueries queries;
    private final @Nullable String requestBody;

    public ESApiException(
            int statusCode, @Nullable String response,
            HttpMethod httpMethod,
            String endpoint,
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


    public String getEndpoint() {
        return endpoint;
    }

    public @Nullable ESApiQueries getQueries() {
        return queries;
    }


    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    @Nullable
    public String getRequestBody() {
        return requestBody;
    }
}
