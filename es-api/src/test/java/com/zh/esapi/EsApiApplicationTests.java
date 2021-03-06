package com.zh.esapi;


import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.zh.esapi.pojo.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.blobstore.DeleteResult;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.core.List;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.engine.Engine;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;

@SpringBootTest
class EsApiApplicationTests {
    @Autowired
    @Qualifier("restHighLevelClient")
    RestHighLevelClient client;


    //1 ?????????????????????
    @Test
    void testCreateIndex() throws IOException {
        //1. ??????????????????
        CreateIndexRequest request = new CreateIndexRequest("zh_index");
        // 2. ???????????????????????????
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse.index()+" create ok");
    }
    // 2 ??????????????????
    @Test
    void testExistIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("zh_index");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }
    // 3 ????????????
    @Test
    void testDeletedIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("zh_index");
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }
    // 1 ??????????????????
    @Test
    void testAddDoc() throws IOException {
        User user = new User("??????",3);
        IndexRequest request = new IndexRequest("zh_index");

        //put  /zh_index/_doc/1
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(1));// 1???
        // ?????????????????????
        request.source(JSONObject.toJSONString(user), XContentType.JSON);
        // ?????????????????????
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
        System.out.println(response.status());
    }
    // 2 ???????????? ????????????????????????
    @Test
    void testIsExists() throws IOException {
        GetRequest getRequest = new GetRequest("zh_index", "1");
        getRequest.fetchSourceContext(new FetchSourceContext(false)); //??????????????????
//        getRequest.storedFields("_none_");
        boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
    }
    //3 ??????????????????
    @Test
    void testGetDoc() throws IOException {
        GetRequest getRequest = new GetRequest("zh_index", "1");
        GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());
    }
    // 4 ????????????
    @Test
    void updateDoc() throws IOException {
        UpdateRequest request = new UpdateRequest("zh_index", "1");
        request.timeout(TimeValue.timeValueSeconds(1));
        User user = new User("????????????",4);
        request.doc(JSONObject.toJSONString(user),XContentType.JSON);
        UpdateResponse update = client.update(request, RequestOptions.DEFAULT);
        System.out.println(update);
        System.out.println(update.status());
    }
    // 5 ????????????
    @Test
    void testDeletedDoc() throws IOException {
        DeleteRequest request = new DeleteRequest("zh_index", "1");
        request.timeout("1s");
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(response.status());
    }

    // 6 ??????????????????
    @Test
    void testInsertBatch() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");
        ArrayList<User> userList = new ArrayList<>();
        userList.add(new User("??????",23));
        userList.add(new User("??????",22));
        userList.add(new User("??????",13));
        userList.add(new User("??????",4));
        for (int i = 0; i < userList.size(); i++) {
            bulkRequest.add(new IndexRequest("zh_index")
                            .id(""+(i+1))
                            .source(JSONObject.toJSONString(userList.get(i)),XContentType.JSON));
        }
        BulkResponse responses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(responses.status());

    }

    // 7 ??????
    @Test
    void testSearch() throws IOException {
        SearchRequest request = new SearchRequest("zh_index");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.highlighter() // ??????

        //????????????  ??????QueryBuilders ?????????
        //termQuery ????????????
        //matchAllQuery ????????????
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", "?????????");
//        QueryBuilders.matchAllQuery();
        searchSourceBuilder.query(matchQueryBuilder);
        request.source(searchSourceBuilder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        System.out.println(JSONObject.toJSONString(response.getHits()));
        System.out.println("========================");
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }

    }
}
