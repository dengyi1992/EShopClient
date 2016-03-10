package com.kerray.eshop.util;

import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;

/**
 * 功能:      json转换为javaBean对象
 * 创建人:     kerray
 * 创建时间:    2015/7/25/14:01
 */
public class GsonUtils
{
    public static <T> T jsonToBean(String jsonResult, Class<T> clz)
    {
        boolean goodJson = JsonUtils.isGoodJson(jsonResult);
        try
        {
            if (JsonUtils.isGoodJson(jsonResult))
            {
                Gson gson = new Gson();
                T t = gson.fromJson(jsonResult, clz);
                return t;
            }
            else
                return null;
        } catch (Exception e)
        {
            LogUtils.e("gson转换出错" + e);
            return null;
        }
    }
}
