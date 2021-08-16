package com.elasticsearch.jd;

import com.elasticsearch.jd.service.ContentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class JdApplicationTests {

    @Autowired
    ContentService contentService;
    @Test
    void contextLoads() throws IOException {
        System.out.println(contentService.parseContent("vue"));
    }

}
