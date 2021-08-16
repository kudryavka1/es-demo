package com.elasticsearch.jd.utils;

import com.elasticsearch.jd.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ZH
 */
public class HtmlParseUtil {
    public static void main(String[] args) throws IOException {
        List<Content> list = parseJd("java");
        for (Content content : list) {
            System.out.println("==========================================================");
            System.out.println(content.getTitle());
            System.out.println(content.getImg());
        }
    }
    public static List<Content> parseJd(String keyWord) throws IOException {
        //获取请求 https://search.jd.com/Search?keyword=java
        // ajax 获取不到
        String url = "https://search.jd.com/Search?keyword="+keyWord;
        //解析网页
        Document document = Jsoup.parse(new URL(url), 100000);
        Element element = document.getElementById("J_goodsList");
//        System.out.println(element.html());
        //获取所有li元素
        Elements lis = element.getElementsByTag("li");
        ArrayList<Content> list = new ArrayList<>();
        for (Element li : lis) {
            String img = li.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = li.getElementsByClass("p-price").eq(0).text();
            String title = li.getElementsByClass("p-name").eq(0).text();
            Content content = new Content(title,img,price);
            list.add(content);
        }
        return list;
    }
}
