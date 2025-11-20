package io.github.sinri.keel.integration.elasticsearch.search;

import io.github.sinri.keel.base.json.UnmodifiableJsonifiableEntityImpl;
import io.vertx.core.json.JsonObject;

/**
 * 同步查询接口的查询结果
 *
 * @since 5.0.0
 */
public class ESSearchResponse extends UnmodifiableJsonifiableEntityImpl {
    public ESSearchResponse(JsonObject jsonObject) {
        super(jsonObject);
    }
}
