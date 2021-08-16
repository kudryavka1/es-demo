package com.elasticsearch.jd.service;

import com.alibaba.fastjson.JSONObject;
import com.elasticsearch.jd.pojo.Content;
import com.elasticsearch.jd.utils.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZH
 */
@Service
public class ContentService {
    private final String indexName = "jd_goods";
    @Autowired
    RestHighLevelClient restHighLevelClient;

    public Boolean parseContent(String keyWord) throws IOException {
        List<Content> contents = HtmlParseUtil.parseJd(keyWord);
        // 把查询出来的数据 放入es中
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("60s");
        for (Content content : contents) {
            bulkRequest.add(new IndexRequest(indexName).source(JSONObject.toJSONString(content), XContentType.JSON));
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }
    public List<Map<String,Object>> searchPage(String keyword,int pageNo,int size) throws IOException {
        if (pageNo<=1){
            pageNo = 1;
        }
        if (size<=1){
            size = 10;
        }
        SearchRequest request = new SearchRequest(indexName);
        TermQueryBuilder queryTitle = QueryBuilders.termQuery("title", keyword);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryTitle)
                .timeout(TimeValue.timeValueSeconds(3))
                .from(pageNo)
                .size(size);
        //执行搜索
        request.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        List<Map<String,Object>> list = new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            list.add(hit.getSourceAsMap());
        }
        return list;
    }


    public List<Map<String,Object>> searchPageHighlight(String keyword,int pageNo,int size) throws IOException {
        if (pageNo<=1){
            pageNo = 1;
        }
        if (size<=1){
            size = 10;
        }
        SearchRequest request = new SearchRequest(indexName);
//        TermQueryBuilder queryTitle = QueryBuilders.termQuery("title", keyword);
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title", keyword);
        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title").preTags("<span style='color:red'>").postTags("</span>");
//        highlightBuilder.requireFieldMatch(false); // 关闭多个高亮显示
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(matchQueryBuilder)
                .timeout(TimeValue.timeValueSeconds(3))
                .from(pageNo)
                .size(size)
                .highlighter(highlightBuilder);


        //执行搜索
        request.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        //分析结果
        List<Map<String,Object>> list = new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            // 解析高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            if (title != null){
                // 将原来的字段 换位新的高亮字段
                Text[] fragments = title.getFragments();
                String newTitle = "";
                for (Text fragment : fragments) {
                    newTitle+=fragment;
                }
                sourceAsMap.put("title",newTitle); // 替换原先的字段
            }
            list.add(sourceAsMap);
        }
        return list;
    }
}
