package com.kerray.eshop.bean;

import java.util.List;

/**
 * 类名: ShopType
 *
 * 功能: 全部分类
 *
 * 创建人:kerray
 *
 * 创建时间:2015/7/15/22:14
 */
public class ShopTypeAll
{
    public List<ShopType>  shopTypes;
    public int retcode;

    public static class ShopType
    {
        public List<ShopTypeItem> children;
        public Long id;
        public String 分类名称;
        public String imagepath; // UUID文件名
        public String parent;
        public boolean state; // 表是否正在使用,true：正在使用；false：停用
    }

    public static class ShopTypeItem
    {
        public Long id;
        public String 分类名称;
        public String imagepath; // UUID文件名
        public String parent;
        public boolean state; // 表是否正在使用,true：正在使用；false：停用
    }


}
