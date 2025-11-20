package io.github.sinri.keel.integration.elasticsearch;


import io.github.sinri.keel.base.configuration.ConfigElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.github.sinri.keel.base.KeelInstance.Keel;


/**
 * ElasticSearch API 的调用配置，针对一个特定的服务。
 *
 * @since 5.0.0
 */
public class ElasticSearchConfig extends ConfigElement {

    public ElasticSearchConfig(ConfigElement configuration) {
        super(configuration);
    }

    public ElasticSearchConfig(String esKey) {
        this(Keel.getConfiguration().extract("es", esKey));
    }

    public String username() {
        return readString(List.of("username"), null);
    }

    public String password() {
        return readString(List.of("password"), null);
    }

    public @NotNull String clusterHost() {
        return Objects.requireNonNull(readString(List.of("cluster", "host"), null));
    }

    public int clusterPort() {
        return readInteger(List.of("cluster", "port"), 9200);
    }

    public @NotNull String clusterScheme() {
        return Objects.requireNonNull(readString(List.of("cluster", "scheme"), "http"));
    }

    public @NotNull String clusterApiUrl(@NotNull String endpoint) {
        return this.clusterScheme() + "://" + this.clusterHost() + ":" + this.clusterPort() + endpoint;
    }

    public @Nullable String opaqueId() {
        return readString(List.of("opaqueId"), null);
    }

    /**
     * @return Version Components in List
     */
    public @Nullable List<Integer> version() {
        var x = readString(List.of("version"));
        if (x == null) return null;
        List<Integer> l = new ArrayList<>();
        for (var c : x.split("[.]")) {
            int i = Integer.parseInt(c.trim());
            l.add(i);
        }
        return l;
    }
}
