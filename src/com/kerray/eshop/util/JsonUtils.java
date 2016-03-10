package com.kerray.eshop.util;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;

/**
 * 功能:      判断字符串是否是正确的json串
 * 创建人:     kerray
 * 创建时间:    2015/7/25/14:01
 */
public class JsonUtils
{
    public static boolean isBadJson(String json)
    {
        return !isGoodJson(json);
    }

    public static boolean isGoodJson(String json)
    {
        if (StringUtils.isBlank(json))
            return false;
        try
        {
            new JsonParser().parse(json);
            return true;
        } catch (JsonParseException e)
        {
            return false;
        }
    }
}
