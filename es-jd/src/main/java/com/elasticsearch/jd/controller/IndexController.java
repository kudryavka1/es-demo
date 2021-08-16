package com.elasticsearch.jd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ZH
 */
@Controller
public class IndexController {
    @RequestMapping({"/","/index"})
    public String index(){
        return "index";
    }
}
