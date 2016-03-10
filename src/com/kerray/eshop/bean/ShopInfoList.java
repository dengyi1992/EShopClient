package com.kerray.eshop.bean;

import java.util.List;

/**
 * 功能:  根据类型id下查询的商品列表
 * 创建人:kerray
 * 创建时间:2015/7/18/15:05
 */
public class ShopInfoList
{
    public int id;
    public int retcode;
    public int count;
    public boolean asc;
    public String orderColumn;
    public List<Shopinfo> shopInfos;

    public static class Shopinfo
    {
        public Long id;
        public String 商品名称;
        public Long 数量;
        public Double 价格;
        public Double 市场价;
        public String 图片地址;
        public Long 商品类型;
        public Long 评论;
        public float 评分;
        public String 颜色;
        public String 规格;
        public String time;
        public String 描述;
        public boolean state;
    }
}
