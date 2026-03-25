---
title: Keel Elasticsearch 5.0.1 使用指南
description: keel-elasticsearch 5.0.1 依赖、配置、ElasticSearchKit API 与错误处理说明
---

# Keel Elasticsearch 5.0.1 使用指南

本库为 [Keel](https://github.com/sinri/keel) 生态下的 **Elasticsearch REST API** 轻量封装：基于 Vert.x
`WebClient` 以 JSON over HTTP(S) 调用集群，接口语义对齐 Elasticsearch 8.9 文档化的 REST 行为（仅涵盖库中已实现的操作）。

适用范围：**Java 17+**，模块化工程可通过 `module-info.java` 依赖 `io.github.sinri.keel.integration.elasticsearch`。

---

## 1. 依赖与坐标

**Maven：**

```xml

<dependency>
    <groupId>io.github.sinri</groupId>
    <artifactId>keel-elasticsearch</artifactId>
    <version>5.0.1</version>
</dependency>
```

**Gradle（Kotlin DSL）：**

```kotlin
dependencies {
    implementation("io.github.sinri:keel-elasticsearch:5.0.1")
}
```

本库以 **API** 方式依赖 `keel-core`（随制品传递引入），用于 `Keel` 异步模型与
`ConfigElement` 配置抽象。运行时尚需 Vert.x 相关组件（由 `keel-core` 及本模块的 `module-info` 传递约束）。

> **说明：** 若从源码构建，仓库中 `gradle.properties` 可能为 `5.0.1-SNAPSHOT`；发布到 Maven Central 的正式版本请以
`5.0.1` 为准。

---

## 2. 核心概念

| 类型                    | 作用                                     |
|-----------------------|----------------------------------------|
| `ElasticSearchKit`    | 对外入口，聚合索引、文档、搜索等能力                     |
| `ElasticSearchConfig` | 单集群连接与安全相关配置（继承 Keel `ConfigElement`）  |
| `ESApiMixin`          | 底层 HTTP 调用、请求头与认证                      |
| `ESApiQueries`        | URL 查询参数（继承 `HashMap`，键值类型均为 `String`） |
| `ESApiException`      | HTTP 非 2xx 时失败的受检异常信息载体                |

`ElasticSearchKit` 实现三个 Mixin 接口，业务侧通常**只依赖 `ElasticSearchKit`
** 即可调用已封装的方法；需要调用未封装的 REST 时，可使用 Mixin 中的 `call` / `callPost`（定义于 `ESApiMixin`）。

---

## 3. 配置说明（ElasticSearchConfig）

配置从 **Keel `ConfigElement`** 树读取，相对路径如下（与代码中 `readString` / `readInteger` 路径一致）：

| 配置路径             | 必填       | 默认值 / 行为                                                       |
|------------------|----------|----------------------------------------------------------------|
| `cluster.host`   | 是（发起请求前） | 无；未配置时构造 URL 会失败                                               |
| `cluster.port`   | 否        | `9200`                                                         |
| `cluster.scheme` | 否        | `http`                                                         |
| `username`       | 否        | 若与 `password` 同时存在，则使用 HTTP Basic 认证                           |
| `password`       | 否        | 同上                                                             |
| `opaqueId`       | 否        | 设置时增加请求头 `X-Opaque-Id`                                         |
| `version`        | 否        | 字符串形式的主次版本，如 `7.17.0` 或 `8.9.0`，用于选择 `Accept` / `Content-Type` |

**`version` 与请求头：** 当解析后的主版本**小于 8** 时，使用 `application/json`；否则使用
`application/vnd.elasticsearch+json`（适用于 Elasticsearch 8.x 的常见约定）。

构造示例（伪代码，具体如何从 YAML/JSON 得到 `ConfigElement` 请参考你项目中的 Keel 配置加载方式）：

```java
ConfigElement esSection = /* 来自应用配置、且路径与下文「配置说明」一致的 ConfigElement 节点 */;
ElasticSearchConfig esConfig = new ElasticSearchConfig(esSection);
```

---

## 4. 初始化 ElasticSearchKit

`ElasticSearchKit` 构造函数：`ElasticSearchKit(Keel keel, ElasticSearchConfig esConfig, WebClient webClient)`。

- **`Keel`：** Keel 异步运行时门面，用于与项目其余 Keel 组件一致的生命周期与调度。
- **`WebClient`：** Vert.x 的 `io.vertx.ext.web.client.WebClient`，通常 `WebClient.create(vertx)` 创建；可在应用中复用同一实例。

```java
Vertx vertx = Vertx.vertx();
WebClient webClient = WebClient.create(vertx);
Keel keel = /* 应用中已初始化的 Keel */;
ElasticSearchConfig esConfig = new ElasticSearchConfig(/* ... */);
ElasticSearchKit es = new ElasticSearchKit(keel, esConfig, webClient);
```

---

## 5. 调用风格与错误处理

- 所有网络调用均返回 Vert.x **`Future`**（具体成功类型因方法而异），请使用 `onSuccess` / `onFailure` 或 `compose` 等组合子处理结果。
- 当 HTTP 状态码 **不在 [200, 299]** 时，`call` 会以 **`ESApiException`** 失败 Future；异常内包含状态码、响应体片段、方法、端点、查询参数与请求体等调试信息（
  `toString()` 为 JSON 形态摘要）。
- 成功响应体会尽量解析为 **`JsonObject`**；若响应体为 JSON 数组，库会包装为形如
  `{ "array": [ ... ] }` 的对象；若无法解析为 JSON，则包装为 `{ "raw": "..." }`。

---

## 6. 已封装 API 一览

下列方法均在 `ElasticSearchKit` 实例上可用。**`indexName`** 等路径参数需符合 Elasticsearch 命名规则；库提供 *
*`ESIndexMixin.isLegalIndexName(String)`** 辅助校验（严格索引名场景）。

### 6.1 索引（`ESIndexMixin`）

| 方法                                             | HTTP              | 说明                                           |
|------------------------------------------------|-------------------|----------------------------------------------|
| `indexGet(indexName, queries)`                 | `GET /{index}`    | 获取索引信息                                       |
| `indexCreate(indexName, queries, requestBody)` | `PUT /{index}`    | 创建索引，`requestBody` 为 mapping/settings 等 JSON |
| `indexDelete(indexName, queries)`              | `DELETE /{index}` | 删除索引                                         |

官方文档可参考 Elasticsearch 8.9： [Create index](https://www.elastic.co/guide/en/elasticsearch/reference/8.9/indices-create-index.html) 等（与代码注释中链接一致）。

### 6.2 文档（`ESDocumentMixin`）

| 方法                                                             | 说明                                                                                                                                                                                                             |
|----------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `documentCreate(indexName, documentId, queries, documentBody)` | `documentId == null` 时自动生成 `_id`（`POST .../_doc/`）；否则 `POST .../_create/{id}`                                                                                                                                  |
| `documentGet(indexName, documentId, queries)`                  | 按 `_id` 获取文档                                                                                                                                                                                                   |
| `documentDelete(indexName, documentId, queries)`               | 删除文档                                                                                                                                                                                                           |
| `documentUpdate(indexName, documentId, queries, requestBody)`  | Update API（请求体为 update DSL JSON）                                                                                                                                                                               |
| `documentBulk(target, queries, requestBodyLines)`              | **Bulk API**：`target == null` 时为 `POST /_bulk`，否则 `POST /{target}/_bulk`。`requestBodyLines` 为**多行 NDJSON 中每一行对应的 `JsonObject`**（顺序与内容需符合 Bulk 规范：通常为 action 行与 source 行交替）；库会将每个 `JsonObject.toString()` 后换行拼接 |

### 6.3 搜索（`ESSearchMixin`）

| 方法                                            | 说明                                        |
|-----------------------------------------------|-------------------------------------------|
| `searchSync(indexName, queries, requestBody)` | `POST /{index}/_search`，同步搜索接口，请求体为查询 DSL |

### 6.4 低级扩展（`ESApiMixin`）

| 方法                                             | 说明                                                                                     |
|------------------------------------------------|----------------------------------------------------------------------------------------|
| `call(method, endpoint, queries, requestBody)` | 通用调用；`GET`/`DELETE` 忽略 body，其它方法发送 `requestBody` 字符串。**Bulk 等非纯 JSON 体可调此方法并自行拼接字符串。** |
| `callPost(endpoint, queries, requestBody)`     | `POST` 便捷方法，`requestBody` 为 `JsonObject`                                               |

**端点格式：** `endpoint` 为路径片段，需以 `/` 开头（如 `"/my-index/_search"`），与配置中的 `scheme://host:port` 拼接。

---

## 7. 响应类型

`indexCreate`、`documentGet`、`searchSync` 等方法的泛型返回类型（如 `ESIndexCreateResponse`、`ESDocumentGetResponse`、
`ESSearchResponse` 等）均继承 Keel 的 **`UnmodifiableJsonifiableEntityImpl`**：内部持有 Elasticsearch 返回的 *
*`JsonObject`**，可按 ES 官方响应字段读取业务数据（例如 `_source`、
`hits` 等）。各响应类当前以封装原始 JSON 为主，字段级 accessor 可参考未来版本 Javadoc。

---

## 8. URL 查询参数（ESApiQueries）

```java
ESApiQueries q = new ESApiQueries();
q.

put("pretty","true");
q.

put("refresh","true");
// 传入各 * 方法的 queries 参数，可为 null 表示无查询串
```

---

## 9. 与 GitHub Pages 的路径

若仓库已启用 **GitHub Pages** 且源目录为 `docs/`，本页一般可通过如下地址访问（具体以仓库设置为准）：

`https://sinri.github.io/keel-elasticsearch/5.0.1/`

---

## 10. 参考链接

- 仓库：<https://github.com/sinri/keel-elasticsearch>
- Elasticsearch REST 概览（8.9）：<https://www.elastic.co/guide/en/elasticsearch/reference/8.9/rest-apis.html>
- 许可证：GPL v3.0（见仓库中声明）

---

*文档版本与库版本对应：5.0.1*
