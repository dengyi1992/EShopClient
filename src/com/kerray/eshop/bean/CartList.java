package com.kerray.eshop.bean;

import java.util.List;

/**
 * 功能:      购物车
 * 创建人:     kerray
 * 创建时间:    2015/7/23/16:44
 */
public class CartList
{
    public int retcode;
    public List<Cart> carts;

    public static class Cart
    {
        public Long id;
        public Long 商品数量;
        public String 购买时间;
        public Double 合计金额;
        public boolean state;

        public ShopInfoList.Shopinfo shopInfo;
        public UserLogin.User user;
    }
}
