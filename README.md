# Elasticsearch

## 1.Elasticsearch 介绍

以下为百度百科内容

Elasticsearch是一个基于[Lucene](https://baike.baidu.com/item/Lucene/6753302)的搜索服务器。它提供了一个分布式多用户能力的[全文搜索引擎](https://baike.baidu.com/item/全文搜索引擎/7847410)，基于RESTful web接口。Elasticsearch是用Java语言开发的，并作为Apache许可条款下的开放源码发布，是一种流行的企业级搜索引擎。Elasticsearch用于[云计算](https://baike.baidu.com/item/云计算/9969353)中，能够达到实时搜索，稳定，可靠，快速，安装使用方便。官方客户端在Java、.NET（C#）、PHP、Python、Apache Groovy、Ruby和许多其他语言中都是可用的。根据DB-Engines的排名显示，Elasticsearch是最受欢迎的企业搜索引擎，其次是Apache Solr，也是基于Lucene。



个人总结: Elasticsearch 是基于Lucene封装的搜索工具(底层还是使用Lucene)，Elasticsearch（Lucene）性能强大在于它使用了倒排索引进行保存数据。在数据量较少时，Solr的速度高于Elasticsearch，随着数据量的增加，Solr的搜索效率会变得更低，而Elasticsearch却没有明显的变化。具体可查阅博客https://blog.csdn.net/jetty_welcome/article/details/104342595



## 2.Elasticsearch 安装部署

Elasticsearch 的安装，需要用到Elasticsearch的本体，和ik分词器。同时可以再部署一个Elasticsearch head 来监控Elasticsearch的运行状态。

**注意**:Elasticsearch插件的版本 必须和主版本一致！（所有文件版本一致）

![image-20210816104506476](\Elasticsearch.assets\image-20210816104506476.png)



#### 2.1 Elasticsearch 7.x 在Linux下安装

可以参考博客https://www.cnblogs.com/tjp40922/p/12194739.html
elasticsearch.yml 的配置（目前ES的配置）

```yml
# ======================== Elasticsearch Configuration =========================
#
# NOTE: Elasticsearch comes with reasonable defaults for most settings.
#       Before you set out to tweak and tune the configuration, make sure you
#       understand what are you trying to accomplish and the consequences.
#
# The primary way of configuring a node is via this file. This template lists
# the most important settings you may want to configure for a production cluster.
#
# Please consult the documentation for further information on configuration options:
# https://www.elastic.co/guide/en/elasticsearch/reference/index.html
#
# ---------------------------------- Cluster -----------------------------------
#
# Use a descriptive name for your cluster:
#
cluster.name: my-application
#
# ------------------------------------ Node ------------------------------------
#
# Use a descriptive name for the node:
#
node.name: node-1
#
# Add custom attributes to the node:
#
#node.attr.rack: r1
#
# ----------------------------------- Paths ------------------------------------
#
# Path to directory where to store the data (separate multiple locations by comma):
#
#path.data: /path/to/data
#
# Path to log files:
#
#path.logs: /path/to/logs
#
# ----------------------------------- Memory -----------------------------------
#
# Lock the memory on startup:
#
#bootstrap.memory_lock: true
#
# Make sure that the heap size is set to about half the memory available
# on the system and that the owner of the process is allowed to use this
# limit.
#
# Elasticsearch performs poorly when the system is swapping the memory.
#
# ---------------------------------- Network -----------------------------------
#
# By default Elasticsearch is only accessible on localhost. Set a different
# address here to expose this node on the network:
#
network.host: 0.0.0.0
#
# By default Elasticsearch listens for HTTP traffic on the first free port it
# finds starting at 9200. Set a specific HTTP port here:
#
http.port: 9200
#
# For more information, consult the network module documentation.
#
# --------------------------------- Discovery ----------------------------------
#
# Pass an initial list of hosts to perform discovery when this node is started:
# The default list of hosts is ["127.0.0.1", "[::1]"]
#
#discovery.seed_hosts: ["host1", "host2"]
#
# Bootstrap the cluster using an initial set of master-eligible nodes:
#
cluster.initial_master_nodes: ["node-1"]
#
# For more information, consult the discovery and cluster formation module documentation.
#
# ---------------------------------- Various -----------------------------------
#
# Require explicit names when deleting indices:
#
#action.destructive_requires_name: true

#开启跨域访问支持，默认为false  
  
http.cors.enabled: true  
  
#跨域访问允许的域名地址，(允许所有域名)以上使用正则  
  
http.cors.allow-origin: /.*/   

xpack.ml.enabled: false
```



#### 2.2 ik 分词器的使用

##### ik 分词器可以让Elasticsearch 对中文有良好的识别率并分词

##### ik分词的两种模式:

* ##### ik_max_word 会将文本做最细粒度的拆分

  比如会将「中华人民共和国国歌」拆分为：中华人民共和国、中华人民、中华、华人、人民共和国、人民、人、民、共和国、共和、和、国国、国歌，会穷尽各种可能的组合；

* ##### ik_smart 最粗粒度的拆分

  比如会将「中华人民共和国国歌」拆分为：中华人民共和国、国歌。

##### 显而易见在搜索效果中来说，拆分越细粒度的搜索效果越好

参考 https://zq99299.github.io/note-book/elasticsearch-senior/ik/30-ik-introduce.html

#### 2.3 Elasticsearch head 的部署

下载 https://github.com/mobz/elasticsearch-head

进入项目目录

```
npm install
npm run start
```

在浏览器访问http://localhost:9100 进入主页面

![image-20210816105445140](\Elasticsearch.assets\image-20210816105445140.png)

## 3 Elasticsearch 在Sprinboot 环境中使用

#### 3.1 搭建环境

在pom目录中 引入 es  注意版本要对应

```xml
 		<dependency>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>elasticsearch-rest-high-level-client</artifactId>
            <version>${elasticsearch.version}</version>
        </dependency>

        <dependency>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>elasticsearch-rest-client</artifactId>
            <version>${elasticsearch.version}</version>
        </dependency>
        <dependency>
            <groupId>org.elasticsearch</groupId>
            <artifactId>elasticsearch</artifactId>
            <version>${elasticsearch.version}</version>
        </dependency>
```

配置Config类

```java
@Configuration
public class ElasticSearchClientConfig {
    private final static String HOST = "121.40.68.176";
    private final static Integer PORT = 9200;
    private final static String SCHEME = "http";
    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(HOST,PORT,SCHEME))
        );
        return client;
    }
}
```

#### 3.2 调用es的增删改查方法

这里以潍柴项目作为举例

```java
   public String save(EsNews esNews) throws IOException {
        IndexRequest request = new IndexRequest(indexName);
        request.id(esNews.getId());
        request.source(JSONObject.toJSONString(esNews),XContentType.JSON);
        IndexResponse index = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        return index.status().toString();
    }
```

```java
 public String deleted(String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(indexName,id);
        DeleteResponse delete = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        return delete.status().toString();
    }

```

```java
 public String update(EsNews esNews) throws IOException {
        UpdateRequest request = new UpdateRequest(indexName, esNews.getId());
        request.doc(JSONObject.toJSONString(esNews),XContentType.JSON);
        UpdateResponse update = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        return update.status().toString();
    }
```

```java
public EsNews getById(String id) throws IOException {
        GetRequest getRequest = new GetRequest(indexName, id);
        GetResponse response = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        EsNews esNews = JSONObject.parseObject(response.getSourceAsString(), EsNews.class);
        return esNews;
    }
```

#### 3.3 批量保存方法

当ES新建，需要大量的数据从其他库导入时，可以调用以下方法。

该方法以MongoDB举例，MySQL同理

```java
public Boolean saveNewsFromDbToEs() throws IOException {
        for (int i = 0; ; i++) {
            Integer size = 100;
            Query query = new Query();
            query.limit(size);
            query.skip(i * size); // 分页保存，每次保存100条
            List<EsNews> list = mongoTemplate.find(query, EsNews.class);
            BulkRequest bulkRequest = new BulkRequest();
            bulkRequest.timeout("10s");
            if (list.isEmpty()){
                break;
            }
            System.out.println("当前执行到"+(i*100+list.size())+"条");
            for (EsNews news : list) {
                bulkRequest.add(new IndexRequest(indexName).id(news.getId()).source(JSONObject.toJSONString(news),XContentType.JSON));
            }
            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT); //执行保存
        }
        return true;
    }
```



####  3.4 搜索

es的搜索分为两种，**match和term。**match在匹配时会对所查找的关键词进行分词，然后按分词匹配查找，而term会直接对关键词进行查找。一般模糊查找的时候，多用match，而精确查找时可以使用term。

潍柴项目中，需要根据title和context进行模糊查询，所以这里使用match搜索，并使用should将两个条件关联（只要满足其一即可）

```java
public List<Map<String, Object>> search(String title,String content) throws IOException {
        SearchRequest searchRequest = new SearchRequest(indexName);
        //构建搜索
        MatchQueryBuilder matchQueryBuilder1 = QueryBuilders.matchQuery("title", title);
        MatchQueryBuilder matchQueryBuilder2 = QueryBuilders.matchQuery("contentText", content);
        BoolQueryBuilder must = 			QueryBuilders.boolQuery().must(matchQueryBuilder1).should(matchQueryBuilder2);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(must)
//                .query(matchQuery)
                .timeout(TimeValue.timeValueSeconds(10))
                .size(20);
        //执行搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        List<Map<String ,Object>> list = new ArrayList<>();
        for (SearchHit hit : hits) {
            if (hit.getScore() >= minScore){
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                sourceAsMap.put("source",hit.getScore());
                list.add(sourceAsMap);
            }
        }
        System.out.println("搜索出了"+hits.getHits().length+"条");
        return list;
    }
```

#### 3.5 高亮搜索

```java
public List<Map<String, Object>> searchHighLight(String title,String content) throws IOException {
        SearchRequest request = new SearchRequest(indexName);
        MatchQueryBuilder matchQueryBuilder1 = QueryBuilders.matchQuery("title", title);
        MatchQueryBuilder matchQueryBuilder2 = QueryBuilders.matchQuery("contentText", content);
        BoolQueryBuilder must = QueryBuilders.boolQuery().must(matchQueryBuilder1).should(matchQueryBuilder2);
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title").preTags("<span style='color:red'>").postTags("</span>");
        highlightBuilder.field("contentText").preTags("<span style='color:red'>").postTags("</span>");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(must)
//                .query(matchQuery)
                .timeout(TimeValue.timeValueSeconds(10))
                .highlighter(highlightBuilder)
                .size(20);
        request.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        //分析结果
        List<Map<String,Object>> list = new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            // 解析高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField restitle = highlightFields.get("title");
            HighlightField resContent = highlightFields.get("contentText");
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            if (restitle != null){
                // 将原来的字段 换位新的高亮字段
                Text[] fragments = restitle.getFragments();
                String newTitle = "";
                for (Text fragment : fragments) {
                    newTitle+=fragment;
                }
                sourceAsMap.put("title",newTitle); // 替换原先的字段
            }
            if (resContent != null){
                // 将原来的字段 换位新的高亮字段
                Text[] fragments = resContent.getFragments();
                String newContentText = "";
                for (Text fragment : fragments) {
                    newContentText+=fragment;
                }
                sourceAsMap.put("contentText",newContentText); // 替换原先的字段
            }
            list.add(sourceAsMap);
        }

        return list;
    }
```



