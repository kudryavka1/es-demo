package com.elasticsearch.jd.controller;

import com.elasticsearch.jd.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author ZH
 */
@RestController
public class ContentController {
    @Autowired
    ContentService contentService;
    @GetMapping("/search/{keyword}/{pageNo}/{size}")
    public List<Map<String,Object>> searchPage(@PathVariable String keyword,@PathVariable int pageNo,@PathVariable int size) throws IOException {
        List<Map<String, Object>> list = contentService.searchPageHighlight(keyword, pageNo, size);
        return list;
    }
}
