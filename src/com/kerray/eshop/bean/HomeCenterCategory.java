package com.kerray.eshop.bean;

import java.util.List;

/**
 * 类名: HomeCenterCategory
 *
 * 功能: 主页列表的 javaBean
 *
 * 创建人:kerray
 *
 * 创建时间:2015/7/12/15:30
 */
public class HomeCenterCategory
{
    public List<HomeCategory> homeLists;
    public int retcode;

    public static class HomeCategory
    {
        public Long id;
        public String 列表名称;
        public String description;
        public String image; // UUID文件名

        public boolean state; // 表是否正在使用,true：正在使用；false：停用
    }
}
