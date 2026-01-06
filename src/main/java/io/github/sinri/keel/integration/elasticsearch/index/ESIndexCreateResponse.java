package io.github.sinri.keel.integration.elasticsearch.index;

import io.github.sinri.keel.base.json.UnmodifiableJsonifiableEntityImpl;
import io.vertx.core.json.JsonObject;
import org.jspecify.annotations.NullMarked;

/**
 * @since 5.0.0
 */
@NullMarked
public class ESIndexCreateResponse extends UnmodifiableJsonifiableEntityImpl {
    public ESIndexCreateResponse(JsonObject jsonObject) {
        super(jsonObject);
    }

    // TODO
}
