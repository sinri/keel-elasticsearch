package io.github.sinri.keel.integration.elasticsearch.document;

import io.github.sinri.keel.base.json.UnmodifiableJsonifiableEntityImpl;
import io.vertx.core.json.JsonObject;

/**
 * @since 5.0.0
 */
public class ESDocumentBulkResponse extends UnmodifiableJsonifiableEntityImpl {
    public ESDocumentBulkResponse(JsonObject jsonObject) {
        super(jsonObject);
    }
}
