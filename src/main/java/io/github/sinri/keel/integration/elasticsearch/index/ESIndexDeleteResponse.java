package io.github.sinri.keel.integration.elasticsearch.index;

import io.github.sinri.keel.base.json.UnmodifiableJsonifiableEntityImpl;
import io.vertx.core.json.JsonObject;
import org.jspecify.annotations.NullMarked;

/**
 * @since 5.0.0
 */
@NullMarked
public class ESIndexDeleteResponse extends UnmodifiableJsonifiableEntityImpl {
    public ESIndexDeleteResponse(JsonObject jsonObject) {
        super(jsonObject);
    }
}
