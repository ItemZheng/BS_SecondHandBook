package com.bs.book.controller;

import com.bs.book.annotation.LoginIgnore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/fe")
@Controller
public class PageController {

    @RequestMapping("/index")
    @LoginIgnore
    public String index(){
        return "index";
    }

    @RequestMapping("/main")
    public String mainPage(){
        return "main";
    }

    @RequestMapping("/bookDetail")
    public String bookDetail(){
        return "bookDetail";
    }

    @RequestMapping("/addBook")
    public String addBook(){
        return "addBook";
    }

    @RequestMapping("/addOrder")
    public String addOrder(){
        return "addOrder";
    }

    @RequestMapping("/messageBox")
    public String messageBox(){
        return "messageBox";
    }

}
