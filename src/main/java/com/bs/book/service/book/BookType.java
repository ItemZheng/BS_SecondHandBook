package com.bs.book.service.book;

import java.util.HashMap;
import java.util.Map;

public class BookType {
    private static String[] bookTypes = {
            "经济金融类", "教育考试类", "计算机与网络类", "语言学习类", "管理类",
            "医学卫生类", "科技工程类", "少儿类", "文学小说类", "文化历史类",
            "法律类", "建筑类", "新闻传播类", "家庭育儿类", "艺术类",
            "生活时尚类", "旅游地理类", "心理类", "宗教哲学类",
            "社会科学类", "自然科学类", "政治军事类"
    };

    private static Map<String, Boolean> bookTypeMap;

    static{
        bookTypeMap = new HashMap<>();
        for(String type : bookTypes){
            bookTypeMap.put(type, true);
        }
    }

    public static String parseType(String type){
        if(bookTypeMap.containsKey(type)){
            return type;
        }
        return "其他类";
    }
}
