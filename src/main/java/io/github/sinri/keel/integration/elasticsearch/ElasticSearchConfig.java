package io.github.sinri.keel.integration.elasticsearch;


import io.github.sinri.keel.base.configuration.ConfigElement;
import io.github.sinri.keel.base.configuration.NotConfiguredException;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * ElasticSearch API 的调用配置，针对一个特定的服务。
 *
 * @since 5.0.0
 */
@NullMarked
public class ElasticSearchConfig extends ConfigElement {

    public ElasticSearchConfig(ConfigElement configuration) {
        super(configuration);
    }


    public @Nullable String username() {
        try {
            return readString(List.of("username"));
        } catch (NotConfiguredException e) {
            return null;
        }
    }


    public @Nullable String password() {
        try {
            return readString(List.of("password"));
        } catch (NotConfiguredException e) {
            return null;
        }
    }

    public String clusterHost() throws NotConfiguredException {
        return readString(List.of("cluster", "host"));
    }

    public int clusterPort() {
        try {
            return readInteger(List.of("cluster", "port"));
        } catch (NotConfiguredException e) {
            return 9200;
        }
    }

    public String clusterScheme() {
        try {
            return Objects.requireNonNull(readString(List.of("cluster", "scheme")));
        } catch (NotConfiguredException e) {
            return "http";
        }
    }

    public String clusterApiUrl(String endpoint) throws NotConfiguredException {
        return this.clusterScheme() + "://" + this.clusterHost() + ":" + this.clusterPort() + endpoint;
    }

    public @Nullable String opaqueId() {
        try {
            return readString(List.of("opaqueId"));
        } catch (NotConfiguredException e) {
            return null;
        }
    }

    /**
     * @return Version Components in List
     */
    public @Nullable List<Integer> version() {
        String x;
        try {
            x = readString(List.of("version"));
        } catch (NotConfiguredException e) {
            return null;
        }
        List<Integer> l = new ArrayList<>();
        for (var c : x.split("[.]")) {
            int i = Integer.parseInt(c.trim());
            l.add(i);
        }
        return l;
    }
}
