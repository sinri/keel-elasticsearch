package io.github.sinri.keel.integration.elasticsearch;

import io.vertx.core.json.JsonObject;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;

/**
 * 在 URL 上的请求内容
 */
@NullMarked
public class ESApiQueries extends HashMap<String, String> {

    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();
        this.forEach(jsonObject::put);
        return jsonObject;
    }
}
